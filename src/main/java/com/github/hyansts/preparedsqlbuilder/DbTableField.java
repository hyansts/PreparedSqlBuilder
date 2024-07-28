package com.github.hyansts.preparedsqlbuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.github.hyansts.preparedsqlbuilder.sql.SqlAggregator;

import static com.github.hyansts.preparedsqlbuilder.sql.SqlConditionOperator.*;
import static com.github.hyansts.preparedsqlbuilder.sql.SqlKeyword.AS;

public class DbTableField<T> {

	public enum SortOrder {
		ASC(" ASC"),
		DESC(" DESC");

		private final String keyword;

		SortOrder(String keyword) { this.keyword = keyword; }

		@Override
		public String toString() { return keyword; }
	}

	private final String FIELD_NAME;
	private final DbTable TABLE;

	private String alias;
	private T insertValue;
	private SortOrder sortOrder = SortOrder.ASC;

	public DbTableField(String name, DbTable table) {
		this.FIELD_NAME = name;
		this.TABLE = table;
	}

	public SqlCondition eq(DbTableField<T> tf) {
		return new SqlCondition(this, EQ, tf);
	}

	public SqlCondition eq(T val) {
		return new SqlCondition(this, EQ, val);
	}

	public SqlCondition ne(DbTableField<T> tf) {
		return new SqlCondition(this, NE, tf);
	}

	public SqlCondition ne(T val) {
		return new SqlCondition(this, NE, val);
	}

	public SqlCondition lt(DbTableField<T> tf) {
		return new SqlCondition(this, LT, tf);
	}

	public SqlCondition lt(T val) {
		return new SqlCondition(this, LT, val);
	}

	public SqlCondition gt(DbTableField<T> tf) {
		return new SqlCondition(this, GT, tf);
	}

	public SqlCondition gt(T val) {
		return new SqlCondition(this, GT, val);
	}

	public SqlCondition ge(DbTableField<T> tf) {
		return new SqlCondition(this, GE, tf);
	}

	public SqlCondition ge(T val) {
		return new SqlCondition(this, GE, val);
	}

	public SqlCondition le(DbTableField<T> tf) {
		return new SqlCondition(this, LE, tf);
	}

	public SqlCondition le(T val) {
		return new SqlCondition(this, LE, val);
	}

	public SqlCondition between(DbTableField<T> tf1, DbTableField<T> tf2) {
		return new SqlCondition(this, BETWEEN, tf1, AND, tf2);
	}

	public SqlCondition between(T val1, T val2) {
		return new SqlCondition(this, BETWEEN, val1, AND, val2);
	}

	public SqlCondition notBetween(DbTableField<T> tf1, DbTableField<T> tf2) {
		return new SqlCondition(this, NOT_BETWEEN, tf1, AND, tf2);
	}

	public SqlCondition notBetween(T val1, T val2) {
		return new SqlCondition(this, NOT_BETWEEN, val1, AND, val2);
	}

	public SqlCondition like(DbTableField<T> tf) {
		return new SqlCondition(this, LIKE, tf);
	}

	public SqlCondition like(T val) {
		return new SqlCondition(this, LIKE, val);
	}

	public SqlCondition notLike(DbTableField<T> tf) {
		return new SqlCondition(this, NOT_LIKE, tf);
	}

	public SqlCondition notLike(T val) {
		return new SqlCondition(this, NOT_LIKE, val);
	}

	public SqlCondition isNull() {
		return new SqlCondition(this, IS_NULL);
	}

	public SqlCondition isNotNull() {
		return new SqlCondition(this, IS_NOT_NULL);
	}

	public SqlCondition in(List<T> values) {
		return new SqlCondition(this, IN, values);
	}

	public SqlCondition notIn(List<T> values) {
		return new SqlCondition(this, NOT_IN, values);
	}

	public DbTableField<T> max() {
		return new DbTableField<>(SqlAggregator.max(this.getFieldLabel()), null);
	}

	public DbTableField<T> min() {
		return new DbTableField<>(SqlAggregator.min(this.getFieldLabel()), null);
	}

	public DbTableField<Double> avg() {
		return new DbTableField<>(SqlAggregator.avg(this.getFieldLabel()), null);
	}

	public DbTableField<Long> count() {
		return new DbTableField<>(SqlAggregator.count(this.getFieldLabel()), null);
	}

	public DbTableField<Double> sum() {
		return new DbTableField<>(SqlAggregator.sum(this.getFieldLabel()), null);
	}

	public DbTableField<T> asc() {
		this.sortOrder = SortOrder.ASC;
		return this;
	}

	public DbTableField<T> desc() {
		this.sortOrder = SortOrder.DESC;
		return this;
	}

	public DbTableField<T> as(String alias) {
		this.alias = alias;
		return this;
	}

	T getInsertValue() {
		return this.insertValue;
	}

	public DbTableField<T> value(T value) {
		this.insertValue = value;
		return this;
	}

	@SuppressWarnings("unchecked")
	public T get(ResultSet resultSet) throws SQLException {
		return (T) resultSet.getObject(this.getFieldLabel());
	}

	public String getFieldLabel() {
		return this.alias == null ? this.getFullFieldName() : this.alias;
	}

	public String getFullFieldName() {
		return this.TABLE == null || this.TABLE.getTableLabel() == null
					   ? this.FIELD_NAME : this.TABLE.getTableLabel() + "." + this.FIELD_NAME;
	}

	public String getFieldNameDefinition() {
		return this.alias == null ? this.getFullFieldName() : this.getFullFieldName() + AS + this.alias;
	}

	public String getFieldName() { return this.FIELD_NAME; }
	public String getAlias() { return this.alias; }
	public SortOrder getSortOrder() { return this.sortOrder; }

	@Override
	public String toString() {
		return this.getFieldLabel();
	}
}