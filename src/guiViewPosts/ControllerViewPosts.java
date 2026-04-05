package guiViewPosts;

import java.util.List;
import java.util.Optional;

import database.Database;
import database.PostStore;
import database.ReplyStore;
import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Controller to view posts
 */
public class ControllerViewPosts {
    private static Database theDatabase = applicationMain.FoundationsMain.database;
	protected static PostStore posts = new PostStore(theDatabase);
	protected static ReplyStore replies = new ReplyStore(theDatabase);
    
	/**
	 *  Returns user to their userhome
	 */
    protected static void performReturnToHome () {
        boolean loginResult = false;
        User user = ViewPosts.theUser;

        if (user.getAdminRole()) {
			loginResult = theDatabase.loginAdmin(user);
			if (loginResult) {
				guiAdminHome.ViewAdminHome.displayAdminHome(ViewPosts.theStage, user);
			}
		} else if (user.getNewRole1()) {
			loginResult = theDatabase.loginRole1(user);
			if (loginResult) {
				guiRole1.ViewRole1Home.displayRole1Home(ViewPosts.theStage, user);
			}
		} else if (user.getNewRole2()) {
			loginResult = theDatabase.loginRole2(user);
			if (loginResult) {
				guiRole2.ViewRole2Home.displayRole2Home(ViewPosts.theStage, user);
			}
			// Other roles
		} else {
			System.out.println("***** UserLogin goToUserHome request has an invalid role");
		}
		
	}	

	/**
	 * Opens the create post dialog box
	 */
    protected static void performCreatePost () {
        CreatePostsDialog.displayCreatePostsDialog();
    }

	/**
	 * Refreshes the postlist in the main window
	 */
	protected static void refreshPostList() {
		User user = ViewPosts.theUser;
		ViewPosts.content.getChildren().clear();

		List<Post> allPosts = posts.readAllPosts();

		// Add posts to pane
		for (Post post : allPosts) { 
			VBox postContainer = new VBox(50);
			HBox buttonContainer = new HBox(270);
			
			
			postContainer.setPadding(new Insets(10));
			postContainer.setStyle("-fx-border-color: lightgrey; -fx-background-color: white;");

			// Reply button - should be always active
			Button button_BodyReplyButton = new Button("Reply");
			button_BodyReplyButton.setOnAction((_) -> {ViewPostDetailed.displayPostDetailed(post); });

			// Update button  
			Button button_BodyUpdateButton = new Button("Update");
			button_BodyUpdateButton.setOnAction((_) -> {updatePost(post); });

			// Delete button  
			Button button_BodyDeleteButton = new Button("Delete");
			button_BodyDeleteButton.setOnAction((_) -> {deletePost(post); });

			// Header
			Label header = new Label(post.getThreadName() + " - " + post.getTitle());
			header.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
			header.setMaxWidth(Double.MAX_VALUE); 
			header.setAlignment(Pos.TOP_LEFT);

			// Body
			Label body = new Label(post.getBody());
			body.setWrapText(true); 
			body.setStyle("-fx-font-size: 14px;");
			body.setMaxWidth(Double.MAX_VALUE);
			body.setAlignment(Pos.TOP_LEFT);
			
			if (posts.canEditPost(user, post)) {
				buttonContainer.getChildren().addAll(button_BodyReplyButton, button_BodyUpdateButton, button_BodyDeleteButton);
			} else {
				buttonContainer.getChildren().addAll(button_BodyReplyButton);
			}
			postContainer.getChildren().addAll(header, body, buttonContainer);


			ViewPosts.content.getChildren().add(postContainer);
		}

	}

