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
import entityClasses.User;


/**
 * This class handles all database operations related to posts in the discussion system.
 *
 * It is responsible for creating, reading, updating, deleting (soft delete), and searching posts.
 * Posts are grouped by threadName so they can be organized by topic.
 *
 * This class supports the student discussion system by allowing users to:
 * - create posts
 * - view posts (all, by thread, or by author)
 * - edit their own posts
 * - delete posts using soft deletion
 * - search posts by keyword
 *
 * Posts are not permanently removed from the database. Instead, they are marked as deleted
 * so the system can still keep track of post history.
 *
 * Validation and permission helper methods are included to ensure valid data and to allow
 * the controller to enforce ownership rules.
 */


public class PostStore {

    private final Connection conn;

    public PostStore(Database db) {
        this.conn = db.getConnection();
    }

    /**
     * Creates a new post and stores it in the database.
     *
     * Validates input and assigns a unique postId and timestamps.
     *
     * @param authorUsername the user creating the post
     * @param threadName the thread the post belongs to
     * @param title the title of the post
     * @param body the post content
     * @return a Result containing the created post or an error message
     */
    public Result<Post> createPost(String authorUsername, String threadName, String title, String body) {
        String error = validatePost(authorUsername, threadName, title, body);
        if (!error.isEmpty()) return Result.fail(error);

        String postId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        String sql = """
                INSERT INTO POSTS
                (POST_ID, AUTHOR_USERNAME, THREAD_NAME, TITLE, BODY, CREATED_AT, UPDATED_AT, DELETED, DELETED_AT)
                VALUES (?,?,?,?,?,?,?,?,?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, postId);
            ps.setString(2, authorUsername.trim());
            ps.setString(3, threadName.trim());
            ps.setString(4, title.trim());
            ps.setString(5, body.trim());
            ps.setTimestamp(6, Timestamp.valueOf(now));
            ps.setTimestamp(7, Timestamp.valueOf(now));
            ps.setBoolean(8, false);
            ps.setTimestamp(9, null);
            ps.executeUpdate();
            return readPostById(postId);
        } catch (SQLException e) {
            return Result.fail("ERROR: Database error creating post: " + e.getMessage());
        }
    }

    /**
     * Retrieves a post using its ID.
     *
     * @param postId the ID of the post
     * @return a Result containing the post or an error if not found
     */
    public Result<Post> readPostById(String postId){
        //acount for if postid is null 
        if(postId == null || postId.trim().isEmpty()) {
            return Result.fail("ERROR: Post ID cannot be empty.");
        }

        String sql = "SELECT * FROM POSTS WHERE POST_ID=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, postId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Result.fail("ERROR: Post ID " + postId + " not found.");
                return Result.ok(mapPost(rs));
            }
        } catch (SQLException e) {
            return Result.fail("ERROR: Database error reading post: " + e.getMessage());
        }
    }


    /**
     * Retrieves all posts from the database.
     *
     * @return a list of all posts ordered by most recent
     */
    public List<Post> readAllPosts() {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM POSTS ORDER BY CREATED_AT DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    posts.add(mapPost(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    /**
     * Retrieves all posts for a specific thread.
     *
     * @param threadName the thread name
     * @return a list of posts in that thread
     */
    public List<Post> readPostsByThread(String threadName) {
        List<Post> posts = new ArrayList<>();
        if (threadName == null || threadName.trim().isEmpty()) {
            return posts;
        }

        String sql = "SELECT * FROM POSTS WHERE THREAD_NAME=? ORDER BY CREATED_AT DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, threadName.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    posts.add(mapPost(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    /**
     * Retrieves all posts created by a specific user.
     *
     * @param authorUsername the username of the author
     * @return a list of posts by that user
     */
    public List<Post> readPostsByAuthor(String authorUsername) {
        List<Post> posts = new ArrayList<>();
        if (authorUsername == null || authorUsername.trim().isEmpty()) {
            return posts;
        }

        String sql = "SELECT * FROM POSTS WHERE AUTHOR_USERNAME = ? ORDER BY CREATED_AT DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, authorUsername.trim());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    posts.add(mapPost(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return posts;
    }
    
    /**
     * Updates an existing post.
     *
     * Ensures the post exists, is not deleted, and passes validation before updating.
     *
     * @param postId the ID of the post to update
     * @param newThreadName the new thread name
     * @param newTitle the new title
     * @param newBody the new body content
     * @return a Result containing the updated post or an error message
     */ 
    public Result<Post> updatePost(String postId, String newThreadName, String newTitle, String newBody) {
        Result<Post> postCheck = readPostById(postId);
        if (!postCheck.isOk()) return Result.fail("ERROR: Cannot update — " + postCheck.getError());

        Post existingPost = postCheck.getValue();
        if (existingPost.isDeleted()) return Result.fail("ERROR: Deleted posts cannot be updated.");

        String error = validatePost(existingPost.getAuthorUsername(), newThreadName, newTitle, newBody);
        if (!error.isEmpty()) return Result.fail(error);

        LocalDateTime now = LocalDateTime.now();

        String sql = "UPDATE POSTS SET THREAD_NAME=?, TITLE=?, BODY=?, UPDATED_AT=? WHERE POST_ID=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newThreadName.trim());
            ps.setString(2, newTitle.trim());
            ps.setString(3, newBody.trim());
            ps.setTimestamp(4, Timestamp.valueOf(now));
            ps.setString(5, postId.trim());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) return Result.fail("ERROR: Post ID " + postId + " not found for update.");
            return readPostById(postId);
        } catch (SQLException e) {
            return Result.fail("ERROR: Database error updating post: " + e.getMessage());
        }
    }

    /**
     * Marks a post as deleted without removing it from the database.
     *
     * The title and body are replaced with default deleted values and the deletion time is recorded.
     *
     * @param postId the ID of the post to delete
     * @return a Result indicating success or failure
     */  
    public Result<Void> softDeletePostById(String postId) {
        Result<Post> existingResult = readPostById(postId);
        if (!existingResult.isOk()) {
            return Result.fail("ERROR: Cannot delete — " + existingResult.getError());
        }
        Post existingPost = existingResult.getValue();

        if (existingPost.isDeleted()) {
            return Result.fail("ERROR: Post ID " + postId + " is already deleted.");
        }

        LocalDateTime now = LocalDateTime.now();
        String sql = """
                UPDATE POSTS
                SET DELETED = ?, DELETED_AT = ?, TITLE = ?, BODY = ?, UPDATED_AT = ?
                WHERE POST_ID = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, true);
            ps.setTimestamp(2, Timestamp.valueOf(now));
            ps.setString(3, Post.DELETED_TITLE);
            ps.setString(4, Post.DELETED_BODY);
            ps.setTimestamp(5, Timestamp.valueOf(now));
            ps.setString(6, postId.trim());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                return Result.fail("ERROR: Post ID " + postId + " not found for deletion.");
            }

            return Result.ok(null);
        } catch (SQLException e) {
            return Result.fail("ERROR: Database error deleting post: " + e.getMessage());
        }
    }

    /**
     * Searches for posts containing a keyword in the title, body, or thread name.
     *
     * @param keyword the search term
     * @return a list of matching posts
     */ 
    public List<Post> searchPostsByKeyword(String keyword) {
        List<Post> posts = new ArrayList<>();

        if(keyword == null || keyword.trim().isEmpty()) {
            return posts;
        }

        String sql = """
                SELECT * FROM POSTS 
                WHERE LOWER(TITLE) LIKE ?
                     OR LOWER(BODY) LIKE ?
                     OR LOWER(THREAD_NAME) LIKE ?
                ORDER BY CREATED_AT DESC
                """;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    posts.add(mapPost(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    /**
     * Converts a database row into a Post object.
     *
     * @param rs the ResultSet from the query
     * @return a Post object
     * @throws SQLException if there is an issue reading the data
     */
    private Post mapPost(ResultSet rs) throws SQLException {
        Timestamp deletedTimestamp = rs.getTimestamp("DELETED_AT");
        return new Post(
            rs.getString("POST_ID"),
            rs.getString("AUTHOR_USERNAME"),
            rs.getString("THREAD_NAME"),
            rs.getString("TITLE"),
            rs.getString("BODY"),
            rs.getTimestamp("CREATED_AT").toLocalDateTime(),
            rs.getTimestamp("UPDATED_AT").toLocalDateTime(),
            rs.getBoolean("DELETED"),
            deletedTimestamp == null ? null : deletedTimestamp.toLocalDateTime()
        );
    }

    
    /**
     * Validates post input before database operations.
     *
     * Checks for empty values and length limits.
     *
     * @param authorUsername the post author
     * @param threadName the thread name
     * @param title the post title
     * @param body the post content
     * @return an error message if invalid, otherwise an empty string
     */
    public String validatePost(String authorUsername, String threadName, String title, String body) {
        if (authorUsername == null || authorUsername.trim().isEmpty()) {
            return "ERROR: Author username cannot be empty.";
        }
        if (threadName == null || threadName.trim().isEmpty()) {
            return "ERROR: Thread name cannot be empty.";
        }

        if (title == null || title.trim().isEmpty()) {
            return "ERROR: Title cannot be empty.";
        }
        if (body == null || body.trim().isEmpty()) {
            return "ERROR: Body cannot be empty.";
        }
        if (authorUsername.trim().length() > 100) {
            return "ERROR: Author username cannot exceed 100 characters.";
        }
        if (threadName.trim().length() > 100) {
            return "ERROR: Thread name cannot exceed 100 characters.";
        }
        if (title.trim().length() > 200) {
            return "ERROR: Title cannot exceed 200 characters.";
        }
        if (body.trim().length() > 5000) {
            return "ERROR: Body cannot exceed 5000 characters.";
        }
        return "";
    }

    //Thread helpers
    /**
     * Retrieves all unique thread names from the database.
     *
     * @return a list of thread names
     */
    public List<String> readAllThreadNames() {
        List<String> threadNames = new ArrayList<>();
        String sql = "SELECT DISTINCT THREAD_NAME FROM POSTS ORDER BY THREAD_NAME ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)){
            try (ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    threadNames.add(rs.getString("THREAD_NAME"));
                }
            }
        }catch(SQLException e){
            System.out.println("Database error getting threadnames: " + e.getMessage());
        }
        return threadNames;
    }

    /**
     * Checks if a thread exists in the database.
     *
     * @param threadName the thread name
     * @return true if the thread exists, false otherwise
     */
    public boolean threadExists(String threadName){
        if (threadName == null || threadName.trim().isEmpty()){
            return false;
        }
        
        String sql = "SELECT 1 FROM POSTS WHERE THREAD_NAME = ? LIMIT 1";

        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, threadName.trim());
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }catch (SQLException e){
            System.out.println("Database error checking for thread existence: " + e.getMessage());
        }
        return false;
    }


    //Permission helpers

    /**
     * Checks if a user is allowed to edit a post.
     *
     * @param user the current user
     * @param post the post being checked
     * @return true if the user is the author
     */
    public boolean canEditPost(User user, Post post){
        return user != null && post != null && post.isAuthor(user.getUserName());
    }
    /**
     * Checks if a user is allowed to delete a post.
     *
     * @param user the current user
     * @param post the post being checked
     * @return true if the user is the author
     */
    public boolean canDeletePost(User user, Post post){
        return user != null && post != null && post.isAuthor(user.getUserName());
    }


}