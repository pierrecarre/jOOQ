/**
 * Copyright (c) 2010, Lukas Eder, lukas.eder@gmail.com
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
package org.jooq.util.h2;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public enum H2DataType {
    INT(Integer.class),
    INTEGER(Integer.class),
    MEDIUMINT(Integer.class),
    INT4(Integer.class),
    SIGNED(Integer.class),

    BOOLEAN(Boolean.class),
    BIT(Boolean.class),
    BOOL(Boolean.class),

    TINYINT(Byte.class),

    SMALLINT(Short.class),
    INT2(Short.class),
    YEAR(Short.class),

    BIGINT(Long.class),
    INT8(Long.class),

    IDENTITY(Long.class),

    DECIMAL(BigDecimal.class),
    NUMBER(BigDecimal.class),
    DEC(BigDecimal.class),
    NUMERIC(BigDecimal.class),

    DOUBLE(Double.class),
    FLOAT(Double.class),
    FLOAT4(Double.class),
    FLOAT8(Double.class),

    REAL(Float.class),

    TIME(Time.class),

    DATE(Date.class),

    TIMESTAMP(Timestamp.class),
    DATETIME(Timestamp.class),
    SMALLDATETIME(Timestamp.class),

    BINARY(byte[].class),
    VARBINARY(byte[].class),
    LONGVARBINARY(byte[].class),
    RAW(byte[].class),
    BYTEA(byte[].class),

    OTHER(Object.class),

    VARCHAR(String.class),
    LONGVARCHAR(String.class),
    VARCHAR2(String.class),
    NVARCHAR(String.class),
    NVARCHAR2(String.class),
    VARCHAR_CASESENSITIVE(String.class),
    VARCHAR_IGNORECASE(String.class),
    CHAR(String.class),
    CHARACTER(String.class),
    NCHAR(String.class),

    BLOB(Blob.class),
    TINYBLOB(Blob.class),
    MEDIUMBLOB(Blob.class),
    LONGBLOB(Blob.class),
    IMAGE(Blob.class),
    OID(Blob.class),

    CLOB(Clob.class),
    TINYTEXT(Clob.class),
    TEXT(Clob.class),
    MEDIUMTEXT(Clob.class),
    LONGTEXT(Clob.class),
    NTEXT(Clob.class),
    NCLOB(Clob.class),

    UUID(java.util.UUID.class),

    ARRAY(Object[].class);
    private final Class<?> type;

    private H2DataType(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }

    public static H2DataType getType(Class<?> type) {
        for (H2DataType value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }

        return null;
    }
}
