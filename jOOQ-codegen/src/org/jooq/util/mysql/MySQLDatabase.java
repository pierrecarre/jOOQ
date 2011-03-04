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

package org.jooq.util.mysql;

import static org.jooq.util.mysql.information_schema.tables.Columns.COLUMNS;
import static org.jooq.util.mysql.information_schema.tables.KeyColumnUsage.KEY_COLUMN_USAGE;
import static org.jooq.util.mysql.information_schema.tables.Tables.TABLES;
import static org.jooq.util.mysql.information_schema.tables.Tables.TABLE_NAME;
import static org.jooq.util.mysql.information_schema.tables.Tables.TABLE_SCHEMA;
import static org.jooq.util.mysql.mysql.tables.Proc.DB;
import static org.jooq.util.mysql.mysql.tables.Proc.PROC;
import static org.jooq.util.mysql.mysql.tables.Proc.TYPE;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SimpleSelectQuery;
import org.jooq.impl.Factory;
import org.jooq.util.AbstractDatabase;
import org.jooq.util.ArrayDefinition;
import org.jooq.util.ColumnDefinition;
import org.jooq.util.DefaultEnumDefinition;
import org.jooq.util.DefaultRelations;
import org.jooq.util.EnumDefinition;
import org.jooq.util.FunctionDefinition;
import org.jooq.util.PackageDefinition;
import org.jooq.util.ProcedureDefinition;
import org.jooq.util.SequenceDefinition;
import org.jooq.util.TableDefinition;
import org.jooq.util.UDTDefinition;
import org.jooq.util.mysql.information_schema.tables.Columns;
import org.jooq.util.mysql.information_schema.tables.KeyColumnUsage;
import org.jooq.util.mysql.information_schema.tables.Tables;
import org.jooq.util.mysql.information_schema.tables.records.KeyColumnUsageRecord;
import org.jooq.util.mysql.mysql.enums.ProcType;
import org.jooq.util.mysql.mysql.tables.Proc;

/**
 * @author Lukas Eder
 */
public class MySQLDatabase extends AbstractDatabase {

    @Override
    protected void loadPrimaryKeys(DefaultRelations relations) throws SQLException {
        SimpleSelectQuery<KeyColumnUsageRecord> q = create().selectQuery(KEY_COLUMN_USAGE);
        q.addConditions(KeyColumnUsage.CONSTRAINT_NAME.equal("PRIMARY"));
        q.addConditions(KeyColumnUsage.TABLE_SCHEMA.equal(getSchemaName()));
        q.execute();

        for (KeyColumnUsageRecord record : q.getResult()) {
            String key = record.getConstraintName();
            String tableName = record.getTableName();
            String columnName = record.getColumnName();

            key = key + "_" + tableName;
            TableDefinition table = getTable(tableName);

            if (table != null) {
                relations.addPrimaryKey(key, table.getColumn(columnName));
            }
        }
    }

    @Override
    protected void loadForeignKeys(DefaultRelations relations) throws SQLException {
        SimpleSelectQuery<KeyColumnUsageRecord> q = create().selectQuery(KEY_COLUMN_USAGE);
        q.addConditions(KeyColumnUsage.CONSTRAINT_NAME.notEqual("PRIMARY"));
        q.addConditions(KeyColumnUsage.TABLE_SCHEMA.equal(getSchemaName()));
        q.execute();

        for (KeyColumnUsageRecord record : q.getResult()) {
            String key = record.getConstraintName();
            String referencingTableName = record.getTableName();
            String referencingColumnName = record.getColumnName();
            String referencedTableName = record.getReferencedTableName();
            String referencedColumnName = record.getReferencedColumnName();

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

    @Override
    protected List<SequenceDefinition> getSequences0() throws SQLException {
        List<SequenceDefinition> result = new ArrayList<SequenceDefinition>();
        return result;
    }

    @Override
    protected List<TableDefinition> getTables0() throws SQLException {
        List<TableDefinition> result = new ArrayList<TableDefinition>();

        for (Record record : create().select(
                Tables.TABLE_NAME,
                Tables.TABLE_COMMENT)
            .from(TABLES)
            .where(TABLE_SCHEMA.equal(getSchemaName()))
            .orderBy(TABLE_NAME)
            .fetch()) {

            String name = record.getValue(Tables.TABLE_NAME);
            String comment = record.getValue(Tables.TABLE_COMMENT);

            MySQLTableDefinition table = new MySQLTableDefinition(this, name, comment);
            result.add(table);
        }

        return result;
    }

    @Override
    protected List<EnumDefinition> getEnums0() throws SQLException {
        List<EnumDefinition> result = new ArrayList<EnumDefinition>();

        Result<Record> records = create()
            .select(
                Columns.COLUMN_COMMENT,
                Columns.TABLE_NAME,
                Columns.COLUMN_NAME,
                Columns.COLUMN_TYPE)
            .from(COLUMNS)
            .where(
                Columns.COLUMN_TYPE.like("enum(%)").and(
                Columns.TABLE_SCHEMA.equal(getSchemaName())))
            .orderBy(
                Columns.TABLE_NAME.ascending(),
                Columns.COLUMN_NAME.ascending())
            .fetch();

        for (Record record : records) {
            String comment = record.getValue(Columns.COLUMN_COMMENT);
            String table = record.getValue(Columns.TABLE_NAME);
            String column = record.getValue(Columns.COLUMN_NAME);
            String name = table + "_" + column;
            String columnType = record.getValue(Columns.COLUMN_TYPE);

            DefaultEnumDefinition definition = new DefaultEnumDefinition(this, name, comment);
            for (String string : columnType.replaceAll("enum\\(|\\)", "").split(",")) {
                definition.addLiteral(string.trim().replaceAll("'", ""));
            }

            result.add(definition);
        }

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

    @Override
    protected List<ProcedureDefinition> getProcedures0() throws SQLException {
        List<ProcedureDefinition> result = new ArrayList<ProcedureDefinition>();

        for (Record record : executeProcedureQuery(ProcType.PROCEDURE)) {
            String name = record.getValue(Proc.NAME);
            String comment = record.getValue(Proc.COMMENT);
            String params = new String(record.getValue(Proc.PARAM_LIST));

            MySQLProcedureDefinition procedure = new MySQLProcedureDefinition(this, name, comment, params);
            result.add(procedure);
        }

        return result;
    }

    @Override
    protected List<FunctionDefinition> getFunctions0() throws SQLException {
        List<FunctionDefinition> result = new ArrayList<FunctionDefinition>();

        for (Record record : executeProcedureQuery(ProcType.FUNCTION)) {
            String name = record.getValue(Proc.NAME);
            String comment = record.getValue(Proc.COMMENT);
            String params = new String(record.getValue(Proc.PARAM_LIST));
            String returnValue = new String(record.getValue(Proc.RETURNS));

            MySQLFunctionDefinition function = new MySQLFunctionDefinition(this, name, comment, params, returnValue);
            result.add(function);
        }

        return result;
    }

    private Result<Record> executeProcedureQuery(ProcType type) throws SQLException {
        return create().select(
                Proc.NAME,
                Proc.COMMENT,
                Proc.PARAM_LIST,
                Proc.RETURNS)
            .from(PROC)
            .where(DB.equal(getSchemaName()))
            .and(TYPE.equal(type))
            .fetch();
    }

    @Override
    protected List<PackageDefinition> getPackages0() throws SQLException {
        List<PackageDefinition> result = new ArrayList<PackageDefinition>();
        return result;
    }

    @Override
    public Factory create() {
        return new Factory(getConnection(), SQLDialect.MYSQL);
    }
}
