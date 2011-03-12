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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.jooq.Configuration;
import org.jooq.DataType;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Table;

class DescribeQuery<R extends Record> extends AbstractQuery<Record> {

    private static final long       serialVersionUID = -2639180702290896997L;
    private static final JooqLogger log              = JooqLogger.getLogger(DescribeQuery.class);
    private final Table<R>          table;

    DescribeQuery(Configuration configuration, Table<R> table) {
        super(configuration);

        this.table = table;
    }

    @Override
    protected final int execute(PreparedStatement statement) throws SQLException {
        ResultSet rs = null;

        try {
            rs = statement.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();

            for (int i = 0; i < meta.getColumnCount(); i++) {
                String fieldName = meta.getColumnName(i + 1);
                int type = meta.getColumnType(i + 1);
                int precision = meta.getPrecision(i + 1);
                int scale = meta.getScale(i + 1);

                Class<?> fieldType = FieldTypeHelper.getClass(type, precision, scale);
                DataType<?> dataType = FieldTypeHelper.getDataType(getDialect(), fieldType);
                addField(fieldName, dataType);
            }

            if (log.isDebugEnabled()) {
                log.debug("Described table : " + table + " with fields " + table.getFields());
            }
        }
        finally {
            SQLUtils.safeClose(rs);
        }

        return 0;
    }

    private final <T> void addField(String fieldName, DataType<T> fieldType) {
        new TableFieldImpl<R, T>(getConfiguration(), fieldName, fieldType, table);
    }

    @Override
    public final String toSQLReference(Configuration configuration, boolean inlineParameters) {
        StringBuilder sb = new StringBuilder();

        Limit limit = new Limit(configuration);
        limit.setNumberOfRows(1);

        sb.append("select * from ");
        sb.append(table.getQueryPart().toSQLReference(configuration, inlineParameters));
        sb.append(" ");

        if (getDialect() != SQLDialect.ORACLE) {
            sb.append(limit.toSQLReference(configuration, inlineParameters));
        } else {
            sb.append("where rownum < 1");
        }

        return sb.toString();
    }

    @Override
    public final int bind(Configuration configuration, PreparedStatement stmt, int initialIndex) throws SQLException {
        return initialIndex;
    }
}
