package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.db.DbComparableField;
import com.github.hyansts.preparedsqlbuilder.db.DbTableLike;

public interface SqlSubquery extends SelectStatement<SqlSubquery>, DbTableLike {
	<T> DbComparableField<T> getField(DbComparableField<T> field);
}