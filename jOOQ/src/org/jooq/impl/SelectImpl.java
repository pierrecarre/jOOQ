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

import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.JoinType;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectFinalStep;
import org.jooq.SelectFromStep;
import org.jooq.SelectGroupByStep;
import org.jooq.SelectHavingStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectLimitStep;
import org.jooq.SelectOnStep;
import org.jooq.SelectOrderByStep;
import org.jooq.SelectQuery;
import org.jooq.SelectWhereStep;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.TableLike;

/**
 * A wrapper for a {@link SelectQuery}
 *
 * @author Lukas Eder
 */
class SelectImpl extends AbstractDelegatingResultProviderQuery<Record> implements

    // Cascading interface implementations for Select behaviour
    SelectFromStep, SelectJoinStep,
    SelectOnStep, SelectWhereStep, SelectGroupByStep,
    SelectHavingStep, SelectOrderByStep {

    /**
     * Generated UID
     */
    private static final long  serialVersionUID = -5425308887382166448L;

    /**
     * A temporary member holding a join
     */
    private transient Table<?> join;

    /**
     * A temporary member holding a join type
     */
    private transient JoinType joinType;

    SelectImpl(Configuration configuration) {
        this(configuration, false);
    }

    SelectImpl(Configuration configuration, boolean distinct) {
        this(configuration, new SelectQueryImpl(configuration, distinct));
    }

    SelectImpl(Configuration configuration, Select<Record> query) {
        super(configuration.getDialect(), query);
    }

    private final SelectQuery getQuery() {
        return (SelectQuery) query;
    }

    final SelectFromStep select(Field<?>... fields) {
        getQuery().addSelect(fields);
        return this;
    }

    final SelectFromStep select(Collection<Field<?>> fields) {
        getQuery().addSelect(fields);
        return this;
    }

    @Override
    public final SelectFromStep from(TableLike<?>... tables) {
        getQuery().addFrom(tables);
        return this;
    }

    @Override
    public final SelectJoinStep from(Collection<TableLike<?>> tables) {
        getQuery().addFrom(tables);
        return this;
    }

    @Override
    public final SelectGroupByStep where(Condition... conditions) {
        getQuery().addConditions(conditions);
        return this;
    }

    @Override
    public final SelectGroupByStep where(Collection<Condition> conditions) {
        getQuery().addConditions(conditions);
        return this;
    }

    @Override
    public final SelectHavingStep groupBy(Field<?>... fields) {
        getQuery().addGroupBy(fields);
        return this;
    }

    @Override
    public final SelectHavingStep groupBy(Collection<Field<?>> fields) {
        getQuery().addGroupBy(fields);
        return this;
    }

    @Override
    public final SelectLimitStep orderBy(Field<?>... fields) {
        getQuery().addOrderBy(fields);
        return this;
    }

    @Override
    public final SelectLimitStep orderBy(SortField<?>... fields) {
        getQuery().addOrderBy(fields);
        return this;
    }

    @Override
    public final SelectLimitStep orderBy(Collection<SortField<?>> fields) {
        getQuery().addOrderBy(fields);
        return this;
    }

    @Override
    public final SelectFinalStep limit(int numberOfRows) {
        getQuery().addLimit(numberOfRows);
        return this;
    }

    @Override
    public final SelectFinalStep limit(int lowerBound, int numberOfRows) {
        getQuery().addLimit(lowerBound, numberOfRows);
        return this;
    }

    @Override
    public final Select<Record> union(Select<Record> select) {
        return new SelectImpl(getConfiguration(), getQuery().union(select));
    }

    @Override
    public final Select<Record> unionAll(Select<Record> select) {
        return new SelectImpl(getConfiguration(), getQuery().unionAll(select));
    }

    @Override
    public final Select<Record> except(Select<Record> select) {
        return new SelectImpl(getConfiguration(), getQuery().except(select));
    }

    @Override
    public final Select<Record> intersect(Select<Record> select) {
        return new SelectImpl(getConfiguration(), getQuery().intersect(select));
    }

    @Override
    public final SelectOrderByStep having(Condition... conditions) {
        getQuery().addHaving(conditions);
        return this;
    }

    @Override
    public final SelectOrderByStep having(Collection<Condition> conditions) {
        getQuery().addHaving(conditions);
        return this;
    }

    @Override
    public final SelectJoinStep on(Condition... conditions) {
        getQuery().addJoin(join, joinType, conditions);
        join = null;
        joinType = null;
        return this;
    }

    @Override
    public final SelectOnStep join(Table<?> table) {
        join = table;
        joinType = JoinType.JOIN;
        return this;
    }

    @Override
    public final SelectOnStep leftJoin(Table<?> table) {
        join = table;
        joinType = JoinType.LEFT_JOIN;
        return this;
    }

    @Override
    public final SelectOnStep leftOuterJoin(Table<?> table) {
        join = table;
        joinType = JoinType.LEFT_OUTER_JOIN;
        return this;
    }

    @Override
    public final SelectOnStep rightJoin(Table<?> table) {
        join = table;
        joinType = JoinType.RIGHT_JOIN;
        return this;
    }

    @Override
    public final SelectOnStep rightOuterJoin(Table<?> table) {
        join = table;
        joinType = JoinType.RIGHT_OUTER_JOIN;
        return this;
    }
}
