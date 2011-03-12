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

package org.jooq.util.derby;

import static org.jooq.util.derby.sys.tables.Sysconglomerates.SYSCONGLOMERATES;
import static org.jooq.util.derby.sys.tables.Sysconstraints.SYSCONSTRAINTS;
import static org.jooq.util.derby.sys.tables.Syskeys.SYSKEYS;
import static org.jooq.util.derby.sys.tables.Sysschemas.SYSSCHEMAS;
import static org.jooq.util.derby.sys.tables.Syssequences.SYSSEQUENCES;
import static org.jooq.util.derby.sys.tables.Systables.SYSTABLES;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.Factory;
import org.jooq.util.AbstractDatabase;
import org.jooq.util.ArrayDefinition;
import org.jooq.util.ColumnDefinition;
import org.jooq.util.DefaultRelations;
import org.jooq.util.DefaultSequenceDefinition;
import org.jooq.util.EnumDefinition;
import org.jooq.util.FunctionDefinition;
import org.jooq.util.PackageDefinition;
import org.jooq.util.ProcedureDefinition;
import org.jooq.util.SequenceDefinition;
import org.jooq.util.TableDefinition;
import org.jooq.util.UDTDefinition;
import org.jooq.util.derby.sys.tables.Sysconglomerates;
import org.jooq.util.derby.sys.tables.Sysconstraints;
import org.jooq.util.derby.sys.tables.Syskeys;
import org.jooq.util.derby.sys.tables.Sysschemas;
import org.jooq.util.derby.sys.tables.Syssequences;
import org.jooq.util.derby.sys.tables.Systables;

/**
 * @author Lukas Eder
 */
public class DerbyDatabase extends AbstractDatabase {

	@Override
	protected void loadPrimaryKeys(DefaultRelations relations) throws SQLException {
	    for (Record record : create().select(
	            Systables.TABLENAME,
	            Systables.TABLEID,
	            Sysconstraints.CONSTRAINTNAME,
	            Sysconglomerates.DESCRIPTOR)
	        .from(SYSCONGLOMERATES)
	        .join(SYSKEYS)
	        .on(Syskeys.CONGLOMERATEID.equal(Sysconglomerates.CONGLOMERATEID))
	        .join(SYSCONSTRAINTS)
	        .on(Sysconstraints.CONSTRAINTID.equal(Syskeys.CONSTRAINTID))
	        .join(SYSTABLES)
	        .on(Systables.TABLEID.equal(Sysconglomerates.TABLEID))
	        .where(Sysconstraints.TYPE.equal("P"))
	        .fetch()) {

	        String key = record.getValue(Sysconstraints.CONSTRAINTNAME);
            String tableName = record.getValue(Systables.TABLENAME);
            String descriptor = record.getValueAsString(Sysconglomerates.DESCRIPTOR);

            TableDefinition table = getTable(tableName);
            if (table != null) {
                for (int index : decode(descriptor)) {
                    relations.addPrimaryKey(key, table.getColumn(index));
                }
            }
	    }
	}

	@Override
	protected void loadForeignKeys(DefaultRelations relations) throws SQLException {
	    Field<String> constraintname = create().plainSQLField("fg.conglomeratename", String.class);
	    Field<String> ftablename = create().plainSQLField("ft.tablename", String.class);
	    Field<?> fdescriptor = create().plainSQLField("fg.descriptor");
	    Field<String> ptablename = create().plainSQLField("pt.tablename", String.class);
	    Field<?> pdescriptor = create().plainSQLField("pg.descriptor");

	    for (Record record : create().select(
	            constraintname,
	            ftablename,
	            fdescriptor,
	            ptablename,
	            pdescriptor)
	        .from("sys.sysconstraints fc")
	        .join("sys.sysforeignkeys f").on("f.constraintid = fc.constraintid")
	        .join("sys.sysconglomerates fg").on("fg.conglomerateid = f.conglomerateid")
	        .join("sys.systables ft").on("ft.tableid = fg.tableid")
	        .join("sys.syskeys pk").on("pk.constraintid = f.keyconstraintid")
	        .join("sys.sysconglomerates pg").on("pg.conglomerateid = pk.conglomerateid")
	        .join("sys.systables pt").on("pt.tableid = pg.tableid")
	        .where("fc.type = 'F'")
	        .fetch()) {


	        String key = record.getValue(constraintname);
            String referencingTableName = record.getValue(ftablename);
            List<Integer> referencingColumnIndexes = decode(record.getValueAsString(fdescriptor));
            String referencedTableName = record.getValue(ptablename);
            List<Integer> referencedColumnIndexes = decode(record.getValueAsString(pdescriptor));

	        TableDefinition referencingTable = getTable(referencingTableName);
            TableDefinition referencedTable = getTable(referencedTableName);

            if (referencingTable != null && referencedTable != null) {
                for (int i = 0; i < referencingColumnIndexes.size(); i++) {
                    ColumnDefinition referencingColumn = referencingTable.getColumn(referencingColumnIndexes.get(i));
                    ColumnDefinition referencedColumn = referencedTable.getColumn(referencedColumnIndexes.get(i));

                    String primaryKey = relations.getPrimaryKeyName(referencedColumn);
                    relations.addForeignKey(key, primaryKey, referencingColumn);
                }
            }
	    }
	}

