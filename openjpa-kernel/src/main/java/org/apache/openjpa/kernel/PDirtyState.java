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

/**
 * Lifecycle state.
 * Represents a persistent instance that is participating in the current
 * transaction, and has been modified.
 *
 * @author Abe White
 */
class PDirtyState extends PCState {
    private static final long serialVersionUID = 1L;

    @Override
    void initialize(StateManagerImpl context, PCState previous) {
        context.saveFields(false);
    }

    @Override
    void beforeFlush(StateManagerImpl context, boolean logical,
        OpCallbacks call) {
        context.preFlush(logical, call);
    }

    @Override
    PCState commit(StateManagerImpl context) {
        return HOLLOW;
    }

    @Override
    PCState commitRetain(StateManagerImpl context) {
        return PNONTRANS;
    }

    @Override
    PCState rollback(StateManagerImpl context) {
        return HOLLOW;
    }

    @Override
    PCState rollbackRestore(StateManagerImpl context) {
        context.restoreFields();
        return PNONTRANS;
    }

    @Override
    PCState delete(StateManagerImpl context) {
        context.preDelete();
        return PDELETED;
    }

    @Override
    PCState nontransactional(StateManagerImpl context) {
        return error("dirty", context);
    }

    @Override
    PCState release(StateManagerImpl context) {
        return error("dirty", context);
    }

    @Override
    boolean isVersionCheckRequired(StateManagerImpl context) {
        return !context.isFlushed() || context.isFlushedDirty();
    }

    @Override
    PCState afterRefresh() {
        return PCLEAN;
    }

    @Override
    PCState afterOptimisticRefresh() {
        return PNONTRANS;
    }

    @Override
    boolean isTransactional() {
        return true;
    }

    @Override
    boolean isPersistent() {
        return true;
    }

    @Override
    boolean isDirty() {
        return true;
    }

    @Override
    public String toString() {
        return "Persistent-Dirty";
    }
}

