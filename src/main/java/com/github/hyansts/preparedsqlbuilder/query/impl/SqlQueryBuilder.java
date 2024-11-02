package com.github.hyansts.preparedsqlbuilder.query.impl;

import java.util.StringJoiner;

import com.github.hyansts.preparedsqlbuilder.db.DbFieldValue;
import com.github.hyansts.preparedsqlbuilder.db.DbTable;
import com.github.hyansts.preparedsqlbuilder.query.DeleteStep;
import com.github.hyansts.preparedsqlbuilder.query.InsertStep;
import com.github.hyansts.preparedsqlbuilder.query.PreparedSql;
import com.github.hyansts.preparedsqlbuilder.query.SetStep;
import com.github.hyansts.preparedsqlbuilder.query.SqlQuery;
import com.github.hyansts.preparedsqlbuilder.query.UpdateQuerySteps;
import com.github.hyansts.preparedsqlbuilder.query.UpdateStep;

import static com.github.hyansts.preparedsqlbuilder.sql.SqlKeyword.*;

/**
 * Implementation of the {@link SqlQuery} interface for building SQL queries using the builder pattern.
 * <p>
 * This class provides implementations for the {@link InsertStep}, {@link UpdateQuerySteps} and {@link DeleteStep}
 * interfaces. The SelectQuerySteps interface is extended from {@link BaseSqlBuilder}.
 * <p>
 * As each statement is called, the SQL string is built converting to plain SQL text the fields, columns, conditions, etc.
 * This class accounts for the SQL order of execution, allowing to define your SQL statements in the same way as you
 * would in plain SQL.
 * <p>
 * The generated SQL query can be retrieved as a string using the {@link #getSql()} method.
 */
class SqlQueryBuilder extends BaseSqlBuilder<SqlQuery> implements SqlQuery, UpdateQuerySteps, DeleteStep, InsertStep {

	/**
	 * Adds a UPDATE clause to the SQL query.
	 * <p>
	 * The passed table is appended to the current SQL query.
	 * <p>
	 * Example: {@code query.update(employees))}
	 * <p>
	 * Expected SQL: {@code "UPDATE employees"}
	 *
	 * @param table the table to be added to the UPDATE clause.
	 * @return the current implementation of {@link UpdateStep}.
	 */
	@Override
	public UpdateStep update(DbTable table) {
		this.sql.append(UPDATE).append(table.getFullTableName());
		return this;
	}

	/**
	 * Adds a SET clause to the SQL query. This is meant to be used only after an UPDATE clause.
	 * <p>
	 * The passed fields are appended to the current SQL query in the format "field = ?". The passed values are added to
	 * the prepared statement's values list. The values are kept in the same order as they appeared in the SQL query and
	 * can be accessed using the {@link #getValues()} method.
	 * <p>
	 * Example: <pre>{@code
	 * query.update(employees)
	 *      .set(employees.name.value("John"), employees.age.value(30))}</pre>
	 * <p>
	 * Expected SQL: {@code "UPDATE employees SET name = ?, age = ?"}
	 * <p>
	 * Expected values list: {@code ["John", 30]}
	 *
	 * @param fields the fields to be added to the SET clause.
	 * @return the current implementation of {@link SetStep}.
	 */
	@Override
	public SetStep set(DbFieldValue<?>... fields) {

		StringJoiner joinedFields = new StringJoiner(", ");

		for (var field : fields) {
			StringBuilder sb = new StringBuilder();
			sb.append(field.getFieldName()).append(" = ?");
			this.values.add(field.getValue());
			joinedFields.add(sb);
		}
		this.sql.append(SET).append(joinedFields);
		return this;
	}

	/**
	 * Adds a DELETE FROM clause to the SQL query.
	 * <p>
	 * The passed table is appended to the current SQL query.
	 * <p>
	 * Example: {@code query.deleteFrom(employees)}
	 * <p>
	 * Expected SQL: {@code "DELETE FROM employees"}
	 *
	 * @param table the table to be added to the DELETE clause.
	 * @return the current implementation of {@link DeleteStep}.
	 */
	@Override
	public DeleteStep deleteFrom(DbTable table) {
		this.sql.append(DELETE_FROM).append(table.getFullTableName());
		return this;
	}

	/**
	 * Adds an INSERT INTO clause to the SQL query.
	 * <p>
	 * The passed table is appended to the current SQL query.
	 * <p>
	 * Example: {@code query.insertInto(employees)}
	 * <p>
	 * Expected SQL: {@code "INSERT INTO employees"}
	 *
	 * @param table the table to be added to the INSERT clause.
	 * @return the current implementation of {@link InsertStep}.
	 */
	@Override
	public InsertStep insertInto(DbTable table) {
		this.sql.append(INSERT_INTO).append(table.getFullTableName()).append(' ');
		return this;
	}

	/**
	 * Adds a VALUES clause to the SQL query. This is meant to be used only after an INSERT INTO clause.
	 * <p>
	 * The passed fields are appended to the current SQL query in the format "(field1, field2, ...)".
	 * <p>
	 * The values are appended in the format "(?, ?, ...)".
	 * <p>
	 * The passed values are added to the prepared statement's values list. The values are kept in the same order as
	 * they appeared in the SQL query and can be accessed using the {@link #getValues()} method.
	 * <p>
	 * Example: <pre>{@code
	 * query.insertInto(employees)
	 *      .values(employees.name.value("John"), employees.age.value(30))}</pre>
	 * <p>
	 * Expected SQL: {@code "INSERT INTO employees (name, age) VALUES (?, ?)"}
	 * <p>
	 * Expected values list: {@code ["John", 30]}
	 *
	 * @param fields the fields to be added to the VALUES clause.
	 * @return the current implementation of {@link PreparedSql}.
	 */
	@Override
	public PreparedSql values(DbFieldValue<?>... fields) {

		StringJoiner joinedFields = new StringJoiner(", ", "(", ")");
		StringJoiner joinedValues = new StringJoiner(", ", "(", ")");

		for (var field : fields) {
			joinedFields.add(field.getFieldName());
			this.values.add(field.getValue());
			joinedValues.add("?");
		}
		this.sql.append(joinedFields).append(VALUES).append(joinedValues);
		return this;
	}

	/**
	 * Get the current query as a {@link SqlQuery}.
	 * <p>
	 * The query object is not modified in the process, it's merely a way to obtain the {@link SqlQuery} from one of the
	 * SQL query Step interfaces. This may be useful for passing this query as an argument to other methods.
	 *
	 * @return the current query implementation as a {@link SqlQuery}.
	 */
	@Override
	public SqlQuery getQuery() {
		return this;
	}

}