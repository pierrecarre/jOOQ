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

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.jooq.Case;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.SQLDialectNotSupportedException;

/**
 * The Functions class provides an abstraction to the various functions in the
 * different SQL dialects.
 * <p>
 * Typically, when switching from MySQL to Oracle, you're not aware that all
 * your CONCATENATE() function calls should change over to CONCAT calls. And
 * there are many more similar examples. When using this API, your SQL will
 * remain valid even after you switched databases.
 * <p>
 * If your function is not available in this API, try creating your own
 * {@link Function} or use {@link PlainSQLField}
 * <p>
 * If by any chance a function is not supported in your current SQL dialect, a
 * {@link SQLDialectNotSupportedException} is thrown before executing the SQL
 *
 * @author Lukas Eder
 * @see {@link http://oreilly.com/catalog/sqlnut/chapter/ch04.html} A very nice
 *      overview over SQL99 standard functions, and implementations thereof in
 *      various SQL dialects
 */
public final class FunctionFactory {

    private final SQLDialect dialect;

    /**
     * Get the functions factory with a specific dialect.
     */
    FunctionFactory(SQLDialect dialect) {
        this.dialect = dialect;
    }

    /**
     * Retrieve the underlying dialect
     */
    public SQLDialect getDialect() {
        return dialect;
    }

    /**
     * Retrieve the rownum pseudo-field
     *
     * @deprecated - Use {@link Factory#rownum()} instead
     */
    @Deprecated
    public Field<Integer> rownum() {
        return new PseudoField<Integer>(getDialect(), "rownum", Integer.class);
    }

    /**
     * Get the sum over a numeric field: sum(field)
     *
     * @deprecated - Use {@link Field#sum()} instead
     */
    @Deprecated
    public <T extends Number> Field<T> sum(Field<T> field) {
        return new Function<T>(getDialect(), "sum", field.getType(), field);
    }

    /**
     * Get the average over a numeric field: avg(field)
     *
     * @deprecated - Use {@link Field#avg()} instead
     */
    @Deprecated
    public <T extends Number> Field<Double> avg(Field<T> field) {
        return new Function<Double>(getDialect(), "avg", Double.class, field);
    }

    /**
     * Get the absolute value of a numeric field: abs(field)
     *
     * @deprecated - Use {@link Field#abs()} instead
     */
    @Deprecated
    public <T extends Number> Field<T> abs(Field<T> field) {
        return new Function<T>(getDialect(), "abs", field.getType(), field);
    }

    /**
     * Get rounded value of a numeric field: round(field)
     *
     * @deprecated - Use {@link Field#round()} instead
     */
    @Deprecated
    public <T extends Number> Field<T> round(Field<T> field) {
        return new Function<T>(getDialect(), "round", field.getType(), field);
    }

    /**
     * Get the min value over a numeric field: min(field)
     *
     * @deprecated - Use {@link Field#min()} instead
     */
    @Deprecated
    public <T> Field<T> min(Field<T> field) {
        return field.min();
    }

    /**
     * Get the max value over a numeric field: max(field)
     *
     * @deprecated - Use {@link Field#max()} instead
     */
    @Deprecated
    public <T> Field<T> max(Field<T> field) {
        return field.max();
    }

    /**
     * Get the count(*) function
     *
     * @deprecated - Use {@link Factory#count()} instead
     */
    @Deprecated
    public Field<Integer> count() {
        return new Count(getDialect());
    }

    /**
     * Get the count(field) function
     *
     * @deprecated - Use {@link Field#count()} instead
     */
    @Deprecated
    public Field<Integer> count(Field<?> field) {
        return field.count();
    }

    /**
     * Get the count(distinct field) function
     *
     * @deprecated - Use {@link Field#countDistinct()} instead
     */
    @Deprecated
    public Field<Integer> countDistinct(Field<?> field) {
        return field.countDistinct();
    }

    /**
     * Get the upper(field) function
     *
     * @deprecated - Use {@link Field#upper()} instead
     */
    @Deprecated
    public Field<String> upper(Field<String> field) {
        return field.upper();
    }

    /**
     * Get the lower(field) function
     *
     * @deprecated - Use {@link Field#lower()} instead
     */
    @Deprecated
    public Field<String> lower(Field<String> field) {
        return field.lower();
    }

    /**
     * Get the trim(field) function
     *
     * @deprecated - Use {@link Field#trim()} instead
     */
    @Deprecated
    public Field<String> trim(Field<String> field) {
        return field.trim();
    }

