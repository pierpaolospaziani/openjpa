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

import org.apache.openjpa.kernel.Filters;
import org.apache.openjpa.kernel.StoreContext;

/**
 * Represents a type literal.
 *
 * @author Catalina Wei
 */
class TypeLit
    extends Val
    implements Literal {

    
    private static final long serialVersionUID = 1L;
    private Object _val;
    private final int _ptype;

    /**
     * Constructor. Provide constant value.
     */
    public TypeLit(Object val, int ptype) {
        _val = val;
        _ptype = ptype;
    }

    @Override
    public Object getValue() {
        return _val;
    }

    @Override
    public void setValue(Object val) {
        _val = val;
    }

    @Override
    public int getParseType() {
        return _ptype;
    }

    @Override
    public Object getValue(Object[] parameters) {
        return _val;
    }

    @Override
    public Class getType() {
        return (_val == null) ? Object.class : _val.getClass();
    }

    @Override
    public void setImplicitType(Class type) {
        _val = Filters.convert(_val, type);
    }

    @Override
    protected Object eval(Object candidate, Object orig,
        StoreContext ctx, Object[] params) {
        return _val;
    }
}
