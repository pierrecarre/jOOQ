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

import org.jooq.Condition;
import org.jooq.ConditionProvider;
import org.jooq.DeleteQuery;
import org.jooq.Table;

/**
 * @author Lukas Eder
 */
class DeleteQueryImpl extends AbstractQuery implements DeleteQuery {

	private static final long serialVersionUID = -1943687511774150929L;
	
	private final Table table;
	private final ConditionProvider condition;

	public DeleteQueryImpl(Table table) {
		this.table = table;
		this.condition = new ConditionProviderImpl();
	}

	@Override
	public int bind(PreparedStatement stmt, int initialIndex) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	protected int execute(PreparedStatement statement) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public Table getFrom() {
		return table;
	}

	@Override
	public Condition getWhere() {
		return condition.getWhere();
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
	public String toSQL(boolean inlineParameters) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("delete from ");
		sb.append(getFrom().toSQL(inlineParameters));

		if (getWhere() != TRUE_CONDITION) {
			sb.append(" where ");
			sb.append(getWhere().toSQL(inlineParameters));
		}
				
		return sb.toString();
	}

}
