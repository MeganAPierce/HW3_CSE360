package guiViewPosts;

import database.Database;
import database.PostStore;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

/**
 * Class to filter threads in the main page
 */
public class ViewFilter {
    private static Database theDatabase = applicationMain.FoundationsMain.database;
	protected static PostStore posts = new PostStore(theDatabase);

    /**
     * Method to display the filter dialog box
     */
    public static void displayFilterDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Filter by keyword");

        ButtonType button_Filter = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(button_Filter, ButtonType.CANCEL);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 10, 10, 10));

        TextArea filter = new TextArea();
        filter.setPromptText("Insert filter");
        filter.setWrapText(true); 

        layout.getChildren().addAll(filter);

        
        dialog.getDialogPane().setContent(layout);

        
        Platform.runLater(() -> filter.requestFocus());

        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == button_Filter) {
                ControllerViewPosts.filterPostList(posts.searchPostsByKeyword(filter.getText()));
                ViewPosts.button_Filter.setText("Clear Filter");
                ViewPosts.button_Filter.setOnAction(event -> {
                    ViewPosts.button_Filter.setText("Filter");
                    ViewPosts.button_Filter.setOnAction((_) -> {ViewFilter.displayFilterDialog(); });
                    ControllerViewPosts.refreshPostList();
                });
            }
            return null;
        });

        dialog.showAndWait();
    }
}
