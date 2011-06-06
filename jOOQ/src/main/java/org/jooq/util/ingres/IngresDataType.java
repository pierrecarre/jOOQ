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

package org.jooq.util.ingres;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.jooq.DataType;
import org.jooq.SQLDialect;
import org.jooq.impl.AbstractDataType;

/**
 * Supported data types for the {@link SQLDialect#INGRES} dialect
 *
 * @author Lukas Eder
 * @see <a href="http://docs.ingres.com/ingres/9.3/quel-reference-guide/1240-data-types">http://docs.ingres.com/ingres/9.3/quel-reference-guide/1240-data-types</a>
 * @see <a href="http://docs.ingres.com/ingres/10.0/sql-reference-guide/2638-storage-formats-of-data-types">http://docs.ingres.com/ingres/10.0/sql-reference-guide/2638-storage-formats-of-data-types</a>
 */
public class IngresDataType<T> extends AbstractDataType<T> {

    /**
     * Generated UID
     */
    private static final long                      serialVersionUID             = -5677365115109672781L;

    public static final IngresDataType<Integer>    INTEGER                      = new IngresDataType<Integer>(Integer.class, "integer");
    public static final IngresDataType<Integer>    INTEGER4                     = new IngresDataType<Integer>(Integer.class, "integer4");
    public static final IngresDataType<Integer>    I4                           = new IngresDataType<Integer>(Integer.class, "i4");
    public static final IngresDataType<Byte>       TINYINT                      = new IngresDataType<Byte>(Byte.class, "tinyint");
    public static final IngresDataType<Byte>       INTEGER1                     = new IngresDataType<Byte>(Byte.class, "integer1");
    public static final IngresDataType<Byte>       I1                           = new IngresDataType<Byte>(Byte.class, "i1");
    public static final IngresDataType<Short>      SMALLINT                     = new IngresDataType<Short>(Short.class, "smallint");
    public static final IngresDataType<Short>      INTEGER2                     = new IngresDataType<Short>(Short.class, "integer2");
    public static final IngresDataType<Short>      I2                           = new IngresDataType<Short>(Short.class, "i2");
    public static final IngresDataType<Long>       BIGINT                       = new IngresDataType<Long>(Long.class, "bigint");
    public static final IngresDataType<Long>       INTEGER8                     = new IngresDataType<Long>(Long.class, "integer8");
    public static final IngresDataType<Long>       I8                           = new IngresDataType<Long>(Long.class, "i8");
    public static final IngresDataType<Double>     FLOAT                        = new IngresDataType<Double>(Double.class, "float");
    public static final IngresDataType<Double>     FLOAT8                       = new IngresDataType<Double>(Double.class, "float8");
    public static final IngresDataType<Double>     DOUBLEPRECISION              = new IngresDataType<Double>(Double.class, "double precision");
    public static final IngresDataType<Float>      REAL                         = new IngresDataType<Float>(Float.class, "real");
    public static final IngresDataType<Float>      FLOAT4                       = new IngresDataType<Float>(Float.class, "float4");

    public static final IngresDataType<Boolean>    BOOLEAN                      = new IngresDataType<Boolean>(Boolean.class, "boolean");
    public static final IngresDataType<BigDecimal> DECIMAL                      = new IngresDataType<BigDecimal>(BigDecimal.class, "decimal", "decimal(38, 19)");



    public static final IngresDataType<String>     VARCHAR                      = new IngresDataType<String>(String.class, "varchar");
    public static final IngresDataType<String>     CHARACTERVARYING             = new IngresDataType<String>(String.class, "character varying");
    public static final IngresDataType<String>     LONGVARCHAR                  = new IngresDataType<String>(String.class, "long varchar");
    public static final IngresDataType<String>     TEXT                         = new IngresDataType<String>(String.class, "text");
    public static final IngresDataType<String>     CLOB                         = new IngresDataType<String>(String.class, "clob");
    public static final IngresDataType<String>     CHARLARGEOBJECT              = new IngresDataType<String>(String.class, "char large object");
    public static final IngresDataType<String>     CHARACTERLARGEOBJECT         = new IngresDataType<String>(String.class, "character large object");
    public static final IngresDataType<String>     C                            = new IngresDataType<String>(String.class, "c");
    public static final IngresDataType<String>     CHAR                         = new IngresDataType<String>(String.class, "char");
    public static final IngresDataType<String>     CHARACTER                    = new IngresDataType<String>(String.class, "character");

