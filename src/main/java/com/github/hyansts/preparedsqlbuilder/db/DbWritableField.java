package com.github.hyansts.preparedsqlbuilder.db;

public interface DbWritableField<T> {
	DbFieldValue<T> value(T value);
}
