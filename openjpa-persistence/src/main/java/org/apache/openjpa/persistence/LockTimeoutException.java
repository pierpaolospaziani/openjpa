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
package org.apache.openjpa.persistence;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;

import org.apache.openjpa.util.ExceptionInfo;
import org.apache.openjpa.util.Exceptions;
import org.apache.openjpa.util.StoreException;

/**
 * Pessimistic lock timeout violation.
 *
 * @since 2.0.0
 */
public class LockTimeoutException
    extends javax.persistence.LockTimeoutException
    implements Serializable, ExceptionInfo {

    private static final long serialVersionUID = 1L;
    private transient boolean _fatal = false;
    private transient Object _failed = null;
    private transient Throwable[] _nested = null;

    public LockTimeoutException(String msg, Throwable[] nested,  Object failed) {
        super(msg, nested == null ? null : nested[0], failed);
        _nested = nested;
        _failed = failed;
    }

    @Override
    public int getType() {
        return STORE;
    }

    @Override
    public int getSubtype() {
        return StoreException.LOCK;
    }

    @Override
    public boolean isFatal() {
        return _fatal;
    }

    @Override
    public Throwable getCause() {
        return PersistenceExceptions.getCause(_nested);
    }

    @Override
    public Throwable[] getNestedThrowables() {
        return (_nested == null) ? Exceptions.EMPTY_THROWABLES : _nested;
    }

    @Override
    public Object getFailedObject() {
        return _failed;
    }

    @Override
    public String toString() {
        return Exceptions.toString(this);
    }

    @Override
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    @Override
    public void printStackTrace(PrintStream out) {
        super.printStackTrace(out);
        Exceptions.printNestedThrowables(this, out);
    }

    @Override
    public void printStackTrace(PrintWriter out) {
        super.printStackTrace(out);
        Exceptions.printNestedThrowables(this, out);
    }

    private void writeObject(ObjectOutputStream out)
        throws IOException {
        out.writeBoolean(_fatal);
        out.writeObject(Exceptions.replaceFailedObject(_failed));
        out.writeObject(Exceptions.replaceNestedThrowables(_nested));
    }

    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        _fatal = in.readBoolean();
        _failed = in.readObject();
        _nested = (Throwable[]) in.readObject();
    }
}

