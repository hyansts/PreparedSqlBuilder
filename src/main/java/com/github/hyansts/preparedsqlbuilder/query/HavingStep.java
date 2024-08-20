package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.db.DbFieldLike;

public interface HavingStep<T> extends CombiningOperation<T> {
	OrderByStep<T> orderBy(DbFieldLike... fields);

	LimitStep<T> limit(Integer number);
}