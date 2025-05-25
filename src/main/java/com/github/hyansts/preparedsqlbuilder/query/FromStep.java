package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.db.DbField;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldOrder;
import com.github.hyansts.preparedsqlbuilder.db.DbTableLike;
import com.github.hyansts.preparedsqlbuilder.sql.SqlCondition;

public interface FromStep<T> extends CombiningOperation<T> {
	WhereStep<T> where(SqlCondition condition);

	JoinStep<T> innerJoin(DbTableLike table);

	JoinStep<T> leftJoin(DbTableLike table);

	JoinStep<T> rightJoin(DbTableLike table);

	JoinStep<T> fullJoin(DbTableLike table);

	FromStep<T> crossJoin(DbTableLike table);

	GroupByStep<T> groupBy(DbField<?>... fields);

	HavingStep<T> having(SqlCondition sqlCondition);

	OrderByStep<T> orderBy(DbFieldOrder... fields);

	LimitStep<T> limit(Integer number);
}