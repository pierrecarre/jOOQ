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

package org.jooq;

import java.math.BigDecimal;
import java.util.Collection;

import org.jooq.impl.Factory;

/**
 * A field used in tables and conditions
 *
 * @author Lukas Eder
 */
public interface Field<T> extends NamedTypeProviderQueryPart<T>, AliasProvider<Field<T>> {

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    /**
     * The name of the field
     */
    @Override
    String getName();

    /**
     * The type of the field
     */
    @Override
    Class<? extends T> getType();

    /**
     * Create an alias for this field
     *
     * @param alias The alias name
     * @return The field alias
     */
    @Override
    Field<T> as(String alias);

    /**
     * Watch out! This is {@link Object#equals(Object)}, not a jOOQ feature! :-)
     */
    @Override
    boolean equals(Object other);

    /**
     * Whether this field represents a <code>null</code> literal.
     * <p>
     * This method is for JOOQ INTERNAL USE only!
     * <p>
     * This method was added to be able to recognise <code>null</code> literals
     * within jOOQ and handle them specially, as some SQL dialects have a rather
     * un-intuitive way of handling <code>null</code> values.
     */
    boolean isNullLiteral();

    // ------------------------------------------------------------------------
    // Type casts
    // ------------------------------------------------------------------------

    /**
     * Cast this field to the type of another field.
     * <p>
     * This results in the same as casting this field to
     * {@link Field#getCastTypeName()}
     *
     * @param <Z> The generic type of the cast field
     * @param field The field whose type is used for the cast
     * @return The cast field
     * @see #cast(DataType)
     */
    <Z> Field<Z> cast(Field<Z> field);

    /**
     * Cast this field to a dialect-specific data type.
     *
     * @param <Z> The generic type of the cast field
     * @param type
     */
    <Z> Field<Z> cast(DataType<Z> type);

    /**
     * Cast this field to another type
     * <p>
     * The actual cast may not be accurate as the {@link DataType} has to be
     * "guessed" from the jOOQ-configured data types. Use
     * {@link #cast(DataType)} for more accurate casts.
     *
     * @param <Z> The generic type of the cast field
     * @param type The type that is used for the cast
     * @return The cast field
     * @see #cast(DataType)
     */
    <Z> Field<Z> cast(Class<? extends Z> type);

    // ------------------------------------------------------------------------
    // Conversion of field into a sort field
    // ------------------------------------------------------------------------

    /**
     * Create an ascending sort field from this
     *
     * @return This field as an ascending sort field
     */
    SortField<T> ascending();

    /**
     * Create a descending sort field from this
     *
     * @return This field as a descending sort field
     */
    SortField<T> descending();

    // ------------------------------------------------------------------------
    // Arithmetic expressions
    // ------------------------------------------------------------------------

    /**
     * An arithmetic expression adding this to value
     */
    Field<T> add(Number value);

    /**
     * An arithmetic expression adding this to value
     */
    Field<T> add(Field<? extends Number> value);

    /**
     * An arithmetic expression subtracting value from this
     */
    Field<T> subtract(Number value);

    /**
     * An arithmetic expression subtracting value from this
     */
    Field<T> subtract(Field<? extends Number> value);

    /**
     * An arithmetic expression multiplying this with value
     */
    Field<T> multiply(Number value);

    /**
     * An arithmetic expression multiplying this with value
     */
    Field<T> multiply(Field<? extends Number> value);

    /**
     * An arithmetic expression dividing this by value
     */
    Field<T> divide(Number value);

    /**
     * An arithmetic expression dividing this by value
     */
    Field<T> divide(Field<? extends Number> value);

    // ------------------------------------------------------------------------
    // Functions created from this field
    // ------------------------------------------------------------------------

    /**
     * Get the count(field) function
     *
     * @see Factory#count()
     */
    Field<Integer> count();

    /**
     * Get the count(distinct field) function
     *
     * @see Factory#count()
     */
    Field<Integer> countDistinct();

    /**
     * Get the max value over a field: max(field)
     */
    Field<T> max();

    /**
     * Get the min value over a field: min(field)
     */
    Field<T> min();

    /**
     * Get the sum over a numeric field: sum(field)
     */
    Field<BigDecimal> sum();

    /**
     * Get the average over a numeric field: avg(field)
     */
    Field<BigDecimal> avg();

    /**
     * Get the absolute value of a numeric field: abs(field)
     */
    Field<T> abs();

