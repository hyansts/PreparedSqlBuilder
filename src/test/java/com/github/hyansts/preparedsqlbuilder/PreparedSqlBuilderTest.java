package com.github.hyansts.preparedsqlbuilder;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PreparedSqlBuilderTest {

	private static class MockDbTable extends BaseDbTable<MockDbTable> {

		public final DbTableField<Integer> ID = new DbTableField<>("id", this);
		public final DbTableField<String> NAME = new DbTableField<>("name", this);
		public final DbTableField<Integer> AGE = new DbTableField<>("age", this);
		public final DbTableField<Boolean> IS_ACTIVE = new DbTableField<>("is_active", this);

		public MockDbTable() { super("employees"); }
	}

	@Test
	public void testSingleSelectWhereClause() {

		MockDbTable tb = new MockDbTable();

		final String name = "John Doe";

		PreparedSqlBuilder buildSql = new PreparedSqlBuilder();
		buildSql.select(tb.ID)
				.from(tb)
				.where(tb.NAME.eq(name));

		String expectedSQL = "SELECT employees.id FROM employees WHERE employees.name = ?;";
		List<Object> expectedValues = List.of(name);
		assertEquals(expectedSQL, buildSql.getSql());
		assertEquals(expectedValues, buildSql.getValues());
	}

	@Test
	public void testSelectDistinctClause() {

		MockDbTable tb = new MockDbTable();

		final String name = "John Doe";

		PreparedSqlBuilder buildSql = new PreparedSqlBuilder();
		buildSql.selectDistinct(tb.ID)
				.from(tb)
				.where(tb.NAME.eq(name));

		String expectedSQL = "SELECT DISTINCT employees.id FROM employees WHERE employees.name = ?;";
		List<Object> expectedValues = List.of(name);
		assertEquals(expectedSQL, buildSql.getSql());
		assertEquals(expectedValues, buildSql.getValues());
	}

	@Test
	public void testSelectCountParamClause() {

		MockDbTable tb = new MockDbTable();

		final String name = "John Doe";

		PreparedSqlBuilder buildSql = new PreparedSqlBuilder();
		buildSql.selectCount(tb.ID)
				.from(tb)
				.where(tb.NAME.eq(name));

		String expectedSQL = "SELECT COUNT(employees.id) FROM employees WHERE employees.name = ?;";
		List<Object> expectedValues = List.of(name);
		assertEquals(expectedSQL, buildSql.getSql());
		assertEquals(expectedValues, buildSql.getValues());
	}

	@Test
	public void testSelectWhereAliasClause() {

		MockDbTable tb = new MockDbTable();

		final boolean isActive = true;
		final String name = "John Doe";
		final int age = 30;

		PreparedSqlBuilder buildSql = new PreparedSqlBuilder();
		buildSql.select(tb.ID, tb.NAME, tb.AGE)
				.from(tb.as("e"))
				.where(tb.IS_ACTIVE.eq(isActive)
								   .and(tb.NAME.eq(name))
								   .and(tb.AGE.eq(age)));

		String expectedSQL =
				"SELECT e.id, e.name, e.age FROM employees AS e WHERE e.is_active = ? AND e.name = ? AND e.age = ?;";
		List<Object> expectedValues = List.of(isActive, name, age);
		assertEquals(expectedSQL, buildSql.getSql());
		assertEquals(expectedValues, buildSql.getValues());
	}

	@Test
	public void testSingleColumnUpdateClause() {

		MockDbTable tb = new MockDbTable();

		final String name = "John Doe";
		final int id = 1;

		PreparedSqlBuilder buildSql = new PreparedSqlBuilder();
		buildSql.update(tb)
				.set(tb.NAME, name)
				.where(tb.ID.eq(id));

		String expectedSQL = "UPDATE employees SET name = ? WHERE employees.id = ?;";
		List<Object> expectedValues = List.of(name, id);
		assertEquals(expectedSQL, buildSql.getSql());
		assertEquals(expectedValues, buildSql.getValues());
	}

	@Test
	public void testMultipleColumnUpdateClause() {

		MockDbTable tb = new MockDbTable();

		final String name = "John Doe";
		final int age = 30;
		final boolean isActive = true;
		final int id = 1;

		PreparedSqlBuilder buildSql = new PreparedSqlBuilder();
		buildSql.update(tb)
				.set(tb.NAME, name)
				.set(tb.AGE, age)
				.set(tb.IS_ACTIVE, isActive)
				.where(tb.ID.eq(id));

		String expectedSQL = "UPDATE employees SET name = ?, age = ?, is_active = ? WHERE employees.id = ?;";
		List<Object> expectedValues = List.of(name, age, isActive, id);
		assertEquals(expectedSQL, buildSql.toString());
		assertEquals(expectedValues, buildSql.getValues());
	}

	@Test
	public void testUpdateNoWhereClause() {

		MockDbTable tb = new MockDbTable();

		final boolean isActive = false;

		PreparedSqlBuilder buildSql = new PreparedSqlBuilder();
		buildSql.update(tb)
				.set(tb.IS_ACTIVE, isActive);

		String expected = "UPDATE employees SET is_active = ?;";
		List<Object> expectedValues = List.of(isActive);
		assertEquals(expected, buildSql.toString());
		assertEquals(expectedValues, buildSql.getValues());
	}

	@Test
	public void testInsertClause() {

		MockDbTable tb = new MockDbTable();

		final int id = 100;
		final String name = "John Doe";
		final int age = 30;
		final boolean isActive = true;

		PreparedSqlBuilder buildSql = new PreparedSqlBuilder();
		buildSql.insertInto(tb)
				.values(tb.ID.value(id),
						tb.NAME.value(name),
						tb.AGE.value(age),
						tb.IS_ACTIVE.value(isActive));

		String expected = "INSERT INTO employees (id, name, age, is_active) VALUES (?, ?, ?, ?);";
		List<Object> expectedValues = List.of(id, name, age, isActive);
		assertEquals(expected, buildSql.toString());
		assertEquals(expectedValues, buildSql.getValues());
	}

}