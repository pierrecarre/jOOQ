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

package org.jooq.util.hsqldb;

import static org.jooq.util.hsqldb.information_schema.tables.ConstraintColumnUsage.CONSTRAINT_COLUMN_USAGE;
import static org.jooq.util.hsqldb.information_schema.tables.KeyColumnUsage.KEY_COLUMN_USAGE;
import static org.jooq.util.hsqldb.information_schema.tables.TableConstraints.TABLE_CONSTRAINTS;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Record;
import org.jooq.SelectQuery;
import org.jooq.impl.QueryFactory;
import org.jooq.util.AbstractDatabase;
import org.jooq.util.ColumnDefinition;
import org.jooq.util.DefaultRelations;
import org.jooq.util.FunctionDefinition;
import org.jooq.util.ProcedureDefinition;
import org.jooq.util.TableDefinition;
import org.jooq.util.hsqldb.information_schema.tables.ConstraintColumnUsage;
import org.jooq.util.hsqldb.information_schema.tables.KeyColumnUsage;
import org.jooq.util.hsqldb.information_schema.tables.TableConstraints;

/**
 * @author Lukas Eder
 */
public class HSQLDBDatabase extends AbstractDatabase {

	@Override
	protected void loadPrimaryKeys(DefaultRelations relations) throws SQLException {
		SelectQuery query = QueryFactory.select()
			.from(TABLE_CONSTRAINTS)
			.join(CONSTRAINT_COLUMN_USAGE)
			.on(TableConstraints.CONSTRAINT_NAME.equal(ConstraintColumnUsage.CONSTRAINT_NAME))
			.where(TableConstraints.CONSTRAINT_TYPE.equal("PRIMARY KEY")
			.and(ConstraintColumnUsage.TABLE_SCHEMA.equal(getSchemaName())))
			.getQuery();

		query.execute(getConnection());
		for (Record record : query.getResult()) {
			String key = record.getValue(TableConstraints.CONSTRAINT_NAME);
			String tableName = record.getValue(ConstraintColumnUsage.TABLE_NAME);
			String columnName = record.getValue(ConstraintColumnUsage.COLUMN_NAME);

			relations.addPrimaryKey(key, getTable(tableName).getColumn(columnName));
		}
	}

	@Override
	protected void loadForeignKeys(DefaultRelations relations) throws SQLException {
		SelectQuery query = QueryFactory.select()
			.from(CONSTRAINT_COLUMN_USAGE)
			.join(KEY_COLUMN_USAGE)
			.on(ConstraintColumnUsage.CONSTRAINT_NAME.equal(KeyColumnUsage.CONSTRAINT_NAME))
			.join(TABLE_CONSTRAINTS)
			.on(ConstraintColumnUsage.CONSTRAINT_NAME.equal(TableConstraints.CONSTRAINT_NAME))
			.where(TableConstraints.CONSTRAINT_TYPE.equal("FOREIGN KEY")
			.and(KeyColumnUsage.TABLE_SCHEMA.equal(getSchemaName())))
			.getQuery();

		query.execute(getConnection());
		for (Record record : query.getResult()) {
			String key = record.getValue(TableConstraints.CONSTRAINT_NAME);
			String referencingTableName = record.getValue(KeyColumnUsage.TABLE_NAME);
			String referencingColumnName = record.getValue(KeyColumnUsage.COLUMN_NAME);
			String referencedTableName = record.getValue(ConstraintColumnUsage.TABLE_NAME);
			String referencedColumnName = record.getValue(ConstraintColumnUsage.COLUMN_NAME);

			ColumnDefinition referencingColumn = getTable(referencingTableName).getColumn(referencingColumnName);
			ColumnDefinition referencedColumn = getTable(referencedTableName).getColumn(referencedColumnName);

			String primaryKey = relations.getPrimaryKey(referencedColumn).getName();
			relations.addForeignKey(key, primaryKey, referencingColumn);
		}
	}

	@Override
	protected List<TableDefinition> getTables0() throws SQLException {
		List<TableDefinition> result = new ArrayList<TableDefinition>();

		Statement statement = getConnection().createStatement();
		ResultSet rs = statement.executeQuery(
				"SELECT * FROM INFORMATION_SCHEMA.TABLES " +
				"WHERE TABLE_SCHEMA = '" + getSchemaName() + "' " +
				"ORDER BY TABLE_NAME");

		while (rs.next()) {
			String name = rs.getString("TABLE_NAME");
			String comment = "";

			HSQLDBTableDefinition table = new HSQLDBTableDefinition(this, name, comment);
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
