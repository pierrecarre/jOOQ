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

import java.util.Arrays;
import java.util.Collection;

import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.JoinType;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.jooq.Table;

/**
 * @author Lukas Eder
 */
class SelectQueryImpl extends AbstractResultProviderSelectQuery<SelectQuery, Record> implements SelectQuery {

    private static final long serialVersionUID = 1555503854543561285L;

    SelectQueryImpl(Configuration configuration) {
        this(configuration, null);
    }

    SelectQueryImpl(Configuration configuration, Table<Record> from) {
        super(configuration, from);
    }

    @Override
    final SelectQuery newSelect(Table<Record> from) {
        return new SelectQueryImpl(getConfiguration(), from);
    }

    @Override
    public final void addFrom(Collection<Table<?>> from) {
        getFrom().addAll(from);
    }

    @Override
    public final void addFrom(Table<?>... from) {
        addFrom(Arrays.asList(from));
    }

    @Override
    public final void addGroupBy(Collection<Field<?>> fields) {
        getGroupBy().addAll(fields);
    }

    @Override
    public final void addGroupBy(Field<?>... fields) {
        addGroupBy(Arrays.asList(fields));
    }

    @Override
    public final <T> void addHaving(Field<T> field, T value) {
        addHaving(field, value, Comparator.EQUALS);
    }

    @Override
    public final <T> void addHaving(Field<T> field, T value, Comparator comparator) {
        addHaving(create().compareCondition(field, value, comparator));
    }

    @Override
    public final void addHaving(Condition... conditions) {
        addHaving(Arrays.asList(conditions));
    }

    @Override
    public final void addHaving(Collection<Condition> conditions) {
        getHaving().addConditions(conditions);
    }

    @Override
    public final <T> void addJoin(Table<?> table, Field<T> field1, Field<T> field2) {
        getJoin().add(create().join(table, field1, field2));
    }

    @Override
    public final void addJoin(Table<?> table, Condition... conditions) {
        getJoin().add(create().join(table, conditions));
    }

    @Override
    public final <T> void addJoin(Table<?> table, JoinType type, Field<T> field1, Field<T> field2) {
        getJoin().add(create().join(table, type, field1, field2));
    }

    @Override
    public final void addJoin(Table<?> table, JoinType type, Condition... conditions) {
        getJoin().add(create().join(table, type, conditions));
    }
}
