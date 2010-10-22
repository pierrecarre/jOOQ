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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jooq.Configuration;
import org.jooq.Parameter;
import org.jooq.SQLDialect;
import org.jooq.SimpleSelectQuery;
import org.jooq.TableField;
import org.jooq.impl.ParameterImpl;
import org.jooq.impl.SchemaImpl;
import org.jooq.impl.StoredFunctionImpl;
import org.jooq.impl.StoredProcedureImpl;
import org.jooq.impl.TableFieldImpl;
import org.jooq.impl.TableImpl;
import org.jooq.impl.TableRecordImpl;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.impl.UpdatableTableImpl;

/**
 * A default implementation for code generation. Replace this code with your own
 * logic, if you need your database schema represented in a different way.
 * <p>
 * Note that you can also extend this class to generate POJO's or other stuff
 * entirely independent of jOOQ.
 *
 * @author Lukas Eder
 */
public class DefaultGenerator implements Generator {

	private String targetPackageName;
	private String targetDirectory;

	@Override
	public void generate(Database database) throws SQLException, IOException {
		File targetPackageDir = new File(targetDirectory + File.separator + targetPackageName.replace('.', File.separatorChar));

		// ----------------------------------------------------------------------
		// Initialising
		// ----------------------------------------------------------------------
		System.out.println("Emptying " + targetPackageDir.getCanonicalPath());
		empty(targetPackageDir);

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
			out.printImport(SQLDialect.class);
			out.println("\tprivate " + schema.getJavaClassName() + "() {");
			out.println("\t\tsuper(SQLDialect." + database.getDialect().name() + ", \"" + schema.getName() + "\");");
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

			String baseClass;
			if (database.generateRelations() && table.hasPrimaryKey()) {
				baseClass = "UpdatableTableImpl";
				out.printImport(UpdatableTableImpl.class);
			} else {
				baseClass = "TableImpl";
				out.printImport(TableImpl.class);
			}

			out.println("public class " + table.getJavaClassName() + " extends " + baseClass + "<"  + table.getJavaClassName("Record") + "> {");
			printSerial(out);
			out.println();
			out.println("\t/**");
			out.println("\t * The singleton instance of " + table.getName());
			out.println("\t */");
			out.println("\tpublic static final " + table.getJavaClassName() + " " + table.getNameUC() + " = new " + table.getJavaClassName() + "();");

			out.println();
			out.println("\t/**");
			out.println("\t * The class holding records for this table");
			out.println("\t */");
			out.println("\tprivate static final Class<" + table.getJavaClassName("Record") + "> __RECORD_TYPE = " + table.getJavaClassName("Record") + ".class;");
			out.println();
			out.println("\t/**");
			out.println("\t * The class holding records for this table");
			out.println("\t */");
			printOverride(out);
			out.println("\tpublic Class<" + table.getJavaClassName("Record") + "> getRecordType() {");
			out.println("\t\treturn __RECORD_TYPE;");
			out.println("\t}");
			out.printImport(targetPackageName + ".tables.records." + table.getJavaClassName("Record"));

			for (ColumnDefinition column : table.getColumns()) {
				printColumn(out, column, table);
			}

			out.println();
			printNoFurtherInstancesAllowedJavadoc(out);
			out.println("\tprivate " + table.getJavaClassName() + "() {");
			out.printImport(SQLDialect.class);
			out.println("\t\tsuper(SQLDialect." + database.getDialect().name() + ", \"" + table.getName() + "\", " + schema.getJavaClassName() + "." + schema.getNameUC() + ");");

			if (database.generateRelations()) {
				Set<String> primaryKeys = new HashSet<String>();

				for (ColumnDefinition column : table.getColumns()) {
					PrimaryKeyDefinition primaryKey = column.getPrimaryKey();

					if (primaryKey != null && !primaryKeys.contains(primaryKey.getNameUC())) {
						primaryKeys.add(primaryKey.getNameUC());

						for (String c : primaryKey.getKeyColumnNames()) {
							String statement = table.getNameUC() + ".addToPrimaryKey(" + c.toUpperCase() + ");";
							out.printStaticInitialisationStatement(statement);
						}
					}
				}
			}

			out.println("\t}");
			out.printImport(targetPackageName + "." + schema.getJavaClassName());

			out.printStaticInitialisationStatementsPlaceholder();
			out.println("}");
			out.close();
		}

