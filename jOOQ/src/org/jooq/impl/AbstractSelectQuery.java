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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

import org.jooq.CombineOperator;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.ExistsCondition;
import org.jooq.ExistsOperator;
import org.jooq.Field;
import org.jooq.FieldList;
import org.jooq.Join;
import org.jooq.JoinCondition;
import org.jooq.JoinList;
import org.jooq.Limit;
import org.jooq.OrderByFieldList;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectQuery;
import org.jooq.SortOrder;
import org.jooq.SubQueryCondition;
import org.jooq.SubQueryOperator;
import org.jooq.Table;
import org.jooq.TableList;

/**
 * @author Lukas Eder
 */
abstract class AbstractSelectQuery extends AbstractQuery implements SelectQuery {

	private static final long serialVersionUID = 1555503854543561285L;

	private ResultImpl result;
	private final FieldList select;
	private final TableList from;
	private final JoinList join;
	private final ConditionProviderImpl condition;
	private final FieldList groupBy;
	private final ConditionProviderImpl having;
	private final OrderByFieldList orderBy;
	private final LimitImpl limit;

	AbstractSelectQuery() {
		this(null);
	}

	AbstractSelectQuery(Table from) {
		this.select = new SelectFieldListImpl();
		this.from = new TableListImpl();
		this.join = new JoinListImpl();
		this.condition = new ConditionProviderImpl();
		this.groupBy = new FieldListImpl();
		this.having = new ConditionProviderImpl();
		this.orderBy = new OrderByFieldListImpl();
		this.limit = new LimitImpl();

		if (from != null) {
			this.from.add(from);
		}
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
	public void addNullCondition(Field<?> field) {
		condition.addNullCondition(field);
	}

	@Override
	public void addNotNullCondition(Field<?> field) {
		condition.addNotNullCondition(field);
	}

	@Override
	public <T> void addInCondition(Field<T> field, Collection<T> values) {
		condition.addInCondition(field, values);
	}

	@Override
	public <T> void addInCondition(Field<T> field, T... values) {
		condition.addInCondition(field, values);
	}

	@Override
	public void addFrom(Collection<Table> from) {
		getFrom().addAll(from);
	}

	@Override
	public final void addFrom(Table... from) {
		addFrom(Arrays.asList(from));
	}

	@Override
	public void addGroupBy(Collection<Field<?>> fields) {
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
		addHaving(QueryFactory.createCompareCondition(field, value, comparator));
	}

	@Override
	public final void addHaving(Condition... conditions) {
		addHaving(Arrays.asList(conditions));
	}

	@Override
	public void addHaving(Collection<Condition> conditions) {
		having.addConditions(conditions);
	}

	@Override
	public final void addLimit(int numberOfRows) {
		addLimit(1, numberOfRows);
	}

	@Override
	public void addLimit(int lowerBound, int numberOfRows) {
		limit.setLowerBound(lowerBound);
		limit.setNumberOfRows(numberOfRows);
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

	final FieldList getSelect0() {
		return select;
	}

	@Override
	public FieldList getSelect() {
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

	@Override
	public Class<? extends Record> getRecordType() {
		return getFrom().getRecordType();
	}

	@Override
	protected final int execute(PreparedStatement statement) throws SQLException {
		ResultSet rs = null;

		try {
			rs = statement.executeQuery();
			result = new ResultImpl(this);

			while (rs.next()) {
				Record record = null;

				Class<? extends Record> recordType = getRecordType();
				try {
					record = recordType.getConstructor(Result.class).newInstance(result);
				} catch (Exception e) {
					record = new RecordImpl(result);
				}

				for (Field<?> f : getSelect()) {

					@SuppressWarnings("unchecked")
					Field<Object> field = (Field<Object>) f;
					Object value = FieldTypeHelper.getFromResultSet(rs, f);

					record.setValue(field, value);
				}

				result.addRecord(record);
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}

		return result.getNumberOfRecords();
	}

	@Override
	public final Result getResult() {
		return result;
	}

	final TableList getFrom() {
		return from;
	}

	final FieldList getGroupBy() {
		return groupBy;
	}

	final JoinList getJoin() {
		return join;
	}

	final Limit getLimit() {
		return limit;
	}

	final Condition getWhere() {
		return condition.getWhere();
	}

	final Condition getHaving() {
		return having.getWhere();
	}

	final OrderByFieldList getOrderBy() {
		return orderBy;
	}

	@Override
	public final void addOrderBy(Field<?> field, SortOrder order) {
		getOrderBy().add(field, order);
	}

	@Override
	public final void addOrderBy(Field<?> field) {
		addOrderBy(field, null);
	}

	@Override
	public final SelectQuery combine(SelectQuery other) {
		return combine(other, CombineOperator.UNION);
	}

	@Override
	public final SelectQuery combine(SelectQuery other, CombineOperator operator) {
		return new CombinedSelectQueryImpl(this, other, operator);
	}

	@Override
	public final Table asTable() {
		return new SelectQueryAsTable(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <T> Field<T> asField() {
		if (getSelect().size() != 1) {
			throw new IllegalStateException("Can only use single-column ResultProviderQuery as a field");
		}

		return new SelectQueryAsField<T>(this, (Class<T>) getSelect().get(0).getType());
	}

	@Override
	public final <T> SubQueryCondition<T> asInCondition(Field<T> field) {
		return asSubQueryCondition(field, SubQueryOperator.IN);
	}

	@Override
	public final <T> SubQueryCondition<T> asNotInCondition(Field<T> field) {
		return asSubQueryCondition(field, SubQueryOperator.NOT_IN);
	}

	@Override
	public final <T> SubQueryCondition<T> asCompareCondition(Field<T> field) {
		return asSubQueryCondition(field, SubQueryOperator.EQUALS);
	}

	@Override
	public final <T> SubQueryCondition<T> asSubQueryCondition(Field<T> field, SubQueryOperator operator) {
		if (getSelect().size() != 1) {
			throw new IllegalStateException("Can only use single-column ResultProviderQuery as an InCondition");
		}

		return new SelectQueryAsSubQueryCondition<T>(this, field, operator);
	}

	@Override
	public final ExistsCondition asExistsCondition() {
		return asExistsCondition(ExistsOperator.EXISTS);
	}

	@Override
	public final ExistsCondition asNotExistsCondition() {
		return asExistsCondition(ExistsOperator.NOT_EXISTS);
	}

	private final ExistsCondition asExistsCondition(ExistsOperator operator) {
		if (getSelect().size() != 1) {
			throw new IllegalStateException("Can only use single-column ResultProviderQuery as an InCondition");
		}

		return new SelectQueryAsExistsCondition(this, operator);
	}
}
