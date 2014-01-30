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
package org.jooq.tools;

import static org.jooq.conf.ParamType.INLINED;

import java.util.logging.Level;

import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.ExecuteType;
import org.jooq.impl.DefaultExecuteListener;

/**
 * A default {@link ExecuteListener} that just logs events to java.util.logging,
 * log4j, or slf4j using the {@link JooqLogger}
 *
 * @author Lukas Eder
 */
public class LoggerListener extends DefaultExecuteListener {

    /**
     * Generated UID
     */
    private static final long serialVersionUID = 7399239846062763212L;

    private static final JooqLogger log   = JooqLogger.getLogger(LoggerListener.class);

    
    public void renderEnd(ExecuteContext ctx) {
        if (log.isDebugEnabled()) {
            String[] batchSQL = ctx.batchSQL();

            if (ctx.query() != null) {

                // Actual SQL passed to JDBC
                log.debug("Executing query", ctx.sql());

                // [#1278] DEBUG log also SQL with inlined bind values, if
                // that is not the same as the actual SQL passed to JDBC
                String inlined = ctx.query().getSQL(INLINED);
                if (!ctx.sql().equals(inlined)) {
                    log.debug("-> with bind values", inlined);
                }
            }
            else if (!StringUtils.isBlank(ctx.sql())) {

                // [#1529] Batch queries should be logged specially
                if (ctx.type() == ExecuteType.BATCH) {
                    log.debug("Executing batch query", ctx.sql());
                }
                else {
                    log.debug("Executing query", ctx.sql());
                }
            }

            // [#2532] Log a complete BatchMultiple query
            else if (batchSQL.length > 0) {
                if (batchSQL[batchSQL.length - 1] != null) {
                    for (String sql : batchSQL) {
                        log.debug("Executing batch query", sql);
                    }
                }
            }
        }
    }

    
    public void recordEnd(ExecuteContext ctx) {
        if (log.isTraceEnabled() && ctx.record() != null)
            logMultiline("Record fetched", ctx.record().toString(), Level.FINER);
    }

    
    public void resultEnd(ExecuteContext ctx) {
        if (log.isDebugEnabled() && ctx.result() != null) {
            logMultiline("Fetched result", ctx.result().format(5), Level.FINE);
        }
    }

    
    public void executeEnd(ExecuteContext ctx) {
        if (log.isDebugEnabled() && ctx.rows() >= 0) {
            log.debug("Affected row(s)", ctx.rows());
        }
    }

    private void logMultiline(String comment, String message, Level level) {
        for (String line : message.split("\n")) {
            if (level == Level.FINE) {
                log.debug(comment, line);
            }
            else {
                log.trace(comment, line);
            }

            comment = "";
        }
    }
}
