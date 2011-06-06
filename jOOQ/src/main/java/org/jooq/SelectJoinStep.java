/**
 * Copyright (c) 2009-2011, Lukas Eder, lukas.eder@gmail.com
 * All rights reserved.
 *
 * This software is licensed to you under the Apache License, Version 2.0
 * (the "License"); You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
 * . Neither the name "jOOQ" nor the names of its contributors may be
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

import org.jooq.impl.Factory;

/**
 * The join step in a {@link Select} query
 * <p>
 * This is the step in query construction, where you can join tables to a query.
 * This step is optional. If you join tables, you must proceed to the
 * {@link SelectOnStep}. Or you can skip that and proceed directly to the
 * {@link SelectWhereStep}
 *
 * @author Lukas Eder
 */
public interface SelectJoinStep extends SelectWhereStep {

    /**
     * <code>INNER JOIN</code> a table and proceed to the next step
     */
    SelectOnStep join(TableLike<?> table);

    /**
     * <code>INNER JOIN</code> a table and proceed to the next step
     *
     * @see Factory#plainSQLCondition(String)
     */
    SelectOnStep join(String sql);

    /**
     * <code>INNER JOIN</code> a table and proceed to the next step
     *
     * @see Factory#plainSQLCondition(String, Object...)
     */
    SelectOnStep join(String sql, Object... bindings);

    /**
     * <code>CROSS JOIN</code> a table and proceed to the next step
     */
    SelectJoinStep crossJoin(TableLike<?> table);

    /**
     * <code>CROSS JOIN</code> a table and proceed to the next step
     *
     * @see Factory#plainSQLCondition(String)
     */
    SelectJoinStep crossJoin(String sql);

    /**
     * <code>CROSS JOIN</code> a table and proceed to the next step
     *
     * @see Factory#plainSQLCondition(String, Object...)
     */
    SelectJoinStep crossJoin(String sql, Object... bindings);

    /**
     * Left join a table and proceed to the next step
     *
     * @deprecated - Use {@link #leftOuterJoin(TableLike)} instead
     */
    @Deprecated
    SelectOnStep leftJoin(TableLike<?> table);

    /**
     * Left join a table and proceed to the next step
     *
     * @see Factory#plainSQLCondition(String)
     * @deprecated - Use {@link #leftOuterJoin(String)} instead
     */
    @Deprecated
    SelectOnStep leftJoin(String sql);

    /**
     * Left join a table and proceed to the next step
     *
     * @see Factory#plainSQLCondition(String, Object...)
     * @deprecated - Use {@link #leftOuterJoin(String, Object...)} instead
     */
    @Deprecated
    SelectOnStep leftJoin(String sql, Object... bindings);

    /**
     * <code>LEFT OUTER JOIN</code> a table and proceed to the next step
     */
    SelectOnStep leftOuterJoin(TableLike<?> table);

    /**
     * <code>LEFT OUTER JOIN</code> a table and proceed to the next step
     *
     * @see Factory#plainSQLCondition(String)
     */
    SelectOnStep leftOuterJoin(String sql);

    /**
     * <code>LEFT OUTER JOIN</code> a table and proceed to the next step
     *
     * @see Factory#plainSQLCondition(String, Object...)
     */
    SelectOnStep leftOuterJoin(String sql, Object... bindings);

    /**
     * Right join a table and proceed to the next step
     *
     * @deprecated - Use {@link #rightOuterJoin(TableLike)} instead
     */
    @Deprecated
    SelectOnStep rightJoin(TableLike<?> table);

    /**
     * Right join a table and proceed to the next step
     *
     * @see Factory#plainSQLCondition(String)
     * @deprecated - Use {@link #rightOuterJoin(String)} instead
     */
    @Deprecated
    SelectOnStep rightJoin(String sql);

    /**
     * Right join a table and proceed to the next step
     *
     * @see Factory#plainSQLCondition(String, Object...)
     * @deprecated - Use {@link #rightOuterJoin(String, Object...)} instead
     */
    @Deprecated
    SelectOnStep rightJoin(String sql, Object... bindings);

    /**
     * <code>RIGHT OUTER JOIN</code> a table and proceed to the next step
     * <p>
     * This is only possible where the underlying RDBMS supports it
     */
    SelectOnStep rightOuterJoin(TableLike<?> table);

