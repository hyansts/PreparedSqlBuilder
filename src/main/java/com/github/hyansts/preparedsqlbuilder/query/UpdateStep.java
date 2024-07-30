package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.DbTableField;

public interface UpdateStep extends PreparedSql {
	<T> SetStep set(DbTableField<T> field, T value);
}
