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

import static org.jooq.CombineOperator.EXCEPT;
import static org.jooq.CombineOperator.INTERSECT;
import static org.jooq.CombineOperator.UNION;
import static org.jooq.CombineOperator.UNION_ALL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import javax.sql.DataSource;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Select;
import org.jooq.SelectQuery;
import org.jooq.SimpleSelect;
import org.jooq.SimpleSelectOrderByStep;
import org.jooq.SimpleSelectQuery;
import org.jooq.SimpleSelectStep;
import org.jooq.SimpleSelectWhereStep;
import org.jooq.SortOrder;
import org.jooq.Table;

/**
 * A wrapper for a {@link SelectQuery} implementing all steps used in
 * {@link Select} and {@link SimpleSelect}
 *
 * @author Lukas Eder
 */
class SimpleSelectImpl<R extends Record> extends AbstractDelegatingQueryPart implements

	// Cascading interface implementations for SimpleSelect behaviour
	SimpleSelect<R>, SimpleSelectStep<R>,
	SimpleSelectWhereStep<R>, SimpleSelectOrderByStep<R> {

	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = -5425308887382166448L;

	/**
	 * The wrapped query
	 */
	private final SimpleSelectQuery<R> query;

	SimpleSelectImpl() {
		this(new SimpleSelectQueryImpl<R>());
	}

	SimpleSelectImpl(Table<R> table) {
		this(new SimpleSelectQueryImpl<R>(table));
	}

	SimpleSelectImpl(SimpleSelectQuery<R> query) {
		super(query);

		this.query = query;
	}

	@Override
	public SimpleSelectWhereStep<R> select(Field<?>... fields) {
		query.addSelect(fields);
		return this;
	}

	@Override
	public SimpleSelectWhereStep<R> select(Collection<Field<?>> fields) {
		query.addSelect(fields);
		return this;
	}

	@Override
	public SimpleSelectOrderByStep<R> where(Condition... conditions) {
		query.addConditions(conditions);
		return this;
	}

	@Override
	public SimpleSelectOrderByStep<R> where(Collection<Condition> conditions) {
		query.addConditions(conditions);
		return this;
	}

	@Override
	public SimpleSelectOrderByStep<R> orderBy(Field<?>... fields) {
		query.addOrderBy(fields);
		return this;
	}

	@Override
	public SimpleSelectOrderByStep<R> orderBy(Collection<Field<?>> fields) {
		query.addOrderBy(fields);
		return this;
	}

	@Override
	public SimpleSelectOrderByStep<R> orderBy(Field<?> field, SortOrder order) {
		query.addOrderBy(field, order);
		return this;
	}

	@Override
	public SimpleSelect<R> union(SimpleSelect<R> select) {
		return new SimpleSelectImpl<R>(query.combine(UNION, select.getQuery()));
	}

	@Override
	public SimpleSelect<R> unionAll(SimpleSelect<R> select) {
		return new SimpleSelectImpl<R>(query.combine(UNION_ALL, select.getQuery()));
	}

	@Override
	public SimpleSelect<R> except(SimpleSelect<R> select) {
		return new SimpleSelectImpl<R>(query.combine(EXCEPT, select.getQuery()));
	}

	@Override
	public SimpleSelect<R> intersect(SimpleSelect<R> select) {
		return new SimpleSelectImpl<R>(query.combine(INTERSECT, select.getQuery()));
	}

	@Override
	public SimpleSelectQuery<R> getQuery() {
		return query;
	}

	@Override
	public Result<R> getResult() {
		return query.getResult();
	}

	@Override
	public int execute(DataSource source) throws SQLException {
		return query.execute(source);
	}

	@Override
	public int execute(Connection connection) throws SQLException {
		return query.execute(connection);
	}

	@Override
	public SimpleSelect<R> getSelect() {
		return this;
	}
}
