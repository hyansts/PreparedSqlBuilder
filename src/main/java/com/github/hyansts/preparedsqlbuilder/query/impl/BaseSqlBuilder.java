package com.github.hyansts.preparedsqlbuilder.query.impl;

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

/**
 * A base abstract class for implementing SQL builders.
 * <p>
 * This class provides implementations of the {@link SelectStatement} and {@link SelectQuerySteps} interfaces.
 * <p>
 * As each statement is called, the SQL string is built converting to plain SQL text the fields, columns, conditions, etc.
 * This class accounts for the SQL order of execution, allowing to define your SQL statements in the same way as you
 * would in plain SQL.
 * <p>
 * For example, you can define a SELECT statement as follows:
 * <p>
 * {@code query.select(employees.id, employees.name, employees.age).from(employees.as("e"))}
 * <p>
 * Expected SQL: {@code "SELECT e.id, e.name, e.age FROM employees AS e"}
 * <p>
 * Even though you the table alias is defined in the query after the fields are passed to the SELECT statement, the
 * resulting SQL still contains the table alias prefixed to the field names. That is possible because the fields passed
 * to the SELECT statement are stored in the {@link #selectedFields} list, those fields are only processed after
 * referencing their table of origin in the FROM or JOIN clause. Selecting a field from a table that is not present in
 * the query will result in an exception.
 */
abstract class BaseSqlBuilder<T> implements SelectStatement<T>, SelectQuerySteps<T> {

	protected final StringBuilder sql = new StringBuilder(128);
	protected final List<Object> values = new ArrayList<>();
	protected final List<DbFieldLike> selectedFields = new ArrayList<>();

	/**
	 * Adds a SELECT clause to the SQL query.
	 * <p>
	 * The passed fields are appended to the current SQL query, if no fields are passed, an asterisk ("*") is added.
	 * <p>
	 * Example: {@code query.select(employees.id, employees.name, employees.age)}
	 * <p>
	 * Expected SQL: {@code "SELECT id, name, age"}
	 * <p>
	 * If the passed fields belong to a table with an alias, the alias is prefixed to the field name. Table names are
	 * not prefixed if they don't have an alias.
	 * <p>
	 * Example: {@code query.select(employees.id, employees.name).from(employees.as("e"))}
	 * <p>
	 * Expected SQL: {@code "SELECT e.id, e.name FROM employees AS e"}
	 *
	 * @param fields the fields to be added to the SELECT clause.
	 * @return the current implementation of {@link SelectStep}.
	 */
	@Override
	public SelectStep<T> select(DbFieldLike... fields) {
		this.sql.append(SELECT).append(chainFieldsDefinitions(fields));
		return this;
	}

	/**
	 * Adds a SELECT clause with an expression and optional fields to the SQL query.
	 * <p>
	 * The passed expression is appended to the current SQL query.
	 * <p>
	 * Example: {@code query.select("e.id + e.age AS calculated_column", employees.name)}
	 * <p>
	 * Expected SQL: {@code "SELECT e.id + e.age AS calculated_column, e.name"}
	 * <p>
	 * If no fields are passed, only the expression is added.
	 *
	 * @param expression the expression to be added to the SELECT clause.
	 * @param fields     the fields to be added to the SELECT clause.
	 * @return the current implementation of {@link SelectStep}.
	 */
	@Override
	public SelectStep<T> select(String expression, DbFieldLike... fields) {
		this.sql.append(SELECT).append(expression);
		if (fields != null && fields.length > 0) {
			this.sql.append(", ").append(chainFieldsDefinitions(fields));
		}
		return this;
	}

