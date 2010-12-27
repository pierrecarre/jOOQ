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

import java.math.BigDecimal;
import java.util.Collection;

import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.SQLDialectNotSupportedException;
import org.jooq.Select;
import org.jooq.SortField;

abstract class AbstractField<T> extends AbstractNamedTypeProviderQueryPart<T> implements Field<T> {

    /**
     * Generated UID
     */
    private static final long serialVersionUID = 2884811923648354905L;

    AbstractField(Configuration configuration, String name, Class<? extends T> type) {
        super(configuration, name, type);
    }

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    @Override
    public Field<T> as(String alias) {
        return new FieldAlias<T>(getConfiguration(), this, alias);
    }

    // ------------------------------------------------------------------------
    // Conversion of field into a sort field
    // ------------------------------------------------------------------------

    @Override
    public final SortField<T> ascending() {
        return new SortFieldImpl<T>(getConfiguration(), this, SortOrder.ASC);
    }

    @Override
    public final SortField<T> descending() {
        return new SortFieldImpl<T>(getConfiguration(), this, SortOrder.DESC);
    }

    // ------------------------------------------------------------------------
    // Arithmetic expressions
    // ------------------------------------------------------------------------

    @Override
    public final Field<T> add(Number value) {
        return add(constant(value));
    }

    /**
     * This default implementation is known to be overridden by
     * {@link ArithmeticExpression} to generate neater expressions
     */
    @Override
    public Field<T> add(Field<? extends Number> value) {
        return new ArithmeticExpression<T>(getConfiguration(), this, value, ArithmeticOperator.ADD);
    }

    @Override
    public final Field<T> subtract(Number value) {
        return subtract(constant(value));
    }

    @Override
    public final Field<T> subtract(Field<? extends Number> value) {
        return new ArithmeticExpression<T>(getConfiguration(), this, value, ArithmeticOperator.SUBTRACT);
    }

    @Override
    public final Field<T> multiply(Number value) {
        return multiply(constant(value));
    }

    /**
     * This default implementation is known to be overridden by
     * {@link ArithmeticExpression} to generate neater expressions
     */
    @Override
    public Field<T> multiply(Field<? extends Number> value) {
        return new ArithmeticExpression<T>(getConfiguration(), this, value, ArithmeticOperator.MULTIPLY);
    }

    @Override
    public final Field<T> divide(Number value) {
        return divide(constant(value));
    }

    @Override
    public final Field<T> divide(Field<? extends Number> value) {
        return new ArithmeticExpression<T>(getConfiguration(), this, value, ArithmeticOperator.DIVIDE);
    }

    // ------------------------------------------------------------------------
    // Functions created from this field
    // ------------------------------------------------------------------------

    @Override
    public final Field<Integer> count() {
        return new Count(getConfiguration(), this, false);
    }

    @Override
    public final Field<Integer> countDistinct() {
        return new Count(getConfiguration(), this, true);
    }

    @Override
    public final Field<T> max() {
        return new Function<T>(getConfiguration(), "max", getType(), this);
    }

    @Override
    public final Field<T> min() {
        return new Function<T>(getConfiguration(), "min", getType(), this);
    }

    @Override
    public final Field<BigDecimal> sum() {
        return new Function<BigDecimal>(getConfiguration(), "sum", BigDecimal.class, this);
    }

    @Override
    public final Field<BigDecimal> avg() {
        return new Function<BigDecimal>(getConfiguration(), "avg", BigDecimal.class, this);
    }

    @Override
    public final Field<T> abs() {
        return new Function<T>(getConfiguration(), "abs", getType(), this);
    }

    @Override
    public Field<T> round() {
        return new Function<T>(getConfiguration(), "round", getType(), this);
    }

    @Override
    public final Field<String> upper() {
        return new Function<String>(getConfiguration(), "upper", String.class, this);
    }

    @Override
    public final Field<String> lower() {
        return new Function<String>(getConfiguration(), "lower", String.class, this);
    }

    @Override
    public final Field<String> trim() {
        return new Function<String>(getConfiguration(), "trim", String.class, this);
    }

    @Override
    public final Field<String> rtrim() {
        return new Function<String>(getConfiguration(), "rtrim", String.class, this);
    }

