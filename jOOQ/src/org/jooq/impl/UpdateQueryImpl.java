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
import java.util.Map.Entry;

import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Operator;
import org.jooq.Table;
import org.jooq.TableRecord;
import org.jooq.UpdateQuery;

/**
 * @author Lukas Eder
 */
class UpdateQueryImpl<R extends TableRecord<R>> extends AbstractStoreQuery<R> implements UpdateQuery<R> {

    private static final long           serialVersionUID = -660460731970074719L;
    private final ConditionProviderImpl condition;

    UpdateQueryImpl(Configuration configuration, Table<R> table) {
        super(configuration, table);

        this.condition = new ConditionProviderImpl(configuration);
    }

    @Override
    public final void setRecord(R record) {
        for (Field<?> field : record.getFields()) {
            if (((RecordImpl) record).getValue0(field).isChanged()) {
                addValue(record, field);
            }
        }
    }

    @Override
    public final int bind(Configuration configuration, PreparedStatement stmt, int initialIndex) throws SQLException {
        int result = initialIndex;

        result = super.bind(configuration, stmt, initialIndex);
        result = condition.bind(configuration, stmt, result);

        return result;
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

    final Condition getWhere() {
        return condition.getWhere();
    }

    @Override
    public final String toSQLReference(Configuration configuration, boolean inlineParameters) {
        StringBuilder sb = new StringBuilder();

        sb.append("update ");
        sb.append(getInto().getQueryPart().toSQLDeclaration(configuration, inlineParameters));
        sb.append(" set ");

        String separator = "";
        if (isExecutable()) {
            for (Entry<Field<?>, Field<?>> entry : getValues0().entrySet()) {
                sb.append(separator);
                sb.append(entry.getKey().getName());
                sb.append(" = ");
                sb.append(entry.getValue().getQueryPart().toSQLReference(configuration, inlineParameters));
                separator = ", ";
            }
        }
        else {
            // This query must not be executed
            sb.append("[ no fields are updated ]");
        }

        if (getWhere() != TRUE_CONDITION) {
            sb.append(" where ");
            sb.append(getWhere().getQueryPart().toSQLReference(configuration, inlineParameters));
        }

        return sb.toString();
    }

    @Override
    protected boolean isExecutable() {
        return getValues0().size() > 0;
    }
}
