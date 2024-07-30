package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.DbTableField;

public interface UnionStep extends CombiningOperation {
	OrderByStep orderBy(DbTableField<?>... fields);

	LimitStep limit(Integer number);
}
