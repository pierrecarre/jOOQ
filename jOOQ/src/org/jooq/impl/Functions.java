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

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.jooq.Configuration;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.SQLDialectNotSupportedException;

/**
 * @author Lukas Eder
 */
public final class Functions {

	public static <T extends Number> Field<T> sum(Field<T> field) {
		return new FunctionImpl<T>("sum", field.getType(), field);
	}

	public static <T extends Number> Field<Double> avg(Field<T> field) {
		return new FunctionImpl<Double>("avg", Double.class, field);
	}

	public static <T> Field<T> min(Field<T> field) {
		return new FunctionImpl<T>("min", field.getType(), field);
	}

	public static <T> Field<T> max(Field<T> field) {
		return new FunctionImpl<T>("max", field.getType(), field);
	}

	public static <T> Field<Integer> count() {
		return new CountFunctionImpl();
	}

	public static Field<Integer> count(Field<?> field) {
		return new CountFunctionImpl(field, false);
	}

	public static Field<Integer> countDistinct(Field<?> field) {
		return new CountFunctionImpl(field, true);
	}
	
	public static Field<String> upper(Field<String> field) {
		return new FunctionImpl<String>("upper", field.getType(), field);
	}

	public static Field<String> lower(Field<String> field) {
		return new FunctionImpl<String>("lower", field.getType(), field);
	}
	
	public static Field<Date> currentDate() throws SQLDialectNotSupportedException {
		switch (Configuration.getInstance().getDialect()) {
		case ORACLE:
			throw new SQLDialectNotSupportedException("current_date not supported");
		}
		
		return new FunctionImpl<Date>("current_date", Date.class);
	}
	
	public static Field<Time> currentTime() throws SQLDialectNotSupportedException {
		switch (Configuration.getInstance().getDialect()) {
		case ORACLE:
			throw new SQLDialectNotSupportedException("current_time not supported");
		}
		
		return new FunctionImpl<Time>("current_time", Time.class);
	}
	
	public static Field<Timestamp> currentTimestamp() {
		switch (Configuration.getInstance().getDialect()) {
		case ORACLE:
			return new FunctionImpl<Timestamp>("sysdate", Timestamp.class);
		}
		
		return new FunctionImpl<Timestamp>("current_timestamp", Timestamp.class);
	}

	public static Field<String> currentUser() {
		switch (Configuration.getInstance().getDialect()) {
		case ORACLE:
			return new FunctionImpl<String>("user", String.class);
		}

		return new FunctionImpl<String>("current_user", String.class);
	}
	
	public static Field<Integer> charLength(Field<?> field) {
		return new FunctionImpl<Integer>("char_length", Integer.class, field);
	}
	
	public static Field<Integer> bitLength(Field<?> field) {
		return new FunctionImpl<Integer>("bit_length", Integer.class, field);
	}
	
	public static Field<Integer> octetLength(Field<?> field) {
		return new FunctionImpl<Integer>("octet_length", Integer.class, field);
	}
	
	public static Field<Integer> extract(Field<?> field, DatePart datePart) {
		switch (Configuration.getInstance().getDialect()) {
		case MYSQL: // No break
		case POSTGRES:
			return new ExtractFunctionImpl(field, datePart);
		case ORACLE:
			throw new UnsupportedOperationException("TODO: Implement TO_CHAR for Oracle");
		case MSSQL:
			throw new UnsupportedOperationException("TODO: Implement CONVERT for MSSQL");
			
		default:
			throw new UnsupportedOperationException("extract not supported");
		}
	}
	
	public static <T> Field<T> constant(T value) {
		if (value == null) {
			throw new IllegalArgumentException("Argument 'value' must not be null");
		}
		
		return new ConstantFieldImpl<T>(value);
	}
	
	public static Field<?> NULL() {
		return new FieldImpl<Object>("null", Object.class);
	}

	private Functions() {
	}
}
