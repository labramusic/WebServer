package hr.fer.zemris.java.custom.scripting.exec;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Stack;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstant;
import hr.fer.zemris.java.custom.scripting.elems.ElementFunction;
import hr.fer.zemris.java.custom.scripting.elems.ElementOperator;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;
import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.INodeVisitor;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Class which executes the document whose parsed tree it obtains.
 * 
 * @author labramusic
 *
 */
public class SmartScriptEngine {

	/**
	 * The document node.
	 */
	private DocumentNode documentNode;

	/**
	 * The request context.
	 */
	private RequestContext requestContext;

	/**
	 * The object multistack.
	 */
	private ObjectMultistack multistack = new ObjectMultistack();

	/**
	 * Tree visitor implementation which sends appropriate content for each node
	 * to the request context.
	 */
	private INodeVisitor visitor = new INodeVisitor() {

		@Override
		public void visitTextNode(TextNode node) {
			try {
				requestContext.write(node.getText());
			} catch (IOException e) {
				System.err.println("IOException " + e + " occurred while writing to request context.");
			}
		}

		@Override
		public void visitForLoopNode(ForLoopNode node) {
			int size = node.numberOfChildren();
			// initializes start value
			String varName = node.getVariable().getName();
			ValueWrapper startValue = new ValueWrapper(node.getStartExpression().asText());
			Object endValue = new ValueWrapper(node.getEndExpression().asText()).getValue();
			Object stepValue = new ValueWrapper(node.getStepExpression().asText()).getValue();

			multistack.push(varName, startValue);
			ValueWrapper value = startValue;
			while (value.numCompare(endValue) <= 0) {
				for (int i = 0; i < size; ++i) {
					node.getChild(i).accept(this);
				}
				multistack.peek(varName).increment(stepValue);
			}
			multistack.pop(varName);
		}

		@Override
		public void visitEchoNode(EchoNode node) {
			Stack<Object> stack = new Stack<>();
			Element[] elements = node.getElements();
			for (Element elem : elements) {
				if (elem instanceof ElementConstant) {
					stack.push(((ElementConstant) elem).getValue());

				} else if (elem instanceof ElementVariable) {
					Object value = multistack.peek(((ElementVariable) elem).getName()).getValue();
					stack.push(value);

				} else if (elem instanceof ElementOperator) {
					execOperator(stack, (ElementOperator) elem);

				} else if (elem instanceof ElementFunction) {
					execFunction(stack, (ElementFunction) elem);
				}
			}

			Stack<String> invStack = new Stack<>();
			while (!stack.isEmpty()) {
				invStack.push(stack.pop().toString());
			}

			while (!invStack.isEmpty()) {
				try {
					requestContext.write(invStack.pop());
				} catch (IOException e) {
					System.err.println("IOException " + e + " occurred while writing to request context.");
				}
			}
		}

		/**
		 * Executes the appropriate action for an operator element. For each
		 * operator pops two arguments, executes the operation and stores the
		 * result on the stack.
		 * 
		 * @param stack
		 *            temporary stack
		 * @param elem
		 *            operator element
		 */
		private void execOperator(Stack<Object> stack, ElementOperator elem) {
			ValueWrapper arg2 = new ValueWrapper(stack.pop());
			ValueWrapper arg1 = new ValueWrapper(stack.pop());
			String symbol = elem.getSymbol();
			switch (symbol) {
			case "+":
				arg1.increment(arg2.getValue());
				break;

			case "-":
				arg1.decrement(arg2.getValue());
				break;

			case "*":
				arg1.multiply(arg2.getValue());
				break;

			case "/":
				arg1.divide(arg2.getValue());
				break;
			}

			stack.push(arg1.getValue());
		}

		/**
		 * Executes the appropriate action for a function element. For each
		 * function pops the required number of arguments from the temporary
		 * stack, executes the function and stores the result on the stack.
		 * 
		 * @param stack
		 *            temporary stack
		 * @param elem
		 *            function element
		 */
		private void execFunction(Stack<Object> stack, ElementFunction elem) {
			String function = elem.getName();
			// angle in degrees
			if (function.equals("sin")) {
				Object value = new ValueWrapper(stack.pop()).getValue();
				Double degAngle = Double.valueOf(value.toString());
				Double radAngle = Math.toRadians(degAngle);
				Double result = Math.sin(radAngle);
				stack.push(result);

			} else if (function.equals("decfmt")) {
				String f = stack.pop().toString();
				DecimalFormat format = new DecimalFormat(f);
				Object value = new ValueWrapper(stack.pop()).getValue();
				String result = format.format(value);
				stack.push(result);

			} else if (function.equals("dup")) {
				Object value = new ValueWrapper(stack.pop()).getValue();
				stack.push(value);
				stack.push(value);

			} else if (function.equals("swap")) {
				Object value1 = new ValueWrapper(stack.pop()).getValue();
				Object value2 = new ValueWrapper(stack.pop()).getValue();
				stack.push(value1);
				stack.push(value2);

			} else if (function.equals("setMimeType")) {
				String mimeType = stack.pop().toString();
				requestContext.setMimeType(mimeType);

			} else if (function.equals("paramGet")) {
				Object defValue = new ValueWrapper(stack.pop()).getValue();
				String name = stack.pop().toString();
				String param = requestContext.getParameter(name);
				stack.push(param == null ? defValue : param);

			} else if (function.equals("pparamGet")) {
				Object defValue = new ValueWrapper(stack.pop()).getValue();
				String name = stack.pop().toString();
				String pparam = requestContext.getPersistentParameter(name);
				stack.push(pparam == null ? defValue : pparam);

			} else if (function.equals("pparamSet")) {
				String name = stack.pop().toString();
				String value = new ValueWrapper(stack.pop()).getValue().toString();
				requestContext.setPersistentParameter(name, value);

			} else if (function.equals("pparamDel")) {
				String name = stack.pop().toString();
				requestContext.removePersistentParameter(name);

			} else if (function.equals("tparamGet")) {
				Object defValue = new ValueWrapper(stack.pop()).getValue();
				String name = stack.pop().toString();
				String tparam = requestContext.getTemporaryParameter(name);
				stack.push(tparam == null ? defValue : tparam);

			} else if (function.equals("tparamSet")) {
				String name = stack.pop().toString();
				String value = new ValueWrapper(stack.pop()).getValue().toString();
				requestContext.setTemporaryParameter(name, value);

			} else if (function.equals("tparamDel")) {
				String name = stack.pop().toString();
				requestContext.removeTemporaryParameter(name);

			}
		}

		@Override
		public void visitDocumentNode(DocumentNode node) {
			int size = node.numberOfChildren();
			for (int i = 0; i < size; ++i) {
				node.getChild(i).accept(this);
			}
		}
	};

	/**
	 * Initializes a new SmartScriptEngine.
	 * 
	 * @param documentNode
	 *            the document node
	 * @param requestContext
	 *            the request context
	 */
	public SmartScriptEngine(DocumentNode documentNode, RequestContext requestContext) {
		this.documentNode = documentNode;
		this.requestContext = requestContext;
	}

	/**
	 * Executes the smart script.
	 */
	public void execute() {
		documentNode.accept(visitor);
	}

}
