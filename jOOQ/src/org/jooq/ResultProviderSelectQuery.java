/**
 * Copyright (c) 2010, Lukas Eder, lukas.eder@gmail.com
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
import java.util.List;

/**
 * A common interface for {@link SelectQuery} and {@link SimpleSelectQuery}
 *
 * @author Lukas Eder
 */
public interface ResultProviderSelectQuery<Q extends ResultProviderSelectQuery<Q, R>, R extends Record>
extends ResultProviderQuery<R>,
        ConditionProvider,
        QueryProvider<Q>,
        RecordTypeProvider<R> {

    /**
     * Combine this SelectQuery with another one, using the
     * {@link CombineOperator#UNION} operator.
     *
     * @param others The SelectQuery to combine this one with
     * @return
     * @see #combine(SelectQuery, CombineOperator)
     */
    Q combine(Q other);

    /**
     * Combine this SelectQuery with another one, using the given operator.
     *
     * @param others The SelectQuery to combine this one with
     * @param operator The combine operator
     * @return
     */
    Q combine(CombineOperator operator, Q other);

    /**
     * Use this query as a table
     * <p>
     * This method is useful for things like
     * <code>SELECT * FROM (SELECT * FROM x WHERE x.a = '1') WHERE
     *
     * @return This result provider as a Table object
     */
    Table<R> asTable();

    /**
     * Use this query as a field.
     * <p>
     * This method is useful for things like
     * <code>SELECT y.*, (SELECT a FROM x) a FROM y</code>
     *
     * @return This result provider as a Field&lt;?&gt; object
     */
    <T> Field<T> asField();

    /**
     * Use this query for IN conditions.
     * <p>
     * This method is useful for things like
     * <code>SELECT * FROM x WHERE x.field IN (SELECT ...)</code>
     *
     * @param field The field to compare this query's results with
     * @return This result provider as a InCondition object
     */
    Condition asInCondition(Field<?> field);

    /**
     * Use this query for NOT IN conditions.
     * <p>
     * This method is useful for things like
     * <code>SELECT * FROM x WHERE x.field NOT IN (SELECT ...)</code>
     *
     * @param field The field to compare this query's results with
     * @return This result provider as a InCondition object
     */
    Condition asNotInCondition(Field<?> field);

    /**
     * Use this query for EQUALS conditions.
     * <p>
     * This method is useful for things like
     * <code>SELECT * FROM x WHERE x.field = (SELECT ...)</code>
     *
     * @param field The field to compare this query's results with
     * @return This result provider as a InCondition object
     */
    Condition asCompareCondition(Field<?> field);

    /**
     * This method is useful for things like
     * <code>SELECT * FROM x WHERE x.field [any operator] (SELECT ...)</code>
     *
     * @param field The field to compare this query's results with
     * @return This result provider as a InCondition object
     */
    Condition asSubQueryCondition(Field<?> field, SubQueryOperator operator);

    /**
     * Use this query for EXISTS conditions.
     * <p>
     * This method is useful for things like
     * <code>SELECT * FROM x WHERE EXISTS (SELECT ...)</code>
     *
     * @param field The field to compare this query's results with
     * @return This result provider as a InCondition object
     */
    Condition asExistsCondition();

    /**
     * Use this query for NOT EXISTS conditions.
     * <p>
     * This method is useful for things like
     * <code>SELECT * FROM x WHERE NOT EXISTS (SELECT ...)</code>
     *
     * @param field The field to compare this query's results with
     * @return This result provider as a InCondition object
     */
    Condition asNotExistsCondition();

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
     * Adds ordering fields, ordering by the default sort order
     *
     * @param fields The ordering fields
     */
    void addOrderBy(Field<?>... fields);

    /**
     * Adds ordering fields, ordering by the default sort order
     *
     * @param fields The ordering fields
     */
    void addOrderBy(Collection<Field<?>> fields);

    /**
     * Adds an ordering field
     *
     * @param field The ordering field
     * @param order The sort order
     */
    void addOrderBy(Field<?> field, SortOrder order);

    /**
     * Limit the results of this select
     * <p>
     * This is the same as calling {@link #addLimit(int, int)} with lowerBound =
     * 1
     *
     * @param numberOfRows The number of rows to return
     */
    void addLimit(int numberOfRows);

    /**
     * Limit the results of this select
     *
     * @param lowerBound The lowest rownum starting at 1
     * @param numberOfRows The number of rows to return
     */
    void addLimit(int lowerBound, int numberOfRows);

    /**
     * All fields selected in this query
     */
    List<Field<?>> getSelect();
}