		// ----------------------------------------------------------------------
		// Generating table records
		// ----------------------------------------------------------------------
		File targetTableRecordPackageDir = new File(new File(targetPackageDir, "tables"), "records");
		System.out.println("Generating classes in " + targetTableRecordPackageDir.getCanonicalPath());

		for (TableDefinition table : database.getTables()) {
			targetTableRecordPackageDir.mkdirs();

			System.out.println("Generating table " + table.getName() + " into " + table.getFileName("Record"));

			GenerationWriter out = new GenerationWriter(new PrintWriter(new File(targetTableRecordPackageDir, table.getFileName("Record"))));
			printHeader(out, targetPackageName + ".tables.records");
			printClassJavadoc(out, table);

			String baseClass;
			if (database.generateRelations() && table.hasPrimaryKey()) {
				baseClass = "UpdatableRecordImpl";
				out.printImport(UpdatableRecordImpl.class);
			} else {
				baseClass = "TableRecordImpl";
				out.printImport(TableRecordImpl.class);
			}

			out.println("public class " + table.getJavaClassName("Record") + " extends " + baseClass + "<"  + table.getJavaClassName("Record") + "> {");
			printSerial(out);

			for (ColumnDefinition column : table.getColumns()) {
				printGetterAndSetter(out, column, table, targetPackageName + ".tables");
			}

			out.println();
			out.println("\tpublic " + table.getJavaClassName("Record") + "(Configuration configuration) {");

			out.print("\t\tsuper(configuration, ");
			out.print(table.getJavaClassName());
			out.print(".");
			out.print(table.getNameUC());
			out.println(");");

			out.println("\t}");
			out.printImport(Configuration.class);

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
				printParameter(out, parameter, procedure);
			}

