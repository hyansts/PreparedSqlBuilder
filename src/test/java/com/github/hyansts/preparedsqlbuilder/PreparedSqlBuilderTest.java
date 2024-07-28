package com.github.hyansts.preparedsqlbuilder;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PreparedSqlBuilderTest {

	private static class EmployeesDbTable extends BaseDbTable<EmployeesDbTable> {

		public final DbTableField<Integer> ID = new DbTableField<>("id", this);
		public final DbTableField<String> NAME = new DbTableField<>("name", this);
		public final DbTableField<Integer> AGE = new DbTableField<>("age", this);
		public final DbTableField<Boolean> IS_ACTIVE = new DbTableField<>("is_active", this);
		public final DbTableField<Integer> DEPARTMENT_ID = new DbTableField<>("department_id", this);

		public EmployeesDbTable() { super("employees"); }
	}

	private static class DepartmentDbTable extends BaseDbTable<DepartmentDbTable> {

		public final DbTableField<Integer> ID = new DbTableField<>("id", this);
		public final DbTableField<String> TITLE = new DbTableField<>("title", this);

		public DepartmentDbTable() { super("department"); }
	}

	@Test
	public void testSingleSelectWhereClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final String name = "John Doe";

		PreparedSqlBuilder buildSql = new PreparedSqlBuilder();
		buildSql.select(tb.ID)
				.from(tb)
				.where(tb.NAME.eq(name));

		String expectedSQL = "SELECT id FROM employees WHERE name = ?;";
		List<Object> expectedValues = List.of(name);
		assertEquals(expectedSQL, buildSql.getSql());
		assertEquals(expectedValues, buildSql.getValues());
	}

	@Test
	public void testSelectDistinctClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final String name = "John Doe";

		PreparedSqlBuilder buildSql = new PreparedSqlBuilder();
		buildSql.selectDistinct(tb.ID)
				.from(tb)
				.where(tb.NAME.eq(name));

		String expectedSQL = "SELECT DISTINCT id FROM employees WHERE name = ?;";
		List<Object> expectedValues = List.of(name);
		assertEquals(expectedSQL, buildSql.getSql());
		assertEquals(expectedValues, buildSql.getValues());
	}

	@Test
	public void testSelectCountClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final String name = "John Doe";

		PreparedSqlBuilder buildSql = new PreparedSqlBuilder();
		buildSql.selectCount()
				.from(tb)
				.where(tb.NAME.eq(name));

		String expectedSQL = "SELECT COUNT(*) FROM employees WHERE name = ?;";
		List<Object> expectedValues = List.of(name);
		assertEquals(expectedSQL, buildSql.getSql());
		assertEquals(expectedValues, buildSql.getValues());
	}

	@Test
	public void testSelectCountParamClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final String name = "John Doe";

		PreparedSqlBuilder buildSql = new PreparedSqlBuilder();
		buildSql.selectCount(tb.ID)
				.from(tb)
				.where(tb.NAME.eq(name));

		String expectedSQL = "SELECT COUNT(id) FROM employees WHERE name = ?;";
		List<Object> expectedValues = List.of(name);
		assertEquals(expectedSQL, buildSql.getSql());
		assertEquals(expectedValues, buildSql.getValues());
	}

	@Test
	public void testSelectWhereAliasClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

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
	public void testSelectWithExpressionClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final String expression = "CASE WHEN e.IS_ACTIVE = true THEN 'USER IS ACTIVE' ELSE 'USER IS INACTIVE' END";
		final String name = "John Doe";
		final int age = 30;

		PreparedSqlBuilder buildSql = new PreparedSqlBuilder();
		buildSql.select(expression, tb.ID, tb.NAME, tb.AGE)
				.from(tb.as("e"))
				.where(tb.NAME.eq(name)
							  .and(tb.AGE.eq(age)));

		String expectedSQL =
				"SELECT CASE WHEN e.IS_ACTIVE = true THEN 'USER IS ACTIVE' ELSE 'USER IS INACTIVE' END, " +
						"e.id, e.name, e.age FROM employees AS e WHERE e.name = ? AND e.age = ?;";
		List<Object> expectedValues = List.of(name, age);
		assertEquals(expectedSQL, buildSql.getSql());
		assertEquals(expectedValues, buildSql.getValues());
	}

	@Test
	public void testSelectDistinctWithExpressionClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final String expression = "CASE WHEN e.IS_ACTIVE = true THEN 'USER IS ACTIVE' ELSE 'USER IS INACTIVE' END";
		final String name = "John Doe";
		final int age = 30;

		PreparedSqlBuilder buildSql = new PreparedSqlBuilder();
		buildSql.selectDistinct(expression, tb.ID, tb.NAME, tb.AGE)
				.from(tb.as("e"))
				.where(tb.NAME.eq(name)
							  .and(tb.AGE.eq(age)));

		String expectedSQL =
				"SELECT DISTINCT CASE WHEN e.IS_ACTIVE = true THEN 'USER IS ACTIVE' ELSE 'USER IS INACTIVE' END, " +
						"e.id, e.name, e.age FROM employees AS e WHERE e.name = ? AND e.age = ?;";
		List<Object> expectedValues = List.of(name, age);
		assertEquals(expectedSQL, buildSql.getSql());
		assertEquals(expectedValues, buildSql.getValues());
	}

	@Test
	public void testSingleColumnUpdateClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final String name = "John Doe";
		final int id = 1;

		PreparedSqlBuilder buildSql = new PreparedSqlBuilder();
		buildSql.update(tb)
				.set(tb.NAME, name)
				.where(tb.ID.eq(id));

		String expectedSQL = "UPDATE employees SET name = ? WHERE id = ?;";
		List<Object> expectedValues = List.of(name, id);
		assertEquals(expectedSQL, buildSql.getSql());
		assertEquals(expectedValues, buildSql.getValues());
	}

	@Test
	public void testMultipleColumnUpdateClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

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

		String expectedSQL = "UPDATE employees SET name = ?, age = ?, is_active = ? WHERE id = ?;";
		List<Object> expectedValues = List.of(name, age, isActive, id);
		assertEquals(expectedSQL, buildSql.toString());
		assertEquals(expectedValues, buildSql.getValues());
	}

	@Test
	public void testUpdateNoWhereClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

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
	public void testDeleteFromClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final Integer id = 1;

		PreparedSqlBuilder buildSql = new PreparedSqlBuilder();
		buildSql.deleteFrom(tb)
				.where(tb.ID.eq(id));

		String expected = "DELETE FROM employees WHERE id = ?;";
		List<Object> expectedValues = List.of(id);
		assertEquals(expected, buildSql.toString());
		assertEquals(expectedValues, buildSql.getValues());
	}

	@Test
	public void testInsertClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

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

	@Test
	public void testInnerJoinClause() {

		EmployeesDbTable tb = new EmployeesDbTable();
		DepartmentDbTable dtb = new DepartmentDbTable();

		final String title = "Sales";

		PreparedSqlBuilder buildSql = new PreparedSqlBuilder();
		buildSql.select("*")
				.from(tb.as("e"))
				.innerJoin(dtb.as("d"))
				.on(tb.DEPARTMENT_ID.eq(dtb.ID))
				.where(dtb.TITLE.eq(title));

		String expected =
				"SELECT * FROM employees AS e INNER JOIN department AS d ON e.department_id = d.id WHERE d.title = ?;";
		List<Object> expectedValues = List.of(title);
		assertEquals(expected, buildSql.toString());
		assertEquals(expectedValues, buildSql.getValues());
	}

	@Test
	public void testLeftJoinClause() {

		EmployeesDbTable tb = new EmployeesDbTable();
		DepartmentDbTable dtb = new DepartmentDbTable();

		final String title = "Sales";

		PreparedSqlBuilder buildSql = new PreparedSqlBuilder();
		buildSql.select("*")
				.from(tb.as("e"))
				.leftJoin(dtb.as("d"))
				.on(tb.DEPARTMENT_ID.eq(dtb.ID))
				.where(dtb.TITLE.eq(title));
		String expected =
				"SELECT * FROM employees AS e LEFT JOIN department AS d ON e.department_id = d.id WHERE d.title = ?;";
		List<Object> expectedValues = List.of(title);
		assertEquals(expected, buildSql.toString());
		assertEquals(expectedValues, buildSql.getValues());
	}

	@Test
	public void testRightJoinClause() {

		EmployeesDbTable tb = new EmployeesDbTable();
		DepartmentDbTable dtb = new DepartmentDbTable();

		final String title = "Sales";

		PreparedSqlBuilder buildSql = new PreparedSqlBuilder();
		buildSql.select("*")
				.from(tb.as("e"))
				.rigtJoin(dtb.as("d"))
				.on(tb.DEPARTMENT_ID.eq(dtb.ID))
				.where(dtb.TITLE.eq(title));

		String expected =
				"SELECT * FROM employees AS e RIGHT JOIN department AS d ON e.department_id = d.id WHERE d.title = ?;";
		List<Object> expectedValues = List.of(title);
		assertEquals(expected, buildSql.toString());
		assertEquals(expectedValues, buildSql.getValues());
	}

	@Test
	public void testGroupByClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		PreparedSqlBuilder buildSql = new PreparedSqlBuilder();
		buildSql.select(tb.DEPARTMENT_ID, tb.AGE, tb.ID.count())
				.from(tb)
				.groupBy(tb.DEPARTMENT_ID, tb.AGE);

		String expected = "SELECT department_id, age, COUNT(id) FROM employees GROUP BY department_id, age;";
		assertEquals(expected, buildSql.getSql());
	}

	@Test
	public void testOrderByClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		PreparedSqlBuilder buildSql = new PreparedSqlBuilder();
		buildSql.select("*")
				.from(tb)
				.orderBy(tb.NAME.asc(), tb.AGE.desc());

		String expected = "SELECT * FROM employees ORDER BY name ASC, age DESC;";
		assertEquals(expected, buildSql.getSql());
	}

}