package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Element representing a function.
 * 
 * @author labramusic
 *
 */
public class ElementFunction extends Element {

	/**
	 * Function name.
	 */
	private String name;

	/**
	 * Constructor which initializes the function name.
	 * 
	 * @param name
	 *            function name
	 */
	public ElementFunction(String name) {
		this.name = name;
	}

	/**
	 * Returns the function name.
	 * 
	 * @return function name
	 */
	public String getName() {
		return name;
	}

	@Override
	public String asText() {
		return "@" + name;
	}

}
