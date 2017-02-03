package hr.fer.zemris.java.custom.scripting.exec;

/**
 * Exception thrown in case of trying to peek or pop from an empty stack.
 * 
 * @author labramusic
 *
 */
public class EmptyStackException extends RuntimeException {

	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor which creates a new EmptyStackException.
	 */
	public EmptyStackException() {

	}

	/**
	 * Constructor which delegates the given message to the super class.
	 * 
	 * @param message
	 *            error message.
	 */
	public EmptyStackException(String message) {
		super(message);
	}

}
