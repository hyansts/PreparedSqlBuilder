package com.github.hyansts.preparedsqlbuilder.db.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import com.github.hyansts.preparedsqlbuilder.db.DbField;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldLike;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldValue;
import com.github.hyansts.preparedsqlbuilder.db.DbTable;
import com.github.hyansts.preparedsqlbuilder.db.DbTableLike;
import com.github.hyansts.preparedsqlbuilder.query.CombinableQuery;
import com.github.hyansts.preparedsqlbuilder.query.SqlQueryBuilder;
import com.github.hyansts.preparedsqlbuilder.query.SqlScalarSubquery;
import com.github.hyansts.preparedsqlbuilder.sql.SqlAggregator;
import com.github.hyansts.preparedsqlbuilder.sql.SqlCondition;

import static com.github.hyansts.preparedsqlbuilder.sql.SqlConditionOperator.EQ;
import static com.github.hyansts.preparedsqlbuilder.sql.SqlKeyword.*;

class PreparedSqlBuilder implements SqlQueryBuilder {

	protected final List<DbFieldLike> selectedFields = new ArrayList<>();
	private final StringBuilder sql = new StringBuilder(128);
	private final List<Object> values = new ArrayList<>();
	private boolean chainNextSetClause = false;

	@Override
	public SqlQueryBuilder select(DbFieldLike... fields) {
		this.sql.append(SELECT).append(chainFieldsDefinitions(fields));
		return this;
	}

	@Override
	public SqlQueryBuilder select(String expression, DbFieldLike... fields) {
		this.sql.append(SELECT).append(expression);
		if (fields != null && fields.length > 0) {
			this.sql.append(", ").append(chainFieldsDefinitions(fields));
		}
		return this;
	}

	@Override
	public SqlQueryBuilder selectDistinct(DbFieldLike... fields) {
		sql.append(SELECT).append(DISTINCT).append(chainFieldsDefinitions(fields));
		return this;
	}

	@Override
	public SqlQueryBuilder selectDistinct(String expression, DbFieldLike... fields) {
		this.sql.append(SELECT).append(DISTINCT).append(expression);
		if (fields != null && fields.length > 0) {
			this.sql.append(", ").append(chainFieldsDefinitions(fields));
		}
		return this;
	}

	@Override
	public SqlQueryBuilder selectCount(DbField field) {
		sql.append(SELECT).append(SqlAggregator.count(field.getFullQualification()));
		return this;
	}

	@Override
	public SqlQueryBuilder selectCount() {
		sql.append(SELECT).append(SqlAggregator.count("*"));
		return this;
	}

	@Override
	public SqlQueryBuilder from(DbTableLike table) {
		this.sql.append(FROM).append(table.getDefinition());
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
	public SqlQueryBuilder set(DbFieldValue<?> field) {
		this.sql.append(this.chainNextSetClause ? ", " : SET).append(field.getFieldName()).append(EQ);
		this.chainNextSetClause = true;
		if (field.getValue() == null) {
			this.sql.append("null");
			return this;
		}
		this.values.add(field.getValue());
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
	public SqlQueryBuilder values(DbFieldValue<?>... fields) {

		StringJoiner joinedFields = new StringJoiner(", ", "(", ")");
		StringJoiner joinedValues = new StringJoiner(", ", "(", ")");

		for (var field : fields) {
			joinedFields.add(field.getFieldName());
			if (field.getValue() == null) {
				joinedValues.add("null");
				continue;
			}
			this.values.add(field.getValue());
			joinedValues.add("?");
		}
		this.sql.append(joinedFields).append(VALUES).append(joinedValues);
		return this;
	}

	@Override
	public SqlQueryBuilder innerJoin(DbTableLike table) {
		this.sql.append(INNER_JOIN).append(table.getDefinition());
		return this;
	}

	@Override
	public SqlQueryBuilder leftJoin(DbTableLike table) {
		this.sql.append(LEFT_JOIN).append(table.getDefinition());
		return this;
	}

	@Override
	public SqlQueryBuilder rightJoin(DbTableLike table) {
		this.sql.append(RIGHT_JOIN).append(table.getDefinition());
		return this;
	}

	@Override
	public SqlQueryBuilder fullJoin(DbTableLike table) {
		this.sql.append(FULL_JOIN).append(table.getDefinition());
		return this;
	}

	@Override
	public SqlQueryBuilder crossJoin(DbTableLike table) {
		this.sql.append(CROSS_JOIN).append(table.getDefinition());
		return this;
	}

	@Override
	public SqlQueryBuilder on(SqlCondition condition) {
		this.values.addAll(condition.getComparedValues());
		this.sql.append(ON).append(condition);
		return this;
	}

	@Override
	public SqlQueryBuilder groupBy(DbField... fields) {
		StringJoiner joinedFields = new StringJoiner(", ");
		for (var field : fields) {
			joinedFields.add(field.getFullQualification());
		}
		this.sql.append(GROUP_BY).append(joinedFields);
		return this;
	}

	@Override
	public SqlQueryBuilder having(SqlCondition condition) {
		this.values.addAll(condition.getComparedValues());
		this.sql.append(HAVING).append(condition);
		return this;
	}

	@Override
	public SqlQueryBuilder orderBy(DbFieldLike... fields) {
		StringJoiner joinedFields = new StringJoiner(", ");
		for (var field : fields) {
			joinedFields.add(field.getLabel() + field.getSortOrder());
		}
		this.sql.append(ORDER_BY).append(joinedFields);
		return this;
	}

	@Override
	public SqlQueryBuilder limit(Integer number) {
		this.sql.append(LIMIT).append(number);
		return this;
	}

	@Override
	public SqlQueryBuilder offset(Integer number) {
		this.sql.append(OFFSET).append(number);
		return this;
	}

	@Override
	public SqlQueryBuilder union(CombinableQuery query) {
		this.values.addAll(query.getValues());
		this.sql.append(UNION).append(query);
		return this;
	}

	@Override
	public SqlQueryBuilder unionAll(CombinableQuery query) {
		this.values.addAll(query.getValues());
		this.sql.append(UNION_ALL).append(query);
		return this;
	}

	@Override
	public SqlQueryBuilder intersect(CombinableQuery query) {
		this.values.addAll(query.getValues());
		this.sql.append(INTERSECT).append(query);
		return this;
	}

	@Override
	public SqlQueryBuilder intersectAll(CombinableQuery query) {
		this.values.addAll(query.getValues());
		this.sql.append(INTERSECT_ALL).append(query);
		return this;
	}

	@Override
	public SqlQueryBuilder except(CombinableQuery query) {
		this.values.addAll(query.getValues());
		this.sql.append(EXCEPT).append(query);
		return this;
	}

	@Override
	public SqlQueryBuilder exceptAll(CombinableQuery query) {
		this.values.addAll(query.getValues());
		this.sql.append(EXCEPT_ALL).append(query);
		return this;
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
			fieldsString[i++] = field.getDefinition();
		}
		return String.format(this.sql.toString(), fieldsString);
	}

	@Override
	public String toString() { return getSql(); }

	private String chainFieldsDefinitions(DbFieldLike... fields) {
		if (fields == null || fields.length == 0) {
			return "*";
		}
		StringJoiner clause = new StringJoiner(", ");
		for (var field : fields) {
			this.selectedFields.add(field);
			clause.add("%s");
			if (field instanceof SqlScalarSubquery<?> subquery) {
				this.values.addAll(subquery.getValues());
			}
		}
		return clause.toString();
	}

}
