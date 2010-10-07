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

import org.jooq.AliasProvider;
import org.jooq.Field;
import org.jooq.FieldList;
import org.jooq.Record;
import org.jooq.Table;

/**
 * @author Lukas Eder
 */
class TableAlias<R extends Record<R>> extends TableImpl<R> implements Table<R>, AliasProvider<Table<R>> {

	private static final long serialVersionUID = -8417114874567698325L;
	private final AliasProviderImpl<Table<R>> aliasProvider;
	private FieldList aliasedFields;

	TableAlias(Table<R> table, String alias) {
		this(table, alias, false);
	}

	TableAlias(Table<R> table, String alias, boolean wrapInParentheses) {
		super(alias, table.getSchema());

		this.aliasProvider = new AliasProviderImpl<Table<R>>(table, alias, wrapInParentheses);
	}

	@Override
	public final String toSQLReference(boolean inlineParameters) {
		return aliasProvider.toSQLReference(inlineParameters);
	}

	@Override
	public final String toSQLDeclaration(boolean inlineParameters) {
		return aliasProvider.toSQLDeclaration(inlineParameters);
	}

	@Override
	public final int bind(PreparedStatement stmt, int initialIndex) throws SQLException {
		return aliasProvider.bind(stmt, initialIndex);
	}

	@Override
	public final Table<R> as(String alias) {
		return aliasProvider.as(alias);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public FieldList getFields() {
		if (aliasedFields == null) {
			aliasedFields = new FieldListImpl();

			for (Field<?> field : aliasProvider.getAliasProvider().getFields()) {

				// Instanciating a TableFieldImpl will add the field to this
				new TableFieldImpl(field.getName(), field.getType(), this);
			}
		}

		return aliasedFields;
	}

	@Override
	public Class<R> getRecordType() {
		return aliasProvider.getAliasProvider().getRecordType();
	}
}
