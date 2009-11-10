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

package org.jooq;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Base functionality declaration for all query objects
 * 
 * @author Lukas Eder
 */
public interface QueryPart extends Serializable {

	/**
	 * Transform this object into SQL. This always results in calling {@link
	 * #toSQL(false)}
	 * 
	 * @return SQL representation of this QueryPart
	 * @see {@link #toSQL(boolean)}
	 */
	String toSQL();

	/**
	 * Transform this object into SQL.
	 * 
	 * @param inlineParameters
	 *            if set to true, all parameters are inlined, not replaced by
	 *            "?"
	 * @return SQL representation of this QueryPart
	 */
	String toSQL(boolean inlineParameters);

	/**
	 * Bind all parameters of this QueryPart to a PreparedStatement. This always
	 * results in calling {@link #bind(stmt, 1)}
	 * 
	 * @param stmt
	 *            The statement to bind values to
	 * @return The index of the next binding variable
	 * @throws SQLException
	 * @see {@link {@link #bind(PreparedStatement, int)}
	 */
	int bind(PreparedStatement stmt) throws SQLException;

	/**
	 * Bind all parameters of this QueryPart to a PreparedStatement.
	 * 
	 * @param stmt
	 *            The statement to bind values to
	 * @param initialIndex The index of the next binding variable
	 * @return The index of the next binding variable
	 * @throws SQLException
	 */
	int bind(PreparedStatement stmt, int initialIndex) throws SQLException;

}
