package com.github.hyansts.preparedsqlbuilder;

import com.github.hyansts.preparedsqlbuilder.sql.SqlSortOrder;

public interface DbFieldLike {
	String getFullQualification();

	String getDefinition();

	String getLabel();

	DbComparableField<?> mapTo(DbTableLike tableLike);

	SqlSortOrder getSortOrder();

	DbFieldLike as(String alias);

	DbFieldLike asc();

	DbFieldLike desc();
}
