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
package org.jooq.util;

import java.sql.SQLException;
import java.util.List;

public abstract class AbstractPackageDefinition extends AbstractDefinition implements PackageDefinition {

    private List<ProcedureDefinition> procedures;
    private List<FunctionDefinition>  functions;

    public AbstractPackageDefinition(Database database, String name, String comment) {
        super(database, name, comment);
    }

    @Override
    public String getSubPackage() {
        return "packages." + getNameLC();
    }

    @Override
    public final List<ProcedureDefinition> getProcedures() throws SQLException {
        if (procedures == null) {
            procedures = getProcedures0();
        }
        
        return procedures;
    }

    @Override
    public final List<FunctionDefinition> getFunctions() throws SQLException {
        if (functions == null) {
            functions = getFunctions0();
        }
        
        return functions;
    }

    protected abstract List<ProcedureDefinition> getProcedures0() throws SQLException;
    protected abstract List<FunctionDefinition> getFunctions0() throws SQLException;
}
