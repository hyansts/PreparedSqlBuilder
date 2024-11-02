package com.github.hyansts.preparedsqlbuilder.query.impl;

import java.util.List;

import com.github.hyansts.preparedsqlbuilder.db.DbComparableField;
import com.github.hyansts.preparedsqlbuilder.db.impl.BaseDbTable;
import com.github.hyansts.preparedsqlbuilder.db.impl.DbAggregateField;
import com.github.hyansts.preparedsqlbuilder.db.impl.DbTableField;
import com.github.hyansts.preparedsqlbuilder.query.SqlQuery;
import com.github.hyansts.preparedsqlbuilder.query.SqlSubquery;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SqlSubqueryBuilderTest {

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
	public void testDerivedTableSubquery() {

		EmployeesDbTable subEmp = new EmployeesDbTable();
		DepartmentDbTable subDep = new DepartmentDbTable();
		DepartmentDbTable dep = new DepartmentDbTable();

		final int departmentId = 10;
		final String title = "A%";
		final int adminId = 1000;
		final int limit = 10;
		final int offset = 3;

		SqlQuery query = SqlQueryFactory.createQuery();
		SqlSubquery subquery = SqlQueryFactory.createSubquery();

		DbAggregateField<Long> id_count = subEmp.id.count();

		subquery.select(id_count.as("id_count"), subEmp.department_id, subDep.title.as("dep_name"))
				.from(subEmp.as("subemp"))
				.innerJoin(subDep.as("subdep"))
				.on(subEmp.department_id.eq(subDep.id))
				.where(subDep.id.gt(departmentId))
				.groupBy(subEmp.department_id, subDep.title)
				.having(subDep.title.like(title));

		DbComparableField<Long> emp_count = subquery.getField(id_count);

		query.select(emp_count, dep.title, dep.admin_id.as("dep_admin"))
			 .from(subquery.as("emp"))
			 .innerJoin(dep.as("dep"))
			 .on(subquery.getField(subEmp.department_id).eq(dep.id))
			 .where(dep.admin_id.ge(adminId))
			 .orderBy(emp_count.desc())
			 .limit(limit)
			 .offset(offset);

		String expectedSQL = "SELECT emp.id_count, dep.title, dep.admin_id AS dep_admin " +
									 "FROM ("
									 + "SELECT COUNT(subemp.id) AS id_count, subemp.department_id, subdep.title AS dep_name "
									 + "FROM employees AS subemp "
									 + "INNER JOIN department AS subdep "
									 + "ON subemp.department_id = subdep.id "
									 + "WHERE subdep.id > ? "
									 + "GROUP BY subemp.department_id, subdep.title "
									 + "HAVING subdep.title LIKE ?) AS emp " +
									 "INNER JOIN department AS dep " +
									 "ON emp.department_id = dep.id " +
									 "WHERE dep.admin_id >= ? " +
									 "ORDER BY emp.id_count DESC " +
									 "LIMIT ? OFFSET ?";

		assertEquals(expectedSQL, query.getSql());
		assertEquals(List.of(departmentId, title, adminId, limit, offset), query.getValues());
	}

	@Test
	public void testSubqueryChaining() {

		EmployeesDbTable emp = new EmployeesDbTable();
		DepartmentDbTable dep = new DepartmentDbTable();

		SqlQuery query = SqlQueryFactory.createQuery();
		SqlSubquery subquery = SqlQueryFactory.createSubquery();

		DbAggregateField<Integer> maxAge = emp.age.max();

		query.select(subquery.getField(emp.department_id), subquery.getField(maxAge), dep.title)
			 .from(subquery.select(emp.department_id, maxAge.as("max_age"))
						   .from(emp)
						   .groupBy(emp.department_id).getQuery().as("sub"))
			 .innerJoin(dep.as("dep")).on(subquery.getField(emp.department_id).eq(dep.id));

		String expectedSQL = "SELECT sub.department_id, sub.max_age, dep.title " +
									 "FROM ("
									 + "SELECT department_id, MAX(age) AS max_age "
									 + "FROM employees "
									 + "GROUP BY department_id) AS sub " +
									 "INNER JOIN department AS dep " +
									 "ON sub.department_id = dep.id";

		assertEquals(expectedSQL, query.getSql());
	}

	@Test
	public void testSubqueryChainingWithFieldAlias() {
		EmployeesDbTable emp = new EmployeesDbTable();
		DepartmentDbTable dep = new DepartmentDbTable();

		SqlQuery query = SqlQueryFactory.createQuery();
		SqlSubquery subquery = SqlQueryFactory.createSubquery();

		DbAggregateField<Integer> maxAge = emp.age.max();

		query.select(subquery.getField(emp.department_id).as("dep_id"), subquery.getField(maxAge), dep.title.as("title"))
			 .from(subquery.select(emp.department_id.as("id"), maxAge.as("max_age"))
						   .from(emp)
						   .groupBy(emp.department_id).getQuery().as("sub"))
			 .innerJoin(dep.as("dep")).on(subquery.getField(emp.department_id).eq(dep.id));

		String expectedSQL = "SELECT sub.id AS dep_id, sub.max_age, dep.title AS title " +
									 "FROM ("
									 + "SELECT department_id AS id, MAX(age) AS max_age "
									 + "FROM employees "
									 + "GROUP BY department_id) AS sub " +
									 "INNER JOIN department AS dep " +
									 "ON sub.id = dep.id";

		assertEquals(expectedSQL, query.getSql());
	}

	@Test
	public void testReferenceSubqueryWithNoAlias() {
		EmployeesDbTable emp = new EmployeesDbTable();
		DepartmentDbTable dep = new DepartmentDbTable();

		SqlQuery query = SqlQueryFactory.createQuery();
		SqlSubquery subquery = SqlQueryFactory.createSubquery();

		DbAggregateField<Integer> maxAge = emp.age.max();

		Exception exception = assertThrows(IllegalStateException.class, () -> {
			query.select(subquery.getField(emp.department_id), subquery.getField(maxAge), dep.title)
				 .from(subquery.select(emp.department_id, maxAge.as("max_age"))
							   .from(emp)
							   .groupBy(emp.department_id)
							   .getQuery())
				 .innerJoin(dep.as("dep")).on(subquery.getField(emp.department_id).eq(dep.id));
		});

		assertTrue(exception.getMessage().contains("Derived table subquery must have an alias"));
	}

	@Test
	public void testSubqueryAggregateFieldWithNoAlias() {
		EmployeesDbTable emp = new EmployeesDbTable();
		DepartmentDbTable dep = new DepartmentDbTable();

		SqlQuery query = SqlQueryFactory.createQuery();
		SqlSubquery subquery = SqlQueryFactory.createSubquery();

		// Default alias = aggregator_fieldName (max_age)
		DbAggregateField<Integer> maxAge = emp.age.max();

		query.select(subquery.getField(emp.department_id).as("dep_id"), subquery.getField(maxAge), dep.title.as("title"))
			 .from(subquery.select(emp.department_id.as("id"), maxAge)
						   .from(emp.as("emp"))
						   .groupBy(emp.department_id).getQuery().as("sub"))
			 .innerJoin(dep.as("dep")).on(subquery.getField(emp.department_id).eq(dep.id));

		String expectedSQL = "SELECT sub.id AS dep_id, sub.max_age, dep.title AS title " +
									 "FROM ("
									 + "SELECT emp.department_id AS id, MAX(emp.age) AS max_age "
									 + "FROM employees AS emp "
									 + "GROUP BY emp.department_id) AS sub " +
									 "INNER JOIN department AS dep " +
									 "ON sub.id = dep.id";

		assertEquals(expectedSQL, query.getSql());
	}

	@Test
	public void testJoinSubquery() {

		EmployeesDbTable emp = new EmployeesDbTable();
		DepartmentDbTable dep = new DepartmentDbTable();

		SqlQuery query = SqlQueryFactory.createQuery();
		SqlSubquery subquery = SqlQueryFactory.createSubquery();

		DbAggregateField<Integer> maxAge = emp.age.max();

		query.select(subquery.getField(emp.department_id), subquery.getField(maxAge), dep.title)
			 .from(dep.as("dep"))
			 .innerJoin(subquery.select(emp.department_id, maxAge.as("max_age"))
								.from(emp)
								.groupBy(emp.department_id).getQuery().as("sub"))
			 .on(subquery.getField(emp.department_id).eq(dep.id));

		String expectedSQL = "SELECT sub.department_id, sub.max_age, dep.title " +
									 "FROM department AS dep " +
									 "INNER JOIN ("
									 + "SELECT department_id, MAX(age) AS max_age "
									 + "FROM employees "
									 + "GROUP BY department_id) AS sub " +
									 "ON sub.department_id = dep.id";

		assertEquals(expectedSQL, query.getSql());
	}

	@Test
	public void testCombiningSubqueries() {

		EmployeesDbTable emp = new EmployeesDbTable();
		EmployeesDbTable emp2 = new EmployeesDbTable();

		final int limit = 10;
		final int offset1 = 10;
		final int offset2 = 90;

		SqlSubquery subquery = SqlQueryFactory.createSubquery();
		SqlSubquery subquery2 = SqlQueryFactory.createSubquery();

		subquery.select(emp.id)
				.from(emp)
				.orderBy(emp.id.desc())
				.limit(limit)
				.offset(offset1)
				.union(subquery2.select(emp2.id)
								.from(emp2)
								.orderBy(emp2.id.desc())
								.limit(limit)
								.offset(offset2));

		String expectedSQL = "(SELECT id FROM employees ORDER BY id DESC LIMIT ? OFFSET ? " +
									 "UNION " +
									 "SELECT id FROM employees ORDER BY id DESC LIMIT ? OFFSET ?)";
		List<Object> expectedParams = List.of(limit, offset1, limit, offset2);
		assertEquals(expectedSQL, subquery.getSql());
		assertEquals(expectedParams, subquery.getValues());
	}

	@Test
	public void testCombiningMultipleSubqueries() {

		EmployeesDbTable emp = new EmployeesDbTable();
		EmployeesDbTable emp2 = new EmployeesDbTable();
		EmployeesDbTable emp3 = new EmployeesDbTable();

		SqlSubquery subquery = SqlQueryFactory.createSubquery();
		SqlSubquery subquery2 = SqlQueryFactory.createSubquery();
		SqlSubquery subquery3 = SqlQueryFactory.createSubquery();

		final int limit = 10;
		final int offset1 = 10;
		final int offset2 = 50;
		final int offset3 = 90;

		subquery.select(emp.id)
				.from(emp)
				.limit(limit)
				.offset(offset1)
				.union(subquery2.select(emp2.id)
								.from(emp2)
								.limit(limit)
								.offset(offset2))
				.union(subquery3.select(emp3.id)
								.from(emp3)
								.limit(limit)
								.offset(offset3));

		String expectedSQL = "(SELECT id FROM employees LIMIT ? OFFSET ? " +
									 "UNION " +
									 "SELECT id FROM employees LIMIT ? OFFSET ? " +
									 "UNION " +
									 "SELECT id FROM employees LIMIT ? OFFSET ?)";
		List<Object> expectedValues = List.of(limit, offset1, limit, offset2, limit, offset3);
		assertEquals(expectedSQL, subquery.getSql());
		assertEquals(expectedValues, subquery.getValues());
	}

	@Test
	public void testCombiningNestedSubqueries() {

		EmployeesDbTable emp = new EmployeesDbTable();
		EmployeesDbTable emp2 = new EmployeesDbTable();
		EmployeesDbTable emp3 = new EmployeesDbTable();

		final int limit = 10;
		final int offset1 = 10;
		final int offset2 = 50;
		final int offset3 = 90;

		SqlSubquery subquery = SqlQueryFactory.createSubquery();
		SqlSubquery subquery2 = SqlQueryFactory.createSubquery();
		SqlSubquery subquery3 = SqlQueryFactory.createSubquery();

		subquery.select(emp.id)
				.from(emp)
				.limit(limit)
				.offset(offset1)
				.union(subquery2.select(emp2.id)
								.from(emp2)
								.limit(limit)
								.offset(offset2)
								.union(subquery3.select(emp3.id)
												.from(emp3)
												.limit(limit)
												.offset(offset3)));

		String expectedSQL = "(SELECT id FROM employees LIMIT ? OFFSET ? " +
									 "UNION " +
									 "SELECT id FROM employees LIMIT ? OFFSET ? " +
									 "UNION " +
									 "SELECT id FROM employees LIMIT ? OFFSET ?)";
		List<Object> expectedValues = List.of(limit, offset1, limit, offset2, limit, offset3);
		assertEquals(expectedSQL, subquery.getSql());
		assertEquals(expectedValues, subquery.getValues());
	}

}