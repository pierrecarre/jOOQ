/**
 * Copyright (c) 2009-2011, Lukas Eder, lukas.eder@gmail.com
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

import static org.jooq.util.db2.syscat.tables.Datatypes.DATATYPES;
import static org.jooq.util.db2.syscat.tables.Funcparms.FUNCPARMS;
import static org.jooq.util.db2.syscat.tables.Functions.FUNCTIONS;
import static org.jooq.util.db2.syscat.tables.Keycoluse.KEYCOLUSE;
import static org.jooq.util.db2.syscat.tables.References.REFERENCES;
import static org.jooq.util.db2.syscat.tables.Sequences.SEQUENCES;
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
import org.jooq.util.ArrayDefinition;
import org.jooq.util.ColumnDefinition;
import org.jooq.util.DefaultRelations;
import org.jooq.util.DefaultSequenceDefinition;
import org.jooq.util.EnumDefinition;
import org.jooq.util.FunctionDefinition;
import org.jooq.util.PackageDefinition;
import org.jooq.util.ProcedureDefinition;
import org.jooq.util.SequenceDefinition;
import org.jooq.util.TableDefinition;
import org.jooq.util.UDTDefinition;
import org.jooq.util.db2.syscat.tables.Datatypes;
import org.jooq.util.db2.syscat.tables.Funcparms;
import org.jooq.util.db2.syscat.tables.Functions;
import org.jooq.util.db2.syscat.tables.Keycoluse;
import org.jooq.util.db2.syscat.tables.Procedures;
import org.jooq.util.db2.syscat.tables.References;
import org.jooq.util.db2.syscat.tables.Sequences;
import org.jooq.util.db2.syscat.tables.Tabconst;
import org.jooq.util.db2.syscat.tables.Tables;
import org.jooq.util.db2.syscat.tables.records.ReferencesRecord;

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
        q.addConditions(Keycoluse.TABSCHEMA.equal(getSchemaName()));
        q.addConditions(Tabconst.TYPE.equal("P"));
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
        q.addConditions(References.TABSCHEMA.equal(getSchemaName()));
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
    protected List<SequenceDefinition> getSequences0() throws SQLException {
        List<SequenceDefinition> result = new ArrayList<SequenceDefinition>();

        for (String name : create().select(Sequences.SEQNAME)
            .from(SEQUENCES)
            .where(Sequences.SEQSCHEMA.equal(getSchemaName()))
            .orderBy(Sequences.SEQNAME)
            .fetch(Sequences.SEQNAME)) {

            result.add(new DefaultSequenceDefinition(this, name));
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<TableDefinition> getTables0() throws SQLException {
        List<TableDefinition> result = new ArrayList<TableDefinition>();

        SelectQuery q = create().selectQuery();
        q.addFrom(TABLES);
        q.addSelect(Tables.TABNAME);
        q.addConditions(Tables.TABSCHEMA.equal(getSchemaName()));
        q.addConditions(Tables.TYPE.in("T", "V")); // tables and views
        q.addOrderBy(Tables.TABNAME);
        q.execute();

        for (Record record : q.getResult()) {
            String name = record.getValue(Tables.TABNAME);
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
        List<ProcedureDefinition> result = new ArrayList<ProcedureDefinition>();

        for (Record record : create().select(Procedures.PROCNAME)
            .from(Procedures.PROCEDURES)
            .where(Procedures.PROCSCHEMA.equal(getSchemaName()))
            .orderBy(Procedures.PROCNAME)
            .fetch()) {

            String name = record.getValue(Procedures.PROCNAME);
            result.add(new DB2ProcedureDefinition(this, null, name));
        }

        return result;
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
        q.addConditions(Funcparms.FUNCSCHEMA.equal(getSchemaName()));
        q.addConditions(Functions.ORIGIN.equal("Q"));
        q.addOrderBy(Funcparms.FUNCNAME);
        q.addOrderBy(Funcparms.ORDINAL);
        q.execute();

        for (Record record : q.getResult()) {
            String name = record.getValue(Funcparms.FUNCNAME);
            String rowType = record.getValue(Funcparms.ROWTYPE);
            String dataType = record.getValue(Funcparms.TYPENAME);
            int position = record.getValue(Funcparms.ORDINAL);
            String paramName = record.getValue(Funcparms.PARMNAME);

            DB2FunctionDefinition function = functionMap.get(name);
            if (function == null) {
                function = new DB2FunctionDefinition(this, null, name, null);
                functionMap.put(name, function);
            }

            if ("C".equals(rowType)) { // result after casting
                function.setReturnValue(dataType);
            }
            else if ("P".equals(rowType)) { // parameter
                function.addParameter(paramName, position, dataType);
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

        for (String name : create().selectDistinct(Datatypes.TYPENAME)
            .from(DATATYPES)
            .where(Datatypes.TYPESCHEMA.equal(getSchemaName()))
            .orderBy(Datatypes.TYPENAME).fetch(Datatypes.TYPENAME)) {

            result.add(new DB2UDTDefinition(this, name, null));
        }

        return result;
    }

    @Override
    protected List<ArrayDefinition> getArrays0() throws SQLException {
        List<ArrayDefinition> result = new ArrayList<ArrayDefinition>();
        return result;
    }
}