	/**
	 * Takes in a list of posts to display. Input list should be already a list
	 * of filtered posts
	 * @param postList - List of posts to display
	 */
	protected static void filterPostList(List<Post> postList) {
		User user = ViewPosts.theUser;
		ViewPosts.content.getChildren().clear();

		// Add posts to pane
		for (Post post : postList) { 
			VBox postContainer = new VBox(50);
			HBox buttonContainer = new HBox(270);
			
			
			postContainer.setPadding(new Insets(10));
			postContainer.setStyle("-fx-border-color: lightgrey; -fx-background-color: white;");

			// Reply button - should be always active
			Button button_BodyReplyButton = new Button("Reply");
			button_BodyReplyButton.setOnAction((_) -> {ViewPostDetailed.displayPostDetailed(post); });

			// Update button  
			Button button_BodyUpdateButton = new Button("Update");
			button_BodyUpdateButton.setOnAction((_) -> {updatePost(post); });

			// Delete button  
			Button button_BodyDeleteButton = new Button("Delete");
			button_BodyDeleteButton.setOnAction((_) -> {deletePost(post); });

			// Header
			Label header = new Label(post.getThreadName() + " - " + post.getTitle());
			header.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
			header.setMaxWidth(Double.MAX_VALUE); 
			header.setAlignment(Pos.TOP_LEFT);

			// Body
			Label body = new Label(post.getBody());
			body.setWrapText(true); 
			body.setStyle("-fx-font-size: 14px;");
			body.setMaxWidth(Double.MAX_VALUE);
			body.setAlignment(Pos.TOP_LEFT);
			
			if (posts.canEditPost(user, post)) {
				buttonContainer.getChildren().addAll(button_BodyReplyButton, button_BodyUpdateButton, button_BodyDeleteButton);
			} else {
				buttonContainer.getChildren().addAll(button_BodyReplyButton);
			}
			postContainer.getChildren().addAll(header, body, buttonContainer);


			ViewPosts.content.getChildren().add(postContainer);
		}

	}

	/**
	 * Refresh replies in the viewdetailedposts window
	 * @param post - used to grab replies for that postID
	 */
	protected static void refreshReplies(Post post) {
		User user = ViewPosts.theUser;

		// Create reply vboxes here
        List<Reply> reply = replies.readRepliesByPostId(post.getPostId());
        
        for (Reply iter : reply) {
			VBox replyContainer = new VBox(50);
			HBox buttonContainer = new HBox(270);
			replyContainer.setPadding(new Insets(10));
			replyContainer.setStyle("-fx-border-color: lightgrey; -fx-background-color: white;");

            Label replyBody = new Label(iter.getBody());
			replyBody.setWrapText(true); 
			replyBody.setStyle("-fx-font-size: 14px;");
			replyBody.setMaxWidth(Double.MAX_VALUE);
			replyBody.setAlignment(Pos.TOP_LEFT);

			// Update button  
			Button button_ReplyUpdateButton = new Button("Update");
			button_ReplyUpdateButton.setOnAction((_) -> {updateReply(iter, post); });

			// Delete button  
			Button button_ReplyDeleteButton = new Button("Delete");
			button_ReplyDeleteButton.setOnAction((_) -> {deleteReply(iter, post); });
			
			if (replies.canEditReply(user, iter)) {
				buttonContainer.getChildren().addAll(button_ReplyUpdateButton, button_ReplyDeleteButton);
				replyContainer.getChildren().addAll(replyBody, buttonContainer);
			} else {
				replyContainer.getChildren().addAll(replyBody);
			}

			ViewPostDetailed.content.getChildren().addAll(replyContainer);
			ViewPostDetailed.content.setPadding(new Insets(10));
			
        }
	}


	/**
	 * Used to update a post
	 * @param post - post object to update
	 */
	protected static void updatePost (Post post) {
		UpdatePost.displayUpdatePostDialog(post);
	}

	/**
	 * Used to delete a post
	 * @param post - post object to update
	 */
	protected static void deletePost (Post post) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirm Action");
		alert.setHeaderText("Delete Post?");
		alert.setContentText("Are you sure you want to delete this post? This cannot be undone.");

		Optional<ButtonType> result = alert.showAndWait();

		if (result.isPresent() && result.get() == ButtonType.OK) {
			
			posts.softDeletePostById(post.getPostId());
			refreshPostList();
		} 
	}

	/**
	 * Used to update a reply
	 * @param reply - target reply object 
	 * @param post - target post object
	 */
	protected static void updateReply (Reply reply, Post post) {
		UpdateReply.displayUpdateReplyDialog(reply, post);
	}


	/**
	 * Used to update a reply
	 * @param reply - target reply object 
	 * @param post - target post object
	 */
	protected static void deleteReply (Reply reply, Post post) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirm Action");
		alert.setHeaderText("Delete Reply?");
		alert.setContentText("Are you sure you want to delete this Reply? This cannot be undone.");

		Optional<ButtonType> result = alert.showAndWait();

		if (result.isPresent() && result.get() == ButtonType.OK) {
			
			replies.softDeleteReplyById(reply.getReplyId());
			ViewPostDetailed.content.getChildren().clear();
			refreshReplies(post);
		} 
	}

}

