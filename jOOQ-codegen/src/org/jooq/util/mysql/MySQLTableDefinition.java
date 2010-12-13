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

import static org.jooq.util.mysql.information_schema.tables.Columns.COLUMNS;
import static org.jooq.util.mysql.information_schema.tables.Columns.ORDINAL_POSITION;
import static org.jooq.util.mysql.information_schema.tables.Columns.TABLE_NAME;
import static org.jooq.util.mysql.information_schema.tables.Columns.TABLE_SCHEMA;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jooq.SimpleSelectQuery;
import org.jooq.impl.JooqLogger;
import org.jooq.util.AbstractTableDefinition;
import org.jooq.util.ColumnDefinition;
import org.jooq.util.DataType;
import org.jooq.util.Database;
import org.jooq.util.DefaultColumnDefinition;
import org.jooq.util.mysql.information_schema.tables.records.ColumnsRecord;

/**
 * @author Lukas Eder
 */
public class MySQLTableDefinition extends AbstractTableDefinition {

    private static final JooqLogger logger = JooqLogger.getLogger(MySQLTableDefinition.class);

    public MySQLTableDefinition(Database database, String name, String comment) {
		super(database, name, comment);
	}

	@Override
	public List<ColumnDefinition> getColumns0() throws SQLException {
		List<ColumnDefinition> result = new ArrayList<ColumnDefinition>();

		SimpleSelectQuery<ColumnsRecord> q = create().selectQuery(COLUMNS);

		q.addConditions(create().compareCondition(TABLE_SCHEMA, getSchemaName()));
		q.addConditions(create().compareCondition(TABLE_NAME, getName()));
		q.addOrderBy(ORDINAL_POSITION);

		q.execute();
		for (ColumnsRecord record : q.getResult()) {
			String name = record.getColumnName();
			int position = record.getOrdinalPosition().intValue();
			String dataType = record.getDataType();
			String comment = record.getColumnComment();

			String type = Object.class.getName();

			try {
				type = MySQLDataType.valueOf(DataType.normalise(dataType)).getType().getCanonicalName();
			} catch (Exception e) {
			    if (getDatabase().getEnum(getName() + "_" + name) != null) {
                    type = getDatabase().getEnum(getName() + "_" + name).getFullJavaClassName();
                } else {
                    logger.warn("Unsupported datatype : " + dataType);
                }
			}

			ColumnDefinition column = new DefaultColumnDefinition(getDatabase(), getName(), name, position, type, comment);
			result.add(column);
		}

		return result;
	}
}
