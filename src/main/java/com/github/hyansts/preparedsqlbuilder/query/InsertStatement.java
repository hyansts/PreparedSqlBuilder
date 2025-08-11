package com.github.hyansts.preparedsqlbuilder.query;

import java.util.function.Consumer;

import com.github.hyansts.preparedsqlbuilder.db.DbTable;

public interface InsertStatement {
	InsertStep insertInto(DbTable table);

	SqlBatchQuery batchInsert(Consumer<InsertStatement> query);
}