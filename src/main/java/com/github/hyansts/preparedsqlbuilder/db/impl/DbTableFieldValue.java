package com.github.hyansts.preparedsqlbuilder.db.impl;

import com.github.hyansts.preparedsqlbuilder.db.DbFieldValue;

public class DbTableFieldValue<T> implements DbFieldValue<T> {

	private final String fieldName;
	private final T value;

	public DbTableFieldValue(String fieldName, T value) {
		this.value = value;
		this.fieldName = fieldName;
	}

	@Override
	public String getFieldName() {
		return this.fieldName;
	}

	@Override
	public T getValue() {
		return this.value;
	}
}
