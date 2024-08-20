package com.github.hyansts.preparedsqlbuilder.query;

public interface CombinableQuery<T> extends PreparedSql {
	T getQuery();
}