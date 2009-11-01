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

import java.sql.PreparedStatement;

import org.jooq.BetweenCondition;
import org.jooq.Field;

/**
 * @author Lukas Eder
 */
class BetweenConditionImpl<T> extends AbstractQueryPart implements BetweenCondition<T> {

	private static final long serialVersionUID = -4666251100802237878L;

	private final Field<T> field;
	private final T minValue;
	private final T maxValue;

	public BetweenConditionImpl(Field<T> field, T minValue, T maxValue) {
		if (field == null)
			throw new IllegalArgumentException("The argument 'field' must not be null");
		if (minValue == null)
			throw new IllegalArgumentException("The argument 'minValue' must not be null");
		if (maxValue == null)
			throw new IllegalArgumentException("The argument 'maxValue' must not be null");

		this.field = field;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	@Override
	public int bind(PreparedStatement stmt, int initialIndex) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public final T getMaxValue() {
		return maxValue;
	}

	@Override
	public T getMinValue() {
		return minValue;
	}

	@Override
	public Field<T> getField() {
		return field;
	}

	@Override
	public String toSQL(boolean inlineParameters) {
		StringBuilder sb = new StringBuilder();

		sb.append(getField().toSQL(inlineParameters));
		sb.append(" between ");
		sb.append(ToSQLHelper.toSQL(getMinValue(), inlineParameters, getField()));
		sb.append(" and ");
		sb.append(ToSQLHelper.toSQL(getMaxValue(), inlineParameters, getField()));

		return sb.toString();
	}
}
