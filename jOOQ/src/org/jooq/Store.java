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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * A common base type for {@link Store} and {@link ArrayStore} providing
 * common, index-based functionality for storage objects
 *
 * @author Lukas Eder
 */
public interface Store<T> {

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @return The value of a field's index contained in this Store
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    T getValue(int index) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @param defaultValue The default value instead of <code>null</code>
     * @return The value of a field's index contained in this Store, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    T getValue(int index, T defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @return The converted value of a field's index contained in this Store
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    BigDecimal getValueAsBigDecimal(int index) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's index contained in this Store,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    BigDecimal getValueAsBigDecimal(int index, BigDecimal defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @return The converted value of a field's index contained in this Store
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    BigInteger getValueAsBigInteger(int index) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's index contained in this Store,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    BigInteger getValueAsBigInteger(int index, BigInteger defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @return The converted value of a field's index contained in this Store
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    Byte getValueAsByte(int index) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's index contained in this Store,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    Byte getValueAsByte(int index, Byte defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @return The converted value of a field's index contained in this Store
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    Date getValueAsDate(int index) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's index contained in this Store,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    Date getValueAsDate(int index, Date defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @return The converted value of a field's index contained in this Store
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    Double getValueAsDouble(int index) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's index contained in this Store,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    Double getValueAsDouble(int index, Double defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @return The converted value of a field's index contained in this Store
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    Float getValueAsFloat(int index) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's index contained in this Store,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    Float getValueAsFloat(int index, Float defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @return The converted value of a field's index contained in this Store
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    Integer getValueAsInteger(int index) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's index contained in this Store,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    Integer getValueAsInteger(int index, Integer defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @return The converted value of a field's index contained in this Store
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    Long getValueAsLong(int index) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's index contained in this Store,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    Long getValueAsLong(int index, Long defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @return The converted value of a field's index contained in this Store
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    Short getValueAsShort(int index) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's index contained in this Store,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    Short getValueAsShort(int index, Short defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @return The converted value of a field's index contained in this Store
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    String getValueAsString(int index) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's index contained in this Store,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    String getValueAsString(int index, String defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @return The converted value of a field's index contained in this Store
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    Time getValueAsTime(int index) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's index contained in this Store,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    Time getValueAsTime(int index, Time defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @return The converted value of a field's index contained in this Store
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    Timestamp getValueAsTimestamp(int index) throws IllegalArgumentException;

    /**
     * Get a value from this Store, providing a field index.
     *
     * @param index The field's index
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's index contained in this Store,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument index is not contained
     *             in the Store
     */
    Timestamp getValueAsTimestamp(int index, Timestamp defaultValue) throws IllegalArgumentException;

}
