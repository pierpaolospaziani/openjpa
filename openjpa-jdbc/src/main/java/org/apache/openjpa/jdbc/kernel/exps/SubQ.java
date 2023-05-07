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
package org.apache.openjpa.jdbc.kernel.exps;

import java.sql.SQLException;

import org.apache.openjpa.jdbc.conf.JDBCConfiguration;
import org.apache.openjpa.jdbc.kernel.EagerFetchModes;
import org.apache.openjpa.jdbc.kernel.JDBCStoreQuery;
import org.apache.openjpa.jdbc.meta.ClassMapping;
import org.apache.openjpa.jdbc.meta.JavaSQLTypes;
import org.apache.openjpa.jdbc.sql.Result;
import org.apache.openjpa.jdbc.sql.SQLBuffer;
import org.apache.openjpa.jdbc.sql.Select;
import org.apache.openjpa.kernel.Filters;
import org.apache.openjpa.kernel.exps.ExpressionVisitor;
import org.apache.openjpa.kernel.exps.QueryExpressions;
import org.apache.openjpa.kernel.exps.Subquery;
import org.apache.openjpa.kernel.exps.Value;
import org.apache.openjpa.meta.ClassMetaData;

/**
 * A subquery.
 *
 * @author Abe White
 */
public class SubQ
    extends AbstractVal
    implements Subquery {

    
    private static final long serialVersionUID = 1L;
    private final ClassMapping _candidate;
    private final boolean _subs;
    private String _subqAlias;
    private final SelectConstructor _cons = new SelectConstructor();

    private Class _type = null;
    private ClassMetaData _meta = null;
    private QueryExpressions _exps = null;
    private Select _select = null;

    /**
     * Constructor. Supply candidate, whether subclasses are included in
     * the query, and the query alias.
     */
    public SubQ(ClassMapping candidate, boolean subs, String alias) {
        _candidate = candidate;
        _subs = subs;
        _subqAlias = alias;
        _select = (((JDBCConfiguration) candidate.getMappingRepository().
            getConfiguration()).getSQLFactoryInstance().newSelect());
        _cons.setSubselect(_select);
    }

    @Override
    public Object getSelect() {
        return _select;
    }

    /**
     * Return the subquery candidate type.
     */
    public ClassMapping getCandidate() {
        return _candidate;
    }

    public boolean getSubs() {
        return _subs;
    }

    @Override
    public void setSubqAlias(String subqAlias) {
        _subqAlias = subqAlias;
    }

    @Override
    public String getSubqAlias() {
        return _subqAlias;
    }

    @Override
    public Class getType() {
        if (_exps != null && _type == null) {
            if (_exps.projections.length == 0)
                return _candidate.getDescribedType();
            if (_exps.projections.length == 1)
                return _exps.projections[0].getType();
        }
        return _type;
    }

    @Override
    public void setImplicitType(Class type) {
        if (_exps != null && _exps.projections.length == 1)
            _exps.projections[0].setImplicitType(type);
        _type = type;
    }

    @Override
    public ClassMetaData getMetaData() {
        return _meta;
    }

    @Override
    public void setMetaData(ClassMetaData meta) {
        _meta = meta;
    }

    @Override
    public String getCandidateAlias() {
        return _subqAlias;
    }

    @Override
    public void setQueryExpressions(QueryExpressions query) {
        _exps = query;
        _select.setContext(query.ctx());
    }

    @Override
    public ExpState initialize(Select sel, ExpContext ctx, int flags) {
        Select select = JDBCStoreQuery.getThreadLocalSelect(_select);
        select.setParent(sel, null);
        if (_exps.projections.length == 1) {
            return ((Val) _exps.projections[0]).initialize(select, ctx, flags);
        }
        return ExpState.NULL;
    }

    @Override
    public Object toDataStoreValue(Select sel, ExpContext ctx, ExpState state,
        Object val) {
        if (_exps.projections.length == 0)
            return _candidate.toDataStoreValue(val,
                _candidate.getPrimaryKeyColumns(), ctx.store);
        if (_exps.projections.length == 1)
            return ((Val) _exps.projections[0]).toDataStoreValue(sel, ctx,
                state, val);
        return val;
    }

    @Override
    public void select(Select sel, ExpContext ctx, ExpState state,
        boolean pks) {
        selectColumns(sel, ctx, state, pks);
    }

    @Override
    public void selectColumns(Select sel, ExpContext ctx, ExpState state,
        boolean pks) {
        sel.select(newSQLBuffer(sel, ctx, state), this);
    }

    @Override
    public void groupBy(Select sel, ExpContext ctx, ExpState state) {
        sel.groupBy(newSQLBuffer(sel, ctx, state));
    }

    @Override
    public void orderBy(Select sel, ExpContext ctx, ExpState state,
        boolean asc) {
        sel.orderBy(newSQLBuffer(sel, ctx, state), asc, false, getSelectAs());
    }

    private SQLBuffer newSQLBuffer(Select sel, ExpContext ctx, ExpState state) {
        SQLBuffer buf = new SQLBuffer(ctx.store.getDBDictionary());
        appendTo(sel, ctx, state, buf, 0);
        return buf;
    }

    @Override
    public Object load(ExpContext ctx, ExpState state, Result res)
        throws SQLException {
        return Filters.convert(res.getObject(this,
            JavaSQLTypes.JDBC_DEFAULT, null), getType());
    }

    @Override
    public void calculateValue(Select sel, ExpContext ctx, ExpState state,
        Val other, ExpState otherState) {
        Value[] projs = _exps.projections;
        for (Value proj : projs) {
            if (proj instanceof GeneralCaseExpression) {
                ((GeneralCaseExpression) proj).setOtherPath(other);
                ((GeneralCaseExpression) proj).setOtherState(otherState);
            }
            else if (proj instanceof SimpleCaseExpression) {
                ((SimpleCaseExpression) proj).setOtherPath(other);
                ((SimpleCaseExpression) proj).setOtherState(otherState);
            }
            else if (proj instanceof NullIfExpression) {
                ((NullIfExpression) proj).setOtherPath(other);
                ((NullIfExpression) proj).setOtherState(otherState);
            }
            else if (proj instanceof CoalesceExpression) {
                ((CoalesceExpression) proj).setOtherPath(other);
                ((CoalesceExpression) proj).setOtherState(otherState);
            }
        }
    }

    @Override
    public int length(Select sel, ExpContext ctx, ExpState state) {
        return 1;
    }

    @Override
    public void appendTo(Select sel, ExpContext ctx, ExpState state,
        SQLBuffer sql, int index) {
        appendTo(sel, ctx, state, sql, index, false);
    }

    private void appendTo(Select sel, ExpContext ctx, ExpState state,
        SQLBuffer sql, int index, boolean size) {
        QueryExpressionsState substate = new QueryExpressionsState();
        Select sub = _cons.evaluate(ctx, sel, _subqAlias, _exps, substate);
        _cons.select(sub, ctx, _candidate, _subs, _exps, substate,
            EagerFetchModes.EAGER_NONE);

        if (size)
            sql.appendCount(sub, ctx.fetch);
        else
            sql.append(sub, ctx.fetch);
    }

    @Override
    public void appendIsEmpty(Select sel, ExpContext ctx, ExpState state,
        SQLBuffer sql) {
        sql.append("NOT EXISTS ");
        appendTo(sel, ctx, state, sql, 0);
    }

    @Override
    public void appendIsNotEmpty(Select sel, ExpContext ctx, ExpState state,
        SQLBuffer sql) {
        sql.append("EXISTS ");
        appendTo(sel, ctx, state, sql, 0);
    }

    @Override
    public void appendSize(Select sel, ExpContext ctx, ExpState state,
        SQLBuffer sql) {
        appendTo(sel, ctx, state, sql, 0, true);
    }

    @Override
    public void acceptVisit(ExpressionVisitor visitor) {
        visitor.enter(this);
        for (int i = 0; i < _exps.projections.length; i++)
            _exps.projections[i].acceptVisit(visitor);
        if (_exps.filter != null)
            _exps.filter.acceptVisit(visitor);
        for (int i = 0; i < _exps.grouping.length; i++)
            _exps.grouping[i].acceptVisit(visitor);
        if (_exps.having != null)
            _exps.having.acceptVisit(visitor);
        for (int i = 0; i < _exps.ordering.length; i++)
            _exps.ordering[i].acceptVisit(visitor);
        visitor.exit(this);
    }
}
