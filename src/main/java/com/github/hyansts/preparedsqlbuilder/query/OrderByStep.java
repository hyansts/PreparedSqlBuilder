package com.github.hyansts.preparedsqlbuilder.query;

public interface OrderByStep extends PreparedSql {
	LimitStep limit(Integer number);
}
