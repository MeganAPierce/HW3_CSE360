package database;

import entityClasses.PrivateReply;
import entityClasses.StaffPrivatePost;
import entityClasses.User;

/**
 * Title: PrivateInteractionAuthorizationService
 * 
 * @author Megan Pierce
 * Description: Prototype authorization service for staff private interactions
 * This class centralizes access control decisions for viewing private posts, replying to private posts,
 * and modifying private replies.
 */
public class PrivateInteractionAuthorizationService {
    
    /**
     * canCreatePrivatePost
     * Use to ensure only staff users may create private posts
     * @param user
     * @return
     */
    public boolean canCreatePrivatePost(User user){
        if (user == null) return false;

        return user.getAdminRole() || user.getNewRole2();
    }

    /**
     * canViewPrivatePost
     * Use to ensure a private post can only be viewed by the author and the intended recipient
     */
    public boolean canViewPrivatePost(User viewer, StaffPrivatePost post){
        if(viewer == null) return false;
        if(viewer.getUserName() == null) return false;

        String viewerName = viewer.getUserName();

        return viewerName.equals(post.getAuthorUserName()) || viewerName.equals(post.getRecipientUserName());
    }

    /**
     * canReplyToPrivatePost
     * Only the author or recipient of a private post can reply
     */
    public boolean canReplyToPrivatePost(User user, StaffPrivatePost post){
        if(user == null || post == null) return false;
        if (user.getUserName() == null) return false;

        String userName = user.getUserName();

        return userName.equals(post.getAuthorUserName()) || userName.equals(post.getRecipientUserName());
    }

    /**
     * canEditPrivateReply
     * Only the author of a private reply may edit it.
     */
    public boolean canEditPrivateReply(User user, PrivateReply reply) {
        if (user == null || reply == null) return false;
        if (user.getUserName() == null) return false;

        return user.getUserName().equals(reply.getAuthorUserName());
    }

    /**
     * canDeletePrivateReply
     * Only the author of a private reply may delete it.
     */
    public boolean canDeletePrivateReply(User user, PrivateReply reply) {
        if (user == null || reply == null) return false;
        if (user.getUserName() == null) return false;

        return user.getUserName().equals(reply.getAuthorUserName());
    }
}
