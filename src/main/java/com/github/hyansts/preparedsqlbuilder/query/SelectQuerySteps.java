package com.github.hyansts.preparedsqlbuilder.query;

public interface SelectQuerySteps<T> extends SelectStep<T>, FromStep<T>, JoinStep<T>, WhereStep<T>, GroupByStep<T>,
													 HavingStep<T>, OrderByStep<T>, LimitStep<T>, UnionStep<T> { }