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

package org.jooq.util.hsqldb;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.jooq.DataType;
import org.jooq.SQLDialect;
import org.jooq.impl.AbstractDataType;

/**
 * Supported data types for the {@link SQLDialect#HSQLDB} dialect
 *
 * @author Lukas Eder
 * @see http://hsqldb.org/doc/guide/ch09.html#datatypes-section
 * @see http://hsqldb.org/doc/2.0/guide/sqlgeneral-chapt.html#sqlgeneral_types_ops-sect
 */
public class HSQLDBDataType<T> extends AbstractDataType<T> {

    public static final HSQLDBDataType<Integer>    INT               = new HSQLDBDataType<Integer>(Integer.class, "int");
    public static final HSQLDBDataType<Integer>    INTEGER           = new HSQLDBDataType<Integer>(Integer.class, "integer");
    public static final HSQLDBDataType<Byte>       TINYINT           = new HSQLDBDataType<Byte>(Byte.class, "tinyint");
    public static final HSQLDBDataType<Short>      SMALLINT          = new HSQLDBDataType<Short>(Short.class, "smallint");
    public static final HSQLDBDataType<Long>       BIGINT            = new HSQLDBDataType<Long>(Long.class, "bigint");
    public static final HSQLDBDataType<Double>     DOUBLE            = new HSQLDBDataType<Double>(Double.class, "double");
    public static final HSQLDBDataType<Double>     DOUBLEPRECISION   = new HSQLDBDataType<Double>(Double.class, "double precision");
    public static final HSQLDBDataType<Double>     FLOAT             = new HSQLDBDataType<Double>(Double.class, "float");
    public static final HSQLDBDataType<Float>      REAL              = new HSQLDBDataType<Float>(Float.class, "double");
    public static final HSQLDBDataType<String>     LONGVARCHAR       = new HSQLDBDataType<String>(String.class, "longvarchar");
    public static final HSQLDBDataType<String>     VARCHAR           = new HSQLDBDataType<String>(String.class, "varchar");
    public static final HSQLDBDataType<String>     VARCHARIGNORECASE = new HSQLDBDataType<String>(String.class, "varchar_ignorecase");
    public static final HSQLDBDataType<String>     CHAR              = new HSQLDBDataType<String>(String.class, "char");
    public static final HSQLDBDataType<String>     CHARACTER         = new HSQLDBDataType<String>(String.class, "character");
    public static final HSQLDBDataType<String>     CHARACTERVARYING  = new HSQLDBDataType<String>(String.class, "character varying");

    public static final HSQLDBDataType<Date>       DATE              = new HSQLDBDataType<Date>(Date.class, "date");
    public static final HSQLDBDataType<Time>       TIME              = new HSQLDBDataType<Time>(Time.class, "time");
    public static final HSQLDBDataType<Timestamp>  TIMESTAMP         = new HSQLDBDataType<Timestamp>(Timestamp.class, "timestamp");
    public static final HSQLDBDataType<Timestamp>  DATETIME          = new HSQLDBDataType<Timestamp>(Timestamp.class, "datetime");
    public static final HSQLDBDataType<BigDecimal> DECIMAL           = new HSQLDBDataType<BigDecimal>(BigDecimal.class, "decimal");
    public static final HSQLDBDataType<BigDecimal> NUMERIC           = new HSQLDBDataType<BigDecimal>(BigDecimal.class, "numeric");
    public static final HSQLDBDataType<Boolean>    BOOLEAN           = new HSQLDBDataType<Boolean>(Boolean.class, "boolean");
    public static final HSQLDBDataType<Boolean>    BIT               = new HSQLDBDataType<Boolean>(Boolean.class, "bit");
    public static final HSQLDBDataType<byte[]>     LONGVARBINARY     = new HSQLDBDataType<byte[]>(byte[].class, "longvarbinary");
    public static final HSQLDBDataType<byte[]>     VARBINARY         = new HSQLDBDataType<byte[]>(byte[].class, "varbinary");
    public static final HSQLDBDataType<byte[]>     BINARY            = new HSQLDBDataType<byte[]>(byte[].class, "binary");
    public static final HSQLDBDataType<Object>     OTHER             = new HSQLDBDataType<Object>(Object.class, "other");
    public static final HSQLDBDataType<Object>     OBJECT            = new HSQLDBDataType<Object>(Object.class, "object");

    // Pseudo data types used for compatibility in casting
    protected static final HSQLDBDataType<BigInteger> __BIGINTEGER   = new HSQLDBDataType<BigInteger>(BigInteger.class, "decimal");
    protected static final HSQLDBDataType<Byte>       __BYTE         = new HSQLDBDataType<Byte>(Byte.class, "smallint");

    private HSQLDBDataType(Class<? extends T> type, String typeName) {
        super(SQLDialect.HSQLDB, type, typeName);
    }

    public static DataType<?> getDataType(String typeName) {
        return getDataType(SQLDialect.HSQLDB, typeName);
    }

    public static <T> DataType<T> getDataType(Class<? extends T> type) {
        return getDataType(SQLDialect.HSQLDB, type);
    }

    public static DataType<Object> getDefaultDataType(String typeName) {
        return getDefaultDataType(SQLDialect.HSQLDB, typeName);
    }
}
