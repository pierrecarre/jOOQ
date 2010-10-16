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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.jooq.CombineOperator;
import org.jooq.FieldList;
import org.jooq.Record;
import org.jooq.ResultProviderSelectQuery;
import org.jooq.SQLDialect;
import org.jooq.Table;

/**
 * @author Lukas Eder
 */
class SelectQueryAsTable<R extends Record> extends TableImpl<R> implements Table<R> {

    private static final long                           serialVersionUID = 6272398035926615668L;
    private final List<ResultProviderSelectQuery<?, R>> queries;
    private final CombineOperator                       operator;

    SelectQueryAsTable(SQLDialect dialect, ResultProviderSelectQuery<?, R>... query) {
        this(dialect, CombineOperator.UNION, query);
    }

    SelectQueryAsTable(SQLDialect dialect, CombineOperator operator, ResultProviderSelectQuery<?, R>... query) {
        super(dialect, "");

        this.operator = operator;
        this.queries = Arrays.asList(query);
    }

    @Override
    public Table<R> as(String alias) {
        return new TableAlias<R>(getDialect(), this, alias, true);
    }

    @Override
    public FieldList getFields() {
        return queries.get(0).getSelect();
    }

    @Override
    public Class<? extends R> getRecordType() {
        return queries.get(0).getRecordType();
    }

    @Override
    public int bind(PreparedStatement stmt, int initialIndex) throws SQLException {
        for (ResultProviderSelectQuery<?, R> query : queries) {
            initialIndex = query.bind(stmt, initialIndex);
        }

        return initialIndex;
    }

    @Override
    public String toSQLReference(boolean inlineParameters) {
        if (queries.size() == 1) {
            return queries.get(0).toSQLReference(inlineParameters);
        }
        else {
            StringBuilder sb = new StringBuilder();

            String connector = "";
            sb.append("(");
            for (ResultProviderSelectQuery<?, R> query : queries) {
                sb.append(connector);
                sb.append("(");
                sb.append(query.toSQLReference(inlineParameters));
                sb.append(")");

                connector = " " + operator.toSQL(getDialect()) + " ";
            }
            sb.append(")");

            return sb.toString();
        }
    }
}
