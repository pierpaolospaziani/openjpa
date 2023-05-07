/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openjpa.lib.jdbc;

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
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.apache.openjpa.lib.util.Closeable;


/**
 * Wrapper around an existing connection. Subclasses can override the
 * methods whose behavior they mean to change. The <code>equals</code> and
 * <code>hashCode</code> methods pass through to the base underlying data
 * store connection.
 *
 * @author Abe White
 */
public class DelegatingConnection implements Connection, Closeable {

    private final Connection _conn;
    private final DelegatingConnection _del;

    public DelegatingConnection(Connection conn) {
        _conn = conn;
        if (conn instanceof DelegatingConnection)
            _del = (DelegatingConnection) _conn;
        else
            _del = null;
    }

    /**
     * Return the wrapped connection.
     */
    public Connection getDelegate() {
        return _conn;
    }

    /**
     * Return the base underlying data store connection.
     */
    public Connection getInnermostDelegate() {
        return (_del == null) ? _conn : _del.getInnermostDelegate();
    }

    @Override
    public int hashCode() {
        return getInnermostDelegate().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (other instanceof DelegatingConnection)
            other = ((DelegatingConnection) other).getInnermostDelegate();
        return getInnermostDelegate().equals(other);
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("conn ").append(hashCode());
        appendInfo(buf);
        return buf.toString();
    }

    protected void appendInfo(StringBuffer buf) {
        if (_del != null)
            _del.appendInfo(buf);
    }

    @Override
    public Statement createStatement() throws SQLException {
        return createStatement(true);
    }

    /**
     * Create a statement, with the option of not wrapping it in a
     * {@link DelegatingStatement}, which is the default.
     */
    protected Statement createStatement(boolean wrap) throws SQLException {
        Statement stmnt;
        if (_del != null)
            stmnt = _del.createStatement(false);
        else
            stmnt = _conn.createStatement();
        if (wrap)
            stmnt = new DelegatingStatement(stmnt, this);
        return stmnt;
    }

    @Override
    public PreparedStatement prepareStatement(String str) throws SQLException {
        return prepareStatement(str, true);
    }

    /**
     * Prepare a statement, with the option of not wrapping it in a
     * {@link DelegatingPreparedStatement}, which is the default.
     */
    protected PreparedStatement prepareStatement(String str, boolean wrap)
        throws SQLException {
        PreparedStatement stmnt;
        if (_del != null)
            stmnt = _del.prepareStatement(str, false);
        else
            stmnt = _conn.prepareStatement(str, ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);
        if (wrap)
            stmnt = new DelegatingPreparedStatement(stmnt, this);
        return stmnt;
    }

    @Override
    public CallableStatement prepareCall(String str) throws SQLException {
        return prepareCall(str, true);
    }

    /**
     * Prepare a call, with the option of not wrapping it in a
     * {@link DelegatingCallableStatement}, which is the default.
     */
    protected CallableStatement prepareCall(String str, boolean wrap)
        throws SQLException {
        CallableStatement stmnt;
        if (_del != null)
            stmnt = _del.prepareCall(str, false);
        else
            stmnt = _conn.prepareCall(str);
        if (wrap)
            stmnt = new DelegatingCallableStatement(stmnt, this);
        return stmnt;
    }

    @Override
    public String nativeSQL(String str) throws SQLException {
        return _conn.nativeSQL(str);
    }

    @Override
    public void setAutoCommit(boolean bool) throws SQLException {
        _conn.setAutoCommit(bool);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return _conn.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        _conn.commit();
    }

    @Override
    public void rollback() throws SQLException {
        _conn.rollback();
    }

    @Override
    public void close() throws SQLException {
        _conn.close();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return _conn.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return getMetaData(true);
    }

    /**
     * Return the metadata, with the option of not wrapping it in a
     * {@link DelegatingDatabaseMetaData}, which is the default.
     */
    protected DatabaseMetaData getMetaData(boolean wrap) throws SQLException {
        DatabaseMetaData meta;
        if (_del != null)
            meta = _del.getMetaData(false);
        else
            meta = _conn.getMetaData();
        if (wrap)
            meta = new DelegatingDatabaseMetaData(meta, this);
        return meta;
    }

