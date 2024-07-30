package com.github.hyansts.preparedsqlbuilder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import com.github.hyansts.preparedsqlbuilder.query.CombinableQuery;
import com.github.hyansts.preparedsqlbuilder.query.SqlQueryBuilder;
import com.github.hyansts.preparedsqlbuilder.sql.SqlAggregator;
import com.github.hyansts.preparedsqlbuilder.sql.SqlCondition;

import static com.github.hyansts.preparedsqlbuilder.sql.SqlConditionOperator.EQ;
import static com.github.hyansts.preparedsqlbuilder.sql.SqlKeyword.*;

class PreparedSqlBuilder implements SqlQueryBuilder {

	private final StringBuilder sql = new StringBuilder(128);
	private final List<DbTableField<?>> selectedFields = new ArrayList<>();
	private final List<Object> values = new ArrayList<>();
	private boolean chainNextSetClause = false;

	@Override
	public SqlQueryBuilder select(DbTableField<?>... fields) {
		this.sql.append(SELECT).append(chainFieldsDefinitions(fields));
		return this;
	}

	@Override
	public SqlQueryBuilder select(String expression, DbTableField<?>... fields) {
		this.sql.append(SELECT).append(expression);
		if (fields != null && fields.length > 0) {
			this.sql.append(", ").append(chainFieldsDefinitions(fields));
		}
		return this;
	}

	@Override
	public SqlQueryBuilder selectDistinct(DbTableField<?>... fields) {
		sql.append(SELECT).append(DISTINCT).append(chainFieldsDefinitions(fields));
		return this;
	}

	@Override
	public SqlQueryBuilder selectDistinct(String expression, DbTableField<?>... fields) {
		this.sql.append(SELECT).append(DISTINCT).append(expression);
		if (fields != null && fields.length > 0) {
			this.sql.append(", ").append(chainFieldsDefinitions(fields));
		}
		return this;
	}

	@Override
	public SqlQueryBuilder selectCount(DbTableField<?> field) {
		sql.append(SELECT).append(SqlAggregator.count(field.getFullFieldName()));
		return this;
	}

	@Override
	public SqlQueryBuilder selectCount() {
		sql.append(SELECT).append(SqlAggregator.count("*"));
		return this;
	}

	@Override
	public SqlQueryBuilder from(DbTable table) {
		this.sql.append(FROM).append(table.getTableNameDefinition());
		return this;
	}

	@Override
	public SqlQueryBuilder where(SqlCondition condition) {
		this.values.addAll(condition.getComparedValues());
		this.sql.append(WHERE).append(condition);
		return this;
	}

	@Override
	public SqlQueryBuilder update(DbTable table) {
		this.sql.append(UPDATE).append(table.getFullTableName());
		return this;
	}

	@Override
	public <T> SqlQueryBuilder set(DbTableField<T> field, T value) {
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

	@Override
	public SqlQueryBuilder deleteFrom(DbTable table) {
		this.sql.append(DELETE_FROM).append(table.getFullTableName());
		return this;
	}

	@Override
	public SqlQueryBuilder insertInto(DbTable table) {
		this.sql.append(INSERT_INTO).append(table.getFullTableName()).append(' ');
		return this;
	}

	@Override
	public SqlQueryBuilder values(DbTableField<?>... fields) {

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

	@Override
	public SqlQueryBuilder innerJoin(DbTable table) {
		this.sql.append(INNER_JOIN).append(table.getTableNameDefinition());
		return this;
	}

	@Override
	public SqlQueryBuilder leftJoin(DbTable table) {
		this.sql.append(LEFT_JOIN).append(table.getTableNameDefinition());
		return this;
	}

	@Override
	public SqlQueryBuilder rightJoin(DbTable table) {
		this.sql.append(RIGHT_JOIN).append(table.getTableNameDefinition());
		return this;
	}

	@Override
	public SqlQueryBuilder fullJoin(DbTable table) {
		//TODO
		return null;
	}

	@Override
	public SqlQueryBuilder crossJoin(DbTable table) {
		//TODO
		return null;
	}

	@Override
	public SqlQueryBuilder on(SqlCondition condition) {
		this.values.addAll(condition.getComparedValues());
		this.sql.append(ON).append(condition);
		return this;
	}

	@Override
	public SqlQueryBuilder groupBy(DbTableField<?>... fields) {
		StringJoiner joinedFields = new StringJoiner(", ");
		for (var field : fields) {
			joinedFields.add(field.getFullFieldName());
		}
		this.sql.append(GROUP_BY).append(joinedFields);
		return this;
	}

	@Override
	public SqlQueryBuilder having(SqlCondition sqlCondition) {
		//TODO
		return null;
	}

	@Override
	public SqlQueryBuilder orderBy(DbTableField<?>... fields) {
		StringJoiner joinedFields = new StringJoiner(", ");
		for (var field : fields) {
			joinedFields.add(field.getFieldLabel() + field.getSortOrder());
		}
		this.sql.append(ORDER_BY).append(joinedFields);
		return this;
	}

	@Override
	public SqlQueryBuilder limit(Integer number) {
		//TODO
		return null;
	}

	@Override
	public SqlQueryBuilder offset(Integer number) {
		//TODO
		return null;
	}

	@Override
	public SqlQueryBuilder union(CombinableQuery query) {
		//TODO
		return null;
	}

	@Override
	public SqlQueryBuilder unionAll(CombinableQuery query) {
		//TODO
		return null;
	}

	@Override
	public SqlQueryBuilder intersect(CombinableQuery query) {
		//TODO
		return null;
	}

	@Override
	public SqlQueryBuilder intersectAll(CombinableQuery query) {
		//TODO
		return null;
	}

	@Override
	public SqlQueryBuilder except(CombinableQuery query) {
		//TODO
		return null;
	}

	@Override
	public SqlQueryBuilder exceptAll(CombinableQuery query) {
		//TODO
		return null;
	}

	//TODO: create a better way to interface with PreparedStatement
	public void prepareValues(PreparedStatement preparedStatement) throws SQLException {
		int i = 1;
		for (var value : this.values) {
			preparedStatement.setObject(i++, value);
		}
	}

	@Override
	public List<Object> getValues() { return values; }

	@Override
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
	public String toString() { return getSql(); }

	private String chainFieldsDefinitions(DbTableField<?>... fields) {
		StringJoiner clause = new StringJoiner(", ");
		for (var field : fields) {
			this.selectedFields.add(field);
			clause.add("%s");
		}
		return clause.toString();
	}

}
