package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;

/**
 * Node representing a single for-loop construct.
 * 
 * @author labramusic
 *
 */
public class ForLoopNode extends Node {

	/**
	 * Variable in a for-loop.
	 */
	private ElementVariable variable;

	/**
	 * Starting for-loop expression.
	 */
	private Element startExpression;

	/**
	 * Ending for-loop expression.
	 */
	private Element endExpression;

	/**
	 * Expression representing a step in a for-loop. Can be null.
	 */
	private Element stepExpression;

	/**
	 * Constructor which initializes the for-loop elements.
	 * 
	 * @param variable
	 *            for-loop variable
	 * @param startExpression
	 *            for-loop start expression
	 * @param endExpression
	 *            for-loop end expression
	 * @param stepExpression
	 *            for-loop step expression
	 */
	public ForLoopNode(ElementVariable variable, Element startExpression, Element endExpression,
			Element stepExpression) {
		this.variable = variable;
		this.startExpression = startExpression;
		this.endExpression = endExpression;
		this.stepExpression = stepExpression;
	}

	/**
	 * Gets the variable.
	 * 
	 * @return the variable
	 */
	public ElementVariable getVariable() {
		return variable;
	}

	/**
	 * Gets the start expression.
	 * 
	 * @return the start expression
	 */
	public Element getStartExpression() {
		return startExpression;
	}

	/**
	 * Gets the end expression.
	 * 
	 * @return the end expression
	 */
	public Element getEndExpression() {
		return endExpression;
	}

	/**
	 * Gets the step expression.
	 * 
	 * @return the step expression
	 */
	public Element getStepExpression() {
		return stepExpression;
	}

	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visitForLoopNode(this);	
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{$ FOR ");
		sb.append(variable.asText());
		sb.append(" ");
		sb.append(startExpression.asText());
		sb.append(" ");
		sb.append(endExpression.asText());
		if (stepExpression != null) {
			sb.append(" ");
			sb.append(stepExpression.asText());
		}
		sb.append(" $}");
		return sb.toString();
	}

}
