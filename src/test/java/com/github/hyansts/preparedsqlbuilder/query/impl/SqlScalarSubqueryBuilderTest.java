package com.github.hyansts.preparedsqlbuilder.query.impl;

import java.util.List;

import com.github.hyansts.preparedsqlbuilder.db.impl.BaseDbTable;
import com.github.hyansts.preparedsqlbuilder.db.impl.DbTableField;
import com.github.hyansts.preparedsqlbuilder.query.SqlQuery;
import com.github.hyansts.preparedsqlbuilder.query.SqlScalarSubquery;
import com.github.hyansts.preparedsqlbuilder.query.SqlSubquery;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SqlScalarSubqueryBuilderTest {

	private static class EmployeesDbTable extends BaseDbTable {

		public final DbTableField<Integer> id = new DbTableField<>("id", this, Integer.class);
		public final DbTableField<Integer> age = new DbTableField<>("age", this, Integer.class);

		public EmployeesDbTable() { super("employees"); }
	}

	private static class DepartmentDbTable extends BaseDbTable {

		public final DbTableField<Integer> id = new DbTableField<>("id", this, Integer.class);
		public final DbTableField<String> title = new DbTableField<>("title", this, String.class);
		public final DbTableField<Integer> admin_id = new DbTableField<>("admin_id", this, Integer.class);

		public DepartmentDbTable() { super("department"); }
	}

	@Test
	public void testSelectWithSubquery() {

		EmployeesDbTable emp = new EmployeesDbTable();
		DepartmentDbTable dep = new DepartmentDbTable();

		final int dep_id = 10;
		final int emp_id = 20;

		SqlQuery query = SqlQueryFactory.createQuery();
		SqlScalarSubquery<Long> subquery = SqlQueryFactory.createScalarSubquery();

		query.select(emp.id,
					 subquery.select(dep.admin_id.max().as("max_adm_id"))
							 .from(dep)
							 .where(dep.id.le(dep_id))
							 .getQuery())
			 .from(emp)
			 .where(emp.id.eq(emp_id));

		String expected = "SELECT id, " +
								  "(SELECT MAX(admin_id) AS max_adm_id FROM department WHERE id <= ?) " +
								  "FROM employees " +
								  "WHERE id = ?";

		List<Object> expectedValues = List.of(dep_id, emp_id);
		assertEquals(expected, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testOrderWithSubquery() {

		EmployeesDbTable emp = new EmployeesDbTable();
		DepartmentDbTable dep = new DepartmentDbTable();

		final int dep_id = 10;
		final int age = 20;

		SqlQuery query = SqlQueryFactory.createQuery();
		SqlScalarSubquery<Long> subquery = SqlQueryFactory.createScalarSubquery();

		query.select(emp.id,
					 subquery.select(dep.admin_id.max().as("max_adm_id"))
							 .from(dep)
							 .where(dep.id.le(dep_id))
							 .getQuery().as("max_id"))
			 .from(emp)
			 .where(emp.age.eq(age))
			 .orderBy(subquery.asc());

		String expected = "SELECT id, " +
								  "(SELECT MAX(admin_id) AS max_adm_id FROM department WHERE id <= ?) AS max_id " +
								  "FROM employees " +
								  "WHERE age = ? " +
								  "ORDER BY max_id ASC";

		List<Object> expectedValues = List.of(dep_id, age);
		assertEquals(expected, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testConditionalSubquery() {

		EmployeesDbTable emp = new EmployeesDbTable();
		DepartmentDbTable dep = new DepartmentDbTable();

		final int dep_id = 10;

		SqlQuery query = SqlQueryFactory.createQuery();
		SqlScalarSubquery<Integer> subquery = SqlQueryFactory.createScalarSubquery();
		query.select(emp.id)
			 .from(emp)
			 .where(emp.id.eq(subquery.select(dep.admin_id.max().as("max_id"))
									  .from(dep)
									  .where(dep.id.le(dep_id))
									  .getQuery()));

		String expected = "SELECT id " +
								  "FROM employees " +
								  "WHERE id = (SELECT MAX(admin_id) AS max_id FROM department WHERE id <= ?)";

		List<Object> expectedValues = List.of(dep_id);
		assertEquals(expected, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testNestedConditionalSubquery() {

		EmployeesDbTable emp = new EmployeesDbTable();
		DepartmentDbTable dep = new DepartmentDbTable();

		final int dep_id = 10;
		final int dt_id = 5;

		SqlQuery query = SqlQueryFactory.createQuery();
		SqlSubquery derivedTable = SqlQueryFactory.createSubquery();
		SqlScalarSubquery<Integer> subquery = SqlQueryFactory.createScalarSubquery();

		query.select(derivedTable.getField(emp.id))
			 .from(derivedTable.select(emp.id)
							   .from(emp)
							   .where(subquery.select(dep.admin_id.max().as("max_id"))
											  .from(dep)
											  .where(dep.id.le(dep_id))
											  .getQuery().eq(emp.age))
							   .getQuery().as("dt"))
			 .where(derivedTable.getField(emp.id).eq(dt_id));

		String expected = "SELECT dt.id " +
								  "FROM (" +
								  "SELECT id " +
								  "FROM employees " +
								  "WHERE (SELECT MAX(admin_id) AS max_id FROM department WHERE id <= ?) = age" +
								  ") AS dt " +
								  "WHERE dt.id = ?";

		List<Object> expectedValues = List.of(dep_id, dt_id);
		assertEquals(expected, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testExistsConditionalSubquery() {

		EmployeesDbTable emp = new EmployeesDbTable();
		DepartmentDbTable dep = new DepartmentDbTable();

		final int dep_id = 10;

		SqlQuery query = SqlQueryFactory.createQuery();
		SqlScalarSubquery<Boolean> subquery = SqlQueryFactory.createScalarSubquery();
		query.select(emp.id)
			 .from(emp)
			 .where(subquery.exists(s -> s
												 .select("1")
												 .from(dep)
												 .where(dep.id.le(dep_id))));

		String expected = "SELECT id " +
								  "FROM employees " +
								  "WHERE EXISTS (SELECT 1 FROM department WHERE id <= ?)";

		List<Object> expectedValues = List.of(dep_id);
		assertEquals(expected, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testNotExistsConditionalSubquery() {

		EmployeesDbTable emp = new EmployeesDbTable();
		DepartmentDbTable dep = new DepartmentDbTable();

		final int dep_id = 10;

		SqlQuery query = SqlQueryFactory.createQuery();
		SqlScalarSubquery<Boolean> subquery = SqlQueryFactory.createScalarSubquery();
		query.select(emp.id)
			 .from(emp)
			 .where(subquery.notExists(s -> s
													.select("1")
													.from(dep)
													.where(dep.id.le(dep_id))));

		String expected = "SELECT id " +
								  "FROM employees " +
								  "WHERE NOT EXISTS (SELECT 1 FROM department WHERE id <= ?)";

		List<Object> expectedValues = List.of(dep_id);
		assertEquals(expected, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

}