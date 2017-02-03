package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * Represents the state in which the lexer operates.
 * 
 * @author labramusic
 *
 */
public enum LexerState {

	/**
	 * Reads text normally.
	 */
	TEXT,
	/**
	 * Reads tags with a special set of rules.
	 */
	TAG;

}