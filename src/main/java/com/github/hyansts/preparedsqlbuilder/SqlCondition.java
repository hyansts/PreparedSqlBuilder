package com.github.hyansts.preparedsqlbuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.hyansts.preparedsqlbuilder.sql.SqlConditionOperator;

public class SqlCondition {

	private final List<Object> comparedValues = new ArrayList<>();
	private String sql;
	private int parenthesisLayer = 1;

	public <T> SqlCondition(DbTableField<T> tf, SqlConditionOperator op) {
		this.sql = tf.getFullFieldName() + op;
	}

	public <T> SqlCondition(DbTableField<T> tf1, SqlConditionOperator op, DbTableField<T> tf2) {
		this.sql = tf1.getFullFieldName() + op + tf2.getFullFieldName();
	}

	public <T> SqlCondition(DbTableField<T> tf1, SqlConditionOperator op, T val) {
		this.comparedValues.add(val);
		this.sql = tf1.getFullFieldName() + op + "?";
	}

	public <T> SqlCondition(DbTableField<T> tf, SqlConditionOperator op1, T val1, SqlConditionOperator op2, T val2) {
		Collections.addAll(this.comparedValues, val1, val2);
		this.sql = tf.getFullFieldName() + op1 + "?" + op2 + "?";
	}

	public <T> SqlCondition(DbTableField<T> tf1, SqlConditionOperator op1, DbTableField<T> tf2, SqlConditionOperator op2, DbTableField<T> tf3) {
		this.sql = tf1.getFullFieldName() + op1 + tf2.getFullFieldName() + op2 + tf3;
	}

	public <T> SqlCondition(DbTableField<T> tf1, SqlConditionOperator op, List<T> values) {

		StringBuilder valueString = new StringBuilder("(");
		for (int i = 0; i < values.size(); i++) {
			this.comparedValues.add(values.get(i));
			valueString.append('?');
			if (i < values.size() - 1) {
				valueString.append(", ");
			}
		}
		valueString.append(')');
		this.sql = tf1.getFullFieldName() + op + valueString;
	}

	public SqlCondition and(SqlCondition sqlCondition) {
		this.comparedValues.addAll(sqlCondition.getComparedValues());
		this.sql += SqlConditionOperator.AND + evaluateParenthesisLayer(sqlCondition);
		return this;
	}

	public SqlCondition or(SqlCondition sqlCondition) {
		this.comparedValues.addAll(sqlCondition.getComparedValues());
		this.sql += SqlConditionOperator.OR + evaluateParenthesisLayer(sqlCondition);
		return this;
	}

	private String evaluateParenthesisLayer(SqlCondition otherCondition) {
		this.parenthesisLayer = otherCondition.parenthesisLayer + 1;
		if (otherCondition.parenthesisLayer % 2 == 0) {
			return "(" + otherCondition.getSql() + ")";
		}
		return otherCondition.getSql();
	}

	public List<Object> getComparedValues() { return comparedValues; }
	public String getSql() { return sql; }

	@Override
	public String toString() {
		return getSql();
	}
}