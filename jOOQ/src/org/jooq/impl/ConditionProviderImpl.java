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

import static org.jooq.impl.TrueCondition.TRUE_CONDITION;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.ConditionProvider;
import org.jooq.Field;
import org.jooq.Record;

/**
 * @author Lukas Eder
 */
class ConditionProviderImpl<R extends Record<R>> extends AbstractQueryPart implements ConditionProvider<R> {

	private static final long serialVersionUID = 6073328960551062973L;

	private Condition condition;

	ConditionProviderImpl() {
	}

	Condition getWhere() {
		if (condition == null) {
			return TRUE_CONDITION;
		}

		return condition;
	}

	@Override
	public void addConditions(Condition... conditions) {
		addConditions(Arrays.asList(conditions));
	}

	@Override
	public void addConditions(Collection<Condition> conditions) {
		if (!conditions.isEmpty()) {
			Condition c;

			if (conditions.size() == 1) {
				c = conditions.iterator().next();
			} else {
				c = Create.combinedCondition(conditions);
			}

			if (getWhere() == TRUE_CONDITION) {
				condition = c;
			} else {
				condition = Create.combinedCondition(getWhere(), c);
			}
		}
	}

	@Override
	public <T> void addBetweenCondition(Field<T> field, T minValue, T maxValue) {
		addConditions(Create.betweenCondition(field, minValue, maxValue));
	}

	@Override
	public <T> void addCompareCondition(Field<T> field, T value, Comparator comparator) {
		addConditions(Create.compareCondition(field, value, comparator));
	}

	@Override
	public <T> void addCompareCondition(Field<T> field, T value) {
		addConditions(Create.compareCondition(field, value));
	}

	@Override
	public void addNullCondition(Field<?> field) {
		addConditions(Create.nullCondition(field));
	}

	@Override
	public void addNotNullCondition(Field<?> field) {
		addConditions(Create.notNullCondition(field));
	}

	@Override
	public <T> void addInCondition(Field<T> field, Collection<T> values) {
		addConditions(Create.inCondition(field, values));
	}

	@Override
	public <T> void addInCondition(Field<T> field, T... values) {
		addConditions(Create.inCondition(field, values));
	}

	@Override
	public int bind(PreparedStatement stmt, int initialIndex) throws SQLException {
		return getWhere().bind(stmt, initialIndex);
	}

	@Override
	public String toSQLReference(boolean inlineParameters) {
		return getWhere().toSQLReference(inlineParameters);
	}
}
