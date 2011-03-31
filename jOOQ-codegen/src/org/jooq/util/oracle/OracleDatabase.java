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

package org.jooq.util.oracle;

import static org.jooq.util.oracle.sys.tables.AllCollTypes.ALL_COLL_TYPES;
import static org.jooq.util.oracle.sys.tables.AllConsColumns.ALL_CONS_COLUMNS;
import static org.jooq.util.oracle.sys.tables.AllConstraints.ALL_CONSTRAINTS;
import static org.jooq.util.oracle.sys.tables.AllObjects.ALL_OBJECTS;
import static org.jooq.util.oracle.sys.tables.AllSequences.ALL_SEQUENCES;
import static org.jooq.util.oracle.sys.tables.AllSequences.SEQUENCE_NAME;
import static org.jooq.util.oracle.sys.tables.AllSequences.SEQUENCE_OWNER;
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
import org.jooq.SelectQuery;
import org.jooq.Table;
import org.jooq.impl.Factory;
import org.jooq.util.AbstractDatabase;
import org.jooq.util.ArrayDefinition;
import org.jooq.util.ColumnDefinition;
import org.jooq.util.DefaultArrayDefinition;
import org.jooq.util.DefaultDataTypeDefinition;
import org.jooq.util.DefaultRelations;
import org.jooq.util.DefaultSequenceDefinition;
import org.jooq.util.EnumDefinition;
import org.jooq.util.FunctionDefinition;
import org.jooq.util.PackageDefinition;
import org.jooq.util.ProcedureDefinition;
import org.jooq.util.SequenceDefinition;
import org.jooq.util.TableDefinition;
import org.jooq.util.UDTDefinition;
import org.jooq.util.oracle.sys.SysFactory;
import org.jooq.util.oracle.sys.tables.AllCollTypes;
import org.jooq.util.oracle.sys.tables.AllConsColumns;
import org.jooq.util.oracle.sys.tables.AllConstraints;
import org.jooq.util.oracle.sys.tables.AllObjects;
import org.jooq.util.oracle.sys.tables.AllTypes;

/**
 * @author Lukas Eder
 */
