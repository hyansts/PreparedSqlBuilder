package com.github.hyansts.preparedsqlbuilder.query.impl;

import com.github.hyansts.preparedsqlbuilder.query.SqlQuery;
import com.github.hyansts.preparedsqlbuilder.query.SqlScalarSubquery;
import com.github.hyansts.preparedsqlbuilder.query.SqlSubquery;

public abstract class SqlQueryFactory {
	/**
	 * Creates a new SQL query builder.
	 * <p>
	 * The returned builder can be used to build an SQL query.
	 * <p>
	 * Example:
	 * <pre>{@code
	 * SqlQuery query = SqlQueryFactory.createQuery();
	 * String sql = query.select(employees.id, employees.name)
	 *              .from(employees.as("emp"))
	 *              .where(employees.age.gt(18))
	 *              .orderBy(employees.name)
	 *              .getSql();}
	 * </pre>
	 * Expected SQL:
	 * <p>
	 * {@code "SELECT emp.id, emp.name FROM employees AS emp WHERE emp.age > 18 ORDER BY emp.name"}
	 *
	 * @return a new SQL query builder.
	 */
	public static SqlQuery createQuery() {
		return new SqlQueryBuilder();
	}

	/**
	 * Creates a new SQL subquery builder.
	 * <p>
	 * The returned builder can be used to build an SQL derived table subquery. This type of subquery results in a
	 * table-like structure that can be referenced in an outer query.
	 * <p>
	 * Example:
	 * <pre>{@code
	 * SqlQuery query = SqlQueryFactory.createQuery();
	 * SqlSubquery subquery = SqlQueryFactory.createSubquery();
	 *
	 * query.select(subquery.getField(maxAge), dep.title)
	 *      .from(subquery.select(emp.department_id, maxAge.as("max_age"))
	 *                    .from(emp)
	 *                    .groupBy(emp.department_id)
	 *                    .getQuery().as("sub"))
	 *      .innerJoin(dep.as("dep")).on(subquery.getField(emp.department_id).eq(dep.id));}
	 * </pre>
	 * Expected SQL:
	 * <p>
	 * <pre>{@code
	 * SELECT sub.max_age, dep.title
	 * FROM (SELECT department_id, MAX(age) AS max_age
	 *       FROM employees
	 *       GROUP BY department_id) AS sub
	 * INNER JOIN department AS dep ON sub.department_id = dep.id}
	 * </pre>
	 *
	 * @return a new SQL subquery builder.
	 */
	public static SqlSubquery createSubquery() {
		return new SqlSubqueryBuilder();
	}

	/**
	 * Creates a new SQL scalar subquery builder.
	 * <p>
	 * The returned builder can be used to build an SQL scalar subquery. This type of subquery results in a single value,
	 * and it's considered a field-like structure that can be referenced in the outer query.
	 * <p>
	 * Example:
	 * <pre>{@code
	 * SqlQuery query = SqlQueryFactory.createQuery();
	 * SqlScalarSubquery<Long> subquery = SqlQueryFactory.createScalarSubquery();
	 *
	 * query.select(emp.id,
	 *              subquery.select(dep.admin_id.max().as("max_adm_id"))
	 *                      .from(dep)
	 *                      .where(dep.id.le(10))
	 *                      .getQuery())
	 *       .from(emp)
	 *       .where(emp.id.eq(20));}
	 * </pre>
	 * Expected SQL:
	 * <p>
	 * <pre>{@code
	 * SELECT id, (SELECT MAX(admin_id) AS max_adm_id
	 *             FROM department
	 *             WHERE id <= ?)
	 * FROM employees
	 * WHERE id = ?}
	 * </pre>
	 *
	 * @param <T> the type of the scalar subquery.
	 * @return a new SQL scalar subquery builder.
	 */
	public static <T> SqlScalarSubquery<T> createScalarSubquery() {
		return new SqlScalarSubqueryBuilder<>();
	}

}