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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jooq.util.AbstractTableDefinition;
import org.jooq.util.ColumnDefinition;
import org.jooq.util.Database;

/**
 * @author Lukas Eder
 */
public class OracleTableDefinition extends AbstractTableDefinition {

	public OracleTableDefinition(Database database, String name, String comment) {
		super(database, name, comment);
	}

	@Override
	public List<ColumnDefinition> getColumns() throws SQLException {
		List<ColumnDefinition> result = new ArrayList<ColumnDefinition>();
		
		PreparedStatement statement = getConnection().prepareStatement(
            "SELECT col.*, com.comments " +
            "FROM ALL_TAB_COLS col " +
            "JOIN ALL_COL_COMMENTS com ON (col.table_name = com.table_name and col.column_name = com.column_name) " +
            "WHERE col.OWNER = '" + getSchema() + "' " +
            "AND col.TABLE_NAME = '" + getName() + "' " +
            "ORDER BY col.COLUMN_ID");
        
        ResultSet rs = statement.executeQuery();
        
        while (rs.next()) {
          String name = rs.getString("COLUMN_NAME");
          int position = rs.getInt("COLUMN_ID");
          String dataType = rs.getString("DATA_TYPE");
          int precision = rs.getInt("DATA_PRECISION");
          int scale = rs.getInt("DATA_SCALE");
          String comment = rs.getString("COMMENTS");

          try {
            Class<?> type = OracleDataType.valueOf(dataType.toUpperCase()).getType();

            OracleColumnDefinition table = new OracleColumnDefinition(getDatabase(), name, position, type, comment);
            result.add(table);
          } catch (Exception e) {
            System.out.println("Could not map datatype : " + dataType);
          }
        }
        rs.close();
        statement.close();
		return result;
	}
}
