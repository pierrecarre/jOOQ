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
import java.sql.SQLException;
import java.util.Collection;

import org.jooq.BetweenCondition;
import org.jooq.Comparator;
import org.jooq.CompareCondition;
import org.jooq.Field;
import org.jooq.FieldCondition;
import org.jooq.InCondition;
import org.jooq.JoinCondition;
import org.jooq.QueryProvider;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.jooq.SubQueryCondition;
import org.jooq.SubQueryOperator;

/**
 * @author Lukas Eder
 */
class FieldImpl<T> extends AbstractNamedTypeProviderQueryPart<T> implements Field<T> {

	private static final long serialVersionUID = 5589200289715501493L;

	FieldImpl(String name, Class<T> type) {
		super(name, type);
	}

	@Override
	public int bind(PreparedStatement stmt, int initialIndex) throws SQLException {
		return initialIndex;
	}

	@Override
	public String toSQLReference(boolean inlineParameters) {
		return getName();
	}

	// ------------------------------------------------------------------------
	// Convenience methods
	// ------------------------------------------------------------------------

	@Override
	public Field<T> as(String alias) {
		return new FieldAlias<T>(this, alias);
	}

	@Override
	public CompareCondition<T> isNull() {
		return Create.nullCondition(this);
	}

	@Override
	public CompareCondition<T> isNotNull() {
		return Create.notNullCondition(this);
	}

	@Override
	public CompareCondition<T> like(T value) {
		return Create.compareCondition(this, value, Comparator.LIKE);
	}

	@Override
	public CompareCondition<T> notLike(T value) {
		return Create.compareCondition(this, value, Comparator.NOT_LIKE);
	}

	@Override
	public InCondition<T> in(T... values) {
		return Create.inCondition(this, values);
	}

	@Override
	public InCondition<T> in(Collection<T> values) {
		return Create.inCondition(this, values);
	}

	@Override
	public <R extends Record> SubQueryCondition<T> in(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asInCondition(this);
	}

	@Override
	public InCondition<T> notIn(T... values) {
		return Create.notInCondition(this, values);
	}

	@Override
	public InCondition<T> notIn(Collection<T> values) {
		return Create.notInCondition(this, values);
	}

	@Override
	public <R extends Record> SubQueryCondition<T> notIn(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asNotInCondition(this);
	}

	@Override
	public BetweenCondition<T> between(T minValue, T maxValue) {
		return Create.betweenCondition(this, minValue, maxValue);
	}

	@Override
	public CompareCondition<T> equal(T value) {
		return Create.compareCondition(this, value);
	}

	@Override
	public JoinCondition<T> equal(Field<T> field) {
		return Create.joinCondition(this, field);
	}

	@Override
	public <R extends Record> FieldCondition<T> equal(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asCompareCondition(this);
	}

	@Override
	public <R extends Record> FieldCondition<T> equalAny(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asSubQueryCondition(this, SubQueryOperator.EQUALS_ANY);
	}

	@Override
	public <R extends Record> FieldCondition<T> equalSome(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asSubQueryCondition(this, SubQueryOperator.EQUALS_SOME);
	}

	@Override
	public <R extends Record> FieldCondition<T> equalAll(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asSubQueryCondition(this, SubQueryOperator.EQUALS_ALL);
	}

	@Override
	public CompareCondition<T> notEqual(T value) {
		return Create.compareCondition(this, value, Comparator.NOT_EQUALS);
	}

	@Override
	public JoinCondition<T> notEqual(Field<T> field) {
		return Create.joinCondition(this, field, Comparator.NOT_EQUALS);
	}

	@Override
	public <R extends Record> FieldCondition<T> notEqual(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asSubQueryCondition(this, SubQueryOperator.NOT_EQUALS);
	}

	@Override
	public <R extends Record> FieldCondition<T> notEqualAny(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asSubQueryCondition(this, SubQueryOperator.NOT_EQUALS_ALL);
	}

	@Override
	public <R extends Record> FieldCondition<T> notEqualSome(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asSubQueryCondition(this, SubQueryOperator.NOT_EQUALS_SOME);
	}

	@Override
	public <R extends Record> FieldCondition<T> notEqualAll(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asSubQueryCondition(this, SubQueryOperator.NOT_EQUALS_ALL);
	}

	@Override
	public CompareCondition<T> lessThan(T value) {
		return Create.compareCondition(this, value, Comparator.LESS);
	}

	@Override
	public JoinCondition<T> lessThan(Field<T> field) {
		return Create.joinCondition(this, field, Comparator.LESS);
	}

	@Override
	public <R extends Record> FieldCondition<T> lessThan(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS);
	}

	@Override
	public <R extends Record> FieldCondition<T> lessThanAny(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS_THAN_ANY);
	}

	@Override
	public <R extends Record> FieldCondition<T> lessThanSome(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS_THAN_SOME);
	}

	@Override
	public <R extends Record> FieldCondition<T> lessThanAll(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS_THAN_ALL);
	}

	@Override
	public CompareCondition<T> lessOrEqual(T value) {
		return Create.compareCondition(this, value, Comparator.LESS_OR_EQUAL);
	}

	@Override
	public JoinCondition<T> lessOrEqual(Field<T> field) {
		return Create.joinCondition(this, field, Comparator.LESS_OR_EQUAL);
	}

	@Override
	public <R extends Record> FieldCondition<T> lessOrEqual(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS_OR_EQUAL);
	}

	@Override
	public <R extends Record> FieldCondition<T> lessOrEqualToAny(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS_OR_EQUAL_TO_ANY);
	}

	@Override
	public <R extends Record> FieldCondition<T> lessOrEqualToSome(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS_OR_EQUAL_TO_SOME);
	}

	@Override
	public <R extends Record> FieldCondition<T> lessOrEqualToAll(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS_OR_EQUAL_TO_ALL);
	}

	@Override
	public CompareCondition<T> greaterThan(T value) {
		return Create.compareCondition(this, value, Comparator.GREATER);
	}

	@Override
	public JoinCondition<T> greaterThan(Field<T> field) {
		return Create.joinCondition(this, field, Comparator.GREATER);
	}

	@Override
	public <R extends Record> FieldCondition<T> greaterThan(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER);
	}

	@Override
	public <R extends Record> FieldCondition<T> greaterThanAny(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER_THAN_ANY);
	}

	@Override
	public <R extends Record> FieldCondition<T> greaterThanSome(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER_THAN_SOME);
	}

	@Override
	public <R extends Record> FieldCondition<T> greaterThanAll(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER_THAN_ALL);
	}

	@Override
	public CompareCondition<T> greaterOrEqual(T value) {
		return Create.compareCondition(this, value, Comparator.GREATER_OR_EQUAL);
	}

	@Override
	public JoinCondition<T> greaterOrEqual(Field<T> field) {
		return Create.joinCondition(this, field, Comparator.GREATER_OR_EQUAL);
	}

	@Override
	public <R extends Record> FieldCondition<T> greaterOrEqual(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER_OR_EQUAL);
	}

	@Override
	public <R extends Record> FieldCondition<T> greaterOrEqualAny(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER_OR_EQUAL_TO_ANY);
	}

	@Override
	public <R extends Record> FieldCondition<T> greaterOrEqualSome(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER_OR_EQUAL_TO_SOME);
	}

	@Override
	public <R extends Record> FieldCondition<T> greaterOrEqualAll(QueryProvider<SelectQuery<R>> query) {
		return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER_OR_EQUAL_TO_ALL);
	}
}
