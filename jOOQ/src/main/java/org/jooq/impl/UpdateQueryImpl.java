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
import static org.jooq.Clause.UPDATE;
import static org.jooq.Clause.UPDATE_RETURNING;
import static org.jooq.Clause.UPDATE_SET;
import static org.jooq.Clause.UPDATE_SET_ASSIGNMENT;
import static org.jooq.Clause.UPDATE_UPDATE;
import static org.jooq.Clause.UPDATE_WHERE;
// ...
// ...
import static org.jooq.impl.DSL.select;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Generated;

import org.jooq.BindContext;
import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Operator;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Record11;
import org.jooq.Record12;
import org.jooq.Record13;
import org.jooq.Record14;
import org.jooq.Record15;
import org.jooq.Record16;
import org.jooq.Record17;
import org.jooq.Record18;
import org.jooq.Record19;
import org.jooq.Record2;
import org.jooq.Record20;
import org.jooq.Record21;
import org.jooq.Record22;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Record6;
import org.jooq.Record7;
import org.jooq.Record8;
import org.jooq.Record9;
import org.jooq.RenderContext;
import org.jooq.Row;
import org.jooq.Row1;
import org.jooq.Row10;
import org.jooq.Row11;
import org.jooq.Row12;
import org.jooq.Row13;
import org.jooq.Row14;
import org.jooq.Row15;
import org.jooq.Row16;
import org.jooq.Row17;
import org.jooq.Row18;
import org.jooq.Row19;
import org.jooq.Row2;
import org.jooq.Row20;
import org.jooq.Row21;
import org.jooq.Row22;
import org.jooq.Row3;
import org.jooq.Row4;
import org.jooq.Row5;
import org.jooq.Row6;
import org.jooq.Row7;
import org.jooq.Row8;
import org.jooq.Row9;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.UpdateQuery;

/**
 * @author Lukas Eder
 */
class UpdateQueryImpl<R extends Record> extends AbstractStoreQuery<R> implements UpdateQuery<R> {

    private static final long           serialVersionUID = -660460731970074719L;
    private static final Clause[]       CLAUSES          = { UPDATE };

    private final FieldMapForUpdate     updateMap;
    private final ConditionProviderImpl condition;
    private Row                         multiRow;
    private Row                         multiValue;
    private Select<?>                   multiSelect;

