package com.github.hyansts.preparedsqlbuilder.sql;

public enum SqlSortOrder {
	ASC(" ASC"),
	DESC(" DESC");

	private final String keyword;

	SqlSortOrder(String keyword) { this.keyword = keyword; }

	@Override
	public String toString() { return keyword; }
}