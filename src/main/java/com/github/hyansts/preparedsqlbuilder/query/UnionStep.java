package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.DbFieldLike;

public interface UnionStep extends CombiningOperation {
	OrderByStep orderBy(DbFieldLike... fields);

	LimitStep limit(Integer number);
}
