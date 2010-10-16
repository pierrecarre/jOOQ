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
package org.jooq.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.StoreQuery;
import org.jooq.Table;
import org.jooq.TableField;
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
    private static final long                   serialVersionUID = 6864591335823160569L;

    private final Table<R>                      into;
    private final Map<TableField<R, ?>, Object> values;

    AbstractStoreQuery(Configuration configuration, Table<R> into) {
        super(configuration);

        this.into = into;
        this.values = new LinkedHashMap<TableField<R, ?>, Object>();
    }

    AbstractStoreQuery(Configuration configuration, Table<R> into, R record) {
        this(configuration, into);

        setRecord(record);
    }

    @Override
    public final Table<R> getInto() {
        return into;
    }

    @Override
    public final Map<TableField<R, ?>, ?> getValues() {
        return Collections.unmodifiableMap(getValues0());
    }

    protected final Map<TableField<R, ?>, Object> getValues0() {
        return values;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void setRecord(R record) {
        for (Field<?> field : record.getFields()) {
            if (!(field instanceof TableField)) {
                throw new IllegalArgumentException("Cannot provide non-tablefields");
            }

            getValues0().put((TableField<R, ?>)field, record.getValue(field));
        }
    }

    @Override
    public final <T> void addValue(TableField<R, T> field, T value) {
        getValues0().put(field, value);
    }

    @Override
    public final void addValues(Map<TableField<R, ?>, ?> values) {
        for (Entry<TableField<R, ?>, ?> value : values.entrySet()) {
            getValues0().put(value.getKey(), value.getValue());
        }
    }

    @Override
    public int bind(PreparedStatement stmt, int initialIndex) throws SQLException {
        int result = initialIndex;

        result = getInto().bind(stmt, result);
        for (Field<?> field : getValues0().keySet()) {
            result = field.bind(stmt, result);
            bind(stmt, result++, field, getValues0().get(field));
        }

        return result;
    }
}
