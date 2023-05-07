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
package org.apache.openjpa.slice.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Map;

/**
 * A chain of ResultSet.
 * Assumes added ResultSet are identical in structure and fetches forward.
 * Can not move absolutely or change fetch direction.
 *
 * @author Pinaki Poddar
 *
 */
public class DistributedResultSet implements ResultSet {

	private LinkedList<ResultSet> comps = new LinkedList<>();
	private ResultSet current;
	private int cursor = -1;

	/**
	 * Adds the ResultSet only if it has rows.
	 */
	public void add(ResultSet rs) {
		try {
			if (rs.first()) {
				comps.add(rs);
				rs.beforeFirst();
			}
		} catch (SQLException e) {
			// ignore
		}
	}

	@Override
    public boolean absolute(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void afterLast() throws SQLException {
		current = null;
		cursor  = comps.size();
	}

	@Override
    public void beforeFirst() throws SQLException {
		current = null;
		cursor  = -1;
	}

	@Override
    public void cancelRowUpdates() throws SQLException {
		current.cancelRowUpdates();
	}

	@Override
    public void clearWarnings() throws SQLException {
		for (ResultSet rs : comps)
			rs.clearWarnings();
	}

	@Override
    public void close() throws SQLException {
		for (ResultSet rs : comps)
			rs.close();
	}

	@Override
    public void deleteRow() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public int findColumn(String arg0) throws SQLException {
		return current.findColumn(arg0);
	}

	@Override
    public boolean first() throws SQLException {
		if (comps.isEmpty())
			return false;
		cursor = 0;
		current = comps.getFirst();
		return true;
	}

	@Override
    public Array getArray(int arg0) throws SQLException {
		return current.getArray(arg0);
	}

	@Override
    public Array getArray(String arg0) throws SQLException {
		return current.getArray(arg0);
	}

	@Override
    public InputStream getAsciiStream(int arg0) throws SQLException {
		return current.getAsciiStream(arg0);
	}

	@Override
    public InputStream getAsciiStream(String arg0) throws SQLException {
		return current.getAsciiStream(arg0);
	}

	@Override
    public BigDecimal getBigDecimal(int arg0) throws SQLException {
		return current.getBigDecimal(arg0);
	}

	@Override
    public BigDecimal getBigDecimal(String arg0) throws SQLException {
		return current.getBigDecimal(arg0);
	}

    @Override
    public BigDecimal getBigDecimal(int arg0, int arg1) throws SQLException {
		return current.getBigDecimal(arg0, arg1);
	}

    @Override
    public BigDecimal getBigDecimal(String arg0, int arg1) throws SQLException {
		return current.getBigDecimal(arg0, arg1);
	}

	@Override
    public InputStream getBinaryStream(int arg0) throws SQLException {
		return current.getBinaryStream(arg0);
	}

	@Override
    public InputStream getBinaryStream(String arg0) throws SQLException {
		return current.getBinaryStream(arg0);
	}

	@Override
    public Blob getBlob(int arg0) throws SQLException {
		return current.getBlob(arg0);
	}

	@Override
    public Blob getBlob(String arg0) throws SQLException {
		return current.getBlob(arg0);
	}

	@Override
    public boolean getBoolean(int arg0) throws SQLException {
		return current.getBoolean(arg0);
	}

	@Override
    public boolean getBoolean(String arg0) throws SQLException {
		return current.getBoolean(arg0);
	}

	@Override
    public byte getByte(int arg0) throws SQLException {
		return current.getByte(arg0);
	}

	@Override
    public byte getByte(String arg0) throws SQLException {
		return current.getByte(arg0);
	}

	@Override
    public byte[] getBytes(int arg0) throws SQLException {
		return current.getBytes(arg0);
	}

	@Override
    public byte[] getBytes(String arg0) throws SQLException {
		return current.getBytes(arg0);
	}

	@Override
    public Reader getCharacterStream(int arg0) throws SQLException {
		return current.getCharacterStream(arg0);
	}

	@Override
    public Reader getCharacterStream(String arg0) throws SQLException {
		return current.getCharacterStream(arg0);
	}

	@Override
    public Clob getClob(int arg0) throws SQLException {
		return current.getClob(arg0);
	}

	@Override
    public Clob getClob(String arg0) throws SQLException {
		return current.getClob(arg0);
	}

	@Override
    public int getConcurrency() throws SQLException {
		return current.getConcurrency();
	}

	@Override
    public String getCursorName() throws SQLException {
		return current.getCursorName();
	}

	@Override
    public Date getDate(int arg0) throws SQLException {
		return current.getDate(arg0);
	}

	@Override
    public Date getDate(String arg0) throws SQLException {
		return current.getDate(arg0);
	}

	@Override
    public Date getDate(int arg0, Calendar arg1) throws SQLException {
		return current.getDate(arg0, arg1);
	}

	@Override
    public Date getDate(String arg0, Calendar arg1) throws SQLException {
		return current.getDate(arg0, arg1);
	}

	@Override
    public double getDouble(int arg0) throws SQLException {
		return current.getDouble(arg0);
	}

	@Override
    public double getDouble(String arg0) throws SQLException {
		return current.getDouble(arg0);
	}

	@Override
    public int getFetchDirection() throws SQLException {
		return current.getFetchDirection();
	}

	@Override
    public int getFetchSize() throws SQLException {
		return current.getFetchSize();
	}

	@Override
    public float getFloat(int arg0) throws SQLException {
		return current.getFloat(arg0);
	}

	@Override
    public float getFloat(String arg0) throws SQLException {
		return current.getFloat(arg0);
	}

	@Override
    public int getInt(int arg0) throws SQLException {
		return current.getInt(arg0);
	}

	@Override
    public int getInt(String arg0) throws SQLException {
		return current.getInt(arg0);
	}

	@Override
    public long getLong(int arg0) throws SQLException {
		return current.getLong(arg0);
	}

	@Override
    public long getLong(String arg0) throws SQLException {
		return current.getLong(arg0);
	}

	@Override
    public ResultSetMetaData getMetaData() throws SQLException {
		return current.getMetaData();
	}

	@Override
    public Object getObject(int arg0) throws SQLException {
		return current.getObject(arg0);
	}

	@Override
    public Object getObject(String arg0) throws SQLException {
		return current.getObject(arg0);
	}

	@Override
    public Object getObject(int arg0, Map<String, Class<?>> arg1)
			throws SQLException {
		return current.getObject(arg0, arg1);
	}

	@Override
    public Object getObject(String arg0, Map<String, Class<?>> arg1)
			throws SQLException {
		return current.getObject(arg0, arg1);
	}

	@Override
    public Ref getRef(int arg0) throws SQLException {
		return current.getRef(arg0);
	}

	@Override
    public Ref getRef(String arg0) throws SQLException {
		return current.getRef(arg0);
	}

	@Override
    public int getRow() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public short getShort(int arg0) throws SQLException {
		return current.getShort(arg0);
	}

	@Override
    public short getShort(String arg0) throws SQLException {
		return current.getShort(arg0);
	}

	@Override
    public Statement getStatement() throws SQLException {
		return current.getStatement();
	}

	@Override
    public String getString(int arg0) throws SQLException {
		return current.getString(arg0);
	}

	@Override
    public String getString(String arg0) throws SQLException {
		return current.getString(arg0);
	}

	@Override
    public Time getTime(int arg0) throws SQLException {
		return current.getTime(arg0);
	}

	@Override
    public Time getTime(String arg0) throws SQLException {
		return current.getTime(arg0);
	}

	@Override
    public Time getTime(int arg0, Calendar arg1) throws SQLException {
		return current.getTime(arg0, arg1);
	}

	@Override
    public Time getTime(String arg0, Calendar arg1) throws SQLException {
		return current.getTime(arg0, arg1);
	}

	@Override
    public Timestamp getTimestamp(int arg0) throws SQLException {
		return current.getTimestamp(arg0);
	}

	@Override
    public Timestamp getTimestamp(String arg0) throws SQLException {
		return current.getTimestamp(arg0);
	}

    @Override
    public Timestamp getTimestamp(int arg0, Calendar arg1) throws SQLException {
		return current.getTimestamp(arg0, arg1);
	}

	@Override
    public Timestamp getTimestamp(String arg0, Calendar arg1)
			throws SQLException {
		return current.getTimestamp(arg0, arg1);
	}

	@Override
    public int getType() throws SQLException {
		return current.getType();
	}

	@Override
    public URL getURL(int arg0) throws SQLException {
		return current.getURL(arg0);
	}

	@Override
    public URL getURL(String arg0) throws SQLException {
		return current.getURL(arg0);
	}

	@Override
    public InputStream getUnicodeStream(int arg0) throws SQLException {
		return current.getUnicodeStream(arg0);
	}

	@Override
    public InputStream getUnicodeStream(String arg0) throws SQLException {
		return current.getUnicodeStream(arg0);
	}

	@Override
    public SQLWarning getWarnings() throws SQLException {
		return current.getWarnings();
	}

	@Override
    public void insertRow() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public boolean isAfterLast() throws SQLException {
		return current == null && cursor >= comps.size();
	}

	@Override
    public boolean isBeforeFirst() throws SQLException {
		return current == null && cursor < 0;
	}

	@Override
    public boolean isFirst() throws SQLException {
		return current != null && current.isFirst() && cursor == 0;
	}

	@Override
    public boolean isLast() throws SQLException {
        return current != null && current.isLast() && cursor == comps.size()-1;
	}

	@Override
    public boolean last() throws SQLException {
		if (comps.isEmpty())
			return false;
		cursor = comps.size()-1;
		current = comps.getLast();
		return current.last();
	}

	@Override
    public void moveToCurrentRow() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void moveToInsertRow() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public boolean next() throws SQLException {
		if (comps.isEmpty())
			return false;
		if (current == null) {
			current = comps.getFirst();
			cursor = 0;
		}
		if (current.next())
			return true;
		cursor++;
		if (cursor < comps.size()) {
			current = comps.get(cursor);
			return current.first();
		}
		return false;
	}

	@Override
    public boolean previous() throws SQLException {
		if (comps.isEmpty())
			return false;
		if (current.previous())
			return true;
		cursor--;
		if (cursor >= 0) {
			current = comps.get(cursor);
			return current.last();
		}
		return false;
	}

	@Override
    public void refreshRow() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public boolean relative(int arg0) throws SQLException {
		if (arg0 == 0)
			return current != null;
		boolean forward = arg0 > 0;
		for (int i = 0; i < arg0; i++) {
			if (forward) {
				if (!next()) {
					return false;
				}
			} else {
				if (!previous()) {
					return false;
				}
			}
		}
		return false;
	}

	@Override
    public boolean rowDeleted() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public boolean rowInserted() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public boolean rowUpdated() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void setFetchDirection(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void setFetchSize(int arg0) throws SQLException {
		for (ResultSet rs : comps)
			rs.setFetchSize(arg0);
	}

	@Override
    public void updateArray(int arg0, Array arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateArray(String arg0, Array arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateAsciiStream(int arg0, InputStream arg1, int arg2)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateAsciiStream(String arg0, InputStream arg1, int arg2)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

    @Override
    public void updateBigDecimal(int arg0, BigDecimal arg1) throws SQLException
    {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateBigDecimal(String arg0, BigDecimal arg1)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateBinaryStream(int arg0, InputStream arg1, int arg2)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateBinaryStream(String arg0, InputStream arg1, int arg2)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateBlob(int arg0, Blob arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateBlob(String arg0, Blob arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateBoolean(int arg0, boolean arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

    @Override
    public void updateBoolean(String arg0, boolean arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateByte(int arg0, byte arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateByte(String arg0, byte arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateBytes(int arg0, byte[] arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateBytes(String arg0, byte[] arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateCharacterStream(int arg0, Reader arg1, int arg2)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateCharacterStream(String arg0, Reader arg1, int arg2)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateClob(int arg0, Clob arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateClob(String arg0, Clob arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateDate(int arg0, Date arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateDate(String arg0, Date arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateDouble(int arg0, double arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateDouble(String arg0, double arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateFloat(int arg0, float arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateFloat(String arg0, float arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateInt(int arg0, int arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateInt(String arg0, int arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateLong(int arg0, long arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateLong(String arg0, long arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateNull(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateNull(String arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateObject(int arg0, Object arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateObject(String arg0, Object arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateObject(int arg0, Object arg1, int arg2)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateObject(String arg0, Object arg1, int arg2)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateRef(int arg0, Ref arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateRef(String arg0, Ref arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateRow() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateShort(int arg0, short arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateShort(String arg0, short arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateString(int arg0, String arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateString(String arg0, String arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateTime(int arg0, Time arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateTime(String arg0, Time arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

    @Override
    public void updateTimestamp(int arg0, Timestamp arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateTimestamp(String arg0, Timestamp arg1)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public boolean wasNull() throws SQLException {
		return current.wasNull();
	}
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public int getHoldability() throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public Reader getNCharacterStream(int arg0) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public Reader getNCharacterStream(String arg0) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public NClob getNClob(int arg0) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public NClob getNClob(String arg0) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public String getNString(int arg0) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public String getNString(String arg0) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public RowId getRowId(int arg0) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public RowId getRowId(String arg0) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public SQLXML getSQLXML(int arg0) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public SQLXML getSQLXML(String arg0) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public boolean isClosed() throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateAsciiStream(int arg0, InputStream arg1)
        throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateAsciiStream(String arg0, InputStream arg1)
        throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateAsciiStream(int arg0, InputStream arg1, long arg2)
        throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateAsciiStream(String arg0, InputStream arg1, long arg2)
        throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateBinaryStream(int arg0, InputStream arg1)
        throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateBinaryStream(String arg0, InputStream arg1)
        throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateBinaryStream(int arg0, InputStream arg1, long arg2)
        throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateBinaryStream(String arg0, InputStream arg1, long arg2)
        throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateBlob(int arg0, InputStream arg1) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateBlob(String arg0, InputStream arg1) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateBlob(int arg0, InputStream arg1, long arg2)
        throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateBlob(String arg0, InputStream arg1, long arg2)
        throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateCharacterStream(int arg0, Reader arg1)
        throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateCharacterStream(String arg0, Reader arg1)
        throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateCharacterStream(int arg0, Reader arg1, long arg2)
        throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateCharacterStream(String arg0, Reader arg1, long arg2)
        throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateClob(int arg0, Reader arg1) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateClob(String arg0, Reader arg1) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateClob(int arg0, Reader arg1, long arg2)
        throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateClob(String arg0, Reader arg1, long arg2)
        throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateNCharacterStream(int arg0, Reader arg1)
        throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateNCharacterStream(String arg0, Reader arg1)
        throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateNCharacterStream(int arg0, Reader arg1, long arg2)
        throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateNCharacterStream(String arg0, Reader arg1, long arg2)
        throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateNClob(int arg0, NClob arg1) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateNClob(String arg0, NClob arg1) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateNClob(int arg0, Reader arg1) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateNClob(String arg0, Reader arg1) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateNClob(int arg0, Reader arg1, long arg2)
        throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateNClob(String arg0, Reader arg1, long arg2)
        throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateNString(int arg0, String arg1) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateNString(String arg0, String arg1) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateRowId(int arg0, RowId arg1) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateRowId(String arg0, RowId arg1) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateSQLXML(int arg0, SQLXML arg1) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public void updateSQLXML(String arg0, SQLXML arg1) throws SQLException {
        throw new UnsupportedOperationException();
    }

    // Java 7 methods follow

    @Override
    public <T>T getObject(String columnLabel, Class<T> type) throws SQLException{
    	throw new UnsupportedOperationException();
    }

    @Override
    public <T>T getObject(int columnIndex, Class<T> type) throws SQLException{
    	throw new UnsupportedOperationException();
    }
}
