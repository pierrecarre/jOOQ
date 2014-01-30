/**
 * Copyright (c) 2009-2013, Data Geekery GmbH (http://www.datageekery.com)
 * All rights reserved.
 *
 * This work is dual-licensed
 * - under the Apache Software License 2.0 (the "ASL")
 * - under the jOOQ License and Maintenance Agreement (the "jOOQ License")
 * =============================================================================
 * You may choose which license applies to you:
 *
 * - If you're using this work with Open Source databases, you may choose
 *   either ASL or jOOQ License.
 * - If you're using this work with at least one commercial database, you must
 *   choose jOOQ License
 *
 * For more information, please visit http://www.jooq.org/licenses
 *
 * Apache Software License 2.0:
 * -----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * jOOQ License and Maintenance Agreement:
 * -----------------------------------------------------------------------------
 * Data Geekery grants the Customer the non-exclusive, timely limited and
 * non-transferable license to install and use the Software under the terms of
 * the jOOQ License and Maintenance Agreement.
 *
 * This library is distributed with a LIMITED WARRANTY. See the jOOQ License
 * and Maintenance Agreement for more details: http://www.jooq.org/licensing
 */
package org.jooq.tools.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

/**
 * A mock connection.
 * <p>
 * Mock connections can be used to supply jOOQ with unit test data, avoiding the
 * round-trip of using an actual in-memory test database, such as Derby, H2 or
 * HSQLDB. A usage example:
 * <p>
 * <code><pre>
 * MockDataProvider provider = new MockDataProvider() {
 *     public MockResult[] execute(MockExecuteContext context) throws SQLException {
 *         Result&lt;MyTableRecord> result = executor.newResult(MY_TABLE);
 *         result.add(executor.newRecord(MY_TABLE));
 *
 *         return new MockResult[] {
 *             new MockResult(1, result)
 *         };
 *     }
 * };
 * Connection connection = new MockConnection(provider);
 * DSLContext create = DSL.using(connection, dialect);
 * assertEquals(1, create.selectOne().fetch().size());
 * </pre></code>
 * <p>
 * While this <code>MockConnection</code> can be used independently of jOOQ, it
 * has been optimised for usage with jOOQ. JDBC features that are not used by
 * jOOQ (e.g. procedure bind value access by parameter name) are not supported
 * in this mock framework
 *
 * @author Lukas Eder
 */
public class MockConnection extends JDBC41Connection implements Connection {

    private final MockDataProvider data;
    private boolean                isClosed;

    public MockConnection(MockDataProvider data) {
        this.data = data;
    }

    // -------------------------------------------------------------------------
    // XXX: Utilities
    // -------------------------------------------------------------------------

    private void checkNotClosed() throws SQLException {
        if (isClosed) {
            throw new SQLException("Connection is already closed");
        }
    }

