package com.github.hyansts.preparedsqlbuilder.db.impl;

import java.util.function.Supplier;

import com.github.hyansts.preparedsqlbuilder.db.DbEntity;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldExtractor;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldValue;
import com.github.hyansts.preparedsqlbuilder.db.DbTable;
import com.github.hyansts.preparedsqlbuilder.sql.SqlCondition;

public class DbTableMapping<E extends DbEntity> {

	private final DbTable table;
	private final Supplier<E> entitySupplier;
	private final DbFieldMapping<E> fieldMapping;

	public DbTableMapping(DbTable table, Supplier<E> entitySupplier, DbFieldMapping<E> fieldMapping) {
		this.table = table;
		this.entitySupplier = entitySupplier;
		this.fieldMapping = fieldMapping;
	}

	public DbFieldValue<?>[] extractFieldValues(E entity) {
		DbFieldValue<?>[] fieldValues = new DbFieldValue[this.fieldMapping.getFieldAccessors().size()];
		int i = 0;
		for (DbFieldAccessor<E, ?> accessor : this.fieldMapping.getFieldAccessors()) {
			fieldValues[i++] = accessor.getFieldValue(entity);
		}
		return fieldValues;
	}

	public DbFieldValue<?>[] extractPrimaryKeyValues(E entity) {
		DbFieldValue<?>[] fieldValues = new DbFieldValue[this.fieldMapping.getPrimaryKeyAccessors().size()];
		int i = 0;
		for (DbFieldAccessor<E, ?> accessor : this.fieldMapping.getPrimaryKeyAccessors()) {
			fieldValues[i++] = accessor.getFieldValue(entity);
		}
		return fieldValues;
	}

	public SqlCondition buildPrimaryKeyCondition(E entity) {
		if (this.fieldMapping.getPrimaryKeyAccessors().isEmpty()) {
			throw new IllegalStateException("Primary key not mapped");
		}
		SqlCondition condition = null;
		for (DbFieldAccessor<E, ?> accessor : this.fieldMapping.getPrimaryKeyAccessors()) {
			if (condition == null) {
				condition = accessor.getEqEntityCondition(entity);
			} else {
				condition = condition.and(accessor.getEqEntityCondition(entity));
			}
		}
		return condition;
	}

	public <T> E extractEntity(DbFieldExtractor extractor) {
		E entity = this.entitySupplier.get();
		for (DbFieldAccessor<E, ?> accessor : this.fieldMapping.getPrimaryKeyAccessors()) {
			accessor.extract(extractor, entity);
		}
		for (DbFieldAccessor<E, ?> accessor : this.fieldMapping.getFieldAccessors()) {
			accessor.extract(extractor, entity);
		}
		return entity;
	}

	public DbTable getTable() { return table; }
	public DbFieldMapping<E> getFieldMapping() { return fieldMapping; }

}