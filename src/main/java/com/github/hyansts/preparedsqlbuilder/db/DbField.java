package com.github.hyansts.preparedsqlbuilder.db;

public interface DbField<T> extends DbFieldLike {
	String getFieldName();

	Class<T> getType();
}
