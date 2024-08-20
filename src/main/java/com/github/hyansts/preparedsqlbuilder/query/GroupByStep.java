package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.db.DbFieldLike;
import com.github.hyansts.preparedsqlbuilder.sql.SqlCondition;

public interface GroupByStep<T> extends CombiningOperation<T> {
	HavingStep<T> having(SqlCondition sqlCondition);

	OrderByStep<T> orderBy(DbFieldLike... fields);

	LimitStep<T> limit(Integer number);
}
