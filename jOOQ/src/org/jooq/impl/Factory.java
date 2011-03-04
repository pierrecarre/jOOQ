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

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import org.jooq.ArrayRecord;
import org.jooq.Case;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.DataType;
import org.jooq.DeleteQuery;
import org.jooq.Field;
import org.jooq.InsertQuery;
import org.jooq.InsertSelectQuery;
import org.jooq.JoinType;
import org.jooq.Operator;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.SQLDialectNotSupportedException;
import org.jooq.SchemaMapping;
import org.jooq.Select;
import org.jooq.SelectFromStep;
import org.jooq.SelectQuery;
import org.jooq.SimpleSelectQuery;
import org.jooq.SimpleSelectWhereStep;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.TableRecord;
import org.jooq.UDTRecord;
import org.jooq.UpdateQuery;

/**
 * A factory providing implementations to the org.jooq interfaces
 * <p>
 * This factory is the main entry point for client code, to access jOOQ classes
 * and functionality. Here, you can instanciate all of those objects that cannot
 * be accessed through other objects. For example, to create a {@link Field}
 * representing a constant value, you can write:
 * <p>
 * <code><pre>
 * Field&lt;String&gt; field = new Factory().constant("Hello World")
 * </pre></code>
 * <p>
 * Also, some SQL clauses cannot be expressed easily with DSL, for instance the
 * EXISTS clause, as it is not applied on a concrete object (yet). Hence you
 * should write
 * <p>
 * <code><pre>
 * Condition condition = new Factory().exists(new Factory().select(...));
 * </pre></code>
 *
 * @author Lukas Eder
 */
public final class Factory implements Configuration {

    private static final Factory[]     DEFAULT_INSTANCES = new Factory[SQLDialect.values().length];

    private final transient Connection connection;
    private final SQLDialect           dialect;
    private final SchemaMapping        mapping;

    /**
     * Create a factory with default settings and no connection configured
     * @deprecated - Do not reuse
     */
    @Deprecated
    Factory() {
        this(SQLDialect.SQL99);
    }

    /**
     * Create a factory with connection configured
     *
     * @param connection The connection to use with objects created from this
     *            factory
     * @deprecated - Do not reuse
     */
    @Deprecated
    public Factory(Connection connection) {
        this(connection, SQLDialect.SQL99);
    }

