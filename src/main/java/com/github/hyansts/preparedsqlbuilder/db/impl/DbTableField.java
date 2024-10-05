package com.github.hyansts.preparedsqlbuilder.db.impl;

import com.github.hyansts.preparedsqlbuilder.db.DbComparableField;
import com.github.hyansts.preparedsqlbuilder.db.DbField;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldLike;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldOrder;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldValue;
import com.github.hyansts.preparedsqlbuilder.db.DbTableLike;
import com.github.hyansts.preparedsqlbuilder.db.DbWritableField;
import com.github.hyansts.preparedsqlbuilder.sql.SqlAggregator;
import com.github.hyansts.preparedsqlbuilder.sql.SqlSortOrder;
import com.github.hyansts.preparedsqlbuilder.util.StringHolder;

import static com.github.hyansts.preparedsqlbuilder.sql.SqlKeyword.AS;

/**
 * Represents a column in a table.
 * <p>
 * This class is designed to provide a way to define table fields and used them in SQL queries.
 * <p>
 * Fields are identified by their name, the table where the field is defined, and an optional alias.
 * <p>
 * Fields also have a generic type, this is the Java representation of the column type, this is not the same as your SQL
 * database type, but rather the type the data will be converted to when the column is fetched.
 */
public class DbTableField<T> implements DbField, DbWritableField<T>, DbComparableField<T> {

	private final StringHolder fieldName;
	private final StringHolder alias = new StringHolder();
	private final DbTableLike table;

	public DbTableField(String name, DbTableLike table) {
		this.fieldName = new StringHolder(name);
		this.table = table;
	}

	DbTableField(StringHolder name, DbTableLike table) {
		this.fieldName = name;
		this.table = table;
	}

	/**
	 * @return a new aggregate field with the MAX aggregation function applied to this field.
	 */
	public DbAggregateField<T> max() {
		return new DbAggregateField<>(SqlAggregator.MAX, this);
	}

	/**
	 * @return a new aggregate field with the MIN aggregation function applied to this field.
	 */
	public DbAggregateField<T> min() {
		return new DbAggregateField<>(SqlAggregator.MIN, this);
	}

	/**
	 * @return a new aggregate field with the AVG aggregation function applied to this field.
	 */
	public DbAggregateField<Double> avg() {
		return new DbAggregateField<>(SqlAggregator.AVG, this);
	}

	/**
	 * @return a new aggregate field with the COUNT aggregation function applied to this field.
	 */
	public DbAggregateField<Long> count() {
		return new DbAggregateField<>(SqlAggregator.COUNT, this);
	}

	/**
	 * @return a new aggregate field with the SUM aggregation function applied to this field.
	 */
	public DbAggregateField<Double> sum() {
		return new DbAggregateField<>(SqlAggregator.SUM, this);
	}

	/**
	 * @return a new DbFieldOrder with the ascending order of this field.
	 */
	@Override
	public DbFieldOrder asc() {
		return new DbTableFieldOrder(getLabel(), SqlSortOrder.ASC);
	}

	/**
	 * @return a new DbFieldOrder with the descending order of this field.
	 */
	@Override
	public DbFieldOrder desc() {
		return new DbTableFieldOrder(getLabel(), SqlSortOrder.DESC);
	}

	/**
	 * @return this field with the alias set.
	 */
	@Override
	public DbFieldLike as(String alias) {
		this.alias.setValue(alias);
		return this;
	}

	/**
	 * Assigns the given value to the field to be used during an INSERT or UPDATE clause in SQL.
	 *
	 * @param value the value to be assigned to the column in the database table.
	 * @return a new DbFieldValue with the value given.
	 */
	@Override
	public DbFieldValue<T> value(T value) { return new DbTableFieldValue<>(this.fieldName.getValue(), value); }

	/**
	 * @return the full qualification of this field, which is the result of combining the table with the field name.
	 */
	@Override
	public String getFullQualification() {
		String fieldName = this.fieldName.getValueOrDefault();
		return this.table == null || this.table.getAlias() == null || this.table.getAlias().isBlank()
					   ? fieldName : this.table.getAlias() + "." + fieldName;
	}

	/**
	 * @return the label of this field, which is the alias if it exists, or the full qualification if no alias is set.
	 */
	@Override
	public String getLabel() {
		return this.alias.isBlank() ? this.getFullQualification() : this.alias.getValue();
	}

	/**
	 * @return the definition of this field, which is the full qualification associated with the alias. If no alias is
	 * set, only the full qualification is returned.
	 */
	@Override
	public String getDefinition() {
		return this.alias.isBlank() ? getFullQualification() : getFullQualification() + AS + this.alias;
	}

	/**
	 * @return the name of this field.
	 */
	@Override
	public String getFieldName() { return this.fieldName.getValueOrDefault(); }

	/**
	 * @return the table where this field is defined.
	 */
	@Override
	public DbTableLike getTableLike() { return this.table; }

	/**
	 * Generates a new DbTableField with the same label as this field but defined to the given table.
	 * <p>
	 * If the original field has an alias, the new field will be named after the alias instead of the original field name.
	 * <p>
	 * This method is useful when referencing a field from a derived table subquery in the outer query. As a subquery
	 * result is a table-like structure, referencing a field from its result-set in the outer query is only possible if
	 * the field is prefixed with the subquery alias instead of its original table name. That can be achieved by calling
	 * this method with passing the subquery as the parameter.
	 * <p>
	 * In summary, this method changes the table name prefixing the field.
	 *
	 * @param tableLike the table to be mapped to.
	 * @return a DbTableField named after this field, defined to the given table.
	 */
	@Override
	public DbComparableField<T> mapTo(DbTableLike tableLike) {
		this.alias.setDefaultHolder(this.fieldName);
		return new DbTableField<>(this.alias, tableLike);
	}

	/**
	 * @return a string representation of this field. It is the same as the label.
	 */
	@Override
	public String toString() { return this.getLabel(); }

}