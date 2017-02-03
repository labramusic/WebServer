package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Element representing an arithmetic operator.
 * 
 * @author labramusic
 *
 */
public class ElementOperator extends Element {

	/**
	 * Operator symbol.
	 */
	private String symbol;

	/**
	 * Constructor which initializes the operator symbol.
	 * 
	 * @param symbol
	 *            operator symbol
	 */
	public ElementOperator(String symbol) {
		this.symbol = symbol;
	}

	/**
	 * Returns the operator symbol.
	 * 
	 * @return operator symbol
	 */
	public String getSymbol() {
		return symbol;
	}

	@Override
	public String asText() {
		return symbol;
	}

}
