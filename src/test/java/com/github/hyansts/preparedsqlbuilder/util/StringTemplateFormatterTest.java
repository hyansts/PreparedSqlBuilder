package com.github.hyansts.preparedsqlbuilder.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringTemplateFormatterTest {

	@Test
	void testPartialFormating() {

		final String TEMPLATE = "Fruits: ${0}, ${1}, ${2}, ${3}, ${4}";

		StringTemplateFormatter formatter = new StringTemplateFormatter();
		formatter.put(0, "Apple");
		formatter.put(2, "Orange");

		String result = formatter.format(TEMPLATE);

		String expected = "Fruits: Apple, ${1}, Orange, ${3}, ${4}";
		assertEquals(expected, result);

		formatter.clear();

		formatter.put(1, "Banana");
		formatter.put(3, "Grape");

		String result2 = formatter.format(result);

		String expected2 = "Fruits: Apple, Banana, Orange, Grape, ${4}";
		assertEquals(expected2, result2);
	}

	@Test
	void testFormatingValueWithPattern() {

		final String TEMPLATE = "Text: [${0}] Number: [${1}] Text again: [${0}]";

		StringTemplateFormatter formatter = new StringTemplateFormatter();
		formatter.put(0, "'${1}' is a placeholder.");
		formatter.put(1, "42");

		String result = formatter.format(TEMPLATE);

		assertEquals("Text: ['${1}' is a placeholder.] Number: [42] Text again: ['${1}' is a placeholder.]", result);
	}

	@Test
	void testFormatingWithDifferentPattern() {

		final String TEMPLATE = "Text: &[0] Default Pattern: ${1}";

		StringTemplateFormatter formatter = new StringTemplateFormatter("&[", "]");
		formatter.put(0, "This replaced a placeholder.");
		formatter.put(1, "This should not replace a placeholder.");

		String result = formatter.format(TEMPLATE);

		assertEquals("Text: This replaced a placeholder. Default Pattern: ${1}", result);
	}
}