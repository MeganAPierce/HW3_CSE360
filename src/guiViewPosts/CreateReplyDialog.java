package guiViewPosts;

import java.util.Optional;

import database.Database;
import database.PostStore;
import database.ReplyStore;
import entityClasses.Post;
import entityClasses.User;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

/**
 * Create reply dialog box class
 */
public class CreateReplyDialog {
    private static Database theDatabase = applicationMain.FoundationsMain.database;
    protected static PostStore posts = new PostStore(theDatabase);
	protected static ReplyStore replies = new ReplyStore(theDatabase);
    
    /**
     * Function to display the create reply dialog
     */
    public static void displayCreateReplyDialog(String postID, String header, Post post) {
        User user = ViewPosts.theUser;
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Replying to " + header);

        ButtonType button_Post = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(button_Post, ButtonType.CANCEL);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 10, 10, 10));

        TextArea replyBody = new TextArea();
        replyBody.setPromptText("Insert Reply");
        replyBody.setWrapText(true); 

        layout.getChildren().addAll(replyBody);

        
        dialog.getDialogPane().setContent(layout);

        
        Platform.runLater(() -> replyBody.requestFocus());

        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == button_Post) {
                String val = replies.validateReply(user.getUserName(), replyBody.getText());

                if (val.contains("ERROR")) {

                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("ERROR");
		            alert.setHeaderText("ERROR!");
		            alert.setContentText(val);

		            Optional<ButtonType> result = alert.showAndWait();
                } else {
                    replies.createReply(postID, posts, user.getUserName(), replyBody.getText()); 
                    ViewPostDetailed.content.getChildren().clear();
                    ControllerViewPosts.refreshPostList();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }
}
