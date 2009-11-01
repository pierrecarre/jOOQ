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

package org.jooq.impl;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import org.jooq.Field;
import org.jooq.QueryPart;

public abstract class AbstractQueryPart implements QueryPart {

	private static final long serialVersionUID = 2078114876079493107L;

	@Override
	public final String toSQL() {
		return toSQL(false);
	}

	public final int bind(PreparedStatement stmt) throws SQLException {
		return bind(stmt, 1);
	}

	protected final void bind(PreparedStatement stmt, int index, Field<?> field, Object value) throws SQLException {
		bind(stmt, index, field.getType(), value);
	}

	protected final void bind (PreparedStatement stmt, int index, Class<?> type, Object value) throws SQLException {
		if (type == Blob.class) {
			stmt.setBlob(index, (Blob) value);
		}
		else if (type == Boolean.class) {
			stmt.setBoolean(index, (Boolean) value);
		}
		else if (type == BigDecimal.class) {
			stmt.setBigDecimal(index, (BigDecimal) value);
		}
		else if (type == Byte.class) {
			stmt.setByte(index, (Byte) value);
		}
		else if (type == byte[].class) {
			stmt.setBytes(index, (byte[]) value);
		}
		else if (type == Clob.class) {
			stmt.setClob(index, (Clob) value);
		}
		else if (type == Date.class) {
			stmt.setDate(index, (Date) value);
		}
		else if (type == Double.class) {
			stmt.setDouble(index, (Double) value);
		}
		else if (type == Float.class) {
			stmt.setFloat(index, (Float) value);
		}
		else if (type == Integer.class) {
			stmt.setInt(index, (Integer) value);
		}
		else if (type == Long.class) {
			stmt.setLong(index, (Long) value);
		}
		else if (type == Short.class) {
			stmt.setShort(index, (Short) value);
		}
		else if (type == String.class) {
			stmt.setString(index, (String) value);
		}
		else if (type == Time.class) {
			stmt.setTime(index, (Time) value);
		}
		else if (type == Timestamp.class) {
			stmt.setTimestamp(index, (Timestamp) value);
		}
		else {
			stmt.setObject(index, value);
		}
	}

	@Override
	public boolean equals(Object that) {
		if (that instanceof QueryPart) {
			return toSQL(true).equals(((QueryPart) that).toSQL(true));
		}

		return false;
	}

	@Override
	public final int hashCode() {
		return toSQL(true).hashCode();
	}

	@Override
	public final String toString() {
		return toSQL(true);
	}
}
