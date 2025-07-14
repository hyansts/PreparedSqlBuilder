package com.github.hyansts.preparedsqlbuilder.query;

import java.util.function.Consumer;

import com.github.hyansts.preparedsqlbuilder.db.DbComparableField;
import com.github.hyansts.preparedsqlbuilder.sql.SqlCondition;

public interface SqlScalarSubquery<T> extends SelectStatement<SqlScalarSubquery<T>>, DbComparableField<T> {
	SqlCondition exists(Consumer<SelectStatement<SqlScalarSubquery<T>>> select);

	SqlCondition notExists(Consumer<SelectStatement<SqlScalarSubquery<T>>> select);
}