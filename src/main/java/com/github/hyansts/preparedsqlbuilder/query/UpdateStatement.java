package com.github.hyansts.preparedsqlbuilder.query;

import java.util.function.Consumer;

import com.github.hyansts.preparedsqlbuilder.db.DbTable;

public interface UpdateStatement {
	UpdateStep update(DbTable table);

	SqlBatchQuery batchUpdate(Consumer<UpdateStatement> query);
}