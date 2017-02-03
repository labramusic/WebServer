package hr.fer.zemris.java.custom.scripting.exec;

/**
 * Wraps the value provided by the user. Supports basic arithmetic operations,
 * defined for integer and double values. This class will treat null values as
 * integers with value of 0 and will try to parse strings into decimal numbers
 * or integers. Other data types are not supported and throw an
 * IllegalArgumentException.
 * 
 * @author labramusic
 *
 */
public class ValueWrapper {

	/**
	 * The stored value.
	 */
	private Object value;

	/**
	 * Error message printed in case of illegal argument.
	 */
	private final static String errorMessage = "Legal values are null or instances of Integer, Double and String classes.";

	/**
	 * Initializes a new ValueWrapper object with the given value. Throws
	 * IllegalArgumentException in case of invalid argument.
	 * 
	 * @param value
	 *            given value
	 */
	public ValueWrapper(Object value) {
		if (illegalValue(value)) {
			throw new IllegalArgumentException(errorMessage);
		}
		this.value = determineType(value);
	}

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            the new value
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * Increments the current value with the given value and sets the stored
	 * value to the calculated result. Throws IllegalArgumentException in case
	 * of invalid argument.
	 * 
	 * @param incValue
	 *            the value for which the current value should be increased
	 */
	public void increment(Object incValue) {
		if (illegalValue(incValue)) {
			throw new IllegalArgumentException(errorMessage);
		}
		Number current = (Number)value;
		Number modValue = determineType(incValue);
		if (current instanceof Double || modValue instanceof Double) {
			value = current.doubleValue() + modValue.doubleValue();
		} else {
			value = current.intValue() + modValue.intValue();
		}
	}

	/**
	 * Increments the current value with the given value and sets the stored
	 * value to the calculated result. Throws IllegalArgumentException in case
	 * of invalid argument.
	 * 
	 * @param decValue
	 *            the value for which the current value should be decremented
	 */
	public void decrement(Object decValue) {
		if (illegalValue(decValue)) {
			throw new IllegalArgumentException(errorMessage);
		}
		Number current = (Number)value;
		Number modValue = determineType(decValue);
		if (current instanceof Double || modValue instanceof Double) {
			value = current.doubleValue() - modValue.doubleValue();
		} else {
			value = current.intValue() - modValue.intValue();
		}
	}

	/**
	 * Multiplies the current value with the given value and sets the stored
	 * value to the calculated result. Throws IllegalArgumentException in case
	 * of invalid argument.
	 * 
	 * @param mulValue
	 *            the value with which the current value should be multiplied
	 */
	public void multiply(Object mulValue) {
		if (illegalValue(mulValue)) {
			throw new IllegalArgumentException(errorMessage);
		}
		Number current = (Number)value;
		Number modValue = determineType(mulValue);
		if (current instanceof Double || modValue instanceof Double) {
			value = current.doubleValue() * modValue.doubleValue();
		} else {
			value = current.intValue() * modValue.intValue();
		}
	}

	/**
	 * Divides the current value with the given value and sets the stored value
	 * to the calculated result. Throws IllegalArgumentException in case of
	 * invalid argument.
	 * 
	 * @param divValue
	 *            the value with which the current value should be divided
	 */
	public void divide(Object divValue) {
		if (illegalValue(divValue)) {
			throw new IllegalArgumentException(errorMessage);
		}
		Number current = (Number)value;
		Number modValue = determineType(divValue);
		if (modValue.intValue() == 0) {
			System.err.println("Cannot divide by zero.");
		} else if (current instanceof Double || modValue instanceof Double) {
			value = current.doubleValue() / modValue.doubleValue();
		} else {
			value = current.intValue() / modValue.intValue();
		}
	}

	/**
	 * Performs numerical comparison between currently stored value and the
	 * given argument. Returns an integer less than zero if the currently stored
	 * value is smaller than the argument, an integer greater than zero if the
	 * currently stored value is larger than the argument or 0 if they are
	 * equal.
	 * 
	 * @param withValue
	 *            value to be compared with
	 * @return difference between stored and given value
	 */
	public int numCompare(Object withValue) {
		if (illegalValue(withValue)) {
			throw new IllegalArgumentException(errorMessage);
		}
		Number current = (Number)value;
		Number modValue = determineType(withValue);
		if (current instanceof Double || modValue instanceof Double) {
			Double double1 = current.doubleValue();
			Double double2 = modValue.doubleValue();
			return double1.compareTo(double2);
		} else {
			return current.intValue() - modValue.intValue();
		}
	}

	/**
	 * Returns true if given value is of illegal type, that is, if value is not
	 * null or is not instance of Integer, Double or String classes.
	 * 
	 * @param value
	 *            given value
	 * @return true if given value is illegal
	 */
	private static boolean illegalValue(Object value) {
		if (value == null || value instanceof Integer || value instanceof Double || value instanceof String) {
			return false;
		}
		return true;
	}

	/**
	 * Tries to parse the provided value as a double or an integer and returns
	 * its value as a number.
	 * 
	 * @param value
	 *            value to be parsed
	 * @return value as Integer or Double
	 */
	private static Number determineType(Object value) {
		if (value == null) {
			value = Integer.valueOf(0);

		} else if (value instanceof String) {
			String string = (String) value;
			if (string.matches("-?\\d*\\.\\d+|-?\\d+[Ee][+-]\\d+")) {
				try {
					value = Double.parseDouble(string);
				} catch (NumberFormatException e) {
					System.err.println("Unable to parse double");
				}

			} else {
				try {
					value = Integer.parseInt((string));
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(errorMessage);
				}
			}
		} else if (!(value instanceof Double) && !(value instanceof Integer)) {
			throw new IllegalArgumentException(errorMessage);
		}
		return (Number) value;
	}

}
