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
import java.util.HashMap;
import java.util.Map;

import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.UDT;
import org.jooq.UDTRecord;

public class UDTImpl<R extends Record> extends AbstractType<R> implements UDT<R> {

    private static final long               serialVersionUID = -2208672099190913126L;
    private final FieldList                 fields;
    private transient Map<String, Class<?>> typeMapping;

    public UDTImpl(SQLDialect dialect, String name, Schema schema) {
        this(Factory.getFactory(dialect), name, schema);
    }

    public UDTImpl(Configuration configuration, String name, Schema schema) {
        super(configuration, name, schema);

        this.fields = new FieldList(configuration);
    }

    @Override
    protected final FieldList getFieldList() {
        return fields;
    }

    /**
     * Subclasses must override this method if they use the generic type
     * parameter <R> for other types than {@link Record}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends R> getRecordType() {
        return (Class<? extends R>) RecordImpl.class;
    }

    @Override
    public final synchronized Map<String, Class<?>> getTypeMapping() throws SQLException {
        if (typeMapping == null) {
            typeMapping = new HashMap<String, Class<?>>();
            typeMapping.put(getName(), getRecordType());

            // Recursively add nested UDT types
            for (Field<?> field : getFieldList()) {
                if (UDTRecord.class.isAssignableFrom(field.getType())) {
                    typeMapping.putAll(FieldTypeHelper.getTypeMapping(field.getType()));
                }
            }
        }

        return typeMapping;
    }

    @Override
    public final String toSQLReference(Configuration configuration, boolean inlineParameters) {
        throw new UnsupportedOperationException("UDTImpl cannot be used as a true QueryPart");
    }

    @Override
    public final int bind(Configuration configuration, PreparedStatement stmt, int initialIndex) throws SQLException {
        throw new UnsupportedOperationException("UDTImpl cannot be used as a true QueryPart");
    }
}
