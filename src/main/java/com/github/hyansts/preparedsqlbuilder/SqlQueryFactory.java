package com.github.hyansts.preparedsqlbuilder;

import com.github.hyansts.preparedsqlbuilder.query.SqlQuery;

public abstract class SqlQueryFactory {
	public static SqlQuery create() {
		return new PreparedSqlBuilder();
	}
}