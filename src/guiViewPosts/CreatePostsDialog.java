package guiViewPosts;

import java.util.Optional;

import database.Database;
import database.PostStore;
import entityClasses.User;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Create posts dialog box class
 */
public class CreatePostsDialog {
    private static Database theDatabase = applicationMain.FoundationsMain.database;
	protected static PostStore posts = new PostStore(theDatabase);

    /**
     * Function to display the create posts dialog
     */
    public static void displayCreatePostsDialog() {
        User user = ViewPosts.theUser;
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Create a Post");

        ButtonType button_Post = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(button_Post, ButtonType.CANCEL);

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

        
        Platform.runLater(() -> title.requestFocus());

        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == button_Post) {
                String val = posts.validatePost(user.getUserName(), threadname.getText(), title.getText(), body.getText());

                if (val.contains("ERROR")) {

                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("ERROR");
		            alert.setHeaderText("ERROR!");
		            alert.setContentText(val);

		            Optional<ButtonType> result = alert.showAndWait();
                } else if (user.getNewRole2() && posts.threadExists(threadname.getText())) {
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("ERROR");
		            alert.setHeaderText("Invalid user role");
		            alert.setContentText("Users with role2 cannot create threads!");
		            Optional<ButtonType> result = alert.showAndWait();
                } else {
                	posts.createPost(user.getUserName(), threadname.getText(), title.getText(), body.getText());
                    ControllerViewPosts.refreshPostList();
                }
            }



            return null;
        });

        dialog.showAndWait();
    }
}
