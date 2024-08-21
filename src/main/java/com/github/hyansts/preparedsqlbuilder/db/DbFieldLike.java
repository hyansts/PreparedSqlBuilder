package com.github.hyansts.preparedsqlbuilder.db;

public interface DbFieldLike {
	String getFullQualification();

	String getDefinition();

	String getLabel();

	DbTableLike getTableLike();

	DbFieldLike as(String alias);
}