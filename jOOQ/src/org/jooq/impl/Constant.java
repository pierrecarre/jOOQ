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

import org.jooq.EnumType;
import org.jooq.MasterDataType;
import org.jooq.SQLDialect;

/**
 * @author Lukas Eder
 */
class Constant<T> extends AbstractField<T> {

    private static final long serialVersionUID = 6807729087019209084L;
    private final T           value;

    @SuppressWarnings("unchecked")
    Constant(SQLDialect dialect, T value) {
        super(dialect, value.toString(), (Class<? extends T>) value.getClass());

        this.value = value;
    }

    @Override
    public final String toSQLReference(boolean inlineParameters) {
        // Generated enums should not be cast...
        // The exception's exception
        if (!(value instanceof EnumType) && !(value instanceof MasterDataType)) {
            switch (getDialect()) {

                // HSQLDB cannot detect the type of a bound constant. It must be cast
                case HSQLDB:
                    return "cast(? as " + FieldTypeHelper.getDialectSQLType(getDialect(), this) + ")";

                    // DB2 cannot detect the type of a bound constant. If must be cast.
                case DB2:
                    String type = FieldTypeHelper.getDialectSQLType(getDialect(), this);

                    // If the type is VARCHAR we also have to specify the size (using the max
                    // size 32672 to support all VARCHAR sizes)
                    if ("VARCHAR".equals(type)) {
                        type = "VARCHAR(32672)";
                    }
                    return "cast(? as " + type + ")";
            }
        }

        // Most RDBMS can handle constants as typeless literals
        return FieldTypeHelper.toSQL(getDialect(), value, inlineParameters, this);
    }

    @Override
    public int bind(PreparedStatement stmt, int initialIndex) throws SQLException {
        JooqUtil.bind(stmt, initialIndex, this, value);
        return initialIndex + 1;
    }
}
