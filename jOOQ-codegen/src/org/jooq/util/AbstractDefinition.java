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

package org.jooq.util;

import java.io.File;
import java.sql.Connection;

import org.jooq.impl.Factory;
import org.jooq.impl.StringUtils;

/**
 * A base implementation for any type of definition.
 *
 * @author Lukas Eder
 */
public abstract class AbstractDefinition implements Definition {

    private final Database     database;
    private final String       name;
    private final String       comment;
    private final String       overload;

    public AbstractDefinition(Database database, String name, String comment) {
        this(database, name, comment, null);
    }

    public AbstractDefinition(Database database, String name, String comment, String overload) {
        this.database = database;
        this.name = name;
        this.comment = comment;
        this.overload = overload;
    }

    @Override
    public final String getOverload() {
        return overload;
    }

	@Override
	public final String getSchemaName() {
		return database.getSchemaName();
	}

	@Override
	public final String getName() {
		return name;
	}

    @Override
    public final String getNameUC() {
        return name.toUpperCase();
    }

    @Override
    public final String getNameLC() {
        return name.toLowerCase();
    }

	@Override
	public final String getComment() {
		return comment;
	}

	@Override
	public final String getJavaClassName() {
	    return getJavaClassName("");
	}

    @Override
    public final String getJavaPackageName() {
        return getJavaPackageName("");
    }

    @Override
    public final String getJavaPackageName(String suffix) {
        StringBuilder sb = new StringBuilder();

        sb.append(getTargetPackage());

        if (!StringUtils.isBlank(getSubPackage())) {
            sb.append(".");
            sb.append(getSubPackage());
        }

        if ("Record".equals(suffix)) {
            sb.append(".records");
        }

        return sb.toString();
    }

    @Override
    public final String getJavaClassNameLC() {
        return getJavaClassNameLC("");
    }

    @Override
    public final String getJavaClassNameLC(String suffix) {
        String result = getJavaClassName0(suffix);

        return result.substring(0, 1).toLowerCase() + result.substring(1);
    }


    @Override
    public String getJavaClassName(String suffix) {
        String result = getJavaClassName0(suffix);

        // [#286] Prevent classes with names String, Integer, BigInteger, etc
        // from being generated that way. They're only trouble
        if (isReserved(result)) {
            return result + "_";
        }


        return result;
    }

	private final String getJavaClassName0(String suffix) {
	    StringBuilder result = new StringBuilder();

        for (String word : getName().split("_")) {

            // Uppercase first letter of a word
            if (word.length() > 0) {

                // #82 - If a word starts with a digit, prevail the
                // underscore to prevent naming clashes
                if (Character.isDigit(word.charAt(0))) {
                    result.append("_");
                }

                result.append(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase());
            }

            // If no letter exists, prevail the underscore (e.g. leading underscores)
            else {
                result.append("_");
            }
        }

        if (!StringUtils.isEmpty(suffix)) {
            result.append(suffix);
        }

        if (!StringUtils.isBlank(overload)) {
            result.append(overload);
        }

        return result.toString();
	}

    /**
     * Public classes from the java.lang.* package are reserved. Objects should
     * not be generated using those names
     */
    protected boolean isReserved(String className) {
        for (String prefix : new String[] { "java.lang.", "java.math." }) {
            try {
                Class.forName(prefix + className);
                return true;
            } catch (Exception ignore) {
            }
        }

        return false;
    }

	@Override
    public final String getFullJavaClassName() {
	    return getFullJavaClassName("");
    }


    @Override
    public final String getFullJavaClassName(String suffix) {
        StringBuilder sb = new StringBuilder();

        sb.append(getJavaPackageName(suffix));
        sb.append(".");
        sb.append(getJavaClassName(suffix));

        return sb.toString();
    }

    @Override
    public final String getTargetPackage() {
        return getDatabase().getTargetPackage();
    }

    @Override
    public final File getFile() {
        return getFile("");
    }

    @Override
    public final File getFile(String suffix) {
        String dir = getDatabase().getTargetDirectory();
        String pkg = getJavaPackageName(suffix).replaceAll("\\.", "/");
        return new File(dir + "/" + pkg, getFileName(suffix));
    }

    @Override
	public final String getFileName() {
		return getJavaClassName() + ".java";
	}

	@Override
	public final String getFileName(String suffix) {
		return getJavaClassName() + suffix + ".java";
	}

	@Override
	public String getQualifiedName() {
		return getName();
	}

	@Override
	public final Database getDatabase() {
		return database;
	}

	protected final Connection getConnection() {
		return database.getConnection();
	}

	@Override
	public final String toString() {
		return getQualifiedName();
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof Definition) {
			Definition that = (Definition) obj;
			return that.getQualifiedName().equals(getQualifiedName());
		}

		return false;
	}

	@Override
	public final int hashCode() {
		return getQualifiedName().hashCode();
	}

	@Override
    public final Factory create() {
	    return database.create();
	}
}
