package com.github.hyansts.preparedsqlbuilder.db.impl;

import com.github.hyansts.preparedsqlbuilder.db.DbTable;

import static com.github.hyansts.preparedsqlbuilder.sql.SqlKeyword.AS;

public abstract class BaseDbTable<T extends BaseDbTable<T>> implements DbTable {

	private final String TABLE_NAME;
	private final String TABLE_PREFIX;
	private String tableAlias;

	protected BaseDbTable(String name) {
		this.TABLE_NAME = name;
		this.TABLE_PREFIX = null;
	}

	protected BaseDbTable(String name, String prefix) {
		this.TABLE_NAME = name;
		this.TABLE_PREFIX = prefix;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T as(String alias) {
		this.tableAlias = alias;
		return (T) this;
	}

	@Override
	public String getTableName() { return this.TABLE_NAME; }

	@Override
	public String getAlias() { return this.tableAlias; }

	@Override
	public String getTablePrefix() { return this.TABLE_PREFIX; }

	@Override
	public String getDefinition() {
		return this.tableAlias == null || this.tableAlias.isBlank()
					   ? getFullTableName() : getFullTableName() + AS + this.tableAlias;
	}

	@Override
	public String getFullTableName() {
		return this.TABLE_PREFIX == null || this.TABLE_PREFIX.isBlank()
					   ? this.TABLE_NAME : this.TABLE_PREFIX + "." + this.TABLE_NAME;
	}

}
