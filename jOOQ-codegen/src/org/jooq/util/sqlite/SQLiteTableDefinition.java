/**
 * Copyright (c) 2010, Lukas Eder, lukas.eder@gmail.com
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
package org.jooq.util.sqlite;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jooq.util.AbstractTableDefinition;
import org.jooq.util.ColumnDefinition;
import org.jooq.util.Database;
import org.jooq.util.DefaultColumnDefinition;

/**
 * H2 table definition
 *
 * @author Espen Stromsnes
 */

public class SQLiteTableDefinition extends AbstractTableDefinition {

    public SQLiteTableDefinition(Database database, String name, String comment) {
        super(database, name, comment);
    }

    @Override
    public List<ColumnDefinition> getColumns0() throws SQLException {
        List<ColumnDefinition> result = new ArrayList<ColumnDefinition>();

        PreparedStatement stmt = getConnection().prepareStatement("pragma table_info('" + getName() + "')");
        ResultSet rs = stmt.executeQuery();

        int position = 0;
        while (rs.next()) {
            String name = rs.getString("name");
            position++;
            String dataType = rs.getString("type").replaceAll("\\(\\d+\\)", "");

            String type = getDatabase().getType(dataType, 0, 0, null);

            ColumnDefinition column = new DefaultColumnDefinition(getDatabase(), getName(), name, position, type, "");
            result.add(column);
        }

        rs.close();
        stmt.close();

        return result;
    }
}
