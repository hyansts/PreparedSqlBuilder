package com.github.hyansts.preparedsqlbuilder.db.impl;

import com.github.hyansts.preparedsqlbuilder.db.DbComparableField;
import com.github.hyansts.preparedsqlbuilder.db.DbField;
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
	private SqlSortOrder sortOrder = SqlSortOrder.ASC;

	public DbTableField(String name, DbTableLike table) {
		this.FIELD_NAME = name;
		this.TABLE = table;
	}

	public DbTableField<T> max() {
		return new DbTableField<>(SqlAggregator.max(this.getFullQualification()), null);
	}

	public DbTableField<T> min() {
		return new DbTableField<>(SqlAggregator.min(this.getFullQualification()), null);
	}

	public DbTableField<Double> avg() {
		return new DbTableField<>(SqlAggregator.avg(this.getFullQualification()), null);
	}

	public DbTableField<Long> count() {
		return new DbTableField<>(SqlAggregator.count(this.getFullQualification()), null);
	}

	public DbTableField<Double> sum() {
		return new DbTableField<>(SqlAggregator.sum(this.getFullQualification()), null);
	}

	@Override
	public DbTableField<T> asc() {
		this.sortOrder = SqlSortOrder.ASC;
		return this;
	}

	@Override
	public DbTableField<T> desc() {
		this.sortOrder = SqlSortOrder.DESC;
		return this;
	}

	@Override
	public DbTableField<T> as(String alias) {
		this.alias = alias;
		return this;
	}

	@Override
	public DbFieldValue<T> value(T value) {
		return new DbTableFieldValue<>(this.FIELD_NAME, value);
	}

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
		return this.alias == null || this.alias.isBlank()
					   ? this.getFullQualification() : this.getFullQualification() + AS + this.alias;
	}

	@Override
	public String getFieldName() { return this.FIELD_NAME; }

	@Override
	public DbComparableField<T> mapTo(DbTableLike tableLike) {
		return new DbTableField<>(this.getLabel(), tableLike);
	}

	@Override
	public SqlSortOrder getSortOrder() { return this.sortOrder; }

	@Override
	public String toString() {
		return this.getLabel();
	}
}