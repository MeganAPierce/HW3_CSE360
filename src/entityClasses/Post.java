package entityClasses;

import java.time.LocalDateTime;
/**
 * <p>Represents a discussion post in the student discussion forum.  
 * This class stores the core information required for a student-created post, 
 * including a unique identifier, the user's username, the thread it belongs to, the post 
 * title and body, and the timestamps used to track creation, updates and deletion.</p>
 * 
 * <p>The post class supports the user stories by providing the data and operations 
 * needed to create posts, edit posts, group posts into threads, determine authorship,
 * and soft delete posts without permanently removing them from the database. Soft deletion preserves
 * the record for later review, testing and possible staff-side review.</p>
 */
public class Post {
	
	/**
	 * Unique identifier for the post.
	 * <p> This value is used to retrieve, update and/or delete a specific post</p>*/
    private final String postId;
    /**
     * Username of the user who created the post.
     * <p>This is used to track authorship, which can be used to check ownership rules for editing, etc.</p>*/
    private final String authorUsername;
    /**
     * Name of the discussion thread that the post belongs to.
     * <p>This allows posts to be grouped by topic, so they can be displayed and navigated through in the front end</p>*/
    private String threadName;
    /**
     * Title of the post.
     * <p>This provides a header for a post.</p>*/
    private String title;
    /**
     * The main text of the post.
     * <p>This stores the message of the post.</p>
     * */
    private String body;
    /**
     * Timestamp for post creation.
     * <p>This stores the initial time when the post was created.</p>*/
    private final LocalDateTime createdAt;
    
    /**
     * Timestamp for edits and/or deletions of a post
     * <p>This is updated whenever a user updates a post, edits a post or soft deletes a post. </p>*/
    private LocalDateTime updatedAt;
    /**
     * Boolean that indicates if a post has been soft deleted.
     * <p>Soft deletion keeps the post info stored in the database but unaccessible from the front end</p>*/
    private boolean deleted;
    /**
	 * Timestamp for when a post was soft deleted.
	 * <p>This is set when a post is soft deleted, and can be used to track when the deletion occurred.</p>*/
    private LocalDateTime deletedAt;
    
    /**
     * This is a default thread name when a valid thread name isn't provided.
     * */
    public static final String DEFAULT_THREAD = "General";
    /**
     * When a post is soft deleted, this stores the deleted title. 
     * */
    public static final String DELETED_TITLE = "[Deleted Post]";
    /**
     * When a post is soft deleted, this stores the deleted body.
     * */
    public static final String DELETED_BODY = "The original post has been deleted.";

    /**
     * This constructs a new post.
     * 
     * <p> This constructor is used when a user creates a new post through the discussion system. 
     * It initializes the post with the provided information, sets the creation and update timestamps
     * to the current time, and marks the post as not deleted.</p>
     * 
     * @param postId unique identifier for the post
     * @param authorUsername username of the post's author
     * @param threadName name of the thread the post belongs to (if null or empty, defaults to "General")
     * @param title title of the post (if null, defaults to an empty string)
     * @param body body of the post (if null, defaults to an empty string)
     * */
    public Post(String postId, String authorUsername, String threadName, String title, String body) {
        this.postId = postId;
        this.authorUsername = authorUsername;
        this.threadName = (threadName == null || threadName.trim().isEmpty())
                ? DEFAULT_THREAD
                : threadName.trim();
        this.title = title == null ? "" : title.trim();
        this.body = body == null ? "" : body.trim();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.deleted = false;
        this.deletedAt = null;
    }

