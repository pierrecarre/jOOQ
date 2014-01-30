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

import static java.sql.ResultSet.CONCUR_UPDATABLE;
import static java.sql.ResultSet.TYPE_SCROLL_SENSITIVE;
import static java.util.Arrays.asList;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
// ...
import static org.jooq.SQLDialect.CUBRID;
// ...
import static org.jooq.impl.Utils.DATA_LOCK_ROWS_FOR_UPDATE;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.jooq.Configuration;
import org.jooq.Converter;
import org.jooq.Cursor;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordHandler;
import org.jooq.RecordMapper;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.jooq.Table;
import org.jooq.exception.DataTypeException;
import org.jooq.tools.Convert;
import org.jooq.tools.JooqLogger;

/**
 * A query that returns a {@link Result}
 *
 * @author Lukas Eder
 */
abstract class AbstractResultQuery<R extends Record> extends AbstractQuery implements ResultQuery<R> {

    /**
     * Generated UID
     */
    private static final long       serialVersionUID = -5588344253566055707L;
    private static final JooqLogger log              = JooqLogger.getLogger(AbstractResultQuery.class);

    private int                     maxRows;
    private int                     resultSetConcurrency;
    private int                     resultSetType;
    private int                     resultSetHoldability;
    private transient boolean       lazy;
    private transient int           size;
    private transient boolean       many;
    private transient Cursor<R>     cursor;
    private Result<R>               result;
    private List<Result<Record>>    results;

    // Some temp variables for String interning
    private int[]                   internIndexes;
    private Field<?>[]              internFields;
    private String[]                internNames;

    AbstractResultQuery(Configuration configuration) {
        super(configuration);
    }

    /**
     * Get a list of fields provided a result set.
     */
    protected abstract Field<?>[] getFields(ResultSetMetaData rs) throws SQLException;

    @SuppressWarnings("unchecked")
    
    public final ResultQuery<R> bind(String param, Object value) throws IllegalArgumentException, DataTypeException {
        return (ResultQuery<R>) super.bind(param, value);
    }

    @SuppressWarnings("unchecked")
    
    public final ResultQuery<R> bind(int index, Object value) throws IllegalArgumentException, DataTypeException {
        return (ResultQuery<R>) super.bind(index, value);
    }

    @SuppressWarnings("unchecked")
    
    public final ResultQuery<R> queryTimeout(int timeout) {
        return (ResultQuery<R>) super.queryTimeout(timeout);
    }

    @SuppressWarnings("unchecked")
    
    public final ResultQuery<R> keepStatement(boolean k) {
        return (ResultQuery<R>) super.keepStatement(k);
    }

    
    public final ResultQuery<R> maxRows(int rows) {
        this.maxRows = rows;
        return this;
    }

    
    public final ResultQuery<R> resultSetConcurrency(int concurrency) {
        this.resultSetConcurrency = concurrency;
        return this;
    }

    
    public final ResultQuery<R> resultSetType(int type) {
        this.resultSetType = type;
        return this;
    }

    
    public final ResultQuery<R> resultSetHoldability(int holdability) {
        this.resultSetHoldability = holdability;
        return this;
    }

    
    public final ResultQuery<R> intern(Field<?>... fields) {
        this.internFields = fields;
        return this;
    }

    
    public final ResultQuery<R> intern(int... fieldIndexes) {
        this.internIndexes = fieldIndexes;
        return this;
    }

    
    public final ResultQuery<R> intern(String... fieldNames) {
        this.internNames = fieldNames;
        return this;
    }

