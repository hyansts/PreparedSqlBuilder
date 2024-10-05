package com.github.hyansts.preparedsqlbuilder.sql;

/**
 * An enumeration of SQL aggregate functions.
 *
 * <p>
 * Each aggregation function can be applied to a field name by using the {@link #applyTo(String)} method, which returns a string
 * representation of the aggregation function to be applied to the given field.
 */
public enum SqlAggregator {
	AVG, MAX, MIN, SUM, COUNT;

	/**
	 * Applies the current aggregation function to the given field name.
	 *
	 * @param field the field name
	 * @return the string representation of the aggregation function to be applied to the given field
	 */
	public String applyTo(String field) { return this + "(" + field + ")"; }

}