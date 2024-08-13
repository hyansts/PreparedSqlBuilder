package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.DbFieldLike;
import com.github.hyansts.preparedsqlbuilder.sql.SqlCondition;

public interface GroupByStep extends CombiningOperation {
	OrderByStep having(SqlCondition sqlCondition);

	OrderByStep orderBy(DbFieldLike... fields);

	LimitStep limit(Integer number);
}
