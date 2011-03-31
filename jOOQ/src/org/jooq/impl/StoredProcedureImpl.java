/**
 * Copyright (c) 2009-2011, Lukas Eder, lukas.eder@gmail.com
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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.jooq.ArrayRecord;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Parameter;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.UDTRecord;

/**
 * @author Lukas Eder
 */
public class StoredProcedureImpl extends AbstractStoredProcedure {

    private static final long                serialVersionUID = -8046199737354507547L;
    private static final JooqLogger          log              = JooqLogger.getLogger(StoredProcedureImpl.class);

    private Map<Parameter<?>, Object>        results;
    private final Map<Parameter<?>, Integer> parameterIndexes;

    public StoredProcedureImpl(SQLDialect dialect, String name, Schema schema) {
        this(Factory.getFactory(dialect), name, schema);
    }

    public StoredProcedureImpl(Configuration configuration, String name, Schema schema) {
        super(configuration, name, schema);

        this.parameterIndexes = new HashMap<Parameter<?>, Integer>();
    }

    @Override
    public final int execute(Connection connection) throws SQLException {
        CallableStatement statement = null;

        if (log.isDebugEnabled()) {
            log.debug("Executing query", this);
        }

        try {
            results = new HashMap<Parameter<?>, Object>();

            statement = connection.prepareCall(toSQLReference(getConfiguration()));
            bind(getConfiguration(), statement);

            // Register all out / inout parameters according to their position
            // Note that some RDBMS do not support binding by name very well
            for (Parameter<?> parameter : getParameters()) {
                if (getOutParameters().contains(parameter)) {
                    int index = parameterIndexes.get(parameter);
                    int sqlType = parameter.getDataType().getSQLType();

                    switch (getDialect()) {

                        // For some user defined types Oracle needs to bind
                        // also the type name
                        case ORACLE: {
                            if (sqlType == Types.STRUCT) {
                                @SuppressWarnings("unchecked")
                                UDTRecord<?> record = JooqUtil.newRecord((Class<? extends UDTRecord<?>>) parameter
                                    .getType());
                                statement.registerOutParameter(index, Types.STRUCT, record.getSQLTypeName());
                            }

                            else if (sqlType == Types.ARRAY) {
                                @SuppressWarnings("unchecked")
                                ArrayRecord<?> record = JooqUtil.newArrayRecord(
                                    (Class<? extends ArrayRecord<?>>) parameter.getType(), getConfiguration());
                                statement.registerOutParameter(index, Types.ARRAY, record.getName());
                            }

                            // The default behaviour is not to register a type
                            // mapping
                            else {
                                statement.registerOutParameter(index, sqlType);
                            }

                            break;
                        }

                        default: {
                            statement.registerOutParameter(index, sqlType);
                            break;
                        }
                    }
                }
            }

            statement.execute();

            // Fetch results for all out parameters
            for (Parameter<?> parameter : getParameters()) {
                int index = parameterIndexes.get(parameter);

                if (getOutParameters().contains(parameter)) {
                    results.put(parameter, FieldTypeHelper.getFromStatement(
                        getConfiguration(), statement, parameter.getType(), index));
                }
            }

            return 0;
        }
        finally {
            SQLUtils.safeClose(statement);
        }
    }

    @Override
    public final int bind(Configuration configuration, PreparedStatement stmt, int initialIndex) throws SQLException {
        int index = initialIndex;

        for (Parameter<?> parameter : getParameters()) {
            parameterIndexes.put(parameter, index);

            if (getInValues().get(parameter) != null) {
                index = getInValues().get(parameter).getQueryPart().bind(configuration, stmt, index);
            }
        }

        return index;
    }

    @Override
    public final String toSQLReference(Configuration configuration, boolean inlineParameters) {
        StringBuilder sb = new StringBuilder();

        sb.append("{ call ");

        if (getSchema() != null) {
            sb.append(getSchema().getQueryPart().toSQLReference(configuration, inlineParameters));
            sb.append(".");
        }

        sb.append(getName());
        sb.append("(");

        String separator = "";
        for (Parameter<?> parameter : getParameters()) {
            sb.append(separator);

            // IN and IN OUT parameters are rendered normally
            if (getInValues().containsKey(parameter)) {
                Field<?> value = getInValues().get(parameter);

                // Disambiguate overloaded procedure signatures
                if (SQLDialect.POSTGRES == getDialect() && isOverloaded()) {
                    value = value.cast(parameter.getType());
                }

                sb.append(value.getQueryPart().toSQLReference(configuration, inlineParameters));
            }

            // OUT parameters are always written as a '?' bind variable
            else {
                sb.append("?");
            }

            separator = ", ";
        }

        sb.append(") }");
        return sb.toString();

    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T getValue(Parameter<T> parameter) {
        return (T) results.get(parameter);
    }
}
