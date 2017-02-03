package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * The state of the token during lexing represented by the last character read.
 * 
 * @author labramusic
 *
 */
public enum TokenState {

	/**
	 * A word has been read.
	 */
	WORD,
	/**
	 * An escape symbol has been read.
	 */
	ESCAPE,
	/**
	 * A tag is being opened.
	 */
	TAG_OPEN,
	/**
	 * A name tag has been read.
	 */
	TAG_NAME,
	/**
	 * A tag is being closed.
	 */
	TAG_CLOSED,
	/**
	 * A variable has been read.
	 */
	VAR,
	/**
	 * An integer constant has been read.
	 */
	CONST_INT,
	/**
	 * A double constant has been read.
	 */
	CONST_DOUBLE,
	/**
	 * A string constant has been read.
	 */
	STRING,
	/**
	 * A function has been read.
	 */
	FUNCTION,
	/**
	 * A minus sign (-) has been read.
	 */
	MINUS,
	/**
	 * An operator has been read.
	 */
	OPERATOR,
	/**
	 * Nothing has been read yet.
	 */
	INIT;

}
