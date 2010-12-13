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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.jooq.EnumType;
import org.jooq.Field;
import org.jooq.MasterDataType;
import org.jooq.NamedTypeProviderQueryPart;
import org.jooq.SQLDialect;
import org.jooq.SQLDialectNotSupportedException;
import org.jooq.UDTRecord;
import org.jooq.util.db2.DB2DataType;
import org.jooq.util.h2.H2DataType;
import org.jooq.util.hsqldb.HSQLDBDataType;
import org.jooq.util.mysql.MySQLDataType;
import org.jooq.util.oracle.OracleDataType;
import org.jooq.util.postgres.PGobjectParser;
import org.jooq.util.postgres.PostgresDataType;

/**
 * @author Lukas Eder
 */
public final class FieldTypeHelper {

    public static String toJava(SQLDialect dialect, Object value) {
        return toJava(dialect, value, value.getClass());
    }

    public static String toJava(SQLDialect dialect, Object value, NamedTypeProviderQueryPart<?> field) {
        return toJava(dialect, value, field.getType());
    }

    public static String toJava(SQLDialect dialect, Object value, Class<?> type) {
        if (type == Blob.class) {
            // Not supported
        }
        else if (type == Boolean.class) {
            return Boolean.toString(JooqUtil.isTrue(String.valueOf(value)));
        }
        else if (type == BigInteger.class) {
            return "new BigInteger(\"" + value + "\")";
        }
        else if (type == BigDecimal.class) {
            return "new BigDecimal(\"" + value + "\")";
        }
        else if (type == Byte.class) {
            return "(byte) " + value;
        }
        else if (type == byte[].class) {
            // Not supported
        }
        else if (type == Clob.class) {
            // Not supported
        }
        else if (type == Date.class) {
            return "new Date(" + ((Date) value).getTime() + "L)";
        }
        else if (type == Double.class) {
            return Double.toString((Double) value);
        }
        else if (type == Float.class) {
            return Float.toString((Float) value) + "f";
        }
        else if (type == Integer.class) {
            return Integer.toString((Integer) value);
        }
        else if (type == Long.class) {
            return Long.toString((Long) value) + "L";
        }
        else if (type == Short.class) {
            return "(short) " + value;
        }
        else if (type == String.class) {
            return "\"" + value.toString().replace("\"", "\\\"") + "\"";
        }
        else if (type == Time.class) {
            return "new Time(" + ((Time) value).getTime() + "L)";
        }
        else if (type == Timestamp.class) {
            return "new Timestamp(" + ((Timestamp) value).getTime() + "L)";
        }
        else if (MasterDataType.class.isAssignableFrom(type)) {
            // Not supported
        }
        else if (EnumType.class.isAssignableFrom(type)) {
            // Not supported
        }
        else if (UDTRecord.class.isAssignableFrom(type)) {
            // Not supported
        }
        else {
            // Not supported
        }

        throw new UnsupportedOperationException("Class " + type + " is not supported");
    }

    public static String toSQL(SQLDialect dialect, Object value, boolean inlineParameters) {
        return toSQL(dialect, value, inlineParameters, value.getClass());
    }

    public static String toSQL(SQLDialect dialect, Object value, boolean inlineParameters,
        NamedTypeProviderQueryPart<?> field) {
        return toSQL(dialect, value, inlineParameters, field.getType());
    }

