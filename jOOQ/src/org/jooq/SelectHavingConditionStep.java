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
 * The step in a {@link Select} query, where additional conditions can be added
 * to the having clause.
 * <p>
 * This is the step in query construction, where you can add conditions to a
 * query's having clause. This step is optional. If you add conditions, you can
 * proceed to the optional {@link SelectOrderByStep}.
 *
 * @author Lukas Eder
 */
public interface SelectHavingConditionStep extends SelectOrderByStep {

    /**
     * Combine the currently assembled conditions with another one using the
     * {@link Operator#AND} operator and proceed to the next step.
     */
    SelectHavingConditionStep and(Condition condition);

    /**
     * Combine the currently assembled conditions with another one using the
     * {@link Operator#AND} operator and proceed to the next step.
     *
     * @see Factory#plainSQLCondition(String)
     */
    SelectHavingConditionStep and(String sql);

    /**
     * Combine the currently assembled conditions with another one using the
     * {@link Operator#AND} operator and proceed to the next step.
     *
     * @see Factory#plainSQLCondition(String, Object...)
     */
    SelectHavingConditionStep and(String sql, Object... bindings);

    /**
     * Combine the currently assembled conditions with a negated other one using
     * the {@link Operator#AND} operator and proceed to the next step.
     */
    SelectHavingConditionStep andNot(Condition condition);

    /**
     * Combine the currently assembled conditions with an EXISTS clause using
     * the {@link Operator#AND} operator and proceed to the next step.
     */
    SelectHavingConditionStep andExists(Select<?> select);

    /**
     * Combine the currently assembled conditions with a NOT EXISTS clause using
     * the {@link Operator#AND} operator and proceed to the next step.
     */
    SelectHavingConditionStep andNotExists(Select<?> select);

    /**
     * Combine the currently assembled conditions with another one using the
     * {@link Operator#OR} operator and proceed to the next step.
     */
    SelectHavingConditionStep or(Condition condition);

    /**
     * Combine the currently assembled conditions with another one using the
     * {@link Operator#OR} operator and proceed to the next step.
     *
     * @see Factory#plainSQLCondition(String)
     */
    SelectHavingConditionStep or(String sql);

    /**
     * Combine the currently assembled conditions with another one using the
     * {@link Operator#OR} operator and proceed to the next step.
     *
     * @see Factory#plainSQLCondition(String, Object...)
     */
    SelectHavingConditionStep or(String sql, Object... bindings);

    /**
     * Combine the currently assembled conditions with a negated other one using
     * the {@link Operator#OR} operator and proceed to the next step.
     */
    SelectHavingConditionStep orNot(Condition condition);

    /**
     * Combine the currently assembled conditions with an EXISTS clause using
     * the {@link Operator#OR} operator and proceed to the next step.
     */
    SelectHavingConditionStep orExists(Select<?> select);

    /**
     * Combine the currently assembled conditions with a NOT EXISTS clause using
     * the {@link Operator#OR} operator and proceed to the next step.
     */
    SelectHavingConditionStep orNotExists(Select<?> select);
}
