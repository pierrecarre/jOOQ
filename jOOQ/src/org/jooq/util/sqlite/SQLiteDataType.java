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
package org.jooq.util.sqlite;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author Lukas Eder
 *
 * @see http://www.sqlite.org/datatype3.html
 */
public enum SQLiteDataType {
    TINYINT(Byte.class),

    SMALLINT(Short.class),
    INT2(Short.class),

    INT(Integer.class),
    INTEGER(Integer.class),
    MEDIUMINT(Integer.class),
    BOOLEAN(Integer.class),

    INT8(Long.class),

    BIGINT(BigInteger.class),
    UNSIGNEDBIGINT(BigInteger.class),

    DOUBLE(Double.class),
    DOUBLEPRECISION(Double.class),

    FLOAT(Float.class),
    REAL(Float.class),

    NUMERIC(BigDecimal.class),
    DECIMAL(BigDecimal.class),

    DATE(Date.class),
    DATETIME(Timestamp.class),

    CHAR(String.class),
    CHARACTER(String.class),
    VARCHAR(String.class),
    LONGVARCHAR(String.class),
    VARYINGCHARACTER(String.class),
    NCHAR(String.class),
    NATIVECHARACTER(String.class),
    NVARCHAR(String.class),
    TEXT(String.class),
    CLOB(String.class),

    LONGVARBINARY(byte[].class),
    BLOB(byte[].class),
    ;

    private final Class<?> type;

    private SQLiteDataType(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }

    public static SQLiteDataType getType(Class<?> type) {
        for (SQLiteDataType value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }

        return null;
    }
}
