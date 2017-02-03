package hr.fer.zemris.java.custom.scripting.demo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.INodeVisitor;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParserException;

/**
 * Demonstrates the work of SmartScriptParser and recreates the original text
 * from the generated structured document tree using the Visitor design pattern.
 * Accepts a single command-line argument: the path to the document.
 * 
 * @author labramusic
 *
 */
public class TreeWriter {

	/**
	 * Visitor implementation which writes the document form on the standard
	 * output.
	 * 
	 * @author labramusic
	 *
	 */
	private static class WriterVisitor implements INodeVisitor {
		@Override
		public void visitTextNode(TextNode node) {
			System.out.print(node);
		}

		@Override
		public void visitForLoopNode(ForLoopNode node) {
			System.out.print(node);

			int size = node.numberOfChildren();
			for (int i = 0; i < size; ++i) {
				node.getChild(i).accept(this);
			}
			System.out.print("{$ END $}");
		}

		@Override
		public void visitEchoNode(EchoNode node) {
			System.out.print(node);
		}

		@Override
		public void visitDocumentNode(DocumentNode node) {
			int size = node.numberOfChildren();
			for (int i = 0; i < size; ++i) {
				node.getChild(i).accept(this);
			}
		}
	}

	/**
	 * Main method invoked upon execution of the program.
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Document name expected as an argument.");
			return;
		}

		String filepath = args[0];
		String docBody = "";
		try {
			docBody = new String(Files.readAllBytes(Paths.get(filepath)), StandardCharsets.UTF_8);
		} catch (IOException e) {
			System.err.println("Error reading file: "+e+".");
		}

		SmartScriptParser parser = null;
		try {
			parser = new SmartScriptParser(docBody);
		} catch (SmartScriptParserException e) {
			System.out.println("Unable to parse document!");
			System.exit(-1);
		}

		WriterVisitor visitor = new WriterVisitor();
		parser.getDocumentNode().accept(visitor);
		// by the time the previous line completes its job, the document
		// should have been written on the standard output
	}

}
