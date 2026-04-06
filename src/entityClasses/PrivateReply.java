package entityClasses;

/**
 * Title: PrivateReply
 * @author Megan Pierce
 * 
 * Description: A lightweight prototype model representing a reply to a private post.
 */
public class PrivateReply {
    
    private String replyId;
    private String authorUserName;
    private String parentPostId;
    /**
     * Constructs a PrivateReply object
     * 
     * @param replyId
     * @param authorUserName
     * @param parentPostId
     */
    public PrivateReply(String replyId, String authorUserName, String parentPostId){
        this.replyId = replyId;
        this.authorUserName = authorUserName;
        this.parentPostId = parentPostId;
    }

    /**
     * 
     * @return the reply ID
     */
    public String getReplyId(){
        return replyId;
    }

    /**
     * 
     * @return the username of the reply author
     */
    public String getAuthorUserName(){
        return authorUserName;
    }

    /**
     * 
     * @return the parent post ID
     */
    public String getParentPostId(){
        return parentPostId;
    }
}
