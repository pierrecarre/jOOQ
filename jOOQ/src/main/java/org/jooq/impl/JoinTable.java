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

import static java.util.Arrays.asList;
import static org.jooq.Clause.TABLE;
import static org.jooq.Clause.TABLE_JOIN;
import static org.jooq.Clause.TABLE_JOIN_CROSS;
import static org.jooq.Clause.TABLE_JOIN_INNER;
import static org.jooq.Clause.TABLE_JOIN_NATURAL;
import static org.jooq.Clause.TABLE_JOIN_NATURAL_OUTER_LEFT;
import static org.jooq.Clause.TABLE_JOIN_NATURAL_OUTER_RIGHT;
import static org.jooq.Clause.TABLE_JOIN_ON;
import static org.jooq.Clause.TABLE_JOIN_OUTER_FULL;
import static org.jooq.Clause.TABLE_JOIN_OUTER_LEFT;
import static org.jooq.Clause.TABLE_JOIN_OUTER_RIGHT;
import static org.jooq.Clause.TABLE_JOIN_PARTITION_BY;
import static org.jooq.Clause.TABLE_JOIN_USING;
import static org.jooq.JoinType.CROSS_JOIN;
import static org.jooq.JoinType.JOIN;
import static org.jooq.JoinType.LEFT_OUTER_JOIN;
import static org.jooq.JoinType.NATURAL_JOIN;
import static org.jooq.JoinType.NATURAL_LEFT_OUTER_JOIN;
import static org.jooq.JoinType.NATURAL_RIGHT_OUTER_JOIN;
import static org.jooq.JoinType.RIGHT_OUTER_JOIN;
// ...
import static org.jooq.SQLDialect.CUBRID;
// ...
import static org.jooq.SQLDialect.H2;
// ...
// ...
// ...
import static org.jooq.impl.DSL.condition;
import static org.jooq.impl.DSL.exists;
import static org.jooq.impl.DSL.notExists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jooq.BindContext;
import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.JoinType;
import org.jooq.Operator;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.RenderContext;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableLike;
import org.jooq.TableOnConditionStep;
import org.jooq.TableOnStep;
import org.jooq.TableOptionalOnStep;
import org.jooq.exception.DataAccessException;

/**
 * A table consisting of two joined tables and possibly a join condition
 *
 * @author Lukas Eder
 */
class JoinTable extends AbstractTable<Record> implements TableOptionalOnStep, TableOnConditionStep {

    /**
     * Generated UID
     */
    private static final long             serialVersionUID = 8377996833996498178L;
    private static final Clause[]         CLAUSES          = { TABLE, TABLE_JOIN };

    private final Table<?>                lhs;
    private final Table<?>                rhs;
    private final QueryPartList<Field<?>> rhsPartitionBy;

    private final JoinType                type;
    private final ConditionProviderImpl   condition;
    private final QueryPartList<Field<?>> using;

    JoinTable(TableLike<?> lhs, TableLike<?> rhs, JoinType type) {
        super("join");

        this.lhs = lhs.asTable();
        this.rhs = rhs.asTable();
        this.rhsPartitionBy = new QueryPartList<Field<?>>();
        this.type = type;

        this.condition = new ConditionProviderImpl();
        this.using = new QueryPartList<Field<?>>();
    }

    // ------------------------------------------------------------------------
    // Table API
    // ------------------------------------------------------------------------

    @SuppressWarnings({ "unchecked", "rawtypes" })
    
