package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Element representing an integer constant.
 * 
 * @author labramusic
 *
 */
public class ElementConstantInteger extends ElementConstant {

	/**
	 * Value of integer.
	 */
	private int value;

	/**
	 * Constructor which initializes the integer value.
	 * 
	 * @param value
	 *            integer value.
	 */
	public ElementConstantInteger(int value) {
		this.value = value;
	}

	@Override
	public Integer getValue() {
		return value;
	}

	@Override
	public String asText() {
		return String.valueOf(value);
	}

}
