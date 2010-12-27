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
package org.jooq.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DefaultForeignKeyDefinition extends AbstractDefinition implements ForeignKeyDefinition {

	private final List<ColumnDefinition> keyColumns;
	private final List<ColumnDefinition> referencedColumns;
	private final String referencingTableName;
	private final String referencedTableName;

	public DefaultForeignKeyDefinition(Database database, String name, String referencingTableName, String referencedTableName) {
		super(database, name, null);

		this.keyColumns = new ArrayList<ColumnDefinition>();
		this.referencedColumns = new ArrayList<ColumnDefinition>();
		this.referencingTableName = referencingTableName;
		this.referencedTableName = referencedTableName;
	}

	@Override
	public TableDefinition getKeyTableDefinition() throws SQLException {
	    return getDatabase().getTable(referencingTableName);
	}

	@Override
	public List<ColumnDefinition> getKeyColumns() {
		return keyColumns;
	}

	@Override
	public String getReferencedTableName() {
		return referencedTableName;
	}

	@Override
	public TableDefinition getReferencedTableDefinition() throws SQLException {
	    return getDatabase().getTable(referencedTableName);
	}

	@Override
	public List<ColumnDefinition> getReferencedColumns() {
		return referencedColumns;
	}

    @Override
    public String getSubPackage() {
        return "";
    }
}
