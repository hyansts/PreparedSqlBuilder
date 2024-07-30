package com.github.hyansts.preparedsqlbuilder.query;

public interface CombiningOperation extends CombinableQuery {
	UnionStep union(CombinableQuery query);

	UnionStep unionAll(CombinableQuery query);

	UnionStep intersect(CombinableQuery query);

	UnionStep intersectAll(CombinableQuery query);

	UnionStep except(CombinableQuery query);

	UnionStep exceptAll(CombinableQuery query);
}
