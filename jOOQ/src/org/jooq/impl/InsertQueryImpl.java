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
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jooq.Field;
import org.jooq.InsertQuery;
import org.jooq.Table;

/**
 * @author Lukas Eder
 */
class InsertQueryImpl extends AbstractQuery implements InsertQuery {

	private static final long serialVersionUID = 4466005417945353842L;
	private final Table into;
	private final Map<Field<?>, Object> values;

	InsertQueryImpl(Table into) {
		this.into = into;
		this.values = new LinkedHashMap<Field<?>, Object>();
	}

	@Override
	public int bind(PreparedStatement stmt, int initialIndex) throws SQLException {
		int result = initialIndex;

		result = getInto().bind(stmt, result);
		for (Field<?> field : getValues0().keySet()) {
			result = field.bind(stmt, result);
			bind(stmt, result++, field, getValues0().get(field));
		}

		return result;
	}

	@Override
	protected int execute(PreparedStatement statement) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public Table getInto() {
		return into;
	}

	@Override
	public Map<Field<?>, ?> getValues() {
		return Collections.unmodifiableMap(getValues0());
	}

	protected Map<Field<?>, Object> getValues0() {
		return values;
	}

	@Override
	public <T> void addValue(Field<T> field, T value) {
		getValues0().put(field, value);
	}

	@Override
	public String toSQLReference(boolean inlineParameters) {
		if (getValues0().isEmpty()) {
			throw new IllegalStateException("Cannot create SQL for empty insert statement");
		}

		StringBuilder sb = new StringBuilder();

		sb.append("insert into ");
		sb.append(getInto().toSQLReference(inlineParameters));
		sb.append(" (");

		String separator1 = "";
		for (Field<?> field : getValues0().keySet()) {
			sb.append(separator1);
			sb.append(field.toSQLReference(inlineParameters));
			separator1 = ", ";
		}

		sb.append(") values (");

		String separator2 = "";
		for (Field<?> field : getValues0().keySet()) {
			Object value = getValues0().get(field);

			sb.append(separator2);
			sb.append(FieldTypeHelper.toSQL(value, inlineParameters, field));
			separator2 = ", ";
		}
		sb.append(")");

		return sb.toString();
	}
}
