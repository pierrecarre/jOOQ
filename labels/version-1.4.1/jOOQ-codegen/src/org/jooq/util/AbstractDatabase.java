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

import org.jooq.SQLDialect;

/**
 * A base implementation for all types of databases.
 *
 * @author Lukas Eder
 */
public abstract class AbstractDatabase implements Database {

	private Connection connection;
	private String schema;
	private String[] excludes;
	private String[] includes;
	private boolean generateRelations = false;

	private List<TableDefinition> tables;
	private List<ProcedureDefinition> procedures;
	private List<FunctionDefinition> functions;
	private Relations relations;

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
	public boolean generateRelations() {
		return generateRelations;
	}

	@Override
	public void setGenerateRelations(boolean generateRelations) {
		this.generateRelations = generateRelations;
	}

	@Override
	public final List<TableDefinition> getTables() throws SQLException {
		if (tables == null) {
			tables = filter(getTables0());
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
			procedures = filter(getProcedures0());
		}

		return procedures;
	}

	@Override
	public final List<FunctionDefinition> getFunctions() throws SQLException {
		if (functions == null) {
			functions = filter(getFunctions0());
		}

		return functions;
	}

	private final <T extends Definition> List<T> filter(List<T> definitions) {
		List<T> result = new ArrayList<T>();

		definitionsLoop: for (T definition : definitions) {
			for (String exclude : excludes) {
				if (definition.getName().matches(exclude)) {
					continue definitionsLoop;
				}
			}

			for (String include : includes) {
				if (definition.getName().matches(include)) {
					result.add(definition);
					continue definitionsLoop;
				}
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
	 * Retrieve foreign keys and store them to relations. Primary keys are already loaded.
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
}