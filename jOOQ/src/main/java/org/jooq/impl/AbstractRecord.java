/**
 * Copyright (c) 2009-2013, Data Geekery GmbH (http://www.datageekery.com)
 * All rights reserved.
 *
 * This work is dual-licensed
 * - under the Apache Software License 2.0 (the "ASL")
 * - under the jOOQ License and Maintenance Agreement (the "jOOQ License")
 * =============================================================================
 * You may choose which license applies to you:
 *
 * - If you're using this work with Open Source databases, you may choose
 *   either ASL or jOOQ License.
 * - If you're using this work with at least one commercial database, you must
 *   choose jOOQ License
 *
 * For more information, please visit http://www.jooq.org/licenses
 *
 * Apache Software License 2.0:
 * -----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * jOOQ License and Maintenance Agreement:
 * -----------------------------------------------------------------------------
 * Data Geekery grants the Customer the non-exclusive, timely limited and
 * non-transferable license to install and use the Software under the terms of
 * the jOOQ License and Maintenance Agreement.
 *
 * This library is distributed with a LIMITED WARRANTY. See the jOOQ License
 * and Maintenance Agreement for more details: http://www.jooq.org/licensing
 */

package org.jooq.impl;

import static java.util.Arrays.asList;
import static org.jooq.conf.SettingsTools.updatablePrimaryKeys;
import static org.jooq.impl.Utils.getAnnotatedGetter;
import static org.jooq.impl.Utils.getAnnotatedMembers;
import static org.jooq.impl.Utils.getMatchingGetter;
import static org.jooq.impl.Utils.getMatchingMembers;
import static org.jooq.impl.Utils.hasColumnAnnotations;
import static org.jooq.impl.Utils.indexOrFail;
import static org.jooq.impl.Utils.settings;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jooq.Attachable;
import org.jooq.Converter;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.UniqueKey;
import org.jooq.exception.InvalidResultException;
import org.jooq.exception.MappingException;
import org.jooq.tools.Convert;

