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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jooq.util.AbstractTableDefinition;
import org.jooq.util.ColumnDefinition;
import org.jooq.util.Database;
import org.jooq.util.DefaultColumnDefinition;

/**
 * @author Lukas Eder
 */
public class HSQLDBTableDefinition extends AbstractTableDefinition {

	public HSQLDBTableDefinition(Database database, String name, String comment) {
		super(database, name, comment);
	}

	@Override
	public List<ColumnDefinition> getColumns0() throws SQLException {
		List<ColumnDefinition> result = new ArrayList<ColumnDefinition>();

		Statement statement = getConnection().createStatement();
		ResultSet rs = statement.executeQuery(
				"SELECT * FROM INFORMATION_SCHEMA.COLUMNS " +
				"WHERE TABLE_SCHEMA = '" + getSchemaName() + "' " +
				"AND TABLE_NAME = '" + getName() + "'" +
				"ORDER BY ORDINAL_POSITION");

		while (rs.next()) {
			String name = rs.getString("COLUMN_NAME");
			int position = rs.getInt("ORDINAL_POSITION");
			String dataType = rs.getString("DATA_TYPE");
			String type = getDatabase().getType(dataType, 0, 0, null);

			ColumnDefinition column = new DefaultColumnDefinition(getDatabase(), getName(), name, position, type, "");
			result.add(column);
		}

		return result;
	}
}
