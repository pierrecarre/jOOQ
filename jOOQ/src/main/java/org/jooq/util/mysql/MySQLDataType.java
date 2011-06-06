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

package org.jooq.util.mysql;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.jooq.DataType;
import org.jooq.SQLDialect;
import org.jooq.impl.AbstractDataType;

/**
 * Supported data types for the {@link SQLDialect#MYSQL} dialect
 *
 * @author Lukas Eder
 * @see <a href="http://dev.mysql.com/doc/refman/5.5/en/data-types.html">http://dev.mysql.com/doc/refman/5.5/en/data-types.html</a>
 * @see <a href="http://dev.mysql.com/doc/refman/5.5/en/cast-functions.html#function_cast">http://dev.mysql.com/doc/refman/5.5/en/cast-functions.html#function_cast</a>
 */
public class MySQLDataType<T> extends AbstractDataType<T> {

    /**
     * Generated UID
     */
    private static final long                     serialVersionUID = -5677365115109672781L;

    public static final MySQLDataType<Boolean>    BOOLEAN          = new MySQLDataType<Boolean>(Boolean.class, "boolean", "unsigned");
    public static final MySQLDataType<Boolean>    BOOL             = new MySQLDataType<Boolean>(Boolean.class, "bool", "unsigned");
    public static final MySQLDataType<Boolean>    BIT              = new MySQLDataType<Boolean>(Boolean.class, "bit", "unsigned");
    public static final MySQLDataType<Byte>       TINYINT          = new MySQLDataType<Byte>(Byte.class, "tinyint", "signed");
    public static final MySQLDataType<Short>      SMALLINT         = new MySQLDataType<Short>(Short.class, "smallint", "signed");
    public static final MySQLDataType<Integer>    INT              = new MySQLDataType<Integer>(Integer.class, "int", "signed");
    public static final MySQLDataType<Integer>    MEDIUMINT        = new MySQLDataType<Integer>(Integer.class, "mediumint", "signed");
    public static final MySQLDataType<Integer>    INTEGER          = new MySQLDataType<Integer>(Integer.class, "integer", "signed");
    public static final MySQLDataType<Long>       BIGINT           = new MySQLDataType<Long>(Long.class, "bigint", "signed");
    public static final MySQLDataType<Float>      FLOAT            = new MySQLDataType<Float>(Float.class, "float", "decimal");
    public static final MySQLDataType<Double>     DOUBLE           = new MySQLDataType<Double>(Double.class, "double", "decimal");
    public static final MySQLDataType<BigDecimal> DECIMAL          = new MySQLDataType<BigDecimal>(BigDecimal.class, "decimal", "decimal");
    public static final MySQLDataType<BigDecimal> DEC              = new MySQLDataType<BigDecimal>(BigDecimal.class, "dec", "decimal");
    public static final MySQLDataType<String>     TEXT             = new MySQLDataType<String>(String.class, "text", "char");
    public static final MySQLDataType<String>     VARCHAR          = new MySQLDataType<String>(String.class, "varchar", "char");
    public static final MySQLDataType<String>     CHAR             = new MySQLDataType<String>(String.class, "char", "char");
    public static final MySQLDataType<String>     MEDIUMTEXT       = new MySQLDataType<String>(String.class, "mediumtext", "char");
    public static final MySQLDataType<String>     LONGTEXT         = new MySQLDataType<String>(String.class, "longtext", "char");
    public static final MySQLDataType<String>     SET              = new MySQLDataType<String>(String.class, "set", "char");
    public static final MySQLDataType<byte[]>     BINARY           = new MySQLDataType<byte[]>(byte[].class, "binary", "binary");
    public static final MySQLDataType<byte[]>     VARBINARY        = new MySQLDataType<byte[]>(byte[].class, "varbinary", "binary");
    public static final MySQLDataType<byte[]>     TINYBLOB         = new MySQLDataType<byte[]>(byte[].class, "tinyblob", "binary");
    public static final MySQLDataType<byte[]>     BLOB             = new MySQLDataType<byte[]>(byte[].class, "blob", "binary");
    public static final MySQLDataType<byte[]>     MEDIUMBLOB       = new MySQLDataType<byte[]>(byte[].class, "mediumblob", "binary");
    public static final MySQLDataType<byte[]>     LONGBLOB         = new MySQLDataType<byte[]>(byte[].class, "longblob", "binary");
    public static final MySQLDataType<Date>       DATE             = new MySQLDataType<Date>(Date.class, "date", "date");
    public static final MySQLDataType<Date>       YEAR             = new MySQLDataType<Date>(Date.class, "year", "date");
    public static final MySQLDataType<Time>       TIME             = new MySQLDataType<Time>(Time.class, "time", "time");
    public static final MySQLDataType<Timestamp>  TIMESTAMP        = new MySQLDataType<Timestamp>(Timestamp.class, "timestamp", "datetime");
    public static final MySQLDataType<Timestamp>  DATETIME         = new MySQLDataType<Timestamp>(Timestamp.class, "datetime", "datetime");

    // Pseudo data types used for compatibility in casting
    protected static final MySQLDataType<BigInteger> __BIGINTEGER = new MySQLDataType<BigInteger>(BigInteger.class, "decimal", "decimal");

    private MySQLDataType(Class<? extends T> type, String typeName, String castTypeName) {
        super(SQLDialect.MYSQL, type, typeName, castTypeName);
    }

    public static DataType<?> getDataType(String typeName) {
        return getDataType(SQLDialect.MYSQL, typeName);
    }

    public static <T> DataType<T> getDataType(Class<? extends T> type) {
        return getDataType(SQLDialect.MYSQL, type);
    }

    public static DataType<Object> getDefaultDataType(String typeName) {
        return getDefaultDataType(SQLDialect.MYSQL, typeName);
    }
}
