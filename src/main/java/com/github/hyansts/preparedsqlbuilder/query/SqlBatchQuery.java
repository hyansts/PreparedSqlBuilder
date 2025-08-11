package com.github.hyansts.preparedsqlbuilder.query;

import java.util.List;

public interface SqlBatchQuery {

	SqlBatchQuery addBatch(Object... values);

	List<Object[]> getValuesBatch();

	String getSql();

}