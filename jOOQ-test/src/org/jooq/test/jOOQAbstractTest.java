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

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.DatePart;
import org.jooq.DeleteQuery;
import org.jooq.EnumType;
import org.jooq.Field;
import org.jooq.InsertQuery;
import org.jooq.InsertSelectQuery;
import org.jooq.MasterDataType;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.SelectQuery;
import org.jooq.SimpleSelectQuery;
import org.jooq.StoreQuery;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableRecord;
import org.jooq.UDTRecord;
import org.jooq.UpdatableRecord;
import org.jooq.UpdateQuery;
import org.jooq.impl.Factory;
import org.jooq.impl.JooqLogger;
import org.jooq.impl.StringUtils;
import org.jooq.impl.TrueCondition;
import org.jooq.util.GenerationTool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Lukas Eder
 */
public abstract class jOOQAbstractTest<A extends UpdatableRecord<A>, B extends UpdatableRecord<B>, L extends TableRecord<L>> {

    private static final JooqLogger log = JooqLogger.getLogger(jOOQAbstractTest.class);
    protected Connection            connection;
    protected String                jdbcURL;
    protected String                jdbcSchema;

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
                // There is no DROP TABLE IF EXISTS statement in Oracle
                if (e.getMessage().contains("ORA-00942")) {
                    continue;
                }

                // There is no DROP {PROCEDURE|FUNCTION} IF EXISTS statement in
                // Oracle
                else if (e.getMessage().contains("ORA-04043")) {
                    continue;
                }

