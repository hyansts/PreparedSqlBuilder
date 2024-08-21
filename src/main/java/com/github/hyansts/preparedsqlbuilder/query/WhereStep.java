package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.db.DbField;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldOrder;

public interface WhereStep<T> extends CombiningOperation<T> {
	GroupByStep<T> groupBy(DbField... fields);

	OrderByStep<T> orderBy(DbFieldOrder... fields);

	LimitStep<T> limit(Integer number);
}