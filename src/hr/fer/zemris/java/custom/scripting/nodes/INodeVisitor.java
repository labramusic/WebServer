package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * A node visitor interface which performs a single operation on a specified
 * node.
 * 
 * @author labramusic
 *
 */
public interface INodeVisitor {

	/**
	 * Visits the given text node.
	 * 
	 * @param node
	 *            text node
	 */
	public void visitTextNode(TextNode node);

	/**
	 * Visits the given for-loop node.
	 * 
	 * @param node
	 *            for-loop node
	 */
	public void visitForLoopNode(ForLoopNode node);

	/**
	 * Visits the given echo node.
	 * 
	 * @param node
	 *            echo node
	 */
	public void visitEchoNode(EchoNode node);

	/**
	 * Visits the given document node.
	 * 
	 * @param node
	 *            document node
	 */
	public void visitDocumentNode(DocumentNode node);

}
