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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.EnumType;
import org.jooq.Field;
import org.jooq.MasterDataType;
import org.jooq.Parameter;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.Sequence;
import org.jooq.SimpleSelectQuery;
import org.jooq.TableField;
import org.jooq.UDTField;
import org.jooq.impl.ArrayRecordImpl;
import org.jooq.impl.JooqLogger;
import org.jooq.impl.ParameterImpl;
import org.jooq.impl.SchemaImpl;
import org.jooq.impl.SequenceImpl;
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
import org.jooq.util.postgres.PostgresSingleUDTOutParameterProcedure;


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

    private static final JooqLogger                 log             = JooqLogger.getLogger(DefaultGenerator.class);
    private static final Map<Class<?>, Set<String>> reservedColumns = new HashMap<Class<?>, Set<String>>();

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
		// XXX Initialising
		// ----------------------------------------------------------------------
		log.info("Emptying " + targetPackageDir.getCanonicalPath());
		empty(targetPackageDir);

		// ----------------------------------------------------------------------
		// XXX Generating schemas
		// ----------------------------------------------------------------------
		log.info("Generating classes in " + targetPackageDir.getCanonicalPath());
		SchemaDefinition schema = database.getSchema();
		GenerationWriter outS = null;
		if (!schema.isDefaultSchema()) {
			targetPackageDir.mkdirs();

			log.info("Generating schema " + schema.getName() + " into " + schema.getFileName());

			outS = new GenerationWriter(new PrintWriter(new File(targetPackageDir, schema.getFileName())));
			printHeader(outS, targetPackage);
			printClassJavadoc(outS, schema);

			outS.println("public class " + schema.getJavaClassName() + " extends SchemaImpl {");
			outS.printSerial();
			outS.printImport(SchemaImpl.class);
			outS.println();
			outS.println("\t/**");
			outS.println("\t * The singleton instance of " + schema.getName());
			outS.println("\t */");
			outS.println("\tpublic static final " + schema.getJavaClassName() + " " + schema.getNameUC() + " = new " + schema.getJavaClassName() + "();");

			outS.println();
			printNoFurtherInstancesAllowedJavadoc(outS);
			outS.printImport(SQLDialect.class);
			outS.println("\tprivate " + schema.getJavaClassName() + "() {");
			outS.println("\t\tsuper(SQLDialect." + database.getDialect().name() + ", \"" + schema.getName() + "\");");
			outS.println("\t}");

			outS.printInitialisationStatementsPlaceholder();
			outS.println("}");
		}

        // ----------------------------------------------------------------------
        // XXX Generating sequences
        // ----------------------------------------------------------------------
		log.info("Generating sequences in " + targetPackageDir.getCanonicalPath());

		if (database.getSequences().size() > 0) {
		    targetPackageDir.mkdirs();

            GenerationWriter out = new GenerationWriter(new PrintWriter(new File(targetPackageDir, "Sequences.java")));
            printHeader(out, targetPackage);
            printClassJavadoc(out, "Convenience access to all sequences in " + schema.getName());
            out.println("public final class Sequences {");

            for (SequenceDefinition sequence : database.getSequences()) {
                out.println();
                out.println("\t/**");
                out.println("\t * The sequence " + sequence.getNameUC());
                out.println("\t */");

                out.print("\tpublic static final Sequence ");
                out.print(sequence.getNameUC());
                out.print(" = new SequenceImpl(SQLDialect.");
                out.print(database.getDialect().name());
                out.print(", \"");
                out.print(sequence.getName());
                out.print("\"");

                if (!schema.isDefaultSchema()) {
                    out.print(", ");
                    out.print(schema.getJavaClassName() + "." + schema.getNameUC());
                    out.printImport(schema.getFullJavaClassName());
                } else {
                    out.print(", null");
                }

                out.println(");");
                out.printImport(SQLDialect.class);
                out.printImport(Sequence.class);
                out.printImport(SequenceImpl.class);
            }

            printPrivateConstructor(out, "Sequences");
            out.println("}");
            out.close();
		}

        // ----------------------------------------------------------------------
        // XXX Generating master data tables
        // ----------------------------------------------------------------------
        File targetMasterDataTablePackageDir = new File(targetPackageDir, "enums");
        log.info("Generating classes in " + targetMasterDataTablePackageDir.getCanonicalPath());

        for (MasterDataTableDefinition table : database.getMasterDataTables()) {
            try {
                targetMasterDataTablePackageDir.mkdirs();

                log.info("Generating master data table " + table.getName() + " into " + table.getFileName());

                GenerationWriter out = new GenerationWriter(new PrintWriter(new File(targetMasterDataTablePackageDir, table.getFileName())));
                printHeader(out, targetPackage + ".enums");
                printClassJavadoc(out, table);

                ColumnDefinition primaryKey = table.getPrimaryKeyColumn();
                out.print("public enum ");
                out.print(table.getJavaClassName());
                out.print(" implements MasterDataType<");
                out.print(primaryKey.getType());
                out.println("> {");
                out.printImport(MasterDataType.class);

                for (Record record : table.getData()) {
                    String literal = String.valueOf(record.getValue(table.getLiteralColumn().getName()));
                    ColumnDefinition descriptionColumn = table.getDescriptionColumn();

                    if (descriptionColumn != null) {
                        String description = String.valueOf(record.getValue(descriptionColumn.getName()));

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
                        out.printNewJavaObject(record.getValue(field), field);

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
                out.print("\tpublic ");
                out.print(primaryKey.getType());
                out.println(" getPrimaryKey() {");
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
            } catch (Exception e) {
                log.error("Exception while generating master data table " + table, e);
            }
        }

		// ----------------------------------------------------------------------
		// XXX Generating tables
		// ----------------------------------------------------------------------
		File targetTablePackageDir = new File(targetPackageDir, "tables");
		log.info("Generating classes in " + targetTablePackageDir.getCanonicalPath());

		for (TableDefinition table : database.getTables()) {
		    try {
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
    			out.printSerial();
    			printSingletonInstance(table, out);
    			printRecordTypeMethod(targetPackage, table, out);

    			for (ColumnDefinition column : table.getColumns()) {
    				printTableColumn(out, column, table);
    			}

    			out.println();
    			printNoFurtherInstancesAllowedJavadoc(out);
    			out.println("\tprivate " + table.getJavaClassName() + "() {");
    			out.printImport(SQLDialect.class);

    			if (!schema.isDefaultSchema()) {
    			    out.println("\t\tsuper(SQLDialect." + database.getDialect().name() + ", \"" + table.getName() + "\", " + schema.getJavaClassName() + "." + schema.getNameUC() + ");");
                    out.printImport(schema.getFullJavaClassName());
    			} else {
    			    out.println("\t\tsuper(SQLDialect." + database.getDialect().name() + ", \"" + table.getName() + "\");");
    			}

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

    			out.printStaticInitialisationStatementsPlaceholder();
    			out.println("}");
    			out.close();
		    } catch (Exception e) {
		        log.error("Error while generating table" + table, e);
		    }
		}

		// ----------------------------------------------------------------------
		// XXX Generating table records
		// ----------------------------------------------------------------------
		File targetTableRecordPackageDir = new File(new File(targetPackageDir, "tables"), "records");
		log.info("Generating classes in " + targetTableRecordPackageDir.getCanonicalPath());

		for (TableDefinition table : database.getTables()) {
		    try {
    			targetTableRecordPackageDir.mkdirs();

    			log.info("Generating table " + table.getName() + " into " + table.getFileName("Record"));

    			GenerationWriter out = new GenerationWriter(new PrintWriter(new File(targetTableRecordPackageDir, table.getFileName("Record"))));
    			printHeader(out, targetPackage + ".tables.records");
    			printClassJavadoc(out, table);

    			String baseClass;

    			Set<String> reserved;
    			if (database.generateRelations() && table.hasPrimaryKey()) {
    				baseClass = "UpdatableRecordImpl";
    				out.printImport(UpdatableRecordImpl.class);
                    reserved = reservedColumns(UpdatableRecordImpl.class);
    			} else {
    				baseClass = "TableRecordImpl";
    				out.printImport(TableRecordImpl.class);
    				reserved = reservedColumns(TableRecordImpl.class);
    			}

    			out.println("public class " + table.getJavaClassName("Record") + " extends " + baseClass + "<"  + table.getJavaClassName("Record") + "> {");
    			out.printSerial();

    			out.printImport(table.getFullJavaClassName());
    			for (ColumnDefinition column : table.getColumns()) {
    				printGetterAndSetter(out, column, table, targetPackage + ".tables", reserved);
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
		    } catch (Exception e) {
		        log.error("Error while generating table record " + table, e);
		    }
		}

        // ----------------------------------------------------------------------
        // XXX Generating UDTs
        // ----------------------------------------------------------------------
        File targetUDTPackageDir = new File(targetPackageDir, "udt");
        log.info("Generating classes in " + targetUDTPackageDir.getCanonicalPath());

        for (UDTDefinition udt : database.getUDTs()) {
            try {
                if (!udt.isReferenced()) {
                    log.info("Skipping unreferenced UDT " + udt);
                    continue;
                }

                targetUDTPackageDir.mkdirs();

                log.info("Generating udt " + udt.getName() + " into " + udt.getFileName());

                GenerationWriter out = new GenerationWriter(new PrintWriter(new File(targetUDTPackageDir, udt.getFileName())));
                printHeader(out, targetPackage + ".udt");
                printClassJavadoc(out, udt);

                out.println("public class " + udt.getJavaClassName() + " extends UDTImpl<" + udt.getJavaClassName("Record") + "> {");
                out.printImport(UDTImpl.class);
                out.printSerial();
                printSingletonInstance(udt, out);
                printRecordTypeMethod(targetPackage, udt, out);

                for (ColumnDefinition column : udt.getColumns()) {
                    printUDTColumn(out, column, udt);
                }

                out.println();
                printNoFurtherInstancesAllowedJavadoc(out);
                out.println("\tprivate " + udt.getJavaClassName() + "() {");
                out.printImport(SQLDialect.class);

                if (!schema.isDefaultSchema()) {
                    out.println("\t\tsuper(SQLDialect." + database.getDialect().name() + ", \"" + udt.getName() + "\", " + schema.getJavaClassName() + "." + schema.getNameUC() + ");");
                    out.printImport(targetPackage + "." + schema.getJavaClassName());
                } else {
                    out.println("\t\tsuper(SQLDialect." + database.getDialect().name() + ", \"" + udt.getName() + "\");");
                }

                out.println("\t}");

                out.println("}");
                out.close();

                if (outS != null) {
                    outS.printInitialisationStatement(
                        "addMapping(\"" + schema.getName() + "." + udt.getName() + "\", " +
                        udt.getJavaClassName("Record") + ".class);");
                    outS.printImport(udt.getFullJavaClassName("Record"));
                }
            } catch (Exception e) {
                log.error("Error while generating udt " + udt, e);
            }
        }

        // ----------------------------------------------------------------------
        // XXX Generating UDT record classes
        // ----------------------------------------------------------------------
        File targetRecordUDTPackageDir = new File(new File(targetPackageDir, "udt"), "records");
        log.info("Generating classes in " + targetRecordUDTPackageDir.getCanonicalPath());

        for (UDTDefinition udt : database.getUDTs()) {
            try {
                if (!udt.isReferenced()) {
                    log.info("Skipping unreferenced UDT " + udt);
                    continue;
                }

                targetRecordUDTPackageDir.mkdirs();

                log.info("Generating udt " + udt.getName() + " into " + udt.getFileName("Record"));

                GenerationWriter out = new GenerationWriter(new PrintWriter(new File(targetRecordUDTPackageDir, udt.getFileName("Record"))));
                printHeader(out, targetPackage + ".udt.records");
                printClassJavadoc(out, udt);

                out.println("public class " + udt.getJavaClassName("Record") + " extends UDTRecordImpl<" + udt.getJavaClassName("Record") + "> {");
                out.printImport(UDTRecordImpl.class);
                Set<String> reserved = reservedColumns(UDTRecordImpl.class);
                out.printSerial();
                out.println();

                out.printImport(udt.getFullJavaClassName());
                for (ColumnDefinition column : udt.getColumns()) {
                    printGetterAndSetter(out, column, udt, targetPackage + ".udt", reserved);
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
            } catch (Exception e) {
                log.error("Error while generating UDT record " + udt, e);
            }
        }

        // ----------------------------------------------------------------------
        // XXX Generating ARRAY record classes
        // ----------------------------------------------------------------------
        File targetRecordARRAYPackageDir = new File(new File(targetPackageDir, "udt"), "records");
        log.info("Generating classes in " + targetRecordARRAYPackageDir.getCanonicalPath());

        for (ArrayDefinition array : database.getArrays()) {
            try {
                if (!array.isReferenced()) {
                    log.info("Skipping unreferenced ARRAY " + array);
                    continue;
                }

                targetRecordARRAYPackageDir.mkdirs();

                log.info("Generating array " + array.getName() + " into " + array.getFileName("Record"));

                GenerationWriter out = new GenerationWriter(new PrintWriter(new File(targetRecordARRAYPackageDir, array.getFileName("Record"))));
                printHeader(out, targetPackage + ".udt.records");
                printClassJavadoc(out, array);

                out.print("public class ");
                out.print(array.getJavaClassName("Record"));
                out.print(" extends ArrayRecordImpl<");
                out.print(array.getElementType());
                out.println("> {");
                out.printImport(ArrayRecordImpl.class);
                out.printImport(Configuration.class);
                out.printSerial();

                out.println();
                out.println("\tpublic " + array.getJavaClassName("Record") + "(Configuration configuration) {");
                out.print("\t\tsuper(configuration, \"");
                out.print(array.getSchemaName());
                out.print(".");
                out.print(array.getName());
                out.print("\", ");
                out.print(array.getElementType().getJavaTypeReference(out));
                out.println(");");
                out.println("\t}");

                out.println();
                out.print("\tpublic ");
                out.print(array.getJavaClassName("Record"));
                out.print("(Configuration configuration, ");
                out.print(array.getElementType());
                out.print("... array");
                out.println(") {");
                out.println("\t\tthis(configuration);");
                out.println("\t\tset(array);");
                out.println("\t}");

                out.println();
                out.print("\tpublic ");
                out.print(array.getJavaClassName("Record"));
                out.print("(Configuration configuration, List<? extends ");
                out.print(array.getElementType());
                out.print("> list");
                out.println(") {");
                out.println("\t\tthis(configuration);");
                out.println("\t\tsetList(list);");
                out.println("\t}");
                out.printImport(List.class);

                out.println("}");
                out.close();
            } catch (Exception e) {
                log.error("Error while generating ARRAY record " + array, e);
            }
        }

        // ----------------------------------------------------------------------
        // XXX Generating enums
        // ----------------------------------------------------------------------
        File targetEnumPackageDir = new File(targetPackageDir, "enums");
        log.info("Generating classes in " + targetEnumPackageDir.getCanonicalPath());

        for (EnumDefinition e : database.getEnums()) {
            try {
                if (!e.isReferenced()) {
                    log.info("Skipping unreferenced enum " + e);
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
            } catch (Exception ex) {
                log.error("Error while generating enum " + e, ex);
            }
        }

		// ----------------------------------------------------------------------
		// XXX Generating stored procedures
		// ----------------------------------------------------------------------
		File targetProcedurePackageDir = new File(targetPackageDir, "procedures");
		log.info("Generating classes in " + targetProcedurePackageDir.getCanonicalPath());

		if (database.getProcedures().size() > 0) {
    		GenerationWriter outP = new GenerationWriter(new PrintWriter(new File(targetPackageDir, "Procedures.java")));
    		printHeader(outP, targetPackage);
    		printClassJavadoc(outP, "Convenience access to all stored procedures in " + schema.getName());
    		outP.println("public final class Procedures {");
    		for (ProcedureDefinition procedure : database.getProcedures()) {
    		    try {
        			printProcedure(database, schema, procedure);

        			// Static execute() convenience method
        			printConvenienceMethodProcedure(outP, procedure);
    		    } catch (Exception e) {
    		        log.error("Error while generating procedure " + procedure, e);
    		    }
    		}

    		printPrivateConstructor(outP, "Procedures");
    		outP.println("}");
    		outP.close();
		}

		// ----------------------------------------------------------------------
		// XXX Generating stored functions
		// ----------------------------------------------------------------------
		File targetFunctionPackageDir = new File(targetPackageDir, "functions");
		log.info("Generating classes in " + targetFunctionPackageDir.getCanonicalPath());

		if (database.getFunctions().size() > 0) {
            GenerationWriter outF = new GenerationWriter(new PrintWriter(new File(targetPackageDir, "Functions.java")));
            printHeader(outF, targetPackage);
            printClassJavadoc(outF, "Convenience access to all stored functions in " + schema.getName());
            outF.println("public final class Functions {");

    		for (FunctionDefinition function : database.getFunctions()) {
    		    try {
        			printFunction(database, schema, function);

                    // Static execute() convenience method
                    printConvenienceMethodFunction(outF, function);

                    // Static asField() convenience method
                    printConvenienceMethodFunctionAsField(outF, function, false);
                    printConvenienceMethodFunctionAsField(outF, function, true);
    		    } catch (Exception e) {
    		        log.error("Error while generating function " + function, e);
    		    }
    		}

    		printPrivateConstructor(outF, "Functions");
            outF.println("}");
            outF.close();
		}

        // ----------------------------------------------------------------------
        // XXX Generating packages
        // ----------------------------------------------------------------------
		File targetPackagesPackageDir = new File(targetPackageDir, "packages");
        log.info("Generating classes in " + targetPackagesPackageDir.getCanonicalPath());

        for (PackageDefinition pkg : database.getPackages()) {
            try {
                File targetPackagePackageDir = new File(targetPackagesPackageDir, pkg.getNameLC());
                targetPackagePackageDir.mkdirs();

                for (ProcedureDefinition procedure : pkg.getProcedures()) {
                    try {
                        printProcedure(database, schema, procedure);
                    } catch (Exception e) {
                        log.error("Error while generating procedure " + procedure, e);
                    }
                }

                for (FunctionDefinition function : pkg.getFunctions()) {
                    try {
                        printFunction(database, schema, function);
                    } catch (Exception e) {
                        log.error("Error while generating function " + function, e);
                    }
                }

                // Static convenience methods
                GenerationWriter outPkg = new GenerationWriter(new PrintWriter(new File(targetPackagesPackageDir, pkg.getFileName())));
                printHeader(outPkg, targetPackage + ".packages");
                printClassJavadoc(outPkg, "Convenience access to all stored procedures and functions in " + pkg.getName());
                outPkg.println("public final class " + pkg.getJavaClassName() + " {");

                for (ProcedureDefinition procedure : pkg.getProcedures()) {
                    try {
                        printConvenienceMethodProcedure(outPkg, procedure);
                    } catch (Exception e) {
                        log.error("Error while generating procedure " + procedure, e);
                    }
                }

                for (FunctionDefinition function : pkg.getFunctions()) {
                    try {
                        printConvenienceMethodFunction(outPkg, function);
                        printConvenienceMethodFunctionAsField(outPkg, function, false);
                        printConvenienceMethodFunctionAsField(outPkg, function, true);
                    } catch (Exception e) {
                        log.error("Error while generating function " + function, e);
                    }
                }

                printPrivateConstructor(outPkg, pkg.getJavaClassName());
                outPkg.println("}");
                outPkg.close();
            } catch (Exception e) {
                log.error("Error while generating package " + pkg, e);
            }
        }

        // Finalise schema
        if (outS != null) {
            outS.close();
        }

        log.info("GENERATION FINISHED!");
	}

	/**
	 * Find all column names that are reserved because of the extended
	 * class hierarchy of a generated class
	 *
	 * @see https://sourceforge.net/apps/trac/jooq/ticket/182
	 */
    private Set<String> reservedColumns(Class<?> clazz) {
        if (clazz == null) {
            return Collections.emptySet();
        }

        Set<String> result = reservedColumns.get(clazz);

        if (result == null) {
            result = new HashSet<String>();
            reservedColumns.put(clazz, result);

            result.addAll(reservedColumns(clazz.getSuperclass()));
            for (Class<?> c : clazz.getInterfaces()) {
                result.addAll(reservedColumns(c));
            }

            for (Method m : clazz.getDeclaredMethods()) {
                String name = m.getName();

                if (name.startsWith("get") && m.getParameterTypes().length == 0) {
                    result.add(name.substring(3));
                }
            }
        }

        return result;
    }

    private void printProcedure(Database database, SchemaDefinition schema, ProcedureDefinition procedure)
        throws FileNotFoundException, SQLException {
        procedure.getFile().getParentFile().mkdirs();
        log.info("Generating procedure " + procedure.getName() + " into " + procedure.getFileName());

        GenerationWriter out = new GenerationWriter(new PrintWriter(procedure.getFile()));
        printHeader(out, procedure.getJavaPackageName());
        printClassJavadoc(out, procedure);

        Class<?> procedureClass = StoredProcedureImpl.class;
        if (database.getDialect() == SQLDialect.POSTGRES &&
            procedure.getOutParameters().size() == 1 &&
            procedure.getOutParameters().get(0).getType().isUDT()) {

            procedureClass = PostgresSingleUDTOutParameterProcedure.class;
        }

        out.println("public class " + procedure.getJavaClassName() + " extends " + procedureClass.getSimpleName() + " {");
        out.printSerial();
        out.printImport(procedureClass);
        out.println();


        for (ColumnDefinition parameter : procedure.getAllParameters()) {
        	printParameter(out, parameter, procedure);
        }

        out.println();
        printNoFurtherInstancesAllowedJavadoc(out);
        out.println("\tpublic " + procedure.getJavaClassName() + "() {");
        out.printImport(SQLDialect.class);
        out.printImport(schema.getFullJavaClassName());
        out.println("\t\tsuper(SQLDialect." + database.getDialect().name() + ", \"" +
            ((procedure.getPackage() != null) ? procedure.getPackage().getName() + "." : "") +
            procedure.getName() + "\", " +
            schema.getJavaClassName() + "." + schema.getNameUC() + ");");

        if (procedure.getAllParameters().size() > 0) {
            out.println();
        }

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

        if (procedure.getOverload() != null) {
            out.println("\t\tsetOverloaded(true);");
        }

        out.println("\t}");

        for (ColumnDefinition parameter : procedure.getInParameters()) {
        	out.println();
        	out.println("\tpublic void set" + parameter.getJavaClassName() + "(" + parameter.getType().getJavaSimpleType() + " value) {");
        	out.println("\t\tsetValue(" + parameter.getNameUC() + ", value);");
        	out.println("\t}");
        }

        for (ColumnDefinition parameter : procedure.getOutParameters()) {
        	out.println();
        	out.println("\tpublic " + parameter.getType().getJavaSimpleType() + " get" + parameter.getJavaClassName() + "() {");
        	out.println("\t\treturn getValue(" + parameter.getNameUC() + ");");
        	out.println("\t}");
        }

        out.println("}");
        out.close();
    }

    private void printFunction(Database database, SchemaDefinition schema, FunctionDefinition function)
        throws SQLException, FileNotFoundException {
        function.getFile().getParentFile().mkdirs();
        log.info("Generating function " + function.getName() + " into " + function.getFileName());

        GenerationWriter out = new GenerationWriter(new PrintWriter(function.getFile()));
        printHeader(out, function.getJavaPackageName());
        printClassJavadoc(out, function);

        out.print("public class ");
        out.print(function.getJavaClassName());
        out.print(" extends StoredFunctionImpl<");
        out.print(function.getReturnType());
        out.println("> {");
        out.printSerial();
        out.printImport(StoredFunctionImpl.class);
        out.println();


        for (ColumnDefinition parameter : function.getInParameters()) {
        	printParameter(out, parameter, function);
        }

        out.println();
        printNoFurtherInstancesAllowedJavadoc(out);
        out.println("\tpublic " + function.getJavaClassName() + "() {");
        out.printImport(SQLDialect.class);
        out.printImport(schema.getFullJavaClassName());
        out.printImportForDialectDataTypes(database.getDialect());
        out.print("\t\tsuper(SQLDialect.");
        out.print(database.getDialect().name());
        out.print(", \"");

        if (function.getPackage() != null) {
            out.print(function.getPackage().getName());
            out.print(".");
        }

        out.print(function.getName());
        out.print("\", ");
        out.print(schema.getJavaClassName());
        out.print(".");
        out.print(schema.getNameUC());
        out.print(", ");
        out.print(function.getReturnType().getJavaTypeReference(out));
        out.println(");");

        if (function.getInParameters().size() > 0) {
            out.println();
        }

        for (ColumnDefinition parameter : function.getInParameters()) {
        	String parameterNameUC = parameter.getName().toUpperCase();

        	out.print("\t\t");
        	out.println("addInParameter(" + parameterNameUC + ");");
        }

        if (function.getOverload() != null) {
            out.println("\t\tsetOverloaded(true);");
        }

        out.println("\t}");

        for (ColumnDefinition parameter : function.getInParameters()) {
            out.println();
            out.println("\t/**");
            out.println("\t * Set the <code>" + parameter.getName() + "</code> parameter to the function");
            out.println("\t */");
            out.println("\tpublic void set" + parameter.getJavaClassName() + "(" + parameter.getType().getJavaSimpleType() + " value) {");
            out.println("\t\tsetValue(" + parameter.getNameUC() + ", value);");
            out.println("\t}");
            out.println();
            out.println("\t/**");
            out.println("\t * Set the <code>" + parameter.getName() + "</code> parameter to the function");
            out.println("\t * <p>");
            out.println("\t * Use this method only, if the function is called as a {@link Field} in a {@link Select} statement!");
            out.println("\t */");
            out.println("\tpublic void set" + parameter.getJavaClassName() + "(Field<" + parameter.getType().getJavaSimpleType() + "> field) {");
            out.println("\t\tsetValue(" + parameter.getNameUC() + ", field);");
            out.println("\t}");

            out.printImport(Select.class);
            out.printImport(Field.class);
        }

        out.println("}");
        out.close();
    }

    private void printConvenienceMethodFunctionAsField(GenerationWriter out, FunctionDefinition function, boolean parametersAsField) throws SQLException {
        // [#281] - Java can't handle more than 255 method parameters
        if (function.getInParameters().size() > 254) {
            log.warn("WARN: Procedure " + function + " has more than 254 in parameters. Skipping generation of convenience method.");
            return;
        }

        // Do not generate separate convenience methods, if there are no IN
        // parameters. They would have the same signature and no additional
        // meaning
        if (parametersAsField && function.getInParameters().isEmpty()) {
            return;
        }

        out.println();
        out.println("\t/**");
        out.println("\t * Get " + function.getNameUC() + " as a field");
        out.println("\t *");
        out.printImport(function.getFullJavaClassName());

        for (ColumnDefinition parameter : function.getInParameters()) {
            out.println("\t * @param " + parameter.getJavaClassNameLC());
        }

        out.println("\t */");
        out.print("\tpublic static Field<");
        out.print(function.getReturnType());
        out.print("> ");
        out.print(function.getJavaClassNameLC());
        out.print("(");
        out.printImport(Field.class);

        String separator = "";
        for (ColumnDefinition parameter : function.getInParameters()) {
            out.print(separator);

            if (parametersAsField) {
                out.print("Field<");
                out.print(parameter.getType());
                out.print(">");
                out.printImport(Field.class);
            } else {
                out.print(parameter.getType());
            }

            out.print(" ");
            out.print(parameter.getJavaClassNameLC());

            separator = ", ";
        }

        out.println(") {");
        out.println("\t\t" + function.getJavaClassName() + " f = new " + function.getJavaClassName() + "();");

        for (ColumnDefinition parameter : function.getInParameters()) {
            out.println("\t\tf.set" + parameter.getJavaClassName() + "(" + parameter.getJavaClassNameLC() + ");");
        }

        out.println();
        out.println("\t\treturn f.asField();");
        out.println("\t}");
    }

    private void printConvenienceMethodFunction(GenerationWriter out, FunctionDefinition function) throws SQLException {
        // [#281] - Java can't handle more than 255 method parameters
        if (function.getInParameters().size() > 254) {
            log.warn("WARN: Procedure " + function + " has more than 254 in parameters. Skipping generation of convenience method.");
            return;
        }

        out.println();
        out.println("\t/**");
        out.println("\t * Invoke " + function.getNameUC());
        out.println("\t *");
        out.printImport(function.getFullJavaClassName());

        for (ColumnDefinition parameter : function.getInParameters()) {
            out.println("\t * @param " + parameter.getJavaClassNameLC());
        }

        out.println("\t */");
        out.print("\tpublic static ");
        out.print(function.getReturnType());
        out.print(" ");
        out.print(function.getJavaClassNameLC());
        out.print("(Connection connection");
        out.printImport(Connection.class);

        for (ColumnDefinition parameter : function.getInParameters()) {
            out.print(", ");
            out.print(parameter.getType());
            out.print(" ");
            out.print(parameter.getJavaClassNameLC());
        }

        out.println(") throws SQLException {");
        out.printImport(SQLException.class);
        out.println("\t\t" + function.getJavaClassName() + " f = new " + function.getJavaClassName() + "();");

        for (ColumnDefinition parameter : function.getInParameters()) {
            out.println("\t\tf.set" + parameter.getJavaClassName() + "(" + parameter.getJavaClassNameLC() + ");");
        }

        out.println();
        out.println("\t\tf.execute(connection);");
        out.println("\t\treturn f.getReturnValue();");
        out.println("\t}");
    }

    private void printPrivateConstructor(GenerationWriter out, String javaClassName) {
        out.println();
        out.println("\t/**");
        out.println("\t * No instances");
        out.println("\t */");
        out.println("\tprivate " + javaClassName + "() {}");
    }

    private void printConvenienceMethodProcedure(GenerationWriter out, ProcedureDefinition procedure) throws SQLException {
        // [#281] - Java can't handle more than 255 method parameters
        if (procedure.getInParameters().size() > 254) {
            log.warn("WARN: Procedure " + procedure + " has more than 254 in parameters. Skipping generation of convenience method.");
            return;
        }

        out.println();
        out.println("\t/**");
        out.println("\t * Invoke " + procedure.getNameUC());
        out.println("\t *");
        out.printImport(procedure.getFullJavaClassName());

        for (ColumnDefinition parameter : procedure.getAllParameters()) {
            out.print("\t * @param " + parameter.getJavaClassNameLC() + " ");

            if (procedure.getInParameters().contains(parameter)) {
                if (procedure.getOutParameters().contains(parameter)) {
                    out.println("IN OUT parameter");
                } else {
                    out.println("IN parameter");
                }
            } else {
                out.println("OUT parameter");
            }
        }

        out.println("\t */");
        out.print("\tpublic static ");

        if (procedure.getOutParameters().size() == 0) {
            out.print("void ");
        }
        else if (procedure.getOutParameters().size() == 1) {
            out.print(procedure.getOutParameters().get(0).getType());
            out.print(" ");
        }
        else {
            out.print(procedure.getJavaClassName() + " ");
        }

        out.print(procedure.getJavaClassNameLC());
        out.print("(Connection connection");
        out.printImport(Connection.class);

        for (ColumnDefinition parameter : procedure.getInParameters()) {
            out.print(", ");
            out.print(parameter.getType());
            out.print(" ");
            out.print(parameter.getJavaClassNameLC());
        }

        out.println(") throws SQLException {");
        out.printImport(SQLException.class);
        out.println("\t\t" + procedure.getJavaClassName() + " p = new " + procedure.getJavaClassName() + "();");

        for (ColumnDefinition parameter : procedure.getInParameters()) {
            out.println("\t\tp.set" + parameter.getJavaClassName() + "(" + parameter.getJavaClassNameLC() + ");");
        }

        out.println();
        out.println("\t\tp.execute(connection);");

        if (procedure.getOutParameters().size() == 1) {
            out.println("\t\treturn p.get" + procedure.getOutParameters().get(0).getJavaClassName() + "();");
        }
        else if (procedure.getOutParameters().size() > 1) {
            out.println("\t\treturn p;");
        }

        out.println("\t}");
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

	private void printGetterAndSetter(GenerationWriter out, ColumnDefinition column, Definition type, String tablePackage, Set<String> reserved) throws SQLException {
        String columnDisambiguationSuffix = "";
        String getterDisambiguationSuffix = "";

		if (column.getNameUC().equals(type.getNameUC())) {
		    columnDisambiguationSuffix = "_";
		}

		if (reserved.contains(column.getJavaClassName())) {
		    getterDisambiguationSuffix = "_";
		}

		printFieldJavaDoc(out, getterDisambiguationSuffix, column);
		out.println("\tpublic void set" + column.getJavaClassName() + getterDisambiguationSuffix + "(" + column.getType().getJavaSimpleType() + " value) {");
		out.println("\t\tsetValue(" + type.getJavaClassName() + "." + column.getNameUC() + columnDisambiguationSuffix + ", value);");
		out.println("\t}");
		printFieldJavaDoc(out, getterDisambiguationSuffix, column);
		out.println("\tpublic " + column.getType().getJavaSimpleType() + " get" + column.getJavaClassName() + getterDisambiguationSuffix + "() {");
		out.println("\t\treturn getValue(" + type.getJavaClassName() + "." + column.getNameUC() + columnDisambiguationSuffix + ");");
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
						out.print("\t\tq.addConditions(");
						out.print(referencing.getJavaClassName());
						out.print(".");
						out.print(foreignKey.getKeyColumns().get(i).getName().toUpperCase());
						out.print(".equal(getValue");

						DataTypeDefinition foreignType = foreignKey.getKeyColumns().get(i).getType();
						DataTypeDefinition primaryType = primaryKey.getKeyColumns().get(i).getType();

						// Convert foreign key value, if there is a type mismatch
						if (!foreignType.equals(primaryType)) {
						    out.print("As");
						    out.print(foreignKey.getKeyColumns().get(i).getType());
						}

						out.print("(");
						out.print(type.getJavaClassName());
						out.print(".");
						out.print(primaryKey.getKeyColumns().get(i).getName().toUpperCase());
						out.println(")));");
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
    					out.print("\t\tq.addConditions(");
    					out.print(referenced.getJavaClassName());
    					out.print(".");
    					out.print(foreignKey.getReferencedColumns().get(i).getName().toUpperCase());
    					out.print(".equal(getValue");

    					DataTypeDefinition foreignType = foreignKey.getKeyColumns().get(i).getType();
                        DataTypeDefinition primaryType = foreignKey.getReferencedColumns().get(i).getType();

                        // Convert foreign key value, if there is a type mismatch
                        if (!foreignType.equals(primaryType)) {
                            out.print("As");
                            out.print(foreignKey.getReferencedColumns().get(i).getType());
                        }

    					out.print("(");
    					out.print(type.getJavaClassName());
    					out.print(".");
    					out.print(foreignKey.getKeyColumns().get(i).getName().toUpperCase());
    					out.println(")));");
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

		out.printImport(column.getType());
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

		out.print("\tpublic static final ");
		out.print(declaredMemberType);
		out.print(genericPrefix);
		out.print(column.getType());
		out.print("> ");
		out.print(column.getNameUC());
		out.print(columnDisambiguationSuffix);
		out.print(" = new ");
		out.print(concreteMemberType);
		out.print(genericPrefix);
		out.print(column.getType());
		out.print(">(SQLDialect.");
		out.print(column.getDatabase().getDialect().name());
		out.print(", \"");
		out.print(column.getName());
		out.print("\", ");
		out.print(column.getType().getJavaTypeReference(out));

		if (hasType) {
		    out.print(", " + type.getNameUC());
		}

		out.println(");");

		out.printImport(SQLDialect.class);
		out.printImport(declaredMemberClass);
		out.printImport(concreteMemberClass);
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

		if (column.getType().getJavaType().equals("java.lang.Object")) {
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
			out.println("\t * This item causes a name clash. That is why an underline character was appended to the Java field name");
		}

		out.println("\t */");
	}

	private void printNoFurtherInstancesAllowedJavadoc(GenerationWriter out) {
		out.println("\t/**");
		out.println("\t * No further instances allowed");
		out.println("\t */");
	}

    private void printClassJavadoc(GenerationWriter out, Definition definition) {
        printClassJavadoc(out, definition.getComment());
    }

    private void printClassJavadoc(GenerationWriter out, String comment) {
		out.println("/**");
		out.println(" * This class is generated by jOOQ.");

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
