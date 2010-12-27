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

import static org.jooq.util.db2.syscat.tables.Funcparms.FUNCPARMS;
import static org.jooq.util.db2.syscat.tables.Functions.FUNCTIONS;
import static org.jooq.util.db2.syscat.tables.Keycoluse.KEYCOLUSE;
import static org.jooq.util.db2.syscat.tables.Procedures.PROCEDURES;
import static org.jooq.util.db2.syscat.tables.Procparms.PROCPARMS;
import static org.jooq.util.db2.syscat.tables.References.REFERENCES;
import static org.jooq.util.db2.syscat.tables.Tabconst.TABCONST;
import static org.jooq.util.db2.syscat.tables.Tables.TABLES;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.SelectQuery;
import org.jooq.SimpleSelectQuery;
import org.jooq.impl.Factory;
import org.jooq.util.AbstractDatabase;
import org.jooq.util.ColumnDefinition;
import org.jooq.util.DefaultRelations;
import org.jooq.util.EnumDefinition;
import org.jooq.util.FunctionDefinition;
import org.jooq.util.PackageDefinition;
import org.jooq.util.ProcedureDefinition;
import org.jooq.util.TableDefinition;
import org.jooq.util.UDTDefinition;
import org.jooq.util.db2.syscat.tables.Funcparms;
import org.jooq.util.db2.syscat.tables.Functions;
import org.jooq.util.db2.syscat.tables.Keycoluse;
import org.jooq.util.db2.syscat.tables.Procedures;
import org.jooq.util.db2.syscat.tables.Procparms;
import org.jooq.util.db2.syscat.tables.References;
import org.jooq.util.db2.syscat.tables.Tabconst;
import org.jooq.util.db2.syscat.tables.Tables;
import org.jooq.util.db2.syscat.tables.records.ReferencesRecord;
import org.jooq.util.db2.syscat.tables.records.TablesRecord;

/**
 * DB2 implementation of {@link AbstractDatabase}
 *
 * @author Espen Stromsnes
 */
public class DB2Database extends AbstractDatabase {

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
        SelectQuery q = create().selectQuery();
        q.addFrom(KEYCOLUSE);
        q.addJoin(TABCONST, Keycoluse.CONSTNAME.equal(Tabconst.CONSTNAME),
            Keycoluse.TABSCHEMA.equal(Tabconst.TABSCHEMA));
        q.addCompareCondition(Keycoluse.TABSCHEMA, getSchemaName());
        q.addCompareCondition(Tabconst.TYPE, "P");
        q.addOrderBy(Keycoluse.COLSEQ);
        q.execute();

        for (Record record : q.getResult()) {
            String key = record.getValue(Keycoluse.CONSTNAME);
            String tableName = record.getValue(Keycoluse.TABNAME);
            String columnName = record.getValue(Keycoluse.COLNAME);

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
        SimpleSelectQuery<ReferencesRecord> q = create().selectQuery(REFERENCES);
        q.addCompareCondition(References.TABSCHEMA, getSchemaName());
        q.execute();

        for (ReferencesRecord record : q.getResult()) {
            String key = record.getConstname().trim();
            String referencingTableName = record.getTabname();
            String referencingColumnName = record.getFkColnames();
            String referencedTableName = record.getReftabname();
            String referencedColumnName = record.getPkColnames();

            TableDefinition referencingTable = getTable(referencingTableName);
            TableDefinition referencedTable = getTable(referencedTableName);

            if (referencingTable != null && referencedTable != null) {
                // Special DB2 hack. If a foreign key consists of several
                // columns, all
                // the columns are contained in a single database column
                // (delimited with space)
                // here we split the combined string into individual columns
                String[] referencingColumnNames = referencingColumnName.trim().split("[ ]+");
                String[] referencedColumnNames = referencedColumnName.trim().split("[ ]+");
                if (referencingColumnNames.length == referencedColumnNames.length) {
                    for (int i = 0; i < referencingColumnNames.length; i++) {
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

        SimpleSelectQuery<TablesRecord> q = create().selectQuery(TABLES);
        q.addCompareCondition(Tables.TABSCHEMA, getSchemaName());
        q.addInCondition(Tables.TYPE, "T", "V"); // tables and views
        q.addOrderBy(Tables.TABNAME);
        q.execute();

        for (TablesRecord record : q.getResult()) {
            String name = record.getTabname();
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

        SelectQuery q = create().selectQuery();
        q.addFrom(PROCPARMS);
        q.addJoin(PROCEDURES, Procparms.PROCSCHEMA.equal(Procedures.PROCSCHEMA),
            Procparms.PROCNAME.equal(Procedures.PROCNAME));
        q.addCompareCondition(Procparms.PROCSCHEMA, getSchemaName());
        q.addOrderBy(Procparms.PROCNAME);
        q.addOrderBy(Procparms.ORDINAL);
        q.execute();

        for (Record record : q.getResult()) {
            String name = record.getValue(Procparms.PROCNAME);
            String paramName = record.getValue(Procparms.PARMNAME);
            String dataType = record.getValue(Procparms.TYPENAME);
            int position = record.getValue(Procparms.ORDINAL);
            int length = record.getValue(Procparms.LENGTH);
            short scale = record.getValue(Procparms.SCALE);
            String paramMode = record.getValue(Procparms.PARM_MODE);

            DB2ProcedureDefinition procedure = procedureMap.get(name);
            if (procedure == null) {
                procedure = new DB2ProcedureDefinition(this, null, name);
                procedureMap.put(name, procedure);
            }

            String type = getType(dataType, 0, 0, null);
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

        SelectQuery q = create().selectQuery();
        q.addFrom(FUNCPARMS);
        q.addJoin(FUNCTIONS, Funcparms.FUNCSCHEMA.equal(Functions.FUNCSCHEMA),
            Funcparms.FUNCNAME.equal(Functions.FUNCNAME));
        q.addCompareCondition(Funcparms.FUNCSCHEMA, getSchemaName());
        q.addOrderBy(Funcparms.FUNCNAME);
        q.addOrderBy(Funcparms.ORDINAL);
        q.execute();

        for (Record record : q.getResult()) {
            String name = record.getValue(Funcparms.FUNCNAME);
            String rowType = record.getValue(Funcparms.ROWTYPE);
            String dataType = record.getValue(Funcparms.TYPENAME);
            int position = record.getValue(Funcparms.ORDINAL);
            String paramName = record.getValue(Funcparms.PARMNAME);
            int length = record.getValue(Funcparms.LENGTH);
            short scale = record.getValue(Funcparms.SCALE);

            DB2FunctionDefinition function = functionMap.get(name);
            if (function == null) {
                function = new DB2FunctionDefinition(this, null, name, null);
                functionMap.put(name, function);
            }

            String type = getType(dataType, 0, 0, null);

            if ("C".equals(rowType)) { // result after casting
                function.setReturnValue(type);
            }
            else if ("P".equals(rowType)) { // parameter
                function.addParameter(paramName, position, type, length, scale);
            }
            else { // result before casting
                   // continue
            }
        }
        return new ArrayList<FunctionDefinition>(functionMap.values());
    }

    @Override
    protected List<PackageDefinition> getPackages0() throws SQLException {
        List<PackageDefinition> result = new ArrayList<PackageDefinition>();
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
