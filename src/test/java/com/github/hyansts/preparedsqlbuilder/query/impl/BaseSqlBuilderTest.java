package com.github.hyansts.preparedsqlbuilder.query.impl;

import java.util.List;

import com.github.hyansts.preparedsqlbuilder.db.impl.BaseDbTable;
import com.github.hyansts.preparedsqlbuilder.db.impl.DbAggregateField;
import com.github.hyansts.preparedsqlbuilder.db.impl.DbTableField;
import com.github.hyansts.preparedsqlbuilder.query.SqlQuery;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseSqlBuilderTest {

	private static class EmployeesDbTable extends BaseDbTable<EmployeesDbTable> {

		public final DbTableField<Integer> id = new DbTableField<>("id", this);
		public final DbTableField<String> name = new DbTableField<>("name", this);
		public final DbTableField<Integer> age = new DbTableField<>("age", this);
		public final DbTableField<Boolean> is_active = new DbTableField<>("is_active", this);
		public final DbTableField<Integer> department_id = new DbTableField<>("department_id", this);

		public EmployeesDbTable() { super("employees"); }
	}

	private static class DepartmentDbTable extends BaseDbTable<DepartmentDbTable> {

		public final DbTableField<Integer> id = new DbTableField<>("id", this);
		public final DbTableField<String> title = new DbTableField<>("title", this);
		public final DbTableField<Integer> admin_id = new DbTableField<>("admin_id", this);

		public DepartmentDbTable() { super("department"); }
	}

	@Test
	public void testSingleSelectWhereClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final String name = "John Doe";

		SqlQuery query = SqlQueryFactory.createQuery();
		query.select(tb.id)
			 .from(tb)
			 .where(tb.name.eq(name));

		String expectedSQL = "SELECT id FROM employees WHERE name = ?";
		List<Object> expectedValues = List.of(name);
		assertEquals(expectedSQL, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testSelectDistinctClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final String name = "John Doe";

		SqlQuery query = SqlQueryFactory.createQuery();
		query.selectDistinct(tb.id)
			 .from(tb)
			 .where(tb.name.eq(name));

		String expectedSQL = "SELECT DISTINCT id FROM employees WHERE name = ?";
		List<Object> expectedValues = List.of(name);
		assertEquals(expectedSQL, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testSelectCountClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final String name = "John Doe";

		SqlQuery query = SqlQueryFactory.createQuery();
		query.selectCount()
			 .from(tb)
			 .where(tb.name.eq(name));

		String expectedSQL = "SELECT COUNT(*) FROM employees WHERE name = ?";
		List<Object> expectedValues = List.of(name);
		assertEquals(expectedSQL, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testSelectCountParamClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final String name = "John Doe";

		SqlQuery query = SqlQueryFactory.createQuery();
		query.selectCount(tb.id)
			 .from(tb)
			 .where(tb.name.eq(name));

		String expectedSQL = "SELECT COUNT(id) FROM employees WHERE name = ?";
		List<Object> expectedValues = List.of(name);
		assertEquals(expectedSQL, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testSelectWhereAliasClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final boolean isActive = true;
		final String name = "John Doe";
		final int age = 30;

		SqlQuery query = SqlQueryFactory.createQuery();
		query.select(tb.id, tb.name, tb.age)
			 .from(tb.as("e"))
			 .where(tb.is_active.eq(isActive)
								.and(tb.name.eq(name))
								.and(tb.age.eq(age)));

		String expectedSQL =
				"SELECT e.id, e.name, e.age FROM employees AS e WHERE e.is_active = ? AND e.name = ? AND e.age = ?";
		List<Object> expectedValues = List.of(isActive, name, age);
		assertEquals(expectedSQL, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testSelectWithExpressionClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final String expression = "CASE WHEN e.IS_ACTIVE = true THEN 'USER IS ACTIVE' ELSE 'USER IS INACTIVE' END";
		final String name = "John Doe";
		final int age = 30;

		SqlQuery query = SqlQueryFactory.createQuery();
		query.select(expression, tb.id, tb.name, tb.age)
			 .from(tb.as("e"))
			 .where(tb.name.eq(name)
						   .and(tb.age.eq(age)));

		String expectedSQL =
				"SELECT CASE WHEN e.IS_ACTIVE = true THEN 'USER IS ACTIVE' ELSE 'USER IS INACTIVE' END, " +
						"e.id, e.name, e.age FROM employees AS e WHERE e.name = ? AND e.age = ?";
		List<Object> expectedValues = List.of(name, age);
		assertEquals(expectedSQL, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testSelectDistinctWithExpressionClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final String expression = "CASE WHEN e.IS_ACTIVE = true THEN 'USER IS ACTIVE' ELSE 'USER IS INACTIVE' END";
		final String name = "John Doe";
		final int age = 30;

		SqlQuery query = SqlQueryFactory.createQuery();
		query.selectDistinct(expression, tb.id, tb.name, tb.age)
			 .from(tb.as("e"))
			 .where(tb.name.eq(name)
						   .and(tb.age.eq(age)));

		String expectedSQL =
				"SELECT DISTINCT CASE WHEN e.IS_ACTIVE = true THEN 'USER IS ACTIVE' ELSE 'USER IS INACTIVE' END, " +
						"e.id, e.name, e.age FROM employees AS e WHERE e.name = ? AND e.age = ?";
		List<Object> expectedValues = List.of(name, age);
		assertEquals(expectedSQL, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testInnerJoinClause() {

		EmployeesDbTable tb = new EmployeesDbTable();
		DepartmentDbTable dtb = new DepartmentDbTable();

		final String title = "Sales";

		SqlQuery query = SqlQueryFactory.createQuery();
		query.select()
			 .from(tb.as("e"))
			 .innerJoin(dtb.as("d"))
			 .on(tb.department_id.eq(dtb.id))
			 .where(dtb.title.eq(title));

		String expected =
				"SELECT * FROM employees AS e INNER JOIN department AS d ON e.department_id = d.id WHERE d.title = ?";
		List<Object> expectedValues = List.of(title);
		assertEquals(expected, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testLeftJoinClause() {

		EmployeesDbTable tb = new EmployeesDbTable();
		DepartmentDbTable dtb = new DepartmentDbTable();

		final String title = "Sales";

		SqlQuery query = SqlQueryFactory.createQuery();
		query.select()
			 .from(tb.as("e"))
			 .leftJoin(dtb.as("d"))
			 .on(tb.department_id.eq(dtb.id))
			 .where(dtb.title.eq(title));
		String expected =
				"SELECT * FROM employees AS e LEFT JOIN department AS d ON e.department_id = d.id WHERE d.title = ?";
		List<Object> expectedValues = List.of(title);
		assertEquals(expected, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testRightJoinClause() {

		EmployeesDbTable tb = new EmployeesDbTable();
		DepartmentDbTable dtb = new DepartmentDbTable();

		final String title = "Sales";

		SqlQuery query = SqlQueryFactory.createQuery();
		query.select()
			 .from(tb.as("e"))
			 .rightJoin(dtb.as("d"))
			 .on(tb.department_id.eq(dtb.id))
			 .where(dtb.title.eq(title));

		String expected =
				"SELECT * FROM employees AS e RIGHT JOIN department AS d ON e.department_id = d.id WHERE d.title = ?";
		List<Object> expectedValues = List.of(title);
		assertEquals(expected, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testFullJoinClause() {

		EmployeesDbTable tb = new EmployeesDbTable();
		DepartmentDbTable dtb = new DepartmentDbTable();

		final String title = "Sales";

		SqlQuery query = SqlQueryFactory.createQuery();
		query.select()
			 .from(tb.as("e"))
			 .fullJoin(dtb.as("d"))
			 .on(tb.department_id.eq(dtb.id))
			 .where(dtb.title.eq(title));

		String expected =
				"SELECT * FROM employees AS e FULL JOIN department AS d ON e.department_id = d.id WHERE d.title = ?";
		List<Object> expectedValues = List.of(title);
		assertEquals(expected, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testCrossJoinClause() {

		EmployeesDbTable tb = new EmployeesDbTable();
		DepartmentDbTable dtb = new DepartmentDbTable();

		final String title = "Sales";

		SqlQuery query = SqlQueryFactory.createQuery();
		query.select()
			 .from(tb.as("e"))
			 .crossJoin(dtb.as("d"))
			 .where(dtb.title.eq(title));

		String expected =
				"SELECT * FROM employees AS e CROSS JOIN department AS d WHERE d.title = ?";
		List<Object> expectedValues = List.of(title);
		assertEquals(expected, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testFieldFromWrongTable() {

		EmployeesDbTable tb = new EmployeesDbTable();
		DepartmentDbTable dtb = new DepartmentDbTable();

		SqlQuery query = SqlQueryFactory.createQuery();

		query.select(dtb.title).from(tb.as("e"));

		Exception exception = assertThrows(IllegalStateException.class, query::getSql);

		assertTrue(exception.getMessage().contains("Selected field was not found in any table in the FROM or JOIN clauses"));
	}

	@Test
	public void testGroupByClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		SqlQuery query = SqlQueryFactory.createQuery();
		query.select(tb.department_id, tb.age, tb.id.count().as("count_id"))
			 .from(tb)
			 .groupBy(tb.department_id, tb.age);

		String expected = "SELECT department_id, age, COUNT(id) AS count_id FROM employees GROUP BY department_id, age";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testHavingClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final int age = 30;

		SqlQuery query = SqlQueryFactory.createQuery();
		query.select(tb.department_id, tb.age, tb.id.count().as("count_id"))
			 .from(tb)
			 .groupBy(tb.department_id, tb.age)
			 .having(tb.age.gt(age));

		List<Object> expectedValues = List.of(age);
		String expected =
				"SELECT department_id, age, COUNT(id) AS count_id FROM employees GROUP BY department_id, age HAVING age > ?";
		assertEquals(expected, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testOrderByClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		SqlQuery query = SqlQueryFactory.createQuery();
		query.select()
			 .from(tb)
			 .orderBy(tb.name.asc(), tb.age.desc());

		String expected = "SELECT * FROM employees ORDER BY name ASC, age DESC";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testLimitClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final int limit = 1;

		SqlQuery query = SqlQueryFactory.createQuery();
		query.select()
			 .from(tb)
			 .limit(limit);

		String expected = "SELECT * FROM employees LIMIT ?";
		List<Object> expectedValues = List.of(limit);
		assertEquals(expected, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testLimitOffsetClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final int limit = 1;
		final int offset = 10;

		SqlQuery query = SqlQueryFactory.createQuery();
		query.select()
			 .from(tb)
			 .limit(limit)
			 .offset(offset);

		String expected = "SELECT * FROM employees LIMIT ? OFFSET ?";
		List<Object> expectedValues = List.of(limit, offset);
		assertEquals(expected, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testUnionClause() {

		EmployeesDbTable tb = new EmployeesDbTable();
		DepartmentDbTable dtb = new DepartmentDbTable();

		SqlQuery query = SqlQueryFactory.createQuery();
		SqlQuery query2 = SqlQueryFactory.createQuery();
		query.select()
			 .from(tb)
			 .union(query2.select()
						  .from(dtb));

		String expected = "SELECT * FROM employees UNION SELECT * FROM department";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testUnionAllClause() {

		EmployeesDbTable tb = new EmployeesDbTable();
		DepartmentDbTable dtb = new DepartmentDbTable();

		SqlQuery query = SqlQueryFactory.createQuery();
		SqlQuery query2 = SqlQueryFactory.createQuery();
		query.select()
			 .from(tb)
			 .unionAll(query2.select()
							 .from(dtb));

		String expected = "SELECT * FROM employees UNION ALL SELECT * FROM department";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testExceptClause() {

		EmployeesDbTable tb = new EmployeesDbTable();
		DepartmentDbTable dtb = new DepartmentDbTable();

		SqlQuery query = SqlQueryFactory.createQuery();
		SqlQuery query2 = SqlQueryFactory.createQuery();
		query.select()
			 .from(tb)
			 .except(query2.select()
						   .from(dtb));

		String expected = "SELECT * FROM employees EXCEPT SELECT * FROM department";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testExceptAllClause() {

		EmployeesDbTable tb = new EmployeesDbTable();
		DepartmentDbTable dtb = new DepartmentDbTable();

		SqlQuery query = SqlQueryFactory.createQuery();
		SqlQuery query2 = SqlQueryFactory.createQuery();
		query.select()
			 .from(tb)
			 .exceptAll(query2.select()
							  .from(dtb));

		String expected = "SELECT * FROM employees EXCEPT ALL SELECT * FROM department";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testIntersectClause() {

		EmployeesDbTable tb = new EmployeesDbTable();
		DepartmentDbTable dtb = new DepartmentDbTable();

		SqlQuery query = SqlQueryFactory.createQuery();
		SqlQuery query2 = SqlQueryFactory.createQuery();
		query.select()
			 .from(tb)
			 .intersect(query2.select()
							  .from(dtb));

		String expected = "SELECT * FROM employees INTERSECT SELECT * FROM department";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testIntersectAllClause() {

		EmployeesDbTable tb = new EmployeesDbTable();
		DepartmentDbTable dtb = new DepartmentDbTable();

		SqlQuery query = SqlQueryFactory.createQuery();
		SqlQuery query2 = SqlQueryFactory.createQuery();
		query.select()
			 .from(tb)
			 .intersectAll(query2.select()
								 .from(dtb));

		String expected = "SELECT * FROM employees INTERSECT ALL SELECT * FROM department";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testFullQuery() {

		SqlQuery query = SqlQueryFactory.createQuery();
		SqlQuery unionQuery = SqlQueryFactory.createQuery();

		EmployeesDbTable emp = new EmployeesDbTable();
		DepartmentDbTable dep = new DepartmentDbTable();

		EmployeesDbTable uemp = new EmployeesDbTable();
		DepartmentDbTable udep = new DepartmentDbTable();

		final boolean isActive = true;
		final int age = 30;
		final String title = "A%";
		final int departmentId = 10;
		final int limit = 10;
		final int offset = 3;

		DbAggregateField<Long> emp_count = emp.id.count();

		query.select(emp_count.as("emp_count"), dep.title.as("dep_name"))
			 .from(emp.as("emp"))
			 .innerJoin(dep.as("dep"))
			 .on(emp.department_id.eq(dep.id))
			 .where(emp.is_active.eq(isActive).and(emp.age.gt(age).or(dep.admin_id.eq(emp.id))))
			 .groupBy(dep.title)
			 .having(dep.title.like(title))
			 .limit(limit)
			 .offset(offset)
			 .union(unionQuery.select(uemp.id.max().as("max_id"), udep.title.as("dep_name"))
							  .from(uemp.as("uemp"))
							  .innerJoin(udep.as("udep"))
							  .on(uemp.department_id.eq(udep.id))
							  .where(udep.id.gt(departmentId))
							  .groupBy(udep.title))
			 .orderBy(emp_count.desc());

		String expectedSQL = "SELECT COUNT(emp.id) AS emp_count, dep.title AS dep_name " +
									 "FROM employees AS emp " +
									 "INNER JOIN department AS dep " +
									 "ON emp.department_id = dep.id " +
									 "WHERE emp.is_active = ? AND (emp.age > ? OR dep.admin_id = emp.id) " +
									 "GROUP BY dep.title HAVING dep.title LIKE ? " +
									 "LIMIT ? OFFSET ? " +
									 "UNION " +
									 "SELECT MAX(uemp.id) AS max_id, udep.title AS dep_name " +
									 "FROM employees AS uemp " +
									 "INNER JOIN department AS udep " +
									 "ON uemp.department_id = udep.id " +
									 "WHERE udep.id > ? " +
									 "GROUP BY udep.title " +
									 "ORDER BY emp_count DESC";

		assertEquals(expectedSQL, query.getSql());
		assertEquals(List.of(isActive, age, title, limit, offset, departmentId), query.getValues());
	}

}