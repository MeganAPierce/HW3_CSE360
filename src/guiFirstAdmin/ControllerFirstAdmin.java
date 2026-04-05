package guiFirstAdmin;

import java.sql.SQLException;
import database.Database;
import entityClasses.User;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/*******
 * <p> Title: ControllerFirstAdmin Class. </p>
 * 
 * <p> Description: ControllerFirstAdmin class provides the controller actions based on the user's
 *  use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.  WHhen those buttons
 * are pressed, an alert pops up to tell the user that the function associated with the button has
 * not been implemented. Also, be aware that What has been implemented may not work the way the
 * final product requires and there maybe defects in this code.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 *  
 */

public class ControllerFirstAdmin {
	/*-********************************************************************************************

	The controller attributes for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/
	
	private static String adminUsername = "";
	private static String adminPassword1 = "";
	private static String adminPassword2 = "";		
	protected static Database theDatabase = applicationMain.FoundationsMain.database;	
	public static String userNameRecognizerErrorMessage = "";	// The error message text
	public static String userNameRecognizerInput = "";			// The input being processed
	public static int userNameRecognizerIndexofError = -1;		// The index of error location
	private static int state = 0;						// The current state value
	private static int nextState = 0;					// The next state value
	private static String inputLine = "";				// The input line
	private static char currentChar;					// The current character in the line
	private static int currentCharNdx;					// The index of the current character
	private static boolean running;						// The flag that specifies if the FSM is 
														// running
	private static int userNameSize = 0;			// A numeric value may not exceed 16 characters


	/*-********************************************************************************************

	The User Interface Actions for this page
	
	*/
	
	/**
	 * Default constructor is not used.
	 */
	public ControllerFirstAdmin() {
	}

	/**********
	 * <p> Method: setAdminUsername() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the username field in the
	 * View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminUsername() {
		adminUsername = ViewFirstAdmin.text_AdminUsername.getText();
	}
	
	
	/**********
	 * <p> Method: setAdminPassword1() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the password 1 field in
	 * the View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminPassword1() {
		adminPassword1 = ViewFirstAdmin.text_AdminPassword1.getText();
		ViewFirstAdmin.label_PasswordsDoNotMatch.setText("");
	}
	
	
	/**********
	 * <p> Method: setAdminPassword2() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the password 2 field in
	 * the View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminPassword2() {
		adminPassword2 = ViewFirstAdmin.text_AdminPassword2.getText();		
		ViewFirstAdmin.label_PasswordsDoNotMatch.setText("");
	}
	

	/**********
	 * <p> Method: moveToNextCharacter() </p>
	 * 
	 * <p> Description: Private method to move to the next character within the limits of the 
	 * input line</p>
	 * 
	 */
	private static void moveToNextCharacter() {
		currentCharNdx++;
		if (currentCharNdx < inputLine.length())
			currentChar = inputLine.charAt(currentCharNdx);
		else {
			currentChar = ' ';
			running = false;
		}
	}