    @Override
    public void setReadOnly(boolean bool) throws SQLException {
        _conn.setReadOnly(bool);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return _conn.isReadOnly();
    }

    @Override
    public void setCatalog(String str) throws SQLException {
        _conn.setCatalog(str);
    }

    @Override
    public String getCatalog() throws SQLException {
        return _conn.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int i) throws SQLException {
        _conn.setTransactionIsolation(i);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return _conn.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return _conn.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        _conn.clearWarnings();
    }

    @Override
    public Statement createStatement(int type, int concur) throws SQLException {
        return createStatement(type, concur, true);
    }

    /**
     * Create a statement, with the option of not wrapping it in a
     * {@link DelegatingStatement}, which is the default.
     */
    protected Statement createStatement(int type, int concur, boolean wrap)
        throws SQLException {
        Statement stmnt;
        if (_del != null)
            stmnt = _del.createStatement(type, concur, false);
        else
            stmnt = _conn.createStatement(type, concur);
        if (wrap)
            stmnt = new DelegatingStatement(stmnt, this);
        return stmnt;
    }

    @Override
    public PreparedStatement prepareStatement(String str, int type, int concur)
        throws SQLException {
        return prepareStatement(str, type, concur, true);
    }

    /**
     * Prepare a statement, with the option of not wrapping it in a
     * {@link DelegatingPreparedStatement}, which is the default.
     */
    protected PreparedStatement prepareStatement(String str, int type,
        int concur, boolean wrap) throws SQLException {
        PreparedStatement stmnt;
        if (_del != null)
            stmnt = _del.prepareStatement(str, type, concur, false);
        else
            stmnt = _conn.prepareStatement(str, type, concur);
        if (wrap)
            stmnt = new DelegatingPreparedStatement(stmnt, this);
        return stmnt;
    }

    @Override
    public CallableStatement prepareCall(String str, int type, int concur)
        throws SQLException {
        return prepareCall(str, type, concur, true);
    }

    /**
     * Prepare a call, with the option of not wrapping it in a
     * {@link DelegatingCallableStatement}, which is the default.
     */
    protected CallableStatement prepareCall(String str, int type, int concur,
        boolean wrap) throws SQLException {
        CallableStatement stmnt;
        if (_del != null)
            stmnt = _del.prepareCall(str, type, concur, false);
        else
            stmnt = _conn.prepareCall(str, type, concur);
        if (wrap)
            stmnt = new DelegatingCallableStatement(stmnt, this);
        return stmnt;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return _conn.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        _conn.setTypeMap(map);
    }

    // JDBC 3.0 methods follow.

    @Override
    public void setHoldability(int holdability) throws SQLException {
        _conn.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return _conn.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return _conn.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String savepoint) throws SQLException {
        return _conn.setSavepoint(savepoint);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        _conn.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        _conn.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType,
        int resultSetConcurrency, int resultSetHoldability)
        throws SQLException {
        return createStatement(resultSetType, resultSetConcurrency,
            resultSetHoldability, true);
    }

