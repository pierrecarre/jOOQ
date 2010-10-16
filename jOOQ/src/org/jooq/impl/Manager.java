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

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.jooq.DeleteQuery;
import org.jooq.InsertQuery;
import org.jooq.Record;
import org.jooq.SimpleSelect;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableRecord;
import org.jooq.UpdateQuery;

/**
 * The Manager class provides many useful shortcuts to common database tasks,
 * such as <code><pre>SELECT * FROM [table] WHERE [id = x]</pre></code>
 *
 * @author Lukas Eder
 */
public final class Manager {

    private transient Factory factory;

    Manager(Factory factory) {
        this.factory = factory;
    }

    public <R extends Record> List<R> select(Table<R> table) throws SQLException {
        SimpleSelect<R> select = factory.select(table);
        select.execute();
        return select.getResult().getRecords();
    }

    public <R extends Record, T> List<R> select(Table<R> table, TableField<R, T> field, T value) throws SQLException {
        SimpleSelect<R> select = factory.select(table).where(field.equal(value)).getSelect();
        select.execute();
        return select.getResult().getRecords();
    }

    public <R extends Record, T> List<R> select(Table<R> table, TableField<R, T> field, T... values) throws SQLException {
        SimpleSelect<R> select = factory.select(table).where(field.in(values)).getSelect();
        select.execute();
        return select.getResult().getRecords();
    }

    public <R extends Record, T> List<R> select(Table<R> table, TableField<R, T> field, Collection<T> values) throws SQLException {
        SimpleSelect<R> select = factory.select(table).where(field.in(values)).getSelect();
        select.execute();
        return select.getResult().getRecords();
    }

    public <R extends Record> R selectOne(Table<R> table) throws SQLException {
        return filterOne(select(table));
    }

    public <R extends Record, T> R selectOne(Table<R> table, TableField<R, T> field, T value) throws SQLException {
        return filterOne(select(table, field, value));
    }

    public <R extends TableRecord<R>> int insert(Table<R> table, R record) throws SQLException {
        InsertQuery<R> insert = factory.insertQuery(table);
        insert.setRecord(record);
        return insert.execute();
    }

    public <R extends TableRecord<R>> int update(Table<R> table, R record) throws SQLException {
        UpdateQuery<R> update = factory.updateQuery(table);
        update.setRecord(record);
        return update.execute();
    }

    public <R extends TableRecord<R>, T> int update(Table<R> table, R record, TableField<R, T> field, T value) throws SQLException {
        UpdateQuery<R> update = factory.updateQuery(table);
        update.addCompareCondition(field, value);
        update.setRecord(record);
        return update.execute();
    }

    public <R extends TableRecord<R>, T> int update(Table<R> table, R record, TableField<R, T> field, T... values) throws SQLException {
        UpdateQuery<R> update = factory.updateQuery(table);
        update.addInCondition(field, values);
        update.setRecord(record);
        return update.execute();
    }

    public <R extends TableRecord<R>, T> int update(Table<R> table, R record, TableField<R, T> field, Collection<T> values) throws SQLException {
        UpdateQuery<R> update = factory.updateQuery(table);
        update.addInCondition(field, values);
        update.setRecord(record);
        return update.execute();
    }

    public <R extends TableRecord<R>> int updateOne(Table<R> table, R record) throws SQLException {
        return filterUpdateOne(update(table, record));
    }

    public <R extends TableRecord<R>, T> int updateOne(Table<R> table, R record, TableField<R, T> field, T value) throws SQLException {
        return filterUpdateOne(update(table, record, field, value));
    }

    public <R extends TableRecord<R>> int delete(Table<R> table) throws SQLException {
        return factory.deleteQuery(table).execute();
    }

    public <R extends TableRecord<R>, T> int delete(Table<R> table, TableField<R, T> field, T value) throws SQLException {
        DeleteQuery<R> delete = factory.deleteQuery(table);
        delete.addCompareCondition(field, value);
        return delete.execute();
    }

    public <R extends TableRecord<R>, T> int delete(Table<R> table, TableField<R, T> field, T... values) throws SQLException {
        DeleteQuery<R> delete = factory.deleteQuery(table);
        delete.addInCondition(field, values);
        return delete.execute();
    }

    public <R extends TableRecord<R>, T> int delete(Table<R> table, TableField<R, T> field, Collection<T> values) throws SQLException {
        DeleteQuery<R> delete = factory.deleteQuery(table);
        delete.addInCondition(field, values);
        return delete.execute();
    }

    public <R extends TableRecord<R>> int deleteOne(Table<R> table) throws SQLException {
        return filterDeleteOne(factory.deleteQuery(table).execute());
    }

    public <R extends TableRecord<R>, T> int deleteOne(Table<R> table, TableField<R, T> field, T value) throws SQLException {
        DeleteQuery<R> delete = factory.deleteQuery(table);
        delete.addCompareCondition(field, value);
        return filterDeleteOne(delete.execute());
    }

    private int filterDeleteOne(int i) throws SQLException {
        return filterOne(i, "deleted");
    }

    private int filterUpdateOne(int i) throws SQLException {
        return filterOne(i, "updated");
    }

    private int filterOne(int i, String action) throws SQLException {
        if (i <= 1) {
            return i;
        }
        else {
            throw new SQLException("Too many rows " + action + " : " + i);
        }
    }

    private <R extends Record> R filterOne(List<R> list) throws SQLException {
        if (list.size() == 0) {
            return null;
        }
        else if (list.size() == 1) {
            return list.get(0);
        }
        else {
            throw new SQLException("Too many rows returned : " + list.size());
        }
    }
}
