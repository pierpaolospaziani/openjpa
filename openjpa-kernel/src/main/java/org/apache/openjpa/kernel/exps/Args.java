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
import java.util.List;

import org.apache.openjpa.kernel.StoreContext;

/**
 * A list of arguments to a multi-argument function.
 *
 * @author Abe White
 */
class Args
    extends Val
    implements Arguments {

    
    private static final long serialVersionUID = 1L;
    private final List<Value> _args = new ArrayList<>(3);

    /**
     * Constructor. Supply values being combined.
     */
    public Args(Value val1, Value val2) {
        this(new Value[]{val1, val2});
    }

    public Args(Value...values) {
        if (values == null) {
           return;
        }
        for (Value v : values) {
            if (v instanceof Args) {
                _args.addAll(((Args)v)._args);
            } else {
                _args.add(v);
            }
        }
    }

    @Override
    public Value[] getValues() {
        return _args.toArray(new Value[_args.size()]);
    }

    @Override
    public Class getType() {
        return Object[].class;
    }

    public Class[] getTypes() {
        Class[] c = new Class[_args.size()];
        for (int i = 0; i < _args.size(); i++)
            c[i] = ((Val) _args.get(i)).getType();
        return c;
    }

    @Override
    public void setImplicitType(Class type) {
    }

    @Override
    protected Object eval(Object candidate, Object orig,
        StoreContext ctx, Object[] params) {
        Object[] vals = new Object[_args.size()];
        for (int i = 0; i < _args.size(); i++)
            vals[i] = ((Val) _args.get(i)).eval(candidate, orig, ctx, params);
        return vals;
    }

    @Override
    public void acceptVisit(ExpressionVisitor visitor) {
        visitor.enter(this);
        for (Value arg : _args) {
            ((Val) arg).acceptVisit(visitor);
        }
        visitor.exit(this);
    }
}
