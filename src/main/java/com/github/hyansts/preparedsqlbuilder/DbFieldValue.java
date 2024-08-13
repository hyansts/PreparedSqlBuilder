package com.github.hyansts.preparedsqlbuilder;

public interface DbFieldValue<T> {
	String getFieldName();

	T getValue();
}
