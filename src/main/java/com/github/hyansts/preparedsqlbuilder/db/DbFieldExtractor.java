package com.github.hyansts.preparedsqlbuilder.db;

public interface DbFieldExtractor {
	<T> T extract(DbField<T> field);
}