package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.DbTable;
import com.github.hyansts.preparedsqlbuilder.DbTableField;
import com.github.hyansts.preparedsqlbuilder.sql.SqlCondition;

public interface FromStep extends CombiningOperation {
	WhereStep where(SqlCondition condition);

	JoinStep innerJoin(DbTable table);

	JoinStep leftJoin(DbTable table);

	JoinStep rightJoin(DbTable table);

	JoinStep fullJoin(DbTable table);

	FromStep crossJoin(DbTable table);

	GroupByStep groupBy(DbTableField<?>... fields);

	OrderByStep orderBy(DbTableField<?>... fields);

	LimitStep limit(Integer number);
}
