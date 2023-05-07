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
package org.apache.openjpa.persistence.query;

import org.apache.openjpa.util.InternalException;

/**
 * A path resulting from KEY() or VALUE() operation on an existing path.
 *
 * @author Pinaki Poddar
 *
 */
public class OperatorPath extends AbstractDomainObject {
    
    private static final long serialVersionUID = 1L;

    public OperatorPath(AbstractDomainObject operand, PathOperator operator) {
		super(operand.getOwner(), operand, operator, null);
	}

	@Override
	public Class getLastSegment() {
		throw new InternalException();
	}

	@Override
	public String getAliasHint(AliasContext ctx) {
		return getParent().getAliasHint(ctx);
	}

	@Override
	public String asProjection(AliasContext ctx) {
        return getOperator() + "(" + getParent().asProjection(ctx) + ")";
	}

	@Override
	public String asExpression(AliasContext ctx) {
        return getOperator() + "(" + getParent().asExpression(ctx) + ")";
	}

	@Override
	public String asJoinable(AliasContext ctx) {
		throw new UnsupportedOperationException();
	}

	@Override
    public String toString() {
		return getOperator() + "(" + getParent().toString() + ")";
	}

}
