package com.github.hyansts.preparedsqlbuilder.db;

public interface DbSortableField extends DbFieldLike {
	DbFieldOrder asc();

	DbFieldOrder desc();
}