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
package org.jooq.util.h2;

import static org.jooq.util.h2.information_schema.tables.Constraints.CONSTRAINTS;
import static org.jooq.util.h2.information_schema.tables.FunctionAliases.FUNCTION_ALIASES;
import static org.jooq.util.h2.information_schema.tables.Sequences.SEQUENCES;
import static org.jooq.util.h2.information_schema.tables.Tables.TABLES;
import static org.jooq.util.h2.information_schema.tables.TypeInfo.TYPE_INFO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.SimpleSelectQuery;
import org.jooq.impl.Factory;
import org.jooq.impl.JooqLogger;
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
import org.jooq.util.h2.information_schema.tables.Constraints;
import org.jooq.util.h2.information_schema.tables.CrossReferences;
import org.jooq.util.h2.information_schema.tables.FunctionAliases;
import org.jooq.util.h2.information_schema.tables.Sequences;
import org.jooq.util.h2.information_schema.tables.Tables;
import org.jooq.util.h2.information_schema.tables.TypeInfo;
import org.jooq.util.h2.information_schema.tables.records.ConstraintsRecord;
import org.jooq.util.h2.information_schema.tables.records.CrossReferencesRecord;

/**
 * H2 implementation of {@link AbstractDatabase}
 * <p>
 * <b>NB! Special notes regarding "aliases":</b>
 * <p>
 * <ul>
 *   <li>aliases which return java.sql.ResultSet are not supported (they are ignored)
 *   <li>aliases which does not return data are mapped to Procedures
 *   <li>all other aliases are mapped to Functions
 * </ul>
 *
 * @author Espen Stromsnes
 */
public class H2Database extends AbstractDatabase {

    private static final JooqLogger log = JooqLogger.getLogger(H2Database.class);
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
        SimpleSelectQuery<ConstraintsRecord> q = create().selectQuery(CONSTRAINTS);
        q.addConditions(Constraints.TABLE_SCHEMA.equal(getSchemaName()));
        q.addConditions(Constraints.CONSTRAINT_TYPE.equal("PRIMARY KEY"));
        q.execute();

        for (ConstraintsRecord record : q.getResult()) {
            String key = record.getConstraintName();
            String tableName = record.getTableName();
            String columnList = record.getColumnList();

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
        SimpleSelectQuery<CrossReferencesRecord> q = create().selectQuery(CrossReferences.CROSS_REFERENCES);
        q.addConditions(CrossReferences.PKTABLE_SCHEMA.equal(getSchemaName()));
        q.addOrderBy(CrossReferences.FK_NAME);
        q.addOrderBy(CrossReferences.ORDINAL_POSITION);
        q.execute();

        for (CrossReferencesRecord record : q.getResult()) {
            String key = record.getFkName();
            String referencingTableName = record.getFktableName();
            String referencingColumnName = record.getFkcolumnName();
            String referencedTableName = record.getPktableName();
            String referencedColumnName = record.getPkcolumnName();

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
    protected List<SequenceDefinition> getSequences0() throws SQLException {
        List<SequenceDefinition> result = new ArrayList<SequenceDefinition>();

        for (String name : create().select(Sequences.SEQUENCE_NAME)
            .from(SEQUENCES)
            .where(Sequences.SEQUENCE_SCHEMA.equal(getSchemaName()))
            .orderBy(Sequences.SEQUENCE_NAME)
            .fetch(Sequences.SEQUENCE_NAME)) {

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

        for (Record record : create().select(
                Tables.TABLE_NAME,
                Tables.REMARKS)
            .from(TABLES)
            .where(Tables.TABLE_SCHEMA.equal(getSchemaName()))
            .orderBy(Tables.ID)
            .fetch()) {

            String name = record.getValue(Tables.TABLE_NAME);
            String comment = record.getValue(Tables.REMARKS);

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

        Select<?> query = create().select(
                FunctionAliases.ALIAS_NAME,
                FunctionAliases.REMARKS,
                FunctionAliases.DATA_TYPE,
                FunctionAliases.RETURNS_RESULT)
            .from(FUNCTION_ALIASES)
            .where(FunctionAliases.ALIAS_SCHEMA.equal(getSchemaName()))
            .and(FunctionAliases.RETURNS_RESULT.equal((short) 1))
            .orderBy(FunctionAliases.ALIAS_NAME);

        for (Record record : query.fetch()) {
            String name = record.getValue(FunctionAliases.ALIAS_NAME);
            String comment = record.getValue(FunctionAliases.REMARKS);

            result.add(new H2ProcedureDefinition(this, name, comment));
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<FunctionDefinition> getFunctions0() throws SQLException {
        List<FunctionDefinition> result = new ArrayList<FunctionDefinition>();

        Select<?> query = create().select(
                FunctionAliases.ALIAS_NAME,
                FunctionAliases.REMARKS,
                FunctionAliases.DATA_TYPE,
                FunctionAliases.RETURNS_RESULT,
                TypeInfo.TYPE_NAME)
            .from(FUNCTION_ALIASES)
            .join(TYPE_INFO)
            .on(FunctionAliases.DATA_TYPE.equal(TypeInfo.DATA_TYPE))
            .where(FunctionAliases.ALIAS_SCHEMA.equal(getSchemaName()))
            .and(FunctionAliases.RETURNS_RESULT.equal((short) 2))
            .orderBy(FunctionAliases.ALIAS_NAME);

        for (Record record : query.fetch()) {
            String name = record.getValue(FunctionAliases.ALIAS_NAME);
            String comment = record.getValue(FunctionAliases.REMARKS);
            int dataType = record.getValue(FunctionAliases.DATA_TYPE);
            String typeName = record.getValue(TypeInfo.TYPE_NAME);

            if (dataType == 0) {
                log.warn("Unsupported function return type for alias, name : " + name);
                continue;
            }

            H2FunctionDefinition function = new H2FunctionDefinition(this, name, comment);
            function.setReturnValue(typeName);
            result.add(function);
        }

        return result;
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

    @Override
    protected List<ArrayDefinition> getArrays0() throws SQLException {
        List<ArrayDefinition> result = new ArrayList<ArrayDefinition>();
        return result;
    }
}
