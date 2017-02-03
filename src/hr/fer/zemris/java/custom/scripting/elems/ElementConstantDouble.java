package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Element representing a double constant.
 * 
 * @author labramusic
 *
 */
public class ElementConstantDouble extends ElementConstant {

	/**
	 * Value of double.
	 */
	private double value;

	/**
	 * Constructor which initializes the double value.
	 * 
	 * @param value
	 *            double value
	 */
	public ElementConstantDouble(double value) {
		this.value = value;
	}

	@Override
	public Double getValue() {
		return value;
	}

	@Override
	public String asText() {
		return String.valueOf(value);
	}

}
