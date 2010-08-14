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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import org.jooq.TableField;
import org.jooq.impl.Parameter;
import org.jooq.impl.SchemaImpl;
import org.jooq.impl.StoredFunctionImpl;
import org.jooq.impl.StoredProcedureImpl;
import org.jooq.impl.TableFieldImpl;
import org.jooq.impl.TableImpl;

/**
 * @author Lukas Eder
 */
public class DefaultGenerator implements Generator {

	private String targetPackageName;
	private String targetDirectory;

	@Override
	public void generate(Database database) throws SQLException, IOException {
		File targetPackageDir = new File(targetDirectory + File.separator + targetPackageName.replace('.', File.separatorChar));
		
		// ----------------------------------------------------------------------
		// Generating schemas
		// ----------------------------------------------------------------------
		System.out.println("Generating classes in " + targetPackageDir.getCanonicalPath());
		SchemaDefinition schema = database.getSchema();
		{
			targetPackageDir.mkdirs();
			
			System.out.println("Generating schema " + schema.getName() + " into " + schema.getFileName());

			GenerationWriter out = new GenerationWriter(new PrintWriter(new File(targetPackageDir, schema.getFileName())));
			printHeader(out, targetPackageName);
			printClassJavadoc(out, schema);

			out.println("public class " + schema.getJavaClassName() + " extends SchemaImpl {");
			printSerial(out);
			out.printImport(SchemaImpl.class);
			out.println();
			out.println("\t/**");
			out.println("\t * The singleton instance of " + schema.getName());
			out.println("\t */");
			out.println("\tpublic static final " + schema.getJavaClassName() + " " + schema.getNameUC() + " = new " + schema.getJavaClassName() + "();");
			
			out.println();
			printNoFurtherInstancesAllowedJavadoc(out);
			out.println("\tprivate " + schema.getJavaClassName() + "() {");
			out.println("\t\tsuper(\"" + schema.getName() + "\");");
			out.println("\t}");
			
			out.println("}");
			out.close();
		}
		
		// ----------------------------------------------------------------------
		// Generating tables
		// ----------------------------------------------------------------------
		File targetTablePackageDir = new File(targetPackageDir, "tables");
		System.out.println("Generating classes in " + targetTablePackageDir.getCanonicalPath());
		
		for (TableDefinition table : database.getTables()) {
			targetTablePackageDir.mkdirs();

			System.out.println("Generating table " + table.getName() + " into " + table.getFileName());

			GenerationWriter out = new GenerationWriter(new PrintWriter(new File(targetTablePackageDir, table.getFileName())));
			printHeader(out, targetPackageName + ".tables");
			printClassJavadoc(out, table);
			
			out.println("public class " + table.getJavaClassName() + " extends TableImpl {");
			printSerial(out);
			out.printImport(TableImpl.class);
			out.println();
			out.println("\t/**");
			out.println("\t * The singleton instance of " + table.getName());
			out.println("\t */");
			out.println("\tpublic static final " + table.getJavaClassName() + " " + table.getNameUC() + " = new " + table.getJavaClassName() + "();");
			
			for (ColumnDefinition column : table.getColumns()) {
				printColumn(out, column, table.getNameUC());
			}
			
			out.println();
			printNoFurtherInstancesAllowedJavadoc(out);
			out.println("\tprivate " + table.getJavaClassName() + "() {");
			out.println("\t\tsuper(\"" + table.getName() + "\", " + schema.getJavaClassName() + "." + schema.getNameUC() + ");");
			out.println("\t}");
			out.printImport(targetPackageName + "." + schema.getJavaClassName());
			
			out.println("}");
			out.close();
		}
		
		
		// ----------------------------------------------------------------------
		// Generating stored procedures
		// ----------------------------------------------------------------------
		File targetProcedurePackageDir = new File(targetPackageDir, "procedures");
		
		System.out.println("Generating classes in " + targetProcedurePackageDir.getCanonicalPath());
		for (ProcedureDefinition procedure : database.getProcedures()) {
			targetProcedurePackageDir.mkdirs();

			System.out.println("Generating procedure " + procedure.getName() + " into " + procedure.getFileName());

			GenerationWriter out = new GenerationWriter(new PrintWriter(new File(targetProcedurePackageDir, procedure.getFileName())));
			printHeader(out, targetPackageName + ".procedures");
			printClassJavadoc(out, procedure);

			out.println("public class " + procedure.getJavaClassName() + " extends StoredProcedureImpl {");
			printSerial(out);
			out.printImport(StoredProcedureImpl.class);
			out.println();
			
			
			for (ColumnDefinition parameter : procedure.getAllParameters()) {
				printParameter(out, parameter);
			}
			
			out.println();
			printNoFurtherInstancesAllowedJavadoc(out);
			out.println("\tpublic " + procedure.getJavaClassName() + "() {");
			out.println("\t\tsuper(\"" + procedure.getName() + "\");");
			out.println();
			
			for (ColumnDefinition parameter : procedure.getAllParameters()) {
				String parameterNameUC = parameter.getName().toUpperCase();

				out.print("\t\t");
				
				if (procedure.getInParameters().contains(parameter)) {
					if (procedure.getOutParameters().contains(parameter)) {
						out.println("addInOutParameter(" + parameterNameUC + ");");
					} else {
						out.println("addInParameter(" + parameterNameUC + ");");
					}
				} else {
					out.println("addOutParameter(" + parameterNameUC + ");");
				}
			}
			
			out.println("\t}");
			
			for (ColumnDefinition parameter : procedure.getInParameters()) {
				out.println();
				out.println("\tpublic void set" + parameter.getJavaClassName() + "(" + parameter.getType() + " value) {");
				out.println("\t\tsetValue(" + parameter.getNameUC() + ", value);");
				out.println("\t}");
			}
			
			for (ColumnDefinition parameter : procedure.getOutParameters()) {
				out.println();
				out.println("\tpublic " + parameter.getType() + " get" + parameter.getJavaClassName() + "() {");
				out.println("\t\treturn getValue(" + parameter.getNameUC() + ");");
				out.println("\t}");
			}
			
			
			out.println("}");
			out.close();
		}
		
		
		// ----------------------------------------------------------------------
		// Generating stored functions
		// ----------------------------------------------------------------------
		File targetFunctionPackageDir = new File(targetPackageDir, "functions");
		
		System.out.println("Generating classes in " + targetFunctionPackageDir.getCanonicalPath());
		for (FunctionDefinition function : database.getFunctions()) {
			targetFunctionPackageDir.mkdirs();

			System.out.println("Generating function " + function.getName() + " into " + function.getFileName());

			GenerationWriter out = new GenerationWriter(new PrintWriter(new File(targetFunctionPackageDir, function.getFileName())));
			printHeader(out, targetPackageName + ".functions");
			printClassJavadoc(out, function);

			out.println("public class " + function.getJavaClassName() + " extends StoredFunctionImpl<" + function.getReturnType() + "> {");
			printSerial(out);
			out.printImport(StoredFunctionImpl.class);
			out.println();
			
			
			for (ColumnDefinition parameter : function.getInParameters()) {
				printParameter(out, parameter);
			}
			
			out.println();
			printNoFurtherInstancesAllowedJavadoc(out);
			out.println("\tpublic " + function.getJavaClassName() + "() {");
			out.println("\t\tsuper(\"" + function.getName() + "\", " + function.getReturnType() + ".class);");
			out.println();
			
			for (ColumnDefinition parameter : function.getInParameters()) {
				String parameterNameUC = parameter.getName().toUpperCase();

				out.print("\t\t");
				out.println("addInParameter(" + parameterNameUC + ");");
			}
			
			out.println("\t}");
			
			for (ColumnDefinition parameter : function.getInParameters()) {
				out.println();
				out.println("\tpublic void set" + parameter.getJavaClassName() + "(" + parameter.getType() + " value) {");
				out.println("\t\tsetValue(" + parameter.getNameUC() + ", value);");
				out.println("\t}");
			}
			
			
			out.println("}");
			out.close();
		}
	}

