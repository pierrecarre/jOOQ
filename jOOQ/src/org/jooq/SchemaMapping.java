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
package org.jooq;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jooq.impl.SchemaImpl;

/**
 * A schema mapping class allowing for translation of schemata defined at
 * codegen-time to schemata configured at run-time.
 *
 * @author Lukas Eder
 * @see https://sourceforge.net/apps/trac/jooq/ticket/173
 * @since 1.5.2
 */
public class SchemaMapping implements Serializable {

    /**
     * Generated UID
     */
    private static final long serialVersionUID = 8269660159338710470L;

    /**
     * The default, unmodifiable mapping that just takes generated schemata
     */
    public static final SchemaMapping NO_MAPPING = new SchemaMapping();

    /**
     * The underlying mapping
     */
    private final Map<String, Schema> mapping = new HashMap<String, Schema>();

    /**
     * Construct an empty mapping
     */
    public SchemaMapping() {}

    /**
     * Add schemata to this mapping
     *
     * @param generatedSchema The schema known at codegen time to be mapped
     * @param configuredSchema The schema configured at run time to be mapped
     */
    public void add(Schema generatedSchema, Schema configuredSchema) {
        mapping.put(generatedSchema.getName(), configuredSchema);
    }

    /**
     * Add schemata to this mapping
     *
     * @param generatedSchema The schema known at codegen time to be mapped
     * @param configuredSchemaName The schema configured at run time to be
     *            mapped
     */
    public void add(Schema generatedSchema, String configuredSchemaName) {
        @SuppressWarnings("deprecation")
        SQLDialect dialect = generatedSchema.getQueryPart().getDialect();
        Schema configuredSchema = new SchemaImpl(dialect, configuredSchemaName);
        add(generatedSchema, configuredSchema);
    }

    /**
     * Apply mapping to a given schema
     *
     * @param generatedSchema The generated schema to be mapped
     * @return The configured schema
     */
    public Schema map(Schema generatedSchema) {
        Schema result = mapping.get(generatedSchema.getName());
        return result != null ? result : generatedSchema;
    }

    @Override
    public String toString() {
        return "SchemaMapping [mapping=" + mapping + "]";
    }
}
