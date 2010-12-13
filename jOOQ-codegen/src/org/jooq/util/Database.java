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
import java.util.List;
import java.util.Properties;

import org.jooq.SQLDialect;
import org.jooq.impl.Factory;

/**
 * A general database model.
 *
 * @author Lukas Eder
 */
public interface Database {

    /**
     * The schema generated from this database
     */
    SchemaDefinition getSchema() throws SQLException;

    /**
     * Retrieve the schema's primary key / foreign key relations
     */
    Relations getRelations() throws SQLException;

    /**
     * The tables contained in this database (for schema {@link #getSchema()})
     */
    List<TableDefinition> getTables() throws SQLException;

    /**
     * Get a table in this database by name
     */
    TableDefinition getTable(String name) throws SQLException;

    /**
     * The master data tables contained in this database (for schema
     * {@link #getSchema()})
     */
    List<MasterDataTableDefinition> getMasterDataTables() throws SQLException;

    /**
     * Get a master data table in this database by name
     */
    MasterDataTableDefinition getMasterDataTable(String name) throws SQLException;

    /**
     * The enum UDTs defined in this database
     */
    List<EnumDefinition> getEnums() throws SQLException;

    /**
     * Get an enum UDT defined in this database by name
     */
    EnumDefinition getEnum(String name) throws SQLException;

    /**
     * The UDTs defined in this database
     */
    List<UDTDefinition> getUDTs() throws SQLException;

    /**
     * Get a UDT defined in this database by name
     */
    UDTDefinition getUDT(String name) throws SQLException;

    /**
     * The stored procedures contained in this database (for schema
     * {@link #getSchema()})
     */
    List<ProcedureDefinition> getProcedures() throws SQLException;

    /**
     * The stored functions contained in this database (for schema
     * {@link #getSchema()})
     */
    List<FunctionDefinition> getFunctions() throws SQLException;

    /**
     * Initialise a connection to this database
     */
    void setConnection(Connection connection);

    /**
     * The database connection
     */
    Connection getConnection();

    /**
     * Initialise a schema name to this database
     */
    void setSchemaName(String schema);

    /**
     * The database schema
     */
    String getSchemaName();

    /**
     * Only database objects matching any of these regular expressions will be
     * generated.
     */
    void setIncludes(String[] includes);

    /**
     * Only database objects matching any of these regular expressions will be
     * generated.
     */
    String[] getIncludes();

    /**
     * Database objects matching any of these regular expressions will be
     * generated as master data tables.
     */
    void setMasterDataTableNames(String[] masterDataTables);

    /**
     * Database objects matching any of these regular expressions will be
     * generated as master data tables.
     */
    String[] getMasterDataTableNames();

    /**
     * Database objects matching any of these regular expressions will not be
     * generated.
     */
    void setExcludes(String[] excludes);

    /**
     * Database objects matching any of these regular expressions will not be
     * generated.
     */
    String[] getExcludes();

    /**
     * Whether foreign key relations should be resolved
     */
    boolean generateRelations();

    /**
     * Whether foreign key relations should be resolved
     */
    void setGenerateRelations(boolean generateRelations);

    /**
     * Initialise the target package name
     */
    void setTargetPackage(String packageName);

    /**
     * Initialise the target directory
     */
    void setTargetDirectory(String directory);

    /**
     * The target package
     */
    String getTargetPackage();

    /**
     * The target directory
     */
    String getTargetDirectory();

    /**
     * Get the dialect for this database
     */
    SQLDialect getDialect();

    /**
     * Create the factory for this database
     */
    Factory create();

    /**
     * Set the configuration properties to this database
     */
    void setProperties(Properties properties);

    /**
     * Get a configuration property from this database
     */
    String getProperty(String property);
}