    public static final IngresDataType<String>     NVARCHAR                     = new IngresDataType<String>(String.class, "nvarchar");
    public static final IngresDataType<String>     LONGNVARCHAR                 = new IngresDataType<String>(String.class, "long nvarchar");
    public static final IngresDataType<String>     NCHAR                        = new IngresDataType<String>(String.class, "nchar");
    public static final IngresDataType<String>     NCLOB                        = new IngresDataType<String>(String.class, "nclob");
    public static final IngresDataType<String>     NCHARLARGEOBJECT             = new IngresDataType<String>(String.class, "nchar large object");
    public static final IngresDataType<String>     NATIONALCHARACTERLARGEOBJECT = new IngresDataType<String>(String.class, "national character large object");

    public static final IngresDataType<Date>       DATE                         = new IngresDataType<Date>(Date.class, "date");
    public static final IngresDataType<Date>       ANSIDATE                     = new IngresDataType<Date>(Date.class, "ansidate");
    public static final IngresDataType<Date>       INGRESDATE                   = new IngresDataType<Date>(Date.class, "ingresdate");
    public static final IngresDataType<Time>       TIME                         = new IngresDataType<Time>(Time.class, "time");
    public static final IngresDataType<Time>       TIMEWITHTIMEZONE             = new IngresDataType<Time>(Time.class, "time with time zone");
    public static final IngresDataType<Time>       TIMEWITHOUTTIMEZONE          = new IngresDataType<Time>(Time.class, "time without time zone");
    public static final IngresDataType<Time>       TIMEWITHLOCALTIMEZONE        = new IngresDataType<Time>(Time.class, "time with local time zone");
    public static final IngresDataType<Timestamp>  TIMESTAMP                    = new IngresDataType<Timestamp>(Timestamp.class, "timestamp");
    public static final IngresDataType<Timestamp>  TIMESTAMPWITHTIMEZONE        = new IngresDataType<Timestamp>(Timestamp.class, "timestamp with time zone");
    public static final IngresDataType<Timestamp>  TIMESTAMPWITHOUTTIMEZONE     = new IngresDataType<Timestamp>(Timestamp.class, "timestamp without time zone");
    public static final IngresDataType<Timestamp>  TIMESTAMPWITHLOCALTIMEZONE   = new IngresDataType<Timestamp>(Timestamp.class, "timestamp with local time zone");

    public static final IngresDataType<byte[]>     BYTE                         = new IngresDataType<byte[]>(byte[].class, "byte");
    public static final IngresDataType<byte[]>     VARBYTE                      = new IngresDataType<byte[]>(byte[].class, "varbyte");
    public static final IngresDataType<byte[]>     BINARY                       = new IngresDataType<byte[]>(byte[].class, "binary");
    public static final IngresDataType<byte[]>     VARBINARY                    = new IngresDataType<byte[]>(byte[].class, "varbinary");
    public static final IngresDataType<byte[]>     LONGBYTE                     = new IngresDataType<byte[]>(byte[].class, "long byte");
    public static final IngresDataType<byte[]>     BLOB                         = new IngresDataType<byte[]>(byte[].class, "blob");
    public static final IngresDataType<byte[]>     BINARYLARGEOBJECT            = new IngresDataType<byte[]>(byte[].class, "binary large object");

    // Pseudo data types used for compatibility in casting
    protected static final IngresDataType<BigInteger> __BIGINTEGER              = new IngresDataType<BigInteger>(BigInteger.class, "decimal", "decimal(38, 0)");

    private IngresDataType(Class<? extends T> type, String typeName) {
        super(SQLDialect.INGRES, type, typeName);
    }

    private IngresDataType(Class<? extends T> type, String typeName, String castTypeName) {
        super(SQLDialect.INGRES, type, typeName, castTypeName);
    }

    public static DataType<?> getDataType(String typeName) {
        return getDataType(SQLDialect.INGRES, typeName);
    }

    public static <T> DataType<T> getDataType(Class<? extends T> type) {
        return getDataType(SQLDialect.INGRES, type);
    }

    public static DataType<Object> getDefaultDataType(String typeName) {
        return getDefaultDataType(SQLDialect.INGRES, typeName);
    }
}
