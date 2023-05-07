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
package org.apache.openjpa.kernel;

import org.apache.openjpa.enhance.FieldManager;

/**
 * FieldManager type used to transfer a single field value.
 *
 * @author Abe White
 */
class TransferFieldManager
    implements FieldManager {

    protected double dblval = 0;
    protected long longval = 0;
    protected Object objval = null;
    protected int field = -1;

    @Override
    public boolean fetchBooleanField(int field) {
        return longval == 1;
    }

    @Override
    public byte fetchByteField(int field) {
        return (byte) longval;
    }

    @Override
    public char fetchCharField(int field) {
        return (char) longval;
    }

    @Override
    public double fetchDoubleField(int field) {
        return dblval;
    }

    @Override
    public float fetchFloatField(int field) {
        return (float) dblval;
    }

    @Override
    public int fetchIntField(int field) {
        return (int) longval;
    }

    @Override
    public long fetchLongField(int field) {
        return longval;
    }

    @Override
    public Object fetchObjectField(int field) {
        return objval;
    }

    @Override
    public short fetchShortField(int field) {
        return (short) longval;
    }

    @Override
    public String fetchStringField(int field) {
        return (String) objval;
    }

    @Override
    public void storeBooleanField(int field, boolean curVal) {
        this.field = field;
        longval = (curVal) ? 1 : 0;
    }

    @Override
    public void storeByteField(int field, byte curVal) {
        this.field = field;
        longval = curVal;
    }

    @Override
    public void storeCharField(int field, char curVal) {
        this.field = field;
        longval = (long) curVal;
    }

    @Override
    public void storeDoubleField(int field, double curVal) {
        this.field = field;
        dblval = curVal;
    }

    @Override
    public void storeFloatField(int field, float curVal) {
        this.field = field;
        dblval = curVal;
    }

    @Override
    public void storeIntField(int field, int curVal) {
        this.field = field;
        longval = curVal;
    }

    @Override
    public void storeLongField(int field, long curVal) {
        this.field = field;
        longval = curVal;
    }

    @Override
    public void storeObjectField(int field, Object curVal) {
        this.field = field;
        objval = curVal;
    }

    @Override
    public void storeShortField(int field, short curVal) {
        this.field = field;
        longval = curVal;
    }

    @Override
    public void storeStringField(int field, String curVal) {
        this.field = field;
        objval = curVal;
    }

    /**
     * Clear any held state. Fields are also cleared automatically when fetched.
     */
    public void clear() {
        dblval = 0;
        longval = 0;
        objval = null;
        field = -1;
    }
}
