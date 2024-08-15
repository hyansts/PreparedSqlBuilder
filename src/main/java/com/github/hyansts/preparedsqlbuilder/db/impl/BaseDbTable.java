package com.github.hyansts.preparedsqlbuilder.db.impl;

import com.github.hyansts.preparedsqlbuilder.db.DbTable;

import static com.github.hyansts.preparedsqlbuilder.sql.SqlKeyword.AS;

public abstract class BaseDbTable<T extends BaseDbTable<T>> implements DbTable {

	private final String tableName;
	private final String tablePrefix;
	private String tableAlias;

	protected BaseDbTable(String name) {
		this.tableName = name;
		this.tablePrefix = null;
	}

	protected BaseDbTable(String name, String prefix) {
		this.tableName = name;
		this.tablePrefix = prefix;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T as(String alias) {
		this.tableAlias = alias;
		return (T) this;
	}

	@Override
	public String getTableName() { return this.tableName; }

	@Override
	public String getAlias() { return this.tableAlias; }

	@Override
	public String getTablePrefix() { return this.tablePrefix; }

	@Override
	public String getDefinition() {
		return this.tableAlias == null || this.tableAlias.isBlank()
					   ? getFullTableName() : getFullTableName() + AS + this.tableAlias;
	}

	@Override
	public String getFullTableName() {
		return this.tablePrefix == null || this.tablePrefix.isBlank()
					   ? this.tableName : this.tablePrefix + "." + this.tableName;
	}

}
