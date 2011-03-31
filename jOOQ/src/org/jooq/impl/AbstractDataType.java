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

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jooq.ArrayRecord;
import org.jooq.DataType;
import org.jooq.EnumType;
import org.jooq.MasterDataType;
import org.jooq.SQLDialect;
import org.jooq.SQLDialectNotSupportedException;
import org.jooq.UDTRecord;

@SuppressWarnings("unchecked")
public abstract class AbstractDataType<T> implements DataType<T> {

    private static final Map<String, DataType<?>>[]   typesByTypeName;
    private static final Map<Class<?>, DataType<?>>[] typesByType;

    private final SQLDialect                          dialect;

    private final Class<? extends T>                  type;
    private final String                              castTypeName;
    private final String                              typeName;

    private final Class<? extends T[]>                arrayType;
    private final String                              arrayCastTypeName;
    private final String                              arrayTypeName;

    private final boolean                             hasPrecisionAndScale;

    static {
        typesByTypeName = new Map[SQLDialect.values().length];
        typesByType = new Map[SQLDialect.values().length];

        for (SQLDialect dialect : SQLDialect.values()) {
            typesByTypeName[dialect.ordinal()] = new LinkedHashMap<String, DataType<?>>();
            typesByType[dialect.ordinal()] = new LinkedHashMap<Class<?>, DataType<?>>();
        }
    }

    protected AbstractDataType(SQLDialect dialect, Class<? extends T> type, String typeName) {
        this(dialect, type, typeName, typeName, false);
    }

    protected AbstractDataType(SQLDialect dialect, Class<? extends T> type, String typeName, String castTypeName) {
        this(dialect, type, typeName, castTypeName, false);
    }

    protected AbstractDataType(SQLDialect dialect, Class<? extends T> type, String typeName, boolean hasPrecisionAndScale) {
        this(dialect, type, typeName, typeName, hasPrecisionAndScale);
    }

    protected AbstractDataType(SQLDialect dialect, Class<? extends T> type, String typeName, String castTypeName, boolean hasPrecisionAndScale) {
        this.dialect = dialect;
        this.type = type;
        this.typeName = typeName;
        this.castTypeName = castTypeName;
        this.hasPrecisionAndScale = hasPrecisionAndScale;

        this.arrayType = (Class<? extends T[]>) Array.newInstance(type, 0).getClass();
        this.arrayTypeName = FieldTypeHelper.getArrayType(typeName, dialect);
        this.arrayCastTypeName = FieldTypeHelper.getArrayType(castTypeName, dialect);

        if (typesByTypeName[dialect.ordinal()].get(FieldTypeHelper.normalise(typeName)) == null) {
            typesByTypeName[dialect.ordinal()].put(FieldTypeHelper.normalise(typeName), this);
        }

        if (typesByType[dialect.ordinal()].get(type) == null) {
            typesByType[dialect.ordinal()].put(type, this);
        }
    }

    @Override
    public int getSQLType() {
        if (type == Blob.class) {
            return Types.BLOB;
        }
        else if (type == Boolean.class) {
            return Types.BOOLEAN;
        }
        else if (type == BigInteger.class) {
            return Types.BIGINT;
        }
        else if (type == BigDecimal.class) {
            return Types.DECIMAL;
        }
        else if (type == Byte.class) {
            return Types.TINYINT;
        }
        else if (type == byte[].class) {
            return Types.BLOB;
        }
        else if (type == Clob.class) {
            return Types.CLOB;
        }
        else if (type == Date.class) {
            return Types.DATE;
        }
        else if (type == Double.class) {
            return Types.DOUBLE;
        }
        else if (type == Float.class) {
            return Types.FLOAT;
        }
        else if (type == Integer.class) {
            return Types.INTEGER;
        }
        else if (type == Long.class) {
            return Types.BIGINT;
        }
        else if (type == Short.class) {
            return Types.SMALLINT;
        }
        else if (type == String.class) {
            return Types.VARCHAR;
        }
        else if (type == Time.class) {
            return Types.TIME;
        }
        else if (type == Timestamp.class) {
            return Types.TIMESTAMP;
        }

        // The type byte[] is handled earlier.
        else if (type.isArray()) {
            return Types.ARRAY;
        }
        else if (ArrayRecord.class.isAssignableFrom(type)) {
            return Types.ARRAY;
        }
        else if (EnumType.class.isAssignableFrom(type)) {
            return Types.VARCHAR;
        }
        else if (MasterDataType.class.isAssignableFrom(type)) {
            return Types.BIGINT;
        }
        else if (UDTRecord.class.isAssignableFrom(type)) {
            return Types.STRUCT;
        }
        else {
            return Types.OTHER;
        }
    }

