package com.github.hyansts.preparedsqlbuilder;

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

	@SuppressWarnings("unchecked")
	public T as(String alias) {
		this.tableAlias = alias;
		return (T) this;
	}

	@Override
	public String getTableName() { return this.TABLE_NAME; }

	@Override
	public String getTableAlias() { return this.tableAlias; }

	@Override
	public String getTablePrefix() { return this.TABLE_PREFIX; }

}
