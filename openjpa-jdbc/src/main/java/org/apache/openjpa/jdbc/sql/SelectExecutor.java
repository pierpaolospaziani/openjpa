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
package org.apache.openjpa.jdbc.sql;

import java.sql.SQLException;

import org.apache.openjpa.jdbc.conf.JDBCConfiguration;
import org.apache.openjpa.jdbc.kernel.JDBCFetchConfiguration;
import org.apache.openjpa.jdbc.kernel.JDBCStore;

/**
 * Interface for configuring and executing a SQL select.
 *
 * @author Abe White
 */
public interface SelectExecutor {

    /**
     * Return the select configuration.
     */
    JDBCConfiguration getConfiguration();

    /**
     * Return this select as a SQL statement formatted for the current
     * dictionary.
     */
    SQLBuffer toSelect(boolean forUpdate, JDBCFetchConfiguration fetch);

    /**
     * Get the buffer if it has been computed by a previous call to
     * {@link #toSelect(boolean, JDBCFetchConfiguration)}, if any.
     *
     * @since 2.0.0
     */
    SQLBuffer getSQL();

    /**
     * Return this select as a COUNT SQL statement formatted for the current
     * dictionary.
     */
    SQLBuffer toSelectCount();

    /**
     * Whether to automatically make results distinct when relational joins
     * would otherwise introduce duplicates.
     */
    boolean getAutoDistinct();

    /**
     * Whether to automatically make results distinct when relational joins
     * would otherwise introduce duplicates.
     */
    void setAutoDistinct(boolean distinct);

    /**
     * Whether this is a SELECT DISTINCT / UNION ALL.
     */
    boolean isDistinct();

    /**
     * Whether this is a SELECT DISTINCT / UNION ALL.
     */
    void setDistinct(boolean distinct);

    /**
     * Whether the result of this select should be treated as a large
     * result set.
     */
    boolean isLRS();

    /**
     * Whether the result of this select should be treated as a large
     * result set.
     */
    void setLRS(boolean lrs);

    /**
     * The expected result count for the query.
     */
    int getExpectedResultCount();

    /**
     * The expected result count for the query.
     *
     * @param force if false, the count will be discarded if this select has
     * any to-many eager joins that would throw off the result count
     */
    void setExpectedResultCount(int expectedResultCount, boolean force);

    /**
     * The join syntax for this select, as one of the syntax constants from
     * {@link JoinSyntaxes}.
     */
    int getJoinSyntax();

    /**
     * The join syntax for this select, as one of the syntax constants from
     * {@link JoinSyntaxes}.
     */
    void setJoinSyntax(int joinSyntax);

    /**
     * Return whether this select can support a random access result set type.
     */
    boolean supportsRandomAccess(boolean forUpdate);

    /**
     * Whether this select can be executed for update.
     */
    boolean supportsLocking();

    /**
     * Return the number of instances matching this select.
     */
    int getCount(JDBCStore store)
        throws SQLException;

    /**
     * Execute this select in the context of the given store manager.
     */
    Result execute(JDBCStore store, JDBCFetchConfiguration fetch)
        throws SQLException;

    /**
     * Execute this select in the context of the given store manager.
     */
    Result execute(JDBCStore store, JDBCFetchConfiguration fetch,
        int lockLevel)
        throws SQLException;

    /**
     * Affirm if this receiver requires more than one selects to fetch its
     * data.
     *
     * @since 2.0.0
     */
    boolean hasMultipleSelects();
}