	/**
	 * Adds a SELECT DISTINCT clause to the SQL query.
	 * <p>
	 * The passed fields are appended to the current SQL query, if no fields are passed, an asterisk ("*") is added.
	 * <p>
	 * Example: {@code query.selectDistinct(employees.name, employees.age)}
	 * <p>
	 * Expected SQL: {@code "SELECT DISTINCT name, age"}
	 * <p>
	 * If the passed fields belong to a table with an alias, the alias is prefixed to the field name. Table names are
	 * not prefixed if they don't have an alias.
	 * <p>
	 * Example: {@code query.selectDistinct(employees.name, employees.age).from(employees.as("e"))}
	 * <p>
	 * Expected SQL: {@code "SELECT DISTINCT e.name, e.age FROM employees AS e"}
	 *
	 * @param fields the fields to be added to the SELECT DISTINCT clause.
	 * @return the current implementation of {@link SelectStep}.
	 */
	@Override
	public SelectStep<T> selectDistinct(DbFieldLike... fields) {
		this.sql.append(SELECT).append(DISTINCT).append(chainFieldsDefinitions(fields));
		return this;
	}

	/**
	 * Adds a SELECT DISTINCT clause with an expression and optional fields to the SQL query.
	 * <p>
	 * The passed expression is appended to the current SQL query.
	 * <p>
	 * Example: {@code query.selectDistinct("e.id + e.age AS calculated_column", employees.name)}
	 * <p>
	 * Expected SQL: {@code "SELECT DISTINCT e.id + e.age AS calculated_column, e.name"}
	 * <p>
	 * If no fields are passed, only the expression is added.
	 *
	 * @param expression the expression to be added to the SELECT DISTINCT clause.
	 * @param fields     the fields to be added to the SELECT DISTINCT clause.
	 * @return the current implementation of {@link SelectStep}.
	 */
	@Override
	public SelectStep<T> selectDistinct(String expression, DbFieldLike... fields) {
		this.sql.append(SELECT).append(DISTINCT).append(expression);
		if (fields != null && fields.length > 0) {
			this.sql.append(", ").append(chainFieldsDefinitions(fields));
		}
		return this;
	}

	/**
	 * Adds a SELECT COUNT clause to the SQL query.
	 * <p>
	 * The passed field is appended to the current SQL query.
	 * <p>
	 * Example: {@code query.selectCount(employees.id)}
	 * <p>
	 * Expected SQL: {@code "SELECT COUNT(id)"}
	 * <p>
	 * <b>Table aliases</b>
	 * <p>
	 * If the passed field belongs to a table with an alias, the alias is prefixed to the field name.
	 * <p>
	 * Example: {@code query.selectCount(employees.id).from(employees.as("e"))}
	 * <p>
	 * Expected SQL: {@code "SELECT COUNT(e.id)"}
	 *
	 * @param field the field to be added to the SELECT COUNT clause.
	 * @return the current implementation of {@link SelectStep}.
	 */
	@Override
	public SelectStep<T> selectCount(DbField field) {
		this.sql.append(SELECT).append(SqlAggregator.COUNT.applyTo(field.getFullQualification()));
		return this;
	}

	/**
	 * Adds a SELECT COUNT(*) clause to the SQL query.
	 * <p>
	 * Example: {@code query.selectCount()}
	 * <p>
	 * Expected SQL: {@code "SELECT COUNT(*)"}
	 *
	 * @return the current implementation of {@link SelectStep}.
	 */
	@Override
	public SelectStep<T> selectCount() {
		this.sql.append(SELECT).append(SqlAggregator.COUNT.applyTo("*"));
		return this;
	}

	/**
	 * Adds a FROM clause to the SQL query.
	 * <p>
	 * The passed table is appended to the current SQL query.
	 * <p>
	 * Example: {@code query.select().from(employees)}
	 * <p>
	 * Expected SQL: {@code "SELECT * FROM employees"}
	 * <p>
	 * <b>Table aliases</b>
	 * <p>
	 * If the passed table has an alias, the alias definition is appended to the table name.
	 * <p>
	 * Example: {@code query.select().from(employees.as("e"))}
	 * <p>
	 * Expected SQL: {@code "SELECT * FROM employees AS e"}
	 *
	 * @param table the table to be added to the FROM clause.
	 * @return the current implementation of {@link FromStep}.
	 */
	@Override
	public FromStep<T> from(DbTableLike table) {
		this.sql.append(FROM).append(table.getDefinition());
		processFieldDefinition(table);
		return this;
	}