    @Override
    public final Field<String> ltrim() {
        return new Function<String>(getConfiguration(), "ltrim", String.class, this);
    }

    @Override
    public Field<String> rpad(Field<? extends Number> length) {
        return new StringFunction(getConfiguration(), "rpad", this, length);
    }

    @Override
    public Field<String> rpad(int length) {
        return rpad(constant(length));
    }

    @Override
    public Field<String> rpad(Field<? extends Number> length, Field<String> c) {
        return new StringFunction(getConfiguration(), "rpad", this, length, c);
    }

    @Override
    public Field<String> rpad(int length, char c) {
        return rpad(constant(length), constant("" + c));
    }

    @Override
    public Field<String> lpad(Field<? extends Number> length) {
        return new StringFunction(getConfiguration(), "lpad", this, length);
    }

    @Override
    public Field<String> lpad(int length) {
        return lpad(constant(length));
    }

    @Override
    public Field<String> lpad(Field<? extends Number> length, Field<String> c) {
        return new StringFunction(getConfiguration(), "lpad", this, length, c);
    }

    @Override
    public Field<String> lpad(int length, char c) {
        return lpad(constant(length), constant("" + c));
    }

    @Override
    public Field<String> replace(Field<String> search) {
        return new StringFunction(getConfiguration(), "replace", this, search);
    }

    @Override
    public Field<String> replace(String search) {
        return replace(constant(search));
    }

    @Override
    public Field<String> replace(Field<String> search, Field<String> replace) {
        return new StringFunction(getConfiguration(), "replace", this, search, replace);
    }

    @Override
    public Field<String> replace(String search, String replace) {
        return replace(constant(search), constant(replace));
    }

    @Override
    public final Field<Integer> position(String search) throws SQLDialectNotSupportedException {
        return position(constant(search));
    }

    @Override
    public final Field<Integer> position(Field<String> search) throws SQLDialectNotSupportedException {
        switch (getDialect()) {
            case MYSQL: // No break
            case POSTGRES:
            case HSQLDB:
            case H2:
            	return new PositionFunctionImpl(getConfiguration(), search, this);
            case DB2:
                return new IntegerFunction(getConfiguration(), "locate", search, this);
            case ORACLE:
                return new IntegerFunction(getConfiguration(), "instr", this, search);
            case MSSQL:
                return new IntegerFunction(getConfiguration(), "charindex", search, this);

            default:
                throw new SQLDialectNotSupportedException("position not supported");
        }
    }

    @Override
    public final Field<Integer> ascii() {
        return new IntegerFunction(getConfiguration(), "ascii", this);
    }

    @Override
    public final Field<String> concatenate(Field<String>... fields) {
        switch (getDialect()) {
            case MYSQL:
                return new StringFunction(getConfiguration(), "concat", fields);
        }

        return new StringFunction(getConfiguration(), "concatenate", fields);
    }

    @Override
    public final Field<String> substring(int startingPosition) {
        return substring(startingPosition, -1);
    }

    @Override
    public final Field<String> substring(int startingPosition, int length) throws SQLDialectNotSupportedException {
        Field<Integer> startingPositionConstant = constant(startingPosition);
        Field<Integer> lengthConstant = constant(length);

        String functionName = "substring";

        switch (getDialect()) {
            case ORACLE:
                functionName = "substr";
                break;
        }

        if (length == -1) {
            return new StringFunction(getConfiguration(), functionName, this, startingPositionConstant);
        }
        else {
            return new StringFunction(getConfiguration(), functionName, this, startingPositionConstant, lengthConstant);
        }
    }

    @Override
    public final Field<Integer> charLength() {
        switch (getDialect()) {
            case ORACLE:
            case DB2:
            case SQLITE:
                return new IntegerFunction(getConfiguration(), "length", this);
        }

        return new IntegerFunction(getConfiguration(), "char_length", this);
    }

    @Override
    public final Field<Integer> bitLength() {
        switch (getDialect()) {
            case DB2:
            case SQLITE:
                return new IntegerFunction(getConfiguration(), "8 * length", this);
            case ORACLE:
                return new IntegerFunction(getConfiguration(), "8 * lengthb", this);
        }

        return new IntegerFunction(getConfiguration(), "bit_length", this);
    }

