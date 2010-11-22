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
package org.jooq.util.db2;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * The mapping from DB2 SQL data type to Java data type
 * 
 * @see http://publib.boulder.ibm.com/infocenter/db2luw/v9/index.jsp?topic=/com.ibm.db2.udb.apdv.java.doc/doc/rjvjdata.htm
 * 
 * @author Espen Stromsnes
 */
public enum DB2DataType {

    SMALLINT(Short.class),
    INTEGER(Integer.class),
    BIGINT(Long.class),

    REAL(Float.class),
    
    DOUBLE(Double.class),
    DECIMAL(BigDecimal.class),
    DECFLOAT(BigDecimal.class),

    VARCHAR(String.class),
    CHAR(String.class),
    CHARACTER(String.class),
    LONGVARCHAR(String.class),
    CLOB(String.class),
    GRAPHIC(String.class),
    VARGRAPHIC(String.class),
    XML(String.class),

    BLOB(byte[].class),

    DATE(Date.class),
    TIME(Time.class),
    TIMESTAMP(Timestamp.class);

    
    private final Class<?> type;

    private DB2DataType(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }

    public static DB2DataType getType(Class<?> type) {
        for (DB2DataType value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }

        return null;
    }
}
