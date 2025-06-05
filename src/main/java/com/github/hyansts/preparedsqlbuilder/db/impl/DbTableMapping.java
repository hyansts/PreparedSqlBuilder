package com.github.hyansts.preparedsqlbuilder.db.impl;

import java.util.function.Function;

import com.github.hyansts.preparedsqlbuilder.db.DbEntity;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldExtractor;
import com.github.hyansts.preparedsqlbuilder.db.DbTable;
import com.github.hyansts.preparedsqlbuilder.query.PreparedSql;
import com.github.hyansts.preparedsqlbuilder.query.impl.SqlQueryFactory;

public class DbTableMapping<E extends DbEntity, ID> {

	private final DbTable table;
	private final Function<ID, E> entityFromId;
	private final DbFieldMapping<E> fieldMapping;

	public DbTableMapping(DbTable table, Function<ID, E> entityFromId, DbFieldMapping<E> fieldMapping) {
		this.table = table;
		this.entityFromId = entityFromId;
		this.fieldMapping = fieldMapping;
	}

	public E mapToEntity(DbFieldExtractor extractor) {
		E entity = this.entityFromId.apply(null);
		this.fieldMapping.populateEntity(entity, extractor);
		return entity;
	}

	public PreparedSql selectByIdQuery(ID id) {
		E entity = this.entityFromId.apply(id);
		return SqlQueryFactory.createQuery().select().from(table).where(fieldMapping.getPrimaryKeyCondition(entity));
	}

	public PreparedSql selectAllQuery() {
		return SqlQueryFactory.createQuery().select().from(table);
	}

	public PreparedSql insertQuery(E entity) {
		return SqlQueryFactory.createQuery()
							  .insertInto(table)
							  .values(fieldMapping.getAllFieldValues(entity));
	}

	public PreparedSql updateQuery(E entity) {
		return SqlQueryFactory.createQuery()
							  .update(table)
							  .set(fieldMapping.getNonPrimaryKeyValues(entity))
							  .where(fieldMapping.getPrimaryKeyCondition(entity));
	}

	public PreparedSql deleteQuery(ID id) {
		E entity = this.entityFromId.apply(id);
		return SqlQueryFactory.createQuery().deleteFrom(table).where(fieldMapping.getPrimaryKeyCondition(entity));
	}

	public DbTable getTable() { return table; }
	public DbFieldMapping<E> getFieldMapping() { return fieldMapping; }

}