package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.DbFieldValue;
import com.github.hyansts.preparedsqlbuilder.sql.SqlCondition;

public interface SetStep extends PreparedSql {
	SetStep set(DbFieldValue<?> field);

	PreparedSql where(SqlCondition condition);
}
