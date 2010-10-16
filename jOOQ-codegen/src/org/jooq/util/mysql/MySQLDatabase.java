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

import static org.jooq.util.mysql.information_schema.tables.KeyColumnUsage.KEY_COLUMN_USAGE;
import static org.jooq.util.mysql.information_schema.tables.Tables.TABLES;
import static org.jooq.util.mysql.information_schema.tables.Tables.TABLE_COMMENT;
import static org.jooq.util.mysql.information_schema.tables.Tables.TABLE_NAME;
import static org.jooq.util.mysql.information_schema.tables.Tables.TABLE_SCHEMA;
import static org.jooq.util.mysql.mysql.tables.Proc.DB;
import static org.jooq.util.mysql.mysql.tables.Proc.PROC;
import static org.jooq.util.mysql.mysql.tables.Proc.TYPE;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Comparator;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SimpleSelectQuery;
import org.jooq.impl.Factory;
import org.jooq.util.AbstractDatabase;
import org.jooq.util.ColumnDefinition;
import org.jooq.util.DefaultRelations;
import org.jooq.util.FunctionDefinition;
import org.jooq.util.ProcedureDefinition;
import org.jooq.util.TableDefinition;
import org.jooq.util.mysql.information_schema.tables.KeyColumnUsage;
import org.jooq.util.mysql.information_schema.tables.records.KeyColumnUsageRecord;
import org.jooq.util.mysql.information_schema.tables.records.TablesRecord;
import org.jooq.util.mysql.mysql.tables.records.ProcRecord;

/**
 * @author Lukas Eder
 */
public class MySQLDatabase extends AbstractDatabase {

	@Override
	protected void loadPrimaryKeys(DefaultRelations relations) throws SQLException {
		SimpleSelectQuery<KeyColumnUsageRecord> q = create().selectQuery(KEY_COLUMN_USAGE);
		q.addCompareCondition(KeyColumnUsage.CONSTRAINT_NAME, "PRIMARY");
		q.addCompareCondition(KeyColumnUsage.TABLE_SCHEMA, getSchemaName());
		q.execute(getConnection());

		for (KeyColumnUsageRecord record : q.getResult()) {
			String key = record.getConstraintName();
			String tableName = record.getTableName();
			String columnName = record.getColumnName();

			key = key + "_" + tableName;
			TableDefinition table = getTable(tableName);

			if (table != null) {
			    relations.addPrimaryKey(key, table.getColumn(columnName));
			}
		}
	}

	@Override
	protected void loadForeignKeys(DefaultRelations relations) throws SQLException {
		SimpleSelectQuery<KeyColumnUsageRecord> q = create().selectQuery(KEY_COLUMN_USAGE);
		q.addCompareCondition(KeyColumnUsage.CONSTRAINT_NAME, "PRIMARY", Comparator.NOT_EQUALS);
		q.addCompareCondition(KeyColumnUsage.TABLE_SCHEMA, getSchemaName());
		q.execute(getConnection());

		for (KeyColumnUsageRecord record : q.getResult()) {
			String key = record.getConstraintName();
			String referencingTableName = record.getTableName();
			String referencingColumnName = record.getColumnName();
			String referencedTableName = record.getReferencedTableName();
			String referencedColumnName = record.getReferencedColumnName();

			TableDefinition referencingTable = getTable(referencingTableName);
			TableDefinition referencedTable = getTable(referencedTableName);

			if (referencingTable != null && referencedTable != null) {
    			ColumnDefinition referencingColumn = referencingTable.getColumn(referencingColumnName);
                ColumnDefinition referencedColumn = referencedTable.getColumn(referencedColumnName);

    			String primaryKey = relations.getPrimaryKeyName(referencedColumn);
    			relations.addForeignKey(key, primaryKey, referencingColumn);
			}
		}
	}

	@Override
	protected List<TableDefinition> getTables0() throws SQLException {
		List<TableDefinition> result = new ArrayList<TableDefinition>();

		SimpleSelectQuery<TablesRecord> q = create().selectQuery(TABLES);
		q.addSelect(TABLE_NAME);
		q.addSelect(TABLE_COMMENT);
		q.addConditions(create().compareCondition(TABLE_SCHEMA, getSchemaName()));
		q.addOrderBy(TABLE_NAME);
		q.execute(getConnection());

		for (TablesRecord record : q.getResult()) {
			String name = record.getTableName();
			String comment = record.getTableComment();

			MySQLTableDefinition table = new MySQLTableDefinition(this, name, comment);
			result.add(table);
		}

		return result;
	}

	@Override
	protected List<ProcedureDefinition> getProcedures0() throws SQLException {
		List<ProcedureDefinition> result = new ArrayList<ProcedureDefinition>();

		for (ProcRecord record : executeProcedureQuery("PROCEDURE")) {
			String name = record.getName();
			String comment = record.getComment();
			String params = new String(record.getParamList());

			MySQLProcedureDefinition procedure = new MySQLProcedureDefinition(this, name, comment, params);
			result.add(procedure);
		}

		return result;
	}

	@Override
	protected List<FunctionDefinition> getFunctions0() throws SQLException {
		List<FunctionDefinition> result = new ArrayList<FunctionDefinition>();

		for (ProcRecord record : executeProcedureQuery("FUNCTION")) {
			String name = record.getName();
			String comment = record.getComment();
			String params = new String(record.getParamList());
			String returnValue = new String(record.getReturns());

			MySQLFunctionDefinition function = new MySQLFunctionDefinition(this, name, comment, params, returnValue);
			result.add(function);
		}

		return result;
	}

	private Result<ProcRecord> executeProcedureQuery(String type) throws SQLException {
		SimpleSelectQuery<ProcRecord> q = create().selectQuery(PROC);
		q.addConditions(create().compareCondition(DB, getSchemaName()));
		q.addConditions(create().compareCondition(TYPE, type));
		q.execute(getConnection());

		return q.getResult();
	}

    @Override
    public Factory create() {
        return new Factory(getConnection(), SQLDialect.MYSQL);
    }
}
