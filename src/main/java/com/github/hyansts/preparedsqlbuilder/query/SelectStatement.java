package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.DbTableField;

public interface SelectStatement {
	SelectStep select(DbTableField<?>... fields);

	SelectStep select(String expression, DbTableField<?>... fields);

	SelectStep selectDistinct(DbTableField<?>... fields);

	SelectStep selectDistinct(String expression, DbTableField<?>... fields);

	SelectStep selectCount(DbTableField<?> field);

	SelectStep selectCount();
}