	/**
	 * Adds a WHERE clause to the SQL query.
	 * <p>
	 * The passed condition is appended to the current SQL query. Conditions are normally created by comparing fields
	 * with values or with each other.
	 * <p>
	 * Example: {@code query.select().from(employees.as("e").where(employees.age.eq(18))}
	 * <p>
	 * Expected SQL: {@code "SELECT * FROM employees AS e WHERE e.age = ?"}
	 * <p>
	 * The passed condition's values are added to the prepared statement's values list. The values are kept in the
	 * same order as they appeared in the SQL query and can be accessed using the {@link #getValues()} method.
	 * <p>
	 * Example: {@code where(employees.age.eq(18).and(employees.id.eq(10))})}
	 * <p>
	 * Expected prepared statement parameter list: {@code [18, 10]}
	 *
	 * @param condition the condition to be added to the WHERE clause.
	 * @return the current implementation of {@link WhereStep}.
	 */
	@Override
	public WhereStep<T> where(SqlCondition condition) {
		this.values.addAll(condition.getComparedValues());
		this.sql.append(WHERE).append(condition);
		return this;
	}

	/**
	 * Adds an INNER JOIN clause to the SQL query.
	 * <p>
	 * The passed table is appended to the current SQL query.
	 * <p>
	 * Example: {@code query.select().from(employees.as("e").innerJoin(department.as("d")}
	 * <p>
	 * Expected SQL: {@code "SELECT * FROM employees AS e INNER JOIN department AS d"}
	 *
	 * @param table the table to be added to the INNER JOIN clause.
	 * @return the current implementation of {@link JoinStep}.
	 */
	@Override
	public JoinStep<T> innerJoin(DbTableLike table) {
		this.sql.append(INNER_JOIN).append(table.getDefinition());
		processFieldDefinition(table);
		return this;
	}

	/**
	 * Adds a LEFT JOIN clause to the SQL query.
	 * <p>
	 * The passed table is appended to the current SQL query.
	 * <p>
	 * Example: {@code query.select().from(employees.as("e").leftJoin(department.as("d")}
	 * <p>
	 * Expected SQL: {@code "SELECT * FROM employees AS e LEFT JOIN department AS d"}
	 *
	 * @param table the table to be added to the LEFT JOIN clause.
	 * @return the current implementation of {@link JoinStep}.
	 */
	@Override
	public JoinStep<T> leftJoin(DbTableLike table) {
		this.sql.append(LEFT_JOIN).append(table.getDefinition());
		processFieldDefinition(table);
		return this;
	}

	/**
	 * Adds an RIGHT JOIN clause to the SQL query.
	 * <p>
	 * The passed table is appended to the current SQL query.
	 * <p>
	 * Example: {@code query.select().from(employees.as("e").rightJoin(department.as("d")}
	 * <p>
	 * Expected SQL: {@code "SELECT * FROM employees AS e RIGHT JOIN department AS d"}
	 *
	 * @param table the table to be added to the RIGHT JOIN clause.
	 * @return the current implementation of {@link JoinStep}.
	 */
	@Override
	public JoinStep<T> rightJoin(DbTableLike table) {
		this.sql.append(RIGHT_JOIN).append(table.getDefinition());
		processFieldDefinition(table);
		return this;
	}

	/**
	 * Adds an FULL JOIN clause to the SQL query.
	 * <p>
	 * The passed table is appended to the current SQL query.
	 * <p>
	 * Example: {@code query.select().from(employees.as("e").fullJoin(department.as("d")}
	 * <p>
	 * Expected SQL: {@code "SELECT * FROM employees AS e FULL JOIN department AS d"}
	 *
	 * @param table the table to be added to the FULL JOIN clause.
	 * @return the current implementation of {@link JoinStep}.
	 */
	@Override
	public JoinStep<T> fullJoin(DbTableLike table) {
		this.sql.append(FULL_JOIN).append(table.getDefinition());
		processFieldDefinition(table);
		return this;
	}

