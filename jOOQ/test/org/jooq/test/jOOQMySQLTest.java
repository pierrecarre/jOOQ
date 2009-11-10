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

package org.jooq.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.apache.commons.io.FileUtils;
import org.jooq.Field;
import org.jooq.Result;
import org.jooq.SelectQuery;
import org.jooq.impl.Functions;
import org.jooq.impl.QueryFactory;
import org.jooq.test.generatedclasses.functions.FAuthorExists;
import org.jooq.test.generatedclasses.procedures.PAuthorExists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Lukas Eder
 */
public class jOOQMySQLTest {
	
	private Connection connection;

	@Before
	public void setUp() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection ("jdbc:mysql://localhost/test", "root", "");

		Statement stmt = null;
		File file = new File(getClass().getResource("/org/jooq/test/mysql/create.sql").toURI());
		String allSQL = FileUtils.readFileToString(file);
		
		for (String sql : allSQL.split("/")) {
			try {
			stmt = connection.createStatement();
			stmt.executeUpdate(sql);
			} finally {
				if (stmt != null) {
					stmt.close();
				}
			}
		}
	}

	@After
	public void tearDown() throws Exception {
		if (connection != null) {
			connection.close();
			connection = null;
		}
	}
	
	@Test
	public final void testSelectQuery() throws Exception {
		SelectQuery q = QueryFactory.createSelectQuery();
		Field<Integer> f1 = Functions.constant(1);
		Field<Double> f2 = Functions.constant(2d);
		Field<String> f3 = Functions.constant("test");
		
		q.addSelect(f1);
		q.addSelect(f2);
		q.addSelect(f3);
		
		int i = q.execute(connection);
		Result result = q.getResult();
		
		assertEquals(1, i);
		assertEquals(1, result.getNumberOfRecords());
		assertEquals(3, result.getFields().size());
		assertTrue(result.getFields().contains(f1));
		assertTrue(result.getFields().contains(f2));
		assertTrue(result.getFields().contains(f3));
		
		assertEquals(3, result.getRecords().get(0).getFields().size());
		assertTrue(result.getRecords().get(0).getFields().contains(f1));
		assertTrue(result.getRecords().get(0).getFields().contains(f2));
		assertTrue(result.getRecords().get(0).getFields().contains(f3));
		
		assertEquals((Integer) 1, result.getRecords().get(0).getValue(f1));
		assertEquals((Double) 2d, result.getRecords().get(0).getValue(f2));
		assertEquals("test", result.getRecords().get(0).getValue(f3));
	}
	
	@Test
	public final void testProcedure() throws Exception {
		PAuthorExists procedure = new PAuthorExists();
		procedure.setAuthorName("Paulo");
		procedure.execute(connection);
		assertTrue(procedure.getResult());
		
		procedure = new PAuthorExists();
		procedure.setAuthorName("Shakespeare");
		procedure.execute(connection);
		assertFalse(procedure.getResult());
	}
	
	@Test
	public final void testFunction1() throws Exception {
		FAuthorExists function1 = new FAuthorExists();
		function1.setAuthorName("Paulo");
		function1.execute(connection);
		assertEquals(1, (int) function1.getReturnValue());
		
		FAuthorExists function2 = new FAuthorExists();
		function2.setAuthorName("Shakespeare");
		function2.execute(connection);
		assertEquals(0, (int) function2.getReturnValue());
	}
	
	@Test
	public final void testFunction2() throws Exception {
		// TODO
		// StoredFunctions cannot be integrated with Functions yet, because
		// Functions expect fields as parameters, whereas StoredFunctions expect
		// constant values
		
		
//		FAuthorExists function1 = new FAuthorExists();
//		function1.setAuthorName("Paulo");
//		Function<Byte> f1 = function1.getFunction();
//		
//		FAuthorExists function2 = new FAuthorExists();
//		function2.setAuthorName("Shakespeare");
//		Function<Byte> f2 = function2.getFunction();
//		
//		SelectQuery q = QueryFactory.createSelectQuery();
//		q.addSelect(f1, f2);
//		q.execute(connection);
//		Result result = q.getResult();
//		
//		assertEquals(1, result.getNumberOfRecords());
//		assertEquals(1, (int) result.getRecord(0).getValue(f1));
//		assertEquals(0, (int) result.getRecord(0).getValue(f2));
	}
}
