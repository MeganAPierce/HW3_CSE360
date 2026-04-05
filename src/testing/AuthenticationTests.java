package testing;

import database.Database;
import entityClasses.User;

/**
 * Title: AuthenticationTests
 * 
 * Description:
 * This class tests authentication-related behavior in our current system, focusing on CWE-306 (Missing Authentication for Critical Function) and 
 * supporting discussion of Cryptographic Failures.
 * 
 * @author Megan Pierce
 * 
 * Good tests should:
 * verify a valid login succeeds
 * verify invalid password fails
 * verify nonexistent password fails
 * verify nonexistent user fails
 * verify blank username and/or password fails
 * verify bad role-based login fails
 * 
 */
public class AuthenticationTests {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) throws Exception{

    }

    /**
     * Method: clearUserTable
     * clears the existing users from the user table for clean slate for tests
     * 
     * @param db 
     * @throws Exception if table can't be cleared
     */
    private static void clearUserTable(Database db) throws Exception{
        db.getConnection().createStatement().executeUpdate("DELETE FROM userDB");
    }
    /**
     * Method: test
     * Runs a test case and increments pass/fail
     */
    private static void test(String name, Runnable r){
        try {
            r.run();
            System.out.println("PASS: " + name);
            passed++;
        } catch (Throwable t) {
            System.out.println("FAIL: " + name);
            System.out.println(" " + t.getMessage());
            failed++;
        }
    }

    /**
     * Method: assertTrue
     * 
     * @param condition condition being tested
     * @message fail message
     */
    private static void assertTrue(boolean condition, String message){
        if(!condition) throw new AssertionError(message);
    }

    /**
     * Method: printSummary
     * 
     * Prints test result summary
     */
    private static void printSummary(){
        System.out.println("\nAuthenticationTests complete.");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
    }
    
}
