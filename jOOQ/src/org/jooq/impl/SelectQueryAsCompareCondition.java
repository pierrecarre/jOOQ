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

import org.jooq.Comparator;
import org.jooq.CompareCondition;
import org.jooq.Field;
import org.jooq.SelectQuery;

/**
 * @author Lukas Eder
 */
class SelectQueryAsCompareCondition<T> extends AbstractNamedQueryPart implements CompareCondition<T> {

	private static final long serialVersionUID = -3125318907657091582L;
	private final SelectQuery query;
	private final Field<T> field;

	SelectQueryAsCompareCondition(SelectQuery query, Field<T> field) {
		super("");

		this.query = query;
		this.field = field;
	}

	@Override
	public Field<T> getField() {
		return field;
	}

	@Override
	public String toSQLReference(boolean inlineParameters) {
		StringBuilder sb = new StringBuilder();

		sb.append(field.toSQLReference(inlineParameters));
		sb.append(" ");
		sb.append(getComparator().toSQL());
		sb.append(" (");
		sb.append(query.toSQLReference(inlineParameters));
		sb.append(")");

		return sb.toString();
	}

	@Override
	public int bind(PreparedStatement stmt, int initialIndex) throws SQLException {
		initialIndex = field.bind(stmt, initialIndex);
		return query.bind(stmt, initialIndex);
	}

	@Override
	public Comparator getComparator() {
		return Comparator.EQUALS;
	}

	@Override
	public T getValue() {
		throw new UnsupportedOperationException("Cannot retrieve values of ResultProviderQueryAsCompareCondition");
	}

}