    private final int[] internIndexes(Field<?>[] fields) {
        if (internIndexes != null) {
            return internIndexes;
        }
        else if (internFields != null) {
            return new Fields<Record>(fields).indexesOf(internFields);
        }
        else if (internNames != null) {
            return new Fields<Record>(fields).indexesOf(internNames);
        }

        return null;
    }

    
    protected final void prepare(ExecuteContext ctx) throws SQLException {

        // [#1846] [#2265] [#2299] Users may explicitly specify how ResultSets
        // created by jOOQ behave. This will override any other default behaviour
        if (resultSetConcurrency != 0 || resultSetType != 0 || resultSetHoldability != 0) {
            int type = resultSetType != 0 ? resultSetType : ResultSet.TYPE_FORWARD_ONLY;
            int concurrency = resultSetConcurrency != 0 ? resultSetConcurrency : ResultSet.CONCUR_READ_ONLY;

            // Sybase doesn't support holdability. Avoid setting it!
            if (resultSetHoldability == 0) {
                ctx.statement(ctx.connection().prepareStatement(ctx.sql(), type, concurrency));
            }
            else {
                ctx.statement(ctx.connection().prepareStatement(ctx.sql(), type, concurrency, resultSetHoldability));
            }
        }

        // [#1296] These dialects do not implement FOR UPDATE. But the same
        // effect can be achieved using ResultSet.CONCUR_UPDATABLE
        else if (isForUpdate() && asList(CUBRID).contains(ctx.configuration().dialect().family())) {
            ctx.data(DATA_LOCK_ROWS_FOR_UPDATE, true);
            ctx.statement(ctx.connection().prepareStatement(ctx.sql(), TYPE_SCROLL_SENSITIVE, CONCUR_UPDATABLE));
        }

        // Regular behaviour
        else {
            ctx.statement(ctx.connection().prepareStatement(ctx.sql()));
        }

        // [#1263] Allow for negative fetch sizes to support some non-standard
        // MySQL feature, where Integer.MIN_VALUE is used
        if (size != 0) {
            if (log.isDebugEnabled())
                log.debug("Setting fetch size", size);

            ctx.statement().setFetchSize(size);
        }

        // [#1854] Set the max number of rows for this result query
        if (maxRows != 0) {
            ctx.statement().setMaxRows(maxRows);
        }
    }

    
    protected final int execute(ExecuteContext ctx, ExecuteListener listener) throws SQLException {
        listener.executeStart(ctx);

        // JTDS doesn't seem to implement PreparedStatement.execute()
        // correctly, at least not for sp_help
        /* [pro] xx
        xx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xx xxxx x
            xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        x

        xx xxxxxxx xxxxx xxxxxxxxxxxxxx xx xxxxx xx xxxxxx xxxxxxx xxxx xxx
        xx xxx xxxxxx x xxxxxxxxxx xxxx xxxxxxxx xxxxxx xxxxxxxxxxxxxxxxxxxxxxx
        xxxx xx [/pro] */if (ctx.statement().execute()) {
            ctx.resultSet(ctx.statement().getResultSet());
        }

        listener.executeEnd(ctx);

        // Fetch a single result set
        if (!many) {
            if (ctx.resultSet() != null) {
                Field<?>[] fields = getFields(ctx.resultSet().getMetaData());
                cursor = new CursorImpl<R>(ctx, listener, fields, internIndexes(fields), keepStatement(), keepResultSet(), getRecordType());

                if (!lazy) {
                    result = cursor.fetch();
                    cursor = null;
                }
            }
            else {
                result = new ResultImpl<R>(ctx.configuration());
            }
        }

        // Fetch several result sets
        else {
            results = new ArrayList<Result<Record>>();
            boolean anyResults = false;

            while (ctx.resultSet() != null) {
                anyResults = true;

                Field<?>[] fields = new MetaDataFieldProvider(ctx.configuration(), ctx.resultSet().getMetaData()).getFields();
                Cursor<Record> c = new CursorImpl<Record>(ctx, listener, fields, internIndexes(fields), true, false);
                results.add(c.fetch());

                if (ctx.statement().getMoreResults()) {
                    ctx.resultSet(ctx.statement().getResultSet());
                }
                else {
                    ctx.resultSet(null);
                }
            }

            // Call this only when there was at least one ResultSet.
            // Otherwise, this call is not supported by ojdbc...
            if (anyResults) {
                ctx.statement().getMoreResults(Statement.CLOSE_ALL_RESULTS);
            }
        }

        return result != null ? result.size() : 0;
    }

    
    protected final boolean keepResultSet() {
        return lazy;
    }

    /**
     * Subclasses should indicate whether a Postgres refcursor is being selected
     */
    abstract boolean isSelectingRefCursor();

