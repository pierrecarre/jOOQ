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
package org.jooq.util;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class DefaultRelations extends AbstractDefinition implements Relations {

	private Map<String, String> columnToPrimaryKey = new LinkedHashMap<String, String>();
	private Map<String, String> columnToForeignKey = new LinkedHashMap<String, String>();

	private Map<String, String> primaryKeyToTable = new LinkedHashMap<String, String>();
	private Map<String, String> foreignKeyToTable = new LinkedHashMap<String, String>();
	private Map<String, Set<ColumnDefinition>> primaryKeyToColumns = new LinkedHashMap<String, Set<ColumnDefinition>>();
	private Map<String, Set<ColumnDefinition>> foreignKeyToColumns = new LinkedHashMap<String, Set<ColumnDefinition>>();

	private Map<String, Set<String>> primaryKeyToForeignKeys = new LinkedHashMap<String, Set<String>>();
	private Map<String, String> foreignKeyToPrimaryKey = new LinkedHashMap<String, String>();

	private Map<String, PrimaryKeyDefinition> primaryKeys = new LinkedHashMap<String, PrimaryKeyDefinition>();
	private Map<String, ForeignKeyDefinition> foreignKeys = new LinkedHashMap<String, ForeignKeyDefinition>();

	public DefaultRelations(Database database) {
		super(database, "", "");
	}

	public void addPrimaryKey(String key, ColumnDefinition column) {
		columnToPrimaryKey.put(column.getQualifiedName(), key);
		primaryKeyToTable.put(key, column.getTableName());
		Set<ColumnDefinition> list = primaryKeyToColumns.get(key);

		if (list == null) {
			list = new LinkedHashSet<ColumnDefinition>();
			primaryKeyToColumns.put(key, list);
		}

		list.add(column);
	}

	public void addForeignKey(String key, String primaryKey, ColumnDefinition column) {
		columnToForeignKey.put(column.getQualifiedName(), key);
		foreignKeyToTable.put(key, column.getTableName());
		Set<ColumnDefinition> list = foreignKeyToColumns.get(key);

		if (list == null) {
			list = new LinkedHashSet<ColumnDefinition>();
			foreignKeyToColumns.put(key, list);
		}

		list.add(column);

		foreignKeyToPrimaryKey.put(key, primaryKey);
		Set<String> list2 = primaryKeyToForeignKeys.get(primaryKey);

		if (list2 == null) {
			list2 = new LinkedHashSet<String>();
			primaryKeyToForeignKeys.put(primaryKey, list2);
		}

		list2.add(key);
	}

	public String getPrimaryKeyName(ColumnDefinition column) {
		String qualifiedName = column.getQualifiedName();
		return columnToPrimaryKey.get(qualifiedName);
	}

	@Override
	public PrimaryKeyDefinition getPrimaryKey(ColumnDefinition column) {
		String qualifiedName = column.getQualifiedName();

		if (!primaryKeys.containsKey(qualifiedName)) {
			String primaryKey = columnToPrimaryKey.get(qualifiedName);
			PrimaryKeyDefinition definition = null;

			if (primaryKey != null) {
				definition = new DefaultPrimaryKeyDefinition(getDatabase(), primaryKey);

				for (ColumnDefinition c : primaryKeyToColumns.get(primaryKey)) {
					definition.getKeyColumns().add(c);
				}

				Set<String> list = primaryKeyToForeignKeys.get(primaryKey);
				if (list != null) {
					for (String foreignKey : list) {
						definition.getForeignKeys().add(getForeignKey(foreignKeyToColumns.get(foreignKey).iterator().next()));
					}
				}
			}

			primaryKeys.put(qualifiedName, definition);
		}

		return primaryKeys.get(qualifiedName);
	}

	@Override
	public ForeignKeyDefinition getForeignKey(ColumnDefinition column) {
		String qualifiedName = column.getQualifiedName();

		if (!foreignKeys.containsKey(column.getQualifiedName())) {
			String foreignKey = columnToForeignKey.get(qualifiedName);
			ForeignKeyDefinition definition = null;

			if (foreignKey != null) {
				String primaryKey = foreignKeyToPrimaryKey.get(foreignKey);

				if (primaryKey != null) {
					definition = new DefaultForeignKeyDefinition(
							getDatabase(), foreignKey,
							foreignKeyToTable.get(foreignKey),
							primaryKeyToTable.get(primaryKey));

					for (ColumnDefinition c : foreignKeyToColumns.get(foreignKey)) {
						definition.getKeyColumns().add(c);
					}

					Set<ColumnDefinition> list = primaryKeyToColumns.get(primaryKey);
					if (list != null) {
						for (ColumnDefinition c : list) {
							definition.getReferencedColumns().add(c);
						}
					}
				}
			}

			foreignKeys.put(qualifiedName, definition);
		}

		return foreignKeys.get(column.getQualifiedName());
	}

    @Override
    public String getSubPackage() {
        return "";
    }
}
