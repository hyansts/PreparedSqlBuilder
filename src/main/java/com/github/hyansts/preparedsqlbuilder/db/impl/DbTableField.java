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

	public DbAggregateField<T> max() {
		return new DbAggregateField<>(SqlAggregator.MAX, this);
	}

	public DbAggregateField<T> min() {
		return new DbAggregateField<>(SqlAggregator.MIN, this);
	}

	public DbAggregateField<Double> avg() {
		return new DbAggregateField<>(SqlAggregator.AVG, this);
	}

	public DbAggregateField<Long> count() {
		return new DbAggregateField<>(SqlAggregator.COUNT, this);
	}

	public DbAggregateField<Double> sum() {
		return new DbAggregateField<>(SqlAggregator.SUM, this);
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
		String fieldName = this.fieldName.getValueOrDefault();
		return this.table == null || this.table.getAlias() == null || this.table.getAlias().isBlank()
					   ? fieldName : this.table.getAlias() + "." + fieldName;
	}

	@Override
	public String getLabel() {
		return this.alias.isBlank() ? this.getFullQualification() : this.alias.getValue();
	}

	@Override
	public String getDefinition() {
		return this.alias.isBlank() ? getFullQualification() : getFullQualification() + AS + this.alias;
	}

	@Override
	public String getFieldName() { return this.fieldName.getValueOrDefault(); }

	@Override
	public DbTableLike getTableLike() { return this.table; }

	@Override
	public DbComparableField<T> mapTo(DbTableLike tableLike) {
		this.alias.setDefaultHolder(this.fieldName);
		return new DbTableField<>(this.alias, tableLike);
	}

	@Override
	public String toString() { return this.getLabel(); }

}