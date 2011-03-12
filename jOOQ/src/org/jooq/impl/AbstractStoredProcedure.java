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

import java.util.ArrayList;
import java.util.List;

import org.jooq.Configuration;
import org.jooq.Parameter;
import org.jooq.Schema;
import org.jooq.StoredProcedure;

/**
 * @author Lukas Eder
 */
public abstract class AbstractStoredProcedure extends AbstractStoredObject implements StoredProcedure {

    private static final long        serialVersionUID = 750586958119197957L;

    private final List<Parameter<?>> allParameters;
    private final List<Parameter<?>> outParameters;

    protected AbstractStoredProcedure(Configuration configuration, String name, Schema schema) {
        super(configuration, name, schema);

        this.allParameters = new ArrayList<Parameter<?>>();
        this.outParameters = new ArrayList<Parameter<?>>();
    }

    @Override
    public final List<Parameter<?>> getOutParameters() {
        return outParameters;
    }

    @Override
    public final List<Parameter<?>> getParameters() {
        return allParameters;
    }

    protected void addInOutParameter(Parameter<?> parameter) {
        super.addInParameter(parameter);
        outParameters.add(parameter);
        allParameters.add(parameter);
    }

    @Override
    protected void addInParameter(Parameter<?> parameter) {
        super.addInParameter(parameter);
        allParameters.add(parameter);
    }

    protected void addOutParameter(Parameter<?> parameter) {
        outParameters.add(parameter);
        allParameters.add(parameter);
    }

    protected abstract <T> T getValue(Parameter<T> parameter);
}
