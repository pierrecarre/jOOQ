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
import static junit.framework.Assert.fail;
import static org.jooq.SortOrder.ASC;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jooq.CombineOperator;
import org.jooq.Comparator;
import org.jooq.DatePart;
import org.jooq.DeleteQuery;
import org.jooq.Field;
import org.jooq.InsertQuery;
import org.jooq.InsertSelectQuery;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectQuery;
import org.jooq.SimpleSelectQuery;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableRecord;
import org.jooq.UpdatableRecord;
import org.jooq.UpdateQuery;
import org.jooq.impl.Factory;
import org.jooq.impl.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Lukas Eder
 */
public abstract class jOOQAbstractTest<A extends UpdatableRecord<A>, B extends UpdatableRecord<B>, L extends TableRecord<L>> {

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
            }
            catch (Exception e) {
                // There is no DROP TABLE IF EXISTS statement in Oracle, so this
                // error is expected
                if (e.getMessage().contains("ORA-00942")) {
                    continue;
                }

                // All other errors
                System.out.println("Error while executing : " + sql.trim());
                System.out.println();
                System.out.println();
                e.printStackTrace();

                System.exit(-1);
            }
            finally {
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
        SelectQuery q = create().selectQuery();
        Field<Integer> f1 = create().functions().constant(1).as("f1");
        Field<Double> f2 = create().functions().constant(2d).as("f2");
        Field<String> f3 = create().functions().constant("test").as("f3");

        q.addSelect(f1);
        q.addSelect(f2);
        q.addSelect(f3);

        int i = q.execute();
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
        SimpleSelectQuery<A> q = create().selectQuery(getTAuthor());
        q.addSelect(getTAuthor().getFields());
        q.addOrderBy(getTAuthor_LAST_NAME());

        int rows = q.execute();
        Result<A> result = q.getResult();

        assertEquals(2, rows);
        assertEquals(2, result.getNumberOfRecords());
        assertEquals("Coelho", result.getRecord(0).getValue(getTAuthor_LAST_NAME()));
        assertEquals("Orwell", result.getRecord(1).getValue(getTAuthor_LAST_NAME()));

        assertFalse(result.getRecord(0).hasChangedValues());
        result.getRecord(0).setValue(getTAuthor_LAST_NAME(), "Coelhinho");
        assertTrue(result.getRecord(0).hasChangedValues());
    }

    @Test
    public final void testGrouping() throws Exception {
        Field<Integer> count = create().functions().count().as("c");
        SelectQuery q = create().select(getTBook_AUTHOR_ID(), count).from(getTBook()).groupBy(getTBook_AUTHOR_ID()).getQuery();

        int rows = q.execute();
        Result<Record> result = q.getResult();

        assertEquals(2, rows);
        assertEquals(2, result.getNumberOfRecords());
        assertEquals(2, (int) result.getRecord(0).getValue(count));
        assertEquals(2, (int) result.getRecord(1).getValue(count));
    }

    @Test
    public final void testInsertUpdateDelete() throws Exception {
        InsertQuery<A> i = create().insertQuery(getTAuthor());
        i.addValue(getTAuthor_ID(), 100);
        i.addValue(getTAuthor_FIRST_NAME(), "Hermann");
        i.addValue(getTAuthor_LAST_NAME(), "Hesse");
        i.addValue(getTAuthor_DATE_OF_BIRTH(), new Date(System.currentTimeMillis()));
        i.addValue(getTAuthor_YEAR_OF_BIRTH(), 2010);
        assertEquals(1, i.execute());

        UpdateQuery<A> u = create().updateQuery(getTAuthor());
        u.addValue(getTAuthor_FIRST_NAME(), "Hermie");
        u.addCompareCondition(getTAuthor_ID(), 100);
        assertEquals(1, u.execute());

        DeleteQuery<A> d = create().deleteQuery(getTAuthor());
        d.addCompareCondition(getTAuthor_ID(), 100);
        assertEquals(1, d.execute());
    }

    @Test
    public final void testInsertSelect() throws Exception {
        InsertSelectQuery i = create().insertQuery(getTAuthor(), create().select(
            create().functions().constant(1000),
            create().functions().constant("Lukas"),
            create().functions().constant("Eder"),
            create().functions().constant(new Date(363589200000L)),
            create().functions().constant(1981)).getQuery());

        i.execute();

        A author = create().manager().selectOne(getTAuthor(), getTAuthor_FIRST_NAME(), "Lukas");
        assertEquals("Lukas", author.getValue(getTAuthor_FIRST_NAME()));
        assertEquals("Eder", author.getValue(getTAuthor_LAST_NAME()));
        assertEquals(Integer.valueOf(1981), author.getValue(getTAuthor_YEAR_OF_BIRTH()));
    }

    @Test
    public final void testUpdateSelect() throws Exception {
        Table<A> a1 = getTAuthor().as("a1");
        Table<A> a2 = getTAuthor().as("a2");
        Field<String> f1 = a1.getField(getTAuthor_FIRST_NAME());
        Field<String> f2 = a2.getField(getTAuthor_LAST_NAME());

        UpdateQuery<A> u = create().updateQuery(a1);
        u.addValue(f1, create().select(f2).from(a2).where(f1.equal(f2)).getQuery().<String>asField());
    }

    @Test
    public final void testBlobAndClob() throws Exception {
        B book = create().manager().selectOne(getTBook(), getTBook_TITLE(), "1984");

        assertTrue(book.getValue(getTBook_CONTENT_TEXT()).contains("doublethink"));
        assertEquals(null, book.getValue(getTBook_CONTENT_PDF()));

        book.setValue(getTBook_CONTENT_TEXT(), "Blah blah");
        book.setValue(getTBook_CONTENT_PDF(), "Blah blah".getBytes());
        book.store();

        book = create().manager().selectOne(getTBook(), getTBook_TITLE(), "1984");

        assertEquals("Blah blah", book.getValue(getTBook_CONTENT_TEXT()));
        assertEquals("Blah blah", new String(book.getValue(getTBook_CONTENT_PDF())));
    }

    @Test
    public final void testManager() throws Exception {
        List<A> select = create().manager().select(getTAuthor());
        assertEquals(2, select.size());

        select = create().manager().select(getTAuthor(), getTAuthor_FIRST_NAME(), "Paulo");
        assertEquals(1, select.size());
        assertEquals("Paulo", select.get(0).getValue(getTAuthor_FIRST_NAME()));

        try {
            create().manager().selectOne(getTAuthor());
            fail();
        }
        catch (Exception expected) {}

        A selectOne = create().manager().selectOne(getTAuthor(), getTAuthor_FIRST_NAME(), "Paulo");
        assertEquals("Paulo", selectOne.getValue(getTAuthor_FIRST_NAME()));
    }

    @Test
    public final void testReferentials() throws Exception {
        SimpleSelectQuery<B> q = create().selectQuery(getTBook());
        q.addCompareCondition(getTBook_TITLE(), "1984");
        q.execute();
        Result<B> result = q.getResult();

        B book = result.getRecord(0);
        Method getTAuthor = book.getClass().getMethod("getTAuthor");

        Record author = (Record) getTAuthor.invoke(book);
        assertEquals("Orwell", author.getValue(getTAuthor_LAST_NAME()));

        Method getTBooks = author.getClass().getMethod("getTBooks");

        List<?> books = (List<?>) getTBooks.invoke(author);

        assertEquals(2, books.size());
    }

    @Test
    public final void testORMapper() throws Exception {
        B book = create().newRecord(getTBook());
        try {
            book.refresh();
        }
        catch (SQLException expected) {}

        // Fetch the original record
        SimpleSelectQuery<B> q = create().selectQuery(getTBook());
        q.addCompareCondition(getTBook_TITLE(), "1984");
        q.execute();
        Result<B> result = q.getResult();

        // Another copy of the original record
        book = create().manager().selectOne(getTBook(), getTBook_TITLE(), "1984");

        // Modify and store the original record
        UpdatableRecord<B> record = result.getRecord(0);
        Integer id = record.getValue(getTBook_ID());
        record.setValue(getTBook_TITLE(), "1985");
        record.store();

        // Fetch the modified record
        q = create().selectQuery(getTBook());
        q.addCompareCondition(getTBook_ID(), id);
        q.execute();
        result = q.getResult();
        record = result.getRecord(0);

        // Refresh the other copy of the original record
        book.refresh();

        assertEquals(id, record.getValue(getTBook_ID()));
        assertEquals("1985", record.getValue(getTBook_TITLE()));
        assertEquals(id, book.getValue(getTBook_ID()));
        assertEquals("1985", book.getValue(getTBook_TITLE()));

        // Delete the modified record
        record.delete();

        // Fetch the remaining records
        q.execute();
        result = q.getResult();

        assertEquals(0, result.getNumberOfRecords());
    }

    protected abstract Table<A> getTAuthor();

    protected abstract TableField<A, String> getTAuthor_LAST_NAME();

    protected abstract TableField<A, String> getTAuthor_FIRST_NAME();

    protected abstract TableField<A, Date> getTAuthor_DATE_OF_BIRTH();

    protected abstract TableField<A, Integer> getTAuthor_YEAR_OF_BIRTH();

    protected abstract TableField<A, Integer> getTAuthor_ID();

    protected abstract Table<B> getTBook();

    protected abstract TableField<B, Integer> getTBook_ID();

    protected abstract TableField<B, Integer> getTBook_AUTHOR_ID();

    protected abstract TableField<B, String> getTBook_TITLE();

    protected abstract TableField<B, Integer> getTBook_PUBLISHED_IN();

    protected abstract TableField<B, String> getTBook_CONTENT_TEXT();

    protected abstract TableField<B, byte[]> getTBook_CONTENT_PDF();

    protected abstract Table<L> getVLibrary();

    protected abstract TableField<L, String> getVLibrary_TITLE();

    protected abstract TableField<L, String> getVLibrary_AUTHOR();

    protected abstract Factory create() throws Exception;

    @Test
    public final void testCombinedSelectQuery() throws Exception {
        SelectQuery q1 = create().selectQuery();
        SelectQuery q2 = create().selectQuery();

        q1.addFrom(getTBook());
        q2.addFrom(getTBook());

        q1.addCompareCondition(getTBook_AUTHOR_ID(), 1);
        q2.addCompareCondition(getTBook_TITLE(), "Brida");

        // Use union all because of clob's
        SelectQuery combine = q1.combine(CombineOperator.UNION_ALL, q2);

        int rows = combine.execute();
        assertEquals(3, rows);
    }

    @Test
    public final void testJoinQuery() throws Exception {
        // Oracle ordering behaviour is a bit different, so exclude "1984"
        SimpleSelectQuery<L> q1 = create().selectQuery(getVLibrary());
        q1.addOrderBy(getVLibrary_TITLE());
        q1.addCompareCondition(getVLibrary_TITLE(), "1984", Comparator.NOT_EQUALS);

        Table<A> a = getTAuthor().as("a");
        Table<B> b = getTBook().as("b");

        Field<Integer> a_authorID = a.getField(getTAuthor_ID());
        Field<Integer> b_authorID = b.getField(getTBook_AUTHOR_ID());
        Field<String> b_title = b.getField(getTBook_TITLE());

        SelectQuery q2 = create().selectQuery();
        q2.addFrom(a);
        q2.addJoin(b, b_authorID, a_authorID);
        q2.addCompareCondition(b_title, "1984", Comparator.NOT_EQUALS);
        q2.addOrderBy(create().functions().lower(b_title));

        int rows1 = q1.execute();
        int rows2 = q2.execute();

        assertEquals(3, rows1);
        assertEquals(3, rows2);

        Result<L> result1 = q1.getResult();
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
        SelectQuery q1 = create().selectQuery();
        Field<Timestamp> now = create().functions().currentTimestamp();
        Field<Timestamp> ts = now.as("ts");
        Field<Date> date = create().functions().currentDate().as("d");
        Field<Time> time = create().functions().currentTime().as("t");

        Field<Integer> year = create().functions().extract(now, DatePart.YEAR).as("y");
        Field<Integer> month = create().functions().extract(now, DatePart.MONTH).as("m");
        Field<Integer> day = create().functions().extract(now, DatePart.DAY).as("dd");
        Field<Integer> hour = create().functions().extract(now, DatePart.HOUR).as("h");
        Field<Integer> minute = create().functions().extract(now, DatePart.MINUTE).as("mn");
        Field<Integer> second = create().functions().extract(now, DatePart.SECOND).as("sec");

        q1.addSelect(ts, date, time, year, month, day, hour, minute, second);
        q1.execute();

        Record record = q1.getResult().getRecord(0);
        String timestamp = record.getValue(ts).toString().replaceFirst("\\.\\d+$", "");

        assertEquals(timestamp.split(" ")[0], record.getValue(date).toString());

        // Weird behaviour in postgres
        // See also interesting thread:
        // http://archives.postgresql.org/pgsql-jdbc/2010-09/msg00037.php
        if (create().getDialect() != SQLDialect.POSTGRES) {
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
        SelectQuery q = create().selectQuery();
        Field<String> constant = create().functions().constant("abc");
        Field<Integer> charLength = create().functions().charLength(constant);
        Field<Integer> bitLength = create().functions().bitLength(constant);
        Field<Integer> octetLength = create().functions().octetLength(constant);

        q.addSelect(charLength, bitLength, octetLength);
        q.execute();

        Record record = q.getResult().getRecord(0);

        assertEquals(Integer.valueOf(3), record.getValue(charLength));

        switch (create().getDialect()) {
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
        SimpleSelectQuery<L> q = create().selectQuery(getVLibrary());

        Field<Integer> position = create().functions().position(getVLibrary_AUTHOR(), "o").as("p");
        q.addSelect(getVLibrary_AUTHOR());
        q.addSelect(position);
        q.addOrderBy(getVLibrary_AUTHOR(), ASC);

        q.execute();
        Record r1 = q.getResult().getRecord(1); // George Orwell
        Record r2 = q.getResult().getRecord(2); // Paulo Coelho

        assertEquals(Integer.valueOf(3), r1.getValue(position));
        assertEquals(Integer.valueOf(5), r2.getValue(position));

        // Implicit check on the rownum function in oracle dialect
        L library = create().manager().selectAny(getVLibrary());
        assertTrue(library != null);
    }

    @Test
    public final void testCaseStatement() throws Exception {
        Field<String> case1 = create().functions().decode()
            .value(getTBook_PUBLISHED_IN()).when(0, "ancient book").as("case1");
        Field<String> case2 = create().functions().decode()
            .when(getTBook_PUBLISHED_IN().equal(1948), "probably orwell")
            .when(getTBook_PUBLISHED_IN().equal(1988), "probably coelho")
            .otherwise("don't know").as("case2");

        SelectQuery query = create().selectQuery();
        query.addSelect(case1, case2);
        query.addFrom(getTBook());
        query.addOrderBy(getTBook_PUBLISHED_IN());
        query.execute();

        Result<Record> result = query.getResult();
        assertEquals(null, result.getValue(0, case1));
        assertEquals(null, result.getValue(1, case1));
        assertEquals(null, result.getValue(2, case1));
        assertEquals(null, result.getValue(3, case1));

        // Note: trims are necessary, as certain databases use
        // CHAR datatype here, not VARCHAR
        assertEquals("don't know", result.getValue(0, case2).trim());
        assertEquals("probably orwell", result.getValue(1, case2).trim());
        assertEquals("probably coelho", result.getValue(2, case2).trim());
        assertEquals("don't know", result.getValue(3, case2).trim());
    }
}
