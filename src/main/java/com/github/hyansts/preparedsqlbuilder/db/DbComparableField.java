package com.github.hyansts.preparedsqlbuilder.db;

import java.util.List;

import com.github.hyansts.preparedsqlbuilder.query.SqlScalarSubquery;
import com.github.hyansts.preparedsqlbuilder.sql.SqlCondition;

import static com.github.hyansts.preparedsqlbuilder.sql.SqlConditionOperator.*;

public interface DbComparableField<T> extends DbSortableField {

	default SqlCondition eq(DbComparableField<T> tf) {
		return new SqlCondition(this, EQ, tf);
	}

	default SqlCondition eq(T val) {
		return new SqlCondition(this, EQ, val);
	}

	default SqlCondition ne(DbComparableField<T> tf) {
		return new SqlCondition(this, NE, tf);
	}

	default SqlCondition ne(T val) {
		return new SqlCondition(this, NE, val);
	}

	default SqlCondition lt(DbComparableField<T> tf) {
		return new SqlCondition(this, LT, tf);
	}

	default SqlCondition lt(T val) {
		return new SqlCondition(this, LT, val);
	}

	default SqlCondition gt(DbComparableField<T> tf) {
		return new SqlCondition(this, GT, tf);
	}

	default SqlCondition gt(T val) {
		return new SqlCondition(this, GT, val);
	}

	default SqlCondition ge(DbComparableField<T> tf) {
		return new SqlCondition(this, GE, tf);
	}

	default SqlCondition ge(T val) {
		return new SqlCondition(this, GE, val);
	}

	default SqlCondition le(DbComparableField<T> tf) {
		return new SqlCondition(this, LE, tf);
	}

	default SqlCondition le(T val) {
		return new SqlCondition(this, LE, val);
	}

	default SqlCondition between(DbComparableField<T> tf1, DbComparableField<T> tf2) {
		return new SqlCondition(this, BETWEEN, tf1, AND, tf2);
	}

	default SqlCondition between(T val1, T val2) {
		return new SqlCondition(this, BETWEEN, val1, AND, val2);
	}

	default SqlCondition between(DbComparableField<T> tf, T val) {
		return new SqlCondition(this, BETWEEN, tf, AND, val);
	}

	default SqlCondition between(T val, DbComparableField<T> tf) {
		return new SqlCondition(this, BETWEEN, val, AND, tf);
	}

	default SqlCondition notBetween(DbComparableField<T> tf1, DbComparableField<T> tf2) {
		return new SqlCondition(this, NOT_BETWEEN, tf1, AND, tf2);
	}

	default SqlCondition notBetween(T val1, T val2) {
		return new SqlCondition(this, NOT_BETWEEN, val1, AND, val2);
	}

	default SqlCondition notBetween(DbComparableField<T> tf, T val) {
		return new SqlCondition(this, NOT_BETWEEN, tf, AND, val);
	}

	default SqlCondition notBetween(T val, DbComparableField<T> tf) {
		return new SqlCondition(this, NOT_BETWEEN, val, AND, tf);
	}

	default SqlCondition like(DbComparableField<T> tf) {
		return new SqlCondition(this, LIKE, tf);
	}

	default SqlCondition like(T val) {
		return new SqlCondition(this, LIKE, val);
	}

	default SqlCondition notLike(DbComparableField<T> tf) {
		return new SqlCondition(this, NOT_LIKE, tf);
	}

	default SqlCondition notLike(T val) {
		return new SqlCondition(this, NOT_LIKE, val);
	}

	default SqlCondition isNull() {
		return new SqlCondition(this, IS_NULL);
	}

	default SqlCondition isNotNull() {
		return new SqlCondition(this, IS_NOT_NULL);
	}

	default SqlCondition in(List<T> values) {
		return new SqlCondition(this, IN, values);
	}

	default SqlCondition in(SqlScalarSubquery<T> subquery) {
		return new SqlCondition(this, IN, subquery);
	}

	default SqlCondition notIn(List<T> values) {
		return new SqlCondition(this, NOT_IN, values);
	}

	default SqlCondition notIn(SqlScalarSubquery<T> subquery) {
		return new SqlCondition(this, NOT_IN, subquery);
	}

	DbComparableField<T> mapTo(DbTableLike tableLike);
}
