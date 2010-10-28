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

import java.sql.Connection;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

import javax.sql.DataSource;

import org.jooq.Case;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.DeleteQuery;
import org.jooq.Field;
import org.jooq.InOperator;
import org.jooq.InsertQuery;
import org.jooq.InsertSelectQuery;
import org.jooq.JoinType;
import org.jooq.Operator;
import org.jooq.Record;
import org.jooq.ResultProviderQuery;
import org.jooq.SQLDialect;
import org.jooq.SQLDialectNotSupportedException;
import org.jooq.Select;
import org.jooq.SelectFromStep;
import org.jooq.SelectQuery;
import org.jooq.SimpleSelect;
import org.jooq.SimpleSelectQuery;
import org.jooq.Table;
import org.jooq.TableRecord;
import org.jooq.UpdateQuery;

/**
 * A factory providing implementations to the org.jooq interfaces
 *
 * @author Lukas Eder
 */
public final class Factory implements Configuration {

    private final transient Connection connection;
    private final transient DataSource dataSource;
    private final SQLDialect           dialect;

    /**
     * Create a factory with default settings and no connection / datasource
     * configured
     */
    Factory() {
        this(SQLDialect.SQL99);
    }

    /**
     * Create a factory with connection configured
     *
     * @param connection The connection to use with objects created from this
     *            factory
     */
    public Factory(Connection connection) {
        this(connection, SQLDialect.SQL99);
    }

    /**
     * Create a factory with data source configured
     *
     * @param dataSource The data source to use with objects created from this
     *            factory
     */
    public Factory(DataSource dataSource) {
        this(dataSource, SQLDialect.SQL99);
    }

    /**
     * Create a factory with no connection / datasource configured
     *
     * @param dialect The dialect to use with objects created from this factory
     */
    Factory(SQLDialect dialect) {
        this((Connection) null, dialect);
    }

    /**
     * Create a factory with connection and dialect configured
     *
     * @param connection The connection to use with objects created from this
     *            factory
     * @param dialect The dialect to use with objects created from this factory
     */
    public Factory(Connection connection, SQLDialect dialect) {
        this.connection = connection;
        this.dataSource = null;
        this.dialect = dialect;
    }

    /**
     * Create a factory with data source and dialect configured
     *
     * @param dataSource The data source to use with objects created from this
     *            factory
     * @param dialect The dialect to use with objects created from this factory
     */
    public Factory(DataSource dataSource, SQLDialect dialect) {
        this.connection = null;
        this.dataSource = dataSource;
        this.dialect = dialect;
    }

    /**
     * Retrieve the function factory with this factory's configuration
     *
     * @deprecated - Do not use {@link FunctionFactory} anymore
     */
    @Deprecated
    public FunctionFactory functions() {
        return new FunctionFactory(getDialect());
    }

    /**
     * Retrieve the persistence manager with this factory's configuration
     */
    public Manager manager() {
        return new Manager(this);
    }

    /**
     * Retrieve the configured dialect
     */
    @Override
    public SQLDialect getDialect() {
        return dialect;
    }

    /**
     * Retrieve the configured connection
     */
    @Override
    public Connection getConnection() {
        return connection;
    }

    /**
     * Retrieve the configured data source
     */
    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * A PlainSQLField is a field that can contain user-defined plain SQL,
     * because sometimes it is easier to express things directly in SQL, for
     * instance complex proprietary functions. There must not be any binding
     * variables contained in the SQL
     * <p>
     * Example:
     * <p>
     * <code><pre>
     * String sql = "DECODE(MY_FIELD, 1, 100, 200)";
     * </pre></code>
     *
     * @param sql The SQL
     * @return A field wrapping the plain SQL
     */
    public Field<?> plainSQLField(String sql) {
        return plainSQLField(sql, new Object[0]);
    }

    /**
     * A PlainSQLField is a field that can contain user-defined plain SQL,
     * because sometimes it is easier to express things directly in SQL, for
     * instance complex proprietary functions. There must be as many binding
     * variables contained in the SQL, as passed in the bindings parameter
     * <p>
     * Example:
     * <p>
     * <code><pre>
     * String sql = "DECODE(MY_FIELD, ?, ?, ?)";
     * Object[] bindings = new Object[] { 1, 100, 200 };</pre></code>
     *
     * @param sql The SQL
     * @return A field wrapping the plain SQL
     */
    public Field<?> plainSQLField(String sql, Object... bindings) {
        return new PlainSQLField(dialect, sql, bindings);
    }

