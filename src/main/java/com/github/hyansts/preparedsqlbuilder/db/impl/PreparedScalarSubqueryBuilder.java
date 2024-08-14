package com.github.hyansts.preparedsqlbuilder.db.impl;

import com.github.hyansts.preparedsqlbuilder.db.DbFieldLike;
import com.github.hyansts.preparedsqlbuilder.db.DbTableLike;
import com.github.hyansts.preparedsqlbuilder.query.SqlScalarSubquery;
import com.github.hyansts.preparedsqlbuilder.sql.SqlSortOrder;

import static com.github.hyansts.preparedsqlbuilder.sql.SqlKeyword.AS;

class PreparedScalarSubqueryBuilder<T> extends PreparedSqlBuilder implements SqlScalarSubquery<T>, DbFieldLike {

	private String alias;
	private DbTableLike tableLike;
	private SqlSortOrder sortOrder;

	public PreparedScalarSubqueryBuilder() { }

	public PreparedScalarSubqueryBuilder(DbTableLike tableLike) {
		this.tableLike = tableLike;
	}

	@Override
	public String getFullQualification() {
		return this.tableLike == null || this.tableLike.getAlias() == null || tableLike.getAlias().isBlank()
					   ? "(" + this.getSql() + ")" : this.tableLike.getAlias() + "." + this.getLabel();
	}

	@Override
	public String getDefinition() {
		return this.alias == null || this.alias.isBlank()
					   ? this.getFullQualification() : this.getFullQualification() + AS + this.alias;
	}

	@Override
	public String getLabel() {
		if (this.alias == null || this.alias.isBlank()) {
			throw new IllegalStateException("Cannot reference a subquery with an empty alias: " + getDefinition());
		}
		return this.alias;
	}

	@Override
	public DbTableLike getTableLike() {
		return tableLike;
	}

	@Override
	public PreparedScalarSubqueryBuilder<T> as(String alias) {
		this.alias = alias;
		return this;
	}

	@Override
	public PreparedScalarSubqueryBuilder<T> asc() {
		this.sortOrder = SqlSortOrder.ASC;
		return this;
	}

	@Override
	public PreparedScalarSubqueryBuilder<T> desc() {
		this.sortOrder = SqlSortOrder.DESC;
		return this;
	}

	@Override
	public SqlSortOrder getSortOrder() { return sortOrder; }

	@Override
	public PreparedScalarSubqueryBuilder<T> mapTo(DbTableLike tableLike) {
		return new PreparedScalarSubqueryBuilder<>(tableLike);
	}

}
