package com.github.hyansts.preparedsqlbuilder;

public interface DbTable extends DbTableLike {
	String getTableName();

	String getTablePrefix();

	String getFullTableName();
}
