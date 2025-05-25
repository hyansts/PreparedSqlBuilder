package com.github.hyansts.preparedsqlbuilder.db.impl;

import com.github.hyansts.preparedsqlbuilder.db.DbComparableField;
import com.github.hyansts.preparedsqlbuilder.db.DbField;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldLike;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldOrder;
import com.github.hyansts.preparedsqlbuilder.db.DbTableLike;
import com.github.hyansts.preparedsqlbuilder.sql.SqlAggregator;
import com.github.hyansts.preparedsqlbuilder.sql.SqlSortOrder;
import com.github.hyansts.preparedsqlbuilder.util.StringHolder;

import static com.github.hyansts.preparedsqlbuilder.sql.SqlKeyword.AS;

/**
 * Represents an aggregate field in a query.
 * <p>
 * An aggregate field is the result of applying an aggregation function to a field in a query.
 * <p>
 * To keep consistency when referencing aggregate fields within a query, Aggregate fields have an alias by default. The
 * default alias is defined as "aggregator_field", e.g.: "avg_age" when applying the "AVG" aggregation
 * to a field named "age". A new alias can be set using the {@link #as(String)} method.
 */
public class DbAggregateField<T> implements DbComparableField<T> {

	private final SqlAggregator aggregator;
	private final DbField<?> field;
	private final StringHolder alias = new StringHolder();
	private final Class<T> type;

	public DbAggregateField(SqlAggregator aggregator, DbField<?> field, Class<T> type) {
		this.aggregator = aggregator;
		this.field = field;
		this.type = type;
		this.alias.setValue((aggregator + "_" + field.getFieldName()).toLowerCase());
	}

	/**
	 * @return a DbFieldOrder with the ascending order of this field.
	 */
	@Override
	public DbFieldOrder asc() {
		return new DbTableFieldOrder(getLabel(), SqlSortOrder.ASC);
	}

	/**
	 * @return a DbFieldOrder with the descending order of this field.
	 */
	@Override
	public DbFieldOrder desc() {
		return new DbTableFieldOrder(getLabel(), SqlSortOrder.DESC);
	}

	/**
	 * @return the full qualification of this field, which is the result of applying the aggregation function to the full
	 * qualification of the original field.
	 */
	@Override
	public String getFullQualification() {
		return this.aggregator.applyTo(this.field.getFullQualification());
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
	 * @return the label of this field, which is the alias if it exists, or the full qualification if no alias is set.
	 */
	@Override
	public String getLabel() {
		if (this.alias.isBlank()) {
			throw new IllegalStateException("Aggregate function must have an alias to be referenced: " + getFullQualification());
		}
		return this.alias.getValue();
	}

	/**
	 * @return the table where the original field is defined.
	 */
	@Override
	public DbTableLike getTableLike() {
		return this.field.getTableLike();
	}

	/**
	 * @param alias the alias to be set to this field.
	 * @return this field with the alias set.
	 */
	@Override
	public DbFieldLike as(String alias) {
		this.alias.setValue(alias);
		return this;
	}

	/**
	 * Generates a new DbTableField with the same label as this field but defined to the given table.
	 * <p>
	 * If the original field has an alias, the new field will be named after the alias instead of the original field name.
	 * <p>
	 * This method is useful when referencing a field from a derived table subquery in the outer query. As a subquery
	 * result is a table-like structure, referencing a field from its result-set in the outer query is only possible if
	 * the field is prefixed with the subquery alias instead of its original table name. That can be achieved by calling
	 * this method passing the subquery as the parameter.
	 * <p>
	 * In summary, this method changes the table name prefixing the field.
	 *
	 * @param tableLike the table to be mapped to.
	 * @return a DbTableField named after this field, defined to the given table.
	 */
	@Override
	public DbComparableField<T> mapTo(DbTableLike tableLike) {
		return new DbTableField<>(this.alias, tableLike, this.type);
	}

}