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
package org.jooq.impl;

import java.math.BigInteger;

import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.SQLDialectNotSupportedException;
import org.jooq.Schema;
import org.jooq.Sequence;

/**
 * @author Lukas Eder
 */
public class SequenceImpl implements Sequence {

    private final Configuration configuration;
    private final String name;
    private final Schema schema;

    public SequenceImpl(SQLDialect dialect, String name, Schema schema) {
        this(Factory.getStaticFactory(dialect), name, schema);
    }

    public SequenceImpl(Configuration configuration, String name, Schema schema) {
        this.configuration = configuration;
        this.name = name;
        this.schema = schema;
    }

    @Override
    public Field<BigInteger> currval() {
        return getSequence("currval");
    }

    @Override
    public Field<BigInteger> nextval() {
        return getSequence("nextval");
    }

    private Field<BigInteger> getSequence(String sequence) {
        switch (configuration.getDialect()) {
            case DB2:    // No break
            case INGRES: // No break
            case ORACLE: // No break
            case SYBASE: {
                String field = getName() + "." + sequence;
                return Factory.getNewFactory(configuration).plainSQLField(field, BigInteger.class);
            }
            case H2: // No break
            case POSTGRES: {
                String field = sequence + "('" + getName() + "')";
                return Factory.getNewFactory(configuration).plainSQLField(field, BigInteger.class);
            }

            case DERBY: // No break
            case HSQLDB: {
                if ("nextval".equals(sequence)) {
                    String field = "next value for " + getName();
                    return Factory.getNewFactory(configuration).plainSQLField(field, BigInteger.class);
                }
                else {
                    throw new SQLDialectNotSupportedException("The sequence's current value functionality is not supported for the " + configuration.getDialect() + " dialect.");
                }
            }
        }

        throw new SQLDialectNotSupportedException("Sequences not supported in dialect " + configuration.getDialect());
    }

    private String getName() {
        if (schema == null) {
            return schema.getName() + "." + name;
        }

        return name;
    }
}
