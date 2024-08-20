package com.github.hyansts.preparedsqlbuilder.util;

import java.util.HashMap;
import java.util.Map;

/**
 * StringTemplateFormatter is a utility class that allows custom templates with named placeholders that can be formatted
 * at any order.
 * <p>
 * Placeholders are identified by a string key surrounded by the prefix and suffix provided in the
 * constructor, if not, they are assumed to be <code>"${"</code> and <code>"}"</code> by default. A placeholder can be provided using
 * {@link #put(String, Object)} or {@link #putAll(Map)}, and can be put in place using {@link #format(String)}.
 */
public class StringTemplateFormatter {

	private final Map<String, Object> placeHolders = new HashMap<>();
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
	 * Inserts a new key-value pair for the placeholder. The key must be the same string used in between the prefix and
	 * suffix pattern in the template.
	 *
	 * @param key   the key used to identify the placeholder.
	 * @param value the value used to replace the placeholder.
	 */
	public void put(String key, Object value) {
		this.placeHolders.put(key, value);
	}

	/**
	 * Adds all the key-value pairs from the provided map to the placeholders map. The key must be the same string used
	 * in between the prefix and suffix pattern in the template.
	 *
	 * @param values the map containing key-value pairs for the placeholders.
	 */
	public void putAll(Map<String, Object> values) {
		this.placeHolders.putAll(values);
	}

	/**
	 * Clears the placeholders map, removing all key-value pairs.
	 */
	public void clear() {
		this.placeHolders.clear();
	}

	/**
	 * Formats a given template by replacing placeholders with actual values. Placeholders are identified by a
	 * string key surrounded by the prefix and suffix provided in the constructor, if not provided they are assumed to
	 * be <code>"${"</code> and <code>"}"</code> by default.
	 * <p>
	 * A placeholders can be provided through {@link #put(String, Object)} or {@link #putAll(Map)}, they can be repeated
	 * multiple times in the template and placed in any order. Placeholders that haven't been mapped to any value are
	 * ignored.
	 * <p>
	 * Previously mapped placeholder values will be preserved in between method calls, this method will try to apply
	 * them to every template unless {@link #clear()} is called.
	 * <p>
	 * Example:
	 * <pre>
	 * {@code
	 * StringTemplateFormatter formatter = new StringTemplateFormatter();
	 * formatter.put("fruit", "Apple");
	 * formatter.put("name", "John");
	 * formatter.format("Example: ${fruit}, ${number}, ${name}");//"Example: Apple, ${number}, John"
	 * }
	 * </pre>
	 *
	 * @param template the template string to be formatted.
	 * @return the formatted string with the mapped placeholders replaced.
	 */
	public String format(String template) {
		StringBuilder sb = new StringBuilder();

		int lastIndex = 0;
		int prefixIndex = template.indexOf(this.prefix);
		int suffixIndex = template.indexOf(this.suffix, prefixIndex + this.prefix.length());
		while (prefixIndex != -1 && suffixIndex != -1) {
			String key = template.substring(prefixIndex + this.prefix.length(), suffixIndex);
			Object val = this.placeHolders.get(key);
			if (val != null) {
				sb.append(template, lastIndex, prefixIndex).append(val);
				lastIndex = suffixIndex + this.suffix.length();
			}
			prefixIndex = template.indexOf(this.prefix, suffixIndex + this.suffix.length());
			suffixIndex = template.indexOf(this.suffix, prefixIndex + this.prefix.length());
		}

		if (lastIndex < template.length()) {
			// append the rest of the template after the last placeholder
			sb.append(template, lastIndex, template.length());
		}

		return sb.toString();
	}

	/**
	 * Finds and returns the first occurrence of a placeholder key inside the prefix and suffix in the template.
	 *
	 * @param template the template string to be searched
	 * @return the first string key found in between the suffix and prefix. Returns {@code null} if none.
	 */
	public String findFirstKey(String template) {
		int prefixIndex = template.indexOf(this.prefix);
		int suffixIndex = template.indexOf(this.suffix, prefixIndex + this.prefix.length());
		if (prefixIndex != -1 && suffixIndex != -1) {
			return template.substring(prefixIndex + this.prefix.length(), suffixIndex);
		}
		return null;
	}

}