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

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.EnumType;
import org.jooq.Field;
import org.jooq.MasterDataType;
import org.jooq.Parameter;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.SimpleSelectQuery;
import org.jooq.TableField;
import org.jooq.UDTField;
import org.jooq.impl.FieldTypeHelper;
import org.jooq.impl.JooqLogger;
import org.jooq.impl.ParameterImpl;
import org.jooq.impl.SchemaImpl;
import org.jooq.impl.StoredFunctionImpl;
import org.jooq.impl.StoredProcedureImpl;
import org.jooq.impl.StringUtils;
import org.jooq.impl.TableFieldImpl;
import org.jooq.impl.TableImpl;
import org.jooq.impl.TableRecordImpl;
import org.jooq.impl.UDTFieldImpl;
import org.jooq.impl.UDTImpl;
import org.jooq.impl.UDTRecordImpl;
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

    private static final JooqLogger log = JooqLogger.getLogger(DefaultGenerator.class);

	@Override
	public void generate(Database database) throws SQLException, IOException {
	    log.info("Starting code generation on database. Database parameters:");
	    log.info("----------------------------------------------------------");
	    log.info("  dialect        : " + database.getDialect());
	    log.info("  schema         : " + database.getSchemaName());
	    log.info("  target dir     : " + database.getTargetDirectory());
	    log.info("  target package : " + database.getTargetPackage());
	    log.info("----------------------------------------------------------");

		String targetDirectory = database.getTargetDirectory();
        String targetPackage = database.getTargetPackage();

        File targetPackageDir = new File(targetDirectory + File.separator + targetPackage.replace('.', File.separatorChar));

		// ----------------------------------------------------------------------
		// Initialising
		// ----------------------------------------------------------------------
		log.info("Emptying " + targetPackageDir.getCanonicalPath());
		empty(targetPackageDir);

		// ----------------------------------------------------------------------
		// Generating schemas
		// ----------------------------------------------------------------------
		log.info("Generating classes in " + targetPackageDir.getCanonicalPath());
		SchemaDefinition schema = database.getSchema();
		{
			targetPackageDir.mkdirs();

			log.info("Generating schema " + schema.getName() + " into " + schema.getFileName());

			GenerationWriter out = new GenerationWriter(new PrintWriter(new File(targetPackageDir, schema.getFileName())));
			printHeader(out, targetPackage);
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
        // Generating master data tables
        // ----------------------------------------------------------------------
        File targetMasterDataTablePackageDir = new File(targetPackageDir, "enums");
        log.info("Generating classes in " + targetMasterDataTablePackageDir.getCanonicalPath());

        for (MasterDataTableDefinition table : database.getMasterDataTables()) {
            targetMasterDataTablePackageDir.mkdirs();

            log.info("Generating master data table " + table.getName() + " into " + table.getFileName());

            GenerationWriter out = new GenerationWriter(new PrintWriter(new File(targetMasterDataTablePackageDir, table.getFileName())));
            printHeader(out, targetPackage + ".enums");
            printClassJavadoc(out, table);

            ColumnDefinition primaryKey = table.getPrimaryKeyColumn();
            out.println("public enum " + table.getJavaClassName() + " implements MasterDataType<" + primaryKey.getType() + "> {");
            out.printImport(MasterDataType.class);
            out.printImport(primaryKey.getTypeClass());

            for (Record record : table.getData()) {
                String literal = String.valueOf(record.getValue(table.getLiteralColumn().getField()));
                ColumnDefinition descriptionColumn = table.getDescriptionColumn();

                if (descriptionColumn != null) {
                    String description = String.valueOf(record.getValue(descriptionColumn.getField()));

                    if (!StringUtils.isEmpty(description)) {
                        out.println();
                        out.println("\t/**");
                        out.println("\t * " + description);
                        out.println("\t */");
                    }
                }

                out.print("\t");
                out.print(GenerationUtil.convertToJavaIdentifier(literal));
                out.print("(");

                String separator = "";
                for (Field<?> field : record.getFields()) {
                    out.print(separator);
                    out.print(FieldTypeHelper.toJava(database.getDialect(), record.getValue(field), field));

                    separator = ", ";
                }

                out.println("),");
            }

            out.println("\t;");
            out.println();

            // Fields
            for (ColumnDefinition column : table.getColumns()) {
                out.print("\tprivate final ");
                out.print(column.getType());
                out.print(" ");
                out.println(column.getJavaClassNameLC() + ";");
                out.printImport(column.getTypeClass());
            }

            // Constructor
            out.println();
            out.print("\tprivate " + table.getJavaClassName() + "(");

            String separator = "";
            for (ColumnDefinition column : table.getColumns()) {
                out.print(separator);
                out.print(column.getType());
                out.print(" ");
                out.print(column.getJavaClassNameLC());

                separator = ", ";
            }

            out.println(") {");
            for (ColumnDefinition column : table.getColumns()) {
                out.print("\t\tthis.");
                out.print(column.getJavaClassNameLC());
                out.print(" = ");
                out.print(column.getJavaClassNameLC());
                out.println(";");
            }
            out.println("\t}");

            // Implementation methods
            out.println();
            printOverride(out);
            out.println("\tpublic " + primaryKey.getType() + " getPrimaryKey() {");
            out.println("\t\treturn " + primaryKey.getJavaClassNameLC() + ";");
            out.println("\t}");

            // Getters
            for (ColumnDefinition column : table.getColumns()) {
                printFieldJavaDoc(out, "", column);
                out.print("\tpublic final ");
                out.print(column.getType());
                out.print(" get");
                out.print(column.getJavaClassName());
                out.println("() {");
                out.print("\t\treturn ");
                out.print(column.getJavaClassNameLC());
                out.println(";");
                out.println("\t}");
            }

            out.println("}");
            out.close();
        }

		// ----------------------------------------------------------------------
		// Generating tables
		// ----------------------------------------------------------------------
		File targetTablePackageDir = new File(targetPackageDir, "tables");
		log.info("Generating classes in " + targetTablePackageDir.getCanonicalPath());

		for (TableDefinition table : database.getTables()) {
			targetTablePackageDir.mkdirs();

			log.info("Generating table " + table.getName() + " into " + table.getFileName());

			GenerationWriter out = new GenerationWriter(new PrintWriter(new File(targetTablePackageDir, table.getFileName())));
			printHeader(out, targetPackage + ".tables");
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
			printSingletonInstance(table, out);
			printRecordTypeMethod(targetPackage, table, out);

			for (ColumnDefinition column : table.getColumns()) {
				printTableColumn(out, column, table);
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

						for (ColumnDefinition c : primaryKey.getKeyColumns()) {
							String statement = table.getNameUC() + ".addToPrimaryKey(" + c.getName().toUpperCase() + ");";
							out.printStaticInitialisationStatement(statement);
						}
					}
				}
			}

			out.println("\t}");
			out.printImport(targetPackage + "." + schema.getJavaClassName());

			out.printStaticInitialisationStatementsPlaceholder();
			out.println("}");
			out.close();
		}

		// ----------------------------------------------------------------------
		// Generating table records
		// ----------------------------------------------------------------------
		File targetTableRecordPackageDir = new File(new File(targetPackageDir, "tables"), "records");
		log.info("Generating classes in " + targetTableRecordPackageDir.getCanonicalPath());

		for (TableDefinition table : database.getTables()) {
			targetTableRecordPackageDir.mkdirs();

			log.info("Generating table " + table.getName() + " into " + table.getFileName("Record"));

			GenerationWriter out = new GenerationWriter(new PrintWriter(new File(targetTableRecordPackageDir, table.getFileName("Record"))));
			printHeader(out, targetPackage + ".tables.records");
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
				printGetterAndSetter(out, column, table, targetPackage + ".tables");
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
        // Generating UDTs
        // ----------------------------------------------------------------------
        File targetUDTPackageDir = new File(targetPackageDir, "udt");
        log.info("Generating classes in " + targetUDTPackageDir.getCanonicalPath());

        for (UDTDefinition udt : database.getUDTs()) {
            if (!udt.isReferenced()) {
                continue;
            }

            targetUDTPackageDir.mkdirs();

            log.info("Generating udt " + udt.getName() + " into " + udt.getFileName());

            GenerationWriter out = new GenerationWriter(new PrintWriter(new File(targetUDTPackageDir, udt.getFileName())));
            printHeader(out, targetPackage + ".udt");
            printClassJavadoc(out, udt);

            out.println("public class " + udt.getJavaClassName() + " extends UDTImpl<" + udt.getJavaClassName("Record") + "> {");
            out.printImport(UDTImpl.class);
            printSerial(out);
            printSingletonInstance(udt, out);
            printRecordTypeMethod(targetPackage, udt, out);

            for (ColumnDefinition column : udt.getColumns()) {
                printUDTColumn(out, column, udt);
            }

            out.println();
            printNoFurtherInstancesAllowedJavadoc(out);
            out.println("\tprivate " + udt.getJavaClassName() + "() {");
            out.printImport(SQLDialect.class);
            out.println("\t\tsuper(SQLDialect." + database.getDialect().name() + ", \"" + udt.getName() + "\", " + schema.getJavaClassName() + "." + schema.getNameUC() + ");");
            out.println("\t}");
            out.printImport(targetPackage + "." + schema.getJavaClassName());

            out.println("}");
            out.close();
        }

        // ----------------------------------------------------------------------
        // Generating UDT record classes
        // ----------------------------------------------------------------------
        File targetRecordUDTPackageDir = new File(new File(targetPackageDir, "udt"), "records");
        log.info("Generating classes in " + targetRecordUDTPackageDir.getCanonicalPath());

        for (UDTDefinition udt : database.getUDTs()) {
            if (!udt.isReferenced()) {
                continue;
            }

            targetRecordUDTPackageDir.mkdirs();

            log.info("Generating udt " + udt.getName() + " into " + udt.getFileName("Record"));

            GenerationWriter out = new GenerationWriter(new PrintWriter(new File(targetRecordUDTPackageDir, udt.getFileName("Record"))));
            printHeader(out, targetPackage + ".udt.records");
            printClassJavadoc(out, udt);

            out.println("public class " + udt.getJavaClassName("Record") + " extends UDTRecordImpl<" + udt.getJavaClassName("Record") + "> {");
            out.printImport(UDTRecordImpl.class);
            printSerial(out);
            out.println();

            for (ColumnDefinition column : udt.getColumns()) {
                printGetterAndSetter(out, column, udt, targetPackage + ".udt");
            }

            out.println();
            out.println("\tpublic " + udt.getJavaClassName("Record") + "() {");

            out.print("\t\tsuper(");
            out.print(udt.getJavaClassName());
            out.print(".");
            out.print(udt.getNameUC());
            out.println(");");

            out.println("\t}");
            out.println("}");
            out.close();
        }

        // ----------------------------------------------------------------------
        // Generating enums
        // ----------------------------------------------------------------------
        File targetEnumPackageDir = new File(targetPackageDir, "enums");
        log.info("Generating classes in " + targetEnumPackageDir.getCanonicalPath());

        for (EnumDefinition e : database.getEnums()) {
            if (!e.isReferenced()) {
                continue;
            }

            targetEnumPackageDir.mkdirs();

            log.info("Generating enum " + e.getName() + " into " + e.getFileName());

            GenerationWriter out = new GenerationWriter(new PrintWriter(new File(targetEnumPackageDir, e.getFileName())));
            printHeader(out, targetPackage + ".enums");
            printClassJavadoc(out, e);

            out.println("public enum " + e.getJavaClassName() + " implements EnumType {");
            out.printImport(EnumType.class);
            out.println();

            for (String literal : e.getLiterals()) {
                out.println("\t" + GenerationUtil.convertToJavaIdentifier(literal) + "(\"" + literal + "\"),");
                out.println();
            }

            out.println("\t;");
            out.println();
            out.println("\tprivate final String literal;");
            out.println();
            out.println("\tprivate " + e.getJavaClassName() + "(String literal) {");
            out.println("\t\tthis.literal = literal;");
            out.println("\t}");
            out.println();
            out.println("\t@Override");
            out.println("\tpublic String getName() {");
            out.println("\t\treturn \"" + e.getName() + "\";");
            out.println("\t}");
            out.println();
            out.println("\t@Override");
            out.println("\tpublic String getLiteral() {");
            out.println("\t\treturn literal;");
            out.println("\t}");

            out.println("}");

            out.close();
        }

		// ----------------------------------------------------------------------
		// Generating stored procedures
		// ----------------------------------------------------------------------
		File targetProcedurePackageDir = new File(targetPackageDir, "procedures");

		log.info("Generating classes in " + targetProcedurePackageDir.getCanonicalPath());
		for (ProcedureDefinition procedure : database.getProcedures()) {
			targetProcedurePackageDir.mkdirs();

			log.info("Generating procedure " + procedure.getName() + " into " + procedure.getFileName());

			GenerationWriter out = new GenerationWriter(new PrintWriter(new File(targetProcedurePackageDir, procedure.getFileName())));
			printHeader(out, targetPackage + ".procedures");
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

		log.info("Generating classes in " + targetFunctionPackageDir.getCanonicalPath());
		for (FunctionDefinition function : database.getFunctions()) {
			targetFunctionPackageDir.mkdirs();

			log.info("Generating function " + function.getName() + " into " + function.getFileName());

			GenerationWriter out = new GenerationWriter(new PrintWriter(new File(targetFunctionPackageDir, function.getFileName())));
			printHeader(out, targetPackage + ".functions");
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

    private void printRecordTypeMethod(String targetPackage, Definition definition, GenerationWriter out) {
        out.println();
        out.println("\t/**");
        out.println("\t * The class holding records for this type");
        out.println("\t */");
        out.println("\tprivate static final Class<" + definition.getJavaClassName("Record") + "> __RECORD_TYPE = " + definition.getJavaClassName("Record") + ".class;");
        out.println();
        out.println("\t/**");
        out.println("\t * The class holding records for this type");
        out.println("\t */");
        printOverride(out);
        out.println("\tpublic Class<" + definition.getJavaClassName("Record") + "> getRecordType() {");
        out.println("\t\treturn __RECORD_TYPE;");
        out.println("\t}");
        out.printImport(targetPackage + "." + definition.getSubPackage() + ".records." + definition.getJavaClassName("Record"));
    }

    private void printSingletonInstance(Definition definition, GenerationWriter out) {
        out.println();
        out.println("\t/**");
        out.println("\t * The singleton instance of " + definition.getName());
        out.println("\t */");
        out.println("\tpublic static final " + definition.getJavaClassName() + " " + definition.getNameUC() + " = new " + definition.getJavaClassName() + "();");
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

	private void printGetterAndSetter(GenerationWriter out, ColumnDefinition column, Definition udt, String tablePackage) throws SQLException {
		String columnDisambiguationSuffix = column.getNameUC().equals(udt.getNameUC()) ? "_" : "";

		printFieldJavaDoc(out, null, column);
		out.println("\tpublic void set" + column.getJavaClassName() + "(" + column.getType() + " value) {");
		out.println("\t\tsetValue(" + udt.getJavaClassName() + "." + column.getNameUC() + columnDisambiguationSuffix + ", value);");
		out.println("\t}");
		printFieldJavaDoc(out, null, column);
		out.println("\tpublic " + column.getType() + " get" + column.getJavaClassName() + "() {");
		out.println("\t\treturn getValue(" + udt.getJavaClassName() + "." + column.getNameUC() + columnDisambiguationSuffix + ");");
		out.println("\t}");

		if (column.getDatabase().generateRelations()) {
			PrimaryKeyDefinition primaryKey = column.getPrimaryKey();
			if (primaryKey != null && out.printOnlyOnce(primaryKey)) {
				foreignKeyLoop: for (ForeignKeyDefinition foreignKey : primaryKey.getForeignKeys()) {

				    // #64 - If the foreign key does not match the referenced key, it
				    // is most likely because it references a non-primary unique key
				    // Skip code generation for this foreign key

				    // #69 - Should resolve this issue more thoroughly.
				    if (foreignKey.getReferencedColumns().size() != foreignKey.getKeyColumns().size()) {
	                    log.warn("Foreign key " + foreignKey.getName() + " does not match its primary key!");
	                    log.warn("No code is generated for this key. See trac tickets #64 and #69");

	                    continue foreignKeyLoop;
				    }

				    // #71 - If the foreign key type does not match the referenced
				    // key type, generating this code would produce compilation
				    // errors. Skip code generation for this foreign key

				    // #73 - This is also an acceptable workaround for another issue.
				    for (int i = 0; i < foreignKey.getReferencedColumns().size(); i++) {
				        String foreignType = foreignKey.getKeyColumns().get(i).getTypeClass();
				        String primaryType = primaryKey.getKeyColumns().get(i).getTypeClass();

				        if (!foreignType.equals(primaryType)) {
				            log.warn("Foreign key " + foreignKey.getName() + " does not match its primary key type!");
	                        log.warn("No code is generated for this key. See trac tickets #71 and #73");

				            continue foreignKeyLoop;
				        }
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

					for (int i = 0; i < foreignKey.getReferencedColumns().size(); i++) {
						out.print("\t\tq.addCompareCondition(");
						out.print(referencing.getJavaClassName());
						out.print(".");
						out.print(foreignKey.getKeyColumns().get(i).getName().toUpperCase());
						out.print(", getValue(");
						out.print(udt.getJavaClassName());
						out.print(".");
						out.print(primaryKey.getKeyColumns().get(i).getName().toUpperCase());
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
			    boolean skipGeneration = false;

                // #64 - If the foreign key does not match the referenced key, it
                // is most likely because it references a non-primary unique key
                // Skip code generation for this foreign key

                // #69 - Should resolve this issue more thoroughly.
                if (foreignKey.getReferencedColumns().size() != foreignKey.getKeyColumns().size()) {
                    log.warn("Foreign key " + foreignKey.getName() + " does not match its primary key!");
                    log.warn("No code is generated for this key. See trac tickets #64 and #69");

                    skipGeneration = true;
                }

                // #71 - If the foreign key type does not match the referenced
                // key type, generating this code would produce compilation
                // errors. Skip code generation for this foreign key

                // #73 - This is also an acceptable workaround for another issue.
                if (!skipGeneration) {
                    for (int i = 0; i < foreignKey.getReferencedColumns().size(); i++) {
                        String foreignType = foreignKey.getKeyColumns().get(i).getTypeClass();
                        String primaryType = foreignKey.getReferencedColumns().get(i).getTypeClass();

                        if (!foreignType.equals(primaryType)) {
                            log.warn("Foreign key " + foreignKey.getName() + " does not match its primary key type!");
                            log.warn("No code is generated for this key. See trac tickets #71 and #73");

                            skipGeneration = true;
                        }
                    }
                }

                // Do not generate referential code for master data tables
                TableDefinition referenced = foreignKey.getReferencedTableDefinition();
                if (referenced instanceof MasterDataTableDefinition) {
                    skipGeneration = true;
                }

                if (!skipGeneration) {
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

    				for (int i = 0; i < foreignKey.getReferencedColumns().size(); i++) {
    					out.print("\t\tq.addCompareCondition(");
    					out.print(referenced.getJavaClassName());
    					out.print(".");
    					out.print(foreignKey.getReferencedColumns().get(i).getName().toUpperCase());
    					out.print(", getValue(");
    					out.print(udt.getJavaClassName());
    					out.print(".");
    					out.print(foreignKey.getKeyColumns().get(i).getName().toUpperCase());
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

		out.printImport(tablePackage + "." + udt.getJavaClassName());
		out.printImport(column.getTypeClass());
	}

    private void printUDTColumn(GenerationWriter out, ColumnDefinition column, Definition table) throws SQLException {
        Class<?> declaredMemberClass = UDTField.class;
        Class<?> concreteMemberClass = UDTFieldImpl.class;

        printColumnDefinition(out, column, table, declaredMemberClass, concreteMemberClass);
    }

    private void printTableColumn(GenerationWriter out, ColumnDefinition column, Definition table) throws SQLException {
        Class<?> declaredMemberClass = TableField.class;
        Class<?> concreteMemberClass = TableFieldImpl.class;

        printColumnDefinition(out, column, table, declaredMemberClass, concreteMemberClass);
    }

	private void printParameter(GenerationWriter out, ColumnDefinition parameter, Definition proc) throws SQLException {
		printColumnDefinition(out, parameter, proc, Parameter.class, ParameterImpl.class);
	}

	private void printColumnDefinition(GenerationWriter out, ColumnDefinition column, Definition type, Class<?> declaredMemberClass, Class<?> concreteMemberClass) throws SQLException {
		String concreteMemberType = concreteMemberClass.getSimpleName();
		String declaredMemberType = declaredMemberClass.getSimpleName();

		String columnDisambiguationSuffix = column.getNameUC().equals(type.getNameUC()) ? "_" : "";
		printFieldJavaDoc(out, columnDisambiguationSuffix, column);

		String genericPrefix = "<";
		boolean hasType =
		    type instanceof TableDefinition ||
		    type instanceof UDTDefinition;

        if (hasType) {
			genericPrefix += type.getJavaClassName("Record") + ", ";
		}

		out.println("\tpublic static final " + declaredMemberType +
				genericPrefix + column.getType() + "> " +
				column.getNameUC() + columnDisambiguationSuffix +
				" = new " + concreteMemberType
				+ genericPrefix + column.getType() + ">(SQLDialect."
				+ column.getDatabase().getDialect().name()
				+ ", \"" + column.getName() + "\", " +
				column.getType() + ".class" +
				(hasType ? ", " + type.getNameUC() : "") +
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

		if (column.getTypeClass().equals("java.lang.Object")) {
		    log.warn("Could not map column to a type : " + column.getQualifiedName());
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
		    out.print(foreignKey.getKeyColumns().toString());
		    out.print(" REFERENCES ");
		    out.print(foreignKey.getReferencedTableName());
		    out.print(" ");
		    out.print(foreignKey.getReferencedColumns().toString());
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
		out.println(
		    "@Generated(value    = \"http://jooq.sourceforge.net\",\n" +
		    "           comments = \"This class is generated by jOOQ\")");

		out.printImport(Generated.class);
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
}
