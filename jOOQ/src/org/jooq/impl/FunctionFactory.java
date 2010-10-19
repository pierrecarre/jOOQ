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

import org.jooq.CaseStartStep;
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
     */
    public Field<Integer> rownum() {
        return new FieldImpl<Integer>(getDialect(), "rownum", Integer.class);
    }

    /**
     * Get the sum over a numeric field: sum(field)
     */
    public <T extends Number> Field<T> sum(Field<T> field) {
        return new Function<T>(getDialect(), "sum", field.getType(), field);
    }

    /**
     * Get the average over a numeric field: avg(field)
     */
    public <T extends Number> Field<Double> avg(Field<T> field) {
        return new Function<Double>(getDialect(), "avg", Double.class, field);
    }

    /**
     * Get the absolute value of a numeric field: abs(field)
     */
    public <T extends Number> Field<T> abs(Field<T> field) {
        return new Function<T>(getDialect(), "abs", field.getType(), field);
    }

    /**
     * Get rounded value of a numeric field: round(field)
     */
    public <T extends Number> Field<T> round(Field<T> field) {
        return new Function<T>(getDialect(), "round", field.getType(), field);
    }

    /**
     * Get the min value over a numeric field: min(field)
     */
    public <T> Field<T> min(Field<T> field) {
        return new Function<T>(getDialect(), "min", field.getType(), field);
    }

    /**
     * Get the max value over a numeric field: max(field)
     */
    public <T> Field<T> max(Field<T> field) {
        return new Function<T>(getDialect(), "max", field.getType(), field);
    }

    /**
     * Get the count(*) function
     */
    public Field<Integer> count() {
        return new Count(getDialect());
    }

    /**
     * Get the count(field) function
     */
    public Field<Integer> count(Field<?> field) {
        return new Count(getDialect(), field, false);
    }

    /**
     * Get the count(distinct field) function
     */
    public Field<Integer> countDistinct(Field<?> field) {
        return new Count(getDialect(), field, true);
    }

    /**
     * Get the upper(field) function
     */
    public Field<String> upper(Field<String> field) {
        return new Function<String>(getDialect(), "upper", field.getType(), field);
    }

    /**
     * Get the lower(field) function
     */
    public Field<String> lower(Field<String> field) {
        return new Function<String>(getDialect(), "lower", field.getType(), field);
    }

    /**
     * Get the trim(field) function
     */
    public Field<String> trim(Field<String> field) {
        return new Function<String>(getDialect(), "trim", field.getType(), field);
    }

    /**
     * Get the rtrim(field) function
     */
    public Field<String> rtrim(Field<String> field) {
        return new Function<String>(getDialect(), "rtrim", field.getType(), field);
    }

    /**
     * Get the ltrim(field) function
     */
    public Field<String> ltrim(Field<String> field) {
        return new Function<String>(getDialect(), "ltrim", field.getType(), field);
    }

    /**
     * Get the rpad(field, length) function
     */
    public Field<String> rpad(Field<String> field, Field<? extends Number> length) {
        return new StringFunction(getDialect(), "rpad", field, length);
    }

    /**
     * Get the rpad(field, length) function
     */
    public Field<String> rpad(Field<String> field, int length) {
        return rpad(field, constant(length));
    }

    /**
     * Get the rpad(field, length, c) function
     */
    public Field<String> rpad(Field<String> field, Field<? extends Number> length, Field<String> c) {
        return new StringFunction(getDialect(), "rpad", field, length, c);
    }

    /**
     * Get the rpad(field, length, c) function
     */
    public Field<String> rpad(Field<String> field, int length, char c) {
        return rpad(field, constant(length), constant("" + c));
    }

    /**
     * Get the rpad(field, length) function
     */
    public Field<String> lpad(Field<String> field, Field<? extends Number> length) {
        return new StringFunction(getDialect(), "lpad", field, length);
    }

    /**
     * Get the rpad(field, length) function
     */
    public Field<String> lpad(Field<String> field, int length) {
        return lpad(field, constant(length));
    }

    /**
     * Get the rpad(field, length, c) function
     */
    public Field<String> lpad(Field<String> field, Field<? extends Number> length, Field<String> c) {
        return new StringFunction(getDialect(), "lpad", field, length, c);
    }

    /**
     * Get the rpad(field, length, c) function
     */
    public Field<String> lpad(Field<String> field, int length, char c) {
        return lpad(field, constant(length), constant("" + c));
    }

    /**
     * Get the replace(in, search) function
     */
    public Field<String> replace(Field<String> in, Field<String> search) {
        return new StringFunction(getDialect(), "replace", in, search);
    }

    /**
     * Get the replace(in, search) function
     */
    public Field<String> replace(Field<String> in, String search) {
        return replace(in, constant(search));
    }

    /**
     * Get the replace(in, search, replace) function
     */
    public Field<String> replace(Field<String> in, Field<String> search, Field<String> replace) {
        return new StringFunction(getDialect(), "replace", in, search, replace);
    }

    /**
     * Get the replace(in, search, replace) function
     */
    public Field<String> replace(Field<String> in, String search, String replace) {
        return replace(in, constant(search), constant(replace));
    }

    /**
     * Get the ascii(field) function
     */
    public Field<Integer> ascii(Field<String> field) {
        return new IntegerFunction(getDialect(), "ascii", field);
    }

    /**
     * Get the concatenate(field[, field, ...]) function
     * <p>
     * This translates into any dialect
     */
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
     */
    public Field<String> substring(Field<String> field, int startingPosition) {
        return substring(field, startingPosition, -1);
    }

    /**
     * Get the substring(field, startingPosition, length) function
     * <p>
     * This translates into any dialect
     */
    public Field<String> substring(Field<String> field, int startingPosition, int length)
        throws SQLDialectNotSupportedException {
        Field<Integer> startingPositionConstant = constant(startingPosition);
        Field<Integer> lengthConstant = constant(length);

        String functionName = "substring";

        switch (getDialect()) {
            case ORACLE:
                functionName = "substr";
                break;
        }

        if (length == -1) {
            return new StringFunction(getDialect(), functionName, field, startingPositionConstant);
        }
        else {
            return new StringFunction(getDialect(), functionName, field, startingPositionConstant, lengthConstant);
        }
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

    /**
     * Get the char_length(field) function
     * <p>
     * This translates into any dialect
     */
    public Field<Integer> charLength(Field<?> field) {
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
     */
    public Field<Integer> bitLength(Field<?> field) {
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
     */
    public Field<Integer> octetLength(Field<?> field) {
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
     */
    public Field<Integer> extract(Field<? extends java.util.Date> field, DatePart datePart)
        throws SQLDialectNotSupportedException {
        switch (getDialect()) {
            case MYSQL: // No break
            case POSTGRES:
            case HSQLDB:
                return new Extract(getDialect(), field, datePart);
            case ORACLE:
                switch (datePart) {
                    case YEAR:
                        return new IntegerFunction(getDialect(), "to_char", field, constant("YYYY"));
                    case MONTH:
                        return new IntegerFunction(getDialect(), "to_char", field, constant("MM"));
                    case DAY:
                        return new IntegerFunction(getDialect(), "to_char", field, constant("DD"));
                    case HOUR:
                        return new IntegerFunction(getDialect(), "to_char", field, constant("HH24"));
                    case MINUTE:
                        return new IntegerFunction(getDialect(), "to_char", field, constant("MI"));
                    case SECOND:
                        return new IntegerFunction(getDialect(), "to_char", field, constant("SS"));
                    default:
                        throw new SQLDialectNotSupportedException("DatePart not supported: " + datePart);
                }
            case MSSQL:
                throw new SQLDialectNotSupportedException("TODO: Implement CONVERT for MSSQL");

            default:
                throw new SQLDialectNotSupportedException("extract not supported");
        }
    }

    /**
     * Get the position(in, search) function
     * <p>
     * This translates into any dialect
     */
    public Field<Integer> position(Field<String> in, String search) throws SQLDialectNotSupportedException {
        return position(in, constant(search));
    }

    /**
     * Get the position(in, search) function
     * <p>
     * This translates into any dialect
     */
    public Field<Integer> position(Field<String> in, Field<String> search) throws SQLDialectNotSupportedException {
        switch (getDialect()) {
            case MYSQL: // No break
            case POSTGRES:
            case HSQLDB:
                return new PositionFunctionImpl(getDialect(), search, in);
            case ORACLE:
                return new IntegerFunction(getDialect(), "instr", in, search);
            case MSSQL:
                return new IntegerFunction(getDialect(), "charindex", search, in);

            default:
                throw new SQLDialectNotSupportedException("position not supported");
        }
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
        return new FieldImpl<Object>(getDialect(), "null", Object.class);
    }

    /**
     * Initialse a {@link CaseStartStep} statement. Decode is used as a method
     * name to avoid name clashes with Java's reserved literal "case"
     *
     * @see CaseStartStep
     */
    public CaseStartStep decode() {
        return new CaseStartStepImpl(getDialect());
    }
}
