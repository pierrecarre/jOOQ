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
import static org.jooq.Clause.INSERT;
import static org.jooq.Clause.INSERT_INSERT_INTO;
import static org.jooq.Clause.INSERT_ON_DUPLICATE_KEY_UPDATE;
import static org.jooq.Clause.INSERT_ON_DUPLICATE_KEY_UPDATE_ASSIGNMENT;
import static org.jooq.Clause.INSERT_RETURNING;
import static org.jooq.SQLDialect.MARIADB;
import static org.jooq.SQLDialect.MYSQL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jooq.BindContext;
import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.InsertQuery;
import org.jooq.Merge;
import org.jooq.MergeNotMatchedStep;
import org.jooq.MergeOnConditionStep;
import org.jooq.Record;
import org.jooq.RenderContext;
import org.jooq.Table;
import org.jooq.exception.SQLDialectNotSupportedException;

/**
 * @author Lukas Eder
 */
class InsertQueryImpl<R extends Record> extends AbstractStoreQuery<R> implements InsertQuery<R> {

    private static final long        serialVersionUID = 4466005417945353842L;
    private static final Clause[]    CLAUSES          = { INSERT };

    private final FieldMapForUpdate  updateMap;
    private final FieldMapsForInsert insertMaps;
    private boolean                  onDuplicateKeyUpdate;
    private boolean                  onDuplicateKeyIgnore;

    InsertQueryImpl(Configuration configuration, Table<R> into) {
        super(configuration, into);

        updateMap = new FieldMapForUpdate(INSERT_ON_DUPLICATE_KEY_UPDATE_ASSIGNMENT);
        insertMaps = new FieldMapsForInsert();
    }

    
    public final void newRecord() {
        insertMaps.newRecord();
    }

    
    protected final FieldMapForInsert getValues() {
        return insertMaps.getMap();
    }

    
    public final void addRecord(R record) {
        newRecord();
        setRecord(record);
    }

    
    public final void onDuplicateKeyUpdate(boolean flag) {
        this.onDuplicateKeyIgnore = false;
        this.onDuplicateKeyUpdate = flag;
    }

    
    public final void onDuplicateKeyIgnore(boolean flag) {
        this.onDuplicateKeyUpdate = false;
        this.onDuplicateKeyIgnore = flag;
    }

    
    public final <T> void addValueForUpdate(Field<T> field, T value) {
        updateMap.put(field, Utils.field(value, field));
    }

    
    public final <T> void addValueForUpdate(Field<T> field, Field<T> value) {
        updateMap.put(field, Utils.field(value, field));
    }

    
    public final void addValuesForUpdate(Map<? extends Field<?>, ?> map) {
        updateMap.set(map);
    }

    
    public final void addValues(Map<? extends Field<?>, ?> map) {
        insertMaps.getMap().set(map);
    }

    
    public final void toSQL(RenderContext context) {

        // ON DUPLICATE KEY UPDATE clause
        // ------------------------------
        if (onDuplicateKeyUpdate) {
            switch (context.configuration().dialect().family()) {

                // MySQL has a nice syntax for this
                case CUBRID:
                case MARIADB:
                case MYSQL: {
                    toSQLInsert(context);
                    context.formatSeparator()
                           .start(INSERT_ON_DUPLICATE_KEY_UPDATE)
                           .keyword("on duplicate key update")
                           .sql(" ")
                           .visit(updateMap)
                           .end(INSERT_ON_DUPLICATE_KEY_UPDATE);

                    break;
                }

                // Some dialects can't really handle this clause. Simulation
                // should be done in two steps
                case H2: {
                    throw new SQLDialectNotSupportedException("The ON DUPLICATE KEY UPDATE clause cannot be simulated for " + context.configuration().dialect());
                }

                // Some databases allow for simulating this clause using a
                // MERGE statement
                /* [pro] xx
                xxxx xxxx
                xxxx xxxxxxx
                xxxx xxxxxxxxxx
                xxxx xxxxxxx
                xx [/pro] */
                case HSQLDB: {
                    context.visit(toMerge(context.configuration()));
                    break;
                }

                default:
                    throw new SQLDialectNotSupportedException("The ON DUPLICATE KEY UPDATE clause cannot be simulated for " + context.configuration().dialect());
            }
        }

        // ON DUPLICATE KEY IGNORE clause
        // ------------------------------
        else if (onDuplicateKeyIgnore) {
            switch (context.configuration().dialect().family()) {

                // MySQL has a nice, native syntax for this
                case MARIADB:
                case MYSQL: {
                    toSQLInsert(context);
                    context.start(INSERT_ON_DUPLICATE_KEY_UPDATE)
                           .end(INSERT_ON_DUPLICATE_KEY_UPDATE);
                    break;
                }

                // CUBRID can simulate this using ON DUPLICATE KEY UPDATE
                case CUBRID: {
                    FieldMapForUpdate update = new FieldMapForUpdate(INSERT_ON_DUPLICATE_KEY_UPDATE_ASSIGNMENT);
                    Field<?> field = getInto().field(0);
                    update.put(field, field);

                    toSQLInsert(context);
                    context.formatSeparator()
                           .start(INSERT_ON_DUPLICATE_KEY_UPDATE)
                           .keyword("on duplicate key update")
                           .sql(" ")
                           .visit(update)
                           .end(INSERT_ON_DUPLICATE_KEY_UPDATE);

                    break;
                }

                // Some dialects can't really handle this clause. Simulation
                // should be done in two steps
                case H2: {
                    throw new SQLDialectNotSupportedException("The ON DUPLICATE KEY IGNORE clause cannot be simulated for " + context.configuration().dialect());
                }

                // Some databases allow for simulating this clause using a
                // MERGE statement
                /* [pro] xx
                xxxx xxxx
                xxxx xxxxxxx
                xxxx xxxxxxxxxx
                xxxx xxxxxxx
                xx [/pro] */
                case HSQLDB: {
                    context.visit(toMerge(context.configuration()));
                    break;
                }

                default:
                    throw new SQLDialectNotSupportedException("The ON DUPLICATE KEY IGNORE clause cannot be simulated for " + context.configuration().dialect());
            }
        }

        // Default mode
        // ------------
        else {
            toSQLInsert(context);
            context.start(INSERT_ON_DUPLICATE_KEY_UPDATE)
                   .end(INSERT_ON_DUPLICATE_KEY_UPDATE);
        }

        context.start(INSERT_RETURNING);
        toSQLReturning(context);
        context.end(INSERT_RETURNING);
    }

    
    public final void bind(BindContext context) {

        // ON DUPLICATE KEY UPDATE clause
        // ------------------------------
        if (onDuplicateKeyUpdate) {
            switch (context.configuration().dialect().family()) {

                // MySQL has a nice syntax for this
                case CUBRID:
                case MARIADB:
                case MYSQL: {
                    bindInsert(context);
                    break;
                }

                // Some dialects can't really handle this clause. Simulation
                // is done in two steps
                case H2: {
                    throw new SQLDialectNotSupportedException("The ON DUPLICATE KEY UPDATE clause cannot be simulated for " + context.configuration().dialect());
                }

                // Some databases allow for simulating this clause using a
                // MERGE statement
                /* [pro] xx
                xxxx xxxx
                xxxx xxxxxxx
                xxxx xxxxxxxxxx
                xxxx xxxxxxx
                xx [/pro] */
                case HSQLDB: {
                    context.visit(toMerge(context.configuration()));
                    break;
                }

                default:
                    throw new SQLDialectNotSupportedException("The ON DUPLICATE KEY UPDATE clause cannot be simulated for " + context.configuration().dialect());
            }
        }

        // ON DUPLICATE KEY IGNORE clause
        // ------------------------------
        else if (onDuplicateKeyIgnore) {
            switch (context.configuration().dialect().family()) {

                // MySQL has a nice, native syntax for this
                case MARIADB:
                case MYSQL: {
                    bindInsert(context);
                    break;
                }

                // CUBRID can simulate this using ON DUPLICATE KEY UPDATE
                case CUBRID: {
                    bindInsert(context);
                    break;
                }

                // Some dialects can't really handle this clause. Simulation
                // is done in two steps
                case H2: {
                    throw new SQLDialectNotSupportedException("The ON DUPLICATE KEY IGNORE clause cannot be simulated for " + context.configuration().dialect());
                }

                // Some databases allow for simulating this clause using a
                // MERGE statement
                /* [pro] xx
                xxxx xxxx
                xxxx xxxxxxx
                xxxx xxxxxxxxxx
                xxxx xxxxxxx
                xx [/pro] */
                case HSQLDB: {
                    context.visit(toMerge(context.configuration()));
                    break;
                }

                default:
                    throw new SQLDialectNotSupportedException("The ON DUPLICATE KEY IGNORE clause cannot be simulated for " + context.configuration().dialect());
            }
        }

        // Default mode
        // ------------
        else {
            bindInsert(context);
        }
    }

    
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    private final void toSQLInsert(RenderContext context) {
        context.start(INSERT_INSERT_INTO)
               .keyword("insert")
               .sql(" ")
               // [#1295] MySQL natively supports the IGNORE keyword
               .keyword((onDuplicateKeyIgnore && asList(MARIADB, MYSQL).contains(context.configuration().dialect())) ? "ignore " : "")
               .keyword("into")
               .sql(" ")
               .visit(getInto())
               .sql(" ");
        insertMaps.insertMaps.get(0).toSQLReferenceKeys(context);
        context.end(INSERT_INSERT_INTO)
               .visit(insertMaps);
    }

