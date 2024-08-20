package com.github.hyansts.preparedsqlbuilder.query;

public interface SqlQuery extends SelectStatement<SqlQuery>, InsertStatement, UpdateStatement, DeleteStatement, CombinableQuery<SqlQuery> { }