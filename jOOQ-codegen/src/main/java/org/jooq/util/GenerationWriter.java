package org.jooq.util;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;

import org.jooq.EnumType;
import org.jooq.Field;
import org.jooq.MasterDataType;
import org.jooq.SQLDialect;
import org.jooq.UDTRecord;
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

    private static final String IMPORT_STATEMENT                = "__IMPORT_STATEMENT__";
    private static final String SUPPRESS_WARNINGS_STATEMENT     = "__SUPPRESS_WARNINGS_STATEMENT__";
    private static final String STATIC_INITIALISATION_STATEMENT = "__STATIC_INITIALISATION_STATEMENT__";
    private static final String INITIALISATION_STATEMENT        = "__INITIALISATION_STATEMENT__";
    private static final String SERIAL_STATEMENT                = "__SERIAL_STATEMENT__";

    private final PrintWriter   writer;
    private final StringBuilder sb;
    private final Set<String>   imported;
    private final List<String>  staticInitialisationStatements;
    private final List<String>  initialisationStatements;
    private final Set<Object>   alreadyPrinted;
    private final Set<String>   suppressWarnings;

    public GenerationWriter(PrintWriter writer) {
        this.writer = writer;
        this.sb = new StringBuilder();
        this.imported = new TreeSet<String>();
        this.staticInitialisationStatements = new ArrayList<String>();
        this.initialisationStatements = new ArrayList<String>();
        this.alreadyPrinted = new HashSet<Object>();
        this.suppressWarnings = new TreeSet<String>();
    }

    public void printImportPlaceholder() {
        println(IMPORT_STATEMENT);
    }

    public void printStaticInitialisationStatementsPlaceholder() {
        println(STATIC_INITIALISATION_STATEMENT);
    }

    public void printInitialisationStatementsPlaceholder() {
        println(INITIALISATION_STATEMENT);
    }

    public void printSuppressWarningsPlaceholder() {
        println(SUPPRESS_WARNINGS_STATEMENT);
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

        if (name.equals("byte[]")) {
            return;
        }

        if (name.contains("[")) {
            if (!name.endsWith("[]")) {
                return;
            }
            else {
                imported.add(name.substring(0, name.length() - 2));
            }
        }
        else {
            imported.add(name);
        }
    }

    public void printImportForDialectDataTypes(SQLDialect dialect) {
        printImport("org.jooq.util." + dialect.getName().toLowerCase() + "." + dialect.getName() + "DataType");
    }

    public void printStaticInitialisationStatement(String statement) {
        staticInitialisationStatements.add(statement);
    }

    public void printInitialisationStatement(String statement) {
        initialisationStatements.add(statement);
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

        StringBuilder staticInits = new StringBuilder();
        StringBuilder inits = new StringBuilder();
        StringBuilder warnings = new StringBuilder();

        boolean hasStaticInits = false;
        boolean hasInits = false;

        for (String statement : staticInitialisationStatements) {
            if (!StringUtils.isBlank(statement)) {
                hasStaticInits = true;
                break;
            }
        }

        for (String statement : initialisationStatements) {
            if (!StringUtils.isBlank(statement)) {
                hasInits = true;
                break;
            }
        }

        if (hasStaticInits) {
            staticInits.append("\n");
            staticInits.append("\t/*\n");
            staticInits.append("\t * static initialiser\n");
            staticInits.append("\t */\n");
            staticInits.append("\tstatic {\n");
            for (String statement : staticInitialisationStatements) {
                staticInits.append("\t\t" + statement + "\n");
            }
            staticInits.append("\t}\n");
        }

        if (hasInits) {
            inits.append("\n");
            inits.append("\t/*\n");
            inits.append("\t * instance initialiser\n");
            inits.append("\t */\n");
            inits.append("\t{\n");
            for (String statement : initialisationStatements) {
                inits.append("\t\t" + statement + "\n");
            }
            inits.append("\t}\n");
        }

        if (!suppressWarnings.isEmpty()) {
            warnings.append("@SuppressWarnings({");

            String separator = "";
            for (String warning : suppressWarnings) {
                warnings.append(separator);
                warnings.append("\"");
                warnings.append(warning);
                warnings.append("\"");

                separator = ", ";
            }

            warnings.append("})\n");
        }

        string = string.replaceAll(IMPORT_STATEMENT,
            Matcher.quoteReplacement(imports.toString()));
        string = string.replaceAll(STATIC_INITIALISATION_STATEMENT + "\\n",
            Matcher.quoteReplacement(staticInits.toString()));
        string = string.replaceAll(INITIALISATION_STATEMENT + "\\n",
            Matcher.quoteReplacement(inits.toString()));
        string = string.replaceAll(SUPPRESS_WARNINGS_STATEMENT + "\\n",
            Matcher.quoteReplacement(warnings.toString()));
        string = string.replaceAll(SERIAL_STATEMENT,
            Matcher.quoteReplacement(String.valueOf(string.hashCode())));

        writer.append(string);
        writer.close();
    }

    public <T> void printNewJavaObject(Object value, Field<?> field) {
        print(getNewJavaObject(value, field));
    }

    private <T> String getNewJavaObject(Object value, Field<?> field) {
        if (value == null) {
            return "null";
        }

        Class<?> type = field.getType();
        if (type == Blob.class) {
            // Not supported
        }
        else if (type == Boolean.class) {
            return Boolean.toString(getIsTrue(String.valueOf(value)));
        }
        else if (type == BigInteger.class) {
            return "new BigInteger(\"" + value + "\")";
        }
        else if (type == BigDecimal.class) {
            return "new BigDecimal(\"" + value + "\")";
        }
        else if (type == Byte.class) {
            return "(byte) " + value;
        }
        else if (type == byte[].class) {
            // Not supported
        }
        else if (type == Clob.class) {
            // Not supported
        }
        else if (type == Date.class) {
            return "new Date(" + ((Date) value).getTime() + "L)";
        }
        else if (type == Double.class) {
            return Double.toString((Double) value);
        }
        else if (type == Float.class) {
            return Float.toString((Float) value) + "f";
        }
        else if (type == Integer.class) {
            return Integer.toString((Integer) value);
        }
        else if (type == Long.class) {
            return Long.toString((Long) value) + "L";
        }
        else if (type == Short.class) {
            return "(short) " + value;
        }
        else if (type == String.class) {
            return "\"" + value.toString().replace("\"", "\\\"") + "\"";
        }
        else if (type == Time.class) {
            return "new Time(" + ((Time) value).getTime() + "L)";
        }
        else if (type == Timestamp.class) {
            return "new Timestamp(" + ((Timestamp) value).getTime() + "L)";
        }
        else if (MasterDataType.class.isAssignableFrom(type)) {
            // Not supported
        }
        else if (EnumType.class.isAssignableFrom(type)) {
            // Not supported
        }
        else if (UDTRecord.class.isAssignableFrom(type)) {
            // Not supported
        }
        else {
            // Not supported
        }

        throw new UnsupportedOperationException("Class " + type + " is not supported");
    }

    private boolean getIsTrue(String string) {
        if ("1".equals(string)) {
            return true;
        }
        else if ("true".equalsIgnoreCase(string)) {
            return true;
        }
        else {
            return false;
        }
    }

    public void suppressWarnings(String string) {
        suppressWarnings.add(string);
    }
}