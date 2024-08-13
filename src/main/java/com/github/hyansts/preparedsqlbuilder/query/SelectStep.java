package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.db.DbTableLike;

public interface SelectStep extends PreparedSql {
	FromStep from(DbTableLike table);
}
