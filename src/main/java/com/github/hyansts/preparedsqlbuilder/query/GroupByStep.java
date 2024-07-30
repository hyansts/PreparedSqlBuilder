package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.DbTableField;
import com.github.hyansts.preparedsqlbuilder.sql.SqlCondition;

public interface GroupByStep extends CombiningOperation {
	OrderByStep having(SqlCondition sqlCondition);

	OrderByStep orderBy(DbTableField<?>... fields);

	LimitStep limit(Integer number);
}
