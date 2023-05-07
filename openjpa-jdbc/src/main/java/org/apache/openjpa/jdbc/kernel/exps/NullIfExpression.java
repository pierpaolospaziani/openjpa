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

import org.apache.openjpa.jdbc.meta.JavaSQLTypes;
import org.apache.openjpa.jdbc.sql.Raw;
import org.apache.openjpa.jdbc.sql.Result;
import org.apache.openjpa.jdbc.sql.SQLBuffer;
import org.apache.openjpa.jdbc.sql.Select;
import org.apache.openjpa.kernel.Filters;
import org.apache.openjpa.kernel.exps.ExpressionVisitor;
import org.apache.openjpa.kernel.exps.Value;
import org.apache.openjpa.meta.ClassMetaData;

/**
 * NullIf expression.
 *
 * @author Catalina Wei
 */
public class NullIfExpression
    extends AbstractVal {

    
    private static final long serialVersionUID = 1L;
    private final Val _val1;
    private final Val _val2;
    private ClassMetaData _meta = null;
    private Class _cast = null;
    private Value other = null;
    private ExpState otherState = null;

    /**
     * Constructor.
     */
    public NullIfExpression(Val val1, Val val2) {
        _val1 = val1;
        _val2 = val2;
    }

    public Val getVal1() {
        return _val1;
    }

    public Val getVal2() {
        return _val2;
    }

    @Override
    public Class getType() {
        if (_cast != null)
            return _cast;
        Class c1 = _val1.getType();
        Class c2 = _val2.getType();
        Class type = Filters.promote(c1, c2);
        if (type == Raw.class)
            return String.class;
        return type;
    }

    @Override
    public ExpState initialize(Select sel, ExpContext ctx, int flags) {
        ExpState s1 = _val1.initialize(sel, ctx, 0);
        ExpState s2 = _val2.initialize(sel, ctx, 0);
        return new BinaryOpExpState(sel.and(s1.joins, s2.joins), s1, s2);
    }

    @Override
    public void appendTo(Select sel, ExpContext ctx, ExpState state,
        SQLBuffer buf, int index) {
        BinaryOpExpState bstate = (BinaryOpExpState) state;

        buf.append(" NULLIF("); // MySQL does not like space before bracket

        _val1.appendTo(sel, ctx, bstate.state1, buf, 0);
        buf.append(",");
        _val2.appendTo(sel, ctx, bstate.state2, buf, 0);

        buf.append(")");
    }

    @Override
    public void selectColumns(Select sel, ExpContext ctx, ExpState state,
        boolean pks) {
        BinaryOpExpState bstate = (BinaryOpExpState) state;
        _val1.selectColumns(sel, ctx, bstate.state1, true);
        _val2.selectColumns(sel, ctx, bstate.state2, true);
    }

    @Override
    public void acceptVisit(ExpressionVisitor visitor) {
        visitor.enter(this);
        _val1.acceptVisit(visitor);
        _val2.acceptVisit(visitor);
        visitor.exit(this);
    }

    @Override
    public int getId() {
        return Val.NULLIF_VAL;
    }

    @Override
    public void calculateValue(Select sel, ExpContext ctx, ExpState state,
        Val other, ExpState otherState) {
        BinaryOpExpState bstate = (BinaryOpExpState) state;
        _val1.calculateValue(sel, ctx, bstate.state1, _val2, bstate.state2);
        _val2.calculateValue(sel, ctx, bstate.state2, _val1, bstate.state1);
    }

    @Override
    public void groupBy(Select sel, ExpContext ctx, ExpState state) {
        sel.groupBy(newSQLBuffer(sel, ctx, state));
    }

    @Override
    public int length(Select sel, ExpContext ctx, ExpState state) {
        return 1;
    }

    private SQLBuffer newSQLBuffer(Select sel, ExpContext ctx, ExpState state) {
        calculateValue(sel, ctx, state, null, null);
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
    public void orderBy(Select sel, ExpContext ctx, ExpState state,
        boolean asc) {
        sel.orderBy(newSQLBuffer(sel, ctx, state), asc, false, getSelectAs());
    }

    @Override
    public void select(Select sel, ExpContext ctx, ExpState state, boolean pks){
        sel.select(newSQLBuffer(sel, ctx, state), this);
    }

    @Override
    public ClassMetaData getMetaData() {
        return _meta;
    }

    @Override
    public void setImplicitType(Class type) {
        _cast = type;
    }

    @Override
    public void setMetaData(ClassMetaData meta) {
        _meta = meta;
    }

    public void setOtherPath(Value other) {
        this.other = other;
    }

    public Value getOtherPath() {
        return other;
    }

    public void setOtherState(ExpState otherState) {
        this.otherState = otherState;
    }

    public ExpState getOtherState() {
        return otherState;
    }

}

