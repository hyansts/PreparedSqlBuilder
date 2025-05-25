package com.github.hyansts.preparedsqlbuilder.db.impl;

import com.github.hyansts.preparedsqlbuilder.db.DbEntity;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldValue;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MappedDbTableTest {

	private static class Entity implements DbEntity {

		static class Table extends MappedDbTable<Entity> {
			public final DbTableField<Integer> id = new DbTableField<>("id", this, Integer.class);
			public final DbTableField<Integer> field = new DbTableField<>("field", this, Integer.class);

			public Table() { super("table", Entity::new); }

			@Override
			public void mapToTable(DbFieldMapping<Entity> fieldMapping) {
				fieldMapping
						.mapPrimaryKey(this.id, Entity::getId, Entity::setId)
						.mapField(this.field, Entity::getField, Entity::setField);
			}
		}

		public Entity() { }

		public Entity(Integer id, Integer field) {
			this.id = id;
			this.field = field;
		}

		private Integer id;
		private Integer field;

		public Integer getId() { return id; }
		public void setId(Integer id) { this.id = id; }
		public Integer getField() { return field; }
		public void setField(Integer field) { this.field = field; }

	}

	@Test
	public void testExtractFieldValues() {
		Entity entity = new Entity(1, 2);
		Entity.Table tb = new Entity.Table();
		DbTableMapping<Entity> mapping = tb.getMapping();
		DbFieldValue<?>[] values = mapping.extractFieldValues(entity);
		assertEquals(1, values.length, "Expected 1 field");
		assertEquals(2, values[0].getValue(), "Expected field value 2");
	}

	@Test
	public void testExtractPrimaryKeyValues() {
		Entity entity = new Entity(1, 2);
		Entity.Table tb = new Entity.Table();
		DbTableMapping<Entity> mapping = tb.getMapping();
		DbFieldValue<?>[] values = mapping.extractPrimaryKeyValues(entity);
		assertEquals(1, values.length, "Expected 1 primary key");
		assertEquals(1, values[0].getValue(), "Expected primary key value 1");
	}

}