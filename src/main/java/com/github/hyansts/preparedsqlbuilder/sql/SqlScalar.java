package com.github.hyansts.preparedsqlbuilder.sql;

import java.util.StringJoiner;

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
	public static String abs(String arg) { return format("ABS", arg); }
	public static String ceil(String arg) { return format("CEIL", arg); }
	public static String floor(String arg) { return format("FLOOR", arg); }
	public static String round(String arg) { return format("ROUND", arg); }

	// :: For dates
	public static String date(String... args) { return format("DATE", (Object[]) args); }
	public static String time(String... args) { return format("TIME", (Object[]) args); }
	public static String datetime(String... args) { return format("DATETIME", (Object[]) args); }

	// :: For null values
	public static String nullif(String arg1, String arg2) { return format("NULLIF", arg1, arg2); }
	public static String coalesce(String... args) { return format("COALESCE", (Object[]) args); }

	private static String format(String str, Object... args) {
		StringJoiner joiner = new StringJoiner(", ", "(", ")");
		for (Object arg : args) {
			joiner.add(String.valueOf(arg));
		}
		return str + joiner;
	}

}