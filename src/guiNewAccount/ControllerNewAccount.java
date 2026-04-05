package guiNewAccount;

import java.sql.SQLException;

import database.Database;
import entityClasses.User;
import javafx.scene.paint.Color;

/*******
 * <p> Title: ControllerNewAccount Class. </p>
 * 
 * <p> Description: The Java/FX-based New Account Page.  This class provides the controller actions
 * to allow the user to establish a new account after responding to an invitation and the use of a
 * one time code.
 * 
 * The controller deals with the user pressing the "User Step" button widget being click.  If also
 * supports the user click on the "Quit" button widget.
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

public class ControllerNewAccount {
	
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
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/

	/**
	 * Default constructor is not used.
	 */
	public ControllerNewAccount() {
	}
	
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	
	/*******
	 * <p> Title: updatePassword - Protected Method </p>
	 * 
	 * <p> Description: This method is called every time the user changes the password (e.g., with 
	 * every key pressed) using the GUI from the PasswordEvaluationGUITestbed.  It resets the 
	 * messages associated with each of the requirements and then evaluates the current password
	 * with respect to those requirements.  The results of that evaluation are display via the View
	 * to the user and via the console.</p>
	 */
	
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

	protected static void updatePassword() {
		ViewNewAccount.resetAssessments();						// Reset the assessment flags to the
		String password1 = ViewNewAccount.text_Password1.getText();	// initial state and fetch the input
		
		// If the input is empty, clear the aspects of the user interface having to do with the
		// user input and tell the user that the input is empty.
		if (password1.isEmpty()) {
			ViewNewAccount.noInputFound.setText("Please enter a password.");
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
				
				ViewNewAccount.noInputFound.setText("");			// There was input, so no error message
				
				// Ensure the button is disabled
				ViewNewAccount.button_UserSetup.setDisable(true);
			}
			else {
				// All the requirements were satisfied - the password is valid
				
				// Enable the button so the user can accept this password or continue to add
				// more characters to the password and make it longer.
				ViewNewAccount.button_UserSetup.setDisable(false);
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
			ViewNewAccount.label_UpperCase.setText("At least one upper case letter");
			ViewNewAccount.label_UpperCase.setTextFill(Color.GREEN);
		}

		if (foundLowerCase) {
			ViewNewAccount.label_LowerCase.setText("At least one lower case letter");
			ViewNewAccount.label_LowerCase.setTextFill(Color.GREEN);
		}

		if (foundNumericDigit) {
			ViewNewAccount.label_NumericDigit.setText("At least one numeric digit");
			ViewNewAccount.label_NumericDigit.setTextFill(Color.GREEN);
		}

		if (foundSpecialChar) {
			ViewNewAccount.label_SpecialChar.setText("At least one special character");
			ViewNewAccount.label_SpecialChar.setTextFill(Color.GREEN);
		}

		if (foundLongEnough) {
			ViewNewAccount.label_LongEnough.setText("At least eight characters");
			ViewNewAccount.label_LongEnough.setTextFill(Color.GREEN);
		}
		if (!foundShortEnough) {
			ViewNewAccount.label_ShortEnough.setText("Shorter than 15 characters");
			ViewNewAccount.label_ShortEnough.setTextFill(Color.RED);
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
	 * <p> Method: public doCreateUser() </p>
	 * 
	 * <p> Description: This method is called when the user has clicked on the User Setup
	 * button.  This method checks the input fields to see that they are valid.  If so, it then
	 * creates the account by adding information to the database.
	 * 
	 * The method reaches batch to the view page and to fetch the information needed rather than
	 * passing that information as parameters.
	 * 
	 */	
	protected static void doCreateUser() {
		
		// Fetch the username and password. (We use the first of the two here, but we will validate
		// that the two password fields are the same before we do anything with it.)
		String username = ViewNewAccount.text_Username.getText();
		String password = ViewNewAccount.text_Password1.getText();
		
		// Display key information to the log
		System.out.println("** Account for Username: " + username + "; theInvitationCode: "+
				ViewNewAccount.theInvitationCode + "; email address: " + 
				ViewNewAccount.emailAddress + "; Role: " + ViewNewAccount.theRole);
		
		// Initialize local variables that will be created during this process
		int roleCode = 0;
		User user = null;
		
		// returns an error message if the user name is invalid, otherwise returns an empty string
		String invalidUserNameMsg = checkForValidUserName(username);
				
		// Make sure the username is valid and the two passwords are the same.	
		if (invalidUserNameMsg.length()==0 && ViewNewAccount.text_Password1.getText().
			compareTo(ViewNewAccount.text_Password2.getText()) == 0) {
					
			// The passwords match so we will set up the role and the User object base on the 
			// information provided in the invitation
			if (ViewNewAccount.theRole.compareTo("Admin") == 0) {
				roleCode = 1;
				user = new User(username, password, "", "", "", "", "", true, false, false);
			} else if (ViewNewAccount.theRole.compareTo("Role1") == 0) {
				roleCode = 2;
				user = new User(username, password, "", "", "", "", "", false, true, false);
			} else if (ViewNewAccount.theRole.compareTo("Role2") == 0) {
				roleCode = 3;
				user = new User(username, password, "", "", "", "", "", false, false, true);
			} else {
				System.out.println(
						"**** Trying to create a New Account for a role that does not exist!");
				System.exit(0);
			}
			
			// Unlike the FirstAdmin, we know the email address, so set that into the user as well.
        	user.setEmailAddress(ViewNewAccount.emailAddress);

        	// Inform the system about which role will be played
			applicationMain.FoundationsMain.activeHomePage = roleCode;
			
        	// Create the account based on user and proceed to the user account update page
            try {
            	// Create a new User object with the pre-set role and register in the database
            	theDatabase.register(user);
            } catch (SQLException e) {
                System.err.println("*** ERROR *** Database error: " + e.getMessage());
                e.printStackTrace();
                System.exit(0);
            }
            
            // The account has been set, so remove the invitation from the system
            theDatabase.removeInvitationAfterUse(
            		ViewNewAccount.text_Invitation.getText());
            
            // Set the database so it has this user and the current user
            theDatabase.getUserAccountDetails(username);

            // Navigate to the Welcome Login Page
            guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewNewAccount.theStage, user);
		}
		else {
			if (invalidUserNameMsg.length()!=0) {
				ViewNewAccount.text_Username.setText("");
				ViewNewAccount.alertUsernameError.setHeaderText(invalidUserNameMsg);
				ViewNewAccount.alertUsernameError.showAndWait();
			}
			if (ViewNewAccount.text_Password1.getText().
				compareTo(ViewNewAccount.text_Password2.getText()) != 0)
			// The two passwords are NOT the same, so clear the passwords, explain the passwords
			// must be the same, and clear the message as soon as the first character is typed.
			ViewNewAccount.text_Password1.setText("");
			ViewNewAccount.text_Password2.setText("");
			ViewNewAccount.alertUsernamePasswordError.showAndWait();
		}
	}

	
	/**********
	 * <p> Method: public performQuit() </p>
	 * 
	 * <p> Description: This method is called when the user has clicked on the Quit button.  Doing
	 * this terminates the execution of the application.  All important data must be stored in the
	 * database, so there is no cleanup required.  (This is important so we can minimize the impact
	 * of crashed.)
	 * 
	 */	
	protected static void performQuit() {
		System.out.println("Perform Quit");
		System.exit(0);
	}	
}
