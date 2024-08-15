package com.github.hyansts.preparedsqlbuilder.sql;

//TODO write tests
public class SqlScalar {

	private SqlScalar() { }

	// :: For strings
	public static String trim(String arg) { return format("TRIM", arg); }
	public static String upper(String arg) { return format("UPPER", arg); }
	public static String lower(String arg) { return format("LOWER", arg); }
	public static String concat(String... args) { return format("CONCAT", args); }
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
	public static String date(String... fields) { return format("DATE", fields); }
	public static String time(String... fields) { return format("TIME", fields); }
	public static String datetime(String... fields) { return format("DATETIME", fields); }

	// :: For null values
	public static String nullif(String field1, String field2) { return format("NULLIF", field1, field2); }
	public static String coalesce(String... fields) { return format("COALESCE", fields); }

	// :: Format helpers
	private static String format(String str, String arg) {
		return String.format(str + "(%s)", arg);
	}

	private static String format(String str, String arg1, String arg2) {
		return String.format(str + "(%s, %s)", arg1, arg2);
	}

	private static String format(String str, String arg1, int arg2) {
		return String.format(str + "(%s, %d)", arg1, arg2);
	}

	private static String format(String str, String arg1, int arg2, int arg3) {
		return String.format(str + "(%s, %d, %d)", arg1, arg2, arg3);
	}

	private static String format(String str, String arg1, String arg2, String arg3) {
		return String.format(str + "(%s, %s, %s)", arg1, arg2, arg3);
	}

	private static String format(String str, String... args) {
		return String.format(str + "(%s" + ", %s".repeat(args.length - 1) + ")", (Object[]) args);
	}
}