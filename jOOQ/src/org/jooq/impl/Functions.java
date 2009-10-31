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

import org.jooq.Field;
import org.jooq.Function;

/**
 * @author Lukas Eder
 */
public final class Functions {

	public static <T extends Number> Function<T> sum(Field<T> field) {
		return new FunctionImpl<T>("sum", field.getType(), field);
	}

	public static <T extends Number> Function<Double> avg(Field<T> field) {
		return new FunctionImpl<Double>("avg", Double.class, field);
	}

	public static <T> Function<T> min(Field<T> field) {
		return new FunctionImpl<T>("min", field.getType(), field);
	}

	public static <T> Function<T> max(Field<T> field) {
		return new FunctionImpl<T>("max", field.getType(), field);
	}

	public static <T> Function<Integer> count() {
		return new CountFunctionImpl();
	}

	public static Function<Integer> count(Field<?> field) {
		return new CountFunctionImpl(field, false);
	}

	public static Function<Integer> countDistinct(Field<?> field) {
		return new CountFunctionImpl(field, true);
	}

	@SuppressWarnings("unchecked")
	public static <T> Function<T> constant(T value) {
		if (value == null) {
			throw new IllegalArgumentException("Argument 'value' must not be null");
		}
		
		return new FunctionImpl<T>(value.toString(), (Class<T>) value.getClass());
	}
	
	public static Function<?> NULL() {
		return new FunctionImpl<Object>("null", Object.class);
	}

	private Functions() {
	}
}
