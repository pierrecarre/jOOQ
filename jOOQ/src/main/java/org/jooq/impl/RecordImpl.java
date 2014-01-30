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

import java.util.Collection;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Record6;
import org.jooq.Record7;
import org.jooq.Record8;
import org.jooq.Record9;
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
import org.jooq.Record20;
import org.jooq.Record21;
import org.jooq.Record22;

/**
 * A general purpose record, typically used for ad-hoc types.
 * <p>
 * This type implements both the general-purpose, type-unsafe {@link Record}
 * interface, as well as the more specific, type-safe {@link Record1},
 * {@link Record2} through {@link Record22} interfaces
 *
 * @author Lukas Eder
 */
@Generated("This class was generated using jOOQ-tools")
@SuppressWarnings({ "unchecked", "rawtypes" })
class RecordImpl<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> extends AbstractRecord
implements

    // This record implementation implements all record types. Type-safety is
    // being checked through the type-safe API. No need for further checks here
    Record1<T1>,
    Record2<T1, T2>,
    Record3<T1, T2, T3>,
    Record4<T1, T2, T3, T4>,
    Record5<T1, T2, T3, T4, T5>,
    Record6<T1, T2, T3, T4, T5, T6>,
    Record7<T1, T2, T3, T4, T5, T6, T7>,
    Record8<T1, T2, T3, T4, T5, T6, T7, T8>,
    Record9<T1, T2, T3, T4, T5, T6, T7, T8, T9>,
    Record10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>,
    Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>,
    Record12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>,
    Record13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>,
    Record14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>,
    Record15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>,
    Record16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>,
    Record17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>,
    Record18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>,
    Record19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19>,
    Record20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20>,
    Record21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21>,
    Record22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> {

    /**
     * Generated UID
     */
    private static final long serialVersionUID = -2201346180421463830L;

    /**
     * Create a new general purpose record
     */
    public RecordImpl(Field<?>... fields) {
        super(fields);
    }

    /**
     * Create a new general purpose record
     */
    public RecordImpl(Collection<? extends Field<?>> fields) {
        super(fields);
    }

    // ------------------------------------------------------------------------
    // XXX: Type-safe Record APIs
    // ------------------------------------------------------------------------

    
    public RowImpl<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> fieldsRow() {
        return fields;
    }

    
    public final RowImpl<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> valuesRow() {
        return new RowImpl(Utils.fields(intoArray(), fields.fields()));
    }

    
    public final Field<T1> field1() {
        return fields.field(0);
    }

    
    public final Field<T2> field2() {
        return fields.field(1);
    }

    
    public final Field<T3> field3() {
        return fields.field(2);
    }

    
    public final Field<T4> field4() {
        return fields.field(3);
    }

    
    public final Field<T5> field5() {
        return fields.field(4);
    }

    
    public final Field<T6> field6() {
        return fields.field(5);
    }

    
    public final Field<T7> field7() {
        return fields.field(6);
    }

    
    public final Field<T8> field8() {
        return fields.field(7);
    }

    
    public final Field<T9> field9() {
        return fields.field(8);
    }

    
    public final Field<T10> field10() {
        return fields.field(9);
    }

    
    public final Field<T11> field11() {
        return fields.field(10);
    }

    
    public final Field<T12> field12() {
        return fields.field(11);
    }

    
    public final Field<T13> field13() {
        return fields.field(12);
    }

    
    public final Field<T14> field14() {
        return fields.field(13);
    }

    
    public final Field<T15> field15() {
        return fields.field(14);
    }

    
    public final Field<T16> field16() {
        return fields.field(15);
    }

    
    public final Field<T17> field17() {
        return fields.field(16);
    }

    
    public final Field<T18> field18() {
        return fields.field(17);
    }

    
    public final Field<T19> field19() {
        return fields.field(18);
    }

    
    public final Field<T20> field20() {
        return fields.field(19);
    }

    
    public final Field<T21> field21() {
        return fields.field(20);
    }

    
    public final Field<T22> field22() {
        return fields.field(21);
    }

    
    public final T1 value1() {
        return (T1) getValue(0);
    }

    
    public final T2 value2() {
        return (T2) getValue(1);
    }

    
    public final T3 value3() {
        return (T3) getValue(2);
    }

    
    public final T4 value4() {
        return (T4) getValue(3);
    }

    
    public final T5 value5() {
        return (T5) getValue(4);
    }

    
    public final T6 value6() {
        return (T6) getValue(5);
    }

    
    public final T7 value7() {
        return (T7) getValue(6);
    }

    
    public final T8 value8() {
        return (T8) getValue(7);
    }

    
    public final T9 value9() {
        return (T9) getValue(8);
    }

    
    public final T10 value10() {
        return (T10) getValue(9);
    }

    
    public final T11 value11() {
        return (T11) getValue(10);
    }

    
    public final T12 value12() {
        return (T12) getValue(11);
    }

    
    public final T13 value13() {
        return (T13) getValue(12);
    }

    
    public final T14 value14() {
        return (T14) getValue(13);
    }

    
    public final T15 value15() {
        return (T15) getValue(14);
    }

    
    public final T16 value16() {
        return (T16) getValue(15);
    }

    
    public final T17 value17() {
        return (T17) getValue(16);
    }

    
    public final T18 value18() {
        return (T18) getValue(17);
    }

    
    public final T19 value19() {
        return (T19) getValue(18);
    }

    
    public final T20 value20() {
        return (T20) getValue(19);
    }

    
    public final T21 value21() {
        return (T21) getValue(20);
    }

    
    public final T22 value22() {
        return (T22) getValue(21);
    }
}
