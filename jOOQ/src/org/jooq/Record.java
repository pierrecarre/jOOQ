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
 * A wrapper for database result records returned by
 * <code>{@link SelectQuery}</code>
 *
 * @author Lukas Eder
 * @see SelectQuery#getResult()
 */
public interface Record extends FieldProvider, Store<Object> {

    /**
     * Get a value from this Record, providing a field.
     *
     * @param <T> The generic field parameter
     * @param field The field
     * @return The value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    <T> T getValue(Field<T> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param <T> The generic field parameter
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The value of a field contained in this record, or defaultValue,
     *         if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    <T> T getValue(Field<T> field, T defaultValue) throws IllegalArgumentException;

    /**
     * Get an array value from this Record, providing an {@link ArrayRecord}
     * field.
     *
     * @param <A> The generic field parameter
     * @param <T> The {@link ArrayRecord} type parameter
     * @param field The field
     * @return The value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    <A extends ArrayRecord<T>, T> T[] getValueAsArray(Field<A> field) throws IllegalArgumentException;

    /**
     * Get an array value from this Record, providing an {@link ArrayRecord}
     * field.
     *
     * @param <A> The generic field parameter
     * @param <T> The {@link ArrayRecord} type parameter
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    <A extends ArrayRecord<T>, T> T[] getValueAsArray(Field<A> field, T[] defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field name.
     *
     * @param fieldName The field's name
     * @return The value of a field's name contained in this record
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    Object getValue(String fieldName) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field name.
     *
     * @param fieldName The field's name
     * @param defaultValue The default value instead of <code>null</code>
     * @return The value of a field's name contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    Object getValue(String fieldName, Object defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getValueAsString(Field)} instead
     */
    @Deprecated
    String getStringValue(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getValueAsString(Field, String)} instead
     */
    @Deprecated
    String getStringValue(Field<?> field, String defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getValueAsByte(Field)} instead
     */
    @Deprecated
    Byte getByteValue(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getValueAsByte(Field, Byte)} instead
     */
    @Deprecated
    Byte getByteValue(Field<?> field, Byte defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getvalueAsShort(Field)} instead
     */
    @Deprecated
    Short getShortValue(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getvalueAsShort(Field, Short)} instead
     */
    @Deprecated
    Short getShortValue(Field<?> field, Short defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getvalueAsInteger(Field)} instead
     */
    @Deprecated
    Integer getIntegerValue(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getvalueAsInteger(Field, Integer)} instead
     */
    @Deprecated
    Integer getIntegerValue(Field<?> field, Integer defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getvalueAsLong(Field)} instead
     */
    @Deprecated
    Long getLongValue(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getvalueAsLong(Field, Long)} instead
     */
    @Deprecated
    Long getLongValue(Field<?> field, Long defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getvalueAsBigInteger(Field)} instead
     */
    @Deprecated
    BigInteger getBigIntegerValue(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getvalueAsBigInteger(Field, BigInteger)}
     *             instead
     */
    @Deprecated
    BigInteger getBigIntegerValue(Field<?> field, BigInteger defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getvalueAsFloat(Field)} instead
     */
    @Deprecated
    Float getFloatValue(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getvalueAsFloat(Field, Float)} instead
     */
    @Deprecated
    Float getFloatValue(Field<?> field, Float defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getvalueAsDouble(Field)} instead
     */
    @Deprecated
    Double getDoubleValue(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getvalueAsDouble(Field, Double)} instead
     */
    @Deprecated
    Double getDoubleValue(Field<?> field, Double defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getvalueAsBigDecimal(Field)} instead
     */
    @Deprecated
    BigDecimal getBigDecimalValue(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getvalueAsBigDecimal(Field, BigDecimal)}
     *             instead
     */
    @Deprecated
    BigDecimal getBigDecimalValue(Field<?> field, BigDecimal defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getvalueAsTimestamp(Field)} instead
     */
    @Deprecated
    Timestamp getTimestampValue(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getvalueAsTimestamp(Field, Timestamp)} instead
     */
    @Deprecated
    Timestamp getTimestampValue(Field<?> field, Timestamp defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getvalueAsDate(Field)} instead
     */
    @Deprecated
    Date getDateValue(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getvalueAsDate(Field, Date)} instead
     */
    @Deprecated
    Date getDateValue(Field<?> field, Date defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getvalueAsTime(Field)} instead
     */
    @Deprecated
    Time getTimeValue(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     * @deprecated - use {@link #getvalueAsTime(Field, Time)} instead
     */
    @Deprecated
    Time getTimeValue(Field<?> field, Time defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    String getValueAsString(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    String getValueAsString(Field<?> field, String defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field name.
     *
     * @param fieldName The field's name
     * @return The converted value of a field's index contained in this record
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    String getValueAsString(String fieldName) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field name.
     *
     * @param fieldName The field's name
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's name contained in this record,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    String getValueAsString(String fieldName, String defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    Byte getValueAsByte(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    Byte getValueAsByte(Field<?> field, Byte defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field name.
     *
     * @param fieldName The field's name
     * @return The converted value of a field's index contained in this record
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    Byte getValueAsByte(String fieldName) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field name.
     *
     * @param fieldName The field's name
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's name contained in this record,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    Byte getValueAsByte(String fieldName, Byte defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    Short getValueAsShort(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    Short getValueAsShort(Field<?> field, Short defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field name.
     *
     * @param fieldName The field's name
     * @return The converted value of a field's index contained in this record
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    Short getValueAsShort(String fieldName) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field name.
     *
     * @param fieldName The field's name
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's name contained in this record,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    Short getValueAsShort(String fieldName, Short defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    Integer getValueAsInteger(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    Integer getValueAsInteger(Field<?> field, Integer defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field name.
     *
     * @param fieldName The field's name
     * @return The converted value of a field's index contained in this record
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    Integer getValueAsInteger(String fieldName) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field name.
     *
     * @param fieldName The field's name
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's name contained in this record,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    Integer getValueAsInteger(String fieldName, Integer defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    Long getValueAsLong(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    Long getValueAsLong(Field<?> field, Long defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field name.
     *
     * @param fieldName The field's name
     * @return The converted value of a field's index contained in this record
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    Long getValueAsLong(String fieldName) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field name.
     *
     * @param fieldName The field's name
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's name contained in this record,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    Long getValueAsLong(String fieldName, Long defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    BigInteger getValueAsBigInteger(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    BigInteger getValueAsBigInteger(Field<?> field, BigInteger defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field name.
     *
     * @param fieldName The field's name
     * @return The converted value of a field's index contained in this record
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    BigInteger getValueAsBigInteger(String fieldName) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field name.
     *
     * @param fieldName The field's name
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's name contained in this record,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    BigInteger getValueAsBigInteger(String fieldName, BigInteger defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    Float getValueAsFloat(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    Float getValueAsFloat(Field<?> field, Float defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field name.
     *
     * @param fieldName The field's name
     * @return The converted value of a field's index contained in this record
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    Float getValueAsFloat(String fieldName) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field name.
     *
     * @param fieldName The field's name
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's name contained in this record,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    Float getValueAsFloat(String fieldName, Float defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    Double getValueAsDouble(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    Double getValueAsDouble(Field<?> field, Double defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field name.
     *
     * @param fieldName The field's name
     * @return The converted value of a field's index contained in this record
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    Double getValueAsDouble(String fieldName) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field name.
     *
     * @param fieldName The field's name
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's name contained in this record,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    Double getValueAsDouble(String fieldName, Double defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    BigDecimal getValueAsBigDecimal(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    BigDecimal getValueAsBigDecimal(Field<?> field, BigDecimal defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field name.
     *
     * @param fieldName The field's name
     * @return The converted value of a field's index contained in this record
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    BigDecimal getValueAsBigDecimal(String fieldName) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field name.
     *
     * @param fieldName The field's name
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's name contained in this record,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    BigDecimal getValueAsBigDecimal(String fieldName, BigDecimal defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    Timestamp getValueAsTimestamp(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    Timestamp getValueAsTimestamp(Field<?> field, Timestamp defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field name.
     *
     * @param fieldName The field's name
     * @return The converted value of a field's index contained in this record
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    Timestamp getValueAsTimestamp(String fieldName) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field name.
     *
     * @param fieldName The field's name
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's name contained in this record,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    Timestamp getValueAsTimestamp(String fieldName, Timestamp defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    Date getValueAsDate(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    Date getValueAsDate(Field<?> field, Date defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field name.
     *
     * @param fieldName The field's name
     * @return The converted value of a field's index contained in this record
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    Date getValueAsDate(String fieldName) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field name.
     *
     * @param fieldName The field's name
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's name contained in this record,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    Date getValueAsDate(String fieldName, Date defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field.
     *
     * @param field The field
     * @return The converted value of a field contained in this record
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    Time getValueAsTime(Field<?> field) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field.
     *
     * @param field The field
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field contained in this record, or
     *         defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument field is not contained
     *             in {@link #getFields()}
     */
    Time getValueAsTime(Field<?> field, Time defaultValue) throws IllegalArgumentException;

    /**
     * Get a value from this Record, providing a field name.
     *
     * @param fieldName The field's name
     * @return The converted value of a field's index contained in this record
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    Time getValueAsTime(String fieldName) throws IllegalArgumentException;

    /**
     * Get a value from this record, providing a field name.
     *
     * @param fieldName The field's name
     * @param defaultValue The default value instead of <code>null</code>
     * @return The converted value of a field's name contained in this record,
     *         or defaultValue, if <code>null</code>
     * @throws IllegalArgumentException If the argument fieldName is not
     *             contained in the record
     */
    Time getValueAsTime(String fieldName, Time defaultValue) throws IllegalArgumentException;

    /**
     * Set a value into this record.
     *
     * @param <T> The generic field parameter
     * @param field The field
     * @param value The value
     */
    <T> void setValue(Field<T> field, T value);

    /**
     * Whether any values have been changed since the record was loaded.
     *
     * @deprecated - This method is part of the internal API and will be removed
     *             in the future.
     */
    @Deprecated
    boolean hasChangedValues();
}
