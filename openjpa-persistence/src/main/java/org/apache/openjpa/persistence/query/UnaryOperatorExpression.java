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

class UnaryOperatorExpression extends ExpressionImpl implements Aggregate {
	
    private static final long serialVersionUID = 1L;
    protected final Expression _e;
	protected final UnaryFunctionalOperator   _op;

    public UnaryOperatorExpression(Expression e, UnaryFunctionalOperator op) {
		_e = e;
		_op = op;
	}

	public Expression getOperand() {
		return _e;
	}

	public UnaryFunctionalOperator getOperator() {
		return _op;
	}

	@Override
    public Expression distinct() {
		return new DistinctExpression(this);
	}

	@Override
    public String asExpression(AliasContext ctx) {
		return _op + "(" + ((Visitable)_e).asExpression(ctx) + ")";
	}

	@Override
    public String asProjection(AliasContext ctx) {
		return _op + "(" + ((Visitable)_e).asProjection(ctx) + ")" +
		    (ctx.hasAlias(this) ? " " + ctx.getAlias(this) : "");
	}
}
