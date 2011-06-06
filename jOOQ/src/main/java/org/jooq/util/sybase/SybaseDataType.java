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
package org.jooq.util.sybase;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.jooq.DataType;
import org.jooq.SQLDialect;
import org.jooq.impl.AbstractDataType;

/**
 * Supported data types for the {@link SQLDialect#SYBASE} dialect
 *
 * @see <a href="http://infocenter.sybase.com/help/topic/com.sybase.help.sqlanywhere.12.0.0/dbreference/rf-datatypes.html">http://infocenter.sybase.com/help/topic/com.sybase.help.sqlanywhere.12.0.0/dbreference/rf-datatypes.html</a>
 * @author Espen Stromsnes
 */
public class SybaseDataType<T> extends AbstractDataType<T> {

    /**
     * Generated UID
     */
    private static final long                      serialVersionUID           = -4442303192680774346L;
    
    public static final SybaseDataType<BigDecimal> MONEY                      = new SybaseDataType<BigDecimal>(BigDecimal.class, "money");
    public static final SybaseDataType<BigDecimal> SMALLMONEY                 = new SybaseDataType<BigDecimal>(BigDecimal.class, "smallmoney");


    public static final SybaseDataType<String>     CHAR                       = new SybaseDataType<String>(String.class, "char");
    public static final SybaseDataType<String>     LONGNVARCHAR               = new SybaseDataType<String>(String.class, "long nvarchar");
    public static final SybaseDataType<String>     LONGVARCHAR                = new SybaseDataType<String>(String.class, "long varchar");
    public static final SybaseDataType<String>     NCHAR                      = new SybaseDataType<String>(String.class, "nchar");
    public static final SybaseDataType<String>     NTEXT                      = new SybaseDataType<String>(String.class, "ntext");
    public static final SybaseDataType<String>     NVARCHAR                   = new SybaseDataType<String>(String.class, "nvarchar");
    public static final SybaseDataType<String>     TEXT                       = new SybaseDataType<String>(String.class, "text");
    public static final SybaseDataType<String>     UNIQUEIDENTIFIERSTR        = new SybaseDataType<String>(String.class, "uniqueidentifierstr");
    public static final SybaseDataType<String>     VARCHAR                    = new SybaseDataType<String>(String.class, "varchar");
    public static final SybaseDataType<String>     XML                        = new SybaseDataType<String>(String.class, "xml");
    public static final SybaseDataType<String>     UNIQUEIDENTIFIER           = new SybaseDataType<String>(String.class, "uniqueidentifier");

    public static final SybaseDataType<BigInteger> BIGINT                     = new SybaseDataType<BigInteger>(java.math.BigInteger.class, "bigint");
    public static final SybaseDataType<BigInteger> UNSIGNEDBIGINT             = new SybaseDataType<BigInteger>(java.math.BigInteger.class, "unsignedbigint");
    public static final SybaseDataType<Boolean>    BIT                        = new SybaseDataType<Boolean>(Boolean.class, "bit");
    public static final SybaseDataType<BigDecimal> DECIMAL                    = new SybaseDataType<BigDecimal>(BigDecimal.class, "decimal");
    public static final SybaseDataType<Double>     DOUBLE                     = new SybaseDataType<Double>(Double.class, "double");
    public static final SybaseDataType<Double>     FLOAT                      = new SybaseDataType<Double>(Double.class, "float");
    public static final SybaseDataType<Integer>    INTEGER                    = new SybaseDataType<Integer>(Integer.class, "integer");
    public static final SybaseDataType<Integer>    UNSIGNEDINT                = new SybaseDataType<Integer>(Integer.class, "unsignedint");
    public static final SybaseDataType<BigDecimal> NUMERIC                    = new SybaseDataType<BigDecimal>(BigDecimal.class, "numeric");
    public static final SybaseDataType<Float>      REAL                       = new SybaseDataType<Float>(Float.class, "real");
    public static final SybaseDataType<Integer>    SMALLINT                   = new SybaseDataType<Integer>(Integer.class, "smallint");
    public static final SybaseDataType<Integer>    UNSIGNEDSMALLLINT          = new SybaseDataType<Integer>(Integer.class, "unsignedsmallint");
    public static final SybaseDataType<Integer>    TINYINT                    = new SybaseDataType<Integer>(Integer.class, "tinyint");

    public static final SybaseDataType<Date>       DATE                       = new SybaseDataType<Date>(Date.class, "date");
    public static final SybaseDataType<Timestamp>  DATETIME                   = new SybaseDataType<Timestamp>(Timestamp.class, "datetime");
    public static final SybaseDataType<Timestamp>  DATETIMEOFFSET             = new SybaseDataType<Timestamp>(Timestamp.class, "datetimeoffset");
    public static final SybaseDataType<Timestamp>  SMALLDATETIME              = new SybaseDataType<Timestamp>(Timestamp.class, "smalldatetime");
    public static final SybaseDataType<Time>       TIME                       = new SybaseDataType<Time>(Time.class, "time");
    public static final SybaseDataType<Timestamp>  TIMESTAMP                  = new SybaseDataType<Timestamp>(Timestamp.class, "timestamp");
    public static final SybaseDataType<Timestamp>  TIMESTAMPWITHTIMEZONE      = new SybaseDataType<Timestamp>(Timestamp.class, "timestampwithtimezone");

    public static final SybaseDataType<byte[]>     BINARY                     = new SybaseDataType<byte[]>(byte[].class, "binary");
    public static final SybaseDataType<byte[]>     IMAGE                      = new SybaseDataType<byte[]>(byte[].class, "image");
    public static final SybaseDataType<byte[]>     LONGBINARY                 = new SybaseDataType<byte[]>(byte[].class, "longbinary");
    public static final SybaseDataType<byte[]>     LONGVARBIT                 = new SybaseDataType<byte[]>(byte[].class, "longvarbit");
    public static final SybaseDataType<byte[]>     VARBIT                     = new SybaseDataType<byte[]>(byte[].class, "varbit");

    public static final SybaseDataType<byte[]>     VARBINARY                  = new SybaseDataType<byte[]>(byte[].class, "varbinary");

    // Pseudo data types used for compatibility in casting
    //protected static final SybaseDataType<Boolean>    __BOOLEAN           = new SybaseDataType<Boolean>(Boolean.class, "smallint");
    protected static final SybaseDataType<Byte>       __BYTE              = new SybaseDataType<Byte>(Byte.class, "smallint");
    protected static final SybaseDataType<Short>      __SHORT             = new SybaseDataType<Short>(Short.class, "smallint");
    protected static final SybaseDataType<Long>       __LONG              = new SybaseDataType<Long>(Long.class, "int");

    private SybaseDataType(Class<? extends T> type, String typeName) {
        super(SQLDialect.SYBASE, type, typeName);
    }

    public static DataType<?> getDataType(String typeName) {
        return getDataType(SQLDialect.SYBASE, typeName);
    }

    public static <T> DataType<T> getDataType(Class<? extends T> type) {
        return getDataType(SQLDialect.SYBASE, type);
    }

    public static DataType<Object> getDefaultDataType(String typeName) {
        return getDefaultDataType(SQLDialect.SYBASE, typeName);
    }
}
