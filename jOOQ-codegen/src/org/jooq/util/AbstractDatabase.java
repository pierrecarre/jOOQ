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

package org.jooq.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jooq.SQLDialect;

/**
 * A base implementation for all types of databases.
 *
 * @author Lukas Eder
 */
public abstract class AbstractDatabase implements Database {

    private Connection                      connection;
    private String                          schema;
    private String[]                        excludes;
    private String[]                        includes;
    private String[]                        masterDataTableNames;
    private boolean                         generateRelations = false;
    private String                          targetPackageName;
    private String                          targetDirectory;
    private Properties                      properties;

    private List<TableDefinition>           tables;
    private List<MasterDataTableDefinition> masterDataTables;
    private List<EnumDefinition>            enums;
    private List<UDTDefinition>             udts;
    private List<ProcedureDefinition>       procedures;
    private List<FunctionDefinition>        functions;
    private Relations                       relations;

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String getProperty(String property) {
        return properties.getProperty(property);
    }

    @Override
    public final SQLDialect getDialect() {
        return create().getDialect();
    }

    @Override
    public final void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public final Connection getConnection() {
        return connection;
    }

    @Override
    public final void setSchemaName(String schema) {
        this.schema = schema;
    }

    @Override
    public final String getSchemaName() {
        return schema;
    }

    @Override
    public final SchemaDefinition getSchema() throws SQLException {
        return new SchemaDefinition(this, getSchemaName(), null);
    }

    @Override
    public final void setExcludes(String[] excludes) {
        this.excludes = excludes;
    }

    @Override
    public final String[] getExcludes() {
        return excludes;
    }

    @Override
    public final void setIncludes(String[] includes) {
        this.includes = includes;
    }

    @Override
    public final String[] getIncludes() {
        return includes;
    }

    @Override
    public final void setMasterDataTableNames(String[] masterDataTableNames) {
        this.masterDataTableNames = masterDataTableNames;
    }

    @Override
    public final String[] getMasterDataTableNames() {
        return masterDataTableNames;
    }

    @Override
    public boolean generateRelations() {
        return generateRelations;
    }

    @Override
    public void setGenerateRelations(boolean generateRelations) {
        this.generateRelations = generateRelations;
    }

    @Override
    public void setTargetPackage(String packageName) {
        this.targetPackageName = packageName;
    }

    @Override
    public void setTargetDirectory(String directory) {
        this.targetDirectory = directory;
    }

    @Override
    public String getTargetPackage() {
        return targetPackageName;
    }

    @Override
    public String getTargetDirectory() {
        return targetDirectory;
    }

    @Override
    public final List<TableDefinition> getTables() throws SQLException {
        if (tables == null) {
            tables = filterMasterDataTables(filterExcludeInclude(getTables0()), false);
        }

        return tables;
    }

    @Override
    public final TableDefinition getTable(String name) throws SQLException {
        for (TableDefinition table : getTables()) {
            if (table.getName().equals(name)) {
                return table;
            }
        }

        for (TableDefinition table : getMasterDataTables()) {
            if (table.getName().equals(name)) {
                return table;
            }
        }

        return null;
    }

    @Override
    public final List<MasterDataTableDefinition> getMasterDataTables() throws SQLException {
        if (masterDataTables == null) {
            masterDataTables = filterMasterDataTables(filterExcludeInclude(getTables0()), true);
        }

        return masterDataTables;
    }

    @Override
    public final MasterDataTableDefinition getMasterDataTable(String name) throws SQLException {
        for (MasterDataTableDefinition table : getMasterDataTables()) {
            if (table.getName().equals(name)) {
                return table;
            }
        }

        return null;
    }

    @Override
    public final List<EnumDefinition> getEnums() throws SQLException {
        if (enums == null) {
            enums = getEnums0();
        }

        return enums;
    }

