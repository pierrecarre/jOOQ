/**
 * Copyright (c) 2009-2011, Lukas Eder, lukas.eder@gmail.com
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
 * A query for data selection
 *
 * @author Lukas Eder
 */
public interface SelectQuery extends Select<Record>, ConditionProvider, OrderProvider {

    /**
     * Add a list of select fields
     *
     * @param fields
     */
    void addSelect(Field<?>... fields);

    /**
     * Add a list of select fields
     *
     * @param fields
     */
    void addSelect(Collection<Field<?>> fields);

    /**
     * Add "distinct" keyword to the select clause
     */
    void setDistinct(boolean distinct);

    /**
     * Add tables to the table product
     *
     * @param from The added tables
     */
    void addFrom(TableLike<?>... from);

    /**
     * Add tables to the table product
     *
     * @param from The added tables
     */
    void addFrom(Collection<TableLike<?>> from);

    /**
     * Joins the existing table product to a new table using a condition
     *
     * @param table The joined table
     * @param conditions The joining conditions
     */
    void addJoin(TableLike<?> table, Condition... conditions);

    /**
     * Joins the existing table product to a new table using a condition
     *
     * @param table The joined table
     * @param type The type of join
     * @param conditions The joining conditions
     */
    void addJoin(TableLike<?> table, JoinType type, Condition... conditions);

    /**
     * Joins the existing table product to a new table joining on two fields
     *
     * @param <T> The common field type
     * @param table The joined table
     * @param field1 The left field of the join condition
     * @param field2 The right field of the join condition
     * @deprecated - Use {@link #addJoin(TableLike, Condition...)} instead
     */
    @Deprecated
    <T> void addJoin(TableLike<?> table, Field<T> field1, Field<T> field2);

    /**
     * Joins the existing table product to a new table joining on two fields
     *
     * @param <T> The common field type
     * @param table The joined table
     * @param type The type of join
     * @param field1 The left field of the join condition
     * @param field2 The right field of the join condition
     * @deprecated - Use {@link #addJoin(TableLike, JoinType, Condition...)} instead
     */
    @Deprecated
    <T> void addJoin(TableLike<?> table, JoinType type, Field<T> field1, Field<T> field2);

    /**
     * Adds grouping fields
     *
     * @param fields The grouping fields
     */
    void addGroupBy(Field<?>... fields);

    /**
     * Adds grouping fields
     *
     * @param fields The grouping fields
     */
    void addGroupBy(Collection<Field<?>> fields);

    /**
     * Adds new conditions to the having clause of the query, connecting it to
     * existing conditions with the and operator.
     *
     * @param conditions The condition
     * @deprecated - Use {@link #addHaving(Condition...)} instead
     */
    @Deprecated
    <T> void addHaving(Field<T> field, T value);

    /**
     * Adds new conditions to the having clause of the query, connecting it to
     * existing conditions with the and operator.
     *
     * @param conditions The condition
     * @deprecated - Use {@link #addHaving(Condition...)} instead
     */
    @Deprecated
    <T> void addHaving(Field<T> field, T value, Comparator comparator);

    /**
     * Adds new conditions to the having clause of the query, connecting it to
     * existing conditions with the and operator.
     *
     * @param conditions The condition
     */
    void addHaving(Condition... conditions);

    /**
     * Adds new conditions to the having clause of the query, connecting it to
     * existing conditions with the and operator.
     *
     * @param conditions The condition
     */
    void addHaving(Collection<Condition> conditions);
}
