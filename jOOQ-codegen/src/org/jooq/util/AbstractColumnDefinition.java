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


/**
 * A base implementation for column definitions.
 *
 * @author Lukas Eder
 */
public abstract class AbstractColumnDefinition extends AbstractDefinition implements ColumnDefinition {

	private final int position;
	private final Class<?> type;
	private final String table;

	private boolean isPrimaryKeyLoaded;
	private boolean isPrimaryKey;
	private boolean foreignKeyLoaded;
	private ForeignKeyDefinition foreignKey;

	public AbstractColumnDefinition(Database database, String table, String name, int position, Class<?> type, String comment) {
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
	public final Class<?> getTypeClass() {
		return type;
	}

	@Override
	public final String getTableName() {
		return table;
	}

	@Override
	public final String getType() {
		return type.getSimpleName();
	}

	@Override
	public final boolean isPrimaryKey() throws SQLException {
		if (!isPrimaryKeyLoaded) {
			isPrimaryKeyLoaded = true;
			isPrimaryKey = isPrimaryKey0();
		}

		return isPrimaryKey;
	}

	@Override
	public final ForeignKeyDefinition getForeignKey() throws SQLException {
		if (!foreignKeyLoaded) {
			foreignKeyLoaded = true;
			foreignKey = getForeignKey0();
		}

		return foreignKey;
	}

	protected abstract boolean isPrimaryKey0() throws SQLException;
	protected abstract ForeignKeyDefinition getForeignKey0() throws SQLException;
}
