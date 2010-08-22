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

/**
 * A base implementation for any type of definition.
 *
 * @author Lukas Eder
 */
public abstract class AbstractDefinition implements Definition {

	private final Database database;
	private final String name;
	private final String comment;

	public AbstractDefinition(Database database, String name, String comment) {
		this.database = database;
		this.name = name;
		this.comment = comment;
	}

	@Override
	public final String getSchemaName() {
		return database.getSchemaName();
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final String getNameUC() {
		return name.toUpperCase();
	}

	@Override
	public final String getComment() {
		return comment;
	}

	@Override
	public final String getJavaClassName() {
		StringBuilder result = new StringBuilder();

		for (String word : getName().split("_")) {
			result.append(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase());
		}

		return result.toString();
	}

	@Override
	public final String getJavaClassName(String suffix) {
		return getJavaClassName() + suffix;
	}

	@Override
	public final String getFileName() {
		return getJavaClassName() + ".java";
	}

	@Override
	public final String getFileName(String suffix) {
		return getJavaClassName() + suffix + ".java";
	}

	@Override
	public final Database getDatabase() {
		return database;
	}

	protected final Connection getConnection() {
		return database.getConnection();
	}
}