    public final List<ForeignKey<Record, ?>> getReferences() {
        List<ForeignKey<?, ?>> result = new ArrayList<ForeignKey<?, ?>>();

        result.addAll(lhs.getReferences());
        result.addAll(rhs.getReferences());

        return (List) result;
    }

    
    public final void toSQL(RenderContext context) {
        JoinType translatedType = translateType(context);
        Clause translatedClause = translateClause(translatedType);

        context.visit(lhs)
               .formatIndentStart()
               .formatSeparator()
               .start(translatedClause)
               .keyword(translatedType.toSQL())
               .sql(" ");

        // [#671] Some databases formally require nested JOINS to be
        // wrapped in parentheses (e.g. MySQL)
        if (rhs instanceof JoinTable) {
            context.sql("(")
                   .formatIndentStart()
                   .formatNewLine();
        }

        context.visit(rhs);

        if (rhs instanceof JoinTable) {
            context.formatIndentEnd()
                   .formatNewLine()
                   .sql(")");
        }

        // [#1645] The Oracle PARTITION BY clause can be put to the right of an
        // OUTER JOINed table
        if (!rhsPartitionBy.isEmpty()) {
            context.formatSeparator()
                   .start(TABLE_JOIN_PARTITION_BY)
                   .keyword("partition by")
                   .sql(" (")
                   .visit(rhsPartitionBy)
                   .sql(")")
                   .end(TABLE_JOIN_PARTITION_BY);
        }

        // CROSS JOIN and NATURAL JOIN do not have any condition clauses
        if (!asList(CROSS_JOIN,
                    NATURAL_JOIN,
                    NATURAL_LEFT_OUTER_JOIN,
                    NATURAL_RIGHT_OUTER_JOIN).contains(translatedType)) {
            toSQLJoinCondition(context);
        }

        context.end(translatedClause)
               .formatIndentEnd();
    }

    /**
     * Translate the join type into a join clause
     */
    final Clause translateClause(JoinType translatedType) {
        switch (translatedType) {
            case JOIN:                     return TABLE_JOIN_INNER;
            case CROSS_JOIN:               return TABLE_JOIN_CROSS;
            case NATURAL_JOIN:             return TABLE_JOIN_NATURAL;
            case LEFT_OUTER_JOIN:          return TABLE_JOIN_OUTER_LEFT;
            case RIGHT_OUTER_JOIN:         return TABLE_JOIN_OUTER_RIGHT;
            case FULL_OUTER_JOIN:          return TABLE_JOIN_OUTER_FULL;
            case NATURAL_LEFT_OUTER_JOIN:  return TABLE_JOIN_NATURAL_OUTER_LEFT;
            case NATURAL_RIGHT_OUTER_JOIN: return TABLE_JOIN_NATURAL_OUTER_RIGHT;
            default: throw new IllegalArgumentException("Bad join type: " + translatedType);
        }
    }

    /**
     * Translate the join type for SQL rendering
     */
    final JoinType translateType(RenderContext context) {
        if (simulateCrossJoin(context)) {
            return JOIN;
        }
        else if (simulateNaturalJoin(context)) {
            return JOIN;
        }
        else if (simulateNaturalLeftOuterJoin(context)) {
            return LEFT_OUTER_JOIN;
        }
        else if (simulateNaturalRightOuterJoin(context)) {
            return RIGHT_OUTER_JOIN;
        }
        else {
            return type;
        }
    }

    private final boolean simulateCrossJoin(RenderContext context) {
        return false/* [pro] xx xx xxxx xx xxxxxxxxxx xx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xx xxxxx [/pro] */;
    }

    private final boolean simulateNaturalJoin(RenderContext context) {
        return type == NATURAL_JOIN && asList(CUBRID).contains(context.configuration().dialect().family());
    }

    private final boolean simulateNaturalLeftOuterJoin(RenderContext context) {
        return type == NATURAL_LEFT_OUTER_JOIN && asList(CUBRID, H2).contains(context.configuration().dialect().family());
    }

    private final boolean simulateNaturalRightOuterJoin(RenderContext context) {
        return type == NATURAL_RIGHT_OUTER_JOIN && asList(CUBRID, H2).contains(context.configuration().dialect().family());
    }

