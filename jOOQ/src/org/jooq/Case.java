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

import org.jooq.impl.FunctionFactory;

/**
 * The SQL case statement.
 * <p>
 * This field can be used to render case statements such as <code><pre>
 * CASE x WHEN 1 THEN 'one'
 *        WHEN 2 THEN 'two'
 *        ELSE        'three'
 * END
 * </pre></code> Instances of Case are created through
 * {@link FunctionFactory#decode(Field, Field)} methods
 *
 * @author Lukas Eder
 */
public interface Case<T, V> extends Field<T> {

    /**
     * Add 'when' element to case expression
     *
     * @param compareValue The value to compare the case structure with
     * @param result The result if the case structure equals compareValue
     * @return The case structure itself to add more conditions
     */
    Case<T, V> when(Field<V> compareValue, Field<T> result);

    /**
     * Add 'when' element to case expression
     *
     * @param compareValue The value to compare the case structure with
     * @param result The result if the case structure equals compareValue
     * @return The case structure itself to add more conditions
     */
    Case<T, V> when(V compareValue, Field<T> result);

    /**
     * Add 'when' element to case expression
     *
     * @param compareValue The value to compare the case structure with
     * @param result The result if the case structure equals compareValue
     * @return The case structure itself to add more conditions
     */
    Case<T, V> when(Field<V> compareValue, T result);

    /**
     * Add 'when' element to case expression
     *
     * @param compareValue The value to compare the case structure with
     * @param result The result if the case structure equals compareValue
     * @return The case structure itself to add more conditions
     */
    Case<T, V> when(V compareValue, T result);

    /**
     * Add 'else' element to case expression
     *
     * @param result The result if there was no matching value yet in the case
     *            structure
     * @return The case structure itself as a field. No more conditions can be
     *         added
     */
    Field<T> otherwise(Field<T> result);

    /**
     * Add 'else' element to case expression
     *
     * @param result The result if there was no matching value yet in the case
     *            structure
     * @return The case structure itself as a field. No more conditions can be
     *         added
     */
    Field<T> otherwise(T result);
}
