package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.DbTable;

public interface InsertStatement {
	InsertStep insertInto(DbTable table);
}
