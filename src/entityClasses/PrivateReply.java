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

    public PrivateReply(String replyId, String authorUserName, String parentPostId){
        this.replyId = replyId;
        this.authorUserName = authorUserName;
        this.parentPostId = parentPostId;
    }

    public String getReplyId(){
        return replyId;
    }

    public String getAuthorUserName(){
        return authorUserName;
    }

    public String getParentPostId(){
        return parentPostId;
    }
}
