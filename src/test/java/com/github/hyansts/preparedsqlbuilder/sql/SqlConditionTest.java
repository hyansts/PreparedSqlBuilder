package com.github.hyansts.preparedsqlbuilder.sql;

import java.util.List;

import com.github.hyansts.preparedsqlbuilder.db.impl.DbTableField;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SqlConditionTest {

	@Test
	void testAndField() {
		DbTableField<Integer> field1 = new DbTableField<>("field1", null);
		DbTableField<Integer> field2 = new DbTableField<>("field2", null);
		DbTableField<Integer> field3 = new DbTableField<>("field3", null);

		SqlCondition condition1 = new SqlCondition(field1, SqlConditionOperator.EQ, field2);
		SqlCondition condition2 = new SqlCondition(field1, SqlConditionOperator.EQ, field3);

		SqlCondition result = condition1.and(condition2);

		String expected = "field1 = field2 AND field1 = field3";
		assertEquals(expected, result.getSql());
	}

	@Test
	void testOrField() {
		DbTableField<Integer> field1 = new DbTableField<>("field1", null);
		DbTableField<Integer> field2 = new DbTableField<>("field2", null);
		DbTableField<Integer> field3 = new DbTableField<>("field3", null);

		SqlCondition condition1 = new SqlCondition(field1, SqlConditionOperator.EQ, field2);
		SqlCondition condition2 = new SqlCondition(field1, SqlConditionOperator.EQ, field3);

		SqlCondition result = condition1.or(condition2);

		String expected = "field1 = field2 OR field1 = field3";
		assertEquals(expected, result.getSql());
	}

	@Test
	void testAndNested() {
		DbTableField<Integer> field1 = new DbTableField<>("field1", null);
		DbTableField<Integer> field2 = new DbTableField<>("field2", null);
		DbTableField<Integer> field3 = new DbTableField<>("field3", null);
		DbTableField<Integer> field4 = new DbTableField<>("field4", null);

		SqlCondition condition1 = new SqlCondition(field1, SqlConditionOperator.EQ, 1);
		SqlCondition condition2 = new SqlCondition(field2, SqlConditionOperator.EQ, 2);
		SqlCondition condition3 = new SqlCondition(field3, SqlConditionOperator.EQ, 3);
		SqlCondition condition4 = new SqlCondition(field4, SqlConditionOperator.EQ, 4);

		SqlCondition result = condition1.and(condition2.and(condition3.and(condition4)));

		String expected = "field1 = ? AND field2 = ? AND (field3 = ? AND field4 = ?)";
		List<Object> expectedValues = List.of(1, 2, 3, 4);
		assertEquals(expected, result.getSql());
		assertEquals(expectedValues, result.getComparedValues());
	}

	@Test
	void testOrNested() {
		DbTableField<Integer> field1 = new DbTableField<>("field1", null);
		DbTableField<Integer> field2 = new DbTableField<>("field2", null);
		DbTableField<Integer> field3 = new DbTableField<>("field3", null);
		DbTableField<Integer> field4 = new DbTableField<>("field4", null);

		SqlCondition condition1 = new SqlCondition(field1, SqlConditionOperator.EQ, 1);
		SqlCondition condition2 = new SqlCondition(field2, SqlConditionOperator.EQ, 2);
		SqlCondition condition3 = new SqlCondition(field3, SqlConditionOperator.EQ, 3);
		SqlCondition condition4 = new SqlCondition(field4, SqlConditionOperator.EQ, 4);

		SqlCondition result = condition1.or(condition2.or(condition3.or(condition4)));

		String expected = "field1 = ? OR field2 = ? OR (field3 = ? OR field4 = ?)";
		List<Object> expectedValues = List.of(1, 2, 3, 4);
		assertEquals(expected, result.getSql());
		assertEquals(expectedValues, result.getComparedValues());
	}

	@Test
	void testAndOR() {
		DbTableField<Integer> field1 = new DbTableField<>("field1", null);
		DbTableField<Integer> field2 = new DbTableField<>("field2", null);
		DbTableField<Integer> field3 = new DbTableField<>("field3", null);
		DbTableField<Integer> field4 = new DbTableField<>("field4", null);

		SqlCondition condition1 = new SqlCondition(field1, SqlConditionOperator.EQ, 1);
		SqlCondition condition2 = new SqlCondition(field2, SqlConditionOperator.EQ, 2);
		SqlCondition condition3 = new SqlCondition(field3, SqlConditionOperator.EQ, 3);
		SqlCondition condition4 = new SqlCondition(field4, SqlConditionOperator.EQ, 4);

		SqlCondition result = condition1.and(condition2.or(condition3)).and(condition4);

		String expected = "field1 = ? AND (field2 = ? OR field3 = ?) AND field4 = ?";
		List<Object> expectedValues = List.of(1, 2, 3, 4);
		assertEquals(expected, result.getSql());
		assertEquals(expectedValues, result.getComparedValues());
	}
}