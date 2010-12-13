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

package org.jooq.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jooq.Field;
import org.jooq.FieldProvider;
import org.jooq.Record;

/**
 * @author Lukas Eder
 */
public class RecordImpl implements Record {

    /**
     * Generated UID
     */
    private static final long       serialVersionUID = -6052512608911220404L;

    private final FieldProvider     fields;
    private Map<Field<?>, Value<?>> values;

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
    public <T> Field<T> getField(Field<T> field) {
        return fields.getField(field);
    }

    @Override
    public Field<?> getField(String name) {
        return fields.getField(name);
    }

    @SuppressWarnings("unchecked")
    protected final <T> Value<T> getValue0(Field<T> field) {
        if (!getValues().containsKey(field)) {
            throw new IllegalArgumentException("Field " + field + " is not contained in Record");
        }

        return (Value<T>) getValues().get(field);
    }

    private final Map<Field<?>, Value<?>> getValues() {
        if (values == null) {
            values = new LinkedHashMap<Field<?>, Value<?>>();

            for (Field<?> field : fields.getFields()) {
                values.put(field, new Value<Object>(null));
            }
        }

        return values;
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
        getValues().put(field, value);
    }

    @Override
    public final boolean hasChangedValues() {
        for (Value<?> value : getValues().values()) {
            if (value.isChanged()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public final String toString() {
        return getClass().getSimpleName() + " [values=" + getValues() + "]";
    }

    @Override
    public final String getStringValue(Field<?> field) throws IllegalArgumentException {
        Object value = getValue(field);
        
        if (value == null) {
            return null;
        }
        if (field.getType() == String.class) {
            return (String) value;
        }
        
        return String.valueOf(value);
    }

    @Override
    public final String getStringValue(Field<?> field, String defaultValue) throws IllegalArgumentException {
        String result = getStringValue(field);
        return result != null ? result : defaultValue;
    }

    @Override
    public final Byte getByteValue(Field<?> field) throws IllegalArgumentException {
        Object value = getValue(field);
        
        if (value == null) {
            return null;
        }
        if (field.getType() == Byte.class) {
            return (Byte) value;
        }
        
        return Byte.valueOf(getStringValue(field));
    }

    @Override
    public final Byte getByteValue(Field<?> field, Byte defaultValue) throws IllegalArgumentException {
        Byte result = getByteValue(field);
        return result != null ? result : defaultValue;
    }

    @Override
    public final Short getShortValue(Field<?> field) throws IllegalArgumentException {
        Object value = getValue(field);
        
        if (value == null) {
            return null;
        }
        if (field.getType() == Short.class) {
            return (Short) value;
        }
        
        return Short.valueOf(getStringValue(field));
    }

    @Override
    public final Short getShortValue(Field<?> field, Short defaultValue) throws IllegalArgumentException {
        Short result = getShortValue(field);
        return result != null ? result : defaultValue;
    }

    @Override
    public final Integer getIntegerValue(Field<?> field) throws IllegalArgumentException {
        Object value = getValue(field);
        
        if (value == null) {
            return null;
        }
        if (field.getType() == Integer.class) {
            return (Integer) value;
        }
        
        return Integer.valueOf(getStringValue(field));
    }

    @Override
    public final Integer getIntegerValue(Field<?> field, Integer defaultValue) throws IllegalArgumentException {
        Integer result = getIntegerValue(field);
        return result != null ? result : defaultValue;
    }

    @Override
    public final Long getLongValue(Field<?> field) throws IllegalArgumentException {
        Object value = getValue(field);
        
        if (value == null) {
            return null;
        }
        if (field.getType() == Long.class) {
            return (Long) value;
        }
        if (value instanceof java.util.Date) {
            return ((java.util.Date) value).getTime();
        }
        
        return Long.valueOf(getStringValue(field));
    }

    @Override
    public final Long getLongValue(Field<?> field, Integer defaultValue) throws IllegalArgumentException {
        Long result = getLongValue(field);
        return result != null ? result : defaultValue;
    }

    @Override
    public final BigInteger getBigIntegerValue(Field<?> field) throws IllegalArgumentException {
        Object value = getValue(field);
        
        if (value == null) {
            return null;
        }
        if (field.getType() == BigInteger.class) {
            return (BigInteger) value;
        }
        
        return new BigInteger(getStringValue(field));
    }

    @Override
    public final BigInteger getBigIntegerValue(Field<?> field, BigInteger defaultValue) throws IllegalArgumentException {
        BigInteger result = getBigIntegerValue(field);
        return result != null ? result : defaultValue;
    }

    @Override
    public final Float getFloatValue(Field<?> field) throws IllegalArgumentException {
        Object value = getValue(field);
        
        if (value == null) {
            return null;
        }
        if (field.getType() == Float.class) {
            return (Float) value;
        }
        
        return Float.valueOf(getStringValue(field));
    }

    @Override
    public final Float getFloatValue(Field<?> field, Float defaultValue) throws IllegalArgumentException {
        Float result = getFloatValue(field);
        return result != null ? result : defaultValue;
    }

    @Override
    public final Double getDoubleValue(Field<?> field) throws IllegalArgumentException {
        Object value = getValue(field);
        
        if (value == null) {
            return null;
        }
        if (field.getType() == Double.class) {
            return (Double) value;
        }
        
        return Double.valueOf(getStringValue(field));
    }

    @Override
    public final Double getDoubleValue(Field<?> field, Double defaultValue) throws IllegalArgumentException {
        Double result = getDoubleValue(field);
        return result != null ? result : defaultValue;
    }

    @Override
    public final BigDecimal getBigDecimalValue(Field<?> field) throws IllegalArgumentException {
        Object value = getValue(field);
        
        if (value == null) {
            return null;
        }
        if (field.getType() == BigDecimal.class) {
            return (BigDecimal) value;
        }
        
        return new BigDecimal(getStringValue(field));
    }

    @Override
    public final BigDecimal getBigDecimalValue(Field<?> field, BigDecimal defaultValue) throws IllegalArgumentException {
        BigDecimal result = getBigDecimalValue(field);
        return result != null ? result : defaultValue;
    }

    @Override
    public final Timestamp getTimestampValue(Field<?> field) throws IllegalArgumentException {
        Object value = getValue(field);
        
        if (value == null) {
            return null;
        }
        if (field.getType() == Timestamp.class) {
            return (Timestamp) value;
        }
        if (field.getType() == Long.class) {
            return new Timestamp((Long) value);
        }
        
        return new Timestamp(((java.util.Date) value).getTime());
    }

    @Override
    public final Timestamp getTimestampValue(Field<?> field, Timestamp defaultValue) throws IllegalArgumentException {
        Timestamp result = getTimestampValue(field);
        return result != null ? result : defaultValue;
    }

    @Override
    public final Date getDateValue(Field<?> field) throws IllegalArgumentException {
        Object value = getValue(field);
        
        if (value == null) {
            return null;
        }
        if (field.getType() == Date.class) {
            return (Date) value;
        }
        if (field.getType() == Long.class) {
            return new Date((Long) value);
        }
        
        return new Date(((java.util.Date) value).getTime());
    }

    @Override
    public final Date getDateValue(Field<?> field, Date defaultValue) throws IllegalArgumentException {
        Date result = getDateValue(field);
        return result != null ? result : defaultValue;
    }

    @Override
    public final Time getTimeValue(Field<?> field) throws IllegalArgumentException {
        Object value = getValue(field);
        
        if (value == null) {
            return null;
        }
        if (field.getType() == Time.class) {
            return (Time) value;
        }
        if (field.getType() == Long.class) {
            return new Time((Long) value);
        }
        
        return new Time(((java.util.Date) value).getTime());
    }

    @Override
    public final Time getTimeValue(Field<?> field, Time defaultValue) throws IllegalArgumentException {
        Time result = getTimeValue(field);
        return result != null ? result : defaultValue;
    }
}
