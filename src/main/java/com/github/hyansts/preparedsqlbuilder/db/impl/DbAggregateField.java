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

public class DbAggregateField<T> implements DbComparableField<T> {

	private final SqlAggregator aggregator;
	private final DbField field;
	private final StringHolder alias = new StringHolder();

	public DbAggregateField(SqlAggregator aggregator, DbField field) {
		this.aggregator = aggregator;
		this.field = field;
		this.alias.setDefaultHolder(new StringHolder((aggregator + "_" + field.getFieldName()).toLowerCase()));
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
	public String getFullQualification() {
		return this.aggregator.applyTo(this.field.getFullQualification());
	}

	@Override
	public String getDefinition() {
		return this.alias.isBlank() ? getFullQualification() : getFullQualification() + AS + this.alias;
	}

	@Override
	public String getLabel() {
		if (this.alias.isBlank()) {
			throw new IllegalStateException("Aggregate function must have an alias to be referenced: " + getFullQualification());
		}
		return this.alias.getValue();
	}

	@Override
	public DbTableLike getTableLike() {
		return this.field.getTableLike();
	}

	@Override
	public DbFieldLike as(String alias) {
		this.alias.setValue(alias);
		return this;
	}

	@Override
	public DbComparableField<T> mapTo(DbTableLike tableLike) {
		return new DbTableField<>(this.alias, tableLike);
	}

}