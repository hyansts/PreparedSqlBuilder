package com.github.hyansts.preparedsqlbuilder.db.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.github.hyansts.preparedsqlbuilder.db.DbEntity;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldExtractor;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldValue;
import com.github.hyansts.preparedsqlbuilder.sql.SqlCondition;

public class DbFieldMapping<E extends DbEntity> {

	public enum FieldMappingType {PRIMARY_KEY, FIELD}

	private record Entry<E>(DbFieldAccessor<E, ?> accessor, FieldMappingType type) { }

	private final List<Entry<E>> mapping = new ArrayList<>();

	public <T> DbFieldMapping<E> mapPrimaryKey(DbTableField<T> field, Function<E, T> getter, BiConsumer<E, T> setter) {
		this.mapping.add(new Entry<>(new DbFieldAccessor<>(field, getter, setter), FieldMappingType.PRIMARY_KEY));
		return this;
	}

	public <T> DbFieldMapping<E> mapField(DbTableField<T> field, Function<E, T> getter, BiConsumer<E, T> setter) {
		this.mapping.add(new Entry<>(new DbFieldAccessor<>(field, getter, setter), FieldMappingType.FIELD));
		return this;
	}

	public E populateEntity(E entity, DbFieldExtractor extractor) {
		for (Entry<E> entry : this.mapping) {
			entry.accessor.extract(extractor, entity);
		}
		return entity;
	}

	public DbFieldValue<?>[] getPrimaryKeyValues(E entity) {
		return this.mapping.stream()
						   .filter(entry -> entry.type == FieldMappingType.PRIMARY_KEY)
						   .map(entry -> entry.accessor.getFieldValue(entity))
						   .toArray(DbFieldValue[]::new);
	}

	public DbFieldValue<?>[] getNonPrimaryKeyValues(E entity) {
		return this.mapping.stream()
						   .filter(entry -> entry.type != FieldMappingType.PRIMARY_KEY)
						   .map(entry -> entry.accessor.getFieldValue(entity))
						   .toArray(DbFieldValue[]::new);
	}

	public DbFieldValue<?>[] getAllFieldValues(E entity) {
		return this.mapping.stream()
						   .map(entry -> entry.accessor.getFieldValue(entity))
						   .toArray(DbFieldValue[]::new);
	}

	public SqlCondition getPrimaryKeyCondition(E entity) {
		SqlCondition condition = null;
		for (Entry<E> entry : this.mapping) {
			if (entry.type == FieldMappingType.PRIMARY_KEY) {
				if (condition == null) {
					condition = entry.accessor.getEqEntityCondition(entity);
				} else {
					condition = condition.and(entry.accessor.getEqEntityCondition(entity));
				}
			}
		}
		if (condition == null) {
			throw new IllegalStateException("Primary key not mapped");
		}
		return condition;
	}

}