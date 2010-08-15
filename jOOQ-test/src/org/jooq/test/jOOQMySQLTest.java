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
import static org.jooq.SortOrder.ASC;
import static org.jooq.test.mysql.generatedclasses.tables.TAuthor.LAST_NAME;
import static org.jooq.test.mysql.generatedclasses.tables.TAuthor.T_AUTHOR;
import static org.jooq.test.mysql.generatedclasses.tables.TBook.AUTHOR_ID;
import static org.jooq.test.mysql.generatedclasses.tables.TBook.TITLE;
import static org.jooq.test.mysql.generatedclasses.tables.TBook.T_BOOK;
import static org.jooq.test.mysql.generatedclasses.tables.VLibrary.AUTHOR;
import static org.jooq.test.mysql.generatedclasses.tables.VLibrary.V_LIBRARY;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.io.FileUtils;
import org.jooq.Configuration;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.ResultProviderQuery;
import org.jooq.SQLDialect;
import org.jooq.SelectQuery;
import org.jooq.impl.Functions;
import org.jooq.impl.QueryFactory;
import org.jooq.test.mysql.generatedclasses.functions.FAuthorExists;
import org.jooq.test.mysql.generatedclasses.procedures.PAuthorExists;
import org.jooq.test.mysql.generatedclasses.tables.TAuthor;
import org.jooq.test.mysql.generatedclasses.tables.TBook;
import org.jooq.test.mysql.generatedclasses.tables.VLibrary;
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
		Configuration.getInstance().setDialect(SQLDialect.MYSQL);

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
	public final void testSelectSimpleQuery() throws Exception {
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
		assertEquals(2d, result.getRecords().get(0).getValue(f2));
		assertEquals("test", result.getRecords().get(0).getValue(f3));
	}

	@Test
	public final void testSelectQuery() throws Exception {
		SelectQuery q = QueryFactory.createSelectQuery(T_AUTHOR);
		q.addSelect(T_AUTHOR.getFields());
		q.addOrderBy(LAST_NAME);

		int rows = q.execute(connection);
		Result result = q.getResult();

		assertEquals(2, rows);
		assertEquals(2, result.getNumberOfRecords());
		assertEquals("Coelho", result.getRecord(0).getValue(LAST_NAME));
		assertEquals("Orwell", result.getRecord(1).getValue(LAST_NAME));
	}

	@Test
	public final void testCombinedSelectQuery() throws Exception {
		SelectQuery q1 = QueryFactory.createSelectQuery(T_BOOK);
		SelectQuery q2 = QueryFactory.createSelectQuery(T_BOOK);

		q1.addCompareCondition(AUTHOR_ID, 1);
		q2.addCompareCondition(TITLE, "Brida");

		ResultProviderQuery combine = q1.combine(q2);

		int rows = combine.execute(connection);
		assertEquals(3, rows);
	}

	@Test
	public final void testJoinQuery() throws Exception {
		SelectQuery q1 = QueryFactory.createSelectQuery(V_LIBRARY);
		q1.addOrderBy(VLibrary.TITLE);

		SelectQuery q2 = QueryFactory.createSelectQuery(T_AUTHOR);
		q2.addJoin(T_BOOK, TBook.AUTHOR_ID, TAuthor.ID);
		q2.addOrderBy(TBook.TITLE);

		int rows1 = q1.execute(connection);
		int rows2 = q2.execute(connection);

		assertEquals(4, rows1);
		assertEquals(4, rows2);

		Result result1 = q1.getResult();
		Result result2 = q2.getResult();

		assertEquals("1984", result1.getRecord(0).getValue(VLibrary.TITLE));
		assertEquals("1984", result2.getRecord(0).getValue(TBook.TITLE));

		assertEquals("Animal Farm", result1.getRecord(1).getValue(VLibrary.TITLE));
		assertEquals("Animal Farm", result2.getRecord(1).getValue(TBook.TITLE));

		assertEquals("Brida", result1.getRecord(2).getValue(VLibrary.TITLE));
		assertEquals("Brida", result2.getRecord(2).getValue(TBook.TITLE));

		assertEquals("O Alquimista", result1.getRecord(3).getValue(VLibrary.TITLE));
		assertEquals("O Alquimista", result2.getRecord(3).getValue(TBook.TITLE));
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

	@Test
	public final void testFunction3() throws Exception {
		SelectQuery q1 = QueryFactory.createSelectQuery();
		Field<Timestamp> now = Functions.currentTimestamp();
		Field<Timestamp> ts = now.alias("ts");
		Field<Date> date = Functions.currentDate().alias("date");
		Field<Time> time = Functions.currentTime().alias("time");

		Field<Integer> year = Functions.extract(now, DatePart.YEAR).alias("y");
		Field<Integer> month = Functions.extract(now, DatePart.MONTH).alias("m");
		Field<Integer> day = Functions.extract(now, DatePart.DAY).alias("day");
		Field<Integer> hour = Functions.extract(now, DatePart.HOUR).alias("h");
		Field<Integer> minute = Functions.extract(now, DatePart.MINUTE).alias("mn");
		Field<Integer> second = Functions.extract(now, DatePart.SECOND).alias("sec");

		q1.addSelect(ts, date, time, year, month, day, hour, minute, second);
		q1.execute(connection);

		Record record = q1.getResult().getRecord(0);
		String timestamp = record.getValue(ts).toString();

		assertEquals(timestamp.split(" ")[0], record.getValue(date).toString());
		assertEquals(timestamp.split(" ")[1], record.getValue(time).toString() + ".0");

		assertEquals(Integer.valueOf(timestamp.split(" ")[0].split("-")[0]), record.getValue(year));
		assertEquals(Integer.valueOf(timestamp.split(" ")[0].split("-")[1]), record.getValue(month));
		assertEquals(Integer.valueOf(timestamp.split(" ")[0].split("-")[2]), record.getValue(day));
		assertEquals(Integer.valueOf(timestamp.split(" ")[1].split(":")[0]), record.getValue(hour));
		assertEquals(Integer.valueOf(timestamp.split(" ")[1].split(":")[1]), record.getValue(minute));
		assertEquals(Integer.valueOf(timestamp.split(" ")[1].split(":")[2].split("\\.")[0]), record.getValue(second));
	}

	@Test
	public final void testFunction4() throws Exception {
		SelectQuery q = QueryFactory.createSelectQuery();
		Field<String> constant = Functions.constant("abc");
		Field<Integer> charLength = Functions.charLength(constant).alias("len");
		Field<Integer> bitLength = Functions.bitLength(constant).alias("bitlen");
		Field<Integer> octetLength = Functions.octetLength(constant).alias("octetlen");

		q.addSelect(charLength, bitLength, octetLength);
		q.execute(connection);

		Record record = q.getResult().getRecord(0);

		assertEquals((Integer) 3, record.getValue(charLength));
		assertEquals((Integer) 24, record.getValue(bitLength));
		assertEquals((Integer) 3, record.getValue(octetLength));
	}

	@Test
	public final void testFunction5() throws Exception {
		SelectQuery q = QueryFactory.createSelectQuery(V_LIBRARY);

		Field<String> o = Functions.constant("o");
		Field<Integer> position = Functions.position(AUTHOR, o).alias("p");
		q.addSelect(AUTHOR);
		q.addSelect(position);
		q.addOrderBy(AUTHOR, ASC);

		q.execute(connection);
		Record r1 = q.getResult().getRecord(1); // George Orwell
		Record r2 = q.getResult().getRecord(2); // Paulo Coelho

		assertEquals((Integer) 3, r1.getValue(position));
		assertEquals((Integer) 5, r2.getValue(position));
	}
}
