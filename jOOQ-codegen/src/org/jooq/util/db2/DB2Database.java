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
package org.jooq.util.db2;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.SQLDialect;
import org.jooq.impl.Factory;
import org.jooq.impl.JooqLogger;
import org.jooq.util.AbstractDatabase;
import org.jooq.util.ColumnDefinition;
import org.jooq.util.DataType;
import org.jooq.util.DefaultRelations;
import org.jooq.util.EnumDefinition;
import org.jooq.util.FunctionDefinition;
import org.jooq.util.ProcedureDefinition;
import org.jooq.util.TableDefinition;
import org.jooq.util.UDTDefinition;


/**
 * DB2 implementation of {@link AbstractDatabase}
 * 
 * @author Espen Stromsnes
 */
public class DB2Database extends AbstractDatabase {

    private static final JooqLogger logger = JooqLogger.getLogger(DB2Database.class);
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Factory create() {
        return new Factory(getConnection(), SQLDialect.DB2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadPrimaryKeys(DefaultRelations relations) throws SQLException {
        Statement statement = getConnection().createStatement();
        ResultSet rs = statement.executeQuery(
                "SELECT KEYCOLUSE.CONSTNAME, KEYCOLUSE.TABNAME, KEYCOLUSE.COLNAME " +
                "FROM SYSCAT.KEYCOLUSE KEYCOLUSE, SYSCAT.TABCONST TABCONST " +
                "WHERE KEYCOLUSE.CONSTNAME = TABCONST.CONSTNAME " +
                "AND KEYCOLUSE.TABSCHEMA = TABCONST.TABSCHEMA " +
                "AND KEYCOLUSE.TABSCHEMA = '" + getSchemaName() + "' " +
                "AND TABCONST.TYPE = 'P' " + 
                "ORDER BY KEYCOLUSE.COLSEQ");

        while (rs.next()) {
            String key = rs.getString("CONSTNAME");
            String tableName = rs.getString("TABNAME");
            String columnName = rs.getString("COLNAME");

            TableDefinition table = getTable(tableName);
            if (table != null) {
                relations.addPrimaryKey(key, table.getColumn(columnName));
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
                "SELECT * FROM SYSCAT.REFERENCES " + 
                "WHERE TABSCHEMA = '" + getSchemaName() + "'");

        while (rs.next()) {
            String key = rs.getString("CONSTNAME").trim();
            String referencingTableName = rs.getString("TABNAME");
            String referencingColumnName = rs.getString("FK_COLNAMES");
            String referencedTableName = rs.getString("REFTABNAME");
            String referencedColumnName = rs.getString("PK_COLNAMES");

            TableDefinition referencingTable = getTable(referencingTableName);
            TableDefinition referencedTable = getTable(referencedTableName);

            if (referencingTable != null && referencedTable != null) {
                // Special DB2 hack. If a foreign key consists of several columns, all
                // the columns are contained in a single database column (delimited with space)
                // here we split the combined string into individual columns 
                String[] referencingColumnNames = referencingColumnName.trim().split("[ ]+");
                String[] referencedColumnNames = referencedColumnName.trim().split("[ ]+");
                if (referencingColumnNames.length == referencedColumnNames.length) {
                    for (int i=0; i < referencingColumnNames.length; i++) {
                        ColumnDefinition referencingColumn = referencingTable.getColumn(referencingColumnNames[i]);
                        ColumnDefinition referencedColumn = referencedTable.getColumn(referencedColumnNames[i]);

                        String primaryKey = relations.getPrimaryKeyName(referencedColumn);
                        relations.addForeignKey(key, primaryKey, referencingColumn);        
                    }
                }
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
                "SELECT * FROM SYSCAT.TABLES " +
                "WHERE TABSCHEMA = '" + getSchemaName() + "' " +
                " AND TYPE IN ('T', 'V')" +  // tables and views 
                "ORDER BY TABNAME");


        while (rs.next()) {
            String name = rs.getString("TABNAME");
            String comment = "";

            DB2TableDefinition table = new DB2TableDefinition(this, name, comment);
            result.add(table);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<ProcedureDefinition> getProcedures0() throws SQLException {
        Map<String, DB2ProcedureDefinition> procedureMap = new HashMap<String, DB2ProcedureDefinition>();
        
        Statement statement = getConnection().createStatement();
        ResultSet rs = statement.executeQuery(
                "SELECT PROCPARMS.* FROM " +
                  " SYSCAT.PROCEDURES PROCEDURES " +
                  " INNER JOIN SYSCAT.PROCPARMS PROCPARMS ON (PROCEDURES.PROCSCHEMA = PROCPARMS.PROCSCHEMA " +
                    " AND PROCEDURES.PROCNAME = PROCPARMS.PROCNAME) " +
                  " WHERE PROCEDURES.PROCSCHEMA = '" + getSchemaName() + "' " +
                "ORDER BY PROCPARMS.PROCNAME, PROCPARMS.ORDINAL");

        while (rs.next()) {
            String name = rs.getString("PROCNAME");
            String paramName = rs.getString("PARMNAME");
            String dataType = rs.getString("TYPENAME");
            int position = rs.getInt("ORDINAL");
            int length = rs.getInt("LENGTH");
            short scale = rs.getShort("SCALE");
            String paramMode = rs.getString("PARM_MODE");

            DB2ProcedureDefinition procedure = procedureMap.get(name);
            if (procedure == null) {
                procedure = new DB2ProcedureDefinition(this, name);
                procedureMap.put(name, procedure);
            }
            
            String type = Object.class.getName();
            try {
                type = DB2DataType.valueOf(DataType.normalise(dataType)).getType().getCanonicalName();
            } catch (Exception e) {
                logger.warn("Unsupported datatype : " + dataType);
            }
            procedure.addParameter(paramMode, paramName, position, type, length, scale);
        }
        return new ArrayList<ProcedureDefinition>(procedureMap.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<FunctionDefinition> getFunctions0() throws SQLException {
        Map<String, DB2FunctionDefinition> functionMap = new HashMap<String, DB2FunctionDefinition>();

        Statement statement = getConnection().createStatement();
        ResultSet rs = statement.executeQuery("SELECT FUNCPARMS.* FROM " + " SYSCAT.FUNCTIONS FUNCTIONS "
            + " INNER JOIN SYSCAT.FUNCPARMS FUNCPARMS ON (FUNCTIONS.FUNCSCHEMA = FUNCPARMS.FUNCSCHEMA "
            + " AND FUNCTIONS.FUNCNAME = FUNCPARMS.FUNCNAME) " + " WHERE FUNCTIONS.FUNCSCHEMA = '" + getSchemaName()
            + "' " + "ORDER BY FUNCPARMS.FUNCNAME, FUNCPARMS.ROWTYPE, FUNCPARMS.ORDINAL");

        while (rs.next()) {
            String name = rs.getString("FUNCNAME");
            String rowType = rs.getString("ROWTYPE");
            String dataType = rs.getString("TYPENAME");
            int position = rs.getInt("ORDINAL");
            String paramName = rs.getString("PARMNAME");
            int length = rs.getInt("LENGTH");
            short scale = rs.getShort("SCALE");

            DB2FunctionDefinition function = functionMap.get(name);
            if (function == null) {
                function = new DB2FunctionDefinition(this, name, null);
                functionMap.put(name, function);
            }

            String type = Object.class.getName();
            try {
                type = DB2DataType.valueOf(DataType.normalise(dataType)).getType().getCanonicalName();
            } catch (Exception e) {
                logger.warn("Unsupported datatype : " + dataType);
            }

            if ("C".equals(rowType)) { // result after casting
                function.setReturnValue(type);
            } else if ("P".equals(rowType)) { // parameter
                function.addParameter(paramName, position, type, length, scale);
            } else { // result before casting
                // continue
            }
        }
        return new ArrayList<FunctionDefinition>(functionMap.values());
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