	/**
	 * Adds an CROSS JOIN clause to the SQL query.
	 * <p>
	 * The passed table is appended to the current SQL query.
	 * <p>
	 * Example: {@code query.select().from(employees.as("e").crossJoin(department.as("d")}
	 * <p>
	 * Expected SQL: {@code "SELECT * FROM employees AS e CROSS JOIN department AS d"}
	 *
	 * @param table the table to be added to the CROSS JOIN clause.
	 * @return the current implementation of {@link JoinStep}.
	 */
	@Override
	public FromStep<T> crossJoin(DbTableLike table) {
		this.sql.append(CROSS_JOIN).append(table.getDefinition());
		processFieldDefinition(table);
		return this;
	}

	/**
	 * Adds an ON clause to the SQL query.
	 * <p>
	 * The passed condition is appended to the current SQL query. Conditions are normally created by comparing fields
	 * with values or with each other.
	 * <p>
	 * <pre>
	 * Example:
	 * {@code
	 * query.select()
	 *      .from(employees.as("e")
	 *      .innerJoin(department.as("d")
	 *      .on(employees.dep_id.eq(department.id)
	 * }
	 * </pre>
	 * <p>
	 * <pre>
	 * Expected SQL:
	 * {@code "SELECT * FROM employees AS e INNER JOIN employees AS m ON e.dep_id = d.id"}
	 * </pre>
	 *
	 * @param condition the condition to be added to the ON clause.
	 * @return the current implementation of {@link FromStep}.
	 */
	@Override
	public FromStep<T> on(SqlCondition condition) {
		this.values.addAll(condition.getComparedValues());
		this.sql.append(ON).append(condition);
		return this;
	}

	/**
	 * Adds a GROUP BY clause to the SQL query.
	 * <p>
	 * The passed fields are appended to the current SQL query.
	 * <p>
	 * Example: {@code groupBy(employees.id, employees.name)}
	 * <p>
	 * Expected SQL: {@code "GROUP BY id, name"}
	 *
	 * @param fields the fields to be added to the GROUP BY clause.
	 * @return the current implementation of {@link GroupByStep}.
	 */
	@Override
	public GroupByStep<T> groupBy(DbField... fields) {
		StringJoiner joinedFields = new StringJoiner(", ");
		for (var field : fields) {
			joinedFields.add(field.getFullQualification());
		}
		this.sql.append(GROUP_BY).append(joinedFields);
		return this;
	}

	/**
	 * Adds a HAVING clause to the SQL query.
	 * <p>
	 * The passed condition is appended to the current SQL query. Conditions are normally created by comparing fields
	 * with values or with each other.
	 * <p>
	 * <pre>
	 * Example:
	 * {@code groupBy(employees.id).having(employees.age.min().gt(18))}
	 * </pre>
	 * <p>
	 * Expected SQL:
	 * {@code "GROUP BY id HAVING MIN(age) > 18"}
	 *
	 * @param condition the condition to be added to the HAVING clause.
	 * @return the current implementation of {@link HavingStep}.
	 */
	@Override
	public HavingStep<T> having(SqlCondition condition) {
		this.values.addAll(condition.getComparedValues());
		this.sql.append(HAVING).append(condition);
		return this;
	}

	/**
	 * Adds an ORDER BY clause to the SQL query.
	 * <p>
	 * The passed fields are appended to the current SQL query.
	 * <p>
	 * Example: {@code orderBy(employees.id, employees.name)}
	 * <p>
	 * Expected SQL: {@code "ORDER BY id, name"}
	 *
	 * @param fieldOrders the fields to be added to the ORDER BY clause.
	 * @return the current implementation of {@link OrderByStep}.
	 */
	@Override
	public OrderByStep<T> orderBy(DbFieldOrder... fieldOrders) {
		StringJoiner joinedFields = new StringJoiner(", ");
		for (var fieldOrder : fieldOrders) {
			joinedFields.add(fieldOrder.getDefinition());
		}
		this.sql.append(ORDER_BY).append(joinedFields);
		return this;
	}

