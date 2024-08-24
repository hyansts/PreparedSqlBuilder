package com.github.hyansts.preparedsqlbuilder.query.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import com.github.hyansts.preparedsqlbuilder.db.DbField;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldLike;
import com.github.hyansts.preparedsqlbuilder.db.DbFieldOrder;
import com.github.hyansts.preparedsqlbuilder.db.DbTableLike;
import com.github.hyansts.preparedsqlbuilder.query.CombinableQuery;
import com.github.hyansts.preparedsqlbuilder.query.CombiningOperation;
import com.github.hyansts.preparedsqlbuilder.query.FromStep;
import com.github.hyansts.preparedsqlbuilder.query.GroupByStep;
import com.github.hyansts.preparedsqlbuilder.query.HavingStep;
import com.github.hyansts.preparedsqlbuilder.query.JoinStep;
import com.github.hyansts.preparedsqlbuilder.query.LimitStep;
import com.github.hyansts.preparedsqlbuilder.query.OrderByStep;
import com.github.hyansts.preparedsqlbuilder.query.SelectQuerySteps;
import com.github.hyansts.preparedsqlbuilder.query.SelectStatement;
import com.github.hyansts.preparedsqlbuilder.query.SelectStep;
import com.github.hyansts.preparedsqlbuilder.query.SqlScalarSubquery;
import com.github.hyansts.preparedsqlbuilder.query.SqlSubquery;
import com.github.hyansts.preparedsqlbuilder.query.UnionStep;
import com.github.hyansts.preparedsqlbuilder.query.WhereStep;
import com.github.hyansts.preparedsqlbuilder.sql.SqlAggregator;
import com.github.hyansts.preparedsqlbuilder.sql.SqlCondition;
import com.github.hyansts.preparedsqlbuilder.util.StringTemplateFormatter;

import static com.github.hyansts.preparedsqlbuilder.sql.SqlKeyword.*;

abstract class BaseSqlBuilder<T> implements SelectStatement<T>, SelectQuerySteps<T> {

	protected final StringBuilder sql = new StringBuilder(128);
	protected final List<Object> values = new ArrayList<>();
	protected final List<DbFieldLike> selectedFields = new ArrayList<>();

	@Override
	public SelectStep<T> select(DbFieldLike... fields) {
		this.sql.append(SELECT).append(chainFieldsDefinitions(fields));
		return this;
	}

	@Override
	public SelectStep<T> select(String expression, DbFieldLike... fields) {
		this.sql.append(SELECT).append(expression);
		if (fields != null && fields.length > 0) {
			this.sql.append(", ").append(chainFieldsDefinitions(fields));
		}
		return this;
	}

	@Override
	public SelectStep<T> selectDistinct(DbFieldLike... fields) {
		this.sql.append(SELECT).append(DISTINCT).append(chainFieldsDefinitions(fields));
		return this;
	}

	@Override
	public SelectStep<T> selectDistinct(String expression, DbFieldLike... fields) {
		this.sql.append(SELECT).append(DISTINCT).append(expression);
		if (fields != null && fields.length > 0) {
			this.sql.append(", ").append(chainFieldsDefinitions(fields));
		}
		return this;
	}

	@Override
	public SelectStep<T> selectCount(DbField field) {
		this.sql.append(SELECT).append(SqlAggregator.COUNT.applyTo(field.getFullQualification()));
		return this;
	}

	@Override
	public SelectStep<T> selectCount() {
		this.sql.append(SELECT).append(SqlAggregator.COUNT.applyTo("*"));
		return this;
	}

	@Override
	public FromStep<T> from(DbTableLike table) {
		this.sql.append(FROM).append(table.getDefinition());
		processFieldDefinition(table);
		return this;
	}

	@Override
	public WhereStep<T> where(SqlCondition condition) {
		this.values.addAll(condition.getComparedValues());
		this.sql.append(WHERE).append(condition);
		return this;
	}

	@Override
	public JoinStep<T> innerJoin(DbTableLike table) {
		this.sql.append(INNER_JOIN).append(table.getDefinition());
		processFieldDefinition(table);
		return this;
	}

	@Override
	public JoinStep<T> leftJoin(DbTableLike table) {
		this.sql.append(LEFT_JOIN).append(table.getDefinition());
		processFieldDefinition(table);
		return this;
	}

	@Override
	public JoinStep<T> rightJoin(DbTableLike table) {
		this.sql.append(RIGHT_JOIN).append(table.getDefinition());
		processFieldDefinition(table);
		return this;
	}

