package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.DbField;
import com.github.hyansts.preparedsqlbuilder.DbFieldLike;
import com.github.hyansts.preparedsqlbuilder.DbTableLike;
import com.github.hyansts.preparedsqlbuilder.sql.SqlCondition;

public interface FromStep extends CombiningOperation {
	WhereStep where(SqlCondition condition);

	JoinStep innerJoin(DbTableLike table);

	JoinStep leftJoin(DbTableLike table);

	JoinStep rightJoin(DbTableLike table);

	JoinStep fullJoin(DbTableLike table);

	FromStep crossJoin(DbTableLike table);

	GroupByStep groupBy(DbField... fields);

	OrderByStep orderBy(DbFieldLike... fields);

	LimitStep limit(Integer number);
}
