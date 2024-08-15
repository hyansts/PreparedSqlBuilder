package com.github.hyansts.preparedsqlbuilder.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.hyansts.preparedsqlbuilder.db.DbComparableField;
import com.github.hyansts.preparedsqlbuilder.query.SqlScalarSubquery;

public class SqlCondition {

	private final List<Object> comparedValues = new ArrayList<>();
	private String sql;
	private int parenthesisLayer = 1;

	public SqlCondition(DbComparableField<?> tf, SqlConditionOperator op) {
		this.sql = tf.getFullQualification() + op;
	}

	public <T> SqlCondition(DbComparableField<T> tf1, SqlConditionOperator op, DbComparableField<T> tf2) {
		this.sql = tf1.getFullQualification() + op + tf2.getFullQualification();
	}

	public <T> SqlCondition(DbComparableField<T> tf1, SqlConditionOperator op, T val) {
		this.comparedValues.add(val);
		this.sql = tf1.getFullQualification() + op + "?";
	}

	public <T> SqlCondition(DbComparableField<T> tf, SqlConditionOperator op1, T val1, SqlConditionOperator op2, T val2) {
		Collections.addAll(this.comparedValues, val1, val2);
		this.sql = tf.getFullQualification() + op1 + "?" + op2 + "?";
	}

	public <T> SqlCondition(DbComparableField<T> tf1, SqlConditionOperator op1, DbComparableField<T> tf2, SqlConditionOperator op2, DbComparableField<T> tf3) {
		this.sql = tf1.getFullQualification() + op1 + tf2.getFullQualification() + op2 + tf3;
	}

	public <T> SqlCondition(DbComparableField<T> tf1, SqlConditionOperator op, List<T> values) {

		StringBuilder valueString = new StringBuilder("(");
		for (int i = 0; i < values.size(); i++) {
			this.comparedValues.add(values.get(i));
			valueString.append('?');
			if (i < values.size() - 1) {
				valueString.append(", ");
			}
		}
		valueString.append(')');
		this.sql = tf1.getFullQualification() + op + valueString;
	}

	//TODO test if this is necessary (SqlScalarSubquery already implements DbComparableField) and test parenthesis
	public <T> SqlCondition(DbComparableField<T> tf1, SqlConditionOperator op, SqlScalarSubquery<T> subquery) {
		this.comparedValues.addAll(subquery.getValues());
		this.sql = tf1.getFullQualification() + op + "(" + subquery + ")";
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

	public List<Object> getComparedValues() { return this.comparedValues; }
	public String getSql() { return this.sql; }

	@Override
	public String toString() {
		return getSql();
	}
}