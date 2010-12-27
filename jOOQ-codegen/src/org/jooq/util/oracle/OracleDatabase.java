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

import static org.jooq.Comparator.NOT_LIKE;
import static org.jooq.util.oracle.sys.tables.AllConsColumns.ALL_CONS_COLUMNS;
import static org.jooq.util.oracle.sys.tables.AllConstraints.ALL_CONSTRAINTS;
import static org.jooq.util.oracle.sys.tables.AllObjects.ALL_OBJECTS;
import static org.jooq.util.oracle.sys.tables.AllTabComments.ALL_TAB_COMMENTS;
import static org.jooq.util.oracle.sys.tables.AllTabComments.COMMENTS;
import static org.jooq.util.oracle.sys.tables.AllTabComments.OWNER;
import static org.jooq.util.oracle.sys.tables.AllTabComments.TABLE_NAME;
import static org.jooq.util.oracle.sys.tables.AllTypes.ALL_TYPES;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectQuery;
import org.jooq.SimpleSelectQuery;
import org.jooq.Table;
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
import org.jooq.util.oracle.sys.tables.AllConsColumns;
import org.jooq.util.oracle.sys.tables.AllConstraints;
import org.jooq.util.oracle.sys.tables.AllObjects;
import org.jooq.util.oracle.sys.tables.AllTypes;
import org.jooq.util.oracle.sys.tables.records.AllObjectsRecord;
import org.jooq.util.oracle.sys.tables.records.AllTabCommentsRecord;

/**
 * @author Lukas Eder
 */
public class OracleDatabase extends AbstractDatabase {

    @Override
    protected void loadPrimaryKeys(DefaultRelations relations) throws SQLException {
        SelectQuery query = create().selectQuery();
        query.addFrom(ALL_CONS_COLUMNS);
        query.addJoin(ALL_CONSTRAINTS, AllConsColumns.CONSTRAINT_NAME, AllConstraints.CONSTRAINT_NAME);
        query.addCompareCondition(AllConstraints.CONSTRAINT_TYPE, "P");
        query.addCompareCondition(AllConsColumns.OWNER, getSchemaName());

        query.execute();
        for (Record record : query.getResult()) {
            String key = record.getValue(AllConsColumns.CONSTRAINT_NAME);
            String tableName = record.getValue(AllConsColumns.TABLE_NAME);
            String columnName = record.getValue(AllConsColumns.COLUMN_NAME);

            TableDefinition table = getTable(tableName);
            if (table != null) {
                relations.addPrimaryKey(key, table.getColumn(columnName));
            }
        }
    }

    @Override
    protected void loadForeignKeys(DefaultRelations relations) throws SQLException {
//      select cc1.*, cc2.* from all_constraints co
//      join all_cons_columns cc1 on (cc1.constraint_name = co.constraint_name)
//      join all_cons_columns cc2 on (cc2.constraint_name = co.r_constraint_name and cc1.position = cc2.position)
//      where cc1.constraint_name = (
//        select cox.constraint_name
//        from all_cons_columns ccx
//        join all_constraints cox on (ccx.constraint_name = cox.constraint_name)
//          where ccx.owner = 'ODS_TEST'
//          and ccx.table_name = '...'
//          and ccx.column_name = '...'
//          and cox.constraint_type = 'R');

        Table<?> cc1 = ALL_CONS_COLUMNS.as("cc1");
        Table<?> cc2 = ALL_CONS_COLUMNS.as("cc2");

        Field<String> constraint = cc2.getField(AllConsColumns.CONSTRAINT_NAME).as("constraint");
        Field<String> referencingTable = cc1.getField(AllConsColumns.TABLE_NAME).as("referencing_table");
        Field<String> referencingColumn = cc1.getField(AllConsColumns.COLUMN_NAME).as("referencing_column");
        Field<String> referencedTable = cc2.getField(AllConsColumns.TABLE_NAME).as("referenced_table");
        Field<String> referencedColumn = cc2.getField(AllConsColumns.COLUMN_NAME).as("referenced_column");

        SelectQuery inner = create().selectQuery();
        inner.addFrom(ALL_CONS_COLUMNS);
        inner.addJoin(ALL_CONSTRAINTS, AllConsColumns.CONSTRAINT_NAME, AllConstraints.CONSTRAINT_NAME);
        inner.addSelect(AllConstraints.CONSTRAINT_NAME);
        inner.addConditions(
            create().compareCondition(AllConstraints.CONSTRAINT_TYPE, "R"),
            create().compareCondition(AllConsColumns.OWNER, getSchemaName()),
            create().joinCondition(AllConsColumns.TABLE_NAME, cc1.getField(AllConsColumns.TABLE_NAME)),
            create().joinCondition(AllConsColumns.COLUMN_NAME, cc1.getField(AllConsColumns.COLUMN_NAME)));

        SelectQuery query = create().selectQuery();
        query.addFrom(ALL_CONSTRAINTS);
        query.addSelect(constraint, referencingTable, referencedTable, referencingColumn, referencedColumn);
        query.addJoin(cc1,
            create().joinCondition(cc1.getField(AllConsColumns.CONSTRAINT_NAME), AllConstraints.CONSTRAINT_NAME));
        query.addJoin(cc2,
            create().joinCondition(cc2.getField(AllConsColumns.CONSTRAINT_NAME), AllConstraints.R_CONSTRAINT_NAME),
            create().joinCondition(cc2.getField(AllConsColumns.POSITION), cc1.getField(AllConsColumns.POSITION)));
        query.addConditions(
            AllConstraints.OWNER.equal(getSchemaName()),
            cc1.getField(AllConsColumns.OWNER).equal(getSchemaName()),
            cc2.getField(AllConsColumns.OWNER).equal(getSchemaName()),
            cc1.getField(AllConsColumns.CONSTRAINT_NAME).equal(inner));

        query.execute();

        for (Record record : query.getResult()) {
            String key = record.getValue(constraint);
            String referencingTableName = record.getValue(referencingTable);
            String referencingColumnName = record.getValue(referencingColumn);
            String referencedTableName = record.getValue(referencedTable);
            String referencedColumnName = record.getValue(referencedColumn);

            TableDefinition referencingTableDef = getTable(referencingTableName);
            TableDefinition referencedTableDef = getTable(referencedTableName);

            if (referencingTableDef != null && referencedTableDef != null) {
                ColumnDefinition referencingColumnDef = referencingTableDef.getColumn(referencingColumnName);
                ColumnDefinition referencedColumnDef = referencedTableDef.getColumn(referencedColumnName);

                String primaryKey = relations.getPrimaryKeyName(referencedColumnDef);
                relations.addForeignKey(key, primaryKey, referencingColumnDef);
            }
        }
    }

