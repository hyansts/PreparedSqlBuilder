package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.db.DbField;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldLike;

public interface WhereStep<T> extends CombiningOperation<T> {
	GroupByStep<T> groupBy(DbField... fields);

	OrderByStep<T> orderBy(DbFieldLike... fields);

	LimitStep<T> limit(Integer number);
}
