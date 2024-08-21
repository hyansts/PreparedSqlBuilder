package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.db.DbFieldOrder;

public interface HavingStep<T> extends CombiningOperation<T> {
	OrderByStep<T> orderBy(DbFieldOrder... fields);

	LimitStep<T> limit(Integer number);
}