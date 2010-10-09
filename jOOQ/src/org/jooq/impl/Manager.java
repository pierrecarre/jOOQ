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

    public static <R extends Record> List<R> select(Connection connection, Table<R> table) throws SQLException {
        return select0(connection, Create.select(table));
    }

    public static <R extends Record, T> List<R> select(Connection connection, Table<R> table, TableField<R, T> field, T value) throws SQLException {
        return select0(connection, Create.select(table).where(field.equal(value)).getSelect());
    }

    public static <R extends Record, T> List<R> select(Connection connection, Table<R> table, TableField<R, T> field, T... values) throws SQLException {
        return select0(connection, Create.select(table).where(field.in(values)).getSelect());
    }

    public static <R extends Record, T> List<R> select(Connection connection, Table<R> table, TableField<R, T> field, Collection<T> values) throws SQLException {
        return select0(connection, Create.select(table).where(field.in(values)).getSelect());
    }

    public static <R extends Record> R selectOne(Connection connection, Table<R> table) throws SQLException {
        return filterOne(select(connection, table));
    }

    public static <R extends Record, T> R selectOne(Connection connection, Table<R> table, TableField<R, T> field, T value) throws SQLException {
        return filterOne(select(connection, table, field, value));
    }

    public static <R extends TableRecord<R>> int insert(Connection connection, Table<R> table, R record) throws SQLException {
        InsertQuery<R> insert = Create.insertQuery(table);
        insert.setRecord(record);
        return insert.execute(connection);
    }

    public static <R extends TableRecord<R>> int update(Connection connection, Table<R> table, R record) throws SQLException {
        UpdateQuery<R> update = Create.updateQuery(table);
        update.setRecord(record);
        return update.execute(connection);
    }

    public static <R extends TableRecord<R>, T> int update(Connection connection, Table<R> table, R record, TableField<R, T> field, T value) throws SQLException {
        UpdateQuery<R> update = Create.updateQuery(table);
        update.addCompareCondition(field, value);
        update.setRecord(record);
        return update.execute(connection);
    }

    public static <R extends TableRecord<R>, T> int update(Connection connection, Table<R> table, R record, TableField<R, T> field, T... values) throws SQLException {
        UpdateQuery<R> update = Create.updateQuery(table);
        update.addInCondition(field, values);
        update.setRecord(record);
        return update.execute(connection);
    }

    public static <R extends TableRecord<R>, T> int update(Connection connection, Table<R> table, R record, TableField<R, T> field, Collection<T> values) throws SQLException {
        UpdateQuery<R> update = Create.updateQuery(table);
        update.addInCondition(field, values);
        update.setRecord(record);
        return update.execute(connection);
    }

    public static <R extends TableRecord<R>> int updateOne(Connection connection, Table<R> table, R record) throws SQLException {
        return filterUpdateOne(update(connection, table, record));
    }

    public static <R extends TableRecord<R>, T> int updateOne(Connection connection, Table<R> table, R record, TableField<R, T> field, T value) throws SQLException {
        return filterUpdateOne(update(connection, table, record, field, value));
    }

    public static <R extends TableRecord<R>> int delete(Connection connection, Table<R> table) throws SQLException {
        return Create.deleteQuery(table).execute(connection);
    }

    public static <R extends TableRecord<R>, T> int delete(Connection connection, Table<R> table, TableField<R, T> field, T value) throws SQLException {
        DeleteQuery<R> delete = Create.deleteQuery(table);
        delete.addCompareCondition(field, value);
        return delete.execute(connection);
    }

    public static <R extends TableRecord<R>, T> int delete(Connection connection, Table<R> table, TableField<R, T> field, T... values) throws SQLException {
        DeleteQuery<R> delete = Create.deleteQuery(table);
        delete.addInCondition(field, values);
        return delete.execute(connection);
    }

    public static <R extends TableRecord<R>, T> int delete(Connection connection, Table<R> table, TableField<R, T> field, Collection<T> values) throws SQLException {
        DeleteQuery<R> delete = Create.deleteQuery(table);
        delete.addInCondition(field, values);
        return delete.execute(connection);
    }

    public static <R extends TableRecord<R>> int deleteOne(Connection connection, Table<R> table) throws SQLException {
        return filterDeleteOne(Create.deleteQuery(table).execute(connection));
    }

    public static <R extends TableRecord<R>, T> int deleteOne(Connection connection, Table<R> table, TableField<R, T> field, T value) throws SQLException {
        DeleteQuery<R> delete = Create.deleteQuery(table);
        delete.addCompareCondition(field, value);
        return filterDeleteOne(delete.execute(connection));
    }

    private static int filterDeleteOne(int i) throws SQLException {
        return filterOne(i, "deleted");
    }

    private static int filterUpdateOne(int i) throws SQLException {
        return filterOne(i, "updated");
    }

    private static int filterOne(int i, String action) throws SQLException {
        if (i <= 1) {
            return i;
        }
        else {
            throw new SQLException("Too many rows " + action + " : " + i);
        }
    }

    private static <R extends Record> R filterOne(List<R> list) throws SQLException {
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

    private static <R extends Record> List<R> select0(Connection connection, SimpleSelect<R> select)
        throws SQLException {
        select.execute(connection);
        return select.getResult().getRecords();
    }

    private Manager() {}
}
