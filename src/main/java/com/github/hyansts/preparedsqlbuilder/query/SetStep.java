package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.DbTableField;
import com.github.hyansts.preparedsqlbuilder.sql.SqlCondition;

public interface SetStep extends PreparedSql {
	<T> SetStep set(DbTableField<T> field, T value);

	PreparedSql where(SqlCondition condition);
}
