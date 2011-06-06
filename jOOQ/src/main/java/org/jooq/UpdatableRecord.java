/**
 * Copyright (c) 2009-2011, Lukas Eder, lukas.eder@gmail.com
 * All rights reserved.
 *
 * This software is licensed to you under the Apache License, Version 2.0
 * (the "License"); You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
 * . Neither the name "jOOQ" nor the names of its contributors may be
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

import java.sql.SQLException;

/**
 * A common interface for records that can be stored back to the database again.
 * <p>
 * <code>UpdatableRecords</code> are {@link Attachable}, which means that they
 * hold an underlying {@link Configuration} that they can be detached from. They
 * can also be instanciated without any underlying {@link Configuration}, in
 * case of which they have to be attached first, in order to be refreshed,
 * stored, or deleted.
 *
 * @param <R> The record type
 * @author Lukas Eder
 */
public interface UpdatableRecord<R extends Record> extends Updatable<R>, TableRecord<R> {

    /**
     * The table from which this record was read
     */
    @Override
    UpdatableTable<R> getTable();

    /**
     * Store this record back to the database.
     * <p>
     * If the primary key was loaded, this results in an update statement.
     * Otherwise, an insert statement is executed.
     *
     * @return <code>1</code> if the record was stored to the database.
     *         <code>0</code> if storing was not necessary.
     * @throws SQLException
     */
    int store() throws SQLException;

    /**
     * Deletes this record from the database.
     *
     * @return <code>1</code> if the record was deleted from the database.
     *         <code>0</code> if deletion was not necessary.
     * @throws SQLException
     */
    int delete() throws SQLException;

    /**
     * Refresh this record from the database.
     *
     * @throws SQLException - If there is an underlying {@link SQLException} or
     *             if the record does not exist anymore in the database.
     */
    void refresh() throws SQLException;

    /**
     * {@inheritDoc}
     */
    @Override
    /*
     * This is overridden again, in order to resolve the deprecation clash
     * between TableRecord and Attachable.
     */
    Configuration getConfiguration();
}
