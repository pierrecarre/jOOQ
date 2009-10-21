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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.jooq.BetweenCondition;
import org.jooq.CombinedCondition;
import org.jooq.Comparator;
import org.jooq.CompareCondition;
import org.jooq.Condition;
import org.jooq.DeleteQuery;
import org.jooq.Field;
import org.jooq.InCondition;
import org.jooq.InsertQuery;
import org.jooq.Join;
import org.jooq.JoinCondition;
import org.jooq.JoinType;
import org.jooq.Operator;
import org.jooq.SelectQuery;
import org.jooq.Table;
import org.jooq.UpdateQuery;

/**
 * A factory providing implementations to the org.jooq interfaces
 * 
 * @author Lukas Eder
 */
public final class QueryFactory {

	public static CombinedCondition createCombinedCondition(Condition... conditions) {
		return createCombinedCondition(Operator.AND, conditions);
	}

	public static CombinedCondition createCombinedCondition(Collection<Condition> conditions) {
		return createCombinedCondition(Operator.AND, conditions);
	}

	public static CombinedCondition createCombinedCondition(Operator operator, Condition... conditions) {
		return createCombinedCondition(operator, Arrays.asList(conditions));
	}

	public static CombinedCondition createCombinedCondition(Operator operator, Collection<Condition> conditions) {
		return new CombinedConditionImpl(operator, conditions);
	}

	public static <T> BetweenCondition<T> createBetweenCondition(Field<T> field, T minValue, T maxValue) {
		return new BetweenConditionImpl<T>(field, minValue, maxValue);
	}

	public static <T> InCondition<T> createInCondition(Field<T> field, T... values) {
		return createInCondition(field, Arrays.asList(values));
	}

	public static <T> InCondition<T> createInCondition(Field<T> field, Collection<T> values) {
		return new InConditionImpl<T>(field, new LinkedHashSet<T>(values));
	}

	public static <T> CompareCondition<T> createCompareCondition(Field<T> field, T value) {
		return createCompareCondition(field, value, Comparator.EQUALS);
	}

	public static <T> CompareCondition<T> createCompareCondition(Field<T> field, T value, Comparator comparator) {
		return new CompareConditionImpl<T>(field, value, comparator);
	}
	
	public static <T> JoinCondition<T> createJoinCondition(Field<T> field1, Field<T> field2) {
		return new JoinConditionImpl<T>(field1, field2);
	}

	public static InsertQuery createInsertQuery(Table into) {
		return new InsertQueryImpl(into);
	}

	public static UpdateQuery createUpdateQuery(Table table) {
		return new UpdateQueryImpl(table);
	}

	public static DeleteQuery createDeleteQuery(Table table) {
		return new DeleteQueryImpl(table);
	}
	
	public static SelectQuery createSelectQuery() {
		return createSelectQuery(null);
	}
	
	public static SelectQuery createSelectQuery(Table table) {
		return new SelectQueryImpl(table);
	}

	public static <T> Join createJoin(Table table, Field<T> field1, Field<T> field2) {
		return createJoin(table, field1, field2, JoinType.JOIN);
	}
	
	public static Join createJoin(Table table, JoinCondition<?> condition) {
		return createJoin(table, condition, JoinType.JOIN);
	}
	
	public static <T> Join createJoin(Table table, Field<T> field1, Field<T> field2, JoinType type) {
		return createJoin(table, createJoinCondition(field1, field2), type);
	}
	
	public static Join createJoin(Table table, JoinCondition<?> condition, JoinType type) {
		return new JoinImpl(table, condition, type);
	}

	public static <T> Join createJoin(Table table) {
		return createJoin(table, null, JoinType.JOIN);
	}
	
	/**
	 * No instances
	 */
	private QueryFactory() {
	}
}
