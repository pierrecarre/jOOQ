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

import org.jooq.Field;
import org.jooq.SQLDialect;

class ArithmeticExpression<T> extends AbstractField<T> {

    /**
     * Generated UID
     */
    private static final long serialVersionUID = -5522799070693019771L;
    private final Field<T> lhs;
    private final FieldList rhs;
    private final ArithmeticOperator operator;

    ArithmeticExpression(SQLDialect dialect, Field<T> lhs, Field<? extends Number> rhs, ArithmeticOperator operator) {
        super(dialect, operator.toSQL(), lhs.getType());

        this.operator = operator;
        this.lhs = lhs;
        this.rhs = new FieldList(dialect);
        this.rhs.add(rhs);
    }

    @Override
    public final Field<T> add(Field<? extends Number> value) {
        if (operator == ArithmeticOperator.ADD) {
            rhs.add(value);
            return this;
        }

        return super.add(value);
    }

    @Override
    public final Field<T> multiply(Field<? extends Number> value) {
        if (operator == ArithmeticOperator.MULTIPLY) {
            rhs.add(value);
            return this;
        }

        return super.multiply(value);
    }

    @Override
    public final String toSQLReference(boolean inlineParameters) {
        StringBuilder sb = new StringBuilder();

        sb.append("(");
        sb.append(lhs.getQueryPart().toSQLReference(inlineParameters));

        for (Field<?> field : rhs) {
            sb.append(" ");
            sb.append(operator.toSQL());
            sb.append(" ");
            sb.append(field.getQueryPart().toSQLReference(inlineParameters));
        }

        sb.append(")");
        return sb.toString();
    }

    @Override
    public final int bind(PreparedStatement stmt, int initialIndex) throws SQLException {
        int result = initialIndex;

        result = lhs.getQueryPart().bind(stmt, result);
        result = rhs.getQueryPart().bind(stmt, result);

        return result;
    }
}
