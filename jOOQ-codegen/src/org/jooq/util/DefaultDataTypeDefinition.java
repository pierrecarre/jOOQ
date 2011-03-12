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

import static org.jooq.impl.FieldTypeHelper.normalise;

import java.sql.SQLException;

import org.jooq.SQLDialect;
import org.jooq.SQLDialectNotSupportedException;
import org.jooq.impl.FieldTypeHelper;
import org.jooq.impl.JooqLogger;
import org.jooq.util.db2.DB2DataType;
import org.jooq.util.derby.DerbyDataType;
import org.jooq.util.h2.H2DataType;
import org.jooq.util.hsqldb.HSQLDBDataType;
import org.jooq.util.mysql.MySQLDataType;
import org.jooq.util.oracle.OracleDataType;
import org.jooq.util.postgres.PostgresDataType;
import org.jooq.util.sqlite.SQLiteDataType;


/**
 * @author Lukas Eder
 */
public class DefaultDataTypeDefinition implements DataTypeDefinition {

    private static final JooqLogger log = JooqLogger.getLogger(DefaultDataTypeDefinition.class);

    private final Database          database;
    private final String            typeName;
    private final String            udtName;
    private final int               precision;
    private final int               scale;

    public DefaultDataTypeDefinition(Database database, String typeName) {
        this(database, typeName, typeName, 0, 0);
    }

    public DefaultDataTypeDefinition(Database database, String typeName, String udtName) {
        this(database, typeName, udtName, 0, 0);
    }

    public DefaultDataTypeDefinition(Database database, String typeName, int precision, int scale) {
        this(database, typeName, typeName, precision, scale);
    }

    private DefaultDataTypeDefinition(Database database, String typeName, String udtName, int precision, int scale) {
        this.database = database;
        this.typeName = typeName;
        this.udtName = udtName;
        this.precision = precision;
        this.scale = scale;
    }

    @Override
    public final Database getDatabase() {
        return database;
    }

    private final SQLDialect getDialect() {
        return getDatabase().getDialect();
    }

    @Override
    public final boolean isUDT() {
        return getDatabase().getUDT(udtName) != null;
    }

    @Override
    public final String getJavaTypeReference(GenerationWriter out) throws SQLException {
        if (getDatabase().isArrayType(typeName)) {
            String baseType = getArrayBaseType(typeName, udtName);
            return getTypeReference(out, baseType, 0, 0, baseType) + ".getArrayDataType()";
        }
        else {
            return getTypeReference(out, typeName, precision, scale, udtName);
        }
    }

    private final String getTypeReference(GenerationWriter out, String t, int p, int s, String u) throws SQLException {
        StringBuilder sb = new StringBuilder();
        if (getDatabase().getArray(u) != null) {
            ArrayDefinition array = getDatabase().getArray(u);

            sb.append(array.getElementType().getJavaTypeReference(out));
            sb.append(".asArrayDataType(");
            sb.append(array.getJavaClassName("Record"));
            sb.append(".class)");
        }
        else if (getDatabase().getUDT(u) != null) {
            UDTDefinition udt = getDatabase().getUDT(u);
            if (out != null) {
                out.printImport(udt.getFullJavaClassName());
            }

            sb.append(udt.getJavaClassName());
            sb.append(".");
            sb.append(udt.getNameUC());
            sb.append(".getDataType()");
        }
        else if (getDatabase().getEnum(u) != null) {
            if (out != null) {
                out.printImportForDialectDataTypes(getDialect());
            }

            sb.append(getDialect().getName());
            sb.append("DataType.");
            sb.append(FieldTypeHelper.normalise(FieldTypeHelper.getDataType(getDialect(), String.class).getTypeName()));
            sb.append(".asEnumDataType(");
            sb.append(getDatabase().getEnum(u).getJavaClassName());
            sb.append(".class)");
        }
        else {
            if (out != null) {
                out.printImportForDialectDataTypes(getDialect());
            }

            sb.append(getDialect().getName());
            sb.append("DataType.");

            try {
                String type1 = getType(t, p, s, u, null);
                String type2 = getType(t, 0, 0, u, null);

                sb.append(FieldTypeHelper.normalise(t));

                if (!type1.equals(type2)) {
                    Class<?> clazz = getDialectSQLType(getDialect(), t, p, s);

                    sb.append(".asNumberDataType(");
                    sb.append(clazz.getSimpleName());
                    sb.append(".class)");

                    if (out != null) {
                        out.printImport(clazz);
                    }
                }
            }

            // Mostly because of unsupported data types
            catch (SQLDialectNotSupportedException e) {
                sb.append("getDefaultDataType(\"");
                sb.append(typeName);
                sb.append("\")");
            }
        }

        return sb.toString();
    }

