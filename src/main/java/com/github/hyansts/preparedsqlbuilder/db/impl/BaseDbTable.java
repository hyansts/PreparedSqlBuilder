package com.github.hyansts.preparedsqlbuilder.db.impl;

import com.github.hyansts.preparedsqlbuilder.db.DbTable;

import static com.github.hyansts.preparedsqlbuilder.sql.SqlKeyword.AS;

/**
 * The base class for a database table model implementation.
 * <p>
 * This class is designed to be extended by user-defined classes to provide
 * a way to use the database table models in SQL queries.
 * <p>
 * The class itself does not provide any methods to query the database.
 * Instead, it provides a convenient way represent a table as a class.
 * <p>
 * The table name is specified in the constructor. In addition to that, a table prefix can also be specified. A table
 * prefix is most commonly used to specify the database name or the schema name. When the prefix is specified, the table
 * name is formatted as "table_prefix.table_name". The table name and prefix are used to reference the table in the SQL
 * statement.
 * <p>
 * The table alias is a parameter that can be specified using the {@link #as(String)} method. The table alias is used to
 * reference the table in the SQL statement.
 */
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
