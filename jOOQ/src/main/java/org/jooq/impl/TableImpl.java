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
import static org.jooq.Clause.TABLE_ALIAS;
import static org.jooq.Clause.TABLE_REFERENCE;

import org.jooq.BindContext;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.Record;
import org.jooq.RenderContext;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.tools.StringUtils;

/**
 * A common base type for tables
 * <p>
 * This type is for JOOQ INTERNAL USE only. Do not reference directly
 *
 * @author Lukas Eder
 */
public class TableImpl<R extends Record> extends AbstractTable<R> {

    private static final long     serialVersionUID        = 261033315221985068L;
    private static final Clause[] CLAUSES_TABLE_REFERENCE = { TABLE, TABLE_REFERENCE };
    private static final Clause[] CLAUSES_TABLE_ALIAS     = { TABLE, TABLE_ALIAS };

    private final Fields<R>       fields;
    private final Alias<Table<R>> alias;

    public TableImpl(String name) {
        this(name, null, null);
    }

    public TableImpl(String name, Schema schema) {
        this(name, schema, null);
    }

    public TableImpl(String name, Schema schema, Table<R> aliased) {
        super(name, schema);

        this.fields = new Fields<R>();

        if (aliased != null) {
            alias = new Alias<Table<R>>(aliased, name);
        }
        else {
            alias = null;
        }
    }

    /**
     * Get the aliased table wrapped by this table
     */
    Table<R> getAliasedTable() {
        if (alias != null) {
            return alias.wrapped();
        }

        return null;
    }

    
    final Fields<R> fields0() {
        return fields;
    }

    
    public final Clause[] clauses(Context<?> ctx) {
        return alias != null ? CLAUSES_TABLE_ALIAS : CLAUSES_TABLE_REFERENCE;
    }

    
    public final void bind(BindContext context) {
        if (alias != null) {
            alias.bind(context);
        }
    }

    
    public final void toSQL(RenderContext context) {
        if (alias != null) {
            alias.toSQL(context);
        }
        else {
            if (context.qualify()) {
                Schema mappedSchema = Utils.getMappedSchema(context.configuration(), getSchema());

                if (mappedSchema != null) {
                    context.visit(mappedSchema);
                    context.sql(".");
                }
            }

            context.literal(Utils.getMappedTable(context.configuration(), this).getName());
        }
    }

    /**
     * Subclasses may override this method to provide custom aliasing
     * implementations
     * <p>
     * {@inheritDoc}
     */
    
    public Table<R> as(String as) {
        if (alias != null) {
            return alias.wrapped().as(as);
        }
        else {
            return new TableAlias<R>(this, as);
        }
    }

    /**
     * Subclasses may override this method to provide custom aliasing
     * implementations
     * <p>
     * {@inheritDoc}
     */
    
    public Table<R> as(String as, String... fieldAliases) {
        if (alias != null) {
            return alias.wrapped().as(as, fieldAliases);
        }
        else {
            return new TableAlias<R>(this, as, fieldAliases);
        }
    }

    /**
     * Subclasses must override this method if they use the generic type
     * parameter <R> for other types than {@link Record}
     * <p>
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    
    public Class<? extends R> getRecordType() {
        return (Class<? extends R>) RecordImpl.class;
    }

    
    public boolean declaresTables() {
        if (alias != null) {
            return true;
        }
        else {
            return super.declaresTables();
        }
    }

    // ------------------------------------------------------------------------
    // XXX: Object API
    // ------------------------------------------------------------------------

    
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }

        // [#2144] TableImpl equality can be decided without executing the
        // rather expensive implementation of AbstractQueryPart.equals()
        if (that instanceof TableImpl) {
            TableImpl<?> other = (TableImpl<?>) that;
            return
                StringUtils.equals(getSchema(), other.getSchema()) &&
                StringUtils.equals(getName(), other.getName());
        }

        return super.equals(that);
    }
}