    /**
     * Subclasses should indicate whether they want an updatable {@link ResultSet}
     */
    abstract boolean isForUpdate();

    
    public final Result<R> fetch() {
        execute();
        return result;
    }

    
    public final ResultSet fetchResultSet() {
        return fetchLazy().resultSet();
    }

    
    public final Cursor<R> fetchLazy() {
        return fetchLazy(0);
    }

    
    public final Cursor<R> fetchLazy(int fetchSize) {
        lazy = true;
        size = fetchSize;

        try {
            execute();
        }
        finally {
            lazy = false;
            size = 0;
        }

        return cursor;
    }

    
    public final List<Result<Record>> fetchMany() {
        many = true;

        try {
            execute();
        }
        finally {
            many = false;
        }

        return results;
    }

    
    public final <T> List<T> fetch(Field<T> field) {
        return fetch().getValues(field);
    }

    
    public final <T> List<T> fetch(Field<?> field, Class<? extends T> type) {
        return fetch().getValues(field, type);
    }

    
    public final <T, U> List<U> fetch(Field<T> field, Converter<? super T, U> converter) {
        return fetch().getValues(field, converter);
    }

    
    public final List<?> fetch(int fieldIndex) {
        return fetch().getValues(fieldIndex);
    }

    
    public final <T> List<T> fetch(int fieldIndex, Class<? extends T> type) {
        return fetch().getValues(fieldIndex, type);
    }

    
    public final <U> List<U> fetch(int fieldIndex, Converter<?, U> converter) {
        return fetch().getValues(fieldIndex, converter);
    }

    
    public final List<?> fetch(String fieldName) {
        return fetch().getValues(fieldName);
    }

    
    public final <T> List<T> fetch(String fieldName, Class<? extends T> type) {
        return fetch().getValues(fieldName, type);
    }

    
    public final <U> List<U> fetch(String fieldName, Converter<?, U> converter) {
        return fetch().getValues(fieldName, converter);
    }

    
    public final <T> T fetchOne(Field<T> field) {
        R record = fetchOne();
        return record == null ? null : record.getValue(field);
    }

    
    public final <T> T fetchOne(Field<?> field, Class<? extends T> type) {
        return Convert.convert(fetchOne(field), type);
    }

    
    public final <T, U> U fetchOne(Field<T> field, Converter<? super T, U> converter) {
        return Convert.convert(fetchOne(field), converter);
    }

    
    public final Object fetchOne(int fieldIndex) {
        R record = fetchOne();
        return record == null ? null : record.getValue(fieldIndex);
    }

    
    public final <T> T fetchOne(int fieldIndex, Class<? extends T> type) {
        return Convert.convert(fetchOne(fieldIndex), type);
    }

    
    public final <U> U fetchOne(int fieldIndex, Converter<?, U> converter) {
        return Convert.convert(fetchOne(fieldIndex), converter);
    }

    
    public final Object fetchOne(String fieldName) {
        R record = fetchOne();
        return record == null ? null : record.getValue(fieldName);
    }

    
    public final <T> T fetchOne(String fieldName, Class<? extends T> type) {
        return Convert.convert(fetchOne(fieldName), type);
    }

    
    public final <U> U fetchOne(String fieldName, Converter<?, U> converter) {
        return Convert.convert(fetchOne(fieldName), converter);
    }

    
    public final R fetchOne() {
        return Utils.fetchOne(fetchLazy());
    }

    
    public final R fetchAny() {
        Cursor<R> c = fetchLazy();

        try {
            return c.fetchOne();
        }
        finally {
            c.close();
        }
    }

    
    public final <K> Map<K, R> fetchMap(Field<K> key) {
        return fetch().intoMap(key);
    }

    
    public final <K, V> Map<K, V> fetchMap(Field<K> key, Field<V> value) {
        return fetch().intoMap(key, value);
    }

    
    public final Map<Record, R> fetchMap(Field<?>[] keys) {
        return fetch().intoMap(keys);
    }

    
    public final <E> Map<List<?>, E> fetchMap(Field<?>[] keys, Class<? extends E> type) {
        return fetch().intoMap(keys, type);
    }

    
    public final <K, E> Map<K, E> fetchMap(Field<K> key, Class<? extends E> type) {
        return fetch().intoMap(key, type);
    }

    
    public final List<Map<String, Object>> fetchMaps() {
        return fetch().intoMaps();
    }

    
    public final Map<String, Object> fetchOneMap() {
        R record = fetchOne();
        return record == null ? null : record.intoMap();
    }

    
    public final <K> Map<K, Result<R>> fetchGroups(Field<K> key) {
        return fetch().intoGroups(key);
    }

    
    public final <K, V> Map<K, List<V>> fetchGroups(Field<K> key, Field<V> value) {
        return fetch().intoGroups(key, value);
    }

    
    public final Map<Record, Result<R>> fetchGroups(Field<?>[] keys) {
        return fetch().intoGroups(keys);
    }

    
    public final <E> Map<Record, List<E>> fetchGroups(Field<?>[] keys, Class<? extends E> type) {
        return fetch().intoGroups(keys, type);
    }

    
    public final Object[][] fetchArrays() {
        return fetch().intoArray();
    }

    
    public final Object[] fetchArray(int fieldIndex) {
        return fetch().intoArray(fieldIndex);
    }

    
    public final <T> T[] fetchArray(int fieldIndex, Class<? extends T> type) {
        return fetch().intoArray(fieldIndex, type);
    }

    
    public final <U> U[] fetchArray(int fieldIndex, Converter<?, U> converter) {
        return fetch().intoArray(fieldIndex, converter);
    }

    
    public final Object[] fetchArray(String fieldName) {
        return fetch().intoArray(fieldName);
    }

    
    public final <T> T[] fetchArray(String fieldName, Class<? extends T> type) {
        return fetch().intoArray(fieldName, type);
    }

    
    public final <U> U[] fetchArray(String fieldName, Converter<?, U> converter) {
        return fetch().intoArray(fieldName, converter);
    }

    
    public final <T> T[] fetchArray(Field<T> field) {
        return fetch().intoArray(field);
    }

    
    public final <T> T[] fetchArray(Field<?> field, Class<? extends T> type) {
        return fetch().intoArray(field, type);
    }

    
    public final <T, U> U[] fetchArray(Field<T> field, Converter<? super T, U> converter) {
        return fetch().intoArray(field, converter);
    }

