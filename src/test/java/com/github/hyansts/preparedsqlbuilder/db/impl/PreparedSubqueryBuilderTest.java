package com.github.hyansts.preparedsqlbuilder.db.impl;

import org.junit.jupiter.api.Test;

class PreparedSubqueryBuilderTest {

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
	void testDerivedTableSubquery() {
		//TODO
	}

	@Test
	void testSelectWithSubquery() {
		//TODO
	}

	@Test
	void testConditionalSubquery() {
		//TODO
	}
}