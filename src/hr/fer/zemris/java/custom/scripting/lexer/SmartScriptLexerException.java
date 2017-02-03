package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * Exception thrown in case of error while generating a new lexic token.
 * 
 * @author labramusic
 *
 */
public class SmartScriptLexerException extends RuntimeException {

	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor which initializes a new SmartScriptLexerException.
	 */
	public SmartScriptLexerException() {

	}

	/**
	 * Constructor which delegates the given message to the super class.
	 * 
	 * @param message
	 *            error message
	 */
	public SmartScriptLexerException(String message) {
		super(message);
	}

}