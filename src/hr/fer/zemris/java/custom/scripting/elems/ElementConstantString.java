package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Element representing a string constant.
 * 
 * @author labramusic
 *
 */
public class ElementConstantString extends ElementConstant {

	/**
	 * String value.
	 */
	private String value;

	/**
	 * Constructor which initializes the string value.
	 * 
	 * @param value
	 *            string value
	 */
	public ElementConstantString(String value) {
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String asText() {
		String text = value.replace("\\", "\\\\");
		text = text.replace("\"", "\\\"");
		return "\"" + text + "\"";
	}

}