    /*
     * Unfortunately the descriptor interface is not exposed publicly Hence, the
     * toString() method is used and its results are parsed The results are
     * something like UNIQUE BTREE (index1, index2, ... indexN)
     */
    private List<Integer> decode(String descriptor) {
        List<Integer> result = new ArrayList<Integer>();

        Pattern p = Pattern.compile(".*?\\((.*?)\\)");
        Matcher m = p.matcher(descriptor);

        while (m.find()) {
            String[] split = m.group(1).split(",");

            if (split != null) {
                for (String index : split) {
                    result.add(Integer.valueOf(index.trim()) - 1);
                }
            }
        }

        return result;
    }

    @Override
    protected List<SequenceDefinition> getSequences0() throws SQLException {
        List<SequenceDefinition> result = new ArrayList<SequenceDefinition>();

        for (String name : create().select(Syssequences.SEQUENCENAME)
            .from(SYSSEQUENCES)
            .join(SYSSCHEMAS)
            .on(Sysschemas.SCHEMAID.equal(Syssequences.SCHEMAID))
            .where(Sysschemas.SCHEMANAME.equal(getSchemaName()))
            .orderBy(Syssequences.SEQUENCENAME)
            .fetch(Syssequences.SEQUENCENAME)) {

            result.add(new DefaultSequenceDefinition(this, name));
        }

        return result;
    }

	@Override
	protected List<TableDefinition> getTables0() throws SQLException {
		List<TableDefinition> result = new ArrayList<TableDefinition>();

		for (Record record : create().select(Systables.TABLENAME, Systables.TABLEID)
            .from(SYSTABLES)
            .join(SYSSCHEMAS)
            .on(Systables.SCHEMAID.equal(Sysschemas.SCHEMAID))
            .where(Sysschemas.SCHEMANAME.equal(getSchemaName()))
	        .fetch()) {

		    String name = record.getValue(Systables.TABLENAME);
		    String id = record.getValue(Systables.TABLEID);

		    DerbyTableDefinition table = new DerbyTableDefinition(this, name, id);
            result.add(table);
		}

		return result;
	}

    @Override
    protected List<EnumDefinition> getEnums0() throws SQLException {
        List<EnumDefinition> result = new ArrayList<EnumDefinition>();
        return result;
    }

    @Override
    protected List<UDTDefinition> getUDTs0() throws SQLException {
        List<UDTDefinition> result = new ArrayList<UDTDefinition>();
        return result;
    }

    @Override
    protected List<ArrayDefinition> getArrays0() throws SQLException {
        List<ArrayDefinition> result = new ArrayList<ArrayDefinition>();
        return result;
    }

	@Override
	protected List<ProcedureDefinition> getProcedures0() throws SQLException {
		List<ProcedureDefinition> result = new ArrayList<ProcedureDefinition>();
        return result;
	}

    @Override
    protected List<FunctionDefinition> getFunctions0() throws SQLException {
        List<FunctionDefinition> result = new ArrayList<FunctionDefinition>();
        return result;
    }

    @Override
    protected List<PackageDefinition> getPackages0() throws SQLException {
        List<PackageDefinition> result = new ArrayList<PackageDefinition>();
        return result;
    }

    @Override
    public Factory create() {
        return new Factory(getConnection(), SQLDialect.DERBY);
    }
}
