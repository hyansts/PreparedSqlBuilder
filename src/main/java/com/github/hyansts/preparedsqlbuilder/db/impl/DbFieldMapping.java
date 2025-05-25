package com.github.hyansts.preparedsqlbuilder.db.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.github.hyansts.preparedsqlbuilder.db.DbEntity;

public class DbFieldMapping<E extends DbEntity> {

	private final List<DbFieldAccessor<E, ?>> fieldAccessors = new ArrayList<>();
	private final List<DbFieldAccessor<E, ?>> primaryKeyAccessors = new ArrayList<>();

	public <T> DbFieldMapping<E> mapPrimaryKey(DbTableField<T> field, Function<E, T> getter, BiConsumer<E, T> setter) {
		this.primaryKeyAccessors.add(new DbFieldAccessor<>(field, getter, setter));
		return this;
	}

	public <T> DbFieldMapping<E> mapField(DbTableField<T> field, Function<E, T> getter, BiConsumer<E, T> setter) {
		this.fieldAccessors.add(new DbFieldAccessor<>(field, getter, setter));
		return this;
	}

	public List<DbFieldAccessor<E, ?>> getFieldAccessors() { return fieldAccessors; }
	public List<DbFieldAccessor<E, ?>> getPrimaryKeyAccessors() { return primaryKeyAccessors; }
}