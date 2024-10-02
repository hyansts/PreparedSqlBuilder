package com.github.hyansts.preparedsqlbuilder.sql;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SqlScalarTest {

	@Test
	void trim() {
		String arg = "'  test  '";
		assertEquals("TRIM(" + arg + ")", SqlScalar.trim(arg));
	}

	@Test
	void concat() {
		String arg1 = "'Hello'";
		String arg2 = "' world!'";
		assertEquals("CONCAT(" + arg1 + ", " + arg2 + ")", SqlScalar.concat(arg1, arg2));
	}

	@Test
	void substring() {
		String arg = "'Hello world!'";
		int start = 3;
		int end = 5;
		assertEquals("SUBSTRING(" + arg + ", " + start + ", " + end + ")", SqlScalar.substring(arg, start, end));
	}

	@Test
	void replace() {
		String arg = "'Hello world!'";
		String tgt = "'world'";
		String rpl = "'Universe'";
		assertEquals("REPLACE(" + arg + ", " + tgt + ", " + rpl + ")", SqlScalar.replace(arg, tgt, rpl));
	}

}