    @Override
    public final EnumDefinition getEnum(String name) throws SQLException {
        for (EnumDefinition e : getEnums()) {
            if (e.getName().equals(name)) {
                e.setReferenced(true);

                return e;
            }
        }

        return null;
    }

    @Override
    public final List<UDTDefinition> getUDTs() throws SQLException {
        if (udts == null) {
            udts = getUDTs0();
        }

        return udts;
    }

    @Override
    public final UDTDefinition getUDT(String name) throws SQLException {
        for (UDTDefinition e : getUDTs()) {
            if (e.getName().equals(name)) {
                e.setReferenced(true);

                return e;
            }
        }

        return null;
    }

    @Override
    public Relations getRelations() throws SQLException {
        if (relations == null) {
            relations = getRelations0();
        }

        return relations;
    }

    @Override
    public final List<ProcedureDefinition> getProcedures() throws SQLException {
        if (procedures == null) {
            procedures = filterExcludeInclude(getProcedures0());
        }

        return procedures;
    }

    @Override
    public final List<FunctionDefinition> getFunctions() throws SQLException {
        if (functions == null) {
            functions = filterExcludeInclude(getFunctions0());
        }

        return functions;
    }

    private final <T extends Definition> List<T> filterExcludeInclude(List<T> definitions) {
        List<T> result = new ArrayList<T>();

        definitionsLoop: for (T definition : definitions) {
            for (String exclude : excludes) {
                if (exclude != null && definition.getName().matches(exclude.trim())) {
                    continue definitionsLoop;
                }
            }

            for (String include : includes) {
                if (include != null && definition.getName().matches(include.trim())) {
                    result.add(definition);
                    continue definitionsLoop;
                }
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private final <T extends TableDefinition> List<T> filterMasterDataTables(List<TableDefinition> list, boolean include) {
        List<T> result = new ArrayList<T>();

        definitionLoop: for (TableDefinition definition : list) {
            for (String table : masterDataTableNames) {
                if (definition.getName().matches(table)) {

                    // If we have a match, then add the table only if master
                    // data tables are included in the result
                    if (include) {
                        result.add((T) new DefaultMasterDataTableDefinition(definition));
                    }

                    continue definitionLoop;
                }

            }

            // If we don't have any match, then add the table only if
            // master data tables are excluded in the result
            if (!include) {
                result.add((T) definition);
            }
        }

        return result;
    }

    /**
     * Retrieve ALL relations from the database.
     */
    protected final Relations getRelations0() throws SQLException {
        DefaultRelations relations = new DefaultRelations(this);

        if (generateRelations()) {
            loadPrimaryKeys(relations);
            loadForeignKeys(relations);
        }

        return relations;
    }

    /**
     * Retrieve primary keys and store them to relations
     */
    protected abstract void loadPrimaryKeys(DefaultRelations relations) throws SQLException;

    /**
     * Retrieve foreign keys and store them to relations. Primary keys are
     * already loaded.
     */
    protected abstract void loadForeignKeys(DefaultRelations relations) throws SQLException;

    /**
     * Retrieve ALL tables from the database. This will be filtered in
     * {@link #getTables()}
     */
    protected abstract List<TableDefinition> getTables0() throws SQLException;

    /**
     * Retrieve ALL stored procedures from the database. This will be filtered
     * in {@link #getProcedures()}
     */
    protected abstract List<ProcedureDefinition> getProcedures0() throws SQLException;

    /**
     * Retrieve ALL stored functions from the database. This will be filtered in
     * {@link #getFunctions()}
     */
    protected abstract List<FunctionDefinition> getFunctions0() throws SQLException;

    /**
     * Retrieve ALL enum UDTs from the database. This will be filtered in
     * {@link #getEnums()}
     */
    protected abstract List<EnumDefinition> getEnums0() throws SQLException;

    /**
     * Retrieve ALL UDTs from the database. This will be filtered in
     * {@link #getEnums()}
     */
    protected abstract List<UDTDefinition> getUDTs0() throws SQLException;
}
