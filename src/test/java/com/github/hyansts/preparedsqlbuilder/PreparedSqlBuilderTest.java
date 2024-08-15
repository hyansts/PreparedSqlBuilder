package com.github.hyansts.preparedsqlbuilder;

import java.util.List;

import com.github.hyansts.preparedsqlbuilder.db.DbFieldLike;
import com.github.hyansts.preparedsqlbuilder.db.impl.BaseDbTable;
import com.github.hyansts.preparedsqlbuilder.db.impl.DbTableField;
import com.github.hyansts.preparedsqlbuilder.db.impl.SqlQueryFactory;
import com.github.hyansts.preparedsqlbuilder.query.SqlQuery;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PreparedSqlBuilderTest {

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
		public final DbTableField<Integer> ADMIN_ID = new DbTableField<>("admin_id", this);

		public DepartmentDbTable() { super("department"); }
	}

	@Test
	public void testSingleSelectWhereClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final String name = "John Doe";

		SqlQuery query = SqlQueryFactory.createQuery();
		query.select(tb.ID)
			 .from(tb)
			 .where(tb.NAME.eq(name));

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
		query.selectDistinct(tb.ID)
			 .from(tb)
			 .where(tb.NAME.eq(name));

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
			 .where(tb.NAME.eq(name));

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
		query.selectCount(tb.ID)
			 .from(tb)
			 .where(tb.NAME.eq(name));

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
		query.select(tb.ID, tb.NAME, tb.AGE)
			 .from(tb.as("e"))
			 .where(tb.IS_ACTIVE.eq(isActive)
								.and(tb.NAME.eq(name))
								.and(tb.AGE.eq(age)));

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
		query.select(expression, tb.ID, tb.NAME, tb.AGE)
			 .from(tb.as("e"))
			 .where(tb.NAME.eq(name)
						   .and(tb.AGE.eq(age)));

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
		query.selectDistinct(expression, tb.ID, tb.NAME, tb.AGE)
			 .from(tb.as("e"))
			 .where(tb.NAME.eq(name)
						   .and(tb.AGE.eq(age)));

		String expectedSQL =
				"SELECT DISTINCT CASE WHEN e.IS_ACTIVE = true THEN 'USER IS ACTIVE' ELSE 'USER IS INACTIVE' END, " +
						"e.id, e.name, e.age FROM employees AS e WHERE e.name = ? AND e.age = ?";
		List<Object> expectedValues = List.of(name, age);
		assertEquals(expectedSQL, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testSingleColumnUpdateClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final String name = "John Doe";
		final int id = 1;

		SqlQuery query = SqlQueryFactory.createQuery();
		query.update(tb)
			 .set(tb.NAME.value(name))
			 .where(tb.ID.eq(id));

		String expectedSQL = "UPDATE employees SET name = ? WHERE id = ?";
		List<Object> expectedValues = List.of(name, id);
		assertEquals(expectedSQL, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testMultipleColumnUpdateClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final String name = "John Doe";
		final int age = 30;
		final boolean isActive = true;
		final int id = 1;

		SqlQuery query = SqlQueryFactory.createQuery();
		query.update(tb)
			 .set(tb.NAME.value(name))
			 .set(tb.AGE.value(age))
			 .set(tb.IS_ACTIVE.value(isActive))
			 .where(tb.ID.eq(id));

		String expectedSQL = "UPDATE employees SET name = ?, age = ?, is_active = ? WHERE id = ?";
		List<Object> expectedValues = List.of(name, age, isActive, id);
		assertEquals(expectedSQL, query.toString());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testUpdateNoWhereClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final boolean isActive = false;

		SqlQuery query = SqlQueryFactory.createQuery();
		query.update(tb)
			 .set(tb.IS_ACTIVE.value(isActive));

		String expected = "UPDATE employees SET is_active = ?";
		List<Object> expectedValues = List.of(isActive);
		assertEquals(expected, query.toString());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testDeleteFromClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final Integer id = 1;

		SqlQuery query = SqlQueryFactory.createQuery();
		query.deleteFrom(tb)
			 .where(tb.ID.eq(id));

		String expected = "DELETE FROM employees WHERE id = ?";
		List<Object> expectedValues = List.of(id);
		assertEquals(expected, query.toString());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testInsertClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final int id = 100;
		final String name = "John Doe";
		final int age = 30;
		final boolean isActive = true;

		SqlQuery query = SqlQueryFactory.createQuery();
		query.insertInto(tb)
			 .values(tb.ID.value(id),
					 tb.NAME.value(name),
					 tb.AGE.value(age),
					 tb.IS_ACTIVE.value(isActive));

		String expected = "INSERT INTO employees (id, name, age, is_active) VALUES (?, ?, ?, ?)";
		List<Object> expectedValues = List.of(id, name, age, isActive);
		assertEquals(expected, query.toString());
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
			 .on(tb.DEPARTMENT_ID.eq(dtb.ID))
			 .where(dtb.TITLE.eq(title));

		String expected =
				"SELECT * FROM employees AS e INNER JOIN department AS d ON e.department_id = d.id WHERE d.title = ?";
		List<Object> expectedValues = List.of(title);
		assertEquals(expected, query.toString());
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
			 .on(tb.DEPARTMENT_ID.eq(dtb.ID))
			 .where(dtb.TITLE.eq(title));
		String expected =
				"SELECT * FROM employees AS e LEFT JOIN department AS d ON e.department_id = d.id WHERE d.title = ?";
		List<Object> expectedValues = List.of(title);
		assertEquals(expected, query.toString());
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
			 .on(tb.DEPARTMENT_ID.eq(dtb.ID))
			 .where(dtb.TITLE.eq(title));

		String expected =
				"SELECT * FROM employees AS e RIGHT JOIN department AS d ON e.department_id = d.id WHERE d.title = ?";
		List<Object> expectedValues = List.of(title);
		assertEquals(expected, query.toString());
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
			 .on(tb.DEPARTMENT_ID.eq(dtb.ID))
			 .where(dtb.TITLE.eq(title));

		String expected =
				"SELECT * FROM employees AS e FULL JOIN department AS d ON e.department_id = d.id WHERE d.title = ?";
		List<Object> expectedValues = List.of(title);
		assertEquals(expected, query.toString());
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
			 .where(dtb.TITLE.eq(title));

		String expected =
				"SELECT * FROM employees AS e CROSS JOIN department AS d WHERE d.title = ?";
		List<Object> expectedValues = List.of(title);
		assertEquals(expected, query.toString());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testGroupByClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		SqlQuery query = SqlQueryFactory.createQuery();
		query.select(tb.DEPARTMENT_ID, tb.AGE, tb.ID.count())
			 .from(tb)
			 .groupBy(tb.DEPARTMENT_ID, tb.AGE);

		String expected = "SELECT department_id, age, COUNT(id) FROM employees GROUP BY department_id, age";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testHavingClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final int age = 30;

		SqlQuery query = SqlQueryFactory.createQuery();
		query.select(tb.DEPARTMENT_ID, tb.AGE, tb.ID.count())
			 .from(tb)
			 .groupBy(tb.DEPARTMENT_ID, tb.AGE)
			 .having(tb.AGE.gt(age));

		List<Object> expectedValues = List.of(age);
		String expected =
				"SELECT department_id, age, COUNT(id) FROM employees GROUP BY department_id, age HAVING age > ?";
		assertEquals(expected, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testOrderByClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		SqlQuery query = SqlQueryFactory.createQuery();
		query.select()
			 .from(tb)
			 .orderBy(tb.NAME.asc(), tb.AGE.desc());

		String expected = "SELECT * FROM employees ORDER BY name ASC, age DESC";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testLimitClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		SqlQuery query = SqlQueryFactory.createQuery();
		query.select()
			 .from(tb)
			 .limit(1);

		String expected = "SELECT * FROM employees LIMIT 1";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testLimitOffsetClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		SqlQuery query = SqlQueryFactory.createQuery();
		query.select()
			 .from(tb)
			 .limit(1)
			 .offset(10);

		String expected = "SELECT * FROM employees LIMIT 1 OFFSET 10";
		assertEquals(expected, query.getSql());
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

		EmployeesDbTable etb = new EmployeesDbTable();
		DepartmentDbTable dtb = new DepartmentDbTable();

		final boolean isActive = true;
		final int age = 30;
		final String title = "A%";
		final int departmentId = 10;

		DbFieldLike emp_count = etb.ID.count().as("emp_count");

		query.select(emp_count, dtb.TITLE.as("dep_name"))
			 .from(etb.as("emp"))
			 .innerJoin(dtb.as("dep"))
			 .on(etb.DEPARTMENT_ID.eq(dtb.ID))
			 .where(etb.IS_ACTIVE.eq(isActive).and(etb.AGE.gt(age).or(dtb.ADMIN_ID.eq(etb.ID))))
			 .groupBy(dtb.TITLE)
			 .having(dtb.TITLE.like(title))
			 .limit(10)
			 .offset(3)
			 .union(unionQuery.select(etb.ID.max(), dtb.TITLE.as("dep_name"))
							  .from(etb.as("uemp"))
							  .innerJoin(dtb.as("udep"))
							  .on(etb.DEPARTMENT_ID.eq(dtb.ID))
							  .where(dtb.ID.gt(departmentId))
							  .groupBy(dtb.TITLE))
			 .orderBy(emp_count.desc());

		String expectedSQL = "SELECT COUNT(emp.id) AS emp_count, dep.title AS dep_name " +
									 "FROM employees AS emp " +
									 "INNER JOIN department AS dep " +
									 "ON emp.department_id = dep.id " +
									 "WHERE emp.is_active = ? AND (emp.age > ? OR dep.admin_id = emp.id) " +
									 "GROUP BY dep.title HAVING dep.title LIKE ? " +
									 "LIMIT 10 OFFSET 3 " +
									 "UNION " +
									 "SELECT MAX(uemp.id), udep.title AS dep_name " +
									 "FROM employees AS uemp " +
									 "INNER JOIN department AS udep " +
									 "ON uemp.department_id = udep.id " +
									 "WHERE udep.id > ? " +
									 "GROUP BY udep.title " +
									 "ORDER BY emp_count DESC";

		assertEquals(expectedSQL, query.getSql());
		assertEquals(List.of(isActive, age, title, departmentId), query.getValues());
	}
}