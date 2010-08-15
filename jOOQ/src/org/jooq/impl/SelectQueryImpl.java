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

import static org.jooq.impl.TrueCondition.TRUE_CONDITION;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.FieldList;
import org.jooq.Join;
import org.jooq.JoinCondition;
import org.jooq.JoinList;
import org.jooq.OrderByFieldList;
import org.jooq.SelectQuery;
import org.jooq.SortOrder;
import org.jooq.Table;
import org.jooq.TableList;

/**
 * @author Lukas Eder
 */
class SelectQueryImpl extends AbstractResultProviderQuery implements SelectQuery {

	private static final long serialVersionUID = -4128783317946627405L;

	private final FieldList select;
	private final TableList from;
	private final JoinList join;
	private final ConditionProviderImpl condition;
	private final FieldList groupBy;
	private final OrderByFieldList orderBy;

	SelectQueryImpl(Table from) {
		this.select = new SelectFieldListImpl();
		this.from = new TableListImpl();
		this.join = new JoinListImpl();
		this.condition = new ConditionProviderImpl();
		this.groupBy = new FieldListImpl();
		this.orderBy = new OrderByFieldListImpl();

		if (from != null) {
			this.from.add(from);
		}
	}

	@Override
	public int bind(PreparedStatement stmt, int initialIndex) throws SQLException {
		int result = initialIndex;

		result = getSelect0().bind(stmt, result);
		result = getFrom().bind(stmt, result);
		result = getJoin().bind(stmt, result);
		result = getWhere().bind(stmt, result);
		result = getGroupBy().bind(stmt, result);
		result = getOrderBy().bind(stmt, result);

		return result;
	}

	@Override
	public void addSelect(Collection<Field<?>> fields) {
		getSelect0().addAll(fields);
	}

	@Override
	public final void addSelect(Field<?>... fields) {
		addSelect(Arrays.asList(fields));
	}

	@Override
	public final void addConditions(Condition... conditions) {
		condition.addConditions(conditions);
	}

	@Override
	public final void addConditions(Collection<Condition> conditions) {
		condition.addConditions(conditions);
	}

	@Override
	public <T> void addBetweenCondition(Field<T> field, T minValue, T maxValue) {
		condition.addBetweenCondition(field, minValue, maxValue);
	}

	@Override
	public <T> void addCompareCondition(Field<T> field, T value, Comparator comparator) {
		condition.addCompareCondition(field, value, comparator);
	}

	@Override
	public <T> void addCompareCondition(Field<T> field, T value) {
		condition.addCompareCondition(field, value);
	}

	@Override
	public <T> void addInCondition(Field<T> field, Collection<T> values) {
		condition.addInCondition(field, values);
	}

	@Override
	public <T> void addInCondition(Field<T> field, T... values) {
		condition.addInCondition(field, values);
	}

	TableList getFrom() {
		return from;
	}

	@Override
	public void addFrom(Collection<Table> from) {
		getFrom().addAll(from);
	}

	@Override
	public final void addFrom(Table... from) {
		addFrom(Arrays.asList(from));
	}

	FieldList getGroupBy() {
		return groupBy;
	}

	@Override
	public void addGroupBy(Collection<Field<?>> fields) {
		getGroupBy().addAll(fields);
	}

	@Override
	public final void addGroupBy(Field<?>... fields) {
		addGroupBy(Arrays.asList(fields));
	}

	JoinList getJoin() {
		return join;
	}

	@Override
	public void addJoin(Join join) {
		getJoin().add(join);
	}

	@Override
	public final <T> void addJoin(Table table, Field<T> field1, Field<T> field2) {
		addJoin(QueryFactory.createJoin(table, field1, field2));
	}

	@Override
	public final void addJoin(Table table, JoinCondition<?> condition) {
		addJoin(QueryFactory.createJoin(table, condition));
	}

	@Override
	public final void addJoin(Table table) {
		addJoin(QueryFactory.createJoin(table));
	}

	OrderByFieldList getOrderBy() {
		return orderBy;
	}

	@Override
	public void addOrderBy(Field<?> field, SortOrder order) {
		getOrderBy().add(field, order);
	}

	@Override
	public final void addOrderBy(Field<?> field) {
		addOrderBy(field, null);
	}

	private FieldList getSelect0() {
		return select;
	}

	@Override
	protected FieldList getSelect() {
		if (getSelect0().isEmpty()) {
			FieldList result = new SelectFieldListImpl();

			for (Table table : getFrom()) {
				for (Field<?> field : table.getFields()) {
					result.add(field);
				}
			}

			for (Join join : getJoin()) {
				for (Field<?> field : join.getTable().getFields()) {
					result.add(field);
				}
			}

			return result;
		}

		return getSelect0();
	}

	final Condition getWhere() {
		return condition.getWhere();
	}

	@Override
	public String toSQLReference(boolean inlineParameters) {
		StringBuilder sb = new StringBuilder();

		sb.append("select ");
		sb.append(getSelect0().toSQLDeclaration(inlineParameters));

		sb.append(" from ");
		sb.append(getFrom().toSQLDeclaration(inlineParameters));

		if (!getJoin().isEmpty()) {
			sb.append(" ");
			sb.append(getJoin().toSQLDeclaration(inlineParameters));
		}

		if (getWhere() != TRUE_CONDITION) {
			sb.append(" where ");
			sb.append(getWhere().toSQLReference(inlineParameters));
		}

		if (!getGroupBy().isEmpty()) {
			sb.append(" group by ");
			sb.append(getGroupBy().toSQLReference(inlineParameters));
		}

		if (!getOrderBy().isEmpty()) {
			sb.append(" order by ");
			sb.append(getOrderBy().toSQLReference(inlineParameters));
		}

		return sb.toString();
	}
}