    private final void toSQLJoinCondition(RenderContext context) {
        if (!using.isEmpty()) {

            // [#582] Some dialects don't explicitly support a JOIN .. USING
            // syntax. This can be simulated with JOIN .. ON
            if (asList(CUBRID, H2).contains(context.configuration().dialect().family())) {
                boolean first = true;
                for (Field<?> field : using) {
                    context.formatSeparator();

                    if (first) {
                        first = false;

                        context.start(TABLE_JOIN_ON)
                               .keyword("on");
                    }
                    else {
                        context.keyword("and");
                    }

                    context.sql(" ")
                           .visit(lhs.field(field))
                           .sql(" = ")
                           .visit(rhs.field(field));
                }

                context.end(TABLE_JOIN_ON);
            }

            // Native supporters of JOIN .. USING
            else {
                context.formatSeparator()
                       .start(TABLE_JOIN_USING)
                       .keyword("using")
                       .sql("( ");
                Utils.fieldNames(context, using);
                context.sql(")")
                       .end(TABLE_JOIN_USING);
            }
        }

        // [#577] If any NATURAL JOIN syntax needs to be simulated, find out
        // common fields in lhs and rhs of the JOIN clause
        else if (simulateNaturalJoin(context) ||
                 simulateNaturalLeftOuterJoin(context) ||
                 simulateNaturalRightOuterJoin(context)) {

            boolean first = true;
            for (Field<?> field : lhs.fields()) {
                Field<?> other = rhs.field(field);

                if (other != null) {
                    context.formatSeparator();

                    if (first) {
                        first = false;

                        context.start(TABLE_JOIN_ON)
                               .keyword("on");
                    }
                    else {
                        context.keyword("and");
                    }

                    context.sql(" ")
                           .visit(field)
                           .sql(" = ")
                           .visit(other);
                }
            }

            context.end(TABLE_JOIN_ON);
        }

        // Regular JOIN condition
        else {
            context.formatSeparator()
                   .start(TABLE_JOIN_ON)
                   .keyword("on")
                   .sql(" ")
                   .visit(condition)
                   .end(TABLE_JOIN_ON);
        }
    }

    
    public final void bind(BindContext context) throws DataAccessException {
        context.visit(lhs).visit(rhs).visit(rhsPartitionBy);

        if (!using.isEmpty()) {
            context.visit(using);
        }
        else {
            context.visit(condition);
        }
    }

    
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    
    public final Table<Record> as(String alias) {
        return new TableAlias<Record>(this, alias, true);
    }

    
    public final Table<Record> as(String alias, String... fieldAliases) {
        return new TableAlias<Record>(this, alias, fieldAliases, true);
    }

    
    public final Class<? extends Record> getRecordType() {
        return RecordImpl.class;
    }

    
    final Fields<Record> fields0() {
        Field<?>[] l = lhs.asTable().fields();
        Field<?>[] r = rhs.asTable().fields();
        Field<?>[] all = new Field[l.length + r.length];

        System.arraycopy(l, 0, all, 0, l.length);
        System.arraycopy(r, 0, all, l.length, r.length);

        return new Fields<Record>(all);
    }

    
    public final boolean declaresTables() {
        return true;
    }

    // ------------------------------------------------------------------------
    // Join API
    // ------------------------------------------------------------------------

    /* [pro] xx
    xxxxxxxxx
    xxxxxx xxxxx xxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxx xxxxxxx x
        xxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    x

    xxxxxxxxx
    xxxxxx xxxxx xxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxx xxxxxxx xxxxxxxxx xxxxxxx x
        xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
        xxxxxx xxxxx
    x

    xx [/pro] */
    
