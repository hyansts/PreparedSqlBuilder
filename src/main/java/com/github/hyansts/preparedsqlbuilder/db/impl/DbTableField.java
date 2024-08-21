package com.github.hyansts.preparedsqlbuilder.db.impl;

import java.util.function.Function;

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

public class DbTableField<T> implements DbField, DbWritableField<T>, DbComparableField<T> {

	private final StringHolder fieldName;
	private final StringHolder alias = new StringHolder();
	private final DbTableLike table;
	private final Function<String, String> aggregateFunction;

	public DbTableField(String name, DbTableLike table) {
		this.fieldName = new StringHolder(name);
		this.table = table;
		this.aggregateFunction = null;
	}

	private DbTableField(StringHolder name, DbTableLike table) {
		this.fieldName = name;
		this.table = table;
		this.aggregateFunction = null;
	}

	private DbTableField(StringHolder name, DbTableLike table, Function<String, String> aggregateFunction) {
		this.fieldName = name;
		this.table = table;
		this.aggregateFunction = aggregateFunction;
	}

	public DbTableField<T> max() {
		return new DbTableField<>(this.fieldName, this.table, SqlAggregator::max);
	}

	public DbTableField<T> min() {
		return new DbTableField<>(this.fieldName, this.table, SqlAggregator::min);
	}

	public DbTableField<Double> avg() {
		return new DbTableField<>(this.fieldName, this.table, SqlAggregator::avg);
	}

	public DbTableField<Long> count() {
		return new DbTableField<>(this.fieldName, this.table, SqlAggregator::count);
	}

	public DbTableField<Double> sum() {
		return new DbTableField<>(this.fieldName, this.table, SqlAggregator::sum);
	}

	@Override
	public DbFieldOrder asc() {
		return new DbTableFieldOrder(getLabel(), SqlSortOrder.ASC);
	}

	@Override
	public DbFieldOrder desc() {
		return new DbTableFieldOrder(getLabel(), SqlSortOrder.DESC);
	}

	@Override
	public DbFieldLike as(String alias) {
		this.alias.setValue(alias);
		return this;
	}

	@Override
	public DbFieldValue<T> value(T value) { return new DbTableFieldValue<>(this.fieldName.getValue(), value); }

	@Override
	public String getFullQualification() {
		return this.table == null || this.table.getAlias() == null || this.table.getAlias().isBlank()
					   ? this.fieldName.getValue() : this.table.getAlias() + "." + this.fieldName.getValue();
	}

	@Override
	public String getLabel() {
		return this.alias.isBlank() ? this.getFullQualification() : this.alias.getValue();
	}

	@Override
	public String getDefinition() {
		String definition;
		if (this.aggregateFunction != null) {
			definition = this.aggregateFunction.apply(this.getFullQualification());
		} else {
			definition = this.getFullQualification();
		}
		return this.alias.isBlank() ? definition : definition + AS + this.alias;
	}

	@Override
	public String getFieldName() { return this.fieldName.getValue(); }

	@Override
	public DbTableLike getTableLike() { return this.table; }

	@Override
	public DbComparableField<T> mapTo(DbTableLike tableLike) {
		this.alias.setDefaultHolder(fieldName);
		return new DbTableField<>(this.alias, tableLike);
	}

	@Override
	public String toString() { return this.getLabel(); }

}