    @Override
    protected List<TableDefinition> getTables0() throws SQLException {
        List<TableDefinition> result = new ArrayList<TableDefinition>();

        SimpleSelectQuery<AllTabCommentsRecord> q = create().selectQuery(ALL_TAB_COMMENTS);
        q.addConditions(create().compareCondition(OWNER, getSchemaName()),
            create().compareCondition(TABLE_NAME, "%$%", NOT_LIKE)); 
        q.addOrderBy(TABLE_NAME);
        q.execute();

        for (Record record : q.getResult()) {
            String name = record.getValue(TABLE_NAME);
            String comment = record.getValue(COMMENTS);

            OracleTableDefinition table = new OracleTableDefinition(this, name, comment);
            result.add(table);
        }

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
        
        for (String name : create().selectDistinct(AllTypes.TYPE_NAME)
            .from(ALL_TYPES)
            .where(AllTypes.OWNER.equal(getSchemaName()))
            .orderBy(AllTypes.TYPE_NAME).fetch(AllTypes.TYPE_NAME)) {

            result.add(new OracleUDTDefinition(this, name, null));
        }
        
        return result;
    }

    @Override
    protected List<ProcedureDefinition> getProcedures0() throws SQLException {
        List<ProcedureDefinition> result = new ArrayList<ProcedureDefinition>();
        
        for (AllObjectsRecord record : executeProcedureQuery("PROCEDURE")) {
            String objectName = record.getObjectName();
            BigDecimal objectId = record.getObjectId();
            result.add(new OracleProcedureDefinition(this, null, objectName, "", objectId, null));
        }
        
        return result;
    }

    @Override
    protected List<FunctionDefinition> getFunctions0() throws SQLException {
        List<FunctionDefinition> result = new ArrayList<FunctionDefinition>();
        
        for (AllObjectsRecord record : executeProcedureQuery("FUNCTION")) {
            String objectName = record.getObjectName();
            BigDecimal objectId = record.getObjectId();
            result.add(new OracleFunctionDefinition(this, null, objectName, "", objectId, null));
        }
        
        return result;
    }

    private Result<AllObjectsRecord> executeProcedureQuery(String type) throws SQLException {
        return create().selectFrom(ALL_OBJECTS)
                .where(AllObjects.OWNER.equal(getSchemaName())
                .and(AllObjects.OBJECT_TYPE.equal(type)))
                .fetch();
    }

    @Override
    protected List<PackageDefinition> getPackages0() throws SQLException {
        List<PackageDefinition> result = new ArrayList<PackageDefinition>();
        
        Result<AllObjectsRecord> records = executeProcedureQuery("PACKAGE");
        for (AllObjectsRecord record : records) {
            result.add(new OraclePackageDefinition(this, record.getObjectName(), ""));
        }
        
        return result;
    }

    @Override
    public Factory create() {
        return new Factory(getConnection(), SQLDialect.ORACLE);
    }
}