    @Override
    public final String getJavaSimpleType() throws SQLException {
        return getJavaType().replaceAll(".*\\.", "");
    }

    @Override
    public final String getType() {
        return typeName;
    }

    @Override
    public final String getJavaType() throws SQLException {
        return getType(typeName, precision, scale, udtName, Object.class.getName());
    }

    private final String getType(String t, int p, int s, String u, String defaultType) throws SQLException {
        String type = defaultType;

        // Array types
        if (getDatabase().isArrayType(t)) {
            String baseType = getArrayBaseType(t, u);
            type = getType(baseType, p, s, baseType, defaultType) + "[]";
        }

        // Check for Oracle-style VARRAY types
        else if (getDatabase().getArray(u) != null) {
            type = getDatabase().getArray(u).getFullJavaClassName("Record");
        }

        // Check for ENUM types
        else if (getDatabase().getEnum(u) != null) {
            type = getDatabase().getEnum(u).getFullJavaClassName();
        }

        // Check for UDTs
        else if (getDatabase().getUDT(u) != null) {
            type = getDatabase().getUDT(u).getFullJavaClassName("Record");
        }

        // Try finding a basic standard SQL type according to the current dialect
        else {
            try {
                type = getDialectSQLType(getDialect(), t, p, s).getCanonicalName();
            }
            catch (SQLDialectNotSupportedException e) {
                if (defaultType != null) {
                    log.warn("Unsupported datatype : " + t + " (" + u + ")");
                }
                else {
                    throw e;
                }
            }
        }

        return type;
    }

    private String getArrayBaseType(String t, String u) {
        switch (getDialect()) {
            case POSTGRES: {

                // The convention is to prepend a "_" to a type to get an array type
                if (u != null && u.startsWith("_")) {
                    return u.substring(1);
                }

                // But there are also arrays with a "vector" suffix
                else {
                    return u;
                }
            }

            case H2: {
                return H2DataType.OTHER.getTypeName();
            }

            case HSQLDB: {
                return t.replace(" ARRAY", "");
            }
        }

        throw new SQLDialectNotSupportedException("getArrayBaseType() is not supported for dialect " + getDialect());
    }

    private Class<?> getDialectSQLType(SQLDialect dialect, String t, int p, int s) throws SQLDialectNotSupportedException {
        switch (dialect) {
            case HSQLDB:
                return HSQLDBDataType.getDataType(normalise(t)).getType();
            case MYSQL:
                return MySQLDataType.getDataType(normalise(t)).getType();
            case ORACLE:
                return OracleDataType.getDataType(normalise(t)).getType(p, s);
            case POSTGRES:
                return PostgresDataType.getDataType(normalise(t)).getType();
            case DB2:
                return DB2DataType.getDataType(normalise(t)).getType();
            case DERBY:
                return DerbyDataType.getDataType(normalise(t)).getType();
            case H2:
                return H2DataType.getDataType(normalise(t)).getType();
            case SQLITE:
                return SQLiteDataType.getDataType(normalise(t)).getType();

            default:
                throw new SQLDialectNotSupportedException("This method is not yet implemented for dialect " + dialect);
        }
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return toString().equals("" + obj);
    }

    @Override
    public String toString() {
        try {
            return "DataType [ " + getJavaTypeReference(null) + " ]";
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