    private final void bindInsert(BindContext context) {
        context.visit(getInto())
               .visit(insertMaps)
               .visit(updateMap);

        bindReturning(context);
    }

    @SuppressWarnings("unchecked")
    private final Merge<R> toMerge(Configuration configuration) {
        Table<R> into = getInto();

        if (into.getPrimaryKey() != null) {
            Condition condition = null;
            List<Field<?>> key = new ArrayList<Field<?>>();

            for (Field<?> f : into.getPrimaryKey().getFields()) {
                Field<Object> field = (Field<Object>) f;
                Field<Object> value = (Field<Object>) insertMaps.getMap().get(field);

                key.add(value);
                Condition other = field.equal(value);

                if (condition == null) {
                    condition = other;
                }
                else {
                    condition = condition.and(other);
                }
            }

            MergeOnConditionStep<R> on =
            create(configuration).mergeInto(into)
                                 .usingDual()
                                 .on(condition);

            // [#1295] Use UPDATE clause only when with ON DUPLICATE KEY UPDATE,
            // not with ON DUPLICATE KEY IGNORE
            MergeNotMatchedStep<R> notMatched = on;
            if (onDuplicateKeyUpdate) {
                notMatched = on.whenMatchedThenUpdate()
                               .set(updateMap);
            }

            return notMatched.whenNotMatchedThenInsert(insertMaps.getMap().keySet())
                             .values(insertMaps.getMap().values());
        }
        else {
            throw new IllegalStateException("The ON DUPLICATE KEY IGNORE/UPDATE clause cannot be simulated when inserting into non-updatable tables : " + getInto());
        }
    }

    
    public final boolean isExecutable() {
        return insertMaps.isExecutable();
    }
}
