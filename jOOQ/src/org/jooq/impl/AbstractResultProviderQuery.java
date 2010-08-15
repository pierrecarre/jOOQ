/**
 * Copyright (c) 2009, Lukas Eder, lukas.eder@gmail.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * . Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * . Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * . Neither the name of the "jOOQ" nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.jooq.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jooq.CombineOperator;
import org.jooq.ExistsCondition;
import org.jooq.ExistsOperator;
import org.jooq.Field;
import org.jooq.FieldList;
import org.jooq.Result;
import org.jooq.ResultProviderQuery;
import org.jooq.SubQueryCondition;
import org.jooq.SubQueryOperator;
import org.jooq.Table;

/**
 * @author Lukas Eder
 */
abstract class AbstractResultProviderQuery extends AbstractQuery implements ResultProviderQuery {

	private static final long serialVersionUID = 1555503854543561285L;

	private ResultImpl result;

	@Override
	protected final int execute(PreparedStatement statement) throws SQLException {
		ResultSet rs = null;

		try {
			rs = statement.executeQuery();
			result = new ResultImpl(this);

			while (rs.next()) {
				RecordImpl record = new RecordImpl(result);

				for (Field<?> field : getSelect()) {
					Object value = FieldTypeHelper.getFromResultSet(rs, field);
					record.addValue(field, value);
				}

				result.addRecord(record);
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}

		return result.getNumberOfRecords();
	}

	protected abstract FieldList getSelect();

	@Override
	public final Result getResult() {
		return result;
	}

	@Override
	public final ResultProviderQuery combine(ResultProviderQuery other) {
		return combine(other, CombineOperator.UNION);
	}

	@Override
	public final ResultProviderQuery combine(ResultProviderQuery other, CombineOperator operator) {
		return new ResultProviderQueryImpl(this, other, operator);
	}

	@Override
	public final Table asTable() {
		return new ResultProviderQueryAsTable(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <T> Field<T> asField() {
		if (getSelect().size() != 1) {
			throw new IllegalStateException("Can only use single-column ResultProviderQuery as a field");
		}

		return new ResultProviderQueryAsField<T>(this, (Class<T>) getSelect().get(0).getType());
	}

	@Override
	public final <T> SubQueryCondition<T> asInCondition(Field<T> field) {
		return asSubQueryCondition(field, SubQueryOperator.IN);
	}

	@Override
	public final <T> SubQueryCondition<T> asNotInCondition(Field<T> field) {
		return asSubQueryCondition(field, SubQueryOperator.NOT_IN);
	}

	@Override
	public final <T> SubQueryCondition<T> asCompareCondition(Field<T> field) {
		return asSubQueryCondition(field, SubQueryOperator.EQUALS);
	}

	@Override
	public final <T> SubQueryCondition<T> asSubQueryCondition(Field<T> field, SubQueryOperator operator) {
		if (getSelect().size() != 1) {
			throw new IllegalStateException("Can only use single-column ResultProviderQuery as an InCondition");
		}

		return new ResultProviderQueryAsSubQueryCondition<T>(this, field, operator);
	}

	@Override
	public final ExistsCondition asExistsCondition() {
		return asExistsCondition(ExistsOperator.EXISTS);
	}

	@Override
	public final ExistsCondition asNotExistsCondition() {
		return asExistsCondition(ExistsOperator.NOT_EXISTS);
	}

	private final ExistsCondition asExistsCondition(ExistsOperator operator) {
		if (getSelect().size() != 1) {
			throw new IllegalStateException("Can only use single-column ResultProviderQuery as an InCondition");
		}

		return new ResultProviderQueryAsExistsCondition(this, operator);
	}
}
