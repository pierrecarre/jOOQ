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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.jooq.ConditionProvider;
import org.jooq.DeleteQuery;
import org.jooq.Field;
import org.jooq.InsertQuery;
import org.jooq.Record;
import org.jooq.RecordMetaData;
import org.jooq.StoreQuery;
import org.jooq.TableField;
import org.jooq.UpdatableRecord;
import org.jooq.UpdatableTable;
import org.jooq.UpdateQuery;

/**
 * A record implementation for a record holding a primary key
 *
 * @author Lukas Eder
 */
public class UpdatableRecordImpl<R extends Record> extends TableRecordImpl<R> implements UpdatableRecord<R> {

	public UpdatableRecordImpl(RecordMetaData metaData, UpdatableTable<R> table) {
		super(metaData, table);
	}

	@Override
	public final UpdatableTable<R> getTable() {
		return (UpdatableTable<R>) super.getTable();
	}

	@Override
	public final List<TableField<R, ?>> getPrimaryKey() {
		return getTable().getPrimaryKey();
	}

	@Override
	public final void store(Connection con) throws SQLException {
		boolean executeUpdate = false;

		for (TableField<R, ?> field : getPrimaryKey()) {

			// If any primary key value is null or changed, execute an insert
			if (getValue(field) == null || getValue0(field).isChanged()) {
				executeUpdate = false;
				break;
			}

			// If primary key values are unchanged, updates are possible
			else {
				executeUpdate = true;
			}
		}

		if (executeUpdate) {
			storeUpdate(con);
		} else {
			storeInsert(con);
		}
	}

	private final void storeInsert(Connection con) throws SQLException {
		InsertQuery<R> insert = QueryFactory.createInsertQuery(getTable());

		for (Field<?> field : getFields()) {
			addValue(insert, field);
		}

		insert.execute(con);
	}

	private final void storeUpdate(Connection con) throws SQLException {
		UpdateQuery<R> update = QueryFactory.createUpdateQuery(getTable());

		for (Field<?> field : getFields()) {
			if (getValue0(field).isChanged()) {
				addValue(update, field);
			}
		}

		for (Field<?> field : getPrimaryKey()) {
			addCondition(update, field);
		}

		update.execute(con);
	}

	@Override
	public final void delete(Connection con) throws SQLException {
		DeleteQuery<R> delete = QueryFactory.createDeleteQuery(getTable());

		for (Field<?> field : getPrimaryKey()) {
			addCondition(delete, field);
		}

		delete.execute(con);
	}

	/**
	 * Extracted method to ensure generic type safety.
	 */
	private final <T> void addCondition(ConditionProvider<R> provider, Field<T> field) {
		provider.addCompareCondition(field, getValue(field));
	}

	/**
	 * Extracted method to ensure generic type safety.
	 */
	private final <T> void addValue(StoreQuery<R> store, Field<T> field) {
		store.addValue(field, getValue(field));
	}
}
