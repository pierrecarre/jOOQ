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
package org.jooq.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jooq.Configuration;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.ResultProviderQuery;
import org.jooq.SQLDialect;

abstract class AbstractDelegatingResultProviderQuery<R extends Record> extends AbstractQueryPart implements
    ResultProviderQuery<R> {

    /**
     * Generated UID
     */
    private static final long            serialVersionUID = 3382400928803573548L;
    private final ResultProviderQuery<R> delegate;

    AbstractDelegatingResultProviderQuery(SQLDialect dialect, ResultProviderQuery<R> delegate) {
        super(dialect);

        this.delegate = delegate;
    }

    @Override
    public final String toSQLDeclaration(boolean inlineParameters) {
        return delegate.toSQLDeclaration(inlineParameters);
    }

    @Override
    public final String toSQLReference(boolean inlineParameters) {
        return delegate.toSQLReference(inlineParameters);
    }

    @Override
    public final int bind(PreparedStatement stmt, int initialIndex) throws SQLException {
        return delegate.bind(stmt, initialIndex);
    }

    @Override
    public final Result<R> getResult() {
        return delegate.getResult();
    }

    @Override
    public final int execute() throws SQLException {
        return delegate.execute();
    }

    @Override
    public final Configuration getConfiguration() {
        return delegate.getConfiguration();
    }
}
