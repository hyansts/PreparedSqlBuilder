package com.github.hyansts.preparedsqlbuilder;

import com.github.hyansts.preparedsqlbuilder.db.impl.SqlStringBuilder;
import com.github.hyansts.preparedsqlbuilder.sql.SqlAggregator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SqlStringBuilderTest {

	@Test
	public void testSelectWhereClause() {
		SqlStringBuilder buildSql = new SqlStringBuilder();

		buildSql.select("id")
				.from("employees")
				.where("name = 'John Doe'");

		String expected = "SELECT id FROM employees WHERE name = 'John Doe'";
		assertEquals(expected, buildSql.getSql());
	}

	@Test
	public void testSelectDistinctClause() {
		SqlStringBuilder buildSql = new SqlStringBuilder();

		buildSql.selectDistinct("id")
				.from("employees")
				.where("name = 'John Doe'");

		String expected = "SELECT DISTINCT id FROM employees WHERE name = 'John Doe'";
		assertEquals(expected, buildSql.getSql());
	}

	@Test
	public void testSelectCountClause() {
		SqlStringBuilder buildSql = new SqlStringBuilder();

		buildSql.selectCount("id")
				.from("employees")
				.where("name = 'John Doe'");

		String expected = "SELECT COUNT(id) FROM employees WHERE name = 'John Doe'";
		assertEquals(expected, buildSql.getSql());
	}

	@Test
	public void testSelectCountNoParamClause() {
		SqlStringBuilder buildSql = new SqlStringBuilder();

		buildSql.selectCount()
				.from("employees")
				.where("name = 'John Doe'");

		String expected = "SELECT COUNT(*) FROM employees WHERE name = 'John Doe'";
		assertEquals(expected, buildSql.getSql());
	}

	@Test
	public void testSingleColumnUpdateClause() {
		SqlStringBuilder buildSql = new SqlStringBuilder();

		buildSql.update("employees")
				.set("name", "John Doe")
				.where("id = 1");

		String expected = "UPDATE employees SET name = 'John Doe' WHERE id = 1";
		assertEquals(expected, buildSql.getSql());
	}

	@Test
	public void testMultipleColumnUpdateClause() {
		SqlStringBuilder buildSql = new SqlStringBuilder();

		buildSql.update("employees")
				.set("name", "John Doe")
				.set("age", 30)
				.set("is_active", true)
				.where("id = 1");

		String expected = "UPDATE employees SET name = 'John Doe', age = 30, is_active = true WHERE id = 1";
		assertEquals(expected, buildSql.getSql());
	}

	@Test
	public void testDeleteClause() {
		SqlStringBuilder buildSql = new SqlStringBuilder();

		buildSql.deleteFrom("employees")
				.where("id = 1");

		String expected = "DELETE FROM employees WHERE id = 1";
		assertEquals(expected, buildSql.getSql());
	}

	@Test
	public void testInsertIntoClause() {
		SqlStringBuilder buildSql = new SqlStringBuilder();

		buildSql.insertInto("employees", "name", "age", "is_active")
				.values("John Doe", 30, true);

		String expected = "INSERT INTO employees (name, age, is_active) VALUES ('John Doe', 30, true)";
		assertEquals(expected, buildSql.getSql());
	}

	@Test
	public void testInnerJoinClause() {
		SqlStringBuilder buildSql = new SqlStringBuilder();

		buildSql.select("*")
				.from("employees as e")
				.innerJoin("department as d")
				.on("e.dep_id = d.id");

		String expected = "SELECT * FROM employees as e INNER JOIN department as d ON e.dep_id = d.id";
		assertEquals(expected, buildSql.getSql());
	}

	@Test
	public void testLeftJoinClause() {
		SqlStringBuilder buildSql = new SqlStringBuilder();

		buildSql.select("*")
				.from("employees as e")
				.leftJoin("department as d")
				.on("e.dep_id = d.id");

		String expected = "SELECT * FROM employees as e LEFT JOIN department as d ON e.dep_id = d.id";
		assertEquals(expected, buildSql.getSql());
	}

	@Test
	public void testRightJoinClause() {
		SqlStringBuilder buildSql = new SqlStringBuilder();

		buildSql.select("*")
				.from("employees as e")
				.rigtJoin("department as d")
				.on("e.dep_id = d.id");

		String expected = "SELECT * FROM employees as e RIGHT JOIN department as d ON e.dep_id = d.id";
		assertEquals(expected, buildSql.getSql());
	}

	@Test
	public void testGroupByClause() {
		SqlStringBuilder buildSql = new SqlStringBuilder();

		buildSql.select("role", "salary", SqlAggregator.count("*"))
				.from("employees")
				.groupBy("role", "salary");

		String expected = "SELECT role, salary, COUNT(*) FROM employees GROUP BY role, salary";
		assertEquals(expected, buildSql.getSql());
	}

	@Test
	public void testOrderByClause() {
		SqlStringBuilder buildSql = new SqlStringBuilder();

		buildSql.select("role", "salary")
				.from("employees")
				.orderBy("salary DESC", "role ASC");

		String expected = "SELECT role, salary FROM employees ORDER BY salary DESC, role ASC";
		assertEquals(expected, buildSql.getSql());
	}
}