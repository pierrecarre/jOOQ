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
     * Join a table and proceed to the next step
     */
    SelectOnStep join(Table<?> table);

    /**
     * Join a table and proceed to the next step
     *
     * @see Factory#plainSQLCondition(String)
     */
    SelectOnStep join(String sql);

    /**
     * Join a table and proceed to the next step
     *
     * @see Factory#plainSQLCondition(String, Object...)
     */
    SelectOnStep join(String sql, Object... bindings);

    /**
     * Left join a table and proceed to the next step
     *
     * @deprecated - Use {@link #leftOuterJoin(Table)} instead
     */
    @Deprecated
    SelectOnStep leftJoin(Table<?> table);

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
     * Left outer join a table and proceed to the next step
     */
    SelectOnStep leftOuterJoin(Table<?> table);

    /**
     * Left outer join a table and proceed to the next step
     *
     * @see Factory#plainSQLCondition(String)
     */
    SelectOnStep leftOuterJoin(String sql);

    /**
     * Left outer join a table and proceed to the next step
     *
     * @see Factory#plainSQLCondition(String, Object...)
     */
    SelectOnStep leftOuterJoin(String sql, Object... bindings);

    /**
     * Right join a table and proceed to the next step
     *
     * @deprecated - Use {@link #rightOuterJoin(Table)} instead
     */
    @Deprecated
    SelectOnStep rightJoin(Table<?> table);

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
     * Right outer join a table and proceed to the next step
     */
    SelectOnStep rightOuterJoin(Table<?> table);

    /**
     * Right outer join a table and proceed to the next step
     *
     * @see Factory#plainSQLCondition(String)
     */
    SelectOnStep rightOuterJoin(String sql);

    /**
     * Right outer join a table and proceed to the next step
     *
     * @see Factory#plainSQLCondition(String, Object...)
     */
    SelectOnStep rightOuterJoin(String sql, Object... bindings);
}
