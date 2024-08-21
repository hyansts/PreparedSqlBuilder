package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.db.DbTable;

public interface UpdateStatement {
	UpdateStep update(DbTable table);
}