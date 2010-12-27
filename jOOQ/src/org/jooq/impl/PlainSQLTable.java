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

import org.jooq.Configuration;
import org.jooq.Record;
import org.jooq.Table;

class PlainSQLTable extends AbstractTable<Record> {

    private static final long   serialVersionUID = -5122023013463718796L;
    private final String        sql;
    private final Object[]      bindings;
    private final Configuration configuration;
    private transient FieldList fields;

    public PlainSQLTable(Configuration configuration, String sql, Object[] bindings) {
        super(configuration, "");

        this.sql = sql;
        this.bindings = (bindings == null) ? new Object[0] : bindings;
        this.configuration = configuration;

        JooqUtil.checkArguments(sql, bindings);
    }

    @Override
    public final Class<? extends Record> getRecordType() {
        return RecordImpl.class;
    }

    @Override
    public final Table<Record> as(String alias) {
        return new TableAlias<Record>(getConfiguration(), this, alias, true);
    }

    @Override
    public final String toSQLReference(Configuration configuration, boolean inlineParameters) {
        return JooqUtil.toSQLReference(getDialect(), sql, bindings, inlineParameters);
    }

    @Override
    public int bind(Configuration configuration, PreparedStatement stmt, int initialIndex) throws SQLException {
        return JooqUtil.bind(stmt, initialIndex, bindings);
    }

    @Override
    protected FieldList getFieldList() {
        if (fields == null) {
            fields = new FieldList(getConfiguration());

            try {
                DescribeQuery<Record> query = new DescribeQuery<Record>(configuration, this);
                query.execute();
            }
            catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }

        return fields;
    }
}
