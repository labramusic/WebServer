package hr.fer.zemris.java.custom.scripting.exec;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of a stack-like collection which allows the user to store 
 * multiple values for the same key. It can be observed as a map which has 
 * values that are separate stacks. Values on those stacks can be accessed
 * with regular stack-like abstraction.
 * @author labramusic
 *
 */
public class ObjectMultistack {

	/**
	 * Maps keys represented by strings to the corresponding stack pointers.
	 */
	private Map<String, MultistackEntry> stacksMap;

	/**
	 * Default constructor which initializes a new ObjectMultistack object.
	 */
	public ObjectMultistack() {
		super();
		stacksMap = new HashMap<>();
	}

	/**
	 * Pushes the given value on the stack associated with the given key.
	 * @param name given key
	 * @param valueWrapper given value
	 */
	public void push(String name, ValueWrapper valueWrapper) {
		if (name == null || valueWrapper == null) {
			throw new IllegalArgumentException("Cannot add null value.");
		}
		MultistackEntry oldEntry = stacksMap.get(name);
		MultistackEntry entry = new MultistackEntry(valueWrapper, oldEntry);
		stacksMap.put(name, entry);
	}

	/**
	 * Removes the last value pushed on the stack associated with the given key 
	 * and returns it. Throws EmptyStackException if the stack is empty when 
	 * this method is called.
	 * @param name given key
	 * @return value at the top of the stack
	 */
	public ValueWrapper pop(String name) {
		if (isEmpty(name)) {
			throw new EmptyStackException("Cannot pop from empty stack.");
		}
		MultistackEntry entry = stacksMap.get(name);
		stacksMap.put(name, entry.next);
		return entry.data;
	}

	/**
	 * Returns last element placed on the stack associated with the given key
	 * without removing it from the stack.
	 * Throws EmptyStackException if the stack is empty when this method is
	 * called.
	 * @param name given key
	 * @return value at the top of the stack
	 */
	public ValueWrapper peek(String name) {
		if (isEmpty(name)) {
			throw new EmptyStackException("Cannot peek from empty stack.");
		}
		return stacksMap.get(name).data;

	}

	/**
	 * Checks if stack associated with given key contains any elements,
	 * returns true if empty.
	 * @param name given key
	 * @return true if stack contains no elements, false otherwise
	 */
	public boolean isEmpty(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Key cannot be null.");
		}
		return stacksMap.get(name) == null;
	}

	/**
	 * Acts as a node of a singly linked list, containing data
	 * and a pointer to the next entry in the list.
	 * @author labramusic
	 *
	 */
	private static class MultistackEntry {

		/**
		 * Data contained in the node.
		 */
		private ValueWrapper data;

		/**
		 * The next node in the linked list.
		 */
		private MultistackEntry next;

		/**
		 * Initializes a new MultistackEntry with the given data and the next node.
		 * @param data given data
		 * @param next next node in the list
		 */
		protected MultistackEntry(ValueWrapper data, MultistackEntry next) {
			this.data = data;
			this.next = next;
		}

	}

}
