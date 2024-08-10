package com.github.hyansts.preparedsqlbuilder.sql;

public enum SqlKeyword {
	SELECT("SELECT "),
	UPDATE("UPDATE "),
	DELETE_FROM("DELETE FROM "),
	INSERT_INTO("INSERT INTO "),
	VALUES(" VALUES "),
	WHERE(" WHERE "),
	FROM(" FROM "),
	INNER_JOIN(" INNER JOIN "),
	LEFT_JOIN(" LEFT JOIN "),
	RIGHT_JOIN(" RIGHT JOIN "),
	FULL_JOIN(" FULL JOIN "),
	CROSS_JOIN(" CROSS JOIN "),
	ON(" ON "),
	SET(" SET "),
	DISTINCT("DISTINCT "),
	ORDER_BY(" ORDER BY "),
	GROUP_BY(" GROUP BY "),
	HAVING(" HAVING "),
	LIMIT(" LIMIT "),
	OFFSET(" OFFSET "),
	UNION(" UNION "),
	UNION_ALL(" UNION ALL "),
	INTERSECT(" INTERSECT "),
	INTERSECT_ALL(" INTERSECT ALL "),
	EXCEPT(" EXCEPT "),
	EXCEPT_ALL(" EXCEPT ALL "),
	AS(" AS ");

	private final String keyword;

	SqlKeyword(String keyword) { this.keyword = keyword; }

	@Override
	public String toString() { return this.keyword; }
}
