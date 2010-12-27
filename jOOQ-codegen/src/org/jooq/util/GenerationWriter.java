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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jooq.impl.StringUtils;

/**
 * A wrapper for a {@link PrintWriter}
 * <p>
 * This wrapper postpones the actual write to the wrapped {@link PrintWriter}
 * until all information about the target Java class is available. This way, the
 * import dependencies can be calculated at the end.
 *
 * @author Lukas Eder
 */
public class GenerationWriter {

	private static final String IMPORT_STATEMENT = "__IMPORT_STATEMENT__";
    private static final String STATIC_INITIALISATION_STATEMENT = "__STATIC_INITIALISATION_STATEMENT__";
    private static final String SERIAL_STATEMENT = "__SERIAL_STATEMENT__";

	private final PrintWriter writer;
	private final StringBuilder sb;
	private final Set<String> imported;
	private final List<String> staticInitialisationStatements;
	private final Set<Object> alreadyPrinted;

	public GenerationWriter(PrintWriter writer) {
		this.writer = writer;
		this.sb = new StringBuilder();
		this.imported = new TreeSet<String>();
		this.staticInitialisationStatements = new ArrayList<String>();
		this.alreadyPrinted = new HashSet<Object>();
	}

	public void printImportPlaceholder() {
		println(IMPORT_STATEMENT);
	}

	public void printStaticInitialisationStatementsPlaceholder() {
		println(STATIC_INITIALISATION_STATEMENT);
	}

	public void printImport(Class<?> clazz) {
		if (clazz.isArray()) {
			return;
		}

		printImport(clazz.getName());
	}

	public void printImport(String name) {
		if (name.startsWith("java.lang") || name.contains("[")) {
			return;
		}

		imported.add(name);
	}

	public void printStaticInitialisationStatement(String statement) {
		staticInitialisationStatements.add(statement);
	}

	public void print(String string) {
		sb.append(string);
	}

	public void println(String string) {
		sb.append(string + "\n");
	}

	public void println() {
		sb.append("\n");
	}

	public boolean printOnlyOnce(Object object) {
		if (!alreadyPrinted.contains(object)) {
			alreadyPrinted.add(object);
			return true;
		}

		return false;
	}

    public void printSerial() {
        println();
        println("\tprivate static final long serialVersionUID = " + SERIAL_STATEMENT + ";");
    }

	public void close() {
		String string = sb.toString();

		StringBuilder imports = new StringBuilder();
		String previous = ".";
		for (String clazz : imported) {
			String domain1 = clazz.substring(0, Math.max(0, clazz.indexOf(".")));
			String domain2 = previous.substring(0, Math.max(0, previous.indexOf(".")));

			if (!domain1.equals(domain2)) {
				imports.append("\n");
			}

			imports.append("import " + clazz + ";\n");
			previous = clazz;
		}

		StringBuilder statics = new StringBuilder();
		boolean hasStatics = false;

		for (String statement : staticInitialisationStatements) {
			if (!StringUtils.isBlank(statement)) {
				hasStatics = true;
				break;
			}
		}

		if (hasStatics) {
			statics.append("\n");
			statics.append("\t/*\n");
			statics.append("\t * static initialiser\n");
			statics.append("\t */\n");
			statics.append("\tstatic {\n");
			for (String statement : staticInitialisationStatements) {
				statics.append("\t\t" + statement + "\n");
			}
			statics.append("\t}\n");
		}

		string = string.replace(IMPORT_STATEMENT, imports.toString());
		string = string.replace(STATIC_INITIALISATION_STATEMENT, statics.toString());
		string = string.replace(SERIAL_STATEMENT, String.valueOf(string.hashCode()));
		
		writer.append(string);
		writer.close();
	}
}
