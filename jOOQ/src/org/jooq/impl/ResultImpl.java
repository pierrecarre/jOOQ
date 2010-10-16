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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jooq.Field;
import org.jooq.FieldList;
import org.jooq.FieldProvider;
import org.jooq.Record;
import org.jooq.Result;

/**
 * @author Lukas Eder
 */
class ResultImpl<R extends Record> implements Result<R> {

    /**
     * Generated UID
     */
    private static final long   serialVersionUID = 6416154375799578362L;

    private final FieldProvider fields;
    private final List<R>       records;

    ResultImpl(FieldProvider fields) {
        this.fields = fields;
        this.records = new ArrayList<R>();
    }

    @Override
    public FieldList getFields() {
        return fields.getFields();
    }

    @Override
    public <T> Field<T> getField(Field<T> field) {
        return fields.getField(field);
    }

    @Override
    public Field<?> getField(String name) {
        return fields.getField(name);
    }

    @Override
    public int getNumberOfRecords() {
        return records.size();
    }

    @Override
    public List<R> getRecords() {
        return Collections.unmodifiableList(records);
    }

    @Override
    public R getRecord(int index) throws IndexOutOfBoundsException {
        return records.get(index);
    }

    @Override
    public <T> T getValue(int index, Field<T> field) throws IndexOutOfBoundsException {
        return getRecord(index).getValue(field);
    }

    @Override
    public <T> T getValue(int index, Field<T> field, T defaultValue) throws IndexOutOfBoundsException {
        return getRecord(index).getValue(field, defaultValue);
    }

    @Override
    public Iterator<R> iterator() {
        return records.iterator();
    }

    void addRecord(R record) {
        records.add(record);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getClass().getSimpleName() + "\n");
        sb.append("Records:\n");

        int i = 0;
        for (; i < 10 && i < getNumberOfRecords(); i++) {
            sb.append(getRecord(i));
            sb.append("\n");
        }

        if (i == 10) {
            sb.append("[...]");
        }

        return sb.toString();
    }
}
