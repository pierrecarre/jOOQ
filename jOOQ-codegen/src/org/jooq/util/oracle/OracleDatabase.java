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

package org.jooq.util.oracle;

import static org.jooq.Comparator.NOT_LIKE;
import static org.jooq.impl.QueryFactory.createCompareCondition;
import static org.jooq.impl.QueryFactory.createSelectQuery;
import static org.jooq.util.oracle.sys.tables.AllTabComments.ALL_TAB_COMMENTS;
import static org.jooq.util.oracle.sys.tables.AllTabComments.COMMENTS;
import static org.jooq.util.oracle.sys.tables.AllTabComments.OWNER;
import static org.jooq.util.oracle.sys.tables.AllTabComments.TABLE_NAME;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Record;
import org.jooq.SelectQuery;
import org.jooq.util.AbstractDatabase;
import org.jooq.util.DefaultRelations;
import org.jooq.util.FunctionDefinition;
import org.jooq.util.ProcedureDefinition;
import org.jooq.util.TableDefinition;

/**
 * @author Lukas Eder
 */
public class OracleDatabase extends AbstractDatabase {

	@Override
	protected void loadPrimaryKeys(DefaultRelations relations) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void loadForeignKeys(DefaultRelations relations) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected List<TableDefinition> getTables0() throws SQLException {
		List<TableDefinition> result = new ArrayList<TableDefinition>();

		SelectQuery<Record> q = createSelectQuery(ALL_TAB_COMMENTS);
		q.addSelect(TABLE_NAME);
		q.addSelect(COMMENTS);
		q.addConditions(
				createCompareCondition(OWNER, getSchemaName()),
				createCompareCondition(TABLE_NAME, "%$%", NOT_LIKE)); // Exclude weird oracle binary objects
		q.addOrderBy(TABLE_NAME);
		q.execute(getConnection());

		for (Record record : q.getResult()) {
			String name = record.getValue(TABLE_NAME);
			String comment = record.getValue(COMMENTS);

			OracleTableDefinition table = new OracleTableDefinition(this, name, comment);
			result.add(table);
		}

		return result;
	}

	@Override
	protected List<ProcedureDefinition> getProcedures0() throws SQLException {
		List<ProcedureDefinition> result = new ArrayList<ProcedureDefinition>();
		return result;
	}

	@Override
	protected List<FunctionDefinition> getFunctions0() throws SQLException {
		List<FunctionDefinition> result = new ArrayList<FunctionDefinition>();
		return result;
	}
}
