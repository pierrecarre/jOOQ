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

package org.jooq.util.derby;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.jooq.DataType;
import org.jooq.SQLDialect;
import org.jooq.impl.AbstractDataType;

/**
 * Supported data types for the {@link SQLDialect#DERBY} dialect
 *
 * @author Lukas Eder
 * @see http://db.apache.org/derby/docs/10.7/ref/crefsqlj31068.html
 */
public class DerbyDataType<T> extends AbstractDataType<T> {

    public static final DerbyDataType<Short>      SMALLINT                   = new DerbyDataType<Short>(Short.class, "smallint");
    public static final DerbyDataType<Integer>    INT                        = new DerbyDataType<Integer>(Integer.class, "int");
    public static final DerbyDataType<Integer>    INTEGER                    = new DerbyDataType<Integer>(Integer.class, "integer");
    public static final DerbyDataType<Long>       BIGINT                     = new DerbyDataType<Long>(Long.class, "bigint");
    public static final DerbyDataType<Double>     DOUBLE                     = new DerbyDataType<Double>(Double.class, "double");
    public static final DerbyDataType<Double>     DOUBLEPRECISION            = new DerbyDataType<Double>(Double.class, "double precision");
    public static final DerbyDataType<Double>     FLOAT                      = new DerbyDataType<Double>(Double.class, "float");
    public static final DerbyDataType<Float>      REAL                       = new DerbyDataType<Float>(Float.class, "real");
    public static final DerbyDataType<BigDecimal> DEC                        = new DerbyDataType<BigDecimal>(BigDecimal.class, "dec");
    public static final DerbyDataType<BigDecimal> DECIMAL                    = new DerbyDataType<BigDecimal>(BigDecimal.class, "decimal");
    public static final DerbyDataType<BigDecimal> NUMERIC                    = new DerbyDataType<BigDecimal>(BigDecimal.class, "numeric");
    public static final DerbyDataType<String>     VARCHAR                    = new DerbyDataType<String>(String.class, "varchar(32672)");
    public static final DerbyDataType<String>     LONGVARCHAR                = new DerbyDataType<String>(String.class, "long varchar");
    public static final DerbyDataType<String>     CHAR                       = new DerbyDataType<String>(String.class, "char");
    public static final DerbyDataType<String>     CHARACTER                  = new DerbyDataType<String>(String.class, "character");
    public static final DerbyDataType<String>     CLOB                       = new DerbyDataType<String>(String.class, "clob");
    public static final DerbyDataType<String>     CHARACTERLARGEOBJECT       = new DerbyDataType<String>(String.class, "character large object");
    public static final DerbyDataType<String>     CHARVARYING                = new DerbyDataType<String>(String.class, "char varying");
    public static final DerbyDataType<String>     CHARACTERVARYING           = new DerbyDataType<String>(String.class, "character varying");
    public static final DerbyDataType<Boolean>    BOOLEAN                    = new DerbyDataType<Boolean>(Boolean.class, "boolean");
    public static final DerbyDataType<Date>       DATE                       = new DerbyDataType<Date>(Date.class, "date");
    public static final DerbyDataType<Time>       TIME                       = new DerbyDataType<Time>(Time.class, "time");
    public static final DerbyDataType<Timestamp>  TIMESTAMP                  = new DerbyDataType<Timestamp>(Timestamp.class, "timestamp");
    public static final DerbyDataType<byte[]>     BLOB                       = new DerbyDataType<byte[]>(byte[].class, "blob");
    public static final DerbyDataType<byte[]>     CHARFORBITDATA             = new DerbyDataType<byte[]>(byte[].class, "char for bit data");
    public static final DerbyDataType<byte[]>     CHARACTERFORBITDATA        = new DerbyDataType<byte[]>(byte[].class, "character for bit data");
    public static final DerbyDataType<byte[]>     BINARYLARGEOBJECT          = new DerbyDataType<byte[]>(byte[].class, "binary large object");
    public static final DerbyDataType<byte[]>     LONGVARCHARFORBITDATA      = new DerbyDataType<byte[]>(byte[].class, "long varchar for bit data");
    public static final DerbyDataType<byte[]>     VARCHARFORBITDATA          = new DerbyDataType<byte[]>(byte[].class, "varchar for bit data");
    public static final DerbyDataType<byte[]>     CHARVARYINGFORBITDATA      = new DerbyDataType<byte[]>(byte[].class, "char varying for bit data");
    public static final DerbyDataType<byte[]>     CHARACTERVARYINGFORBITDATA = new DerbyDataType<byte[]>(byte[].class, "character varying for bit data");

    // Derby types
    public static final DerbyDataType<String>     ORGAPACHEDERBYCATALOGTYPEDESCRIPTOR
                                                                             = new DerbyDataType<String>(String.class, "org.apache.derby.catalog.TypeDescriptor");
    public static final DerbyDataType<String>     ORGAPACHEDERBYCATALOGINDEXDESCRIPTOR
                                                                             = new DerbyDataType<String>(String.class, "org.apache.derby.catalog.IndexDescriptor");
    public static final DerbyDataType<String>     JAVAIOSERIALIZABLE         = new DerbyDataType<String>(String.class, "java.io.Serializable");

    // Pseudo data types used for compatibility in casting
    protected static final DerbyDataType<BigInteger> __BIGINTEGER            = new DerbyDataType<BigInteger>(BigInteger.class, "decimal");
    protected static final DerbyDataType<Byte>       __BYTE                  = new DerbyDataType<Byte>(Byte.class, "smallint");

    private DerbyDataType(Class<? extends T> type, String typeName) {
        super(SQLDialect.DERBY, type, typeName);
    }

    public static DataType<?> getDataType(String typeName) {
        return getDataType(SQLDialect.DERBY, typeName);
    }

    public static <T> DataType<T> getDataType(Class<? extends T> type) {
        return getDataType(SQLDialect.DERBY, type);
    }

    public static DataType<Object> getDefaultDataType(String typeName) {
        return getDefaultDataType(SQLDialect.DERBY, typeName);
    }
}
