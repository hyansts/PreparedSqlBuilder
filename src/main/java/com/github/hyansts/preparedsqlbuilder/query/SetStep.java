package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.sql.SqlCondition;

public interface SetStep extends PreparedSql {
	PreparedSql where(SqlCondition condition);
}
