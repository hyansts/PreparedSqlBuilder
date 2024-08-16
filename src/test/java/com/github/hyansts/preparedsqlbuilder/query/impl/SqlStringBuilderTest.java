package com.github.hyansts.preparedsqlbuilder.query.impl;

import com.github.hyansts.preparedsqlbuilder.sql.SqlAggregator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SqlStringBuilderTest {

	@Test
	public void testSelectWhereClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		query.select("id")
			 .from("employees")
			 .where("name = 'John Doe'");

		String expected = "SELECT id FROM employees WHERE name = 'John Doe'";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testSelectDistinctClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		query.selectDistinct("id")
			 .from("employees")
			 .where("name = 'John Doe'");

		String expected = "SELECT DISTINCT id FROM employees WHERE name = 'John Doe'";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testSelectCountClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		query.selectCount("id")
			 .from("employees")
			 .where("name = 'John Doe'");

		String expected = "SELECT COUNT(id) FROM employees WHERE name = 'John Doe'";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testSelectCountNoParamClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		query.selectCount()
			 .from("employees")
			 .where("name = 'John Doe'");

		String expected = "SELECT COUNT(*) FROM employees WHERE name = 'John Doe'";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testSingleColumnUpdateClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		query.update("employees")
			 .set("name", "John Doe")
			 .where("id = 1");

		String expected = "UPDATE employees SET name = 'John Doe' WHERE id = 1";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testMultipleColumnUpdateClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		query.update("employees")
			 .set("name", "John Doe")
			 .set("age", 30)
			 .set("is_active", true)
			 .where("id = 1");

		String expected = "UPDATE employees SET name = 'John Doe', age = 30, is_active = true WHERE id = 1";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testDeleteClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		query.deleteFrom("employees")
			 .where("id = 1");

		String expected = "DELETE FROM employees WHERE id = 1";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testInsertIntoClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		query.insertInto("employees", "name", "age", "is_active")
			 .values("John Doe", 30, true);

		String expected = "INSERT INTO employees (name, age, is_active) VALUES ('John Doe', 30, true)";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testInnerJoinClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		query.select("*")
			 .from("employees as e")
			 .innerJoin("department as d")
			 .on("e.dep_id = d.id");

		String expected = "SELECT * FROM employees as e INNER JOIN department as d ON e.dep_id = d.id";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testLeftJoinClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		query.select("*")
			 .from("employees as e")
			 .leftJoin("department as d")
			 .on("e.dep_id = d.id");

		String expected = "SELECT * FROM employees as e LEFT JOIN department as d ON e.dep_id = d.id";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testRightJoinClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		query.select("*")
			 .from("employees as e")
			 .rightJoin("department as d")
			 .on("e.dep_id = d.id");

		String expected = "SELECT * FROM employees as e RIGHT JOIN department as d ON e.dep_id = d.id";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testFullJoinClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		query.select("*")
			 .from("employees as e")
			 .fullJoin("department as d")
			 .on("e.dep_id = d.id");

		String expected = "SELECT * FROM employees as e FULL JOIN department as d ON e.dep_id = d.id";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testCrossJoinClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		query.select("*")
			 .from("employees as e")
			 .crossJoin("department as d");

		String expected = "SELECT * FROM employees as e CROSS JOIN department as d";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testGroupByClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		query.select("role", "salary", SqlAggregator.count("*"))
			 .from("employees")
			 .groupBy("role", "salary");

		String expected = "SELECT role, salary, COUNT(*) FROM employees GROUP BY role, salary";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testHavingClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		query.select("role", "salary", SqlAggregator.count("*"))
			 .from("employees")
			 .groupBy("role", "salary")
			 .having("salary > 5000");

		String expected = "SELECT role, salary, COUNT(*) FROM employees GROUP BY role, salary HAVING salary > 5000";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testLimitClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		query.select("*")
			 .from("employees")
			 .limit(1);

		String expected = "SELECT * FROM employees LIMIT 1";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testLimitOffsetClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		query.select("*")
			 .from("employees")
			 .limit(1)
			 .offset(10);

		String expected = "SELECT * FROM employees LIMIT 1 OFFSET 10";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testOrderByClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		query.select("role", "salary")
			 .from("employees")
			 .orderBy("salary DESC", "role ASC");

		String expected = "SELECT role, salary FROM employees ORDER BY salary DESC, role ASC";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testUnionClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		SqlStringBuilder query2 = new SqlStringBuilder();
		query.select("*")
			 .from("employees")
			 .union(query2.select("*")
						  .from("department").getSql());

		String expected = "SELECT * FROM employees UNION SELECT * FROM department";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testUnionAllClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		SqlStringBuilder query2 = new SqlStringBuilder();
		query.select("*")
			 .from("employees")
			 .unionAll(query2.select("*")
							 .from("department").getSql());

		String expected = "SELECT * FROM employees UNION ALL SELECT * FROM department";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testExceptClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		SqlStringBuilder query2 = new SqlStringBuilder();
		query.select("*")
			 .from("employees")
			 .except(query2.select("*")
						   .from("department").getSql());

		String expected = "SELECT * FROM employees EXCEPT SELECT * FROM department";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testExceptAllClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		SqlStringBuilder query2 = new SqlStringBuilder();
		query.select("*")
			 .from("employees")
			 .exceptAll(query2.select("*")
							  .from("department").getSql());

		String expected = "SELECT * FROM employees EXCEPT ALL SELECT * FROM department";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testIntersectClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		SqlStringBuilder query2 = new SqlStringBuilder();
		query.select("*")
			 .from("employees")
			 .intersect(query2.select("*")
							  .from("department").getSql());

		String expected = "SELECT * FROM employees INTERSECT SELECT * FROM department";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testIntersectAllClause() {

		SqlStringBuilder query = new SqlStringBuilder();
		SqlStringBuilder query2 = new SqlStringBuilder();
		query.select("*")
			 .from("employees")
			 .intersectAll(query2.select("*")
								 .from("department").getSql());

		String expected = "SELECT * FROM employees INTERSECT ALL SELECT * FROM department";
		assertEquals(expected, query.getSql());
	}

	@Test
	public void testFullQuery() {

		SqlStringBuilder query = new SqlStringBuilder();
		SqlStringBuilder query2 = new SqlStringBuilder();

		query.select("COUNT(emp.id) AS emp_count", "dep.title AS dep_name")
			 .from("employees AS emp")
			 .innerJoin("department AS dep")
			 .on("emp.department_id = dep.id")
			 .where("emp.is_active = true")
			 .and("(emp.age > 30")
			 .or("dep.admin_id = emp.id)")
			 .groupBy("dep.title")
			 .having("dep.title LIKE 'A%'")
			 .limit(10)
			 .offset(3)
			 .union(query2.select("MAX(uemp.id)", "udep.title AS dep_name")
						  .from("employees AS uemp")
						  .innerJoin("department AS udep")
						  .on("uemp.department_id = udep.id")
						  .where("udep.id > 10")
						  .groupBy("udep.title").getSql())
			 .orderBy("emp_count DESC");

		String expectedSQL = "SELECT COUNT(emp.id) AS emp_count, dep.title AS dep_name " +
									 "FROM employees AS emp " +
									 "INNER JOIN department AS dep " +
									 "ON emp.department_id = dep.id " +
									 "WHERE emp.is_active = true AND (emp.age > 30 OR dep.admin_id = emp.id) " +
									 "GROUP BY dep.title HAVING dep.title LIKE 'A%' " +
									 "LIMIT 10 OFFSET 3 " +
									 "UNION " +
									 "SELECT MAX(uemp.id), udep.title AS dep_name " +
									 "FROM employees AS uemp " +
									 "INNER JOIN department AS udep " +
									 "ON uemp.department_id = udep.id " +
									 "WHERE udep.id > 10 " +
									 "GROUP BY udep.title " +
									 "ORDER BY emp_count DESC";

		assertEquals(expectedSQL, query.getSql());
	}
}