	/**********
	 * <p> Method: checkForValidUserName() </p>
	 * 
	 * <p> Description: This method is a mechanical transformation of a Finite State Machine diagram into a Java
	 * method.
	 * 
	 * @param input		The input string for the Finite State Machine
	 * @return			An output string that is empty if every things is okay or it is a String
	 * 						with a helpful description of the error</p>
	 */
	public static String checkForValidUserName(String input) {
		// Check to ensure that there is input to process
		if(input.length() <= 0) {
			userNameRecognizerIndexofError = 0;	// Error at first character;
			return "\n*** ERROR *** The input is empty";
		}
		
		// The local variables used to perform the Finite State Machine simulation
		state = 0;							// This is the FSM state number
		inputLine = input;					// Save the reference to the input line as a global
		currentCharNdx = 0;					// The index of the current character
		currentChar = input.charAt(0);		// The current character from above indexed position

		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state

		userNameRecognizerInput = input;	// Save a copy of the input
		running = true;						// Start the loop
		nextState = -1;						// There is no next state
		System.out.println("\nCurrent Final Input  Next  Date\nState   State Char  State  Size");
		
		// This is the place where semantic actions for a transition to the initial state occur
		
		userNameSize = 0;					// Initialize the UserName size

		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state
		while (running) {
			// The switch statement takes the execution to the code for the current state, where
			// that code sees whether or not the current character is valid to transition to a
			// next state
			switch (state) {
			case 0: 
				// State 0 has 1 valid transition that is addressed by an if statement.
				
				// The current character is checked against A-Z, a-z. If any are matched
				// the FSM goes to state 1
				
				// A-Z, a-z -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
						(currentChar >= 'a' && currentChar <= 'z' )) {	// Check for a-z
					nextState = 1;
					
					// Count the character 
					userNameSize++;
					
					// This only occurs once, so there is no need to check for the size getting
					// too large.
				}
				// If it is none of those characters, the FSM halts
				else 
					running = false;
				
				// The execution of this state is finished
				break;
			
			case 1: 
				// State 1 has two valid transitions, 
				//	1: a A-Z, a-z, 0-9 that transitions back to state 1
				//  2: a minus sign, period, or underscore that transitions to state 2 

				
				// A-Z, a-z, 0-9 -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
						(currentChar >= 'a' && currentChar <= 'z' ) ||	// Check for a-z
						(currentChar >= '0' && currentChar <= '9' )) {	// Check for 0-9
					nextState = 1;
					
					// Count the character
					userNameSize++;
				}
				// -, ., _ -> State 2
				else if (currentChar == '-' || currentChar == '.' || currentChar == '_') {	// Check for -/./_
					nextState = 2;
					
					// Count the -/./_
					userNameSize++;
				}				
				// If it is none of those characters, the FSM halts
				else
					running = false;
					
				// The execution of this state is finished
				// If the size is larger than 16, the loop must stop
				if (userNameSize > 16)
					running = false;
				
				break;
				
			case 2: 
				// State 2 deals with a character after a minus sign, period, or underscore in the name.
				
				// A-Z, a-z, 0-9 -> State 1
				if ((currentChar >= 'A' && currentChar <= 'Z' ) ||		// Check for A-Z
						(currentChar >= 'a' && currentChar <= 'z' ) ||	// Check for a-z
						(currentChar >= '0' && currentChar <= '9' )) {	// Check for 0-9
					nextState = 1;
					
					// Count the odd digit
					userNameSize++;
					
				}
				// If it is none of those characters, the FSM halts
				else 
					running = false;
				
				// The execution of this state is finished
				// If the size is larger than 16, the loop must stop
				if (userNameSize > 16)
					running = false;
				
				break;			
			}
			
