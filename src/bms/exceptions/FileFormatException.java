package bms.exceptions;

/**
 * Exception thrown when a save file containing a list of building data
 * is invalid.
 */
public class FileFormatException extends Exception{
    /**
     * Constructs a normal FileFormatException with no error message or cause.
     */
    public FileFormatException() {
        super();
    }

    /**
     * Constructs a FileFormatException that contains a helpful message
     * detailing why the exception occurred.
     * <p>
     * <b>Note:</b> implementing this constructor is <b>optional</b>.
     * It has only been included in the Javadoc to ensure your code will compile
     * if you give your exception a message when throwing it. This practice
     * can be useful for debugging purposes.
     * <p>
     * <b>Important:</b> do not write JUnit tests that expect a valid
     * implementation of the assignment to have a certain error message, as the
     * official solution will use different messages to those you are expecting,
     * if any at all.
     */
    public FileFormatException(String message) {
        super(message);
    }
}
