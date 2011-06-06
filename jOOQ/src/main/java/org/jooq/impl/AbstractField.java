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

import static org.jooq.impl.ExpressionOperator.ADD;
import static org.jooq.impl.ExpressionOperator.CONCAT;
import static org.jooq.impl.ExpressionOperator.DIVIDE;
import static org.jooq.impl.ExpressionOperator.MODULO;
import static org.jooq.impl.ExpressionOperator.MULTIPLY;
import static org.jooq.impl.ExpressionOperator.SUBTRACT;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jooq.CaseConditionStep;
import org.jooq.CaseValueStep;
import org.jooq.CaseWhenStep;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.DataType;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.SQLDialectNotSupportedException;
import org.jooq.Select;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.jooq.WindowIgnoreNullsStep;
import org.jooq.WindowPartitionByStep;

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
    public abstract int bindReference(Configuration configuration, PreparedStatement stmt, int initialIndex) throws SQLException;

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

    @SuppressWarnings("unchecked")
    @Override
    public final <Z> Field<Z> cast(DataType<Z> type) {

        // [#473] Prevent unnecessary casts
        if (getDataType().equals(type)) {
            return (Field<Z>) this;
        }
        else {
            return new Cast<Z>(getConfiguration(), this, type);
        }
    }

    @Override
    public final <Z> Field<Z> cast(Class<? extends Z> type) {
        return cast(getDataType(type));
    }

    // ------------------------------------------------------------------------
    // Conversion of field into a sort field
    // ------------------------------------------------------------------------

    @Override
    public final SortField<T> asc() {
        return new SortFieldImpl<T>(getConfiguration(), this, SortOrder.ASC);
    }

    @Override
    public final SortField<T> desc() {
        return new SortFieldImpl<T>(getConfiguration(), this, SortOrder.DESC);
    }

    @Override
    public final SortField<Integer> sortAsc(List<T> sortList) {
        Map<T, Integer> map = new LinkedHashMap<T, Integer>();

        for (int i = 0; i < sortList.size(); i++) {
            map.put(sortList.get(i), i);
        }

        return sort(map);
    }

    @Override
    public final SortField<Integer> sortAsc(T... sortList) {
        return sortAsc(Arrays.asList(sortList));
    }

    @Override
    public final SortField<Integer> sortDesc(List<T> sortList) {
        Map<T, Integer> map = new LinkedHashMap<T, Integer>();

        for (int i = 0; i < sortList.size(); i++) {
            map.put(sortList.get(i), -i);
        }

        return sort(map);
    }

    @Override
    public final SortField<Integer> sortDesc(T... sortList) {
        return sortDesc(Arrays.asList(sortList));
    }

    @Override
    public final <Z> SortField<Z> sort(Map<T, Z> sortMap) {
        CaseValueStep<T> decode = create().decode().value(this);
        CaseWhenStep<T, Z> result = null;

        for (Entry<T, Z> entry : sortMap.entrySet()) {
            if (result == null) {
                result = decode.when(entry.getKey(), entry.getValue());
            }
            else {
                result.when(entry.getKey(), entry.getValue());
            }
        }

        if (result == null) {
            return null;
        }
        else {
            return result.asc();
        }
    }

    @Override
    @Deprecated
    public final SortField<T> ascending() {
        return asc();
    }

    @Override
    @Deprecated
    public final SortField<T> descending() {
        return desc();
    }

    // ------------------------------------------------------------------------
    // Arithmetic expressions
    // ------------------------------------------------------------------------

    @Override
    public final Field<T> neg() {
        return new Neg<T>(getConfiguration(), this);
    }

    @Override
    public final Field<T> add(Number value) {

        // Date time arithmetic
        if (java.util.Date.class.isAssignableFrom(getType())) {
            switch (getDialect()) {
                case DB2:
                case HSQLDB:
                    return add(create().plainSQLField("? day", BigDecimal.class, value));

                case DERBY:
                    return new FnPrefixFunction<T>(getConfiguration(), "timestampadd", getDataType(),
                        create().plainSQLField("SQL_TSI_DAY"),
                        constant(value.intValue()),
                        this);

                case INGRES:
                    return add(create().plainSQLField("date('" + value + " days')", BigDecimal.class));

                case MYSQL:
                    return new Function<T>(getConfiguration(), "timestampadd", getDataType(),
                        create().plainSQLField("day"),
                        constant(value),
                        this);

                case POSTGRES:
                    return add(create().plainSQLField("interval '" + value + " days'", BigDecimal.class));

                case SQLITE:
                    return new Function<T>(getConfiguration(), "datetime", getDataType(),
                        this,
                        constant("+" + value + " day"));

                default:
                    return add(constant(value));
            }
        }

        // Numeric arithmetic
        else {
            return add(constant(value));
        }
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
    public final Field<T> sub(Number value) {

        // Date time arithmetic
        if (java.util.Date.class.isAssignableFrom(getType())) {
            switch (getDialect()) {
                case DB2:
                case HSQLDB:
                    return sub(create().plainSQLField("? day", BigDecimal.class, value));

                case DERBY:
                    return new FnPrefixFunction<T>(getConfiguration(), "timestampadd", getDataType(),
                        create().plainSQLField("SQL_TSI_DAY"),
                        constant(-value.intValue()),
                        this);

                case INGRES:
                    return sub(create().plainSQLField("date('" + value + " days')", BigDecimal.class));

                case MYSQL:
                    return new Function<T>(getConfiguration(), "timestampadd", getDataType(),
                        create().plainSQLField("day"),
                        constant(BigDecimal.ZERO.subtract(new BigDecimal("" + value))),
                        this);

                case POSTGRES:
                    return sub(create().plainSQLField("interval '" + value + " days'", BigDecimal.class));

                case SQLITE:
                    return new Function<T>(getConfiguration(), "datetime", getDataType(),
                        this,
                        constant("-" + value + " day"));

                default:
                    return sub(constant(value));
            }
        }

        // Numeric arithmetic
        else {
            return sub(constant(value));
        }
    }

    @Override
    public final Field<T> sub(Field<? extends Number> value) {
        return new Expression<T>(getConfiguration(), SUBTRACT, this, value);
    }

    @Override
    @Deprecated
    public final Field<T> subtract(Number value) {
        return sub(value);
    }

    @Override
    @Deprecated
    public final Field<T> subtract(Field<? extends Number> value) {
        return sub(value);
    }

    @Override
    public final Field<T> mul(Number value) {
        return mul(constant(value));
    }

    /**
     * This default implementation is known to be overridden by
     * {@link Expression} to generate neater expressions
     */
    @Override
    public Field<T> mul(Field<? extends Number> value) {
        return new Expression<T>(getConfiguration(), MULTIPLY, this, value);
    }

    @Override
    @Deprecated
    public final Field<T> multiply(Number value) {
        return mul(value);
    }

    @Override
    @Deprecated
    public final Field<T> multiply(Field<? extends Number> value) {
        return mul(value);
    }

    @Override
    public final Field<T> div(Number value) {
        return div(constant(value));
    }

    @Override
    public final Field<T> div(Field<? extends Number> value) {
        return new Expression<T>(getConfiguration(), DIVIDE, this, value);
    }

    @Override
    @Deprecated
    public final Field<T> divide(Number value) {
        return div(value);
    }

    @Override
    @Deprecated
    public final Field<T> divide(Field<? extends Number> value) {
        return div(value);
    }

    @Override
    public final Field<T> mod(Number value) {
        return mod(constant(value));
    }

    @Override
    public final Field<T> mod(Field<? extends Number> value) {
        switch (getDialect()) {
            case SQLITE:
            case SQLSERVER:
                return new Expression<T>(getConfiguration(), MODULO, this, value);
        }

        return new Function<T>(getConfiguration(), "mod", getDataType(), this, value);
    }

    // ------------------------------------------------------------------------
    // Window functions created from this field
    // ------------------------------------------------------------------------

    @Override
    public final WindowPartitionByStep<Integer> countOver() {
        return new WindowFunction<Integer>(
            getConfiguration(), "count", create().getDataType(Integer.class), this);
    }

    @Override
    public final WindowPartitionByStep<T> maxOver() {
        return new WindowFunction<T>(getConfiguration(), "max", getDataType(), this);
    }

    @Override
    public final WindowPartitionByStep<T> minOver() {
        return new WindowFunction<T>(getConfiguration(), "min", getDataType(), this);
    }

    @Override
    public final WindowPartitionByStep<BigDecimal> sumOver() {
        return new WindowFunction<BigDecimal>(
            getConfiguration(), "sum", create().getDataType(BigDecimal.class), this);
    }

    @Override
    public final WindowPartitionByStep<BigDecimal> avgOver() {
        return new WindowFunction<BigDecimal>(
            getConfiguration(), "avg", create().getDataType(BigDecimal.class), this);
    }

    @Override
    public final WindowIgnoreNullsStep<T> firstValue() {
        return new WindowFunction<T>(getConfiguration(), "first_value", getDataType(), this);
    }

    @Override
    public final WindowIgnoreNullsStep<T> lastValue() {
        return new WindowFunction<T>(getConfiguration(), "last_value", getDataType(), this);
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
        return new Function<BigDecimal>(getConfiguration(), "sum", getDataType(BigDecimal.class), this);
    }

    @Override
    public final Field<BigDecimal> avg() {
        return new Function<BigDecimal>(getConfiguration(), "avg", getDataType(BigDecimal.class), this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Field<Integer> sign() {
        switch (getDialect()) {
            case SQLITE:
                T zero = (T) Integer.valueOf(0);

                return create().decode()
                    .when(greaterThan(zero), constant(1))
                    .when(lessThan(zero), constant(-1))
                    .otherwise(0);

            default:
                return new IntegerFunction(getConfiguration(), "sign", this);
        }
    }

    @Override
    public final Field<T> abs() {
        return new Function<T>(getConfiguration(), "abs", getDataType(), this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Field<T> round() {
        switch (getDialect()) {

            // evaluate "round" if unavailable
            case DERBY:
                return create().decode().when(
                    sub((Field<? extends Number>) floor()).lessThan((T) Double.valueOf(0.5)), floor()).otherwise(ceil());

            case H2:        // No break
            case INGRES:    // No break
            case SQLSERVER: // No break
            case SYBASE:
                return round(0);

            default:
                return new Function<T>(getConfiguration(), "round", getDataType(), this);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Field<T> round(int decimals) {
        switch (getDialect()) {

            // evaluate "round" if unavailable
            case DERBY: // No break
                BigDecimal factor = BigDecimal.ONE.movePointRight(decimals);

                return create().decode()
                    .when(mul(factor).sub((Field<? extends Number>) mul(factor).floor())
                    .lessThan((T) Double.valueOf(0.5)),
                        mul(factor).floor().div(factor))
                    .otherwise(
                        mul(factor).ceil().div(factor));

            // There's no function round(double precision, integer) in Postgres
            case POSTGRES:
                return new Function<T>(getConfiguration(), "round", getDataType(), cast(BigDecimal.class), constant(decimals));

            default:
                return new Function<T>(getConfiguration(), "round", getDataType(), this, constant(decimals));
        }
    }

    @Override
    public final Field<T> floor() {
        switch (getDialect()) {

            // evaluate "floor" if unavailable
            case SQLITE:
                return sub(0.5).round();

            default:
                return new Function<T>(getConfiguration(), "floor", getDataType(), this);
        }
    }

    @Override
    public final Field<T> ceil() {
        switch (getDialect()) {

            // evaluate "floor" if unavailable
            case SQLITE:
                return add(0.5).round();

            case H2: // No break
            case SQLSERVER:
                return new Function<T>(getConfiguration(), "ceiling", getDataType(), this);

            default:
                return new Function<T>(getConfiguration(), "ceil", getDataType(), this);
        }
    }

    // ------------------------------------------------------------------------
    // Mathematical functions created from this field
    // ------------------------------------------------------------------------

    @Override
    public final Field<T> greatest(T... others) {
        return greatest(constants(others).toArray(new Field<?>[0]));
    }

    @Override
    public final Field<T> greatest(Field<?>... others) {
        Field<?>[] combined = JooqUtil.combine(this, others);

        switch (getDialect()) {
            case DERBY:     // No break
            case SQLSERVER: // No break
            case SYBASE: {
                @SuppressWarnings("unchecked")
                Field<T> other = (Field<T>) others[0];

                if (others.length > 1) {
                    Field<?>[] remaining = new Field<?>[others.length - 1];
                    System.arraycopy(others, 1, remaining, 0, remaining.length);

                    return create().decode()
                        .when(greaterThan(other), greatest(remaining))
                        .otherwise(other.greatest(remaining));
                }
                else {
                    return create().decode()
                        .when(greaterThan(other), this)
                        .otherwise(other);
                }
            }

            case SQLITE:
                return new Function<T>(getConfiguration(), "max", getDataType(), combined);

            default:
                return new Function<T>(getConfiguration(), "greatest", getDataType(), combined);
        }
    }

    @Override
    public final Field<T> least(T... others) {
        return least(constants(others).toArray(new Field<?>[0]));
    }

    @Override
    public final Field<T> least(Field<?>... others) {
        Field<?>[] combined = JooqUtil.combine(this, others);

        switch (getDialect()) {
            case DERBY:     // No break
            case SQLSERVER: // No break
            case SYBASE: {
                @SuppressWarnings("unchecked")
                Field<T> other = (Field<T>) others[0];

                if (others.length > 1) {
                    Field<?>[] remaining = new Field<?>[others.length - 1];
                    System.arraycopy(others, 1, remaining, 0, remaining.length);

                    return create().decode()
                        .when(lessThan(other), least(remaining))
                        .otherwise(other.least(remaining));
                }
                else {
                    return create().decode()
                        .when(lessThan(other), this)
                        .otherwise(other);
                }
            }

            case SQLITE:
                return new Function<T>(getConfiguration(), "min", getDataType(), combined);

            default:
                return new Function<T>(getConfiguration(), "least", getDataType(), combined);
        }
    }

    @Override
    public final Field<BigDecimal> sqrt() {
        switch (getDialect()) {
            case SQLITE:
                return power(0.5);

            default:
                return new Function<BigDecimal>(getConfiguration(), "sqrt", getDataType(BigDecimal.class), this);
        }
    }

    @Override
    public final Field<BigDecimal> exp() {
        return new Function<BigDecimal>(getConfiguration(), "exp", getDataType(BigDecimal.class), this);
    }

    @Override
    public final Field<BigDecimal> ln() {
        switch (getDialect()) {
            case H2:
            case SQLSERVER:
                return new Function<BigDecimal>(getConfiguration(), "log", getDataType(BigDecimal.class), this);

            default:
                return new Function<BigDecimal>(getConfiguration(), "ln", getDataType(BigDecimal.class), this);
        }
    }

    @Override
    public final Field<BigDecimal> log(int base) {
        switch (getDialect()) {
            case DB2:       // No break
            case DERBY:     // No break
            case H2:        // No break
            case HSQLDB:    // No break
            case INGRES:    // No break
            case SQLSERVER: // No break
            case SYBASE:
                return ln().div(constant(base).ln());

            default:
                return new Function<BigDecimal>(getConfiguration(), "log", getDataType(BigDecimal.class), constant(base), this);
        }
    }

    @Override
    public final Field<BigDecimal> power(Number exponent) {
        switch (getDialect()) {
            case DERBY:
            case SQLITE:
                return ln().mul(exponent).exp();

            default:
                return new Function<BigDecimal>(getConfiguration(), "power", getDataType(BigDecimal.class), this, constant(exponent));
        }
    }

    @Override
    public final Field<BigDecimal> acos() {
        return new Function<BigDecimal>(getConfiguration(), "acos", getDataType(BigDecimal.class), this);
    }

    @Override
    public final Field<BigDecimal> asin() {
        return new Function<BigDecimal>(getConfiguration(), "asin", getDataType(BigDecimal.class), this);
    }

    @Override
    public final Field<BigDecimal> atan() {
        return new Function<BigDecimal>(getConfiguration(), "atan", getDataType(BigDecimal.class), this);
    }

    @Override
    public final Field<BigDecimal> atan2(Number y) {
        return atan2(constant(y));
    }

    @Override
    public final Field<BigDecimal> atan2(Field<? extends Number> y) {
        if (y == null) {
            return atan2((Number) null);
        }

        switch (getDialect()) {
            case SQLSERVER:
                return new Function<BigDecimal>(
                    getConfiguration(), "atn2", getDataType(BigDecimal.class), this, y);

            default:
                return new Function<BigDecimal>(
                    getConfiguration(), "atan2", getDataType(BigDecimal.class), this, y);
        }
    }

    @Override
    public final Field<BigDecimal> cos() {
        return new Function<BigDecimal>(getConfiguration(), "cos", getDataType(BigDecimal.class), this);
    }

    @Override
    public final Field<BigDecimal> sin() {
        return new Function<BigDecimal>(getConfiguration(), "sin", getDataType(BigDecimal.class), this);
    }

    @Override
    public final Field<BigDecimal> tan() {
        return new Function<BigDecimal>(getConfiguration(), "tan", getDataType(BigDecimal.class), this);
    }

    @Override
    public final Field<BigDecimal> cot() {
        switch (getDialect()) {
            case INGRES:
            case ORACLE:
                return cos().div(sin());

            default:
                return new Function<BigDecimal>(getConfiguration(), "cot", getDataType(BigDecimal.class), this);
        }
    }

    @Override
    public final Field<BigDecimal> sinh() {
        switch (getDialect()) {
            case HSQLDB:
            case INGRES:
            case MYSQL:
            case POSTGRES:
            case SQLSERVER:
            case SYBASE:
                return mul(2).exp().sub(1).div(exp().mul(2));

            default:
                return new Function<BigDecimal>(getConfiguration(), "sinh", getDataType(BigDecimal.class), this);
        }
    }

    @Override
    public final Field<BigDecimal> cosh() {
        switch (getDialect()) {
            case HSQLDB:
            case INGRES:
            case MYSQL:
            case POSTGRES:
            case SQLSERVER:
            case SYBASE:
                return mul(2).exp().add(1).div(exp().mul(2));

            default:
                return new Function<BigDecimal>(getConfiguration(), "cosh", getDataType(BigDecimal.class), this);
        }
    }

    @Override
    public final Field<BigDecimal> tanh() {
        switch (getDialect()) {
            case HSQLDB:
            case INGRES:
            case MYSQL:
            case POSTGRES:
            case SQLSERVER:
            case SYBASE:
                return mul(2).exp().sub(1).div(mul(2).exp().add(1));

            default:
                return new Function<BigDecimal>(getConfiguration(), "tanh", getDataType(BigDecimal.class), this);
        }
    }

    @Override
    public final Field<BigDecimal> coth() {
        return mul(2).exp().add(1).div(mul(2).exp().sub(1));
    }

    @Override
    public final Field<BigDecimal> deg() {
        switch (getDialect()) {
            case INGRES:
            case ORACLE:
                return cast(BigDecimal.class).mul(180).div(Math.PI);

            default:
                return new Function<BigDecimal>(getConfiguration(), "degrees", getDataType(BigDecimal.class), this);
        }
    }

    @Override
    public final Field<BigDecimal> rad() {
        switch (getDialect()) {
            case INGRES:
            case ORACLE:
                return cast(BigDecimal.class).mul(Math.PI).div(180);

            default:
                return new Function<BigDecimal>(getConfiguration(), "radians", getDataType(BigDecimal.class), this);
        }
    }

    // ------------------------------------------------------------------------
    // Other functions created from this field
    // ------------------------------------------------------------------------

    @Override
    public final Field<String> upper() {
        return new Function<String>(getConfiguration(), "upper", getDataType(String.class), this);
    }

    @Override
    public final Field<String> lower() {
        return new Function<String>(getConfiguration(), "lower", getDataType(String.class), this);
    }

    @Override
    public final Field<String> trim() {
        switch (getDialect()) {
            case SQLSERVER:
                return rtrim().ltrim();

            default:
                return new Function<String>(getConfiguration(), "trim", getDataType(String.class), this);
        }
    }

    @Override
    public final Field<String> rtrim() {
        return new Function<String>(getConfiguration(), "rtrim", getDataType(String.class), this);
    }

    @Override
    public final Field<String> ltrim() {
        return new Function<String>(getConfiguration(), "ltrim", getDataType(String.class), this);
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
            case INGRES:   // No break
            case SYBASE:
                return new IntegerFunction(getConfiguration(), "locate", this, search);
            case ORACLE:
                return new IntegerFunction(getConfiguration(), "instr", this, search);
            case SQLSERVER:
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
    @Override
    public Field<String> concat(Field<?>... fields) {

        // [#461] Type cast the concat expression, if this isn't a VARCHAR field
        Field<String> castThis = create().castAll(String.class, this)[0];
        Field<String>[] castFields = create().castAll(String.class, fields);

        switch (getDialect()) {
            case MYSQL: {
                List<Field<?>> list = new ArrayList<Field<?>>();
                list.add(castThis);
                list.addAll(Arrays.asList(castFields));

                return new StringFunction(getConfiguration(), "concat", list.toArray(new Field<?>[0]));
            }

            case SQLSERVER: {
                return new Expression<String>(getConfiguration(), ADD, castThis, castFields);
            }

            default: {
                return new Expression<String>(getConfiguration(), CONCAT, castThis, castFields);
            }
        }
    }

    @Override
    public final Field<String> concat(String... values) {
        return concat(create().constants((Object[]) values).toArray(new Field[0]));
    }

    @Override
    public final Field<String> concatenate(Field<String> field, Field<String>... fields) {
        List<Field<?>> list = new ArrayList<Field<?>>();
        list.add(field);
        list.addAll(Arrays.asList(fields));
        return concat(list.toArray(new Field<?>[0]));
    }

    @Override
    public final Field<String> concatenate(String value, String... values) {
        List<String> list = new ArrayList<String>();
        list.add(value);
        list.addAll(Arrays.asList(values));
        return concat(list.toArray(new String[0]));
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
            case DB2:    // No break
            case DERBY:  // No break
            case INGRES: // No break
            case ORACLE: // No break
            case SQLITE: // No break
            case SYBASE:
                return new IntegerFunction(getConfiguration(), "length", this);

            case SQLSERVER:
                return new IntegerFunction(getConfiguration(), "len", this);
        }

        return new IntegerFunction(getConfiguration(), "char_length", this);
    }

    @Override
    public final Field<Integer> bitLength() {
        switch (getDialect()) {
            case DB2:    // No break
            case DERBY:  // No break
            case INGRES: // No break
            case SQLITE: // No break
            case SYBASE:
                return new IntegerFunction(getConfiguration(), "8 * length", this);

            case SQLSERVER:
                return new IntegerFunction(getConfiguration(), "8 * len", this);

            case ORACLE:
                return new IntegerFunction(getConfiguration(), "8 * lengthb", this);
        }

        return new IntegerFunction(getConfiguration(), "bit_length", this);
    }

    @Override
    public final Field<Integer> octetLength() {
        switch (getDialect()) {
            case DB2:    // No break
            case DERBY:  // No break
            case INGRES: // No break
            case SQLITE: // No break
            case SYBASE:
                return new IntegerFunction(getConfiguration(), "length", this);
            case SQLSERVER:
                return new IntegerFunction(getConfiguration(), "len", this);
            case ORACLE:
                return new IntegerFunction(getConfiguration(), "lengthb", this);
        }

        return new IntegerFunction(getConfiguration(), "octet_length", this);
    }

    @Override
    public final Field<Integer> extract(DatePart datePart) throws SQLDialectNotSupportedException {
        switch (getDialect()) {
            case INGRES:   // No break
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

            case SQLSERVER:
            case SYBASE:
                switch (datePart) {
                    case YEAR:
                        return new IntegerFunction(getConfiguration(), "datepart", create().plainSQLField("yy"), this);
                    case MONTH:
                        return new IntegerFunction(getConfiguration(), "datepart", create().plainSQLField("mm"), this);
                    case DAY:
                        return new IntegerFunction(getConfiguration(), "datepart", create().plainSQLField("dd"), this);
                    case HOUR:
                        return new IntegerFunction(getConfiguration(), "datepart", create().plainSQLField("hh"), this);
                    case MINUTE:
                        return new IntegerFunction(getConfiguration(), "datepart", create().plainSQLField("mi"), this);
                    case SECOND:
                        return new IntegerFunction(getConfiguration(), "datepart", create().plainSQLField("ss"), this);
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
            case INGRES:   // No break
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
    public final <Z> Field<Z> nvl2(Field<Z> valueIfNotNull, Field<Z> valueIfNull) {
        if (valueIfNotNull == null || valueIfNull == null) {
            return nvl2(constant((Z) valueIfNotNull), constant((Z) valueIfNull));
        }

        switch (getDialect()) {
            case INGRES:
            case ORACLE:
                return new Function<Z>(getConfiguration(),
                    "nvl2", valueIfNotNull.getDataType(), this, valueIfNotNull, valueIfNull);

            default:
                return create().decode().when(isNotNull(), valueIfNotNull).otherwise(valueIfNull);
        }
    }

    @Override
    public final <Z> Field<Z> nvl2(Z valueIfNotNull, Z valueIfNull) {
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
            constants(more).toArray(new Field<?>[0]));
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
        return coalesce(constant(option), constants(options).toArray(new Field<?>[0]));
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
        return in(constants(values).toArray(new Field<?>[0]));
    }

    @Override
    public final Condition in(Field<?>... values) {
        if (values == null || values.length == 0) {
            return create().falseCondition();
        }
        else {
            return new InCondition<T>(getConfiguration(), this, values, InOperator.IN);
        }
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
        if (values == null || values.length == 0) {
            return create().trueCondition();
        }
        else {
            return notIn(constants(values).toArray(new Field<?>[0]));
        }
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
