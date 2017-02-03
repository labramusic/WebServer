package hr.fer.zemris.java.custom.scripting.exec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ObjectMultistackTests {

	private ObjectMultistack multistack;

	@Before
	public void initialize() {
		multistack = new ObjectMultistack();
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNullKey() {
		multistack.push(null, new ValueWrapper(20));
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNullWrapper() {
		multistack.push("Key", null);
	}

	@Test(expected=EmptyStackException.class)
	public void testPopFromEmptyStack() {
		multistack.pop("Key");
	}

	@Test(expected=EmptyStackException.class)
	public void testPeekFromEmptyStack() {
		multistack.peek("Key");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIllegalWrapperValue() {
		new ValueWrapper(new Object());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIllegalIncrement() {
		ValueWrapper wrapper = new ValueWrapper(2);
		wrapper.increment("abc");
	}

	@Test
	public void testNull() {
		ValueWrapper wrapper = new ValueWrapper(null);
		assertTrue(wrapper.getValue() instanceof Integer);
		assertEquals(0, wrapper.getValue());
	}

	@Test
	public void testParseDouble() {
		List<ValueWrapper> doubles = new ArrayList<>();
		doubles.add(new ValueWrapper("3.25"));
		doubles.add(new ValueWrapper("-3.1"));
		doubles.add(new ValueWrapper(".2"));
		doubles.add(new ValueWrapper("2E+2"));
		doubles.add(new ValueWrapper("-1e-3"));

		for (ValueWrapper doubleValue : doubles) {
			doubleValue.increment(0);
			assertTrue(doubleValue.getValue() instanceof Double);
		}
	}

	@Test
	public void testStacks() {
		multistack.push("key1", new ValueWrapper(2));
		multistack.push("key1", new ValueWrapper(3));
		multistack.push("key2", new ValueWrapper(2));

		assertEquals(Integer.valueOf(3), multistack.peek("key1").getValue());
		assertEquals(Integer.valueOf(2), multistack.peek("key2").getValue());

		multistack.pop("key1");
		assertEquals(Integer.valueOf(2), multistack.peek("key1").getValue());
	}

	@Test
	public void testIncrement() {
		ValueWrapper wrapper = new ValueWrapper(2);
		assertTrue(wrapper.getValue() instanceof Integer);
		assertEquals(2, wrapper.getValue());

		wrapper.increment(2);
		assertTrue(wrapper.getValue() instanceof Integer);
		assertEquals(4, wrapper.getValue());

		wrapper.increment(2.0);
		assertTrue(wrapper.getValue() instanceof Double);
		assertEquals(6.0, wrapper.getValue());

		wrapper.increment(2);
		assertTrue(wrapper.getValue() instanceof Double);
		assertEquals(8.0, wrapper.getValue());
	}

	@Test
	public void testDecrement() {
		ValueWrapper wrapper = new ValueWrapper(8);
		assertTrue(wrapper.getValue() instanceof Integer);
		assertEquals(8, wrapper.getValue());

		wrapper.decrement(2);
		assertTrue(wrapper.getValue() instanceof Integer);
		assertEquals(6, wrapper.getValue());

		wrapper.decrement(2.0);
		assertTrue(wrapper.getValue() instanceof Double);
		assertEquals(4.0, wrapper.getValue());

		wrapper.decrement(2);
		assertTrue(wrapper.getValue() instanceof Double);
		assertEquals(2.0, wrapper.getValue());
	}

	@Test
	public void testMultiply() {
		ValueWrapper wrapper = new ValueWrapper(2);
		assertTrue(wrapper.getValue() instanceof Integer);
		assertEquals(2, wrapper.getValue());

		wrapper.multiply(2);
		assertTrue(wrapper.getValue() instanceof Integer);
		assertEquals(4, wrapper.getValue());

		wrapper.multiply(2.0);
		assertTrue(wrapper.getValue() instanceof Double);
		assertEquals(8.0, wrapper.getValue());

		wrapper.multiply(2);
		assertTrue(wrapper.getValue() instanceof Double);
		assertEquals(16.0, wrapper.getValue());
	}

	@Test
	public void testDivide() {
		ValueWrapper wrapper = new ValueWrapper(16);
		assertTrue(wrapper.getValue() instanceof Integer);
		assertEquals(16, wrapper.getValue());

		wrapper.divide(2);;
		assertTrue(wrapper.getValue() instanceof Integer);
		assertEquals(8, wrapper.getValue());

		wrapper.divide(2.0);
		assertTrue(wrapper.getValue() instanceof Double);
		assertEquals(4.0, wrapper.getValue());

		wrapper.divide(2);
		assertTrue(wrapper.getValue() instanceof Double);
		assertEquals(2.0, wrapper.getValue());
	}

	@Test
	public void testNumCompare() {
		ValueWrapper wrapper = new ValueWrapper(5);
		assertTrue(wrapper.numCompare(-1) > 0);
		assertTrue(wrapper.numCompare(5) == 0);
		assertTrue(wrapper.numCompare(8) < 0);

		wrapper = new ValueWrapper(5.0);
		assertTrue(wrapper.numCompare(-1.5) > 0);
		assertTrue(wrapper.numCompare(5) == 0);
		assertTrue(wrapper.numCompare(5.01) < 0);
	}

}