    /**
     * Get rounded value of a numeric field: round(field)
     */
    Field<T> round();

    /**
     * Get the upper(field) function
     */
    Field<String> upper();

    /**
     * Get the lower(field) function
     */
    Field<String> lower();

    /**
     * Get the trim(field) function
     */
    Field<String> trim();

    /**
     * Get the rtrim(field) function
     */
    Field<String> rtrim();

    /**
     * Get the ltrim(field) function
     */
    Field<String> ltrim();

    /**
     * Get the rpad(field, length) function
     */
    Field<String> rpad(Field<? extends Number> length);

    /**
     * Get the rpad(field, length) function
     */
    Field<String> rpad(int length);

    /**
     * Get the rpad(field, length, c) function
     */
    Field<String> rpad(Field<? extends Number> length, Field<String> c);

    /**
     * Get the rpad(field, length, c) function
     */
    Field<String> rpad(int length, char c);

    /**
     * Get the rpad(field, length) function
     */
    Field<String> lpad(Field<? extends Number> length);

    /**
     * Get the rpad(field, length) function
     */
    Field<String> lpad(int length);

    /**
     * Get the rpad(field, length, c) function
     */
    Field<String> lpad(Field<? extends Number> length, Field<String> c);

    /**
     * Get the rpad(field, length, c) function
     */
    Field<String> lpad(int length, char c);

    /**
     * Get the replace(in, search) function
     */
    Field<String> replace(Field<String> search);

    /**
     * Get the replace(in, search) function
     */
    Field<String> replace(String search);

    /**
     * Get the replace(in, search, replace) function
     */
    Field<String> replace(Field<String> search, Field<String> replace);

    /**
     * Get the replace(in, search, replace) function
     */
    Field<String> replace(String search, String replace);

    /**
     * Get the position(in, search) function
     * <p>
     * This translates into any dialect
     */
    Field<Integer> position(String search);

    /**
     * Get the position(in, search) function
     * <p>
     * This translates into any dialect
     */
    Field<Integer> position(Field<String> search);

    /**
     * Get the ascii(field) function
     */
    Field<Integer> ascii();

    /**
     * Get the concatenate(field[, field, ...]) function
     * <p>
     * This translates into any dialect
     */
    Field<String> concatenate(Field<String> field, Field<String>... fields);

    /**
     * Get the concatenate(value[, value, ...]) function
     * <p>
     * This translates into any dialect
     */
    Field<String> concatenate(String value, String... values);

    /**
     * Get the substring(field, startingPosition) function
     * <p>
     * This translates into any dialect
     */
    Field<String> substring(int startingPosition);

    /**
     * Get the substring(field, startingPosition, length) function
     * <p>
     * This translates into any dialect
     */
    Field<String> substring(int startingPosition, int length) throws SQLDialectNotSupportedException;

    /**
     * Get the char_length(field) function
     * <p>
     * This translates into any dialect
     */
    Field<Integer> charLength();

    /**
     * Get the bit_length(field) function
     * <p>
     * This translates into any dialect
     */
    Field<Integer> bitLength();

    /**
     * Get the octet_length(field) function
     * <p>
     * This translates into any dialect
     */
    Field<Integer> octetLength();

    /**
     * Get the extract(field, datePart) function
     * <p>
     * This translates into any dialect
     */
    Field<Integer> extract(DatePart datePart);

    /**
     * Gets the Oracle-style NVL(value, defaultValue) function
     *
     * @see #nvl(Field)
     */
    Field<T> nvl(T defaultValue);

    /**
     * Gets the Oracle-style NVL(value, defaultValue) function
     * <p>
     * Returns the dialect's equivalent to NVL:
     * <ul>
     * <li>DB2 <a href=
     * "http://publib.boulder.ibm.com/infocenter/db2luw/v9r7/index.jsp?topic=/com.ibm.db2.luw.sql.ref.doc/doc/r0052627.html"
     * >NVL</a></li>
     * <li>Derby <a
     * href="http://db.apache.org/derby/docs/10.7/ref/rreffunccoalesce.html"
     * >COALESCE</a></li>
     * <li>H2 <a
     * href="http://www.h2database.com/html/functions.html#ifnull">IFNULL</a></li>
     * <li>HSQLDB <a
     * href="http://hsqldb.org/doc/2.0/guide/builtinfunctions-chapt.html"
     * >NVL</a></li>
     * <li>MySQL <a href=
     * "http://dev.mysql.com/doc/refman/5.0/en/control-flow-functions.html"
     * >IFNULL</a></li>
     * <li>Oracle <a
     * href="http://www.techonthenet.com/oracle/functions/nvl.php">NVL</a></li>
     * <li>Postgres <a href=
     * "http://www.postgresql.org/docs/8.1/static/functions-conditional.html"
     * >COALESCE</a></li>
     * <li>SQLite <a
     * href="http://www.sqlite.org/lang_corefunc.html#ifnull">IFNULL</a></li>
     * </ul>
     */
    Field<T> nvl(Field<T> defaultValue);

