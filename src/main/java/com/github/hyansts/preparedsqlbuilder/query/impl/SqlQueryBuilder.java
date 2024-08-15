package com.github.hyansts.preparedsqlbuilder.query.impl;

import java.util.StringJoiner;

import com.github.hyansts.preparedsqlbuilder.db.DbFieldValue;
import com.github.hyansts.preparedsqlbuilder.db.DbTable;
import com.github.hyansts.preparedsqlbuilder.query.DeleteStep;
import com.github.hyansts.preparedsqlbuilder.query.InsertStep;
import com.github.hyansts.preparedsqlbuilder.query.PreparedSql;
import com.github.hyansts.preparedsqlbuilder.query.SetStep;
import com.github.hyansts.preparedsqlbuilder.query.SqlQuery;
import com.github.hyansts.preparedsqlbuilder.query.UpdateQuerySteps;
import com.github.hyansts.preparedsqlbuilder.query.UpdateStep;

import static com.github.hyansts.preparedsqlbuilder.sql.SqlConditionOperator.EQ;
import static com.github.hyansts.preparedsqlbuilder.sql.SqlKeyword.*;

class SqlQueryBuilder extends BaseSqlBuilder implements SqlQuery, UpdateQuerySteps, DeleteStep, InsertStep {

	private boolean chainNextSetClause = false;

	@Override
	public UpdateStep update(DbTable table) {
		this.sql.append(UPDATE).append(table.getFullTableName());
		return this;
	}

	//TODO change to take varargs instead
	@Override
	public SetStep set(DbFieldValue<?> field) {
		this.sql.append(this.chainNextSetClause ? ", " : SET).append(field.getFieldName()).append(EQ);
		this.chainNextSetClause = true;
		if (field.getValue() == null) {
			//FIXME this might break preparedStatement batch execution
			this.sql.append("null");
			return this;
		}
		this.values.add(field.getValue());
		this.sql.append('?');
		return this;
	}

	@Override
	public DeleteStep deleteFrom(DbTable table) {
		this.sql.append(DELETE_FROM).append(table.getFullTableName());
		return this;
	}

	@Override
	public InsertStep insertInto(DbTable table) {
		this.sql.append(INSERT_INTO).append(table.getFullTableName()).append(' ');
		return this;
	}

	@Override
	public PreparedSql values(DbFieldValue<?>... fields) {

		StringJoiner joinedFields = new StringJoiner(", ", "(", ")");
		StringJoiner joinedValues = new StringJoiner(", ", "(", ")");

		for (var field : fields) {
			joinedFields.add(field.getFieldName());
			if (field.getValue() == null) {
				//FIXME this might break preparedStatement batch execution
				joinedValues.add("null");
				continue;
			}
			this.values.add(field.getValue());
			joinedValues.add("?");
		}
		this.sql.append(joinedFields).append(VALUES).append(joinedValues);
		return this;
	}

}