package com.github.hyansts.preparedsqlbuilder;

import com.github.hyansts.preparedsqlbuilder.query.SqlQuery;
import com.github.hyansts.preparedsqlbuilder.query.SqlScalarSubquery;
import com.github.hyansts.preparedsqlbuilder.query.SqlSubquery;

public abstract class SqlQueryFactory {
	public static SqlQuery createQuery() {
		return new PreparedSqlBuilder();
	}
	public static SqlSubquery createSubquery() {
		return new PreparedSubqueryBuilder();
	}

	public static <T> SqlScalarSubquery<T> createScalarSubquery() {
		return new PreparedScalarSubqueryBuilder<>();
	}
}