    /**
     * Gets the Oracle-style NVL2(value, valueIfNotNull, valueIfNull) function
     *
     * @see #nvl2(Field)
     */
    Field<T> nvl2(T valueIfNotNull, T valueIfNull);

    /**
     * Gets the Oracle-style NVL2(value, valueIfNotNull, valueIfNull) function
     * <p>
     * Returns the dialect's equivalent to NVL2:
     * <ul>
     * <li>Oracle <a
     * href="http://www.techonthenet.com/oracle/functions/nvl2.php">NVL2</a></li>
     * </ul>
     * <p>
     * Other dialects:
     * <code>CASE WHEN [value] IS NULL THEN [valueIfNull] ELSE [valueIfNotNull] END</code>
     */
    Field<T> nvl2(Field<T> valueIfNotNull, Field<T> valueIfNull);


    /**
     * Gets the Oracle-style
     * <code>DECODE(expression, search, result[, search , result]... [, default])</code>
     * function
     *
     * @see #decode(Field, Field, Field[])
     */
    <Z> Field<Z> decode(T search, Z result);

    /**
     * Gets the Oracle-style
     * <code>DECODE(expression, search, result[, search , result]... [, default])</code>
     * function
     *
     * @see #decode(Field, Field, Field[])
     */
    <Z> Field<Z> decode(T search, Z result, Object... more);

    /**
     * Gets the Oracle-style
     * <code>DECODE(expression, search, result[, search , result]... [, default])</code>
     * function
     *
     * @see #decode(Field, Field, Field[])
     */
    <Z> Field<Z> decode(Field<T> search, Field<Z> result);

    /**
     * Gets the Oracle-style
     * <code>DECODE(expression, search, result[, search , result]... [, default])</code>
     * function
     * <p>
     * Returns the dialect's equivalent to DECODE:
     * <ul>
     * <li>Oracle <a
     * href="http://www.techonthenet.com/oracle/functions/decode.php">DECODE</a></li>
     * </ul>
     * <p>
     * Other dialects: <code><pre>
     * CASE WHEN [this = search] THEN [result],
     *     [WHEN more...         THEN more...]
     *     [ELSE more...]
     * END
     * </pre></code>
     *
     * @param search the mandatory first search parameter
     * @param result the mandatory first result parameter
     * @param more the optional parameters. If <code>more.length</code> is even,
     *            then it is assumed that it contains more search/result pairs.
     *            If <code>more.length</code> is odd, then it is assumed that it
     *            contains more search/result pairs plus a default at the end.
     */
    <Z> Field<Z> decode(Field<T> search, Field<Z> result, Field<?>... more);

    /**
     * Gets the Oracle-style <code>COALESCE(expr1, expr2, ... , exprn)</code>
     * function
     *
     * @see #coalesce(Field, Field...)
     */
    Field<T> coalesce(T option, T... options);

    /**
     * Gets the Oracle-style <code>COALESCE(expr1, expr2, ... , exprn)</code>
     * function
     * <p>
     * Returns the dialect's equivalent to COALESCE:
     * <ul>
     * <li>Oracle <a
     * href="http://www.techonthenet.com/oracle/functions/decode.php">DECODE</a>
     * </li>
     * </ul>
     */
    Field<T> coalesce(Field<T> option, Field<?>... options);

    // ------------------------------------------------------------------------
    // Conditions created from this field
    // ------------------------------------------------------------------------

    /**
     * <code>this is null</code>
     */
    Condition isNull();

    /**
     * <code>this is not null</code>
     */
    Condition isNotNull();

    /**
     * <code>this like value</code>
     */
    Condition like(T value);

    /**
     * <code>this not like value</code>
     */
    Condition notLike(T value);

    /**
     * <code>this in (values...)</code>
     */
    Condition in(T... values);

    /**
     * <code>this in (select...)</code>
     */
    Condition in(Select<?> query);

