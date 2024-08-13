package com.github.hyansts.preparedsqlbuilder.db;

public interface DbTableLike {
	String getDefinition();

	String getAlias();

	DbTableLike as(String alias);
}
