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
		addSubqueryValues(tf);
		this.sql = tf.getFullQualification() + op;
	}

	public <T> SqlCondition(DbComparableField<T> tf1, SqlConditionOperator op, DbComparableField<T> tf2) {
		addSubqueryValues(tf1, tf2);
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
		addSubqueryValues(tf1, tf2, tf3);
		this.sql = tf1.getFullQualification() + op1 + tf2.getFullQualification() + op2 + tf3;
	}

	public <T> SqlCondition(DbComparableField<T> tf1, SqlConditionOperator op1, DbComparableField<T> tf2, SqlConditionOperator op2, T val) {
		addSubqueryValues(tf1, tf2);
		this.comparedValues.add(val);
		this.sql = tf1.getFullQualification() + op1 + tf2.getFullQualification() + op2 + "?";
	}

	public <T> SqlCondition(DbComparableField<T> tf1, SqlConditionOperator op1, T val, SqlConditionOperator op2, DbComparableField<T> tf2) {
		addSubqueryValues(tf1);
		this.comparedValues.add(val);
		addSubqueryValues(tf2);
		this.sql = tf1.getFullQualification() + op1 + "?" + op2 + tf2.getFullQualification();
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

	private void addSubqueryValues(DbComparableField<?>... fields) {
		for (DbComparableField<?> field : fields) {
			if (field instanceof SqlScalarSubquery<?> subquery) {
				this.comparedValues.addAll(subquery.getValues());
			}
		}
	}

	public List<Object> getComparedValues() { return this.comparedValues; }
	public String getSql() { return this.sql; }

	@Override
	public String toString() {
		return getSql();
	}

}