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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Parameter;
import org.jooq.Schema;
import org.jooq.StoredObject;

/**
 * @author Lukas Eder
 */
abstract class AbstractStoredObject extends AbstractSchemaProviderQueryPart implements StoredObject {

    private static final long                 serialVersionUID = 5478305057107861491L;

    private final List<Parameter<?>>          inParameters;
    private final Map<Parameter<?>, Field<?>> inValues;
    private boolean                           overloaded;

    AbstractStoredObject(Configuration configuration, String name, Schema schema) {
        super(configuration, name, schema);

        this.inParameters = new ArrayList<Parameter<?>>();
        this.inValues = new HashMap<Parameter<?>, Field<?>>();
    }

    protected final Map<Parameter<?>, Field<?>> getInValues() {
        return inValues;
    }

    protected final <T> void setValue(Parameter<T> parameter, T value) {
        setValue(parameter, constant(value));
    }

    protected final <T> void setValue(Parameter<T> parameter, Field<T> value) {
        // Be sure null is correctly represented as a null field
        if (value == null) {
            setValue(parameter, this.<T> constant(null));
        }

        // Add the field to the in-values
        else {
            inValues.put(parameter, value);
        }
    }

    public final List<Parameter<?>> getInParameters() {
        return Collections.unmodifiableList(inParameters);
    }

    protected void addInParameter(Parameter<?> parameter) {
        inParameters.add(parameter);

        // IN parameters are initialised with null by default
        inValues.put(parameter, constant(null));
    }

    protected final void setOverloaded(boolean overloaded) {
        this.overloaded = overloaded;
    }

    protected final boolean isOverloaded() {
        return overloaded;
    }
}
