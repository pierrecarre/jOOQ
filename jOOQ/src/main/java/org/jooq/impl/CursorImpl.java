/**
 * Copyright (c) 2009-2013, Data Geekery GmbH (http://www.datageekery.com)
 * All rights reserved.
 *
 * This work is dual-licensed
 * - under the Apache Software License 2.0 (the "ASL")
 * - under the jOOQ License and Maintenance Agreement (the "jOOQ License")
 * =============================================================================
 * You may choose which license applies to you:
 *
 * - If you're using this work with Open Source databases, you may choose
 *   either ASL or jOOQ License.
 * - If you're using this work with at least one commercial database, you must
 *   choose jOOQ License
 *
 * For more information, please visit http://www.jooq.org/licenses
 *
 * Apache Software License 2.0:
 * -----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * jOOQ License and Maintenance Agreement:
 * -----------------------------------------------------------------------------
 * Data Geekery grants the Customer the non-exclusive, timely limited and
 * non-transferable license to install and use the Software under the terms of
 * the jOOQ License and Maintenance Agreement.
 *
 * This library is distributed with a LIMITED WARRANTY. See the jOOQ License
 * and Maintenance Agreement for more details: http://www.jooq.org/licensing
 */
package org.jooq.impl;

import static java.lang.Boolean.TRUE;
import static org.jooq.impl.Utils.DATA_LOCK_ROWS_FOR_UPDATE;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jooq.Cursor;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordHandler;
import org.jooq.RecordMapper;
import org.jooq.RecordType;
import org.jooq.Result;
import org.jooq.Row;
import org.jooq.Table;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.jdbc.JDBC41ResultSet;
import org.jooq.tools.jdbc.JDBCUtils;

/**
 * @author Lukas Eder
 */
class CursorImpl<R extends Record> implements Cursor<R> {

    private static final JooqLogger   log = JooqLogger.getLogger(CursorImpl.class);

    private final ExecuteContext      ctx;
    private final ExecuteListener     listener;
    private final Field<?>[]          fields;
    private final boolean[]           intern;
    private final boolean             keepResultSet;
    private final boolean             keepStatement;
    private final Class<? extends R>  type;
    private boolean                   isClosed;

    private transient CursorResultSet rs;
    private transient Iterator<R>     iterator;
    private transient int             rows;

    @SuppressWarnings("unchecked")
    CursorImpl(ExecuteContext ctx, ExecuteListener listener, Field<?>[] fields, int[] internIndexes, boolean keepStatement, boolean keepResultSet) {
        this(ctx, listener, fields, internIndexes, keepStatement, keepResultSet, (Class<? extends R>) RecordImpl.class);
    }

