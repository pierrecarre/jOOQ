/**
 * Copyright (c) 2010, Lukas Eder, lukas.eder@gmail.com
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

import java.util.Collection;

import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.ResultProviderSelectQuery;
import org.jooq.SQLDialect;
import org.jooq.SubQueryOperator;

abstract class AbstractField<T> extends AbstractNamedTypeProviderQueryPart<T> implements Field<T> {

    /**
     * Generated UID
     */
    private static final long serialVersionUID = 2884811923648354905L;

    AbstractField(SQLDialect dialect, String name, Class<? extends T> type) {
        super(dialect, name, type);
    }

    @Override
    public Field<T> as(String alias) {
        return new FieldAlias<T>(getDialect(), this, alias);
    }

    // ------------------------------------------------------------------------
    // Convenience methods
    // ------------------------------------------------------------------------

    @Override
    public final Condition isNull() {
        return create().nullCondition(this);
    }

    @Override
    public final Condition isNotNull() {
        return create().notNullCondition(this);
    }

    @Override
    public final Condition like(T value) {
        return create().compareCondition(this, value, Comparator.LIKE);
    }

    @Override
    public final Condition notLike(T value) {
        return create().compareCondition(this, value, Comparator.NOT_LIKE);
    }

    @Override
    public final Condition in(T... values) {
        return create().inCondition(this, values);
    }

    @Override
    public final Condition in(Collection<T> values) {
        return create().inCondition(this, values);
    }

    @Override
    public final Condition in(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asInCondition(this);
    }

    @Override
    public final Condition notIn(T... values) {
        return create().notInCondition(this, values);
    }

    @Override
    public final Condition notIn(Collection<T> values) {
        return create().notInCondition(this, values);
    }

    @Override
    public final Condition notIn(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asNotInCondition(this);
    }

    @Override
    public final Condition between(T minValue, T maxValue) {
        return create().betweenCondition(this, minValue, maxValue);
    }

    @Override
    public final Condition equal(T value) {
        return create().compareCondition(this, value);
    }

    @Override
    public final Condition equal(Field<T> field) {
        return create().joinCondition(this, field);
    }

    @Override
    public final Condition equal(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asCompareCondition(this);
    }

    @Override
    public final Condition equalAny(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.EQUALS_ANY);
    }

    @Override
    public final Condition equalSome(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.EQUALS_SOME);
    }

    @Override
    public final Condition equalAll(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.EQUALS_ALL);
    }

    @Override
    public final Condition notEqual(T value) {
        return create().compareCondition(this, value, Comparator.NOT_EQUALS);
    }

    @Override
    public final Condition notEqual(Field<T> field) {
        return create().joinCondition(this, field, Comparator.NOT_EQUALS);
    }

    @Override
    public final Condition notEqual(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.NOT_EQUALS);
    }

    @Override
    public final Condition notEqualAny(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.NOT_EQUALS_ALL);
    }

    @Override
    public final Condition notEqualSome(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.NOT_EQUALS_SOME);
    }

    @Override
    public final Condition notEqualAll(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.NOT_EQUALS_ALL);
    }

    @Override
    public final Condition lessThan(T value) {
        return create().compareCondition(this, value, Comparator.LESS);
    }

    @Override
    public final Condition lessThan(Field<T> field) {
        return create().joinCondition(this, field, Comparator.LESS);
    }

    @Override
    public final Condition lessThan(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS);
    }

    @Override
    public final Condition lessThanAny(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS_THAN_ANY);
    }

    @Override
    public final Condition lessThanSome(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS_THAN_SOME);
    }

    @Override
    public final Condition lessThanAll(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS_THAN_ALL);
    }

    @Override
    public final Condition lessOrEqual(T value) {
        return create().compareCondition(this, value, Comparator.LESS_OR_EQUAL);
    }

    @Override
    public final Condition lessOrEqual(Field<T> field) {
        return create().joinCondition(this, field, Comparator.LESS_OR_EQUAL);
    }

    @Override
    public final Condition lessOrEqual(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS_OR_EQUAL);
    }

    @Override
    public final Condition lessOrEqualToAny(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS_OR_EQUAL_TO_ANY);
    }

    @Override
    public final Condition lessOrEqualToSome(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS_OR_EQUAL_TO_SOME);
    }

    @Override
    public final Condition lessOrEqualToAll(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.LESS_OR_EQUAL_TO_ALL);
    }

    @Override
    public final Condition greaterThan(T value) {
        return create().compareCondition(this, value, Comparator.GREATER);
    }

    @Override
    public final Condition greaterThan(Field<T> field) {
        return create().joinCondition(this, field, Comparator.GREATER);
    }

    @Override
    public final Condition greaterThan(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER);
    }

    @Override
    public final Condition greaterThanAny(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER_THAN_ANY);
    }

    @Override
    public final Condition greaterThanSome(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER_THAN_SOME);
    }

    @Override
    public final Condition greaterThanAll(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER_THAN_ALL);
    }

    @Override
    public final Condition greaterOrEqual(T value) {
        return create().compareCondition(this, value, Comparator.GREATER_OR_EQUAL);
    }

    @Override
    public final Condition greaterOrEqual(Field<T> field) {
        return create().joinCondition(this, field, Comparator.GREATER_OR_EQUAL);
    }

    @Override
    public final Condition greaterOrEqual(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER_OR_EQUAL);
    }

    @Override
    public final Condition greaterOrEqualAny(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER_OR_EQUAL_TO_ANY);
    }

    @Override
    public final Condition greaterOrEqualSome(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER_OR_EQUAL_TO_SOME);
    }

    @Override
    public final Condition greaterOrEqualAll(ResultProviderSelectQuery<?, ?> query) {
        return query.getQuery().asSubQueryCondition(this, SubQueryOperator.GREATER_OR_EQUAL_TO_ALL);
    }
}
