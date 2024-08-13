package com.github.hyansts.preparedsqlbuilder;

import com.github.hyansts.preparedsqlbuilder.query.SqlSubquery;

import static com.github.hyansts.preparedsqlbuilder.sql.SqlKeyword.AS;

public class PreparedSubqueryBuilder extends PreparedSqlBuilder implements SqlSubquery, DbTableLike {

	private String alias;

	@Override
	public String getAlias() {
		if (this.alias == null || this.alias.isBlank()) {
			throw new IllegalStateException("Cannot reference a subquery with an empty alias: " + getDefinition());
		}
		return this.alias;
	}

	@Override
	public String getDefinition() {
		if (this.alias == null || this.alias.isBlank()) {
			throw new IllegalStateException("Cannot leave a derived table subquery with an empty alias: " + getDefinition());
		}
		return "(" + this.getSql() + ")" + AS + this.alias;
	}

	@Override
	public PreparedSubqueryBuilder as(String alias) {
		this.alias = alias;
		return this;
	}

	@Override
	public DbComparableField<?> getField(int fieldIndex) {
		return this.selectedFields.get(fieldIndex).mapTo(this);
	}
}
