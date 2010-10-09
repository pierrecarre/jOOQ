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
package org.jooq.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jooq.Configuration;
import org.jooq.Limit;
import org.jooq.SQLDialectNotSupportedException;

/**
 * @author Lukas Eder
 */
class LimitImpl extends AbstractQueryPart implements Limit {

    /**
     * Generated UID
     */
    private static final long serialVersionUID = 2053741242981425602L;
    private int               lowerBound;
    private int               numberOfRows;

    LimitImpl() {
        lowerBound = 1;

        // such that getUpperBound() == Integer.MAX_VALUE
        numberOfRows = Integer.MAX_VALUE - 2;
    }

    @Override
    public final String toSQLReference(boolean inlineParameters) {
        StringBuilder sb = new StringBuilder();

        switch (Configuration.getInstance().getDialect()) {
            case MYSQL: {
                sb.append("limit ");
                sb.append(getLowerBound());
                sb.append(", ");
                sb.append(getNumberOfRows());
                break;
            }
            default:
                throw new SQLDialectNotSupportedException("LIMIT not supported");
        }

        return sb.toString();
    }

    @Override
    public final int bind(PreparedStatement stmt, int initialIndex) throws SQLException {
        return initialIndex;
    }

    @Override
    public final int getLowerBound() {
        return lowerBound;
    }

    @Override
    public final int getUpperBound() {
        return lowerBound + numberOfRows + 1;
    }

    @Override
    public final int getNumberOfRows() {
        return numberOfRows;
    }

    @Override
    public final boolean isApplicable() {
        return getLowerBound() != 1 || getUpperBound() != Integer.MAX_VALUE;
    }

    void setLowerBound(int lowerBound) {
        this.lowerBound = lowerBound;
    }

    void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }
}
