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
import org.jooq.JoinType;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Select;
import org.jooq.SelectFromStep;
import org.jooq.SelectGroupByStep;
import org.jooq.SelectHavingStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectOnStep;
import org.jooq.SelectOrderByStep;
import org.jooq.SelectQuery;
import org.jooq.SelectStep;
import org.jooq.SelectWhereStep;
import org.jooq.SortOrder;
import org.jooq.Table;

/**
 * A wrapper for a {@link SelectQuery} implementing all steps used in
 * {@link Select}
 *
 * @author Lukas Eder
 */
class SelectImpl<R extends Record> implements Select<R>, SelectStep<R>, SelectFromStep<R>, SelectJoinStep<R>, SelectOnStep<R>, SelectWhereStep<R>,
		SelectGroupByStep<R>, SelectHavingStep<R>, SelectOrderByStep<R> {

	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = -5425308887382166448L;

	/**
	 * The wrapped query
	 */
	private final SelectQuery<R> query;

	/**
	 * A temporary member holding a join
	 */
	private transient Table<?> join;

	/**
	 * A temporary member holding a join type
	 */
	private transient JoinType joinType;

	SelectImpl() {
		this(new SelectQueryImpl<R>());
	}

	SelectImpl(SelectQuery<R> query) {
		this.query = query;
	}

	@Override
	public SelectFromStep<R> select(Field<?>... fields) {
		query.addSelect(fields);
		return this;
	}

	@Override
	public SelectFromStep<R> select(Collection<Field<?>> fields) {
		query.addSelect(fields);
		return this;
	}

	@Override
	public SelectJoinStep<R> from(Table<?>... tables) {
		query.addFrom(tables);
		return this;
	}

	@Override
	public SelectJoinStep<R> from(Collection<Table<?>> tables) {
		query.addFrom(tables);
		return this;
	}

	@Override
	public SelectGroupByStep<R> where(Condition... conditions) {
		query.addConditions(conditions);
		return this;
	}

	@Override
	public SelectGroupByStep<R> where(Collection<Condition> conditions) {
		query.addConditions(conditions);
		return this;
	}

	@Override
	public SelectHavingStep<R> groupBy(Field<?>... fields) {
		query.addGroupBy(fields);
		return this;
	}

	@Override
	public SelectHavingStep<R> groupBy(Collection<Field<?>> fields) {
		query.addGroupBy(fields);
		return this;
	}

	@Override
	public SelectOrderByStep<R> orderBy(Field<?>... fields) {
		query.addOrderBy(fields);
		return this;
	}

	@Override
	public SelectOrderByStep<R> orderBy(Collection<Field<?>> fields) {
		query.addOrderBy(fields);
		return this;
	}

	@Override
	public SelectOrderByStep<R> orderBy(Field<?> field, SortOrder order) {
		query.addOrderBy(field, order);
		return this;
	}

	@Override
	public Select<R> union(Select<R> select) {
		return new SelectImpl<R>(query.combine(UNION, select.getQuery()));
	}

	@Override
	public Select<R> unionAll(Select<R> select) {
		return new SelectImpl<R>(query.combine(UNION_ALL, select.getQuery()));
	}

	@Override
	public Select<R> except(Select<R> select) {
		return new SelectImpl<R>(query.combine(EXCEPT, select.getQuery()));
	}

	@Override
	public Select<R> intersect(Select<R> select) {
		return new SelectImpl<R>(query.combine(INTERSECT, select.getQuery()));
	}

	@Override
	public SelectQuery<R> getQuery() {
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
	public SelectOrderByStep<R> having(Condition... conditions) {
		query.addHaving(conditions);
		return this;
	}

	@Override
	public SelectOrderByStep<R> having(Collection<Condition> conditions) {
		query.addHaving(conditions);
		return this;
	}

	@Override
	public SelectJoinStep<R> on(Condition... conditions) {
		query.addJoin(new JoinImpl(join, joinType, conditions));
		join = null;
		joinType = null;
		return this;
	}

	@Override
	public SelectOnStep<R> join(Table<?> table) {
		join = table;
		joinType = JoinType.JOIN;
		return this;
	}

	@Override
	public SelectOnStep<R> leftJoin(Table<?> table) {
		join = table;
		joinType = JoinType.LEFT_JOIN;
		return this;
	}

	@Override
	public SelectOnStep<R> leftOuterJoin(Table<?> table) {
		join = table;
		joinType = JoinType.LEFT_OUTER_JOIN;
		return this;
	}

	@Override
	public SelectOnStep<R> rightJoin(Table<?> table) {
		join = table;
		joinType = JoinType.RIGHT_JOIN;
		return this;
	}

	@Override
	public SelectOnStep<R> rightOuterJoin(Table<?> table) {
		join = table;
		joinType = JoinType.RIGHT_OUTER_JOIN;
		return this;
	}

	@Override
	public Select<R> getSelect() {
		return this;
	}

	@Override
	public String toString() {
		return query.toString();
	}
}
