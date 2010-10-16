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
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.FieldCondition;
import org.jooq.InCondition;
import org.jooq.ResultProviderSelectQuery;
import org.jooq.SQLDialect;
import org.jooq.SubQueryCondition;
import org.jooq.SubQueryOperator;

/**
 * @author Lukas Eder
 */
class FieldImpl<T> extends AbstractNamedTypeProviderQueryPart<T> implements Field<T> {

    private static final long serialVersionUID = 5589200289715501493L;

    FieldImpl(SQLDialect dialect, String name, Class<? extends T> type) {
        super(dialect, name, type);
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
        return new FieldAlias<T>(getDialect(), this, alias);
    }

    @Override
    public CompareCondition<T> isNull() {
        return create().nullCondition(this);
    }

    @Override
    public CompareCondition<T> isNotNull() {
        return create().notNullCondition(this);
    }

    @Override
    public CompareCondition<T> like(T value) {
        return create().compareCondition(this, value, Comparator.LIKE);
    }

    @Override
    public CompareCondition<T> notLike(T value) {
        return create().compareCondition(this, value, Comparator.NOT_LIKE);
    }

    @Override
    public InCondition<T> in(T... values) {
        return create().inCondition(this, values);
    }

    @Override
    public InCondition<T> in(Collection<T> values) {
        return create().inCondition(this, values);
    }

    @Override
    public SubQueryCondition<T> in(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asInCondition(this);
    }

    @Override
    public InCondition<T> notIn(T... values) {
        return create().notInCondition(this, values);
    }

    @Override
    public InCondition<T> notIn(Collection<T> values) {
        return create().notInCondition(this, values);
    }

    @Override
    public SubQueryCondition<T> notIn(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asNotInCondition(this);
    }

    @Override
    public BetweenCondition<T> between(T minValue, T maxValue) {
        return create().betweenCondition(this, minValue, maxValue);
    }

    @Override
    public CompareCondition<T> equal(T value) {
        return create().compareCondition(this, value);
    }

    @Override
    public Condition equal(Field<T> field) {
        return create().joinCondition(this, field);
    }

    @Override
    public FieldCondition<T> equal(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asCompareCondition(this);
    }

    @Override
    public FieldCondition<T> equalAny(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.EQUALS_ANY);
    }

    @Override
    public FieldCondition<T> equalSome(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.EQUALS_SOME);
    }

    @Override
    public FieldCondition<T> equalAll(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.EQUALS_ALL);
    }

    @Override
    public CompareCondition<T> notEqual(T value) {
        return create().compareCondition(this, value, Comparator.NOT_EQUALS);
    }

    @Override
    public Condition notEqual(Field<T> field) {
        return create().joinCondition(this, field, Comparator.NOT_EQUALS);
    }

    @Override
    public FieldCondition<T> notEqual(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.NOT_EQUALS);
    }

    @Override
    public FieldCondition<T> notEqualAny(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.NOT_EQUALS_ALL);
    }

    @Override
    public FieldCondition<T> notEqualSome(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.NOT_EQUALS_SOME);
    }

    @Override
    public FieldCondition<T> notEqualAll(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.NOT_EQUALS_ALL);
    }

    @Override
    public CompareCondition<T> lessThan(T value) {
        return create().compareCondition(this, value, Comparator.LESS);
    }

    @Override
    public Condition lessThan(Field<T> field) {
        return create().joinCondition(this, field, Comparator.LESS);
    }

    @Override
    public FieldCondition<T> lessThan(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS);
    }

    @Override
    public FieldCondition<T> lessThanAny(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS_THAN_ANY);
    }

    @Override
    public FieldCondition<T> lessThanSome(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS_THAN_SOME);
    }

    @Override
    public FieldCondition<T> lessThanAll(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS_THAN_ALL);
    }

    @Override
    public CompareCondition<T> lessOrEqual(T value) {
        return create().compareCondition(this, value, Comparator.LESS_OR_EQUAL);
    }

    @Override
    public Condition lessOrEqual(Field<T> field) {
        return create().joinCondition(this, field, Comparator.LESS_OR_EQUAL);
    }

    @Override
    public FieldCondition<T> lessOrEqual(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS_OR_EQUAL);
    }

    @Override
    public FieldCondition<T> lessOrEqualToAny(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS_OR_EQUAL_TO_ANY);
    }

    @Override
    public FieldCondition<T> lessOrEqualToSome(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS_OR_EQUAL_TO_SOME);
    }

    @Override
    public FieldCondition<T> lessOrEqualToAll(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS_OR_EQUAL_TO_ALL);
    }

    @Override
    public CompareCondition<T> greaterThan(T value) {
        return create().compareCondition(this, value, Comparator.GREATER);
    }

    @Override
    public Condition greaterThan(Field<T> field) {
        return create().joinCondition(this, field, Comparator.GREATER);
    }

    @Override
    public FieldCondition<T> greaterThan(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER);
    }

    @Override
    public FieldCondition<T> greaterThanAny(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER_THAN_ANY);
    }

    @Override
    public FieldCondition<T> greaterThanSome(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER_THAN_SOME);
    }

    @Override
    public FieldCondition<T> greaterThanAll(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER_THAN_ALL);
    }

    @Override
    public CompareCondition<T> greaterOrEqual(T value) {
        return create().compareCondition(this, value, Comparator.GREATER_OR_EQUAL);
    }

    @Override
    public Condition greaterOrEqual(Field<T> field) {
        return create().joinCondition(this, field, Comparator.GREATER_OR_EQUAL);
    }

    @Override
    public FieldCondition<T> greaterOrEqual(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER_OR_EQUAL);
    }

    @Override
    public FieldCondition<T> greaterOrEqualAny(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER_OR_EQUAL_TO_ANY);
    }

    @Override
    public FieldCondition<T> greaterOrEqualSome(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER_OR_EQUAL_TO_SOME);
    }

    @Override
    public FieldCondition<T> greaterOrEqualAll(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER_OR_EQUAL_TO_ALL);
    }
}
