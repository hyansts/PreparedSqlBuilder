package com.github.hyansts.preparedsqlbuilder.query;

public interface OrderByStep<T> extends PreparedSql {
	LimitStep<T> limit(Integer number);
}