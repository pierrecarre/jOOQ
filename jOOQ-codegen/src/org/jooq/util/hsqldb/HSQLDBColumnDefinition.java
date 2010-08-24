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

import static org.jooq.impl.QueryFactory.createCompareCondition;
import static org.jooq.impl.QueryFactory.createSelectQuery;
import static org.jooq.util.hsqldb.information_schema.tables.ConstraintColumnUsage.CONSTRAINT_COLUMN_USAGE;
import static org.jooq.util.hsqldb.information_schema.tables.KeyColumnUsage.KEY_COLUMN_USAGE;
import static org.jooq.util.hsqldb.information_schema.tables.TableConstraints.TABLE_CONSTRAINTS;

import java.sql.SQLException;

import org.jooq.Result;
import org.jooq.SelectQuery;
import org.jooq.util.AbstractColumnDefinition;
import org.jooq.util.Database;
import org.jooq.util.DefaultForeignKeyDefinition;
import org.jooq.util.DefaultPrimaryKeyDefinition;
import org.jooq.util.ForeignKeyDefinition;
import org.jooq.util.PrimaryKeyDefinition;
import org.jooq.util.hsqldb.information_schema.tables.ConstraintColumnUsage;
import org.jooq.util.hsqldb.information_schema.tables.KeyColumnUsage;
import org.jooq.util.hsqldb.information_schema.tables.TableConstraints;

/**
 * @author Lukas Eder
 */
public class HSQLDBColumnDefinition extends AbstractColumnDefinition {

	public HSQLDBColumnDefinition(Database database, String table, String name, int position, Class<?> type,
			String comment) {
		super(database, table, name, position, type, comment);
	}

	@Override
	protected PrimaryKeyDefinition getPrimaryKey0() throws SQLException {
		PrimaryKeyDefinition definition = null;

		SelectQuery q = createSelectQuery(TABLE_CONSTRAINTS);
		q.addJoin(CONSTRAINT_COLUMN_USAGE,
				TableConstraints.CONSTRAINT_NAME,
				ConstraintColumnUsage.CONSTRAINT_NAME);
		q.addCompareCondition(TableConstraints.CONSTRAINT_TYPE, "PRIMARY KEY");
		q.addCompareCondition(ConstraintColumnUsage.TABLE_SCHEMA, getSchemaName());
		q.addCompareCondition(ConstraintColumnUsage.TABLE_NAME, getTableName());
		q.addCompareCondition(ConstraintColumnUsage.COLUMN_NAME, getName());
		q.execute(getConnection());

		if (q.getResult().getNumberOfRecords() > 0) {
			definition = new DefaultPrimaryKeyDefinition(getDatabase(), q.getResult().getValue(0, TableConstraints.CONSTRAINT_NAME));
		}

		return definition;
	}

	@Override
	protected ForeignKeyDefinition getForeignKey0() throws SQLException {
		DefaultForeignKeyDefinition definition = null;

		// Find the constraint name (if any) for this column
		SelectQuery inner = createSelectQuery(KEY_COLUMN_USAGE);
		inner.addSelect(KeyColumnUsage.CONSTRAINT_NAME);
		inner.addJoin(TABLE_CONSTRAINTS,
				TableConstraints.CONSTRAINT_NAME,
				KeyColumnUsage.CONSTRAINT_NAME);
		inner.addConditions(
				createCompareCondition(TableConstraints.CONSTRAINT_TYPE, "FOREIGN KEY"),
				createCompareCondition(KeyColumnUsage.TABLE_SCHEMA, getSchemaName()),
				createCompareCondition(KeyColumnUsage.TABLE_NAME, getTableName()),
				createCompareCondition(KeyColumnUsage.COLUMN_NAME, getName()));

		// Find all columns participating in the constraint name (if any)
		SelectQuery referenced = createSelectQuery(CONSTRAINT_COLUMN_USAGE);
		referenced.addConditions(
				inner.asCompareCondition(ConstraintColumnUsage.CONSTRAINT_NAME));

		referenced.execute(getConnection());

		if (referenced.getResult().getNumberOfRecords() > 0) {
			SelectQuery referencing = createSelectQuery(KEY_COLUMN_USAGE);
			referencing.addConditions(
					inner.asCompareCondition(KeyColumnUsage.CONSTRAINT_NAME));

			referencing.execute(getConnection());

			Result resultReferenced = referenced.getResult();
			Result resultReferencing = referencing.getResult();

			for (int i = 0; i < resultReferenced.getNumberOfRecords(); i++) {
				if (definition == null) {
					definition = new DefaultForeignKeyDefinition(
							getDatabase(),
							resultReferenced.getValue(i, ConstraintColumnUsage.CONSTRAINT_NAME),
							resultReferenced.getValue(i, ConstraintColumnUsage.TABLE_NAME));
				}

				definition.getKeyColumnNames().add(
						resultReferencing.getValue(i, KeyColumnUsage.COLUMN_NAME));
				definition.getReferencedColumnNames().add(
						resultReferenced.getValue(i, ConstraintColumnUsage.COLUMN_NAME));
			}
		}

		return definition;
	}
}
