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

import org.jooq.impl.Factory;

/**
 * A field used in tables and conditions
 *
 * @author Lukas Eder
 */
public interface Field<T> extends NamedTypeProviderQueryPart<T>, AliasProvider<Field<T>> {

    // ------------------------------------------------------------------------
    // API
    // ------------------------------------------------------------------------

    /**
     * @return The name of the field
     */
    @Override
    String getName();

    /**
     * @return The type of the field
     */
    @Override
    Class<? extends T> getType();

    /**
     * Create an alias for this field
     *
     * @param alias The alias name
     * @return The field alias
     */
    @Override
    Field<T> as(String alias);

    /**
     * Watch out! This is {@link Object#equals(Object)}, not a jOOQ feature! :-)
     */
    @Override
    boolean equals(Object other);

    // ------------------------------------------------------------------------
    // Conversion of field into a sort field
    // ------------------------------------------------------------------------

    /**
     * Create an ascending sort field from this
     *
     * @return This field as an ascending sort field
     */
    SortField<T> ascending();

    /**
     * Create a descending sort field from this
     *
     * @return This field as a descending sort field
     */
    SortField<T> descending();

    // ------------------------------------------------------------------------
    // Arithmetic expressions
    // ------------------------------------------------------------------------

    /**
     * @return an arithmetic expression adding this to value
     */
    Field<T> add(Number value);

    /**
     * @return an arithmetic expression adding this to value
     */
    Field<T> add(Field<? extends Number> value);

    /**
     * @return an arithmetic expression subtracting value from this
     */
    Field<T> subtract(Number value);

    /**
     * @return an arithmetic expression subtracting value from this
     */
    Field<T> subtract(Field<? extends Number> value);

    /**
     * @return an arithmetic expression multiplying this with value
     */
    Field<T> multiply(Number value);

    /**
     * @return an arithmetic expression multiplying this with value
     */
    Field<T> multiply(Field<? extends Number> value);

    /**
     * @return an arithmetic expression dividing this by value
     */
    Field<T> divide(Number value);

    /**
     * @return an arithmetic expression dividing this by value
     */
    Field<T> divide(Field<? extends Number> value);

    // ------------------------------------------------------------------------
    // Functions created from this field
    // ------------------------------------------------------------------------

    /**
     * Get the count(field) function
     *
     * @see Factory#count()
     */
    Field<Integer> count();

    /**
     * Get the count(distinct field) function
     *
     * @see Factory#count()
     */
    Field<Integer> countDistinct();

    /**
     * Get the max value over a field: max(field)
     */
    Field<T> max();

    /**
     * Get the min value over a field: min(field)
     */
    Field<T> min();

    // ------------------------------------------------------------------------
    // Conditions created from this field
    // ------------------------------------------------------------------------

    /**
     * @return <code>this is null</code>
     */
    Condition isNull();

    /**
     * @return <code>this is not null</code>
     */
    Condition isNotNull();

    /**
     * @return <code>this like value</code>
     */
    Condition like(T value);

    /**
     * @return <code>this not like value</code>
     */
    Condition notLike(T value);

    /**
     * @return <code>this in (values...)</code>
     */
    Condition in(T... values);

    /**
     * @return <code>this in (select...)</code>
     */
    Condition in(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this not in (values...)</code>
     */
    Condition notIn(Collection<T> values);

    /**
     * @return <code>this not in (values...)</code>
     */
    Condition notIn(T... values);

    /**
     * @return <code>this not in (select...)</code>
     */
    Condition notIn(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this in (values...)</code>
     */
    Condition in(Collection<T> values);

    /**
     * @return <code>this between minValue and maxValue</code>
     */
    Condition between(T minValue, T maxValue);

    /**
     * @return <code>this = value</code>
     */
    Condition equal(T value);

    /**
     * @return <code>this = field</code>
     */
    Condition equal(Field<T> field);

    /**
     * @return <code>this = (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition equal(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this = any (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition equalAny(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this = some (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition equalSome(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this = all (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition equalAll(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this != value</code>
     */
    Condition notEqual(T value);

    /**
     * @return <code>this != field</code>
     */
    Condition notEqual(Field<T> field);

    /**
     * @return <code>this != (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition notEqual(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this != any (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition notEqualAny(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this != some (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition notEqualSome(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this != all (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition notEqualAll(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this < value</code>
     */
    Condition lessThan(T value);

    /**
     * @return <code>this < field</code>
     */
    Condition lessThan(Field<T> field);

    /**
     * @return <code>this < (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition lessThan(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this < any (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition lessThanAny(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this < some (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition lessThanSome(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this < all (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition lessThanAll(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this <= value</code>
     */
    Condition lessOrEqual(T value);

    /**
     * @return <code>this <= field</code>
     */
    Condition lessOrEqual(Field<T> field);

    /**
     * @return <code>this <= (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition lessOrEqual(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this <= any (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition lessOrEqualToAny(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this <= some (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition lessOrEqualToSome(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this <= all (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition lessOrEqualToAll(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this > value</code>
     */
    Condition greaterThan(T value);

    /**
     * @return <code>this > field</code>
     */
    Condition greaterThan(Field<T> field);

    /**
     * @return <code>this > (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition greaterThan(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this > any (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition greaterThanAny(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this > some (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition greaterThanSome(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this > all (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition greaterThanAll(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this >= value</code>
     */
    Condition greaterOrEqual(T value);

    /**
     * @return <code>this >= field</code>
     */
    Condition greaterOrEqual(Field<T> field);

    /**
     * @return <code>this >= (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition greaterOrEqual(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this >= any (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition greaterOrEqualAny(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this >= some (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition greaterOrEqualSome(ResultProviderSelectQuery<?, ?> query);

    /**
     * @return <code>this >= all (ResultProviderSelectQuery<?, ?> ...)</code>
     */
    Condition greaterOrEqualAll(ResultProviderSelectQuery<?, ?> query);

}
