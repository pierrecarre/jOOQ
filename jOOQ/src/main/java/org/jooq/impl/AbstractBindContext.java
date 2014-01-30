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

import static org.jooq.impl.Utils.visitAll;

import java.sql.SQLException;
import java.util.Collection;

import org.jooq.BindContext;
import org.jooq.Configuration;
import org.jooq.QueryPart;
import org.jooq.QueryPartInternal;

/**
 * A base class for {@link BindContext} implementations
 *
 * @author Lukas Eder
 */
abstract class AbstractBindContext extends AbstractContext<BindContext> implements BindContext {

    AbstractBindContext(Configuration configuration) {
        super(configuration);
    }

    // ------------------------------------------------------------------------
    // BindContext API
    // ------------------------------------------------------------------------

    
    @Deprecated
    public final BindContext bind(Collection<? extends QueryPart> parts) {
        return visitAll(this, parts);
    }

    
    @Deprecated
    public final BindContext bind(QueryPart[] parts) {
        return visitAll(this, parts);
    }

    
    @Deprecated
    public final BindContext bind(QueryPart part) {
        return visit(part);
    }

    
    protected void visit0(QueryPartInternal internal) {
        bindInternal(internal);
    }

    
    public final BindContext bindValues(Object... values) {

        // [#724] When values is null, this is probably due to API-misuse
        // The user probably meant new Object[] { null }
        if (values == null) {
            bindValues(new Object[] { null });
        }
        else {
            for (Object value : values) {
                Class<?> type = (value == null) ? Object.class : value.getClass();
                bindValue(value, type);
            }
        }

        return this;
    }

    
    public final BindContext bindValue(Object value, Class<?> type) {
        try {
            return bindValue0(value, type);
        }
        catch (SQLException e) {
            throw Utils.translate(null, e);
        }
    }

    // ------------------------------------------------------------------------
    // AbstractBindContext template methods
    // ------------------------------------------------------------------------

    /**
     * Subclasses may override this method to achieve different behaviour
     */
    protected void bindInternal(QueryPartInternal internal) {
        internal.bind(this);
    }

    /**
     * Subclasses may override this method to achieve different behaviour
     */
    @SuppressWarnings("unused")
    protected BindContext bindValue0(Object value, Class<?> type) throws SQLException {
        return this;
    }

    // ------------------------------------------------------------------------
    // Object API
    // ------------------------------------------------------------------------

    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toString(sb);
        return sb.toString();
    }
}
