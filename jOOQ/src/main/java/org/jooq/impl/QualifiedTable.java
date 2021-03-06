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

import static org.jooq.Clause.TABLE;
import static org.jooq.Clause.TABLE_REFERENCE;

import org.jooq.BindContext;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.Record;
import org.jooq.RenderContext;
import org.jooq.Table;

/**
 * A <code>QualifiedTable</code> is a {@link Table} that always renders a table
 * name or alias as a literal using {@link RenderContext#literal(String)}
 *
 * @author Lukas Eder
 */
class QualifiedTable extends AbstractTable<Record> {

    /**
     * Generated UID
     */
    private static final long     serialVersionUID = 6937002867156868761L;
    private static final Clause[] CLAUSES          = { TABLE, TABLE_REFERENCE };

    private final String[]    sql;

    QualifiedTable(String... sql) {
        super(sql[sql.length - 1]);

        this.sql = sql;
    }

    // ------------------------------------------------------------------------
    // Table API
    // ------------------------------------------------------------------------

    
    public final void toSQL(RenderContext context) {
        String separator = "";
        for (String string : sql) {
            context.sql(separator);
            context.literal(string);

            separator = ".";
        }
    }

    
    public final void bind(BindContext context) {}

    
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    
    public final Class<? extends Record> getRecordType() {
        return RecordImpl.class;
    }

    
    public final Table<Record> as(String alias) {
        return new TableAlias<Record>(this, alias);
    }

    
    public final Table<Record> as(String alias, String... fieldAliases) {
        return new TableAlias<Record>(this, alias, fieldAliases);
    }

    
    final Fields<Record> fields0() {
        return new Fields<Record>();
    }
}