    /**
     * Create a new condition holding plain SQL. There must not be any binding
     * variables contained in the SQL
     * <p>
     * Example:
     * <p>
     * <code><pre>
     * String sql = "(X = 1 and Y = 2)";</pre></code>
     *
     * @param sql The SQL
     * @return A condition wrapping the plain SQL
     */
    public Condition plainSQLCondition(String sql) {
        return plainSQLCondition(sql, new Object[0]);
    }

    /**
     * Create a new condition holding plain SQL. There must be as many binding
     * variables contained in the SQL, as passed in the bindings parameter
     * <p>
     * Example:
     * <p>
     * <code><pre>
     * String sql = "(X = ? and Y = ?)";
     * Object[] bindings = new Object[] { 1, 2 };</pre></code>
     *
     * @param sql The SQL
     * @param bindings The bindings
     * @return A condition wrapping the plain SQL
     */
    public Condition plainSQLCondition(String sql, Object... bindings) {
        return new PlainSQLCondition(dialect, sql, bindings);
    }

    /**
     * Combine a list of conditions with the {@link Operator#AND} operator
     */
    public Condition combinedCondition(Condition... conditions) {
        return combinedCondition(Operator.AND, conditions);
    }

    /**
     * Combine a collection of conditions with the {@link Operator#AND} operator
     */
    public Condition combinedCondition(Collection<Condition> conditions) {
        return combinedCondition(Operator.AND, conditions);
    }

    /**
     * Combine a list of conditions with any operator
     */
    public Condition combinedCondition(Operator operator, Condition... conditions) {
        return combinedCondition(operator, Arrays.asList(conditions));
    }

    /**
     * Combine a collection of conditions with any operator
     */
    public Condition combinedCondition(Operator operator, Collection<Condition> conditions) {
        return new CombinedCondition(dialect, operator, conditions);
    }

    /**
     * Create a {@link BetweenCondition} for a field and two values
     *
     * @param <T> The generic type parameter
     * @param field The field to compare to the values
     * @param minValue The lower bound
     * @param maxValue The upper bound
     * @return A {@link BetweenCondition}
     */
    public <T> Condition betweenCondition(Field<T> field, T minValue, T maxValue) {
        return new BetweenCondition<T>(dialect, field, minValue, maxValue);
    }

    /**
     * Create an {@link InCondition} for a list of values
     *
     * @param <T> The generic type parameter
     * @param field The field to compare to the values
     * @param values The accepted values
     * @return An {@link InCondition}
     */
    public <T> Condition inCondition(Field<T> field, T... values) {
        return inCondition(field, Arrays.asList(values));
    }

    /**
     * Create an {@link InCondition} for a collection of values to be excluded
     *
     * @param <T> The generic type parameter
     * @param field The field to compare to the values
     * @param values The excluded values
     * @return An {@link InCondition}
     */
    public <T> Condition notInCondition(Field<T> field, Collection<T> values) {
        return new InCondition<T>(dialect, field, new LinkedHashSet<T>(values), InOperator.NOT_IN);
    }

    /**
     * Create an {@link InCondition} for a list of values to be excluded
     *
     * @param <T> The generic type parameter
     * @param field The field to compare to the values
     * @param values The excluded values
     * @return An {@link InCondition}
     */
    public <T> Condition notInCondition(Field<T> field, T... values) {
        return notInCondition(field, Arrays.asList(values));
    }

    /**
     * Create an {@link InCondition} for a collection of values
     *
     * @param <T> The generic type parameter
     * @param field The field to compare to the values
     * @param values The accepted values
     * @return An {@link InCondition}
     */
    public <T> Condition inCondition(Field<T> field, Collection<T> values) {
        return new InCondition<T>(dialect, field, new LinkedHashSet<T>(values));
    }

    /**
     * Create a condition comparing a field with a value using the
     * {@link Comparator#EQUALS} comparator
     *
     * @param <T> The generic type parameter
     * @param field The field to compare to the value
     * @param value The accepted value
     * @return A {@link CompareCondition}
     */
    public <T> Condition compareCondition(Field<T> field, T value) {
        return compareCondition(field, value, Comparator.EQUALS);
    }

