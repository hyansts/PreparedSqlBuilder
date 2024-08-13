package com.github.hyansts.preparedsqlbuilder.db;

public interface DbTable extends DbTableLike {
	String getTableName();

	String getTablePrefix();

	String getFullTableName();
}
