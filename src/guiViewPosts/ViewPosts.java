package guiViewPosts;

import java.io.File;

import database.Database;
import database.PostStore;
import entityClasses.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ViewPosts {
    	/*-********************************************************************************************

	Attributes

	 *********************************************************************************************/

	// These are the application values required by the user interface

	private static double width = 800;
	private static double height = 600;

	//private static Label label_ApplicationTitle = new Label("View all posts page");

	private static Button button_CreatePost = new Button("Create Post");
	public static Button button_Filter = new Button("Filter");
	private static Button button_Back = new Button("Back");

	protected static Stage theStage;
	protected static User theUser;		
	private static Pane theRootPane;
	public static Scene theViewPostsScene = null;	

	public static VBox content = new VBox(10); 

	private static Database theDatabase = applicationMain.FoundationsMain.database;
	protected static PostStore posts = new PostStore(theDatabase);

	private static ViewPosts theView = null;	//	private static guiUserLogin.ControllerUserLogin theController;


	/**
	 * Establishes the variables for the user and the pane to draw the GUI on
	 * @param ps
	 * @param user
	 */

	public static void displayAllPosts(Stage ps, User user) { 
		
		// Establish the references to the GUI. There is no current user yet.
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewPosts();
		

		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("View Posts page");		
		theStage.setScene(theViewPostsScene);
		theStage.show();
	}



	/**********
	 * <p> Method: ViewUserLoginPage() </p>
	 * 
	 * <p> Description: This method is called by display all posts and draws the main post window
	 */
	private ViewPosts() {
		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theViewPostsScene = new Scene(theRootPane, width, height);

		// Fill box with posts		
		ControllerViewPosts.refreshPostList();
		content.setPadding(new Insets(10));
		
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

		setupButtonUI(button_CreatePost, "Dialog", 18, 200, Pos.CENTER, 10, 5);
		button_CreatePost.setOnAction((_) -> {ControllerViewPosts.performCreatePost(); });

		setupButtonUI(button_Filter, "Dialog", 18, 200, Pos.CENTER, 590, 5);
		button_Filter.setOnAction((_) -> {ViewFilter.displayFilterDialog(); });

		setupButtonUI(button_Back, "Dialog", 18, 200, Pos.CENTER, 590, 560);
		button_Back.setOnAction((_) -> {ControllerViewPosts.performReturnToHome(); });

		theRootPane.getChildren().addAll(
				button_CreatePost,
				button_Filter,
				scrollPane, 
				button_Back);
	}



	/*-********************************************************************************************

	Helper methods to reduce code length

	 *********************************************************************************************/

	/**********
	 * Private local method to initialize the standard fields for a label
	 */

	private void setupLabelUI (Label l, String ff, double f, double w, Pos p, double x, double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}


	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}

	/**********
	 * Private local method to initialize the standard fields for a text field
	 */
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(e);
	}		
}
