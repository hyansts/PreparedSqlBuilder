package com.github.hyansts.preparedsqlbuilder;

import static com.github.hyansts.preparedsqlbuilder.sql.SqlKeyword.AS;

public interface DbTable {
	String getTableName();

	String getTableAlias();

	String getTablePrefix();

	default String getTableNameDefinition() {
		return getTableAlias() == null ? getFullTableName() : getFullTableName() + AS + getTableAlias();
	}

	default String getFullTableName() {
		return getTablePrefix() == null ? getTableName() : getTablePrefix() + "." + getTableName();
	}
}