                // There is no DROP TABLE IF EXISTS statement in DB2
                else if (e.getMessage().contains("SQLCODE=-204") && e.getMessage().contains("SQLSTATE=42704")) {
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

    @After
    public void tearDown() throws Exception {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    protected final Connection getConnection() throws Exception {
        String property = System.getProperty("jdbc.properties");
        if (property == null) {
            log.error("No system property 'jdbc.properties' found");
            log.error("-----------");
            log.error("Please be sure property is set; example: -Djdbc.properties=/org/jooq/configuration/${env_var:USERNAME}/db2/library.properties");
            throw new Exception();
        }
        InputStream in = GenerationTool.class.getResourceAsStream(property);
        if (in == null) {
            log.error("Cannot find " + property);
            log.error("-----------");
            log.error("Please be sure it is located on the classpath and qualified as a classpath location.");
            log.error("If it is located at the current working directory, try adding a '/' to the path");
            throw new Exception();
        }

        Properties properties = new Properties();

        try {
            properties.load(in);
        }
        finally {
            in.close();
        }

        String driver = properties.getProperty("jdbc.Driver");
        this.jdbcURL = properties.getProperty("jdbc.URL");
        String jdbcUser = properties.getProperty("jdbc.User");
        String jdbcPassword = properties.getProperty("jdbc.Password");
        this.jdbcSchema = properties.getProperty("jdbc.Schema");

        Class.forName(driver);
        return DriverManager.getConnection(getJdbcURL(), jdbcUser, jdbcPassword);
    }

    /**
     * Gets the jdbc url.
     *
     * Subclasses can override this method to add special parameters to the url
     *
     * @return
     */
    protected String getJdbcURL() {
        return jdbcURL;
    }

    protected abstract String getCreateScript();

    protected abstract Table<A> getTAuthor();

    protected abstract TableField<A, String> getTAuthor_LAST_NAME();

    protected abstract TableField<A, String> getTAuthor_FIRST_NAME();

    protected abstract TableField<A, Date> getTAuthor_DATE_OF_BIRTH();

    protected abstract TableField<A, Integer> getTAuthor_YEAR_OF_BIRTH();

    protected abstract TableField<A, Integer> getTAuthor_ID();

    protected abstract TableField<A, ? extends UDTRecord<?>> getTAuthor_ADDRESS();

    protected abstract Table<B> getTBook();

    protected abstract TableField<B, Integer> getTBook_ID();

    protected abstract TableField<B, Integer> getTBook_AUTHOR_ID();

    protected abstract TableField<B, String> getTBook_TITLE();

    protected abstract TableField<B, ? extends Enum<?>> getTBook_LANGUAGE_ID();

    protected abstract TableField<B, Integer> getTBook_PUBLISHED_IN();

    protected abstract TableField<B, String> getTBook_CONTENT_TEXT();

    protected abstract TableField<B, byte[]> getTBook_CONTENT_PDF();

    protected abstract TableField<B, ? extends Enum<?>> getTBook_STATUS();

    protected abstract Table<L> getVLibrary();

    protected abstract TableField<L, String> getVLibrary_TITLE();

    protected abstract TableField<L, String> getVLibrary_AUTHOR();

    protected abstract Factory create() throws Exception;

    @Test
    public final void testSelectSimpleQuery() throws Exception {
        SelectQuery q = create().selectQuery();
        Field<Integer> f1 = create().constant(1).as("f1");
        Field<Double> f2 = create().constant(2d).as("f2");
        Field<String> f3 = create().constant("test").as("f3");

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
        SelectQuery q = create().selectQuery();
        q.addFrom(getTAuthor());
        q.addSelect(getTAuthor().getFields());
        q.addOrderBy(getTAuthor_LAST_NAME());

        int rows = q.execute();
        Result<?> result = q.getResult();

        assertEquals(2, rows);
        assertEquals(2, result.getNumberOfRecords());
        assertEquals("Coelho", result.getRecord(0).getValue(getTAuthor_LAST_NAME()));
        assertEquals("Orwell", result.getRecord(1).getValue(getTAuthor_LAST_NAME()));

        assertFalse(result.getRecord(0).hasChangedValues());
        result.getRecord(0).setValue(getTAuthor_LAST_NAME(), "Coelhinho");
        assertTrue(result.getRecord(0).hasChangedValues());
    }

    @Test
    public final void testTypeConversions() throws Exception {
        SelectQuery q = create().selectQuery();
        q.addFrom(getTAuthor());
        q.addConditions(getTAuthor_LAST_NAME().equal("Coelho"));
        Record record = q.fetchOne();

        assertEquals("Paulo", record.getValue(getTAuthor_FIRST_NAME()));
        assertEquals("Paulo", record.getStringValue(getTAuthor_FIRST_NAME()));

        assertEquals(Integer.valueOf("1947"), record.getValue(getTAuthor_YEAR_OF_BIRTH()));
        assertEquals(Short.valueOf("1947"), record.getShortValue(getTAuthor_YEAR_OF_BIRTH()));
        assertEquals(Long.valueOf("1947"), record.getLongValue(getTAuthor_YEAR_OF_BIRTH()));
        assertEquals(new BigInteger("1947"), record.getBigIntegerValue(getTAuthor_YEAR_OF_BIRTH()));
        assertEquals(Float.valueOf("1947"), record.getFloatValue(getTAuthor_YEAR_OF_BIRTH()));
        assertEquals(Double.valueOf("1947"), record.getDoubleValue(getTAuthor_YEAR_OF_BIRTH()));
        assertEquals(new BigDecimal("1947"), record.getBigDecimalValue(getTAuthor_YEAR_OF_BIRTH()));

        long dateOfBirth = record.getValue(getTAuthor_DATE_OF_BIRTH()).getTime();
        assertEquals(dateOfBirth, record.getDateValue(getTAuthor_DATE_OF_BIRTH()).getTime());
        assertEquals(dateOfBirth, record.getTimestampValue(getTAuthor_DATE_OF_BIRTH()).getTime());
        assertEquals(dateOfBirth, record.getTimeValue(getTAuthor_DATE_OF_BIRTH()).getTime());
    }

    @Test
    public final void testConditionalSelect() throws Exception {
        Condition c = TrueCondition.TRUE_CONDITION;

        assertEquals(4, create().selectFrom(getTBook()).where(c).execute());

        c = c.and(getTBook_PUBLISHED_IN().greaterThan(1945));
        assertEquals(3, create().selectFrom(getTBook()).where(c).execute());

        c = c.not();
        assertEquals(1, create().selectFrom(getTBook()).where(c).execute());

        c = c.or(getTBook_AUTHOR_ID().equal(
            create().select(getTAuthor_ID()).from(getTAuthor()).where(getTAuthor_FIRST_NAME().equal("Paulo"))));
        assertEquals(3, create().selectFrom(getTBook()).where(c).execute());
    }

    @Test
    public final void testSubSelect() throws Exception {
        assertEquals(
            3,
            create().selectFrom(getTBook())
                .where(getTBook_TITLE().notIn(create()
                    .select(getTBook_TITLE())
                    .from(getTBook())
                    .where(getTBook_TITLE().in("1984"))))
                .execute());

        assertEquals(
            3,
            create()
                .selectFrom(getTBook())
                .whereNotExists(create()
                    .select(create().constant(1))
                    .from(getTAuthor())
                    .where(getTAuthor_YEAR_OF_BIRTH().greaterOrEqual(getTBook_PUBLISHED_IN())))

                // Add additional useless queries to check query correctness
                .orNotExists(create().select())
                .andExists(create().select()).execute());
    }

    @Test
    public final void testDistinctQuery() throws Exception {
        Result<Record> result = create()
            .selectDistinct(getTBook_AUTHOR_ID())
            .from(getTBook())
            .orderBy(getTBook_AUTHOR_ID())
            .fetch();

        assertEquals(2, result.getNumberOfRecords());
        assertEquals(Integer.valueOf(1), result.getRecord(0).getValue(getTBook_AUTHOR_ID()));
        assertEquals(Integer.valueOf(2), result.getRecord(1).getValue(getTBook_AUTHOR_ID()));
    }

    @Test
    public final void testFetch() throws Exception {
        SelectQuery q = create().selectQuery();
        q.addFrom(getTAuthor());
        q.addSelect(getTAuthor().getFields());
        q.addOrderBy(getTAuthor_LAST_NAME());

        Result<?> result = q.fetch();

        assertEquals(2, result.getNumberOfRecords());
        assertEquals("Coelho", result.getRecord(0).getValue(getTAuthor_LAST_NAME()));
        assertEquals("Orwell", result.getRecord(1).getValue(getTAuthor_LAST_NAME()));

        assertFalse(result.getRecord(0).hasChangedValues());
        result.getRecord(0).setValue(getTAuthor_LAST_NAME(), "Coelhinho");
        assertTrue(result.getRecord(0).hasChangedValues());

        try {
            q.fetchOne();
            fail();
        }
        catch (Exception expected) {}

        Record record = q.fetchAny();

        assertEquals("Coelho", record.getValue(getTAuthor_LAST_NAME()));

        assertFalse(record.hasChangedValues());
        record.setValue(getTAuthor_LAST_NAME(), "Coelhinho");
        assertTrue(record.hasChangedValues());
    }

    @Test
    public final void testGrouping() throws Exception {
        Field<Integer> count = create().count().as("c");
        Result<Record> result = create()
            .select(getTBook_AUTHOR_ID(), count)
            .from(getTBook())
            .groupBy(getTBook_AUTHOR_ID()).fetch();

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

        // Check insertion of UDTs and Enums if applicable
        if (getTAuthor_ADDRESS() != null) {
            addAddressValue(i, getTAuthor_ADDRESS());
        }

        assertEquals(1, i.execute());

        UpdateQuery<A> u = create().updateQuery(getTAuthor());
        u.addValue(getTAuthor_FIRST_NAME(), "Hermie");
        u.addCompareCondition(getTAuthor_ID(), 100);
        assertEquals(1, u.execute());

        A hermie = create().fetchOne(getTAuthor(), getTAuthor_FIRST_NAME(), "Hermie");
        if (getTAuthor_ADDRESS() != null) {
            UDTRecord<?> address = hermie.getValue(getTAuthor_ADDRESS());
            Object street1 = address.getClass().getMethod("getStreet").invoke(address);
            Object street2 = street1.getClass().getMethod("getStreet").invoke(street1);
            assertEquals("Bahnhofstrasse", street2);
        }

        DeleteQuery<A> d = create().deleteQuery(getTAuthor());
        d.addCompareCondition(getTAuthor_ID(), 100);
        assertEquals(1, d.execute());
    }

    // Generic type safety...
    private final <T extends UDTRecord<?>> void addAddressValue(StoreQuery<?> q, Field<T> field) throws Exception {
        Class<? extends T> addressType = field.getType();
        Class<?> countryType = addressType.getMethod("getCountry").getReturnType();
        Class<?> streetType = addressType.getMethod("getStreet").getReturnType();

        Object country = null;
        try {
            countryType.getMethod("valueOf", String.class).invoke(countryType, "Germany");
        }
        catch (NoSuchMethodException e) {
            country = "Germany";
        }

        Object street = streetType.newInstance();
        T address = addressType.newInstance();

        streetType.getMethod("setStreet", String.class).invoke(street, "Bahnhofstrasse");
        streetType.getMethod("setNo", String.class).invoke(street, "1");

        addressType.getMethod("setCountry", countryType).invoke(address, country);
        addressType.getMethod("setCity", String.class).invoke(address, "Calw");
        addressType.getMethod("setStreet", streetType).invoke(address, street);

        q.addValue(field, address);
    }

    @Test
    public final void testInsertSelect() throws Exception {
        InsertSelectQuery i = create().insertQuery(
            getTAuthor(),
            create().select(
                create().constant(1000),
                create().constant("Lukas"),
                create().constant("Eder"),
                create().constant(new Date(363589200000L)),
                create().constant(1981),
                create().NULL()));

        i.execute();

        A author = create().fetchOne(getTAuthor(), getTAuthor_FIRST_NAME(), "Lukas");
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
        u.addValue(f1, create().select(f2).from(a2).where(f1.equal(f2)).<String> asField());
    }

    @Test
    public final void testBlobAndClob() throws Exception {
        B book = create().fetchOne(getTBook(), getTBook_TITLE(), "1984");

        assertTrue(book.getValue(getTBook_CONTENT_TEXT()).contains("doublethink"));
        assertEquals(null, book.getValue(getTBook_CONTENT_PDF()));

        book.setValue(getTBook_CONTENT_TEXT(), "Blah blah");
        book.setValue(getTBook_CONTENT_PDF(), "Blah blah".getBytes());
        book.store();

        book = create().fetchOne(getTBook(), getTBook_TITLE(), "1984");

        assertEquals("Blah blah", book.getValue(getTBook_CONTENT_TEXT()));
        assertEquals("Blah blah", new String(book.getValue(getTBook_CONTENT_PDF())));
    }

    @Test
    public final void testManager() throws Exception {
        List<A> select = create().fetch(getTAuthor());
        assertEquals(2, select.size());

        select = create().fetch(getTAuthor(), getTAuthor_FIRST_NAME(), "Paulo");
        assertEquals(1, select.size());
        assertEquals("Paulo", select.get(0).getValue(getTAuthor_FIRST_NAME()));

        try {
            create().fetchOne(getTAuthor());
            fail();
        }
        catch (Exception expected) {}

        A selectOne = create().fetchOne(getTAuthor(), getTAuthor_FIRST_NAME(), "Paulo");
        assertEquals("Paulo", selectOne.getValue(getTAuthor_FIRST_NAME()));
    }

    @Test
    public final void testReferentials() throws Exception {
        try {
            SimpleSelectQuery<B> q = create().selectQuery(getTBook());
            q.addCompareCondition(getTBook_TITLE(), "1984");
            q.execute();
            Result<B> result = q.getResult();

            B book = result.getRecord(0);
            Method getTAuthor = getTBook().getClass().getMethod("getTAuthor");

            Record author = (Record) getTAuthor.invoke(book);
            assertEquals("Orwell", author.getValue(getTAuthor_LAST_NAME()));

            Method getTBooks = author.getClass().getMethod("getTBooks");

            List<?> books = (List<?>) getTBooks.invoke(author);

            assertEquals(2, books.size());
        } catch (NoSuchMethodException expected) {
            log.info("Skipping referentials test");
        }
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
        book = create().fetchOne(getTBook(), getTBook_TITLE(), "1984");

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

    @Test
    public final void testCombinedSelectQuery() throws Exception {
        SelectQuery q1 = create().selectQuery();
        SelectQuery q2 = create().selectQuery();

        q1.addFrom(getTBook());
        q2.addFrom(getTBook());

        q1.addCompareCondition(getTBook_AUTHOR_ID(), 1);
        q2.addCompareCondition(getTBook_TITLE(), "Brida");

        // Use union all because of clob's
        Select<?> union = q1.unionAll(q2);
        int rows = union.execute();
        assertEquals(3, rows);

        // Use union all because of clob's
        rows = create().selectDistinct(union.getField(getTBook_AUTHOR_ID()), getTAuthor_FIRST_NAME())
            .from(union)
            .join(getTAuthor())
            .on(union.getField(getTBook_AUTHOR_ID()).equal(getTAuthor_ID()))
            .orderBy(getTAuthor_FIRST_NAME())
            .execute();

        assertEquals(2, rows);
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
        q2.addOrderBy(b_title.lower());

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
    public final void testArithmeticExpressions() throws Exception {
        Field<Integer> f1 = create().constant(1).add(2).add(3).divide(2);
        Field<Integer> f2 = create().constant(10).divide(5).add(create().constant(3).subtract(2));

        SelectQuery q1 = create().selectQuery();
        q1.addSelect(f1, f2);
        q1.execute();

        Result<?> result = q1.getResult();
        assertEquals(1, result.getNumberOfRecords());
        assertEquals(Integer.valueOf(3), result.getValue(0, f1));
        assertEquals(Integer.valueOf(3), result.getValue(0, f2));

        Field<Integer> f3 = getTBook_PUBLISHED_IN().add(3).divide(7);
        Field<Integer> f4 = getTBook_PUBLISHED_IN().subtract(4).multiply(8);

        SelectQuery q2 = create().selectQuery();
        q2.addSelect(f3);
        q2.addSelect(f4);
        q2.addFrom(getTBook());
        q2.addConditions(getTBook_TITLE().equal("1984"));
        q2.execute();

        result = q2.getResult();
        assertEquals(Integer.valueOf((1948 + 3) / 7), result.getValue(0, f3));
        assertEquals(Integer.valueOf((1948 - 4) * 8), result.getValue(0, f4));
    }

    @Test
    public final void testFunction3() throws Exception {
        SelectQuery q1 = create().selectQuery();
        Field<Timestamp> now = create().currentTimestamp();
        Field<Timestamp> ts = now.as("ts");
        Field<Date> date = create().currentDate().as("d");
        Field<Time> time = create().currentTime().as("t");

        Field<Integer> year = now.extract(DatePart.YEAR).as("y");
        Field<Integer> month = now.extract(DatePart.MONTH).as("m");
        Field<Integer> day = now.extract(DatePart.DAY).as("dd");
        Field<Integer> hour = now.extract(DatePart.HOUR).as("h");
        Field<Integer> minute = now.extract(DatePart.MINUTE).as("mn");
        Field<Integer> second = now.extract(DatePart.SECOND).as("sec");

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
        Field<String> constant = create().constant("abc");
        Field<Integer> charLength = constant.charLength();
        Field<Integer> bitLength = constant.bitLength();
        Field<Integer> octetLength = constant.octetLength();

        // These two tests will validate #154
        Field<String> x = constant.replace("b", "x");
        Field<String> y = constant.replace("b", "y");

        q.addSelect(x, y, charLength, bitLength, octetLength);
        q.execute();

        Record record = q.getResult().getRecord(0);

        assertEquals("axc", record.getValue(x));
        assertEquals("ayc", record.getValue(y));
        assertEquals(Integer.valueOf(3), record.getValue(charLength));

        switch (create().getDialect()) {
            case HSQLDB:
            case H2:
                // HSQLDB and H2 uses Java-style characters (16 bit)
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
        // SQLite does not have anything like the position function
        if (create().getDialect() == SQLDialect.SQLITE) {
            return;
        }

        SelectQuery q = create().selectQuery();
        q.addFrom(getVLibrary());

        Field<Integer> position = getVLibrary_AUTHOR().position("o").as("p");
        q.addSelect(getVLibrary_AUTHOR());
        q.addSelect(position);
        q.addOrderBy(getVLibrary_AUTHOR());

        q.execute();
        Record r1 = q.getResult().getRecord(1); // George Orwell
        Record r2 = q.getResult().getRecord(2); // Paulo Coelho

        assertEquals(Integer.valueOf(3), r1.getValue(position));
        assertEquals(Integer.valueOf(5), r2.getValue(position));

        // Implicit check on the rownum function in oracle dialect
        L library = create().fetchAny(getVLibrary());
        assertTrue(library != null);
    }

    @Test
    public final void testCaseStatement() throws Exception {
        Field<String> case1 = create().decode()
            .value(getTBook_PUBLISHED_IN())
            .when(0, "ancient book")
            .as("case1");

        Field<String> case2 = create().decode()
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public final void testEnums() throws Exception {
        if (getTBook_STATUS() == null) {
            return;
        }

        B book = create()
            .selectFrom(getTBook())
            .where(getTBook_PUBLISHED_IN().equal(1990))
            .fetchOne();
        Enum<?> value = book.getValue(getTBook_STATUS());
        assertEquals("SOLD_OUT", value.name());
        assertEquals("SOLD OUT", ((EnumType) value).getLiteral());

        // Another copy of the original record
        book = create().fetchOne(getTBook(), getTBook_TITLE(), "1984");
        book.setValue((Field) getTBook_STATUS(), Enum.valueOf(value.getClass(), "ON_STOCK"));
        book.store();

        book = create().fetchOne(getTBook(), getTBook_TITLE(), "1984");
        value = book.getValue(getTBook_STATUS());
        assertEquals("ON_STOCK", value.name());
        assertEquals("ON STOCK", ((EnumType) value).getLiteral());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public final void testMasterData() throws Exception {
        if (getTBook_LANGUAGE_ID() != null) {
            B book = create().fetchOne(getTBook(), getTBook_TITLE().equal("1984"));

            Enum<?> value = book.getValue(getTBook_LANGUAGE_ID());
            assertEquals(Integer.valueOf(1), ((MasterDataType<?>) value).getPrimaryKey());
            assertEquals("en", value.name());

            book.setValue((Field) getTBook_LANGUAGE_ID(), Enum.valueOf(value.getClass(), "de"));
            book.store();

            book = create().fetchOne(getTBook(), getTBook_TITLE(), "1984");
            value = book.getValue(getTBook_LANGUAGE_ID());
            assertEquals(Integer.valueOf(2), ((MasterDataType<?>) value).getPrimaryKey());
            assertEquals("de", value.name());
        } else {
            log.info("Skipping master data test");
        }
    }

    @Test
    public final void testUDTs() throws Exception {
        if (getTAuthor_ADDRESS() == null) {
            return;
        }

        Result<A> authors = create().selectFrom(getTAuthor()).fetch();
        UDTRecord<?> a1 = authors.getRecord(0).getValue(getTAuthor_ADDRESS());
        UDTRecord<?> a2 = authors.getRecord(1).getValue(getTAuthor_ADDRESS());

        Object street1 = a1.getClass().getMethod("getStreet").invoke(a1);
        assertEquals("77", street1.getClass().getMethod("getNo").invoke(street1));
        assertEquals("Parliament Hill", street1.getClass().getMethod("getStreet").invoke(street1));
        assertEquals("NW31A9", a1.getClass().getMethod("getZip").invoke(a1));
        assertEquals("Hampstead", a1.getClass().getMethod("getCity").invoke(a1));
        assertEquals("England", "" + a1.getClass().getMethod("getCountry").invoke(a1));
        assertEquals(null, a1.getClass().getMethod("getCode").invoke(a1));

        Object street2 = a2.getClass().getMethod("getStreet").invoke(a2);
        assertEquals("43.003", street1.getClass().getMethod("getNo").invoke(street2));
        assertEquals("Caixa Postal", street1.getClass().getMethod("getStreet").invoke(street2));
        assertEquals(null, a2.getClass().getMethod("getZip").invoke(a2));
        assertEquals("Rio de Janeiro", a2.getClass().getMethod("getCity").invoke(a2));
        assertEquals("Brazil", "" + a1.getClass().getMethod("getCountry").invoke(a2));
        assertEquals(2, a1.getClass().getMethod("getCode").invoke(a2));
    }

    @Test
    public final void testNULL() throws Exception {
        Field<?> n = create().NULL();
        Field<Integer> c = create().constant(1);

        Record record = create().select(n).fetchOne();
        assertEquals(null, record.getValue(n));

        record = create().select(c).where(n.isNull()).fetchOne();
        assertEquals(Integer.valueOf(1), record.getValue(c));

        record = create().select(c).where(n.isNotNull()).fetchAny();
        assertEquals(null, record);

        UpdateQuery<A> u = create().updateQuery(getTAuthor());
        u.addValue(getTAuthor_YEAR_OF_BIRTH(), null);
        u.execute();

        Result<A> records = create()
            .selectFrom(getTAuthor())
            .where(getTAuthor_YEAR_OF_BIRTH().isNull())
            .fetch();
        assertEquals(2, records.getNumberOfRecords());
        assertEquals(null, records.getValue(0, getTAuthor_YEAR_OF_BIRTH()));
    }
}