    /**
     * <code>RIGHT OUTER JOIN</code> a table and proceed to the next step
     * <p>
     * This is only possible where the underlying RDBMS supports it
     *
     * @see Factory#plainSQLCondition(String)
     */
    SelectOnStep rightOuterJoin(String sql);

    /**
     * <code>RIGHT OUTER JOIN</code> a table and proceed to the next step
     * <p>
     * This is only possible where the underlying RDBMS supports it
     *
     * @see Factory#plainSQLCondition(String, Object...)
     */
    SelectOnStep rightOuterJoin(String sql, Object... bindings);

    /**
     * <code>FULL OUTER JOIN</code> a table and proceed to the next step
     * <p>
     * This is only possible where the underlying RDBMS supports it
     */
    SelectOnStep fullOuterJoin(TableLike<?> table);

    /**
     * <code>FULL OUTER JOIN</code> a table and proceed to the next step
     * <p>
     * This is only possible where the underlying RDBMS supports it
     *
     * @see Factory#plainSQLCondition(String)
     */
    SelectOnStep fullOuterJoin(String sql);

    /**
     * <code>FULL OUTER JOIN</code> a table and proceed to the next step
     * <p>
     * This is only possible where the underlying RDBMS supports it
     *
     * @see Factory#plainSQLCondition(String, Object...)
     */
    SelectOnStep fullOuterJoin(String sql, Object... bindings);

    /**
     * <code>NATURAL JOIN</code> a table and proceed to the next step
     * <p>
     * Natural joins are supported by most RDBMS. If they aren't supported, they
     * are simulated if jOOQ has enough information.
     */
    SelectJoinStep naturalJoin(TableLike<?> table);

    /**
     * <code>NATURAL JOIN</code> a table and proceed to the next step
     * <p>
     * Natural joins are supported by most RDBMS. If they aren't supported, they
     * are simulated if jOOQ has enough information.
     *
     * @see Factory#plainSQLCondition(String)
     */
    SelectJoinStep naturalJoin(String sql);

    /**
     * <code>NATURAL JOIN</code> a table and proceed to the next step
     * <p>
     * Natural joins are supported by most RDBMS. If they aren't supported, they
     * are simulated if jOOQ has enough information.
     *
     * @see Factory#plainSQLCondition(String, Object...)
     */
    SelectJoinStep naturalJoin(String sql, Object... bindings);

    /**
     * <code>NATURAL LEFT OUTER JOIN</code> a table and proceed to the next step
     * <p>
     * Natural joins are supported by most RDBMS. If they aren't supported, they
     * are simulated if jOOQ has enough information.
     */
    SelectJoinStep naturalLeftOuterJoin(TableLike<?> table);

    /**
     * <code>NATURAL LEFT OUTER JOIN</code> a table and proceed to the next step
     * <p>
     * Natural joins are supported by most RDBMS. If they aren't supported, they
     * are simulated if jOOQ has enough information.
     *
     * @see Factory#plainSQLCondition(String)
     */
    SelectJoinStep naturalLeftOuterJoin(String sql);

    /**
     * <code>NATURAL LEFT OUTER JOIN</code> a table and proceed to the next step
     * <p>
     * Natural joins are supported by most RDBMS. If they aren't supported, they
     * are simulated if jOOQ has enough information.
     *
     * @see Factory#plainSQLCondition(String, Object...)
     */
    SelectJoinStep naturalLeftOuterJoin(String sql, Object... bindings);

    /**
     * <code>NATURAL RIGHT OUTER JOIN</code> a table and proceed to the next
     * step
     * <p>
     * Natural joins are supported by most RDBMS. If they aren't supported, they
     * are simulated if jOOQ has enough information.
     */
    SelectJoinStep naturalRightOuterJoin(TableLike<?> table);

    /**
     * <code>NATURAL RIGHT OUTER JOIN</code> a table and proceed to the next
     * step
     * <p>
     * Natural joins are supported by most RDBMS. If they aren't supported, they
     * are simulated if jOOQ has enough information.
     *
     * @see Factory#plainSQLCondition(String)
     */
    SelectJoinStep naturalRightOuterJoin(String sql);

    /**
     * <code>NATURAL RIGHT OUTER JOIN</code> a table and proceed to the next
     * step
     * <p>
     * Natural joins are supported by most RDBMS. If they aren't supported, they
     * are simulated if jOOQ has enough information.
     *
     * @see Factory#plainSQLCondition(String, Object...)
     */
    SelectJoinStep naturalRightOuterJoin(String sql, Object... bindings);

}