/**
 * A general base class for all {@link Record} types
 *
 * @author Lukas Eder
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
abstract class AbstractRecord extends AbstractStore implements Record {

    /**
     * Generated UID
     */
    private static final long   serialVersionUID = -6052512608911220404L;

    final RowImpl               fields;
    final Value<?>[]            values;

    AbstractRecord(Collection<? extends Field<?>> fields) {
        this(new RowImpl(fields));
    }

    AbstractRecord(Field<?>... fields) {
        this(new RowImpl(fields));
    }

    AbstractRecord(RowImpl fields) {
        this.fields = fields;
        this.values = new Value<?>[fields.size()];

        for (int i = 0; i < values.length; i++) {
            values[i] = new Value<Object>(null);
        }
    }

    // ------------------------------------------------------------------------
    // XXX: Attachable API
    // ------------------------------------------------------------------------

    
    final List<Attachable> getAttachables() {
        List<Attachable> result = new ArrayList<Attachable>();

        int size = size();
        for (int i = 0; i < size; i++) {
            Object value = getValue0(i).getValue();

            if (value instanceof Attachable) {
                result.add((Attachable) value);
            }
        }

        return result;
    }

    // ------------------------------------------------------------------------
    // XXX: FieldProvider API
    // ------------------------------------------------------------------------

    
    public final <T> Field<T> field(Field<T> field) {
        return fieldsRow().field(field);
    }

    
    public final Field<?> field(String name) {
        return fieldsRow().field(name);
    }

    
    public final Field<?> field(int index) {
        return index >= 0 && index < fields.size() ? fields.field(index) : null;
    }

    
    public final Field<?>[] fields() {
        return fields.fields();
    }

    
    public final Field<?>[] fields(Field<?>... f) {
        return fields.fields(f);
    }

    
    public final Field<?>[] fields(String... fieldNames) {
        return fields.fields(fieldNames);
    }

    
    public final Field<?>[] fields(int... fieldIndexes) {
        return fields.fields(fieldIndexes);
    }

    // ------------------------------------------------------------------------
    // XXX: Record API
    // ------------------------------------------------------------------------

    
    public final int size() {
        return fields.size();
    }

    
    public final <T> T getValue(Field<T> field) {
        return getValue0(field).getValue();
    }

    
    public final <T> T getValue(Field<T> field, T defaultValue) {
        return getValue0(field).getValue(defaultValue);
    }

    
    public final <T> T getValue(Field<?> field, Class<? extends T> type) {
        return Convert.convert(getValue(field), type);
    }

    
    public final <T> T getValue(Field<?> field, Class<? extends T> type, T defaultValue) {
        final T result = getValue(field, type);
        return result == null ? defaultValue : result;
    }

    
    public final <T, U> U getValue(Field<T> field, Converter<? super T, U> converter) {
        return converter.from(getValue(field));
    }

    
    public final <T, U> U getValue(Field<T> field, Converter<? super T, U> converter, U defaultValue) {
        final U result = getValue(field, converter);
        return result == null ? defaultValue : result;
    }

    
    public final Object getValue(int index) {
        return getValue0(index).getValue();
    }

    
    public final Object getValue(int index, Object defaultValue) {
        final Object result = getValue(index);
        return result == null ? defaultValue : result;
    }

    
    public final <T> T getValue(int index, Class<? extends T> type) {
        return Convert.convert(getValue(index), type);
    }

    
    public final <T> T getValue(int index, Class<? extends T> type, T defaultValue) {
        final T result = getValue(index, type);
        return result == null ? defaultValue : result;
    }

    
    public final <U> U getValue(int index, Converter<?, U> converter) {
        return Convert.convert(getValue(index), converter);
    }

    
    public final <U> U getValue(int index, Converter<?, U> converter, U defaultValue) {
        final U result = getValue(index, converter);
        return result == null ? defaultValue : result;
    }

    
    public final Object getValue(String fieldName) {
        return getValue(field(fieldName));
    }

    
    public final Object getValue(String fieldName, Object defaultValue) {
        return getValue((Field<Object>) field(fieldName), defaultValue);
    }

    
    public final <T> T getValue(String fieldName, Class<? extends T> type) {
        return Convert.convert(getValue(fieldName), type);
    }

    
    public final <T> T getValue(String fieldName, Class<? extends T> type, T defaultValue) {
        final T result = getValue(fieldName, type);
        return result == null ? defaultValue : result;
    }

    
    public final <U> U getValue(String fieldName, Converter<?, U> converter) {
        return Convert.convert(getValue(fieldName), converter);
    }

    
    public final <U> U getValue(String fieldName, Converter<?, U> converter, U defaultValue) {
        final U result = getValue(fieldName, converter);
        return result == null ? defaultValue : result;
    }

    final <T> Value<T> getValue0(int index) {
        Value<?>[] v = getValues();

        if (index >= v.length) {
            throw new IllegalArgumentException("Field " + index + " is not contained in list");
        }

        return (Value<T>) v[index];
    }

    final <T> Value<T> getValue0(Field<T> field) {
        return getValue0(indexOrFail(fieldsRow(), field));
    }

    final Value<?>[] getValues() {
        return values;
    }

    /**
     * Subclasses may type-unsafely set a value to a record index. This method
     * takes care of converting the value to the appropriate type.
     */
    protected final void setValue(int index, Object value) {
        setValue(index, (Field) field(index), value);
    }

    
    public final <T> void setValue(Field<T> field, T value) {
        setValue(indexOrFail(fields, field), field, value);
    }

    private final <T> void setValue(int index, Field<T> field, T value) {
        Value<T> val = getValue0(index);
        UniqueKey<?> key = getPrimaryKey();

        // Normal fields' changed flag is always set to true
        if (key == null || !key.getFields().contains(field)) {
            val.setValue(value);
        }

        // The primary key's changed flag might've been set previously
        else if (val.isChanged()) {
            val.setValue(value);
        }

        // [#2764] Users may override updatability of primary key values
        else if (updatablePrimaryKeys(settings(this))) {
            val.setValue(value);
        }

        // [#979] If the primary key is being changed, all other fields' flags
        // need to be set to true for in case this record is stored again, an
        // INSERT statement will thus be issued
        else {
            val.setValue(value, true);

            if (val.isChanged()) {
                changed(true);
            }
        }
    }

    
    public final <T, U> void setValue(Field<T> field, U value, Converter<T, ? super U> converter) {
        setValue(field, converter.to(value));
    }

    final void setValues(Field<?>[] fields, AbstractRecord record) {
        for (Field<?> field : fields) {
            setValue(field, record.getValue0(field));
        }
    }

    final void setValue(Field<?> field, Value<?> value) {
        setValue(indexOrFail(fieldsRow(), field), value);
    }

    final void setValue(int index, Value<?> value) {
        getValues()[index] = value;
    }

    /**
     * Subclasses may override this
     */
    UniqueKey<?> getPrimaryKey() {
        return null;
    }

    /*
     * This method is overridden covariantly by TableRecordImpl
     */
    
    public Record original() {
        return Utils.newRecord((Class<AbstractRecord>) getClass(), fields.fields.fields, configuration())
                    .operate(new RecordOperation<AbstractRecord, RuntimeException>() {

            
            public AbstractRecord operate(AbstractRecord record) throws RuntimeException {
                Value<?>[] v = getValues();

                for (int i = 0; i < v.length; i++) {
                    record.setValue(i, new Value<Object>(v[i].getOriginal()));
                }

                return record;
            }
        });
    }

    
    public final <T> T original(Field<T> field) {
        return (T) original(indexOrFail(fieldsRow(), field));
    }

    
    public final Object original(int fieldIndex) {
        return getValues()[fieldIndex].getOriginal();
    }

    
    public final Object original(String fieldName) {
        return original(indexOrFail(fieldsRow(), fieldName));
    }

    
    public final boolean changed() {
        for (Value<?> value : getValues()) {
            if (value.isChanged()) {
                return true;
            }
        }

        return false;
    }

    
    public final boolean changed(Field<?> field) {
        return changed(indexOrFail(fieldsRow(), field));
    }

    
    public final boolean changed(int fieldIndex) {
        return getValue0(fieldIndex).isChanged();
    }

    
    public final boolean changed(String fieldName) {
        return changed(indexOrFail(fieldsRow(), fieldName));
    }

    
    public final void changed(boolean changed) {
        for (Value<?> value : getValues()) {
            value.setChanged(changed);
        }
    }

    
    public final void changed(Field<?> field, boolean changed) {
        changed(indexOrFail(fieldsRow(), field), changed);
    }

    
    public final void changed(int fieldIndex, boolean changed) {
        getValue0(fieldIndex).setChanged(changed);
    }

    
    public final void changed(String fieldName, boolean changed) {
        changed(indexOrFail(fieldsRow(), fieldName), changed);
    }

    
    public final void reset() {
        for (Value<?> value : getValues()) {
            value.reset();
        }
    }

    
    public final void reset(Field<?> field) {
        reset(indexOrFail(fieldsRow(), field));
    }

    
    public final void reset(int fieldIndex) {
        getValue0(fieldIndex).reset();
    }

    
    public final void reset(String fieldName) {
        reset(indexOrFail(fieldsRow(), fieldName));
    }

    
    public final Object[] intoArray() {
        return into(Object[].class);
    }

    
    public final Map<String, Object> intoMap() {
        Map<String, Object> map = new LinkedHashMap<String, Object>();

        int size = fields.size();
        for (int i = 0; i < size; i++) {
            Field<?> field = fields.field(i);

            if (map.put(field.getName(), getValue(i)) != null) {
                throw new InvalidResultException("Field " + field.getName() + " is not unique in Record : " + this);
            }
        }

        return map;
    }

    
    public final <E> E into(Class<? extends E> type) {
        return (E) Utils.configuration(this).recordMapperProvider().provide(fields.fields, type).map(this);
    }

    
    public final <E> E into(E object) {
        if (object == null) {
            throw new NullPointerException("Cannot copy Record into null");
        }

        Class<E> type = (Class<E>) object.getClass();

        try {
            return new DefaultRecordMapper<Record, E>(fields.fields, type, object).map(this);
        }

        // Pass MappingExceptions on to client code
        catch (MappingException e) {
            throw e;
        }

        // All other reflection exceptions are intercepted
        catch (Exception e) {
            throw new MappingException("An error ocurred when mapping record to " + type, e);
        }
    }

    
    public final <R extends Record> R into(final Table<R> table) {
        return Utils.newRecord(table, configuration())
                    .operate(new RecordOperation<R, MappingException>() {

            
            public R operate(R record) throws MappingException {
                try {
                    for (Field<?> targetField : table.fields()) {
                        Field<?> sourceField = field(targetField);

                        if (sourceField != null) {
                            Utils.setValue(record, targetField, AbstractRecord.this, sourceField);
                        }
                    }

                    // [#1522] If the primary key has been fully fetched, then changed
                    // flags should all be reset in order for the returned record to be
                    // updatable using store()
                    if (record instanceof AbstractRecord) {
                        UniqueKey<?> key = ((AbstractRecord) record).getPrimaryKey();

                        if (key != null) {
                            boolean isKeySet = true;

                            for (Field<?> field : key.getFields()) {
                                isKeySet = isKeySet && (field(field) != null);
                            }

                            if (isKeySet) {
                                record.changed(false);
                            }
                        }
                    }

                    return record;
                    // All reflection exceptions are intercepted
                }
                catch (Exception e) {
                    throw new MappingException("An error ocurred when mapping record to " + table, e);
                }
            }
        });
    }

    
    public final ResultSet intoResultSet() {
        ResultImpl<Record> result = new ResultImpl<Record>(configuration(), fields.fields.fields);
        result.add(this);
        return result.intoResultSet();
    }

    
    public final <E> E map(RecordMapper<Record, E> mapper) {
        return mapper.map(this);
    }

    
    public final void from(Object source) {
        if (source == null) return;

        // [#1987] Distinguish between various types to load data from
        // Maps are loaded using a {field-name -> value} convention
        if (source instanceof Map) {
            fromMap((Map<String, ?>) source);
        }

        // Arrays are loaded through index mapping
        else if (source instanceof Object[]) {
            fromArray((Object[]) source);
        }

        // All other types are expected to be POJOs
        else {
            from(source, fields());
        }
    }

    
    public final void from(Object source, Field<?>... f) {
        if (source == null) return;

        // [#1987] Distinguish between various types to load data from
        // Maps are loaded using a {field-name -> value} convention
        if (source instanceof Map) {
            fromMap((Map<String, ?>) source, f);
        }

        // Arrays are loaded through index mapping
        else if (source instanceof Object[]) {
            fromArray((Object[]) source, f);
        }

        // All other types are expected to be POJOs
        else {
            Class<?> type = source.getClass();

            try {
                boolean useAnnotations = hasColumnAnnotations(type);

                for (Field<?> field : f) {
                    List<java.lang.reflect.Field> members;
                    Method method;

                    // Annotations are available and present
                    if (useAnnotations) {
                        members = getAnnotatedMembers(type, field.getName());
                        method = getAnnotatedGetter(type, field.getName());
                    }

                    // No annotations are present
                    else {
                        members = getMatchingMembers(type, field.getName());
                        method = getMatchingGetter(type, field.getName());
                    }

                    // Use only the first applicable method or member
                    if (method != null) {
                        Utils.setValue(this, field, method.invoke(source));
                    }
                    else if (members.size() > 0) {
                        from(source, members.get(0), field);
                    }
                }
            }

            // All reflection exceptions are intercepted
            catch (Exception e) {
                throw new MappingException("An error ocurred when mapping record from " + type, e);
            }
        }
    }

    
    public final void from(Object source, String... fieldNames) {
        from(source, fields(fieldNames));
    }

    
    public final void from(Object source, int... fieldIndexes) {
        from(source, fields(fieldIndexes));
    }

    
    public final void fromMap(Map<String, ?> map) {
        from(map, fields());
    }

    
    public final void fromMap(Map<String, ?> map, Field<?>... f) {
        for (int i = 0; i < f.length; i++) {
            String name = f[i].getName();

            // Set only those values contained in the map
            if (map.containsKey(name)) {
                Utils.setValue(this, f[i], map.get(name));
            }
        }
    }

    
    public final void fromMap(Map<String, ?> map, String... fieldNames) {
        fromMap(map, fields(fieldNames));
    }

    
    public final void fromMap(Map<String, ?> map, int... fieldIndexes) {
        fromMap(map, fields(fieldIndexes));
    }

    
    public final void fromArray(Object... array) {
        fromArray(array, fields());
    }

    
    public final void fromArray(Object[] array, Field<?>... f) {
        Fields accept = new Fields(f);
        int size = fields.size();

        for (int i = 0; i < size && i < array.length; i++) {
            Field field = fields.field(i);

            if (accept.field(field) != null) {
                Utils.setValue(this, field, array[i]);
            }
        }
    }

    
    public final void fromArray(Object[] array, String... fieldNames) {
        fromArray(array, fields(fieldNames));
    }

    
    public final void fromArray(Object[] array, int... fieldIndexes) {
        fromArray(array, fields(fieldIndexes));
    }

    /**
     * This method was implemented with [#799]. It may be useful to make it
     * public for broader use...?
     */
    protected final void from(Record source) {
        for (Field<?> field : fields.fields.fields) {
            Field<?> sourceField = source.field(field);

            if (sourceField != null) {
                Utils.setValue(this, field, source, sourceField);
            }
        }
    }

    private final void from(Object source, java.lang.reflect.Field member, Field<?> field)
        throws IllegalAccessException {

        Class<?> mType = member.getType();

        if (mType.isPrimitive()) {
            if (mType == byte.class) {
                Utils.setValue(this, field, member.getByte(source));
            }
            else if (mType == short.class) {
                Utils.setValue(this, field, member.getShort(source));
            }
            else if (mType == int.class) {
                Utils.setValue(this, field, member.getInt(source));
            }
            else if (mType == long.class) {
                Utils.setValue(this, field, member.getLong(source));
            }
            else if (mType == float.class) {
                Utils.setValue(this, field, member.getFloat(source));
            }
            else if (mType == double.class) {
                Utils.setValue(this, field, member.getDouble(source));
            }
            else if (mType == boolean.class) {
                Utils.setValue(this, field, member.getBoolean(source));
            }
            else if (mType == char.class) {
                Utils.setValue(this, field, member.getChar(source));
            }
        }
        else {
            Utils.setValue(this, field, member.get(source));
        }
    }

    // ------------------------------------------------------------------------
    // XXX: Object and Comparable API
    // ------------------------------------------------------------------------

    
    public String toString() {
        Result<AbstractRecord> result = new ResultImpl<AbstractRecord>(configuration(), fields.fields.fields);
        result.add(this);
        return result.toString();
    }

    
    public int compareTo(Record that) {
        // Note: keep this implementation in-sync with AbstractStore.equals()!

        if (that == null) {
            throw new NullPointerException();
        }
        if (size() != that.size()) {
            throw new ClassCastException(String.format("Trying to compare incomparable records (wrong degree):\n%s\n%s", this, that));
        }

        Class<?>[] thisTypes = this.fieldsRow().types();
        Class<?>[] thatTypes = that.fieldsRow().types();

        if (!asList(thisTypes).equals(asList(thatTypes))) {
            throw new ClassCastException(String.format("Trying to compare incomparable records (type mismatch):\n%s\n%s", this, that));
        }

        for (int i = 0; i < size(); i++) {
            final Object thisValue = getValue(i);
            final Object thatValue = that.getValue(i);

            // [#1850] Only return -1/+1 early. In all other cases,
            // continue checking the remaining fields
            if (thisValue == null && thatValue == null) {
                continue;
            }

            // Order column values in a SQL NULLS LAST manner
            else if (thisValue == null) {
                return 1;
            }

            else if (thatValue == null) {
                return -1;
            }

            // [#985] Compare arrays too.
            else if (thisValue.getClass().isArray() && thatValue.getClass().isArray()) {

                // Might be byte[]
                if (thisValue.getClass() == byte[].class) {
                    int compare = compare((byte[]) thisValue, (byte[]) thatValue);

                    if (compare != 0) {
                        return compare;
                    }
                }

                // Other primitive types are not expected
                else if (!thisValue.getClass().getComponentType().isPrimitive()) {
                    int compare = compare((Object[]) thisValue, (Object[]) thatValue);

                    if (compare != 0) {
                        return compare;
                    }
                }

                else {
                    throw new ClassCastException(String.format("Unsupported data type in natural ordering: %s", thisValue.getClass()));
                }
            }
            else {
                int compare = ((Comparable) thisValue).compareTo(thatValue);

                if (compare != 0) {
                    return compare;
                }
            }
        }

        // If we got through the above loop, the two records are equal
        return 0;
    }

    /**
     * Compare two byte arrays
     */
    final int compare(byte[] array1, byte[] array2) {
        int length = Math.min(array1.length, array2.length);

        for (int i = 0; i < length; i++) {
            int v1 = (array1[i] & 0xff);
            int v2 = (array2[i] & 0xff);

            if (v1 != v2) {
                return v1 < v2 ? -1 : 1;
            }
        }

        return array1.length - array2.length;
    }

    /**
     * Compare two arrays
     */
    final int compare(Object[] array1, Object[] array2) {
        int length = Math.min(array1.length, array2.length);

        for (int i = 0; i < length; i++) {
            int compare = ((Comparable) array1[i]).compareTo(array2[i]);

            if (compare != 0) {
                return compare;
            }
        }

        return array1.length - array2.length;
    }
}
