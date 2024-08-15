package com.github.hyansts.preparedsqlbuilder.query;

import com.github.hyansts.preparedsqlbuilder.db.DbComparableField;

public interface SqlScalarSubquery<T> extends SelectStatement, PreparedSql, DbComparableField<T> { }