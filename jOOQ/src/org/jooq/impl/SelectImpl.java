/**
 * Copyright (c) 2009-2011, Lukas Eder, lukas.eder@gmail.com
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
import org.jooq.Operator;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectFromStep;
import org.jooq.SelectHavingConditionStep;
import org.jooq.SelectHavingStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.SelectOnStep;
import org.jooq.SelectQuery;
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
    SelectFromStep, SelectOnStep,
    SelectOnConditionStep,
    SelectConditionStep, SelectHavingStep,
    SelectHavingConditionStep {

    /**
     * Generated UID
     */
    private static final long  serialVersionUID = -5425308887382166448L;

    /**
     * A temporary member holding a join table
     */
    private transient Table<?> joinTable;

    /**
     * A temporary member holding a join type
     */
    private transient JoinType joinType;

    /**
     * A temporary member holding a join condition
     */
    private transient ConditionProviderImpl joinConditions;

    /**
     * The step that is currently receiving new conditions
     */
    private transient ConditionStep conditionStep = ConditionStep.WHERE;

    SelectImpl(Configuration configuration) {
        this(configuration, false);
    }

    SelectImpl(Configuration configuration, boolean distinct) {
        this(configuration, new SelectQueryImpl(configuration, distinct));
    }

    SelectImpl(Configuration configuration, Select<Record> query) {
        super(configuration, query);
    }

    private final SelectQuery getQuery() {
        return (SelectQuery) query;
    }

    final SelectImpl select(Field<?>... fields) {
        getQuery().addSelect(fields);
        return this;
    }

    final SelectImpl select(Collection<Field<?>> fields) {
        getQuery().addSelect(fields);
        return this;
    }

    @Override
    public final SelectImpl from(TableLike<?>... tables) {
        getQuery().addFrom(tables);
        return this;
    }

    @Override
    public final SelectImpl from(Collection<TableLike<?>> tables) {
        getQuery().addFrom(tables);
        return this;
    }

    @Override
    public final SelectImpl from(String sql) {
        return from(create().plainSQLTable(sql));
    }

    @Override
    public final SelectImpl from(String sql, Object... bindings) {
        return from(create().plainSQLTable(sql, bindings));
    }

    @Override
    public final SelectImpl where(Condition... conditions) {
        conditionStep = ConditionStep.WHERE;
        getQuery().addConditions(conditions);
        return this;
    }

    @Override
    public final SelectImpl where(Collection<Condition> conditions) {
        conditionStep = ConditionStep.WHERE;
        getQuery().addConditions(conditions);
        return this;
    }

    @Override
    public final SelectImpl where(String sql) {
        return where(create().plainSQLCondition(sql));
    }

    @Override
    public final SelectImpl where(String sql, Object... bindings) {
        return where(create().plainSQLCondition(sql, bindings));
    }

    @Override
    public final SelectImpl whereExists(Select<?> select) {
        conditionStep = ConditionStep.WHERE;
        return andExists(select);
    }

    @Override
    public final SelectImpl whereNotExists(Select<?> select) {
        conditionStep = ConditionStep.WHERE;
        return andNotExists(select);
    }

    @Override
    public final SelectImpl and(Condition condition) {
        switch (conditionStep) {
            case WHERE:
                getQuery().addConditions(condition);
                break;
            case HAVING:
                getQuery().addHaving(condition);
                break;
            case ON:
                joinConditions.addConditions(condition);
                break;
        }

        return this;
    }

    @Override
    public final SelectImpl and(String sql) {
        return and(create().plainSQLCondition(sql));
    }

    @Override
    public final SelectImpl and(String sql, Object... bindings) {
        return and(create().plainSQLCondition(sql, bindings));
    }

    @Override
    public final SelectImpl andNot(Condition condition) {
        return and(condition.not());
    }

    @Override
    public final SelectImpl andExists(Select<?> select) {
        return and(create().exists(select));
    }

    @Override
    public final SelectImpl andNotExists(Select<?> select) {
        return and(create().notExists(select));
    }

    @Override
    public final SelectImpl or(Condition condition) {
        switch (conditionStep) {
            case WHERE:
                getQuery().addConditions(Operator.OR, condition);
                break;
            case HAVING:
                getQuery().addHaving(Operator.OR, condition);
                break;
            case ON:
                joinConditions.addConditions(Operator.OR, condition);
                break;
        }

        return this;
    }

    @Override
    public final SelectImpl or(String sql) {
        return or(create().plainSQLCondition(sql));
    }

    @Override
    public final SelectImpl or(String sql, Object... bindings) {
        return or(create().plainSQLCondition(sql, bindings));
    }

    @Override
    public final SelectImpl orNot(Condition condition) {
        return or(condition.not());
    }

    @Override
    public final SelectImpl orExists(Select<?> select) {
        return or(create().exists(select));
    }

    @Override
    public final SelectImpl orNotExists(Select<?> select) {
        return or(create().notExists(select));
    }

    @Override
    public final SelectImpl groupBy(Field<?>... fields) {
        getQuery().addGroupBy(fields);
        return this;
    }

    @Override
    public final SelectImpl groupBy(Collection<Field<?>> fields) {
        getQuery().addGroupBy(fields);
        return this;
    }

    @Override
    public final SelectImpl orderBy(Field<?>... fields) {
        getQuery().addOrderBy(fields);
        return this;
    }

    @Override
    public final SelectImpl orderBy(SortField<?>... fields) {
        getQuery().addOrderBy(fields);
        return this;
    }

    @Override
    public final SelectImpl orderBy(Collection<SortField<?>> fields) {
        getQuery().addOrderBy(fields);
        return this;
    }

    @Override
    public final SelectImpl limit(int numberOfRows) {
        getQuery().addLimit(numberOfRows);
        return this;
    }

    @Override
    public final SelectImpl limit(int lowerBound, int numberOfRows) {
        getQuery().addLimit(lowerBound, numberOfRows);
        return this;
    }

    @Override
    public final SelectImpl union(Select<Record> select) {
        return new SelectImpl(getConfiguration(), query.union(select));
    }

    @Override
    public final SelectImpl unionAll(Select<Record> select) {
        return new SelectImpl(getConfiguration(), query.unionAll(select));
    }

    @Override
    public final SelectImpl except(Select<Record> select) {
        return new SelectImpl(getConfiguration(), query.except(select));
    }

    @Override
    public final SelectImpl intersect(Select<Record> select) {
        return new SelectImpl(getConfiguration(), query.intersect(select));
    }

    @Override
    public final SelectImpl having(Condition... conditions) {
        conditionStep = ConditionStep.HAVING;
        getQuery().addHaving(conditions);
        return this;
    }

    @Override
    public final SelectImpl having(Collection<Condition> conditions) {
        conditionStep = ConditionStep.HAVING;
        getQuery().addHaving(conditions);
        return this;
    }

    @Override
    public final SelectImpl having(String sql) {
        return having(create().plainSQLCondition(sql));
    }

    @Override
    public final SelectImpl having(String sql, Object... bindings) {
        return having(create().plainSQLCondition(sql, bindings));
    }

    @Override
    public final SelectImpl on(Condition... conditions) {
        conditionStep = ConditionStep.ON;
        joinConditions = new ConditionProviderImpl(getConfiguration());
        joinConditions.addConditions(conditions);
        getQuery().addJoin(joinTable, joinType, joinConditions);
        joinTable = null;
        joinType = null;
        return this;
    }

    @Override
    public final SelectImpl on(String sql) {
        return on(create().plainSQLCondition(sql));
    }

    @Override
    public final SelectImpl on(String sql, Object... bindings) {
        return on(create().plainSQLCondition(sql, bindings));
    }

    @Override
    public final SelectImpl join(Table<?> table) {
        return join(table, JoinType.JOIN);
    }

    @Override
    @Deprecated
    public final SelectImpl leftJoin(Table<?> table) {
        return join(table, JoinType.LEFT_JOIN);
    }

    @Override
    public final SelectImpl leftOuterJoin(Table<?> table) {
        return join(table, JoinType.LEFT_OUTER_JOIN);
    }

    @Override
    @Deprecated
    public final SelectImpl rightJoin(Table<?> table) {
        return join(table, JoinType.RIGHT_JOIN);
    }

    @Override
    public final SelectImpl rightOuterJoin(Table<?> table) {
        return join(table, JoinType.RIGHT_OUTER_JOIN);
    }

    private final SelectImpl join(Table<?> table, JoinType type) {
        conditionStep = ConditionStep.ON;
        joinTable = table;
        joinType = type;
        joinConditions = null;
        return this;
    }

    @Override
    public final SelectImpl join(String sql) {
        return join(create().plainSQLTable(sql));
    }

    @Override
    public final SelectImpl join(String sql, Object... bindings) {
        return join(create().plainSQLTable(sql, bindings));
    }

    @Override
    @Deprecated
    public final SelectImpl leftJoin(String sql) {
        return leftJoin(create().plainSQLTable(sql));
    }

    @Override
    @Deprecated
    public final SelectImpl leftJoin(String sql, Object... bindings) {
        return leftJoin(create().plainSQLTable(sql, bindings));
    }

    @Override
    public final SelectImpl leftOuterJoin(String sql) {
        return leftOuterJoin(create().plainSQLTable(sql));
    }

    @Override
    public final SelectImpl leftOuterJoin(String sql, Object... bindings) {
        return leftOuterJoin(create().plainSQLTable(sql, bindings));
    }

    @Override
    @Deprecated
    public final SelectImpl rightJoin(String sql) {
        return rightJoin(create().plainSQLTable(sql));
    }

    @Override
    @Deprecated
    public final SelectImpl rightJoin(String sql, Object... bindings) {
        return rightJoin(create().plainSQLTable(sql, bindings));
    }

    @Override
    public final SelectImpl rightOuterJoin(String sql) {
        return rightOuterJoin(create().plainSQLTable(sql));
    }

    @Override
    public final SelectImpl rightOuterJoin(String sql, Object... bindings) {
        return rightOuterJoin(create().plainSQLTable(sql, bindings));
    }

    /**
     * The {@link SelectImpl} current condition step
     * <p>
     * This enumeration models the step that is currently receiving new
     * conditions via the {@link SelectImpl#and(Condition)},
     * {@link SelectImpl#or(Condition)}, etc methods
     *
     * @author Lukas Eder
     */
    private static enum ConditionStep {

        /**
         * Additional conditions go to the JOIN clause that is currently being
         * added.
         */
        ON,

        /**
         * Additional conditions go to the WHERE clause that is currently being
         * added.
         */
        WHERE,

        /**
         * Additional conditions go to the HAVING clause that is currently being
         * added.
         */
        HAVING
    }
}
