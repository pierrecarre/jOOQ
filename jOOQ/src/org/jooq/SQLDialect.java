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

package org.jooq;

/**
 * This enumeration lists all supported dialects. The dialect used by the
 * framework can be set in {@link Configuration#setDialect(SQLDialect)} or using
 * the JVM parameter -Dorg.jooq.sql-dialect
 *
 * @author Lukas Eder
 */
public enum SQLDialect {
    /**
     * The standard SQL dialect.
     *
     * @deprecated - Do not reference this pseudo-dialect. It is only used for unit testing
     */
    @Deprecated
    SQL99(null),

    /**
     * The MySQL dialect
     */
    MYSQL("MySQL"),

    /**
     * The Oracle dialect
     */
    ORACLE("Oracle"),

    /**
     * The Microsoft SQL dialect
     */
    MSSQL("MSSQL"),

    /**
     * The PostGres dialect
     */
    POSTGRES("Postgres"),

    /**
     * The Hypersonic SQL dialect
     */
    HSQLDB("HSQLDB"),

    /**
     * The IBM DB2 SQL dialect
     */
    DB2("DB2"),

    /**
     * The Apache Derby SQL dialect
     */
    DERBY("Derby"),

    /**
     * The H2 SQL dialect
     */
    H2("H2"),

    /**
     * The SQLite dialect
     */
    SQLITE("SQLite");

    private final String name;

    private SQLDialect(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }
}
