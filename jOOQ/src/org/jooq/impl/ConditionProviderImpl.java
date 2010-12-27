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

/**
 * @author Lukas Eder
 */
class ConditionProviderImpl extends AbstractQueryPart implements ConditionProvider {

    private static final long serialVersionUID = 6073328960551062973L;

    private Condition         condition;

    ConditionProviderImpl(Configuration configuration) {
        super(configuration);
    }

    final Condition getWhere() {
        if (condition == null) {
            return TRUE_CONDITION;
        }

        return condition;
    }

    @Override
    public final void addConditions(Condition... conditions) {
        addConditions(Operator.AND, conditions);
    }

    @Override
    public final void addConditions(Collection<Condition> conditions) {
        addConditions(Operator.AND, conditions);
    }

    @Override
    public final void addConditions(Operator operator, Condition... conditions) {
        addConditions(operator, Arrays.asList(conditions));
    }

    @Override
    public final void addConditions(Operator operator, Collection<Condition> conditions) {
        if (!conditions.isEmpty()) {
            Condition c;

            if (conditions.size() == 1) {
                c = conditions.iterator().next();
            }
            else {
                c = create().combinedCondition(operator, conditions);
            }

            if (getWhere() == TRUE_CONDITION) {
                condition = c;
            }
            else {
                condition = create().combinedCondition(operator, getWhere(), c);
            }
        }
    }

    @Override
    public final <T> void addBetweenCondition(Field<T> field, T minValue, T maxValue) {
        addConditions(create().betweenCondition(field, minValue, maxValue));
    }

    @Override
    public final <T> void addCompareCondition(Field<T> field, T value, Comparator comparator) {
        addConditions(create().compareCondition(field, value, comparator));
    }

    @Override
    public final <T> void addCompareCondition(Field<T> field, T value) {
        addConditions(create().compareCondition(field, value));
    }

    @Override
    public final void addNullCondition(Field<?> field) {
        addConditions(create().nullCondition(field));
    }

    @Override
    public final void addNotNullCondition(Field<?> field) {
        addConditions(create().notNullCondition(field));
    }

    @Override
    public final <T> void addInCondition(Field<T> field, Collection<T> values) {
        addConditions(create().inCondition(field, values));
    }

    @Override
    public final <T> void addInCondition(Field<T> field, T... values) {
        addConditions(create().inCondition(field, values));
    }

    @Override
    public final int bind(Configuration configuration, PreparedStatement stmt, int initialIndex) throws SQLException {
        return getWhere().getQueryPart().bind(configuration, stmt, initialIndex);
    }

    @Override
    public final String toSQLReference(Configuration configuration, boolean inlineParameters) {
        return getWhere().getQueryPart().toSQLReference(configuration, inlineParameters);
    }
}
