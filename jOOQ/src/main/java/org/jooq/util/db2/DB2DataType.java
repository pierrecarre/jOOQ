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
package org.jooq.util.db2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.jooq.DataType;
import org.jooq.SQLDialect;
import org.jooq.impl.AbstractDataType;

/**
 * Supported data types for the {@link SQLDialect#DB2} dialect
 *
 * @see <a href="http://publib.boulder.ibm.com/infocenter/db2luw/v9/index.jsp?topic=/com.ibm.db2.udb.apdv.java.doc/doc/rjvjdata.htm">http://publib.boulder.ibm.com/infocenter/db2luw/v9/index.jsp?topic=/com.ibm.db2.udb.apdv.java.doc/doc/rjvjdata.htm</a>
 * @author Espen Stromsnes
 */
public class DB2DataType<T> extends AbstractDataType<T> {

    /**
     * Generated UID
     */
    private static final long                   serialVersionUID  = -5677365115109672781L;

    public static final DB2DataType<Short>      SMALLINT          = new DB2DataType<Short>(Short.class, "smallint");
    public static final DB2DataType<Integer>    INTEGER           = new DB2DataType<Integer>(Integer.class, "integer");
    public static final DB2DataType<Long>       BIGINT            = new DB2DataType<Long>(Long.class, "bigint");
    public static final DB2DataType<Float>      REAL              = new DB2DataType<Float>(Float.class, "real");
    public static final DB2DataType<Double>     DOUBLE            = new DB2DataType<Double>(Double.class, "double");
    public static final DB2DataType<BigDecimal> DECIMAL           = new DB2DataType<BigDecimal>(BigDecimal.class, "decimal");
    public static final DB2DataType<BigDecimal> DECFLOAT          = new DB2DataType<BigDecimal>(BigDecimal.class, "decfloat");
    public static final DB2DataType<String>     VARCHAR           = new DB2DataType<String>(String.class, "varchar", "varchar(32672)");
    public static final DB2DataType<String>     CHAR              = new DB2DataType<String>(String.class, "char");
    public static final DB2DataType<String>     CHARACTER         = new DB2DataType<String>(String.class, "character");
    public static final DB2DataType<String>     LONGVARCHAR       = new DB2DataType<String>(String.class, "long varchar");
    public static final DB2DataType<String>     CLOB              = new DB2DataType<String>(String.class, "clob");
    public static final DB2DataType<String>     DBCLOB            = new DB2DataType<String>(String.class, "dbclob");
    public static final DB2DataType<String>     GRAPHIC           = new DB2DataType<String>(String.class, "graphic");
    public static final DB2DataType<String>     VARGRAPHIC        = new DB2DataType<String>(String.class, "vargraphic");
    public static final DB2DataType<String>     XML               = new DB2DataType<String>(String.class, "xml");
    public static final DB2DataType<byte[]>     BLOB              = new DB2DataType<byte[]>(byte[].class, "blob");
    public static final DB2DataType<byte[]>     CHARFORBITDATA    = new DB2DataType<byte[]>(byte[].class, "char for bit data");
    public static final DB2DataType<byte[]>     VARCHARFORBITDATA = new DB2DataType<byte[]>(byte[].class, "varchar(32672) for bit data");
    public static final DB2DataType<byte[]>     BINARY            = new DB2DataType<byte[]>(byte[].class, "binary");
    public static final DB2DataType<byte[]>     VARBINARY         = new DB2DataType<byte[]>(byte[].class, "varbinary");
    public static final DB2DataType<byte[]>     ROWID             = new DB2DataType<byte[]>(byte[].class, "rowid");
    public static final DB2DataType<Date>       DATE              = new DB2DataType<Date>(Date.class, "date");
    public static final DB2DataType<Time>       TIME              = new DB2DataType<Time>(Time.class, "time");
    public static final DB2DataType<Timestamp>  TIMESTAMP         = new DB2DataType<Timestamp>(Timestamp.class, "timestamp");

    // Pseudo data types used for compatibility in casting
    protected static final DB2DataType<BigInteger> __BIGINTEGER   = new DB2DataType<BigInteger>(BigInteger.class, "decimal");
    protected static final DB2DataType<Boolean>    __BOOLEAN      = new DB2DataType<Boolean>(Boolean.class, "smallint");
    protected static final DB2DataType<Byte>       __BYTE         = new DB2DataType<Byte>(Byte.class, "smallint");

    private DB2DataType(Class<? extends T> type, String typeName) {
        super(SQLDialect.DB2, type, typeName);
    }

    private DB2DataType(Class<? extends T> type, String typeName, String castTypeName) {
        super(SQLDialect.DB2, type, typeName, castTypeName);
    }

    public static DataType<?> getDataType(String typeName) {
        return getDataType(SQLDialect.DB2, typeName);
    }

    public static <T> DataType<T> getDataType(Class<? extends T> type) {
        return getDataType(SQLDialect.DB2, type);
    }

    public static DataType<Object> getDefaultDataType(String typeName) {
        return getDefaultDataType(SQLDialect.DB2, typeName);
    }
}
