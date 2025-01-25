package com.github.hyansts.preparedsqlbuilder.util;

public final class StringUtil {

	private StringUtil() { }

	public static boolean isBlank(String s) {
		return s == null || s.isBlank();
	}

}