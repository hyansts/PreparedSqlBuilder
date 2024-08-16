package com.github.hyansts.preparedsqlbuilder.query.impl;

import java.util.StringJoiner;

import com.github.hyansts.preparedsqlbuilder.sql.SqlAggregator;

import static com.github.hyansts.preparedsqlbuilder.sql.SqlConditionOperator.EQ;
import static com.github.hyansts.preparedsqlbuilder.sql.SqlKeyword.*;

/**
 * A utility class for constructing SQL String queries programmatically in a fluent, chainable manner.
 * <p>
 * This class provides methods to build various SQL clauses, such as SELECT, INSERT, UPDATE, DELETE, JOIN, WHERE,
 * GROUP BY, ORDER BY, and more. The resulting SQL query can be retrieved as a string using the {@link #getSql()} method.
 * <p>
 * Example:
 * <pre>{@code
 * SqlStringBuilder builder = new SqlStringBuilder();
 * String sql = builder.select("id", "name")
 *                     .from("users")
 *                     .where("age > 18")
 *                     .orderBy("name")
 *                     .getSql();
 * }</pre>
 */
public class SqlStringBuilder {

	private final StringBuilder sql = new StringBuilder(128);
	private boolean chainNextSetClause = false;

	/**
	 * Appends a SELECT clause to the SQL query with the specified fields.
	 *
	 * @param fields the columns to be selected in the query.
	 * @return the current instance of {@code SqlStringBuilder} with the appended SELECT clause.
	 */
	public SqlStringBuilder select(String... fields) {
		this.sql.append(SELECT).append(chainFields(fields));
		return this;
	}

	/**
	 * Appends a SELECT DISTINCT clause to the SQL query with the specified fields.
	 *
	 * @param fields the columns to be selected distinctly in the query.
	 * @return the current instance of {@code SqlStringBuilder} with the appended SELECT DISTINCT clause.
	 */
	public SqlStringBuilder selectDistinct(String... fields) {
		this.sql.append(SELECT).append(DISTINCT).append(chainFields(fields));
		return this;
	}

	/**
	 * Appends a SELECT COUNT clause to the SQL query for the specified field.
	 *
	 * @param field the column to be counted.
	 * @return the current instance of {@code SqlStringBuilder} with the appended SELECT COUNT clause.
	 */
	public SqlStringBuilder selectCount(String field) {
		this.sql.append(SELECT).append(SqlAggregator.count(field));
		return this;
	}

	/**
	 * Appends a SELECT COUNT(*) clause to the SQL query.
	 *
	 * @return the current instance of {@code SqlStringBuilder} with the appended SELECT COUNT(*) clause.
	 */
	public SqlStringBuilder selectCount() {
		selectCount("*");
		return this;
	}

	/**
	 * Appends a FROM clause to the SQL query with the specified table.
	 *
	 * @param table the name of the table from which to select data.
	 * @return the current instance of {@code SqlStringBuilder} with the appended FROM clause.
	 */
	public SqlStringBuilder from(String table) {
		this.sql.append(FROM).append(table);
		return this;
	}

	/**
	 * Appends a WHERE clause to the SQL query with the specified condition.
	 *
	 * @param condition the condition to be applied in the WHERE clause.
	 * @return the current instance of {@code SqlStringBuilder} with the appended WHERE clause.
	 */
	public SqlStringBuilder where(String condition) {
		this.sql.append(WHERE).append(condition);
		return this;
	}

	/**
	 * Appends an UPDATE clause to the SQL query with the specified table.
	 *
	 * @param table the name of the table to be updated.
	 * @return the current instance of {@code SqlStringBuilder} with the appended UPDATE clause.
	 */
	public SqlStringBuilder update(String table) {
		this.sql.append(UPDATE).append(table);
		return this;
	}

	/**
	 * Append a SET clause. Used after the {@link #update(String)} clause for assigning a value to a field.
	 * <p>
	 * If the {@code value} is a {@code String} object, single quotes will be appended around it.
	 * Otherwise, it will be cast to {@code String} without any additional formatting.
	 * Date and time-related fields should be converted to {@code String} beforehand to ensure the correct format.
	 * <p>
	 * This method allows chaining to set multiple values:
	 * <pre>{@code
	 * sqlStringBuilder.update("myTable")
	 *                 .set("col1", "Hi")
	 *                 .set("col2", 1000)
	 *                 .set("col3", true)
	 * }</pre>
	 *
	 * @param field name of the column being updated
	 * @param value the value to be assigned to the field.
	 * @return the current instance of {@code SqlStringBuilder} with the appended SET clause.
	 */
	public SqlStringBuilder set(String field, Object value) {
		this.sql.append(this.chainNextSetClause ? ", " : SET).append(field).append(EQ).append(valueObjectToString(value));
		this.chainNextSetClause = true;
		return this;
	}

