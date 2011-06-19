/**
 * Copyright (c) 2009-2011, Lukas Eder, lukas.eder@gmail.com
 * All rights reserved.
 *
 * This software is licensed to you under the Apache License, Version 2.0
 * (the "License"); You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
 * . Neither the name "jOOQ" nor the names of its contributors may be
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jooq.Configuration;
import org.jooq.Cursor;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Select;
import org.jooq.Table;

abstract class AbstractResultProviderQuery<R extends Record> extends AbstractQuery implements Select<R> {

    private static final long       serialVersionUID = 5432006637149005588L;
    private static final JooqLogger log              = JooqLogger.getLogger(AbstractResultProviderQuery.class);

    private ResultImpl<R>           result;

    AbstractResultProviderQuery(Configuration configuration) {
        super(configuration);
    }

    @Override
    protected final int execute(Configuration configuration, PreparedStatement statement) throws SQLException {
        Cursor<R> cursor = executeLazy(configuration, statement);

        try {
            result = new ResultImpl<R>(configuration, ((CursorImpl<R>) cursor).fields);
            R record = null;

            while ((record = cursor.fetch()) != null) {
                result.addRecord(record);
            }

            if (log.isDebugEnabled()) {
                String comment = "Fetched result";

                for (String line : result.format(5).split("\n")) {
                    log.debug(comment, line);
                    comment = "";
                }
            }
        }
        finally {
            SQLUtils.safeClose(cursor);
        }

        return result.getNumberOfRecords();
    }

    @Override
    public final Result<R> fetch() throws SQLException {
        execute();
        return getResult();
    }

    @Override
    public final Cursor<R> fetchLazy() throws SQLException {
        Configuration configuration = attachable.getConfiguration();
        Connection connection = configuration.getConnection();

        if (connection != null) {
            return executeLazy(configuration, connection);
        }
        else {
            throw new SQLException("Cannot execute query. No Connection configured");
        }
    }

    private final Cursor<R> executeLazy(Configuration configuration, Connection connection) throws SQLException {
        PreparedStatement statement = null;

        String sql = toSQLReference(configuration);

        if (log.isDebugEnabled()) {
            log.debug("Lazy executing query", toSQLReference(configuration, true));
        }

        statement = connection.prepareStatement(sql);
        bindReference(configuration, statement);
        return executeLazy(configuration, statement);
    }

    private final Cursor<R> executeLazy(Configuration configuration, PreparedStatement statement) throws SQLException {
        ResultSet rs = statement.executeQuery();

        Class<? extends R> type = getRecordType();
        FieldList fields = new FieldList(getSelect());
        return new CursorImpl<R>(configuration, fields, type, statement, rs);
    }

    @Override
    public final <T> List<T> fetch(Field<T> field) throws SQLException {
        return fetch().getValues(field);
    }

    @Override
    public final List<?> fetch(int fieldIndex) throws SQLException {
        return fetch().getValues(fieldIndex);
    }

    @Override
    public final List<?> fetch(String fieldName) throws SQLException {
        return fetch().getValues(fieldName);
    }

    @Override
    public final <T> T fetchOne(Field<T> field) throws SQLException {
        R record = fetchOne();
        return record == null ? null : record.getValue(field);
    }

    @Override
    public final Object fetchOne(int fieldIndex) throws SQLException {
        R record = fetchOne();
        return record == null ? null : record.getValue(fieldIndex);
    }

    @Override
    public final Object fetchOne(String fieldName) throws SQLException {
        R record = fetchOne();
        return record == null ? null : record.getValue(fieldName);
    }

    @Override
    public final R fetchOne() throws SQLException {
        Result<R> r = fetch();

        if (r.getNumberOfRecords() == 1) {
            return r.getRecord(0);
        }
        else if (r.getNumberOfRecords() > 1) {
            throw new SQLException("Query returned more than one result");
        }

        return null;
    }

    @Override
    public final R fetchAny() throws SQLException {
        // TODO: restrict ROWNUM = 1
        Result<R> r = fetch();

        if (r.getNumberOfRecords() > 0) {
            return r.getRecord(0);
        }

        return null;
    }

    @Override
    public final <K> Map<K, R> fetchMap(Field<K> key) throws SQLException {
        Map<K, R> map = new LinkedHashMap<K, R>();

        for (R record : fetch()) {
            if (map.put(record.getValue(key), record) != null) {
                throw new SQLException("Key " + key + " is not unique in Result for " + this);
            }
        }

        return map;
    }

    @Override
    public final <K, V> Map<K, V> fetchMap(Field<K> key, Field<V> value) throws SQLException {
        Map<K, V> map = new LinkedHashMap<K, V>();

        for (Map.Entry<K, R> entry : fetchMap(key).entrySet()) {
            map.put(entry.getKey(), entry.getValue().getValue(value));
        }

        return map;
    }

    @Override
    public final Result<R> getResult() {
        return result;
    }

    @Override
    public final Select<R> union(Select<R> select) {
        return new UnionResultProviderQuery<R>(attachable.getConfiguration(), this, select, CombineOperator.UNION);
    }

    @Override
    public final Select<R> unionAll(Select<R> select) {
        return new UnionResultProviderQuery<R>(attachable.getConfiguration(), this, select, CombineOperator.UNION_ALL);
    }

    @Override
    public final Select<R> except(Select<R> select) {
        return new UnionResultProviderQuery<R>(attachable.getConfiguration(), this, select, CombineOperator.EXCEPT);
    }

    @Override
    public final Select<R> intersect(Select<R> select) {
        return new UnionResultProviderQuery<R>(attachable.getConfiguration(), this, select, CombineOperator.INTERSECT);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T> Field<T> asField() {
        if (getSelect().size() != 1) {
            throw new IllegalStateException("Can only use single-column ResultProviderQuery as a field");
        }

        return new SelectQueryAsField<T>(this, (DataType<T>) getSelect().get(0).getDataType());
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
    public final Field<?> getField(int index) {
        return asTable().getField(index);
    }

    @Override
    public final List<Field<?>> getFields() {
        return asTable().getFields();
    }

    @Override
    public final int getIndex(Field<?> field) throws IllegalArgumentException {
        return asTable().getIndex(field);
    }

    @Override
    public final Table<R> asTable() {
        Table<R> table = new SelectQueryAsTable<R>(this);

        // Its usually better to alias nested selects that are used in
        // the FROM clause of a query
        return table.as("alias_" + Math.abs(table.hashCode()));
    }

    @Override
    public final Table<R> asTable(String alias) {
        return asTable().as(alias);
    }
}
