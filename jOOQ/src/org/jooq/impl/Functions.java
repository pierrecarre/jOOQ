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

	public static <T extends Number> Field<T> abs(Field<T> field) {
		return new FunctionImpl<T>("abs", field.getType(), field);
	}

	public static <T extends Number> Field<T> round(Field<T> field) {
		return new FunctionImpl<T>("round", field.getType(), field);
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

	public static Field<String> trim(Field<String> field) {
		return new FunctionImpl<String>("trim", field.getType(), field);
	}

	public static Field<String> rtrim(Field<String> field) {
		return new FunctionImpl<String>("rtrim", field.getType(), field);
	}

	public static Field<String> ltrim(Field<String> field) {
		return new FunctionImpl<String>("ltrim", field.getType(), field);
	}

	public static Field<String> rpad(Field<String> field, Field<Integer> length) {
		return new StringFunction("rpad", field, length);
	}

	public static Field<String> rpad(Field<String> field, int length) {
		return rpad(field, constant(length));
	}

	public static Field<String> rpad(Field<String> field, Field<Integer> length, Field<String> c) {
		return new StringFunction("rpad", field, length, c);
	}

	public static Field<String> rpad(Field<String> field, int length, char c) {
		return rpad(field, constant(length), constant("" + c));
	}

	public static Field<String> lpad(Field<String> field, Field<Integer> length) {
		return new StringFunction("lpad", field, length);
	}

	public static Field<String> lpad(Field<String> field, int length) {
		return lpad(field, constant(length));
	}

	public static Field<String> lpad(Field<String> field, Field<Integer> length, Field<String> c) {
		return new StringFunction("lpad", field, length, c);
	}

	public static Field<String> lpad(Field<String> field, int length, char c) {
		return lpad(field, constant(length), constant("" + c));
	}

	public static Field<String> replace(Field<String> in, Field<String> search) {
		return new StringFunction("replace", in, search);
	}

	public static Field<String> replace(Field<String> in, String search) {
		return replace(in, constant(search));
	}

	public static Field<String> replace(Field<String> in, Field<String> search, Field<String> replace) {
		return new StringFunction("replace", in, search, replace);
	}

	public static Field<String> replace(Field<String> in, String search, String replace) {
		return replace(in, constant(search), constant(replace));
	}

	public static Field<Integer> ascii(Field<String> field) {
		return new IntegerFunction("ascii", field);
	}

	public static Field<String> concatenate(Field<String>... fields) {
		switch (Configuration.getInstance().getDialect()) {
		case MYSQL:
			return new StringFunction("concat", fields);
		}

		return new StringFunction("concatenate", fields);
	}

	public static Field<String> substring(Field<String> field, int startingPosition) {
		return substring(field, startingPosition, -1);
	}

	public static Field<String> substring(Field<String> field, int startingPosition, int length) throws SQLDialectNotSupportedException {
		Field<Integer> startingPositionConstant = constant(startingPosition);
		Field<Integer> lengthConstant = constant(length);

		String functionName = "substring";

		switch (Configuration.getInstance().getDialect()) {
		case ORACLE:
			functionName = "substr";
			break;
		}

		if (length == -1) {
			return new StringFunction(functionName, field, startingPositionConstant);
		} else {
			return new StringFunction(functionName, field, startingPositionConstant, lengthConstant);
		}
	}

	public static Field<Date> currentDate() throws SQLDialectNotSupportedException {
		switch (Configuration.getInstance().getDialect()) {
		case ORACLE:
			return new FunctionImpl<Date>("sysdate", Date.class);
		}

		return new FunctionImpl<Date>("current_date", Date.class);
	}

	public static Field<Time> currentTime() throws SQLDialectNotSupportedException {
		switch (Configuration.getInstance().getDialect()) {
		case ORACLE:
			return new FunctionImpl<Time>("sysdate", Time.class);
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
			return new StringFunction("user");
		}

		return new StringFunction("current_user");
	}

	public static Field<Integer> charLength(Field<?> field) {
		switch (Configuration.getInstance().getDialect()) {
		case ORACLE:
			return new IntegerFunction("length", field);
		}

		return new IntegerFunction("char_length", field);
	}

	public static Field<Integer> bitLength(Field<?> field) {
		switch (Configuration.getInstance().getDialect()) {
		case ORACLE:
			return new IntegerFunction("8 * lengthb", field);
		}

		return new IntegerFunction("bit_length", field);
	}

	public static Field<Integer> octetLength(Field<?> field) {
		switch (Configuration.getInstance().getDialect()) {
		case ORACLE:
			return new IntegerFunction("lengthb", field);
		}

		return new IntegerFunction("octet_length", field);
	}

	public static Field<Integer> extract(Field<?> field, DatePart datePart) throws SQLDialectNotSupportedException {
		switch (Configuration.getInstance().getDialect()) {
		case MYSQL: // No break
		case POSTGRES:
		case HSQLDB:
			return new ExtractFunctionImpl(field, datePart);
		case ORACLE:
			switch (datePart) {
			case YEAR:
				return new IntegerFunction("to_char", field, constant("YYYY"));
			case MONTH:
				return new IntegerFunction("to_char", field, constant("MM"));
			case DAY:
				return new IntegerFunction("to_char", field, constant("DD"));
			case HOUR:
				return new IntegerFunction("to_char", field, constant("HH24"));
			case MINUTE:
				return new IntegerFunction("to_char", field, constant("MI"));
			case SECOND:
				return new IntegerFunction("to_char", field, constant("SS"));
			default:
				throw new SQLDialectNotSupportedException("DatePart not supported: " + datePart);
			}
		case MSSQL:
			throw new SQLDialectNotSupportedException("TODO: Implement CONVERT for MSSQL");

		default:
			throw new SQLDialectNotSupportedException("extract not supported");
		}
	}

	public static Field<Integer> position(Field<String> in, String search) throws SQLDialectNotSupportedException {
		return position(in, constant(search));
	}

	public static Field<Integer> position(Field<String> in, Field<String> search) throws SQLDialectNotSupportedException {
		switch (Configuration.getInstance().getDialect()) {
		case MYSQL: // No break
		case POSTGRES:
		case HSQLDB:
			return new PositionFunctionImpl(search, in);
		case ORACLE:
			return new IntegerFunction("instr", in, search);
		case MSSQL:
			return new IntegerFunction("charindex", search, in);

		default:
			throw new SQLDialectNotSupportedException("position not supported");
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