    UpdateQueryImpl(Configuration configuration, Table<R> table) {
        super(configuration, table);

        this.condition = new ConditionProviderImpl();
        this.updateMap = new FieldMapForUpdate(UPDATE_SET_ASSIGNMENT);
    }

    
    protected final FieldMapForUpdate getValues() {
        return updateMap;
    }

// [jooq-tools] START [addValues]

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1> void addValues(Row1<T1> row, Row1<T1> value) {
        addValues0(row, value);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2> void addValues(Row2<T1, T2> row, Row2<T1, T2> value) {
        addValues0(row, value);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3> void addValues(Row3<T1, T2, T3> row, Row3<T1, T2, T3> value) {
        addValues0(row, value);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4> void addValues(Row4<T1, T2, T3, T4> row, Row4<T1, T2, T3, T4> value) {
        addValues0(row, value);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5> void addValues(Row5<T1, T2, T3, T4, T5> row, Row5<T1, T2, T3, T4, T5> value) {
        addValues0(row, value);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6> void addValues(Row6<T1, T2, T3, T4, T5, T6> row, Row6<T1, T2, T3, T4, T5, T6> value) {
        addValues0(row, value);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7> void addValues(Row7<T1, T2, T3, T4, T5, T6, T7> row, Row7<T1, T2, T3, T4, T5, T6, T7> value) {
        addValues0(row, value);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8> void addValues(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row, Row8<T1, T2, T3, T4, T5, T6, T7, T8> value) {
        addValues0(row, value);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9> void addValues(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row, Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> value) {
        addValues0(row, value);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> void addValues(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row, Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> value) {
        addValues0(row, value);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> void addValues(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row, Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> value) {
        addValues0(row, value);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> void addValues(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row, Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> value) {
        addValues0(row, value);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> void addValues(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row, Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> value) {
        addValues0(row, value);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> void addValues(Row14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> row, Row14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> value) {
        addValues0(row, value);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> void addValues(Row15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> row, Row15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> value) {
        addValues0(row, value);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> void addValues(Row16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> row, Row16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> value) {
        addValues0(row, value);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> void addValues(Row17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> row, Row17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> value) {
        addValues0(row, value);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> void addValues(Row18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> row, Row18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> value) {
        addValues0(row, value);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> void addValues(Row19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> row, Row19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> value) {
        addValues0(row, value);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> void addValues(Row20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> row, Row20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> value) {
        addValues0(row, value);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> void addValues(Row21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> row, Row21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> value) {
        addValues0(row, value);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> void addValues(Row22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> row, Row22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> value) {
        addValues0(row, value);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1> void addValues(Row1<T1> row, Select<? extends Record1<T1>> select) {
        addValues0(row, select);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2> void addValues(Row2<T1, T2> row, Select<? extends Record2<T1, T2>> select) {
        addValues0(row, select);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3> void addValues(Row3<T1, T2, T3> row, Select<? extends Record3<T1, T2, T3>> select) {
        addValues0(row, select);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4> void addValues(Row4<T1, T2, T3, T4> row, Select<? extends Record4<T1, T2, T3, T4>> select) {
        addValues0(row, select);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5> void addValues(Row5<T1, T2, T3, T4, T5> row, Select<? extends Record5<T1, T2, T3, T4, T5>> select) {
        addValues0(row, select);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6> void addValues(Row6<T1, T2, T3, T4, T5, T6> row, Select<? extends Record6<T1, T2, T3, T4, T5, T6>> select) {
        addValues0(row, select);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7> void addValues(Row7<T1, T2, T3, T4, T5, T6, T7> row, Select<? extends Record7<T1, T2, T3, T4, T5, T6, T7>> select) {
        addValues0(row, select);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8> void addValues(Row8<T1, T2, T3, T4, T5, T6, T7, T8> row, Select<? extends Record8<T1, T2, T3, T4, T5, T6, T7, T8>> select) {
        addValues0(row, select);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9> void addValues(Row9<T1, T2, T3, T4, T5, T6, T7, T8, T9> row, Select<? extends Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> select) {
        addValues0(row, select);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> void addValues(Row10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> row, Select<? extends Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> select) {
        addValues0(row, select);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> void addValues(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row, Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        addValues0(row, select);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> void addValues(Row12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> row, Select<? extends Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>> select) {
        addValues0(row, select);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> void addValues(Row13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> row, Select<? extends Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>> select) {
        addValues0(row, select);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> void addValues(Row14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> row, Select<? extends Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>> select) {
        addValues0(row, select);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> void addValues(Row15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> row, Select<? extends Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>> select) {
        addValues0(row, select);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> void addValues(Row16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> row, Select<? extends Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>> select) {
        addValues0(row, select);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> void addValues(Row17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> row, Select<? extends Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>> select) {
        addValues0(row, select);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> void addValues(Row18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> row, Select<? extends Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>> select) {
        addValues0(row, select);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> void addValues(Row19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> row, Select<? extends Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>> select) {
        addValues0(row, select);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> void addValues(Row20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> row, Select<? extends Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>> select) {
        addValues0(row, select);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> void addValues(Row21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> row, Select<? extends Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>> select) {
        addValues0(row, select);
    }

    @Generated("This method was generated using jOOQ-tools")
    
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> void addValues(Row22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> row, Select<? extends Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22>> select) {
        addValues0(row, select);
    }

// [jooq-tools] END [addValues]

    private final void addValues0(Row row, Row value) {
        multiRow = row;
        multiValue = value;
    }

    private final void addValues0(Row row, Select<?> select) {
        multiRow = row;
        multiSelect = select;
    }

    
    public final void addValues(Map<? extends Field<?>, ?> map) {
        updateMap.set(map);
    }

    
    public final void addConditions(Collection<? extends Condition> conditions) {
        condition.addConditions(conditions);
    }

    
    public final void addConditions(Condition... conditions) {
        condition.addConditions(conditions);
    }

    
    public final void addConditions(Operator operator, Condition... conditions) {
        condition.addConditions(operator, conditions);
    }

    
    public final void addConditions(Operator operator, Collection<? extends Condition> conditions) {
        condition.addConditions(operator, conditions);
    }

    final Condition getWhere() {
        return condition.getWhere();
    }

    
    public final void toSQL(RenderContext context) {
        context.start(UPDATE_UPDATE)
               .keyword("update")
               .sql(" ")
               .declareTables(true)
               .visit(getInto())
               .declareTables(false)
               .end(UPDATE_UPDATE)
               .formatSeparator()
               .start(UPDATE_SET)
               .keyword("set")
               .sql(" ");

        // A multi-row update was specified
        if (multiRow != null) {
            boolean qualify = context.qualify();

            context.start(UPDATE_SET_ASSIGNMENT)
                   .qualify(false)
                   .visit(multiRow)
                   .qualify(qualify)
                   .sql(" = ");

            // Some dialects don't really support row value expressions on the
            // right hand side of a SET clause
            if (multiValue != null && !asList().contains(context.configuration().dialect().family())) {
                context.visit(multiValue);
            }

            // Subselects or subselect simulations of row value expressions
            else {
                Select<?> select = multiSelect;

                if (multiValue != null) {
                    select = select(multiValue.fields());
                }

                context.sql("(")
                       .formatIndentStart()
                       .formatNewLine()
                       .subquery(true)
                       .visit(select)
                       .subquery(false)
                       .formatIndentEnd()
                       .formatNewLine()
                       .sql(")");
            }

            context.end(UPDATE_SET_ASSIGNMENT);
        }

        // A regular (non-multi-row) update was specified
        else {
            context.formatIndentLockStart()
                   .visit(updateMap)
                   .formatIndentLockEnd();
        }

        context.end(UPDATE_SET)
               .start(UPDATE_WHERE);

        if (!(getWhere() instanceof TrueCondition)) {
            context.formatSeparator()
                   .keyword("where").sql(" ")
                   .visit(getWhere());
        }

        context.end(UPDATE_WHERE)
               .start(UPDATE_RETURNING);

        toSQLReturning(context);

        context.end(UPDATE_RETURNING);
    }

    
    public final void bind(BindContext context) {
        context.visit(getInto());

        // A multi-row update was specified
        if (multiRow != null) {
            context.visit(multiRow);

            if (multiValue != null) {
                context.visit(multiValue);
            }
            else {
                context.subquery(true)
                       .visit(multiSelect)
                       .subquery(false);
            }
        }

        // A regular (non-multi-row) update was specified
        else {
            context.visit(updateMap);
        }

        context.visit(condition);
        bindReturning(context);
    }

    
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    
    public final boolean isExecutable() {
        return updateMap.size() > 0 || multiRow != null;
    }
}
