package com.github.hyansts.preparedsqlbuilder.sql;

public enum SqlAggregator {
	AVG, MAX, MIN, SUM, COUNT;

	public String applyTo(String field) { return this + "(" + field + ")"; }

}