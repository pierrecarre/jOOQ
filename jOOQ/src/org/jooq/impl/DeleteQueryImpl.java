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

import static org.jooq.impl.TrueCondition.TRUE_CONDITION;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.DeleteQuery;
import org.jooq.Field;
import org.jooq.Operator;
import org.jooq.Record;
import org.jooq.Table;

/**
 * @author Lukas Eder
 */
class DeleteQueryImpl<R extends Record> extends AbstractQuery<R> implements DeleteQuery<R> {

    private static final long           serialVersionUID = -1943687511774150929L;

    private final Table<R>              table;
    private final ConditionProviderImpl condition;

    DeleteQueryImpl(Configuration configuration, Table<R> table) {
        super(configuration);

        this.table = table;
        this.condition = new ConditionProviderImpl(configuration);
    }

    @Override
    public final int bind(Configuration configuration, PreparedStatement stmt, int initialIndex) throws SQLException {
        int result = initialIndex;

        result = getFrom().getQueryPart().bind(configuration, stmt, result);
        result = getWhere().getQueryPart().bind(configuration, stmt, result);

        return result;
    }

    final Table<R> getFrom() {
        return table;
    }

    final Condition getWhere() {
        return condition.getWhere();
    }

    @Override
    public final void addConditions(Collection<Condition> conditions) {
        condition.addConditions(conditions);
    }

    @Override
    public final void addConditions(Condition... conditions) {
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
    @Deprecated
    public final <T> void addBetweenCondition(Field<T> field, T minValue, T maxValue) {
        condition.addBetweenCondition(field, minValue, maxValue);
    }

    @Override
    @Deprecated
    public final <T> void addCompareCondition(Field<T> field, T value, Comparator comparator) {
        condition.addCompareCondition(field, value, comparator);
    }

    @Override
    @Deprecated
    public final <T> void addCompareCondition(Field<T> field, T value) {
        condition.addCompareCondition(field, value);
    }

    @Override
    @Deprecated
    public final void addNullCondition(Field<?> field) {
        condition.addNullCondition(field);
    }

    @Override
    @Deprecated
    public final void addNotNullCondition(Field<?> field) {
        condition.addNotNullCondition(field);
    }

    @Override
    @Deprecated
    public final <T> void addInCondition(Field<T> field, Collection<T> values) {
        condition.addInCondition(field, values);
    }

    @Override
    @Deprecated
    public final <T> void addInCondition(Field<T> field, T... values) {
        condition.addInCondition(field, values);
    }

    @Override
    public final String toSQLReference(Configuration configuration, boolean inlineParameters) {
        StringBuilder sb = new StringBuilder();

        sb.append("delete from ");
        sb.append(getFrom().getQueryPart().toSQLReference(configuration, inlineParameters));

        if (getWhere() != TRUE_CONDITION) {
            sb.append(" where ");
            sb.append(getWhere().getQueryPart().toSQLReference(configuration, inlineParameters));
        }

        return sb.toString();
    }
}
