package guiViewPosts;

import database.Database;
import database.PostStore;
import entityClasses.Post;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Class to update a post
 */
public class UpdatePost {
    private static Database theDatabase = applicationMain.FoundationsMain.database;
	protected static PostStore posts = new PostStore(theDatabase);

    /**
     * Function to spawn the update post dialog box
     * @param post - pass in post object
     */
    public static void displayUpdatePostDialog(Post post) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Update your post");

        ButtonType button_Submit = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(button_Submit, ButtonType.CANCEL);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 10, 10, 10));

        TextField threadname = new TextField();
        threadname.setPromptText("Insert Thread Name");

        TextField title = new TextField();
        title.setPromptText("Insert Title");

        TextArea body = new TextArea();
        body.setPromptText("Insert Body");
        body.setWrapText(true); 

        layout.getChildren().addAll(threadname, title, body);

        
        dialog.getDialogPane().setContent(layout);

        
        Platform.runLater(() -> threadname.requestFocus());

        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == button_Submit) {
                posts.updatePost(post.getPostId(), threadname.getText(), title.getText(), body.getText());
                ControllerViewPosts.refreshPostList();
            }
            return null;
        });

        dialog.showAndWait();
    }
}
