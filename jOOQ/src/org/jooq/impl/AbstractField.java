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

import static org.jooq.impl.ExpressionOperator.ADD;
import static org.jooq.impl.ExpressionOperator.CONCAT;
import static org.jooq.impl.ExpressionOperator.DIVIDE;
import static org.jooq.impl.ExpressionOperator.MULTIPLY;
import static org.jooq.impl.ExpressionOperator.SUBTRACT;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jooq.CaseConditionStep;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.DataType;
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

    AbstractField(Configuration configuration, String name, DataType<T> type) {
        super(configuration, name, type);
    }

    // ------------------------------------------------------------------------
    // API (not implemented)
    // ------------------------------------------------------------------------

    @Override
    public abstract String toSQLReference(Configuration configuration, boolean inlineParameters);

    @Override
    public abstract int bind(Configuration configuration, PreparedStatement stmt, int initialIndex) throws SQLException;

    @Override
    public abstract boolean isNullLiteral();

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    @Override
    public Field<T> as(String alias) {
        return new FieldAlias<T>(getConfiguration(), this, alias);
    }

    // ------------------------------------------------------------------------
    // Type casts
    // ------------------------------------------------------------------------

    @Override
    public final <Z> Field<Z> cast(Field<Z> field) {
        return cast(field.getDataType());
    }

    @Override
    public final <Z> Field<Z> cast(DataType<Z> type) {
        return new Cast<Z>(getConfiguration(), this, type);
    }

    @Override
    public final <Z> Field<Z> cast(Class<? extends Z> type) {
        return cast(FieldTypeHelper.getDataType(getDialect(), type));
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
     * {@link Expression} to generate neater expressions
     */
    @Override
    public Field<T> add(Field<? extends Number> value) {
        return new Expression<T>(getConfiguration(), ADD, this, value);
    }

    @Override
    public final Field<T> subtract(Number value) {
        return subtract(constant(value));
    }

    @Override
    public final Field<T> subtract(Field<? extends Number> value) {
        return new Expression<T>(getConfiguration(), SUBTRACT, this, value);
    }

    @Override
    public final Field<T> multiply(Number value) {
        return multiply(constant(value));
    }

    /**
     * This default implementation is known to be overridden by
     * {@link Expression} to generate neater expressions
     */
    @Override
    public Field<T> multiply(Field<? extends Number> value) {
        return new Expression<T>(getConfiguration(), MULTIPLY, this, value);
    }

    @Override
    public final Field<T> divide(Number value) {
        return divide(constant(value));
    }

    @Override
    public final Field<T> divide(Field<? extends Number> value) {
        return new Expression<T>(getConfiguration(), DIVIDE, this, value);
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
        return new Function<T>(getConfiguration(), "max", getDataType(), this);
    }

    @Override
    public final Field<T> min() {
        return new Function<T>(getConfiguration(), "min", getDataType(), this);
    }

    @Override
    public final Field<BigDecimal> sum() {
        return new Function<BigDecimal>(getConfiguration(), "sum", FieldTypeHelper.getDataType(getDialect(), BigDecimal.class), this);
    }

    @Override
    public final Field<BigDecimal> avg() {
        return new Function<BigDecimal>(getConfiguration(), "avg", FieldTypeHelper.getDataType(getDialect(), BigDecimal.class), this);
    }

    @Override
    public final Field<T> abs() {
        return new Function<T>(getConfiguration(), "abs", getDataType(), this);
    }

    @Override
    public final Field<T> round() {
        return new Function<T>(getConfiguration(), "round", getDataType(), this);
    }

    @Override
    public final Field<String> upper() {
        return new Function<String>(getConfiguration(), "upper", FieldTypeHelper.getDataType(getDialect(), String.class), this);
    }

    @Override
    public final Field<String> lower() {
        return new Function<String>(getConfiguration(), "lower", FieldTypeHelper.getDataType(getDialect(), String.class), this);
    }

    @Override
    public final Field<String> trim() {
        return new Function<String>(getConfiguration(), "trim", FieldTypeHelper.getDataType(getDialect(), String.class), this);
    }

    @Override
    public final Field<String> rtrim() {
        return new Function<String>(getConfiguration(), "rtrim", FieldTypeHelper.getDataType(getDialect(), String.class), this);
    }

    @Override
    public final Field<String> ltrim() {
        return new Function<String>(getConfiguration(), "ltrim", FieldTypeHelper.getDataType(getDialect(), String.class), this);
    }

    @Override
    public final Field<String> rpad(Field<? extends Number> length) {
        return new StringFunction(getConfiguration(), "rpad", this, length);
    }

    @Override
    public final Field<String> rpad(int length) {
        return rpad(constant(length));
    }

    @Override
    public final Field<String> rpad(Field<? extends Number> length, Field<String> c) {
        return new StringFunction(getConfiguration(), "rpad", this, length, c);
    }

    @Override
    public final Field<String> rpad(int length, char c) {
        return rpad(constant(length), constant("" + c));
    }

    @Override
    public final Field<String> lpad(Field<? extends Number> length) {
        return new StringFunction(getConfiguration(), "lpad", this, length);
    }

    @Override
    public final Field<String> lpad(int length) {
        return lpad(constant(length));
    }

    @Override
    public final Field<String> lpad(Field<? extends Number> length, Field<String> c) {
        return new StringFunction(getConfiguration(), "lpad", this, length, c);
    }

    @Override
    public final Field<String> lpad(int length, char c) {
        return lpad(constant(length), constant("" + c));
    }

    @Override
    public final Field<String> replace(Field<String> search) {
        return new StringFunction(getConfiguration(), "replace", this, search);
    }

    @Override
    public final Field<String> replace(String search) {
        return replace(constant(search));
    }

    @Override
    public final Field<String> replace(Field<String> search, Field<String> replace) {
        return new StringFunction(getConfiguration(), "replace", this, search, replace);
    }

    @Override
    public final Field<String> replace(String search, String replace) {
        return replace(constant(search), constant(replace));
    }

    @Override
    public final Field<Integer> position(String search) throws SQLDialectNotSupportedException {
        return position(constant(search));
    }

    @SuppressWarnings("deprecation")
    @Override
    public final Field<Integer> position(Field<String> search) throws SQLDialectNotSupportedException {
        switch (getDialect()) {
            case MYSQL:    // No break
            case POSTGRES: // No break
            case HSQLDB:   // No break
            case H2:
            	return new PositionFunctionImpl(getConfiguration(), search, this);
            case DB2:      // No break
            case DERBY:
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

    /**
     * This default implementation is known to be overridden by
     * {@link Expression} to generate neater expressions
     */
    @SuppressWarnings("unchecked")
    @Override
    public Field<String> concatenate(Field<String> field, Field<String>... fields) {
        switch (getDialect()) {
            case MYSQL: {
                List<Field<?>> all = new ArrayList<Field<?>>();
                all.add(this);
                all.add(field);
                all.addAll(Arrays.asList(fields));
                return new StringFunction(getConfiguration(), "concat", all.toArray(new Field[0]));
            }
        }

        return (Field<String>) new Expression<T>(getConfiguration(), CONCAT, this, field, fields);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Field<String> concatenate(String value, String... values) {
        return concatenate(
            create().constant(value),
            create().constant((Object[]) values).toArray(new Field[0]));
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
            case ORACLE: // No break
            case DERBY:
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
            case ORACLE: // No break
            case DB2:    // No break
            case SQLITE: // No break
            case DERBY:
                return new IntegerFunction(getConfiguration(), "length", this);
        }

        return new IntegerFunction(getConfiguration(), "char_length", this);
    }

    @Override
    public final Field<Integer> bitLength() {
        switch (getDialect()) {
            case DB2:    // No break
            case SQLITE: // No break
            case DERBY:  // No break
                return new IntegerFunction(getConfiguration(), "8 * length", this);

            case ORACLE:
                return new IntegerFunction(getConfiguration(), "8 * lengthb", this);
        }

        return new IntegerFunction(getConfiguration(), "bit_length", this);
    }

    @Override
    public final Field<Integer> octetLength() {
        switch (getDialect()) {
            case DB2:    // No break
            case SQLITE: // No break
            case DERBY:  // No break
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
            case DERBY:    // No break
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

            default:
                throw new SQLDialectNotSupportedException("extract not supported");
        }
    }

    @Override
    public final Field<T> nvl(Field<T> defaultValue) {
        if (defaultValue == null) {
            return nvl((T) null);
        }

        switch (getDialect()) {
            case DB2:      // No break
            case HSQLDB:   // No break
            case ORACLE:
                return new Function<T>(getConfiguration(), "nvl", getDataType(), this, defaultValue);

            case DERBY:    // No break
            case POSTGRES:
                return new Function<T>(getConfiguration(), "coalesce", getDataType(), this, defaultValue);

            case H2:       // No break
            case MYSQL:    // No break
            case SQLITE:   // No break
                return new Function<T>(getConfiguration(), "ifnull", getDataType(), this, defaultValue);

            default:
                return create().decode().when(isNotNull(), this).otherwise(defaultValue);
        }
    }

    @Override
    public final Field<T> nvl(T defaultValue) {
        return nvl(constant(defaultValue));
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Field<T> nvl2(Field<T> valueIfNotNull, Field<T> valueIfNull) {
        if (valueIfNotNull == null || valueIfNull == null) {
            return nvl2(constant((T) valueIfNotNull), constant((T) valueIfNull));
        }

        switch (getDialect()) {
            case ORACLE:
                return new Function<T>(getConfiguration(), "nvl2", getDataType(), this, valueIfNotNull, valueIfNull);

            default:
                return create().decode().when(isNotNull(), valueIfNotNull).otherwise(valueIfNull);
        }
    }

    @Override
    public final Field<T> nvl2(T valueIfNotNull, T valueIfNull) {
        return nvl2(constant(valueIfNotNull), constant(valueIfNull));
    }

    @Override
    public final Field<T> nullif(T other) {
        return nullif(constant(other));
    }

    @Override
    public final Field<T> nullif(Field<T> other) {
        return new Function<T>(getConfiguration(), "nullif", getDataType(), this, other);
    }

    @Override
    public final <Z> Field<Z> decode(T search, Z result) {
        return decode(search, result, new Object[0]);
    }

    @Override
    public final <Z> Field<Z> decode(T search, Z result, Object... more) {
        return decode(
            constant(search),
            constant(result),
            constant(more).toArray(new Field<?>[0]));
    }

    @Override
    public final <Z> Field<Z> decode(Field<T> search, Field<Z> result) {
        return decode(search, result, new Field<?>[0]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <Z> Field<Z> decode(Field<T> search, Field<Z> result, Field<?>... more) {
        if (search == null || result == null) {
            return decode(constant((T) search), constant((Z) result), more);
        }

        switch (getDialect()) {
            case ORACLE: {
                Field<?>[] arguments = new Field<?>[more.length + 3];
                arguments[0] = this;
                arguments[1] = search;
                arguments[2] = result;
                System.arraycopy(more, 0, arguments, 3, more.length);
                return new Function<Z>(getConfiguration(), "decode", result.getDataType(), arguments);
            }

            default: {
                CaseConditionStep<Z> when = create().decode().when(equal(search), result);

                for (int i = 0; i < more.length; i += 2) {
                    // search/result pair
                    if (i + 1 < more.length) {
                        when = when.when(equal((Field<T>) more[i]), (Field<Z>) more[i + 1]);
                    }

                    // trailing default value
                    else {
                        return when.otherwise((Field<Z>) more[i]);
                    }
                }

                return when;
            }
        }
    }

    @Override
    public final Field<T> coalesce(T option, T... options) {
        return coalesce(constant(option), constant(options).toArray(new Field<?>[0]));
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Field<T> coalesce(Field<T> option, Field<?>... options) {
        if (option == null) {
            return coalesce(constant((T) option), options);
        }

        Field<?>[] arguments = new Field<?>[options.length + 2];
        arguments[0] = this;
        arguments[1] = option;
        System.arraycopy(options, 0, arguments, 2, options.length);
        return new Function<T>(getConfiguration(), "coalesce", getDataType(), arguments);
    }

    // ------------------------------------------------------------------------
    // Conditions created from this field
    // ------------------------------------------------------------------------

    @Override
    public final Condition isNull() {
        return equal((T) null);
    }

    @Override
    public final Condition isNotNull() {
        return notEqual((T) null);
    }

    @Override
    public final Condition like(T value) {
        return new CompareCondition<T>(getConfiguration(), this, constant(value), Comparator.LIKE);
    }

    @Override
    public final Condition notLike(T value) {
        return new CompareCondition<T>(getConfiguration(), this, constant(value), Comparator.NOT_LIKE);
    }

    @Override
    public final Condition in(T... values) {
        return in(constant(values).toArray(new Field<?>[0]));
    }

    @Override
    public final Condition in(Field<?>... values) {
        return new InCondition<T>(getConfiguration(), this, values, InOperator.IN);
    }

    @Override
    public final Condition in(Collection<T> values) {
        List<Field<?>> fields = new ArrayList<Field<?>>();

        for (T value : values) {
            fields.add(constant(value));
        }

        return in(fields.toArray(new Field<?>[0]));
    }

    @Override
    public final Condition in(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(
            getConfiguration(), query, this, SubQueryOperator.IN);
    }

    @Override
    public final Condition notIn(T... values) {
        return notIn(constant(values).toArray(new Field<?>[0]));
    }

    @Override
    public final Condition notIn(Field<?>... values) {
        return new InCondition<T>(getConfiguration(), this, values, InOperator.NOT_IN);
    }

    @Override
    public final Condition notIn(Collection<T> values) {
        List<Field<?>> fields = new ArrayList<Field<?>>();

        for (T value : values) {
            fields.add(constant(value));
        }

        return notIn(fields.toArray(new Field<?>[0]));
    }

    @Override
    public final Condition notIn(Select<?> query) {
        return new SelectQueryAsSubQueryCondition(
            getConfiguration(), query, this, SubQueryOperator.NOT_IN);
    }

    @Override
    public final Condition between(T minValue, T maxValue) {
        return between(constant(minValue), constant(maxValue));
    }

    @Override
    public final Condition between(Field<T> minValue, Field<T> maxValue) {
        return new BetweenCondition<T>(getConfiguration(), this, minValue, maxValue);
    }

    @Override
    public final Condition equal(T value) {
        return equal(constant(value));
    }

    @Override
    public final Condition equal(Field<T> field) {
        return new CompareCondition<T>(getConfiguration(), this, field, Comparator.EQUALS);
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
        return notEqual(constant(value));
    }

    @Override
    public final Condition notEqual(Field<T> field) {
        return new CompareCondition<T>(getConfiguration(), this, field, Comparator.NOT_EQUALS);
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
        return lessThan(constant(value));
    }

    @Override
    public final Condition lessThan(Field<T> field) {
        return new CompareCondition<T>(getConfiguration(), this, field, Comparator.LESS);
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
        return lessOrEqual(constant(value));
    }

    @Override
    public final Condition lessOrEqual(Field<T> field) {
        return new CompareCondition<T>(getConfiguration(), this, field, Comparator.LESS_OR_EQUAL);
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
        return greaterThan(constant(value));
    }

    @Override
    public final Condition greaterThan(Field<T> field) {
        return new CompareCondition<T>(getConfiguration(), this, field, Comparator.GREATER);
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
        return greaterOrEqual(constant(value));
    }

    @Override
    public final Condition greaterOrEqual(Field<T> field) {
        return new CompareCondition<T>(getConfiguration(), this, field, Comparator.GREATER_OR_EQUAL);
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