	/**
	 * Adds a LIMIT clause to the SQL query.
	 * <p>
	 * The passed limit is appended to the current SQL query.
	 * <p>
	 * Example: {@code limit(1)}
	 * <p>
	 * Expected SQL: {@code "LIMIT 1"}
	 *
	 * @param number the limit to be added to the LIMIT clause.
	 * @return the current implementation of {@link LimitStep}.
	 */
	@Override
	public LimitStep<T> limit(Integer number) {
		this.sql.append(LIMIT).append(number);
		return this;
	}

	/**
	 * Adds an OFFSET clause to the SQL query.
	 * <p>
	 * The passed offset is appended to the current SQL query.
	 * <p>
	 * Example: {@code limit(1).offset(3)}
	 * <p>
	 * Expected SQL: {@code "LIMIT 1 OFFSET 3"}
	 *
	 * @param number the offset to be added to the OFFSET clause.
	 * @return the current implementation of {@link CombiningOperation}.
	 */
	@Override
	public CombiningOperation<T> offset(Integer number) {
		this.sql.append(OFFSET).append(number);
		return this;
	}

	/**
	 * Appends a UNION clause to the SQL current query with the specified query.
	 * <p>
	 * All values from the passed query are appended to the current query values list.
	 *
	 * @param query the query to be united with the main query.
	 * @return the current implementation of {@link UnionStep}.
	 */
	@Override
	public UnionStep<T> union(CombinableQuery<T> query) {
		this.values.addAll(query.getValues());
		this.sql.append(UNION).append(query);
		return this;
	}

	/**
	 * Appends a UNION ALL clause to the current SQL query with the specified query.
	 * <p>
	 * All values from the passed query are appended to the current query values list.
	 *
	 * @param query the query to be united with the main query.
	 * @return the current implementation of {@link UnionStep}.
	 */
	@Override
	public UnionStep<T> unionAll(CombinableQuery<T> query) {
		this.values.addAll(query.getValues());
		this.sql.append(UNION_ALL).append(query);
		return this;
	}

	/**
	 * Appends an INTERSECT clause to the current SQL query with the specified query.
	 * <p>
	 * All values from the passed query are appended to the current query values list.
	 *
	 * @param query the query to be intersected with the main query.
	 * @return the current implementation of {@link UnionStep}.
	 */
	@Override
	public UnionStep<T> intersect(CombinableQuery<T> query) {
		this.values.addAll(query.getValues());
		this.sql.append(INTERSECT).append(query);
		return this;
	}

	/**
	 * Appends an INTERSECT ALL clause to the current SQL query with the specified query.
	 * <p>
	 * All values from the passed query are appended to the current query values list.
	 *
	 * @param query the query to be intersected with the main query.
	 * @return the current implementation of {@link UnionStep}.
	 */
	@Override
	public UnionStep<T> intersectAll(CombinableQuery<T> query) {
		this.values.addAll(query.getValues());
		this.sql.append(INTERSECT_ALL).append(query);
		return this;
	}

	/**
	 * Appends an EXCEPT clause to the current SQL query with the specified query.
	 * <p>
	 * All values from the passed query are appended to the current query values list.
	 *
	 * @param query the query to be excepted from the main query.
	 * @return the current implementation of {@link UnionStep}.
	 */
	@Override
	public UnionStep<T> except(CombinableQuery<T> query) {
		this.values.addAll(query.getValues());
		this.sql.append(EXCEPT).append(query);
		return this;
	}

	/**
	 * Appends an EXCEPT ALL clause to the current SQL query with the specified query.
	 * <p>
	 * All values from the passed query are appended to the current query values list.
	 *
	 * @param query the query to be excepted from the main query.
	 * @return the current implementation of {@link UnionStep}.
	 */
	@Override
	public UnionStep<T> exceptAll(CombinableQuery<T> query) {
		this.values.addAll(query.getValues());
		this.sql.append(EXCEPT_ALL).append(query);
		return this;
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