    public static String toSQL(SQLDialect dialect, Object value, boolean inlineParameters, Class<?> type) {
        if (inlineParameters) {
            if (type == Blob.class) {
                return "[BLOB]";
            }
            else if (type == Boolean.class) {
                return value.toString();
            }
            else if (type == BigInteger.class) {
                return value.toString();
            }
            else if (type == BigDecimal.class) {
                return value.toString();
            }
            else if (type == Byte.class) {
                return value.toString();
            }
            else if (type == byte[].class) {
                return "'" + new String((byte[]) value).replace("'", "''") + "'";
            }
            else if (type == Clob.class) {
                return "[CLOB]";
            }
            else if (type == Date.class) {
                if (value instanceof Date) {
                    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                    return "'" + f.format((Date) value) + "'";
                }
            }
            else if (type == Double.class) {
                return value.toString();
            }
            else if (type == Float.class) {
                return value.toString();
            }
            else if (type == Integer.class) {
                return value.toString();
            }
            else if (type == Long.class) {
                return value.toString();
            }
            else if (type == Short.class) {
                return value.toString();
            }
            else if (type == String.class) {
                return "'" + value.toString().replace("'", "''") + "'";
            }
            else if (type == Time.class) {
                if (value instanceof Date) {
                    SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
                    return "'" + f.format((Time) value) + "'";
                }
            }
            else if (type == Timestamp.class) {
                if (value instanceof Timestamp) {
                    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    return "'" + f.format((Timestamp) value) + "'";
                }
            }
            else if (EnumType.class.isAssignableFrom(type)) {
                if (value instanceof EnumType) {
                    return toSQL(dialect, ((EnumType) value).getLiteral(), inlineParameters);
                }
            }
            else if (MasterDataType.class.isAssignableFrom(type)) {
                if (value instanceof MasterDataType) {
                    return toSQL(dialect, ((MasterDataType<?>) value).getPrimaryKey(), inlineParameters);
                }
            }
            else if (UDTRecord.class.isAssignableFrom(type)) {
                return "[UDT]";
            }
            else {
                // Not supported
            }

            throw new UnsupportedOperationException("Class " + type + " is not supported");
        }

        if (EnumType.class.isAssignableFrom(type)) {
            switch (dialect) {

                // For some weird reason, PostGreSQL cannot bind a string
                // value to an enum type automatically, it has to be
                // done explicitly
                case POSTGRES:
                    return "?::" + ((EnumType) value).getName();
            }
        }

        return "?";
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFromResultSet(SQLDialect dialect, ResultSet rs, Class<? extends T> type, int index)
        throws SQLException {
        if (type == Blob.class) {
            return (T) rs.getBlob(index);
        }
        else if (type == Boolean.class) {
            return (T) Boolean.valueOf(rs.getBoolean(index));
        }
        else if (type == BigInteger.class) {
            return (T) rs.getBigDecimal(index);
        }
        else if (type == BigDecimal.class) {
            return (T) rs.getBigDecimal(index);
        }
        else if (type == Byte.class) {
            return (T) Byte.valueOf(rs.getByte(index));
        }
        else if (type == byte[].class) {
            return (T) rs.getBytes(index);
        }
        else if (type == Clob.class) {
            return (T) rs.getClob(index);
        }
        else if (type == Date.class) {
            return (T) rs.getDate(index);
        }
        else if (type == Double.class) {
            return (T) Double.valueOf(rs.getDouble(index));
        }
        else if (type == Float.class) {
            return (T) Float.valueOf(rs.getFloat(index));
        }
        else if (type == Integer.class) {
            return (T) Integer.valueOf(rs.getInt(index));
        }
        else if (type == Long.class) {
            return (T) Long.valueOf(rs.getLong(index));
        }
        else if (type == Short.class) {
            return (T) Short.valueOf(rs.getShort(index));
        }
        else if (type == String.class) {
            return (T) rs.getString(index);
        }
        else if (type == Time.class) {
            return (T) rs.getTime(index);
        }
        else if (type == Timestamp.class) {
            return (T) rs.getTimestamp(index);
        }
        else if (EnumType.class.isAssignableFrom(type)) {
            return getEnumType(type, rs.getString(index));
        }
        else if (MasterDataType.class.isAssignableFrom(type)) {
            return (T) getMasterDataType(type, rs.getObject(index));
        }
        else if (UDTRecord.class.isAssignableFrom(type)) {
            switch (dialect) {
                case POSTGRES:
                    return (T) createPostgresUDTRecord(type, rs.getObject(index));
            }

            return (T) rs.getObject(index);
        }
        else {
            return (T) rs.getObject(index);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFromString(SQLDialect dialect, Class<? extends T> type, String string) throws SQLException {
        if (string == null) {
            return null;
        }
        else if (type == Blob.class) {
            // Not supported
        }
        else if (type == Boolean.class) {
            return (T) Boolean.valueOf(string);
        }
        else if (type == BigInteger.class) {
            return (T) new BigInteger(string);
        }
        else if (type == BigDecimal.class) {
            return (T) new BigDecimal(string);
        }
        else if (type == Byte.class) {
            return (T) Byte.valueOf(string);
        }
        else if (type == byte[].class) {
            // Not supported
        }
        else if (type == Clob.class) {
            // Not supported
        }
        else if (type == Date.class) {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            return (T) new Date(parse(string, f).getTime());
        }
        else if (type == Double.class) {
            return (T) Double.valueOf(string);
        }
        else if (type == Float.class) {
            return (T) Float.valueOf(string);
        }
        else if (type == Integer.class) {
            return (T) Integer.valueOf(string);
        }
        else if (type == Long.class) {
            return (T) Long.valueOf(string);
        }
        else if (type == Short.class) {
            return (T) Short.valueOf(string);
        }
        else if (type == String.class) {
            return (T) string;
        }
        else if (type == Time.class) {
            SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
            return (T) new Time(parse(string, f).getTime());
        }
        else if (type == Timestamp.class) {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return (T) new Timestamp(parse(string, f).getTime());
        }
        else if (EnumType.class.isAssignableFrom(type)) {
            return getEnumType(type, string);
        }
        else if (MasterDataType.class.isAssignableFrom(type)) {
            return (T) getMasterDataType(type, string);
        }
        else if (UDTRecord.class.isAssignableFrom(type)) {
            switch (dialect) {
                case POSTGRES:
                    return (T) createPostgresUDTRecord(type, string);
            }
        }

        throw new UnsupportedOperationException("Class " + type + " is not supported");
    }

    private static java.util.Date parse(String string, SimpleDateFormat f) throws SQLException {
        try {
            return f.parse(string);
        }
        catch (ParseException e) {
            throw new SQLException(e);
        }
    }

    /**
     * Create a UDT record from a PGobject
     * <p>
     * Unfortunately, this feature is very poorly documented and true UDT
     * support by the PostGreSQL JDBC driver has been postponed for a long time.
     *
     * @param object An object of type PGobject. The actual argument type cannot
     *            be expressed in the method signature, as no explicit
     *            dependency to postgres logic is desired
     * @return The converted {@link UDTRecord}
     */
    private static UDTRecord<?> createPostgresUDTRecord(Class<?> type, Object object) throws SQLException {
        if (object == null) {
            return null;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        UDTRecord<?> record = (UDTRecord<?>) JooqUtil.newRecord((Class) type);
        List<String> values = new PGobjectParser().parse(object.toString());

        List<Field<?>> fields = record.getFields();
        for (int i = 0; i < fields.size(); i++) {
            setValue(SQLDialect.POSTGRES, record, fields.get(i), values.get(i));
        }

        return record;
    }

    private static <T> void setValue(SQLDialect dialect, UDTRecord<?> record, Field<T> field, String value)
        throws SQLException {
        record.setValue(field, getFromString(dialect, field.getType(), value));
    }

    @SuppressWarnings("unchecked")
    private static <T> T getEnumType(Class<? extends T> type, String literal) throws SQLException {
        try {
            Object[] list = (Object[]) type.getMethod("values").invoke(type);

            for (Object e : list) {
                String l = ((EnumType) e).getLiteral();

                if (l.equals(literal)) {
                    return (T) e;
                }
            }
        }
        catch (Exception e) {
            throw new SQLException("Unknown enum literal found : " + literal);
        }

        return null;
    }


    static MasterDataType<?> getMasterDataType(Class<?> type, Object primaryKey) throws SQLException {
        try {
            Object[] values = (Object[]) type.getMethod("values").invoke(type);

            for (Object value : values) {
                MasterDataType<?> result = (MasterDataType<?>) value;

                if (String.valueOf(primaryKey).equals(String.valueOf(result.getPrimaryKey()))) {
                    return result;
                }
            }
        }
        catch (Exception e) {
            throw new SQLException("Unknown enum literal found : " + primaryKey);
        }

        return null;
    }

    public static <T> T getFromStatement(SQLDialect dialect, CallableStatement statement,
        NamedTypeProviderQueryPart<T> field, int fieldIndex) throws SQLException {

        return getFromStatement(dialect, statement, field.getType(), fieldIndex);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFromStatement(SQLDialect dialect, CallableStatement statement, Class<? extends T> type,
        int fieldIndex) throws SQLException {

        if (type == Blob.class) {
            return (T) statement.getBlob(fieldIndex);
        }
        else if (type == Boolean.class) {
            return (T) Boolean.valueOf(statement.getBoolean(fieldIndex));
        }
        else if (type == BigInteger.class) {
            BigDecimal result = statement.getBigDecimal(fieldIndex);
            return (T) (result == null ? null : result.toBigInteger());
        }
        else if (type == BigDecimal.class) {
            return (T) statement.getBigDecimal(fieldIndex);
        }
        else if (type == Byte.class) {
            return (T) Byte.valueOf(statement.getByte(fieldIndex));
        }
        else if (type == byte[].class) {
            return (T) statement.getBytes(fieldIndex);
        }
        else if (type == Clob.class) {
            return (T) statement.getClob(fieldIndex);
        }
        else if (type == Date.class) {
            return (T) statement.getDate(fieldIndex);
        }
        else if (type == Double.class) {
            return (T) Double.valueOf(statement.getDouble(fieldIndex));
        }
        else if (type == Float.class) {
            return (T) Float.valueOf(statement.getFloat(fieldIndex));
        }
        else if (type == Integer.class) {
            return (T) Integer.valueOf(statement.getInt(fieldIndex));
        }
        else if (type == Long.class) {
            return (T) Long.valueOf(statement.getLong(fieldIndex));
        }
        else if (type == Short.class) {
            return (T) Short.valueOf(statement.getShort(fieldIndex));
        }
        else if (type == String.class) {
            return (T) statement.getString(fieldIndex);
        }
        else if (type == Time.class) {
            return (T) statement.getTime(fieldIndex);
        }
        else if (type == Timestamp.class) {
            return (T) statement.getTimestamp(fieldIndex);
        }
        else if (EnumType.class.isAssignableFrom(type)) {
            return getEnumType(type, statement.getString(fieldIndex));
        }
        else if (MasterDataType.class.isAssignableFrom(type)) {
            return (T) getMasterDataType(type, statement.getString(fieldIndex));
        }
        else {
            return (T) statement.getObject(fieldIndex);
        }
    }

    public static int getSQLType(NamedTypeProviderQueryPart<?> field) {
        return getSQLType(field.getType());
    }

    public static int getSQLType(Class<?> type) {
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
        else {
            return Types.OTHER;
        }
    }
    
    public static Class<?> getClass(int sqlType, int precision, int scale) {
        if (sqlType == Types.BLOB) {
            return Blob.class;
        }
        else if (sqlType == Types.BOOLEAN) {
            return Boolean.class;
        }
        else if (sqlType == Types.BIGINT) {
            return BigInteger.class;
        }
        else if (sqlType == Types.DECIMAL) {
            return BigDecimal.class;
        }
        else if (sqlType == Types.REAL) {
            return BigDecimal.class;
        }
        else if (sqlType == Types.NUMERIC) {
            
            // Integer numbers
            if (scale == 0 && precision != 0) {

                // if (precision == 1) {
                //     Booleans could have precision == 1, but that's a tough guess
                // }
                if (precision < String.valueOf(Byte.MAX_VALUE).length()) {
                    return Byte.class;
                }
                if (precision < String.valueOf(Short.MAX_VALUE).length()) {
                    return Short.class;
                }
                if (precision < String.valueOf(Integer.MAX_VALUE).length()) {
                    return Integer.class;
                }
                if (precision < String.valueOf(Long.MAX_VALUE).length()) {
                    return Long.class;
                }

                // Default integer number
                return BigInteger.class;
            }

            // Real numbers should not be represented as float or double
            else {
                return BigDecimal.class;
            }
        }
        else if (sqlType == Types.DECIMAL) {
            return BigDecimal.class;
        }
        else if (sqlType == Types.TINYINT) {
            return Byte.class;
        }
        else if (sqlType == Types.BLOB) {
            return byte[].class;
        }
        else if (sqlType == Types.CLOB) {
            return Clob.class;
        }
        else if (sqlType == Types.DATE) {
            return Date.class;
        }
        else if (sqlType == Types.DOUBLE) {
            return Double.class;
        }
        else if (sqlType == Types.FLOAT) {
            return Float.class;
        }
        else if (sqlType == Types.INTEGER) {
            return Integer.class;
        }
        else if (sqlType == Types.SMALLINT) {
            return Short.class;
        }
        else if (sqlType == Types.CHAR) {
            return String.class;
        }
        else if (sqlType == Types.VARCHAR) {
            return String.class;
        }
        else if (sqlType == Types.LONGVARCHAR) {
            return String.class;
        }
        else if (sqlType == Types.TIME) {
            return Time.class;
        }
        else if (sqlType == Types.TIMESTAMP) {
            return Timestamp.class;
        }
        else {
            return Object.class;
        }
    }

    public static String getDialectSQLType(SQLDialect dialect, NamedTypeProviderQueryPart<?> field) {
        return getDialectSQLType(dialect, field.getType());
    }

    public static String getDialectSQLType(SQLDialect dialect, Class<?> type) {
        Enum<?> result = null;

        switch (dialect) {
            case HSQLDB:
                result = HSQLDBDataType.getType(type);
                break;
            case MYSQL:
                result = MySQLDataType.getType(type);
                break;
            case ORACLE:
                result = OracleDataType.getType(type);
                break;
            case POSTGRES:
                result = PostgresDataType.getType(type);
                break;
            case DB2:
                result = DB2DataType.getType(type);
                break;
            case H2:
                result = H2DataType.getType(type);
                break;

            default:
                throw new SQLDialectNotSupportedException("This method is not yet implemented for dialect " + dialect);
        }

        if (result == null) {
            throw new SQLDialectNotSupportedException(type + " cannot be mapped to any type in dialect " + dialect);
        }

        return String.valueOf(result);
    }

    private FieldTypeHelper() {}
}