    @Override
    public final Field<Integer> octetLength() {
        switch (getDialect()) {
            case DB2:
            case SQLITE:
                return new IntegerFunction(getConfiguration(), "length", this);
            case ORACLE:
                return new IntegerFunction(getConfiguration(), "lengthb", this);
        }

        return new IntegerFunction(getConfiguration(), "octet_length", this);
    }

    @Override
    public final Field<Integer> extract(DatePart datePart) throws SQLDialectNotSupportedException {
        switch (getDialect()) {
            case MYSQL:    // No break
            case POSTGRES: // No break
            case HSQLDB:   // No break
            case H2:
                return new Extract(getConfiguration(), this, datePart);
            case SQLITE:
                switch (datePart) {
                    case YEAR:
                        return new IntegerFunction(getConfiguration(), "strftime", constant("%Y"), this);
                    case MONTH:
                        return new IntegerFunction(getConfiguration(), "strftime", constant("%m"), this);
                    case DAY:
                        return new IntegerFunction(getConfiguration(), "strftime", constant("%d"), this);
                    case HOUR:
                        return new IntegerFunction(getConfiguration(), "strftime", constant("%H"), this);
                    case MINUTE:
                        return new IntegerFunction(getConfiguration(), "strftime", constant("%M"), this);
                    case SECOND:
                        return new IntegerFunction(getConfiguration(), "strftime", constant("%S"), this);
                    default:
                        throw new SQLDialectNotSupportedException("DatePart not supported: " + datePart);
                }
            case DB2:
                switch (datePart) {
                    case YEAR:
                        return new IntegerFunction(getConfiguration(), "year", this);
                    case MONTH:
                        return new IntegerFunction(getConfiguration(), "month", this);
                    case DAY:
                        return new IntegerFunction(getConfiguration(), "day", this);
                    case HOUR:
                        return new IntegerFunction(getConfiguration(), "hour", this);
                    case MINUTE:
                        return new IntegerFunction(getConfiguration(), "minute", this);
                    case SECOND:
                        return new IntegerFunction(getConfiguration(), "second", this);
                    default:
                        throw new SQLDialectNotSupportedException("DatePart not supported: " + datePart);
                }
            case ORACLE:
                switch (datePart) {
                    case YEAR:
                        return new IntegerFunction(getConfiguration(), "to_char", this, constant("YYYY"));
                    case MONTH:
                        return new IntegerFunction(getConfiguration(), "to_char", this, constant("MM"));
                    case DAY:
                        return new IntegerFunction(getConfiguration(), "to_char", this, constant("DD"));
                    case HOUR:
                        return new IntegerFunction(getConfiguration(), "to_char", this, constant("HH24"));
                    case MINUTE:
                        return new IntegerFunction(getConfiguration(), "to_char", this, constant("MI"));
                    case SECOND:
                        return new IntegerFunction(getConfiguration(), "to_char", this, constant("SS"));
                    default:
                        throw new SQLDialectNotSupportedException("DatePart not supported: " + datePart);
                }
            case MSSQL:
                throw new SQLDialectNotSupportedException("TODO: Implement CONVERT for MSSQL");

            default:
                throw new SQLDialectNotSupportedException("extract not supported");
        }
    }

    // ------------------------------------------------------------------------
    // Conditions created from this field
    // ------------------------------------------------------------------------

    @Override
    public final Condition isNull() {
        return create().nullCondition(this);
    }

    @Override
    public final Condition isNotNull() {
        return create().notNullCondition(this);
    }

    @Override
    public final Condition like(T value) {
        return create().compareCondition(this, value, Comparator.LIKE);
    }

    @Override
    public final Condition notLike(T value) {
        return create().compareCondition(this, value, Comparator.NOT_LIKE);
    }

    @Override
    public final Condition in(T... values) {
        return create().inCondition(this, values);
    }

    @Override
    public final Condition in(Collection<T> values) {
        return create().inCondition(this, values);
    }

    @Override
    public final Condition in(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(
            getConfiguration(), query, this, SubQueryOperator.IN);
    }

    @Override
    public final Condition notIn(T... values) {
        return create().notInCondition(this, values);
    }

    @Override
    public final Condition notIn(Collection<T> values) {
        return create().notInCondition(this, values);
    }

