package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.DbField;
import com.github.hyansts.preparedsqlbuilder.DbFieldLike;

public interface WhereStep extends CombiningOperation {
	GroupByStep groupBy(DbField... fields);

	OrderByStep orderBy(DbFieldLike... fields);

	LimitStep limit(Integer number);
}