    protected Statement createStatement(int resultSetType,
        int resultSetConcurrency, int resultSetHoldability, boolean wrap)
        throws SQLException {
        Statement stmnt;
        if (_del != null)
            stmnt = _del.createStatement(resultSetType, resultSetConcurrency,
                resultSetHoldability, false);
        else {
            stmnt = _conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        if (wrap)
            stmnt = new DelegatingStatement(stmnt, this);
        return stmnt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql,
        int resultSetType, int resultSetConcurrency, int resultSetHoldability)
        throws SQLException {
        return prepareStatement(sql, resultSetType, resultSetConcurrency,
            resultSetHoldability, true);
    }

    protected PreparedStatement prepareStatement(String sql,
        int resultSetType, int resultSetConcurrency, int resultSetHoldability,
        boolean wrap) throws SQLException {
        PreparedStatement stmnt;
        if (_del != null)
            stmnt = _del.prepareStatement(sql, resultSetType,
                resultSetConcurrency, resultSetHoldability, false);
        else {
            stmnt = _conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        if (wrap)
            stmnt = new DelegatingPreparedStatement(stmnt, this);
        return stmnt;
    }

    @Override
    public CallableStatement prepareCall(String sql,
        int resultSetType, int resultSetConcurrency, int resultSetHoldability)
        throws SQLException {
        return prepareCall(sql, resultSetType, resultSetConcurrency,
            resultSetHoldability, true);
    }

    protected CallableStatement prepareCall(String sql, int resultSetType,
        int resultSetConcurrency, int resultSetHoldability, boolean wrap)
        throws SQLException {
        CallableStatement stmnt;
        if (_del != null)
            stmnt = _del.prepareCall(sql, resultSetType,
                resultSetConcurrency, resultSetHoldability, false);
        else {
            stmnt = _conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        if (wrap)
            stmnt = new DelegatingCallableStatement(stmnt, this);
        return stmnt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
        throws SQLException {
        return prepareStatement(sql, autoGeneratedKeys, true);
    }

    protected PreparedStatement prepareStatement(String sql,
        int autoGeneratedKeys, boolean wrap) throws SQLException {
        PreparedStatement stmnt;
        if (_del != null)
            stmnt = _del.prepareStatement(sql, autoGeneratedKeys);
        else {
            stmnt = _conn.prepareStatement(sql, autoGeneratedKeys);
        }
        if (wrap)
            stmnt = new DelegatingPreparedStatement(stmnt, this);
        return stmnt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
        throws SQLException {
        return prepareStatement(sql, columnIndexes, true);
    }

    protected PreparedStatement prepareStatement(String sql,
        int[] columnIndexes, boolean wrap) throws SQLException {
        PreparedStatement stmnt;
        if (_del != null)
            stmnt = _del.prepareStatement(sql, columnIndexes, wrap);
        else {
            stmnt = _conn.prepareStatement(sql, columnIndexes);
        }
        if (wrap)
            stmnt = new DelegatingPreparedStatement(stmnt, this);
        return stmnt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames)
        throws SQLException {
        return prepareStatement(sql, columnNames, true);
    }

    protected PreparedStatement prepareStatement(String sql,
        String[] columnNames, boolean wrap) throws SQLException {
        PreparedStatement stmnt;
        if (_del != null)
            stmnt = _del.prepareStatement(sql, columnNames, wrap);
        else {
            stmnt = _conn.prepareStatement(sql, columnNames);
        }
        if (wrap)
            stmnt = new DelegatingPreparedStatement(stmnt, this);
        return stmnt;
    }

    //  JDBC 4.0 methods follow.

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(getDelegate().getClass());
    }

    /**
     * From java.sql.Wrapper javadoc:
     *
     * Returns an object that implements the given interface to allow access to
     * non-standard methods, or standard methods not exposed by the proxy. If
     * the receiver implements the interface then the result is the receiver
     * or a proxy for the receiver. If the receiver is a wrapper and the
     * wrapped object implements the interface then the result is the wrapped
     * object or a proxy for the wrapped object. Otherwise return the the
     * result of calling unwrap recursively on the wrapped object or a proxy
     * for that result. If the receiver is not a wrapper and does not implement
     * the interface, then an SQLException is thrown.
     *
     */
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (isWrapperFor(iface)) {
            return (T) getDelegate();
        } else {
            return getDelegate().unwrap(iface);
        }
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return _conn.createArrayOf(typeName, elements);
    }

    @Override
    public Blob createBlob() throws SQLException {
        return _conn.createBlob();
    }

    @Override
    public Clob createClob() throws SQLException {
        return _conn.createClob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return _conn.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return _conn.createSQLXML();
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return _conn.createStruct(typeName, attributes);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return _conn.getClientInfo();
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return _conn.getClientInfo(name);
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return _conn.isValid(timeout);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        _conn.setClientInfo(properties);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        _conn.setClientInfo(name, value);
    }

    // Java 7 methods follow

    @Override
    public void abort(Executor executor) throws SQLException {
    	throw new UnsupportedOperationException();
    }

    @Override
    public int getNetworkTimeout() throws SQLException{
    	throw new UnsupportedOperationException();
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException{
    	throw new UnsupportedOperationException();
    }

    @Override
    public String getSchema() throws SQLException {
    	throw new UnsupportedOperationException();
    }

    @Override
    public void setSchema(String schema)throws SQLException {
    	throw new UnsupportedOperationException();
    }
}
