package com.github.hyansts.preparedsqlbuilder.query.impl;

import com.github.hyansts.preparedsqlbuilder.query.SqlQuery;
import com.github.hyansts.preparedsqlbuilder.query.SqlScalarSubquery;
import com.github.hyansts.preparedsqlbuilder.query.SqlSubquery;

public abstract class SqlQueryFactory {
	public static SqlQuery createQuery() {
		return new SqlQueryBuilder();
	}

	public static SqlSubquery createSubquery() {
		return new SqlSubqueryBuilder();
	}

	public static <T> SqlScalarSubquery<T> createScalarSubquery() {
		return new SqlScalarSubqueryBuilder<>();
	}

}