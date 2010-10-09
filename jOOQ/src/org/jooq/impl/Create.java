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
import java.util.LinkedHashSet;

import org.jooq.BetweenCondition;
import org.jooq.CombinedCondition;
import org.jooq.Comparator;
import org.jooq.CompareCondition;
import org.jooq.Condition;
import org.jooq.DeleteQuery;
import org.jooq.Field;
import org.jooq.InCondition;
import org.jooq.InOperator;
import org.jooq.InsertQuery;
import org.jooq.Join;
import org.jooq.JoinCondition;
import org.jooq.JoinType;
import org.jooq.Operator;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectFromStep;
import org.jooq.SelectQuery;
import org.jooq.SimpleSelect;
import org.jooq.SimpleSelectQuery;
import org.jooq.Table;
import org.jooq.UpdateQuery;

/**
 * A factory providing implementations to the org.jooq interfaces
 *
 * @author Lukas Eder
 */
public final class Create {

	/**
	 * Create a new condition holding plain SQL. There must not be any binding
	 * variables contained in the SQL
	 * <p>
	 * Example:
	 * <p>
	 * <code><pre>
	 * String sql = "(X = 1 and Y = 2)";</pre></code>
	 *
	 * @param sql
	 *            The SQL
	 * @return A condition wrapping the plain SQL
	 */
	public static Condition plainSQLCondition(String sql) {
		return plainSQLCondition(sql, new Object[0]);
	}

	/**
	 * Create a new condition holding plain SQL. There must be as binding
	 * variables contained in the SQL, as passed in the bindings parameter
	 * <p>
	 * Example:
	 * <p>
	 * <code><pre>
	 * String sql = "(X = ? and Y = ?)";
	 * Object[] bindings = new Object[] { 1, 2 };</pre></code>
	 *
	 * @param sql
	 *            The SQL
	 * @param bindings
	 *            The bindings
	 * @return A condition wrapping the plain SQL
	 */
	public static Condition plainSQLCondition(String sql, Object... bindings) {
		return new PlainSQLCondition(sql, bindings);
	}

	/**
	 * Combine a list of conditions with the {@link Operator#AND} operator
	 */
	public static CombinedCondition combinedCondition(Condition... conditions) {
		return combinedCondition(Operator.AND, conditions);
	}

	/**
	 * Combine a collection of conditions with the {@link Operator#AND} operator
	 */
	public static CombinedCondition combinedCondition(Collection<Condition> conditions) {
		return combinedCondition(Operator.AND, conditions);
	}

	/**
	 * Combine a list of conditions with any operator
	 */
	public static CombinedCondition combinedCondition(Operator operator, Condition... conditions) {
		return combinedCondition(operator, Arrays.asList(conditions));
	}

	/**
	 * Combine a collection of conditions with any operator
	 */
	public static CombinedCondition combinedCondition(Operator operator, Collection<Condition> conditions) {
		return new CombinedConditionImpl(operator, conditions);
	}

	/**
	 * Create a {@link BetweenCondition} for a field and two values
	 *
	 * @param <T>
	 *            The generic type parameter
	 * @param field
	 *            The field to compare to the values
	 * @param minValue
	 *            The lower bound
	 * @param maxValue
	 *            The upper bound
	 * @return A {@link BetweenCondition}
	 */
	public static <T> BetweenCondition<T> betweenCondition(Field<T> field, T minValue, T maxValue) {
		return new BetweenConditionImpl<T>(field, minValue, maxValue);
	}

	/**
	 * Create an {@link InCondition} for a list of values
	 *
	 * @param <T>
	 *            The generic type parameter
	 * @param field
	 *            The field to compare to the values
	 * @param values
	 *            The accepted values
	 * @return An {@link InCondition}
	 */
	public static <T> InCondition<T> inCondition(Field<T> field, T... values) {
		return inCondition(field, Arrays.asList(values));
	}

