package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.scripting.elems.Element;

/**
 * Node representing a command which generates some textual output dynamically.
 * 
 * @author labramusic
 *
 */
public class EchoNode extends Node {

	/**
	 * Elements of the echo node.
	 */
	private Element[] elements;

	/**
	 * Constructor which initializes the echo node elements.
	 * 
	 * @param elements
	 *            echo node elements
	 */
	public EchoNode(Element[] elements) {
		this.elements = elements;
	}

	/**
	 * Gets the elements of the echo node.
	 * 
	 * @return elements of the echo node
	 */
	public Element[] getElements() {
		return elements;
	}

	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visitEchoNode(this);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int length = elements.length;
		sb.append("{$ = ");
		for (int i = 0; i < length; ++i) {
			sb.append(elements[i].asText());
			sb.append(" ");
		}
		sb.append("$}");
		return sb.toString();
	}

}
