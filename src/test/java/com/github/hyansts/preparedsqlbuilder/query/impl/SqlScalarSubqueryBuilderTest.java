package com.github.hyansts.preparedsqlbuilder.query.impl;

import java.util.List;

import com.github.hyansts.preparedsqlbuilder.db.impl.BaseDbTable;
import com.github.hyansts.preparedsqlbuilder.db.impl.DbTableField;
import com.github.hyansts.preparedsqlbuilder.query.SqlQuery;
import com.github.hyansts.preparedsqlbuilder.query.SqlScalarSubquery;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SqlScalarSubqueryBuilderTest {

	private static class EmployeesDbTable extends BaseDbTable<EmployeesDbTable> {

		public final DbTableField<Integer> id = new DbTableField<>("id", this);
		public final DbTableField<Integer> age = new DbTableField<>("age", this);
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
	public void testSelectWithSubquery() {

		EmployeesDbTable emp = new EmployeesDbTable();
		DepartmentDbTable dep = new DepartmentDbTable();

		final int dep_id = 10;
		final int emp_id = 20;

		SqlQuery query = SqlQueryFactory.createQuery();
		SqlScalarSubquery<Long> subquery = SqlQueryFactory.createScalarSubquery();

		query.select(emp.id,
					 subquery.select(dep.admin_id.max().as("max_id"))
							 .from(dep)
							 .where(dep.id.le(dep_id))
							 .getQuery())
			 .from(emp)
			 .where(emp.id.eq(emp_id));

		String expected = "SELECT id, " +
								  "(SELECT MAX(admin_id) AS max_id FROM department WHERE id <= ?) " +
								  "FROM employees " +
								  "WHERE id = ?";

		List<Object> expectedValues = List.of(dep_id, emp_id);
		assertEquals(expected, query.getSql());
		assertEquals(expectedValues, query.getValues());
	}

	@Test
	public void testConditionalSubquery() {
		//TODO
	}

}