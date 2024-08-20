package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.sql.SqlCondition;

public interface JoinStep<T> extends PreparedSql {
	FromStep<T> on(SqlCondition condition);
}