	private void printColumn(GenerationWriter out, ColumnDefinition column, String targetTableNameUC) {
		printColumnDefinition(out, column, targetTableNameUC, TableField.class, TableFieldImpl.class);
	}

	private void printParameter(GenerationWriter out, ColumnDefinition parameter) {
		printColumnDefinition(out, parameter, null, Parameter.class, Parameter.class);
	}
	
	private void printColumnDefinition(GenerationWriter out, ColumnDefinition column, String targetObjectNameUC, Class<?> declaredMemberClass, Class<?> concreteMemberClass) {
		String concreteMemberType = concreteMemberClass.getSimpleName();
		String declaredMemberType = declaredMemberClass.getSimpleName();

		String columnDisambiguationSuffix = column.getNameUC().equals(targetObjectNameUC) ? "_" : "";
		printFieldJavaDoc(out, columnDisambiguationSuffix, column);

		out.println("\tpublic static final " + declaredMemberType + "<" + column.getType() + "> " + 
				column.getNameUC() + columnDisambiguationSuffix +
				" = new " + concreteMemberType + "<" + column.getType() + ">(\"" 
				+ column.getName() + "\", " + 
				column.getType() + ".class" +
				(targetObjectNameUC != null ? ", " + targetObjectNameUC : "") +
				");");
		out.printImport(declaredMemberClass);
		out.printImport(concreteMemberClass);
		out.printImport(column.getTypeClass());
	}

	private void printSerial(GenerationWriter out) {
		out.println();
		out.println("\tprivate static final long serialVersionUID = 1L;");
	}

	private void printFieldJavaDoc(GenerationWriter out, String disambiguationSuffix, Definition definition) {
		out.println();
		out.println("\t/**");
		
		String comment = definition.getComment();
		
		if (comment != null && comment.length() > 0) {
			out.println("\t * " + comment);
		} else {
			out.println("\t * An uncommented item");
		}

		if (disambiguationSuffix != null && disambiguationSuffix.length() > 0) {
			out.println("\t * ");
			out.println("\t * This item has the same name as its container. That is why an underline character was appended to the Java field name");
		}
		
		out.println("\t */");
	}

	private void printNoFurtherInstancesAllowedJavadoc(GenerationWriter out) {
		out.println("\t/**");
		out.println("\t * No further instances allowed");
		out.println("\t */");
	}

	private void printClassJavadoc(GenerationWriter out, Definition definition) {
		out.println("/**");
		out.println(" * This class is generated by jOOQ.");
		
		String comment = definition.getComment();
		
		if (comment != null && comment.length() > 0) {
			out.println(" *");
			out.println(" * " + comment);
		}
		
		out.println(" */");
	}

	private void printHeader(GenerationWriter out, String packageName) {
		out.println("/**");
		out.println(" * This class is generated by jOOQ");
		out.println(" */");
		out.println("package " + packageName + ";");
		out.println();
		out.printImportPlaceholder();
		out.println();
	}

	@Override
	public void setTargetPackage(String packageName) {
		this.targetPackageName = packageName;
	}

	@Override
	public void setTargetDirectory(String directory) {
		this.targetDirectory = directory;
	}
}
