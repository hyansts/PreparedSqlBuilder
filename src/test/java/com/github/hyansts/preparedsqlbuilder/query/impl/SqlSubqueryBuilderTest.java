package com.github.hyansts.preparedsqlbuilder.query.impl;

import java.util.List;

import com.github.hyansts.preparedsqlbuilder.db.DbFieldLike;
import com.github.hyansts.preparedsqlbuilder.db.impl.BaseDbTable;
import com.github.hyansts.preparedsqlbuilder.db.impl.DbTableField;
import com.github.hyansts.preparedsqlbuilder.query.SqlQuery;
import com.github.hyansts.preparedsqlbuilder.query.SqlSubquery;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SqlSubqueryBuilderTest {

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
	public void testDerivedTableSubquery() {
		SqlQuery query = SqlQueryFactory.createQuery();
		SqlSubquery subquery = SqlQueryFactory.createSubquery();

		EmployeesDbTable subEmp = new EmployeesDbTable();
		DepartmentDbTable subDep = new DepartmentDbTable();
		DepartmentDbTable dep = new DepartmentDbTable();

		final int departmentId = 10;
		final String title = "A%";
		final int adminId = 1000;

		subquery.select(subEmp.id.count().as("id_count"), subEmp.department_id, subDep.title.as("dep_name"))
				.from(subEmp.as("subEmp"))
				.innerJoin(subDep.as("subDep"))
				.on(subEmp.department_id.eq(subDep.id))
				.where(subDep.id.gt(departmentId))
				.groupBy(subEmp.department_id, subDep.title)
				.having(subDep.title.like(title));

		DbFieldLike emp_count = subquery.getField(0, Integer.class);

		query.select(emp_count, dep.title, dep.admin_id.as("dep_admin"))
			 .from(subquery.as("emp"))
			 .innerJoin(dep.as("dep"))
			 .on(subquery.getField(1, Integer.class).eq(dep.id))
			 .where(dep.admin_id.ge(adminId))
			 .orderBy(emp_count.desc())
			 .limit(10)
			 .offset(3);

		String expectedSQL = "SELECT emp.id_count, dep.title, dep.admin_id AS dep_admin " +
									 "FROM ("
									 + "SELECT COUNT(subEmp.id) AS id_count, subEmp.department_id, subDep.title AS dep_name "
									 + "FROM employees AS subEmp "
									 + "INNER JOIN department AS subDep "
									 + "ON subEmp.department_id = subDep.id "
									 + "WHERE subDep.id > ? "
									 + "GROUP BY subEmp.department_id, subDep.title "
									 + "HAVING subDep.title LIKE ?) AS emp " +
									 "INNER JOIN department AS dep " +
									 "ON emp.department_id = dep.id " +
									 "WHERE dep.admin_id >= ? " +
									 "ORDER BY emp.id_count DESC " +
									 "LIMIT 10 OFFSET 3";

		assertEquals(expectedSQL, query.getSql());
		assertEquals(List.of(departmentId, title, adminId), query.getValues());
	}

	@Test
	public void testSelectWithSubquery() {
		//TODO
	}

	@Test
	public void testConditionalSubquery() {
		//TODO
	}

	@Test
	public void testCombiningSubquery() {
		//TODO union of subqueries
	}
}