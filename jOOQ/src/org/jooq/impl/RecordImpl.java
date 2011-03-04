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

package org.jooq.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jooq.ArrayRecord;
import org.jooq.Field;
import org.jooq.FieldProvider;
import org.jooq.Record;

/**
 * @author Lukas Eder
 */
public class RecordImpl extends AbstractStore<Object> implements Record {

    /**
     * Generated UID
     */
    private static final long      serialVersionUID = -6052512608911220404L;

    private final FieldProvider    fields;
    private Value<?>[]             values;
    private Map<Field<?>, Integer> indexes;

    public RecordImpl(FieldProvider fields) {
        this.fields = fields;
    }

    FieldProvider getMetaData() {
        return fields;
    }

    @Override
    public final List<Field<?>> getFields() {
        return fields.getFields();
    }

    @Override
    public final <T> Field<T> getField(Field<T> field) {
        return fields.getField(field);
    }

    @Override
    public final Field<?> getField(String name) {
        return fields.getField(name);
    }

    @Override
    public final Field<?> getField(int index) {
        return fields.getField(index);
    }

    @SuppressWarnings("unchecked")
    protected final <T> Value<T> getValue0(Field<T> field) {
        if (!getIndexes().containsKey(field)) {
            if (!getIndexes().containsKey(getField(field))) {
                throw new IllegalArgumentException("Field " + field + " is not contained in Record");
            }

            return (Value<T>) getValues()[getIndexes().get(getField(field))];
        }

        return (Value<T>) getValues()[getIndexes().get(field)];
    }

    private final Value<?>[] getValues() {
        if (values == null) {
            init();
        }

        return values;
    }

    private final Map<Field<?>, Integer> getIndexes() {
        if (indexes == null) {
            init();
        }

        return indexes;
    }

    private void init() {
        List<Field<?>> list = fields.getFields();

        values = new Value<?>[list.size()];
        indexes = new LinkedHashMap<Field<?>, Integer>();

        for (int i = 0; i < list.size(); i++) {
            values[i] = new Value<Object>(null);
            indexes.put(list.get(i), i);
        }
    }

    @Override
    public final <T> T getValue(Field<T> field) throws IllegalArgumentException {
        return getValue0(field).getValue();
    }

    @Override
    public final <T> T getValue(Field<T> field, T defaultValue) throws IllegalArgumentException {
        return getValue0(field).getValue(defaultValue);
    }

    @Override
    public final <T> void setValue(Field<T> field, T value) {
        getValue0(field).setValue(value);
    }

    final <T> void setValue(Field<T> field, Value<T> value) {
        getValues()[getIndexes().get(field)] = value;
    }