    /**
     * Create a condition comparing a field with a value using any comparator
     *
     * @param <T> The generic type parameter
     * @param field The field to compare to the value
     * @param value The accepted value
     * @param comparator The comparator
     * @return A {@link CompareCondition}
     */
    public <T> Condition compareCondition(Field<T> field, T value, Comparator comparator) {
        return new CompareCondition<T>(dialect, field, value, comparator);
    }

    /**
     * Create a condition comparing a field with <code>null</code>
     *
     * @param <T> The generic type parameter
     * @param field The field to compare to null
     * @return A {@link CompareCondition}
     */
    public <T> Condition nullCondition(Field<T> field) {
        return compareCondition(field, null, Comparator.EQUALS);
    }

    /**
     * Create a condition comparing a field with <code>null</code>
     *
     * @param <T> The generic type parameter
     * @param field The field to compare to null
     * @return A {@link CompareCondition}
     */
    public <T> Condition notNullCondition(Field<T> field) {
        return compareCondition(field, null, Comparator.NOT_EQUALS);
    }

    /**
     * Create a condition comparing two fields (typically used for joins)
     *
     * @param <T> The generic type parameter
     * @param field1 The first field
     * @param field2 The second field
     * @return A {@link JoinCondition}
     */
    public <T> Condition joinCondition(Field<T> field1, Field<T> field2) {
        return joinCondition(field1, field2, Comparator.EQUALS);
    }

    /**
     * Create a condition comparing two fields (typically used for joins)
     *
     * @param <T> The generic type parameter
     * @param field1 The first field
     * @param field2 The second field
     * @param comparator The comparator to compare the two fields with
     * @return A {@link JoinCondition}
     */
    public <T> Condition joinCondition(Field<T> field1, Field<T> field2, Comparator comparator) {
        return new JoinCondition<T>(dialect, field1, field2);
    }

    /**
     * Create a new {@link InsertQuery}
     *
     * @param into The table to insert data into
     * @return The new {@link InsertQuery}
     */
    public <R extends TableRecord<R>> InsertQuery<R> insertQuery(Table<R> into) {
        return new InsertQueryImpl<R>(this, into);
    }

    /**
     * Create a new {@link InsertSelectQuery}
     *
     * @param into The table to insert data into
     * @param select The select statement to select data from
     * @return The new {@link InsertSelectQuery}
     */
    public InsertSelectQuery insertQuery(Table<?> into, ResultProviderQuery<?> select) {
        return new InsertSelectQueryImpl(this, into, select);
    }

    /**
     * Create a new {@link UpdateQuery}
     *
     * @param table The table to update data into
     * @return The new {@link UpdateQuery}
     */
    public <R extends TableRecord<R>> UpdateQuery<R> updateQuery(Table<R> table) {
        return new UpdateQueryImpl<R>(this, table);
    }

    /**
     * Create a new {@link DeleteQuery}
     *
     * @param table The table to delete data from
     * @return The new {@link DeleteQuery}
     */
    public <R extends TableRecord<R>> DeleteQuery<R> deleteQuery(Table<R> table) {
        return new DeleteQueryImpl<R>(this, table);
    }

    /**
     * Create a new {@link Select}
     */
    public Select select() {
        return new SelectImpl(this);
    }

    /**
     * Create a new {@link Select}
     */
    public <R extends Record> SimpleSelect<R> select(Table<R> table) {
        return new SimpleSelectImpl<R>(this, table);
    }

    /**
     * Create a new {@link Select}
     */
    public SelectFromStep select(Field<?>... fields) {
        return new SelectImpl(this).select(fields);
    }

    /**
     * Create a new {@link Select}
     */
    public SelectFromStep select(Collection<Field<?>> fields) {
        return new SelectImpl(this).select(fields);
    }

    /**
     * Create a new {@link SelectQuery}
     */
    public SelectQuery selectQuery() {
        return new SelectQueryImpl(this);
    }

    /**
     * Create a new {@link SelectQuery}
     *
     * @param table The table to select data from
     * @return The new {@link SelectQuery}
     */
    public <R extends Record> SimpleSelectQuery<R> selectQuery(Table<R> table) {
        return new SimpleSelectQueryImpl<R>(this, table);
    }

