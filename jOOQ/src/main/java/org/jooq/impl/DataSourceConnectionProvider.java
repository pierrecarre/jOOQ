/**
 * Copyright (c) 2009-2012, Lukas Eder, lukas.eder@gmail.com
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

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jooq.ConnectionProvider;
import org.jooq.exception.DataAccessException;

/**
 * A default implementation for a pooled {@link DataSource}-oriented
 * {@link ConnectionProvider}
 * <p>
 * This implementation wraps a JDBC {@link DataSource}. jOOQ will use that data
 * source for initialising connections, and creating statements.
 * <p>
 * Use this connection provider if you want to run distributed transactions,
 * such as <code>javax.transaction.UserTransaction</code>. jOOQ will
 * {@link Connection#close() close()} all connections after query execution (and
 * result fetching) in order to return the connection to the connection pool. If
 * you do not use distributed transactions, this will produce driver-specific
 * behaviour at the end of query execution at <code>close()</code> invocation
 * (e.g. a transaction rollback). Use a {@link DefaultConnectionProvider}
 * instead, to control the connection's lifecycle, or implement your own
 * {@link ConnectionProvider}.
 *
 * @author Aaron Digulla
 * @author Lukas Eder
 */
public class DataSourceConnectionProvider implements ConnectionProvider {

    private final DataSource datasource;
    private Connection       connection;

    public DataSourceConnectionProvider(DataSource datasource) {
        this.datasource = datasource;
    }

    @Override
    public Connection acquire() {
        if (null == connection) {
            try {
                connection = datasource.getConnection();
            }
            catch (SQLException e) {
                throw new DataAccessException("Error getting connection from data source " + datasource, e);
            }
        }

        return connection;
    }

    @Override
    public void release(Connection released) {
        if (this.connection != released) {
            throw new IllegalArgumentException("Expected " + this.connection + " but got " + released);
        }

        try {
            connection.close();
            this.connection = null;
        }
        catch (SQLException e) {
            throw new DataAccessException("Error closing connection " + connection, e);
        }
    }
}