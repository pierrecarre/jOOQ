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

import static org.jooq.Clause.SELECT_EXCEPT;
import static org.jooq.Clause.SELECT_INTERSECT;
import static org.jooq.Clause.SELECT_UNION;
import static org.jooq.Clause.SELECT_UNION_ALL;
import static org.jooq.impl.Utils.visitAll;

import java.util.ArrayList;
import java.util.List;

import org.jooq.BindContext;
import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RenderContext;
import org.jooq.Select;

/**
 * A union is a <code>SELECT</code> statement that combines several sub-selects
 * with a <code>UNION</code> or a similar operator.
 *
 * @author Lukas Eder
 */
class Union<R extends Record> extends AbstractSelect<R> {

    private static final long               serialVersionUID = 7491446471677986172L;

    private final List<Select<? extends R>> queries;
    private final CombineOperator           operator;
    private final Clause[]                  clauses;

    Union(Configuration configuration, Select<R> query1, Select<? extends R> query2, CombineOperator operator) {
        super(configuration);

        this.queries = new ArrayList<Select<? extends R>>();
        this.queries.add(query1);
        this.queries.add(query2);
        this.operator = operator;

        switch (operator) {
            case EXCEPT:    this.clauses = new Clause[] { SELECT_EXCEPT }    ; break;
            case INTERSECT: this.clauses = new Clause[] { SELECT_INTERSECT } ; break;
            case UNION:     this.clauses = new Clause[] { SELECT_UNION }     ; break;
            case UNION_ALL: this.clauses = new Clause[] { SELECT_UNION_ALL } ; break;
            default:        throw new IllegalArgumentException("Operator not supported : " + operator);
        }
    }

    
    public final Class<? extends R> getRecordType() {
        return queries.get(0).getRecordType();
    }

    
    public final List<Field<?>> getSelect() {
        return queries.get(0).getSelect();
    }

    
    public final void toSQL(RenderContext context) {
        for (int i = 0; i < queries.size(); i++) {
            if (i != 0) {
                context.formatSeparator()
                       .keyword(operator.toSQL(context.configuration().dialect()))
                       .formatSeparator();
            }

            wrappingParenthesis(context, "(");
            context.visit(queries.get(i));
            wrappingParenthesis(context, ")");
        }
    }

    private final void wrappingParenthesis(RenderContext context, String parenthesis) {
        switch (context.configuration().dialect()) {
            // Sybase ASE, Derby, Firebird and SQLite have some syntax issues with unions.
            // Check out https://issues.apache.org/jira/browse/DERBY-2374
            /* [pro] xx
            xxxx xxxx
            xx [/pro] */
            case DERBY:
            case FIREBIRD:
            case SQLITE:

            // [#288] MySQL has a very special way of dealing with UNION's
            // So include it as well
            case MARIADB:
            case MYSQL:
                return;
        }

        if (")".equals(parenthesis)) {
            context.formatIndentEnd()
                   .formatNewLine();
        }

        context.sql(parenthesis);

        if ("(".equals(parenthesis)) {
            context.formatIndentStart()
                   .formatNewLine();
        }
    }

    
    public final void bind(BindContext context) {
        visitAll(context, queries);
    }

    
    public final Clause[] clauses(Context<?> ctx) {
        return clauses;
    }

    
    final boolean isForUpdate() {
        return false;
    }
}
