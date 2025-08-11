package com.github.hyansts.preparedsqlbuilder.db.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.hyansts.preparedsqlbuilder.db.DbEntity;
import com.github.hyansts.preparedsqlbuilder.db.DbField;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldExtractor;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldValue;
import com.github.hyansts.preparedsqlbuilder.query.PreparedSql;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MappedDbTableTest {

	private static class Entity implements DbEntity {

		static class Table extends MappedDbTable<Entity, Integer, Table> {
			public final DbTableField<Integer> id = new DbTableField<>("id", this, Integer.class);
			public final DbTableField<Integer> field = new DbTableField<>("field", this, Integer.class);
			public final DbTableField<Integer> field2 = new DbTableField<>("field2", this, Integer.class);

			public Table() { super("table", Entity::new); }

			@Override
			public void mapToTable(DbFieldMapping<Entity> fieldMapping) {
				fieldMapping
						.mapPrimaryKey(this.id, Entity::getId, Entity::setId)
						.mapField(this.field, Entity::getField, Entity::setField)
						.mapField(this.field2, Entity::getField2, Entity::setField2);
			}
		}

		private Integer id;
		private Integer field;
		private Integer field2;

		public Entity(Integer id) {
			this.id = id;
		}
		public Entity(Integer id, Integer field, Integer field2) {
			this.id = id;
			this.field = field;
			this.field2 = field2;
		}

		public Integer getId() { return id; }
		public void setId(Integer id) { this.id = id; }
		public Integer getField() { return field; }
		public void setField(Integer field) { this.field = field; }
		public Integer getField2() { return field2; }
		public void setField2(Integer field2) { this.field2 = field2; }
	}

	@Test
	public void testSelectByIdQuery() {
		var mapping = new Entity.Table().getMapping();
		PreparedSql query = mapping.selectByIdQuery(1);
		assertEquals("SELECT * FROM table WHERE id = ?", query.getSql());
		assertEquals(List.of(1), query.getValues());
	}

	@Test
	public void testSelectAllQuery() {
		var mapping = new Entity.Table().getMapping();
		PreparedSql query = mapping.selectAllQuery();
		assertEquals("SELECT * FROM table", query.getSql());
		assertEquals(Collections.EMPTY_LIST, query.getValues());
	}

	@Test
	public void testSelectQuery() {
		var mapping = new Entity.Table().getMapping();
		PreparedSql query = mapping.selectQuery((q, tb) -> q.where(tb.field.eq(2)).limit(1));
		assertEquals("SELECT * FROM table WHERE field = ? LIMIT ?", query.getSql());
		assertEquals(List.of(2, 1), query.getValues());
	}

	@Test
	public void testSelectCountQuery() {
		var mapping = new Entity.Table().getMapping();
		PreparedSql query = mapping.selectCountQuery((q, tb) -> q.where(tb.field.eq(2)).limit(1));
		assertEquals("SELECT COUNT(*) FROM table WHERE field = ? LIMIT ?", query.getSql());
		assertEquals(List.of(2, 1), query.getValues());
	}

	@Test
	public void testInsertQuery() {
		Entity entity = new Entity(1, 2, 3);
		var mapping = new Entity.Table().getMapping();
		PreparedSql query = mapping.insertQuery(entity);
		assertEquals("INSERT INTO table (id, field, field2) VALUES (?, ?, ?)", query.getSql());
		assertEquals(List.of(1, 2, 3), query.getValues());
	}

	@Test
	public void testInsertQueryConsumer() {
		var mapping = new Entity.Table().getMapping();
		PreparedSql query = mapping.insertQuery(
				(q, tb) ->
						q.values(tb.id.value(1), tb.field.value(2), tb.field2.value(3)));
		assertEquals("INSERT INTO table (id, field, field2) VALUES (?, ?, ?)", query.getSql());
		assertEquals(List.of(1, 2, 3), query.getValues());
	}

	@Test
	public void testUpdateQuery() {
		Entity entity = new Entity(1, 2, 3);
		var mapping = new Entity.Table().getMapping();
		PreparedSql query = mapping.updateQuery(entity);
		assertEquals("UPDATE table SET field = ?, field2 = ? WHERE id = ?", query.getSql());
		assertEquals(List.of(2, 3, 1), query.getValues());
	}

	@Test
	public void testUpdateQueryCustom() {
		var mapping = new Entity.Table().getMapping();
		PreparedSql query = mapping.updateQuery(
				(q, tb) ->
						q.set(tb.field.value(2), tb.field2.value(3))
						 .where(tb.id.ge(1).and(tb.id.le(10))));
		assertEquals("UPDATE table SET field = ?, field2 = ? WHERE id >= ? AND id <= ?", query.getSql());
		assertEquals(List.of(2, 3, 1, 10), query.getValues());
	}

	@Test
	public void testDeleteQuery() {
		var mapping = new Entity.Table().getMapping();
		PreparedSql query = mapping.deleteQuery(1);
		assertEquals("DELETE FROM table WHERE id = ?", query.getSql());
		assertEquals(List.of(1), query.getValues());
	}

	@Test
	public void testDeleteQueryCustom() {
		var mapping = new Entity.Table().getMapping();
		PreparedSql query = mapping.deleteQuery((q, tb) -> q.where(tb.id.notIn(List.of(1, 2, 3))));
		assertEquals("DELETE FROM table WHERE id NOT IN (?, ?, ?)", query.getSql());
		assertEquals(List.of(1, 2, 3), query.getValues());
	}

	@Test
	public void testMapToEntity() {
		var mapping = new Entity.Table().getMapping();

		Map<String, Integer> values = Map.of("id", 1, "field", 2, "field2", 3);

		DbFieldExtractor extractor = new DbFieldExtractor() {
			@Override
			public <T> T extract(DbField<T> field) {
				return field.getType().cast(values.get(field.getLabel()));
			}
		};

		Entity entity = mapping.mapToEntity(extractor);

		assertNotNull(entity);
		assertEquals(1, entity.getId());
		assertEquals(2, entity.getField());
		assertEquals(3, entity.getField2());
	}

	@Test
	public void testExtractNonPrimaryKeyValues() {
		Entity entity = new Entity(1, 2, 3);
		var mapping = new Entity.Table().getMapping();
		DbFieldValue<?>[] values = mapping.getFieldMapping().getNonPrimaryKeyValues(entity);
		assertEquals(2, values.length, "Expected 2 fields");
		assertEquals(2, values[0].getValue(), "Expected field value 2");
		assertEquals(3, values[1].getValue(), "Expected field value 3");
		assertEquals("field", values[0].getFieldName(), "Expected field name 'field'");
		assertEquals("field2", values[1].getFieldName(), "Expected field name 'field2'");
	}

	@Test
	public void testExtractPrimaryKeyValues() {
		Entity entity = new Entity(1, 2, 3);
		var mapping = new Entity.Table().getMapping();
		DbFieldValue<?>[] values = mapping.getFieldMapping().getPrimaryKeyValues(entity);
		assertEquals(1, values.length, "Expected 1 primary key");
		assertEquals(1, values[0].getValue(), "Expected primary key value 1");
		assertEquals("id", values[0].getFieldName(), "Expected field name 'id'");
	}

	@Test
	public void testNoPrimaryKeyMappedError() {

		class NoPkTable extends MappedDbTable<Entity, Integer, NoPkTable> {

			final DbTableField<Integer> id = new DbTableField<>("id", this, Integer.class);
			final DbTableField<Integer> field = new DbTableField<>("field", this, Integer.class);
			final DbTableField<Integer> field2 = new DbTableField<>("field2", this, Integer.class);

			public NoPkTable() {
				super("noPkTable", Entity::new);
			}

			@Override
			public void mapToTable(DbFieldMapping<Entity> fieldMapping) {
				fieldMapping.mapField(this.id, Entity::getId, Entity::setId)
							.mapField(this.field, Entity::getField, Entity::setField)
							.mapField(this.field2, Entity::getField2, Entity::setField2);
			}
		}

		Entity entity = new Entity(1, 2, 3);
		var mapping = new NoPkTable().getMapping();
		Exception e = assertThrows(IllegalStateException.class, () -> {
			mapping.getFieldMapping().getPrimaryKeyCondition(entity);
		});
		assertEquals("Primary key not mapped", e.getMessage());
	}

}