package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.db.DbComparableField;

public interface SqlSubquery extends SelectStatement, PreparedSql {
	DbComparableField<?> getField(int fieldIndex);
}