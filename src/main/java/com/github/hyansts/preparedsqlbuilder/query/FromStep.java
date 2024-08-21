package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.db.DbField;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldOrder;
import com.github.hyansts.preparedsqlbuilder.db.DbTableLike;
import com.github.hyansts.preparedsqlbuilder.sql.SqlCondition;

public interface FromStep<T> extends CombiningOperation<T> {
	WhereStep<T> where(SqlCondition condition);

	JoinStep<T> innerJoin(DbTableLike table);

	JoinStep<T> innerJoin(CombinableQuery<SqlSubquery> tableLike);

	JoinStep<T> leftJoin(DbTableLike table);

	JoinStep<T> leftJoin(CombinableQuery<SqlSubquery> tableLike);

	JoinStep<T> rightJoin(DbTableLike table);

	JoinStep<T> rightJoin(CombinableQuery<SqlSubquery> tableLike);

	JoinStep<T> fullJoin(DbTableLike table);

	JoinStep<T> fullJoin(CombinableQuery<SqlSubquery> tableLike);

	FromStep<T> crossJoin(DbTableLike table);

	FromStep<T> crossJoin(CombinableQuery<SqlSubquery> tableLike);

	GroupByStep<T> groupBy(DbField... fields);

	OrderByStep<T> orderBy(DbFieldOrder... fields);

	LimitStep<T> limit(Integer number);
}