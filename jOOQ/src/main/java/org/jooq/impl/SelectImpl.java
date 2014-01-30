/**
 * Copyright (c) 2009-2013, Data Geekery GmbH (http://www.datageekery.com)
 * All rights reserved.
 *
 * This work is dual-licensed
 * - under the Apache Software License 2.0 (the "ASL")
 * - under the jOOQ License and Maintenance Agreement (the "jOOQ License")
 * =============================================================================
 * You may choose which license applies to you:
 *
 * - If you're using this work with Open Source databases, you may choose
 *   either ASL or jOOQ License.
 * - If you're using this work with at least one commercial database, you must
 *   choose jOOQ License
 *
 * For more information, please visit http://www.jooq.org/licenses
 *
 * Apache Software License 2.0:
 * -----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * jOOQ License and Maintenance Agreement:
 * -----------------------------------------------------------------------------
 * Data Geekery grants the Customer the non-exclusive, timely limited and
 * non-transferable license to install and use the Software under the terms of
 * the jOOQ License and Maintenance Agreement.
 *
 * This library is distributed with a LIMITED WARRANTY. See the jOOQ License
 * and Maintenance Agreement for more details: http://www.jooq.org/licensing
 */
package org.jooq.impl;

import static org.jooq.impl.DSL.condition;
import static org.jooq.impl.DSL.exists;
import static org.jooq.impl.DSL.notExists;
import static org.jooq.impl.DSL.table;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Converter;
import org.jooq.Cursor;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.GroupField;
import org.jooq.JoinType;
import org.jooq.Operator;
import org.jooq.Param;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.RecordHandler;
import org.jooq.RecordMapper;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.jooq.Row;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectConnectByConditionStep;
import org.jooq.SelectForUpdateOfStep;
import org.jooq.SelectHavingConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectOffsetStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.SelectOnStep;
import org.jooq.SelectOptionalOnStep;
import org.jooq.SelectQuery;
import org.jooq.SelectSelectStep;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableLike;
import org.jooq.exception.DataAccessException;

/**
 * A wrapper for a {@link SelectQuery}
 *
 * @author Lukas Eder
 */