    /**
     * Constructor used when loading a post from the database.
     * 
     * <p> This constructor is used when retrieving a post's data from the database.
     * It initializes the post with all of the provided information,
     * including authorship, thread, title, body, and all timestamps. This allows the post to be 
     * fully reconstructed with its original data, including any edits or deletions that may have occurred.</p>
     * 
     * @param postId unique identifier for the post
     * @param authorUsername username of the post's author
     * @param threadName name of the thread the post belongs to (if null or empty, defaults to "General")
     * @title title of the post (if null, defaults to an empty string)
     * @body body of the post (if null, defaults to an empty string)
     * @createdAt timestamp for when the post was created
     * @updatedAt timestamp for when the post was last updated
     * @deleted boolean indicating if the post has been soft deleted
     * @deletedAt timestamp for when the post was soft deleted (if applicable)
     * */
    public Post(String postId, String authorUsername, String threadName, String title, String body,
                LocalDateTime createdAt, LocalDateTime updatedAt,
                boolean deleted, LocalDateTime deletedAt) {
        this.postId = postId;
        this.authorUsername = authorUsername;
        this.threadName = (threadName == null || threadName.trim().isEmpty())
                ? DEFAULT_THREAD
                : threadName.trim();
        this.title = title == null ? "" : title.trim();
        this.body = body == null ? "" : body.trim();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
    }

    /**
    * Returns the unique identifier of the post.
    *
    * @return the post ID
    */
    public String getPostId() { return postId; }

    /**
    * Returns the username of the author of the post.
    *
    * @return the authorUsername
    */
    public String getAuthorUsername() { return authorUsername; }

    /**
    * Returns the threadname of the post.
    *
    * @return the post threadName
    */
    public String getThreadName() { return threadName; }

    /**
    * Returns the title of the post.
    *
    * @return the post title
    */
    public String getTitle() { return title; }


    /**
    * Returns the bodyof the post.
    *
    * @return the post body
    */
    public String getBody() { return body; }

    /**
    * Returns the creation timestamp of the post.
    *
    * @return the post creation timestamp
    */
    public LocalDateTime getCreatedAt() { return createdAt; }


    /**
    * Returns the most recent update timestamp of the post.
    *
    * @return the post update timestamp
    */
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    /**
    * Returns a boolean that indicates whether or not a post has been soft deleted.
    *
    * @return true if post has been deleted, false if not 
    */
    public boolean isDeleted() { return deleted; }

    /**
    * Returns the deletion timestamp of the post when its been soft deleted.
    *
    * @return the deletion timestamp or null if post is not deleted
    */
    public LocalDateTime getDeletedAt() { return deletedAt; }


    /**
     * Updates the content of a post.
     * 
     * <p>This method supports the student user story that allows users to 
     * modify existing posts. The thread name, title and body may be updated. The
     * updatedAt timestamp is updated. If the post has been deleted it will not
     * be updated.</p>
     * 
     *
     * @param newThreadName new thread name to assign if not blank
     * @param newTitle new title to assign if not null
     * @param newBody new message to assign if not null
     * @throws IllegalStateException if the post has already been deleted
     */
    public void updatePost(String newThreadName, String newTitle, String newBody) {
        if (deleted) {
            throw new IllegalStateException("Cannot update a deleted post.");
        }

        if (newThreadName != null && !newThreadName.trim().isEmpty()) {
            this.threadName = newThreadName.trim();
        }

        if (newTitle != null) {
            this.title = newTitle.trim();
        }

        if (newBody != null) {
            this.body = newBody.trim();
        }

        this.updatedAt = LocalDateTime.now();
    }

    /**
     * This method soft deletes a post.
     * <p>This method does not remove the post object from the system entirely, but 
     * it marks the post as "Deleted" and keeps the records. This supports delete behavior
     * while preserving the information for later review if necessary.</p>
     */
    public void softDelete() {
        if (!deleted) {
            deleted = true;
            deletedAt = LocalDateTime.now();
            title = DELETED_TITLE;
            body = DELETED_BODY;
            updatedAt = deletedAt;
        }
    }

    /**
     * Determines whether the given username is the author of the post.
     * <p>This helper method supports ownership checks so the controller will be
     * able to determine if a user should be allowed to edit a post or delete it. 
     * 
     * @param username the username to use to compare to the post authorUsername
     * @return true if the username and authorUsername match, false otherwise
     * */
    public boolean isAuthor(String username){
        return username != null && authorUsername.equals(username.trim());
    }

};