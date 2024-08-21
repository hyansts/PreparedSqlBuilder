package com.github.hyansts.preparedsqlbuilder.util;

public class StringHolder {

	private String value;
	private StringHolder defaultHolder;

	public StringHolder() { }

	public StringHolder(String string) { this.value = string; }

	public boolean isBlank() { return this.value == null || this.value.isEmpty(); }
	public String getValue() { return isBlank() && this.defaultHolder != null ? this.defaultHolder.value : this.value; }
	public void setValue(String value) { this.value = value; }
	public StringHolder getDefaultHolder() { return defaultHolder; }
	public void setDefaultHolder(StringHolder defaultValue) { this.defaultHolder = defaultValue; }

	@Override
	public String toString() { return this.value; }

}