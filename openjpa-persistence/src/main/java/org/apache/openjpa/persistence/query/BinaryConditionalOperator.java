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

/**
 * Enumeration of conditional operator that operates on ordered pair of
 * expression to generate a predicate.
 *
 * @see BinaryExpressionPredicate
 *
 * @author Pinaki Poddar
 *
 */
public enum BinaryConditionalOperator {
	BETWEEN("BETWEEN"),
	BETWEEN_NOT("NOT BETWEEN"),
	EQUAL("="),
	EQUAL_NOT("<>"),
	GREATER(">"),
	GREATEREQUAL(">="),
	IN("IN"),
	IN_NOT("NOT IN"),
	LESS("<"),
	LESSEQUAL("<="),
	LIKE("LIKE"),
	LIKE_NOT("NOT LIKE"),
	MEMBER("MEMBER OF"),
	MEMBER_NOT("NOT MEMBER OF");

	private final String _symbol;

	BinaryConditionalOperator(String symbol) {
		_symbol = symbol;
	}

	@Override
    public String toString() {
		return _symbol;
	}
}
