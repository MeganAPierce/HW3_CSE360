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
