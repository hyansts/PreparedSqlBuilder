package com.github.hyansts.preparedsqlbuilder.db.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import com.github.hyansts.preparedsqlbuilder.db.DbEntity;

public abstract class MappedDbTable<E extends DbEntity, ID> extends BaseDbTable {

	private final DbTableMapping<E, ID> mapping;
	private final AtomicBoolean initialized = new AtomicBoolean(false);

	protected MappedDbTable(String name, Function<ID, E> entityFromId) {
		super(name);
		this.mapping = new DbTableMapping<>(this, entityFromId, new DbFieldMapping<>());
	}

	protected MappedDbTable(String name, String prefix, Function<ID, E> entityFromId) {
		super(name, prefix);
		this.mapping = new DbTableMapping<>(this, entityFromId, new DbFieldMapping<>());
	}

	public abstract void mapToTable(DbFieldMapping<E> fieldMapping);

	public DbTableMapping<E, ID> getMapping() {
		if (initialized.compareAndSet(false, true)) {
			this.mapToTable(mapping.getFieldMapping());
		}
		return mapping;
	}

}