package com.github.hyansts.preparedsqlbuilder;

public class DbTableFieldValue<T> implements DbFieldValue<T> {

	private final String FIELD_NAME;
	private final T VALUE;

	public DbTableFieldValue(String fieldName, T value) {
		this.VALUE = value;
		this.FIELD_NAME = fieldName;
	}

	@Override
	public String getFieldName() {
		return this.FIELD_NAME;
	}

	@Override
	public T getValue() {
		return this.VALUE;
	}
}
