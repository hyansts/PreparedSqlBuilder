package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.DbComparableField;

public interface SqlSubquery extends SqlQueryBuilder {
	DbComparableField<?> getField(int fieldIndex);
}