package com.github.hyansts.preparedsqlbuilder.query.impl;

import com.github.hyansts.preparedsqlbuilder.db.DbComparableField;
import com.github.hyansts.preparedsqlbuilder.db.DbTableLike;
import com.github.hyansts.preparedsqlbuilder.query.SqlSubquery;
import com.github.hyansts.preparedsqlbuilder.util.StringUtil;

import static com.github.hyansts.preparedsqlbuilder.sql.SqlKeyword.AS;

class SqlSubqueryBuilder extends BaseSqlBuilder<SqlSubquery> implements SqlSubquery {

	private String alias;

	@Override
	public String getAlias() {
		return this.alias;
	}

	@Override
	public String getDefinition() {
		if (StringUtil.isBlank(this.alias)) {
			throw new IllegalStateException("Derived table subquery must have an alias: " + this.getSql());
		}
		return this.getSql() + AS + this.alias;
	}

	@Override
	public DbTableLike as(String alias) {
		this.alias = alias;
		return this;
	}

	@Override
	public <T> DbComparableField<T> getField(DbComparableField<T> field) {
		return field.mapTo(this);
	}

	@Override
	public SqlSubquery getQuery() {
		return this;
	}

	@Override
	public String getSql() {
		return "(" + super.getSql() + ")";
	}

}