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

import org.jooq.AliasProvider;
import org.jooq.Configuration;

/**
 * @author Lukas Eder
 */
class AliasProviderImpl<T extends AliasProvider<T>> extends AbstractNamedQueryPart implements AliasProvider<T> {

    private static final long serialVersionUID = -2456848365524191614L;
    private final T           aliasProvider;
    private final String      alias;
    private final boolean     wrapInParentheses;

    AliasProviderImpl(Configuration configuration, T aliasProvider, String alias) {
        this(configuration, aliasProvider, alias, false);
    }

    AliasProviderImpl(Configuration configuration, T aliasProvider, String alias, boolean wrapInParentheses) {
        super(configuration, alias);

        this.aliasProvider = aliasProvider;
        this.alias = alias;
        this.wrapInParentheses = wrapInParentheses;
    }

    protected final T getAliasProvider() {
        return aliasProvider;
    }

    @Override
    public final String toSQLReference(Configuration configuration, boolean inlineParameters) {
        return alias;
    }

    @Override
    public final String toSQLDeclaration(Configuration configuration, boolean inlineParameters) {
        StringBuilder sb = new StringBuilder();

        if (wrapInParentheses) {
            sb.append("(");
        }

        sb.append(aliasProvider.getQueryPart().toSQLDeclaration(configuration, inlineParameters));

        if (wrapInParentheses) {
            sb.append(")");
        }

        switch (getDialect()) {

            // [#291] some aliases cause trouble, if they are not explicitly marked using "as"
            case POSTGRES:
            case HSQLDB:
            case DERBY:
            case MYSQL:
                sb.append(" as");
                break;
        }

        sb.append(" ");
        sb.append(alias);

        return sb.toString();
    }

    @Override
    public final int bind(Configuration configuration, PreparedStatement stmt, int initialIndex) throws SQLException {
        return aliasProvider.getQueryPart().bind(configuration, stmt, initialIndex);
    }

    @Override
    public final T as(String as) {
        return aliasProvider.as(as);
    }
}