class SelectImpl<R extends Record> extends AbstractDelegatingQuery<Select<R>> implements

    // Cascading interface implementations for Select behaviour
    SelectSelectStep<R>,
    SelectOptionalOnStep<R>,
    SelectOnConditionStep<R>,
    SelectConditionStep<R>,
    SelectConnectByConditionStep<R>,
    SelectHavingConditionStep<R>,
    SelectOffsetStep<R>,
    SelectForUpdateOfStep<R> {

    /**
     * Generated UID
     */
    private static final long               serialVersionUID = -5425308887382166448L;

    /**
     * A temporary member holding a join table
     */
    private transient TableLike<?>          joinTable;

    /**
     * A temporary member holding a join partition by expression
     */
    private transient Field<?>[]            joinPartitionBy;

    /**
     * A temporary member holding a join type
     */
    private transient JoinType              joinType;

    /**
     * A temporary member holding a join condition
     */
    private transient ConditionProviderImpl joinConditions;

    /**
     * The step that is currently receiving new conditions
     */
    private transient ConditionStep         conditionStep;

    /**
     * The limit that has been added in a limit(int).offset(int) construct
     */
    private transient Integer               limit;
    private transient Param<Integer>        limitParam;

    SelectImpl(Configuration configuration) {
        this(configuration, false);
    }

    SelectImpl(Configuration configuration, boolean distinct) {
        this(new SelectQueryImpl<R>(configuration, distinct));
    }

    SelectImpl(Select<R> query) {
        super(query);
    }

    
    public final SelectQuery<R> getQuery() {
        return (SelectQuery<R>) getDelegate();
    }

    
    public final int fetchCount() {
        return getDelegate().fetchCount();
    }

    /**
     * This method must be able to return both incompatible types
     * SelectSelectStep&lt;Record> and SelectSelectStep&lt;R>
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    
    public final SelectImpl select(Field<?>... fields) {
        getQuery().addSelect(fields);
        return this;
    }

    /**
     * This method must be able to return both incompatible types
     * SelectSelectStep&lt;Record> and SelectSelectStep&lt;R>
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    
    public final SelectImpl select(Collection<? extends Field<?>> fields) {
        getQuery().addSelect(fields);
        return this;
    }

    
    public final SelectImpl<R> hint(String hint) {
        getQuery().addHint(hint);
        return this;
    }

    
    public final SelectImpl<R> option(String hint) {
        getQuery().addOption(hint);
        return this;
    }

    
    public final SelectImpl<R> from(TableLike<?>... tables) {
        getQuery().addFrom(tables);
        return this;
    }

    
    public final SelectImpl<R> from(Collection<? extends TableLike<?>> tables) {
        getQuery().addFrom(tables);
        return this;
    }

    
    public final SelectImpl<R> from(String sql) {
        return from(table(sql));
    }

    
    public final SelectImpl<R> from(String sql, Object... bindings) {
        return from(table(sql, bindings));
    }

    
    public final SelectImpl<R> from(String sql, QueryPart... parts) {
        return from(table(sql, parts));
    }

    
    public final SelectImpl<R> where(Condition... conditions) {
        conditionStep = ConditionStep.WHERE;
        getQuery().addConditions(conditions);
        return this;
    }

    
    public final SelectImpl<R> where(Collection<? extends Condition> conditions) {
        conditionStep = ConditionStep.WHERE;
        getQuery().addConditions(conditions);
        return this;
    }

    
    public final SelectImpl<R> where(Field<Boolean> condition) {
        return where(condition(condition));
    }

    
    public final SelectImpl<R> where(String sql) {
        return where(condition(sql));
    }

    
    public final SelectImpl<R> where(String sql, Object... bindings) {
        return where(condition(sql, bindings));
    }

    
    public final SelectImpl<R> where(String sql, QueryPart... parts) {
        return where(condition(sql, parts));
    }

    
    public final SelectImpl<R> whereExists(Select<?> select) {
        conditionStep = ConditionStep.WHERE;
        return andExists(select);
    }

    
    public final SelectImpl<R> whereNotExists(Select<?> select) {
        conditionStep = ConditionStep.WHERE;
        return andNotExists(select);
    }

    
    public final SelectImpl<R> and(Condition condition) {
        switch (conditionStep) {
            case WHERE:
                getQuery().addConditions(condition);
                break;
            case CONNECT_BY:
                getQuery().addConnectBy(condition);
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

    
    public final SelectImpl<R> and(Field<Boolean> condition) {
        return and(condition(condition));
    }

    
    public final SelectImpl<R> and(String sql) {
        return and(condition(sql));
    }

    
    public final SelectImpl<R> and(String sql, Object... bindings) {
        return and(condition(sql, bindings));
    }

    
    public final SelectImpl<R> and(String sql, QueryPart... parts) {
        return and(condition(sql, parts));
    }

    
    public final SelectImpl<R> andNot(Condition condition) {
        return and(condition.not());
    }

    
    public final SelectImpl<R> andNot(Field<Boolean> condition) {
        return andNot(condition(condition));
    }

    
    public final SelectImpl<R> andExists(Select<?> select) {
        return and(exists(select));
    }

    
    public final SelectImpl<R> andNotExists(Select<?> select) {
        return and(notExists(select));
    }

    
    public final SelectImpl<R> or(Condition condition) {
        switch (conditionStep) {
            case WHERE:
                getQuery().addConditions(Operator.OR, condition);
                break;
            case CONNECT_BY:
                throw new IllegalStateException("Cannot connect conditions for the CONNECT BY clause using the OR operator");
            case HAVING:
                getQuery().addHaving(Operator.OR, condition);
                break;
            case ON:
                joinConditions.addConditions(Operator.OR, condition);
                break;
        }

        return this;
    }

    
    public final SelectImpl<R> or(Field<Boolean> condition) {
        return or(condition(condition));
    }

    
    public final SelectImpl<R> or(String sql) {
        return or(condition(sql));
    }

    
    public final SelectImpl<R> or(String sql, Object... bindings) {
        return or(condition(sql, bindings));
    }

    
    public final SelectImpl<R> or(String sql, QueryPart... parts) {
        return or(condition(sql, parts));
    }

    
    public final SelectImpl<R> orNot(Condition condition) {
        return or(condition.not());
    }

    
    public final SelectImpl<R> orNot(Field<Boolean> condition) {
        return orNot(condition(condition));
    }

    
    public final SelectImpl<R> orExists(Select<?> select) {
        return or(exists(select));
    }

    
    public final SelectImpl<R> orNotExists(Select<?> select) {
        return or(notExists(select));
    }

    
    public final SelectImpl<R> connectBy(Condition condition) {
        conditionStep = ConditionStep.CONNECT_BY;
        getQuery().addConnectBy(condition);
        return this;
    }

    
    public final SelectImpl<R> connectBy(Field<Boolean> condition) {
        return connectBy(condition(condition));
    }

    
    public final SelectImpl<R> connectBy(String sql) {
        return connectBy(condition(sql));
    }

    
    public final SelectImpl<R> connectBy(String sql, Object... bindings) {
        return connectBy(condition(sql, bindings));
    }

    
    public final SelectImpl<R> connectBy(String sql, QueryPart... parts) {
        return connectBy(condition(sql, parts));
    }

    
    public final SelectImpl<R> connectByNoCycle(Condition condition) {
        conditionStep = ConditionStep.CONNECT_BY;
        getQuery().addConnectByNoCycle(condition);
        return this;
    }

    
    public final SelectImpl<R> connectByNoCycle(Field<Boolean> condition) {
        return connectByNoCycle(condition(condition));
    }

    
    public final SelectImpl<R> connectByNoCycle(String sql) {
        return connectByNoCycle(condition(sql));
    }

    
    public final SelectImpl<R> connectByNoCycle(String sql, Object... bindings) {
        return connectByNoCycle(condition(sql, bindings));
    }

    
    public final SelectImpl<R> connectByNoCycle(String sql, QueryPart... parts) {
        return connectByNoCycle(condition(sql, parts));
    }

    
    public final SelectImpl<R> startWith(Condition condition) {
        getQuery().setConnectByStartWith(condition);
        return this;
    }

    
    public final SelectImpl<R> startWith(Field<Boolean> condition) {
        return startWith(condition(condition));
    }

    
    public final SelectImpl<R> startWith(String sql) {
        return startWith(condition(sql));
    }

    
    public final SelectImpl<R> startWith(String sql, Object... bindings) {
        return startWith(condition(sql, bindings));
    }

    
    public final SelectImpl<R> startWith(String sql, QueryPart... parts) {
        return startWith(condition(sql, parts));
    }

    
    public final SelectImpl<R> groupBy(GroupField... fields) {
        getQuery().addGroupBy(fields);
        return this;
    }

    
    public final SelectImpl<R> groupBy(Collection<? extends GroupField> fields) {
        getQuery().addGroupBy(fields);
        return this;
    }

    
    public final SelectImpl<R> orderBy(Field<?>... fields) {
        getQuery().addOrderBy(fields);
        return this;
    }

    
    public final SelectImpl<R> orderBy(SortField<?>... fields) {
        getQuery().addOrderBy(fields);
        return this;
    }

    
    public final SelectImpl<R> orderBy(Collection<? extends SortField<?>> fields) {
        getQuery().addOrderBy(fields);
        return this;
    }

    
    public final SelectImpl<R> orderBy(int... fieldIndexes) {
        getQuery().addOrderBy(fieldIndexes);
        return this;
    }

    
    public final SelectImpl<R> orderSiblingsBy(Field<?>... fields) {
        getQuery().addOrderBy(fields);
        getQuery().setOrderBySiblings(true);
        return this;
    }

    
    public final SelectImpl<R> orderSiblingsBy(SortField<?>... fields) {
        getQuery().addOrderBy(fields);
        getQuery().setOrderBySiblings(true);
        return this;
    }

    
    public final SelectImpl<R> orderSiblingsBy(Collection<? extends SortField<?>> fields) {
        getQuery().addOrderBy(fields);
        getQuery().setOrderBySiblings(true);
        return this;
    }

    
    public final SelectImpl<R> orderSiblingsBy(int... fieldIndexes) {
        getQuery().addOrderBy(fieldIndexes);
        getQuery().setOrderBySiblings(true);
        return this;
    }

    
    public final SelectImpl<R> limit(int numberOfRows) {
        this.limit = numberOfRows;
        this.limitParam = null;
        getQuery().addLimit(numberOfRows);
        return this;
    }

    
    public final SelectImpl<R> limit(Param<Integer> numberOfRows) {
        this.limit = null;
        this.limitParam = numberOfRows;
        getQuery().addLimit(numberOfRows);
        return this;
    }

    
    public final SelectImpl<R> limit(int offset, int numberOfRows) {
        getQuery().addLimit(offset, numberOfRows);
        return this;
    }

    
    public final SelectImpl<R> limit(int offset, Param<Integer> numberOfRows) {
        getQuery().addLimit(offset, numberOfRows);
        return this;
    }

    
    public final SelectImpl<R> limit(Param<Integer> offset, int numberOfRows) {
        getQuery().addLimit(offset, numberOfRows);
        return this;
    }

    
    public final SelectImpl<R> limit(Param<Integer> offset, Param<Integer> numberOfRows) {
        getQuery().addLimit(offset, numberOfRows);
        return this;
    }

    
    public final SelectImpl<R> offset(int offset) {
        if (limit != null) {
            getQuery().addLimit(offset, limit);
        }
        else if (limitParam != null) {
            getQuery().addLimit(offset, limitParam);
        }

        return this;
    }

    
    public final SelectImpl<R> offset(Param<Integer> offset) {
        if (limit != null) {
            getQuery().addLimit(offset, limit);
        }
        else if (limitParam != null) {
            getQuery().addLimit(offset, limitParam);
        }

        return this;
    }

    
    public final SelectImpl<R> forUpdate() {
        getQuery().setForUpdate(true);
        return this;
    }

    
    public final SelectImpl<R> of(Field<?>... fields) {
        getQuery().setForUpdateOf(fields);
        return this;
    }

    
    public final SelectImpl<R> of(Collection<? extends Field<?>> fields) {
        getQuery().setForUpdateOf(fields);
        return this;
    }

    
    public final SelectImpl<R> of(Table<?>... tables) {
        getQuery().setForUpdateOf(tables);
        return this;
    }

    /* [pro] xx
    xxxxxxxxx
    xxxxxx xxxxx xxxxxxxxxxxxx xxxxxxxx xxxxxxxx x
        xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        xxxxxx xxxxx
    x

    xx [/pro] */
    
    public final SelectImpl<R> noWait() {
        getQuery().setForUpdateNoWait();
        return this;
    }

    /* [pro] xx
    xxxxxxxxx
    xxxxxx xxxxx xxxxxxxxxxxxx xxxxxxxxxxxx x
        xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        xxxxxx xxxxx
    x

    xx [/pro] */
    
    public final SelectImpl<R> forShare() {
        getQuery().setForShare(true);
        return this;
    }

    
    public final SelectImpl<R> union(Select<? extends R> select) {
        return new SelectImpl<R>(getDelegate().union(select));
    }

    
    public final SelectImpl<R> unionAll(Select<? extends R> select) {
        return new SelectImpl<R>(getDelegate().unionAll(select));
    }

    
    public final SelectImpl<R> except(Select<? extends R> select) {
        return new SelectImpl<R>(getDelegate().except(select));
    }

    
    public final SelectImpl<R> intersect(Select<? extends R> select) {
        return new SelectImpl<R>(getDelegate().intersect(select));
    }

    
    public final SelectImpl<R> having(Condition... conditions) {
        conditionStep = ConditionStep.HAVING;
        getQuery().addHaving(conditions);
        return this;
    }

    
    public final SelectImpl<R> having(Collection<? extends Condition> conditions) {
        conditionStep = ConditionStep.HAVING;
        getQuery().addHaving(conditions);
        return this;
    }

    
    public final SelectImpl<R> having(Field<Boolean> condition) {
        return having(condition(condition));
    }

    
    public final SelectImpl<R> having(String sql) {
        return having(condition(sql));
    }

    
    public final SelectImpl<R> having(String sql, Object... bindings) {
        return having(condition(sql, bindings));
    }

    
    public final SelectImpl<R> having(String sql, QueryPart... parts) {
        return having(condition(sql, parts));
    }

    
    public final SelectImpl<R> on(Condition... conditions) {
        conditionStep = ConditionStep.ON;
        joinConditions = new ConditionProviderImpl();
        joinConditions.addConditions(conditions);
        getQuery().addJoin(joinTable, joinType, new Condition[] { joinConditions } /* [pro] xxx xxxxxxxxxxxxxxx xx [/pro] */);
        joinTable = null;
        joinPartitionBy = null;
        joinType = null;
        return this;
    }

    
    public final SelectImpl<R> on(Field<Boolean> condition) {
        return on(condition(condition));
    }

    
    public final SelectImpl<R> on(String sql) {
        return on(condition(sql));
    }

    
    public final SelectImpl<R> on(String sql, Object... bindings) {
        return on(condition(sql, bindings));
    }

    
    public final SelectImpl<R> on(String sql, QueryPart... parts) {
        return on(condition(sql, parts));
    }

    
    public final SelectImpl<R> onKey() throws DataAccessException {
        conditionStep = ConditionStep.ON;
        getQuery().addJoinOnKey(joinTable, joinType);
        joinTable = null;
        joinPartitionBy = null;
        joinType = null;
        return this;
    }

    
    public final SelectImpl<R> onKey(TableField<?, ?>... keyFields) throws DataAccessException {
        conditionStep = ConditionStep.ON;
        getQuery().addJoinOnKey(joinTable, joinType, keyFields);
        joinTable = null;
        joinPartitionBy = null;
        joinType = null;
        return this;
    }

    
    public final SelectImpl<R> onKey(ForeignKey<?, ?> key) {
        conditionStep = ConditionStep.ON;
        getQuery().addJoinOnKey(joinTable, joinType, key);
        joinTable = null;
        joinPartitionBy = null;
        joinType = null;
        return this;

    }

    
    public final SelectImpl<R> using(Field<?>... fields) {
        return using(Arrays.asList(fields));
    }

    
    public final SelectImpl<R> using(Collection<? extends Field<?>> fields) {
        getQuery().addJoinUsing(joinTable, joinType, fields);
        joinTable = null;
        joinPartitionBy = null;
        joinType = null;
        return this;
    }

    
    public final SelectImpl<R> join(TableLike<?> table) {
        return join(table, JoinType.JOIN);
    }

    
    public final SelectImpl<R> leftOuterJoin(TableLike<?> table) {
        return join(table, JoinType.LEFT_OUTER_JOIN);
    }

    
    public final SelectImpl<R> rightOuterJoin(TableLike<?> table) {
        return join(table, JoinType.RIGHT_OUTER_JOIN);
    }

    
    public final SelectOnStep<R> fullOuterJoin(TableLike<?> table) {
        return join(table, JoinType.FULL_OUTER_JOIN);
    }

    
    public final SelectImpl<R> join(TableLike<?> table, JoinType type) {
        switch (type) {
            case CROSS_JOIN:
            case NATURAL_JOIN:
            case NATURAL_LEFT_OUTER_JOIN:
            case NATURAL_RIGHT_OUTER_JOIN: {
                getQuery().addJoin(table, type);
                joinTable = null;
                joinPartitionBy = null;
                joinType = null;

                return this;
            }

            default: {
                conditionStep = ConditionStep.ON;
                joinTable = table;
                joinType = type;
                joinPartitionBy = null;
                joinConditions = null;

                return this;
            }
        }
    }

    
    public final SelectJoinStep<R> crossJoin(TableLike<?> table) {
        return join(table, JoinType.CROSS_JOIN);
    }

    
    public final SelectImpl<R> naturalJoin(TableLike<?> table) {
        return join(table, JoinType.NATURAL_JOIN);
    }

    
    public final SelectImpl<R> naturalLeftOuterJoin(TableLike<?> table) {
        return join(table, JoinType.NATURAL_LEFT_OUTER_JOIN);
    }

    
    public final SelectImpl<R> naturalRightOuterJoin(TableLike<?> table) {
        return join(table, JoinType.NATURAL_RIGHT_OUTER_JOIN);
    }

    
    public final SelectImpl<R> join(String sql) {
        return join(table(sql));
    }

    
    public final SelectImpl<R> join(String sql, Object... bindings) {
        return join(table(sql, bindings));
    }

    
    public final SelectImpl<R> join(String sql, QueryPart... parts) {
        return join(table(sql, parts));
    }

    
    public final SelectImpl<R> leftOuterJoin(String sql) {
        return leftOuterJoin(table(sql));
    }

    
    public final SelectImpl<R> leftOuterJoin(String sql, Object... bindings) {
        return leftOuterJoin(table(sql, bindings));
    }

    
    public final SelectImpl<R> leftOuterJoin(String sql, QueryPart... parts) {
        return leftOuterJoin(table(sql, parts));
    }

    
    public final SelectImpl<R> rightOuterJoin(String sql) {
        return rightOuterJoin(table(sql));
    }

    
    public final SelectImpl<R> rightOuterJoin(String sql, Object... bindings) {
        return rightOuterJoin(table(sql, bindings));
    }

    
    public final SelectImpl<R> rightOuterJoin(String sql, QueryPart... parts) {
        return rightOuterJoin(table(sql, parts));
    }

    
    public final SelectOnStep<R> fullOuterJoin(String sql) {
        return fullOuterJoin(table(sql));
    }

    
    public final SelectOnStep<R> fullOuterJoin(String sql, Object... bindings) {
        return fullOuterJoin(table(sql, bindings));
    }

    
    public final SelectOnStep<R> fullOuterJoin(String sql, QueryPart... parts) {
        return fullOuterJoin(table(sql, parts));
    }

    
    public final SelectJoinStep<R> crossJoin(String sql) {
        return crossJoin(table(sql));
    }

    
    public final SelectJoinStep<R> crossJoin(String sql, Object... bindings) {
        return crossJoin(table(sql, bindings));
    }

    
    public final SelectJoinStep<R> crossJoin(String sql, QueryPart... parts) {
        return crossJoin(table(sql, parts));
    }

    
    public final SelectImpl<R> naturalJoin(String sql) {
        return naturalJoin(table(sql));
    }

    
    public final SelectImpl<R> naturalJoin(String sql, Object... bindings) {
        return naturalJoin(table(sql, bindings));
    }

    
    public final SelectImpl<R> naturalJoin(String sql, QueryPart... parts) {
        return naturalJoin(table(sql, parts));
    }

    
    public final SelectImpl<R> naturalLeftOuterJoin(String sql) {
        return naturalLeftOuterJoin(table(sql));
    }

    
    public final SelectImpl<R> naturalLeftOuterJoin(String sql, Object... bindings) {
        return naturalLeftOuterJoin(table(sql, bindings));
    }

    
    public final SelectImpl<R> naturalLeftOuterJoin(String sql, QueryPart... parts) {
        return naturalLeftOuterJoin(table(sql, parts));
    }

    
    public final SelectImpl<R> naturalRightOuterJoin(String sql) {
        return naturalRightOuterJoin(table(sql));
    }

    
    public final SelectImpl<R> naturalRightOuterJoin(String sql, Object... bindings) {
        return naturalRightOuterJoin(table(sql, bindings));
    }

    
    public final SelectImpl<R> naturalRightOuterJoin(String sql, QueryPart... parts) {
        return naturalRightOuterJoin(table(sql, parts));
    }

    /* [pro] xx
    xxxxxxxxx
    xxxxxx xxxxx xxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxx xxxxxxx x
        xxxxxxxxxxxxxxx x xxxxxxx
        xxxxxx xxxxx
    x

    xxxxxxxxx
    xxxxxx xxxxx xxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxx xxxxxxx xxxxxxxxx xxxxxxx x
        xxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxx
    x

    xx [/pro] */
    
    public final ResultQuery<R> maxRows(int rows) {
        return getDelegate().maxRows(rows);
    }

    
    public final ResultQuery<R> resultSetConcurrency(int resultSetConcurrency) {
        return getDelegate().resultSetConcurrency(resultSetConcurrency);
    }

    
    public final ResultQuery<R> resultSetType(int resultSetType) {
        return getDelegate().resultSetType(resultSetType);
    }

    
    public final ResultQuery<R> resultSetHoldability(int resultSetHoldability) {
        return getDelegate().resultSetHoldability(resultSetHoldability);
    }

    
    public final ResultQuery<R> intern(Field<?>... fields) {
        return getDelegate().intern(fields);
    }

    
    public final ResultQuery<R> intern(int... fieldIndexes) {
        return getDelegate().intern(fieldIndexes);
    }

    
    public final ResultQuery<R> intern(String... fieldNames) {
        return getDelegate().intern(fieldNames);
    }

    
    public final Class<? extends R> getRecordType() {
        return getDelegate().getRecordType();
    }

    
    public final List<Field<?>> getSelect() {
        return getDelegate().getSelect();
    }

    
    public final Result<R> getResult() {
        return getDelegate().getResult();
    }

    
    public final Result<R> fetch() {
        return getDelegate().fetch();
    }

    
    public final ResultSet fetchResultSet() {
        return getDelegate().fetchResultSet();
    }

    
    public final Cursor<R> fetchLazy() {
        return getDelegate().fetchLazy();
    }

    
    public final Cursor<R> fetchLazy(int fetchSize) {
        return getDelegate().fetchLazy(fetchSize);
    }

    
    public final List<Result<Record>> fetchMany() {
        return getDelegate().fetchMany();
    }

    
    public final <T> List<T> fetch(Field<T> field) {
        return getDelegate().fetch(field);
    }

    
    public final <T> List<T> fetch(Field<?> field, Class<? extends T> type) {
        return getDelegate().fetch(field, type);
    }

    
    public final <T, U> List<U> fetch(Field<T> field, Converter<? super T, U> converter) {
        return getDelegate().fetch(field, converter);
    }

    
    public final List<?> fetch(int fieldIndex) {
        return getDelegate().fetch(fieldIndex);
    }

    
    public final <T> List<T> fetch(int fieldIndex, Class<? extends T> type) {
        return getDelegate().fetch(fieldIndex, type);
    }

    
    public final <U> List<U> fetch(int fieldIndex, Converter<?, U> converter) {
        return getDelegate().fetch(fieldIndex, converter);
    }

    
    public final List<?> fetch(String fieldName) {
        return getDelegate().fetch(fieldName);
    }

    
    public final <T> List<T> fetch(String fieldName, Class<? extends T> type) {
        return getDelegate().fetch(fieldName, type);
    }

    
    public final <U> List<U> fetch(String fieldName, Converter<?, U> converter) {
        return getDelegate().fetch(fieldName, converter);
    }

    
    public final <T> T fetchOne(Field<T> field) {
        return getDelegate().fetchOne(field);
    }

    
    public final <T> T fetchOne(Field<?> field, Class<? extends T> type) {
        return getDelegate().fetchOne(field, type);
    }

    
    public final <T, U> U fetchOne(Field<T> field, Converter<? super T, U> converter) {
        return getDelegate().fetchOne(field, converter);
    }

    
    public final Object fetchOne(int fieldIndex) {
        return getDelegate().fetchOne(fieldIndex);
    }

    
    public final <T> T fetchOne(int fieldIndex, Class<? extends T> type) {
        return getDelegate().fetchOne(fieldIndex, type);
    }

    
    public final <U> U fetchOne(int fieldIndex, Converter<?, U> converter) {
        return getDelegate().fetchOne(fieldIndex, converter);
    }

    
    public final Object fetchOne(String fieldName) {
        return getDelegate().fetchOne(fieldName);
    }

    
    public final <T> T fetchOne(String fieldName, Class<? extends T> type) {
        return getDelegate().fetchOne(fieldName, type);
    }

    
    public final <U> U fetchOne(String fieldName, Converter<?, U> converter) {
        return getDelegate().fetchOne(fieldName, converter);
    }

    
    public final R fetchOne() {
        return getDelegate().fetchOne();
    }

    
    public final R fetchAny() {
        return getDelegate().fetchAny();
    }

    
    public final <K> Map<K, R> fetchMap(Field<K> key) {
        return getDelegate().fetchMap(key);
    }

    
    public final <K, V> Map<K, V> fetchMap(Field<K> key, Field<V> value) {
        return getDelegate().fetchMap(key, value);
    }

    
    public final Map<Record, R> fetchMap(Field<?>[] keys) {
        return getDelegate().fetchMap(keys);
    }

    
    public final <E> Map<List<?>, E> fetchMap(Field<?>[] keys, Class<? extends E> type) {
        return getDelegate().fetchMap(keys, type);
    }

    
    public final <K, E> Map<K, E> fetchMap(Field<K> key, Class<? extends E> type) {
        return getDelegate().fetchMap(key, type);
    }

    
    public final List<Map<String, Object>> fetchMaps() {
        return getDelegate().fetchMaps();
    }

    
    public final Map<String, Object> fetchOneMap() {
        return getDelegate().fetchOneMap();
    }

    
    public final <K> Map<K, Result<R>> fetchGroups(Field<K> key) {
        return getDelegate().fetchGroups(key);
    }

    
    public final <K, V> Map<K, List<V>> fetchGroups(Field<K> key, Field<V> value) {
        return getDelegate().fetchGroups(key, value);
    }

    
    public final Map<Record, Result<R>> fetchGroups(Field<?>[] keys) {
        return getDelegate().fetchGroups(keys);
    }

    
    public final <E> Map<Record, List<E>> fetchGroups(Field<?>[] keys, Class<? extends E> type) {
        return getDelegate().fetchGroups(keys, type);
    }

    
    public final Object[][] fetchArrays() {
        return getDelegate().fetchArrays();
    }

    
    public final Object[] fetchArray(int fieldIndex) {
        return getDelegate().fetchArray(fieldIndex);
    }

    
    public final <T> T[] fetchArray(int fieldIndex, Class<? extends T> type) {
        return getDelegate().fetchArray(fieldIndex, type);
    }

    
    public final <U> U[] fetchArray(int fieldIndex, Converter<?, U> converter) {
        return getDelegate().fetchArray(fieldIndex, converter);
    }

    
    public final Object[] fetchArray(String fieldName) {
        return getDelegate().fetchArray(fieldName);
    }

    
    public final <T> T[] fetchArray(String fieldName, Class<? extends T> type) {
        return getDelegate().fetchArray(fieldName, type);
    }

    
    public final <U> U[] fetchArray(String fieldName, Converter<?, U> converter) {
        return getDelegate().fetchArray(fieldName, converter);
    }

    
    public final <T> T[] fetchArray(Field<T> field) {
        return getDelegate().fetchArray(field);
    }

    
    public final <T> T[] fetchArray(Field<?> field, Class<? extends T> type) {
        return getDelegate().fetchArray(field, type);
    }

    
    public final <T, U> U[] fetchArray(Field<T> field, Converter<? super T, U> converter) {
        return getDelegate().fetchArray(field, converter);
    }

    
    public final Object[] fetchOneArray() {
        return getDelegate().fetchOneArray();
    }

    
    public final <T> List<T> fetchInto(Class<? extends T> type) {
        return getDelegate().fetchInto(type);
    }

    
    public final <E> E fetchOneInto(Class<? extends E> type) {
        return getDelegate().fetchOneInto(type);
    }

    
    public final <Z extends Record> Z fetchOneInto(Table<Z> table) {
        return getDelegate().fetchOneInto(table);
    }

    
    public final <Z extends Record> Result<Z> fetchInto(Table<Z> table) {
        return getDelegate().fetchInto(table);
    }

    
    public final <H extends RecordHandler<? super R>> H fetchInto(H handler) {
        return getDelegate().fetchInto(handler);
    }

    
    public final <E> List<E> fetch(RecordMapper<? super R, E> mapper) {
        return getDelegate().fetch(mapper);
    }

    
    public final <K, E> Map<K, List<E>> fetchGroups(Field<K> key, Class<? extends E> type) {
        return getDelegate().fetchGroups(key, type);
    }

    
    @Deprecated
    public final org.jooq.FutureResult<R> fetchLater() {
        return getDelegate().fetchLater();
    }

    
    @Deprecated
    public final org.jooq.FutureResult<R> fetchLater(ExecutorService executor) {
        return getDelegate().fetchLater(executor);
    }

    
    public final Table<R> asTable() {
        return getDelegate().asTable();
    }

    
    public final Table<R> asTable(String alias) {
        return getDelegate().asTable(alias);
    }

    
    public final Table<R> asTable(String alias, String... fieldAliases) {
        return getDelegate().asTable(alias, fieldAliases);
    }

    
    public final <T> Field<T> asField() {
        return getDelegate().asField();
    }

    
    public final <T> Field<T> asField(String alias) {
        return getDelegate().asField(alias);
    }

    
    public final Row fieldsRow() {
        return getDelegate().fieldsRow();
    }

    
    public final <T> Field<T> field(Field<T> field) {
        return getDelegate().field(field);
    }

    
    public final Field<?> field(String string) {
        return getDelegate().field(string);
    }

    
    public final Field<?> field(int index) {
        return getDelegate().field(index);
    }

    
    public final Field<?>[] fields() {
        return getDelegate().fields();
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
         * Additional conditions go to the <code>JOIN</code> clause that is
         * currently being added.
         */
        ON,

        /**
         * Additional conditions go to the <code>WHERE</code> clause that is
         * currently being added.
         */
        WHERE,

        /**
         * Additional conditions go to the <code>CONNECT BY</code> clause that
         * is currently being added.
         */
        CONNECT_BY,

        /**
         * Additional conditions go to the <code>HAVING</code> clause that is
         * currently being added.
         */
        HAVING
    }
}
