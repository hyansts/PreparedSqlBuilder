package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.sql.SqlCondition;

public interface JoinStep extends PreparedSql {
	FromStep on(SqlCondition condition);
}
