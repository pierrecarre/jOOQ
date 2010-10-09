/**
 * Copyright (c) 2009, Lukas Eder, lukas.eder@gmail.com
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

package org.jooq;

/**
 * The configuration
 *
 * @author Lukas Eder
 */
public class Configuration {

    private static final Configuration INSTANCE = new Configuration();
    private SQLDialect                 dialect;

    public static Configuration getInstance() {
        return INSTANCE;
    }

    /**
     * The used SQL dialect. This can be set externally using JVM flag
     * -Dorg.jooq.sql-dialect. If no dialect is provided,
     * {@link SQLDialect#SQL99} is used.
     *
     * @return The used {@link SQLDialect}
     * @see SQLDialect
     * @throws SQLDialectNotSupportedException if dialect configured in
     *             -Dorg.jooq.sql-dialect is unknown
     */
    public SQLDialect getDialect() throws SQLDialectNotSupportedException {
        if (dialect == null) {
            String dialectName = System.getProperty("org.jooq.sql-dialect");

            if (dialectName != null) {
                try {
                    dialect = SQLDialect.valueOf(dialectName);
                }
                catch (IllegalArgumentException ignore) {
                    throw new SQLDialectNotSupportedException("Unknown dialect : " + dialectName);
                }
            }

            if (dialect == null) {
                dialect = SQLDialect.SQL99;
            }
        }
        return dialect;
    }

    /**
     * Set a new dialect to the configuration
     *
     * @param dialect The new dialect
     * @throws SQLDialectNotSupportedException if dialect is not supported
     */
    public void setDialect(SQLDialect dialect) throws SQLDialectNotSupportedException {
        this.dialect = dialect;
    }

    private Configuration() {}
}
