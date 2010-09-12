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

package org.jooq.util.mysql;

import static org.jooq.impl.QueryFactory.createCompareCondition;
import static org.jooq.impl.QueryFactory.createSelectQuery;
import static org.jooq.util.mysql.information_schema.tables.KeyColumnUsage.KEY_COLUMN_USAGE;
import static org.jooq.util.mysql.information_schema.tables.Tables.TABLES;
import static org.jooq.util.mysql.information_schema.tables.Tables.TABLE_COMMENT;
import static org.jooq.util.mysql.information_schema.tables.Tables.TABLE_NAME;
import static org.jooq.util.mysql.information_schema.tables.Tables.TABLE_SCHEMA;
import static org.jooq.util.mysql.mysql.tables.Proc.COMMENT;
import static org.jooq.util.mysql.mysql.tables.Proc.DB;
import static org.jooq.util.mysql.mysql.tables.Proc.NAME;
import static org.jooq.util.mysql.mysql.tables.Proc.PARAM_LIST;
import static org.jooq.util.mysql.mysql.tables.Proc.PROC;
import static org.jooq.util.mysql.mysql.tables.Proc.RETURNS;
import static org.jooq.util.mysql.mysql.tables.Proc.TYPE;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Comparator;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectQuery;
import org.jooq.util.AbstractDatabase;
import org.jooq.util.ColumnDefinition;
import org.jooq.util.DefaultRelations;
import org.jooq.util.FunctionDefinition;
import org.jooq.util.ProcedureDefinition;
import org.jooq.util.TableDefinition;
import org.jooq.util.mysql.information_schema.tables.KeyColumnUsage;

/**
 * @author Lukas Eder
 */
public class MySQLDatabase extends AbstractDatabase {

	@Override
	protected void loadPrimaryKeys(DefaultRelations relations) throws SQLException {
		SelectQuery q = createSelectQuery(KEY_COLUMN_USAGE);
		q.addCompareCondition(KeyColumnUsage.CONSTRAINT_NAME, "PRIMARY");
		q.addCompareCondition(KeyColumnUsage.TABLE_SCHEMA, getSchemaName());
		q.execute(getConnection());

		for (Record record : q.getResult()) {
			String key = record.getValue(KeyColumnUsage.CONSTRAINT_NAME);
			String tableName = record.getValue(KeyColumnUsage.TABLE_NAME);
			String columnName = record.getValue(KeyColumnUsage.COLUMN_NAME);

			key = key + "_" + tableName;
			relations.addPrimaryKey(key, getTable(tableName).getColumn(columnName));
		}
	}

	@Override
	protected void loadForeignKeys(DefaultRelations relations) throws SQLException {
		SelectQuery q = createSelectQuery(KEY_COLUMN_USAGE);
		q.addCompareCondition(KeyColumnUsage.CONSTRAINT_NAME, "PRIMARY", Comparator.NOT_EQUALS);
		q.addCompareCondition(KeyColumnUsage.TABLE_SCHEMA, getSchemaName());
		q.execute(getConnection());

		for (Record record : q.getResult()) {
			String key = record.getValue(KeyColumnUsage.CONSTRAINT_NAME);
			String referencingTableName = record.getValue(KeyColumnUsage.TABLE_NAME);
			String referencingColumnName = record.getValue(KeyColumnUsage.COLUMN_NAME);
			String referencedTableName = record.getValue(KeyColumnUsage.REFERENCED_TABLE_NAME);
			String referencedColumnName = record.getValue(KeyColumnUsage.REFERENCED_COLUMN_NAME);

			ColumnDefinition referencingColumn = getTable(referencingTableName).getColumn(referencingColumnName);
			ColumnDefinition referencedColumn = getTable(referencedTableName).getColumn(referencedColumnName);

			String primaryKey = relations.getPrimaryKey(referencedColumn).getName();
			relations.addForeignKey(key, primaryKey, referencingColumn);
		}
	}

	@Override
	protected List<TableDefinition> getTables0() throws SQLException {
		List<TableDefinition> result = new ArrayList<TableDefinition>();

		SelectQuery q = createSelectQuery(TABLES);
		q.addSelect(TABLE_NAME);
		q.addSelect(TABLE_COMMENT);
		q.addConditions(createCompareCondition(TABLE_SCHEMA, getSchemaName()));
		q.addOrderBy(TABLE_NAME);
		q.execute(getConnection());

		for (Record record : q.getResult()) {
			String name = record.getValue(TABLE_NAME);
			String comment = record.getValue(TABLE_COMMENT);

			MySQLTableDefinition table = new MySQLTableDefinition(this, name, comment);
			result.add(table);
		}

		return result;
	}

	@Override
	protected List<ProcedureDefinition> getProcedures0() throws SQLException {
		List<ProcedureDefinition> result = new ArrayList<ProcedureDefinition>();

		for (Record record : executeProcedureQuery("PROCEDURE")) {
			String name = record.getValue(NAME);
			String comment = record.getValue(COMMENT);
			String params = new String(record.getValue(PARAM_LIST));

			MySQLProcedureDefinition procedure = new MySQLProcedureDefinition(this, name, comment, params);
			result.add(procedure);
		}

		return result;
	}

	@Override
	protected List<FunctionDefinition> getFunctions0() throws SQLException {
		List<FunctionDefinition> result = new ArrayList<FunctionDefinition>();

		for (Record record : executeProcedureQuery("FUNCTION")) {
			String name = record.getValue(NAME);
			String comment = record.getValue(COMMENT);
			String params = new String(record.getValue(PARAM_LIST));
			String returnValue = new String(record.getValue(RETURNS));

			MySQLFunctionDefinition function = new MySQLFunctionDefinition(this, name, comment, params, returnValue);
			result.add(function);
		}

		return result;
	}

	private Result executeProcedureQuery(String type) throws SQLException {
		SelectQuery q = createSelectQuery(PROC);
		q.addSelect(NAME);
		q.addSelect(PARAM_LIST);
		q.addSelect(COMMENT);
		q.addSelect(RETURNS);
		q.addConditions(createCompareCondition(DB, getSchemaName()));
		q.addConditions(createCompareCondition(TYPE, type));
		q.execute(getConnection());

		return q.getResult();
	}
}
