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
import java.util.Set;
import java.util.TreeSet;

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

	private final PrintWriter writer;
	private final StringBuilder sb;
	private final Set<String> imported;

	public GenerationWriter(PrintWriter writer) {
		this.writer = writer;
		this.sb = new StringBuilder();
		this.imported = new TreeSet<String>();
	}

	public void printImportPlaceholder() {
		println(IMPORT_STATEMENT);
	}

	public void printImport(Class<?> clazz) {
		if (clazz.isArray()) {
			return;
		}

		printImport(clazz.getName());
	}

	public void printImport(String name) {
		if (name.startsWith("java.lang")) {
			return;
		}

		imported.add(name);
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

	public void close() {
		String string = sb.toString();

		StringBuilder imports = new StringBuilder();
		for (String clazz : imported) {
			imports.append("import " + clazz + ";\n");
		}

		string = string.replaceAll(IMPORT_STATEMENT, imports.toString());

		writer.append(string);
		writer.close();
	}
}
