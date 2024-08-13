package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.DbFieldValue;

public interface UpdateStep extends PreparedSql {
	SetStep set(DbFieldValue<?> field);
}
