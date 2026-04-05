package testing;

import database.Database;
import database.PostStore;
import database.Result;
import entityClasses.Post;

public class PostStoreBoundaryTests {

    /*******
    * <p> Title: PostStoreBoundary Tests </p>
    *  @author Megan Pierce
    * <p> Description: This class is responsible for the testing of PostStore. </p>
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

    /*******
    * <p> Method: assertTrue() </p> 
    */

    /*******
    * <p> Method: printSummary() </p> 
    */

}
