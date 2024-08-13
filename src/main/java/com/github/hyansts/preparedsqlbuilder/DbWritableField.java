package com.github.hyansts.preparedsqlbuilder;

public interface DbWritableField<T> {
	DbFieldValue<T> value(T value);
}
