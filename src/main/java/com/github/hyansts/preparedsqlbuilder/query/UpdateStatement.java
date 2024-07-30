package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.DbTable;

public interface UpdateStatement {
	UpdateStep update(DbTable table);
}
