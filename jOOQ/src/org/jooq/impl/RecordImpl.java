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

import java.util.LinkedHashMap;
import java.util.Map;

import org.jooq.Field;
import org.jooq.FieldList;
import org.jooq.FieldProvider;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Value;

/**
 * @author Lukas Eder
 */
public class RecordImpl implements Record {

    /**
     * Generated UID
     */
    private static final long       serialVersionUID = -6052512608911220404L;

    private final SQLDialect        dialect;
    private final FieldProvider     fields;
    private Map<Field<?>, Value<?>> values;

    public RecordImpl(SQLDialect dialect, FieldProvider fields) {
        this.dialect = dialect;
        this.fields = fields;
    }

    @Override
    public SQLDialect getDialect() {
        return dialect;
    }

    FieldProvider getMetaData() {
        return fields;
    }

    @Override
    public final FieldList getFields() {
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
                values.put(field, new ValueImpl<Object>(null));
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

    @Override
    public final <T> void setValue(Field<T> field, Value<T> value) {
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
}
