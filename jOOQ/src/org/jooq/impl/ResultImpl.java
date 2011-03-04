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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jooq.ArrayRecord;
import org.jooq.Field;
import org.jooq.FieldProvider;
import org.jooq.Record;
import org.jooq.Result;

/**
 * @author Lukas Eder
 */
class ResultImpl<R extends Record> implements Result<R> {

    /**
     * Generated UID
     */
    private static final long   serialVersionUID = 6416154375799578362L;

    private final FieldProvider fields;
    private final List<R>       records;

    ResultImpl(FieldProvider fields) {
        this.fields = fields;
        this.records = new ArrayList<R>();
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

    @Override
    public final int getNumberOfRecords() {
        return records.size();
    }

    @Override
    public final List<R> getRecords() {
        return Collections.unmodifiableList(records);
    }

    @Override
    public final R getRecord(int index) throws IndexOutOfBoundsException {
        return records.get(index);
    }

    @Override
    public final <T> T getValue(int index, Field<T> field) throws IndexOutOfBoundsException {
        return getRecord(index).getValue(field);
    }

    @Override
    public final <T> T getValue(int index, Field<T> field, T defaultValue) throws IndexOutOfBoundsException {
        return getRecord(index).getValue(field, defaultValue);
    }

    @Override
    public final Object getValue(int index, int fieldIndex) throws IndexOutOfBoundsException {
        return getRecord(index).getValue(fieldIndex);
    }

    @Override
    public final Object getValue(int index, int fieldIndex, Object defaultValue) throws IndexOutOfBoundsException {
        return getRecord(index).getValue(fieldIndex, defaultValue);
    }

    @Override
    public final Object getValue(int index, String fieldName) throws IndexOutOfBoundsException {
        return getRecord(index).getValue(fieldName);
    }

    @Override
    public final Object getValue(int index, String fieldName, Object defaultValue) throws IndexOutOfBoundsException {
        return getRecord(index).getValue(fieldName, defaultValue);
    }

    @Override
    public final <A extends ArrayRecord<T>, T> T[] getValueAsArray(int index, Field<A> field)
        throws IndexOutOfBoundsException {
        return getRecord(index).getValueAsArray(field);
    }

    @Override
    public final <A extends ArrayRecord<T>, T> T[] getValueAsArray(int index, Field<A> field, T[] defaultValue)
        throws IndexOutOfBoundsException {
        return getRecord(index).getValueAsArray(field, defaultValue);
    }

    @Override
    public final String getValueAsString(int index, Field<?> field) throws IllegalArgumentException {
        return getRecord(index).getValueAsString(field);
    }

    @Override
    public final String getValueAsString(int index, Field<?> field, String defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsString(field, defaultValue);
    }

    @Override
    public final String getValueAsString(int index, int fieldIndex) throws IllegalArgumentException {
        return getRecord(index).getValueAsString(fieldIndex);
    }

    @Override
    public final String getValueAsString(int index, int fieldIndex, String defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsString(fieldIndex, defaultValue);
    }

    @Override
    public final Byte getValueAsByte(int index, Field<?> field) throws IllegalArgumentException {
        return getRecord(index).getValueAsByte(field);
    }

    @Override
    public final Byte getValueAsByte(int index, Field<?> field, Byte defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsByte(field, defaultValue);
    }

    @Override
    public final Byte getValueAsByte(int index, int fieldIndex) throws IllegalArgumentException {
        return getRecord(index).getValueAsByte(fieldIndex);
    }

    @Override
    public final Byte getValueAsByte(int index, int fieldIndex, Byte defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsByte(fieldIndex, defaultValue);
    }

    @Override
    public final Short getValueAsShort(int index, Field<?> field) throws IllegalArgumentException {
        return getRecord(index).getValueAsShort(field);
    }

    @Override
    public final Short getValueAsShort(int index, Field<?> field, Short defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsShort(field, defaultValue);
    }

    @Override
    public final Short getValueAsShort(int index, int fieldIndex) throws IllegalArgumentException {
        return getRecord(index).getValueAsShort(fieldIndex);
    }

    @Override
    public final Short getValueAsShort(int index, int fieldIndex, Short defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsShort(fieldIndex, defaultValue);
    }

    @Override
    public final Integer getValueAsInteger(int index, Field<?> field) throws IllegalArgumentException {
        return getRecord(index).getValueAsInteger(field);
    }

    @Override
    public final Integer getValueAsInteger(int index, Field<?> field, Integer defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsInteger(field, defaultValue);
    }

    @Override
    public final Integer getValueAsInteger(int index, int fieldIndex) throws IllegalArgumentException {
        return getRecord(index).getValueAsInteger(fieldIndex);
    }

    @Override
    public final Integer getValueAsInteger(int index, int fieldIndex, Integer defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsInteger(fieldIndex, defaultValue);
    }

    @Override
    public final Long getValueAsLong(int index, Field<?> field) throws IllegalArgumentException {
        return getRecord(index).getValueAsLong(field);
    }

    @Override
    public final Long getValueAsLong(int index, Field<?> field, Long defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsLong(field, defaultValue);
    }

    @Override
    public final Long getValueAsLong(int index, int fieldIndex) throws IllegalArgumentException {
        return getRecord(index).getValueAsLong(fieldIndex);
    }

    @Override
    public final Long getValueAsLong(int index, int fieldIndex, Long defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsLong(fieldIndex, defaultValue);
    }

    @Override
    public final BigInteger getValueAsBigInteger(int index, Field<?> field) throws IllegalArgumentException {
        return getRecord(index).getValueAsBigInteger(field);
    }

    @Override
    public final BigInteger getValueAsBigInteger(int index, Field<?> field, BigInteger defaultValue)
        throws IllegalArgumentException {
        return getRecord(index).getValueAsBigInteger(field, defaultValue);
    }

    @Override
    public final BigInteger getValueAsBigInteger(int index, int fieldIndex) throws IllegalArgumentException {
        return getRecord(index).getValueAsBigInteger(fieldIndex);
    }

    @Override
    public final BigInteger getValueAsBigInteger(int index, int fieldIndex, BigInteger defaultValue)
        throws IllegalArgumentException {
        return getRecord(index).getValueAsBigInteger(fieldIndex, defaultValue);
    }

    @Override
    public final Float getValueAsFloat(int index, Field<?> field) throws IllegalArgumentException {
        return getRecord(index).getValueAsFloat(field);
    }

    @Override
    public final Float getValueAsFloat(int index, Field<?> field, Float defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsFloat(field, defaultValue);
    }

    @Override
    public final Float getValueAsFloat(int index, int fieldIndex) throws IllegalArgumentException {
        return getRecord(index).getValueAsFloat(fieldIndex);
    }

    @Override
    public final Float getValueAsFloat(int index, int fieldIndex, Float defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsFloat(fieldIndex, defaultValue);
    }

    @Override
    public final Double getValueAsDouble(int index, Field<?> field) throws IllegalArgumentException {
        return getRecord(index).getValueAsDouble(field);
    }

    @Override
    public final Double getValueAsDouble(int index, Field<?> field, Double defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsDouble(field, defaultValue);
    }

    @Override
    public final Double getValueAsDouble(int index, int fieldIndex) throws IllegalArgumentException {
        return getRecord(index).getValueAsDouble(fieldIndex);
    }

    @Override
    public final Double getValueAsDouble(int index, int fieldIndex, Double defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsDouble(fieldIndex, defaultValue);
    }

    @Override
    public final BigDecimal getValueAsBigDecimal(int index, Field<?> field) throws IllegalArgumentException {
        return getRecord(index).getValueAsBigDecimal(field);
    }

    @Override
    public final BigDecimal getValueAsBigDecimal(int index, Field<?> field, BigDecimal defaultValue)
        throws IllegalArgumentException {
        return getRecord(index).getValueAsBigDecimal(field, defaultValue);
    }

    @Override
    public final BigDecimal getValueAsBigDecimal(int index, int fieldIndex) throws IllegalArgumentException {
        return getRecord(index).getValueAsBigDecimal(fieldIndex);
    }

    @Override
    public final BigDecimal getValueAsBigDecimal(int index, int fieldIndex, BigDecimal defaultValue)
        throws IllegalArgumentException {
        return getRecord(index).getValueAsBigDecimal(fieldIndex, defaultValue);
    }

    @Override
    public final Timestamp getValueAsTimestamp(int index, Field<?> field) throws IllegalArgumentException {
        return getRecord(index).getValueAsTimestamp(field);
    }

    @Override
    public final Timestamp getValueAsTimestamp(int index, Field<?> field, Timestamp defaultValue)
        throws IllegalArgumentException {
        return getRecord(index).getValueAsTimestamp(field, defaultValue);
    }

    @Override
    public final Timestamp getValueAsTimestamp(int index, int fieldIndex) throws IllegalArgumentException {
        return getRecord(index).getValueAsTimestamp(fieldIndex);
    }

    @Override
    public final Timestamp getValueAsTimestamp(int index, int fieldIndex, Timestamp defaultValue)
        throws IllegalArgumentException {
        return getRecord(index).getValueAsTimestamp(fieldIndex, defaultValue);
    }

    @Override
    public final Date getValueAsDate(int index, Field<?> field) throws IllegalArgumentException {
        return getRecord(index).getValueAsDate(field);
    }

    @Override
    public final Date getValueAsDate(int index, Field<?> field, Date defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsDate(field, defaultValue);
    }

    @Override
    public final Date getValueAsDate(int index, int fieldIndex) throws IllegalArgumentException {
        return getRecord(index).getValueAsDate(fieldIndex);
    }

    @Override
    public final Date getValueAsDate(int index, int fieldIndex, Date defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsDate(fieldIndex, defaultValue);
    }

    @Override
    public final Time getValueAsTime(int index, Field<?> field) throws IllegalArgumentException {
        return getRecord(index).getValueAsTime(field);
    }

    @Override
    public final Time getValueAsTime(int index, Field<?> field, Time defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsTime(field, defaultValue);
    }

    @Override
    public final Time getValueAsTime(int index, int fieldIndex) throws IllegalArgumentException {
        return getRecord(index).getValueAsTime(fieldIndex);
    }

    @Override
    public final Time getValueAsTime(int index, int fieldIndex, Time defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsTime(fieldIndex, defaultValue);
    }

    @Override
    public final String getValueAsString(int index, String fieldName) throws IllegalArgumentException {
        return getRecord(index).getValueAsString(fieldName);
    }

    @Override
    public final String getValueAsString(int index, String fieldName, String defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsString(fieldName, defaultValue);
    }

    @Override
    public final Byte getValueAsByte(int index, String fieldName) throws IllegalArgumentException {
        return getRecord(index).getValueAsByte(fieldName);
    }

    @Override
    public final Byte getValueAsByte(int index, String fieldName, Byte defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsByte(fieldName, defaultValue);
    }

    @Override
    public final Short getValueAsShort(int index, String fieldName) throws IllegalArgumentException {
        return getRecord(index).getValueAsShort(fieldName);
    }

    @Override
    public final Short getValueAsShort(int index, String fieldName, Short defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsShort(fieldName, defaultValue);
    }

    @Override
    public final Integer getValueAsInteger(int index, String fieldName) throws IllegalArgumentException {
        return getRecord(index).getValueAsInteger(fieldName);
    }

    @Override
    public final Integer getValueAsInteger(int index, String fieldName, Integer defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsInteger(fieldName, defaultValue);
    }

    @Override
    public final Long getValueAsLong(int index, String fieldName) throws IllegalArgumentException {
        return getRecord(index).getValueAsLong(fieldName);
    }

    @Override
    public final Long getValueAsLong(int index, String fieldName, Long defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsLong(fieldName, defaultValue);
    }

    @Override
    public final BigInteger getValueAsBigInteger(int index, String fieldName) throws IllegalArgumentException {
        return getRecord(index).getValueAsBigInteger(fieldName);
    }

    @Override
    public final BigInteger getValueAsBigInteger(int index, String fieldName, BigInteger defaultValue)
        throws IllegalArgumentException {
        return getRecord(index).getValueAsBigInteger(fieldName, defaultValue);
    }

    @Override
    public final Float getValueAsFloat(int index, String fieldName) throws IllegalArgumentException {
        return getRecord(index).getValueAsFloat(fieldName);
    }

    @Override
    public final Float getValueAsFloat(int index, String fieldName, Float defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsFloat(fieldName, defaultValue);
    }

    @Override
    public final Double getValueAsDouble(int index, String fieldName) throws IllegalArgumentException {
        return getRecord(index).getValueAsDouble(fieldName);
    }

    @Override
    public final Double getValueAsDouble(int index, String fieldName, Double defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsDouble(fieldName, defaultValue);
    }

    @Override
    public final BigDecimal getValueAsBigDecimal(int index, String fieldName) throws IllegalArgumentException {
        return getRecord(index).getValueAsBigDecimal(fieldName);
    }

    @Override
    public final BigDecimal getValueAsBigDecimal(int index, String fieldName, BigDecimal defaultValue)
        throws IllegalArgumentException {
        return getRecord(index).getValueAsBigDecimal(fieldName, defaultValue);
    }

    @Override
    public final Timestamp getValueAsTimestamp(int index, String fieldName) throws IllegalArgumentException {
        return getRecord(index).getValueAsTimestamp(fieldName);
    }

    @Override
    public final Timestamp getValueAsTimestamp(int index, String fieldName, Timestamp defaultValue)
        throws IllegalArgumentException {
        return getRecord(index).getValueAsTimestamp(fieldName, defaultValue);
    }

    @Override
    public final Date getValueAsDate(int index, String fieldName) throws IllegalArgumentException {
        return getRecord(index).getValueAsDate(fieldName);
    }

    @Override
    public final Date getValueAsDate(int index, String fieldName, Date defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsDate(fieldName, defaultValue);
    }

    @Override
    public final Time getValueAsTime(int index, String fieldName) throws IllegalArgumentException {
        return getRecord(index).getValueAsTime(fieldName);
    }

    @Override
    public final Time getValueAsTime(int index, String fieldName, Time defaultValue) throws IllegalArgumentException {
        return getRecord(index).getValueAsTime(fieldName, defaultValue);
    }
    @Override
    public final <T> List<T> getValues(Field<T> field) {
        List<T> result = new ArrayList<T>();

        for (R record : this) {
            result.add(record.getValue(field));
        }

        return result;
    }

    @Override
    public final List<?> getValues(int fieldIndex) {
        return getValues(getField(fieldIndex));
    }

    @Override
    public final List<?> getValues(String fieldName) {
        return getValues(getField(fieldName));
    }

    @Override
    public final List<BigDecimal> getValuesAsBigDecimal(Field<?> field) {
        List<BigDecimal> result = new ArrayList<BigDecimal>();

        for (R record : this) {
            result.add(record.getValueAsBigDecimal(field));
        }

        return result;
    }

    @Override
    public final List<BigDecimal> getValuesAsBigDecimal(int fieldIndex) {
        return getValuesAsBigDecimal(getField(fieldIndex));
    }

    @Override
    public final List<BigInteger> getValuesAsBigInteger(Field<?> field) {
        List<BigInteger> result = new ArrayList<BigInteger>();

        for (R record : this) {
            result.add(record.getValueAsBigInteger(field));
        }

        return result;
    }

    @Override
    public final List<BigInteger> getValuesAsBigInteger(int fieldIndex) {
        return getValuesAsBigInteger(getField(fieldIndex));
    }

    @Override
    public final List<Byte> getValuesAsByte(Field<?> field) {
        List<Byte> result = new ArrayList<Byte>();

        for (R record : this) {
            result.add(record.getValueAsByte(field));
        }

        return result;
    }

    @Override
    public final List<Byte> getValuesAsByte(int fieldIndex) {
        return getValuesAsByte(getField(fieldIndex));
    }

    @Override
    public final List<Date> getValuesAsDate(Field<?> field) {
        List<Date> result = new ArrayList<Date>();

        for (R record : this) {
            result.add(record.getValueAsDate(field));
        }

        return result;
    }

    @Override
    public final List<Date> getValuesAsDate(int fieldIndex) {
        return getValuesAsDate(getField(fieldIndex));
    }

    @Override
    public final List<Double> getValuesAsDouble(Field<?> field) {
        List<Double> result = new ArrayList<Double>();

        for (R record : this) {
            result.add(record.getValueAsDouble(field));
        }

        return result;
    }

    @Override
    public final List<Double> getValuesAsDouble(int fieldIndex) {
        return getValuesAsDouble(getField(fieldIndex));
    }

    @Override
    public final List<Float> getValuesAsFloat(Field<?> field) {
        List<Float> result = new ArrayList<Float>();

        for (R record : this) {
            result.add(record.getValueAsFloat(field));
        }

        return result;
    }

    @Override
    public final List<Float> getValuesAsFloat(int fieldIndex) {
        return getValuesAsFloat(getField(fieldIndex));
    }

    @Override
    public final List<Integer> getValuesAsInteger(Field<?> field) {
        List<Integer> result = new ArrayList<Integer>();

        for (R record : this) {
            result.add(record.getValueAsInteger(field));
        }

        return result;
    }

    @Override
    public final List<Integer> getValuesAsInteger(int fieldIndex) {
        return getValuesAsInteger(getField(fieldIndex));
    }

    @Override
    public final List<Long> getValuesAsLong(Field<?> field) {
        List<Long> result = new ArrayList<Long>();

        for (R record : this) {
            result.add(record.getValueAsLong(field));
        }

        return result;
    }

    @Override
    public final List<Long> getValuesAsLong(int fieldIndex) {
        return getValuesAsLong(getField(fieldIndex));
    }

    @Override
    public final List<Short> getValuesAsShort(Field<?> field) {
        List<Short> result = new ArrayList<Short>();

        for (R record : this) {
            result.add(record.getValueAsShort(field));
        }

        return result;
    }

    @Override
    public final List<Short> getValuesAsShort(int fieldIndex) {
        return getValuesAsShort(getField(fieldIndex));
    }

    @Override
    public final List<String> getValuesAsString(Field<?> field) {
        List<String> result = new ArrayList<String>();

        for (R record : this) {
            result.add(record.getValueAsString(field));
        }

        return result;
    }

    @Override
    public final List<String> getValuesAsString(int fieldIndex) {
        return getValuesAsString(getField(fieldIndex));
    }

    @Override
    public final List<Time> getValuesAsTime(Field<?> field) {
        List<Time> result = new ArrayList<Time>();

        for (R record : this) {
            result.add(record.getValueAsTime(field));
        }

        return result;
    }

    @Override
    public final List<Time> getValuesAsTime(int fieldIndex) {
        return getValuesAsTime(getField(fieldIndex));
    }

    @Override
    public final List<Timestamp> getValuesAsTimestamp(Field<?> field) {
        List<Timestamp> result = new ArrayList<Timestamp>();

        for (R record : this) {
            result.add(record.getValueAsTimestamp(field));
        }

        return result;
    }

    @Override
    public final List<Timestamp> getValuesAsTimestamp(int fieldIndex) {
        return getValuesAsTimestamp(getField(fieldIndex));
    }

    @Override
    public final List<BigDecimal> getValuesAsBigDecimal(String fieldName) {
        return getValuesAsBigDecimal(getField(fieldName));
    }

    @Override
    public final List<BigInteger> getValuesAsBigInteger(String fieldName) {
        return getValuesAsBigInteger(getField(fieldName));
    }

    @Override
    public final List<Byte> getValuesAsByte(String fieldName) {
        return getValuesAsByte(getField(fieldName));
    }

    @Override
    public final List<Date> getValuesAsDate(String fieldName) {
        return getValuesAsDate(getField(fieldName));
    }

    @Override
    public final List<Double> getValuesAsDouble(String fieldName) {
        return getValuesAsDouble(getField(fieldName));
    }

    @Override
    public final List<Float> getValuesAsFloat(String fieldName) {
        return getValuesAsFloat(getField(fieldName));
    }

    @Override
    public final List<Integer> getValuesAsInteger(String fieldName) {
        return getValuesAsInteger(getField(fieldName));
    }

    @Override
    public final List<Long> getValuesAsLong(String fieldName) {
        return getValuesAsLong(getField(fieldName));
    }

    @Override
    public final List<Short> getValuesAsShort(String fieldName) {
        return getValuesAsShort(getField(fieldName));
    }

    @Override
    public final List<String> getValuesAsString(String fieldName) {
        return getValuesAsString(getField(fieldName));
    }

    @Override
    public final List<Time> getValuesAsTime(String fieldName) {
        return getValuesAsTime(getField(fieldName));
    }

    @Override
    public final List<Timestamp> getValuesAsTimestamp(String fieldName) {
        return getValuesAsTimestamp(getField(fieldName));
    }

    @Override
    public final Iterator<R> iterator() {
        return records.iterator();
    }

    final void addRecord(R record) {
        records.add(record);
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getClass().getSimpleName() + "\n");
        sb.append("Records:\n");

        int i = 0;
        for (; i < 10 && i < getNumberOfRecords(); i++) {
            sb.append(getRecord(i));
            sb.append("\n");
        }

        if (i == 10) {
            sb.append("[...]");
        }

        return sb.toString();
    }
}
