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

package org.jooq.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Configuration;
import org.jooq.QueryPart;
import org.jooq.QueryPartProvider;
import org.jooq.SQLDialect;

/**
 * @author Lukas Eder
 */
abstract class AbstractQueryPartList<T extends QueryPartProvider> extends AbstractList<T> implements QueryPart, QueryPartProvider {

    private static final long   serialVersionUID = -2936922742534009564L;
    private final List<T>       wrappedList      = new ArrayList<T>();
    private final Configuration configuration;

    AbstractQueryPartList(Configuration configuration) {
        this(configuration, null);
    }

    AbstractQueryPartList(Configuration configuration, List<? extends T> wrappedList) {
        this.configuration = configuration;

        if (wrappedList != null) {
            addAll(wrappedList);
        }
    }

    @Override
    public final QueryPart getQueryPart() {
        return this;
    }

    @Override
    public final T get(int index) {
        return wrappedList.get(index);
    }

    @Override
    public final int size() {
        return wrappedList.size();
    }

    @Override
    public final void add(int index, T element) {
        wrappedList.add(index, element);
    }

    // -------------------------------------------------------------------------
    // The new API
    // -------------------------------------------------------------------------

    @Override
    public final String toSQLReference(Configuration configuration) {
        return toSQLReference(configuration, false);
    }

    @Override
    public final String toSQLReference(Configuration configuration, boolean inlineParameters) {
        return toSQL(configuration, inlineParameters, false);
    }

    @Override
    public final String toSQLDeclaration(Configuration configuration) {
        return toSQLDeclaration(configuration, false);
    }

    @Override
    public final String toSQLDeclaration(Configuration configuration, boolean inlineParameters) {
        return toSQL(configuration, inlineParameters, true);
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

    private final String toSQL(Configuration configuration, boolean inlineParameters, boolean renderAsDeclaration) {
        if (isEmpty()) {
            return toSQLEmptyList();
        }

        StringBuilder sb = new StringBuilder();

        String separator = "";
        for (T queryPart : this) {
            sb.append(separator);

            if (renderAsDeclaration) {
                sb.append(toSQLDeclaration(configuration, queryPart, inlineParameters));
            }
            else {
                sb.append(toSQLReference(configuration, queryPart, inlineParameters));
            }

            separator = getListSeparator() + " ";
        }

        return sb.toString();
    }

    /**
     * Subclasses may override this method
     */
    protected String toSQLReference(Configuration configuration, T queryPart, boolean inlineParameters) {
        return queryPart.getQueryPart().toSQLReference(configuration, inlineParameters);
    }

    /**
     * Subclasses may override this method
     */
    protected String toSQLDeclaration(Configuration configuration, T queryPart, boolean inlineParameters) {
        return queryPart.getQueryPart().toSQLDeclaration(configuration, inlineParameters);
    }

    @Override
    public final int bind(Configuration configuration, PreparedStatement stmt, int initialIndex) throws SQLException {
        int result = initialIndex;

        for (T queryPart : this) {
            result = queryPart.getQueryPart().bind(configuration, stmt, result);
        }

        return result;
    }

    @Override
    public final int bind(Configuration configuration, PreparedStatement stmt) throws SQLException {
        return bind(configuration, stmt, 1);
    }

    /**
     * Subclasses may override this method
     */
    protected String toSQLEmptyList() {
        throw new IllegalStateException("This list does not support generating SQL from empty lists : " + getClass());
    }

    /**
     * Subclasses may override this method
     */
    protected String getListSeparator() {
        return ",";
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
}
