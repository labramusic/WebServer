package hr.fer.zemris.java.custom.scripting.nodes;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all graph nodes representing structured documents.
 * 
 * @author labramusic
 *
 */
public abstract class Node {

	/**
	 * Children nodes.
	 */
	private List<Node> children;

	/**
	 * Adds given child to an internally managed collection of children. This
	 * collection is created on the first call of this function.
	 * 
	 * @param child
	 *            child node
	 */
	public void addChildNode(Node child) {
		if (children == null) {
			children = new ArrayList<>();
		}
		children.add(child);
	}

	/**
	 * Returns the number of direct children.
	 * 
	 * @return number of direct children
	 */
	public int numberOfChildren() {
		if (children == null) {
			return 0;
		}
		return children.size();
	}

	/**
	 * Returns child at given index. Throws NullPointerException in case node
	 * has no child nodes. Throws IndexOutOfBoundsException if index is invalid.
	 * 
	 * @param index
	 *            index of child
	 * @return child at given index
	 */
	public Node getChild(int index) {
		if (children == null) {
			throw new NullPointerException("Node doesn't contain any children.");
		}
		return children.get(index);
	}

	/**
	 * Visits the node using the appropriate node visitor method.
	 * 
	 * @param visitor
	 *            node visitor
	 */
	public abstract void accept(INodeVisitor visitor);

}
