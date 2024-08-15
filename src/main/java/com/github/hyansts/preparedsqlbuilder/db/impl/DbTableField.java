package com.github.hyansts.preparedsqlbuilder.db.impl;

import java.util.function.Function;

import com.github.hyansts.preparedsqlbuilder.db.DbComparableField;
import com.github.hyansts.preparedsqlbuilder.db.DbField;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldLike;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldValue;
import com.github.hyansts.preparedsqlbuilder.db.DbTableLike;
import com.github.hyansts.preparedsqlbuilder.db.DbWritableField;
import com.github.hyansts.preparedsqlbuilder.sql.SqlAggregator;
import com.github.hyansts.preparedsqlbuilder.sql.SqlSortOrder;

import static com.github.hyansts.preparedsqlbuilder.sql.SqlKeyword.AS;

public class DbTableField<T> implements DbField, DbWritableField<T>, DbComparableField<T> {

	private final String FIELD_NAME;
	private final DbTableLike TABLE;

	private String alias;
	private SqlSortOrder sortOrder;
	private Function<String, String> aggregateFunction;

	public DbTableField(String name, DbTableLike table) {
		this.FIELD_NAME = name;
		this.TABLE = table;
	}

	private DbTableField(String name, DbTableLike table, Function<String, String> aggregateFunction) {
		this.FIELD_NAME = name;
		this.TABLE = table;
		this.aggregateFunction = aggregateFunction;
	}

	public DbTableField<T> max() {
		return new DbTableField<>(this.FIELD_NAME, this.TABLE, SqlAggregator::max);
	}

	public DbTableField<T> min() {
		return new DbTableField<>(this.FIELD_NAME, this.TABLE, SqlAggregator::min);
	}

	public DbTableField<Double> avg() {
		return new DbTableField<>(this.FIELD_NAME, this.TABLE, SqlAggregator::avg);
	}

	public DbTableField<Long> count() {
		return new DbTableField<>(this.FIELD_NAME, this.TABLE, SqlAggregator::count);
	}

	public DbTableField<Double> sum() {
		return new DbTableField<>(this.FIELD_NAME, this.TABLE, SqlAggregator::sum);
	}

	@Override
	public DbFieldLike asc() {
		this.sortOrder = SqlSortOrder.ASC;
		return this;
	}

	@Override
	public DbFieldLike desc() {
		this.sortOrder = SqlSortOrder.DESC;
		return this;
	}

	@Override
	public DbFieldLike as(String alias) {
		this.alias = alias;
		return this;
	}

	@Override
	public DbFieldValue<T> value(T value) { return new DbTableFieldValue<>(this.FIELD_NAME, value); }

	@Override
	public String getFullQualification() {
		return this.TABLE == null || this.TABLE.getAlias() == null || this.TABLE.getAlias().isBlank()
					   ? this.FIELD_NAME : this.TABLE.getAlias() + "." + this.FIELD_NAME;
	}

	@Override
	public String getLabel() {
		return this.alias == null || this.alias.isBlank() ? this.getFullQualification() : this.alias;
	}

	@Override
	public String getDefinition() {
		String definition;
		if (this.aggregateFunction != null) {
			definition = this.aggregateFunction.apply(this.getFullQualification());
		} else {
			definition = this.getFullQualification();
		}
		return this.alias == null || this.alias.isBlank() ? definition : definition + AS + this.alias;
	}

	@Override
	public String getFieldName() { return this.FIELD_NAME; }

	@Override
	public DbTableLike getTableLike() { return this.TABLE; }

	@Override
	public DbComparableField<T> mapTo(DbTableLike tableLike) { return new DbTableField<>(this.getLabel(), tableLike); }

	@Override
	public SqlSortOrder getSortOrder() { return this.sortOrder; }

	@Override
	public String toString() { return this.getLabel(); }
}