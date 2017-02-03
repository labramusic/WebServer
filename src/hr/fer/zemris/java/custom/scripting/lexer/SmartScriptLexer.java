package hr.fer.zemris.java.custom.scripting.lexer;

/**
 * A lexer which can generate tokens on demand. It can generate tokens given by
 * the TokenType enumeration. If it reads a tag opener ( "{$" ), it will
 * generate tokens based on a different set of rules until the tag is closed
 * with a tag closer ( "$}" ).
 * 
 * @author labramusic
 *
 */
public class SmartScriptLexer {

	/**
	 * Input text.
	 */
	private char[] data;

	/**
	 * The current token.
	 */
	private Token token;

	/**
	 * Index of the first unevaluated character.
	 */
	private int currentIndex;

	/**
	 * Current state of the lexer.
	 */
	private LexerState state;

	/**
	 * Current state of the token.
	 */
	private TokenState tokenState;

	/**
	 * Constructor which accepts the input text for tokenization.
	 * 
	 * @param text
	 *            text to be tokenized
	 */
	public SmartScriptLexer(String text) {
		if (text == null) {
			throw new IllegalArgumentException();
		}
		data = text.toCharArray();
		state = LexerState.TEXT;
		tokenState = TokenState.INIT;
	}

	/**
	 * Generates and returns the next token. SmartScriptLexerException is thrown
	 * in case of error.
	 * 
	 * @throws SmartScriptLexerException
	 *             in case of error while generating tokens
	 * @return next token
	 */
	public Token nextToken() {
		if (token != null && token.getType().equals(TokenType.EOF)) {
			throw new SmartScriptLexerException();
		}

		if (currentIndex == data.length) {
			if (state.equals(LexerState.TAG)) {
				throw new SmartScriptLexerException("Invalid syntax. Tags must be closed.");
			}
			token = new Token(TokenType.EOF, null);
			return token;
		}

		StringBuilder buffer = new StringBuilder();
		char ch;

		switch (state) {
		case TEXT:
			while (true) {
				if (currentIndex < data.length) {
					ch = data[currentIndex];
				} else {
					if (tokenState.equals(TokenState.ESCAPE)) {
						throw new SmartScriptLexerException("Invalid syntax. Tags must be closed.");
					}
					ch = '\n';
				}

				switch (tokenState) {
				case WORD:
					if (currentIndex == data.length) {
						token = new Token(TokenType.TEXT, buffer.toString());
						return token;
					} else if (ch == '{') {
						tokenState = TokenState.TAG_OPEN;
					} else if (ch == '\\') {
						tokenState = TokenState.ESCAPE;
					} else {
						buffer.append(ch);
					}
					++currentIndex;
					break;

				case ESCAPE:
					if (ch == '{' || ch == '\\') {
						tokenState = TokenState.WORD;
						buffer.append(ch);
					} else {
						throw new SmartScriptLexerException("Invalid escape sequence.");
					}
					++currentIndex;
					break;

				case TAG_OPEN:
					if (ch == '$') {
						state = LexerState.TAG;
						tokenState = TokenState.TAG_NAME;
						++currentIndex;
						if (buffer.length() != 0) {
							token = new Token(TokenType.TEXT, buffer.toString());
							return token;
						} else {
							return nextToken();
						}
					} else {
						buffer.append("{");
						tokenState = TokenState.WORD;
					}

				case INIT:
					if (ch == '{') {
						tokenState = TokenState.TAG_OPEN;
						++currentIndex;
					} else if (ch == '\\') {
						tokenState = TokenState.ESCAPE;
						++currentIndex;
					} else {
						tokenState = TokenState.WORD;
					}
					break;

				default:
					throw new SmartScriptLexerException("Irregular state.");
				}
			}

		case TAG:
			skipWhitespaces();
			while (true) {
				if (currentIndex < data.length) {
					ch = data[currentIndex];
				} else {
					throw new SmartScriptLexerException("Invalid syntax. Tags must be closed.");
				}

				switch (tokenState) {
				case TAG_NAME:
					if (ch == '=') {
						tokenState = TokenState.INIT;
						++currentIndex;
						token = new Token(TokenType.TAG, String.valueOf(ch));
						return token;
					} else if (Character.isLetter(ch)) {
						buffer.append(ch);
					} else if (buffer.length() != 0) {
						if (Character.isDigit(ch) || ch == '_') {
							buffer.append(ch);
						} else {
							tokenState = TokenState.INIT;
							token = new Token(TokenType.TAG, buffer.toString());
							return token;
						}
					} else {
						throw new SmartScriptLexerException("Invalid tag name");
					}
					++currentIndex;
					break;

				case FUNCTION:
					if (Character.isLetter(ch)) {
						buffer.append(ch);
					} else if (buffer.length() != 0) {
						if (Character.isDigit(ch) || ch == '_') {
							buffer.append(ch);
						} else {
							tokenState = TokenState.INIT;
							token = new Token(TokenType.FUNCTION, buffer.toString());
							return token;
						}
					} else {
						throw new SmartScriptLexerException("Invalid tag name");
					}
					++currentIndex;
					break;

				case VAR:
					if (Character.isLetterOrDigit(ch) || ch == '_') {
						buffer.append(ch);
					} else {
						tokenState = TokenState.INIT;
						token = new Token(TokenType.VAR, buffer.toString());
						return token;
					}
					++currentIndex;
					break;

				case CONST_INT:
					if (Character.isDigit(ch)) {
						buffer.append(ch);
					} else if (ch == '.') {
						buffer.append(ch);
						++currentIndex;
						tokenState = TokenState.CONST_DOUBLE;
						break;
					} else
						try {
							tokenState = TokenState.INIT;
							int number = Integer.valueOf(buffer.toString());
							token = new Token(TokenType.CONST_INT, number);
							return token;
						} catch (NumberFormatException ex) {
							throw new SmartScriptLexerException("Number too big.");
						}
					++currentIndex;
					break;

				case CONST_DOUBLE:
					if (Character.isDigit(ch)) {
						buffer.append(ch);
					} else if (buffer.toString().endsWith(".")) {
						throw new SmartScriptLexerException("Invalid syntax.");
					} else
						try {
							tokenState = TokenState.INIT;
							double number = Double.valueOf(buffer.toString());
							token = new Token(TokenType.CONST_DOUBLE, number);
							return token;
						} catch (NumberFormatException ex) {
							throw new SmartScriptLexerException("Number too big.");
						}
					++currentIndex;
					break;

				case MINUS:
					if (buffer.length() == 0) {
						buffer.append(ch);
						++currentIndex;
					} else if (Character.isDigit(ch)) {
						tokenState = TokenState.CONST_INT;
					} else {
						tokenState = TokenState.INIT;
						token = new Token(TokenType.OPERATOR, buffer.toString());
						return token;
					}
					break;

				case OPERATOR:
					tokenState = TokenState.INIT;
					++currentIndex;
					token = new Token(TokenType.OPERATOR, String.valueOf(ch));
					return token;

				case STRING:
					if (ch == '\"') {
						if (buffer.length() == 0) {
							throw new SmartScriptLexerException("String cannot be empty.");
						}
						tokenState = TokenState.INIT;
						++currentIndex;
						token = new Token(TokenType.STRING, buffer.toString());
						return token;
					} else if (ch == '\\') {
						tokenState = TokenState.ESCAPE;
					} else {
						buffer.append(ch);
					}
					++currentIndex;
					break;

				case ESCAPE:
					if (ch == '\\' || ch == '\"') {
						buffer.append(ch);
					} else if (ch == 'r') {
						buffer.append('\r');
					} else if (ch == 'n') {
						buffer.append('\n');
					} else if (ch == 't') {
						buffer.append('\t');
					} else {
						throw new SmartScriptLexerException("Invalid escape sequence.");
					}
					tokenState = TokenState.STRING;
					++currentIndex;
					break;

				case TAG_CLOSED:
					if (ch == '}') {
						state = LexerState.TEXT;
						tokenState = TokenState.INIT;
						++currentIndex;
						token = new Token(TokenType.END_TAG, null);
						return token;
					} else {
						throw new SmartScriptLexerException("Invalid syntax.");
					}

				case INIT:
					if (Character.isLetter(ch)) {
						tokenState = TokenState.VAR;
					} else if (Character.isDigit(ch)) {
						tokenState = TokenState.CONST_INT;
					} else if (ch == '-') {
						tokenState = TokenState.MINUS;
					} else if (ch == '+' || ch == '*' || ch == '/' || ch == '^') {
						tokenState = TokenState.OPERATOR;
					} else if (ch == '@') {
						tokenState = TokenState.FUNCTION;
						++currentIndex;
					} else if (ch == '\"') {
						tokenState = TokenState.STRING;
						++currentIndex;
					} else if (ch == '$') {
						tokenState = TokenState.TAG_CLOSED;
						++currentIndex;
					} else {
						throw new SmartScriptLexerException("Invalid tag element.");
					}
					break;

				default:
					throw new SmartScriptLexerException("Irregular state.");
				}
			}

		}

		return token;
	}

	/**
	 * Skips all whitespaces until the next character in the given text.
	 */
	private void skipWhitespaces() {
		while (currentIndex < data.length) {
			char ch = data[currentIndex];
			if (Character.isWhitespace(ch)) {
				++currentIndex;
				continue;
			}
			break;
		}
	}

	/**
	 * Returns the previously generated token, without generating the next one.
	 * 
	 * @return previously generated token
	 */
	public Token getToken() {
		return token;
	}

	/**
	 * Sets the lexer state.
	 * 
	 * @param state
	 *            new lexer state
	 */
	public void setState(LexerState state) {
		if (state == null) {
			throw new IllegalArgumentException();
		}
		this.state = state;
	}

}
