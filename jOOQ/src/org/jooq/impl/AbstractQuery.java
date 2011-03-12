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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jooq.Configuration;
import org.jooq.Query;
import org.jooq.Record;

/**
 * @author Lukas Eder
 */
abstract class AbstractQuery<R extends Record> extends AbstractQueryPart implements Query {

    private static final long       serialVersionUID = -8046199737354507547L;
    private static final JooqLogger log              = JooqLogger.getLogger(AbstractQuery.class);

    AbstractQuery(Configuration configuration) {
        super(configuration);
    }

    @Override
    public final int execute() throws SQLException {
        if (getConfiguration().getConnection() != null) {
            return execute(getConfiguration().getConnection());
        }
        else {
            throw new SQLException("Cannot execute query. No Connection configured");
        }
    }

    final int execute(Connection connection) throws SQLException {
        PreparedStatement statement = null;

        if (log.isDebugEnabled()) {
            log.debug("Executing query : " + this);
        }

        try {
            statement = connection.prepareStatement(toSQLReference(getConfiguration()));
            bind(getConfiguration(), statement);
            return execute(statement);
        }
        finally {
            SQLUtils.safeClose(statement);
        }
    }

    @Override
    public final Configuration getConfiguration() {
        return super.getConfiguration();
    }

    /**
     * Default implementation for query execution. Subclasses may override this
     * method.
     */
    protected int execute(PreparedStatement statement) throws SQLException {
        return statement.executeUpdate();
    }

    @Override
    Factory create() {
        return Factory.getFactory(getConfiguration());
    }
}