	/**
	 * Create an {@link InCondition} for a collection of values to be excluded
	 *
	 * @param <T>
	 *            The generic type parameter
	 * @param field
	 *            The field to compare to the values
	 * @param values
	 *            The excluded values
	 * @return An {@link InCondition}
	 */
	public static <T> InCondition<T> notInCondition(Field<T> field, Collection<T> values) {
		return new InConditionImpl<T>(field, new LinkedHashSet<T>(values), InOperator.NOT_IN);
	}

	/**
	 * Create an {@link InCondition} for a list of values to be excluded
	 *
	 * @param <T>
	 *            The generic type parameter
	 * @param field
	 *            The field to compare to the values
	 * @param values
	 *            The excluded values
	 * @return An {@link InCondition}
	 */
	public static <T> InCondition<T> notInCondition(Field<T> field, T... values) {
		return notInCondition(field, Arrays.asList(values));
	}

	/**
	 * Create an {@link InCondition} for a collection of values
	 *
	 * @param <T>
	 *            The generic type parameter
	 * @param field
	 *            The field to compare to the values
	 * @param values
	 *            The accepted values
	 * @return An {@link InCondition}
	 */
	public static <T> InCondition<T> inCondition(Field<T> field, Collection<T> values) {
		return new InConditionImpl<T>(field, new LinkedHashSet<T>(values));
	}

	/**
	 * Create a condition comparing a field with a value using the
	 * {@link Comparator#EQUALS} comparator
	 *
	 * @param <T>
	 *            The generic type parameter
	 * @param field
	 *            The field to compare to the value
	 * @param value
	 *            The accepted value
	 * @return A {@link CompareCondition}
	 */
	public static <T> CompareCondition<T> compareCondition(Field<T> field, T value) {
		return compareCondition(field, value, Comparator.EQUALS);
	}

	/**
	 * Create a condition comparing a field with a value using any comparator
	 *
	 * @param <T>
	 *            The generic type parameter
	 * @param field
	 *            The field to compare to the value
	 * @param value
	 *            The accepted value
	 * @param comparator
	 *            The comparator
	 * @return A {@link CompareCondition}
	 */
	public static <T> CompareCondition<T> compareCondition(Field<T> field, T value, Comparator comparator) {
		return new CompareConditionImpl<T>(field, value, comparator);
	}

	/**
	 * Create a condition comparing a field with <code>null</code>
	 *
	 * @param <T>
	 *            The generic type parameter
	 * @param field
	 *            The field to compare to null
	 * @return A {@link CompareCondition}
	 */
	public static <T> CompareCondition<T> nullCondition(Field<T> field) {
		return compareCondition(field, null, Comparator.EQUALS);
	}

	/**
	 * Create a condition comparing a field with <code>null</code>
	 *
	 * @param <T>
	 *            The generic type parameter
	 * @param field
	 *            The field to compare to null
	 * @return A {@link CompareCondition}
	 */
	public static <T> CompareCondition<T> notNullCondition(Field<T> field) {
		return compareCondition(field, null, Comparator.NOT_EQUALS);
	}

	/**
	 * Create a condition comparing two fields (typically used for joins)
	 *
	 * @param <T>
	 *            The generic type parameter
	 * @param field1
	 *            The first field
	 * @param field2
	 *            The second field
	 * @return A {@link JoinCondition}
	 */
	public static <T> JoinCondition<T> joinCondition(Field<T> field1, Field<T> field2) {
		return joinCondition(field1, field2, Comparator.EQUALS);
	}

	/**
	 * Create a condition comparing two fields (typically used for joins)
	 *
	 * @param <T>
	 *            The generic type parameter
	 * @param field1
	 *            The first field
	 * @param field2
	 *            The second field
	 * @param comparator
	 * 			  The comparator to compare the two fields with
	 * @return A {@link JoinCondition}
	 */
	public static <T> JoinCondition<T> joinCondition(Field<T> field1, Field<T> field2, Comparator comparator) {
		return new JoinConditionImpl<T>(field1, field2);
	}

