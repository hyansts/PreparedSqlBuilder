package com.github.hyansts.preparedsqlbuilder.sql;

/**
 * An enumeration of SQL sort orders.
 * <p>
 * This enumeration is used to specify the sort order of a field in a query.
 * Fields can be sorted in ASCENDING or DESCENDING order.
 */
public enum SqlSortOrder {
	ASC(" ASC"),
	DESC(" DESC");

	private final String keyword;

	SqlSortOrder(String keyword) { this.keyword = keyword; }

	@Override
	public String toString() { return keyword; }
}