    @Override
    public final Condition notIn(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(
            getConfiguration(), query, this, SubQueryOperator.NOT_IN);
    }

    @Override
    public final Condition between(T minValue, T maxValue) {
        return create().betweenCondition(this, minValue, maxValue);
    }

    @Override
    public final Condition equal(T value) {
        return create().compareCondition(this, value);
    }

    @Override
    public final Condition equal(Field<T> field) {
        return create().joinCondition(this, field);
    }

    @Override
    public final Condition equal(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.EQUALS);
    }

    @Override
    public final Condition equalAny(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.EQUALS_ANY);
    }

    @Override
    public final Condition equalSome(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.EQUALS_SOME);
    }

    @Override
    public final Condition equalAll(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.EQUALS_ALL);
    }

    @Override
    public final Condition notEqual(T value) {
        return create().compareCondition(this, value, Comparator.NOT_EQUALS);
    }

    @Override
    public final Condition notEqual(Field<T> field) {
        return create().joinCondition(this, field, Comparator.NOT_EQUALS);
    }

    @Override
    public final Condition notEqual(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.NOT_EQUALS);
    }

    @Override
    public final Condition notEqualAny(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.NOT_EQUALS_ALL);
    }

    @Override
    public final Condition notEqualSome(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.NOT_EQUALS_SOME);
    }

    @Override
    public final Condition notEqualAll(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.NOT_EQUALS_ALL);
    }

    @Override
    public final Condition lessThan(T value) {
        return create().compareCondition(this, value, Comparator.LESS);
    }

    @Override
    public final Condition lessThan(Field<T> field) {
        return create().joinCondition(this, field, Comparator.LESS);
    }

    @Override
    public final Condition lessThan(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.LESS);
    }

    @Override
    public final Condition lessThanAny(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.LESS_THAN_ANY);
    }

    @Override
    public final Condition lessThanSome(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.LESS_THAN_SOME);
    }

    @Override
    public final Condition lessThanAll(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.LESS_THAN_ALL);
    }

    @Override
    public final Condition lessOrEqual(T value) {
        return create().compareCondition(this, value, Comparator.LESS_OR_EQUAL);
    }

    @Override
    public final Condition lessOrEqual(Field<T> field) {
        return create().joinCondition(this, field, Comparator.LESS_OR_EQUAL);
    }

    @Override
    public final Condition lessOrEqual(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.LESS_OR_EQUAL);
    }

    @Override
    public final Condition lessOrEqualToAny(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.LESS_OR_EQUAL_TO_ANY);
    }

    @Override
    public final Condition lessOrEqualToSome(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.LESS_OR_EQUAL_TO_SOME);
    }

    @Override
    public final Condition lessOrEqualToAll(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.LESS_OR_EQUAL_TO_ALL);
    }

    @Override
    public final Condition greaterThan(T value) {
        return create().compareCondition(this, value, Comparator.GREATER);
    }

    @Override
    public final Condition greaterThan(Field<T> field) {
        return create().joinCondition(this, field, Comparator.GREATER);
    }

    @Override
    public final Condition greaterThan(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.GREATER);
    }

    @Override
    public final Condition greaterThanAny(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.GREATER_THAN_ANY);
    }

    @Override
    public final Condition greaterThanSome(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.GREATER_THAN_SOME);
    }

    @Override
    public final Condition greaterThanAll(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.GREATER_THAN_ALL);
    }

    @Override
    public final Condition greaterOrEqual(T value) {
        return create().compareCondition(this, value, Comparator.GREATER_OR_EQUAL);
    }

    @Override
    public final Condition greaterOrEqual(Field<T> field) {
        return create().joinCondition(this, field, Comparator.GREATER_OR_EQUAL);
    }

    @Override
    public final Condition greaterOrEqual(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.GREATER_OR_EQUAL);
    }

    @Override
    public final Condition greaterOrEqualAny(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.GREATER_OR_EQUAL_TO_ANY);
    }

    @Override
    public final Condition greaterOrEqualSome(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.GREATER_OR_EQUAL_TO_SOME);
    }

    @Override
    public final Condition greaterOrEqualAll(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(getConfiguration(), query, this, SubQueryOperator.GREATER_OR_EQUAL_TO_ALL);
    }
}
