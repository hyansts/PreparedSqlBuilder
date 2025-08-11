package com.github.hyansts.preparedsqlbuilder.query.impl;

import java.util.List;

import com.github.hyansts.preparedsqlbuilder.db.impl.BaseDbTable;
import com.github.hyansts.preparedsqlbuilder.db.impl.DbTableField;
import com.github.hyansts.preparedsqlbuilder.query.SqlBatchQuery;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SqlBatchBuilderTest {

	private static class EmployeesDbTable extends BaseDbTable {

		public final DbTableField<Integer> id = new DbTableField<>("id", this, Integer.class);
		public final DbTableField<String> name = new DbTableField<>("name", this, String.class);
		public final DbTableField<Integer> age = new DbTableField<>("age", this, Integer.class);

		public EmployeesDbTable() { super("employees"); }
	}

	@Test
	public void testBatchInsert() {
		EmployeesDbTable tb = new EmployeesDbTable();

		SqlBatchQuery query = SqlQueryFactory.createQuery().batchInsert(
				(q) ->
						q.insertInto(tb)
						 .values(tb.id.value(null),
								 tb.name.value(null),
								 tb.age.value(null)));

		final Object[] values1 = {1, "John", 20};
		final Object[] values2 = {2, "Jane", 25};
		final Object[] values3 = {3, "Bob", 30};

		query.addBatch(values1)
			 .addBatch(values2)
			 .addBatch(values3);

		String expected = "INSERT INTO employees (id, name, age) VALUES (?, ?, ?)";
		assertEquals(expected, query.getSql());
		assertEquals(List.of(values1, values2, values3), query.getValuesBatch());
	}

	@Test
	public void testBatchUpdate() {
		EmployeesDbTable tb = new EmployeesDbTable();

		SqlBatchQuery query = SqlQueryFactory.createQuery().batchUpdate(
				(q) ->
						q.update(tb)
						 .set(tb.name.value(null),
								 tb.age.value(null))
						 .where(tb.id.eq((Integer) null)));

		final Object[] values1 = {"John", 20, 1};
		final Object[] values2 = {"Jane", 25, 2};
		final Object[] values3 = {"Bob", 30, 3};

		query.addBatch(values1)
			 .addBatch(values2)
			 .addBatch(values3);

		String expected = "UPDATE employees SET name = ?, age = ? WHERE id = ?";
		assertEquals(expected, query.getSql());
		assertEquals(List.of(values1, values2, values3), query.getValuesBatch());
	}

	@Test
	public void testBatchDelete() {
		EmployeesDbTable tb = new EmployeesDbTable();

		SqlBatchQuery query = SqlQueryFactory.createQuery().batchDelete(
				(q) ->
						q.deleteFrom(tb)
						 .where(tb.name.eq((String) null)
									   .and(tb.age.eq((Integer) null))));

		final Object[] values1 = {"John", 20};
		final Object[] values2 = {"Jane", 25};

		query.addBatch(values1)
			 .addBatch(values2);

		String expected = "DELETE FROM employees WHERE name = ? AND age = ?";
		assertEquals(expected, query.getSql());
		assertEquals(List.of(values1, values2), query.getValuesBatch());
	}

}