package guiViewPosts;

import java.io.File;

import database.Database;
import database.PostStore;
import database.ReplyStore;
import entityClasses.Post;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * Class to view a specific posts replies, title and threadname
 */
public class ViewPostDetailed {
    private static Database theDatabase = applicationMain.FoundationsMain.database;
	protected static PostStore posts = new PostStore(theDatabase);
    protected static ReplyStore replies = new ReplyStore(theDatabase);
	public static VBox content = new VBox(10);

    /**
     * This method will display the replies associated with a post
     * @param post - pass in post object
     */
    protected static void displayPostDetailed(Post post) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Detailed view for " + post.getTitle());
        ViewPostDetailed.content.getChildren().clear();

        ButtonType button_Reply = new ButtonType("Reply", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(button_Reply, ButtonType.CANCEL);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 10, 10, 10));

        VBox postContainer = new VBox(50);
        postContainer.setStyle("-fx-border-color: lightgrey; -fx-background-color: white;");
        

        // Header
		Label header = new Label(post.getThreadName() + " - " + post.getTitle());
		header.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
		header.setMaxWidth(Double.MAX_VALUE);
		header.setAlignment(Pos.TOP_CENTER); 

        // Body
		Label body = new Label(post.getBody());
		body.setWrapText(true); 
		body.setStyle("-fx-font-size: 14px;");
		body.setMaxWidth(Double.MAX_VALUE);
		body.setAlignment(Pos.TOP_LEFT);

        postContainer.getChildren().addAll(body);
		content.getChildren().addAll(postContainer);
		content.setPadding(new Insets(10));

		ControllerViewPosts.refreshReplies(post);

        // Add replies here
		
		ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(content);
		scrollPane.setPrefHeight(500);
		scrollPane.setPrefWidth(780);
		scrollPane.setLayoutX(10);
		scrollPane.setLayoutY(50);
		scrollPane.setFitToWidth(true);
		
		// Very dumb garbage I had to do to get JavaFX to find the css file
		File cssFile = new File("src\\guiViewPosts\\viewposts.css");
		String javaFxUrl = cssFile.toURI().toString();
		scrollPane.getStylesheets().add(javaFxUrl);


        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        layout.getChildren().addAll(header, scrollPane);
        dialog.getDialogPane().setContent(layout);
    
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == button_Reply) {
                CreateReplyDialog.displayCreateReplyDialog(post.getPostId(), (post.getThreadName() + " - " + post.getTitle()), post);
            }
            return null;
        });

        dialog.showAndWait();
    }
}
