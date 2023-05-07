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
package org.apache.openjpa.kernel.exps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.openjpa.kernel.StoreContext;
import org.apache.openjpa.meta.ClassMetaData;

/**
 * An in-memory representation of a {@link Value}.
 *
 * @author Abe White
 */
public abstract class Val
    implements Value {

    
    private static final long serialVersionUID = 1L;
    private ClassMetaData _meta = null;
    private String _alias = null;

    /**
     * Return this value as a projection on the given candidate.
     */
    public final Object evaluate(Object candidate, Object orig,
        StoreContext ctx, Object[] params) {
        try {
            return eval(candidate, candidate, ctx, params);
        } catch (NullPointerException | ClassCastException npe) {
            return null;
        }
    }

    /**
     * Return this value as an aggregate projection on the given group
     * of candidates.
     */
    public final Object evaluate(Collection candidates, Object orig,
        StoreContext ctx, Object[] params) {
        try {
            Collection c = eval(candidates, orig, ctx, params);
            if (c.isEmpty())
                return null;
            return c.iterator().next();
        } catch (NullPointerException | ClassCastException npe) {
            return null;
        }
    }

    /**
     * Return this value for the given candidate.
     */
    protected abstract Object eval(Object candidate, Object orig,
        StoreContext ctx, Object[] params);

    /**
     * Return a list of values resulting from evaluating each given
     * candidate. By default, this implementation invokes
     * {@link #eval(Object,Object,StoreContext,Map)} for
     * each instance and packs the return value into a new list. Aggregates
     * should override.
     */
    protected Collection eval(Collection candidates, Object orig,
        StoreContext ctx, Object[] params) {
        Collection ret = new ArrayList(candidates.size());
        Object candidate;
        for (Object o : candidates) {
            candidate = o;
            ret.add(evaluate(candidate, (orig == null) ? candidate : orig,
                    ctx, params));
        }
        return ret;
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
    public boolean isVariable() {
        return false;
    }

    @Override
    public boolean isAggregate() {
        return false;
    }

    @Override
    public boolean isXPath() {
        return false;
    }

    @Override
    public void acceptVisit(ExpressionVisitor visitor) {
        visitor.enter(this);
        visitor.exit(this);
    }

    @Override
    public String getAlias() {
        return _alias;
    }

    @Override
    public void setAlias(String alias) {
        _alias = alias;
    }

    @Override
    public Value getSelectAs() {
        return _alias != null ? this : null;
    }

    @Override
    public Path getPath() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
