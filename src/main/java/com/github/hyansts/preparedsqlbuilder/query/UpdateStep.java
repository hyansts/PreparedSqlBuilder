package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.db.DbFieldValue;

public interface UpdateStep extends PreparedSql {
	SetStep set(DbFieldValue<?> field);
}
