package com.github.hyansts.preparedsqlbuilder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Supplier;

import com.github.hyansts.preparedsqlbuilder.sql.SqlAggregator;

import static com.github.hyansts.preparedsqlbuilder.sql.SqlConditionOperator.EQ;
import static com.github.hyansts.preparedsqlbuilder.sql.SqlKeyword.*;

public class PreparedSqlBuilder {

	private final StringBuilder sql = new StringBuilder(128);
	private final List<DbTableField<?>> selectedFields = new ArrayList<>();
	private final List<Object> values = new ArrayList<>();
	private boolean chainNextSetClause = false;

	public PreparedSqlBuilder select(DbTableField<?>... fields) {
		this.sql.append(SELECT).append(chainFieldsDefinitions(fields));
		return this;
	}

	public PreparedSqlBuilder select(String expression, DbTableField<?>... fields) {
		this.sql.append(SELECT).append(expression);
		if (fields != null && fields.length > 0) {
			this.sql.append(", ").append(chainFieldsDefinitions(fields));
		}
		return this;
	}

	public PreparedSqlBuilder selectDistinct(DbTableField<?>... fields) {
		sql.append(SELECT).append(DISTINCT).append(chainFieldsDefinitions(fields));
		return this;
	}

	public PreparedSqlBuilder selectDistinct(String expression, DbTableField<?>... fields) {
		this.sql.append(SELECT).append(DISTINCT).append(expression);
		if (fields != null && fields.length > 0) {
			this.sql.append(", ").append(chainFieldsDefinitions(fields));
		}
		return this;
	}

	public PreparedSqlBuilder selectCount(DbTableField<?> field) {
		sql.append(SELECT).append(SqlAggregator.count(field.getFullFieldName()));
		return this;
	}

	public PreparedSqlBuilder selectCount() {
		sql.append(SELECT).append(SqlAggregator.count("*"));
		return this;
	}

	public PreparedSqlBuilder from(DbTable table) {
		this.sql.append(FROM).append(table.getTableNameDefinition());
		return this;
	}

	public PreparedSqlBuilder where(SqlCondition condition) {
		this.values.addAll(condition.getComparedValues());
		this.sql.append(WHERE).append(condition);
		return this;
	}

	public PreparedSqlBuilder update(DbTable table) {
		this.sql.append(UPDATE).append(table.getFullTableName());
		return this;
	}

	public <T> PreparedSqlBuilder set(DbTableField<T> field, T value) {
		this.sql.append(this.chainNextSetClause ? ", " : SET).append(field.getFieldName()).append(EQ);
		this.chainNextSetClause = true;
		if (value == null) {
			this.sql.append("null");
			return this;
		}
		this.values.add(value);
		this.sql.append('?');
		return this;
	}

	public PreparedSqlBuilder deleteFrom(DbTable table) {
		this.sql.append(DELETE_FROM).append(table.getFullTableName());
		return this;
	}

	public PreparedSqlBuilder insertInto(DbTable table) {
		this.sql.append(INSERT_INTO).append(table.getFullTableName()).append(' ');
		return this;
	}

	public PreparedSqlBuilder values(DbTableField<?>... fields) {

		StringJoiner joinedFields = new StringJoiner(", ", "(", ")");
		StringJoiner joinedValues = new StringJoiner(", ", "(", ")");

		for (var field : fields) {
			joinedFields.add(field.getFieldName());
			if (field.getInsertValue() == null) {
				joinedValues.add("null");
				continue;
			}
			this.values.add(field.getInsertValue());
			joinedValues.add("?");
		}
		this.sql.append(joinedFields).append(VALUES).append(joinedValues);
		return this;
	}

	public PreparedSqlBuilder innerJoin(DbTable table) {
		this.sql.append(INNER_JOIN).append(table.getTableNameDefinition());
		return this;
	}

	public PreparedSqlBuilder leftJoin(DbTable table) {
		this.sql.append(LEFT_JOIN).append(table.getTableNameDefinition());
		return this;
	}

	public PreparedSqlBuilder rigtJoin(DbTable table) {
		this.sql.append(RIGHT_JOIN).append(table.getTableNameDefinition());
		return this;
	}

	public PreparedSqlBuilder on(SqlCondition condition) {
		this.values.addAll(condition.getComparedValues());
		this.sql.append(ON).append(condition);
		return this;
	}

	public PreparedSqlBuilder groupBy(DbTableField<?>... fields) {
		StringJoiner joinedFields = new StringJoiner(", ");
		for (var field : fields) {
			joinedFields.add(field.getFullFieldName());
		}
		this.sql.append(GROUP_BY).append(joinedFields);
		return this;
	}

	public PreparedSqlBuilder orderBy(DbTableField<?>... fields) {
		StringJoiner joinedFields = new StringJoiner(", ");
		for (var field : fields) {
			joinedFields.add(field.getFieldLabel() + field.getSortOrder());
		}
		this.sql.append(ORDER_BY).append(joinedFields);
		return this;
	}

	private String chainFieldsDefinitions(DbTableField<?>... fields) {
		StringJoiner clause = new StringJoiner(", ");
		for (var field : fields) {
			this.selectedFields.add(field);
			clause.add("%s");
		}
		return clause.toString();
	}

	public void prepareValues(PreparedStatement preparedStatement) throws SQLException {
		int i = 1;
		for (var value : this.values) {
			preparedStatement.setObject(i++, value);
		}
	}

	public List<Object> getValues() { return values; }

	public String getSql() {
		Object[] fieldsString = new String[this.selectedFields.size()];
		int i = 0;
		for (var field : this.selectedFields) {
			fieldsString[i++] = field.getFieldNameDefinition();
		}
		this.sql.append(';');
		return String.format(this.sql.toString(), fieldsString);
	}

	@Override
	public String toString() {
		return getSql();
	}

}
