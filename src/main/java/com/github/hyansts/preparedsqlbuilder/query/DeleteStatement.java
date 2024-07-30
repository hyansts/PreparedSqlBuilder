package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.DbTable;

public interface DeleteStatement {
	DeleteStep deleteFrom(DbTable table);
}
