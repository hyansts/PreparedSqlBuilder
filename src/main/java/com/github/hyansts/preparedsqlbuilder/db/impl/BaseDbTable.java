package com.github.hyansts.preparedsqlbuilder.db.impl;

import com.github.hyansts.preparedsqlbuilder.db.DbTable;
import com.github.hyansts.preparedsqlbuilder.util.StringUtil;

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
 * name is formatted as "table_prefix.table_name" to reference the table in the SQL statement.
 * <p>
 * A table alias can be specified using the {@link #as(String)} method. The table alias is used to reference the table
 * in the SQL statement.
 */
public abstract class BaseDbTable implements DbTable {

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
	public DbTable as(String alias) {
		this.tableAlias = alias;
		return this;
	}

	/**
	 * @return the Table name as defined in the constructor. Does not include the table prefix.
	 */
	@Override
	public String getTableName() { return this.tableName; }

	/**
	 * @return table alias as defined using the {@link #as(String)} method.
	 */
	@Override
	public String getAlias() { return this.tableAlias; }

	/**
	 * @return the table prefix as defined in the constructor. Usually, this is the database name or the schema name.
	 */
	@Override
	public String getTablePrefix() { return this.tablePrefix; }

	/**
	 * @return the table definition as: {@code "table_prefix.table_name AS table_alias"}. If no alias is defined, simply
	 * returns the full table name.
	 */
	@Override
	public String getDefinition() {
		return StringUtil.isBlank(this.tableAlias) ? getFullTableName() : getFullTableName() + AS + this.tableAlias;
	}

	/**
	 * @return the full table name, which is the result of combining the table prefix with the table name:
	 * {@code "table_prefix.table_name"}. If no table prefix is defined, simply returns the table name.
	 */
	@Override
	public String getFullTableName() {
		return StringUtil.isBlank(this.tablePrefix) ? this.tableName : this.tablePrefix + "." + this.tableName;
	}

}
