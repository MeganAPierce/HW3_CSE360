package database;

import entityClasses.PrivateReply;
import entityClasses.StaffPrivatePost;
import entityClasses.User;

/**
 * Title: PrivateInteractionAuthorizationService
 * 
 * @author Megan Pierce
 * Description: This class centralizes access-control decisions for private posts and their
 * replies so that the same rules can be applied consistently before a full TP3 implementation
 * is put into place with database storage and UI support.
 * The methods in this class are validated by the tests within the 
 * PrivateInteractionAuthorizationServiceTests test class.
 */
public class PrivateInteractionAuthorizationService {
    
    /**
     * canCreatePrivatePost
     * Use to ensure only staff users may create private posts
     * 
     * @param user
     * @return true if the user is allowed to create a private post, false otherwise
     * 
     * "Staff can create private post"
     * "Admin can create private post"
     * "Student can't create private post"
     * 
     */
    public boolean canCreatePrivatePost(User user){
        if (user == null) return false;

        return user.getAdminRole() || user.getNewRole2();
    }

    /**
     * canViewPrivatePost
     * Determines whether a user is allowed to view a private post.
     * Only the author and the intended recipient are allowed to view the private post.
     * The method returns false for null users, null posts, or users with null usernames.
     * 
     * Validations:
     * "Author can view private post"
     * "Intended recipient can view the private post"
     * "Unrelated user can't view the private post"
     * "Null user/post can't view private post/be viewed"
     * 
     * @param viewer the user attempting to view the post
     * @param post the private post
     * @return true if viewer is author or intended recipient, false otherwise
     */
    public boolean canViewPrivatePost(User viewer, StaffPrivatePost post){
        if(viewer == null || post == null) return false;
        if(viewer.getUserName() == null) return false;

        String viewerName = viewer.getUserName();

        return viewerName.equals(post.getAuthorUserName()) || viewerName.equals(post.getRecipientUserName());
    }

    /**
     * canReplyToPrivatePost
     * Determines whether a user is allowed to reply to private post
     * @param user the user attempting to reply
     * @param post the private post 
     * @return true if user is allowed to reply, false otherwise
     */
    public boolean canReplyToPrivatePost(User user, StaffPrivatePost post){
        if(user == null || post == null) return false;
        if (user.getUserName() == null) return false;

        String userName = user.getUserName();

        return userName.equals(post.getAuthorUserName()) || userName.equals(post.getRecipientUserName());
    }

    /**
     * canEditPrivateReply
     * Determines whether a user is allowed to edit a private reply
     * @param user the user attempting to edit a reply
     * @param reply the reply being edited 
     * @return true if the user can reply, false otherwise
     */
    public boolean canEditPrivateReply(User user, PrivateReply reply) {
        if (user == null || reply == null) return false;
        if (user.getUserName() == null) return false;

        return user.getUserName().equals(reply.getAuthorUserName());
    }

    /**
     * canDeletePrivateReply
     * Determines whether a user is allowed to delete a private reply
     * @param user the user attempting to delete a reply
     * @param reply the reply being deleted
     * @return true if the user can delete, false otherwise
     */
    public boolean canDeletePrivateReply(User user, PrivateReply reply) {
        if (user == null || reply == null) return false;
        if (user.getUserName() == null) return false;

        return user.getUserName().equals(reply.getAuthorUserName());
    }
}
