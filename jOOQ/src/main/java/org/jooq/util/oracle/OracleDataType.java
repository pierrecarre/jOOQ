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
 * . Redistributions in binary form must reproduce the above copyright notice, *   this list of conditions and the following disclaimer in the documentation
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

package org.jooq.util.oracle;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.jooq.DataType;
import org.jooq.SQLDialect;
import org.jooq.impl.AbstractDataType;

/**
 * Supported data types for the {@link SQLDialect#ORACLE} dialect
 *
 * @author Lukas Eder
 * @see <a href="http://www.techonthenet.com/oracle/datatypes.php">http://www.techonthenet.com/oracle/datatypes.php</a>
 * @see <a href="http://download.oracle.com/docs/cd/B19306_01/appdev.102/b14261/datatypes.htm">http://download.oracle.com/docs/cd/B19306_01/appdev.102/b14261/datatypes.htm</a>
 */
public class OracleDataType<T> extends AbstractDataType<T> {

    /**
     * Generated UID
     */
    private static final long                      serialVersionUID       = -5677365115109672781L;

    public static final OracleDataType<String>     VARCHAR2               = new OracleDataType<String>(String.class, "varchar2", "varchar2(4000)");
    public static final OracleDataType<String>     CHAR                   = new OracleDataType<String>(String.class, "char", "varchar2(4000)");
    public static final OracleDataType<String>     NCHAR                  = new OracleDataType<String>(String.class, "nchar", "varchar2(4000)");
    public static final OracleDataType<String>     NVARCHAR2              = new OracleDataType<String>(String.class, "nvarchar2", "varchar2(4000)");
    public static final OracleDataType<String>     NVARCHAR               = new OracleDataType<String>(String.class, "nvarchar", "varchar2(4000)");
    public static final OracleDataType<String>     VARCHAR                = new OracleDataType<String>(String.class, "varchar", "varchar2(4000)");
    public static final OracleDataType<String>     LONG                   = new OracleDataType<String>(String.class, "long");
    public static final OracleDataType<String>     CLOB                   = new OracleDataType<String>(String.class, "clob");
    public static final OracleDataType<String>     NCLOB                  = new OracleDataType<String>(String.class, "nclob");
    public static final OracleDataType<BigDecimal> NUMBER                 = new OracleDataType<BigDecimal>(BigDecimal.class, "number", true);
    public static final OracleDataType<BigDecimal> NUMERIC                = new OracleDataType<BigDecimal>(BigDecimal.class, "numeric", true);
    public static final OracleDataType<BigDecimal> DEC                    = new OracleDataType<BigDecimal>(BigDecimal.class, "dec", true);
    public static final OracleDataType<BigDecimal> DECIMAL                = new OracleDataType<BigDecimal>(BigDecimal.class, "decimal", true);
    public static final OracleDataType<Date>       DATE                   = new OracleDataType<Date>(Date.class, "date");
    public static final OracleDataType<Timestamp>  TIMESTAMP              = new OracleDataType<Timestamp>(Timestamp.class, "timestamp");
    public static final OracleDataType<Timestamp>  TIMESTAMP6             = new OracleDataType<Timestamp>(Timestamp.class, "timestamp");
    public static final OracleDataType<Timestamp>  TIMESTAMP6WITHTIMEZONE = new OracleDataType<Timestamp>(Timestamp.class, "timestamp");
    public static final OracleDataType<byte[]>     BLOB                   = new OracleDataType<byte[]>(byte[].class, "blob");
    public static final OracleDataType<byte[]>     RAW                    = new OracleDataType<byte[]>(byte[].class, "raw");
    public static final OracleDataType<byte[]>     LONGRAW                = new OracleDataType<byte[]>(byte[].class, "longraw");
    public static final OracleDataType<byte[]>     BFILE                  = new OracleDataType<byte[]>(byte[].class, "bfile");

