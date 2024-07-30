package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.sql.SqlCondition;

public interface DeleteStep extends PreparedSql {
	PreparedSql where(SqlCondition condition);
}
