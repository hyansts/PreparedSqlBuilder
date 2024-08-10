package com.github.hyansts.preparedsqlbuilder.sql;

public enum SqlConditionOperator {
	EQ(" = "),
	NE(" <> "),
	GT(" > "),
	LT(" < "),
	GE(" >= "),
	LE(" <= "),
	LIKE(" LIKE "),
	NOT_LIKE(" NOT LIKE "),
	IN(" IN "),
	NOT_IN(" NOT IN "),
	BETWEEN(" BETWEEN "),
	NOT_BETWEEN(" NOT BETWEEN "),
	IS_NULL(" IS NULL"),
	IS_NOT_NULL(" IS NOT NULL"),
	AND(" AND "),
	OR(" OR ");

	private final String operator;

	SqlConditionOperator(String operator) { this.operator = operator; }

	@Override
	public String toString() {
		return this.operator;
	}
}
