package com.github.hyansts.preparedsqlbuilder.query.impl;

import java.util.ArrayList;
import java.util.List;

import com.github.hyansts.preparedsqlbuilder.query.SqlBatchQuery;

class SqlBatchBuilder implements SqlBatchQuery {

	private final String sql;
	private final List<Object[]> valuesBatch = new ArrayList<>();

	SqlBatchBuilder(String sql) {
		this.sql = sql;
	}

	@Override
	public SqlBatchQuery addBatch(Object... values) {
		this.valuesBatch.add(values);
		return this;
	}

	@Override
	public List<Object[]> getValuesBatch() {
		return this.valuesBatch;
	}

	@Override
	public String getSql() {
		return this.sql;
	}

	@Override
	public String toString() {
		return getSql();
	}

}