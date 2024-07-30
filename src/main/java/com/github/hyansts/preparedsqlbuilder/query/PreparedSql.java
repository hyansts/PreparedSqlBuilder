package com.github.hyansts.preparedsqlbuilder.query;

import java.util.List;

public interface PreparedSql {
	String getSql();

	List<Object> getValues();
}