    /**
     * Get the rtrim(field) function
     *
     * @deprecated - Use {@link Field#rtrim()} instead
     */
    @Deprecated
    public Field<String> rtrim(Field<String> field) {
        return field.rtrim();
    }

    /**
     * Get the ltrim(field) function
     *
     * @deprecated - Use {@link Field#ltrim()} instead
     */
    @Deprecated
    public Field<String> ltrim(Field<String> field) {
        return field.ltrim();
    }

    /**
     * Get the rpad(field, length) function
     *
     * @deprecated - Use {@link Field#rpad(Field)} instead
     */
    @Deprecated
    public Field<String> rpad(Field<String> field, Field<? extends Number> length) {
        return field.rpad(length);
    }

    /**
     * Get the rpad(field, length) function
     *
     * @deprecated - Use {@link Field#rpad(int)} instead
     */
    @Deprecated
    public Field<String> rpad(Field<String> field, int length) {
        return field.rpad(length);
    }

    /**
     * Get the rpad(field, length, c) function
     *
     * @deprecated - Use {@link Field#rpad(Field, Field)} instead
     */
    @Deprecated
    public Field<String> rpad(Field<String> field, Field<? extends Number> length, Field<String> c) {
        return field.rpad(length, c);
    }

    /**
     * Get the rpad(field, length, c) function
     *
     * @deprecated - Use {@link Field#rpad(int, char)} instead
     */
    @Deprecated
    public Field<String> rpad(Field<String> field, int length, char c) {
        return field.rpad(length, c);
    }

    /**
     * Get the rpad(field, length) function
     *
     * @deprecated - Use {@link Field#lpad(Field)} instead
     */
    @Deprecated
    public Field<String> lpad(Field<String> field, Field<? extends Number> length) {
        return field.lpad(length);
    }

    /**
     * Get the rpad(field, length) function
     *
     * @deprecated - Use {@link Field#lpad(int)} instead
     */
    @Deprecated
    public Field<String> lpad(Field<String> field, int length) {
        return field.lpad(length);
    }

    /**
     * Get the rpad(field, length, c) function
     *
     * @deprecated - Use {@link Field#lpad(Field, Field)} instead
     */
    @Deprecated
    public Field<String> lpad(Field<String> field, Field<? extends Number> length, Field<String> c) {
        return field.lpad(length, c);
    }

    /**
     * Get the rpad(field, length, c) function
     *
     * @deprecated - Use {@link Field#lpad(int, char)} instead
     */
    @Deprecated
    public Field<String> lpad(Field<String> field, int length, char c) {
        return field.lpad(length, c);
    }

    /**
     * Get the replace(in, search) function
     *
     * @deprecated - Use {@link Field#replace(Field)} instead
     */
    @Deprecated
    public Field<String> replace(Field<String> in, Field<String> search) {
        return in.replace(search);
    }

    /**
     * Get the replace(in, search) function
     *
     * @deprecated - Use {@link Field#replace(String)} instead
     */
    @Deprecated
    public Field<String> replace(Field<String> in, String search) {
        return in.replace(search);
    }

    /**
     * Get the replace(in, search, replace) function
     *
     * @deprecated - Use {@link Field#replace(Field, Field)} instead
     */
    @Deprecated
    public Field<String> replace(Field<String> in, Field<String> search, Field<String> replace) {
        return in.replace(search, replace);
    }

    /**
     * Get the replace(in, search, replace) function
     *
     * @deprecated - Use {@link Field#replace(String, String)} instead
     */
    @Deprecated
    public Field<String> replace(Field<String> in, String search, String replace) {
        return in.replace(search, replace);
    }

    /**
     * Get the ascii(field) function
     *
     * @deprecated - Use {@link Field#ascii()} instead
     */
    @Deprecated
    public Field<Integer> ascii(Field<String> field) {
        return field.ascii();
    }

    /**
     * Get the concatenate(field[, field, ...]) function
     * <p>
     * This translates into any dialect
     *
     * @deprecated - Use {@link Field#concatenate(Field...)} instead
     */
    @Deprecated
    public Field<String> concatenate(Field<String>... fields) {
        switch (getDialect()) {
            case MYSQL:
                return new StringFunction(getDialect(), "concat", fields);
        }

        return new StringFunction(getDialect(), "concatenate", fields);
    }

