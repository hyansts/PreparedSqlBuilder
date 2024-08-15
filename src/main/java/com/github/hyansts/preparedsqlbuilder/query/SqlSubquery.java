package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.db.DbComparableField;
import com.github.hyansts.preparedsqlbuilder.db.DbTableLike;

public interface SqlSubquery extends SelectStatement, PreparedSql, DbTableLike {
	DbComparableField<?> getField(int fieldIndex);
}