    CursorImpl(ExecuteContext ctx, ExecuteListener listener, Field<?>[] fields, int[] internIndexes, boolean keepStatement, boolean keepResultSet, Class<? extends R> type) {
        this.ctx = ctx;
        this.listener = (listener != null ? listener : new ExecuteListeners(ctx));
        this.fields = fields;
        this.type = type;
        this.keepStatement = keepStatement;
        this.keepResultSet = keepResultSet;
        this.rs = new CursorResultSet();
        this.intern = new boolean[fields.length];

        if (internIndexes != null) {
            for (int i : internIndexes) {
                intern[i] = true;
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    
    public final RecordType<R> recordType() {
        return new RowImpl(fields).fields;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    
    public final Row fieldsRow() {
        return new RowImpl(fields);
    }

    
    public final <T> Field<T> field(Field<T> field) {
        return fieldsRow().field(field);
    }

    
    public final Field<?> field(String name) {
        return fieldsRow().field(name);
    }

    
    public final Field<?> field(int index) {
        return index >= 0 && index < fields.length ? fields[index] : null;
    }

    
    public final Field<?>[] fields() {
        return fields.clone();
    }

    
    public final Iterator<R> iterator() {
        if (iterator == null) {
            iterator = new CursorIterator();
            listener.fetchStart(ctx);
        }

        return iterator;
    }

    
    public final boolean hasNext() {
        return iterator().hasNext();
    }

    
    public final Result<R> fetch() {
        return fetch(Integer.MAX_VALUE);
    }

    
    public final R fetchOne() {
        Result<R> result = fetch(1);

        if (result.size() == 1) {
            return result.get(0);
        }

        return null;
    }

    
    public final Result<R> fetch(int number) {
        // [#1157] This invokes listener.fetchStart(ctx), which has to be called
        // Before listener.resultStart(ctx)
        iterator();

        ResultImpl<R> result = new ResultImpl<R>(ctx.configuration(), fields);
        R record = null;

        ctx.result(result);
        listener.resultStart(ctx);

        for (int i = 0; i < number && ((record = iterator().next()) != null); i++) {
            result.addRecord(record);
        }

        ctx.result(result);
        listener.resultEnd(ctx);

        return result;
    }

    
    public final <H extends RecordHandler<? super R>> H fetchOneInto(H handler) {
        handler.next(fetchOne());
        return handler;
    }

    
    public final <H extends RecordHandler<? super R>> H fetchInto(H handler) {
        while (hasNext()) {
            fetchOneInto(handler);
        }

        return handler;
    }

    
    public final <E> E fetchOne(RecordMapper<? super R, E> mapper) {
        return mapper.map(fetchOne());
    }

    
    public final <E> List<E> fetch(RecordMapper<? super R, E> mapper) {
        return fetch().map(mapper);
    }

    
    public final <E> E fetchOneInto(Class<? extends E> clazz) {
        R record = fetchOne();
        return record == null ? null : record.into(clazz);
    }

    
    public final <E> List<E> fetchInto(Class<? extends E> clazz) {
        return fetch().into(clazz);
    }

    
    public final <Z extends Record> Z fetchOneInto(Table<Z> table) {
        return fetchOne().into(table);
    }

    
    public final <Z extends Record> Result<Z> fetchInto(Table<Z> table) {
        return fetch().into(table);
    }

    
    public final void close() {
        JDBCUtils.safeClose(rs);
        rs = null;
        isClosed = true;
    }

    
    public final boolean isClosed() {
        return isClosed;
    }

    
    public final ResultSet resultSet() {
        return rs;
    }

    /**
     * A wrapper for the underlying JDBC {@link ResultSet} and {@link Statement}
     */
    final class CursorResultSet extends JDBC41ResultSet implements ResultSet {

        // ---------------------------------------------------------------------
        // XXX: Wrapper methods
        // ---------------------------------------------------------------------

        
        public final <T> T unwrap(Class<T> iface) throws SQLException {
            return ctx.resultSet().unwrap(iface);
        }

        
        public final boolean isWrapperFor(Class<?> iface) throws SQLException {
            return ctx.resultSet().isWrapperFor(iface);
        }

        // ---------------------------------------------------------------------
        // XXX: Informational methods
        // ---------------------------------------------------------------------

        
        public final Statement getStatement() throws SQLException {
            return ctx.resultSet().getStatement();
        }

        
        public final SQLWarning getWarnings() throws SQLException {
            return ctx.resultSet().getWarnings();
        }

        
        public final void clearWarnings() throws SQLException {
            ctx.resultSet().clearWarnings();
        }

        
        public final String getCursorName() throws SQLException {
            return ctx.resultSet().getCursorName();
        }

        
        public final ResultSetMetaData getMetaData() throws SQLException {
            return ctx.resultSet().getMetaData();
        }

        
        public final int findColumn(String columnLabel) throws SQLException {
            return ctx.resultSet().findColumn(columnLabel);
        }

        
        public final void setFetchDirection(int direction) throws SQLException {
            ctx.resultSet().setFetchDirection(direction);
        }

        
        public final int getFetchDirection() throws SQLException {
            return ctx.resultSet().getFetchDirection();
        }

        
        public final void setFetchSize(int rows) throws SQLException {
            ctx.resultSet().setFetchSize(rows);
        }

        
        public final int getFetchSize() throws SQLException {
            return ctx.resultSet().getFetchSize();
        }

        
        public final int getType() throws SQLException {
            return ctx.resultSet().getType();
        }

        
        public final int getConcurrency() throws SQLException {
            return ctx.resultSet().getConcurrency();
        }

        
        public final int getHoldability() throws SQLException {
            return ctx.resultSet().getHoldability();
        }

        // ---------------------------------------------------------------------
        // XXX: Navigational methods
        // ---------------------------------------------------------------------

        
        public final boolean isBeforeFirst() throws SQLException {
            return ctx.resultSet().isBeforeFirst();
        }

        
        public final boolean isAfterLast() throws SQLException {
            return ctx.resultSet().isAfterLast();
        }

        
        public final boolean isFirst() throws SQLException {
            return ctx.resultSet().isFirst();
        }

        
        public final boolean isLast() throws SQLException {
            return ctx.resultSet().isLast();
        }

        
        public final boolean next() throws SQLException {
            return ctx.resultSet().next();
        }

        
        public final boolean previous() throws SQLException {
            return ctx.resultSet().previous();
        }

        
        public final void beforeFirst() throws SQLException {
            ctx.resultSet().beforeFirst();
        }

        
        public final void afterLast() throws SQLException {
            ctx.resultSet().afterLast();
        }

        
        public final boolean first() throws SQLException {
            return ctx.resultSet().first();
        }

        
        public final boolean last() throws SQLException {
            return ctx.resultSet().last();
        }

        
        public final int getRow() throws SQLException {
            return ctx.resultSet().getRow();
        }

        
        public final boolean absolute(int row) throws SQLException {
            return ctx.resultSet().absolute(row);
        }

        
        public final boolean relative(int r) throws SQLException {
            return ctx.resultSet().relative(r);
        }

        
        public final void moveToInsertRow() throws SQLException {
            ctx.resultSet().moveToInsertRow();
        }

        
        public final void moveToCurrentRow() throws SQLException {
            ctx.resultSet().moveToCurrentRow();
        }

        
        public final void close() throws SQLException {
            ctx.rows(rows);
            listener.fetchEnd(ctx);

            // [#1868] If this Result / Cursor was "kept" through a lazy
            // execution, we must assure that the ExecuteListener lifecycle is
            // correctly terminated.
            Utils.safeClose(listener, ctx, keepStatement, keepResultSet);
        }

        
        public final boolean isClosed() throws SQLException {
            return ctx.resultSet().isClosed();
        }

        // ---------------------------------------------------------------------
        // XXX: Data retrieval
        // ---------------------------------------------------------------------

        
        public final boolean wasNull() throws SQLException {
            return ctx.resultSet().wasNull();
        }

        
        public final Array getArray(int columnIndex) throws SQLException {
            return ctx.resultSet().getArray(columnIndex);
        }

        
        public final Array getArray(String columnLabel) throws SQLException {
            return ctx.resultSet().getArray(columnLabel);
        }

        
        public final InputStream getAsciiStream(int columnIndex) throws SQLException {
            return ctx.resultSet().getAsciiStream(columnIndex);
        }

        
        public final InputStream getAsciiStream(String columnLabel) throws SQLException {
            return ctx.resultSet().getAsciiStream(columnLabel);
        }

        
        public final BigDecimal getBigDecimal(int columnIndex) throws SQLException {
            return ctx.resultSet().getBigDecimal(columnIndex);
        }

        
        @Deprecated
        public final BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
            return ctx.resultSet().getBigDecimal(columnIndex, scale);
        }

        
        public final BigDecimal getBigDecimal(String columnLabel) throws SQLException {
            return ctx.resultSet().getBigDecimal(columnLabel);
        }

        
        @Deprecated
        public final BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
            return ctx.resultSet().getBigDecimal(columnLabel, scale);
        }

        
        public final InputStream getBinaryStream(int columnIndex) throws SQLException {
            return ctx.resultSet().getBinaryStream(columnIndex);
        }

        
        public final InputStream getBinaryStream(String columnLabel) throws SQLException {
            return ctx.resultSet().getBinaryStream(columnLabel);
        }

        
        public final Blob getBlob(int columnIndex) throws SQLException {
            return ctx.resultSet().getBlob(columnIndex);
        }

        
        public final Blob getBlob(String columnLabel) throws SQLException {
            return ctx.resultSet().getBlob(columnLabel);
        }

        
        public final boolean getBoolean(int columnIndex) throws SQLException {
            return ctx.resultSet().getBoolean(columnIndex);
        }

        
        public final boolean getBoolean(String columnLabel) throws SQLException {
            return ctx.resultSet().getBoolean(columnLabel);
        }

        
        public final byte getByte(int columnIndex) throws SQLException {
            return ctx.resultSet().getByte(columnIndex);
        }

        
        public final byte getByte(String columnLabel) throws SQLException {
            return ctx.resultSet().getByte(columnLabel);
        }

        
        public final byte[] getBytes(int columnIndex) throws SQLException {
            return ctx.resultSet().getBytes(columnIndex);
        }

        
        public final byte[] getBytes(String columnLabel) throws SQLException {
            return ctx.resultSet().getBytes(columnLabel);
        }

        
        public final Reader getCharacterStream(int columnIndex) throws SQLException {
            return ctx.resultSet().getCharacterStream(columnIndex);
        }

        
        public final Reader getCharacterStream(String columnLabel) throws SQLException {
            return ctx.resultSet().getCharacterStream(columnLabel);
        }

        
        public final Clob getClob(int columnIndex) throws SQLException {
            return ctx.resultSet().getClob(columnIndex);
        }

        
        public final Clob getClob(String columnLabel) throws SQLException {
            return ctx.resultSet().getClob(columnLabel);
        }

        
        public final Date getDate(int columnIndex) throws SQLException {
            return ctx.resultSet().getDate(columnIndex);
        }

        
        public final Date getDate(int columnIndex, Calendar cal) throws SQLException {
            return ctx.resultSet().getDate(columnIndex, cal);
        }

        
        public final Date getDate(String columnLabel) throws SQLException {
            return ctx.resultSet().getDate(columnLabel);
        }

        
        public final Date getDate(String columnLabel, Calendar cal) throws SQLException {
            return ctx.resultSet().getDate(columnLabel, cal);
        }

        
        public final double getDouble(int columnIndex) throws SQLException {
            return ctx.resultSet().getDouble(columnIndex);
        }

        
        public final double getDouble(String columnLabel) throws SQLException {
            return ctx.resultSet().getDouble(columnLabel);
        }

        
        public final float getFloat(int columnIndex) throws SQLException {
            return ctx.resultSet().getFloat(columnIndex);
        }

        
        public final float getFloat(String columnLabel) throws SQLException {
            return ctx.resultSet().getFloat(columnLabel);
        }

        
        public final int getInt(int columnIndex) throws SQLException {
            return ctx.resultSet().getInt(columnIndex);
        }

        
        public final int getInt(String columnLabel) throws SQLException {
            return ctx.resultSet().getInt(columnLabel);
        }

        
        public final long getLong(int columnIndex) throws SQLException {
            return ctx.resultSet().getLong(columnIndex);
        }

        
        public final long getLong(String columnLabel) throws SQLException {
            return ctx.resultSet().getLong(columnLabel);
        }

        
        public final Reader getNCharacterStream(int columnIndex) throws SQLException {
            return ctx.resultSet().getNCharacterStream(columnIndex);
        }

        
        public final Reader getNCharacterStream(String columnLabel) throws SQLException {
            return ctx.resultSet().getNCharacterStream(columnLabel);
        }

        
        public final NClob getNClob(int columnIndex) throws SQLException {
            return ctx.resultSet().getNClob(columnIndex);
        }

        
        public final NClob getNClob(String columnLabel) throws SQLException {
            return ctx.resultSet().getNClob(columnLabel);
        }

        
        public final String getNString(int columnIndex) throws SQLException {
            return ctx.resultSet().getNString(columnIndex);
        }

        
        public final String getNString(String columnLabel) throws SQLException {
            return ctx.resultSet().getNString(columnLabel);
        }

        
        public final Object getObject(int columnIndex) throws SQLException {
            return ctx.resultSet().getObject(columnIndex);
        }

        
        public final Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
            return ctx.resultSet().getObject(columnIndex, map);
        }

        
        public final Object getObject(String columnLabel) throws SQLException {
            return ctx.resultSet().getObject(columnLabel);
        }

        
        public final Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
            return ctx.resultSet().getObject(columnLabel, map);
        }

        
        public final Ref getRef(int columnIndex) throws SQLException {
            return ctx.resultSet().getRef(columnIndex);
        }

        
        public final Ref getRef(String columnLabel) throws SQLException {
            return ctx.resultSet().getRef(columnLabel);
        }

        
        public final RowId getRowId(int columnIndex) throws SQLException {
            return ctx.resultSet().getRowId(columnIndex);
        }

        
        public final RowId getRowId(String columnLabel) throws SQLException {
            return ctx.resultSet().getRowId(columnLabel);
        }

        
        public final short getShort(int columnIndex) throws SQLException {
            return ctx.resultSet().getShort(columnIndex);
        }

        
        public final short getShort(String columnLabel) throws SQLException {
            return ctx.resultSet().getShort(columnLabel);
        }

        
        public final SQLXML getSQLXML(int columnIndex) throws SQLException {
            return ctx.resultSet().getSQLXML(columnIndex);
        }

        
        public final SQLXML getSQLXML(String columnLabel) throws SQLException {
            return ctx.resultSet().getSQLXML(columnLabel);
        }

        
        public final String getString(int columnIndex) throws SQLException {
            return ctx.resultSet().getString(columnIndex);
        }

        
        public final String getString(String columnLabel) throws SQLException {
            return ctx.resultSet().getString(columnLabel);
        }

        
        public final Time getTime(int columnIndex) throws SQLException {
            return ctx.resultSet().getTime(columnIndex);
        }

        
        public final Time getTime(int columnIndex, Calendar cal) throws SQLException {
            return ctx.resultSet().getTime(columnIndex, cal);
        }

        
        public final Time getTime(String columnLabel) throws SQLException {
            return ctx.resultSet().getTime(columnLabel);
        }

        
        public final Time getTime(String columnLabel, Calendar cal) throws SQLException {
            return ctx.resultSet().getTime(columnLabel, cal);
        }

        
        public final Timestamp getTimestamp(int columnIndex) throws SQLException {
            return ctx.resultSet().getTimestamp(columnIndex);
        }

        
        public final Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
            return ctx.resultSet().getTimestamp(columnIndex, cal);
        }

        
        public final Timestamp getTimestamp(String columnLabel) throws SQLException {
            return ctx.resultSet().getTimestamp(columnLabel);
        }

        
        public final Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
            return ctx.resultSet().getTimestamp(columnLabel, cal);
        }

        
        @Deprecated
        public final InputStream getUnicodeStream(int columnIndex) throws SQLException {
            return ctx.resultSet().getUnicodeStream(columnIndex);
        }

        
        @Deprecated
        public final InputStream getUnicodeStream(String columnLabel) throws SQLException {
            return ctx.resultSet().getUnicodeStream(columnLabel);
        }

        
        public final URL getURL(int columnIndex) throws SQLException {
            return ctx.resultSet().getURL(columnIndex);
        }

        
        public final URL getURL(String columnLabel) throws SQLException {
            return ctx.resultSet().getURL(columnLabel);
        }

        // ---------------------------------------------------------------------
        // XXX: Data modification
        // ---------------------------------------------------------------------

        private final void logUpdate(int columnIndex, Object x) throws SQLException {
            if (log.isDebugEnabled()) {
                log.debug("Updating Result", "Updating Result position " + getRow() + ":" + columnIndex + " with value " + x);
            }
        }

        private final void logUpdate(String columnLabel, Object x) throws SQLException {
            if (log.isDebugEnabled()) {
                log.debug("Updating Result", "Updating Result position " + getRow() + ":" + columnLabel + " with value " + x);
            }
        }

        
        public final boolean rowUpdated() throws SQLException {
            return ctx.resultSet().rowUpdated();
        }

        
        public final boolean rowInserted() throws SQLException {
            return ctx.resultSet().rowInserted();
        }

        
        public final boolean rowDeleted() throws SQLException {
            return ctx.resultSet().rowDeleted();
        }

        
        public final void insertRow() throws SQLException {
            ctx.resultSet().insertRow();
        }

        
        public final void updateRow() throws SQLException {
            ctx.resultSet().updateRow();
        }

        
        public final void deleteRow() throws SQLException {
            ctx.resultSet().deleteRow();
        }

        
        public final void refreshRow() throws SQLException {
            ctx.resultSet().refreshRow();
        }

        
        public final void cancelRowUpdates() throws SQLException {
            ctx.resultSet().cancelRowUpdates();
        }

        
        public final void updateNull(int columnIndex) throws SQLException {
            logUpdate(columnIndex, null);
            ctx.resultSet().updateNull(columnIndex);
        }

        
        public final void updateNull(String columnLabel) throws SQLException {
            logUpdate(columnLabel, null);
            ctx.resultSet().updateNull(columnLabel);
        }

        
        public final void updateArray(int columnIndex, Array x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateArray(columnIndex, x);
        }

        
        public final void updateArray(String columnLabel, Array x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateArray(columnLabel, x);
        }

        
        public final void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateAsciiStream(columnIndex, x);
        }

        
        public final void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateAsciiStream(columnIndex, x, length);
        }

        
        public final void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateAsciiStream(columnIndex, x, length);
        }

        
        public final void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateAsciiStream(columnLabel, x);
        }

        
        public final void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateAsciiStream(columnLabel, x, length);
        }

        
        public final void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateAsciiStream(columnLabel, x, length);
        }

        
        public final void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateBigDecimal(columnIndex, x);
        }

        
        public final void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateBigDecimal(columnLabel, x);
        }

        
        public final void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateBinaryStream(columnIndex, x);
        }

        
        public final void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateBinaryStream(columnIndex, x, length);
        }

        
        public final void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateBinaryStream(columnIndex, x, length);
        }

        
        public final void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateBinaryStream(columnLabel, x);
        }

        
        public final void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateBinaryStream(columnLabel, x, length);
        }

        
        public final void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateBinaryStream(columnLabel, x, length);
        }

        
        public final void updateBlob(int columnIndex, Blob x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateBlob(columnIndex, x);
        }

        
        public final void updateBlob(int columnIndex, InputStream x, long length) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateBlob(columnIndex, x, length);
        }

        
        public final void updateBlob(String columnLabel, Blob x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateBlob(columnLabel, x);
        }

        
        public final void updateBlob(int columnIndex, InputStream x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateBlob(columnIndex, x);
        }

        
        public final void updateBlob(String columnLabel, InputStream x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateBlob(columnLabel, x);
        }

        
        public final void updateBlob(String columnLabel, InputStream x, long length) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateBlob(columnLabel, x, length);
        }

        
        public final void updateBoolean(int columnIndex, boolean x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateBoolean(columnIndex, x);
        }

        
        public final void updateBoolean(String columnLabel, boolean x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateBoolean(columnLabel, x);
        }

        
        public final void updateByte(int columnIndex, byte x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateByte(columnIndex, x);
        }

        
        public final void updateByte(String columnLabel, byte x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateByte(columnLabel, x);
        }

        
        public final void updateBytes(int columnIndex, byte[] x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateBytes(columnIndex, x);
        }

        
        public final void updateBytes(String columnLabel, byte[] x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateBytes(columnLabel, x);
        }

        
        public final void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateCharacterStream(columnIndex, x);
        }

        
        public final void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateCharacterStream(columnIndex, x, length);
        }

        
        public final void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateCharacterStream(columnIndex, x, length);
        }

        
        public final void updateCharacterStream(String columnLabel, Reader x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateCharacterStream(columnLabel, x);
        }

        
        public final void updateCharacterStream(String columnLabel, Reader x, int length) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateCharacterStream(columnLabel, x, length);
        }

        
        public final void updateCharacterStream(String columnLabel, Reader x, long length) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateCharacterStream(columnLabel, x, length);
        }

        
        public final void updateClob(int columnIndex, Clob x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateClob(columnIndex, x);
        }

        
        public final void updateClob(int columnIndex, Reader x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateClob(columnIndex, x);
        }

        
        public final void updateClob(int columnIndex, Reader x, long length) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateClob(columnIndex, x, length);
        }

        
        public final void updateClob(String columnLabel, Clob x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateClob(columnLabel, x);
        }

        
        public final void updateClob(String columnLabel, Reader x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateClob(columnLabel, x);
        }

        
        public final void updateClob(String columnLabel, Reader x, long length) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateClob(columnLabel, x, length);
        }

        
        public final void updateDate(int columnIndex, Date x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateDate(columnIndex, x);
        }

        
        public final void updateDate(String columnLabel, Date x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateDate(columnLabel, x);
        }

        
        public final void updateDouble(int columnIndex, double x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateDouble(columnIndex, x);
        }

        
        public final void updateDouble(String columnLabel, double x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateDouble(columnLabel, x);
        }

        
        public final void updateFloat(int columnIndex, float x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateFloat(columnIndex, x);
        }

        
        public final void updateFloat(String columnLabel, float x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateFloat(columnLabel, x);
        }

        
        public final void updateInt(int columnIndex, int x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateInt(columnIndex, x);
        }

        
        public final void updateInt(String columnLabel, int x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateInt(columnLabel, x);
        }

        
        public final void updateLong(int columnIndex, long x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateLong(columnIndex, x);
        }

        
        public final void updateLong(String columnLabel, long x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateLong(columnLabel, x);
        }

        
        public final void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateNCharacterStream(columnIndex, x);
        }

        
        public final void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateNCharacterStream(columnIndex, x, length);
        }

        
        public final void updateNCharacterStream(String columnLabel, Reader x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateNCharacterStream(columnLabel, x);
        }

        
        public final void updateNCharacterStream(String columnLabel, Reader x, long length) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateNCharacterStream(columnLabel, x, length);
        }

        
        public final void updateNClob(int columnIndex, NClob x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateNClob(columnIndex, x);
        }

        
        public final void updateNClob(int columnIndex, Reader x, long length) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateNClob(columnIndex, x, length);
        }

        
        public final void updateNClob(int columnIndex, Reader x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateNClob(columnIndex, x);
        }

        
        public final void updateNClob(String columnLabel, NClob x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateNClob(columnLabel, x);
        }

        
        public final void updateNClob(String columnLabel, Reader x, long length) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateNClob(columnLabel, x, length);
        }

        
        public final void updateNClob(String columnLabel, Reader x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateNClob(columnLabel, x);
        }

        
        public final void updateNString(int columnIndex, String x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateNString(columnIndex, x);
        }

        
        public final void updateNString(String columnLabel, String x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateNString(columnLabel, x);
        }

        
        public final void updateObject(int columnIndex, Object x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateObject(columnIndex, x);
        }

        
        public final void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateObject(columnIndex, x, scaleOrLength);
        }

        
        public final void updateObject(String columnLabel, Object x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateObject(columnLabel, x);
        }

        
        public final void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateObject(columnLabel, x, scaleOrLength);
        }

        
        public final void updateRef(int columnIndex, Ref x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateRef(columnIndex, x);
        }

        
        public final void updateRef(String columnLabel, Ref x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateRef(columnLabel, x);
        }

        
        public final void updateRowId(int columnIndex, RowId x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateRowId(columnIndex, x);
        }

        
        public final void updateRowId(String columnLabel, RowId x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateRowId(columnLabel, x);
        }

        
        public final void updateShort(int columnIndex, short x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateShort(columnIndex, x);
        }

        
        public final void updateShort(String columnLabel, short x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateShort(columnLabel, x);
        }

        
        public final void updateSQLXML(int columnIndex, SQLXML x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateSQLXML(columnIndex, x);
        }

        
        public final void updateSQLXML(String columnLabel, SQLXML x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateSQLXML(columnLabel, x);
        }

        
        public final void updateString(int columnIndex, String x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateString(columnIndex, x);
        }

        
        public final void updateString(String columnLabel, String x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateString(columnLabel, x);
        }

        
        public final void updateTime(int columnIndex, Time x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateTime(columnIndex, x);
        }

        
        public final void updateTime(String columnLabel, Time x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateTime(columnLabel, x);
        }

        
        public final void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
            logUpdate(columnIndex, x);
            ctx.resultSet().updateTimestamp(columnIndex, x);
        }

        
        public final void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
            logUpdate(columnLabel, x);
            ctx.resultSet().updateTimestamp(columnLabel, x);
        }
    }

    /**
     * An iterator for records fetched by this cursor
     */
    final class CursorIterator implements Iterator<R> {

        /**
         * The (potentially) pre-fetched next record
         */
        private R                             next;

        /**
         * Whether the underlying {@link ResultSet} has a next record. This
         * boolean has three states:
         * <ul>
         * <li>null: it's not known whether there is a next record</li>
         * <li>true: there is a next record, and it has been pre-fetched</li>
         * <li>false: there aren't any next records</li>
         * </ul>
         */
        private Boolean                       hasNext;

        /**
         * A delegate runnable that handles record initialisation.
         */
        private final CursorRecordInitialiser initialiser = new CursorRecordInitialiser();

        
        public final boolean hasNext() {
            if (hasNext == null) {
                next = fetchOne();
                hasNext = (next != null);
            }

            return hasNext;
        }

        
        public final R next() {
            if (hasNext == null) {
                return fetchOne();
            }

            R result = next;
            hasNext = null;
            next = null;
            return result;
        }

        @SuppressWarnings("unchecked")
        private final R fetchOne() {
            AbstractRecord record = null;

            try {
                if (!isClosed && rs.next()) {

                    // [#1296] Force a row-lock by updating the row if the
                    // FOR UPDATE clause is simulated
                    if (TRUE.equals(ctx.data(DATA_LOCK_ROWS_FOR_UPDATE))) {
                        rs.updateObject(1, rs.getObject(1));
                        rs.updateRow();
                    }

                    record = Utils.newRecord((Class<AbstractRecord>) type, fields, ctx.configuration())
                                  .operate(initialiser);

                    rows++;
                }
            }
            catch (SQLException e) {
                ctx.sqlException(e);
                listener.exception(ctx);
                throw ctx.exception();
            }

            // [#1868] [#2373] [#2385] This calls through to Utils.safeClose()
            // if necessary, lazy-terminating the ExecuteListener lifecycle if
            // the result is not eager-fetched.
            if (record == null) {
                CursorImpl.this.close();
            }

            return (R) record;
        }

        
        public final void remove() {
            throw new UnsupportedOperationException();
        }

        private class CursorRecordInitialiser implements RecordOperation<AbstractRecord, SQLException> {

            
            public AbstractRecord operate(AbstractRecord record) throws SQLException {
                ctx.record(record);
                listener.recordStart(ctx);

                for (int i = 0; i < fields.length; i++) {
                    setValue(record, fields[i], i);

                    if (intern[i]) {
                        record.getValue0(i).intern();
                    }
                }

                ctx.record(record);
                listener.recordEnd(ctx);

                return record;
            }

            /**
             * Utility method to prevent unnecessary unchecked conversions
             */
            private final <T> void setValue(AbstractRecord record, Field<T> field, int index) throws SQLException {
                T value = Utils.getFromResultSet(ctx, field, index + 1);
                record.setValue(index, new Value<T>(value));
            }
        }
    }
}
