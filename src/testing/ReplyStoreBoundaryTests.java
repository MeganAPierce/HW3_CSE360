package testing;

import database.Database;
import database.PostStore;
import database.ReplyStore;
import database.Result;
import entityClasses.Post;
import entityClasses.Reply;

/*** Title: ReplyStoreBoundaryTests
* Description: 
* This class tests boundary conditions and invalid input handling for ReplyStore, focusing mainly on CWE-20 and CWE-476
* @author Megan Pierce
* Good tests should:
* valid login succeeds
* invalid password fails
* nonexistent user fails
* blank username/password fails
* sensitive behavior is not leaked through messaging
* SUPPORTS CWE-306 & Cryptographic Failures
*/

public class ReplyStoreBoundaryTests {
 
    private static int passed = 0;
    private static int failed = 0;

    /***
     * main()
     * Runs all of the ReplyStore boundary tests
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        Database db = new Database();
        db.connectToDatabase();
        db.createDiscussionTables();

        PostStore postStore = new PostStore(db);
        ReplyStore replyStore = new ReplyStore(db);

        DiscussionTests.clearDiscussionTables(db.getConnection());
        
        test("Reply valid create succeeds", () -> {
            Result<Post> post = postStore.createPost(
                "megan",
                "CSE360",
                "Valid Title",
                "Valid body"
            );
            assertTrue(post.isOk(), "Expected valid post creation to succeed first.");

            Result<Reply> reply = replyStore.createReply(
                post.getValue().getPostId(),
                postStore,
                "nick",
                "Valid reply"
            );
            assertTrue(reply.isOk(), "Expected valid reply creation to succeed.");
        });

        test("Reply empty body rejected", () -> {
            Result<Post> post = postStore.createPost(
                "megan",
                "CSE360",
                "Valid Title",
                "Valid body"
            );
            assertTrue(post.isOk(), "Expected valid post creation to succeed first.");

            Result<Reply> reply = replyStore.createReply(
                post.getValue().getPostId(),
                postStore,
                "nick",
                ""
            );
            assertTrue(!reply.isOk(), "Expected empty reply body to be rejected.");
        });

        test("Reply whitespace-only body rejected", () -> {
            Result<Post> post = postStore.createPost(
                "megan",
                "CSE360",
                "Valid Title",
                "Valid body"
            );
            assertTrue(post.isOk(), "Expected valid post creation to succeed first.");

            Result<Reply> reply = replyStore.createReply(
                post.getValue().getPostId(),
                postStore,
                "nick",
                "   "
            );
            assertTrue(!reply.isOk(), "Expected whitespace-only reply body to be rejected.");
        });

        test("Reply body length 2000 accepted", () -> {
            Result<Post> post = postStore.createPost(
                "megan",
                "CSE360",
                "Valid Title",
                "Valid body"
            );
            assertTrue(post.isOk(), "Expected valid post creation to succeed first.");

            String body = "a".repeat(2000);
            Result<Reply> reply = replyStore.createReply(
                post.getValue().getPostId(),
                postStore,
                "nick",
                body
            );
            assertTrue(reply.isOk(), "Expected reply body length 2000 to succeed.");
        });

        test("Reply body length 2001 rejected", () -> {
            Result<Post> post = postStore.createPost(
                "megan",
                "CSE360",
                "Valid Title",
                "Valid body"
            );
            assertTrue(post.isOk(), "Expected valid post creation to succeed first.");

            String body = "a".repeat(2001);
            Result<Reply> reply = replyStore.createReply(
                post.getValue().getPostId(),
                postStore,
                "nick",
                body
            );
            assertTrue(!reply.isOk(), "Expected reply body length 2001 to be rejected.");
        });

        test("Reply to nonexistent post rejected", () -> {
            Result<Reply> reply = replyStore.createReply(
                "missing-post-id",
                postStore,
                "nick",
                "This should fail"
            );
            assertTrue(!reply.isOk(), "Expected reply to nonexistent post to fail.");
        });

        test("Read nonexistent reply ID fails safely", () -> {
            Result<Reply> result = replyStore.readReplyById("missing-reply-id");
            assertTrue(!result.isOk(), "Expected nonexistent reply ID to fail.");
        });

        test("Read blank reply ID fails safely", () -> {
            Result<Reply> result = replyStore.readReplyById("   ");
            assertTrue(!result.isOk(), "Expected blank reply ID to fail.");
        });

        test("Cannot reply to deleted post", () -> {
            Result<Post> post = postStore.createPost(
                "megan",
                "CSE360",
                "Delete Test",
                "Body"
            );
            assertTrue(post.isOk(), "Expected valid post creation to succeed first.");

            Result<Void> deleted = postStore.softDeletePostById(post.getValue().getPostId());
            assertTrue(deleted.isOk(), "Expected post deletion to succeed.");

            Result<Reply> reply = replyStore.createReply(
                post.getValue().getPostId(),
                postStore,
                "nick",
                "Reply after delete"
            );
            assertTrue(!reply.isOk(), "Expected reply to deleted post to be rejected.");
        });

        printSummary();

    }

    /***
     * Method: test()
     * Will throw an AssertionError if a condition is false
     * 
     * @param name
     * @param r
     * @throws AssertionError (through assertTrue)
     */
    private static void test(String name, Runnable r){
        try {
            r.run();
            System.out.println("PASS: " + name);
            passed ++;
        } catch (Throwable t) {
            System.out.println("FAIL: " + name);
            System.out.println(" " + t.getMessage());
            failed ++;
        }
    }

    /***
     * Method: assertTrue()
     * 
     * @param condition 
     * @param message the error message
     */
    private static void assertTrue(boolean condition, String message){
        if(!condition) throw new AssertionError(message);
    }

    /***
     * Method: printSummary()
     * Prints the summary of the test results
     */
    private static void printSummary(){
        System.out.println("\nReplyStoreBoundaryTests complete.");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
    }
}
