package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;

/**
 * This class handles all database operations related to replies in the discussion system.
 *
 * It is responsible for creating, reading, updating, deleting (soft delete), and searching replies.
 * Each reply is tied to a specific post using postId so that replies stay grouped correctly.
 *
 * This class supports the student discussion system by allowing users to:
 * - create replies to posts
 * - view replies for a post
 * - edit their own replies
 * - delete replies using soft deletion
 * - search for replies
 *
 * Replies are not permanently removed from the database. Instead, they are marked as deleted
 * so the system can still keep track of activity and history.
 *
 * Validation and permission checks are included to make sure only valid data is stored and
 * only the author of a reply can modify or delete it.
 */

public class ReplyStore{

    private final Connection conn;

    public ReplyStore(Database db) {
        this.conn = db.getConnection();
    }


    /**
     * Creates a new reply for a given post.
     *
     * Checks that the post exists and is not deleted before allowing the reply.
     * Also validates the input before inserting into the database.
     *
     * @param postId the ID of the post being replied to
     * @param postStore used to verify that the post exists
     * @param authorUsername the user creating the reply
     * @param body the reply content
     * @return a Result containing the created reply or an error message
     */
    public Result<Reply> createReply(String postId, PostStore postStore, String authorUsername, String body) {
        if (postId == null || postId.trim().isEmpty()) {
            return Result.fail("ERROR: Post ID cannot be empty.");
        }

        Result<Post> postCheck = postStore.readPostById(postId);
        if (!postCheck.isOk()) {
            return Result.fail("ERROR: Cannot create reply — " + postCheck.getError());
        }

        if (postCheck.getValue().isDeleted()) {
            return Result.fail("ERROR: Cannot reply to a deleted post.");
        }

        String error = validateReply(authorUsername, body);
        if (!error.isEmpty()) return Result.fail(error);

        String replyId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        String sql = """
                INSERT INTO REPLIES
                (REPLY_ID, POST_ID, AUTHOR_USERNAME, BODY, CREATED_AT, UPDATED_AT, DELETED, DELETED_AT)
                VALUES (?,?,?,?,?,?,?,?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, replyId);
            ps.setString(2, postId.trim());
            ps.setString(3, authorUsername.trim());
            ps.setString(4, body.trim());
            ps.setTimestamp(5, Timestamp.valueOf(now));
            ps.setTimestamp(6, Timestamp.valueOf(now));
            ps.setBoolean(7, false);
            ps.setTimestamp(8, null);
            ps.executeUpdate();
            return readReplyById(replyId);
        } catch (SQLException e) {
            return Result.fail("ERROR: Database error creating reply: " + e.getMessage());
        }
    }

    /**
     * Retrieves a single reply using its ID.
     *
     * @param replyId the ID of the reply
     * @return a Result containing the reply or an error if not found
     */
    public Result<Reply> readReplyById(String replyId) {
        if (replyId == null || replyId.trim().isEmpty()) {
            return Result.fail("ERROR: Reply ID cannot be empty.");
        }

        String sql = "SELECT * FROM REPLIES WHERE REPLY_ID=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, replyId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Result.fail("ERROR: Reply ID " + replyId + " not found.");
                return Result.ok(mapReply(rs));
            }
        } catch (SQLException e) {
            return Result.fail("ERROR: Database error reading reply: " + e.getMessage());
        }
    }

    /**
     * Gets all replies associated with a specific post.
     *
     * @param postId the ID of the post
     * @return a list of replies for that post
     */
    public List<Reply> readRepliesByPostId(String postId) {
        List<Reply> replies = new ArrayList<>();

        if (postId == null || postId.trim().isEmpty()) {
            return replies;
        }

        String sql = "SELECT * FROM REPLIES WHERE POST_ID=? ORDER BY CREATED_AT ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, postId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    replies.add(mapReply(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return replies;
    }

    /**
     * Gets all replies created by a specific user.
     *
     * @param authorUsername the username of the author
     * @return a list of replies by that user
     */
    public List<Reply> readRepliesByAuthor(String authorUsername) {
        List<Reply> replies = new ArrayList<>();

        if (authorUsername == null || authorUsername.trim().isEmpty()) {
            return replies;
        }

        String sql = "SELECT * FROM REPLIES WHERE AUTHOR_USERNAME=? ORDER BY CREATED_AT DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, authorUsername.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    replies.add(mapReply(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return replies;
    }

    /**
     * Updates the body of an existing reply.
     *
     * Ensures the reply exists, is not deleted, and passes validation before updating.
     *
     * @param replyId the ID of the reply to update
     * @param newBody the new reply content
     * @return a Result containing the updated reply or an error message
     */
    public Result<Reply> updateReply(String replyId, String newBody) {
        Result<Reply> replyCheck = readReplyById(replyId);
        if (!replyCheck.isOk()) return Result.fail("ERROR: Cannot update — " + replyCheck.getError());

        Reply existingReply = replyCheck.getValue();
        if (existingReply.isDeleted()) return Result.fail("ERROR: Deleted replies cannot be updated.");

        String error = validateReply(existingReply.getAuthorUsername(), newBody);
        if (!error.isEmpty()) return Result.fail(error);

        LocalDateTime now = LocalDateTime.now();

        String sql = "UPDATE REPLIES SET BODY=?, UPDATED_AT=? WHERE REPLY_ID=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newBody.trim());
            ps.setTimestamp(2, Timestamp.valueOf(now));
            ps.setString(3, replyId.trim());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) return Result.fail("ERROR: Reply ID " + replyId + " not found for update.");
            return readReplyById(replyId);
        } catch (SQLException e) {
            return Result.fail("ERROR: Database error updating reply: " + e.getMessage());
        }
    }

    /**
     * Marks a reply as deleted without removing it from the database.
     *
     * The reply body is replaced with a default message and the deletion time is recorded.
     *
     * @param replyId the ID of the reply to delete
     * @return a Result indicating success or failure
     */
    public Result<Void> softDeleteReplyById(String replyId) {
        Result<Reply> existingResult = readReplyById(replyId);
        if (!existingResult.isOk()) {
            return Result.fail("ERROR: Cannot delete — " + existingResult.getError());
        }

        Reply existingReply = existingResult.getValue();
        if (existingReply.isDeleted()) {
            return Result.fail("ERROR: Reply ID " + replyId + " is already deleted.");
        }

        LocalDateTime now = LocalDateTime.now();
        String sql = """
                UPDATE REPLIES
                SET DELETED = ?, DELETED_AT = ?, BODY = ?, UPDATED_AT = ?
                WHERE REPLY_ID = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, true);
            ps.setTimestamp(2, Timestamp.valueOf(now));
            ps.setString(3, Reply.DELETED_BODY);
            ps.setTimestamp(4, Timestamp.valueOf(now));
            ps.setString(5, replyId.trim());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                return Result.fail("ERROR: Reply ID " + replyId + " not found for deletion.");
            }

            return Result.ok(null);
        } catch (SQLException e) {
            return Result.fail("ERROR: Database error deleting reply: " + e.getMessage());
        }
    }

    /**
     * Searches for replies containing a keyword in the body.
     *
     * @param keyword the search term
     * @return a list of matching replies
     */
    public List<Reply> searchRepliesByKeyword(String keyword) {
        List<Reply> replies = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return replies;
        }

        String sql = """
                SELECT * FROM REPLIES
                WHERE LOWER(BODY) LIKE ?
                ORDER BY CREATED_AT DESC
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            ps.setString(1, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    replies.add(mapReply(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return replies;
    }

    /**
     * Searches for replies containing a keyword within a specific post.
     *
     * @param postId the ID of the post
     * @param keyword the search term
     * @return a list of matching replies for that post
     */
    public List<Reply> searchRepliesByKeywordForPost(String postId, String keyword) {
        List<Reply> replies = new ArrayList<>();

        if (postId == null || postId.trim().isEmpty() || keyword == null || keyword.trim().isEmpty()) {
            return replies;
        }

        String sql = """
                SELECT * FROM REPLIES
                WHERE POST_ID = ?
                  AND LOWER(BODY) LIKE ?
                ORDER BY CREATED_AT ASC
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, postId.trim());
            ps.setString(2, "%" + keyword.trim().toLowerCase() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    replies.add(mapReply(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return replies;
    }

    /**
     * Converts a database row into a Reply object.
     *
     * @param rs the ResultSet from the query
     * @return a Reply object
     * @throws SQLException if there is an issue reading the data
     */
    private Reply mapReply(ResultSet rs) throws SQLException {
        Timestamp deletedTimestamp = rs.getTimestamp("DELETED_AT");
        return new Reply(
            rs.getString("REPLY_ID"),
            rs.getString("POST_ID"),
            rs.getString("AUTHOR_USERNAME"),
            rs.getString("BODY"),
            rs.getTimestamp("CREATED_AT").toLocalDateTime(),
            rs.getTimestamp("UPDATED_AT").toLocalDateTime(),
            rs.getBoolean("DELETED"),
            deletedTimestamp == null ? null : deletedTimestamp.toLocalDateTime()
        );
    }
    
    /**
     * Validates reply input before database operations.
     *
     * Checks for empty values and length limits.
     *
     * @param authorUsername the reply author
     * @param body the reply content
     * @return an error message if invalid, otherwise an empty string
     */
    public String validateReply(String authorUsername, String body) {
        if (authorUsername == null || authorUsername.trim().isEmpty()) {
            return "ERROR: Author username cannot be empty.";
        }
        if (body == null || body.trim().isEmpty()) {
            return "ERROR: Body cannot be empty.";
        }
        if (authorUsername.trim().length() > 100) {
            return "ERROR: Author username cannot exceed 100 characters.";
        }
        if (body.trim().length() > 2000) {
            return "ERROR: Body cannot exceed 2000 characters.";
        }
        return "";
    }

    /**
     * Checks if a user is allowed to edit a reply.
     *
     * @param user the current user
     * @param reply the reply being checked
     * @return true if the user is the author
     */
    public boolean canEditReply(User user, Reply reply){
        return user != null && reply != null && reply.isAuthor(user.getUserName());
    }
    /**
     * Checks if a user is allowed to delete a reply.
     *
     * @param user the current user
     * @param reply the reply being checked
     * @return true if the user is the author
     */
    public boolean canDeleteReply(User user, Reply reply){
        return user != null && reply != null && reply.isAuthor(user.getUserName());
    }

}