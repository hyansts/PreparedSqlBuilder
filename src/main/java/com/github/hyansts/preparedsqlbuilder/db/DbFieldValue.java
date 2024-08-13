package com.github.hyansts.preparedsqlbuilder.db;

public interface DbFieldValue<T> {
	String getFieldName();

	T getValue();
}
