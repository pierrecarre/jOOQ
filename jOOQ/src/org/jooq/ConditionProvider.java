/**
 * Copyright (c) 2009, Lukas Eder, lukas.eder@gmail.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * . Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * . Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * . Neither the name of the "jOOQ" nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.jooq;

import java.util.Collection;

import org.jooq.impl.Create;

/**
 * A common interface for all objects holding conditions (e.g. queries)
 *
 * @author Lukas Eder
 */
public interface ConditionProvider<R extends Record> {

	/**
	 * Adds new conditions to the update query, connecting it to existing
	 * conditions with the and operator.
	 *
	 * @param conditions
	 *            The condition
	 */
	void addConditions(Condition... conditions);

	/**
	 * Adds new conditions to the update query, connecting it to existing
	 * conditions with the and operator.
	 *
	 * @param conditions
	 *            The condition
	 */
	void addConditions(Collection<Condition> conditions);

	/**
	 * Shortcut for calling {@link #addConditions(Condition)} with argument
	 * {@link Create#compareCondition(Field, Object)}
	 */
	<T> void addCompareCondition(Field<T> field, T value);

	/**
	 * Shortcut for calling {@link #addConditions(Condition)} with argument
	 * {@link QueryFactory#createCompareCondition(Field, Object, Comparator))}
	 */
	<T> void addCompareCondition(Field<T> field, T value, Comparator comparator);

	/**
	 * Shortcut for calling {@link #addConditions(Condition)} with argument
	 * {@link QueryFactory#createCompareCondition(Field, null, EQUALS))}
	 */
	void addNullCondition(Field<?> field);

	/**
	 * Shortcut for calling {@link #addConditions(Condition)} with argument
	 * {@link QueryFactory#createCompareCondition(Field, null, NOT_EQUALS))}
	 */
	void addNotNullCondition(Field<?> field);

	/**
	 * Shortcut for calling {@link #addConditions(Condition)} with argument
	 * {@link Create#inCondition(Field, Collection)}
	 */
	<T> void addInCondition(Field<T> field, Collection<T> values);

	/**
	 * Shortcut for calling {@link #addConditions(Condition)} with argument
	 * {@link Create#inCondition(Field, Object...)}
	 */
	<T> void addInCondition(Field<T> field, T... values);

	/**
	 * Shortcut for calling {@link #addConditions(Condition)} with argument
	 * {@link Create#betweenCondition(Field, Object, Object)}
	 */
	<T> void addBetweenCondition(Field<T> field, T minValue, T maxValue);
}
