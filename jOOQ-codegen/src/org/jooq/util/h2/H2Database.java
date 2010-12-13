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
package org.jooq.util.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jooq.SQLDialect;
import org.jooq.impl.Factory;
import org.jooq.util.AbstractDatabase;
import org.jooq.util.ColumnDefinition;
import org.jooq.util.DefaultRelations;
import org.jooq.util.EnumDefinition;
import org.jooq.util.FunctionDefinition;
import org.jooq.util.ProcedureDefinition;
import org.jooq.util.TableDefinition;
import org.jooq.util.UDTDefinition;

/**
 * H2 implementation of {@link AbstractDatabase}
 * 
 * @author Espen Stromsnes
 */
public class H2Database  extends AbstractDatabase {

    /**
     * {@inheritDoc}
     */
    @Override
    public Factory create() {
        return new Factory(getConnection(), SQLDialect.H2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadPrimaryKeys(DefaultRelations relations) throws SQLException {
        Statement statement = getConnection().createStatement();
        ResultSet rs = statement.executeQuery(
            "SELECT * FROM INFORMATION_SCHEMA.CONSTRAINTS " +
            "WHERE TABLE_SCHEMA = '" + getSchemaName() + "' " +
            "AND CONSTRAINT_TYPE = 'PRIMARY KEY'");
        while (rs.next()) {
            String key = rs.getString("CONSTRAINT_NAME");
            String tableName = rs.getString("TABLE_NAME");
            String columnList = rs.getString("COLUMN_LIST");
            TableDefinition table = getTable(tableName);
            if (table != null) {
                String[] columnNames = columnList.split("[,]+");
                for (String columnName : columnNames) {
                    relations.addPrimaryKey(key, table.getColumn(columnName));
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadForeignKeys(DefaultRelations relations) throws SQLException {
        Statement statement = getConnection().createStatement();
        ResultSet rs = statement.executeQuery(
            "SELECT * FROM INFORMATION_SCHEMA.CROSS_REFERENCES " +
            "WHERE PKTABLE_SCHEMA = '" + getSchemaName() + "' " +
            "ORDER BY FK_NAME, ORDINAL_POSITION");
        
        while (rs.next()) {
            String key = rs.getString("FK_NAME");
            String referencingTableName = rs.getString("FKTABLE_NAME");
            String referencingColumnName = rs.getString("FKCOLUMN_NAME");
            String referencedTableName = rs.getString("PKTABLE_NAME");
            String referencedColumnName = rs.getString("PKCOLUMN_NAME");

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

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<TableDefinition> getTables0() throws SQLException {
        List<TableDefinition> result = new ArrayList<TableDefinition>();

        Statement statement = getConnection().createStatement();
        ResultSet rs = statement.executeQuery(
                "SELECT * FROM INFORMATION_SCHEMA.TABLES " +
                "WHERE TABLE_SCHEMA = '" + getSchemaName() + "' " +
                "ORDER BY ID");

        while (rs.next()) {
            String name = rs.getString("TABLE_NAME");
            String comment = "";

            H2TableDefinition table = new H2TableDefinition(this, name, comment);
            result.add(table);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<ProcedureDefinition> getProcedures0() throws SQLException {
        List<ProcedureDefinition> result = new ArrayList<ProcedureDefinition>();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<FunctionDefinition> getFunctions0() throws SQLException {
        List<FunctionDefinition> result = new ArrayList<FunctionDefinition>();
        return result;
    }
    
    @Override
    protected List<EnumDefinition> getEnums0() throws SQLException {
        List<EnumDefinition> result = new ArrayList<EnumDefinition>();
        return result;
    }

    @Override
    protected List<UDTDefinition> getUDTs0() throws SQLException {
        List<UDTDefinition> result = new ArrayList<UDTDefinition>();
        return result;
    }

}
