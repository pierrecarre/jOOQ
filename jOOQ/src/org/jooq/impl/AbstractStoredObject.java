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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.Parameter;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.StoredObject;
import org.jooq.util.db2.DB2Util;

/**
 * @author Lukas Eder
 */
abstract class AbstractStoredObject extends AbstractNamedQueryPart implements StoredObject {

    private static final long               serialVersionUID = 5478305057107861491L;

    private final List<Parameter<?>>        inParameters;
    private final Map<Parameter<?>, Object> inValues;
    private final Schema                    schema;

    AbstractStoredObject(SQLDialect dialect, String name, Schema schema) {
        super(dialect, name);

        this.inParameters = new ArrayList<Parameter<?>>();
        this.inValues = new HashMap<Parameter<?>, Object>();
        this.schema = schema;
    }

    @Override
    public final int bind(PreparedStatement stmt, int initialIndex) throws SQLException {
        int result = initialIndex;

        for (Parameter<?> parameter : getParameters()) {
            JooqUtil.bind(stmt, result++, parameter, inValues.get(parameter));
        }

        return result;
    }

    @Override
    public final String toSQLReference(boolean inlineParameters) {
        StringBuilder sb = new StringBuilder();

        sb.append(toSQLPrefix());
        sb.append(" ");

        if (getSchema() != null) {
            sb.append(getSchema().getName());
            sb.append(".");
        }

        sb.append(getName());
        sb.append("(");

        String separator = "";
        for (Parameter<?> parameter : getParameters()) {
            sb.append(separator);

            if (inlineParameters && getInValues().containsKey(parameter)) {
                sb.append(FieldTypeHelper.toSQL(getDialect(), getInValues().get(parameter), inlineParameters, parameter));
            }
            else {
                switch (getDialect()) {
                    case DB2:
                        // TODO: This could be done in the StoredFunctionImpl subclass
                        // since it's only needed for functions (DB2 supports ? for stored
                        // procedures). But we do it here because stored procedures both 
                        // supports plain ? and casts.
                        sb.append(DB2Util.getTypeAsCastType(parameter));
                        break;
                    default:
                        sb.append("?");
                        break;
                }          
            }

            separator = ", ";
        }

        sb.append(")");
        String postFix = toSQLPostFix();

        if (postFix != null && postFix.length() > 0) {
            sb.append(" ");
            sb.append(postFix);
        }

        return sb.toString();

    }

    @Override
    public final Schema getSchema() {
        return schema;
    }

    protected abstract String toSQLPrefix();

    protected String toSQLPostFix() {
        return null;
    }

    protected final Map<Parameter<?>, Object> getInValues() {
        return inValues;
    }

    protected final <T> void setValue(Parameter<T> parameter, T value) {
        inValues.put(parameter, value);
    }

    public final List<Parameter<?>> getInParameters() {
        return inParameters;
    }

    protected void addInParameter(Parameter<?> parameter) {
        inParameters.add(parameter);
    }
}
