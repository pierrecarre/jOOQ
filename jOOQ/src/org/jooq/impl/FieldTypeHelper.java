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
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jooq.ArrayRecord;
import org.jooq.Configuration;
import org.jooq.DataType;
import org.jooq.EnumType;
import org.jooq.Field;
import org.jooq.MasterDataType;
import org.jooq.NamedTypeProviderQueryPart;
import org.jooq.SQLDialect;
import org.jooq.SQLDialectNotSupportedException;
import org.jooq.UDTRecord;
import org.jooq.util.db2.DB2DataType;
import org.jooq.util.derby.DerbyDataType;
import org.jooq.util.h2.H2DataType;
import org.jooq.util.hsqldb.HSQLDBDataType;
import org.jooq.util.mysql.MySQLDataType;
import org.jooq.util.oracle.OracleDataType;
import org.jooq.util.postgres.PGobjectParser;
import org.jooq.util.postgres.PostgresDataType;
import org.jooq.util.sqlite.SQLiteDataType;

/**
 * Utility methods related to the treatment of fields and their types
 * <p>
 * This class is for JOOQ INTERNAL USE only. Do not reference directly
 *
 * @author Lukas Eder
 */
public final class FieldTypeHelper {

    private static final int LONG_PRECISION = String.valueOf(Long.MAX_VALUE).length();
    private static final int INTEGER_PRECISION = String.valueOf(Integer.MAX_VALUE).length();
    private static final int SHORT_PRECISION = String.valueOf(Short.MAX_VALUE).length();
    private static final int BYTE_PRECISION = String.valueOf(Byte.MAX_VALUE).length();

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
                return "'" + value + "'";
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
                return "'" + value + "'";
            }
            else if (type == Timestamp.class) {
                return "'" + value + "'";
            }
            else if (type.isArray()) {
                return "ARRAY" + Arrays.asList((Object[]) value).toString();
            }
            else if (ArrayRecord.class.isAssignableFrom(type)) {
                return value.toString();
            }
            else if (EnumType.class.isAssignableFrom(type)) {
                return toSQL(dialect, ((EnumType) value).getLiteral(), inlineParameters);
            }
            else if (MasterDataType.class.isAssignableFrom(type)) {
                return toSQL(dialect, ((MasterDataType<?>) value).getPrimaryKey(), inlineParameters);
            }
            else if (UDTRecord.class.isAssignableFrom(type)) {
                return "[UDT]";
            }
            else {
                // Not supported
            }

            throw new UnsupportedOperationException("Class " + type + " is not supported");
        }

        // In Postgres, some additional casting must be done in some cases...
        // TODO: Improve this implementation with #215 (cast support)
        if (dialect == SQLDialect.POSTGRES) {

            // Don't cast this type as an array type. It's a BLOB, bytea
            if (byte[].class == type) {
            }

            // Postgres needs explicit casting for array types
            else if (type.isArray()) {
                StringBuilder sb = new StringBuilder();
                sb.append("?::");
                sb.append(getDataType(dialect, type).getCastTypeName());

                return sb.toString();
            }

            else if (EnumType.class.isAssignableFrom(type)) {
                return "?::" + ((EnumType) value).getName();
            }
        }

        return "?";
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFromSQLInput(SQLInput stream, Field<T> field) throws SQLException {
        Class<? extends T> type = field.getType();

        if (type == Blob.class) {
            return (T) stream.readBlob();
        }
        else if (type == Boolean.class) {
            return (T) checkWasNull(stream, Boolean.valueOf(stream.readBoolean()));
        }
        else if (type == BigInteger.class) {
            BigDecimal result = stream.readBigDecimal();
            return (T) (result == null ? null : result.toBigInteger());
        }
        else if (type == BigDecimal.class) {
            return (T) stream.readBigDecimal();
        }
        else if (type == Byte.class) {
            return (T) checkWasNull(stream, Byte.valueOf(stream.readByte()));
        }
        else if (type == byte[].class) {
            return (T) stream.readBytes();
        }
        else if (type == Clob.class) {
            return (T) stream.readClob();
        }
        else if (type == Date.class) {
            return (T) stream.readDate();
        }
        else if (type == Double.class) {
            return (T) checkWasNull(stream, Double.valueOf(stream.readDouble()));
        }
        else if (type == Float.class) {
            return (T) checkWasNull(stream, Float.valueOf(stream.readFloat()));
        }
        else if (type == Integer.class) {
            return (T) checkWasNull(stream, Integer.valueOf(stream.readInt()));
        }
        else if (type == Long.class) {
            return (T) checkWasNull(stream, Long.valueOf(stream.readLong()));
        }
        else if (type == Short.class) {
            return (T) checkWasNull(stream, Short.valueOf(stream.readShort()));
        }
        else if (type == String.class) {
            return (T) stream.readString();
        }
        else if (type == Time.class) {
            return (T) stream.readTime();
        }
        else if (type == Timestamp.class) {
            return (T) stream.readTimestamp();
        }

        // The type byte[] is handled earlier. byte[][] can be handled here
        else if (type.isArray()) {
            Array result = stream.readArray();
            return (T) (result == null ? null : result.getArray());
        }
        else if (ArrayRecord.class.isAssignableFrom(type)) {
            throw new UnsupportedOperationException("Type " + type + " is not supported");
        }
        else if (EnumType.class.isAssignableFrom(type)) {
            return getEnumType(type, stream.readString());
        }
        else if (MasterDataType.class.isAssignableFrom(type)) {
            return (T) getMasterDataType(type, stream.readObject());
        }
        else if (UDTRecord.class.isAssignableFrom(type)) {
            return (T) stream.readObject();
        }
        else {
            return (T) stream.readObject();
        }
    }

    public static <T> void writeToSQLOutput(SQLOutput stream, Field<T> field, T value) throws SQLException {
        Class<? extends T> type = field.getType();

        writeToSQLOutput(stream, type, value);
    }

    public static <T> void writeToSQLOutput(SQLOutput stream, Class<? extends T> type, T value) throws SQLException {
        if (value == null) {
            stream.writeObject(null);
        }
        else if (type == Blob.class) {
            stream.writeBlob((Blob) value);
        }
        else if (type == Boolean.class) {
            stream.writeBoolean((Boolean) value);
        }
        else if (type == BigInteger.class) {
            stream.writeBigDecimal(new BigDecimal((BigInteger) value));
        }
        else if (type == BigDecimal.class) {
            stream.writeBigDecimal((BigDecimal) value);
        }
        else if (type == Byte.class) {
            stream.writeByte((Byte) value);
        }
        else if (type == byte[].class) {
            stream.writeBytes((byte[]) value);
        }
        else if (type == Clob.class) {
            stream.writeClob((Clob) value);
        }
        else if (type == Date.class) {
            stream.writeDate((Date) value);
        }
        else if (type == Double.class) {
            stream.writeDouble((Double) value);
        }
        else if (type == Float.class) {
            stream.writeFloat((Float) value);
        }
        else if (type == Integer.class) {
            stream.writeInt((Integer) value);
        }
        else if (type == Long.class) {
            stream.writeLong((Long) value);
        }
        else if (type == Short.class) {
            stream.writeShort((Short) value);
        }
        else if (type == String.class) {
            stream.writeString((String) value);
        }
        else if (type == Time.class) {
            stream.writeTime((Time) value);
        }
        else if (type == Timestamp.class) {
            stream.writeTimestamp((Timestamp) value);
        }
//        else if (type.isArray()) {
//            stream.writeArray(value);
//        }
        else if (ArrayRecord.class.isAssignableFrom(type)) {
            stream.writeArray(((ArrayRecord<?>) value).createArray());
        }
        else if (EnumType.class.isAssignableFrom(type)) {
            stream.writeString(((EnumType) value).getLiteral());
        }
        else if (MasterDataType.class.isAssignableFrom(type)) {
            Object key = ((MasterDataType<?>) value).getPrimaryKey();
            writeToSQLOutput(stream, key.getClass(), key);
        }
        else if (UDTRecord.class.isAssignableFrom(type)) {
            stream.writeObject((UDTRecord<?>) value);
        }
        else {
            throw new UnsupportedOperationException("Type " + type + " is not supported");
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFromResultSet(Configuration configuration, ResultSet rs, Field<T> field, int index)
        throws SQLException {
        Class<? extends T> type = field.getType();

        if (type == Blob.class) {
            return (T) rs.getBlob(index);
        }
        else if (type == Boolean.class) {
            return (T) checkWasNull(rs, Boolean.valueOf(rs.getBoolean(index)));
        }
        else if (type == BigInteger.class) {
            BigDecimal result = rs.getBigDecimal(index);
            return (T) (result == null ? null : result.toBigInteger());
        }
        else if (type == BigDecimal.class) {
            return (T) rs.getBigDecimal(index);
        }
        else if (type == Byte.class) {
            return (T) checkWasNull(rs, Byte.valueOf(rs.getByte(index)));
        }
        else if (type == byte[].class) {
            return (T) rs.getBytes(index);
        }
        else if (type == Clob.class) {
            return (T) rs.getClob(index);
        }
        else if (type == Date.class) {
            return (T) getDate(configuration.getDialect(), rs, index);
        }
        else if (type == Double.class) {
            return (T) checkWasNull(rs, Double.valueOf(rs.getDouble(index)));
        }
        else if (type == Float.class) {
            return (T) checkWasNull(rs, Float.valueOf(rs.getFloat(index)));
        }
        else if (type == Integer.class) {
            return (T) checkWasNull(rs, Integer.valueOf(rs.getInt(index)));
        }
        else if (type == Long.class) {
            return (T) checkWasNull(rs, Long.valueOf(rs.getLong(index)));
        }
        else if (type == Short.class) {
            return (T) checkWasNull(rs, Short.valueOf(rs.getShort(index)));
        }
        else if (type == String.class) {
            return (T) rs.getString(index);
        }
        else if (type == Time.class) {
            return (T) getTime(configuration.getDialect(), rs, index);
        }
        else if (type == Timestamp.class) {
            return (T) getTimestamp(configuration.getDialect(), rs, index);
        }

        // The type byte[] is handled earlier. byte[][] can be handled here
        else if (type.isArray()) {

            // Note: due to a HSQLDB bug, it is not recommended to call rs.getObject() here:
            // See https://sourceforge.net/tracker/?func=detail&aid=3181365&group_id=23316&atid=378131
            return (T) convertArray(rs.getArray(index), (Class<? extends Object[]>)type);
        }
        else if (ArrayRecord.class.isAssignableFrom(type)) {
            return (T) getArrayRecord(configuration, rs.getArray(index), (Class<? extends ArrayRecord<?>>) type);
        }
        else if (EnumType.class.isAssignableFrom(type)) {
            return getEnumType(type, rs.getString(index));
        }
        else if (MasterDataType.class.isAssignableFrom(type)) {
            return (T) getMasterDataType(type, rs.getObject(index));
        }
        else if (UDTRecord.class.isAssignableFrom(type)) {
            switch (configuration.getDialect()) {
                case POSTGRES:
                    return (T) createPostgresUDTRecord(type, rs.getObject(index));
            }

            return (T) rs.getObject(index, getTypeMapping(type));
        }
        else {
            return (T) rs.getObject(index);
        }
    }

    private static ArrayRecord<?> getArrayRecord(Configuration configuration, Array array, Class<? extends ArrayRecord<?>> type)
        throws SQLException {

        if (array == null) {
            return null;
        }
        else {
            ArrayRecord<?> record = JooqUtil.newArrayRecord(type, configuration);
            record.set(array);
            return record;
        }
    }

    private static Object[] convertArray(Object array, Class<? extends Object[]> type) throws SQLException {
        if (array instanceof Object[]) {
            return TypeUtils.convert((Object[]) array, type);
        }
        else if (array instanceof Array) {
            return convertArray((Array) array, type);
        }

        return null;
    }

    private static Object[] convertArray(Array array, Class<? extends Object[]> type) throws SQLException {
        if (array != null) {
            return TypeUtils.convert((Object[]) array.getArray(), type);
        }

        return null;
    }

    private static Date getDate(SQLDialect dialect, ResultSet rs, int index) throws SQLException {
        // SQLite's type affinity needs special care...
        if (dialect == SQLDialect.SQLITE) {
            String date = rs.getString(index);

            if (date != null) {
                return new Date(parse("yyyy-MM-dd", date));
            }

            return null;
        } else {
            return rs.getDate(index);
        }
    }

    private static Time getTime(SQLDialect dialect, ResultSet rs, int index) throws SQLException {
        // SQLite's type affinity needs special care...
        if (dialect == SQLDialect.SQLITE) {
            String time = rs.getString(index);

            if (time != null) {
                return new Time(parse("HH:mm:ss", time));
            }

            return null;
        } else {
            return rs.getTime(index);
        }
    }

    private static Timestamp getTimestamp(SQLDialect dialect, ResultSet rs, int index) throws SQLException {
        // SQLite's type affinity needs special care...
        if (dialect == SQLDialect.SQLITE) {
            String timestamp = rs.getString(index);

            if (timestamp != null) {
                return new Timestamp(parse("yyyy-MM-dd HH:mm:ss", timestamp));
            }

            return null;
        } else {
            return rs.getTimestamp(index);
        }
    }

    private static long parse(String pattern, String date) throws SQLException {
        try {

            // Try reading a plain number first
            try {
                return Long.valueOf(date);
            }

            // If that fails, try reading a formatted date
            catch (NumberFormatException e) {
                return new SimpleDateFormat(pattern).parse(date).getTime();
            }
        }
        catch (ParseException e) {
            throw new SQLException("Could not parse date " + date, e);
        }
    }

    public static Map<String, Class<?>> getTypeMapping(Class<?> udtType) throws SQLException {
        try {
            return ((UDTRecord<?>) udtType.newInstance()).getUDT().getTypeMapping();
        } catch (Exception e) {
            throw new SQLException("Cannot retrieve type mapping for " + udtType, e);
        }
    }

    private static <T> T checkWasNull(SQLInput stream, T value) throws SQLException {
        return stream.wasNull() ? null : value;
    }

    private static <T> T checkWasNull(ResultSet rs, T value) throws SQLException {
        return rs.wasNull() ? null : value;
    }

    private static <T> T checkWasNull(CallableStatement statement, T value) throws SQLException {
        return statement.wasNull() ? null : value;
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
        else if (type.isArray()) {
            // Not supported
        }
        else if (ArrayRecord.class.isAssignableFrom(type)) {
            // Not supported
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

    @SuppressWarnings("unchecked")
    public static <T> T getFromStatement(Configuration configuration, CallableStatement stmt, Class<? extends T> type, int index) throws SQLException {
        if (type == Blob.class) {
            return (T) stmt.getBlob(index);
        }
        else if (type == Boolean.class) {
            return (T) checkWasNull(stmt, Boolean.valueOf(stmt.getBoolean(index)));
        }
        else if (type == BigInteger.class) {
            BigDecimal result = stmt.getBigDecimal(index);
            return (T) (result == null ? null : result.toBigInteger());
        }
        else if (type == BigDecimal.class) {
            return (T) stmt.getBigDecimal(index);
        }
        else if (type == Byte.class) {
            return (T) checkWasNull(stmt, Byte.valueOf(stmt.getByte(index)));
        }
        else if (type == byte[].class) {
            return (T) stmt.getBytes(index);
        }
        else if (type == Clob.class) {
            return (T) stmt.getClob(index);
        }
        else if (type == Date.class) {
            return (T) stmt.getDate(index);
        }
        else if (type == Double.class) {
            return (T) checkWasNull(stmt, Double.valueOf(stmt.getDouble(index)));
        }
        else if (type == Float.class) {
            return (T) checkWasNull(stmt, Float.valueOf(stmt.getFloat(index)));
        }
        else if (type == Integer.class) {
            return (T) checkWasNull(stmt, Integer.valueOf(stmt.getInt(index)));
        }
        else if (type == Long.class) {
            return (T) checkWasNull(stmt, Long.valueOf(stmt.getLong(index)));
        }
        else if (type == Short.class) {
            return (T) checkWasNull(stmt, Short.valueOf(stmt.getShort(index)));
        }
        else if (type == String.class) {
            return (T) stmt.getString(index);
        }
        else if (type == Time.class) {
            return (T) stmt.getTime(index);
        }
        else if (type == Timestamp.class) {
            return (T) stmt.getTimestamp(index);
        }

        // The type byte[] is handled earlier. byte[][] can be handled here
        else if (type.isArray()) {
            return (T) convertArray(stmt.getObject(index), (Class<? extends Object[]>)type);
        }
        else if (ArrayRecord.class.isAssignableFrom(type)) {
            return (T) getArrayRecord(configuration, stmt.getArray(index), (Class<? extends ArrayRecord<?>>) type);
        }
        else if (EnumType.class.isAssignableFrom(type)) {
            return getEnumType(type, stmt.getString(index));
        }
        else if (MasterDataType.class.isAssignableFrom(type)) {
            return (T) getMasterDataType(type, stmt.getString(index));
        }
        else if (UDTRecord.class.isAssignableFrom(type)) {
            switch (configuration.getDialect()) {
                case POSTGRES:
                    return (T) createPostgresUDTRecord(type, stmt.getObject(index));
            }

            return (T) stmt.getObject(index, getTypeMapping(type));
        }
        else {
            return (T) stmt.getObject(index);
        }
    }

    public static Class<?> getClass(int sqlType, int precision, int scale) {
        if (sqlType == Types.BLOB) {
            return byte[].class;
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
                if (precision < BYTE_PRECISION) {
                    return Byte.class;
                }
                if (precision < SHORT_PRECISION) {
                    return Short.class;
                }
                if (precision < INTEGER_PRECISION) {
                    return Integer.class;
                }
                if (precision < LONG_PRECISION) {
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
        else if (sqlType == Types.CLOB) {
            return String.class;
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

    public static String getArrayType(String dataType, SQLDialect dialect) {
        switch (dialect) {
            case HSQLDB:
                return dataType + " ARRAY";
            case POSTGRES:
                return dataType + "[]";
            case H2:
                return "ARRAY";
        }

        return null;
    }

    public static <T> DataType<T> getDataType(SQLDialect dialect, Class<? extends T> type) {
        switch (dialect) {
            case HSQLDB:
                return HSQLDBDataType.getDataType(type);
            case MYSQL:
                return MySQLDataType.getDataType(type);
            case ORACLE:
                return OracleDataType.getDataType(type);
            case POSTGRES:
                return PostgresDataType.getDataType(type);
            case DB2:
                return DB2DataType.getDataType(type);
            case H2:
                return H2DataType.getDataType(type);
            case SQLITE:
                return SQLiteDataType.getDataType(type);
            case DERBY:
                return DerbyDataType.getDataType(type);

            default:
                throw new SQLDialectNotSupportedException("This method is not yet implemented for dialect " + dialect);
        }
    }

    /**
     * @return The type name without all special characters and white spaces
     */
    public static String normalise(String typeName) {
        return typeName.toUpperCase().replaceAll("\"|\\.|\\s|\\(\\d+\\)|(NOT\\s*NULL)?", "");
    }

    private FieldTypeHelper() {}
}
