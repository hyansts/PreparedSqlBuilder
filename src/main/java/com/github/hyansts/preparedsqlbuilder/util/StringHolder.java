package com.github.hyansts.preparedsqlbuilder.util;

/**
 * A class that holds a String value and a default StringHolder.
 * <p>
 * This allows for multiple objects to have a reference to the same String constant, and propagate the side effects of
 * changing that reference to a new String.
 * <p>
 * It's also possible to specify a default StringHolder, which will be used if the String value is blank.
 * It's not intended to nest default StringHolder more than one layer deep. The {@link #getDefaultHolder()} method uses
 * its default StringHolder value directly, meaning that nested default StringHolders will not be evaluated.
 */
public class StringHolder {

	private String value;
	private StringHolder defaultHolder;

	public StringHolder() { }

	public StringHolder(String string) { this.value = string; }

	public boolean isBlank() { return this.value == null || this.value.isEmpty(); }
	public String getValue() { return this.value; }
	public void setValue(String value) { this.value = value; }
	public StringHolder getDefaultHolder() { return defaultHolder; }
	public void setDefaultHolder(StringHolder defaultValue) { this.defaultHolder = defaultValue; }
	public String getValueOrDefault() {
		return isBlank() && this.defaultHolder != null ? this.defaultHolder.value : this.value;
	}

	@Override
	public String toString() { return this.value; }

}