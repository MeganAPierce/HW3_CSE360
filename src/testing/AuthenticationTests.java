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
        Database db = new Database();
        db.connectToDatabase();

        clearUserTable(db);

        test("Valid role1 login succeeds", () -> {
            User user = new User(
                "student1",
                "password123",
                "Megan",
                "",
                "Pierce",
                "Megan",
                "megan@test.com",
                false,
                true,
                false
            );

            db.register(user);
            boolean result = db.loginRole1(user);
            assertTrue(result, "Expected valid role1 login to succeed.");
        });

        test("Wrong password fails", () -> {
            User realUser = new User(
                "student2",
                "realpass",
                "A",
                "",
                "B",
                "A",
                "a@test.com",
                false,
                true,
                false
            );

            db.register(realUser);

            User wrongPasswordUser = new User(
                "student2",
                "wrongpass",
                "A",
                "",
                "B",
                "A",
                "a@test.com",
                false,
                true,
                false
            );

            boolean result = db.loginRole1(wrongPasswordUser);
            assertTrue(!result, "Expected login with wrong password to fail.");
        });

        test("Nonexistent user fails", () -> {
            User fakeUser = new User(
                "notARealUser",
                "password123",
                "Fake",
                "",
                "User",
                "Fake",
                "fake@test.com",
                false,
                true,
                false
            );

            boolean result = db.loginRole1(fakeUser);
            assertTrue(!result, "Expected nonexistent user login to fail.");
        });

        test("Blank username fails", () -> {
            User blankUsername = new User(
                "",
                "password123",
                "Blank",
                "",
                "User",
                "Blank",
                "blank@test.com",
                false,
                true,
                false
            );

            boolean result = db.loginRole1(blankUsername);
            assertTrue(!result, "Expected blank username login to fail.");
        });

        test("Blank password fails", () -> {
            User user = new User(
                "student3",
                "realpass",
                "Test",
                "",
                "User",
                "Test",
                "test@test.com",
                false,
                true,
                false
            );

            db.register(user);

            User blankPassword = new User(
                "student3",
                "",
                "Test",
                "",
                "User",
                "Test",
                "test@test.com",
                false,
                true,
                false
            );

            boolean result = db.loginRole1(blankPassword);
            assertTrue(!result, "Expected blank password login to fail.");
        });

        test("Wrong role login fails", () -> {
            User adminOnly = new User(
                "admin1",
                "adminpass",
                "Admin",
                "",
                "User",
                "Admin",
                "admin@test.com",
                true,
                false,
                false
            );

            db.register(adminOnly);

            boolean result = db.loginRole1(adminOnly);
            assertTrue(!result, "Expected role1 login for admin-only account to fail.");
        });

        test("Valid admin login succeeds", () -> {
            User admin = new User(
                "admin2",
                "securepass",
                "Admin",
                "",
                "User",
                "Admin",
                "admin2@test.com",
                true,
                false,
                false
            );

            db.register(admin);

            boolean result = db.loginAdmin(admin);
            assertTrue(result, "Expected valid admin login to succeed.");
        });

        printSummary();
    }
    
    @FunctionalInterface
    private interface ThrowingRunnable{
    	void run() throws Exception;
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
    private static void test(String name, ThrowingRunnable r){
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
