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

import java.util.Collection;

import org.apache.openjpa.kernel.Filters;

/**
 * Average values.
 *
 * @author Abe White
 */
class Avg
    extends AggregateVal {

    
    private static final long serialVersionUID = 1L;

    /**
     * Constructor. Provide the value to average.
     */
    public Avg(Val val) {
        super(val);
    }

    @Override
    protected Class getType(Class c) {
        return c;
    }

    @Override
    protected Object operate(Collection os, Class c) {
        if (os.isEmpty())
            return null;

        Object sum = Filters.convert(0, c);
        Object cur;
        int size = 0;
        for (Object o : os) {
            cur = o;
            if (cur == null)
                continue;

            sum = Filters.add(sum, c, cur, c);
            size++;
        }
        if (size == 0)
            return null;
        return Filters.divide(sum, c, size, int.class);
    }
}
