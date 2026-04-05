package testing;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import database.Database;
import database.PostStore;
import database.ReplyStore;
import database.Result;
import entityClasses.Post;
import entityClasses.Reply;

/*******
 * <p> Title: DiscussionTests Class. </p>
 * 
 * <p> Description: This class is an application used for the semi-automated testing of Project 2. 
 * Tests include creating, updating, and deleting posts and replies of varying content and lengths.
 * Each test returns and prints to the console whether it passed or failed.
 * </p>
 * 
 * @author Megan Pierce
 * 
 * @version 2.00		2026-03-22 Wrote tests indicating pass or fail for Post and Reply CRUD
 */

public class DiscussionTests {

	/**
	 * Number of tests passed.
	 * <p> This value is used to track and print how many tests passed.</p>
	 * */
    private static int passed = 0;
    /**
	 * Number of tests failed.
	 * <p> This value is used to track and print how many tests failed.</p>
	 * */
    private static int failed = 0;

    /**
     * This is the main function.
     * 
     * <p> This function contains the main body of all test cases, as well as updating information
     * for whether the tests passed or failed based on factors such as input validation and
     * user permissions. A new database is instantiated and setup to store Post and Reply values
     * and associated inputs before tests are conducted. Test cases are carried out and results
     * (pass/fail) are then stored.</p>
     * 
     * @param args receives a String array input
     * @throws exception if any type of exception is raised in the body of the code
     * */
    public static void main(String[] args) throws Exception {
    	
    	// This instantiates a new database to contain and keep track of all changes
        // to Reply and Post objects and other associated variables in the database
        Database db = new Database();
        
        // connect to the database and create tables to contain values for Posts and Replies
        db.connectToDatabase();
        db.createDiscussionTables();

        // create new PostStore and ReplyStore classes to contain all posts, and all replies to each post
        PostStore postStore = new PostStore(db);
        ReplyStore replyStore = new ReplyStore(db);

        // clear all database tables and reset the database input fields to start the application
        clearDiscussionTables(db.getConnection());

        // Post tests
        /**
    	 * Test Case 1 (Pass)
    	 * <p> This is test case 1, which should pass. The test involves creating a normal
    	 * valid post with the thread name CSE310, an existing thread, and an accepted character count
    	 * for the title and body of the post.</p>
    	 * */
        test("TEST CASE: P1 Create Post valid", () -> {
            Result<Post> r = postStore.createPost("megan", "CSE310", "Merge sort recursion", "How many levels?");
            assertTrue(r.isOk(), "Expected OK but got: " + r.getError());
        });

        /**
    	 * Test Case 2 (Pass)
    	 * <p> This is test case 2, which should pass if it results in an error. The 
    	 * test involves creating a post with the thread name CSE310, 
    	 * an existing thread, and a not accepted long character count for the post body.</p>
    	 * */
        test("TEST CASE: Inputing a large post body", () -> {
            Result<Post> r = postStore.createPost("megan", "CSE310", "Merge sort recursion", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            assertTrue(r.isOk(), "Expected OK but got: " + r.getError());
        });

        /**
    	 * Test Case 3 (Pass)
    	 * <p> This is test case 3, which should pass if it results in an error. The 
    	 * test involves creating a post with the thread name CSE310, 
    	 * an existing thread, and an accepted character count for the body of the post, but an
    	 * empty/zero character title.</p>
    	 * */
        test("TEST CASE: P2 Create Post empty title rejected", () -> {
            Result<Post> r = postStore.createPost("tino", "CSE310", "", "Body");
            assertTrue(!r.isOk(), "Expected FAIL");
            assertTrue(r.getError().contains("Title cannot be empty"), "Bad error: " + r.getError());
        });

        /**
    	 * Test Case 4 (Pass)
    	 * <p> This is test case 4, which should pass. The test involves calling the function to read a post by calling its
    	 * unique post id, which then returns the associated post in the database. First, the post must
    	 * be created using valid input and character counts and an existing thread. Then,
    	 * the post id function is validated. If either of these fail, the test case fails.</p>
    	 * */
        test("TEST CASE: P3 Read Post by ID success", () -> {
            Result<Post> r = postStore.createPost("cheese", "CSE310", "Heap question", "Min-heap vs max-heap?");
            assertTrue(r.isOk(), "Expected create OK but got: " + r.getError());

            String id = r.getValue().getPostId();
            Result<Post> read = postStore.readPostById(id);
            assertTrue(read.isOk(), "Expected OK but got: " + read.getError());
            assertTrue(read.getValue().getTitle().equals("Heap question"), "Unexpected title");
        });

        /**
    	 * Test Case 5 (Pass)
    	 * <p> This is test case 5, which should pass if it results in an error. The 
    	 * test involves attempting to update a post for which the new title 
    	 * is empty, breaking the character minimum limit requirement. The post updating
    	 * function is called if the post has been created successfully, and then new valid thread and body are
    	 * input in addition to an invalid, empty title field.</p>
    	 * */
        test("TEST CASE: P4 Update Post invalid rejected", () -> {
            Result<Post> r = postStore.createPost("tanis", "CSE330", "Title", "Body");
            assertTrue(r.isOk(), "Expected create OK but got: " + r.getError());

            String id = r.getValue().getPostId();
            Result<Post> upd = postStore.updatePost(id, "CSE310", "", "NewBody");
            assertTrue(!upd.isOk(), "Expected FAIL");
            assertTrue(upd.getError().contains("Title cannot be empty"), "Bad error: " + upd.getError());
        });

        /**
    	 * Test Case 6 (Pass)
    	 * <p> This is test case 6, which should pass. The test involves soft deleting a post
    	 * and ensuring that the post then shows up with a title and body indicating deletion, 
    	 * as well as disabling all CRUD abilities on the post.</p>
    	 * */
        test("TEST CASE: P5 Soft Delete Post marks it deleted", () -> {
            Result<Post> r = postStore.createPost("chelsea", "CSE310", "Delete me", "Bye");
            assertTrue(r.isOk(), "Expected create OK but got: " + r.getError());

            String id = r.getValue().getPostId();
            Result<Void> del = postStore.softDeletePostById(id);
            assertTrue(del.isOk(), "Expected OK but got deletion error");

            Result<Post> read = postStore.readPostById(id);
            assertTrue(read.isOk(), "Expected read OK after soft delete");
            assertTrue(read.getValue().isDeleted(), "Expected post to be marked deleted");
        });

        /**
    	 * Test Case 7 (Pass)
    	 * <p> This is test case 7, which should pass. The test involves searching the post database
    	 * by keyword and making sure all matching posts with matching contents are then chosen to be 
    	 * displayed.</p>
    	 * */
        test("TEST CASE: P6 Search Posts subset", () -> {
            postStore.createPost("megan", "CSE310", "Merge sort recursion", "How many levels?");
            assertTrue(postStore.searchPostsByKeyword("merge").size() >= 1, "Expected at least 1 match");
            assertTrue(postStore.searchPostsByKeyword("ffffff").size() == 0, "Expected empty subset");
        });

        
        //Reply tests
        /**
    	 * Test Case 8 (Pass)
    	 * <p> This is test case 8, which should pass. The test involves creating a valid reply with the thread name 
    	 * CSE310, an existing thread, and an accepted character count for the title and body of the reply.
    	 * The reply should be associated with the appropriate post id for the post under which it resides.</p>
    	 * */
        test("TEST CASE: R1 Create Reply valid", () -> {
            Result<Post> p = postStore.createPost("megan", "CSE310", "Recursion help", "Need help");
            assertTrue(p.isOk(), "Expected post OK but got: " + p.getError());

            Result<Reply> r = replyStore.createReply(p.getValue().getPostId(), postStore, "tino", "Try drawing the recursion tree.");
            assertTrue(r.isOk(), "Expected OK but got: " + r.getError());
        });

        /**
    	 * Test Case 9 (Pass)
    	 * <p> This is test case 9, which should pass if it results in an error. The 
    	 * test involves creating a valid reply with an invalid
    	 * post id for which it corresponds to. This should not create a new reply because it has no valid
    	 * existing post to which it should be attached.</p>
    	 * */
        test("TEST CASE: R2 Create Reply missing post rejected", () -> {
            Result<Reply> r = replyStore.createReply("missing-post-id", postStore, "tino", "This should fail");
            assertTrue(!r.isOk(), "Expected FAIL");
        });

        /**
    	 * Test Case 10 (Pass)
    	 * <p> This is test case 10, which should pass. The test involves finding a reply by its associated
    	 * id. When the getReplyId() function is called with the reply id, the associated reply should return.</p>
    	 * */
        test("TEST CASE: R3 Read Reply by ID success", () -> {
            Result<Post> p = postStore.createPost("cheese", "CSE310", "Heap question", "Need help with heaps");
            assertTrue(p.isOk(), "Expected post OK but got: " + p.getError());

            Result<Reply> r = replyStore.createReply(p.getValue().getPostId(), postStore, "tanis", "Use the heap property.");
            assertTrue(r.isOk(), "Expected reply OK but got: " + r.getError());

            String replyId = r.getValue().getReplyId();
            Result<Reply> read = replyStore.readReplyById(replyId);
            assertTrue(read.isOk(), "Expected read OK but got: " + read.getError());
            assertTrue(read.getValue().getBody().equals("Use the heap property."), "Unexpected reply body");
        });

        /**
    	 * Test Case 11 (Pass)
    	 * <p> This is test case 11, which should pass. The test involves reading all replies that are under a post
    	 * with the given post id.</p>
    	 * */
        test("TEST CASE: R4 Read Replies by Post ID", () -> {
            Result<Post> p = postStore.createPost("megan", "CSE310", "DFS question", "Need DFS help");
            assertTrue(p.isOk(), "Expected post OK but got: " + p.getError());

            String postId = p.getValue().getPostId();
            replyStore.createReply(postId, postStore, "tino", "Check discovery times.");
            replyStore.createReply(postId, postStore, "cheese", "Also look at finish times.");

            List<Reply> replies = replyStore.readRepliesByPostId(postId);
            assertTrue(replies.size() >= 2, "Expected at least 2 replies");
        });

        /**
    	 * Test Case 12 (Pass)
    	 * <p> This is test case 12, which should pass. The test involves creating a valid reply with the thread name 
    	 * CSE310, an existing thread, and an accepted character count for the title and body of the reply.
    	 * The reply is then updated to have a new body with a valid character count.</p>
    	 * */
        test("TEST CASE: R5 Update Reply valid", () -> {
            Result<Post> p = postStore.createPost("megan", "CSE310", "Graph question", "Need graph help");
            assertTrue(p.isOk(), "Expected post OK but got: " + p.getError());

            Result<Reply> r = replyStore.createReply(p.getValue().getPostId(), postStore, "tino", "Old reply");
            assertTrue(r.isOk(), "Expected reply OK but got: " + r.getError());

            String replyId = r.getValue().getReplyId();
            Result<Reply> upd = replyStore.updateReply(replyId, "New reply");
            assertTrue(upd.isOk(), "Expected update OK but got: " + upd.getError());
            assertTrue(upd.getValue().getBody().equals("New reply"), "Reply not updated");
        });

        /**
    	 * Test Case 13 (Pass)
    	 * <p> This is test case 13, which should pass if it results in an error. The 
    	 * test involves creating a valid reply with the thread name 
    	 * CSE310, an existing thread, and an accepted character count for the title and body of the reply as well as
    	 * an associated existing post. The reply is then updated to an invalid empty body.</p>
    	 * */
        test("TEST CASE: R6 Update Reply invalid rejected", () -> {
            Result<Post> p = postStore.createPost("megan", "CSE310", "Binary tree", "Need help");
            assertTrue(p.isOk(), "Expected post OK but got: " + p.getError());

            Result<Reply> r = replyStore.createReply(p.getValue().getPostId(), postStore, "tino", "Valid body");
            assertTrue(r.isOk(), "Expected reply OK but got: " + r.getError());

            String replyId = r.getValue().getReplyId();
            Result<Reply> upd = replyStore.updateReply(replyId, "");
            assertTrue(!upd.isOk(), "Expected FAIL");
            assertTrue(upd.getError().contains("Body cannot be empty"), "Bad error: " + upd.getError());
        });

        /**
    	 * Test Case 14 (Pass)
    	 * <p> This is test case 14, which should pass. The test involves creating a valid reply to a valid
    	 * existing post, and then soft deleting it from the database. The title and body should indicate
    	 * that it was deleted afterwards, and CRUD abilities should be disabled for the reply.</p>
    	 * */
        test("TEST CASE: R7 Soft Delete Reply marks it deleted", () -> {
            Result<Post> p = postStore.createPost("megan", "CSE310", "Queue question", "Need queue help");
            assertTrue(p.isOk(), "Expected post OK but got: " + p.getError());

            Result<Reply> r = replyStore.createReply(p.getValue().getPostId(), postStore, "tino", "Reply to delete");
            assertTrue(r.isOk(), "Expected reply OK but got: " + r.getError());

            String replyId = r.getValue().getReplyId();
            Result<Void> del = replyStore.softDeleteReplyById(replyId);
            assertTrue(del.isOk(), "Expected delete OK");

            Result<Reply> read = replyStore.readReplyById(replyId);
            assertTrue(read.isOk(), "Expected read OK after soft delete");
            assertTrue(read.getValue().isDeleted(), "Expected reply to be marked deleted");
        });

        /**
    	 * Test Case 15 (Pass)
    	 * <p> This is test case 15, which should pass if it results in an error. The test attempts to update a reply that
    	 * has already been deleted. A post is created, then a reply to that post, and then the reply is deleted.
    	 * Then the function to update a reply is called on the deleted reply, which should result in an error.</p>
    	 * */
        test("TEST CASE: R8 Cannot update deleted reply", () -> {
            Result<Post> p = postStore.createPost("megan", "CSE310", "Stack question", "Need stack help");
            assertTrue(p.isOk(), "Expected post OK but got: " + p.getError());

            Result<Reply> r = replyStore.createReply(p.getValue().getPostId(), postStore, "tino", "Reply body");
            assertTrue(r.isOk(), "Expected reply OK but got: " + r.getError());

            String replyId = r.getValue().getReplyId();
            Result<Void> del = replyStore.softDeleteReplyById(replyId);
            assertTrue(del.isOk(), "Expected delete OK");

            Result<Reply> upd = replyStore.updateReply(replyId, "Updated reply");
            assertTrue(!upd.isOk(), "Expected FAIL");
            assertTrue(upd.getError().contains("Deleted replies cannot be updated"), "Bad error: " + upd.getError());
        });

        /**
    	 * Test Case 16 (Pass)
    	 * <p> This is test case 16, which should pass. The test involves searching for all matching replies
    	 * to an input keyword. All replies containing this keyword in the body must be returned for displaying.</p>
    	 * */
        test("TEST CASE: R9 Search Replies by keyword", () -> {
            Result<Post> p = postStore.createPost("megan", "CSE310", "Sorting", "Need sorting help");
            assertTrue(p.isOk(), "Expected post OK but got: " + p.getError());

            String postId = p.getValue().getPostId();
            replyStore.createReply(postId, postStore, "tino", "Try merge sort.");
            replyStore.createReply(postId, postStore, "cheese", "Quick sort is another option.");

            assertTrue(replyStore.searchRepliesByKeyword("merge").size() >= 1, "Expected at least 1 reply match");
            assertTrue(replyStore.searchRepliesByKeyword("asefae").size() == 0, "Expected empty subset");
        });

        /**
    	 * Test Case 17 (Pass)
    	 * <p> This is test case 17, which should pass. The test is the same as test case 16 except it only searches
    	 * and returns from within an input parameter for the specific post being searched within.</p>
    	 * */
        test("TEST CASE: R10 Search Replies by keyword for one post", () -> {
            Result<Post> p1 = postStore.createPost("megan", "CSE310", "Post one", "Body one");
            Result<Post> p2 = postStore.createPost("megan", "CSE310", "Post two", "Body two");
            assertTrue(p1.isOk() && p2.isOk(), "Expected both posts OK");

            replyStore.createReply(p1.getValue().getPostId(), postStore, "tino", "merge sort answer");
            replyStore.createReply(p2.getValue().getPostId(), postStore, "cheese", "merge sort answer");

            List<Reply> repliesForPost1 = replyStore.searchRepliesByKeywordForPost(p1.getValue().getPostId(), "merge");
            assertTrue(repliesForPost1.size() == 1, "Expected exactly 1 match for post 1");
        });

        //Author helper tests
        /**
    	 * Test Case 18 (Pass)
    	 * <p> This is test case 18, which should pass if isAuthor returns true. It calls
    	 * the function isAuthor for a post with the given true author of the post, and should
    	 * return true.</p>
    	 * */
        test("TEST CASE: A1 Post isAuthor true for author", () -> {
            Result<Post> r = postStore.createPost("megan", "CSE360", "Author test", "Body");
            assertTrue(r.isOk(), "Expected create OK but got: " + r.getError());
            assertTrue(r.getValue().isAuthor("megan"), "Expected isAuthor to return true");
        });

        /**
    	 * Test Case 19 (Pass)
    	 * <p> This is test case 19, which should pass if isAuthor returns false. It calls
    	 * the function isAuthor for a post with a given incorrect author of the post or
    	 * non-existing user/author, and should return false.</p>
    	 * */
        test("TEST CASE: A2 Post isAuthor false for non-author",() -> {
            Result<Post> r = postStore.createPost("megan", "CSE360", "Author test", "Body");
            assertTrue(r.isOk(), "Expected create OK but got: " + r.getError());
            assertTrue(!r.getValue().isAuthor("notmegan"),"Expected isAuthor to return false");
        });

        /**
    	 * Test Case 20 (Pass)
    	 * <p> This is test case 20, which should pass if isAuthor returns true. It calls
    	 * the function isAuthor for a reply with the given true author of the reply, and should
    	 * return true.</p>
    	 * */
        test("TEST CASE: A3 Post isAuthor true for author", () -> {
            Result<Post> p = postStore.createPost("megan", "CSE360", "Reply author test", "Body");
            assertTrue(p.isOk(), "Expected post OK but got: " + p.getError());

            Result<Reply> r = replyStore.createReply(p.getValue().getPostId(), postStore, "tino", "Reply body");
            assertTrue(r.isOk(), "Expected reply OK but got: " + r.getError());
            assertTrue(r.getValue().isAuthor("tino"), "Expected isAuthor to return true");
        });

        /**
    	 * Test Case 21 (Pass)
    	 * <p> This is test case 21, which should pass if isAuthor returns false. It calls
    	 * the function isAuthor for a reply with a given incorrect author of the reply or
    	 * non-existing user/author, and should return false.</p>
    	 * */
        test("TEST CASE: A4 Post isAuthor false for non-author",() -> {
            Result<Post> p = postStore.createPost("megan", "CSE310", "Reply author test", "Body");
            assertTrue(p.isOk(), "Expected post OK but got: " + p.getError());

            Result<Reply> r = replyStore.createReply(p.getValue().getPostId(), postStore, "tino", "Reply body");
            assertTrue(r.isOk(), "Expected reply OK but got: " + r.getError());
            assertTrue(!r.getValue().isAuthor("megan"), "Expected isAuthor to return false");
        });

        //Thread helper tests
        /**
    	 * Test Case 22 (Pass)
    	 * <p> This is test case 22, which should pass. It checks whether all threads being created 
    	 * are being stored long term by checking and returning all thread names in the list returns by the
    	 * function readAllThreadNames called on the postStore containing all posts.</p>
    	 * */
        test("TEST CASE: T1 Read All Thread Names", () -> {
            postStore.createPost("megan", "ThreadA", "Post 1", "Body 1");
            postStore.createPost("tanis", "ThreadB", "Post 2", "Body 2");
            postStore.createPost("cheese", "ThreadA", "Post 3", "Body 3");

            List<String> threadNames = postStore.readAllThreadNames();
            assertTrue(threadNames.contains("ThreadA"), "Expected ThreadA in thread list");
            assertTrue(threadNames.contains("ThreadB"), "Expected ThreadB in thread list");
        });

        /**
    	 * Test Case 23 (Pass)
    	 * <p> This is test case 23, which should pass. It creates a post in a thread and then
    	 * checks if the thread exists amongst the names of threads in all posts contained in the
    	 * postStore in the database.</p>
    	 * */
        test("TEST CASE: T2 Thread Exists true for existing thread", () -> {
            postStore.createPost("megan", "ExistingThread", "Post", "Body");
            assertTrue(postStore.threadExists("ExistingThread"), "Expected thread to exist");
        });

        /**
    	 * Test Case 24 (Pass)
    	 * <p> This is test case 24, which should pass if threadExists returns false. It checks if a nonexistent 
    	 * thread exists amongst the names of threads in all posts contained in the postStore in the database.</p>
    	 * */
        test("TEST CASE: T3 Thread Exists false for missing thread", () -> {
            assertTrue(!postStore.threadExists("MissingThread"), "Expected thread not to exist");
        });

        // print the summary of all test results and whether the test cases passed or failed
        System.out.println("\nDISCUSSION TEST SUMMARY");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        db.closeConnection();
    }


    /**
     * This is the clearDiscussionTables function.
     * 
     * <p> This function clears all of the existing database fields and tables to aid in the setup
     * for test cases in main.</p>
     * 
     * @param conn establishes a connection to the database
     * @throws SQLexception if any type of exception is raised in the creation and setup of the database
     * */
    //Clear tables before testing - clean slate for testing, not something you'd want in actual production  
    private static void clearDiscussionTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM REPLIES");
            stmt.executeUpdate("DELETE FROM POSTS");
        }
    }

    /**
     * This is the test function.
     * 
     * <p> This function runs and validates a provided test, then print whether or not the test case passed based on the
     * resulting output from the provided test code.</p>
     * 
     * @param testName receives the name of the test to output to the console
     * @param testcode receives the runnable code to run and validate the test case whether it passes or not
     * */
    //run a test   
    private static void test(String testName, Runnable testCode) {
        try {
            testCode.run();
            System.out.println("[PASS] " + testName);
            passed++;
        } catch (AssertionError e) {
            System.out.println("[FAIL] " + testName + " - " + e.getMessage());
            failed++;
        }
    }
    
    /**
     * This is the assertTrue function.
     * 
     * <p> This is a helper function that assists with assertion and validation in tests in main and 
     * can throw an AssertionError if an error arises, with an error message from the string provided</p>
     * 
     * @param condition evaluates true or false
     * @param message evaluates to a String message for the error message
     * */
    //helper function for assertions in tests
    private static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
