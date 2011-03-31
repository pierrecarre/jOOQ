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

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jooq.ArrayRecord;
import org.jooq.Configuration;
import org.jooq.SQLDialectNotSupportedException;

/**
 * @author Lukas Eder
 */
class ArrayConstant<T> extends AbstractField<T> {

    private static final long    serialVersionUID = -8538560256712388066L;
    private final ArrayRecord<?> array;

    ArrayConstant(Configuration configuration, ArrayRecord<T> array) {
        super(configuration, array.getName(), array.getDataType());

        this.array = array;
    }

    @Override
    public String toSQLReference(Configuration configuration, boolean inlineParameters) {
        switch (getDialect()) {
            case ORACLE: {
                if (inlineParameters) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(array.getName());
                    sb.append("(");

                    String separator = "";
                    for (Object object : array.get()) {
                        sb.append(separator);
                        sb.append(create().constant(object).getQueryPart().toSQLReference(configuration, inlineParameters));

                        separator = ", ";
                    }

                    sb.append(")");
                    return sb.toString();
                } else {
                    return "?";
                }
            }
        }

        throw new SQLDialectNotSupportedException("ARRAYs not supported in dialect " + getDialect());
    }

    @Override
    public int bind(Configuration configuration, PreparedStatement stmt, int initialIndex) throws SQLException {
        switch (getDialect()) {
            case ORACLE:
                return JooqUtil.bind(getDialect(), stmt, initialIndex, array);
        }

        throw new SQLDialectNotSupportedException("ARRAYs not supported in dialect " + getDialect());
    }

    @Override
    public final boolean isNullLiteral() {
        return array == null;
    }
}
