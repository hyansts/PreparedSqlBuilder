package com.github.hyansts.preparedsqlbuilder.util;

import java.util.Map;
import java.util.TreeMap;

public class StringTemplateFormatter {

	private final Map<Integer, String> placeHolders = new TreeMap<>();
	private final String prefix;
	private final String suffix;

	public StringTemplateFormatter() {
		this("${", "}");
	}

	public StringTemplateFormatter(String prefix, String suffix) {
		this.prefix = prefix;
		this.suffix = suffix;
	}

	/**
	 * Inserts a new key-value pair for the placeholder. The key must be the same integer used in between the prefix and
	 * suffix pattern in the template.
	 *
	 * @param key   the key used to identify the placeholder.
	 * @param value the value used to replace the placeholder.
	 */
	public void put(int key, String value) {
		placeHolders.put(key, value);
	}

	/**
	 * Adds all the key-value pairs from the provided map to the placeholders map. The key must be the same integer used
	 * in between the prefix and suffix pattern in the template
	 *
	 * @param values the map containing key-value pairs for the placeholders.
	 */
	public void putAll(Map<Integer, String> values) {
		this.placeHolders.putAll(values);
	}

	/**
	 * Clears the placeholders map, removing all key-value pairs.
	 */
	public void clear() {
		this.placeHolders.clear();
	}
	/**
	 * Formats a given template string by replacing placeholders with actual values. The placeholders are identified by
	 * the prefix and suffix provided in the constructor, if not provided they are assumed to be ${ and } by default.
	 * <p>
	 * A placeholder is identified by the integer key provided through {@link #put(int, String)} or {@link #putAll(Map)}
	 * methods, they can also be repeated multiple times in the template and placed in any order. Placeholders that
	 * haven't been mapped to any value are ignored.
	 * <p>
	 * Previously mapped placeholder values will be preserved in between method calls, this method will try to apply
	 * them to every template unless {@link #clear()} is called.
	 *
	 * @param template the template string to be formatted
	 * @return the formatted string with the mapped placeholders replaced
	 */
	public String format(String template) {
		StringBuilder stringBuilder = new StringBuilder(template);

		TreeMap<Integer, Integer> indexes = new TreeMap<>();

		for (Integer key : this.placeHolders.keySet()) {
			String param = getPlaceholder(key);
			int index = stringBuilder.indexOf(param);
			while (index != -1) {
				indexes.put(index, key);
				index = stringBuilder.indexOf(param, index + param.length());
			}
		}

		for (var entry : indexes.descendingMap().entrySet()) {
			int index = entry.getKey();
			int key = entry.getValue();
			stringBuilder.replace(index, index + getPlaceholder(key).length(), this.placeHolders.get(key));
		}

		return stringBuilder.toString();
	}

	private String getPlaceholder(int key) {
		return this.prefix + key + this.suffix;
	}

}
