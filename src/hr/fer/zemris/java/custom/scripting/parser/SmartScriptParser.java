package hr.fer.zemris.java.custom.scripting.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantDouble;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantInteger;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantString;
import hr.fer.zemris.java.custom.scripting.elems.ElementFunction;
import hr.fer.zemris.java.custom.scripting.elems.ElementOperator;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;
import hr.fer.zemris.java.custom.scripting.lexer.SmartScriptLexer;
import hr.fer.zemris.java.custom.scripting.lexer.SmartScriptLexerException;
import hr.fer.zemris.java.custom.scripting.lexer.Token;
import hr.fer.zemris.java.custom.scripting.lexer.TokenType;
import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.Node;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;

/**
 * A parser which generates nodes from the tokens generated by SmartScriptLexer
 * and builds a structured document tree. Content enclosed in non-empty tags is
 * added as children nodes to the non-empty tag node. The text can afterwards be
 * rebuilt from the generated document.
 * 
 * @author labramusic
 *
 */
public class SmartScriptParser {

	/**
	 * The node containing the document.
	 */
	private DocumentNode documentNode;

	/**
	 * Constructor which initializes a new SmartScriptParser, which forwards the
	 * given document to the SmartScriptLexer and then parses it.
	 * 
	 * @param document
	 *            document text
	 */
	public SmartScriptParser(String document) {
		documentNode = new DocumentNode();
		SmartScriptLexer lexer = new SmartScriptLexer(document);
		parse(lexer);
	}

	/**
	 * Parses the document and sets the structure of the document node.
	 * 
	 * @param lexer
	 *            an instance of SmartScriptLexer
	 * @throws SmartScriptParserException
	 *             in case of error while parsing
	 */
	private void parse(SmartScriptLexer lexer) {
		Stack<Node> stack = new Stack<>();
		stack.push(documentNode);
		try {
			lexer.nextToken();
			while (!lexer.getToken().getType().equals(TokenType.EOF)) {
				Token token = lexer.getToken();

				if (token.getType().equals(TokenType.TEXT)) {
					// text node
					Node node = new TextNode(token.getValue().toString());
					Node last = stack.peek();
					last.addChildNode(node);

				} else if (token.getType().equals(TokenType.TAG)) {
					String name = token.getValue().toString();
					if (name.equals("=")) {
						// empty tag

						lexer.nextToken();
						List<Element> list = new ArrayList<>();
						while (!lexer.getToken().getType().equals(TokenType.END_TAG)) {
							Element element = elementType(lexer.getToken());
							list.add(element);
							lexer.nextToken();
						}
						if (list.isEmpty()) {
							throw new SmartScriptParserException("Echo tag must contain at least one element.");
						}
						Object[] objects = list.toArray();
						Element[] elements = new Element[objects.length];
						for (int i = 0; i < objects.length; ++i) {
							elements[i] = (Element) objects[i];
						}
						Node node = new EchoNode(elements);
						Node last = stack.peek();
						last.addChildNode(node);

					} else if (name.equalsIgnoreCase("FOR")) {
						// non-empty tag

						lexer.nextToken();
						if (!lexer.getToken().getType().equals(TokenType.VAR)) {
							throw new SmartScriptParserException("Invalid for-loop syntax. Variable expected.");
						}
						ElementVariable var = new ElementVariable(lexer.getToken().getValue().toString());

						lexer.nextToken();
						if (!isForElement(lexer.getToken())) {
							throw new SmartScriptParserException("Invalid for-loop syntax.");
						}
						Element start = elementType(lexer.getToken());

						lexer.nextToken();
						if (!isForElement(lexer.getToken())) {
							throw new SmartScriptParserException("Invalid for-loop syntax.");
						}
						Element end = elementType(lexer.getToken());

						lexer.nextToken();
						Element step = null;
						if (isForElement(lexer.getToken())) {
							step = elementType(lexer.getToken());
							lexer.nextToken();
						}
						if (!lexer.getToken().getType().equals(TokenType.END_TAG)) {
							throw new SmartScriptParserException("Invalid for-loop syntax.");
						}
						Node node = new ForLoopNode(var, start, end, step);
						Node last = stack.peek();
						last.addChildNode(node);
						stack.push(node);

					} else if (name.equalsIgnoreCase("END")) {
						// end tag

						stack.pop();
						if (stack.isEmpty()) {
							throw new SmartScriptParserException("Document contains an excess end tag.");
						}
						lexer.nextToken();
						if (!lexer.getToken().getType().equals(TokenType.END_TAG)) {
							throw new SmartScriptParserException("Invalid end tag.");
						}

					} else {
						throw new SmartScriptParserException("Invalid tag name.");
					}

				} else {
					throw new SmartScriptParserException("Invalid token.");
				}

				lexer.nextToken();
			}

			if (stack.size() != 1) {
				throw new SmartScriptParserException("There are unclosed tags in the document.");
			}
			documentNode = (DocumentNode) stack.pop();

		} catch (SmartScriptLexerException e) {
			throw new SmartScriptParserException();
		}
	}

	/**
	 * Checks if token type is a valid for-loop element.
	 * 
	 * @param token
	 *            token to be evaluated
	 * @return true only if token type is a valid element
	 */
	private static boolean isForElement(Token token) {
		TokenType type = token.getType();
		if (type.equals(TokenType.VAR) || type.equals(TokenType.STRING) || type.equals(TokenType.CONST_INT)
				|| type.equals(TokenType.CONST_DOUBLE)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks the token type and returns a new element of the appropriate type.
	 * 
	 * @param token
	 *            generated token
	 * @return element of appropriate type
	 */
	private static Element elementType(Token token) {
		Object value = token.getValue();
		switch (token.getType()) {
		case VAR:
			return new ElementVariable(value.toString());
		case STRING:
			return new ElementConstantString(value.toString());
		case CONST_INT:
			return new ElementConstantInteger((int) value);
		case CONST_DOUBLE:
			return new ElementConstantDouble((double) value);
		case FUNCTION:
			return new ElementFunction(value.toString());
		case OPERATOR:
			return new ElementOperator(value.toString());
		default:
			return null;
		}
	}

	/**
	 * Gets the document node.
	 * 
	 * @return the document node
	 */
	public DocumentNode getDocumentNode() {
		return documentNode;
	}

}
