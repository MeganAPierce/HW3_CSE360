/*

This will be used to determine the result of CRUD operations. 

*/

package database;

/**
 * <p>This class represents the result of a test to create a discussion post in the student discussion forum.  
 * This class stores a Post/Result object itself as well as any error message and a boolean value indicating whether
 * or not a valid post/result has been successfully created.</p>
 */
public class Result<T> {
	/**
	 * Ok indicates a pass or fail of object creation
	 * <p>Represents the result of an object's creation and whether it passed or failed to be validly created.</p>
	 */
    private final boolean ok; // pass or fail
    /**
	 * value stores the object to be returned
	 * <p>Represents the Post/Reply object containing all details about the object being returned after creation, if 
	 * creation was successful.</p>
	 */
    private final T value; // if pass then the value of the operation 
    /**
	 * error contains the String value of the error message
	 * <p>Represents the error message to be shown if the object creation fails or is invalid.</p>
	 */
    private final String error; //if fail, error message   

    /**
     * This constructs a new Result.
     * 
     * <p> This constructor is used when a user creates a new post/reply and the system calls the createPost or createReply
     * function, which then attempts to validate its creation, and store its status information in this Result.</p>
     * 
     * @param boolean ok indicates whether the object created is valid or not
     * @param value stores the object itself, which would be the Post or Result object
     * @param error stores an error message to be used if the creation did not pass
     * */
    private Result(boolean ok, T value, String error) {
        this.ok = ok;
        this.value = value;
        this.error = error;
    }

    /**
     * This function returns an Result<T> that is ok.
     * 
     * <p> This function returns an Result<T> that is ok, and therefore has no error message String but does
     * contain the original passed object.</p>
     * 
     * @param value stores the object itself, which would be the Post or Result object
     * */
    public static <T> Result<T> ok(T value) { return new Result<>(true, value, ""); }
    
    /**
     * This function returns an Result<T> that is not ok or failed.
     * 
     * <p> This function returns an Result<T> that is not ok, and therefore has the passed error message String,
     * and a null value for the object T itself because it wasn't successfully created.</p>
     * 
     * @param error stores an error message to be used if the creation did not pass
     * */
    public static <T> Result<T> fail(String error) { return new Result<>(false, null, error); }

    /**
     * This function returns this.ok.
     * 
     * <p> This function returns a boolean indicating if the Result is ok.</p>
     * */
    public boolean isOk() { return ok; }
    
    /**
     * This function returns this.value.
     * 
     * <p> This function returns an object indicating the Result's value or stored object.</p>
     * */
    public T getValue() { return value; }
    
    /**
     * This function returns this.error.
     * 
     * <p> This function returns a String indicating the Result's error message or empty if there isn't one.</p>
     * */
    public String getError() { return error; }
}
