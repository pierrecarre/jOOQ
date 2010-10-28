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

package org.jooq.util;

import org.jooq.impl.Factory;

/**
 * A general interface defining any database object, such as tables, views,
 * stored procedures, etc.
 *
 * @author Lukas Eder
 */
public interface Definition {

    /**
     * @return A reference to the Database context
     */
    Database getDatabase();

    /**
     * @return The schema of this object
     */
    String getSchemaName();

    /**
     * @return The name of this object, e.g. [my_table]
     */
    String getName();

    /**
     * @return The name of this object in upper case letters, e.g. [MY_TABLE]
     */
    String getNameUC();

    /**
     * @return The comment of this object
     */
    String getComment();

    /**
     * @return The Java class name representing this object, e.g. [MyTable]
     */
    String getJavaClassName();

    /**
     * @return The full Java class name representing this object, e.g.
     *         [org.jooq.generated.MyTable]
     */
    String getFullJavaClassName();

    /**
     * @return The Java class name representing this object, e.g.
     *         [MyTableSuffix]
     */
    String getJavaClassName(String suffix);

    /**
     * @return The Java class file name representing this object, e.g.
     *         [MyTable.java]
     */
    String getFileName();

    /**
     * @return The Java class file name representing this object, e.g.
     *         [MyTableSuffix.java]
     */
    String getFileName(String suffix);

    /**
     * @return A qualified name for this object
     */
    String getQualifiedName();

    /**
     * @return The factory for this definition's database
     */
    Factory create();

    /**
     * @return Get the target package for the current configuration
     */
    String getTargetPackage();

    /**
     * @return Get the target package's subpackage for this definition
     */
    String getSubPackage();
}
