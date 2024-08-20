package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.db.DbField;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldLike;

public interface SelectStatement<T> extends PreparedSql {
	SelectStep<T> select(DbFieldLike... fields);

	SelectStep<T> select(String expression, DbFieldLike... fields);

	SelectStep<T> selectDistinct(DbFieldLike... fields);

	SelectStep<T> selectDistinct(String expression, DbFieldLike... fields);

	SelectStep<T> selectCount(DbField field);

	SelectStep<T> selectCount();
}