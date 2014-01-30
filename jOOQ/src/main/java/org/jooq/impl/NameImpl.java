/**
 * Copyright (c) 2009-2013, Data Geekery GmbH (http://www.datageekery.com)
 * All rights reserved.
 *
 * This work is dual-licensed
 * - under the Apache Software License 2.0 (the "ASL")
 * - under the jOOQ License and Maintenance Agreement (the "jOOQ License")
 * =============================================================================
 * You may choose which license applies to you:
 *
 * - If you're using this work with Open Source databases, you may choose
 *   either ASL or jOOQ License.
 * - If you're using this work with at least one commercial database, you must
 *   choose jOOQ License
 *
 * For more information, please visit http://www.jooq.org/licenses
 *
 * Apache Software License 2.0:
 * -----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * jOOQ License and Maintenance Agreement:
 * -----------------------------------------------------------------------------
 * Data Geekery grants the Customer the non-exclusive, timely limited and
 * non-transferable license to install and use the Software under the terms of
 * the jOOQ License and Maintenance Agreement.
 *
 * This library is distributed with a LIMITED WARRANTY. See the jOOQ License
 * and Maintenance Agreement for more details: http://www.jooq.org/licensing
 */
package org.jooq.impl;

import java.util.Arrays;

import org.jooq.BindContext;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.Name;
import org.jooq.RenderContext;
import org.jooq.tools.StringUtils;

/**
 * The default implementation for a SQL identifier
 *
 * @author Lukas Eder
 */
class NameImpl extends AbstractQueryPart implements Name {

    /**
     * Generated UID
     */
    private static final long serialVersionUID = 8562325639223483938L;

    private String[]          qualifiedName;

    NameImpl(String[] qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    
    public final void toSQL(RenderContext context) {
        String separator = "";

        for (String name : qualifiedName) {
            if (!StringUtils.isEmpty(name)) {
                context.sql(separator).literal(name);
                separator = ".";
            }
        }
    }

    
    public final void bind(BindContext context) {}

    
    public final Clause[] clauses(Context<?> ctx) {
        return null;
    }

    
    public final String[] getName() {
        return qualifiedName;
    }

    // ------------------------------------------------------------------------
    // XXX: Object API
    // ------------------------------------------------------------------------

    
    public int hashCode() {
        return Arrays.hashCode(getName());
    }

    
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }

        // [#1626] NameImpl equality can be decided without executing the
        // rather expensive implementation of AbstractQueryPart.equals()
        if (that instanceof NameImpl) {
            return Arrays.equals(getName(), (((NameImpl) that).getName()));
        }

        return super.equals(that);
    }
}
