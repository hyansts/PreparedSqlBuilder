package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.DbFieldValue;

public interface InsertStep {
	PreparedSql values(DbFieldValue<?>... fields);
}
