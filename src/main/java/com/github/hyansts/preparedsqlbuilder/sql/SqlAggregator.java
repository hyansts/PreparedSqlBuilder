package com.github.hyansts.preparedsqlbuilder.sql;

public class SqlAggregator {

	private SqlAggregator() { }

	public static String avg(String field) { return String.format("AVG(%s)", field); }
	public static String max(String field) { return String.format("MAX(%s)", field); }
	public static String min(String field) { return String.format("MIN(%s)", field); }
	public static String sum(String field) { return String.format("SUM(%s)", field); }
	public static String count(String field) { return String.format("COUNT(%s)", field); }
}

