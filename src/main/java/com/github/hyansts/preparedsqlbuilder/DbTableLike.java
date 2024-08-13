package com.github.hyansts.preparedsqlbuilder;

public interface DbTableLike {
	String getDefinition();

	String getAlias();

	DbTableLike as(String alias);
}
