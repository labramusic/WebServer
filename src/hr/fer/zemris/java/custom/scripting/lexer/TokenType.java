package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * Enumeration which lists possible token types.
 * 
 * @author labramusic
 *
 */
public enum TokenType {

	/**
	 * End of file token.
	 */
	EOF,
	/**
	 * A sequence of characters.
	 */
	TEXT,
	/**
	 * Name of tag. It must start with a letter, can be followed by digits or
	 * underscores. Can also be equal to the character '='.
	 */
	TAG,
	/**
	 * Name of variable. It must start with a letter, can be followed by digits
	 * or underscores.
	 */
	VAR,
	/**
	 * Integer constant.
	 */
	CONST_INT,
	/**
	 * Double constant.
	 */
	CONST_DOUBLE,
	/**
	 * String constant.
	 */
	STRING,
	/**
	 * Arithmetic operator. Valid operators are + (plus), - (minus), *
	 * (multiplication), / (division), ^ (power).
	 */
	OPERATOR,
	/**
	 * Name of function. It must start with a '@' sign and a letter, can be
	 * followed by digits or underscores.
	 */
	FUNCTION,
	/**
	 * Marks the end of a tag.
	 */
	END_TAG;

}
