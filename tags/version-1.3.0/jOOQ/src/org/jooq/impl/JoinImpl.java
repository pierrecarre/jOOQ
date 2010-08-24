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
import java.sql.SQLException;

import org.jooq.Condition;
import org.jooq.Join;
import org.jooq.JoinType;
import org.jooq.Table;

/**
 * @author Lukas Eder
 */
class JoinImpl extends AbstractQueryPart implements Join {

	private static final long serialVersionUID = 2275930365728978050L;

	private final Table table;
	private final ConditionProviderImpl condition;
	private final JoinType type;

	JoinImpl(Table table, JoinType type, Condition... conditions) {
		this.condition = new ConditionProviderImpl();

		this.table = table;
		this.condition.addConditions(conditions);
		this.type = type;
	}

	@Override
	public int bind(PreparedStatement stmt, int initialIndex) throws SQLException {
		int result = initialIndex;

		result = getTable().bind(stmt, result);
		if (getCondition() != null) {
			result = getCondition().bind(stmt, result);
		}

		return result;
	}

	@Override
	public Condition getCondition() {
		return condition.getWhere();
	}

	@Override
	public Table getTable() {
		return table;
	}

	@Override
	public JoinType getType() {
		return type;
	}

	@Override
	public String toSQLDeclaration(boolean inlineParameters) {
		return toSQL(inlineParameters, true);
	}

	@Override
	public String toSQLReference(boolean inlineParameters) {
		return toSQL(inlineParameters, false);
	}

	private String toSQL(boolean inlineParameters, boolean renderAsDeclaration) {
		StringBuilder sb = new StringBuilder();

		sb.append(getType().toSQL());
		sb.append(" ");

		if (renderAsDeclaration) {
			sb.append(getTable().toSQLDeclaration(inlineParameters));
		} else {
			sb.append(getTable().toSQLReference(inlineParameters));
		}

		if (getCondition() != TRUE_CONDITION) {
			sb.append(" on ");

			if (renderAsDeclaration) {
				sb.append(getCondition().toSQLDeclaration(inlineParameters));
			} else {
				sb.append(getCondition().toSQLReference(inlineParameters));
			}
		}

		return sb.toString();
	}
}
