/**
 * Copyright (c) 2009-2011, Lukas Eder, lukas.eder@gmail.com
 * All rights reserved.
 *
 * This software is licensed to you under the Apache License, Version 2.0
 * (the "License"); You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
 * . Neither the name "jOOQ" nor the names of its contributors may be
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
import java.util.Collection;
import java.util.List;

import org.jooq.Attachable;
import org.jooq.AttachableInternal;
import org.jooq.Configuration;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Parameter;
import org.jooq.QueryPart;
import org.jooq.QueryPartInternal;
import org.jooq.SQLDialect;
import org.jooq.SQLDialectNotSupportedException;
import org.jooq.Schema;
import org.jooq.SchemaMapping;
import org.jooq.Store;
import org.jooq.Table;

/**
 * @author Lukas Eder
 */
abstract class AbstractQueryPart implements QueryPartInternal, AttachableInternal {

    private static final long       serialVersionUID = 2078114876079493107L;

    private final AttachableImpl    attachable;

    AbstractQueryPart() {
        this(null);
    }

    AbstractQueryPart(Configuration configuration) {
        this.attachable = new AttachableImpl(this, configuration);
    }

    @Override
    public final <T> T internalAPI(Class<T> internalType) {
        return internalType.cast(this);
    }

    protected final AttachableInternal internal(Attachable part) {
        return part.internalAPI(AttachableInternal.class);
    }

    protected final QueryPartInternal internal(QueryPart part) {
        return part.internalAPI(QueryPartInternal.class);
    }

    @Override
    public final String getSQL() {
        return toSQLReference(getConfiguration());
    }

    // -------------------------------------------------------------------------
    // The Attachable API
    // -------------------------------------------------------------------------

    @Override
    public final void attach(Configuration c) {
        attachable.attach(c);
    }

    @Override
    public final Configuration getConfiguration() {
        return attachable.getConfiguration();
    }

    /**
     * Internal convenience method
     */
    protected final List<Attachable> getAttachables(Collection<? extends QueryPart> list) {
        return attachable.getAttachableQueryParts(list);
    }

    /**
     * Internal convenience method
     */
    protected final List<Attachable> getAttachables(QueryPart... list) {
        return attachable.getAttachableQueryParts(list);
    }

    /**
     * Internal convenience method
     */
    protected final List<Attachable> getAttachables(Store<?> store) {
        return attachable.getAttachableStores(store);
    }

    /**
     * Internal convenience method
     */
    protected final Factory create() {
        return Factory.getNewFactory(getConfiguration());
    }

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

    @Override
    public final int bind(Configuration c, PreparedStatement stmt, int initialIndex) throws SQLException {
        return bindReference(c, stmt, 1);
    }

    @Override
    public final int bindReference(Configuration configuration, PreparedStatement stmt) throws SQLException {
        return bindReference(configuration, stmt, 1);
    }

    @Override
    public final int bindDeclaration(Configuration configuration, PreparedStatement stmt) throws SQLException {
        return bindDeclaration(configuration, stmt, 1);
    }

    /**
     * The default implementation is the same as that of
     * {@link #bindReference(Configuration, PreparedStatement, int)}. Subclasses may override this method.
     */
    @Override
    public int bindDeclaration(Configuration configuration, PreparedStatement stmt, int initialIndex) throws SQLException {
        return bindReference(configuration, stmt, initialIndex);
    }

    @Override
    public final boolean equals(Object that) {
        if (that instanceof QueryPartInternal) {
            String sql1 = toSQLReference(getConfiguration(), true);
            String sql2 = ((QueryPartInternal) that).toSQLReference(getConfiguration(), true);

            return sql1.equals(sql2);
        }

        return false;
    }

    @Override
    public final int hashCode() {
        return toSQLReference(getConfiguration(), true).hashCode();
    }

    @Override
    public final String toString() {
        try {
            return toSQLReference(getConfiguration(), true);
        }
        catch (SQLDialectNotSupportedException e) {
            return "[ ... " + e.getMessage() + " ... ]";
        }
    }

    @Override
    public final SQLDialect getDialect() {
        return getConfiguration().getDialect();
    }

    /**
     * Internal convenience method
     */
    final <T> DataType<T> getDataType(Class<? extends T> type) {
        return FieldTypeHelper.getDataType(getDialect(), type);
    }

    /**
     * Internal convenience method
     */
    final SchemaMapping getSchemaMapping() {
        return getConfiguration().getSchemaMapping();
    }

    /**
     * Internal convenience method
     */
    final Schema getMappedSchema(Configuration configuration, Schema schema) {
        if (configuration.getSchemaMapping() != null) {
            return configuration.getSchemaMapping().map(schema);
        }
        else {
            return schema;
        }
    }

    /**
     * Internal convenience method
     */
    final Table<?> getMappedTable(Configuration configuration, Table<?> table) {
        if (configuration.getSchemaMapping() != null) {
            return configuration.getSchemaMapping().map(table);
        }
        else {
            return table;
        }
    }

    /**
     * Internal convenience method
     */
    final <T> Field<T> constant(T value) {
        return create().constant(value);
    }

    /**
     * Internal convenience method
     */
    final <T> Field<T> constant(Object value, Field<T> field) {
        return create().constant(value, field);
    }

    /**
     * Internal convenience method
     */
    final <T> Field<T> constant(Object value, Parameter<T> parameter) {
        return create().constant(value, parameter.getDataType());
    }

    /**
     * Internal convenience method
     */
    final <T> Field<T> constant(Object value, DataType<T> type) {
        return create().constant(value, type);
    }

    /**
     * Internal convenience method
     */
    final List<Field<?>> constants(Object... value) {
        return create().constants(value);
    }

    /**
     * Wrap a piece of SQL code in parentheses, if not wrapped already
     */
    protected final String wrapInParentheses(String sql) {
        if (sql.startsWith("(")) {
            return sql;
        }
        else {
            return "(" + sql + ")";
        }
    }
}
