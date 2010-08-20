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
import static org.jooq.util.mysql.information_schema.tables.Columns.COLUMNS;
import static org.jooq.util.mysql.information_schema.tables.Columns.COLUMN_COMMENT;
import static org.jooq.util.mysql.information_schema.tables.Columns.COLUMN_NAME;
import static org.jooq.util.mysql.information_schema.tables.Columns.DATA_TYPE;
import static org.jooq.util.mysql.information_schema.tables.Columns.ORDINAL_POSITION;
import static org.jooq.util.mysql.information_schema.tables.Columns.TABLE_NAME;
import static org.jooq.util.mysql.information_schema.tables.Columns.TABLE_SCHEMA;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Record;
import org.jooq.SelectQuery;
import org.jooq.util.AbstractTableDefinition;
import org.jooq.util.ColumnDefinition;
import org.jooq.util.Database;

/**
 * @author Lukas Eder
 */
public class MySQLTableDefinition extends AbstractTableDefinition {

	public MySQLTableDefinition(Database database, String name, String comment) {
		super(database, name, comment);
	}

	@Override
	public List<ColumnDefinition> getColumns0() throws SQLException {
		List<ColumnDefinition> result = new ArrayList<ColumnDefinition>();

		SelectQuery q = createSelectQuery(COLUMNS);

		q.addSelect(COLUMN_NAME);
		q.addSelect(ORDINAL_POSITION);
		q.addSelect(DATA_TYPE);
		q.addSelect(COLUMN_COMMENT);
		q.addConditions(createCompareCondition(TABLE_SCHEMA, getSchemaName()));
		q.addConditions(createCompareCondition(TABLE_NAME, getName()));
		q.addOrderBy(ORDINAL_POSITION);

		q.execute(getConnection());
		for (Record record : q.getResult()) {
			String name = record.getValue(COLUMN_NAME);
			int position = record.getValue(ORDINAL_POSITION);
			String dataType = record.getValue(DATA_TYPE);
			String comment = record.getValue(COLUMN_COMMENT);

			Class<?> type = MySQLDataType.valueOf(dataType.toUpperCase()).getType();
			MySQLColumnDefinition column = new MySQLColumnDefinition(getDatabase(), getName(), name, position, type, comment);
			result.add(column);
		}

		return result;
	}
}
