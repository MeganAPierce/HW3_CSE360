package testing;

import database.PrivateInteractionAuthorizationService;
import entityClasses.PrivateReply;
import entityClasses.StaffPrivatePost;
import entityClasses.User;

/**
 * Tests for the PrivateInteractionAuthorizationService prototype.
 */
public class PrivateInteractionAuthorizationServiceTests {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        PrivateInteractionAuthorizationService service = new PrivateInteractionAuthorizationService();

        User staffAuthor = new User(
            "staff1", "pw",
            "Staff", "", "Author", "Staff",
            "staff1@test.com",
            false, false, true
        );

        User recipientStudent = new User(
            "student1", "pw",
            "Student", "", "Recipient", "Student",
            "student1@test.com",
            false, true, false
        );

        User unrelatedStudent = new User(
            "student2", "pw",
            "Other", "", "Student", "Other",
            "student2@test.com",
            false, true, false
        );

        User adminUser = new User(
            "admin1", "pw",
            "Admin", "", "User", "Admin",
            "admin@test.com",
            true, false, false
        );

        StaffPrivatePost privatePost = new StaffPrivatePost(
            "pp1",
            "staff1",
            "student1"
        );

        PrivateReply replyByRecipient = new PrivateReply(
            "pr1",
            "student1",
            "pp1"
        );

        PrivateReply replyByAuthor = new PrivateReply(
            "pr2",
            "staff1",
            "pp1"
        );

        test("Staff can create private post", () -> {
            assertTrue(service.canCreatePrivatePost(staffAuthor),
                "Expected staff user to be allowed to create private post.");
        });

        test("Admin can create private post", () -> {
            assertTrue(service.canCreatePrivatePost(adminUser),
                "Expected admin user to be allowed to create private post.");
        });

        test("Student cannot create private post", () -> {
            assertTrue(!service.canCreatePrivatePost(recipientStudent),
                "Expected student to be blocked from creating private post.");
        });

        test("Author can view private post", () -> {
            assertTrue(service.canViewPrivatePost(staffAuthor, privatePost),
                "Expected author to be allowed to view private post.");
        });

        test("Recipient can view private post", () -> {
            assertTrue(service.canViewPrivatePost(recipientStudent, privatePost),
                "Expected recipient to be allowed to view private post.");
        });

        test("Unrelated user cannot view private post", () -> {
            assertTrue(!service.canViewPrivatePost(unrelatedStudent, privatePost),
                "Expected unrelated user to be blocked from viewing private post.");
        });

        test("Author can reply to private post", () -> {
            assertTrue(service.canReplyToPrivatePost(staffAuthor, privatePost),
                "Expected author to be allowed to reply to private post.");
        });

        test("Recipient can reply to private post", () -> {
            assertTrue(service.canReplyToPrivatePost(recipientStudent, privatePost),
                "Expected recipient to be allowed to reply to private post.");
        });

        test("Unrelated user cannot reply to private post", () -> {
            assertTrue(!service.canReplyToPrivatePost(unrelatedStudent, privatePost),
                "Expected unrelated user to be blocked from replying.");
        });

        test("Reply author can edit own reply", () -> {
            assertTrue(service.canEditPrivateReply(recipientStudent, replyByRecipient),
                "Expected reply author to be allowed to edit own reply.");
        });

        test("Non-author cannot edit reply", () -> {
            assertTrue(!service.canEditPrivateReply(unrelatedStudent, replyByRecipient),
                "Expected non-author to be blocked from editing reply.");
        });

        test("Reply author can delete own reply", () -> {
            assertTrue(service.canDeletePrivateReply(staffAuthor, replyByAuthor),
                "Expected reply author to be allowed to delete own reply.");
        });

        test("Non-author cannot delete reply", () -> {
            assertTrue(!service.canDeletePrivateReply(recipientStudent, replyByAuthor),
                "Expected non-author to be blocked from deleting reply.");
        });

        test("Null user cannot view private post", () -> {
            assertTrue(!service.canViewPrivatePost(null, privatePost),
                "Expected null user to be blocked.");
        });

        test("Null post cannot be viewed", () -> {
            assertTrue(!service.canViewPrivatePost(staffAuthor, null),
                "Expected null post to be blocked.");
        });

        printSummary();
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private static void test(String name, ThrowingRunnable r) {
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

    private static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void printSummary() {
        System.out.println("\nPrivateInteractionAuthorizationServiceTests complete.");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
    }
}