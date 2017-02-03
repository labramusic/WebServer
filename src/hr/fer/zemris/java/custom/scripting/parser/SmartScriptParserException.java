package hr.fer.zemris.java.custom.scripting.parser;

/**
 * Exception thrown in case of error while parsing a text document.
 * 
 * @author labramusic
 *
 */
public class SmartScriptParserException extends RuntimeException {

	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor which initializes a new SmartScriptParserException.
	 */
	public SmartScriptParserException() {

	}

	/**
	 * Constructor which delegates the given message to the super class.
	 * 
	 * @param message
	 *            error message
	 */
	public SmartScriptParserException(String message) {
		super(message);
	}

}
