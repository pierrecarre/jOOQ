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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jooq.Field;
import org.jooq.OrderByFieldList;
import org.jooq.SortOrder;

/**
 * @author Lukas Eder
 */
class OrderByFieldListImpl extends FieldListImpl implements OrderByFieldList {

	private static final long serialVersionUID = -1825164005148183725L;

	private final Map<Field<?>, SortOrder> ordering;

	OrderByFieldListImpl() {
		this(new ArrayList<Field<?>>());
	}

	OrderByFieldListImpl(List<Field<?>> wrappedList) {
		super(wrappedList);

		this.ordering = new HashMap<Field<?>, SortOrder>();
	}

	private Map<Field<?>, SortOrder> getOrdering() {
		return ordering;
	}

	private SortOrder getOrdering(Field<?> field) {
		return getOrdering().get(field);
	}

	@Override
	public void add(Field<?> field, SortOrder order) {
		add(field);

		if (order != null) {
			getOrdering().put(field, order);
		}
	}

	@Override
	public void addAll(Collection<Field<?>> fields, Collection<SortOrder> orders) {
		if (fields.size() != orders.size()) {
			throw new IllegalArgumentException("The argument 'fields' and the argument 'orders' must be of equal length");
		}

		Iterator<Field<?>> it1 = fields.iterator();
		Iterator<SortOrder> it2 = orders.iterator();

		while (it1.hasNext() && it2.hasNext()) {
			add(it1.next(), it2.next());
		}
	}

	@Override
	protected String toSQLReference(Field<?> field, boolean inlineParameters) {
		StringBuilder sb = new StringBuilder();

		sb.append(super.toSQLReference(field, inlineParameters));

		if (getOrdering(field) != null) {
			sb.append(" ");
			sb.append(getOrdering(field).toSQL());
		}

		return sb.toString();
	}
}
