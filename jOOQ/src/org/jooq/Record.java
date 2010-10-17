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

/**
 * A wrapper for database result records returned by
 * <code>{@link SelectQuery}</code>
 *
 * @author Lukas Eder
 * @see SelectQuery#getResult()
 */
public interface Record extends FieldProvider {

    /**
     * @param <T> The generic field parameter
     * @param field The field
     * @return The value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    <T> T getValue(Field<T> field) throws IllegalArgumentException;

    /**
     * @param <T> The generic field parameter
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The value of a field contained in this record, or defaultValue, if
     *         <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    <T> T getValue(Field<T> field, T defaultValue) throws IllegalArgumentException;

    /**
     * @param <T> The generic field parameter
     * @param field The field
     * @param value The value
     */
    <T> void setValue(Field<T> field, T value);

    /**
     * @return Whether any values have been changed since the record was loaded.
     */
    boolean hasChangedValues();
}
