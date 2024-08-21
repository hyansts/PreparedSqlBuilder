package com.github.hyansts.preparedsqlbuilder.db;

import com.github.hyansts.preparedsqlbuilder.sql.SqlSortOrder;

public interface DbFieldOrder {
	String getFieldLabel();

	SqlSortOrder getOrder();

	default String getDefinition() {
		return getOrder() != null ? getFieldLabel() + getOrder().toString() : getFieldLabel();
	}

}