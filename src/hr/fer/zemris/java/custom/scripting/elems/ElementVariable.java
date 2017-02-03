package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Element representing a variable.
 * 
 * @author labramusic
 *
 */
public class ElementVariable extends Element {

	/**
	 * Variable name.
	 */
	private String name;

	/**
	 * Constructor which initializes the variable name.
	 * 
	 * @param name
	 *            variable name
	 */
	public ElementVariable(String name) {
		this.name = name;
	}

	/**
	 * Returns the variable name.
	 * 
	 * @return variable name
	 */
	public String getName() {
		return name;
	}

	@Override
	public String asText() {
		return name;
	}

}