    /**
     * <code>this not in (values...)</code>
     */
    Condition notIn(Collection<T> values);

    /**
     * <code>this not in (values...)</code>
     */
    Condition notIn(T... values);

    /**
     * <code>this not in (select...)</code>
     */
    Condition notIn(Select<?> query);

    /**
     * <code>this in (values...)</code>
     */
    Condition in(Collection<T> values);

    /**
     * <code>this between minValue and maxValue</code>
     */
    Condition between(T minValue, T maxValue);

    /**
     * <code>this = value</code>
     */
    Condition equal(T value);

    /**
     * <code>this = field</code>
     */
    Condition equal(Field<T> field);

    /**
     * <code>this = (Select<?> ...)</code>
     */
    Condition equal(Select<?> query);

    /**
     * <code>this = any (Select<?> ...)</code>
     */
    Condition equalAny(Select<?> query);

    /**
     * <code>this = some (Select<?> ...)</code>
     */
    Condition equalSome(Select<?> query);

    /**
     * <code>this = all (Select<?> ...)</code>
     */
    Condition equalAll(Select<?> query);

    /**
     * <code>this != value</code>
     */
    Condition notEqual(T value);

    /**
     * <code>this != field</code>
     */
    Condition notEqual(Field<T> field);

    /**
     * <code>this != (Select<?> ...)</code>
     */
    Condition notEqual(Select<?> query);

    /**
     * <code>this != any (Select<?> ...)</code>
     */
    Condition notEqualAny(Select<?> query);

    /**
     * <code>this != some (Select<?> ...)</code>
     */
    Condition notEqualSome(Select<?> query);

    /**
     * <code>this != all (Select<?> ...)</code>
     */
    Condition notEqualAll(Select<?> query);

    /**
     * <code>this < value</code>
     */
    Condition lessThan(T value);

    /**
     * <code>this < field</code>
     */
    Condition lessThan(Field<T> field);

    /**
     * <code>this < (Select<?> ...)</code>
     */
    Condition lessThan(Select<?> query);

    /**
     * <code>this < any (Select<?> ...)</code>
     */
    Condition lessThanAny(Select<?> query);

    /**
     * <code>this < some (Select<?> ...)</code>
     */
    Condition lessThanSome(Select<?> query);

    /**
     * <code>this < all (Select<?> ...)</code>
     */
    Condition lessThanAll(Select<?> query);

    /**
     * <code>this <= value</code>
     */
    Condition lessOrEqual(T value);

    /**
     * <code>this <= field</code>
     */
    Condition lessOrEqual(Field<T> field);

    /**
     * <code>this <= (Select<?> ...)</code>
     */
    Condition lessOrEqual(Select<?> query);

    /**
     * <code>this <= any (Select<?> ...)</code>
     */
    Condition lessOrEqualToAny(Select<?> query);

    /**
     * <code>this <= some (Select<?> ...)</code>
     */
    Condition lessOrEqualToSome(Select<?> query);

    /**
     * <code>this <= all (Select<?> ...)</code>
     */
    Condition lessOrEqualToAll(Select<?> query);

    /**
     * <code>this > value</code>
     */
    Condition greaterThan(T value);

    /**
     * <code>this > field</code>
     */
    Condition greaterThan(Field<T> field);

    /**
     * <code>this > (Select<?> ...)</code>
     */
    Condition greaterThan(Select<?> query);

    /**
     * <code>this > any (Select<?> ...)</code>
     */
    Condition greaterThanAny(Select<?> query);

    /**
     * <code>this > some (Select<?> ...)</code>
     */
    Condition greaterThanSome(Select<?> query);

    /**
     * <code>this > all (Select<?> ...)</code>
     */
    Condition greaterThanAll(Select<?> query);

    /**
     * <code>this >= value</code>
     */
    Condition greaterOrEqual(T value);

    /**
     * <code>this >= field</code>
     */
    Condition greaterOrEqual(Field<T> field);

    /**
     * <code>this >= (Select<?> ...)</code>
     */
    Condition greaterOrEqual(Select<?> query);

    /**
     * <code>this >= any (Select<?> ...)</code>
     */
    Condition greaterOrEqualAny(Select<?> query);

    /**
     * <code>this >= some (Select<?> ...)</code>
     */
    Condition greaterOrEqualSome(Select<?> query);

    /**
     * <code>this >= all (Select<?> ...)</code>
     */
    Condition greaterOrEqualAll(Select<?> query);

}
