package com.github.hyansts.preparedsqlbuilder.db.impl;

import com.github.hyansts.preparedsqlbuilder.db.DbFieldOrder;
import com.github.hyansts.preparedsqlbuilder.sql.SqlSortOrder;

/**
 * Represents a field in an ASCENDING or DESCENDING order.
 * <p>
 * This class is used to define the order of a field in an Order by clause.
 * <p>
 * The field label is the name or the alias of the field if defined.
 */
public class DbTableFieldOrder implements DbFieldOrder {

	private final String fieldLabel;
	private final SqlSortOrder sortOrder;

	public DbTableFieldOrder(String fieldLabel, SqlSortOrder sortOrder) {
		this.fieldLabel = fieldLabel;
		this.sortOrder = sortOrder;
	}

	@Override
	public String getFieldLabel() {
		return fieldLabel;
	}

	@Override
	public SqlSortOrder getOrder() {
		return sortOrder;
	}

}