    /**
     * Subclasses may override this method
     * <p>
     * {@inheritDoc}
     */
    
    public Class<? extends R> getRecordType() {
        return null;
    }

    
    public final Object[] fetchOneArray() {
        R record = fetchOne();
        return record == null ? null : record.intoArray();
    }

    
    public final <T> List<T> fetchInto(Class<? extends T> type) {
        return fetch().into(type);
    }

    
    public final <E> E fetchOneInto(Class<? extends E> type) {
        R record = fetchOne();
        return record == null ? null : record.into(type);
    }

    
    public final <Z extends Record> Result<Z> fetchInto(Table<Z> table) {
        return fetch().into(table);
    }

    
    public final <Z extends Record> Z fetchOneInto(Table<Z> table) {
        R record = fetchOne();
        return record == null ? null : record.into(table);
    }

    
    public final <H extends RecordHandler<? super R>> H fetchInto(H handler) {
        return fetch().into(handler);
    }

    
    public final <E> List<E> fetch(RecordMapper<? super R, E> mapper) {
        return fetch().map(mapper);
    }

    
    public final <K, E> Map<K, List<E>> fetchGroups(Field<K> key, Class<? extends E> type) {
        return fetch().intoGroups(key, type);
    }

    
    @Deprecated
    public final org.jooq.FutureResult<R> fetchLater() {
        ExecutorService executor = newSingleThreadExecutor();
        Future<Result<R>> future = executor.submit(new ResultQueryCallable());
        return new FutureResultImpl<R>(future, executor);
    }

    
    @Deprecated
    public final org.jooq.FutureResult<R> fetchLater(ExecutorService executor) {
        Future<Result<R>> future = executor.submit(new ResultQueryCallable());
        return new FutureResultImpl<R>(future);
    }

    
    public final Result<R> getResult() {
        return result;
    }

    /**
     * A wrapper for the {@link ResultQuery#fetch()} method
     */
    private final class ResultQueryCallable implements Callable<Result<R>> {

        
        public final Result<R> call() throws Exception {
            return fetch();
        }
    }
}
