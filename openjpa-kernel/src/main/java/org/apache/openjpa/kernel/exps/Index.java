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

import org.apache.openjpa.lib.util.Localizer;
import org.apache.openjpa.util.UnsupportedException;

/**
 * Returns the index of a value within a collection/map.
 *
 * @author Catalina Wei
 */
class Index extends UnaryMathVal {

    
    private static final long serialVersionUID = 1L;
    private static final Localizer _loc = Localizer.forPackage(Index.class);

    /**
     * Constructor.
     */
    public Index(Val val) {
        super(val);
    }

    @Override
    public Class getType(Class c) {
        return int.class;
    }

    @Override
    protected Object operate(Object o, Class c) {
        throw new UnsupportedException(_loc.get("in-mem-index"));
    }
}
