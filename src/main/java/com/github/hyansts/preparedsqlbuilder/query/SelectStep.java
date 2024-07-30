package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.DbTable;

public interface SelectStep extends PreparedSql {
	FromStep from(DbTable table);
}