	/**
	 * Create a new {@link InsertQuery}
	 *
	 * @param into
	 *            The table to insert data into
	 * @return The new {@link InsertQuery}
	 */
	public static <R extends Record> InsertQuery<R> insertQuery(Table<R> into) {
		return new InsertQueryImpl<R>(into);
	}

	/**
	 * Create a new {@link UpdateQuery}
	 *
	 * @param table
	 *            The table to update data into
	 * @return The new {@link UpdateQuery}
	 */
	public static <R extends Record> UpdateQuery<R> updateQuery(Table<R> table) {
		return new UpdateQueryImpl<R>(table);
	}

	/**
	 * Create a new {@link DeleteQuery}
	 *
	 * @param table
	 *            The table to delete data from
	 * @return The new {@link DeleteQuery}
	 */
	public static <R extends Record> DeleteQuery<R> deleteQuery(Table<R> table) {
		return new DeleteQueryImpl<R>(table);
	}

	/**
	 * Create a new {@link Select}
	 */
	public static Select select() {
		return new SelectImpl();
	}

	/**
	 * Create a new {@link Select}
	 */
	public static <R extends Record> SimpleSelect<R> select(Table<R> table) {
		return new SimpleSelectImpl<R>(table);
	}

	/**
	 * Create a new {@link Select}
	 */
	public static SelectFromStep select(Field<?>... fields) {
		return new SelectImpl().select(fields);
	}

	/**
	 * Create a new {@link Select}
	 */
	public static SelectFromStep select(Collection<Field<?>> fields) {
		return new SelectImpl().select(fields);
	}

	/**
	 * Create a new {@link SelectQuery}
	 */
	public static SelectQuery selectQuery() {
		return new SelectQueryImpl();
	}

	/**
	 * Create a new {@link SelectQuery}
	 *
	 * @param table
	 *            The table to select data from
	 * @return The new {@link SelectQuery}
	 */
	@SuppressWarnings("unchecked")
	public static <R extends Record> SimpleSelectQuery<R> selectQuery(Table<R> table) {
		return (SimpleSelectQuery<R>) new SelectQueryImpl(table);
	}

	/**
	 * Create a new {@link Join} part using a {@link JoinCondition}
	 *
	 * @param <T>
	 *            The generic type parameter
	 * @param table
	 *            The table to join
	 * @param field1
	 *            The first field of the join condition
	 * @param field2
	 *            The second field of the join condition
	 * @return A new {@link Join} part
	 */
	public static <T> Join join(Table<?> table, Field<T> field1, Field<T> field2) {
		return join(table, JoinType.JOIN, field1, field2);
	}

	/**
	 * Create a new {@link Join} part using any number of conditions
	 *
	 * @param table
	 *            The table to join
	 * @param conditions
	 *            Any number of conditions
	 * @return A new {@link Join} part
	 */
	public static Join join(Table<?> table, Condition... conditions) {
		return join(table, JoinType.JOIN, conditions);
	}

	/**
	 * Create a new {@link Join} part using a {@link JoinCondition}
	 *
	 * @param <T>
	 *            The generic type parameter
	 * @param table
	 *            The table to join
	 * @param type
	 *            The join type
	 * @param field1
	 *            The first field of the join condition
	 * @param field2
	 *            The second field of the join condition
	 * @return A new {@link Join} part
	 */
	public static <T> Join join(Table<?> table, JoinType type, Field<T> field1, Field<T> field2) {
		return join(table, type, joinCondition(field1, field2));
	}

	/**
	 * Create a new {@link Join} part using any number of conditions
	 *
	 * @param table
	 *            The table to join
	 * @param type
	 *            The join type
	 * @param conditions
	 *            Any number of conditions
	 * @return A new {@link Join} part
	 */
	public static Join join(Table<?> table, JoinType type, Condition... conditions) {
		return new JoinImpl(table, type, conditions);
	}

	/**
	 * No instances
	 */
	private Create() {
	}
}
