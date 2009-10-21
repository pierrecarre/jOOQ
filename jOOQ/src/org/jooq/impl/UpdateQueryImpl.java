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

import static org.jooq.impl.TrueCondition.TRUE_CONDITION;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jooq.Condition;
import org.jooq.ConditionProvider;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.UpdateQuery;

/**
 * @author Lukas Eder
 */
class UpdateQueryImpl extends AbstractQueryPart implements UpdateQuery {

	private static final long serialVersionUID = -660460731970074719L;
	private final Table table;
	private final Map<Field<?>, Object> values;
	private final ConditionProvider condition;
	
	public UpdateQueryImpl(Table table) {
		this.table = table;
		this.values = new LinkedHashMap<Field<?>, Object>();
		this.condition = new ConditionProviderImpl();
	}

	@Override
	protected int bind(PreparedStatement stmt, int initialIndex) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public Table getTable() {
		return table;
	}
	
	@Override
	public void addConditions(Collection<Condition> conditions) {
		condition.addConditions(conditions);
	}

	@Override
	public void addConditions(Condition... conditions) {
		condition.addConditions(conditions);
	}

	@Override
	public Condition getWhere() {
		return condition.getWhere();
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
	public String toSQL(boolean inlineParameters) {
		if (getValues0().isEmpty()) {
			throw new IllegalStateException("Cannot create SQL for empty insert statement");
		}
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("update ");
		sb.append(getTable().toSQL(inlineParameters));
		sb.append(" set ");
		
		String separator = "";
		for (Field<?> field : getValues0().keySet()) {
			Object value = getValues0().get(field);
			
			sb.append(separator);
			sb.append(field.toSQL(inlineParameters));
			sb.append(" = ");
			sb.append(ToSQLHelper.toSQL(value, inlineParameters, field));
			separator = ", ";
		}
		
		if (getWhere() != TRUE_CONDITION) {
			sb.append(" where ");
			sb.append(getWhere().toSQL(inlineParameters));
		}
				
		return sb.toString();
	}
}
