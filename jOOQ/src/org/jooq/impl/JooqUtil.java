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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import org.jooq.ArrayRecord;
import org.jooq.Configuration;
import org.jooq.EnumType;
import org.jooq.FieldProvider;
import org.jooq.MasterDataType;
import org.jooq.NamedTypeProviderQueryPart;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.SQLDialectNotSupportedException;

/**
 * General jooq utilities
 *
 * @author Lukas Eder
 */
final class JooqUtil {

    static <R extends ArrayRecord<?>> R newArrayRecord(Class<R> type, Configuration configuration) {
       try{
           return type.getConstructor(Configuration.class).newInstance(configuration);
       } catch (Exception e) {
           throw new IllegalStateException(
               "ArrayRecord type does not provide a constructor with signature ArrayRecord(FieldProvider) : " + type +
               ". Exception : " + e.getMessage());

       }
    }

    /**
     * Create a new record
     */
    static <R extends Record> R newRecord(Class<R> type) {
        return newRecord(type, null);
    }

    /**
     * Create a new record
     */
    static <R extends Record> R newRecord(Class<R> type, FieldProvider provider) {
        return newRecord(type, provider, null);
    }

    /**
     * Create a new record
     */
    @SuppressWarnings("unchecked")
    static <R extends Record> R newRecord(Class<R> type, FieldProvider provider, Configuration configuration) {
        if (type == RecordImpl.class) {
            return (R) new RecordImpl(provider);
        }

        else {
            try {
                if (configuration == null) {
                    return type.newInstance();
                } else {
                    return type.getConstructor(Configuration.class).newInstance(configuration);
                }
            }
            catch (Exception e) {
                throw new IllegalStateException(
                    "Record type does not provide a constructor with signature Record(FieldProvider) : " + type +
                    ". Exception : " + e.getMessage());
            }
        }
    }

    static void checkArguments(String sql, Object[] bindings) {
        // This comparison is a bit awkward, and probably not very precise...
        if (StringUtils.countMatches(sql, "?") != bindings.length) {
            throw new IllegalArgumentException(
                "The number of bind variables must match the number of bindings. " +
                "SQL = [" + sql + "], bindings.length = " + bindings.length);
        }
    }

    static String toSQLReference(SQLDialect dialect, String sql, Object[] bindings, boolean inlineParameters) {
        String result = sql;

        if (inlineParameters) {
            for (Object binding : bindings) {
                result = result.replaceFirst("\\?", FieldTypeHelper.toSQL(dialect, binding, inlineParameters));
            }
        }

        return result;
    }

    static String toSQLReferenceWithParentheses(SQLDialect dialect, String sql, Object[] bindings, boolean inlineParameters) {
        return "(" + toSQLReference(dialect, sql, bindings, inlineParameters) + ")";
    }

    static void bind(SQLDialect dialect, PreparedStatement stmt, int index, Class<?> type, Object value) throws SQLException {
        if (value == null) {
            stmt.setNull(index, FieldTypeHelper.getDataType(dialect, type).getSQLType());
            return;
        }

        if (type == Blob.class) {
            stmt.setBlob(index, (Blob) value);
        }
        else if (type == Boolean.class) {
            stmt.setBoolean(index, (Boolean) value);
        }
        else if (type == BigDecimal.class) {
            stmt.setBigDecimal(index, (BigDecimal) value);
        }
        else if (type == BigInteger.class) {
            stmt.setBigDecimal(index, new BigDecimal((BigInteger) value));
        }
        else if (type == Byte.class) {
            stmt.setByte(index, (Byte) value);
        }
        else if (type == byte[].class) {
            stmt.setBytes(index, (byte[]) value);
        }
        else if (type == Clob.class) {
            stmt.setClob(index, (Clob) value);
        }
        else if (type == Date.class) {
            stmt.setDate(index, (Date) value);
        }
        else if (type == Double.class) {
            stmt.setDouble(index, (Double) value);
        }
        else if (type == Float.class) {
            stmt.setFloat(index, (Float) value);
        }
        else if (type == Integer.class) {
            stmt.setInt(index, (Integer) value);
        }
        else if (type == Long.class) {
            stmt.setLong(index, (Long) value);
        }
        else if (type == Short.class) {
            stmt.setShort(index, (Short) value);
        }
        else if (type == String.class) {
            stmt.setString(index, (String) value);
        }
        else if (type == Time.class) {
            stmt.setTime(index, (Time) value);
        }
        else if (type == Timestamp.class) {
            stmt.setTimestamp(index, (Timestamp) value);
        }

        // The type byte[] is handled earlier. byte[][] can be handled here
        else if (type.isArray()) {
            switch (dialect) {
                case POSTGRES: {
                    StringBuilder sb = new StringBuilder();
                    sb.append("{");

                    String separator = "";
                    for (Object o : (Object[]) value) {
                        sb.append(separator);
                        sb.append("\"");
                        sb.append(o);
                        sb.append("\"");

                        separator = ", ";
                    }

                    sb.append("}");
                    stmt.setString(index, sb.toString());
                    break;
                }
                case HSQLDB:
                    stmt.setArray(index, new DefaultArray(dialect, (Object[]) value, type));
                    break;
                case H2:
                    stmt.setObject(index, value);
                    break;
                default:
                    throw new SQLDialectNotSupportedException("Cannot bind ARRAY types in dialect " + dialect);
            }
        }
        else if (ArrayRecord.class.isAssignableFrom(type)) {
            stmt.setArray(index, ((ArrayRecord<?>) value).createArray());
        }
        else if (EnumType.class.isAssignableFrom(type)) {
            stmt.setString(index, ((EnumType) value).getLiteral());
        }
        else if (MasterDataType.class.isAssignableFrom(type)) {
            Object primaryKey = ((MasterDataType<?>) value).getPrimaryKey();
            bind(dialect, stmt, index, primaryKey.getClass(), primaryKey);
        }
        else {
            stmt.setObject(index, value);
        }
    }

    static void bind(SQLDialect dialect, PreparedStatement stmt, int initialIndex, NamedTypeProviderQueryPart<?> field, Object value)
        throws SQLException {
        bind(dialect, stmt, initialIndex, field.getType(), value);
    }

    static int bind(SQLDialect dialect, PreparedStatement stmt, int initialIndex, Object... bindings) throws SQLException {
        for (Object binding : bindings) {
            Class<?> type = (binding == null) ? Object.class : binding.getClass();
            bind(dialect, stmt, initialIndex++, type, binding);
        }

        return initialIndex;
    }
}
