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

import static org.jooq.impl.TrueCondition.TRUE_CONDITION;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.ConditionProvider;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Operator;
import org.jooq.OrderProvider;
import org.jooq.Record;
import org.jooq.SortField;
import org.jooq.TableLike;

/**
 * @author Lukas Eder
 */
abstract class AbstractResultProviderSelectQuery<R extends Record> extends AbstractResultProviderQuery<R> implements
    OrderProvider, ConditionProvider {

    /**
     * Generated UID
     */
    private static final long           serialVersionUID = 1646393178384872967L;

    private final FieldList             select;
    private boolean                     distinct;
    private final TableList             from;
    private final JoinList              join;
    private final ConditionProviderImpl condition;
    private final FieldList             groupBy;
    private final ConditionProviderImpl having;
    private final SortFieldList         orderBy;
    private final Limit                 limit;

    AbstractResultProviderSelectQuery(Configuration configuration) {
        this(configuration, null);
    }

    AbstractResultProviderSelectQuery(Configuration configuration, TableLike<? extends R> from) {
        this(configuration, from, false);
    }

    AbstractResultProviderSelectQuery(Configuration configuration, TableLike<? extends R> from, boolean distinct) {
        super(configuration);

        this.distinct = distinct;
        this.select = new SelectFieldList(configuration);
        this.from = new TableList(configuration);
        this.join = new JoinList(configuration);
        this.condition = new ConditionProviderImpl(configuration);
        this.groupBy = new FieldList(configuration);
        this.having = new ConditionProviderImpl(configuration);
        this.orderBy = new SortFieldList(configuration);
        this.limit = new Limit(configuration);

        if (from != null) {
            this.from.add(from.asTable());
        }
    }

    @Override
    public final int bind(Configuration configuration, PreparedStatement stmt, int initialIndex) throws SQLException {
        int result = initialIndex;

        result = getSelect0().bind(configuration, stmt, result);
        result = getFrom().bind(configuration, stmt, result);
        result = getJoin().bind(configuration, stmt, result);
        result = getWhere().bind(configuration, stmt, result);
        result = getGroupBy().bind(configuration, stmt, result);
        result = getHaving().bind(configuration, stmt, result);
        result = getOrderBy().bind(configuration, stmt, result);

        return result;
    }

    @Override
    public final String toSQLReference(Configuration configuration, boolean inlineParameters) {
        StringBuilder sb = new StringBuilder();

        sb.append("select ");
        if (distinct) {
            sb.append("distinct ");
        }

        sb.append(getSelect().toSQLDeclaration(configuration, inlineParameters));
        if (!getFrom().toSQLDeclaration(configuration, inlineParameters).isEmpty()) {
            sb.append(" from ");
            sb.append(getFrom().toSQLDeclaration(configuration, inlineParameters));
        }

        if (!getJoin().isEmpty()) {
            sb.append(" ");
            sb.append(getJoin().toSQLDeclaration(configuration, inlineParameters));
        }

        if (getWhere().getWhere() != TRUE_CONDITION) {
            sb.append(" where ");
            sb.append(getWhere().toSQLReference(configuration, inlineParameters));
        }

        if (!getGroupBy().isEmpty()) {
            sb.append(" group by ");
            sb.append(getGroupBy().toSQLReference(configuration, inlineParameters));
        }

        if (getHaving().getWhere() != TRUE_CONDITION) {
            sb.append(" having ");
            sb.append(getHaving().toSQLReference(configuration, inlineParameters));
        }

        if (!getOrderBy().isEmpty()) {
            sb.append(" order by ");
            sb.append(getOrderBy().toSQLReference(configuration, inlineParameters));
        }

        if (getLimit().isApplicable()) {
            sb.append(" ");
            sb.append(getLimit().toSQLReference(configuration, inlineParameters));
        }

        return sb.toString();
    }

    // @Mixin - Declaration in SelectQuery
    public final void addSelect(Collection<Field<?>> fields) {
        getSelect0().addAll(fields);
    }

    // @Mixin - Declaration in SelectQuery
    public final void addSelect(Field<?>... fields) {
        addSelect(Arrays.asList(fields));
    }

    // @Mixin - Declaration in SelectQuery
    public final void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public final void addLimit(int numberOfRows) {
        addLimit(1, numberOfRows);
    }

    @Override
    public final void addLimit(int lowerBound, int numberOfRows) {
        limit.setLowerBound(lowerBound);
        limit.setNumberOfRows(numberOfRows);
    }

    final FieldList getSelect0() {
        return select;
    }

    @Override
    public final FieldList getSelect() {
        // #109 : Don't allow empty select lists to render select *
        // Even if select * would be useful, generated client code
        // would be required to be in sync with the database schema

        if (getSelect0().isEmpty()) {
            FieldList result = new SelectFieldList(getConfiguration());

            for (TableLike<?> table : getFrom()) {
                for (Field<?> field : table.asTable().getFields()) {
                    result.add(field);
                }
            }

            for (Join join : getJoin()) {
                for (Field<?> field : join.getTable().asTable().getFields()) {
                    result.add(field);
                }
            }

            return result;
        }

        return getSelect0();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final Class<? extends R> getRecordType() {
        // Generated record classes only come into play, when the select is
        // - on a single table
        // - a select *
        if (getTables().size() == 1 && getSelect0().isEmpty()) {
            return (Class<? extends R>) getTables().get(0).asTable().getRecordType();
        }
        else {
            return (Class<? extends R>) RecordImpl.class;
        }
    }

    final TableList getTables() {
        TableList result = new TableList(getConfiguration(), getFrom());

        for (Join join : getJoin()) {
            result.add(join.getTable().asTable());
        }

        return result;
    }

    final TableList getFrom() {
        return from;
    }

    final FieldList getGroupBy() {
        return groupBy;
    }

    final JoinList getJoin() {
        return join;
    }

    final Limit getLimit() {
        return limit;
    }

    final ConditionProviderImpl getWhere() {
        return condition;
    }

    final ConditionProviderImpl getHaving() {
        return having;
    }

    final SortFieldList getOrderBy() {
        return orderBy;
    }

    @Override
    public final void addOrderBy(Collection<SortField<?>> fields) {
        getOrderBy().addAll(fields);
    }

    @Override
    public final void addOrderBy(Field<?>... fields) {
        getOrderBy().addAll(fields);
    }

    @Override
    public final void addOrderBy(SortField<?>... fields) {
        addOrderBy(Arrays.asList(fields));
    }

    @Override
    public final void addConditions(Condition... conditions) {
        condition.addConditions(conditions);
    }

    @Override
    public final void addConditions(Collection<Condition> conditions) {
        condition.addConditions(conditions);
    }

    @Override
    public final void addConditions(Operator operator, Condition... conditions) {
        condition.addConditions(operator, conditions);
    }

    @Override
    public final void addConditions(Operator operator, Collection<Condition> conditions) {
        condition.addConditions(operator, conditions);
    }

    @Override
    public final <T> void addBetweenCondition(Field<T> field, T minValue, T maxValue) {
        condition.addBetweenCondition(field, minValue, maxValue);
    }

    @Override
    public final <T> void addCompareCondition(Field<T> field, T value, Comparator comparator) {
        condition.addCompareCondition(field, value, comparator);
    }

    @Override
    public final <T> void addCompareCondition(Field<T> field, T value) {
        condition.addCompareCondition(field, value);
    }

    @Override
    public final void addNullCondition(Field<?> field) {
        condition.addNullCondition(field);
    }

    @Override
    public final void addNotNullCondition(Field<?> field) {
        condition.addNotNullCondition(field);
    }

    @Override
    public final <T> void addInCondition(Field<T> field, Collection<T> values) {
        condition.addInCondition(field, values);
    }

    @Override
    public final <T> void addInCondition(Field<T> field, T... values) {
        condition.addInCondition(field, values);
    }
}
