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
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.jooq.SortOrder.ASC;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jooq.Comparator;
import org.jooq.Configuration;
import org.jooq.DatePart;
import org.jooq.DeleteQuery;
import org.jooq.Field;
import org.jooq.InsertQuery;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectQuery;
import org.jooq.SimpleSelectQuery;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UpdatableRecord;
import org.jooq.UpdateQuery;
import org.jooq.impl.Create;
import org.jooq.impl.Functions;
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
        SelectQuery<?> q = Create.selectQuery();
        Field<Integer> f1 = Functions.constant(1).as("f1");
        Field<Double> f2 = Functions.constant(2d).as("f2");
        Field<String> f3 = Functions.constant("test").as("f3");

        q.addSelect(f1);
        q.addSelect(f2);
        q.addSelect(f3);

        int i = q.execute(connection);
        Result<?> result = q.getResult();

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

        assertEquals(Integer.valueOf(1), result.getRecords().get(0).getValue(f1));
        assertEquals(2d, result.getRecords().get(0).getValue(f2));
        assertEquals("test", result.getRecords().get(0).getValue(f3));
    }

    @Test
    public final void testSelectQuery() throws Exception {
        SimpleSelectQuery<? extends Record<?>> q = Create.selectQuery(getTAuthor());
        q.addSelect(getTAuthor().getFields());
        q.addOrderBy(getTAuthor_LAST_NAME());

        int rows = q.execute(connection);
        Result<? extends Record<?>> result = q.getResult();

        assertEquals(2, rows);
        assertEquals(2, result.getNumberOfRecords());
        assertEquals("Coelho", result.getRecord(0).getValue(getTAuthor_LAST_NAME()));
        assertEquals("Orwell", result.getRecord(1).getValue(getTAuthor_LAST_NAME()));

        assertFalse(result.getRecord(0).hasChangedValues());
        result.getRecord(0).setValue(getTAuthor_LAST_NAME(), "Coelhinho");
        assertTrue(result.getRecord(0).hasChangedValues());
    }

    @Test
    public final void testInsertUpdateDelete() throws Exception {
    	InsertQuery<? extends Record<?>> i = Create.insertQuery(getTAuthor());
    	i.addValue(getTAuthor_ID(), 100);
    	i.addValue(getTAuthor_FIRST_NAME(), "Hermann");
    	i.addValue(getTAuthor_LAST_NAME(), "Hesse");
    	i.addValue(getTAuthor_DATE_OF_BIRTH(), new Date(System.currentTimeMillis()));
    	i.addValue(getTAuthor_YEAR_OF_BIRTH(), 2010);
    	assertEquals(1, i.execute(connection));

    	UpdateQuery<? extends Record<?>> u = Create.updateQuery(getTAuthor());
    	u.addValue(getTAuthor_FIRST_NAME(), "Hermie");
    	u.addCompareCondition(getTAuthor_ID(), 100);
    	assertEquals(1, u.execute(connection));

    	DeleteQuery<? extends Record<?>> d = Create.deleteQuery(getTAuthor());
    	d.addCompareCondition(getTAuthor_ID(), 100);
    	assertEquals(1, d.execute(connection));
    }

    @Test
    public final void testReferentials() throws Exception {
    	SimpleSelectQuery<?> q = Create.selectQuery(getTBook());
        q.addCompareCondition(getTBook_TITLE(), "1984");
        q.execute(connection);
        Result<?> result = q.getResult();

        Record<?> book = result.getRecord(0);
        Method getTAuthor = book.getClass().getMethod("getTAuthor", Connection.class);

        Record<?> author = (Record<?>) getTAuthor.invoke(book, connection);
        assertEquals("Orwell", author.getValue(getTAuthor_LAST_NAME()));

        Method getTBooks = author.getClass().getMethod("getTBooks", Connection.class);

		List<?> books = (List<?>) getTBooks.invoke(author, connection);

        assertEquals(2, books.size());
    }

	@Test
    public final void testORMapper() throws Exception {

    	// Fetch the original record
    	SimpleSelectQuery<? extends Record<?>> q = Create.selectQuery(getTBook());
        q.addCompareCondition(getTBook_TITLE(), "1984");
        q.execute(connection);
        Result<?> result = q.getResult();

        // Modify and store the original record
		UpdatableRecord<?> record = (UpdatableRecord<?>) result.getRecord(0);
        Integer id = record.getValue(getTBook_ID());
		record.setValue(getTBook_TITLE(), "1985");
		record.store(connection);

		// Fetch the modified record
		q = Create.selectQuery(getTBook());
		q.addCompareCondition(getTBook_ID(), id);
		q.execute(connection);
		result = q.getResult();
        record = (UpdatableRecord<?>) result.getRecord(0);

		assertEquals(id, record.getValue(getTBook_ID()));
		assertEquals("1985", record.getValue(getTBook_TITLE()));

		// Delete the modified record
		record.delete(connection);

		// Fetch the remaining records
		q.execute(connection);
		result = q.getResult();

		assertEquals(0, result.getNumberOfRecords());
    }

	protected abstract Table<? extends Record<?>> getTAuthor();
	protected abstract TableField<? extends Record<?>, String> getTAuthor_LAST_NAME();
	protected abstract TableField<? extends Record<?>, String> getTAuthor_FIRST_NAME();
	protected abstract TableField<? extends Record<?>, Date> getTAuthor_DATE_OF_BIRTH();
	protected abstract TableField<? extends Record<?>, Integer> getTAuthor_YEAR_OF_BIRTH();
	protected abstract TableField<? extends Record<?>, Integer> getTAuthor_ID() ;
	protected abstract Table<? extends Record<?>> getTBook();
	protected abstract TableField<? extends Record<?>, Integer> getTBook_ID();
	protected abstract TableField<? extends Record<?>, Integer> getTBook_AUTHOR_ID();
	protected abstract TableField<? extends Record<?>, String> getTBook_TITLE();
	protected abstract Table<? extends Record<?>> getVLibrary();
	protected abstract TableField<? extends Record<?>, String> getVLibrary_TITLE();
	protected abstract TableField<? extends Record<?>, String> getVLibrary_AUTHOR();

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
    public final void testCombinedSelectQuery() throws Exception {
    	SelectQuery q1 = Create.selectQuery();
    	SelectQuery q2 = Create.selectQuery();

		q1.addFrom(getTBook());
    	q2.addFrom(getTBook());

        q1.addCompareCondition(getTBook_AUTHOR_ID(), 1);
        q2.addCompareCondition(getTBook_TITLE(), "Brida");

        SimpleSelectQuery<?> combine = q1.combine(q2);

        int rows = combine.execute(connection);
        assertEquals(3, rows);
    }

    @Test
    public final void testJoinQuery() throws Exception {
   	 	// Oracle ordering behaviour is a bit different, so exclude "1984"
    	SimpleSelectQuery<?> q1 = Create.selectQuery(getVLibrary());
        q1.addOrderBy(getVLibrary_TITLE());
        q1.addCompareCondition(getVLibrary_TITLE(), "1984", Comparator.NOT_EQUALS);

        Table<?> a = getTAuthor().as("a");
        Table<?> b = getTBook().as("b");

        Field<Integer> a_authorID = a.getField(getTAuthor_ID());
        Field<Integer> b_authorID = b.getField(getTBook_AUTHOR_ID());
        Field<String> b_title = b.getField(getTBook_TITLE());

		SelectQuery<?> q2 = Create.selectQuery();
		q2.addFrom(a);
		q2.addJoin(b, b_authorID, a_authorID);
		q2.addCompareCondition(b_title, "1984", Comparator.NOT_EQUALS);
        q2.addOrderBy(Functions.lower(b_title));

        int rows1 = q1.execute(connection);
        int rows2 = q2.execute(connection);

        assertEquals(3, rows1);
        assertEquals(3, rows2);

        Result<?> result1 = q1.getResult();
        Result<?> result2 = q2.getResult();

        assertEquals("Animal Farm", result1.getRecord(0).getValue(getVLibrary_TITLE()));
        assertEquals("Animal Farm", result2.getRecord(0).getValue(b_title));

        assertEquals("Brida", result1.getRecord(1).getValue(getVLibrary_TITLE()));
        assertEquals("Brida", result2.getRecord(1).getValue(b_title));

        assertEquals("O Alquimista", result1.getRecord(2).getValue(getVLibrary_TITLE()));
        assertEquals("O Alquimista", result2.getRecord(2).getValue(b_title));
    }

    @Test
    public final void testFunction3() throws Exception {
        SelectQuery<? extends Record<?>> q1 = Create.selectQuery();
        Field<Timestamp> now = Functions.currentTimestamp();
        Field<Timestamp> ts = now.as("ts");
        Field<Date> date = Functions.currentDate().as("d");
        Field<Time> time = Functions.currentTime().as("t");

        Field<Integer> year = Functions.extract(now, DatePart.YEAR).as("y");
        Field<Integer> month = Functions.extract(now, DatePart.MONTH).as("m");
        Field<Integer> day = Functions.extract(now, DatePart.DAY).as("dd");
        Field<Integer> hour = Functions.extract(now, DatePart.HOUR).as("h");
        Field<Integer> minute = Functions.extract(now, DatePart.MINUTE).as("mn");
        Field<Integer> second = Functions.extract(now, DatePart.SECOND).as("sec");

        q1.addSelect(ts, date, time, year, month, day, hour, minute, second);
        q1.execute(connection);

        Record<?> record = q1.getResult().getRecord(0);
        String timestamp = record.getValue(ts).toString().replaceFirst("\\.\\d+$", "");

        assertEquals(timestamp.split(" ")[0], record.getValue(date).toString());

        // Weird behaviour in postgres
        // See also interesting thread: http://archives.postgresql.org/pgsql-jdbc/2010-09/msg00037.php
        if (Configuration.getInstance().getDialect() != SQLDialect.POSTGRES) {
        	assertEquals(timestamp.split(" ")[1], record.getValue(time).toString());
        }

        assertEquals(Integer.valueOf(timestamp.split(" ")[0].split("-")[0]), record.getValue(year));
        assertEquals(Integer.valueOf(timestamp.split(" ")[0].split("-")[1]), record.getValue(month));
        assertEquals(Integer.valueOf(timestamp.split(" ")[0].split("-")[2]), record.getValue(day));
        assertEquals(Integer.valueOf(timestamp.split(" ")[1].split(":")[0]), record.getValue(hour));
        assertEquals(Integer.valueOf(timestamp.split(" ")[1].split(":")[1]), record.getValue(minute));
        assertEquals(Integer.valueOf(timestamp.split(" ")[1].split(":")[2].split("\\.")[0]), record.getValue(second));
    }

    @Test
    public final void testFunction4() throws Exception {
        SelectQuery<? extends Record<?>> q = Create.selectQuery();
        Field<String> constant = Functions.constant("abc");
        Field<Integer> charLength = Functions.charLength(constant).as("len");
        Field<Integer> bitLength = Functions.bitLength(constant).as("bitlen");
        Field<Integer> octetLength = Functions.octetLength(constant).as("octetlen");

        q.addSelect(charLength, bitLength, octetLength);
        q.execute(connection);

        Record<?> record = q.getResult().getRecord(0);

        assertEquals(Integer.valueOf(3), record.getValue(charLength));

        switch (Configuration.getInstance().getDialect()) {
        case HSQLDB:
        	// HSQLDB uses Java-style characters (16 bit)
        	assertEquals(Integer.valueOf(48), record.getValue(bitLength));
        	assertEquals(Integer.valueOf(6), record.getValue(octetLength));
        	break;
        default:
        	assertEquals(Integer.valueOf(24), record.getValue(bitLength));
        	assertEquals(Integer.valueOf(3), record.getValue(octetLength));
        	break;
        }
    }

    @Test
    public final void testFunction5() throws Exception {
    	SimpleSelectQuery<? extends Record<?>> q = Create.selectQuery(getVLibrary());

        Field<String> o = Functions.constant("o");
        Field<Integer> position = Functions.position(getVLibrary_AUTHOR(), o).as("p");
        q.addSelect(getVLibrary_AUTHOR());
        q.addSelect(position);
        q.addOrderBy(getVLibrary_AUTHOR(), ASC);

        q.execute(connection);
        Record<?> r1 = q.getResult().getRecord(1); // George Orwell
        Record<?> r2 = q.getResult().getRecord(2); // Paulo Coelho

        assertEquals(Integer.valueOf(3), r1.getValue(position));
        assertEquals(Integer.valueOf(5), r2.getValue(position));
    }
}
