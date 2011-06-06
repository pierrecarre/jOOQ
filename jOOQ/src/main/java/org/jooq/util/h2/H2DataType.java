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
package org.jooq.util.h2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.jooq.DataType;
import org.jooq.SQLDialect;
import org.jooq.impl.AbstractDataType;

/**
 * Supported data types for the {@link SQLDialect#H2} dialect
 *
 * @author Lukas Eder
 * @see <a href="http://www.h2database.com/html/datatypes.html">http://www.h2database.com/html/datatypes.html</a>
 */
public class H2DataType<T> extends AbstractDataType<T> {

    /**
     * Generated UID
     */
    private static final long                  serialVersionUID      = -5677365115109672781L;

    public static final H2DataType<Integer>    INT                   = new H2DataType<Integer>(Integer.class, "int");
    public static final H2DataType<Integer>    INTEGER               = new H2DataType<Integer>(Integer.class, "integer");
    public static final H2DataType<Integer>    MEDIUMINT             = new H2DataType<Integer>(Integer.class, "mediumint");
    public static final H2DataType<Integer>    INT4                  = new H2DataType<Integer>(Integer.class, "int4");
    public static final H2DataType<Integer>    SIGNED                = new H2DataType<Integer>(Integer.class, "signed");
    public static final H2DataType<Boolean>    BOOLEAN               = new H2DataType<Boolean>(Boolean.class, "boolean");
    public static final H2DataType<Boolean>    BIT                   = new H2DataType<Boolean>(Boolean.class, "bit");
    public static final H2DataType<Boolean>    BOOL                  = new H2DataType<Boolean>(Boolean.class, "bool");
    public static final H2DataType<Byte>       TINYINT               = new H2DataType<Byte>(Byte.class, "tinyint");
    public static final H2DataType<Short>      SMALLINT              = new H2DataType<Short>(Short.class, "smallint");
    public static final H2DataType<Short>      INT2                  = new H2DataType<Short>(Short.class, "int2");
    public static final H2DataType<Short>      YEAR                  = new H2DataType<Short>(Short.class, "year");
    public static final H2DataType<Long>       BIGINT                = new H2DataType<Long>(Long.class, "bigint");
    public static final H2DataType<Long>       INT8                  = new H2DataType<Long>(Long.class, "int8");
    public static final H2DataType<Long>       IDENTITY              = new H2DataType<Long>(Long.class, "identity");
    public static final H2DataType<BigDecimal> DECIMAL               = new H2DataType<BigDecimal>(BigDecimal.class, "decimal");
    public static final H2DataType<BigDecimal> NUMBER                = new H2DataType<BigDecimal>(BigDecimal.class, "number");
    public static final H2DataType<BigDecimal> DEC                   = new H2DataType<BigDecimal>(BigDecimal.class, "dec");
    public static final H2DataType<BigDecimal> NUMERIC               = new H2DataType<BigDecimal>(BigDecimal.class, "numeric");
    public static final H2DataType<Double>     DOUBLE                = new H2DataType<Double>(Double.class, "double");
    public static final H2DataType<Double>     FLOAT                 = new H2DataType<Double>(Double.class, "float");
    public static final H2DataType<Double>     FLOAT4                = new H2DataType<Double>(Double.class, "float4");
    public static final H2DataType<Double>     FLOAT8                = new H2DataType<Double>(Double.class, "float8");
    public static final H2DataType<Float>      REAL                  = new H2DataType<Float>(Float.class, "real");
    public static final H2DataType<Time>       TIME                  = new H2DataType<Time>(Time.class, "time");
    public static final H2DataType<Date>       DATE                  = new H2DataType<Date>(Date.class, "date");
    public static final H2DataType<Timestamp>  TIMESTAMP             = new H2DataType<Timestamp>(Timestamp.class, "timestamp");
    public static final H2DataType<Timestamp>  DATETIME              = new H2DataType<Timestamp>(Timestamp.class, "datetime");
    public static final H2DataType<Timestamp>  SMALLDATETIME         = new H2DataType<Timestamp>(Timestamp.class, "smalldatetime");
    public static final H2DataType<byte[]>     BINARY                = new H2DataType<byte[]>(byte[].class, "binary");
    public static final H2DataType<byte[]>     VARBINARY             = new H2DataType<byte[]>(byte[].class, "varbinary");
    public static final H2DataType<byte[]>     LONGVARBINARY         = new H2DataType<byte[]>(byte[].class, "longvarbinary");
    public static final H2DataType<byte[]>     RAW                   = new H2DataType<byte[]>(byte[].class, "raw");
    public static final H2DataType<byte[]>     BYTEA                 = new H2DataType<byte[]>(byte[].class, "bytea");
    public static final H2DataType<byte[]>     BLOB                  = new H2DataType<byte[]>(byte[].class, "blob");
    public static final H2DataType<byte[]>     TINYBLOB              = new H2DataType<byte[]>(byte[].class, "tinyblob");
    public static final H2DataType<byte[]>     MEDIUMBLOB            = new H2DataType<byte[]>(byte[].class, "mediumblob");
    public static final H2DataType<byte[]>     LONGBLOB              = new H2DataType<byte[]>(byte[].class, "longblob");
    public static final H2DataType<byte[]>     IMAGE                 = new H2DataType<byte[]>(byte[].class, "image");
    public static final H2DataType<byte[]>     OID                   = new H2DataType<byte[]>(byte[].class, "oid");
    public static final H2DataType<Object>     OTHER                 = new H2DataType<Object>(Object.class, "other");
    public static final H2DataType<String>     VARCHAR               = new H2DataType<String>(String.class, "varchar");
    public static final H2DataType<String>     LONGVARCHAR           = new H2DataType<String>(String.class, "longvarchar");
    public static final H2DataType<String>     VARCHAR2              = new H2DataType<String>(String.class, "varchar2");
    public static final H2DataType<String>     NVARCHAR              = new H2DataType<String>(String.class, "nvarchar");
    public static final H2DataType<String>     NVARCHAR2             = new H2DataType<String>(String.class, "nvarchar2");
    public static final H2DataType<String>     VARCHAR_CASESENSITIVE = new H2DataType<String>(String.class, "varchar_casesensitive");
    public static final H2DataType<String>     VARCHAR_IGNORECASE    = new H2DataType<String>(String.class, "varchar_ignorecase");
    public static final H2DataType<String>     CHAR                  = new H2DataType<String>(String.class, "char");
    public static final H2DataType<String>     CHARACTER             = new H2DataType<String>(String.class, "character");
    public static final H2DataType<String>     NCHAR                 = new H2DataType<String>(String.class, "nchar");
    public static final H2DataType<String>     CLOB                  = new H2DataType<String>(String.class, "clob");
    public static final H2DataType<String>     TINYTEXT              = new H2DataType<String>(String.class, "tinytext");
    public static final H2DataType<String>     TEXT                  = new H2DataType<String>(String.class, "text");
    public static final H2DataType<String>     MEDIUMTEXT            = new H2DataType<String>(String.class, "mediumtext");
    public static final H2DataType<String>     LONGTEXT              = new H2DataType<String>(String.class, "longtext");
    public static final H2DataType<String>     NTEXT                 = new H2DataType<String>(String.class, "ntext");
    public static final H2DataType<String>     NCLOB                 = new H2DataType<String>(String.class, "nclob");
    public static final H2DataType<String>     UUID                  = new H2DataType<String>(String.class, "uuid");


    // Pseudo data types used for compatibility in casting
    protected static final H2DataType<BigInteger> __BIGINTEGER       = new H2DataType<BigInteger>(BigInteger.class, "decimal");

    private H2DataType(Class<? extends T> type, String typeName) {
        super(SQLDialect.H2, type, typeName);
    }

    public static DataType<?> getDataType(String typeName) {
        return getDataType(SQLDialect.H2, typeName);
    }

    public static <T> DataType<T> getDataType(Class<? extends T> type) {
        return getDataType(SQLDialect.H2, type);
    }

    public static DataType<Object> getDefaultDataType(String typeName) {
        return getDefaultDataType(SQLDialect.H2, typeName);
    }
}
