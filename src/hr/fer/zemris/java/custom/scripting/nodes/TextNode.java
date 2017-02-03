package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * Node representing a piece of textual data.
 * 
 * @author labramusic
 *
 */
public class TextNode extends Node {

	/**
	 * Textual data.
	 */
	private String text;

	/**
	 * Constructor which initializes the node text.
	 * 
	 * @param text
	 *            text data
	 */
	public TextNode(String text) {
		this.text = text;
	}

	/**
	 * Returns the text.
	 * 
	 * @return text data
	 */
	public String getText() {
		return text;
	}

	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visitTextNode(this);
	}

	@Override
	public String toString() {
		String text = this.text.replace("\\", "\\\\");
		text = text.replace("{", "\\{");
		return text;
	}

}
