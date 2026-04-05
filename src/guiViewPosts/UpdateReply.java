package guiViewPosts;

import database.Database;
import database.ReplyStore;
import entityClasses.Post;
import entityClasses.Reply;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

/**
 * Update replay class
 */
public class UpdateReply {
    private static Database theDatabase = applicationMain.FoundationsMain.database;
	protected static ReplyStore replies = new ReplyStore(theDatabase);

    /**
     * Function to spawn the update reply dialog box
     * @param post - pass in reply object
     */
    public static void displayUpdateReplyDialog(Reply reply, Post post) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Update your reply");

        ButtonType button_Submit = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(button_Submit, ButtonType.CANCEL);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 10, 10, 10));

        TextArea body = new TextArea();
        body.setPromptText("Insert Reply");
        body.setWrapText(true); 

        layout.getChildren().addAll(body);

        
        dialog.getDialogPane().setContent(layout);

        
        Platform.runLater(() -> body.requestFocus());

        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == button_Submit) {
                replies.updateReply(reply.getReplyId(), body.getText());
                ViewPostDetailed.content.getChildren().clear();
                ControllerViewPosts.refreshReplies(post);
            }
            return null;
        });

        dialog.showAndWait();
    }
}
