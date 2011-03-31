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
package org.jooq.util.hsqldb;

import static org.jooq.util.hsqldb.information_schema.tables.Parameters.PARAMETERS;

import java.sql.SQLException;

import org.jooq.Record;
import org.jooq.Result;
import org.jooq.util.AbstractFunctionDefinition;
import org.jooq.util.ColumnDefinition;
import org.jooq.util.DataTypeDefinition;
import org.jooq.util.Database;
import org.jooq.util.DefaultColumnDefinition;
import org.jooq.util.DefaultDataTypeDefinition;
import org.jooq.util.InOutDefinition;
import org.jooq.util.hsqldb.information_schema.tables.Parameters;

/**
 * HSQLDB implementation of {@link AbstractFunctionDefinition}
 *
 * @author Espen Stromsnes
 */
public class HSQLDBFunctionDefinition extends AbstractFunctionDefinition {

    /**
     * internal name for the function used by HSQLDB
     */
    private final String specificName;

    public HSQLDBFunctionDefinition(Database database, String name, String specificName, String dataType) {
        super(database, null, name, null, null);

        DataTypeDefinition type = new DefaultDataTypeDefinition(getDatabase(), dataType);

        this.returnValue = new DefaultColumnDefinition(getDatabase(), getName(), "RETURN_VALUE", -1, type, null);
        this.specificName = specificName;
    }

    @Override
    protected void init0() throws SQLException {
        Result<Record> result = create().select(
                Parameters.PARAMETER_MODE,
                Parameters.PARAMETER_NAME,
                Parameters.DATA_TYPE,
                Parameters.ORDINAL_POSITION)
            .from(PARAMETERS)
            .where(Parameters.SPECIFIC_SCHEMA.equal(getSchemaName()))
            .and(Parameters.SPECIFIC_NAME.equal(this.specificName))
            .orderBy(Parameters.ORDINAL_POSITION.ascending()).fetch();

        for (Record record : result) {
            String inOut = record.getValue(Parameters.PARAMETER_MODE);

            DataTypeDefinition type = new DefaultDataTypeDefinition(getDatabase(),
                record.getValue(Parameters.DATA_TYPE));

            ColumnDefinition column = new DefaultColumnDefinition(
                getDatabase(),
                getName(),
                record.getValue(Parameters.PARAMETER_NAME),
                record.getValueAsInteger(Parameters.ORDINAL_POSITION),
                type, null);

            if (InOutDefinition.getFromString(inOut) == InOutDefinition.IN) {
                inParameters.add(column);
            } else {
                // This should not happen. Return value is handled when
                // reading functions that exist (see HSQLDBDatabase.getFunctions0)

                // TODO: correctly separate functions from procedures, see also
                // https://sourceforge.net/apps/trac/jooq/ticket/193
                // https://sourceforge.net/apps/trac/jooq/ticket/195
                returnValue = column;
            }
        }
    }
}
