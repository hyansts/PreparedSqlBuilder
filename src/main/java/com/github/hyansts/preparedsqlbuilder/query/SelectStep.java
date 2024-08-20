package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.db.DbTableLike;

public interface SelectStep<T> extends PreparedSql {
	FromStep<T> from(DbTableLike table);

	FromStep<T> from(CombinableQuery<SqlSubquery> tableLike);
}