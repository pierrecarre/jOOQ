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
package org.jooq.util.db2;

import java.util.ArrayList;
import java.util.List;

import org.jooq.util.AbstractProcedureDefinition;
import org.jooq.util.ColumnDefinition;
import org.jooq.util.Database;
import org.jooq.util.DefaultColumnDefinition;
import org.jooq.util.InOutDefinition;
import org.jooq.util.PackageDefinition;

/**
 * DB2 implementation of {@link AbstractProcedureDefinition}
 * 
 * @author Espen Stromsnes
 */
public class DB2ProcedureDefinition extends AbstractProcedureDefinition {

    private List<ColumnDefinition> inParameters = new ArrayList<ColumnDefinition>();
    private List<ColumnDefinition> outParameters = new ArrayList<ColumnDefinition>();
    private List<ColumnDefinition> allParameters = new ArrayList<ColumnDefinition>();


    public DB2ProcedureDefinition(Database database, PackageDefinition pkg, String name) {
        super(database, pkg, name, null, null);
    }
    
    @Override
    public List<ColumnDefinition> getInParameters() {
        return this.inParameters;
    }

    @Override
    public List<ColumnDefinition> getOutParameters() {
        return this.outParameters;
    }

    @Override
    public List<ColumnDefinition> getAllParameters() {
        return this.allParameters;
    }

    /**
     * Adds parameter to procedure
     * 
     * @param paramMode is this an IN, OUT or INOUT parameter?
     * @param paramName parameter name
     * @param position parameter ordinal position
     * @param paramType parameter data type
     * @param length parameter length
     * @param scale parameter scale
     */
    public void addParameter(String paramMode, String paramName, int position, String paramType, int length, short scale) {
        ColumnDefinition column = new DefaultColumnDefinition(getDatabase(), getName(), paramName, position, paramType, null);
        allParameters.add(column);

        switch (InOutDefinition.getFromString(paramMode)) {
        case IN:
            inParameters.add(column);
            break;
        case OUT:
            outParameters.add(column);
            break;
        case INOUT:
            inParameters.add(column);
            outParameters.add(column);
            break;
        }

    }

}