    @Override
    public final Class<? extends T> getType() {
        return type;
    }

    @Override
    public Class<?> getType(int precision, int scale) {
        if (hasPrecisionAndScale) {
            return FieldTypeHelper.getClass(Types.NUMERIC, precision, scale);
        }

        // If no precise type could be guessed, take the default
        return getType();
    }

    @Override
    public final Class<? extends T[]> getArrayType() {
        return arrayType;
    }

    @Override
    public final String getTypeName() {
        return typeName;
    }

    @Override
    public final String getCastTypeName() {
        return castTypeName;
    }

    @Override
    public final String getArrayTypeName() {
        return arrayTypeName;
    }

    @Override
    public final DataType<T[]> getArrayDataType() {
        return new DefaultDataType<T[]>(dialect, arrayType, arrayTypeName, arrayCastTypeName);
    }

    @Override
    public final <A extends ArrayRecord<T>> DataType<A> asArrayDataType(Class<A> arrayDataType) {
        return new DefaultDataType<A>(dialect, arrayDataType, typeName, castTypeName);
    }

    @Override
    public final <M extends MasterDataType<T>> DataType<M> asMasterDataType(Class<M> masterDataType) {
        return new DefaultDataType<M>(dialect, masterDataType, typeName, castTypeName);
    }

    @Override
    public <E extends EnumType> DataType<E> asEnumDataType(Class<E> enumDataType) {
        return new DefaultDataType<E>(dialect, enumDataType, typeName, castTypeName);
    }

    @Override
    public <N extends Number> DataType<N> asNumberDataType(Class<N> numberDataType) {
        return FieldTypeHelper.getDataType(dialect, numberDataType);
    }

    @Override
    public final SQLDialect getDialect() {
        return dialect;
    }

    protected static DataType<Object> getDefaultDataType(SQLDialect dialect, String typeName) {
        return new DefaultDataType<Object>(dialect, Object.class, typeName, typeName);
    }

    protected static DataType<?> getDataType(SQLDialect dialect, String typeName) {
        DataType<?> result = typesByTypeName[dialect.ordinal()].get(FieldTypeHelper.normalise(typeName));

        if (result == null) {
            throw new SQLDialectNotSupportedException("Type " + typeName + " is not supported in dialect " + dialect);
        }

        return result;
    }

    protected static <T> DataType<T> getDataType(SQLDialect dialect, Class<? extends T> type) {

        // Recurse for arrays
        if (byte[].class != type && type.isArray()) {
            return (DataType<T>) getDataType(dialect, type.getComponentType()).getArrayDataType();
        }

        // Base types are registered statically
        else {
            DataType<?> result = typesByType[dialect.ordinal()].get(type);

            if (result == null) {

                // Object has a default fallback, if it is not registerd explicitly
                if (type == Object.class) {
                    return new DefaultDataType<T>(dialect, (Class<? extends T>) Object.class, "", "");
                }

                // All other data types are illegal
                else {
                    throw new SQLDialectNotSupportedException("Type " + type + " is not supported in dialect " + dialect);
                }
            }

            return (DataType<T>) result;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + type + ", " + typeName + "]";
    }
}
