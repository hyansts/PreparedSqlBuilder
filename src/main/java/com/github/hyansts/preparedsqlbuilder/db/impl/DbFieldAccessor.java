package com.github.hyansts.preparedsqlbuilder.db.impl;

import java.util.function.BiConsumer;
import java.util.function.Function;

import com.github.hyansts.preparedsqlbuilder.db.DbFieldExtractor;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldValue;
import com.github.hyansts.preparedsqlbuilder.sql.SqlCondition;

public class DbFieldAccessor<E, T> {
	private final DbTableField<T> field;
	private final Function<E, T> getter;
	private final BiConsumer<E, T> setter;

	public DbFieldAccessor(DbTableField<T> field, Function<E, T> getter, BiConsumer<E, T> setter) {
		this.field = field;
		this.getter = getter;
		this.setter = setter;
	}

	public DbFieldValue<T> getFieldValue(E entity) {
		return this.field.value(this.getter.apply(entity));
	}

	public SqlCondition getEqEntityCondition(E entity) {
		return this.field.eq(this.getter.apply(entity));
	}

	public SqlCondition getEqCondition(T value) {
		return this.field.eq(value);
	}

	public void extract(DbFieldExtractor extractor, E entity) {
		T value = extractor.extract(this.field);
		this.setter.accept(entity, value);
	}

}