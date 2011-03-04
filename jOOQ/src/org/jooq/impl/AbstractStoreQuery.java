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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jooq.ArrayRecord;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.StoreQuery;
import org.jooq.Table;
import org.jooq.TableRecord;

/**
 * A default implementation for store queries.
 *
 * @author Lukas Eder
 */
abstract class AbstractStoreQuery<R extends TableRecord<R>> extends AbstractQuery<R> implements StoreQuery<R> {

    /**
     * Generated UID
     */
    private static final long             serialVersionUID = 6864591335823160569L;

    private final Table<R>                into;
    private final Map<Field<?>, Field<?>> values;

    AbstractStoreQuery(Configuration configuration, Table<R> into) {
        super(configuration);

        this.into = into;
        this.values = new LinkedHashMap<Field<?>, Field<?>>();
    }

    AbstractStoreQuery(Configuration configuration, Table<R> into, R record) {
        this(configuration, into);

        setRecord(record);
    }

    final Table<R> getInto() {
        return into;
    }

    final Map<Field<?>, Field<?>> getValues0() {
        return values;
    }

    @Override
    public final void setRecord(R record) {
        for (Field<?> field : record.getFields()) {
            addValue(record, field);
        }
    }

    final <T> void addValue(R record, Field<T> field) {
        addValue(field, record.getValue(field));
    }

    @Override
    public final <T> void addValue(Field<T> field, T value) {
        addValue(field, constant(value));
    }

    @Override
    public final <T> void addValue(Field<T> field, Field<T> value) {
        if (value == null) {
            values.put(field, create().NULL());
        }
        else {
            values.put(field, value);
        }
    }

    @Override
    public final <A extends ArrayRecord<T>, T> void addValueAsArray(Field<A> field, T... value) {
        if (value == null) {
            values.put(field, create().NULL());
        }
        else {
            addValueAsArray(field, Arrays.asList(value));
        }
    }

    @Override
    public final <A extends ArrayRecord<T>, T> void addValueAsArray(Field<A> field, List<T> value) {
        if (value == null) {
            values.put(field, create().NULL());
        }
        else {
            try {
                A record = JooqUtil.newArrayRecord(field.getType(), getConfiguration());
                record.setList(value);
                addValue(field, record);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Field is not a valid ArrayRecord field: " + field);
            }
        }
    }

    @Override
    public int bind(Configuration configuration, PreparedStatement stmt, int initialIndex) throws SQLException {
        int result = initialIndex;

        result = getInto().getQueryPart().bind(configuration, stmt, result);

        for (Entry<Field<?>, Field<?>> entry : getValues0().entrySet()) {
            result = entry.getKey().getQueryPart().bind(configuration, stmt, result);
            result = entry.getValue().getQueryPart().bind(configuration, stmt, result);
        }

        return result;
    }
}
