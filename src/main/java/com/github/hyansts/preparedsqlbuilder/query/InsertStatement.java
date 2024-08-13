package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.db.DbTable;

public interface InsertStatement {
	InsertStep insertInto(DbTable table);
}