	/**
	 * Appends a DELETE FROM clause to the SQL query for the specified table.
	 *
	 * @param table the name of the table from which to delete data.
	 * @return the current instance of {@code SqlStringBuilder} with the appended DELETE FROM clause.
	 */
	public SqlStringBuilder deleteFrom(String table) {
		this.sql.append(DELETE_FROM).append(table);
		return this;
	}

	/**
	 * Appends an INSERT INTO clause to the SQL query for the specified table and fields.
	 *
	 * @param table  the name of the table into which data will be inserted.
	 * @param fields the columns into which values will be inserted.
	 * @return the current instance of {@code SqlStringBuilder} with the appended INSERT INTO clause.
	 */
	public SqlStringBuilder insertInto(String table, String... fields) {
		this.sql.append(INSERT_INTO).append(table).append(" (").append(chainFields(fields)).append(')');
		return this;
	}

	/**
	 * Appends a VALUES clause to the SQL query to assign values to the fields specified in the
	 * {@link #insertInto(String, String...)} clause.
	 * <p>
	 * If the {@code value} is a {@code String} object, single quotes will be appended around it.
	 * Otherwise, it will be cast to {@code String} without any additional formatting.
	 * Date and time-related fields should be converted to {@code String} beforehand to ensure the correct format.
	 *
	 * @param values the values to be inserted into the fields.
	 * @return the current instance of {@code SqlStringBuilder} with the appended VALUES clause.
	 */
	public SqlStringBuilder values(Object... values) {
		StringJoiner joinedFields = new StringJoiner(", ", "(", ")");
		for (var value : values) {
			joinedFields.add(valueObjectToString(value));
		}
		this.sql.append(VALUES).append(joinedFields);
		return this;
	}

	/**
	 * Appends an INNER JOIN clause to the SQL query for the specified table.
	 *
	 * @param table the name of the table to be joined.
	 * @return the current instance of {@code SqlStringBuilder} with the appended INNER JOIN clause.
	 */
	public SqlStringBuilder innerJoin(String table) {
		this.sql.append(INNER_JOIN).append(table);
		return this;
	}

	/**
	 * Appends a LEFT JOIN clause to the SQL query for the specified table.
	 *
	 * @param table the name of the table to be joined.
	 * @return the current instance of {@code SqlStringBuilder} with the appended LEFT JOIN clause.
	 */
	public SqlStringBuilder leftJoin(String table) {
		this.sql.append(LEFT_JOIN).append(table);
		return this;
	}

	/**
	 * Appends a RIGHT JOIN clause to the SQL query for the specified table.
	 *
	 * @param table the name of the table to be joined.
	 * @return the current instance of {@code SqlStringBuilder} with the appended RIGHT JOIN clause.
	 */
	public SqlStringBuilder rightJoin(String table) {
		this.sql.append(RIGHT_JOIN).append(table);
		return this;
	}

	/**
	 * Appends a FULL JOIN clause to the SQL query for the specified table.
	 *
	 * @param table the name of the table to be joined.
	 * @return the current instance of {@code SqlStringBuilder} with the appended FULL JOIN clause.
	 */
	public SqlStringBuilder fullJoin(String table) {
		this.sql.append(FULL_JOIN).append(table);
		return this;
	}

	/**
	 * Appends a CROSS JOIN clause to the SQL query for the specified table.
	 *
	 * @param table the name of the table to be joined.
	 * @return the current instance of {@code SqlStringBuilder} with the appended CROSS JOIN clause.
	 */
	public SqlStringBuilder crossJoin(String table) {
		this.sql.append(CROSS_JOIN).append(table);
		return this;
	}

	/**
	 * Appends an ON clause to the SQL query with the specified join condition.
	 *
	 * @param condition the condition to be applied in the ON clause.
	 * @return the current instance of {@code SqlStringBuilder} with the appended ON clause.
	 */
	public SqlStringBuilder on(String condition) {
		this.sql.append(ON).append(condition);
		return this;
	}

	/**
	 * Appends a GROUP BY clause to the SQL query with the specified fields.
	 *
	 * @param fields the columns by which the result set should be grouped.
	 * @return the current instance of {@code SqlStringBuilder} with the appended GROUP BY clause.
	 */
	public SqlStringBuilder groupBy(String... fields) {
		this.sql.append(GROUP_BY).append(chainFields(fields));
		return this;
	}