    // Pseudo data types used for compatibility in casting
    protected static final OracleDataType<Boolean>    __BOOLEAN           = new OracleDataType<Boolean>(Boolean.class, "number");
    protected static final OracleDataType<Byte>       __BYTE              = new OracleDataType<Byte>(Byte.class, "number");
    protected static final OracleDataType<Short>      __SHORT             = new OracleDataType<Short>(Short.class, "number");
    protected static final OracleDataType<Integer>    __INTEGER           = new OracleDataType<Integer>(Integer.class, "number");
    protected static final OracleDataType<Long>       __LONG              = new OracleDataType<Long>(Long.class, "number");
    protected static final OracleDataType<Float>      __FLOAT             = new OracleDataType<Float>(Float.class, "number");
    protected static final OracleDataType<Double>     __DOUBLE            = new OracleDataType<Double>(Double.class, "number");
    protected static final OracleDataType<BigInteger> __BIGINTEGER        = new OracleDataType<BigInteger>(BigInteger.class, "number");
    protected static final OracleDataType<Time>       __TIME              = new OracleDataType<Time>(Time.class, "timestamp");

    // PL/SQL data types
    public static final OracleDataType<Integer>    BINARY_INTEGER         = new OracleDataType<Integer>(Integer.class, "binary_integer");
    public static final OracleDataType<Integer>    PLS_INTEGER            = new OracleDataType<Integer>(Integer.class, "pls_integer");
    public static final OracleDataType<Integer>    NATURAL                = new OracleDataType<Integer>(Integer.class, "natural");
    public static final OracleDataType<Integer>    NATURALN               = new OracleDataType<Integer>(Integer.class, "naturaln");
    public static final OracleDataType<Integer>    POSITIVE               = new OracleDataType<Integer>(Integer.class, "positive");
    public static final OracleDataType<Integer>    POSITIVEN              = new OracleDataType<Integer>(Integer.class, "positiven");
    public static final OracleDataType<Integer>    SIGNTYPE               = new OracleDataType<Integer>(Integer.class, "signtype");
    public static final OracleDataType<Double>     REAL                   = new OracleDataType<Double>(Double.class, "real");
    public static final OracleDataType<Double>     DOUBLE_PRECISION       = new OracleDataType<Double>(Double.class, "double_precision");
    public static final OracleDataType<Double>     BINARY_DOUBLE          = new OracleDataType<Double>(Double.class, "binary_double");
    public static final OracleDataType<BigDecimal> FLOAT                  = new OracleDataType<BigDecimal>(BigDecimal.class, "float");
    public static final OracleDataType<BigDecimal> BINARY_FLOAT           = new OracleDataType<BigDecimal>(BigDecimal.class, "binary_float");
    public static final OracleDataType<BigInteger> INTEGER                = new OracleDataType<BigInteger>(BigInteger.class, "integer");
    public static final OracleDataType<BigInteger> INT                    = new OracleDataType<BigInteger>(BigInteger.class, "int");
    public static final OracleDataType<BigInteger> SMALLINT               = new OracleDataType<BigInteger>(BigInteger.class, "smallint");
    public static final OracleDataType<Boolean>    BOOLEAN                = new OracleDataType<Boolean>(Boolean.class, "boolean");

    private OracleDataType(Class<? extends T> type, String typeName) {
        this(type, typeName, false);
    }

    private OracleDataType(Class<? extends T> type, String typeName, String castTypeName) {
        this(type, typeName, castTypeName, false);
    }

    private OracleDataType(Class<? extends T> type, String typeName, boolean hasPrecisionAndScale) {
        super(SQLDialect.ORACLE, type, typeName, hasPrecisionAndScale);
    }

    private OracleDataType(Class<? extends T> type, String typeName, String castTypeName, boolean hasPrecisionAndScale) {
        super(SQLDialect.ORACLE, type, typeName, castTypeName, hasPrecisionAndScale);
    }

    public static DataType<?> getDataType(String typeName) {
        return getDataType(SQLDialect.ORACLE, typeName);
    }

    public static <T> DataType<T> getDataType(Class<? extends T> type) {
        return getDataType(SQLDialect.ORACLE, type);
    }

    public static DataType<Object> getDefaultDataType(String typeName) {
        return getDefaultDataType(SQLDialect.ORACLE, typeName);
    }
}
