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

import static org.jooq.impl.QueryFactory.createCompareCondition;
import static org.jooq.impl.QueryFactory.createJoinCondition;
import static org.jooq.impl.QueryFactory.createSelectQuery;
import static org.jooq.util.oracle.sys.tables.AllConsColumns.ALL_CONS_COLUMNS;
import static org.jooq.util.oracle.sys.tables.AllConstraints.ALL_CONSTRAINTS;

import java.sql.SQLException;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectQuery;
import org.jooq.Table;
import org.jooq.util.AbstractColumnDefinition;
import org.jooq.util.Database;
import org.jooq.util.DefaultForeignKeyDefinition;
import org.jooq.util.DefaultPrimaryKeyDefinition;
import org.jooq.util.ForeignKeyDefinition;
import org.jooq.util.PrimaryKeyDefinition;
import org.jooq.util.oracle.sys.tables.AllConsColumns;
import org.jooq.util.oracle.sys.tables.AllConstraints;


/**
 * @author Lukas Eder
 */
public class OracleColumnDefinition extends AbstractColumnDefinition {

	public OracleColumnDefinition(Database database, String table, String name, int position, Class<?> type,
			String comment) {
		super (database, table, name, position, type, comment);
	}

	@Override
	protected PrimaryKeyDefinition getPrimaryKey0() throws SQLException {
		PrimaryKeyDefinition definition = null;
		
		SelectQuery q = createSelectQuery(ALL_CONS_COLUMNS);
		q.addJoin(ALL_CONSTRAINTS, 
				AllConsColumns.CONSTRAINT_NAME, 
				AllConstraints.CONSTRAINT_NAME);
		q.addCompareCondition(AllConstraints.CONSTRAINT_TYPE, "P");
		q.addCompareCondition(AllConsColumns.OWNER, getSchemaName());
		q.addCompareCondition(AllConsColumns.TABLE_NAME, getTableName());
		q.addCompareCondition(AllConsColumns.COLUMN_NAME, getName());
		q.execute(getConnection());

		if (q.getResult().getNumberOfRecords() > 0) {
			definition = new DefaultPrimaryKeyDefinition(getDatabase(), q.getResult().getValue(0, AllConsColumns.CONSTRAINT_NAME));
		}

		return definition;
	}

	@Override
	protected ForeignKeyDefinition getForeignKey0() throws SQLException {
		DefaultForeignKeyDefinition definition = null;
		
//		select cc1.*, cc2.* from all_constraints co
//		join all_cons_columns cc1 on (cc1.constraint_name = co.constraint_name)
//		join all_cons_columns cc2 on (cc2.constraint_name = co.r_constraint_name and cc1.position = cc2.position)
//		where cc1.constraint_name = (
//		  select cox.constraint_name 
//		  from all_cons_columns ccx
//		  join all_constraints cox on (ccx.constraint_name = cox.constraint_name)
//		    where ccx.owner = 'ODS_TEST' 
//		    and ccx.table_name = 'X_UNUSED'
//		    and ccx.column_name = 'NAME_REF'
//		    and cox.constraint_type = 'R');
		
		Table cc1 = ALL_CONS_COLUMNS.alias("cc1");
		Table cc2 = ALL_CONS_COLUMNS.alias("cc2");
		
		Field<String> constraint = cc2.getField(AllConsColumns.CONSTRAINT_NAME).alias("constraint");
		Field<String> referencedTable = cc2.getField(AllConsColumns.TABLE_NAME).alias("referenced_table");
		Field<String> referencingColumn = cc1.getField(AllConsColumns.COLUMN_NAME).alias("referencing_column");
		Field<String> referencedColumn = cc2.getField(AllConsColumns.COLUMN_NAME).alias("referenced_column");
		
		SelectQuery inner = createSelectQuery(ALL_CONS_COLUMNS);
		inner.addJoin(ALL_CONSTRAINTS, AllConsColumns.CONSTRAINT_NAME, AllConstraints.CONSTRAINT_NAME);
		inner.addSelect(AllConstraints.CONSTRAINT_NAME);
		inner.addConditions(
				createCompareCondition(AllConstraints.CONSTRAINT_TYPE, "R"),
				createCompareCondition(AllConsColumns.OWNER, getSchemaName()),
				createCompareCondition(AllConsColumns.TABLE_NAME, getTableName()),
				createCompareCondition(AllConsColumns.COLUMN_NAME, getName()));
		
		SelectQuery q = createSelectQuery(ALL_CONSTRAINTS);
		q.addSelect(constraint, referencingColumn, referencedTable, referencedColumn);
		q.addJoin(cc1,
				createJoinCondition(cc1.getField(AllConsColumns.CONSTRAINT_NAME), AllConstraints.CONSTRAINT_NAME));
		q.addJoin(cc2, 
				createJoinCondition(cc2.getField(AllConsColumns.CONSTRAINT_NAME), AllConstraints.R_CONSTRAINT_NAME),
				createJoinCondition(cc2.getField(AllConsColumns.POSITION), cc1.getField(AllConsColumns.POSITION)));
		q.addConditions(inner.asCompareCondition(cc1.getField(AllConsColumns.CONSTRAINT_NAME)));
		
		q.execute(getConnection());

		Result result = q.getResult();
		for (Record record : result) {
			if (definition == null) {
				definition = new DefaultForeignKeyDefinition(
						getDatabase(),
						record.getValue(constraint),
						record.getValue(referencedTable));
			}

			definition.getKeyColumnNames().add(
					record.getValue(referencingColumn));
			definition.getReferencedColumnNames().add(
					record.getValue(referencedColumn));
		}
		
		return definition;
	}
}