    public final JoinTable on(Condition... conditions) {
        condition.addConditions(conditions);
        return this;
    }

    
    public final JoinTable on(Field<Boolean> c) {
        return on(condition(c));
    }

    
    public final JoinTable on(String sql) {
        and(sql);
        return this;
    }

    
    public final JoinTable on(String sql, Object... bindings) {
        and(sql, bindings);
        return this;
    }

    
    public final JoinTable on(String sql, QueryPart... parts) {
        and(sql, parts);
        return this;
    }

    
    public final JoinTable onKey() throws DataAccessException {
        List<?> leftToRight = lhs.getReferencesTo(rhs);
        List<?> rightToLeft = rhs.getReferencesTo(lhs);

        if (leftToRight.size() == 1 && rightToLeft.size() == 0) {
            return onKey((ForeignKey<?, ?>) leftToRight.get(0));
        }
        else if (rightToLeft.size() == 1 && leftToRight.size() == 0) {
            return onKey((ForeignKey<?, ?>) rightToLeft.get(0));
        }

        throw onKeyException();
    }

    
    public final JoinTable onKey(TableField<?, ?>... keyFields) throws DataAccessException {
        if (keyFields != null && keyFields.length > 0) {
            if (keyFields[0].getTable().equals(lhs)) {
                for (ForeignKey<?, ?> key : lhs.getReferences()) {
                    if (key.getFields().containsAll(asList(keyFields))) {
                        return onKey(key);
                    }
                }
            }
            else if (keyFields[0].getTable().equals(rhs)) {
                for (ForeignKey<?, ?> key : rhs.getReferences()) {
                    if (key.getFields().containsAll(asList(keyFields))) {
                        return onKey(key);
                    }
                }
            }
        }

        throw onKeyException();
    }

    @SuppressWarnings("unchecked")
    
    public final JoinTable onKey(ForeignKey<?, ?> key) {
        JoinTable result = this;

        TableField<?, ?>[] references = key.getFieldsArray();
        TableField<?, ?>[] referenced = key.getKey().getFieldsArray();

        for (int i = 0; i < references.length; i++) {
            result.and(((Field<Void>) references[i]).equal((Field<Void>) referenced[i]));
        }

        return result;
    }

    private final DataAccessException onKeyException() {
        return new DataAccessException("Key ambiguous between tables " + lhs + " and " + rhs);
    }

    
    public final JoinTable using(Field<?>... fields) {
        return using(asList(fields));
    }

    
    public final JoinTable using(Collection<? extends Field<?>> fields) {
        using.addAll(fields);
        return this;
    }

    
    public final JoinTable and(Condition c) {
        condition.addConditions(c);
        return this;
    }

    
    public final JoinTable and(Field<Boolean> c) {
        return and(condition(c));
    }

    
    public final JoinTable and(String sql) {
        return and(condition(sql));
    }

    
    public final JoinTable and(String sql, Object... bindings) {
        return and(condition(sql, bindings));
    }

    
    public final JoinTable and(String sql, QueryPart... parts) {
        return and(condition(sql, parts));
    }

    
    public final JoinTable andNot(Condition c) {
        return and(c.not());
    }

    
    public final JoinTable andNot(Field<Boolean> c) {
        return andNot(condition(c));
    }

    
    public final JoinTable andExists(Select<?> select) {
        return and(exists(select));
    }

    
    public final JoinTable andNotExists(Select<?> select) {
        return and(notExists(select));
    }

    
    public final JoinTable or(Condition c) {
        condition.addConditions(Operator.OR, c);
        return this;
    }

    
    public final JoinTable or(Field<Boolean> c) {
        return or(condition(c));
    }

    
    public final JoinTable or(String sql) {
        return or(condition(sql));
    }

    
    public final JoinTable or(String sql, Object... bindings) {
        return or(condition(sql, bindings));
    }

    
    public final JoinTable or(String sql, QueryPart... parts) {
        return or(condition(sql, parts));
    }

    
    public final JoinTable orNot(Condition c) {
        return or(c.not());
    }

    
    public final JoinTable orNot(Field<Boolean> c) {
        return orNot(condition(c));
    }

    
    public final JoinTable orExists(Select<?> select) {
        return or(exists(select));
    }

    
    public final JoinTable orNotExists(Select<?> select) {
        return or(notExists(select));
    }
}
