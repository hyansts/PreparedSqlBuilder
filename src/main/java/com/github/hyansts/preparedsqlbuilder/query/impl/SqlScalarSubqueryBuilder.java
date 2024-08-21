package com.github.hyansts.preparedsqlbuilder.query.impl;

import com.github.hyansts.preparedsqlbuilder.db.DbComparableField;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldLike;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldOrder;
import com.github.hyansts.preparedsqlbuilder.db.DbTableLike;
import com.github.hyansts.preparedsqlbuilder.db.impl.DbTableFieldOrder;
import com.github.hyansts.preparedsqlbuilder.query.SqlScalarSubquery;
import com.github.hyansts.preparedsqlbuilder.sql.SqlSortOrder;
import com.github.hyansts.preparedsqlbuilder.util.StringHolder;

import static com.github.hyansts.preparedsqlbuilder.sql.SqlKeyword.AS;

class SqlScalarSubqueryBuilder<T> extends BaseSqlBuilder<SqlScalarSubquery<T>> implements SqlScalarSubquery<T> {

	private final StringHolder alias;
	private final DbTableLike tableLike;

	public SqlScalarSubqueryBuilder() {
		this.alias = new StringHolder();
		this.tableLike = null;
	}

	private SqlScalarSubqueryBuilder(StringHolder alias, DbTableLike tableLike) {
		this.alias = alias;
		this.tableLike = tableLike;
	}

	@Override
	public String getFullQualification() {
		return this.tableLike == null || this.tableLike.getAlias() == null || tableLike.getAlias().isBlank()
					   ? this.getSql() : this.tableLike.getAlias() + "." + this.getLabel();
	}

	@Override
	public String getDefinition() {
		return this.alias.isBlank() ? this.getFullQualification() : this.getFullQualification() + AS + this.alias;
	}

	@Override
	public String getLabel() {
		if (this.alias.isBlank()) {
			throw new IllegalStateException("Cannot reference a subquery with an empty alias: " + this.getSql());
		}
		return this.alias.getValue();
	}

	@Override
	public DbTableLike getTableLike() {
		return tableLike;
	}

	@Override
	public DbFieldLike as(String alias) {
		this.alias.setValue(alias);
		return this;
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
	public DbComparableField<T> mapTo(DbTableLike tableLike) {
		return new SqlScalarSubqueryBuilder<>(this.alias, tableLike);
	}

	@Override
	public SqlScalarSubquery<T> getQuery() {
		return this;
	}

	@Override
	public String getSql() {
		return "(" + super.getSql() + ")";
	}

}