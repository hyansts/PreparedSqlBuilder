package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.db.DbField;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldLike;

public interface SelectStatement {
	SelectStep select(DbFieldLike... fields);

	SelectStep select(String expression, DbFieldLike... fields);

	SelectStep selectDistinct(DbFieldLike... fields);

	SelectStep selectDistinct(String expression, DbFieldLike... fields);

	SelectStep selectCount(DbField field);

	SelectStep selectCount();
}
