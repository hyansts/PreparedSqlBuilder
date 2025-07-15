package com.github.hyansts.preparedsqlbuilder.db.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import com.github.hyansts.preparedsqlbuilder.db.DbEntity;

public abstract class MappedDbTable<E extends DbEntity, ID, T extends MappedDbTable<E, ID, T>> extends BaseDbTable {

	private final DbTableMapping<E, ID, T> mapping;
	private final AtomicBoolean initialized = new AtomicBoolean(false);

	@SuppressWarnings("unchecked")
	protected MappedDbTable(String name, Function<ID, E> entityFromId) {
		super(name);
		this.mapping = new DbTableMapping<>((T) this, entityFromId, new DbFieldMapping<>());
	}

	@SuppressWarnings("unchecked")
	protected MappedDbTable(String name, String prefix, Function<ID, E> entityFromId) {
		super(name, prefix);
		this.mapping = new DbTableMapping<>((T) this, entityFromId, new DbFieldMapping<>());
	}

	public abstract void mapToTable(DbFieldMapping<E> fieldMapping);

	public DbTableMapping<E, ID, T> getMapping() {
		if (initialized.compareAndSet(false, true)) {
			this.mapToTable(mapping.getFieldMapping());
		}
		return mapping;
	}

}