    /**
     * Create a new {@link Join} part using a {@link JoinCondition}
     *
     * @param <T> The generic type parameter
     * @param table The table to join
     * @param field1 The first field of the join condition
     * @param field2 The second field of the join condition
     * @return A new {@link Join} part
     */
    public <T> Join join(Table<?> table, Field<T> field1, Field<T> field2) {
        return join(table, JoinType.JOIN, field1, field2);
    }

    /**
     * Create a new {@link Join} part using any number of conditions
     *
     * @param table The table to join
     * @param conditions Any number of conditions
     * @return A new {@link Join} part
     */
    public Join join(Table<?> table, Condition... conditions) {
        return join(table, JoinType.JOIN, conditions);
    }

    /**
     * Create a new {@link Join} part using a {@link JoinCondition}
     *
     * @param <T> The generic type parameter
     * @param table The table to join
     * @param type The join type
     * @param field1 The first field of the join condition
     * @param field2 The second field of the join condition
     * @return A new {@link Join} part
     */
    public <T> Join join(Table<?> table, JoinType type, Field<T> field1, Field<T> field2) {
        return join(table, type, joinCondition(field1, field2));
    }

    /**
     * Create a new {@link Join} part using any number of conditions
     *
     * @param table The table to join
     * @param type The join type
     * @param conditions Any number of conditions
     * @return A new {@link Join} part
     */
    public Join join(Table<?> table, JoinType type, Condition... conditions) {
        return new Join(dialect, table, type, conditions);
    }

    /**
     * Create a new {@link Record} that can be inserted into the corresponding
     * table.
     *
     * @param <R> The generic record type
     * @param table The table holding records of type <R>
     * @return The new record
     */
    public <R extends Record> R newRecord(Table<R> table) {
        return JooqUtil.newRecord(table.getRecordType(), table, this);
    }

    /**
     * Initialse a {@link Case} statement. Decode is used as a method name to
     * avoid name clashes with Java's reserved literal "case"
     *
     * @see Case
     */
    public Case decode() {
        return new CaseImpl(getDialect());
    }

    /**
     * Get a constant value
     */
    public <T> Field<T> constant(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Argument 'value' must not be null");
        }

        return new Constant<T>(getDialect(), value);
    }

    /**
     * Get the null field
     */
    public Field<?> NULL() {
        return new PseudoField<Object>(getDialect(), "null", Object.class);
    }

    /**
     * Retrieve the rownum pseudo-field
     */
    public Field<Integer> rownum() {
        return new PseudoField<Integer>(getDialect(), "rownum", Integer.class);
    }

    /**
     * Get the count(*) function
     *
     * @see Field#count()
     * @see Field#countDistinct()
     */
    public Field<Integer> count() {
        return new Count(getDialect());
    }

    /**
     * Get the current_date() function
     * <p>
     * This translates into any dialect
     */
    public Field<Date> currentDate() throws SQLDialectNotSupportedException {
        switch (getDialect()) {
            case ORACLE:
                return new Function<Date>(getDialect(), "sysdate", Date.class);
        }

        return new Function<Date>(getDialect(), "current_date", Date.class);
    }

    /**
     * Get the current_time() function
     * <p>
     * This translates into any dialect
     */
    public Field<Time> currentTime() throws SQLDialectNotSupportedException {
        switch (getDialect()) {
            case ORACLE:
                return new Function<Time>(getDialect(), "sysdate", Time.class);
        }

        return new Function<Time>(getDialect(), "current_time", Time.class);
    }

    /**
     * Get the current_timestamp() function
     * <p>
     * This translates into any dialect
     */
    public Field<Timestamp> currentTimestamp() {
        switch (getDialect()) {
            case ORACLE:
                return new Function<Timestamp>(getDialect(), "sysdate", Timestamp.class);
        }

        return new Function<Timestamp>(getDialect(), "current_timestamp", Timestamp.class);
    }

    /**
     * Get the current_user() function
     * <p>
     * This translates into any dialect
     */
    public Field<String> currentUser() {
        switch (getDialect()) {
            case ORACLE:
                return new StringFunction(getDialect(), "user");
        }

        return new StringFunction(getDialect(), "current_user");
    }
}
