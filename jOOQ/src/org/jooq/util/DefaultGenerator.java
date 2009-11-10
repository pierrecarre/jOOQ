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
import java.util.Random;

import org.jooq.TableField;
import org.jooq.impl.Parameter;
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
		// Generating tables
		// ----------------------------------------------------------------------
		File targetTablePackageDir = new File(targetPackageDir, "tables");
		System.out.println("Generating classes in " + targetPackageDir.getCanonicalPath());
		
		for (TableDefinition table : database.getTables()) {
			targetTablePackageDir.mkdirs();
			
			String targetSchemaName = table.getSchema();
			String targetTableName = table.getName();
			String targetTableNameUC = targetTableName.toUpperCase();
			String targetClassName = getJavaClassName(targetTableName);
			String targetFileName = targetClassName + ".java";
			String targetComment = table.getComment();
			
			System.out.println("Generating table " + targetTableName + " into " + targetFileName);

			GenerationWriter out = new GenerationWriter(new PrintWriter(new File(targetTablePackageDir, targetFileName)));
			printHeader(out, targetPackageName + ".tables");
			printClassJavadoc(out, targetComment);
			
			out.println("public class " + targetClassName + " extends TableImpl {");
			printSerial(out);
			out.printImport(TableImpl.class);
			out.println();
			out.println("\t/**");
			out.println("\t * The singleton instance of " + targetTableName);
			out.println("\t */");
			out.println("\tpublic static final " + targetClassName + " " + targetTableNameUC + " = new " + targetClassName + "();");
			
			for (ColumnDefinition column : table.getColumns()) {
				printColumn(out, column, targetTableNameUC);
			}
			
			out.println();
			printNoFurtherInstancesAllowedJavadoc(out);
			out.println("\tprivate " + targetClassName + "() {");
			out.println("\t\tsuper(\"" + targetTableName + "\", \"" + targetSchemaName + "\");");
			out.println("\t}");
			
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

			String targetProcedureName = procedure.getName();
			String targetClassName = getJavaClassName(targetProcedureName);
			String targetFileName = targetClassName + ".java";
			String targetComment = procedure.getComment();
			
			System.out.println("Generating procedure " + targetProcedureName + " into " + targetFileName);

			GenerationWriter out = new GenerationWriter(new PrintWriter(new File(targetProcedurePackageDir, targetFileName)));
			printHeader(out, targetPackageName + ".procedures");
			printClassJavadoc(out, targetComment);

			out.println("public class " + targetClassName + " extends StoredProcedureImpl {");
			printSerial(out);
			out.printImport(StoredProcedureImpl.class);
			out.println();
			
			
			for (ColumnDefinition parameter : procedure.getAllParameters()) {
				printParameter(out, parameter);
			}
			
			out.println();
			printNoFurtherInstancesAllowedJavadoc(out);
			out.println("\tpublic " + targetClassName + "() {");
			out.println("\t\tsuper(\"" + targetProcedureName + "\");");
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
				Class<?> parameterClass = parameter.getType();
				String parameterType = parameterClass.getSimpleName();
				String parameterNameUC = parameter.getName().toUpperCase();

				out.println();
				out.println("\tpublic void set" + getJavaClassName(parameterNameUC) + "(" + parameterType + " value) {");
				out.println("\t\tsetValue(" + parameterNameUC + ", value);");
				out.println("\t}");
			}
			
			for (ColumnDefinition parameter : procedure.getOutParameters()) {
				Class<?> parameterClass = parameter.getType();
				String parameterType = parameterClass.getSimpleName();
				String parameterNameUC = parameter.getName().toUpperCase();

				out.println();
				out.println("\tpublic " + parameterType + " get" + getJavaClassName(parameterNameUC) + "() {");
				out.println("\t\treturn getValue(" + parameterNameUC + ");");
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

			String targetFunctionName = function.getName();
			String targetClassName = getJavaClassName(targetFunctionName);
			String targetFileName = targetClassName + ".java";
			String targetComment = function.getComment();
			
			ColumnDefinition returnValue = function.getReturnValue();
			Class<?> returnValueClass = returnValue.getType();
			String returnValueType = returnValueClass.getSimpleName();
			
			System.out.println("Generating function " + targetFunctionName + " into " + targetFileName);

			GenerationWriter out = new GenerationWriter(new PrintWriter(new File(targetFunctionPackageDir, targetFileName)));
			printHeader(out, targetPackageName + ".functions");
			printClassJavadoc(out, targetComment);

			out.println("public class " + targetClassName + " extends StoredFunctionImpl<" + returnValueType + "> {");
			printSerial(out);
			out.printImport(StoredFunctionImpl.class);
			out.println();
			
			
			for (ColumnDefinition parameter : function.getInParameters()) {
				printParameter(out, parameter);
			}
			
			out.println();
			printNoFurtherInstancesAllowedJavadoc(out);
			out.println("\tpublic " + targetClassName + "() {");
			out.println("\t\tsuper(\"" + targetFunctionName + "\", " + returnValueType + ".class);");
			out.println();
			
			for (ColumnDefinition parameter : function.getInParameters()) {
				String parameterNameUC = parameter.getName().toUpperCase();

				out.print("\t\t");
				out.println("addInParameter(" + parameterNameUC + ");");
			}
			
			out.println("\t}");
			
			for (ColumnDefinition parameter : function.getInParameters()) {
				Class<?> parameterClass = parameter.getType();
				String parameterType = parameterClass.getSimpleName();
				String parameterNameUC = parameter.getName().toUpperCase();

				out.println();
				out.println("\tpublic void set" + getJavaClassName(parameterNameUC) + "(" + parameterType + " value) {");
				out.println("\t\tsetValue(" + parameterNameUC + ", value);");
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

		Class<?> columnClass = column.getType();
		String columnType = columnClass.getSimpleName();
		String columnName = column.getName();
		String columnNameUC = columnName.toUpperCase();
		String columnDisambiguationSuffix = columnNameUC.equals(targetObjectNameUC) ? "_" : "";
		String columnComment = column.getComment();

		printFieldJavaDoc(out, columnDisambiguationSuffix, columnComment);

		out.println("\tpublic static final " + declaredMemberType + "<" + columnType + "> " + 
				columnNameUC + columnDisambiguationSuffix +
				" = new " + concreteMemberType + "<" + columnType + ">(\"" 
				+ columnName + "\", " + 
				columnType + ".class" +
				(targetObjectNameUC != null ? ", " + targetObjectNameUC : "") +
				");");
		out.printImport(declaredMemberClass);
		out.printImport(concreteMemberClass);
		out.printImport(columnClass);
	}

	private void printSerial(GenerationWriter out) {
		out.println();
		out.println("\tprivate static final long serialVersionUID = " + new Random().nextLong() + "L;");
	}

	private void printFieldJavaDoc(GenerationWriter out, String disambiguationSuffix, String comment) {
		out.println();
		out.println("\t/**");
		
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

	private void printClassJavadoc(GenerationWriter out, String comment) {
		out.println("/**");
		out.println(" * This class is generated by jOOQ.");
		
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

	private String getJavaClassName(String tableName) {
		StringBuilder result = new StringBuilder();
		
		for (String word : tableName.split("_")) {
			result.append(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase());
		}
		
		return result.toString();
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
