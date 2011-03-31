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
package org.jooq.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.jooq.impl.FalseCondition.FALSE_CONDITION;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.jooq.ArrayRecord;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Cursor;
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
import org.jooq.Sequence;
import org.jooq.SimpleSelectQuery;
import org.jooq.StoreQuery;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableRecord;
import org.jooq.UDTRecord;
import org.jooq.UpdatableRecord;
import org.jooq.UpdateQuery;
import org.jooq.impl.CustomCondition;
import org.jooq.impl.CustomField;
import org.jooq.impl.Factory;
import org.jooq.impl.JooqLogger;
import org.jooq.impl.StringUtils;
import org.jooq.impl.TrueCondition;
import org.jooq.util.GenerationTool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.postgresql.util.PSQLException;

/**
 * @author Lukas Eder
 */
public abstract class jOOQAbstractTest<
        A extends UpdatableRecord<A>,
        B extends UpdatableRecord<B>,
        S extends UpdatableRecord<S>,
        L extends TableRecord<L>,
        X extends TableRecord<X>> {

    private static final String     JDBC_SCHEMA   = "jdbc.Schema";
    private static final String     JDBC_PASSWORD = "jdbc.Password";
    private static final String     JDBC_USER     = "jdbc.User";
    private static final String     JDBC_URL      = "jdbc.URL";
    private static final String     JDBC_DRIVER   = "jdbc.Driver";

    private static final JooqLogger log           = JooqLogger.getLogger(jOOQAbstractTest.class);
    private static boolean          initialised;
    protected static Connection     connection;
    protected static String         jdbcURL;
    protected static String         jdbcSchema;

    protected void execute(String script) throws Exception {
        Statement stmt = null;
        File file = new File(getClass().getResource(script).toURI());
        String allSQL = FileUtils.readFileToString(file);

        for (String sql : allSQL.split("/")) {
            try {
                if (!StringUtils.isBlank(sql)) {
                    sql = sql.replace("{" + JDBC_SCHEMA + "}", jdbcSchema);
                    stmt = connection.createStatement();
                    stmt.execute(sql.trim());
                }
            }
            catch (Exception e) {
                // There is no DROP TABLE IF EXISTS statement in Oracle
                if (e.getMessage().contains("ORA-00942")) {
                    continue;
                }

                // There is no DROP SEQUENCE IF EXISTS statement in Oracle
                else if (e.getMessage().contains("ORA-02289")) {
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

                // There is no DROP TRANSFORM IF EXISTS statement in DB2
                else if (e.getMessage().contains("SQLCODE=-20012") && e.getMessage().contains("SQLSTATE=42740")) {
                    continue;
                }

                // There is no DROP FUNCTION IF EXISTS statement in Postgres
                else if (e instanceof PSQLException) {
                    if ("42883".equals(((PSQLException) e).getSQLState())) {
                        continue;
                    }
                }

                // There is no DROP ** IF EXISTS statement in Derby
                else if (e.getCause() instanceof org.apache.derby.client.am.SqlException) {
                    if (sql.contains("DROP") || sql.contains("CREATE SCHEMA")) {
                        continue;
                    }
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

    @Before
    public void setUp() throws Exception {
        connection = getConnection();

        if (!initialised) {
            initialised = true;
            execute(getCreateScript());
        }

        execute(getResetScript());
    }

    @After
    public void tearDown() throws Exception {
    }

    protected final Connection getConnection() throws Exception {
        if (connection == null) {
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

            String driver = properties.getProperty(JDBC_DRIVER);
            jdbcURL = properties.getProperty(JDBC_URL);
            String jdbcUser = properties.getProperty(JDBC_USER);
            String jdbcPassword = properties.getProperty(JDBC_PASSWORD);
            jdbcSchema = properties.getProperty(JDBC_SCHEMA);

            Class.forName(driver);
            connection = DriverManager.getConnection(getJdbcURL(), jdbcUser, jdbcPassword);
        }

        return connection;
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

    protected final String getCreateScript() throws Exception {
        return "/org/jooq/test/" + create().getDialect().getName().toLowerCase() + "/create.sql";
    }

    protected final String getResetScript() throws Exception {
        return "/org/jooq/test/" + create().getDialect().getName().toLowerCase() + "/reset.sql";
    }

    protected final <T> Field<T> constant(T value) throws Exception {
        return create().constant(value);
    }

    protected abstract Table<X> TArrays();
    protected abstract TableField<X, Integer> TArrays_ID();
    protected abstract TableField<X, String[]> TArrays_STRING();
    protected abstract TableField<X, Integer[]> TArrays_NUMBER();
    protected abstract TableField<X, Date[]> TArrays_DATE();
    protected abstract TableField<X, ? extends ArrayRecord<String>> TArrays_STRING_R();
    protected abstract TableField<X, ? extends ArrayRecord<Integer>> TArrays_NUMBER_R();
    protected abstract TableField<X, ? extends ArrayRecord<Long>> TArrays_NUMBER_LONG_R();
    protected abstract TableField<X, ? extends ArrayRecord<Date>> TArrays_DATE_R();
    protected abstract Table<A> TAuthor();
    protected abstract TableField<A, String> TAuthor_LAST_NAME();
    protected abstract TableField<A, String> TAuthor_FIRST_NAME();
    protected abstract TableField<A, Date> TAuthor_DATE_OF_BIRTH();
    protected abstract TableField<A, Integer> TAuthor_YEAR_OF_BIRTH();
    protected abstract TableField<A, Integer> TAuthor_ID();
    protected abstract TableField<A, ? extends UDTRecord<?>> TAuthor_ADDRESS();
    protected abstract Class<? extends UDTRecord<?>> UAddressType();
    protected abstract Class<? extends UDTRecord<?>> UStreetType();
    protected abstract Table<B> TBook();
    protected abstract TableField<B, Integer> TBook_ID();
    protected abstract TableField<B, Integer> TBook_AUTHOR_ID();
    protected abstract TableField<B, String> TBook_TITLE();
    protected abstract TableField<B, ?> TBook_LANGUAGE_ID();
    protected abstract TableField<B, Integer> TBook_PUBLISHED_IN();
    protected abstract TableField<B, String> TBook_CONTENT_TEXT();
    protected abstract TableField<B, byte[]> TBook_CONTENT_PDF();
    protected abstract TableField<B, ? extends Enum<?>> TBook_STATUS();
    protected abstract Table<S> TBookStore();
    protected abstract TableField<S, String> TBookStore_NAME();
    protected abstract Table<L> VLibrary();
    protected abstract TableField<L, String> VLibrary_TITLE();
    protected abstract TableField<L, String> VLibrary_AUTHOR();
    protected abstract Field<? extends Number> FAuthorExistsField(String authorName);
    protected abstract Field<? extends Number> FOneField();
    protected abstract Field<? extends Number> FNumberField(Number n);
    protected abstract Field<? extends Number> FNumberField(Field<? extends Number> n);
    protected abstract Field<? extends Number> F317Field(Number n1, Number n2, Number n3, Number n4);
    protected abstract Field<? extends Number> F317Field(Field<? extends Number> n1, Field<? extends Number> n2, Field<? extends Number> n3, Field<? extends Number> n4);
    protected abstract Class<?> Procedures();
    protected abstract boolean supportsOUTParameters();
    protected abstract boolean supportsReferences();
    protected abstract Class<?> Functions();
    protected abstract Class<?> Library();
    protected abstract Class<?> Sequences();
    protected abstract Factory create() throws Exception;

    @Test
    public final void testLazyFetching() throws Exception {
        Cursor<B> cursor = create().selectFrom(TBook()).orderBy(TBook_ID()).fetchLazy();

        assertTrue(cursor.hasNext());
        assertTrue(cursor.hasNext());
        assertEquals(Integer.valueOf(1), cursor.fetch().getValue(TBook_ID()));
        assertEquals(Integer.valueOf(2), cursor.fetch().getValue(TBook_ID()));

        assertTrue(cursor.hasNext());
        assertTrue(cursor.hasNext());

        Iterator<B> it = cursor.iterator();
        assertTrue(it.hasNext());
        assertTrue(cursor.hasNext());
        assertTrue(it.hasNext());
        assertTrue(cursor.hasNext());
        assertTrue(it.hasNext());
        assertTrue(cursor.hasNext());
        assertEquals(Integer.valueOf(3), it.next().getValue(TBook_ID()));
        assertEquals(Integer.valueOf(4), it.next().getValue(TBook_ID()));

        assertFalse(it.hasNext());
        assertFalse(cursor.hasNext());
        assertFalse(it.hasNext());
        assertFalse(cursor.hasNext());
        assertFalse(it.hasNext());
        assertFalse(cursor.hasNext());

        assertEquals(null, it.next());
        assertEquals(null, it.next());
        assertEquals(null, cursor.fetch());
        assertEquals(null, cursor.fetch());

        cursor.close();
    }

    @Test
    public final void testFetchMap() throws Exception {
        try {
            create().selectFrom(TBook()).orderBy(TBook_ID()).fetchMap(TBook_AUTHOR_ID());
            fail();
        } catch (SQLException expected) {}

        // Key -> Record map
        Map<Integer, B> map1 = create().selectFrom(TBook()).orderBy(TBook_ID()).fetchMap(TBook_ID());
        for (Entry<Integer, B> entry : map1.entrySet()) {
            assertEquals(entry.getKey(), entry.getValue().getValue(TBook_ID()));
        }
        assertEquals(Arrays.asList(1, 2, 3, 4), new ArrayList<Integer>(map1.keySet()));

        // Key -> Value map
        Map<Integer, String> map2 = create().selectFrom(TBook()).orderBy(TBook_ID()).fetchMap(TBook_ID(), TBook_TITLE());
        assertEquals(Arrays.asList(1, 2, 3, 4), new ArrayList<Integer>(map2.keySet()));
        assertEquals(Arrays.asList("1984", "Animal Farm", "O Alquimista", "Brida"), new ArrayList<String>(map2.values()));
    }

    @Test
    public final void testPlainSQL() throws Exception {
        Field<?> ID = create().plainSQLField(TBook_ID().getName());
        Result<Record> result = create().select().from("t_book").orderBy(ID).fetch();

        assertEquals(4, result.getNumberOfRecords());
        assertEquals(Integer.valueOf(1), result.getValue(0, TBook_ID().getName()));
        assertEquals(Integer.valueOf(2), result.getValue(1, TBook_ID().getName()));
        assertEquals(Integer.valueOf(3), result.getValue(2, TBook_ID().getName()));
        assertEquals(Integer.valueOf(4), result.getValue(3, TBook_ID().getName()));
        assertEquals("1984", result.getValue(0, TBook_TITLE().getName()));
        assertEquals("Animal Farm", result.getValue(1, TBook_TITLE().getName()));
        assertEquals("O Alquimista", result.getValue(2, TBook_TITLE().getName()));
        assertEquals("Brida", result.getValue(3, TBook_TITLE().getName()));


        Field<?> LAST_NAME = create().plainSQLField(TAuthor_LAST_NAME().getName());
        Field<?> COUNT1 = create().plainSQLField("count(*) x");
        Field<?> COUNT2 = create().plainSQLField("count(*) y", Integer.class);

        result = create().select(LAST_NAME, COUNT1, COUNT2)
            .from("t_author a")
            .join("t_book b").on("a.id = b.author_id")
            .where("b.title != 'Brida'")
            .groupBy(LAST_NAME)
            .orderBy(LAST_NAME).fetch();

        assertEquals(2, result.getNumberOfRecords());
        assertEquals("Coelho", result.getValue(0, LAST_NAME));
        assertEquals("Orwell", result.getValue(1, LAST_NAME));
        assertEquals("1", result.getValueAsString(0, COUNT1));
        assertEquals("2", result.getValueAsString(1, COUNT1));
        assertEquals(Integer.valueOf(1), result.getValue(0, COUNT2));
        assertEquals(Integer.valueOf(2), result.getValue(1, COUNT2));


        result = create().select(LAST_NAME, COUNT1, COUNT2)
            .from("t_author a")
            .join("t_book b").on("a.id = b.author_id")
            .where("b.title != 'Brida'")
            .groupBy(LAST_NAME)
            .having("count(*) = ?", 1).fetch();

        assertEquals(1, result.getNumberOfRecords());
        assertEquals("Coelho", result.getValue(0, LAST_NAME));
        assertEquals("1", result.getValueAsString(0, COUNT1));
        assertEquals(Integer.valueOf(1), result.getValue(0, COUNT2));
    }

    @Test
    public final void testCustomSQL() throws Exception {
        final Field<Integer> IDx2 = new CustomField<Integer>(create(), "ID", TBook_ID().getDataType()) {
            private static final long serialVersionUID = 1L;

            @Override
            public String toSQLReference(Configuration configuration, boolean inlineParameters) {
                if (inlineParameters) {
                    return "ID * 2";
                }
                else {
                    return "ID * ?";
                }
            }

            @Override
            public int bind(Configuration configuration, PreparedStatement stmt, int initialIndex) throws SQLException {
                stmt.setInt(initialIndex, 2);
                return initialIndex + 1;
            }
        };

        Condition c = new CustomCondition(create()) {
            private static final long serialVersionUID = -629253722638033620L;

            @Override
            public String toSQLReference(Configuration configuration, boolean inlineParameters) {
                StringBuilder sb = new StringBuilder();

                sb.append(IDx2.getQueryPart().toSQLReference(configuration, inlineParameters));
                sb.append(" > ");

                if (inlineParameters) {
                    sb.append("3");
                }
                else {
                    sb.append("?");
                }

                return sb.toString();
            }

            @Override
            public int bind(Configuration configuration, PreparedStatement stmt, int initialIndex) throws SQLException {
                int result = initialIndex;

                result = IDx2.getQueryPart().bind(configuration, stmt, result);
                stmt.setInt(result++, 3);

                return result;
            }
        };

        Result<Record> result = create()
            .select(TBook_ID(), IDx2)
            .from(TBook())
            .where(c)
            .orderBy(IDx2)
            .fetch();

        assertEquals(3, result.getNumberOfRecords());
        assertEquals(Integer.valueOf(2), result.getValue(0, TBook_ID()));
        assertEquals(Integer.valueOf(3), result.getValue(1, TBook_ID()));
        assertEquals(Integer.valueOf(4), result.getValue(2, TBook_ID()));

        assertEquals(Integer.valueOf(4), result.getValue(0, IDx2));
        assertEquals(Integer.valueOf(6), result.getValue(1, IDx2));
        assertEquals(Integer.valueOf(8), result.getValue(2, IDx2));
    }

    @Test
    public final void testCasting() throws Exception {
        switch (create().getDialect()) {
            // DERBY's cast support is very restrictive
            // http://db.apache.org/derby/docs/10.7/ref/rrefsqlj33562.html
            case DERBY:
                log.info("SKIPPING", "most casting tests");
                break;
            default:
                if (create().getDialect() != SQLDialect.HSQLDB) {
                    assertEquals(true, create().select(create().cast("1", Boolean.class)).fetchOne(0));
                    assertEquals(true, create().select(create().cast(1, Boolean.class)).fetchOne(0));
                }

                // Not implemented by the driver
                if (create().getDialect() != SQLDialect.SQLITE) {
                    assertEquals(BigInteger.ONE, create().select(create().cast("1", BigInteger.class)).fetchOne(0));
                    assertEquals(BigDecimal.ONE, create().select(create().cast("1", BigDecimal.class)).fetchOne(0));
                    assertEquals(BigInteger.ONE, create().select(create().cast(1, BigInteger.class)).fetchOne(0));
                    assertEquals(BigDecimal.ONE, create().select(create().cast(1, BigDecimal.class)).fetchOne(0));
                }

                assertEquals((byte) 1, create().select(create().cast("1", Byte.class)).fetchOne(0));
                assertEquals((short) 1, create().select(create().cast("1", Short.class)).fetchOne(0));
                assertEquals(1, create().select(create().cast("1", Integer.class)).fetchOne(0));
                assertEquals(1L, create().select(create().cast("1", Long.class)).fetchOne(0));

                assertEquals(1.0f, create().select(create().cast("1", Float.class)).fetchOne(0));
                assertEquals(1.0, create().select(create().cast("1", Double.class)).fetchOne(0));
                assertEquals("1", create().select(create().cast("1", String.class)).fetchOne(0));

                assertEquals((byte) 1, create().select(create().cast(1, Byte.class)).fetchOne(0));
                assertEquals((short) 1, create().select(create().cast(1, Short.class)).fetchOne(0));
                assertEquals(1, create().select(create().cast(1, Integer.class)).fetchOne(0));
                assertEquals(1L, create().select(create().cast(1, Long.class)).fetchOne(0));
                assertEquals(1.0f, create().select(create().cast(1, Float.class)).fetchOne(0));
                assertEquals(1.0, create().select(create().cast(1, Double.class)).fetchOne(0));
                assertEquals("1", create().select(create().cast(1, String.class)).fetchOne(0));
        }

        assertEquals(null, create().select(create().castNull(Boolean.class)).fetchOne(0));
        assertEquals(null, create().select(create().castNull(Byte.class)).fetchOne(0));
        assertEquals(null, create().select(create().castNull(Short.class)).fetchOne(0));
        assertEquals(null, create().select(create().castNull(Integer.class)).fetchOne(0));
        assertEquals(null, create().select(create().castNull(Long.class)).fetchOne(0));

        // Not implemented by the driver
        if (create().getDialect() != SQLDialect.SQLITE) {
            assertEquals(null, create().select(create().castNull(BigInteger.class)).fetchOne(0));
            assertEquals(null, create().select(create().castNull(BigDecimal.class)).fetchOne(0));
        }

        assertEquals(null, create().select(create().castNull(Float.class)).fetchOne(0));
        assertEquals(null, create().select(create().castNull(Double.class)).fetchOne(0));
        assertEquals(null, create().select(create().castNull(String.class)).fetchOne(0));
        assertEquals(null, create().select(create().castNull(Date.class)).fetchOne(0));
        assertEquals(null, create().select(create().castNull(Time.class)).fetchOne(0));
        assertEquals(null, create().select(create().castNull(Timestamp.class)).fetchOne(0));

        assertEquals(1984, create()
            .select(TBook_TITLE().cast(Integer.class))
            .from(TBook())
            .where(TBook_ID().equal(1))
            .fetch().getValue(0, 0));
    }

    @Test
    public final void testSequences() throws Exception {
        if (Sequences() == null) {
            log.info("SKIPPING", "sequences test");
            return;
        }

        Sequence sequence = (Sequence) Sequences().getField("S_AUTHOR_ID").get(Sequences());
        Field<BigInteger> nextval = sequence.nextval();
        Field<BigInteger> currval = null;

        assertEquals("3", "" + create().select(nextval).fetchOne(nextval));
        assertEquals("4", "" + create().select(nextval).fetchOne(nextval));
        assertEquals("5", "" + create().select(nextval).fetchOne(nextval));

        switch (create().getDialect()) {
            // HSQLDB and DERBY don't support currval, so don't test it
            case HSQLDB:
            case DERBY:

            // DB2 supports currval, but there seems to be a minor issue:
            // https://sourceforge.net/apps/trac/jooq/ticket/241
            case DB2:
                break;

            default:
                currval = sequence.currval();
                assertEquals("5", "" + create().select(currval).fetchOne(currval));
                assertEquals("5", "" + create().select(currval).fetchOne(currval));

                assertEquals(new BigInteger("5"), create().currval(sequence));
                assertEquals(new BigInteger("5"), create().currval(sequence));
        }

        assertEquals("6", "" + create().select(nextval).fetchOne(nextval));

        // Test convenience syntax
        assertEquals(new BigInteger("7"), create().nextval(sequence));
        assertEquals(new BigInteger("8"), create().nextval(sequence));
    }

    @Test
    public final void testSelectSimpleQuery() throws Exception {
        SelectQuery q = create().selectQuery();
        Field<Integer> f1 = constant(1).as("f1");
        Field<Double> f2 = constant(2d).as("f2");
        Field<String> f3 = constant("test").as("f3");

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
        q.addFrom(TAuthor());
        q.addSelect(TAuthor().getFields());
        q.addOrderBy(TAuthor_LAST_NAME());

        int rows = q.execute();
        Result<?> result = q.getResult();

        assertEquals(2, rows);
        assertEquals(2, result.getNumberOfRecords());
        assertEquals("Coelho", result.getRecord(0).getValue(TAuthor_LAST_NAME()));
        assertEquals("Orwell", result.getRecord(1).getValue(TAuthor_LAST_NAME()));
    }

    @Test
    public final void testTypeConversions() throws Exception {
        Record record = create().fetchOne(TAuthor(), TAuthor_LAST_NAME().equal("Orwell"));

        assertEquals("George", record.getValue(TAuthor_FIRST_NAME()));
        assertEquals("George", record.getValueAsString(TAuthor_FIRST_NAME()));
        assertEquals("George", record.getValueAsString(TAuthor_FIRST_NAME(), "gnarf"));
        assertEquals("George", record.getValueAsString(1));
        assertEquals("George", record.getValueAsString(1, "gnarf"));

        assertEquals(Integer.valueOf("1903"), record.getValue(TAuthor_YEAR_OF_BIRTH()));
        assertEquals(Integer.valueOf("1903"), record.getValue(TAuthor_YEAR_OF_BIRTH(), 123));
        assertEquals(Integer.valueOf("1903"), record.getValue(4));
        assertEquals(Integer.valueOf("1903"), record.getValue(4, 123));

        assertEquals(Short.valueOf("1903"), record.getValueAsShort(TAuthor_YEAR_OF_BIRTH()));
        assertEquals(Short.valueOf("1903"), record.getValueAsShort(TAuthor_YEAR_OF_BIRTH(), (short) 123));
        assertEquals(Short.valueOf("1903"), record.getValueAsShort(4));
        assertEquals(Short.valueOf("1903"), record.getValueAsShort(4, (short) 123));

        assertEquals(Long.valueOf("1903"), record.getValueAsLong(TAuthor_YEAR_OF_BIRTH()));
        assertEquals(Long.valueOf("1903"), record.getValueAsLong(TAuthor_YEAR_OF_BIRTH(), 123L));
        assertEquals(Long.valueOf("1903"), record.getValueAsLong(4));
        assertEquals(Long.valueOf("1903"), record.getValueAsLong(4, 123L));

        assertEquals(new BigInteger("1903"), record.getValueAsBigInteger(TAuthor_YEAR_OF_BIRTH()));
        assertEquals(new BigInteger("1903"), record.getValueAsBigInteger(TAuthor_YEAR_OF_BIRTH(), new BigInteger("123")));
        assertEquals(new BigInteger("1903"), record.getValueAsBigInteger(4));
        assertEquals(new BigInteger("1903"), record.getValueAsBigInteger(4, new BigInteger("123")));

        assertEquals(Float.valueOf("1903"), record.getValueAsFloat(TAuthor_YEAR_OF_BIRTH()));
        assertEquals(Float.valueOf("1903"), record.getValueAsFloat(TAuthor_YEAR_OF_BIRTH(), 123f));
        assertEquals(Float.valueOf("1903"), record.getValueAsFloat(4));
        assertEquals(Float.valueOf("1903"), record.getValueAsFloat(4, 123f));

        assertEquals(Double.valueOf("1903"), record.getValueAsDouble(TAuthor_YEAR_OF_BIRTH()));
        assertEquals(Double.valueOf("1903"), record.getValueAsDouble(TAuthor_YEAR_OF_BIRTH(), 123d));
        assertEquals(Double.valueOf("1903"), record.getValueAsDouble(4));
        assertEquals(Double.valueOf("1903"), record.getValueAsDouble(4, 123d));

        assertEquals(new BigDecimal("1903"), record.getValueAsBigDecimal(TAuthor_YEAR_OF_BIRTH()));
        assertEquals(new BigDecimal("1903"), record.getValueAsBigDecimal(TAuthor_YEAR_OF_BIRTH(), new BigDecimal("123")));
        assertEquals(new BigDecimal("1903"), record.getValueAsBigDecimal(4));
        assertEquals(new BigDecimal("1903"), record.getValueAsBigDecimal(4, new BigDecimal("123")));


        long dateOfBirth = record.getValue(TAuthor_DATE_OF_BIRTH()).getTime();
        assertEquals(dateOfBirth, record.getValueAsDate(TAuthor_DATE_OF_BIRTH()).getTime());
        assertEquals(dateOfBirth, record.getValueAsTimestamp(TAuthor_DATE_OF_BIRTH()).getTime());
        assertEquals(dateOfBirth, record.getValueAsTime(TAuthor_DATE_OF_BIRTH()).getTime());
    }

    @Test
    public final void testConditionalSelect() throws Exception {
        Condition c = TrueCondition.TRUE_CONDITION;

        assertEquals(4, create().selectFrom(TBook()).where(c).execute());

        c = c.and(TBook_PUBLISHED_IN().greaterThan(1945));
        assertEquals(3, create().selectFrom(TBook()).where(c).execute());

        c = c.not();
        assertEquals(1, create().selectFrom(TBook()).where(c).execute());

        c = c.or(TBook_AUTHOR_ID().equal(
            create().select(TAuthor_ID()).from(TAuthor()).where(TAuthor_FIRST_NAME().equal("Paulo"))));
        assertEquals(3, create().selectFrom(TBook()).where(c).execute());
    }

    @Test
    public final void testConditions() throws Exception {
        // The BETWEEN clause
        assertEquals(Arrays.asList(2, 3), create().select()
            .from(TBook())
            .where(TBook_ID().between(2, 3))
            .orderBy(TBook_ID()).fetch(TBook_ID()));

        assertEquals(Arrays.asList(3, 4), create().select()
            .from(TBook())
            .where(create().constant(3).between(TBook_AUTHOR_ID(), TBook_ID()))
            .orderBy(TBook_ID()).fetch(TBook_ID()));

        // The IN clause
        assertEquals(Arrays.asList(1, 2), create().select()
            .from(TBook())
            .where(TBook_ID().in(1, 2))
            .orderBy(TBook_ID()).fetch(TBook_ID()));

        assertEquals(Arrays.asList(2, 3, 4), create().select()
            .from(TBook())
            .where(create().constant(2).in(TBook_ID(), TBook_AUTHOR_ID()))
            .orderBy(TBook_ID()).fetch(TBook_ID()));
    }

    @Test
    public final void testSubSelect() throws Exception {
        // ---------------------------------------------------------------------
        // Testing the IN condition
        // ---------------------------------------------------------------------
        assertEquals(3,
            create().selectFrom(TBook())
                .where(TBook_TITLE().notIn(create()
                    .select(TBook_TITLE())
                    .from(TBook())
                    .where(TBook_TITLE().in("1984"))))
                .execute());

        // ---------------------------------------------------------------------
        // Testing the EXISTS condition
        // ---------------------------------------------------------------------
        assertEquals(3,
            create()
                .selectFrom(TBook())
                .whereNotExists(create()
                    .select(1)
                    .from(TAuthor())
                    .where(TAuthor_YEAR_OF_BIRTH().greaterOrEqual(TBook_PUBLISHED_IN())))

                // Add additional useless queries to check query correctness
                .orNotExists(create().select())
                .andExists(create().select()).execute());

        // ---------------------------------------------------------------------
        // Testing selecting from a select
        // ---------------------------------------------------------------------
        Table<Record> nested = create().select(TBook_AUTHOR_ID(), create().count().as("books"))
            .from(TBook())
            .groupBy(TBook_AUTHOR_ID()).asTable("nested");

        Result<Record> records = create().select(nested.getFields())
            .from(nested)
            .orderBy(nested.getField("books"), nested.getField(TBook_AUTHOR_ID())).fetch();

        assertEquals(2, records.getNumberOfRecords());
        assertEquals(Integer.valueOf(1), records.getValue(0, nested.getField(TBook_AUTHOR_ID())));
        assertEquals(Integer.valueOf(2), records.getValue(0, nested.getField("books")));
        assertEquals(Integer.valueOf(2), records.getValue(1, nested.getField(TBook_AUTHOR_ID())));
        assertEquals(Integer.valueOf(2), records.getValue(1, nested.getField("books")));

        Field<Object> books = create().select(create().count())
                .from(TBook())
                .where(TBook_AUTHOR_ID().equal(TAuthor_ID())).asField("books");

        records = create().select(TAuthor_ID(), books)
                          .from(TAuthor())
                          .orderBy(books, TAuthor_ID()).fetch();

        assertEquals(2, records.getNumberOfRecords());
        assertEquals(Integer.valueOf(1), records.getValue(0, TAuthor_ID()));
        assertEquals(Integer.valueOf(2), records.getValue(0, books));
        assertEquals(Integer.valueOf(2), records.getValue(1, TAuthor_ID()));
        assertEquals(Integer.valueOf(2), records.getValue(1, books));
    }

    @Test
    public final void testDistinctQuery() throws Exception {
        Result<Record> result = create()
            .selectDistinct(TBook_AUTHOR_ID())
            .from(TBook())
            .orderBy(TBook_AUTHOR_ID())
            .fetch();

        assertEquals(2, result.getNumberOfRecords());
        assertEquals(Integer.valueOf(1), result.getRecord(0).getValue(TBook_AUTHOR_ID()));
        assertEquals(Integer.valueOf(2), result.getRecord(1).getValue(TBook_AUTHOR_ID()));
    }

    @Test
    public final void testFetch() throws Exception {
        SelectQuery q = create().selectQuery();
        q.addFrom(TAuthor());
        q.addSelect(TAuthor().getFields());
        q.addOrderBy(TAuthor_LAST_NAME());

        Result<?> result = q.fetch();

        assertEquals(2, result.getNumberOfRecords());
        assertEquals("Coelho", result.getRecord(0).getValue(TAuthor_LAST_NAME()));
        assertEquals("Orwell", result.getRecord(1).getValue(TAuthor_LAST_NAME()));

        try {
            q.fetchOne();
            fail();
        }
        catch (Exception expected) {}

        Record record = q.fetchAny();
        assertEquals("Coelho", record.getValue(TAuthor_LAST_NAME()));
    }

    @Test
    public final void testGrouping() throws Exception {

        // Test a simple group by query
        Field<Integer> count = create().count().as("c");
        Result<Record> result = create()
            .select(TBook_AUTHOR_ID(), count)
            .from(TBook())
            .groupBy(TBook_AUTHOR_ID()).fetch();

        assertEquals(2, result.getNumberOfRecords());
        assertEquals(2, (int) result.getRecord(0).getValue(count));
        assertEquals(2, (int) result.getRecord(1).getValue(count));

        // Test a group by query with a single HAVING clause
        result = create()
            .select(TAuthor_LAST_NAME(), count)
            .from(TBook())
            .join(TAuthor()).on(TBook_AUTHOR_ID().equal(TAuthor_ID()))
            .where(TBook_TITLE().notEqual("1984"))
            .groupBy(TAuthor_LAST_NAME())
            .having(create().count().equal(2))
            .fetch();

        assertEquals(1, result.getNumberOfRecords());
        assertEquals(2, (int) result.getValue(0, count));
        assertEquals("Coelho", result.getValue(0, TAuthor_LAST_NAME()));

        // Test a group by query with a combined HAVING clause
        result = create()
            .select(TAuthor_LAST_NAME(), count)
            .from(TBook())
            .join(TAuthor()).on(TBook_AUTHOR_ID().equal(TAuthor_ID()))
            .where(TBook_TITLE().notEqual("1984"))
            .groupBy(TAuthor_LAST_NAME())
            .having(create().count().equal(2))
            .or(create().count().greaterOrEqual(2))
            .andExists(create().select(1))
            .fetch();

        assertEquals(1, result.getNumberOfRecords());
        assertEquals(2, (int) result.getValue(0, count));
        assertEquals("Coelho", result.getValue(0, TAuthor_LAST_NAME()));

        // Test a group by query with a plain SQL having clause
        result = create()
            .select(VLibrary_AUTHOR(), count)
            .from(VLibrary())
            .where(VLibrary_TITLE().notEqual("1984"))
            .groupBy(VLibrary_AUTHOR())

            // MySQL seems to have a bug with fully qualified view names in the
            // having clause. TODO: Fully analyse this issue
            // https://sourceforge.net/apps/trac/jooq/ticket/277
            .having("v_library.author like ?", "Paulo%")
            .fetch();

        assertEquals(1, result.getNumberOfRecords());
        assertEquals(2, (int) result.getValue(0, count));

        // SQLite loses type information when views select functions.
        // In this case: concatenation. So as a workaround, SQLlite only selects
        // FIRST_NAME in the view
        assertEquals("Paulo", result.getValue(0, VLibrary_AUTHOR()).substring(0, 5));
    }

    @Test
    public final void testInsertUpdateDelete() throws Exception {
        InsertQuery<A> i = create().insertQuery(TAuthor());
        i.addValue(TAuthor_ID(), 100);
        i.addValue(TAuthor_FIRST_NAME(), "Hermann");
        i.addValue(TAuthor_LAST_NAME(), "Hesse");
        i.addValue(TAuthor_DATE_OF_BIRTH(), new Date(System.currentTimeMillis()));
        i.addValue(TAuthor_YEAR_OF_BIRTH(), 2010);

        // Check insertion of UDTs and Enums if applicable
        if (TAuthor_ADDRESS() != null) {
            addAddressValue(i, TAuthor_ADDRESS());
        }

        assertEquals(1, i.execute());

        UpdateQuery<A> u = create().updateQuery(TAuthor());
        u.addValue(TAuthor_FIRST_NAME(), "Hermie");
        u.addConditions(TAuthor_ID().equal(100));
        assertEquals(1, u.execute());

        A hermie = create().fetchOne(TAuthor(), TAuthor_FIRST_NAME().equal("Hermie"));
        if (TAuthor_ADDRESS() != null) {
            UDTRecord<?> address = hermie.getValue(TAuthor_ADDRESS());
            Object street1 = invoke(address, "getStreet");
            Object street2 = invoke(street1, "getStreet");
            assertEquals("Bahnhofstrasse", street2);
        }

        DeleteQuery<A> d = create().deleteQuery(TAuthor());
        d.addConditions(TAuthor_ID().equal(100));
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
        Field<?> nullField = null;
        switch (create().getDialect()) {
            case ORACLE:
            case POSTGRES:
                // TODO: cast this to the UDT type
                nullField = create().NULL();
                break;
            default:
                nullField = create().castNull(String.class);
                break;
        }

        InsertSelectQuery<A> i = create().insertQuery(
            TAuthor(),
            create().select(
                1000,
                constant("Lukas"),
                "Eder",
                constant(new Date(363589200000L)),
                create().castNull(Integer.class),
                nullField));

        i.execute();

        A author = create().fetchOne(TAuthor(), TAuthor_FIRST_NAME().equal("Lukas"));
        assertEquals("Lukas", author.getValue(TAuthor_FIRST_NAME()));
        assertEquals("Eder", author.getValue(TAuthor_LAST_NAME()));
        assertEquals(null, author.getValue(TAuthor_YEAR_OF_BIRTH()));
    }

    @Test
    public final void testInsertWithSelectAsField() throws Exception {
        Field<Integer> ID3;
        Field<Integer> ID4;

        switch (create().getDialect()) {
            case MYSQL:
                ID3 = create().select(3).asField();
                ID4 = create().select(4).asField();
                break;
            default:
                ID3 = create()
                    .select(TAuthor_ID().max().add(1))
                    .from(TAuthor()).asField();
                ID4 = create()
                    .select(TAuthor_ID().max().add(1))
                    .from(TAuthor()).asField();
                break;
        }

        InsertQuery<A> insert = create().insertQuery(TAuthor());
        insert.addValue(TAuthor_ID(), ID3);
        insert.addValue(TAuthor_LAST_NAME(), create()
            .select("Hornby").<String> asField());
        insert.execute();

        A author = create().fetchOne(TAuthor(), TAuthor_LAST_NAME().equal("Hornby"));
        assertEquals(Integer.valueOf(3), author.getValue(TAuthor_ID()));
        assertEquals("Hornby", author.getValue(TAuthor_LAST_NAME()));

        UpdateQuery<A> update = create().updateQuery(TAuthor());
        update.addValue(TAuthor_ID(), ID4);
        update.addValue(TAuthor_LAST_NAME(), create()
            .select("Hitchcock").<String> asField());
        update.addConditions(TAuthor_ID().equal(3));
        update.execute();

        author = create().fetchOne(TAuthor(), TAuthor_LAST_NAME().equal("Hitchcock"));
        assertEquals(Integer.valueOf(4), author.getValue(TAuthor_ID()));
        assertEquals("Hitchcock", author.getValue(TAuthor_LAST_NAME()));
    }

    @Test
    public final void testUpdateSelect() throws Exception {
        switch (create().getDialect()) {
            case SQLITE:
            case MYSQL:
                log.info("SKIPPING", "UPDATE .. SET .. = (SELECT ..) integration test. This syntax is poorly supported by " + create().getDialect());
                return;
        }

        Table<A> a1 = TAuthor().as("a1");
        Table<A> a2 = TAuthor().as("a2");
        Field<String> f1 = a1.getField(TAuthor_FIRST_NAME());
        Field<String> f2 = a2.getField(TAuthor_FIRST_NAME());
        Field<String> f3 = a2.getField(TAuthor_LAST_NAME());

        UpdateQuery<A> u = create().updateQuery(a1);
        u.addValue(f1, create().select(f3.max()).from(a2).where(f1.equal(f2)).<String> asField());
        u.execute();

        Field<Integer> c = create().count();
        assertEquals(Integer.valueOf(2), create().select(c)
            .from(TAuthor())
            .where(TAuthor_FIRST_NAME().equal(TAuthor_LAST_NAME()))
            .fetchOne(c));
    }

    @Test
    public final void testBlobAndClob() throws Exception {
        B book = create().fetchOne(TBook(), TBook_TITLE().equal("1984"));

        assertTrue(book.getValue(TBook_CONTENT_TEXT()).contains("doublethink"));
        assertEquals(null, book.getValue(TBook_CONTENT_PDF()));

        book.setValue(TBook_CONTENT_TEXT(), "Blah blah");
        book.setValue(TBook_CONTENT_PDF(), "Blah blah".getBytes());
        book.store();

        book = create().fetchOne(TBook(), TBook_TITLE().equal("1984"));

        assertEquals("Blah blah", book.getValue(TBook_CONTENT_TEXT()));
        assertEquals("Blah blah", new String(book.getValue(TBook_CONTENT_PDF())));
    }

    @Test
    public final void testManager() throws Exception {
        List<A> select = create().fetch(TAuthor());
        assertEquals(2, select.size());

        select = create().fetch(TAuthor(), TAuthor_FIRST_NAME().equal("Paulo"));
        assertEquals(1, select.size());
        assertEquals("Paulo", select.get(0).getValue(TAuthor_FIRST_NAME()));

        try {
            create().fetchOne(TAuthor());
            fail();
        }
        catch (Exception expected) {}

        A selectOne = create().fetchOne(TAuthor(), TAuthor_FIRST_NAME().equal("Paulo"));
        assertEquals("Paulo", selectOne.getValue(TAuthor_FIRST_NAME()));

        // Some CRUD operations
        A author = create().newRecord(TAuthor());
        author.setValue(TAuthor_ID(), 15);
        author.setValue(TAuthor_LAST_NAME(), "Kästner");

        assertEquals(1, create().executeInsert(TAuthor(), author));
        author.refresh();
        assertEquals(Integer.valueOf(15), author.getValue(TAuthor_ID()));
        assertEquals("Kästner", author.getValue(TAuthor_LAST_NAME()));

        assertEquals(0, create().executeUpdate(TAuthor(), author, TAuthor_ID().equal(15)));
        author.setValue(TAuthor_FIRST_NAME(), "Erich");
        assertEquals(1, create().executeUpdate(TAuthor(), author, TAuthor_ID().equal(15)));
        author = create().fetchOne(TAuthor(), TAuthor_FIRST_NAME().equal("Erich"));
        assertEquals(Integer.valueOf(15), author.getValue(TAuthor_ID()));
        assertEquals("Erich", author.getValue(TAuthor_FIRST_NAME()));
        assertEquals("Kästner", author.getValue(TAuthor_LAST_NAME()));

        create().executeDelete(TAuthor(), TAuthor_LAST_NAME().equal("Kästner"));
        assertEquals(null, create().fetchOne(TAuthor(), TAuthor_FIRST_NAME().equal("Erich")));
    }

    @Test
    public final void testRelations() throws Exception {
        if (create().getDialect() == SQLDialect.SQLITE) {
            log.info("SKIPPING", "referentials test");
            return;
        }

        // Get the book 1984
        B book1984 = create().fetchOne(TBook(), TBook_TITLE().equal("1984"));

        // Navigate to the book's author
        Record authorOrwell = (Record) invoke(book1984, "getTAuthorByAuthorId");
        assertEquals("Orwell", authorOrwell.getValue(TAuthor_LAST_NAME()));

        // Navigate back to the author's books
        List<?> books1 = (List<?>) invoke(authorOrwell, "getTBookListByAuthorId");
        assertEquals(2, books1.size());

        // Navigate through m:n relationships of books
        List<Object> booksToBookStores = new ArrayList<Object>();
        for (Object b : books1) {
            booksToBookStores.addAll((List<?>) invoke(b, "getTBookToBookStoreList"));
        }
        assertEquals(3, booksToBookStores.size());

        // Navigate to book stores
        Set<String> bookStoreNames = new TreeSet<String>();
        List<Object> bookStores = new ArrayList<Object>();
        for (Object b : booksToBookStores) {
            Object store = invoke(b, "getTBookStore");
            bookStores.add(store);
            bookStoreNames.add((String) invoke(store, "getName"));
        }
        assertEquals(Arrays.asList("Ex Libris", "Orell Füssli"), new ArrayList<String>(bookStoreNames));

        // Navigate through m:n relationships of book stores
        booksToBookStores = new ArrayList<Object>();
        for (Object b : bookStores) {
            booksToBookStores.addAll((List<?>) invoke(b, "getTBookToBookStoreList"));
        }

        // Navigate back to books
        Set<String> book2Names = new TreeSet<String>();
        List<Object> books2 = new ArrayList<Object>();
        for (Object b : booksToBookStores) {
            Object book = invoke(b, "getTBook");
            books2.add(book);
            book2Names.add((String) invoke(book, "getTitle"));
        }
        assertEquals(Arrays.asList("1984", "Animal Farm", "O Alquimista"), new ArrayList<String>(book2Names));

        // Navigate back to authors
        Set<String> authorNames = new TreeSet<String>();
        for (Object b : books2) {
            Object author = invoke(b, "getTAuthorByAuthorId");
            authorNames.add((String) invoke(author, "getLastName"));
        }
        assertEquals(Arrays.asList("Coelho", "Orwell"), new ArrayList<String>(authorNames));
    }

    @Test
    public final void testUpdatablesPK() throws Exception {
        B book = create().newRecord(TBook());
        try {
            book.refresh();
        }
        catch (SQLException expected) {}

        // Fetch the original record
        B book1 = create().fetchOne(TBook(), TBook_TITLE().equal("1984"));

        // Another copy of the original record
        B book2 = create().fetchOne(TBook(), TBook_TITLE().equal("1984"));

        // Immediately store the original record. That shouldn't have any effect
        assertEquals(0, book1.store());

        // Modify and store the original record
        Integer id = book1.getValue(TBook_ID());
        book1.setValue(TBook_TITLE(), "1985");
        assertEquals(1, book1.store());

        // Fetch the modified record
        book1 = create().fetchOne(TBook(), TBook_ID().equal(id));

        // Modify the record
        book1.setValue(TBook_TITLE(), "1999");
        assertEquals("1999", book1.getValue(TBook_TITLE()));

        // And refresh it again
        book1.refresh();
        assertEquals("1985", book1.getValue(TBook_TITLE()));
        assertEquals(0, book1.store());

        // Refresh the other copy of the original record
        book2.refresh();

        assertEquals(id, book1.getValue(TBook_ID()));
        assertEquals(id, book2.getValue(TBook_ID()));
        assertEquals("1985", book1.getValue(TBook_TITLE()));
        assertEquals("1985", book2.getValue(TBook_TITLE()));

        // Delete the modified record
        assertEquals(1, book1.delete());
        assertEquals(0, book1.delete());

        // Fetch the remaining records
        assertEquals(null, create().fetchOne(TBook(), TBook_ID().equal(id)));
    }

    @Test
    public final void testUpdatablesUK() throws Exception {
        S store = create().newRecord(TBookStore());
        try {
            store.refresh();
        }
        catch (SQLException expected) {}

        store.setValue(TBookStore_NAME(), "Rösslitor");
        store.store();
        store = create().fetchOne(TBookStore(), TBookStore_NAME().equal("Rösslitor"));
        assertEquals("Rösslitor", store.getValue(TBookStore_NAME()));

        // Updating the main unique key should result in a new record
        store.setValue(TBookStore_NAME(), "Amazon");
        store.store();
        store = create().fetchOne(TBookStore(), TBookStore_NAME().equal("Amazon"));

        assertEquals("Amazon", store.getValue(TBookStore_NAME()));

        store.delete();
        assertEquals(null, create().fetchOne(TBookStore(), TBookStore_NAME().equal("Amazon")));

        store = create().fetchOne(TBookStore(), TBookStore_NAME().equal("Rösslitor"));
        assertEquals("Rösslitor", store.getValue(TBookStore_NAME()));
    }

    @Test
    public final void testCombinedSelectQuery() throws Exception {
        SelectQuery q1 = create().selectQuery();
        SelectQuery q2 = create().selectQuery();

        q1.addFrom(TBook());
        q2.addFrom(TBook());

        q1.addConditions(TBook_AUTHOR_ID().equal(1));
        q2.addConditions(TBook_TITLE().equal("Brida"));

        // Use union all because of clob's
        Select<?> union = q1.unionAll(q2);
        int rows = union.execute();
        assertEquals(3, rows);

        // Use union all because of clob's
        rows = create().selectDistinct(union.getField(TBook_AUTHOR_ID()), TAuthor_FIRST_NAME())
            .from(union)
            .join(TAuthor())
            .on(union.getField(TBook_AUTHOR_ID()).equal(TAuthor_ID()))
            .orderBy(TAuthor_FIRST_NAME())
            .execute();

        assertEquals(2, rows);
    }

    @Test
    public final void testComplexUnions() throws Exception {
        Select<Record> s1 = create().select(TBook_TITLE()).from(TBook()).where(TBook_ID().equal(1));
        Select<Record> s2 = create().select(TBook_TITLE()).from(TBook()).where(TBook_ID().equal(2));
        Select<Record> s3 = create().select(TBook_TITLE()).from(TBook()).where(TBook_ID().equal(3));
        Select<Record> s4 = create().select(TBook_TITLE()).from(TBook()).where(TBook_ID().equal(4));

        Result<Record> result = create().select().from(s1.union(s2).union(s3).union(s4)).fetch();
        assertEquals(4, result.getNumberOfRecords());

        result = create().select().from(s1.union(s2).union(s3.union(s4))).fetch();
        assertEquals(4, result.getNumberOfRecords());

        assertEquals(4, create().select().from(s1.union(
                            create().select().from(s2.unionAll(
                                create().select().from(s3.union(s4))))))
                                    .fetch().getNumberOfRecords());

        // [#289] Handle bad syntax scenario provided by user Gunther
        Select<Record> q = create().select(constant(2008).as("y"));
        for (int year = 2009; year <= 2011; year++) {
            q = q.union(create().select(constant(year).as("y")));
        }

        assertEquals(4, q.execute());
    }

    @Test
    public final void testJoinQuery() throws Exception {
        SimpleSelectQuery<L> q1 = create().selectQuery(VLibrary());

        // TODO: Fix this when funny issue is fixed in Derby:
        // https://sourceforge.net/apps/trac/jooq/ticket/238
        // q1.addOrderBy(VLibrary_TITLE());
        q1.addOrderBy(create().plainSQLField(VLibrary().getName() + "." + VLibrary_TITLE().getName()));

        // Oracle ordering behaviour is a bit different, so exclude "1984"
        q1.addConditions(VLibrary_TITLE().notEqual("1984"));

        Table<A> a = TAuthor().as("a");
        Table<B> b = TBook().as("b");

        Field<Integer> a_authorID = a.getField(TAuthor_ID());
        Field<Integer> b_authorID = b.getField(TBook_AUTHOR_ID());
        Field<String> b_title = b.getField(TBook_TITLE());

        SelectQuery q2 = create().selectQuery();
        q2.addFrom(a);
        q2.addJoin(b, b_authorID.equal(a_authorID));
        q2.addConditions(b_title.notEqual("1984"));
        q2.addOrderBy(b_title.lower());

        int rows1 = q1.execute();
        int rows2 = q2.execute();

        assertEquals(3, rows1);
        assertEquals(3, rows2);

        Result<L> result1 = q1.getResult();
        Result<?> result2 = q2.getResult();

        assertEquals("Animal Farm", result1.getRecord(0).getValue(VLibrary_TITLE()));
        assertEquals("Animal Farm", result2.getRecord(0).getValue(b_title));

        assertEquals("Brida", result1.getRecord(1).getValue(VLibrary_TITLE()));
        assertEquals("Brida", result2.getRecord(1).getValue(b_title));

        assertEquals("O Alquimista", result1.getRecord(2).getValue(VLibrary_TITLE()));
        assertEquals("O Alquimista", result2.getRecord(2).getValue(b_title));

        // DB2 does not allow subselects in join conditions:
        // http://publib.boulder.ibm.com/infocenter/dzichelp/v2r2/index.jsp?topic=/com.ibm.db29.doc.sqlref/db2z_sql_joincondition.htm
        if (create().getDialect() != SQLDialect.DB2) {

            // Advanced JOIN usages with single JOIN condition
            Result<Record> result = create().select()
                .from(TAuthor())
                .join(TBook())
                .on(TAuthor_ID().equal(TBook_AUTHOR_ID())
                .and(TBook_LANGUAGE_ID().in(create().select(create().plainSQLField("id"))
                                                    .from("t_language")
                                                    .where("upper(cd) in (?, ?)", "DE", "EN")))
                .orExists(create().select(1).where(FALSE_CONDITION)))
                .orderBy(TBook_ID()).fetch();

            assertEquals(3, result.getNumberOfRecords());
            assertEquals("1984", result.getValue(0, TBook_TITLE()));
            assertEquals("Animal Farm", result.getValue(1, TBook_TITLE()));
            assertEquals("Brida", result.getValue(2, TBook_TITLE()));


            // Advanced JOIN usages with several JOIN condition
            // ------------------------------------------------
            Select<A> author = create().selectFrom(TAuthor());
            result = create().select()
                .from(author)
                .join(TBook())
                .on(author.getField(TAuthor_ID()).equal(TBook_AUTHOR_ID()))
                .and(TBook_LANGUAGE_ID().in(create().select(create().plainSQLField("id"))
                                                    .from("t_language")
                                                    .where("upper(cd) in (?, ?)", "DE", "EN")))
                .orExists(create().select(1).where(FALSE_CONDITION))
                .orderBy(TBook_ID()).fetch();

            assertEquals(3, result.getNumberOfRecords());
            assertEquals("1984", result.getValue(0, TBook_TITLE()));
            assertEquals("Animal Farm", result.getValue(1, TBook_TITLE()));
            assertEquals("Brida", result.getValue(2, TBook_TITLE()));

            Select<B> book = create().selectFrom(TBook());
            result = create().select()
                .from(TAuthor())
                .join(book)
                .on(TAuthor_ID().equal(book.getField(TBook_AUTHOR_ID())))
                .and(book.getField(TBook_LANGUAGE_ID()).in(create().select(create().plainSQLField("id"))
                                                    .from("t_language")
                                                    .where("upper(cd) in (?, ?)", "DE", "EN")))
                .orExists(create().select(1).where(FALSE_CONDITION))
                .orderBy(book.getField(TBook_ID())).fetch();

            assertEquals(3, result.getNumberOfRecords());
            assertEquals("1984", result.getValue(0, TBook_TITLE()));
            assertEquals("Animal Farm", result.getValue(1, TBook_TITLE()));
            assertEquals("Brida", result.getValue(2, TBook_TITLE()));

        }
    }

    @Test
    public final void testAliasing() throws Exception {
        Table<B> b = TBook().as("b");
        Field<Integer> b_ID = b.getField(TBook_ID());

        List<Integer> ids = create().select(b_ID).from(b).orderBy(b_ID).fetch(b_ID);
        assertEquals(4, ids.size());
        assertEquals(Arrays.asList(1, 2, 3, 4), ids);

        Result<Record> books = create().select().from(b).orderBy(b_ID).fetch();
        assertEquals(4, books.getNumberOfRecords());
        assertEquals(Arrays.asList(1, 2, 3, 4), books.getValues(b_ID));
    }

    @Test
    public final void testArithmeticExpressions() throws Exception {
        Field<Integer> f1 = constant(1).add(2).add(3).divide(2);
        Field<Integer> f2 = constant(10).divide(5).add(constant(3).subtract(2));

        SelectQuery q1 = create().selectQuery();
        q1.addSelect(f1, f2);
        q1.execute();

        Result<?> result = q1.getResult();
        assertEquals(1, result.getNumberOfRecords());
        assertEquals(Integer.valueOf(3), result.getValue(0, f1));
        assertEquals(Integer.valueOf(3), result.getValue(0, f2));

        Field<Integer> f3 = TBook_PUBLISHED_IN().add(3).divide(7);
        Field<Integer> f4 = TBook_PUBLISHED_IN().subtract(4).multiply(8);

        SelectQuery q2 = create().selectQuery();
        q2.addSelect(f3);
        q2.addSelect(f4);
        q2.addFrom(TBook());
        q2.addConditions(TBook_TITLE().equal("1984"));
        q2.execute();

        result = q2.getResult();
        assertEquals(Integer.valueOf((1948 + 3) / 7), result.getValue(0, f3));
        assertEquals(Integer.valueOf((1948 - 4) * 8), result.getValue(0, f4));
    }

    @Test
    public final void testStoredFunctions() throws Exception {
        if (Functions() == null) {
            log.info("SKIPPING", "functions test");
            return;
        }

        // ---------------------------------------------------------------------
        // Standalone calls
        // ---------------------------------------------------------------------
        assertEquals("0", "" + invoke(Functions(), "fAuthorExists", connection, null));
        assertEquals("1", "" + invoke(Functions(), "fAuthorExists", connection, "Paulo"));
        assertEquals("0", "" + invoke(Functions(), "fAuthorExists", connection, "Shakespeare"));
        assertEquals("1", "" + invoke(Functions(), "fOne", connection));
        assertEquals("1", "" + invoke(Functions(), "fNumber", connection, 1));
        assertEquals(null, invoke(Functions(), "fNumber", connection, null));
        assertEquals("1204", "" + invoke(Functions(), "f317", connection, 1, 2, 3, 4));
        assertEquals("1204", "" + invoke(Functions(), "f317", connection, 1, 2, null, 4));
        assertEquals("4301", "" + invoke(Functions(), "f317", connection, 4, 3, 2, 1));
        assertEquals("4301", "" + invoke(Functions(), "f317", connection, 4, 3, null, 1));
        assertEquals("1101", "" + invoke(Functions(), "f317", connection, 1, 1, 1, 1));
        assertEquals("1101", "" + invoke(Functions(), "f317", connection, 1, 1, null, 1));

        // ---------------------------------------------------------------------
        // Embedded calls
        // ---------------------------------------------------------------------
        Field<Integer> f1a = FAuthorExistsField("Paulo").cast(Integer.class);
        Field<Integer> f2a = FAuthorExistsField("Shakespeare").cast(Integer.class);
        Field<Integer> f3a = FOneField().cast(Integer.class);
        Field<Integer> f4a = FNumberField(42).cast(Integer.class);
        Field<Integer> f5a = FNumberField(FNumberField(FOneField())).cast(Integer.class);
        Field<Integer> f6a = F317Field(1, 2, null, 4).cast(Integer.class);
        Field<Integer> f7a = F317Field(4, 3, null, 1).cast(Integer.class);
        Field<Integer> f8a = F317Field(1, 1, null, 1).cast(Integer.class);
        Field<Integer> f9a = F317Field(FNumberField(1), FNumberField(2), FNumberField((Number) null), FNumberField(4)).cast(Integer.class);

        // Repeat fields to check correct fetching from resultset
        Field<Integer> f1b = FAuthorExistsField("Paulo").cast(Integer.class);
        Field<Integer> f2b = FAuthorExistsField("Shakespeare").cast(Integer.class);
        Field<Integer> f3b = FOneField().cast(Integer.class);
        Field<Integer> f4b = FNumberField(42).cast(Integer.class);
        Field<Integer> f5b = FNumberField(FNumberField(FOneField())).cast(Integer.class);
        Field<Integer> f6b = F317Field(1, 2, 3, 4).cast(Integer.class);
        Field<Integer> f7b = F317Field(4, 3, 2, 1).cast(Integer.class);
        Field<Integer> f8b = F317Field(1, 1, 1, 1).cast(Integer.class);
        Field<Integer> f9b = F317Field(FNumberField(1), FNumberField(2), FNumberField(3), FNumberField(4)).cast(Integer.class);

        // Null argument checks
        Field<Integer> f10 = FAuthorExistsField(null).cast(Integer.class);

        SelectQuery q = create().selectQuery();
        q.addSelect(
            f1a, f2a, f3a, f4a, f5a, f6a, f7a, f8a, f9a,
            f1b, f2b, f3b, f4b, f5b, f6b, f7b, f8b, f9b, f10);
        q.execute();
        Result<Record> result = q.getResult();

        assertEquals(1, result.getNumberOfRecords());
        assertEquals("1", result.getRecord(0).getValueAsString(f1a));
        assertEquals("0", result.getRecord(0).getValueAsString(f2a));
        assertEquals("1", result.getRecord(0).getValueAsString(f3a));
        assertEquals("42", result.getRecord(0).getValueAsString(f4a));
        assertEquals("1", result.getRecord(0).getValueAsString(f5a));
        assertEquals("1204", result.getRecord(0).getValueAsString(f6a));
        assertEquals("4301", result.getRecord(0).getValueAsString(f7a));
        assertEquals("1101", result.getRecord(0).getValueAsString(f8a));
        assertEquals("1204", result.getRecord(0).getValueAsString(f9a));

        assertEquals("1", result.getRecord(0).getValueAsString(f1b));
        assertEquals("0", result.getRecord(0).getValueAsString(f2b));
        assertEquals("1", result.getRecord(0).getValueAsString(f3b));
        assertEquals("42", result.getRecord(0).getValueAsString(f4b));
        assertEquals("1", result.getRecord(0).getValueAsString(f5b));
        assertEquals("1204", result.getRecord(0).getValueAsString(f6b));
        assertEquals("4301", result.getRecord(0).getValueAsString(f7b));
        assertEquals("1101", result.getRecord(0).getValueAsString(f8b));
        assertEquals("1204", result.getRecord(0).getValueAsString(f9b));

        assertEquals("0", result.getRecord(0).getValueAsString(f10));

        // ---------------------------------------------------------------------
        // Functions in conditions
        // ---------------------------------------------------------------------
        assertEquals(Integer.valueOf(1),
            create().select(1).where(f4b.equal(1)).or(f1b.equal(1)).fetchOne(0));
        assertEquals(null,
            create().select(1).where(f4b.equal(1)).and(f1b.equal(1)).fetchOne(0));
        assertEquals(null,
            create().select(1).where(f4b.equal(1)).and(f1b.equal(1)).fetchOne());

        // ---------------------------------------------------------------------
        // Functions in SQL
        // ---------------------------------------------------------------------
        result = create().select(
                FNumberField(1).cast(Integer.class),
                FNumberField(TAuthor_ID()).cast(Integer.class),
                FNumberField(FNumberField(TAuthor_ID())).cast(Integer.class))
            .from(TAuthor())
            .orderBy(TAuthor_ID())
            .fetch();

        assertEquals(Integer.valueOf(1), result.getValue(0, 0));
        assertEquals(Integer.valueOf(1), result.getValue(0, 1));
        assertEquals(Integer.valueOf(1), result.getValue(0, 2));
        assertEquals(Integer.valueOf(1), result.getValue(1, 0));
        assertEquals(Integer.valueOf(2), result.getValue(1, 1));
        assertEquals(Integer.valueOf(2), result.getValue(1, 2));
    }

    @Test
    public final void testFunctionsOnDates() throws Exception {
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
    public final void testFunctionsOnStrings() throws Exception {
        assertEquals("abc", create().select(constant("a").concatenate("b", "c")).fetchOne(0));
        assertEquals("George Orwell", create()
            .select(TAuthor_FIRST_NAME().concatenate(" ").concatenate(TAuthor_LAST_NAME()))
            .from(TAuthor())
            .where(TAuthor_FIRST_NAME().equal("George")).fetchOne(0));

        SelectQuery q = create().selectQuery();
        Field<String> constant = constant("abc");
        Field<Integer> charLength = constant.charLength();
        Field<Integer> bitLength = constant.bitLength();
        Field<Integer> octetLength = constant.octetLength();

        switch (create().getDialect()) {

            // DERBY does not have a replace function
            case DERBY:
                log.info("SKIPPING", "replace function test");
                break;

            // These two tests will validate #154
            default: {
                Field<String> x = constant.replace("b", "x");
                Field<String> y = constant.replace("b", "y");
                Record record = create().select(x, y).fetchOne();

                assertEquals("axc", record.getValue(x));
                assertEquals("ayc", record.getValue(y));
            }
        }

        q.addSelect(charLength, bitLength, octetLength);
        q.execute();

        Record record = q.getResult().getRecord(0);

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
    public final void testFunctionPosition() throws Exception {
        // SQLite does not have anything like the position function
        if (create().getDialect() == SQLDialect.SQLITE) {
            log.info("SKIPPING", "position function test");
            return;
        }

        SelectQuery q = create().selectQuery();
        q.addFrom(VLibrary());

        Field<Integer> position = VLibrary_AUTHOR().position("o").as("p");
        q.addSelect(VLibrary_AUTHOR());
        q.addSelect(position);

        // https://issues.apache.org/jira/browse/DERBY-5005
        q.addOrderBy(create().plainSQLField("AUTHOR"));

        q.execute();
        Record r1 = q.getResult().getRecord(1); // George Orwell
        Record r2 = q.getResult().getRecord(2); // Paulo Coelho

        assertEquals(Integer.valueOf(3), r1.getValue(position));
        assertEquals(Integer.valueOf(5), r2.getValue(position));

        // Implicit check on the rownum function in oracle dialect
        L library = create().fetchAny(VLibrary());
        assertTrue(library != null);
    }

    @Test
    public final void testFunctionsLikeDecode() throws Exception {
        Field<String> sNull = create().castNull(String.class);
        Field<Integer> iNull = create().castNull(Integer.class);

        // ---------------------------------------------------------------------
        // NULLIF
        // ---------------------------------------------------------------------
        assertEquals("1", create().select(constant("1").nullif("2")).fetchOne(0));
        assertEquals(null, create().select(constant("1").nullif("1")).fetchOne(0));
        assertEquals("1", "" + create().select(constant(1).nullif(2)).fetchOne(0));
        assertEquals(null, create().select(constant(1).nullif(1)).fetchOne(0));

        // ---------------------------------------------------------------------
        // NVL
        // ---------------------------------------------------------------------
        assertEquals(null, create().select(sNull.nvl(sNull)).fetchOne(0));
        assertEquals(Integer.valueOf(1), create().select(iNull.nvl(1)).fetchOne(0));
        assertEquals("1", create().select(sNull.nvl("1")).fetchOne(0));
        assertEquals(Integer.valueOf(2), create().select(constant(2).nvl(1)).fetchOne(0));
        assertEquals("2", create().select(constant("2").nvl("1")).fetchOne(0));
        assertTrue(("" + create()
            .select(TBook_CONTENT_TEXT().nvl("abc"))
            .from(TBook())
            .where(TBook_ID().equal(1)).fetchOne(0)).startsWith("To know and"));
        assertEquals("abc", create()
            .select(TBook_CONTENT_TEXT().nvl("abc"))
            .from(TBook())
            .where(TBook_ID().equal(2)).fetchOne(0));

        // ---------------------------------------------------------------------
        // NVL2
        // ---------------------------------------------------------------------
        assertEquals(null, create().select(sNull.nvl2(sNull, sNull)).fetchOne(0));
        assertEquals(Integer.valueOf(1), create().select(iNull.nvl2(2, 1)).fetchOne(0));
        assertEquals("1", create().select(sNull.nvl2("2", "1")).fetchOne(0));
        assertEquals(Integer.valueOf(2), create().select(constant(2).nvl2(2, 1)).fetchOne(0));
        assertEquals("2", create().select(constant("2").nvl2("2", "1")).fetchOne(0));
        assertEquals("abc", create()
            .select(TBook_CONTENT_TEXT().nvl2("abc", "xyz"))
            .from(TBook())
            .where(TBook_ID().equal(1)).fetchOne(0));
        assertEquals("xyz", create()
            .select(TBook_CONTENT_TEXT().nvl2("abc", "xyz"))
            .from(TBook())
            .where(TBook_ID().equal(2)).fetchOne(0));

        // ---------------------------------------------------------------------
        // COALESCE
        // ---------------------------------------------------------------------
        assertEquals(null, create().select(sNull.coalesce(sNull)).fetchOne(0));
        assertEquals(Integer.valueOf(1), create().select(iNull.coalesce(1)).fetchOne(0));
        assertEquals(Integer.valueOf(1), create().select(iNull.coalesce(iNull, constant(1))).fetchOne(0));
        assertEquals(Integer.valueOf(1), create().select(iNull.coalesce(iNull, iNull, constant(1))).fetchOne(0));

        assertEquals("1", create().select(sNull.coalesce("1")).fetchOne(0));
        assertEquals("1", create().select(sNull.coalesce(sNull, constant("1"))).fetchOne(0));
        assertEquals("1", create().select(sNull.coalesce(sNull, sNull, constant("1"))).fetchOne(0));

        assertEquals(Integer.valueOf(2), create().select(constant(2).coalesce(1)).fetchOne(0));
        assertEquals(Integer.valueOf(2), create().select(constant(2).coalesce(1, 1)).fetchOne(0));
        assertEquals(Integer.valueOf(2), create().select(constant(2).coalesce(1, 1, 1)).fetchOne(0));

        assertEquals("2", create().select(constant("2").coalesce("1")).fetchOne(0));
        assertEquals("2", create().select(constant("2").coalesce("1", "1")).fetchOne(0));
        assertEquals("2", create().select(constant("2").coalesce("1", "1", "1")).fetchOne(0));

        assertTrue(("" + create()
            .select(TBook_CONTENT_TEXT().cast(String.class).coalesce(sNull, constant("abc")))
            .from(TBook())
            .where(TBook_ID().equal(1)).fetchOne(0)).startsWith("To know and"));
        assertEquals("abc", create()
            .select(TBook_CONTENT_TEXT().cast(String.class).coalesce(sNull, constant("abc")))
            .from(TBook())
            .where(TBook_ID().equal(2)).fetchOne(0));

        // ---------------------------------------------------------------------
        // DECODE
        // ---------------------------------------------------------------------
        assertEquals(null, create().select(sNull.decode(sNull, sNull)).fetchOne(0));

        assertEquals(null, create().select(iNull.decode(2, 1)).fetchOne(0));
        assertEquals(Integer.valueOf(1), create().select(iNull.decode(2, 1, 1)).fetchOne(0));
        assertEquals(Integer.valueOf(1), create().select(iNull.decode(iNull, constant(1))).fetchOne(0));
        assertEquals(Integer.valueOf(1), create().select(iNull.decode(iNull, constant(1), constant(2))).fetchOne(0));
        assertEquals(Integer.valueOf(1), create().select(iNull.decode(constant(2), constant(2), iNull, constant(1))).fetchOne(0));
        assertEquals(Integer.valueOf(1), create().select(iNull.decode(constant(2), constant(2), iNull, constant(1), constant(3))).fetchOne(0));

        assertEquals(null, create().select(sNull.decode("2", "1")).fetchOne(0));
        assertEquals("1", create().select(sNull.decode("2", "1", "1")).fetchOne(0));
        assertEquals("1", create().select(sNull.decode(sNull, constant("1"))).fetchOne(0));
        assertEquals("1", create().select(sNull.decode(sNull, constant("1"), constant("2"))).fetchOne(0));
        assertEquals("1", create().select(sNull.decode(constant("2"), constant("2"), sNull, constant("1"))).fetchOne(0));
        assertEquals("1", create().select(sNull.decode(constant("2"), constant("2"), sNull, constant("1"), constant("3"))).fetchOne(0));

        Field<Integer> lang = TBook_LANGUAGE_ID().cast(Integer.class);
        Result<Record> result = create().select(
                lang.decode(1, "EN"),
                lang.decode(1, "EN", "Other"),
                lang.decode(1, "EN", 2, "DE"),
                lang.decode(1, "EN", 2, "DE", "Other"))
            .from(TBook())
            .orderBy(TBook_ID()).fetch();

        assertEquals("EN", result.getValue(0, 0));
        assertEquals("EN", result.getValue(1, 0));
        assertEquals(null, result.getValue(2, 0));
        assertEquals(null, result.getValue(3, 0));

        assertEquals("EN", result.getValue(0, 1));
        assertEquals("EN", result.getValue(1, 1));
        assertEquals("Other", result.getValue(2, 1));
        assertEquals("Other", result.getValue(3, 1));

        assertEquals("EN", result.getValue(0, 2));
        assertEquals("EN", result.getValue(1, 2));
        assertEquals(null, result.getValue(2, 2));
        assertEquals("DE", result.getValue(3, 2));

        assertEquals("EN", result.getValue(0, 3));
        assertEquals("EN", result.getValue(1, 3));
        assertEquals("Other", result.getValue(2, 3));
        assertEquals("DE", result.getValue(3, 3));
    }

    @Test
    public final void testCaseStatement() throws Exception {
        if (create().getDialect() != SQLDialect.DERBY) {
            Field<String> case1 = create().decode()
                .value(TBook_PUBLISHED_IN())
                .when(0, "ancient book")
                .as("case1");


            Field<?> case2 = create().decode()
                .value(TBook_AUTHOR_ID())
                .when(1, create().select(TAuthor_LAST_NAME())
                    .from(TAuthor())
                    .where(TAuthor_ID().equal(TBook_AUTHOR_ID())).asField())
                .otherwise("unknown");

            SelectQuery query = create().selectQuery();
            query.addSelect(case1, case2);
            query.addFrom(TBook());
            query.addOrderBy(TBook_PUBLISHED_IN());
            query.execute();

            Result<Record> result = query.getResult();
            assertEquals(null, result.getValue(0, case1));
            assertEquals(null, result.getValue(1, case1));
            assertEquals(null, result.getValue(2, case1));
            assertEquals(null, result.getValue(3, case1));

            assertEquals("Orwell", result.getValue(0, case2));
            assertEquals("Orwell", result.getValue(1, case2));
            assertEquals("unknown", result.getValue(2, case2));
            assertEquals("unknown", result.getValue(3, case2));
        }

        Field<String> case3 = create().decode()
            .when(TBook_PUBLISHED_IN().equal(1948), "probably orwell")
            .when(TBook_PUBLISHED_IN().equal(1988), "probably coelho")
            .otherwise("don't know").as("case3");

        SelectQuery query = create().selectQuery();
        query.addSelect(case3);
        query.addFrom(TBook());
        query.addOrderBy(TBook_PUBLISHED_IN());
        query.execute();

        Result<Record> result = query.getResult();

        // Note: trims are necessary, as certain databases use
        // CHAR datatype here, not VARCHAR
        assertEquals("don't know", result.getValue(0, case3).trim());
        assertEquals("probably orwell", result.getValue(1, case3).trim());
        assertEquals("probably coelho", result.getValue(2, case3).trim());
        assertEquals("don't know", result.getValue(3, case3).trim());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public final void testEnums() throws Exception {
        if (TBook_STATUS() == null) {
            log.info("SKIPPING", "enums test");
            return;
        }

        B book = create()
            .selectFrom(TBook())
            .where(TBook_PUBLISHED_IN().equal(1990))
            .fetchOne();
        Enum<?> value = book.getValue(TBook_STATUS());
        assertEquals("SOLD_OUT", value.name());
        assertEquals("SOLD OUT", ((EnumType) value).getLiteral());

        // Another copy of the original record
        book = create().fetchOne(TBook(), TBook_TITLE().equal("1984"));
        book.setValue((Field) TBook_STATUS(), Enum.valueOf(value.getClass(), "ON_STOCK"));
        book.store();

        book = create().fetchOne(TBook(), TBook_TITLE().equal("1984"));
        value = book.getValue(TBook_STATUS());
        assertEquals("ON_STOCK", value.name());
        assertEquals("ON STOCK", ((EnumType) value).getLiteral());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public final void testMasterData() throws Exception {
        if (supportsReferences()) {
            B book = create().fetchOne(TBook(), TBook_TITLE().equal("1984"));

            Enum<?> value = (Enum<?>) book.getValue(TBook_LANGUAGE_ID());
            assertEquals(Integer.valueOf(1), ((MasterDataType<?>) value).getPrimaryKey());
            assertEquals("en", value.name());

            book.setValue((Field) TBook_LANGUAGE_ID(), Enum.valueOf(value.getClass(), "de"));
            book.store();

            book = create().fetchOne(TBook(), TBook_TITLE().equal("1984"));
            value = (Enum<?>) book.getValue(TBook_LANGUAGE_ID());
            assertEquals(Integer.valueOf(2), ((MasterDataType<?>) value).getPrimaryKey());
            assertEquals("de", value.name());
        } else {
            log.info("SKIPPING", "master data test");
        }
    }

    protected void prepareArrayTest() throws Exception {
    }

    @Test
    public final void testARRAYType() throws Exception {
        prepareArrayTest();

        if (TArrays() == null) {
            log.info("SKIPPING", "ARRAY type test");
            return;
        }

        if (TArrays_STRING_R() != null) {
            Result<?> arrays = create().select(
                TArrays_STRING_R(),
                TArrays_NUMBER_R(),
                TArrays_DATE_R())
            .from(TArrays())
            .orderBy(TArrays_ID())
            .fetch();

            assertEquals(null, arrays.getValue(0, TArrays_STRING_R()));
            assertEquals(null, arrays.getValue(0, TArrays_NUMBER_R()));
            assertEquals(null, arrays.getValue(0, TArrays_DATE_R()));

            assertEquals(Arrays.asList(), Arrays.asList(arrays.getValueAsArray(1, TArrays_STRING_R())));
            assertEquals(Arrays.asList(), Arrays.asList(arrays.getValueAsArray(1, TArrays_NUMBER_R())));
            assertEquals(Arrays.asList(), Arrays.asList(arrays.getValueAsArray(1, TArrays_DATE_R())));

            assertEquals(Arrays.asList("a"), Arrays.asList(arrays.getValueAsArray(2, TArrays_STRING_R())));
            assertEquals(Arrays.asList(1), Arrays.asList(arrays.getValueAsArray(2, TArrays_NUMBER_R())));
            assertEquals("[1981-07-10]", Arrays.asList(arrays.getValueAsArray(2, TArrays_DATE_R())).toString());

            assertEquals(Arrays.asList("a", "b"), Arrays.asList(arrays.getValueAsArray(3, TArrays_STRING_R())));
            assertEquals(Arrays.asList(1, 2), Arrays.asList(arrays.getValueAsArray(3, TArrays_NUMBER_R())));
            assertEquals("[1981-07-10, 2000-01-01]", Arrays.asList(arrays.getValueAsArray(3, TArrays_DATE_R())).toString());



            InsertQuery<?> insert = create().insertQuery(TArrays());
            insert.addValue(TArrays_ID(), 5);
            insert.addValueAsArray(TArrays_NUMBER_R(), 1, 2, 3);
            insert.addValueAsArray(TArrays_STRING_R(), "a", "b", "c");
            insert.addValueAsArray(TArrays_DATE_R(), new Date(0), new Date(84600 * 1000), new Date(84600 * 2000));
            insert.execute();

            Record array = create().select(
                TArrays_STRING_R(),
                TArrays_NUMBER_R(),
                TArrays_DATE_R())
            .from(TArrays())
            .where(TArrays_ID().equal(5))
            .fetchOne();

            assertEquals(Arrays.asList("a", "b", "c"), Arrays.asList(array.getValueAsArray(TArrays_STRING_R())));
            assertEquals(Arrays.asList(1, 2, 3), Arrays.asList(array.getValueAsArray(TArrays_NUMBER_R())));
            assertEquals("[1970-01-01, 1970-01-02, 1970-01-03]", Arrays.asList(array.getValueAsArray(TArrays_DATE_R())).toString());



            UpdateQuery<X> update = create().updateQuery(TArrays());
            update.addValueAsArray(TArrays_NUMBER_R(), 3, 2, 1);
            update.addValueAsArray(TArrays_STRING_R(), "c", "b", "a");
            update.addValueAsArray(TArrays_DATE_R(), new Date(84600 * 2000), new Date(84600 * 1000), new Date(0));
            update.addConditions(TArrays_ID().equal(5));
            update.execute();

            array = create().select(
                TArrays_STRING_R(),
                TArrays_NUMBER_R(),
                TArrays_DATE_R())
            .from(TArrays())
            .where(TArrays_ID().equal(5))
            .fetchOne();

            assertEquals(Arrays.asList("c", "b", "a"), Arrays.asList(array.getValueAsArray(TArrays_STRING_R())));
            assertEquals(Arrays.asList(3, 2, 1), Arrays.asList(array.getValueAsArray(TArrays_NUMBER_R())));
            assertEquals("[1970-01-03, 1970-01-02, 1970-01-01]", Arrays.asList(array.getValueAsArray(TArrays_DATE_R())).toString());
        }

        if (TArrays_STRING() != null) {
            Result<?> arrays = create().select(
                TArrays_STRING(),
                TArrays_NUMBER(),
                TArrays_DATE())
            .from(TArrays())
            .orderBy(TArrays_ID())
            .fetch();

            assertEquals(null, arrays.getValue(0, TArrays_STRING()));
            assertEquals(null, arrays.getValue(0, TArrays_NUMBER()));
            assertEquals(null, arrays.getValue(0, TArrays_DATE()));


            // These expressions are a bit verbose. Unfortunately, H2 does not
            // support typed arrays, hence the tests are kept general
            // http://groups.google.com/group/h2-database/browse_thread/thread/42e38afa682d4fc2
            Object[] s = (Object[]) arrays.getValue(1, 0);
            Object[] n = (Object[]) arrays.getValue(1, 1);
            Object[] d = (Object[]) arrays.getValue(1, 2);
            assertEquals(0, s.length);
            assertEquals(0, n.length);
            assertEquals(0, d.length);
            assertEquals(TArrays_STRING().getType(), s.getClass());
            assertEquals(TArrays_NUMBER().getType(), n.getClass());
            assertEquals(TArrays_DATE().getType(), d.getClass());


            s = (Object[]) arrays.getValue(2, 0);
            n = (Object[]) arrays.getValue(2, 1);
            d = (Object[]) arrays.getValue(2, 2);
            assertEquals(1, s.length);
            assertEquals(1, n.length);
            assertEquals(1, d.length);
            assertEquals(TArrays_STRING().getType(), s.getClass());
            assertEquals(TArrays_NUMBER().getType(), n.getClass());
            assertEquals(TArrays_DATE().getType(), d.getClass());
            assertEquals("a", s[0].toString());
            assertEquals("1", n[0].toString());
            assertEquals("1981-07-10", d[0].toString());


            s = (Object[]) arrays.getValue(3, 0);
            n = (Object[]) arrays.getValue(3, 1);
            d = (Object[]) arrays.getValue(3, 2);
            assertEquals(2, s.length);
            assertEquals(2, n.length);
            assertEquals(2, d.length);
            assertEquals(TArrays_STRING().getType(), s.getClass());
            assertEquals(TArrays_NUMBER().getType(), n.getClass());
            assertEquals(TArrays_DATE().getType(), d.getClass());
            assertEquals("a", s[0].toString());
            assertEquals("b", s[1].toString());
            assertEquals("1", n[0].toString());
            assertEquals("2", n[1].toString());
            assertEquals("1981-07-10", d[0].toString());
            assertEquals("2000-01-01", d[1].toString());




            InsertQuery<?> insert = create().insertQuery(TArrays());
            insert.addValue(TArrays_ID(), 5);
            insert.addValue(TArrays_NUMBER(), new Integer[] { 1, 2, 3 });
            insert.addValue(TArrays_STRING(), new String[] { "a", "b", "c" });
            insert.addValue(TArrays_DATE(), new Date[] { new Date(0), new Date(84600 * 1000), new Date(84600 * 2000)});
            insert.execute();

            Record array = create().select(
                TArrays_STRING(),
                TArrays_NUMBER(),
                TArrays_DATE())
            .from(TArrays())
            .where(TArrays_ID().equal(5))
            .fetchOne();

            s = (Object[]) array.getValue(0);
            n = (Object[]) array.getValue(1);
            d = (Object[]) array.getValue(2);
            assertEquals(3, s.length);
            assertEquals(3, n.length);
            assertEquals(3, d.length);
            assertEquals(TArrays_STRING().getType(), s.getClass());
            assertEquals(TArrays_NUMBER().getType(), n.getClass());
            assertEquals(TArrays_DATE().getType(), d.getClass());
            assertEquals("a", s[0].toString());
            assertEquals("b", s[1].toString());
            assertEquals("c", s[2].toString());
            assertEquals("1", n[0].toString());
            assertEquals("2", n[1].toString());
            assertEquals("3", n[2].toString());
            assertEquals("1970-01-01", d[0].toString());
            assertEquals("1970-01-02", d[1].toString());
            assertEquals("1970-01-03", d[2].toString());



            UpdateQuery<X> update = create().updateQuery(TArrays());
            update.addValue(TArrays_NUMBER(), new Integer[] { 3, 2, 1});
            update.addValue(TArrays_STRING(), new String[] { "c", "b", "a" });
            update.addValue(TArrays_DATE(), new Date[] { new Date(84600 * 2000), new Date(84600 * 1000), new Date(0) });
            update.addConditions(TArrays_ID().equal(5));
            update.execute();

            array = create().select(
                TArrays_STRING(),
                TArrays_NUMBER(),
                TArrays_DATE())
            .from(TArrays())
            .where(TArrays_ID().equal(5))
            .fetchOne();

            s = (Object[]) array.getValue(0);
            n = (Object[]) array.getValue(1);
            d = (Object[]) array.getValue(2);
            assertEquals(3, s.length);
            assertEquals(3, n.length);
            assertEquals(3, d.length);
            assertEquals(TArrays_STRING().getType(), s.getClass());
            assertEquals(TArrays_NUMBER().getType(), n.getClass());
            assertEquals(TArrays_DATE().getType(), d.getClass());
            assertEquals("c", s[0].toString());
            assertEquals("b", s[1].toString());
            assertEquals("a", s[2].toString());
            assertEquals("3", n[0].toString());
            assertEquals("2", n[1].toString());
            assertEquals("1", n[2].toString());
            assertEquals("1970-01-03", d[0].toString());
            assertEquals("1970-01-02", d[1].toString());
            assertEquals("1970-01-01", d[2].toString());
        }
    }

    @Test
    public final void testARRAYProcedure() throws Exception {
        if (Procedures() == null) {
            log.info("SKIPPING", "ARRAY procedure test (no procedure support)");
            return;
        }

        if (TArrays() == null) {
            log.info("SKIPPING", "ARRAY procedure test (no array support)");
            return;
        }

        if (TArrays_STRING_R() != null) {
            ArrayRecord<Integer> i;
            ArrayRecord<Long> l;
            ArrayRecord<String> s;

            assertEquals(null, invoke(Procedures(), "pArrays1", connection, null));
            assertEquals(null, invoke(Procedures(), "pArrays2", connection, null));
            assertEquals(null, invoke(Procedures(), "pArrays3", connection, null));
            assertEquals(null, invoke(Functions(), "fArrays1", connection, null));
            assertEquals(null, invoke(Functions(), "fArrays2", connection, null));
            assertEquals(null, invoke(Functions(), "fArrays3", connection, null));

            i = newNUMBER_R();
            l = newNUMBER_LONG_R();
            s = newSTRING_R();

            assertEquals(
                Arrays.asList(new Integer[0]),
                Arrays.asList(((ArrayRecord<?>) invoke(Procedures(), "pArrays1", connection, i)).get()));
            assertEquals(
                Arrays.asList(new Long[0]),
                Arrays.asList(((ArrayRecord<?>) invoke(Procedures(), "pArrays2", connection, l)).get()));
            assertEquals(
                Arrays.asList(new String[0]),
                Arrays.asList(((ArrayRecord<?>) invoke(Procedures(), "pArrays3", connection, s)).get()));
            assertEquals(
                Arrays.asList(new Integer[0]),
                Arrays.asList(((ArrayRecord<?>) invoke(Functions(), "fArrays1", connection, i)).get()));
            assertEquals(
                Arrays.asList(new Long[0]),
                Arrays.asList(((ArrayRecord<?>) invoke(Functions(), "fArrays2", connection, l)).get()));
            assertEquals(
                Arrays.asList(new String[0]),
                Arrays.asList(((ArrayRecord<?>) invoke(Functions(), "fArrays3", connection, s)).get()));

            i = newNUMBER_R();
            l = newNUMBER_LONG_R();
            s = newSTRING_R();

            i.set(1);
            l.set(1L);
            s.set("1");

            assertEquals(
                Arrays.asList(1),
                Arrays.asList(((ArrayRecord<?>) invoke(Procedures(), "pArrays1", connection, i)).get()));
            assertEquals(
                Arrays.asList(1L),
                Arrays.asList(((ArrayRecord<?>) invoke(Procedures(), "pArrays2", connection, l)).get()));
            assertEquals(
                Arrays.asList("1"),
                Arrays.asList(((ArrayRecord<?>) invoke(Procedures(), "pArrays3", connection, s)).get()));
            assertEquals(
                Arrays.asList(1),
                Arrays.asList(((ArrayRecord<?>) invoke(Functions(), "fArrays1", connection, i)).get()));
            assertEquals(
                Arrays.asList(1L),
                Arrays.asList(((ArrayRecord<?>) invoke(Functions(), "fArrays2", connection, l)).get()));
            assertEquals(
                Arrays.asList("1"),
                Arrays.asList(((ArrayRecord<?>) invoke(Functions(), "fArrays3", connection, s)).get()));

            i = newNUMBER_R();
            l = newNUMBER_LONG_R();
            s = newSTRING_R();

            i.set(1, 2);
            l.set(1L, 2L);
            s.set("1", "2");

            assertEquals(
                Arrays.asList(1, 2),
                Arrays.asList(((ArrayRecord<?>) invoke(Procedures(), "pArrays1", connection, i)).get()));
            assertEquals(
                Arrays.asList(1L, 2L),
                Arrays.asList(((ArrayRecord<?>) invoke(Procedures(), "pArrays2", connection, l)).get()));
            assertEquals(
                Arrays.asList("1", "2"),
                Arrays.asList(((ArrayRecord<?>) invoke(Procedures(), "pArrays3", connection, s)).get()));
            assertEquals(
                Arrays.asList(1, 2),
                Arrays.asList(((ArrayRecord<?>) invoke(Functions(), "fArrays1", connection, i)).get()));
            assertEquals(
                Arrays.asList(1L, 2L),
                Arrays.asList(((ArrayRecord<?>) invoke(Functions(), "fArrays2", connection, l)).get()));
            assertEquals(
                Arrays.asList("1", "2"),
                Arrays.asList(((ArrayRecord<?>) invoke(Functions(), "fArrays3", connection, s)).get()));
        }

        if (TArrays_STRING() != null) {
            if (supportsOUTParameters()) {
                assertEquals(null, invoke(Procedures(), "pArrays1", connection, null));
                assertEquals(null, invoke(Procedures(), "pArrays2", connection, null));
                assertEquals(null, invoke(Procedures(), "pArrays3", connection, null));
            }

            assertEquals(null, invoke(Functions(), "fArrays1", connection, null));
            assertEquals(null, invoke(Functions(), "fArrays2", connection, null));
            assertEquals(null, invoke(Functions(), "fArrays3", connection, null));

            if (supportsOUTParameters()) {
                assertEquals(
                    Arrays.asList(new Integer[0]),
                    Arrays.asList((Integer[]) invoke(Procedures(), "pArrays1", connection, new Integer[0])));
                assertEquals(
                    Arrays.asList(new Long[0]),
                    Arrays.asList((Long[]) invoke(Procedures(), "pArrays2", connection, new Long[0])));
                assertEquals(
                    Arrays.asList(new String[0]),
                    Arrays.asList((String[]) invoke(Procedures(), "pArrays3", connection, new String[0])));
            }

            assertEquals(
                Arrays.asList(new Integer[0]),
                Arrays.asList((Object[]) invoke(Functions(), "fArrays1", connection, new Integer[0])));
            assertEquals(
                Arrays.asList(new Long[0]),
                Arrays.asList((Object[]) invoke(Functions(), "fArrays2", connection, new Long[0])));
            assertEquals(
                Arrays.asList(new String[0]),
                Arrays.asList((Object[]) invoke(Functions(), "fArrays3", connection, new String[0])));

            if (supportsOUTParameters()) {
                assertEquals(
                    Arrays.asList(1),
                    Arrays.asList((Integer[]) invoke(Procedures(), "pArrays1", connection, new Integer[] {1})));
                assertEquals(
                    Arrays.asList(1L),
                    Arrays.asList((Long[]) invoke(Procedures(), "pArrays2", connection, new Long[] {1L})));
                assertEquals(
                    Arrays.asList("1"),
                    Arrays.asList((String[]) invoke(Procedures(), "pArrays3", connection, new String[] {"1"})));
            }

            assertEquals(
                Arrays.asList(1),
                Arrays.asList((Object[]) invoke(Functions(), "fArrays1", connection, new Integer[] {1})));
            assertEquals(
                Arrays.asList(1L),
                Arrays.asList((Object[]) invoke(Functions(), "fArrays2", connection, new Long[] {1L})));
            assertEquals(
                Arrays.asList("1"),
                Arrays.asList((Object[]) invoke(Functions(), "fArrays3", connection, new String[] {"1"})));

            if (supportsOUTParameters()) {
                assertEquals(
                    Arrays.asList(1, 2),
                    Arrays.asList((Integer[]) invoke(Procedures(), "pArrays1", connection, new Integer[] {1, 2})));
                assertEquals(
                    Arrays.asList(1L, 2L),
                    Arrays.asList((Long[]) invoke(Procedures(), "pArrays2", connection, new Long[] {1L, 2L})));
                assertEquals(
                    Arrays.asList("1", "2"),
                    Arrays.asList((String[]) invoke(Procedures(), "pArrays3", connection, new String[] {"1", "2"})));
            }

            assertEquals(
                Arrays.asList(1, 2),
                Arrays.asList((Object[]) invoke(Functions(), "fArrays1", connection, new Integer[] {1, 2})));
            assertEquals(
                Arrays.asList(1L, 2L),
                Arrays.asList((Object[]) invoke(Functions(), "fArrays2", connection, new Long[] {1L, 2L})));
            assertEquals(
                Arrays.asList("1", "2"),
                Arrays.asList((Object[]) invoke(Functions(), "fArrays3", connection, new String[] {"1", "2"})));
        }
    }

    private ArrayRecord<Integer> newNUMBER_R() throws Exception {
        ArrayRecord<Integer> result = TArrays_NUMBER_R().getType().getConstructor(Configuration.class).newInstance(create());
        return result;
    }

    private ArrayRecord<Long> newNUMBER_LONG_R() throws Exception {
        ArrayRecord<Long> result = TArrays_NUMBER_LONG_R().getType().getConstructor(Configuration.class).newInstance(create());
        return result;
    }

    private ArrayRecord<String> newSTRING_R() throws Exception {
        ArrayRecord<String> result = TArrays_STRING_R().getType().getConstructor(Configuration.class).newInstance(create());
        return result;
    }

    @Test
    public final void testUDTs() throws Exception {
        if (TAuthor_ADDRESS() == null) {
            log.info("SKIPPING", "UDT test");
            return;
        }

        Result<A> authors = create().selectFrom(TAuthor()).fetch();
        UDTRecord<?> a1 = authors.getRecord(0).getValue(TAuthor_ADDRESS());
        UDTRecord<?> a2 = authors.getRecord(1).getValue(TAuthor_ADDRESS());

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
    public final void testUDTProcedure() throws Exception {
        if (UAddressType() == null) {
            log.info("SKIPPING", "UDT procedure test (no UDT support)");
            return;
        }

        if (Procedures() == null) {
            log.info("SKIPPING", "UDT procedure test (no procedure support)");
            return;
        }

        UDTRecord<?> address = UAddressType().newInstance();
        UDTRecord<?> street = UStreetType().newInstance();
        invoke(street, "setNo", "35");
        invoke(address, "setStreet", street);

        // First procedure
        Object result = invoke(Procedures(), "pEnhanceAddress1", connection, address);
        assertEquals("35", result);

        // Second procedure
        address = (UDTRecord<?>) invoke(Procedures(), "pEnhanceAddress2", connection);
        street = (UDTRecord<?>) invoke(address, "getStreet");
        assertEquals("Parliament Hill", invoke(street, "getStreet"));
        assertEquals("77", invoke(street, "getNo"));

        // Third procedure
        address = (UDTRecord<?>) invoke(Procedures(), "pEnhanceAddress3", connection, address);
        street = (UDTRecord<?>) invoke(address, "getStreet");
        assertEquals("Zwinglistrasse", invoke(street, "getStreet"));
        assertEquals("17", invoke(street, "getNo"));
    }

    @Test
    public final void testNULL() throws Exception {
        Field<Integer> n = create().castNull(Integer.class);
        Field<Integer> c = constant(1);

        assertEquals(null, create().select(n).fetchOne(n));
        assertEquals(Integer.valueOf(1), create().select(c).where(n.isNull()).fetchOne(c));
        assertEquals(Integer.valueOf(1), create().select(c).where(n.equal(n)).fetchOne(c));
        assertEquals(null, create().select(1).where(n.isNotNull()).fetchAny());
        assertEquals(null, create().select(1).where(n.notEqual(n)).fetchAny());

        UpdateQuery<A> u = create().updateQuery(TAuthor());
        u.addValue(TAuthor_YEAR_OF_BIRTH(), (Integer) null);
        u.execute();

        Result<A> records = create()
            .selectFrom(TAuthor())
            .where(TAuthor_YEAR_OF_BIRTH().isNull())
            .fetch();
        assertEquals(2, records.getNumberOfRecords());
        assertEquals(null, records.getValue(0, TAuthor_YEAR_OF_BIRTH()));
    }

    @Test
    public final void testPackage() throws Exception {
        if (Library() == null) {
            log.info("SKIPPING", "packages test");
            return;
        }

        assertEquals("1", "" + invoke(Library(), "pkgPAuthorExists1", connection, "Paulo"));
        assertEquals("0", "" + invoke(Library(), "pkgPAuthorExists1", connection, "Shakespeare"));
        assertEquals("1", "" + invoke(Library(), "pkgFAuthorExists1", connection, "Paulo"));
        assertEquals("0", "" + invoke(Library(), "pkgFAuthorExists1", connection, "Shakespeare"));
    }

    @Test
    public final void testProcedure() throws Exception {
        if (Procedures() == null) {
            log.info("SKIPPING", "procedure test");
            return;
        }

        if (supportsOUTParameters()) {
            assertEquals("0", "" + invoke(Procedures(), "pAuthorExists", connection, null));
            assertEquals("1", "" + invoke(Procedures(), "pAuthorExists", connection, "Paulo"));
            assertEquals("0", "" + invoke(Procedures(), "pAuthorExists", connection, "Shakespeare"));
        } else {
            log.info("SKIPPING", "procedure test for OUT parameters");
        }

        assertEquals(null, create().fetchOne(
            TAuthor(),
            TAuthor_FIRST_NAME().equal("William")));
        invoke(Procedures(), "pCreateAuthor", connection);
        assertEquals("Shakespeare", create().fetchOne(
            TAuthor(),
            TAuthor_FIRST_NAME().equal("William")).getValue(TAuthor_LAST_NAME()));

        assertEquals(null, create().fetchOne(
            TAuthor(),
            TAuthor_FIRST_NAME().equal("Hermann")));
        invoke(Procedures(), "pCreateAuthorByName", connection, "Hermann", "Hesse");
        assertEquals("Hesse", create().fetchOne(
            TAuthor(),
            TAuthor_FIRST_NAME().equal("Hermann")).getValue(TAuthor_LAST_NAME()));

        assertEquals(null, create().fetchOne(
            TAuthor(),
            TAuthor_LAST_NAME().equal("Kaestner")));
        invoke(Procedures(), "pCreateAuthorByName", connection, null, "Kaestner");
        assertEquals("Kaestner", create().fetchOne(
            TAuthor(),
            TAuthor_LAST_NAME().equal("Kaestner")).getValue(TAuthor_LAST_NAME()));
    }

    /**
     * Reflection helper
     */
    protected Object invoke(Class<?> clazz, String methodName, Object... parameters) throws Exception {
        return invoke0(clazz, clazz, methodName, parameters);
    }

    /**
     * Reflection helper
     */
    protected Object invoke(Object object, String methodName, Object... parameters) throws Exception {
        return invoke0(object.getClass(), object, methodName, parameters);
    }

    /**
     * Reflection helper
     */
    private Object invoke0(Class<?> clazz, Object object, String methodName, Object... parameters) throws Exception {
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                try {
                    return method.invoke(object, parameters);
                }
                catch (IllegalArgumentException ignore) {
                }
            }
        }

        throw new NoSuchMethodException();
    }
}