    /**
     * Create a factory with no connection configured
     *
     * @param dialect The dialect to use with objects created from this factory
     * @deprecated 1.5.2 use {@link #getFactory(SQLDialect)} instead
     */
    @Deprecated
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
        this(connection, dialect, new SchemaMapping());
    }

    /**
     * Create a factory with connection, a dialect and a schema mapping
     * configured
     * <p>
     * THIS FUNCTIONALITY IS EXPERIMENTAL. USE AT OWN RISK
     *
     * @param connection The connection to use with objects created from this
     *            factory
     * @param dialect The dialect to use with objects created from this factory
     * @param mapping The schema mapping to use with objects created from this
     *            factory
     */
    public Factory(Connection connection, SQLDialect dialect, SchemaMapping mapping) {
        this.connection = connection;
        this.dialect = dialect;
        this.mapping = mapping;
    }

    /**
     * Create a factory from a Configuration
     *
     * @param configuration The configuration used to create a new factory
     * @deprecated 1.5.2 use any other constructor instead
     */
    @Deprecated
    public Factory(Configuration configuration) {
        this(configuration.getConnection(), configuration.getDialect(), new SchemaMapping());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SQLDialect getDialect() {
        return dialect;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection() {
        return connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchemaMapping getSchemaMapping() {
        return mapping;
    }

    /**
     * A PlainSQLTable is a table that can contain user-defined plain SQL,
     * because sometimes it is easier to express things directly in SQL, for
     * instance complex, but static subqueries or tables from different schemas.
     * <p>
     * Example
     * <p>
     * <code><pre>
     * String sql = "SELECT * FROM USER_TABLES WHERE OWNER = 'MY_SCHEMA'";
     * </pre></code>
     * <p>
     * The provided SQL must evaluate as a table whose type can be dynamically
     * discovered using JDBC's {@link ResultSetMetaData} methods. That way, you
     * can be sure that calling methods, such as {@link Table#getFields()} will
     * list the actual fields returned from your result set.
     *
     * @param sql The SQL
     * @return A table wrapping the plain SQL
     */
    public Table<Record> plainSQLTable(String sql) {
        return plainSQLTable(sql, new Object[0]);
    }

    /**
     * A PlainSQLTable is a table that can contain user-defined plain SQL,
     * because sometimes it is easier to express things directly in SQL, for
     * instance complex, but static subqueries or tables from different schemas.
     * There must be as many binding variables contained in the SQL, as passed
     * in the bindings parameter
     * <p>
     * Example
     * <p>
     * <code><pre>
     * String sql = "SELECT * FROM USER_TABLES WHERE OWNER = ?";
     * Object[] bindings = new Object[] { "MY_SCHEMA" };
     * </pre></code>
     * <p>
     * The provided SQL must evaluate as a table whose type can be dynamically
     * discovered using JDBC's {@link ResultSetMetaData} methods. That way, you
     * can be sure that calling methods, such as {@link Table#getFields()} will
     * list the actual fields returned from your result set.
     *
     * @param sql The SQL
     * @return A table wrapping the plain SQL
     */
    public Table<Record> plainSQLTable(String sql, Object... bindings) {
        return new PlainSQLTable(this, sql, bindings);
    }

    /**
     * A PlainSQLField is a field that can contain user-defined plain SQL,
     * because sometimes it is easier to express things directly in SQL, for
     * instance complex proprietary functions. There must not be any binding
     * variables contained in the SQL.
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
     * @param bindings The bindings for the field
     * @return A field wrapping the plain SQL
     */
    public Field<?> plainSQLField(String sql, Object... bindings) {
        return plainSQLField(sql, Object.class, bindings);
    }

    /**
     * A PlainSQLField is a field that can contain user-defined plain SQL,
     * because sometimes it is easier to express things directly in SQL, for
     * instance complex proprietary functions. There must not be any binding
     * variables contained in the SQL.
     * <p>
     * Example:
     * <p>
     * <code><pre>
     * String sql = "DECODE(MY_FIELD, 1, 100, 200)";
     * </pre></code>
     *
     * @param sql The SQL
     * @param type The field type
     * @return A field wrapping the plain SQL
     */
    public <T> Field<T> plainSQLField(String sql, Class<T> type) {
        return plainSQLField(sql, type, new Object[0]);
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
     * @param type The field type
     * @param bindings The bindings for the field
     * @return A field wrapping the plain SQL
     */
    public <T> Field<T> plainSQLField(String sql, Class<T> type, Object... bindings) {
        return new PlainSQLField<T>(this, sql, type, bindings);
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
        return new PlainSQLCondition(this, sql, bindings);
    }

    /**
     * Combine a list of conditions with the {@link Operator#AND} operator
     *
     * @deprecated - Use {@link Condition#and(Condition)} instead
     */
    @Deprecated
    public Condition combinedCondition(Condition... conditions) {
        return combinedCondition(Operator.AND, conditions);
    }

    /**
     * Combine a collection of conditions with the {@link Operator#AND} operator
     *
     * @deprecated - Use {@link Condition#and(Condition)} instead
     */
    @Deprecated
    public Condition combinedCondition(Collection<Condition> conditions) {
        return combinedCondition(Operator.AND, conditions);
    }

    /**
     * Combine a list of conditions with any operator
     *
     * @deprecated - Use {@link Condition#and(Condition)} instead
     */
    @Deprecated
    public Condition combinedCondition(Operator operator, Condition... conditions) {
        return combinedCondition(operator, Arrays.asList(conditions));
    }

    /**
     * Combine a collection of conditions with any operator
     *
     * @deprecated - Use {@link Condition#and(Condition)} or
     *             {@link Condition#or(Condition)} instead
     */
    @Deprecated
    public Condition combinedCondition(Operator operator, Collection<Condition> conditions) {
        return new CombinedCondition(this, operator, conditions);
    }

    /**
     * Invert a condition
     *
     * @deprecated - Use {@link Condition#not()} instead
     */
    @Deprecated
    public Condition notCondition(Condition condition) {
        return new NotCondition(this, condition);
    }

    /**
     * Create a not exists condition.
     * <p>
     * <code>EXISTS ([query])</code>
     */
    public Condition exists(Select<?> query) {
        return new SelectQueryAsExistsCondition(this, query, ExistsOperator.EXISTS);
    }

    /**
     * Create a not exists condition.
     * <p>
     * <code>NOT EXISTS ([query])</code>
     */
    public Condition notExists(Select<?> query) {
        return new SelectQueryAsExistsCondition(this, query, ExistsOperator.NOT_EXISTS);
    }

    /**
     * Create a {@link BetweenCondition} for a field and two values
     *
     * @param <T> The generic type parameter
     * @param field The field to compare to the values
     * @param minValue The lower bound
     * @param maxValue The upper bound
     * @return A {@link BetweenCondition}
     *
     * @deprecated - Use {@link Field#between(Object, Object)} instead
     */
    @Deprecated
    public <T> Condition betweenCondition(Field<T> field, T minValue, T maxValue) {
        return new BetweenCondition<T>(this, field, minValue, maxValue);
    }

    /**
     * Create an {@link InCondition} for a list of values
     *
     * @param <T> The generic type parameter
     * @param field The field to compare to the values
     * @param values The accepted values
     * @return An {@link InCondition}
     * @deprecated - Use {@link Field#in(Object...)} instead
     */
    @Deprecated
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
     * @deprecated - Use {@link Field#notIn(Collection)} instead
     */
    @Deprecated
    public <T> Condition notInCondition(Field<T> field, Collection<T> values) {
        return new InCondition<T>(this, field, new LinkedHashSet<T>(values), InOperator.NOT_IN);
    }

    /**
     * Create an {@link InCondition} for a list of values to be excluded
     *
     * @param <T> The generic type parameter
     * @param field The field to compare to the values
     * @param values The excluded values
     * @return An {@link InCondition}
     * @deprecated - Use {@link Field#notIn(Object...)} instead
     */
    @Deprecated
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
     * @deprecated - Use {@link Field#in(Collection)} instead
     */
    @Deprecated
    public <T> Condition inCondition(Field<T> field, Collection<T> values) {
        return new InCondition<T>(this, field, new LinkedHashSet<T>(values));
    }

    /**
     * Create a condition comparing a field with a value using the
     * {@link Comparator#EQUALS} comparator
     *
     * @param <T> The generic type parameter
     * @param field The field to compare to the value
     * @param value The accepted value
     * @return A {@link CompareCondition}
     * @deprecated - Use {@link Field#equal(Object)} instead
     */
    @Deprecated
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
     * @deprecated - Use {@link Field#equal(Object)} or any similar one instead
     */
    @Deprecated
    public <T> Condition compareCondition(Field<T> field, T value, Comparator comparator) {
        return new CompareCondition<T>(this, field, value, comparator);
    }

    /**
     * Create a condition comparing a field with <code>null</code>
     *
     * @param <T> The generic type parameter
     * @param field The field to compare to null
     * @return A {@link CompareCondition}
     * @deprecated - Use {@link Field#isNull()} instead
     */
    @Deprecated
    public <T> Condition nullCondition(Field<T> field) {
        return compareCondition(field, null, Comparator.EQUALS);
    }

    /**
     * Create a condition comparing a field with <code>null</code>
     *
     * @param <T> The generic type parameter
     * @param field The field to compare to null
     * @return A {@link CompareCondition}
     * @deprecated - Use {@link Field#isNotNull()} instead
     */
    @Deprecated
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
     * @deprecated - Use {@link Field#equal(Field)} instead
     */
    @Deprecated
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
     * @deprecated - Use {@link Field#equal(Field)} instead, or any similar
     *             method.
     */
    @Deprecated
    public <T> Condition joinCondition(Field<T> field1, Field<T> field2, Comparator comparator) {
        return new JoinCondition<T>(this, field1, field2, comparator);
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
    public InsertSelectQuery insertQuery(Table<?> into, Select<?> select) {
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
     * Create a new DSL select statement
     * <p>
     * Example: <code><pre>
     * SELECT * FROM [table] WHERE [conditions] ORDER BY [ordering] LIMIT [limit clause]
     * </pre></code>
     */
    public <R extends Record> SimpleSelectWhereStep<R> selectFrom(Table<R> table) {
        return new SimpleSelectImpl<R>(this, table);
    }

    /**
     * Create a new DSL select statement.
     * <p>
     * Example: <code><pre>
     * Factory create = new Factory();
     *
     * create.select(field1, field2)
     *      .from(table1)
     *      .join(table2).on(field1.equal(field2))
     *      .where(field1.greaterThan(100))
     *      .orderBy(field2);
     * query.execute(connection);
     * </pre></code>
     */
    public SelectFromStep select(Field<?>... fields) {
        return new SelectImpl(this).select(fields);
    }

    /**
     * Create a new DSL select statement for constant values
     * <p>
     * Example: <code><pre>
     * Factory create = new Factory();
     *
     * create.select(value1, value2)
     *      .from(table1)
     *      .join(table2).on(field1.equal(field2))
     *      .where(field1.greaterThan(100))
     *      .orderBy(field2);
     * query.execute(connection);
     * </pre></code>
     */
    public SelectFromStep select(Object... values) {
        return new SelectImpl(this).select(constant(values));
    }

    /**
     * Create a new DSL select statement.
     * <p>
     * Example: <code><pre>
     * Factory create = new Factory();
     *
     * create.selectDistinct(field1, field2)
     *      .from(table1)
     *      .join(table2).on(field1.equal(field2))
     *      .where(field1.greaterThan(100))
     *      .orderBy(field2);
     * </pre></code>
     */
    public SelectFromStep selectDistinct(Field<?>... fields) {
        return new SelectImpl(this, true).select(fields);
    }

    /**
     * Create a new DSL select statement.
     * <p>
     * Example: <code><pre>
     * Factory create = new Factory();
     *
     * create.select(fields)
     *      .from(table1)
     *      .join(table2).on(field1.equal(field2))
     *      .where(field1.greaterThan(100))
     *      .orderBy(field2);
     * </pre></code>
     */
    public SelectFromStep select(Collection<Field<?>> fields) {
        return new SelectImpl(this).select(fields);
    }

    /**
     * Create a new DSL select statement.
     * <p>
     * Example: <code><pre>
     * Factory create = new Factory();
     *
     * create.selectDistinct(fields)
     *      .from(table1)
     *      .join(table2).on(field1.equal(field2))
     *      .where(field1.greaterThan(100))
     *      .orderBy(field2);
     * </pre></code>
     */
    public SelectFromStep selectDistinct(Collection<Field<?>> fields) {
        return new SelectImpl(this, true).select(fields);
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
    public <R extends Record> SimpleSelectQuery<R> selectQuery(TableLike<R> table) {
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
     * @deprecated - This method is used internally, only. Do not reference.
     */
    @Deprecated
    public <T> Join join(TableLike<?> table, Field<T> field1, Field<T> field2) {
        return join(table, JoinType.JOIN, field1, field2);
    }

    /**
     * Create a new {@link Join} part using any number of conditions
     *
     * @param table The table to join
     * @param conditions Any number of conditions
     * @return A new {@link Join} part
     * @deprecated - This method is used internally, only. Do not reference.
     */
    @Deprecated
    public Join join(TableLike<?> table, Condition... conditions) {
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
     * @deprecated - This method is used internally, only. Do not reference.
     */
    @Deprecated
    public <T> Join join(TableLike<?> table, JoinType type, Field<T> field1, Field<T> field2) {
        return join(table, type, joinCondition(field1, field2));
    }

    /**
     * Create a new {@link Join} part using any number of conditions
     *
     * @param table The table to join
     * @param type The join type
     * @param conditions Any number of conditions
     * @return A new {@link Join} part
     * @deprecated - This method is used internally, only. Do not reference.
     */
    @Deprecated
    public Join join(TableLike<?> table, JoinType type, Condition... conditions) {
        return new Join(this, table, type, conditions);
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
     * Initialise a {@link Case} statement. Decode is used as a method name to
     * avoid name clashes with Java's reserved literal "case"
     *
     * @see Case
     */
    public Case decode() {
        return new CaseImpl(this);
    }

    /**
     * Cast a value to the type of another field.
     *
     * @param <T> The generic type of the cast field
     * @param value The value to cast
     * @param as The field whose type is used for the cast
     * @return The cast field
     */
    public <T> Field<T> cast(Object value, Field<T> as) {
        return constant(value).cast(as);
    }

    /**
     * Cast null to the type of another field.
     *
     * @param <T> The generic type of the cast field
     * @param as The field whose type is used for the cast
     * @return The cast field
     */
    public <T> Field<T> castNull(Field<T> as) {
        return NULL().cast(as);
    }

    /**
     * Cast a value to another type
     *
     * @param <T> The generic type of the cast field
     * @param value The value to cast
     * @param type The type that is used for the cast
     * @return The cast field
     */
    public <T> Field<T> cast(Object value, Class<? extends T> type) {
        return constant(value).cast(type);
    }

    /**
     * Cast null to a type
     *
     * @param <T> The generic type of the cast field
     * @param type The type that is used for the cast
     * @return The cast field
     */
    public <T> Field<T> castNull(DataType<T> type) {
        return NULL().cast(type);
    }

    /**
     * Cast a value to another type
     *
     * @param <T> The generic type of the cast field
     * @param value The value to cast
     * @param type The type that is used for the cast
     * @return The cast field
     */
    public <T> Field<T> cast(Object value, DataType<T> type) {
        return constant(value).cast(type);
    }

    /**
     * Cast null to a type
     *
     * @param <T> The generic type of the cast field
     * @param type The type that is used for the cast
     * @return The cast field
     */
    public <T> Field<T> castNull(Class<? extends T> type) {
        return NULL().cast(type);
    }

    /**
     * Get a constant value
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> Field<T> constant(T value) {
        if (value == null) {
            return (Field<T>) NULL();
        }
        else if (value instanceof Field<?>) {
            return (Field<T>) value;
        }
        else if (value instanceof UDTRecord) {
            return new UDTConstant(this, (UDTRecord) value);
        }
        else if (value instanceof ArrayRecord) {
            return new ArrayConstant(this, (ArrayRecord) value);
        }
        else {
            return new Constant<T>(this, value);
        }
    }

    /**
     * Get a list of constant values and fields
     */
    public List<Field<?>> constant(Object... values) {
        if (values == null) {
            throw new IllegalArgumentException("Cannot create a list of constants for null");
        }
        else {
            FieldList result = new FieldList(this);

            for (Object value : values) {

                // Fields can be mixed with constant values
                if (value instanceof Field<?>) {
                    result.add((Field<?>) value);
                }
                else {
                    result.add(constant(value));
                }
            }

            return result;
        }
    }

    /**
     * Get the null field
     */
    public Field<?> NULL() {
        return new PseudoField<Object>(this, "null",
            FieldTypeHelper.getDataType(getDialect(), Object.class));
    }

    /**
     * Retrieve the rownum pseudo-field
     */
    public Field<Integer> rownum() {
        return new PseudoField<Integer>(this, "rownum",
            FieldTypeHelper.getDataType(getDialect(), Integer.class));
    }

    /**
     * Get the count(*) function
     *
     * @see Field#count()
     * @see Field#countDistinct()
     */
    public Field<Integer> count() {
        return new Count(this);
    }

    /**
     * Get the current_date() function
     * <p>
     * This translates into any dialect
     */
    public Field<Date> currentDate() throws SQLDialectNotSupportedException {
        switch (getDialect()) {
            case ORACLE:
                return new Function<Date>(this, "sysdate",
                    FieldTypeHelper.getDataType(getDialect(), Date.class));

            case HSQLDB: // No break
            case POSTGRES:
                return new PseudoField<Date>(this, "current_date",
                    FieldTypeHelper.getDataType(getDialect(), Date.class));
        }

        return new Function<Date>(this, "current_date",
            FieldTypeHelper.getDataType(getDialect(), Date.class));
    }

    /**
     * Get the current_time() function
     * <p>
     * This translates into any dialect
     */
    public Field<Time> currentTime() throws SQLDialectNotSupportedException {
        switch (getDialect()) {
            case ORACLE:
                return new Function<Time>(this, "sysdate",
                    FieldTypeHelper.getDataType(getDialect(), Time.class));

            case HSQLDB: // No break
            case POSTGRES:
                return new PseudoField<Time>(this, "current_time",
                    FieldTypeHelper.getDataType(getDialect(), Time.class));
        }

        return new Function<Time>(this, "current_time",
            FieldTypeHelper.getDataType(getDialect(), Time.class));
    }

    /**
     * Get the current_timestamp() function
     * <p>
     * This translates into any dialect
     */
    public Field<Timestamp> currentTimestamp() {
        switch (getDialect()) {
            case ORACLE:
                return new Function<Timestamp>(this, "sysdate",
                    FieldTypeHelper.getDataType(getDialect(), Timestamp.class));

            case HSQLDB: // No break
            case POSTGRES:
                return new PseudoField<Timestamp>(this, "current_timestamp",
                    FieldTypeHelper.getDataType(getDialect(), Timestamp.class));
        }

        return new Function<Timestamp>(this, "current_timestamp",
            FieldTypeHelper.getDataType(getDialect(), Timestamp.class));
    }

    /**
     * Get the current_user() function
     * <p>
     * This translates into any dialect
     */
    public Field<String> currentUser() {
        switch (getDialect()) {
            case ORACLE:
                return new StringFunction(this, "user");

            case HSQLDB: // No break
            case POSTGRES:
                return new PseudoField<String>(this, "current_user",
                    FieldTypeHelper.getDataType(getDialect(), String.class));
        }

        return new StringFunction(this, "current_user");
    }

    // ------------------------------------------------------------------------
    // Former Manager methods
    // ------------------------------------------------------------------------

    /**
     * Execute and return all records for
     * <code><pre>SELECT * FROM [table]</pre></code>
     */
    public <R extends Record> List<R> fetch(Table<R> table) throws SQLException {
        return fetch(table, TrueCondition.TRUE_CONDITION);
    }

    /**
     * Execute and return all records for
     * <code><pre>SELECT * FROM [table] WHERE [field] IN [values]</pre></code>
     *
     * @deprecated - Use {@link #fetch(Table, Condition)} instead
     */
    @Deprecated
    public <R extends Record, T> List<R> fetch(Table<R> table, Field<T> field, T... values) throws SQLException {
        return fetch(table, field.in(values));
    }

    /**
     * Execute and return all records for
     * <code><pre>SELECT * FROM [table] WHERE [field] IN [values]</pre></code>
     *
     * @deprecated - Use {@link #fetch(Table, Condition)} instead
     */
    @Deprecated
    public <R extends Record, T> List<R> fetch(Table<R> table, Field<T> field, Collection<T> values)
        throws SQLException {
        return fetch(table, field.in(values));
    }

    /**
     * Execute and return all records for
     * <code><pre>SELECT * FROM [table] WHERE [condition] </pre></code>
     */
    public <R extends Record, T> List<R> fetch(Table<R> table, Condition condition) throws SQLException {
        return selectFrom(table).where(condition).fetch().getRecords();
    }

    /**
     * Execute and return zero or one record for
     * <code><pre>SELECT * FROM [table]</pre></code>
     *
     * @return The record or <code>null</code> if no record was returned
     * @throws SQLException if more than one record was found
     */
    public <R extends Record> R fetchOne(Table<R> table) throws SQLException {
        return filterOne(fetch(table));
    }

    /**
     * Execute and return zero or one record for
     * <code><pre>SELECT * FROM [table] WHERE [field] IN [values]</pre></code>
     *
     * @return The record or <code>null</code> if no record was returned
     * @throws SQLException if more than one record was found
     * @deprecated - Use {@link #fetchOne(Table, Condition)} instead
     */
    @Deprecated
    public <R extends Record, T> R fetchOne(Table<R> table, Field<T> field, T... values) throws SQLException {
        return filterOne(fetch(table, field, values));
    }

    /**
     * Execute and return zero or one record for
     * <code><pre>SELECT * FROM [table] WHERE [field] IN [values] </pre></code>
     *
     * @return The record or <code>null</code> if no record was returned
     * @throws SQLException if more than one record was found
     * @deprecated - Use {@link #fetchOne(Table, Condition)} instead
     */
    @Deprecated
    public <R extends Record, T> R fetchOne(Table<R> table, Field<T> field, Collection<T> values) throws SQLException {
        return filterOne(fetch(table, field, values));
    }

    /**
     * Execute and return zero or one record for
     * <code><pre>SELECT * FROM [table] WHERE [condition] </pre></code>
     *
     * @return The record or <code>null</code> if no record was returned
     * @throws SQLException if more than one record was found
     */
    public <R extends Record, T> R fetchOne(Table<R> table, Condition condition) throws SQLException {
        return filterOne(fetch(table, condition));
    }

    /**
     * Execute and return zero or one record for
     * <code><pre>SELECT * FROM [table] WHERE [field] IN [values] </pre></code>
     *
     * @return The record or <code>null</code> if no record was returned
     */
    public <R extends Record> R fetchAny(Table<R> table) throws SQLException {
        switch (getDialect()) {
            case HSQLDB: // No break
            case MYSQL: // No break
            case POSTGRES: // No break
            case DB2: // No break
            case H2: // No break
            case DERBY: // No break
            case SQLITE:
                return filterOne(selectFrom(table).limit(1).fetch().getRecords());

            case ORACLE:
                return filterOne(fetch(table, rownum(), 1));

            default:
                throw new SQLDialectNotSupportedException("This operation is not supported by dialect " + getDialect());
        }
    }

    /**
     * Insert one record
     * <code><pre>INSERT INTO [table] ... VALUES [record] </pre></code>
     *
     * @return The number of inserted records
     */
    public <R extends TableRecord<R>> int executeInsert(Table<R> table, R record) throws SQLException {
        InsertQuery<R> insert = insertQuery(table);
        insert.setRecord(record);
        return insert.execute();
    }

    /**
     * Update a table
     * <code><pre>UPDATE [table] SET [modified values in record] </pre></code>
     *
     * @return The number of updated records
     */
    public <R extends TableRecord<R>> int executeUpdate(Table<R> table, R record) throws SQLException {
        return executeUpdate(table, record, TrueCondition.TRUE_CONDITION);
    }

    /**
     * Update a table
     * <code><pre>UPDATE [table] SET [modified values in record] WHERE [field] IN [values]</pre></code>
     *
     * @return The number of updated records
     * @deprecated - Use {@link #executeUpdate(Table, TableRecord, Condition)}
     *             instead
     */
    @Deprecated
    public <R extends TableRecord<R>, T> int executeUpdate(Table<R> table, R record, Field<T> field, T... values)
        throws SQLException {
        return executeUpdate(table, record, field.in(values));
    }

    /**
     * Update a table
     * <code><pre>UPDATE [table] SET [modified values in record] WHERE [field] IN [values]</pre></code>
     *
     * @return The number of updated records
     * @deprecated - Use
     *             {@link #executeUpdateOne(Table, TableRecord, Condition)}
     *             instead
     */
    @Deprecated
    public <R extends TableRecord<R>, T> int executeUpdate(Table<R> table, R record, Field<T> field,
        Collection<T> values) throws SQLException {
        return executeUpdate(table, record, field.in(values));
    }

    /**
     * Update a table
     * <code><pre>UPDATE [table] SET [modified values in record] WHERE [condition]</pre></code>
     *
     * @return The number of updated records
     */
    public <R extends TableRecord<R>, T> int executeUpdate(Table<R> table, R record, Condition condition)
        throws SQLException {
        UpdateQuery<R> update = updateQuery(table);
        update.addConditions(condition);
        update.setRecord(record);
        return update.execute();
    }

    /**
     * Update one record in a table
     * <code><pre>UPDATE [table] SET [modified values in record]</pre></code>
     *
     * @return The number of updated records
     * @throws SQLException if more than one record was updated
     */
    public <R extends TableRecord<R>> int executeUpdateOne(Table<R> table, R record) throws SQLException {
        return filterUpdateOne(executeUpdate(table, record));
    }

    /**
     * Update one record in a table
     * <code><pre>UPDATE [table] SET [modified values in record] WHERE [field] IN [values]</pre></code>
     *
     * @return The number of updated records
     * @throws SQLException if more than one record was updated
     * @deprecated - Use
     *             {@link #executeUpdateOne(Table, TableRecord, Condition)}
     *             instead
     */
    @Deprecated
    public <R extends TableRecord<R>, T> int executeUpdateOne(Table<R> table, R record, Field<T> field, T... values)
        throws SQLException {
        return filterUpdateOne(executeUpdate(table, record, field, values));
    }

    /**
     * Update one record in a table
     * <code><pre>UPDATE [table] SET [modified values in record] WHERE [field] IN [values]</pre></code>
     *
     * @return The number of updated records
     * @throws SQLException if more than one record was updated
     * @deprecated - Use
     *             {@link #executeUpdateOne(Table, TableRecord, Condition)}
     *             instead
     */
    @Deprecated
    public <R extends TableRecord<R>, T> int executeUpdateOne(Table<R> table, R record, Field<T> field,
        Collection<T> values) throws SQLException {
        return filterUpdateOne(executeUpdate(table, record, field, values));
    }

    /**
     * Update one record in a table
     * <code><pre>UPDATE [table] SET [modified values in record] WHERE [condition]</pre></code>
     *
     * @return The number of updated records
     * @throws SQLException if more than one record was updated
     */
    public <R extends TableRecord<R>, T> int executeUpdateOne(Table<R> table, R record, Condition condition)
        throws SQLException {
        return filterUpdateOne(executeUpdate(table, record, condition));
    }

    /**
     * Delete records from a table <code><pre>DELETE FROM [table]</pre></code>
     *
     * @return The number of deleted records
     */
    public <R extends TableRecord<R>> int executeDelete(Table<R> table) throws SQLException {
        return executeDelete(table, TrueCondition.TRUE_CONDITION);
    }

    /**
     * Delete records from a table
     * <code><pre>DELETE FROM [table] WHERE [field] IN [values]</pre></code>
     *
     * @return The number of deleted records
     * @deprecated - Use {@link #executeDelete(Table, Condition)} instead
     */
    @Deprecated
    public <R extends TableRecord<R>, T> int executeDelete(Table<R> table, Field<T> field, T... values)
        throws SQLException {
        return executeDelete(table, field.in(values));
    }

    /**
     * Delete records from a table
     * <code><pre>DELETE FROM [table] WHERE [field] IN [values]</pre></code>
     *
     * @return The number of deleted records
     * @deprecated - Use {@link #executeDeleteOne(Table, Condition)} instead
     */
    @Deprecated
    public <R extends TableRecord<R>, T> int executeDelete(Table<R> table, Field<T> field, Collection<T> values)
        throws SQLException {
        return executeDelete(table, field.in(values));
    }

    /**
     * Delete records from a table
     * <code><pre>DELETE FROM [table] WHERE [condition]</pre></code>
     *
     * @return The number of deleted records
     */
    public <R extends TableRecord<R>, T> int executeDelete(Table<R> table, Condition condition) throws SQLException {
        DeleteQuery<R> delete = deleteQuery(table);
        delete.addConditions(condition);
        return delete.execute();
    }

    /**
     * Delete one record in a table <code><pre>DELETE FROM [table]</pre></code>
     *
     * @return The number of deleted records
     * @throws SQLException if more than one record was deleted
     */
    public <R extends TableRecord<R>> int executeDeleteOne(Table<R> table) throws SQLException {
        return executeDeleteOne(table, TrueCondition.TRUE_CONDITION);
    }

    /**
     * Delete one record in a table
     * <code><pre>DELETE FROM [table] WHERE [field] IN [values]</pre></code>
     *
     * @return The number of deleted records
     * @throws SQLException if more than one record was deleted
     * @deprecated - Use {@link #executeDeleteOne(Table, Condition)} instead
     */
    @Deprecated
    public <R extends TableRecord<R>, T> int executeDeleteOne(Table<R> table, Field<T> field, T... values)
        throws SQLException {
        return executeDeleteOne(table, field.in(values));
    }

    /**
     * Delete one record in a table
     * <code><pre>DELETE FROM [table] WHERE [field] IN [values]</pre></code>
     *
     * @return The number of deleted records
     * @throws SQLException if more than one record was deleted
     * @deprecated - Use {@link #executeDeleteOne(Table, Condition)} instead
     */
    @Deprecated
    public <R extends TableRecord<R>, T> int executeDeleteOne(Table<R> table, Field<T> field, Collection<T> values)
        throws SQLException {
        return executeDeleteOne(table, field.in(values));
    }

    /**
     * Delete one record in a table
     * <code><pre>DELETE FROM [table] WHERE [condition]</pre></code>
     *
     * @return The number of deleted records
     * @throws SQLException if more than one record was deleted
     */
    public <R extends TableRecord<R>, T> int executeDeleteOne(Table<R> table, Condition condition) throws SQLException {
        DeleteQuery<R> delete = deleteQuery(table);
        delete.addConditions(condition);
        return filterDeleteOne(delete.execute());
    }

    private int filterDeleteOne(int i) throws SQLException {
        return filterOne(i, "deleted");
    }

    private int filterUpdateOne(int i) throws SQLException {
        return filterOne(i, "updated");
    }

    private int filterOne(int i, String action) throws SQLException {
        if (i <= 1) {
            return i;
        }
        else {
            throw new SQLException("Too many rows " + action + " : " + i);
        }
    }

    private <R extends Record> R filterOne(List<R> list) throws SQLException {
        if (list.size() == 0) {
            return null;
        }
        else if (list.size() == 1) {
            return list.get(0);
        }
        else {
            throw new SQLException("Too many rows returned : " + list.size());
        }
    }

    @Override
    public String toString() {
        return "Factory [dialect=" + dialect + ", mapping=" + mapping + "]";
    }

    static {
        for (SQLDialect dialect : SQLDialect.values()) {
            Factory.DEFAULT_INSTANCES[dialect.ordinal()] = new Factory(dialect);
        }
    }

    public static Factory getFactory(SQLDialect dialect) {
        return DEFAULT_INSTANCES[dialect.ordinal()];
    }

    public static Factory getFactory(Configuration configuration) {
        return new Factory(configuration.getConnection(), configuration.getDialect(), configuration.getSchemaMapping());
    }
}