	@Override
	public JoinStep<T> fullJoin(DbTableLike table) {
		this.sql.append(FULL_JOIN).append(table.getDefinition());
		processFieldDefinition(table);
		return this;
	}

	@Override
	public FromStep<T> crossJoin(DbTableLike table) {
		this.sql.append(CROSS_JOIN).append(table.getDefinition());
		processFieldDefinition(table);
		return this;
	}

	@Override
	public FromStep<T> on(SqlCondition condition) {
		this.values.addAll(condition.getComparedValues());
		this.sql.append(ON).append(condition);
		return this;
	}

	@Override
	public GroupByStep<T> groupBy(DbField... fields) {
		StringJoiner joinedFields = new StringJoiner(", ");
		for (var field : fields) {
			joinedFields.add(field.getFullQualification());
		}
		this.sql.append(GROUP_BY).append(joinedFields);
		return this;
	}

	@Override
	public HavingStep<T> having(SqlCondition condition) {
		this.values.addAll(condition.getComparedValues());
		this.sql.append(HAVING).append(condition);
		return this;
	}

	@Override
	public OrderByStep<T> orderBy(DbFieldOrder... fieldOrders) {
		StringJoiner joinedFields = new StringJoiner(", ");
		for (var fieldOrder : fieldOrders) {
			joinedFields.add(fieldOrder.getDefinition());
		}
		this.sql.append(ORDER_BY).append(joinedFields);
		return this;
	}

	@Override
	public LimitStep<T> limit(Integer number) {
		this.sql.append(LIMIT).append(number);
		return this;
	}

	@Override
	public CombiningOperation<T> offset(Integer number) {
		this.sql.append(OFFSET).append(number);
		return this;
	}

	@Override
	public UnionStep<T> union(CombinableQuery<T> query) {
		this.values.addAll(query.getValues());
		this.sql.append(UNION).append(query);
		return this;
	}

	@Override
	public UnionStep<T> unionAll(CombinableQuery<T> query) {
		this.values.addAll(query.getValues());
		this.sql.append(UNION_ALL).append(query);
		return this;
	}

	@Override
	public UnionStep<T> intersect(CombinableQuery<T> query) {
		this.values.addAll(query.getValues());
		this.sql.append(INTERSECT).append(query);
		return this;
	}

	@Override
	public UnionStep<T> intersectAll(CombinableQuery<T> query) {
		this.values.addAll(query.getValues());
		this.sql.append(INTERSECT_ALL).append(query);
		return this;
	}

	@Override
	public UnionStep<T> except(CombinableQuery<T> query) {
		this.values.addAll(query.getValues());
		this.sql.append(EXCEPT).append(query);
		return this;
	}

	@Override
	public UnionStep<T> exceptAll(CombinableQuery<T> query) {
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
		processFieldDefinition(null);
		validate();
		return this.sql.toString();
	}

	@Override
	public String toString() {
		processFieldDefinition(null);
		return this.sql.toString();
	}

	protected void validate() {
		String undefinedFieldKey = new StringTemplateFormatter().findFirstKey(this.sql.toString());
		if (undefinedFieldKey != null && !undefinedFieldKey.isEmpty()) {
			String undefinedField = this.selectedFields.get(Integer.parseInt(undefinedFieldKey)).getDefinition();
			throw new IllegalStateException("Selected field was not found in any table in the FROM or JOIN clauses: '"
													+ undefinedField + "'");
		}
	}

	protected String chainFieldsDefinitions(DbFieldLike... fields) {
		if (fields == null || fields.length == 0) {
			return "*";
		}
		StringJoiner clause = new StringJoiner(", ");
		for (int i = 0; i < fields.length; i++) {
			if (fields[i] instanceof SqlScalarSubquery<?> subquery) {
				this.values.addAll(subquery.getValues());
			}
			this.selectedFields.add(fields[i]);
			clause.add("${" + i + "}");
		}
		return clause.toString();
	}

	protected void processFieldDefinition(DbTableLike tableLike) {
		if (tableLike instanceof SqlSubquery subquery) {
			this.values.addAll(subquery.getValues());
		}
		StringTemplateFormatter formatter = new StringTemplateFormatter();
		for (int i = 0; i < this.selectedFields.size(); i++) {
			if (this.selectedFields.get(i).getTableLike() == tableLike) {
				formatter.put(Integer.toString(i), this.selectedFields.get(i).getDefinition());
			}
		}
		String formattedSql = formatter.format(this.sql.toString());
		this.sql.delete(0, this.sql.length());
		this.sql.append(formattedSql);
	}

}