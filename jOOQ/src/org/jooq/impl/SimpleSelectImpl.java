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

import java.util.Collection;

import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectQuery;
import org.jooq.SimpleSelectFinalStep;
import org.jooq.SimpleSelectLimitStep;
import org.jooq.SimpleSelectOrderByStep;
import org.jooq.SimpleSelectQuery;
import org.jooq.SimpleSelectWhereStep;
import org.jooq.SortField;
import org.jooq.Table;

/**
 * A wrapper for a {@link SelectQuery}
 *
 * @author Lukas Eder
 */
class SimpleSelectImpl<R extends Record> extends AbstractDelegatingResultProviderQuery<R>
    implements

    // Cascading interface implementations for SimpleSelect behaviour
    SimpleSelectWhereStep<R>, SimpleSelectOrderByStep<R>,
    SimpleSelectLimitStep<R> {

    /**
     * Generated UID
     */
    private static final long          serialVersionUID = -5425308887382166448L;

    SimpleSelectImpl(Configuration configuration) {
        this(configuration, new SimpleSelectQueryImpl<R>(configuration));
    }

    SimpleSelectImpl(Configuration configuration, Table<R> table) {
        this(configuration, table, false);
    }

    SimpleSelectImpl(Configuration configuration, Table<R> table, boolean distinct) {
        this(configuration, new SimpleSelectQueryImpl<R>(configuration, table, distinct));
    }

    SimpleSelectImpl(Configuration configuration, Select<R> query) {
        super(configuration.getDialect(), query);
    }

    private final SimpleSelectQuery<R> getQuery() {
        return (SimpleSelectQuery<R>) query;
    }

    @Override
    public final SimpleSelectOrderByStep<R> where(Condition... conditions) {
        getQuery().addConditions(conditions);
        return this;
    }

    @Override
    public final SimpleSelectOrderByStep<R> where(Collection<Condition> conditions) {
        getQuery().addConditions(conditions);
        return this;
    }

    @Override
    public final SimpleSelectLimitStep<R> orderBy(Field<?>... fields) {
        getQuery().addOrderBy(fields);
        return this;
    }

    @Override
    public final SimpleSelectOrderByStep<R> orderBy(SortField<?>... fields) {
        getQuery().addOrderBy(fields);
        return this;
    }

    @Override
    public final SimpleSelectOrderByStep<R> orderBy(Collection<SortField<?>> fields) {
        getQuery().addOrderBy(fields);
        return this;
    }

    @Override
    public final SimpleSelectFinalStep<R> limit(int numberOfRows) {
        getQuery().addLimit(numberOfRows);
        return this;
    }

    @Override
    public final SimpleSelectFinalStep<R> limit(int lowerBound, int numberOfRows) {
        getQuery().addLimit(lowerBound, numberOfRows);
        return this;
    }

    @Override
    public final Select<R> union(Select<R> select) {
        return new SimpleSelectImpl<R>(getQuery().getConfiguration(), getQuery().union(select));
    }

    @Override
    public final Select<R> unionAll(Select<R> select) {
        return new SimpleSelectImpl<R>(getQuery().getConfiguration(), getQuery().unionAll(select));
    }

    @Override
    public final Select<R> except(Select<R> select) {
        return new SimpleSelectImpl<R>(getQuery().getConfiguration(), getQuery().except(select));
    }

    @Override
    public final Select<R> intersect(Select<R> select) {
        return new SimpleSelectImpl<R>(getQuery().getConfiguration(), getQuery().intersect(select));
    }
}