public class OracleDatabase extends AbstractDatabase {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadPrimaryKeys(DefaultRelations relations) throws SQLException {
        for (Record record : fetchKeys("P")) {
            String key = record.getValue(AllConsColumns.CONSTRAINT_NAME);
            String tableName = record.getValue(AllConsColumns.TABLE_NAME);
            String columnName = record.getValue(AllConsColumns.COLUMN_NAME);

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
    protected void loadUniqueKeys(DefaultRelations relations) throws SQLException {
        for (Record record : fetchKeys("U")) {
            String key = record.getValue(AllConsColumns.CONSTRAINT_NAME);
            String tableName = record.getValue(AllConsColumns.TABLE_NAME);
            String columnName = record.getValue(AllConsColumns.COLUMN_NAME);

            TableDefinition table = getTable(tableName);
            if (table != null) {
                relations.addUniqueKey(key, table.getColumn(columnName));
            }
        }
    }

    private List<Record> fetchKeys(String constraintType) throws SQLException {
        return create().select(
                AllConsColumns.CONSTRAINT_NAME,
                AllConsColumns.TABLE_NAME,
                AllConsColumns.COLUMN_NAME)
            .from(ALL_CONS_COLUMNS)
            .join(ALL_CONSTRAINTS)
            .on(AllConsColumns.CONSTRAINT_NAME.equal(AllConstraints.CONSTRAINT_NAME))
            .where(AllConstraints.CONSTRAINT_TYPE.equal(constraintType))
            .and(AllConstraints.CONSTRAINT_NAME.notLike("BIN$%"))
            .and(AllConsColumns.OWNER.equal(getSchemaName()))
            .orderBy(
                AllConstraints.CONSTRAINT_NAME,
                AllConsColumns.POSITION)
            .fetch()
            .getRecords();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadForeignKeys(DefaultRelations relations) throws SQLException {
        Table<?> cc1 = ALL_CONS_COLUMNS.as("cc1");
        Table<?> cc2 = ALL_CONS_COLUMNS.as("cc2");

        Field<String> foreignKey = cc1.getField(AllConsColumns.CONSTRAINT_NAME).as("fk_name");
        Field<String> foreignKeyTable = cc1.getField(AllConsColumns.TABLE_NAME).as("fk_table");
        Field<String> foreignKeyColumn = cc1.getField(AllConsColumns.COLUMN_NAME).as("fk_column");
        Field<String> uniqueKey = cc2.getField(AllConsColumns.CONSTRAINT_NAME).as("uk_name");
        Field<String> uniqueKeyTable = cc2.getField(AllConsColumns.TABLE_NAME).as("uk_table");

        SelectQuery inner = create().selectQuery();
        inner.addFrom(ALL_CONS_COLUMNS);
        inner.addJoin(ALL_CONSTRAINTS, AllConsColumns.CONSTRAINT_NAME.equal(AllConstraints.CONSTRAINT_NAME));
        inner.addSelect(AllConstraints.CONSTRAINT_NAME);
        inner.addConditions(
            AllConstraints.CONSTRAINT_TYPE.equal("R"),
            AllConsColumns.OWNER.equal(getSchemaName()),
            AllConsColumns.TABLE_NAME.equal(cc1.getField(AllConsColumns.TABLE_NAME)),
            AllConsColumns.COLUMN_NAME.equal(cc1.getField(AllConsColumns.COLUMN_NAME)));

        SelectQuery query = create().selectQuery();
        query.addFrom(ALL_CONSTRAINTS);
        query.addSelect(foreignKey, uniqueKey, foreignKeyTable, uniqueKeyTable, foreignKeyColumn);
        query.addJoin(cc1,
            cc1.getField(AllConsColumns.CONSTRAINT_NAME).equal(AllConstraints.CONSTRAINT_NAME));
        query.addJoin(cc2,
            cc2.getField(AllConsColumns.CONSTRAINT_NAME).equal(AllConstraints.R_CONSTRAINT_NAME),
            cc2.getField(AllConsColumns.POSITION).equal(cc1.getField(AllConsColumns.POSITION)));
        query.addConditions(
            AllConstraints.OWNER.equal(getSchemaName()),
            cc1.getField(AllConsColumns.OWNER).equal(getSchemaName()),
            cc2.getField(AllConsColumns.OWNER).equal(getSchemaName()),
            cc1.getField(AllConsColumns.CONSTRAINT_NAME).equal(inner));
        query.addOrderBy(foreignKey);
        query.addOrderBy(cc2.getField(AllConsColumns.POSITION));

        query.execute();

        for (Record record : query.getResult()) {
            String foreignKeyName = record.getValue(foreignKey);
            String foreignKeyTableName = record.getValue(foreignKeyTable);
            String foreignKeyColumnName = record.getValue(foreignKeyColumn);
            String uniqueKeyName = record.getValue(uniqueKey);

            TableDefinition referencingTable = getTable(foreignKeyTableName);

            if (referencingTable != null) {
                ColumnDefinition column = referencingTable.getColumn(foreignKeyColumnName);
                relations.addForeignKey(foreignKeyName, uniqueKeyName, column);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<SequenceDefinition> getSequences0() throws SQLException {
        List<SequenceDefinition> result = new ArrayList<SequenceDefinition>();

        for (String name : create().select(SEQUENCE_NAME)
            .from(ALL_SEQUENCES)
            .where(SEQUENCE_OWNER.equal(getSchemaName()))
            .orderBy(SEQUENCE_NAME)
            .fetch(SEQUENCE_NAME)) {

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

        for (Record record : create().select(TABLE_NAME, COMMENTS)
            .from(ALL_TAB_COMMENTS)
            .where(OWNER.equal(getSchemaName()))
            .and(TABLE_NAME.notLike("%$%"))
            .orderBy(TABLE_NAME)
            .fetch()) {

            String name = record.getValue(TABLE_NAME);
            String comment = record.getValue(COMMENTS);

            OracleTableDefinition table = new OracleTableDefinition(this, name, comment);
            result.add(table);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<EnumDefinition> getEnums0() throws SQLException {
        List<EnumDefinition> result = new ArrayList<EnumDefinition>();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<UDTDefinition> getUDTs0() throws SQLException {
        List<UDTDefinition> result = new ArrayList<UDTDefinition>();

        for (String name : create().selectDistinct(AllTypes.TYPE_NAME)
            .from(ALL_TYPES)
            .where(AllTypes.OWNER.equal(getSchemaName()))
            .and(AllTypes.TYPECODE.equal("OBJECT"))
            .orderBy(AllTypes.TYPE_NAME)
            .fetch(AllTypes.TYPE_NAME)) {

            result.add(new OracleUDTDefinition(this, name, null));
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<ArrayDefinition> getArrays0() throws SQLException {
        List<ArrayDefinition> arrays = new ArrayList<ArrayDefinition>();

        for (Record record : create().select(
                AllCollTypes.TYPE_NAME,
                AllCollTypes.ELEM_TYPE_NAME,
                AllCollTypes.PRECISION,
                AllCollTypes.SCALE)
            .from(ALL_COLL_TYPES)
            .where(AllCollTypes.OWNER.equal(getSchemaName()))
            .and(AllCollTypes.COLL_TYPE.equal("VARYING ARRAY"))
            .orderBy(AllCollTypes.TYPE_NAME)
            .fetch()) {

            String name = record.getValue(AllCollTypes.TYPE_NAME);
            String dataType = record.getValue(AllCollTypes.ELEM_TYPE_NAME);
            int precision = record.getValue(AllCollTypes.PRECISION, BigDecimal.ZERO).intValue();
            int scale = record.getValue(AllCollTypes.SCALE, BigDecimal.ZERO).intValue();

            DefaultDataTypeDefinition type = new DefaultDataTypeDefinition(this, dataType, precision, scale);
            DefaultArrayDefinition array = new DefaultArrayDefinition(this, name, type);

            arrays.add(array);
        }

        return arrays;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<ProcedureDefinition> getProcedures0() throws SQLException {
        List<ProcedureDefinition> result = new ArrayList<ProcedureDefinition>();

        for (Record record : executeProcedureQuery("PROCEDURE")) {
            String objectName = record.getValue(AllObjects.OBJECT_NAME);
            BigDecimal objectId = record.getValue(AllObjects.OBJECT_ID);
            result.add(new OracleProcedureDefinition(this, null, objectName, "", objectId, null));
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<FunctionDefinition> getFunctions0() throws SQLException {
        List<FunctionDefinition> result = new ArrayList<FunctionDefinition>();

        for (Record record : executeProcedureQuery("FUNCTION")) {
            String objectName = record.getValue(AllObjects.OBJECT_NAME);
            BigDecimal objectId = record.getValue(AllObjects.OBJECT_ID);
            result.add(new OracleFunctionDefinition(this, null, objectName, "", objectId, null));
        }

        return result;
    }

    private Result<Record> executeProcedureQuery(String type) throws SQLException {
        return create().select(AllObjects.OBJECT_NAME, AllObjects.OBJECT_ID)
                .from(ALL_OBJECTS)
                .where(AllObjects.OWNER.equal(getSchemaName())
                .and(AllObjects.OBJECT_TYPE.equal(type)))
                .orderBy(AllObjects.OBJECT_NAME, AllObjects.OBJECT_ID)
                .fetch();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<PackageDefinition> getPackages0() throws SQLException {
        List<PackageDefinition> result = new ArrayList<PackageDefinition>();

        for (Record record : executeProcedureQuery("PACKAGE")) {
            String name = record.getValue(AllObjects.OBJECT_NAME);
            result.add(new OraclePackageDefinition(this, name, ""));
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Factory create() {
        return new SysFactory(getConnection());
    }
}
