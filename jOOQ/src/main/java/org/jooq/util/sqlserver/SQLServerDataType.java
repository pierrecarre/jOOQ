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
package org.jooq.util.sqlserver;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.jooq.DataType;
import org.jooq.SQLDialect;
import org.jooq.impl.AbstractDataType;

/**
 * Supported data types for the {@link SQLDialect#SQLSERVER} dialect
 *
 * @author Lukas Eder
 * @see <a href="http://msdn.microsoft.com/en-us/library/aa258271%28v=sql.80%29.aspx">http://msdn.microsoft.com/en-us/library/aa258271%28v=sql.80%29.aspx</a>
 */
public class SQLServerDataType<T> extends AbstractDataType<T> {

    /**
     * Generated UID
     */
    private static final long                         serialVersionUID = -5677365115109672781L;

    public static final SQLServerDataType<Byte>       TINYINT          = new SQLServerDataType<Byte>(Byte.class, "tinyint");
    public static final SQLServerDataType<Short>      SMALLINT         = new SQLServerDataType<Short>(Short.class, "smallint");
    public static final SQLServerDataType<Integer>    INT              = new SQLServerDataType<Integer>(Integer.class, "int");
    public static final SQLServerDataType<Long>       BIGINT           = new SQLServerDataType<Long>(Long.class, "bigint");
    public static final SQLServerDataType<Double>     FLOAT            = new SQLServerDataType<Double>(Double.class, "float");
    public static final SQLServerDataType<Float>      REAL             = new SQLServerDataType<Float>(Float.class, "real");

    public static final SQLServerDataType<Date>       DATE             = new SQLServerDataType<Date>(Date.class, "date");
    public static final SQLServerDataType<Timestamp>  SMALLDATETIME    = new SQLServerDataType<Timestamp>(Timestamp.class, "smalldatetime");
    public static final SQLServerDataType<Timestamp>  DATETIME         = new SQLServerDataType<Timestamp>(Timestamp.class, "datetime");
    public static final SQLServerDataType<Timestamp>  DATETIME2        = new SQLServerDataType<Timestamp>(Timestamp.class, "datetime2");
    public static final SQLServerDataType<Timestamp>  DATETIMEOFFSET   = new SQLServerDataType<Timestamp>(Timestamp.class, "datetimeoffset");
    public static final SQLServerDataType<Time>       TIME             = new SQLServerDataType<Time>(Time.class, "time");

    public static final SQLServerDataType<Boolean>    BIT              = new SQLServerDataType<Boolean>(Boolean.class, "bit");

    public static final SQLServerDataType<BigDecimal> NUMERIC          = new SQLServerDataType<BigDecimal>(BigDecimal.class, "numeric");
    public static final SQLServerDataType<BigDecimal> DECIMAL          = new SQLServerDataType<BigDecimal>(BigDecimal.class, "decimal");
    public static final SQLServerDataType<BigDecimal> MONEY            = new SQLServerDataType<BigDecimal>(BigDecimal.class, "money");
    public static final SQLServerDataType<BigDecimal> SMALLMONEY       = new SQLServerDataType<BigDecimal>(BigDecimal.class, "smallmoney");
    public static final SQLServerDataType<String>     VARCHAR          = new SQLServerDataType<String>(String.class, "varchar");
    public static final SQLServerDataType<String>     NVARCHAR         = new SQLServerDataType<String>(String.class, "nvarchar");
    public static final SQLServerDataType<String>     CHAR             = new SQLServerDataType<String>(String.class, "char");
    public static final SQLServerDataType<String>     NCHAR            = new SQLServerDataType<String>(String.class, "nchar");

    public static final SQLServerDataType<String>     TEXT             = new SQLServerDataType<String>(String.class, "text");
    public static final SQLServerDataType<String>     NTEXT            = new SQLServerDataType<String>(String.class, "ntext");
    public static final SQLServerDataType<byte[]>     VARBINARY        = new SQLServerDataType<byte[]>(byte[].class, "varbinary", "varbinary(max)");
    public static final SQLServerDataType<byte[]>     BINARY           = new SQLServerDataType<byte[]>(byte[].class, "binary");
    public static final SQLServerDataType<byte[]>     IMAGE            = new SQLServerDataType<byte[]>(byte[].class, "image");

    // Pseudo data types used for compatibility in casting
    public static final SQLServerDataType<BigInteger> __BIGINTEGER     = new SQLServerDataType<BigInteger>(BigInteger.class, "numeric");

    private SQLServerDataType(Class<? extends T> type, String typeName) {
        super(SQLDialect.SQLSERVER, type, typeName);
    }

    private SQLServerDataType(Class<? extends T> type, String typeName, String castTypeName) {
        super(SQLDialect.SQLSERVER, type, typeName, castTypeName);
    }

    public static DataType<?> getDataType(String typeName) {
        return getDataType(SQLDialect.SQLSERVER, typeName);
    }

    public static <T> DataType<T> getDataType(Class<? extends T> type) {
        return getDataType(SQLDialect.SQLSERVER, type);
    }

    public static DataType<Object> getDefaultDataType(String typeName) {
        return getDefaultDataType(SQLDialect.SQLSERVER, typeName);
    }
}