			out.println();
			printNoFurtherInstancesAllowedJavadoc(out);
			out.println("\tpublic " + procedure.getJavaClassName() + "() {");
            out.printImport(SQLDialect.class);
			out.println("\t\tsuper(SQLDialect." + database.getDialect().name() + ", \"" + procedure.getName() + "\");");
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
				printParameter(out, parameter, function);
			}

			out.println();
			printNoFurtherInstancesAllowedJavadoc(out);
			out.println("\tpublic " + function.getJavaClassName() + "() {");
            out.printImport(SQLDialect.class);
			out.println("\t\tsuper(SQLDialect." + database.getDialect().name() + ", \"" + function.getName() + "\", " + function.getReturnType() + ".class);");
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

	private void printOverride(GenerationWriter out) {
		out.println("\t@Override");
	}

	/**
	 * If file is a directory, recursively empty its children.
	 * If file is a file, delete it
	 */
	private void empty(File file) {
		if (file != null) {
			if (file.isDirectory()) {
				File[] children = file.listFiles();

				if (children != null) {
					for (File child : children) {
						empty(child);
					}
				}
			} else {
				if (file.getName().endsWith(".java")) {
					file.delete();
				}
			}
		}
	}

	private void printGetterAndSetter(GenerationWriter out, ColumnDefinition column, TableDefinition table, String tablePackage) throws SQLException {
		String columnDisambiguationSuffix = column.getNameUC().equals(table.getNameUC()) ? "_" : "";

		printFieldJavaDoc(out, null, column);
		out.println("\tpublic void set" + column.getJavaClassName() + "(" + column.getType() + " value) {");
		out.println("\t\tsetValue(" + table.getJavaClassName() + "." + column.getNameUC() + columnDisambiguationSuffix + ", value);");
		out.println("\t}");
		printFieldJavaDoc(out, null, column);
		out.println("\tpublic " + column.getType() + " get" + column.getJavaClassName() + "() {");
		out.println("\t\treturn getValue(" + table.getJavaClassName() + "." + column.getNameUC() + columnDisambiguationSuffix + ");");
		out.println("\t}");

		if (column.getDatabase().generateRelations()) {
			PrimaryKeyDefinition primaryKey = column.getPrimaryKey();
			if (primaryKey != null && out.printOnlyOnce(primaryKey)) {
				for (ForeignKeyDefinition foreignKey : primaryKey.getForeignKeys()) {

				    // #64 - If the foreign key does not match the referenced key, it
				    // is most likely because it references a non-primary unique key
				    // Skip code generation for this foreign key

				    // #69 - Should resolve this issue more thoroughly.
				    if (foreignKey.getReferencedColumnNames().size() != foreignKey.getKeyColumnNames().size()) {
	                    System.err.println("Foreign key " + foreignKey.getName() + " does not match its primary key!");
	                    System.err.println("No code is generated for this key. See trac tickets #64 and #69");

	                    continue;
				    }

					TableDefinition referencing = foreignKey.getKeyTableDefinition();
					printFieldJavaDoc(out, null, column);
					out.print("\tpublic List<");
					out.print(referencing.getJavaClassName("Record"));
					out.print("> get");
					out.print(referencing.getJavaClassName());
					if (!referencing.getJavaClassName().endsWith("s")) {
						out.print("s");
					}
					out.println("() throws SQLException {");

					out.print("\t\tSimpleSelectQuery<" + referencing.getJavaClassName("Record") + "> q = create().selectQuery(");
					out.print(referencing.getJavaClassName());
					out.print(".");
					out.print(referencing.getNameUC());
					out.println(");");

					for (int i = 0; i < foreignKey.getReferencedColumnNames().size(); i++) {
						out.print("\t\tq.addCompareCondition(");
						out.print(referencing.getJavaClassName());
						out.print(".");
						out.print(foreignKey.getKeyColumnNames().get(i).toUpperCase());
						out.print(", getValue(");
						out.print(table.getJavaClassName());
						out.print(".");
						out.print(primaryKey.getKeyColumnNames().get(i).toUpperCase());
						out.println("));");
					}

					out.println("\t\tq.execute();");
					out.println();
					out.println("\t\treturn q.getResult().getRecords();");
					out.println("\t}");

					out.printImport(tablePackage + "." + referencing.getJavaClassName());
					out.printImport(SimpleSelectQuery.class);
					out.printImport(SQLException.class);
					out.printImport(List.class);
				}
			}

			ForeignKeyDefinition foreignKey = column.getForeignKey();
			if (foreignKey != null && out.printOnlyOnce(foreignKey)) {

                // #64 - If the foreign key does not match the referenced key, it
                // is most likely because it references a non-primary unique key
                // Skip code generation for this foreign key

                // #69 - Should resolve this issue more thoroughly.
                if (foreignKey.getReferencedColumnNames().size() != foreignKey.getKeyColumnNames().size()) {
                    System.err.println("Foreign key " + foreignKey.getName() + " does not match its primary key!");
                    System.err.println("No code is generated for this key. See trac tickets #64 and #69");
                } else {

                    TableDefinition referenced = foreignKey.getReferencedTableDefinition();
    				printFieldJavaDoc(out, null, column);
    				out.print("\tpublic ");
    				out.print(referenced.getJavaClassName("Record"));
    				out.print(" get");
    				out.print(referenced.getJavaClassName());
    				out.println("() throws SQLException {");

    				out.print("\t\tSimpleSelectQuery<" + referenced.getJavaClassName("Record") + "> q = create().selectQuery(");
    				out.print(referenced.getJavaClassName());
    				out.print(".");
    				out.print(referenced.getNameUC());
    				out.println(");");

    				for (int i = 0; i < foreignKey.getReferencedColumnNames().size(); i++) {
    					out.print("\t\tq.addCompareCondition(");
    					out.print(referenced.getJavaClassName());
    					out.print(".");
    					out.print(foreignKey.getReferencedColumnNames().get(i).toUpperCase());
    					out.print(", getValue(");
    					out.print(table.getJavaClassName());
    					out.print(".");
    					out.print(foreignKey.getKeyColumnNames().get(i).toUpperCase());
    					out.println("));");
    				}

    				out.println("\t\tq.execute();");
    				out.println();
    				out.println("\t\tList<" + referenced.getJavaClassName("Record") + "> result = q.getResult().getRecords();");
    				out.println("\t\treturn result.size() == 1 ? result.get(0) : null;");

    				out.println("\t}");

    				out.printImport(tablePackage + "." + referenced.getJavaClassName());
    				out.printImport(SimpleSelectQuery.class);
    				out.printImport(SQLException.class);
    				out.printImport(List.class);
                }
			}
		}

		out.printImport(tablePackage + "." + table.getJavaClassName());
		out.printImport(column.getTypeClass());
	}

	private void printColumn(GenerationWriter out, ColumnDefinition column, TableDefinition table) throws SQLException {
		Class<?> declaredMemberClass = TableField.class;
		Class<?> concreteMemberClass = TableFieldImpl.class;

		printColumnDefinition(out, column, table, declaredMemberClass, concreteMemberClass);
	}

	private void printParameter(GenerationWriter out, ColumnDefinition parameter, Definition proc) throws SQLException {
		printColumnDefinition(out, parameter, proc, Parameter.class, ParameterImpl.class);
	}

	private void printColumnDefinition(GenerationWriter out, ColumnDefinition column, Definition table, Class<?> declaredMemberClass, Class<?> concreteMemberClass) throws SQLException {
		String concreteMemberType = concreteMemberClass.getSimpleName();
		String declaredMemberType = declaredMemberClass.getSimpleName();

		String columnDisambiguationSuffix = column.getNameUC().equals(table.getNameUC()) ? "_" : "";
		printFieldJavaDoc(out, columnDisambiguationSuffix, column);

		String genericPrefix = "<";
		if (table instanceof TableDefinition) {
			genericPrefix += table.getJavaClassName("Record") + ", ";
		}

		out.println("\tpublic static final " + declaredMemberType +
				genericPrefix + column.getType() + "> " +
				column.getNameUC() + columnDisambiguationSuffix +
				" = new " + concreteMemberType
				+ genericPrefix + column.getType() + ">(SQLDialect."
				+ column.getDatabase().getDialect().name()
				+ ", \"" + column.getName() + "\", " +
				column.getType() + ".class" +
				(table instanceof TableDefinition ? ", " + table.getNameUC() : "") +
				");");
		out.printImport(SQLDialect.class);
		out.printImport(declaredMemberClass);
		out.printImport(concreteMemberClass);
		out.printImport(column.getTypeClass());
	}

	private void printSerial(GenerationWriter out) {
		out.println();
		out.println("\tprivate static final long serialVersionUID = 1L;");
	}

	private void printFieldJavaDoc(GenerationWriter out, String disambiguationSuffix, ColumnDefinition column) throws SQLException {
		PrimaryKeyDefinition primaryKey = column.getPrimaryKey();
		ForeignKeyDefinition foreignKey = column.getForeignKey();

		out.println();
		out.println("\t/**");

		String comment = column.getComment();

		if (comment != null && comment.length() > 0) {
			out.println("\t * " + comment);
		} else {
			out.println("\t * An uncommented item");
		}

		if (column.getTypeClass() == Object.class) {
		    out.println("\t * ");
		    out.println("\t * The SQL type of this item could not be mapped. Deserialising this field might not work!");
		}

		if (primaryKey != null) {
		    out.println("\t * ");
		    out.print("\t * PRIMARY KEY");
		    out.println();
		}

		if (foreignKey != null) {
		    out.println("\t * ");
		    out.print("\t * FOREIGN KEY ");
		    out.print(foreignKey.getKeyColumnNames().toString());
		    out.print(" REFERENCES ");
		    out.print(foreignKey.getReferencedTableName());
		    out.print(" ");
		    out.print(foreignKey.getReferencedColumnNames().toString());
		    out.println();
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
