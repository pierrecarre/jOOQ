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

import org.jooq.SelectQuery;
import org.jooq.Table;

/**
 * @author Lukas Eder
 */
class SelectQueryImpl extends AbstractSelectQuery implements SelectQuery {

	private static final long serialVersionUID = -4128783317946627405L;

	SelectQueryImpl() {
	}

	SelectQueryImpl(Table from) {
		super(from);
	}

	@Override
	public int bind(PreparedStatement stmt, int initialIndex) throws SQLException {
		int result = initialIndex;

		result = getSelect0().bind(stmt, result);
		result = getFrom().bind(stmt, result);
		result = getJoin().bind(stmt, result);
		result = getWhere().bind(stmt, result);
		result = getGroupBy().bind(stmt, result);
		result = getHaving().bind(stmt, result);
		result = getOrderBy().bind(stmt, result);

		return result;
	}

	@Override
	public String toSQLReference(boolean inlineParameters) {
		StringBuilder sb = new StringBuilder();

		sb.append("select ");
		sb.append(getSelect0().toSQLDeclaration(inlineParameters));

		sb.append(" from ");
		sb.append(getFrom().toSQLDeclaration(inlineParameters));

		if (!getJoin().isEmpty()) {
			sb.append(" ");
			sb.append(getJoin().toSQLDeclaration(inlineParameters));
		}

		if (getWhere() != TRUE_CONDITION) {
			sb.append(" where ");
			sb.append(getWhere().toSQLReference(inlineParameters));
		}

		if (!getGroupBy().isEmpty()) {
			sb.append(" group by ");
			sb.append(getGroupBy().toSQLReference(inlineParameters));
		}

		if (getHaving() != TRUE_CONDITION) {
			sb.append(" having ");
			sb.append(getHaving().toSQLDeclaration(inlineParameters));
		}

		if (!getOrderBy().isEmpty()) {
			sb.append(" order by ");
			sb.append(getOrderBy().toSQLReference(inlineParameters));
		}

		if (getLimit().isApplicable()) {
			sb.append(" ");
			sb.append(getLimit().toSQLReference(inlineParameters));
		}

		return sb.toString();
	}
}
