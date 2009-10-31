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

package org.jooq;

import java.util.Collection;

/**
 * A query for data selection
 * 
 * @author Lukas Eder
 */
public interface SelectQuery extends Query, ConditionProvider {
	
	/**
	 * @return The list of select fields
	 */
	FieldList getSelect();
	
	/**
	 * @return The list of tables from which selection is made
	 */
	TableList getFrom();
	
	/**
	 * @return The list of join statements
	 */
	JoinList getJoin();
	
	/**
	 * @return A list of grouping fields
	 */
	FieldList getGroupBy();
	
	/**
	 * @return A list of ordering fields, and their corresponding sort order
	 */
	OrderByFieldList getOrderBy();

	/**
	 * {@inheritDoc}
	 */
	@Override
	Condition getWhere();

	/**
	 * Add a list of select fields
	 * 
	 * @param fields
	 */
	void addSelect(Field<?>... fields);
	
	/**
	 * Add a list of select fields
	 * 
	 * @param fields
	 */
	void addSelect(Collection<Field<?>> fields);
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	void addConditions(Condition... conditions);

	/**
	 * {@inheritDoc}
	 */
	@Override
	void addConditions(Collection<Condition> conditions);

	/**
	 * Add tables to the table product
	 * 
	 * @param from The added tables
	 */
	void addFrom(Table... from);
	
	/**
	 * Add tables to the table product
	 * 
	 * @param from The added tables
	 */
	void addFrom(Collection<Table> from);

	/**
	 * Joins the existing table product to a new table
	 * 
	 * @param table The joined table
	 */
	void addJoin(Table table);

	/**
	 * Joins the existing table product to a new table using a condition
	 * 
	 * @param table The joined table
	 * @param condition The joining condition
	 */
	void addJoin(Table table, JoinCondition<?> condition);
	
	/**
	 * Joins the existing table product to a new table joining on two fields
	 * 
	 * @param <T> The common field type
	 * @param table The joined table
	 * @param field1 The left field of the join condition
	 * @param field2 The right field of the join condition
	 */
	<T> void addJoin(Table table, Field<T> field1, Field<T> field2);

	/**
	 * Joins the existing table product to join object
	 * 
	 * @param join The join object
	 */
	void addJoin(Join join);

	/**
	 * Adds grouping fields
	 * 
	 * @param fields The grouping fields
	 */
	void addGroupBy(Field<?>... fields);

	/**
	 * Adds grouping fields
	 * 
	 * @param fields The grouping fields
	 */
	void addGroupBy(Collection<Field<?>> fields);

	/**
	 * Adds an ordering field, ordering by the default sort order
	 * 
	 * @param field The ordering field
	 */
	void addOrderBy(Field<?> field);

	/**
	 * Adds an ordering field
	 * 
	 * @param field The ordering field
	 * @param order The sort order
	 */
	void addOrderBy(Field<?> field, SortOrder order);
}
