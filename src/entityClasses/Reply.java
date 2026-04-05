//Victor Figueroa
//CSE360 TP01
package entityClasses;

import java.time.LocalDateTime;

public class Reply {
    
    private final String replyId;
    private final String postId;
    private final String authorUsername;
    private String body;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private boolean deleted;
    private LocalDateTime deletedAt;

    public static final String DELETED_BODY = "[Deleted Reply]";

    //constructor for creating a new reply
    public Reply(String replyId, String postId, String authorUsername, String body) {

        this.replyId = replyId;
        this.postId = postId;
        this.authorUsername = authorUsername;
        this.body = body == null ? "" : body.trim();

        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;

        this.deleted = false;
        this.deletedAt = null;
    }

    //constructor for loading an existing reply from the database
    public Reply(String replyId, String postId, String authorUsername, String body, LocalDateTime createdAt, LocalDateTime updatedAt,boolean deleted, LocalDateTime deletedAt) {
        this.replyId = replyId;
        this.postId = postId;
        this.authorUsername = authorUsername;
        this.body = body == null ? "" : body.trim();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
    }

    public String getReplyId() { return replyId; }

    public String getPostId() { return postId; }

    public String getAuthorUsername() { return authorUsername; }

    public String getBody() { return body; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public boolean isDeleted() { return deleted; } //for soft delete, we want to keep the reply but mark it as deleted

    public LocalDateTime getDeletedAt() { return deletedAt; }

    public void updateBody(String newBody) {

        if (deleted)
            throw new IllegalStateException("Cannot update a deleted reply.");

        if (newBody != null)
            body = newBody.trim();

        updatedAt = LocalDateTime.now();
    }

    public void softDelete() {

        if (!deleted) {
            deleted = true;
            deletedAt = LocalDateTime.now();
            body = DELETED_BODY;
            updatedAt = deletedAt;
        }
    }

    //Permissions helpers
    //check if user is the author
    public boolean isAuthor(String username){
        return username != null && authorUsername.equals(username.trim());
    }
}
