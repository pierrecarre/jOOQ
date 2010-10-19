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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jooq.CaseWhenStep;
import org.jooq.Field;
import org.jooq.SQLDialect;

class CaseWhenStepImpl<V, T> extends FieldImpl<T> implements CaseWhenStep<V, T> {

    /**
     * Generated UID
     */
    private static final long    serialVersionUID = -3817194006479624228L;

    private final Field<V>       value;
    private final List<Field<V>> compareValues;
    private final List<Field<T>> results;
    private Field<T> otherwise;

    CaseWhenStepImpl(SQLDialect dialect, String name, Class<? extends T> type, Field<V> value, Field<V> compareValue,
        Field<T> result) {

        super(dialect, name, type);

        this.value = value;
        this.compareValues = new ArrayList<Field<V>>();
        this.results = new ArrayList<Field<T>>();

        when(compareValue, result);
    }

    private <Z> Field<Z> constant(Z value) {
        return new FunctionFactory(getDialect()).constant(value);
    }

    @Override
    public Field<T> otherwise(T result) {
        return otherwise(constant(result));
    }

    @Override
    public Field<T> otherwise(Field<T> result) {
        this.otherwise = result;

        return this;
    }

    @Override
    public CaseWhenStep<V, T> when(V compareValue, T result) {
        return when(constant(compareValue), constant(result));
    }

    @Override
    public CaseWhenStep<V, T> when(V compareValue, Field<T> result) {
        return when(constant(compareValue), result);
    }

    @Override
    public CaseWhenStep<V, T> when(Field<V> compareValue, T result) {
        return when(compareValue, constant(result));
    }

    @Override
    public CaseWhenStep<V, T> when(Field<V> compareValue, Field<T> result) {
        compareValues.add(compareValue);
        results.add(result);

        return this;
    }

    @Override
    public int bind(PreparedStatement stmt, int initialIndex) throws SQLException {
        int result = value.bind(stmt, initialIndex);

        for (int i = 0; i < compareValues.size(); i++) {
            result = compareValues.get(i).bind(stmt, result);
            result = results.get(i).bind(stmt, result);
        }

        if (otherwise != null) {
            result = otherwise.bind(stmt, result);
        }
        return result;
    }

    @Override
    public String toSQLReference(boolean inlineParameters) {
        StringBuilder sb = new StringBuilder();
        sb.append("case ");
        sb.append(value.toSQLReference(inlineParameters));

        for (int i = 0; i < compareValues.size(); i++) {
            sb.append(" when ");
            sb.append(compareValues.get(i).toSQLReference(inlineParameters));
            sb.append(" then ");
            sb.append(results.get(i).toSQLReference(inlineParameters));
        }

        if (otherwise != null) {
            sb.append(" else ");
            sb.append(otherwise.toSQLReference(inlineParameters));
        }

        sb.append(" end");
        return sb.toString();
    }
}
