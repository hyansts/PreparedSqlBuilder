package com.github.hyansts.preparedsqlbuilder.query;

import java.util.function.Consumer;

import com.github.hyansts.preparedsqlbuilder.db.DbTable;

public interface DeleteStatement {
	DeleteStep deleteFrom(DbTable table);

	SqlBatchQuery batchDelete(Consumer<DeleteStatement> query);
}
