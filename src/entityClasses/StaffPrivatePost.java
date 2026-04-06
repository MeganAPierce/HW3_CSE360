package entityClasses;

/**
 * Title: StaffPrivatePost
 * @author Megan Pierce
 * 
 * Description: A lightweight prototype model representing a private post created by staff for a sepcific recipient
 */
public class StaffPrivatePost {
    private String postId;
    private String authorUserName;
    private String recipientUserName;

    public StaffPrivatePost(String postId, String authorUserName, String recipientUserName){
        this.postId = postId;
        this.authorUserName = authorUserName;
        this.recipientUserName = recipientUserName;
    }

    public String getPostId(){
        return postId;
    }

    public String getAuthorUserName(){
        return authorUserName;
    }

    public String getRecipientUserName(){
        return recipientUserName;
    }
}
