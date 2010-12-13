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

import java.sql.SQLException;

import org.jooq.Field;

/**
 * A base implementation for column definitions.
 *
 * @author Lukas Eder
 */
public class DefaultColumnDefinition extends AbstractDefinition implements ColumnDefinition {

    private final int            position;
    private final String         type;
    private final String         table;

    private boolean              primaryKeyLoaded;
    private PrimaryKeyDefinition primaryKey;
    private boolean              foreignKeyLoaded;
    private ForeignKeyDefinition foreignKey;

    public DefaultColumnDefinition(Database database, String table, String name, int position, String type,
        String comment) {
        super(database, name, comment);

        this.table = table;
        this.position = position;
        this.type = type;
    }

    @Override
    public final int getPosition() {
        return position;
    }

    @Override
    public final String getTypeClass() throws SQLException {
        ForeignKeyDefinition fk = getDatabase().getRelations().getForeignKey(this);

        if (fk != null) {
            TableDefinition referencedTable = fk.getReferencedTableDefinition();

            if (referencedTable instanceof MasterDataTableDefinition) {
                return referencedTable.getFullJavaClassName();
            }
        }

        return type;
    }

    @Override
    public final String getTableName() {
        return table;
    }

    @Override
    public final String getType() throws SQLException {
        return getTypeClass().replaceAll(".*\\.", "");
    }

    @Override
    public final String getQualifiedName() {
        return getSchemaName() + "." + getTableName() + "." + getName();
    }

    @Override
    public final PrimaryKeyDefinition getPrimaryKey() throws SQLException {
        if (!primaryKeyLoaded) {
            primaryKeyLoaded = true;
            primaryKey = getPrimaryKey0();
        }

        return primaryKey;
    }

    @Override
    public final ForeignKeyDefinition getForeignKey() throws SQLException {
        if (!foreignKeyLoaded) {
            foreignKeyLoaded = true;
            foreignKey = getForeignKey0();
        }

        return foreignKey;
    }

    protected final PrimaryKeyDefinition getPrimaryKey0() throws SQLException {
        return getDatabase().getRelations().getPrimaryKey(this);
    }

    protected final ForeignKeyDefinition getForeignKey0() throws SQLException {
        return getDatabase().getRelations().getForeignKey(this);
    }

    @Override
    public String getSubPackage() {
        return "";
    }

    @Override
    public final Field<?> getField() {
        return create().plainSQLField(getQualifiedName());
    }
}
