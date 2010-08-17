/**
 * Copyright (c) 2009, Lukas Eder, lukas.eder@gmail.com
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

package org.jooq.util.oracle;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author Lukas Eder
 */
public enum OracleDataType {

	NUMBER(BigDecimal.class),

	CHAR(String.class),
	NCHAR(String.class),
	VARCHAR(String.class),
	VARCHAR2(String.class),
	NVARCHAR(String.class),
	LONG(String.class),
	CLOB(String.class),
	NCLOB(String.class),

	RAW(byte[].class),
	LONG_RAW(byte[].class),
	BLOB(byte[].class),
	BFILE(byte[].class),

	DATE(Date.class),
	TIMESTAMP(Timestamp.class);

	private final Class<?> type;
	private final int precision;
    private final int scale;

    private OracleDataType(Class<?> type) {
      this(type, 0, 0);
    }

    private OracleDataType(Class<?> type, int precision) {
      this(type, precision, 0);
    }

    private OracleDataType(Class<?> type, int precision, int scale) {
      this.type = type;
      this.precision = precision;
      this.scale = scale;
    }

	public Class<?> getType() {
		return type;
	}

    public int getPrecision() {
      return precision;
    }
  
    public int getScale() {
      return scale;
    }
}
