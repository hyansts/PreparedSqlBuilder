package com.github.hyansts.preparedsqlbuilder.query;

public interface LimitStep extends CombiningOperation {
	CombiningOperation offset(Integer number);
}
