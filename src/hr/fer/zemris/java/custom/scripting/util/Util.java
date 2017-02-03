package hr.fer.zemris.java.custom.scripting.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Scanner;

import hr.fer.zemris.java.webserver.SmartHttpServer;

/**
 * Utility class used by the {@link SmartHttpServer} class.
 * 
 * @author labramusic
 *
 */
public class Util {

	/**
	 * Requires the given property not to be null and returns its integer value
	 * if possible.
	 * 
	 * @param property
	 *            given property
	 * @return integer value
	 */
	public static Integer getAsInt(String property) {
		Objects.requireNonNull(property);
		try {
			return Integer.parseInt(property);
		} catch (NumberFormatException e) {
			throw new NumberFormatException("The given property " + property + " cannot be parsed to an integer");
		}
	}

	/**
	 * Reads the file from the given path and composes a string out of its
	 * contents.
	 * 
	 * @param path
	 *            file
	 * @return extracted string
	 */
	public static String getStringFromPath(Path path) {
		String string = "";
		try (Scanner sc = new Scanner(path)) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				string += (line + "\r\n");
			}
		} catch (IOException e) {
			System.err.println("File at " + path + " couldn't be read.");
		}
		return string;
	}

}
