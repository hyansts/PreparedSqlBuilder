package com.github.hyansts.preparedsqlbuilder.query.impl;

import java.util.List;

import com.github.hyansts.preparedsqlbuilder.db.impl.BaseDbTable;
import com.github.hyansts.preparedsqlbuilder.db.impl.DbTableField;
import com.github.hyansts.preparedsqlbuilder.query.SqlQuery;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SqlQueryBuilderTest {

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

}