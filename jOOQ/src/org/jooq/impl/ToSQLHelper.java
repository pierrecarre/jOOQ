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
import java.sql.Time;
import java.sql.Timestamp;

import org.jooq.Field;

/**
 * @author Lukas Eder
 */
final class ToSQLHelper {

	public static String toSQL(Object value, boolean inlineParameters) {
		return toSQL(value, inlineParameters, value.getClass());
	}
	
	public static String toSQL(Object value, boolean inlineParameters, Field<?> field) {
		return toSQL(value, inlineParameters, field.getType());
	}
	
	public static String toSQL(Object value, boolean inlineParameters, Class<?> type) {
		if (inlineParameters) {
			if (type == Blob.class) {
				// Not supported
			}
			else if (type == Boolean.class) {
				return value.toString();
			}
			else if (type == BigDecimal.class) {
				return value.toString();
			}
			else if (type == Byte.class) {
				return value.toString();
			}
			else if (type == byte[].class) {
				// Not supported
			}
			else if (type == Clob.class) {
				// Not supported
			}
			else if (type == Date.class) {
				// Not supported
			}
			else if (type == Double.class) {
				return value.toString();
			}
			else if (type == Float.class) {
				return value.toString();
			}
			else if (type == Integer.class) {
				return value.toString();
			}
			else if (type == Long.class) {
				return value.toString();
			}
			else if (type == Short.class) {
				return value.toString();
			}
			else if (type == String.class) {
				return "'" + value.toString().replace("'", "''") + "'";
			}
			else if (type == Time.class) {
				// Not supported
			}
			else if (type == Timestamp.class) {
				// Not supported
			}
			else {
				// Not supported
			}

			throw new UnsupportedOperationException("Class " + type + " is not supported");
		}
		
		return "?";
	}
	
	private ToSQLHelper() {}
}
