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

package org.jooq.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.QueryPart;
import org.jooq.QueryPartProvider;
import org.jooq.SQLDialect;
import org.jooq.Schema;

abstract class AbstractQueryPart implements QueryPart, QueryPartProvider {

    private static final long   serialVersionUID = 2078114876079493107L;
    private final Configuration configuration;

    AbstractQueryPart(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public final QueryPart getQueryPart() {
        return this;
    }

    // -------------------------------------------------------------------------
    // The new API
    // -------------------------------------------------------------------------

    @Override
    public final String toSQLReference(Configuration c) {
        return toSQLReference(c, false);
    }

    @Override
    public final String toSQLDeclaration(Configuration c) {
        return toSQLDeclaration(c, false);
    }

    /**
     * The default implementation is the same as that of
     * {@link #toSQLReference(Configuration, boolean)}. Subclasses may override this method.
     */
    @Override
    public String toSQLDeclaration(Configuration c, boolean inlineParameters) {
        return toSQLReference(c, inlineParameters);
    }

    @Override
    public final int bind(Configuration c, PreparedStatement stmt) throws SQLException {
        return bind(c, stmt, 1);
    }

    // -------------------------------------------------------------------------
    // The deprecated API
    // -------------------------------------------------------------------------

    @Override
    @Deprecated
    public final String toSQLReference() {
        return toSQLReference(configuration);
    }

    @Override
    @Deprecated
    public final String toSQLReference(boolean inlineParameters) {
        return toSQLReference(configuration, inlineParameters);
    }

    @Override
    @Deprecated
    public final String toSQLDeclaration() {
        return toSQLDeclaration(configuration);
    }

    @Override
    @Deprecated
    public final String toSQLDeclaration(boolean inlineParameters) {
        return toSQLDeclaration(configuration, inlineParameters);
    }

    @Override
    @Deprecated
    public final int bind(PreparedStatement stmt) throws SQLException {
        return bind(configuration, stmt);
    }

    @Override
    @Deprecated
    public final int bind(PreparedStatement stmt, int initialIndex) throws SQLException {
        return bind(configuration, stmt, initialIndex);
    }

    @Override
    public final boolean equals(Object that) {
        if (that instanceof QueryPart) {
            return toSQLReference(configuration, true).equals(((QueryPart) that).toSQLReference(configuration, true));
        }

        return false;
    }

    @Override
    public final int hashCode() {
        return toSQLReference(configuration, true).hashCode();
    }

    @Override
    public final String toString() {
        return toSQLReference(configuration, true);
    }

    @Override
    public final SQLDialect getDialect() {
        return configuration.getDialect();
    }

    /*
     * Due to deprecation in org.jooq.Query, this method cannot be declared
     * final yet.
     */
    /* final */ Configuration getConfiguration() {
        return configuration;
    }

    final Schema map(Schema schema) {
        return configuration.getSchemaMapping().map(schema);
    }

    final <T> Field<T> constant(T value) {
        return create().constant(value);
    }

    Factory create() {
        return Factory.getFactory(configuration);
    }
}
