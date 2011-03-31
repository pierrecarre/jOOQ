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

package org.jooq.util.postgres;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.jooq.DataType;
import org.jooq.SQLDialect;
import org.jooq.impl.AbstractDataType;

/**
 * Supported data types for the {@link SQLDialect#POSTGRES} dialect
 *
 * @author Lukas Eder
 * @see http://www.postgresql.org/docs/8.4/interactive/datatype.html
 */
public class PostgresDataType<T> extends AbstractDataType<T> {

    public static final PostgresDataType<Long>       BIGINT                   = new PostgresDataType<Long>(Long.class, "bigint");
    public static final PostgresDataType<Long>       INT8                     = new PostgresDataType<Long>(Long.class, "int8");
    public static final PostgresDataType<Long>       BIGSERIAL                = new PostgresDataType<Long>(Long.class, "bigserial");
    public static final PostgresDataType<Long>       SERIAL8                  = new PostgresDataType<Long>(Long.class, "serial8");
    public static final PostgresDataType<String>     TEXT                     = new PostgresDataType<String>(String.class, "text");
    public static final PostgresDataType<String>     VARCHAR                  = new PostgresDataType<String>(String.class, "varchar");
    public static final PostgresDataType<String>     BIT                      = new PostgresDataType<String>(String.class, "bit");
    public static final PostgresDataType<String>     BITVARYING               = new PostgresDataType<String>(String.class, "bit varying");
    public static final PostgresDataType<String>     VARBIT                   = new PostgresDataType<String>(String.class, "varbit");
    public static final PostgresDataType<String>     CHARACTERVARYING         = new PostgresDataType<String>(String.class, "character varying");
    public static final PostgresDataType<String>     CHARACTER                = new PostgresDataType<String>(String.class, "character");
    public static final PostgresDataType<String>     CHAR                     = new PostgresDataType<String>(String.class, "char");
    public static final PostgresDataType<Boolean>    BOOLEAN                  = new PostgresDataType<Boolean>(Boolean.class, "boolean");
    public static final PostgresDataType<Boolean>    BOOL                     = new PostgresDataType<Boolean>(Boolean.class, "bool");
    public static final PostgresDataType<byte[]>     BYTEA                    = new PostgresDataType<byte[]>(byte[].class, "bytea");
    public static final PostgresDataType<Double>     DOUBLEPRECISION          = new PostgresDataType<Double>(Double.class, "double precision");
    public static final PostgresDataType<Double>     FLOAT8                   = new PostgresDataType<Double>(Double.class, "float8");
    public static final PostgresDataType<Integer>    INT                      = new PostgresDataType<Integer>(Integer.class, "int");
    public static final PostgresDataType<Integer>    INTEGER                  = new PostgresDataType<Integer>(Integer.class, "integer");
    public static final PostgresDataType<Integer>    INT4                     = new PostgresDataType<Integer>(Integer.class, "int4");
    public static final PostgresDataType<Integer>    SERIAL                   = new PostgresDataType<Integer>(Integer.class, "serial");
    public static final PostgresDataType<Integer>    SERIAL4                  = new PostgresDataType<Integer>(Integer.class, "serial4");
    public static final PostgresDataType<BigDecimal> NUMERIC                  = new PostgresDataType<BigDecimal>(BigDecimal.class, "numeric");
    public static final PostgresDataType<BigDecimal> DECIMAL                  = new PostgresDataType<BigDecimal>(BigDecimal.class, "decimal");
    public static final PostgresDataType<BigDecimal> MONEY                    = new PostgresDataType<BigDecimal>(BigDecimal.class, "money");
    public static final PostgresDataType<Float>      REAL                     = new PostgresDataType<Float>(Float.class, "real");
    public static final PostgresDataType<Float>      FLOAT4                   = new PostgresDataType<Float>(Float.class, "float4");
    public static final PostgresDataType<Short>      SMALLINT                 = new PostgresDataType<Short>(Short.class, "smallint");
    public static final PostgresDataType<Short>      INT2                     = new PostgresDataType<Short>(Short.class, "int2");
    public static final PostgresDataType<Date>       DATE                     = new PostgresDataType<Date>(Date.class, "date");
    public static final PostgresDataType<Time>       TIME                     = new PostgresDataType<Time>(Time.class, "time");
    public static final PostgresDataType<Time>       TIMEWITHOUTTIMEZONE      = new PostgresDataType<Time>(Time.class, "time without time zone");
    public static final PostgresDataType<Time>       TIMEWITHTIMEZONE         = new PostgresDataType<Time>(Time.class, "time with time zone");
    public static final PostgresDataType<Time>       TIMETZ                   = new PostgresDataType<Time>(Time.class, "timetz");
    public static final PostgresDataType<Timestamp>  TIMESTAMP                = new PostgresDataType<Timestamp>(Timestamp.class, "timestamp");
    public static final PostgresDataType<Timestamp>  TIMESTAMPWITHOUTTIMEZONE = new PostgresDataType<Timestamp>(Timestamp.class, "timestamp without time zone");
    public static final PostgresDataType<Timestamp>  TIMESTAMPWITHTIMEZONE    = new PostgresDataType<Timestamp>(Timestamp.class, "timestamp with time zone");
    public static final PostgresDataType<Timestamp>  TIMESTAMPTZ              = new PostgresDataType<Timestamp>(Timestamp.class, "timestamptz");

    // Meta-table types
    public static final PostgresDataType<Long>       OID                      = new PostgresDataType<Long>(Long.class, "oid");
    public static final PostgresDataType<Long>       OIDVECTOR                = new PostgresDataType<Long>(Long.class, "oidvector");
    public static final PostgresDataType<Long>       XID                      = new PostgresDataType<Long>(Long.class, "xid");
    public static final PostgresDataType<Long>       TID                      = new PostgresDataType<Long>(Long.class, "tid");
    public static final PostgresDataType<Long>       CID                      = new PostgresDataType<Long>(Long.class, "cid");
    public static final PostgresDataType<String>     ACLITEM                  = new PostgresDataType<String>(String.class, "aclitem");
    public static final PostgresDataType<String>     NAME                     = new PostgresDataType<String>(String.class, "name");
    public static final PostgresDataType<String>     REGPROC                  = new PostgresDataType<String>(String.class, "regproc");

    // Pseudo data types used for compatibility in casting
    protected static final PostgresDataType<BigInteger> __BIGINTEGER          = new PostgresDataType<BigInteger>(BigInteger.class, "bigint");
    protected static final PostgresDataType<Byte>       __BYTE                = new PostgresDataType<Byte>(Byte.class, "smallint");

    private PostgresDataType(Class<? extends T> type, String typeName) {
        super(SQLDialect.POSTGRES, type, typeName);
    }

    public static DataType<?> getDataType(String typeName) {
        return getDataType(SQLDialect.POSTGRES, typeName);
    }

    public static <T> DataType<T> getDataType(Class<? extends T> type) {
        return getDataType(SQLDialect.POSTGRES, type);
    }

    public static DataType<Object> getDefaultDataType(String typeName) {
        return getDefaultDataType(SQLDialect.POSTGRES, typeName);
    }
}
