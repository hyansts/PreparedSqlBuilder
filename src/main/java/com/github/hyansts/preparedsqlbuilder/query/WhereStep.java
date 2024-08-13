package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.db.DbField;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldLike;

public interface WhereStep extends CombiningOperation {
	GroupByStep groupBy(DbField... fields);

	OrderByStep orderBy(DbFieldLike... fields);

	LimitStep limit(Integer number);
}
