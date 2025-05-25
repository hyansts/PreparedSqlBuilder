package com.github.hyansts.preparedsqlbuilder.db.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import com.github.hyansts.preparedsqlbuilder.db.DbEntity;

public abstract class MappedDbTable<E extends DbEntity> extends BaseDbTable {

	private final DbTableMapping<E> mapping;
	private final AtomicBoolean initialized = new AtomicBoolean(false);

	protected MappedDbTable(String name, Supplier<E> entitySupplier) {
		super(name);
		this.mapping = new DbTableMapping<>(this, entitySupplier, new DbFieldMapping<>());
	}

	protected MappedDbTable(String name, String prefix, Supplier<E> entitySupplier) {
		super(name, prefix);
		this.mapping = new DbTableMapping<>(this, entitySupplier, new DbFieldMapping<>());
	}

	public abstract void mapToTable(DbFieldMapping<E> fieldMapping);

	public DbTableMapping<E> getMapping() {
		if (initialized.compareAndSet(false, true)) {
			this.mapToTable(mapping.getFieldMapping());
		}
		return mapping;
	}

}