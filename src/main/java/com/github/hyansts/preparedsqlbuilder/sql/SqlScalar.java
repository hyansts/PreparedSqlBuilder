package com.github.hyansts.preparedsqlbuilder.sql;

import java.util.StringJoiner;

//TODO write tests
public class SqlScalar {

	private SqlScalar() { }

	// :: For strings
	public static String trim(String arg) { return format("TRIM", arg); }
	public static String upper(String arg) { return format("UPPER", arg); }
	public static String lower(String arg) { return format("LOWER", arg); }
	public static String concat(String... args) { return format("CONCAT", (Object[]) args); }
	public static String length(String arg) { return format("LENGTH", arg); }
	public static String substring(String arg, int start) { return format("SUBSTRING", arg, start); }
	public static String substring(String arg, int start, int end) { return format("SUBSTRING", arg, start, end); }
	public static String replace(String arg, String tgt, String rpl) { return format("REPLACE", arg, tgt, rpl); }

	// :: For numbers
	public static String abs(String field) { return format("ABS", field); }
	public static String ceil(String field) { return format("CEIL", field); }
	public static String floor(String field) { return format("FLOOR", field); }
	public static String round(String field) { return format("ROUND", field); }

	// :: For dates
	public static String date(String... fields) { return format("DATE", (Object[]) fields); }
	public static String time(String... fields) { return format("TIME", (Object[]) fields); }
	public static String datetime(String... fields) { return format("DATETIME", (Object[]) fields); }

	// :: For null values
	public static String nullif(String field1, String field2) { return format("NULLIF", field1, field2); }
	public static String coalesce(String... fields) { return format("COALESCE", (Object[]) fields); }

	private static String format(String str, Object... args) {
		StringJoiner joiner = new StringJoiner(", ", "(", ")");
		for (Object arg : args) {
			joiner.add(String.valueOf(arg));
		}
		return joiner.toString();
	}
}