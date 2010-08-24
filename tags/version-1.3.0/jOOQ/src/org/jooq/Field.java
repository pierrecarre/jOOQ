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

/**
 * A field used in tables and conditions
 *
 * @author Lukas Eder
 */
public interface Field<T> extends NamedTypeProviderQueryPart<T>, AliasProvider<Field<T>> {
	/**
	 * @return The name of the field
	 */
	@Override
	String getName();

	/**
	 * @return The type of the field
	 */
	@Override
	Class<T> getType();

	/**
	 * Create an alias for this field
	 *
	 * @param alias The alias name
	 * @return The field alias
	 */
	@Override
	Field<T> as(String alias);

	// ------------------------------------------------------------------------
	// Convenience methods
	// ------------------------------------------------------------------------

	/**
	 * @return <code>this is null</code>
	 */
	CompareCondition<T> isNull();

	/**
	 * @return <code>this is not null</code>
	 */
	CompareCondition<T> isNotNull();

	/**
	 * @return <code>this like value</code>
	 */
	CompareCondition<T> like(T value);

	/**
	 * @return <code>this not like value</code>
	 */
	CompareCondition<T> notLike(T value);

	/**
	 * @return <code>this in (values...)</code>
	 */
	InCondition<T> in(T... values);

	/**
	 * @return <code>this in (select...)</code>
	 */
	SubQueryCondition<T> in(SelectQuery query);

	/**
	 * @return <code>this in (select...)</code>
	 */
	SubQueryCondition<T> in(Select query);

	/**
	 * @return <code>this not in (values...)</code>
	 */
	InCondition<T> notIn(Collection<T> values);

	/**
	 * @return <code>this not in (values...)</code>
	 */
	InCondition<T> notIn(T... values);

	/**
	 * @return <code>this not in (select...)</code>
	 */
	SubQueryCondition<T> notIn(SelectQuery query);

	/**
	 * @return <code>this not in (select...)</code>
	 */
	SubQueryCondition<T> notIn(Select query);

	/**
	 * @return <code>this in (values...)</code>
	 */
	InCondition<T> in(Collection<T> values);

	/**
	 * @return <code>this between minValue and maxValue</code>
	 */
	BetweenCondition<T> between(T minValue, T maxValue);

	/**
	 * Watch out! This is {@link Object#equals(Object)}, not a jOOQ feature! :-)
	 */
	@Override
	boolean equals(Object other);

	/**
	 * @return <code>this = value</code>
	 */
	CompareCondition<T> equal(T value);

	/**
	 * @return <code>this = field</code>
	 */
	JoinCondition<T> equal(Field<T> field);

	/**
	 * @return <code>this = (select ...)</code>
	 */
	FieldCondition<T> equal(SelectQuery query);

	/**
	 * @return <code>this = (select ...)</code>
	 */
	FieldCondition<T> equal(Select query);

	/**
	 * @return <code>this = any (select ...)</code>
	 */
	FieldCondition<T> equalAny(SelectQuery query);

	/**
	 * @return <code>this = any (select ...)</code>
	 */
	FieldCondition<T> equalAny(Select query);

	/**
	 * @return <code>this = some (select ...)</code>
	 */
	FieldCondition<T> equalSome(SelectQuery query);

	/**
	 * @return <code>this = some (select ...)</code>
	 */
	FieldCondition<T> equalSome(Select query);

	/**
	 * @return <code>this = all (select ...)</code>
	 */
	FieldCondition<T> equalAll(SelectQuery query);

	/**
	 * @return <code>this = all (select ...)</code>
	 */
	FieldCondition<T> equalAll(Select query);

	/**
	 * @return <code>this != value</code>
	 */
	CompareCondition<T> notEqual(T value);

	/**
	 * @return <code>this != field</code>
	 */
	JoinCondition<T> notEqual(Field<T> field);

	/**
	 * @return <code>this != (select ...)</code>
	 */
	FieldCondition<T> notEqual(SelectQuery query);

	/**
	 * @return <code>this != (select ...)</code>
	 */
	FieldCondition<T> notEqual(Select query);

	/**
	 * @return <code>this != any (select ...)</code>
	 */
	FieldCondition<T> notEqualAny(SelectQuery query);

	/**
	 * @return <code>this != any (select ...)</code>
	 */
	FieldCondition<T> notEqualAny(Select query);

	/**
	 * @return <code>this != some (select ...)</code>
	 */
	FieldCondition<T> notEqualSome(SelectQuery query);

	/**
	 * @return <code>this != some (select ...)</code>
	 */
	FieldCondition<T> notEqualSome(Select query);

	/**
	 * @return <code>this != all (select ...)</code>
	 */
	FieldCondition<T> notEqualAll(SelectQuery query);

	/**
	 * @return <code>this != all (select ...)</code>
	 */
	FieldCondition<T> notEqualAll(Select query);

	/**
	 * @return <code>this < value</code>
	 */
	CompareCondition<T> lessThan(T value);

	/**
	 * @return <code>this < field</code>
	 */
	JoinCondition<T> lessThan(Field<T> field);

