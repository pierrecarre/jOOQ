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

import static org.jooq.impl.Create.compareCondition;
import static org.jooq.impl.Create.joinCondition;
import static org.jooq.impl.Create.selectQuery;
import static org.jooq.util.oracle.sys.tables.AllColComments.ALL_COL_COMMENTS;
import static org.jooq.util.oracle.sys.tables.AllTabCols.ALL_TAB_COLS;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Record;
import org.jooq.SelectQuery;
import org.jooq.util.AbstractTableDefinition;
import org.jooq.util.ColumnDefinition;
import org.jooq.util.DataType;
import org.jooq.util.Database;
import org.jooq.util.oracle.sys.tables.AllColComments;
import org.jooq.util.oracle.sys.tables.AllTabCols;

/**
 * @author Lukas Eder
 */
public class OracleTableDefinition extends AbstractTableDefinition {

	public OracleTableDefinition(Database database, String name, String comment) {
		super(database, name, comment);
	}

	@Override
	public List<ColumnDefinition> getColumns0() throws SQLException {
		List<ColumnDefinition> result = new ArrayList<ColumnDefinition>();

		SelectQuery q = selectQuery();
		q.addFrom(ALL_TAB_COLS);
		q.addFrom(ALL_COL_COMMENTS);
		q.addConditions(
				joinCondition(AllTabCols.TABLE_NAME, AllColComments.TABLE_NAME),
				joinCondition(AllTabCols.COLUMN_NAME, AllColComments.COLUMN_NAME),
				compareCondition(AllTabCols.OWNER, getSchemaName()),
				compareCondition(AllTabCols.TABLE_NAME, getName()));
		q.addOrderBy(AllTabCols.COLUMN_ID);

		q.execute(getConnection());
		for (Record record : q.getResult()) {
			String name = record.getValue(AllTabCols.COLUMN_NAME);
			int position = record.getValue(AllTabCols.COLUMN_ID).intValue();
			String dataType = record.getValue(AllTabCols.DATA_TYPE);
			int precision = record.getValue(AllTabCols.DATA_PRECISION, BigDecimal.ZERO).intValue();
			int scale = record.getValue(AllTabCols.DATA_SCALE, BigDecimal.ZERO).intValue();
			String comment = record.getValue(AllColComments.COMMENTS);

			Class<?> type = Object.class;

			try {
				type = OracleDataType.valueOf(DataType.normalise(dataType)).getType(precision, scale);
			} catch (Exception e) {
				System.out.println("Unsupported datatype : " + dataType);
			}

			OracleColumnDefinition table = new OracleColumnDefinition(getDatabase(), getName(), name, position, type, comment);
			result.add(table);
		}

		return result;
	}
}