			if (running) {
				// When the processing of a state has finished, the FSM proceeds to the next
				// character in the input and if there is one, it fetches that character and
				// updates the currentChar.  If there is no next character the currentChar is
				// set to a blank.
				moveToNextCharacter();

				// Move to the next state
				state = nextState;
				
				// Ensure that one of the cases sets this to a valid value
				nextState = -1;
			}
			// Should the FSM get here, the loop starts again
	
		}
		
		System.out.println("The loop has ended.");
		
		// When the FSM halts, we must determine if the situation is an error or not.  That depends
		// of the current state of the FSM and whether or not the whole string has been consumed.
		// This switch directs the execution to separate code for each of the FSM states and that
		// makes it possible for this code to display a very specific error message to improve the
		// user experience.
		userNameRecognizerIndexofError = currentCharNdx;	// Set index of a possible error;
		userNameRecognizerErrorMessage = "\n*** ERROR *** ";
		
		// The following code is a slight variation to support just console output.
		switch (state) {
		case 0:
			// State 0 is not a final state, so we can return a very specific error message
			userNameRecognizerErrorMessage += "A UserName must start with A-Z or a-z.\n";
			return userNameRecognizerErrorMessage;

		case 1:
			// State 1 is a final state.  Check to see if the UserName length is valid.  If so we
			// we must ensure the whole string has been consumed.

			if (userNameSize < 4) {
				// UserName is too small
				userNameRecognizerErrorMessage += "A UserName must have at least 4 characters.\n";
				return userNameRecognizerErrorMessage;
			}
			else if (userNameSize > 16) {
				// UserName is too long
				userNameRecognizerErrorMessage += 
					"A UserName must have no more than 16 characters.\n";
				return userNameRecognizerErrorMessage;
			}
			else if (currentCharNdx < input.length()) {
				// There are characters remaining in the input, so the input is not valid
				userNameRecognizerErrorMessage += 
						"A UserName character may only contain the characters A-Z, a-z, 0-9, -, ., _.\n";
				return userNameRecognizerErrorMessage;
			}
			else {
					// UserName is valid
					userNameRecognizerIndexofError = -1;
					userNameRecognizerErrorMessage = "";
					return userNameRecognizerErrorMessage;
			}

		case 2:
			// State 2 is not a final state, so we can return a very specific error message
			userNameRecognizerErrorMessage +=
					"A UserName character of -,.,_ must be followed by A-Z, a-z, 0-9.\n";
			return userNameRecognizerErrorMessage;
			
		default:
			// This is for the case where we have a state that is outside of the valid range.
			// This should not happen
			return "";
		}
	}
	
	
	
	/*******
	 * <p> Title: updatePassword - Protected Method </p>
	 * 
	 * <p> Description: This method is called every time the user changes the password (e.g., with 
	 * every key pressed) using the GUI from the PasswordEvaluationGUITestbed.  It resets the 
	 * messages associated with each of the requirements and then evaluates the current password
	 * with respect to those requirements.  The results of that evaluation are display via the View
	 * to the user and via the console.</p>
	 */

	protected static void updatePassword() {
		ViewFirstAdmin.resetAssessments();						// Reset the assessment flags to the
		String password1 = ViewFirstAdmin.text_AdminPassword1.getText();	// initial state and fetch the input
		
		// If the input is empty, clear the aspects of the user interface having to do with the
		// user input and tell the user that the input is empty.
		if (password1.isEmpty()) {
			ViewFirstAdmin.noInputFound.setText("Please enter a password.");
		}
		else
		{
			// There is user input, so evaluate it to see if it satisfies the requirements
			String errMessage = evaluatePassword(password1);
			
			// Based on the evaluation, change the flag to green for each satisfied requirement
			updateFlags();
			
			// An empty string means there is no error message, which means the input is valid
			if (errMessage != "") {
				
				// Since the output is not empty, at least one requirement have not been satisfied.
				
				ViewFirstAdmin.noInputFound.setText("");			// There was input, so no error message
				
				// Ensure the button is disabled
				ViewFirstAdmin.button_AdminSetup.setDisable(true);
			}
			else {
				// All the requirements were satisfied - the password is valid
				
				// Enable the button so the user can accept this password or continue to add
				// more characters to the password and make it longer.
				ViewFirstAdmin.button_AdminSetup.setDisable(false);
			} 
		}
	}
	
	/*-********************************************************************************************
	 * 
	 * Attributes used by the Finite State Machine to inform the user about what was and was not
	 * valid and point to the character of the error.  This will enhance the user experience.
	 * 
	 */

	public static String passwordErrorMessage = "";		// The error message text
	public static String passwordInput = "";			// The input being processed
	public static int passwordIndexofError = -1;		// The index where the error was located
	public static boolean foundUpperCase = false;
	public static boolean foundLowerCase = false;
	public static boolean foundNumericDigit = false;
	public static boolean foundSpecialChar = false;
	public static boolean foundLongEnough = false;
	public static boolean foundShortEnough = false;
	private static String pinputLine = "";				// The input line
	private static char pcurrentChar;					// The current character in the line
	private static int pcurrentCharNdx;					// The index of the current character
	private static boolean prunning;						// The flag that specifies if the FSM is 
														// running
	/*
	 * This private method checks each of the requirements and if one is satisfied, it changes the
	 * the text to tell the user of this fact and changes the text color from red to green.
	 * 
	 */
	
	private static void updateFlags() {
		if (foundUpperCase) {
			ViewFirstAdmin.label_UpperCase.setText("At least one upper case letter");
			ViewFirstAdmin.label_UpperCase.setTextFill(Color.GREEN);
		}

		if (foundLowerCase) {
			ViewFirstAdmin.label_LowerCase.setText("At least one lower case letter");
			ViewFirstAdmin.label_LowerCase.setTextFill(Color.GREEN);
		}

		if (foundNumericDigit) {
			ViewFirstAdmin.label_NumericDigit.setText("At least one numeric digit");
			ViewFirstAdmin.label_NumericDigit.setTextFill(Color.GREEN);
		}

		if (foundSpecialChar) {
			ViewFirstAdmin.label_SpecialChar.setText("At least one special character");
			ViewFirstAdmin.label_SpecialChar.setTextFill(Color.GREEN);
		}

		if (foundLongEnough) {
			ViewFirstAdmin.label_LongEnough.setText("At least eight characters");
			ViewFirstAdmin.label_LongEnough.setTextFill(Color.GREEN);
		}
		if (!foundShortEnough) {
			ViewFirstAdmin.label_ShortEnough.setText("Shorter than 15 characters");
			ViewFirstAdmin.label_ShortEnough.setTextFill(Color.RED);
		}
	}
	

	/**********
	 * <p> Title: evaluatePassword - Public Method </p>
	 * 
	 * <p> Description: This method is a mechanical transformation of a Directed Graph diagram 
	 * into a Java method. This method is used by both the GUI version of the application as well
	 * as the testing automation version.
	 * 
	 * @param input		The input string evaluated by the directed graph processing
	 * @return			An output string that is empty if every things is okay or it will be
	 * 						a string with a helpful description of the error follow by two lines
	 * 						that shows the input line follow by a line with an up arrow at the
	 *						point where the error was found.
	 */
	
	public static String evaluatePassword(String input) {
		// The following are the local variable used to perform the Directed Graph simulation
		passwordErrorMessage = "";
		passwordIndexofError = 0;			// Initialize the IndexofError
		pinputLine = input;					// Save the reference to the input line as a global
		pcurrentCharNdx = 0;					// The index of the current character
		
		if(input.length() <= 0) {
			return "*** Error *** The password is empty!";
		}
		
		// The input is not empty, so we can access the first character
		pcurrentChar = input.charAt(0);		// The current character from the above indexed position

		// The Directed Graph simulation continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state.  This
		// local variable is a working copy of the input.
		passwordInput = input;				// Save a copy of the input
		
		// The following are the attributes associated with each of the requirements
		foundUpperCase = false;				// Reset the Boolean flag
		foundLowerCase = false;				// Reset the Boolean flag
		foundNumericDigit = false;			// Reset the Boolean flag
		foundSpecialChar = false;			// Reset the Boolean flag
		foundNumericDigit = false;			// Reset the Boolean flag
		foundLongEnough = false;			// Reset the Boolean flag
		foundShortEnough = true;			// Reset the Boolean flag
		
		// This flag determines whether the directed graph (FSM) loop is operating or not
		prunning = true;						// Start the loop

		// The Directed Graph simulation continues until the end of the input is reached or at some
		// state the current character does not match any valid transition
		while (prunning) {
			// The cascading if statement sequentially tries the current character against all of
			// the valid transitions, each associated with one of the requirements
			if (pcurrentChar >= 'A' && pcurrentChar <= 'Z') {
				System.out.println("Upper case letter found");
				foundUpperCase = true;
			} else if (pcurrentChar >= 'a' && pcurrentChar <= 'z') {
				System.out.println("Lower case letter found");
				foundLowerCase = true;
			} else if (pcurrentChar >= '0' && pcurrentChar <= '9') {
				System.out.println("Digit found");
				foundNumericDigit = true;
			} else if ("~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/".indexOf(pcurrentChar) >= 0) {
				System.out.println("Special character found");
				foundSpecialChar = true;
			} else {
				passwordIndexofError = pcurrentCharNdx;
				return "*** Error *** An invalid character has been found!";
			}
			if (pcurrentCharNdx >= 7) {
				System.out.println("At least 8 characters found");
				foundLongEnough = true;
			}
			if (pcurrentCharNdx >= 15) {
				System.out.println("15 Characters is the max.");
				foundShortEnough = false;
			}
			
			
			// Go to the next character if there is one
			pcurrentCharNdx++;
			if (pcurrentCharNdx >= pinputLine.length())
				prunning = false;
			else
				pcurrentChar = input.charAt(pcurrentCharNdx);
			
			System.out.println();
		}
		
		// Construct a String with a list of the requirement elements that were found.
		String errMessage = "";
		if (!foundUpperCase)
			errMessage += "Upper case; ";
		
		if (!foundLowerCase)
			errMessage += "Lower case; ";
		
		if (!foundNumericDigit)
			errMessage += "Numeric digits; ";
			
		if (!foundSpecialChar)
			errMessage += "Special character; ";
			
		if (!foundLongEnough)
			errMessage += "Long Enough; ";
		
		if (errMessage == "")
			return "";
		
		// If it gets here, there something was not found, so return an appropriate message
		passwordIndexofError = pcurrentCharNdx;
		return errMessage + "conditions were not satisfied";
	}
	
	
	
	/**********
	 * <p> Method: doSetupAdmin() </p>
	 * 
	 * <p> Description: This method is called when the user presses the button to set up the Admin
	 * account.  It start by trying to establish a new user and placing that user into the
	 * database.  If that is successful, we proceed to the UserUpdate page.</p>
	 * 
	 */
	protected static void doSetupAdmin(Stage ps, int r) {
		// returns an error message if the user name is invalid, otherwise returns an empty string
		String invalidUserNameMsg = checkForValidUserName(adminUsername);
		
		// Make sure the user name is valid and the two passwords are the same
		if (invalidUserNameMsg.length()==0 && adminPassword1.compareTo(adminPassword2) == 0) {
        	// Create the passwords and proceed to the user home page
        	User user = new User(adminUsername, adminPassword1, "", "", "", "", "", true, false, 
        			false);
            try {
            	// Create a new User object with admin role and register in the database
            	theDatabase.register(user);
            	}
            catch (SQLException e) {
                System.err.println("*** ERROR *** Database error trying to register a user: " + 
                		e.getMessage());
                e.printStackTrace();
                System.exit(0);
            }
            
            // User was established in the database, so navigate to the User Update Page
        	guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewFirstAdmin.theStage, user);
		}
		else {
			if (invalidUserNameMsg.length()!=0) {
				// The user name is NOT valid, so clear the user name, explain the user name
				// is invalid, and clear the message as soon as the first character is typed.
				ViewFirstAdmin.text_AdminUsername.setText("");
				ViewFirstAdmin.label_InvalidUsername.setText(invalidUserNameMsg);
			}
			if (adminPassword1.compareTo(adminPassword2) != 0) {
				// The two passwords are NOT the same, so clear the passwords, explain the passwords
				// must be the same, and clear the message as soon as the first character is typed.
				ViewFirstAdmin.text_AdminPassword1.setText("");
				ViewFirstAdmin.text_AdminPassword2.setText("");
				ViewFirstAdmin.label_PasswordsDoNotMatch.setText(
						"The two passwords must match. Please try again!");
			}
		}
	}
	
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */
	protected static void performQuit() {
		System.out.println("Perform Quit");
		System.exit(0);
	}	
}

