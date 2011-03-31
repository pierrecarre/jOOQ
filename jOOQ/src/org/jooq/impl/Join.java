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

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.JoinType;
import org.jooq.Table;
import org.jooq.TableLike;

/**
 * @author Lukas Eder
 */
class Join extends AbstractQueryPart {

    private static final long           serialVersionUID = 2275930365728978050L;

    private final TableLike<?>          table;
    private final ConditionProviderImpl condition;
    private final JoinType              type;

    Join(Configuration configuration, TableLike<?> table, JoinType type, Condition... conditions) {
        super(configuration);

        this.condition = new ConditionProviderImpl(configuration);

        this.table = table;
        this.condition.addConditions(conditions);
        this.type = type;
    }

    @Override
    public int bind(Configuration configuration, PreparedStatement stmt, int initialIndex) throws SQLException {
        int result = initialIndex;

        result = getTable().getQueryPart().bind(configuration, stmt, result);
        if (getCondition() != null) {
            result = getCondition().getQueryPart().bind(configuration, stmt, result);
        }

        return result;
    }

    public Condition getCondition() {
        return condition.getWhere();
    }

    public Table<?> getTable() {
        return table.asTable();
    }

    public JoinType getType() {
        return type;
    }

    @Override
    public String toSQLDeclaration(Configuration configuration, boolean inlineParameters) {
        return toSQL(configuration, inlineParameters, true);
    }

    @Override
    public String toSQLReference(Configuration configuration, boolean inlineParameters) {
        return toSQL(configuration, inlineParameters, false);
    }

    private String toSQL(Configuration configuration, boolean inlineParameters, boolean renderAsDeclaration) {
        StringBuilder sb = new StringBuilder();

        sb.append(getType().toSQL());
        sb.append(" ");

        if (renderAsDeclaration) {
            sb.append(getTable().getQueryPart().toSQLDeclaration(configuration, inlineParameters));
        }
        else {
            sb.append(getTable().getQueryPart().toSQLReference(configuration, inlineParameters));
        }

        sb.append(" on ");

        if (renderAsDeclaration) {
            sb.append(getCondition().getQueryPart().toSQLDeclaration(configuration, inlineParameters));
        }
        else {
            sb.append(getCondition().getQueryPart().toSQLReference(configuration, inlineParameters));
        }

        return sb.toString();
    }
}