	/**
	 * Appends a HAVING clause to the SQL query with the specified condition.
	 *
	 * @param condition the condition to be applied in the HAVING clause.
	 * @return the current instance of {@code SqlStringBuilder} with the appended HAVING clause.
	 */
	public SqlStringBuilder having(String condition) {
		this.sql.append(HAVING).append(condition);
		return this;
	}

	/**
	 * Appends an ORDER BY clause to the SQL query with the specified fields.
	 *
	 * @param fields the columns by which the result set should be ordered.
	 * @return the current instance of {@code SqlStringBuilder} with the appended ORDER BY clause.
	 */
	public SqlStringBuilder orderBy(String... fields) {
		this.sql.append(ORDER_BY).append(chainFields(fields));
		return this;
	}

	/**
	 * Appends a LIMIT clause to the SQL query to limit the number of rows returned.
	 *
	 * @param number the maximum number of rows to return.
	 * @return the current instance of {@code SqlStringBuilder} with the appended LIMIT clause.
	 */
	public SqlStringBuilder limit(Integer number) {
		this.sql.append(LIMIT).append(number);
		return this;
	}

	/**
	 * Appends an OFFSET clause to the SQL query to skip a specified number of rows before returning results.
	 *
	 * @param number the number of rows to skip.
	 * @return the current instance of {@code SqlStringBuilder} with the appended OFFSET clause.
	 */
	public SqlStringBuilder offset(Integer number) {
		this.sql.append(OFFSET).append(number);
		return this;
	}

	/**
	 * Appends a UNION clause to the SQL query with the specified subquery.
	 *
	 * @param query the subquery to be united with the main query.
	 * @return the current instance of {@code SqlStringBuilder} with the appended UNION clause.
	 */
	public SqlStringBuilder union(String query) {
		this.sql.append(UNION).append(query);
		return this;
	}

	/**
	 * Appends a UNION ALL clause to the SQL query with the specified subquery.
	 *
	 * @param query the subquery to be united with the main query, including duplicates.
	 * @return the current instance of {@code SqlStringBuilder} with the appended UNION ALL clause.
	 */
	public SqlStringBuilder unionAll(String query) {
		this.sql.append(UNION_ALL).append(query);
		return this;
	}

	/**
	 * Appends an INTERSECT clause to the SQL query with the specified subquery.
	 *
	 * @param query the subquery to intersect with the main query.
	 * @return the current instance of {@code SqlStringBuilder} with the appended INTERSECT clause.
	 */
	public SqlStringBuilder intersect(String query) {
		this.sql.append(INTERSECT).append(query);
		return this;
	}

	/**
	 * Appends an INTERSECT ALL clause to the SQL query with the specified subquery.
	 *
	 * @param query the subquery to intersect with the main query, including duplicates.
	 * @return the current instance of {@code SqlStringBuilder} with the appended INTERSECT ALL clause.
	 */
	public SqlStringBuilder intersectAll(String query) {
		this.sql.append(INTERSECT_ALL).append(query);
		return this;
	}

	/**
	 * Appends an EXCEPT clause to the SQL query with the specified subquery.
	 *
	 * @param query the subquery to exclude from the main query results.
	 * @return the current instance of {@code SqlStringBuilder} with the appended EXCEPT clause.
	 */
	public SqlStringBuilder except(String query) {
		this.sql.append(EXCEPT).append(query);
		return this;
	}

	/**
	 * Appends an EXCEPT ALL clause to the SQL query with the specified subquery.
	 *
	 * @param query the subquery to exclude from the main query results, including duplicates.
	 * @return the current instance of {@code SqlStringBuilder} with the appended EXCEPT ALL clause.
	 */
	public SqlStringBuilder exceptAll(String query) {
		this.sql.append(EXCEPT_ALL).append(query);
		return this;
	}

	/**
	 * Retrieves the constructed SQL query as a string.
	 *
	 * @return the complete SQL query as a string.
	 */
	public String getSql() { return this.sql.toString(); }

	@Override
	public String toString() { return getSql(); }

	private String chainFields(String... fields) {
		StringJoiner clause = new StringJoiner(", ");
		for (var field : fields) {
			clause.add(field);
		}
		return clause.toString();
	}

	private String valueObjectToString(Object value) {
		if (value instanceof String s) {
			return "'" + s + "'";
		}
		return String.valueOf(value);
	}

}