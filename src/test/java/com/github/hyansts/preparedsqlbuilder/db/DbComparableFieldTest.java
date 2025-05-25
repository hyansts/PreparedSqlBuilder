package com.github.hyansts.preparedsqlbuilder.db;

import java.util.List;

import com.github.hyansts.preparedsqlbuilder.db.impl.BaseDbTable;
import com.github.hyansts.preparedsqlbuilder.db.impl.DbTableField;
import com.github.hyansts.preparedsqlbuilder.query.SqlScalarSubquery;
import com.github.hyansts.preparedsqlbuilder.query.impl.SqlQueryFactory;
import com.github.hyansts.preparedsqlbuilder.sql.SqlCondition;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DbComparableFieldTest {

	private static class EmployeesDbTable extends BaseDbTable {

		public final DbTableField<Integer> id = new DbTableField<>("id", this, Integer.class);
		public final DbTableField<String> name = new DbTableField<>("name", this, String.class);
		public final DbTableField<Integer> age = new DbTableField<>("age", this, Integer.class);

		public EmployeesDbTable() { super("employees"); }
	}

	private static class DepartmentDbTable extends BaseDbTable {

		public final DbTableField<Integer> admin_id = new DbTableField<>("admin_id", this, Integer.class);
		public final DbTableField<String> title = new DbTableField<>("title", this, String.class);
		public final DbTableField<Integer> min_age = new DbTableField<>("min_age", this, Integer.class);
		public final DbTableField<Integer> max_age = new DbTableField<>("max_age", this, Integer.class);

		public DepartmentDbTable() { super("department"); }
	}

	@Test
	void testEq() {
		EmployeesDbTable table = new EmployeesDbTable();
		SqlCondition condition = table.name.eq("John");

		assertEquals("name = ?", condition.getSql());
		assertEquals(List.of("John"), condition.getComparedValues());
	}

	@Test
	void testEqField() {
		EmployeesDbTable employeesTable = new EmployeesDbTable();
		DepartmentDbTable departmentTable = new DepartmentDbTable();
		SqlCondition condition = employeesTable.id.eq(departmentTable.admin_id);

		assertEquals("id = admin_id", condition.getSql());
		assertEquals(List.of(), condition.getComparedValues());
	}

	@Test
	void testNe() {
		EmployeesDbTable table = new EmployeesDbTable();
		SqlCondition condition = table.name.ne("John");

		assertEquals("name <> ?", condition.getSql());
		assertEquals(List.of("John"), condition.getComparedValues());
	}

	@Test
	void testNeField() {
		EmployeesDbTable employeesTable = new EmployeesDbTable();
		DepartmentDbTable departmentTable = new DepartmentDbTable();
		SqlCondition condition = employeesTable.age.ne(departmentTable.max_age);

		assertEquals("age <> max_age", condition.getSql());
		assertEquals(List.of(), condition.getComparedValues());
	}

	@Test
	void testLt() {
		EmployeesDbTable table = new EmployeesDbTable();
		SqlCondition condition = table.age.lt(18);

		assertEquals("age < ?", condition.getSql());
		assertEquals(List.of(18), condition.getComparedValues());
	}

	@Test
	void testLtField() {
		EmployeesDbTable employeesTable = new EmployeesDbTable();
		DepartmentDbTable departmentTable = new DepartmentDbTable();
		SqlCondition condition = employeesTable.age.lt(departmentTable.max_age);

		assertEquals("age < max_age", condition.getSql());
		assertEquals(List.of(), condition.getComparedValues());
	}

	@Test
	void testGt() {
		EmployeesDbTable table = new EmployeesDbTable();
		SqlCondition condition = table.age.gt(18);

		assertEquals("age > ?", condition.getSql());
		assertEquals(List.of(18), condition.getComparedValues());
	}

	@Test
	void testGtField() {
		EmployeesDbTable employeesTable = new EmployeesDbTable();
		DepartmentDbTable departmentTable = new DepartmentDbTable();
		SqlCondition condition = employeesTable.age.gt(departmentTable.min_age);

		assertEquals("age > min_age", condition.getSql());
		assertEquals(List.of(), condition.getComparedValues());
	}

	@Test
	void testGtEq() {
		EmployeesDbTable table = new EmployeesDbTable();
		SqlCondition condition = table.age.ge(18);

		assertEquals("age >= ?", condition.getSql());
		assertEquals(List.of(18), condition.getComparedValues());
	}

	@Test
	void testGtEqField() {
		EmployeesDbTable employeesTable = new EmployeesDbTable();
		DepartmentDbTable departmentTable = new DepartmentDbTable();
		SqlCondition condition = employeesTable.age.ge(departmentTable.min_age);

		assertEquals("age >= min_age", condition.getSql());
		assertEquals(List.of(), condition.getComparedValues());
	}

	@Test
	void testLtEq() {
		EmployeesDbTable table = new EmployeesDbTable();
		SqlCondition condition = table.age.le(18);

		assertEquals("age <= ?", condition.getSql());
		assertEquals(List.of(18), condition.getComparedValues());
	}

	@Test
	void testLtEqField() {
		EmployeesDbTable employeesTable = new EmployeesDbTable();
		DepartmentDbTable departmentTable = new DepartmentDbTable();
		SqlCondition condition = employeesTable.age.le(departmentTable.max_age);

		assertEquals("age <= max_age", condition.getSql());
		assertEquals(List.of(), condition.getComparedValues());
	}

	@Test
	void testBetween() {
		EmployeesDbTable table = new EmployeesDbTable();
		SqlCondition condition = table.age.between(18, 25);

		assertEquals("age BETWEEN ? AND ?", condition.getSql());
		assertEquals(List.of(18, 25), condition.getComparedValues());
	}

	@Test
	void testBetweenFieldVal() {
		EmployeesDbTable employeesTable = new EmployeesDbTable();
		DepartmentDbTable departmentTable = new DepartmentDbTable();
		SqlCondition condition = employeesTable.age.between(departmentTable.min_age, 25);

		assertEquals("age BETWEEN min_age AND ?", condition.getSql());
		assertEquals(List.of(25), condition.getComparedValues());
	}

	@Test
	void testBetweenValField() {
		EmployeesDbTable employeesTable = new EmployeesDbTable();
		DepartmentDbTable departmentTable = new DepartmentDbTable();
		SqlCondition condition = employeesTable.age.between(25, departmentTable.max_age);

		assertEquals("age BETWEEN ? AND max_age", condition.getSql());
		assertEquals(List.of(25), condition.getComparedValues());
	}

	@Test
	void testBetweenFields() {
		EmployeesDbTable employeesTable = new EmployeesDbTable();
		DepartmentDbTable departmentTable = new DepartmentDbTable();
		SqlCondition condition = employeesTable.age.between(departmentTable.min_age, departmentTable.max_age);

		assertEquals("age BETWEEN min_age AND max_age", condition.getSql());
		assertEquals(List.of(), condition.getComparedValues());
	}

	@Test
	void testNotBetween() {
		EmployeesDbTable table = new EmployeesDbTable();
		SqlCondition condition = table.age.notBetween(18, 25);

		assertEquals("age NOT BETWEEN ? AND ?", condition.getSql());
		assertEquals(List.of(18, 25), condition.getComparedValues());
	}

	@Test
	void testNotBetweenFieldVal() {
		EmployeesDbTable employeesTable = new EmployeesDbTable();
		DepartmentDbTable departmentTable = new DepartmentDbTable();
		SqlCondition condition = employeesTable.age.notBetween(departmentTable.min_age, 25);

		assertEquals("age NOT BETWEEN min_age AND ?", condition.getSql());
		assertEquals(List.of(25), condition.getComparedValues());
	}

	@Test
	void testNotBetweenValField() {
		EmployeesDbTable employeesTable = new EmployeesDbTable();
		DepartmentDbTable departmentTable = new DepartmentDbTable();
		SqlCondition condition = employeesTable.age.notBetween(25, departmentTable.max_age);

		assertEquals("age NOT BETWEEN ? AND max_age", condition.getSql());
		assertEquals(List.of(25), condition.getComparedValues());
	}

	@Test
	void testNotBetweenFields() {
		EmployeesDbTable employeesTable = new EmployeesDbTable();
		DepartmentDbTable departmentTable = new DepartmentDbTable();
		SqlCondition condition = employeesTable.age.notBetween(departmentTable.min_age, departmentTable.max_age);

		assertEquals("age NOT BETWEEN min_age AND max_age", condition.getSql());
		assertEquals(List.of(), condition.getComparedValues());
	}

	@Test
	void testIn() {
		EmployeesDbTable table = new EmployeesDbTable();
		SqlCondition condition = table.name.in(List.of("John", "Jane"));

		assertEquals("name IN (?, ?)", condition.getSql());
		assertEquals(List.of("John", "Jane"), condition.getComparedValues());
	}

	@Test
	void testInSubquery() {
		EmployeesDbTable employeesTable = new EmployeesDbTable();
		DepartmentDbTable departmentTable = new DepartmentDbTable();
		SqlScalarSubquery<Integer> subquery = SqlQueryFactory.createScalarSubquery();

		SqlCondition condition =
				employeesTable.id.in(subquery.select(departmentTable.admin_id).from(departmentTable).getQuery());

		assertEquals("id IN (SELECT admin_id FROM department)", condition.getSql());
		assertEquals(List.of(), condition.getComparedValues());
	}

	@Test
	void testNotIn() {
		EmployeesDbTable table = new EmployeesDbTable();
		SqlCondition condition = table.name.notIn(List.of("John", "Jane"));

		assertEquals("name NOT IN (?, ?)", condition.getSql());
		assertEquals(List.of("John", "Jane"), condition.getComparedValues());
	}

	@Test
	void testNotInSubquery() {
		EmployeesDbTable employeesTable = new EmployeesDbTable();
		DepartmentDbTable departmentTable = new DepartmentDbTable();
		SqlScalarSubquery<Integer> subquery = SqlQueryFactory.createScalarSubquery();

		SqlCondition condition =
				employeesTable.id.notIn(subquery.select(departmentTable.admin_id).from(departmentTable).getQuery());

		assertEquals("id NOT IN (SELECT admin_id FROM department)", condition.getSql());
		assertEquals(List.of(), condition.getComparedValues());
	}

	@Test
	void testLike() {
		EmployeesDbTable table = new EmployeesDbTable();
		SqlCondition condition = table.name.like("%ohn%");

		assertEquals("name LIKE ?", condition.getSql());
		assertEquals(List.of("%ohn%"), condition.getComparedValues());
	}

	@Test
	void testLikeField() {
		EmployeesDbTable employeesTable = new EmployeesDbTable();
		DepartmentDbTable departmentTable = new DepartmentDbTable();
		SqlCondition condition = employeesTable.name.like(departmentTable.title);

		assertEquals("name LIKE title", condition.getSql());
		assertEquals(List.of(), condition.getComparedValues());
	}

	@Test
	void testNotLike() {
		EmployeesDbTable table = new EmployeesDbTable();
		SqlCondition condition = table.name.notLike("%ohn%");

		assertEquals("name NOT LIKE ?", condition.getSql());
		assertEquals(List.of("%ohn%"), condition.getComparedValues());
	}

	@Test
	void testNotLikeField() {
		EmployeesDbTable employeesTable = new EmployeesDbTable();
		DepartmentDbTable departmentTable = new DepartmentDbTable();
		SqlCondition condition = employeesTable.name.notLike(departmentTable.title);

		assertEquals("name NOT LIKE title", condition.getSql());
		assertEquals(List.of(), condition.getComparedValues());
	}

	@Test
	void testIsNull() {
		EmployeesDbTable table = new EmployeesDbTable();
		SqlCondition condition = table.name.isNull();

		assertEquals("name IS NULL", condition.getSql());
		assertEquals(List.of(), condition.getComparedValues());
	}

	@Test
	void testIsNotNull() {
		EmployeesDbTable table = new EmployeesDbTable();
		SqlCondition condition = table.name.isNotNull();

		assertEquals("name IS NOT NULL", condition.getSql());
		assertEquals(List.of(), condition.getComparedValues());
	}
}