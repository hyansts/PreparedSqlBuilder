package com.github.hyansts.preparedsqlbuilder;

public interface DbTable {
	String getTableName();

	String getTableAlias();

	String getTablePrefix();

	default String getTableLabel() {
		return getTableAlias() == null ? getFullTableName() : getTableAlias();
	}

	default String getTableNameDefinition() {
		return getTableAlias() == null ? getFullTableName() : getFullTableName() + " as " + getTableAlias();
	}

	default String getFullTableName() {
		return getTablePrefix() == null ? getTableName() : getTablePrefix() + "." + getTableName();
	}
}
