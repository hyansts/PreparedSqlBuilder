package com.github.hyansts.preparedsqlbuilder.query;

public interface LimitStep<T> extends CombiningOperation<T> {
	CombiningOperation<T> offset(Integer number);
}