	/**
	 * @return <code>this < (select ...)</code>
	 */
	FieldCondition<T> lessThan(SelectQuery query);

	/**
	 * @return <code>this < (select ...)</code>
	 */
	FieldCondition<T> lessThan(Select query);

	/**
	 * @return <code>this < any (select ...)</code>
	 */
	FieldCondition<T> lessThanAny(SelectQuery query);

	/**
	 * @return <code>this < any (select ...)</code>
	 */
	FieldCondition<T> lessThanAny(Select query);

	/**
	 * @return <code>this < some (select ...)</code>
	 */
	FieldCondition<T> lessThanSome(SelectQuery query);

	/**
	 * @return <code>this < some (select ...)</code>
	 */
	FieldCondition<T> lessThanSome(Select query);

	/**
	 * @return <code>this < all (select ...)</code>
	 */
	FieldCondition<T> lessThanAll(SelectQuery query);

	/**
	 * @return <code>this < all (select ...)</code>
	 */
	FieldCondition<T> lessThanAll(Select query);

	/**
	 * @return <code>this <= value</code>
	 */
	CompareCondition<T> lessOrEqual(T value);

	/**
	 * @return <code>this <= field</code>
	 */
	JoinCondition<T> lessOrEqual(Field<T> field);

	/**
	 * @return <code>this <= (select ...)</code>
	 */
	FieldCondition<T> lessOrEqual(SelectQuery query);

	/**
	 * @return <code>this <= (select ...)</code>
	 */
	FieldCondition<T> lessOrEqual(Select query);

	/**
	 * @return <code>this <= any (select ...)</code>
	 */
	FieldCondition<T> lessOrEqualToAny(SelectQuery query);

	/**
	 * @return <code>this <= any (select ...)</code>
	 */
	FieldCondition<T> lessOrEqualToAny(Select query);

	/**
	 * @return <code>this <= some (select ...)</code>
	 */
	FieldCondition<T> lessOrEqualToSome(SelectQuery query);

	/**
	 * @return <code>this <= some (select ...)</code>
	 */
	FieldCondition<T> lessOrEqualToSome(Select query);

	/**
	 * @return <code>this <= all (select ...)</code>
	 */
	FieldCondition<T> lessOrEqualToAll(SelectQuery query);

	/**
	 * @return <code>this <= all (select ...)</code>
	 */
	FieldCondition<T> lessOrEqualToAll(Select query);

	/**
	 * @return <code>this > value</code>
	 */
	CompareCondition<T> greaterThan(T value);

	/**
	 * @return <code>this > field</code>
	 */
	JoinCondition<T> greaterThan(Field<T> field);

	/**
	 * @return <code>this > (select ...)</code>
	 */
	FieldCondition<T> greaterThan(SelectQuery query);

	/**
	 * @return <code>this > (select ...)</code>
	 */
	FieldCondition<T> greaterThan(Select query);

	/**
	 * @return <code>this > any (select ...)</code>
	 */
	FieldCondition<T> greaterThanAny(SelectQuery query);

	/**
	 * @return <code>this > any (select ...)</code>
	 */
	FieldCondition<T> greaterThanAny(Select query);

	/**
	 * @return <code>this > some (select ...)</code>
	 */
	FieldCondition<T> greaterThanSome(SelectQuery query);

	/**
	 * @return <code>this > some (select ...)</code>
	 */
	FieldCondition<T> greaterThanSome(Select query);

	/**
	 * @return <code>this > all (select ...)</code>
	 */
	FieldCondition<T> greaterThanAll(SelectQuery query);

	/**
	 * @return <code>this > all (select ...)</code>
	 */
	FieldCondition<T> greaterThanAll(Select query);

	/**
	 * @return <code>this >= value</code>
	 */
	CompareCondition<T> greaterOrEqual(T value);

	/**
	 * @return <code>this >= field</code>
	 */
	JoinCondition<T> greaterOrEqual(Field<T> field);

	/**
	 * @return <code>this >= (select ...)</code>
	 */
	FieldCondition<T> greaterOrEqual(SelectQuery query);

	/**
	 * @return <code>this >= (select ...)</code>
	 */
	FieldCondition<T> greaterOrEqual(Select query);

	/**
	 * @return <code>this >= any (select ...)</code>
	 */
	FieldCondition<T> greaterOrEqualAny(SelectQuery query);

	/**
	 * @return <code>this >= any (select ...)</code>
	 */
	FieldCondition<T> greaterOrEqualAny(Select query);

	/**
	 * @return <code>this >= some (select ...)</code>
	 */
	FieldCondition<T> greaterOrEqualSome(SelectQuery query);

	/**
	 * @return <code>this >= some (select ...)</code>
	 */
	FieldCondition<T> greaterOrEqualSome(Select query);

	/**
	 * @return <code>this >= all (select ...)</code>
	 */
	FieldCondition<T> greaterOrEqualAll(SelectQuery query);

	/**
	 * @return <code>this >= all (select ...)</code>
	 */
	FieldCondition<T> greaterOrEqualAll(Select query);

}
