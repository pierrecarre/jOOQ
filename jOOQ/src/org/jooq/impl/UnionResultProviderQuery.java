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

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Select;

class UnionResultProviderQuery<R extends Record> extends
    AbstractResultProviderQuery<R> {

    private static final long                     serialVersionUID = 7491446471677986172L;
    private final List<Select<R>> queries;
    private final CombineOperator                 operator;

    UnionResultProviderQuery(Select<R> query1, Select<R> query2,
        CombineOperator operator) {

        super(query1.getConfiguration());

        this.operator = operator;
        this.queries = new ArrayList<Select<R>>();
        this.queries.add(query1);
        this.queries.add(query2);
    }

    @Override
    public final Class<? extends R> getRecordType() {
        return queries.get(0).getRecordType();
    }

    @Override
    public final List<Field<?>> getSelect() {
        return queries.get(0).getSelect();
    }

    @Override
    public final String toSQLReference(boolean inlineParameters) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < queries.size(); i++) {
            if (i != 0) {
                sb.append(" ");
                sb.append(operator.toSQL(getDialect()));
                sb.append(" ");
            }

            sb.append("(");
            sb.append(queries.get(i).getQueryPart().toSQLReference(inlineParameters));
            sb.append(")");
        }

        return sb.toString();
    }

    @Override
    public final int bind(PreparedStatement stmt, int initialIndex) throws SQLException {
        int result = initialIndex;

        for (Select<R> query : queries) {
            result = query.getQueryPart().bind(stmt, result);
        }

        return result;
    }
}
