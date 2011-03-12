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
package org.jooq.util.db2;

import java.util.ArrayList;
import java.util.List;

import org.jooq.util.AbstractFunctionDefinition;
import org.jooq.util.ColumnDefinition;
import org.jooq.util.DataTypeDefinition;
import org.jooq.util.Database;
import org.jooq.util.DefaultColumnDefinition;
import org.jooq.util.DefaultDataTypeDefinition;
import org.jooq.util.PackageDefinition;

/**
 * DB2 implementation of {@link AbstractFunctionDefinition}
 *
 * @author Espen Stromsnes
 */
public class DB2FunctionDefinition extends AbstractFunctionDefinition {

    private ColumnDefinition             returnValue;
    private final List<ColumnDefinition> inParameters;

    public DB2FunctionDefinition(Database database, PackageDefinition pkg, String name, String comment) {
        super(database, pkg, name, comment, null);

        inParameters = new ArrayList<ColumnDefinition>();
    }

    @Override
    public ColumnDefinition getReturnValue() {
        return this.returnValue;
    }

    /**
     * Sets the return value of the function
     *
     * @param dataType the return value data type
     */
    public void setReturnValue(String dataType) {
        DataTypeDefinition type = new DefaultDataTypeDefinition(getDatabase(), dataType);
        this.returnValue = new DefaultColumnDefinition(getDatabase(), getName(), "RETURN_VALUE", -1, type, null);
    }

    /**
     * Adds parameter to function
     *
     * @param paramName parameter name
     * @param position parameter ordinal position
     * @param paramType parameter data type
     * @param length parameter length
     * @param scale parameter scale
     */
    public void addParameter(String paramName, int position, String paramType) {
        DataTypeDefinition type = new DefaultDataTypeDefinition(getDatabase(), paramType);
        ColumnDefinition column = new DefaultColumnDefinition(getDatabase(), getName(), paramName, position, type, null);

        getInParameters().add(column);
    }

    @Override
    public final List<ColumnDefinition> getInParameters() {
        return inParameters;
    }
}
