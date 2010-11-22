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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Select;
import org.jooq.Table;

abstract class AbstractResultProviderQuery<R extends Record> extends AbstractQuery<R> implements Select<R> {

    private static final long       serialVersionUID = 5432006637149005588L;
    private static final JooqLogger log              = JooqLogger.getLogger(AbstractResultProviderQuery.class);
    private ResultImpl<R>           result;

    AbstractResultProviderQuery(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected final int execute(PreparedStatement statement) throws SQLException {
        Class<? extends R> type = getRecordType();

        ResultSet rs = null;

        try {
            rs = statement.executeQuery();

            FieldList fields = new FieldList(getDialect(), getSelect());
            result = new ResultImpl<R>(fields);

            while (rs.next()) {
                R record = JooqUtil.newRecord(type, fields, getConfiguration());

                for (int i = 0; i < fields.size(); i++) {
                    // All Records extend RecordImpl, so this cast is safe
                    setValue((RecordImpl) record, fields.get(i), i + 1, rs);
                }

                if (log.isTraceEnabled()) {
                    log.trace("Fetching record : " + record);
                }

                result.addRecord(record);
            }
        }
        finally {
            SQLUtils.safeClose(rs);
        }

        return result.getNumberOfRecords();
    }

    @Override
    public final Result<R> fetch() throws SQLException {
        execute();
        return getResult();
    }

    @Override
    public final R fetchOne() throws SQLException {
        Result<R> result = fetch();

        if (result.getNumberOfRecords() == 1) {
            return result.getRecord(0);
        }
        else if (result.getNumberOfRecords() > 1) {
            throw new SQLException("Query returned more than one result");
        }

        return null;
    }

    @Override
    public final R fetchAny() throws SQLException {
        // TODO: restrict ROWNUM = 1
        Result<R> result = fetch();

        if (result.getNumberOfRecords() > 0) {
            return result.getRecord(0);
        }

        return null;
    }

    /**
     * Utility method to prevent unnecessary unchecked conversions
     */
    private final <T> void setValue(RecordImpl record, Field<T> field, int index, ResultSet rs) throws SQLException {
        T value = FieldTypeHelper.getFromResultSet(getDialect(), rs, field.getType(), index);
        record.setValue(field, new Value<T>(value));
    }

    @Override
    public final Result<R> getResult() {
        return result;
    }

    @Override
    public final Select<R> union(Select<R> select) {
        return new UnionResultProviderQuery<R>(this, select, CombineOperator.UNION);
    }

    @Override
    public final Select<R> unionAll(Select<R> select) {
        return new UnionResultProviderQuery<R>(this, select, CombineOperator.UNION_ALL);
    }

    @Override
    public final Select<R> except(Select<R> select) {
        return new UnionResultProviderQuery<R>(this, select, CombineOperator.EXCEPT);
    }

    @Override
    public final Select<R> intersect(Select<R> select) {
        return new UnionResultProviderQuery<R>(this, select, CombineOperator.INTERSECT);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T> Field<T> asField() {
        if (getSelect().size() != 1) {
            throw new IllegalStateException("Can only use single-column ResultProviderQuery as a field");
        }

        return new SelectQueryAsField<T>(getDialect(), this, (Class<? extends T>) getSelect().get(0).getType());
    }

    @Override
    public final <T> Field<T> asField(String alias) {
        return this.<T> asField().as(alias);
    }

    @Override
    public final <T> Field<T> getField(Field<T> field) {
        return asTable().getField(field);
    }

    @Override
    public final Field<?> getField(String name) {
        return asTable().getField(name);
    }

    @Override
    public final List<Field<?>> getFields() {
        return asTable().getFields();
    }

    @Override
    public final Table<R> asTable() {
        Table<R> result = new SelectQueryAsTable<R>(getDialect(), this);

        // Its usually better to alias nested selects that are used in
        // the FROM clause of a query
        return result.as("alias_" + Math.abs(result.getQueryPart().toSQLReference().hashCode()));
    }

    @Override
    public final Table<R> asTable(String alias) {
        return asTable().as(alias);
    }
}
