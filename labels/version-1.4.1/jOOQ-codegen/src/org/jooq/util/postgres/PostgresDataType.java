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

package org.jooq.util.postgres;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * @author Lukas Eder
 *
 * @see http://www.postgresql.org/docs/8.4/interactive/datatype.html
 */
public enum PostgresDataType {

	BIGINT(Long.class),
	INT8(Long.class),
	BIGSERIAL(Long.class),
	SERIAL8(Long.class),

	BIT(String.class),
	BITVARYING(String.class),
	VARBIT(String.class),

	BOOLEAN(Boolean.class),
	BOOL(Boolean.class),

	BYTEA(byte[].class),

	CHARACTERVARYING(String.class),
	VARCHAR(String.class),
	CHARACTER(String.class),
	CHAR(String.class),
	TEXT(String.class),

	DOUBLEPRECISION(Double.class),
	FLOAT8(Double.class),

	INTEGER(Integer.class),
	INT(Integer.class),
	INT4(Integer.class),
	SERIAL(Integer.class),
	SERIAL4(Integer.class),

	MONEY(BigDecimal.class),
	NUMERIC(BigDecimal.class),

	REAL(Float.class),
	FLOAT4(Float.class),

	SMALLINT(Short.class),
	INT2(Short.class),

	DATE(Date.class),
	TIME(Time.class),
	TIMEWITHOUTTIMEZONE(Time.class),
	TIMEWITHTIMEZONE(Time.class),
	TIMETZ(Time.class),
	TIMESTAMP(Timestamp.class),
	TIMESTAMPWITHOUTTIMEZONE(Timestamp.class),
	TIMESTAMPWITHTIMEZONE(Timestamp.class),
	TIMESTAMPTZ(Timestamp.class)

	;

	private final Class<?> type;

	private PostgresDataType(Class<?> type) {
		this.type = type;
	}

	public Class<?> getType() {
		return type;
	}
}
