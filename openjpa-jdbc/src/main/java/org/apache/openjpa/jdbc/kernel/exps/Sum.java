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

/**
 * Sum.
 *
 * @author Abe White
 */
class Sum extends NullableAggregateUnaryOp { // OPENJPA-1794
    private static final long serialVersionUID = 1L;

    /**
     * Constructor. Provide the value to operate on.
     */
    public Sum(Val val) {
        super(val);
    }

    /**
     * As per spec section 4.8.5 Aggregate Functions in the SELECT Clause we
     * need to handle a few types in a special way.
     */
    @Override
    protected Class getType(Class c) {
        if (c == Integer.class ||
            c == int.class ||
            c == Short.class ||
            c == short.class ||
            c == Byte.class ||
            c == byte.class) {
            return Long.class;
        }
        if (c == Float.class ||
            c == float.class ||
            c == Double.class ||
            c == double.class ) {
            return Double.class;
        }
        return c;
    }

    @Override
    protected String getOperator() {
        return "SUM";
    }

    @Override
    public boolean isAggregate() {
        return true;
    }
}
