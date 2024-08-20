package com.github.hyansts.preparedsqlbuilder.query;

public interface CombiningOperation<T> extends CombinableQuery<T> {
	UnionStep<T> union(CombinableQuery<T> query);

	UnionStep<T> unionAll(CombinableQuery<T> query);

	UnionStep<T> intersect(CombinableQuery<T> query);

	UnionStep<T> intersectAll(CombinableQuery<T> query);

	UnionStep<T> except(CombinableQuery<T> query);

	UnionStep<T> exceptAll(CombinableQuery<T> query);
}
