package testing;

import database.Database;
import database.PostStore;
import database.Result;
import entityClasses.Post;
import testing.DiscussionTests;

public class PostStoreBoundaryTests {

    /*******
    * Title: PostStoreBoundary Tests 
    * 
    * Description: 
    * This class tests boundary conditions and invalid input handling for PostStore, focusing on CWE-20 & CWE-476
    * @author Megan Pierce
    *  Good tests should:
    * Reject empty title
    * Reject empty thread name if applicable
    * Reject empty body
    * accept minimum valid values
    * accept maximum valid values
    * reject values over the char limit
    * safe handling for nonexistent post ID lookup
    * safe handling for null-like or invalid ID input if the method allows it
    * THIS SUPPORTS MAINLY CWE-20 & CWE-476 
    */
    
    private static int passed = 0;
    private static int failed = 0;
	/*******
    *  Method: main()  
    *  Runs all PostStore boundary tests.
    * @param args command-line arguments
    * @throws Exception if database setup fails
    */
    public static void main(String[] args) throws Exception{
        Database db = new Database();
        db.connectToDatabase();
        db.createDiscussionTables();

        PostStore postStore = new PostStore(db);

        clearDiscussionTables(db);

        test("Post valid create succeeds", () -> {
            Result<Post> result = postStore.createPost(
                "megan",
                "CSE360",
                "Valid Title",
                "Valid body"
            );
            assertTrue(result.isOk(), "Expected valid post creation to succeed.");
        });

        test("Post empty title rejected", () -> {
            Result<Post> result = postStore.createPost(
                "megan",
                "CSE360",
                "",
                "Valid body"
            );
            assertTrue(!result.isOk(), "Expected empty title to fail.");
        });

        test("Post whitespace body rejected", () -> {
            Result<Post> result = postStore.createPost(
                "megan",
                "CSE360",
                "Valid Title",
                "   "
            );
            assertTrue(!result.isOk(), "Expected whitespace-only body to fail.");
        });

        test("Post title length 200 accepted", () -> {
            String title = "a".repeat(200);
            Result<Post> result = postStore.createPost(
                "megan",
                "CSE360",
                title,
                "Valid body"
            );
            assertTrue(result.isOk(), "Expected title length 200 to succeed.");
        });

        test("Post title length 201 rejected", () -> {
            String title = "a".repeat(201);
            Result<Post> result = postStore.createPost(
                "megan",
                "CSE360",
                title,
                "Valid body"
            );
            assertTrue(!result.isOk(), "Expected title length 201 to fail.");
        });

        test("Post body length 5000 accepted", () -> {
            String body = "a".repeat(5000);
            Result<Post> result = postStore.createPost(
                "megan",
                "CSE360",
                "Valid Title",
                body
            );
            assertTrue(result.isOk(), "Expected body length 5000 to succeed.");
        });

        test("Post body length 5001 rejected", () -> {
            String body = "a".repeat(5001);
            Result<Post> result = postStore.createPost(
                "megan",
                "CSE360",
                "Valid Title",
                body
            );
            assertTrue(!result.isOk(), "Expected body length 5001 to fail.");
        });

        test("Read post by blank ID fails safely", () -> {
            try {
                Result<Post> result = postStore.readPostById("   ");
                assertTrue(!result.isOk(), "Expected blank post ID to fail.");
            } catch (Exception e) {
                assertTrue(true, "Handled exception safely.");
            }
        });

        printSummary();
    }
    
    /***
     * Method: clearDiscussionTables()
     * @param db
     * @throws Exception
     */
    private static void clearDiscussionTables(Database db) throws Exception {
        db.getConnection().createStatement().executeUpdate("DELETE FROM REPLIES");
        db.getConnection().createStatement().executeUpdate("DELETE FROM POSTS");
    }
    
    /*******
    *  Method: test()  
    */
    private static void test(String name, Runnable r){
        try {
            r.run();
            System.out.println("PASS: " + name);
            passed++;
        } catch (Throwable t) {
            System.out.println("FAIL: " + name);
            System.out.println("  " + t.getMessage());
            failed++;
        }
    }

    /*******
    *  Method: assertTrue()  
    */
    private static void assertTrue(boolean condition, String message){
        if (!condition) throw new AssertionError(message);
    }

    /*******
    *  Method: printSummary()  
    */
    private static void printSummary(){
        System.out.println("\nPostStoreBoundaryTests complete.");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
    }
}