    /**
     * Get the substring(field, startingPosition) function
     * <p>
     * This translates into any dialect
     *
     * @deprecated - Use {@link Field#substring(int)} instead
     */
    @Deprecated
    public Field<String> substring(Field<String> field, int startingPosition) {
        return field.substring(startingPosition);
    }

    /**
     * Get the substring(field, startingPosition, length) function
     * <p>
     * This translates into any dialect
     *
     * @deprecated - Use {@link Field#substring(int, int)} instead
     */
    @Deprecated
    public Field<String> substring(Field<String> field, int startingPosition, int length)
        throws SQLDialectNotSupportedException {
        return field.substring(startingPosition, length);
    }

    /**
     * Get the current_date() function
     * <p>
     * This translates into any dialect
     *
     * @deprecated - Use {@link Factory#currentDate()} instead
     */
    @Deprecated
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
     *
     * @deprecated - Use {@link Factory#currentTime()} instead
     */
    @Deprecated
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
     *
     * @deprecated - Use {@link Factory#currentTimestamp()} instead
     */
    @Deprecated
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
     *
     * @deprecated - Use {@link Factory#currentUser()} instead
     */
    @Deprecated
    public Field<String> currentUser() {
        switch (getDialect()) {
            case ORACLE:
                return new StringFunction(getDialect(), "user");
        }

        return new StringFunction(getDialect(), "current_user");
    }

    /**
     * Get the char_length(field) function
     * <p>
     * This translates into any dialect
     *
     * @deprecated - Use {@link Field#charLength()} instead
     */
    @Deprecated
    public Field<Integer> charLength(Field<String> field) {
        switch (getDialect()) {
            case ORACLE:
                return new IntegerFunction(getDialect(), "length", field);
        }

        return new IntegerFunction(getDialect(), "char_length", field);
    }

    /**
     * Get the bit_length(field) function
     * <p>
     * This translates into any dialect
     *
     * @deprecated - Use {@link Field#bitLength()} instead
     */
    @Deprecated
    public Field<Integer> bitLength(Field<String> field) {
        switch (getDialect()) {
            case ORACLE:
                return new IntegerFunction(getDialect(), "8 * lengthb", field);
        }

        return new IntegerFunction(getDialect(), "bit_length", field);
    }

    /**
     * Get the octet_length(field) function
     * <p>
     * This translates into any dialect
     *
     * @deprecated - Use {@link Field#octetLength()} instead
     */
    @Deprecated
    public Field<Integer> octetLength(Field<String> field) {
        switch (getDialect()) {
            case ORACLE:
                return new IntegerFunction(getDialect(), "lengthb", field);
        }

        return new IntegerFunction(getDialect(), "octet_length", field);
    }

    /**
     * Get the extract(field, datePart) function
     * <p>
     * This translates into any dialect
     *
     * @deprecated - Use {@link Field#extract(DatePart)} instead
     */
    @Deprecated
    public Field<Integer> extract(Field<? extends java.util.Date> field, DatePart datePart)
        throws SQLDialectNotSupportedException {
        return field.extract(datePart);
    }

    /**
     * Get the position(in, search) function
     * <p>
     * This translates into any dialect
     *
     * @deprecated - Use {@link Field#position(String)} instead
     */
    @Deprecated
    public Field<Integer> position(Field<String> in, String search) throws SQLDialectNotSupportedException {
        return in.position(search);
    }

    /**
     * Get the position(in, search) function
     * <p>
     * This translates into any dialect
     *
     * @deprecated - Use {@link Field#position(Field)} instead
     */
    @Deprecated
    public Field<Integer> position(Field<String> in, Field<String> search) throws SQLDialectNotSupportedException {
        return in.position(search);
    }

    /**
     * Get a constant value
     *
     * @deprecated - Use {@link Factory#constant(Object)} instead
     */
    @Deprecated
    public <T> Field<T> constant(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Argument 'value' must not be null");
        }

        return new Constant<T>(getDialect(), value);
    }

    /**
     * Get the null field
     *
     * @deprecated - Use {@link Factory#NULL()} instead
     */
    @Deprecated
    public Field<?> NULL() {
        return new PseudoField<Object>(getDialect(), "null", Object.class);
    }

    /**
     * Initialse a {@link Case} statement. Decode is used as a method name to
     * avoid name clashes with Java's reserved literal "case"
     *
     * @see Case
     * @deprecated - Use {@link Factory#decode()} instead
     */
    @Deprecated
    public Case decode() {
        return new CaseImpl(getDialect());
    }
}
