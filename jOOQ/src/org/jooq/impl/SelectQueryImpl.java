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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

import org.jooq.CombineOperator;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.ExistsCondition;
import org.jooq.ExistsOperator;
import org.jooq.Field;
import org.jooq.FieldList;
import org.jooq.Join;
import org.jooq.JoinList;
import org.jooq.JoinType;
import org.jooq.Limit;
import org.jooq.OrderByFieldList;
import org.jooq.Record;
import org.jooq.RecordMetaData;
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
class SelectQueryImpl<R extends Record> extends AbstractQuery<R> implements SelectQuery<R> {

	private static final long serialVersionUID = 1555503854543561285L;

	private ResultImpl<R> result;
	private final FieldList select;
	private final TableList from;
	private final JoinList join;
	private final ConditionProviderImpl condition;
	private final FieldList groupBy;
	private final ConditionProviderImpl having;
	private final OrderByFieldList orderBy;
	private final LimitImpl limit;

	SelectQueryImpl() {
		this(null);
	}

	SelectQueryImpl(Table<R> from) {
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
	public int bind(PreparedStatement stmt, int initialIndex) throws SQLException {
		int result = initialIndex;

		result = getSelect0().bind(stmt, result);
		result = getFrom().bind(stmt, result);
		result = getJoin().bind(stmt, result);
		result = getWhere().bind(stmt, result);
		result = getGroupBy().bind(stmt, result);
		result = getHaving().bind(stmt, result);
		result = getOrderBy().bind(stmt, result);

		return result;
	}

	@Override
	public String toSQLReference(boolean inlineParameters) {
		StringBuilder sb = new StringBuilder();

		sb.append("select ");
		sb.append(getSelect0().toSQLDeclaration(inlineParameters));

		if (!getFrom().toSQLDeclaration(inlineParameters).isEmpty()) {
			sb.append(" from ");
			sb.append(getFrom().toSQLDeclaration(inlineParameters));
		}

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

		if (getHaving() != TRUE_CONDITION) {
			sb.append(" having ");
			sb.append(getHaving().toSQLReference(inlineParameters));
		}

		if (!getOrderBy().isEmpty()) {
			sb.append(" order by ");
			sb.append(getOrderBy().toSQLReference(inlineParameters));
		}

		if (getLimit().isApplicable()) {
			sb.append(" ");
			sb.append(getLimit().toSQLReference(inlineParameters));
		}

		return sb.toString();
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
	public void addFrom(Collection<Table<?>> from) {
		getFrom().addAll(from);
	}

	@Override
	public final void addFrom(Table<?>... from) {
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
	public final <T> void addJoin(Table<?> table, Field<T> field1, Field<T> field2) {
		addJoin(QueryFactory.createJoin(table, field1, field2));
	}

	@Override
	public final void addJoin(Table<?> table, Condition... conditions) {
		addJoin(QueryFactory.createJoin(table, conditions));
	}

	@Override
	public final <T> void addJoin(Table<?> table, JoinType type, Field<T> field1, Field<T> field2) {
		addJoin(QueryFactory.createJoin(table, type, field1, field2));
	}

	@Override
	public final void addJoin(Table<?> table, JoinType type, Condition... conditions) {
		addJoin(QueryFactory.createJoin(table, type, conditions));
	}

	final FieldList getSelect0() {
		return select;
	}

	@Override
	public FieldList getSelect() {
		if (getSelect0().isEmpty()) {
			FieldList result = new SelectFieldListImpl();

			for (Table<?> table : getFrom()) {
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
	public TableList getTables() {
		TableList result = new TableListImpl(getFrom());

		for (Join join : getJoin()) {
			result.add(join.getTable());
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<R> getRecordType() {
		return (Class<R>) getTables().getRecordType();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected final int execute(PreparedStatement statement) throws SQLException {
		ResultSet rs = null;

		try {
			rs = statement.executeQuery();
			result = new ResultImpl<R>(this);

			while (rs.next()) {
				R record = null;

				Class<R> recordType = getRecordType();
				try {
					record = recordType.getConstructor(RecordMetaData.class).newInstance(result);
				} catch (Exception e) {
					record = (R) new RecordImpl(result);
				}

				for (Field<?> f : getSelect()) {
					Field<Object> field = (Field<Object>) f;
					Object value = FieldTypeHelper.getFromResultSet(rs, f);

					record.setValue(field, new ValueImpl<Object>(value));
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
	public final Result<R> getResult() {
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
	public final void addOrderBy(Collection<Field<?>> fields) {
		for (Field<?> field : fields) {
			addOrderBy(field, null);
		}
	}

	@Override
	public void addOrderBy(OrderByFieldList fields) {
		for (Field<?> field : fields) {
			addOrderBy(field, fields.getOrdering().get(field));
		}
	}

	@Override
	public final void addOrderBy(Field<?>... fields) {
		addOrderBy(Arrays.asList(fields));
	}

	@Override
	public final SelectQuery<R> combine(SelectQuery<R> other) {
		return combine(CombineOperator.UNION, other);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final SelectQuery<R> combine(CombineOperator operator, SelectQuery<R> other) {
		return new SelectQueryImpl<R>(asTable(operator, this, other));
	}

	@SuppressWarnings("unchecked")
	@Override
	public final Table<R> asTable() {
		return asTable(CombineOperator.UNION, this);
	}

	private Table<R> asTable(CombineOperator operator, SelectQuery<R>... queries) {
		Table<R> result = new SelectQueryAsTable<R>(operator, queries);

		// Some dialects require derived tables to provide an alias
		switch (Configuration.getInstance().getDialect()) {
		case MYSQL:
		case POSTGRES:
			result = result.as("gen_" + (int) (Math.random() * 1000000));
			break;
		}

		return result;
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
