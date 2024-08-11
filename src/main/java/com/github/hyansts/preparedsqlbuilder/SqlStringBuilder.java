package com.github.hyansts.preparedsqlbuilder;

import java.util.StringJoiner;

import com.github.hyansts.preparedsqlbuilder.sql.SqlAggregator;

import static com.github.hyansts.preparedsqlbuilder.sql.SqlConditionOperator.EQ;
import static com.github.hyansts.preparedsqlbuilder.sql.SqlKeyword.*;

public class SqlStringBuilder {

	private final StringBuilder sql = new StringBuilder(128);
	private boolean chainNextSetClause = false;

	public SqlStringBuilder select(String... fields) {
		sql.append(SELECT).append(chainFields(fields));
		return this;
	}

	public SqlStringBuilder selectDistinct(String... fields) {
		sql.append(SELECT).append(DISTINCT).append(chainFields(fields));
		return this;
	}

	public SqlStringBuilder selectCount(String field) {
		sql.append(SELECT).append(SqlAggregator.count(field));
		return this;
	}

	public SqlStringBuilder selectCount() {
		selectCount("*");
		return this;
	}

	public SqlStringBuilder from(String table) {
		sql.append(FROM).append(table);
		return this;
	}

	public SqlStringBuilder where(String condition) {
		sql.append(WHERE).append(condition);
		return this;
	}

	public SqlStringBuilder update(String table) {
		sql.append(UPDATE).append(table);
		return this;
	}

	public SqlStringBuilder set(String field, Object value) {
		sql.append(this.chainNextSetClause ? ", " : SET).append(field).append(EQ).append(valueObjectToString(value));
		this.chainNextSetClause = true;
		return this;
	}

	public SqlStringBuilder deleteFrom(String table) {
		sql.append(DELETE_FROM).append(table);
		return this;
	}

	public SqlStringBuilder insertInto(String table, String... fields) {
		sql.append(INSERT_INTO).append(table).append(" (").append(chainFields(fields)).append(')');
		return this;
	}

	public SqlStringBuilder values(Object... values) {
		StringJoiner joinedFields = new StringJoiner(", ", "(", ")");
		for (var value : values) {
			joinedFields.add(valueObjectToString(value));
		}
		this.sql.append(VALUES).append(joinedFields);
		return this;
	}

	public SqlStringBuilder innerJoin(String table) {
		sql.append(INNER_JOIN).append(table);
		return this;
	}

	public SqlStringBuilder leftJoin(String table) {
		sql.append(LEFT_JOIN).append(table);
		return this;
	}

	public SqlStringBuilder rigtJoin(String table) {
		sql.append(RIGHT_JOIN).append(table);
		return this;
	}

	public SqlStringBuilder on(String condition) {
		sql.append(ON).append(condition);
		return this;
	}

	public SqlStringBuilder groupBy(String... fields) {
		sql.append(GROUP_BY).append(chainFields(fields));
		return this;
	}

	public SqlStringBuilder orderBy(String... fields) {
		sql.append(ORDER_BY).append(chainFields(fields));
		return this;
	}

	private String chainFields(String... fields) {
		StringJoiner clause = new StringJoiner(", ");
		for (var field : fields) {
			clause.add(field);
		}
		return clause.toString();
	}

	private String valueObjectToString(Object value) {
		switch (value) {
			case Number n -> { return String.valueOf(n); }
			case Boolean b -> { return String.valueOf(b); }
			case String s -> { return "'" + s + "'"; }
			default -> throw new IllegalArgumentException(value + " Must be an instance of Number, Boolean or String");
		}
	}

	public String getSql() { return sql.toString(); }

	@Override
	public String toString() { return getSql(); }
}
