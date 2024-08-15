package com.github.hyansts.preparedsqlbuilder.query.impl;

import java.util.List;

import com.github.hyansts.preparedsqlbuilder.db.impl.BaseDbTable;
import com.github.hyansts.preparedsqlbuilder.db.impl.DbTableField;
import com.github.hyansts.preparedsqlbuilder.query.SqlQuery;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SqlQueryBuilderTest {

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
	public void testSingleColumnUpdateClause() {

		EmployeesDbTable tb = new EmployeesDbTable();

		final String name = "John Doe";
		final int id = 1;

		SqlQuery query = SqlQueryFactory.createQuery();
		query.update(tb)
			 .set(tb.name.value(name))
			 .where(tb.id.eq(id));

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
			 .set(tb.name.value(name))
			 .set(tb.age.value(age))
			 .set(tb.is_active.value(isActive))
			 .where(tb.id.eq(id));

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
			 .set(tb.is_active.value(isActive));

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
			 .where(tb.id.eq(id));

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
			 .values(tb.id.value(id),
					 tb.name.value(name),
					 tb.age.value(age),
					 tb.is_active.value(isActive));

		String expected = "INSERT INTO employees (id, name, age, is_active) VALUES (?, ?, ?, ?)";
		List<Object> expectedValues = List.of(id, name, age, isActive);
		assertEquals(expected, query.toString());
		assertEquals(expectedValues, query.getValues());
	}

}