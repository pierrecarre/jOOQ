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
 * <p>
 * This interface is for JOOQ INTERNAL USE only. Do not reference directly
 *
 * @author Lukas Eder
 */
public interface QueryPart extends Serializable {

    /**
     * Transform this object into SQL, such that it can be used as a reference.
     * This always results in calling {@link #toSQLReference(false)}
     * <p>
     * This method is for JOOQ INTERNAL USE only. Do not reference directly
     *
     * @param configuration The configuration overriding dialects and schemata
     * @return SQL representation of this QueryPart
     * @see {@link #toSQLReference(boolean)}
     */
    String toSQLReference(Configuration configuration);

    /**
     * Transform this object into SQL, such that it can be used as a reference.
     * <p>
     * This method is for JOOQ INTERNAL USE only. Do not reference directly
     *
     * @param configuration The configuration overriding dialects and schemata
     * @param inlineParameters if set to true, all parameters are inlined, not
     *            replaced by "?"
     * @return SQL representation of this QueryPart
     */
    String toSQLReference(Configuration configuration, boolean inlineParameters);

    /**
     * Transform this object into SQL, such that it can be used as a
     * declaration. Usually, this is the same as calling
     * {@link #toSQLReference()}. This always results in calling
     * {@link #toSQLDeclaration(false)}
     * <p>
     * This method is for JOOQ INTERNAL USE only. Do not reference directly
     *
     * @param configuration The configuration overriding dialects and schemata
     * @return SQL representation of this QueryPart
     * @see {@link #toSQLReference(boolean)}
     */
    String toSQLDeclaration(Configuration configuration);

    /**
     * Transform this object into SQL, such that it can be used as a
     * declaration. Usually, this is the same as calling
     * {@link #toSQLReference(boolean)}
     * <p>
     * This method is for JOOQ INTERNAL USE only. Do not reference directly
     *
     * @param configuration The configuration overriding dialects and schemata
     * @param inlineParameters if set to true, all parameters are inlined, not
     *            replaced by "?"
     * @return SQL representation of this QueryPart
     */
    String toSQLDeclaration(Configuration configuration, boolean inlineParameters);

    /**
     * Bind all parameters of this QueryPart to a PreparedStatement. This always
     * results in calling {@link #bind(stmt, 1)}
     * <p>
     * This method is for JOOQ INTERNAL USE only. Do not reference directly
     *
     * @param configuration The configuration overriding dialects and schemata
     * @param stmt The statement to bind values to
     * @return The index of the next binding variable
     * @throws SQLException
     * @see {@link {@link #bind(PreparedStatement, int)}
     */
    int bind(Configuration configuration, PreparedStatement stmt) throws SQLException;

    /**
     * Bind all parameters of this QueryPart to a PreparedStatement.
     * <p>
     * This method is for JOOQ INTERNAL USE only. Do not reference directly
     *
     * @param configuration The configuration overriding dialects and schemata
     * @param stmt The statement to bind values to
     * @param initialIndex The index of the next binding variable
     * @return The index of the next binding variable
     * @throws SQLException
     */
    int bind(Configuration configuration, PreparedStatement stmt, int initialIndex) throws SQLException;

    /**
     * Transform this object into SQL, such that it can be used as a reference.
     * This always results in calling {@link #toSQLReference(false)}
     * <p>
     * This method is for JOOQ INTERNAL USE only. Do not reference directly
     *
     * @return SQL representation of this QueryPart
     * @see {@link #toSQLReference(boolean)}
     * @deprecated 1.5.2 use {@link #toSQLReference(Configuration)} instead
     */
    @Deprecated
    String toSQLReference();

    /**
     * Transform this object into SQL, such that it can be used as a reference.
     * <p>
     * This method is for JOOQ INTERNAL USE only. Do not reference directly
     *
     * @param inlineParameters if set to true, all parameters are inlined, not
     *            replaced by "?"
     * @return SQL representation of this QueryPart
     * @deprecated 1.5.2 use {@link #toSQLReference(Configuration, boolean)}
     *             instead
     */
    @Deprecated
    String toSQLReference(boolean inlineParameters);

    /**
     * Transform this object into SQL, such that it can be used as a
     * declaration. Usually, this is the same as calling
     * {@link #toSQLReference()}. This always results in calling
     * {@link #toSQLDeclaration(false)}
     * <p>
     * This method is for JOOQ INTERNAL USE only. Do not reference directly
     *
     * @return SQL representation of this QueryPart
     * @see {@link #toSQLReference(boolean)}
     * @deprecated 1.5.2 use {@link #toSQLDeclaration(Configuration)} instead
     */
    @Deprecated
    String toSQLDeclaration();

    /**
     * Transform this object into SQL, such that it can be used as a
     * declaration. Usually, this is the same as calling
     * {@link #toSQLReference(boolean)}
     * <p>
     * This method is for JOOQ INTERNAL USE only. Do not reference directly
     *
     * @param inlineParameters if set to true, all parameters are inlined, not
     *            replaced by "?"
     * @return SQL representation of this QueryPart
     * @deprecated 1.5.2 use {@link #toSQLDeclaration(Configuration, boolean)}
     *             instead
     */
    @Deprecated
    String toSQLDeclaration(boolean inlineParameters);

    /**
     * Bind all parameters of this QueryPart to a PreparedStatement. This always
     * results in calling {@link #bind(stmt, 1)}
     * <p>
     * This method is for JOOQ INTERNAL USE only. Do not reference directly
     *
     * @param stmt The statement to bind values to
     * @return The index of the next binding variable
     * @throws SQLException
     * @see {@link {@link #bind(PreparedStatement, int)}
     * @deprecated 1.5.2 use {@link #bind(Configuration, PreparedStatement)}
     *             instead
     */
    @Deprecated
    int bind(PreparedStatement stmt) throws SQLException;

    /**
     * Bind all parameters of this QueryPart to a PreparedStatement.
     * <p>
     * This method is for JOOQ INTERNAL USE only. Do not reference directly
     *
     * @param stmt The statement to bind values to
     * @param initialIndex The index of the next binding variable
     * @return The index of the next binding variable
     * @throws SQLException
     * @deprecated 1.5.2 use
     *             {@link #bind(Configuration, PreparedStatement, int)} instead
     */
    @Deprecated
    int bind(PreparedStatement stmt, int initialIndex) throws SQLException;

    /**
     * Reproduce the SQL dialect this QueryPart was created with
     * <p>
     * This method is for JOOQ INTERNAL USE only. Do not reference directly
     *
     * @return The SQL dialect
     * @deprecated 1.5.2 dialects should not be accessed via {@link QueryPart}
     *             anymore
     */
    @Deprecated
    SQLDialect getDialect();
}