    // -------------------------------------------------------------------------
    // XXX: Creating statements
    // -------------------------------------------------------------------------

    
    public Statement createStatement() throws SQLException {
        return createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
    }

    
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return createStatement(resultSetType, resultSetConcurrency, ResultSet.CLOSE_CURSORS_AT_COMMIT);
    }

    
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
        throws SQLException {
        checkNotClosed();

        MockStatement result = new MockStatement(this, data);
        result.resultSetType = resultSetType;
        result.resultSetConcurrency = resultSetConcurrency;
        result.resultSetHoldability = resultSetHoldability;
        return result;
    }

    
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
    }

    
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return prepareStatement(sql, resultSetType, resultSetConcurrency, ResultSet.CLOSE_CURSORS_AT_COMMIT);
    }

    
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
        int resultSetHoldability) throws SQLException {
        checkNotClosed();

        MockStatement result = new MockStatement(this, data, sql);
        result.resultSetType = resultSetType;
        result.resultSetConcurrency = resultSetConcurrency;
        result.resultSetHoldability = resultSetHoldability;
        return result;
    }

    
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        checkNotClosed();

        MockStatement result = new MockStatement(this, data, sql);
        result.autoGeneratedKeys = autoGeneratedKeys;
        return result;
    }

    
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        checkNotClosed();

        MockStatement result = new MockStatement(this, data, sql);
        result.autoGeneratedKeys = Statement.RETURN_GENERATED_KEYS;
        result.columnIndexes = columnIndexes;
        return result;
    }

    
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        checkNotClosed();

        MockStatement result = new MockStatement(this, data, sql);
        result.autoGeneratedKeys = Statement.RETURN_GENERATED_KEYS;
        result.columnNames = columnNames;
        return result;
    }

    
    public CallableStatement prepareCall(String sql) throws SQLException {
        return prepareCall(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
    }

    
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return prepareCall(sql, resultSetType, resultSetConcurrency, ResultSet.CLOSE_CURSORS_AT_COMMIT);
    }

    
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
        int resultSetHoldability) throws SQLException {
        checkNotClosed();

        MockStatement result = new MockStatement(this, data, sql);
        result.resultSetType = resultSetType;
        result.resultSetConcurrency = resultSetConcurrency;
        result.resultSetHoldability = resultSetHoldability;
        return result;
    }

    // -------------------------------------------------------------------------
    // XXX: Ignored operations
    // -------------------------------------------------------------------------

    
    public void commit() throws SQLException {
        checkNotClosed();
    }

    
    public void rollback() throws SQLException {
        checkNotClosed();
    }

    
    public void rollback(Savepoint savepoint) throws SQLException {
        checkNotClosed();
    }

    
    public void close() throws SQLException {
        isClosed = true;
    }

    
    public Savepoint setSavepoint() throws SQLException {
        checkNotClosed();
        return null;
    }

    
    public Savepoint setSavepoint(String name) throws SQLException {
        checkNotClosed();
        return null;
    }

    
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        checkNotClosed();
    }

    
    public boolean isClosed() throws SQLException {
        return isClosed;
    }

    
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        checkNotClosed();
    }

    
    public boolean getAutoCommit() throws SQLException {
        checkNotClosed();
        return false;
    }

    
    public void setReadOnly(boolean readOnly) throws SQLException {
        checkNotClosed();
    }

    
    public boolean isReadOnly() throws SQLException {
        checkNotClosed();
        return false;
    }

    
    public void setCatalog(String catalog) throws SQLException {
        checkNotClosed();}

    
    public String getCatalog() throws SQLException {
        checkNotClosed();
        return null;
    }

    
    public SQLWarning getWarnings() throws SQLException {
        checkNotClosed();
        return null;
    }

    
    public void clearWarnings() throws SQLException {
        checkNotClosed();
    }

    
    public void setTransactionIsolation(int level) throws SQLException {
        checkNotClosed();
    }

    
    public int getTransactionIsolation() throws SQLException {
        checkNotClosed();
        return 0;
    }

    
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        checkNotClosed();
    }

    
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        checkNotClosed();
        return null;
    }

    
    public void setHoldability(int holdability) throws SQLException {
        checkNotClosed();
    }

    
    public int getHoldability() throws SQLException {
        checkNotClosed();
        return 0;
    }

    
    public boolean isValid(int timeout) throws SQLException {
        checkNotClosed();
        return false;
    }

    
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
    }

    
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
    }

    
    public String getClientInfo(String name) throws SQLException {
        checkNotClosed();
        return null;
    }

    
    public Properties getClientInfo() throws SQLException {
        checkNotClosed();
        return null;
    }

    // -------------------------------------------------------------------------
    // XXX: Unsupported operations
    // -------------------------------------------------------------------------

    
    public DatabaseMetaData getMetaData() throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported Operation");
    }

    
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported Operation");
    }

    
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported Operation");
    }

    
    public String nativeSQL(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported Operation");
    }

    
    public Clob createClob() throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported Operation");
    }

    
    public Blob createBlob() throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported Operation");
    }

    
    public NClob createNClob() throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported Operation");
    }

    
    public SQLXML createSQLXML() throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported Operation");
    }

    
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported Operation");
    }

    
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        throw new SQLFeatureNotSupportedException("Unsupported Operation");
    }
}
