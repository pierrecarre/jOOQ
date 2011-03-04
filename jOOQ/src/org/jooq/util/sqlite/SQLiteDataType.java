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
package org.jooq.util.sqlite;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.jooq.DataType;
import org.jooq.SQLDialect;
import org.jooq.impl.AbstractDataType;

/**
 * Supported data types for the {@link SQLDialect#SQLITE} dialect
 *
 * @author Lukas Eder
 * @see http://www.sqlite.org/datatype3.html
 */
public class SQLiteDataType<T> extends AbstractDataType<T> {

    public static final SQLiteDataType<Byte>       TINYINT          = new SQLiteDataType<Byte>(Byte.class, "tinyint");
    public static final SQLiteDataType<Short>      SMALLINT         = new SQLiteDataType<Short>(Short.class, "smallint");
    public static final SQLiteDataType<Short>      INT2             = new SQLiteDataType<Short>(Short.class, "int2");
    public static final SQLiteDataType<Integer>    INT              = new SQLiteDataType<Integer>(Integer.class, "int");
    public static final SQLiteDataType<Integer>    INTEGER          = new SQLiteDataType<Integer>(Integer.class, "integer");
    public static final SQLiteDataType<Integer>    MEDIUMINT        = new SQLiteDataType<Integer>(Integer.class, "mediumint");
    public static final SQLiteDataType<Boolean>    BOOLEAN          = new SQLiteDataType<Boolean>(Boolean.class, "boolean");
    public static final SQLiteDataType<Long>       INT8             = new SQLiteDataType<Long>(Long.class, "int8");
    public static final SQLiteDataType<BigInteger> BIGINT           = new SQLiteDataType<BigInteger>(BigInteger.class, "bigint");
    public static final SQLiteDataType<BigInteger> UNSIGNEDBIGINT   = new SQLiteDataType<BigInteger>(BigInteger.class, "unsigned big int");
    public static final SQLiteDataType<Double>     DOUBLE           = new SQLiteDataType<Double>(Double.class, "double");
    public static final SQLiteDataType<Double>     DOUBLEPRECISION  = new SQLiteDataType<Double>(Double.class, "double precision");
    public static final SQLiteDataType<Float>      FLOAT            = new SQLiteDataType<Float>(Float.class, "float");
    public static final SQLiteDataType<Float>      REAL             = new SQLiteDataType<Float>(Float.class, "real");
    public static final SQLiteDataType<BigDecimal> NUMERIC          = new SQLiteDataType<BigDecimal>(BigDecimal.class, "numeric");
    public static final SQLiteDataType<BigDecimal> DECIMAL          = new SQLiteDataType<BigDecimal>(BigDecimal.class, "decimal");
    public static final SQLiteDataType<Date>       DATE             = new SQLiteDataType<Date>(Date.class, "date");
    public static final SQLiteDataType<Timestamp>  DATETIME         = new SQLiteDataType<Timestamp>(Timestamp.class, "datetime");
    public static final SQLiteDataType<String>     LONGVARCHAR      = new SQLiteDataType<String>(String.class, "longvarchar");
    public static final SQLiteDataType<String>     CHAR             = new SQLiteDataType<String>(String.class, "char");
    public static final SQLiteDataType<String>     CHARACTER        = new SQLiteDataType<String>(String.class, "character");
    public static final SQLiteDataType<String>     VARCHAR          = new SQLiteDataType<String>(String.class, "varchar");
    public static final SQLiteDataType<String>     VARYINGCHARACTER = new SQLiteDataType<String>(String.class, "varying character");
    public static final SQLiteDataType<String>     NCHAR            = new SQLiteDataType<String>(String.class, "nchar");
    public static final SQLiteDataType<String>     NATIVECHARACTER  = new SQLiteDataType<String>(String.class, "native character");
    public static final SQLiteDataType<String>     NVARCHAR         = new SQLiteDataType<String>(String.class, "nvarchar");
    public static final SQLiteDataType<String>     TEXT             = new SQLiteDataType<String>(String.class, "text");
    public static final SQLiteDataType<String>     CLOB             = new SQLiteDataType<String>(String.class, "clob");
    public static final SQLiteDataType<byte[]>     LONGVARBINARY    = new SQLiteDataType<byte[]>(byte[].class, "longvarbinary");
    public static final SQLiteDataType<byte[]>     BLOB             = new SQLiteDataType<byte[]>(byte[].class, "blob");

    // Pseudo data types used for compatibility in casting
    protected static final SQLiteDataType<Time>       __TIME        = new SQLiteDataType<Time>(Time.class, "datetime");

    private SQLiteDataType(Class<? extends T> type, String typeName) {
        super(SQLDialect.SQLITE, type, typeName);
    }

    public static DataType<?> getDataType(String typeName) {
        return getDataType(SQLDialect.SQLITE, typeName);
    }

    public static <T> DataType<T> getDataType(Class<? extends T> type) {
        return getDataType(SQLDialect.SQLITE, type);
    }

    public static DataType<Object> getDefaultDataType(String typeName) {
        return getDefaultDataType(SQLDialect.SQLITE, typeName);
    }
}
