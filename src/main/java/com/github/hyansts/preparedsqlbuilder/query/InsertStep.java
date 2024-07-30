package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.DbTableField;

public interface InsertStep {
	PreparedSql values(DbTableField<?>... fields);
}
