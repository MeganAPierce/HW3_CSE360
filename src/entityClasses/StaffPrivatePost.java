package entityClasses;

/**
 * Title: StaffPrivatePost
 * @author Megan Pierce
 * 
 * Description: A lightweight prototype model representing a private post created by staff for a specific recipient
 */
public class StaffPrivatePost {
    private String postId;
    private String authorUserName;
    private String recipientUserName;

    /**
     * StaffPrivatePost
     * Construcst a StaffPrivatePost object
     * 
     * @param postId unique postID - identifier for the post
     * @param authorUserName the staff user who created the post
     * @param recipientUserName the intended recipient of the post
     */
    public StaffPrivatePost(String postId, String authorUserName, String recipientUserName){
        this.postId = postId;
        this.authorUserName = authorUserName;
        this.recipientUserName = recipientUserName;
    }

    /**
     * 
     * @return the post ID
     */
    public String getPostId(){
        return postId;
    }
    /**
     * 
     * @return the username of the staff author
     */
    public String getAuthorUserName(){
        return authorUserName;
    }
    /**
     * 
     * @return the username of the intended recipient
     */
    public String getRecipientUserName(){
        return recipientUserName;
    }
}
