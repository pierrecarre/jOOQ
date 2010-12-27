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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.NamedQueryPart;
import org.jooq.Parameter;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.StoredFunction;

/**
 * @author Lukas Eder
 */
public class StoredFunctionImpl<T> extends AbstractStoredObject implements StoredFunction<T> {

    private static final long        serialVersionUID = -2938795269169609664L;
    private static final JooqLogger  log              = JooqLogger.getLogger(StoredFunctionImpl.class);

    private T                        result;
    private Field<T>                 function;
    private final Class<? extends T> type;

    public StoredFunctionImpl(SQLDialect dialect, String name, Schema schema, Class<? extends T> type) {
        this(Factory.getFactory(dialect), name, schema, type);
    }

    public StoredFunctionImpl(Configuration configuration, String name, Schema schema, Class<? extends T> type) {
        super(configuration, name, schema);

        this.type = type;
    }

    @Override
    public final T getReturnValue() {
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Field<T> asField() {
        if (function == null) {
            String name = getName();

            if (getSchema() != null) {
                name = getSchema().getQueryPart().toSQLReference(getConfiguration()) + "." + name;
            }

            function = new Function<T>(getConfiguration(), name, type, getInValueParts().toArray(new NamedQueryPart[0]));
        }

        return function;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Field<T> asField(String alias) {
        return asField().as(alias);
    }

    @Override
    protected final String toSQLPrefix() {
        return "select";
    }

    @Override
    protected final String toSQLPostFix() {
        return "from " + EmptyTable.getInstance(getDialect()).toSQLReference(getConfiguration());
    }

    @Override
    public final int execute(Connection connection) throws SQLException {
        PreparedStatement statement = null;
        ResultSet rs = null;

        if (log.isDebugEnabled()) {
            log.debug("Executing query : " + this);
        }

        try {
            statement = connection.prepareStatement(toSQLReference(getConfiguration()));
            bind(getConfiguration(), statement);

            rs = statement.executeQuery();
            if (rs.next()) {
                // TODO: Merge configuration into StoredObjects for better
                // support of UDTs, etc
                result = FieldTypeHelper.getFromResultSet(null, rs, asField(), 1);
            }

            return 0;
        }
        finally {
            SQLUtils.safeClose(rs, statement);
        }
    }

    @Override
    public final List<Parameter<?>> getParameters() {
        return getInParameters();
    }

    private final List<? extends NamedQueryPart> getInValueParts() {
        List<NamedQueryPart> list = new ArrayList<NamedQueryPart>();

        for (Object o : getInValues().values()) {
            list.add(create().constant(o));
        }

        return list;
    }
}
