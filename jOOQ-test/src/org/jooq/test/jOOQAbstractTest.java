/**
 * Copyright (c) 2010, Lukas Eder, lukas.eder@gmail.com
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
import static junit.framework.Assert.assertTrue;
import static org.jooq.SortOrder.ASC;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.io.FileUtils;
import org.jooq.Comparator;
import org.jooq.Configuration;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectQuery;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.Functions;
import org.jooq.impl.QueryFactory;
import org.jooq.impl.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Lukas Eder
 */
public abstract class jOOQAbstractTest {

    protected Connection connection;

    @Before
    public void setUp() throws Exception {
        connection = getConnection();

        Statement stmt = null;
        File file = new File(getClass().getResource(getCreateScript()).toURI());
        String allSQL = FileUtils.readFileToString(file);

        for (String sql : allSQL.split("/")) {
            try {
                if (!StringUtils.isBlank(sql)) {
                	stmt = connection.createStatement();
                	stmt.execute(sql.trim());
                }
            } catch (Exception e) {
                // There is no DROP TABLE IF EXISTS statement in Oracle, so this
                // error is expected
                if (e.getMessage().contains("ORA-00942")) {
                    continue;
                }

                // All other errors
                System.out.println("Error while executing : " + sql.trim());
                System.exit(-1);
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
        }
    }

    protected abstract Connection getConnection() throws Exception;
    protected abstract String getCreateScript();

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
        Field<Integer> f1 = Functions.constant(1).alias("f1");
        Field<Double> f2 = Functions.constant(2d).alias("f2");
        Field<String> f3 = Functions.constant("test").alias("f3");

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
        SelectQuery q = QueryFactory.createSelectQuery(getTAuthor());
        q.addSelect(getTAuthor().getFields());
        q.addOrderBy(getTAuthor_LAST_NAME());

        int rows = q.execute(connection);
        Result result = q.getResult();

        assertEquals(2, rows);
        assertEquals(2, result.getNumberOfRecords());
        assertEquals("Coelho", result.getRecord(0).getValue(getTAuthor_LAST_NAME()));
        assertEquals("Orwell", result.getRecord(1).getValue(getTAuthor_LAST_NAME()));
    }

	protected abstract Table getTAuthor();
	protected abstract TableField<String> getTAuthor_LAST_NAME();
	protected abstract TableField<Integer> getTAuthor_ID() ;
	protected abstract Table getTBook();
	protected abstract TableField<Integer> getTBook_AUTHOR_ID();
	protected abstract TableField<String> getTBook_TITLE();
	protected abstract Table getVLibrary();
	protected abstract TableField<String> getVLibrary_TITLE();
	protected abstract TableField<String> getVLibrary_AUTHOR();

    @Test
    public final void testCombinedSelectQuery() throws Exception {
        SelectQuery q1 = QueryFactory.createSelectQuery(getTBook());
        SelectQuery q2 = QueryFactory.createSelectQuery(getTBook());

        q1.addCompareCondition(getTBook_AUTHOR_ID(), 1);
        q2.addCompareCondition(getTBook_TITLE(), "Brida");

        SelectQuery combine = q1.combine(q2);

        int rows = combine.execute(connection);
        assertEquals(3, rows);
    }

    @Test
    public final void testJoinQuery() throws Exception {
   	 	// Oracle ordering behaviour is a bit different, so exclude "1984"
        SelectQuery q1 = QueryFactory.createSelectQuery(getVLibrary());
        q1.addOrderBy(getVLibrary_TITLE());
        q1.addCompareCondition(getVLibrary_TITLE(), "1984", Comparator.NOT_EQUALS);

        Table a = getTAuthor().alias("a");
        Table b = getTBook().alias("b");
        
        Field<Integer> a_authorID = a.getField(getTAuthor_ID());
        Field<Integer> b_authorID = b.getField(getTBook_AUTHOR_ID());
        Field<String> b_title = b.getField(getTBook_TITLE());
        
		SelectQuery q2 = QueryFactory.createSelectQuery(a);
		q2.addJoin(b, b_authorID, a_authorID);
		q2.addCompareCondition(b_title, "1984", Comparator.NOT_EQUALS);
        q2.addOrderBy(Functions.lower(b_title));

        int rows1 = q1.execute(connection);
        int rows2 = q2.execute(connection);

        assertEquals(3, rows1);
        assertEquals(3, rows2);

        Result result1 = q1.getResult();
        Result result2 = q2.getResult();

        assertEquals("Animal Farm", result1.getRecord(0).getValue(getVLibrary_TITLE()));
        assertEquals("Animal Farm", result2.getRecord(0).getValue(b_title));

        assertEquals("Brida", result1.getRecord(1).getValue(getVLibrary_TITLE()));
        assertEquals("Brida", result2.getRecord(1).getValue(b_title));

        assertEquals("O Alquimista", result1.getRecord(2).getValue(getVLibrary_TITLE()));
        assertEquals("O Alquimista", result2.getRecord(2).getValue(b_title));
    }

    @Test
    public final void testFunction3() throws Exception {
        SelectQuery q1 = QueryFactory.createSelectQuery();
        Field<Timestamp> now = Functions.currentTimestamp();
        Field<Timestamp> ts = now.alias("ts");
        Field<Date> date = Functions.currentDate().alias("d");
        Field<Time> time = Functions.currentTime().alias("t");

        Field<Integer> year = Functions.extract(now, DatePart.YEAR).alias("y");
        Field<Integer> month = Functions.extract(now, DatePart.MONTH).alias("m");
        Field<Integer> day = Functions.extract(now, DatePart.DAY).alias("dd");
        Field<Integer> hour = Functions.extract(now, DatePart.HOUR).alias("h");
        Field<Integer> minute = Functions.extract(now, DatePart.MINUTE).alias("mn");
        Field<Integer> second = Functions.extract(now, DatePart.SECOND).alias("sec");

        q1.addSelect(ts, date, time, year, month, day, hour, minute, second);
        q1.execute(connection);

        Record record = q1.getResult().getRecord(0);
        String timestamp = record.getValue(ts).toString().replaceFirst("\\.\\d{1,3}$", "");

        assertEquals(timestamp.split(" ")[0], record.getValue(date).toString());
        assertEquals(timestamp.split(" ")[1], record.getValue(time).toString());

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

        switch (Configuration.getInstance().getDialect()) {
        case HSQLDB:
        	// HSQLDB uses Java-style characters (16 bit)
        	assertEquals((Integer) 48, record.getValue(bitLength));
        	assertEquals((Integer) 6, record.getValue(octetLength));
        	break;
        default:
        	assertEquals((Integer) 24, record.getValue(bitLength));
        	assertEquals((Integer) 3, record.getValue(octetLength));
        	break;
        }
    }

    @Test
    public final void testFunction5() throws Exception {
        SelectQuery q = QueryFactory.createSelectQuery(getVLibrary());

        Field<String> o = Functions.constant("o");
        Field<Integer> position = Functions.position(getVLibrary_AUTHOR(), o).alias("p");
        q.addSelect(getVLibrary_AUTHOR());
        q.addSelect(position);
        q.addOrderBy(getVLibrary_AUTHOR(), ASC);

        q.execute(connection);
        Record r1 = q.getResult().getRecord(1); // George Orwell
        Record r2 = q.getResult().getRecord(2); // Paulo Coelho

        assertEquals((Integer) 3, r1.getValue(position));
        assertEquals((Integer) 5, r2.getValue(position));
    }
}