    @Override
    public final boolean hasChangedValues() {
        for (Value<?> value : getValues()) {
            if (value.isChanged()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public final String toString() {
        return getClass().getSimpleName() + " [values=" + Arrays.asList(getValues()) + "]";
    }

    @Override
    @Deprecated
    public final String getStringValue(Field<?> field) throws IllegalArgumentException {
        return getValueAsString(field);
    }

    @Override
    @Deprecated
    public final String getStringValue(Field<?> field, String defaultValue) throws IllegalArgumentException {
        return getValueAsString(field, defaultValue);
    }

    @Override
    @Deprecated
    public final Byte getByteValue(Field<?> field) throws IllegalArgumentException {
        return getValueAsByte(field);
    }

    @Override
    @Deprecated
    public final Byte getByteValue(Field<?> field, Byte defaultValue) throws IllegalArgumentException {
        return getValueAsByte(field, defaultValue);
    }

    @Override
    @Deprecated
    public final Short getShortValue(Field<?> field) throws IllegalArgumentException {
        return getValueAsShort(field);
    }

    @Override
    @Deprecated
    public final Short getShortValue(Field<?> field, Short defaultValue) throws IllegalArgumentException {
        return getValueAsShort(field, defaultValue);
    }

    @Override
    @Deprecated
    public final Integer getIntegerValue(Field<?> field) throws IllegalArgumentException {
        return getValueAsInteger(field);
    }

    @Override
    @Deprecated
    public final Integer getIntegerValue(Field<?> field, Integer defaultValue) throws IllegalArgumentException {
        return getValueAsInteger(field, defaultValue);
    }

    @Override
    @Deprecated
    public final Long getLongValue(Field<?> field) throws IllegalArgumentException {
        return getValueAsLong(field);
    }

    @Override
    @Deprecated
    public final Long getLongValue(Field<?> field, Long defaultValue) throws IllegalArgumentException {
        return getValueAsLong(field, defaultValue);
    }

    @Override
    @Deprecated
    public final BigInteger getBigIntegerValue(Field<?> field) throws IllegalArgumentException {
        return getValueAsBigInteger(field);
    }

    @Override
    @Deprecated
    public final BigInteger getBigIntegerValue(Field<?> field, BigInteger defaultValue) throws IllegalArgumentException {
        return getValueAsBigInteger(field, defaultValue);
    }

    @Override
    @Deprecated
    public final Float getFloatValue(Field<?> field) throws IllegalArgumentException {
        return getValueAsFloat(field);
    }

    @Override
    @Deprecated
    public final Float getFloatValue(Field<?> field, Float defaultValue) throws IllegalArgumentException {
        return getValueAsFloat(field, defaultValue);
    }

    @Override
    @Deprecated
    public final Double getDoubleValue(Field<?> field) throws IllegalArgumentException {
        return getValueAsDouble(field);
    }

    @Override
    @Deprecated
    public final Double getDoubleValue(Field<?> field, Double defaultValue) throws IllegalArgumentException {
        return getValueAsDouble(field, defaultValue);
    }

    @Override
    @Deprecated
    public final BigDecimal getBigDecimalValue(Field<?> field) throws IllegalArgumentException {
        return getValueAsBigDecimal(field);
    }

    @Override
    @Deprecated
    public final BigDecimal getBigDecimalValue(Field<?> field, BigDecimal defaultValue) throws IllegalArgumentException {
        return getValueAsBigDecimal(field, defaultValue);
    }

    @Override
    @Deprecated
    public final Timestamp getTimestampValue(Field<?> field) throws IllegalArgumentException {
        return getValueAsTimestamp(field);
    }

    @Override
    @Deprecated
    public final Timestamp getTimestampValue(Field<?> field, Timestamp defaultValue) throws IllegalArgumentException {
        return getValueAsTimestamp(field, defaultValue);
    }

    @Override
    @Deprecated
    public final Date getDateValue(Field<?> field) throws IllegalArgumentException {
        return getValueAsDate(field);
    }

    @Override
    @Deprecated
    public final Date getDateValue(Field<?> field, Date defaultValue) throws IllegalArgumentException {
        return getValueAsDate(field);
    }

    @Override
    @Deprecated
    public final Time getTimeValue(Field<?> field) throws IllegalArgumentException {
        return getValueAsTime(field);
    }

    @Override
    @Deprecated
    public final Time getTimeValue(Field<?> field, Time defaultValue) throws IllegalArgumentException {
        return getValueAsTime(field, defaultValue);
    }

    @Override
    public final String getValueAsString(Field<?> field) throws IllegalArgumentException {
        return getValueAsString(getIndexes().get(field));
    }

    @Override
    public final String getValueAsString(Field<?> field, String defaultValue) throws IllegalArgumentException {
        return getValueAsString(getIndexes().get(field), defaultValue);
    }

    @Override
    public final Byte getValueAsByte(Field<?> field) throws IllegalArgumentException {
        return getValueAsByte(getIndexes().get(field));
    }

    @Override
    public final Byte getValueAsByte(Field<?> field, Byte defaultValue) throws IllegalArgumentException {
        return getValueAsByte(getIndexes().get(field), defaultValue);
    }

    @Override
    public final Short getValueAsShort(Field<?> field) throws IllegalArgumentException {
        return getValueAsShort(getIndexes().get(field));
    }

    @Override
    public final Short getValueAsShort(Field<?> field, Short defaultValue) throws IllegalArgumentException {
        return getValueAsShort(getIndexes().get(field), defaultValue);
    }

    @Override
    public final Integer getValueAsInteger(Field<?> field) throws IllegalArgumentException {
        return getValueAsInteger(getIndexes().get(field));
    }

    @Override
    public final Integer getValueAsInteger(Field<?> field, Integer defaultValue) throws IllegalArgumentException {
        return getValueAsInteger(getIndexes().get(field), defaultValue);
    }

    @Override
    public final Long getValueAsLong(Field<?> field) throws IllegalArgumentException {
        return getValueAsLong(getIndexes().get(field));
    }

    @Override
    public final Long getValueAsLong(Field<?> field, Long defaultValue) throws IllegalArgumentException {
        return getValueAsLong(getIndexes().get(field), defaultValue);
    }

    @Override
    public final BigInteger getValueAsBigInteger(Field<?> field) throws IllegalArgumentException {
        return getValueAsBigInteger(getIndexes().get(field));
    }

    @Override
    public final BigInteger getValueAsBigInteger(Field<?> field, BigInteger defaultValue)
        throws IllegalArgumentException {
        return getValueAsBigInteger(getIndexes().get(field), defaultValue);
    }

    @Override
    public final Float getValueAsFloat(Field<?> field) throws IllegalArgumentException {
        return getValueAsFloat(getIndexes().get(field));
    }

    @Override
    public final Float getValueAsFloat(Field<?> field, Float defaultValue) throws IllegalArgumentException {
        return getValueAsFloat(getIndexes().get(field), defaultValue);
    }

    @Override
    public final Double getValueAsDouble(Field<?> field) throws IllegalArgumentException {
        return getValueAsDouble(getIndexes().get(field));
    }

    @Override
    public final Double getValueAsDouble(Field<?> field, Double defaultValue) throws IllegalArgumentException {
        return getValueAsDouble(getIndexes().get(field), defaultValue);
    }

    @Override
    public final BigDecimal getValueAsBigDecimal(Field<?> field) throws IllegalArgumentException {
        return getValueAsBigDecimal(getIndexes().get(field));
    }

    @Override
    public final BigDecimal getValueAsBigDecimal(Field<?> field, BigDecimal defaultValue)
        throws IllegalArgumentException {
        return getValueAsBigDecimal(getIndexes().get(field), defaultValue);
    }

    @Override
    public final Timestamp getValueAsTimestamp(Field<?> field) throws IllegalArgumentException {
        return getValueAsTimestamp(getIndexes().get(field));
    }

    @Override
    public final Timestamp getValueAsTimestamp(Field<?> field, Timestamp defaultValue) throws IllegalArgumentException {
        return getValueAsTimestamp(getIndexes().get(field), defaultValue);
    }

    @Override
    public final Date getValueAsDate(Field<?> field) throws IllegalArgumentException {
        return getValueAsDate(getIndexes().get(field));
    }

    @Override
    public final Date getValueAsDate(Field<?> field, Date defaultValue) throws IllegalArgumentException {
        return getValueAsDate(getIndexes().get(field), defaultValue);
    }

    @Override
    public final Time getValueAsTime(Field<?> field) throws IllegalArgumentException {
        return getValueAsTime(getIndexes().get(field));
    }

    @Override
    public final Time getValueAsTime(Field<?> field, Time defaultValue) throws IllegalArgumentException {
        return getValueAsTime(getIndexes().get(field), defaultValue);
    }

    @Override
    public final Object getValue(int index) throws IllegalArgumentException {
        return getValues()[index].getValue();
    }

    @Override
    public final Object getValue(String fieldName) throws IllegalArgumentException {
        return getValue(getField(fieldName));
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Object getValue(String fieldName, Object defaultValue) throws IllegalArgumentException {
        return getValue((Field<Object>) getField(fieldName), defaultValue);
    }

    @Override
    public final <A extends ArrayRecord<T>, T> T[] getValueAsArray(Field<A> field) throws IllegalArgumentException {
        A result = getValue(field);
        return result == null ? null : result.get();
    }

    @Override
    public final <A extends ArrayRecord<T>, T> T[] getValueAsArray(Field<A> field, T[] defaultValue)
        throws IllegalArgumentException {
        final T[] result = getValueAsArray(field);
        return result == null ? defaultValue : result;
    }

    @Override
    public final String getValueAsString(String fieldName) throws IllegalArgumentException {
        return getValueAsString(getField(fieldName));
    }

    @Override
    public final String getValueAsString(String fieldName, String defaultValue) throws IllegalArgumentException {
        return getValueAsString(getField(fieldName), defaultValue);
    }

    @Override
    public final Byte getValueAsByte(String fieldName) throws IllegalArgumentException {
        return getValueAsByte(getField(fieldName));
    }

    @Override
    public final Byte getValueAsByte(String fieldName, Byte defaultValue) throws IllegalArgumentException {
        return getValueAsByte(getField(fieldName), defaultValue);
    }

    @Override
    public final Short getValueAsShort(String fieldName) throws IllegalArgumentException {
        return getValueAsShort(getField(fieldName));
    }

    @Override
    public final Short getValueAsShort(String fieldName, Short defaultValue) throws IllegalArgumentException {
        return getValueAsShort(getField(fieldName), defaultValue);
    }

    @Override
    public final Integer getValueAsInteger(String fieldName) throws IllegalArgumentException {
        return getValueAsInteger(getField(fieldName));
    }

    @Override
    public final Integer getValueAsInteger(String fieldName, Integer defaultValue) throws IllegalArgumentException {
        return getValueAsInteger(getField(fieldName), defaultValue);
    }

    @Override
    public final Long getValueAsLong(String fieldName) throws IllegalArgumentException {
        return getValueAsLong(getField(fieldName));
    }

    @Override
    public final Long getValueAsLong(String fieldName, Long defaultValue) throws IllegalArgumentException {
        return getValueAsLong(getField(fieldName), defaultValue);
    }

    @Override
    public final BigInteger getValueAsBigInteger(String fieldName) throws IllegalArgumentException {
        return getValueAsBigInteger(getField(fieldName));
    }

    @Override
    public final BigInteger getValueAsBigInteger(String fieldName, BigInteger defaultValue)
        throws IllegalArgumentException {
        return getValueAsBigInteger(getField(fieldName), defaultValue);
    }

    @Override
    public final Float getValueAsFloat(String fieldName) throws IllegalArgumentException {
        return getValueAsFloat(getField(fieldName));
    }

    @Override
    public final Float getValueAsFloat(String fieldName, Float defaultValue) throws IllegalArgumentException {
        return getValueAsFloat(getField(fieldName), defaultValue);
    }

    @Override
    public final Double getValueAsDouble(String fieldName) throws IllegalArgumentException {
        return getValueAsDouble(getField(fieldName));
    }

    @Override
    public final Double getValueAsDouble(String fieldName, Double defaultValue) throws IllegalArgumentException {
        return getValueAsDouble(getField(fieldName), defaultValue);
    }

    @Override
    public final BigDecimal getValueAsBigDecimal(String fieldName) throws IllegalArgumentException {
        return getValueAsBigDecimal(getField(fieldName));
    }

    @Override
    public final BigDecimal getValueAsBigDecimal(String fieldName, BigDecimal defaultValue)
        throws IllegalArgumentException {
        return getValueAsBigDecimal(getField(fieldName), defaultValue);
    }

    @Override
    public final Timestamp getValueAsTimestamp(String fieldName) throws IllegalArgumentException {
        return getValueAsTimestamp(getField(fieldName));
    }

    @Override
    public final Timestamp getValueAsTimestamp(String fieldName, Timestamp defaultValue)
        throws IllegalArgumentException {
        return getValueAsTimestamp(getField(fieldName), defaultValue);
    }

    @Override
    public final Date getValueAsDate(String fieldName) throws IllegalArgumentException {
        return getValueAsDate(getField(fieldName));
    }

    @Override
    public final Date getValueAsDate(String fieldName, Date defaultValue) throws IllegalArgumentException {
        return getValueAsDate(getField(fieldName), defaultValue);
    }

    @Override
    public final Time getValueAsTime(String fieldName) throws IllegalArgumentException {
        return getValueAsTime(getField(fieldName));
    }

    @Override
    public final Time getValueAsTime(String fieldName, Time defaultValue) throws IllegalArgumentException {
        return getValueAsTime(getField(fieldName), defaultValue);
    }
}
