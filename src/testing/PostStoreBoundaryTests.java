package testing;

import database.Database;
import database.PostStore;
import database.Result;
import entityClasses.Post;
import testing.DiscussionTests;

public class PostStoreBoundaryTests {

    /*******
    * <p> Title: PostStoreBoundary Tests </p>
    * 
    * <p> Description: 
    * This class tests boundary conditions and invalid input handling for PostStore, focusing on CWE-20 & CWE-476</p>
    * @author Megan Pierce
    * <p> Good tests should:
    * Reject empty title
    * Reject empty thread name if applicable
    * Reject empty body
    * accept minimum valid values
    * accept maximum valid values
    * reject values over the char limit
    * safe handling for nonexistent post ID lookup
    * safe handling for null-like or invalid ID input if the method allows it
    * THIS SUPPORTS MAINLY CWE-20 & CWE-476 </p>
    */
    
    private static int passed = 0;
    private static int failed = 0;
	/*******
    * <p> Method: main() </p> 
    * <p> Runs all PostStore boundary tests.</p>
    * @params args command-line arguments
    * @throws Exception if database setup fails
    */
    public static void main(String[] args) throws Exception{
        Database db = new Database();
        db.connectToDatabase();
        db.createDiscussionTables();

        PostStore postStore = new PostStore(db);

        DiscussionTests.clearDiscussionTables(db.getConnection());
    }

    /*******
    * <p> Method: test() </p> 
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
    * <p> Method: assertTrue() </p> 
    */
    private static void assertTrue(boolean condition, String message){
        if (!condition) throw new AssertionError(message);
    }

    /*******
    * <p> Method: printSummary() </p> 
    */
    private static void printSummary(){
        System.out.println("\nPostStoreBoundaryTests complete.");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
    }
}
