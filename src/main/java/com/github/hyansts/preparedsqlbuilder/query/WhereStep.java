package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.DbTableField;

public interface WhereStep extends CombiningOperation {
	GroupByStep groupBy(DbTableField<?>... fields);

	OrderByStep orderBy(DbTableField<?>... fields);

	LimitStep limit(Integer number);
}
