package com.github.hyansts.preparedsqlbuilder.db.impl;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.github.hyansts.preparedsqlbuilder.db.DbEntity;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldExtractor;
import com.github.hyansts.preparedsqlbuilder.query.DeleteStep;
import com.github.hyansts.preparedsqlbuilder.query.FromStep;
import com.github.hyansts.preparedsqlbuilder.query.InsertStep;
import com.github.hyansts.preparedsqlbuilder.query.PreparedSql;
import com.github.hyansts.preparedsqlbuilder.query.SqlQuery;
import com.github.hyansts.preparedsqlbuilder.query.UpdateStep;
import com.github.hyansts.preparedsqlbuilder.query.impl.SqlQueryFactory;

public class DbTableMapping<E extends DbEntity, ID, T extends MappedDbTable<E, ID, T>> {

	private final T table;
	private final Function<ID, E> entityFromId;
	private final DbFieldMapping<E> fieldMapping;

	public DbTableMapping(T table, Function<ID, E> entityFromId, DbFieldMapping<E> fieldMapping) {
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

	public PreparedSql selectQuery(BiFunction<FromStep<SqlQuery>, T, PreparedSql> query) {
		return query.apply(SqlQueryFactory.createQuery().select().from(table), table);
	}

	public PreparedSql selectCountQuery(BiFunction<FromStep<SqlQuery>, T, PreparedSql> query) {
		return query.apply(SqlQueryFactory.createQuery().selectCount().from(table), table);
	}

	public PreparedSql insertQuery(E entity) {
		return SqlQueryFactory.createQuery()
							  .insertInto(table)
							  .values(fieldMapping.getAllFieldValues(entity));
	}

	public PreparedSql insertQuery(BiFunction<InsertStep, T, PreparedSql> query) {
		return query.apply(SqlQueryFactory.createQuery().insertInto(table), table);
	}

	public PreparedSql updateQuery(E entity) {
		return SqlQueryFactory.createQuery()
							  .update(table)
							  .set(fieldMapping.getNonPrimaryKeyValues(entity))
							  .where(fieldMapping.getPrimaryKeyCondition(entity));
	}

	public PreparedSql updateQuery(BiFunction<UpdateStep, T, PreparedSql> query) {
		return query.apply(SqlQueryFactory.createQuery().update(table), table);
	}

	public PreparedSql deleteQuery(ID id) {
		E entity = this.entityFromId.apply(id);
		return SqlQueryFactory.createQuery().deleteFrom(table).where(fieldMapping.getPrimaryKeyCondition(entity));
	}

	public PreparedSql deleteQuery(BiFunction<DeleteStep, T, PreparedSql> query) {
		return query.apply(SqlQueryFactory.createQuery().deleteFrom(table), table);
	}

	public T getTable() { return table; }
	public DbFieldMapping<E> getFieldMapping() { return fieldMapping; }

}