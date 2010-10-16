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
import static junit.framework.Assert.assertTrue;
import static org.jooq.JoinType.LEFT_OUTER_JOIN;
import static org.jooq.impl.FalseCondition.FALSE_CONDITION;
import static org.jooq.impl.TrueCondition.TRUE_CONDITION;
import static org.jooq.test.Data.FIELD_DATE1;
import static org.jooq.test.Data.FIELD_ID1;
import static org.jooq.test.Data.FIELD_ID2;
import static org.jooq.test.Data.FIELD_ID3;
import static org.jooq.test.Data.FIELD_NAME1;
import static org.jooq.test.Data.TABLE1;
import static org.jooq.test.Data.TABLE2;
import static org.jooq.test.Data.TABLE3;

import java.sql.Date;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jooq.BetweenCondition;
import org.jooq.CombinedCondition;
import org.jooq.Condition;
import org.jooq.DeleteQuery;
import org.jooq.Field;
import org.jooq.InCondition;
import org.jooq.InsertQuery;
import org.jooq.Join;
import org.jooq.Select;
import org.jooq.SelectQuery;
import org.jooq.SimpleSelectQuery;
import org.jooq.SortOrder;
import org.jooq.Table;
import org.jooq.UpdateQuery;
import org.jooq.impl.Factory;
import org.jooq.test.Data.Table1Record;
import org.jooq.test.Data.Table2Record;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * A test suite for basic jOOQ functionality
 *
 * @author Lukas Eder
 */
public class jOOQTest {

    private Mockery context;
    private PreparedStatement statement;
    private Factory create;

    @Before
    public void setUp() throws Exception {
        context = new Mockery();
        statement = context.mock(PreparedStatement.class);
        create = new Factory((DataSource) null);
    }

    @After
    public void tearDown() throws Exception {
        statement = null;
        context = null;
    }

    @Test
    public final void testAliasing() throws Exception {
        assertEquals("TABLE1", TABLE1.toSQLDeclaration());
        assertEquals("TABLE1", TABLE1.toSQLReference());

        assertEquals("TABLE1 t1", TABLE1.as("t1").toSQLDeclaration());
        assertEquals("t1",        TABLE1.as("t1").toSQLReference());

        assertEquals("TABLE1.ID1", TABLE1.getField(FIELD_ID1).toSQLDeclaration());
        assertEquals("TABLE1.ID1", TABLE1.getField(FIELD_ID1).toSQLReference());

        assertEquals("TABLE1.ID1 f1", TABLE1.getField(FIELD_ID1).as("f1").toSQLDeclaration());
        assertEquals("f1",            TABLE1.getField(FIELD_ID1).as("f1").toSQLReference());

        assertEquals("t1.ID1", TABLE1.as("t1").getField(FIELD_ID1).toSQLDeclaration());
        assertEquals("t1.ID1", TABLE1.as("t1").getField(FIELD_ID1).toSQLReference());

        assertEquals("t1.ID1 f1", TABLE1.as("t1").getField(FIELD_ID1).as("f1").toSQLDeclaration());
        assertEquals("f1",        TABLE1.as("t1").getField(FIELD_ID1).as("f1").toSQLReference());
    }

    @Test
    public final void testEmptyCombinedCondition() throws Exception {
        CombinedCondition c = create.combinedCondition();
        assertEquals("1 = 1", c.toSQLReference());

        int i = c.bind(statement);
        assertEquals(1, i);
    }

    @Test
    public final void testSingleCombinedCondition() throws Exception {
        CombinedCondition c = create.combinedCondition(TRUE_CONDITION);
        assertEquals(TRUE_CONDITION.toSQLReference(true), c.toSQLReference(true));
        assertEquals(TRUE_CONDITION.toSQLReference(false), c.toSQLReference(false));

        int i = c.bind(statement);
        assertEquals(1, i);
    }

    @Test
    public final void testMultipleCombinedCondition() throws Exception {
        Condition c1 = FIELD_ID1.equal(10);
        Condition c2 = FIELD_ID2.equal(20);
        Condition c3 = FIELD_ID1.equal(30);
        Condition c4 = FIELD_ID2.equal(40);

        CombinedCondition c = c1.and(c2).or(c3.and(c4));
        assertEquals("((TABLE1.ID1 = 10 and TABLE2.ID2 = 20) or (TABLE1.ID1 = 30 and TABLE2.ID2 = 40))", c.toSQLReference(true));
        assertEquals("((TABLE1.ID1 = ? and TABLE2.ID2 = ?) or (TABLE1.ID1 = ? and TABLE2.ID2 = ?))", c.toSQLReference(false));

        c = c1.and(c2).or(c3).and(c4);
        assertEquals("(((TABLE1.ID1 = 10 and TABLE2.ID2 = 20) or TABLE1.ID1 = 30) and TABLE2.ID2 = 40)", c.toSQLReference(true));
        assertEquals("(((TABLE1.ID1 = ? and TABLE2.ID2 = ?) or TABLE1.ID1 = ?) and TABLE2.ID2 = ?)", c.toSQLReference(false));

        c = c1.and(c2).and(c3).or(c4);
        assertEquals("((TABLE1.ID1 = 10 and TABLE2.ID2 = 20 and TABLE1.ID1 = 30) or TABLE2.ID2 = 40)", c.toSQLReference(true));
        assertEquals("((TABLE1.ID1 = ? and TABLE2.ID2 = ? and TABLE1.ID1 = ?) or TABLE2.ID2 = ?)", c.toSQLReference(false));

        c = c1.and(c2).and(c3).and(c4);
        assertEquals("(TABLE1.ID1 = 10 and TABLE2.ID2 = 20 and TABLE1.ID1 = 30 and TABLE2.ID2 = 40)", c.toSQLReference(true));
        assertEquals("(TABLE1.ID1 = ? and TABLE2.ID2 = ? and TABLE1.ID1 = ? and TABLE2.ID2 = ?)", c.toSQLReference(false));

        context.checking(new Expectations() {{
            oneOf(statement).setInt(1, 10);
            oneOf(statement).setInt(2, 20);
            oneOf(statement).setInt(3, 30);
            oneOf(statement).setInt(4, 40);
        }});

        int i = c.bind(statement);
        assertEquals(5, i);

        context.assertIsSatisfied();
    }

    @Test
    public final void testBetweenCondition() throws Exception {
        BetweenCondition<Integer> c = FIELD_ID1.between(1, 10);
        assertEquals("TABLE1.ID1 between 1 and 10", c.toSQLReference(true));
        assertEquals("TABLE1.ID1 between ? and ?", c.toSQLReference(false));

        context.checking(new Expectations() {{
            oneOf(statement).setInt(1, 1);
            oneOf(statement).setInt(2, 10);
        }});

        int i = c.bind(statement);
        assertEquals(3, i);

        context.assertIsSatisfied();
    }

    @Test
    public final void testInCondition() throws Exception {
        InCondition<Integer> c = FIELD_ID1.in(1, 10);
        assertEquals("TABLE1.ID1 in (1, 10)", c.toSQLReference(true));
        assertEquals("TABLE1.ID1 in (?, ?)", c.toSQLReference(false));

        context.checking(new Expectations() {{
            oneOf(statement).setInt(1, 1);
            oneOf(statement).setInt(2, 10);
        }});

        int i = c.bind(statement);
        assertEquals(3, i);

        context.assertIsSatisfied();
    }

    @Test
    public final void testCompareCondition() throws Exception {
        Condition c = FIELD_ID1.equal(10);
        assertEquals("TABLE1.ID1 = 10", c.toSQLReference(true));
        assertEquals("TABLE1.ID1 = ?", c.toSQLReference(false));

        context.checking(new Expectations() {{
            oneOf(statement).setInt(1, 10);
        }});

        int i = c.bind(statement);
        assertEquals(2, i);

        context.assertIsSatisfied();
    }

    @Test
    public final void testPlainSQLCondition() throws Exception {
        Condition c1 = create.plainSQLCondition("TABLE1.ID = 10");
        Condition c2 = create.plainSQLCondition("TABLE1.ID = ? and TABLE2.ID = ?", 10, "20");

        try {
            create.plainSQLCondition("ABC", "ABC");
            Assert.fail("Binding mismatch");
        } catch (Exception expected) {}

        assertEquals("(TABLE1.ID = 10)", c1.toSQLReference(true));
        assertEquals("(TABLE1.ID = 10)", c1.toSQLReference(false));
        assertEquals("(TABLE1.ID = 10 and TABLE2.ID = '20')", c2.toSQLReference(true));
        assertEquals("(TABLE1.ID = ? and TABLE2.ID = ?)", c2.toSQLReference(false));

        context.checking(new Expectations() {{
            oneOf(statement).setInt(1, 10);
            oneOf(statement).setString(2, "20");
        }});

        int i = c2.bind(statement);
        assertEquals(3, i);

        context.assertIsSatisfied();
    }

    @Test
    public final void testPlainSQLField() throws Exception {
        Field<?> f1 = create.plainSQLField("DECODE(TABLE1.ID, 1, 'a', 'b')");
        Field<?> f2 = create.plainSQLField("DECODE(TABLE1.ID, 1, ?, ?)", "a", "b");

        try {
            create.plainSQLField("ABC", "ABC");
            Assert.fail("Binding mismatch");
        } catch (Exception expected) {}

        assertEquals("DECODE(TABLE1.ID, 1, 'a', 'b')", f1.toSQLReference(true));
        assertEquals("DECODE(TABLE1.ID, 1, 'a', 'b')", f1.toSQLReference(false));
        assertEquals("DECODE(TABLE1.ID, 1, 'a', 'b')", f2.toSQLReference(true));
        assertEquals("DECODE(TABLE1.ID, 1, ?, ?)", f2.toSQLReference(false));

        context.checking(new Expectations() {{
            oneOf(statement).setString(1, "a");
            oneOf(statement).setString(2, "b");
        }});

        int i = f2.bind(statement);
        assertEquals(3, i);

        context.assertIsSatisfied();
    }

    @Test
    public final void testIsNullCondition() throws Exception {
        Condition c1 = FIELD_ID1.isNull();
        assertEquals("TABLE1.ID1 is null", c1.toSQLReference(true));
        assertEquals("TABLE1.ID1 is null", c1.toSQLReference(false));

        Condition c2 = FIELD_ID1.isNotNull();
        assertEquals("TABLE1.ID1 is not null", c2.toSQLReference(true));
        assertEquals("TABLE1.ID1 is not null", c2.toSQLReference(false));

        int i = c1.bind(statement);
        assertEquals(1, i);

        int j = c2.bind(statement);
        assertEquals(1, j);
    }

    @Test
    public final void testNullFunction() throws Exception {
        Field<?> f = create.functions().NULL();
        assertEquals("null", f.toSQLReference(true));
        assertEquals("null", f.toSQLReference(false));

        int i = f.bind(statement);
        assertEquals(1, i);
    }

    @Test
    public final void testConstantFunction() throws Exception {
        Field<Integer> f1 = create.functions().constant(Integer.valueOf(1));
        assertEquals(Integer.class, f1.getType());
        assertEquals("'1'", f1.toSQLReference(true));
        assertEquals("'1'", f1.toSQLReference(false));
        assertEquals("'1'", f1.toSQLDeclaration(true));
        assertEquals("'1'", f1.toSQLDeclaration(false));

        Field<String> f2 = create.functions().constant("test");
        assertEquals(String.class, f2.getType());
        assertEquals("'test'", f2.toSQLReference(true));
        assertEquals("'test'", f2.toSQLReference(false));
        assertEquals("'test'", f2.toSQLDeclaration(true));
        assertEquals("'test'", f2.toSQLDeclaration(false));

        Field<Integer> f3 = create.functions().constant(Integer.valueOf(1)).as("value");
        assertEquals(Integer.class, f3.getType());
        assertEquals("value", f3.toSQLReference(true));
        assertEquals("value", f3.toSQLReference(false));
        assertEquals("'1' value", f3.toSQLDeclaration(true));
        assertEquals("'1' value", f3.toSQLDeclaration(false));

        int i = f1.bind(statement);
        assertEquals(1, i);

        int j = f2.bind(statement);
        assertEquals(1, j);

        int k = f3.bind(statement);
        assertEquals(1, k);
    }

    @Test
    public final void testArithmeticFunctions() throws Exception {
        Field<Integer> sum1 = create.functions().sum(FIELD_ID1);
        assertEquals(Integer.class, sum1.getType());
        assertEquals("sum(TABLE1.ID1)", sum1.toSQLReference(true));
        assertEquals("sum(TABLE1.ID1)", sum1.toSQLReference(false));
        assertEquals("sum(TABLE1.ID1)", sum1.toSQLDeclaration(true));
        assertEquals("sum(TABLE1.ID1)", sum1.toSQLDeclaration(false));
        assertEquals(1, sum1.bind(statement));

        Field<Integer> sum2 = create.functions().sum(FIELD_ID1).as("value");
        assertEquals(Integer.class, sum2.getType());
        assertEquals("value", sum2.toSQLReference(true));
        assertEquals("value", sum2.toSQLReference(false));
        assertEquals("sum(TABLE1.ID1) value", sum2.toSQLDeclaration(true));
        assertEquals("sum(TABLE1.ID1) value", sum2.toSQLDeclaration(false));
        assertEquals(1, sum2.bind(statement));

        Field<Double> avg1 = create.functions().avg(FIELD_ID1);
        assertEquals(Double.class, avg1.getType());
        assertEquals("avg(TABLE1.ID1)", avg1.toSQLReference(true));
        assertEquals("avg(TABLE1.ID1)", avg1.toSQLReference(false));
        assertEquals("avg(TABLE1.ID1)", avg1.toSQLDeclaration(true));
        assertEquals("avg(TABLE1.ID1)", avg1.toSQLDeclaration(false));
        assertEquals(1, avg1.bind(statement));

        Field<Double> avg2 = create.functions().avg(FIELD_ID1).as("value");
        assertEquals(Double.class, avg2.getType());
        assertEquals("value", avg2.toSQLReference(true));
        assertEquals("value", avg2.toSQLReference(false));
        assertEquals("avg(TABLE1.ID1) value", avg2.toSQLDeclaration(true));
        assertEquals("avg(TABLE1.ID1) value", avg2.toSQLDeclaration(false));
        assertEquals(1, avg2.bind(statement));

        Field<Integer> min1 = create.functions().min(FIELD_ID1);
        assertEquals(Integer.class, min1.getType());
        assertEquals("min(TABLE1.ID1)", min1.toSQLReference(true));
        assertEquals("min(TABLE1.ID1)", min1.toSQLReference(false));
        assertEquals("min(TABLE1.ID1)", min1.toSQLDeclaration(true));
        assertEquals("min(TABLE1.ID1)", min1.toSQLDeclaration(false));
        assertEquals(1, min1.bind(statement));

        Field<Integer> min2 = create.functions().min(FIELD_ID1).as("value");
        assertEquals(Integer.class, min2.getType());
        assertEquals("value", min2.toSQLReference(true));
        assertEquals("value", min2.toSQLReference(false));
        assertEquals("min(TABLE1.ID1) value", min2.toSQLDeclaration(true));
        assertEquals("min(TABLE1.ID1) value", min2.toSQLDeclaration(false));
        assertEquals(1, min2.bind(statement));

        Field<Integer> max1 = create.functions().max(FIELD_ID1);
        assertEquals(Integer.class, max1.getType());
        assertEquals("max(TABLE1.ID1)", max1.toSQLReference(true));
        assertEquals("max(TABLE1.ID1)", max1.toSQLReference(false));
        assertEquals("max(TABLE1.ID1)", max1.toSQLDeclaration(true));
        assertEquals("max(TABLE1.ID1)", max1.toSQLDeclaration(false));
        assertEquals(1, max1.bind(statement));

        Field<Integer> max2 = create.functions().max(FIELD_ID1).as("value");
        assertEquals(Integer.class, max2.getType());
        assertEquals("value", max2.toSQLReference(true));
        assertEquals("value", max2.toSQLReference(false));
        assertEquals("max(TABLE1.ID1) value", max2.toSQLDeclaration(true));
        assertEquals("max(TABLE1.ID1) value", max2.toSQLDeclaration(false));
        assertEquals(1, max2.bind(statement));

        Field<Integer> count1 = create.functions().count();
        assertEquals(Integer.class, count1.getType());
        assertEquals("count(*)", count1.toSQLReference(true));
        assertEquals("count(*)", count1.toSQLReference(false));
        assertEquals("count(*)", count1.toSQLDeclaration(true));
        assertEquals("count(*)", count1.toSQLDeclaration(false));
        assertEquals(1, count1.bind(statement));

        Field<Integer> count1a = create.functions().count().as("cnt");
        assertEquals(Integer.class, count1a.getType());
        assertEquals("cnt", count1a.toSQLReference(true));
        assertEquals("cnt", count1a.toSQLReference(false));
        assertEquals("count(*) cnt", count1a.toSQLDeclaration(true));
        assertEquals("count(*) cnt", count1a.toSQLDeclaration(false));
        assertEquals(1, count1a.bind(statement));

        Field<Integer> count2 = create.functions().count(FIELD_ID1);
        assertEquals(Integer.class, count2.getType());
        assertEquals("count(TABLE1.ID1)", count2.toSQLReference(true));
        assertEquals("count(TABLE1.ID1)", count2.toSQLReference(false));
        assertEquals("count(TABLE1.ID1)", count2.toSQLDeclaration(true));
        assertEquals("count(TABLE1.ID1)", count2.toSQLDeclaration(false));
        assertEquals(1, count2.bind(statement));

        Field<Integer> count2a = create.functions().count(FIELD_ID1).as("cnt");
        assertEquals(Integer.class, count2a.getType());
        assertEquals("cnt", count2a.toSQLReference(true));
        assertEquals("cnt", count2a.toSQLReference(false));
        assertEquals("count(TABLE1.ID1) cnt", count2a.toSQLDeclaration(true));
        assertEquals("count(TABLE1.ID1) cnt", count2a.toSQLDeclaration(false));
        assertEquals(1, count2a.bind(statement));

        Field<Integer> count3 = create.functions().countDistinct(FIELD_ID1);
        assertEquals(Integer.class, count3.getType());
        assertEquals("count(distinct TABLE1.ID1)", count3.toSQLReference(true));
        assertEquals("count(distinct TABLE1.ID1)", count3.toSQLReference(false));
        assertEquals("count(distinct TABLE1.ID1)", count3.toSQLDeclaration(true));
        assertEquals("count(distinct TABLE1.ID1)", count3.toSQLDeclaration(false));
        assertEquals(1, count3.bind(statement));

        Field<Integer> count3a = create.functions().countDistinct(FIELD_ID1).as("cnt");
        assertEquals(Integer.class, count3a.getType());
        assertEquals("cnt", count3a.toSQLReference(true));
        assertEquals("cnt", count3a.toSQLReference(false));
        assertEquals("count(distinct TABLE1.ID1) cnt", count3a.toSQLDeclaration(true));
        assertEquals("count(distinct TABLE1.ID1) cnt", count3a.toSQLDeclaration(false));
        assertEquals(1, count3a.bind(statement));
    }

    @Test(expected = IllegalStateException.class)
    public final void testEmptyInsertQuery() throws Exception {
        InsertQuery<Table1Record> q = create.insertQuery(TABLE1);
        q.toSQLReference();
    }

    @Test
    public final void testInsertQuery1() throws Exception {
        InsertQuery<Table1Record> q = create.insertQuery(TABLE1);

        q.addValue(FIELD_ID1, 10);
        assertEquals("insert into TABLE1 (ID1) values (10)", q.toSQLReference(true));
        assertEquals("insert into TABLE1 (ID1) values (?)", q.toSQLReference(false));

        context.checking(new Expectations() {{
            oneOf(statement).setInt(1, 10);
        }});

        int i = q.bind(statement);
        assertEquals(2, i);

        context.assertIsSatisfied();
    }

    @Test
    public final void testInsertQuery2() throws Exception {
        InsertQuery<Table1Record> q = create.insertQuery(TABLE1);

        q.addValue(FIELD_ID1, 10);
        q.addValue(FIELD_NAME1, "ABC");
        q.addValue(FIELD_DATE1, new Date(0));
        assertEquals("insert into TABLE1 (ID1, NAME1, DATE1) values (10, 'ABC', '1970-01-01')", q.toSQLReference(true));
        assertEquals("insert into TABLE1 (ID1, NAME1, DATE1) values (?, ?, ?)", q.toSQLReference(false));

        context.checking(new Expectations() {{
            oneOf(statement).setInt(1, 10);
            oneOf(statement).setString(2, "ABC");
            oneOf(statement).setDate(3, new Date(0));
        }});

        int i = q.bind(statement);
        assertEquals(4, i);

        context.assertIsSatisfied();
    }

    @Test(expected = IllegalStateException.class)
    public final void testEmptyUpdateQuery() throws Exception {
        UpdateQuery<Table1Record> q = create.updateQuery(TABLE1);
        q.toSQLReference();
    }

    @Test
    public final void testUpdateQuery1() throws Exception {
        UpdateQuery<Table1Record> q = create.updateQuery(TABLE1);

        q.addValue(FIELD_ID1, 10);
        assertEquals("update TABLE1 set ID1 = 10", q.toSQLReference(true));
        assertEquals("update TABLE1 set ID1 = ?", q.toSQLReference(false));

        context.checking(new Expectations() {{
            oneOf(statement).setInt(1, 10);
        }});

        int i = q.bind(statement);
        assertEquals(2, i);

        context.assertIsSatisfied();
    }

    @Test
    public final void testUpdateQuery2() throws Exception {
        UpdateQuery<Table1Record> q = create.updateQuery(TABLE1);

        q.addValue(FIELD_ID1, 10);
        q.addValue(FIELD_NAME1, "ABC");
        assertEquals("update TABLE1 set ID1 = 10, NAME1 = 'ABC'", q.toSQLReference(true));
        assertEquals("update TABLE1 set ID1 = ?, NAME1 = ?", q.toSQLReference(false));

        context.checking(new Expectations() {{
            oneOf(statement).setInt(1, 10);
            oneOf(statement).setString(2, "ABC");
        }});

        int i = q.bind(statement);
        assertEquals(3, i);

        context.assertIsSatisfied();
    }

    @Test
    public final void testUpdateQuery3() throws Exception {
        UpdateQuery<Table1Record> q = create.updateQuery(TABLE1);
        Condition c = FIELD_ID1.equal(10);

        q.addValue(FIELD_ID1, 10);
        q.addValue(FIELD_NAME1, "ABC");
        q.addConditions(c);
        assertEquals("update TABLE1 set ID1 = 10, NAME1 = 'ABC' where TABLE1.ID1 = 10", q.toSQLReference(true));
        assertEquals("update TABLE1 set ID1 = ?, NAME1 = ? where TABLE1.ID1 = ?", q.toSQLReference(false));

        context.checking(new Expectations() {{
            oneOf(statement).setInt(1, 10);
            oneOf(statement).setString(2, "ABC");
            oneOf(statement).setInt(3, 10);
        }});

        int i = q.bind(statement);
        assertEquals(4, i);

        context.assertIsSatisfied();
    }

    @Test
    public final void testUpdateQuery4() throws Exception {
        UpdateQuery<Table1Record> q = create.updateQuery(TABLE1);
        Condition c1 = FIELD_ID1.equal(10);
        Condition c2 = FIELD_ID1.equal(20);

        q.addValue(FIELD_ID1, 10);
        q.addValue(FIELD_NAME1, "ABC");
        q.addConditions(c1);
        q.addConditions(c2);
        assertEquals("update TABLE1 set ID1 = 10, NAME1 = 'ABC' where (TABLE1.ID1 = 10 and TABLE1.ID1 = 20)", q.toSQLReference(true));
        assertEquals("update TABLE1 set ID1 = ?, NAME1 = ? where (TABLE1.ID1 = ? and TABLE1.ID1 = ?)", q.toSQLReference(false));

        context.checking(new Expectations() {{
            oneOf(statement).setInt(1, 10);
            oneOf(statement).setString(2, "ABC");
            oneOf(statement).setInt(3, 10);
            oneOf(statement).setInt(4, 20);
        }});

        int i = q.bind(statement);
        assertEquals(5, i);

        context.assertIsSatisfied();
    }

    @Test
    public final void testUpdateQuery5() throws Exception {
        UpdateQuery<Table1Record> q = create.updateQuery(TABLE1);
        Condition c1 = FIELD_ID1.equal(10);
        Condition c2 = FIELD_ID1.equal(20);

        q.addValue(FIELD_ID1, 10);
        q.addValue(FIELD_NAME1, "ABC");
        q.addConditions(c1);
        q.addConditions(c2);
        q.addConditions(c2, c1);
        assertEquals("update TABLE1 set ID1 = 10, NAME1 = 'ABC' where (TABLE1.ID1 = 10 and TABLE1.ID1 = 20 and TABLE1.ID1 = 20 and TABLE1.ID1 = 10)", q.toSQLReference(true));
        assertEquals("update TABLE1 set ID1 = ?, NAME1 = ? where (TABLE1.ID1 = ? and TABLE1.ID1 = ? and TABLE1.ID1 = ? and TABLE1.ID1 = ?)", q.toSQLReference(false));

        context.checking(new Expectations() {{
            oneOf(statement).setInt(1, 10);
            oneOf(statement).setString(2, "ABC");
            oneOf(statement).setInt(3, 10);
            oneOf(statement).setInt(4, 20);
            oneOf(statement).setInt(5, 20);
            oneOf(statement).setInt(6, 10);
        }});

        int i = q.bind(statement);
        assertEquals(7, i);

        context.assertIsSatisfied();
    }

    @Test
    public final void testDeleteQuery1() throws Exception {
        DeleteQuery<Table1Record> q = create.deleteQuery(TABLE1);

        assertEquals("delete from TABLE1", q.toSQLReference(true));
        assertEquals("delete from TABLE1", q.toSQLReference(false));
    }

    @Test
    public final void testDeleteQuery2() throws Exception {
        DeleteQuery<Table1Record> q = create.deleteQuery(TABLE1);

        q.addConditions(FALSE_CONDITION);
        assertEquals("delete from TABLE1 where 1 = 0", q.toSQLReference(true));
        assertEquals("delete from TABLE1 where 1 = 0", q.toSQLReference(false));
    }

    @Test
    public final void testDeleteQuery3() throws Exception {
        DeleteQuery<Table1Record> q = create.deleteQuery(TABLE1);
        Condition c1 = FIELD_ID1.equal(10);
        Condition c2 = FIELD_ID1.equal(20);

        q.addConditions(c1);
        q.addConditions(c2);
        assertEquals("delete from TABLE1 where (TABLE1.ID1 = 10 and TABLE1.ID1 = 20)", q.toSQLReference(true));
        assertEquals("delete from TABLE1 where (TABLE1.ID1 = ? and TABLE1.ID1 = ?)", q.toSQLReference(false));

        context.checking(new Expectations() {{
            oneOf(statement).setInt(1, 10);
            oneOf(statement).setInt(2, 20);
        }});

        int i = q.bind(statement);
        assertEquals(3, i);

        context.assertIsSatisfied();
    }

    @Test
    public final void testDeleteQuery4() throws Exception {
        DeleteQuery<Table1Record> q = create.deleteQuery(TABLE1);
        Condition c1 = FIELD_ID1.equal(10);
        Condition c2 = FIELD_ID1.equal(20);

        q.addConditions(c1);
        q.addConditions(c2);
        q.addConditions(c2, c1);
        assertEquals("delete from TABLE1 where (TABLE1.ID1 = 10 and TABLE1.ID1 = 20 and TABLE1.ID1 = 20 and TABLE1.ID1 = 10)", q.toSQLReference(true));
        assertEquals("delete from TABLE1 where (TABLE1.ID1 = ? and TABLE1.ID1 = ? and TABLE1.ID1 = ? and TABLE1.ID1 = ?)", q.toSQLReference(false));

        context.checking(new Expectations() {{
            oneOf(statement).setInt(1, 10);
            oneOf(statement).setInt(2, 20);
            oneOf(statement).setInt(3, 20);
            oneOf(statement).setInt(4, 10);
        }});

        int i = q.bind(statement);
        assertEquals(5, i);

        context.assertIsSatisfied();
    }

    @Test
    public final void testConditionalSelectQuery1() throws Exception {
        SelectQuery q = create.selectQuery();
        Select s = create.select();

        assertEquals("select * from dual", q.toSQLReference(true));
        assertEquals("select * from dual", q.toSQLReference(false));
        assertEquals(q, s.getQuery());
    }

    @Test
    public final void testConditionalSelectQuery2() throws Exception {
        SelectQuery q = create.selectQuery();

        q.addConditions(FALSE_CONDITION);
        assertEquals("select * from dual where 1 = 0", q.toSQLReference(true));
        assertEquals("select * from dual where 1 = 0", q.toSQLReference(false));
        assertEquals(q, create.select().where(FALSE_CONDITION).getQuery());
    }

    @Test
    public final void testConditionalSelectQuery3() throws Exception {
        SelectQuery q = create.selectQuery();

        q.addConditions(FALSE_CONDITION);
        q.addConditions(TRUE_CONDITION);
        assertEquals("select * from dual where (1 = 0 and 1 = 1)", q.toSQLReference(true));
        assertEquals("select * from dual where (1 = 0 and 1 = 1)", q.toSQLReference(false));
        assertEquals(q, create.select().where(FALSE_CONDITION.and(TRUE_CONDITION)).getQuery());
    }

    @Test
    public final void testConditionalSelectQuery4() throws Exception {
        SelectQuery q = create.selectQuery();
        Condition c1 = FIELD_ID1.equal(10);
        Condition c2 = FIELD_ID1.equal(20);

        q.addConditions(c1);
        q.addConditions(c2);
        q.addConditions(c2, c1);
        assertEquals("select * from dual where (TABLE1.ID1 = 10 and TABLE1.ID1 = 20 and TABLE1.ID1 = 20 and TABLE1.ID1 = 10)", q.toSQLReference(true));
        assertEquals("select * from dual where (TABLE1.ID1 = ? and TABLE1.ID1 = ? and TABLE1.ID1 = ? and TABLE1.ID1 = ?)", q.toSQLReference(false));
        assertEquals(q, create.select().where(c1.and(c2).and(c2.and(c1))).getQuery());

        context.checking(new Expectations() {{
            oneOf(statement).setInt(1, 10);
            oneOf(statement).setInt(2, 20);
            oneOf(statement).setInt(3, 20);
            oneOf(statement).setInt(4, 10);
        }});

        int i = q.bind(statement);
        assertEquals(5, i);

        context.assertIsSatisfied();
    }

    @Test
    public final void testConditionalSelectQuery5() throws Exception {
        SelectQuery q = create.selectQuery();
        Condition c1 = create.plainSQLCondition("TABLE1.ID1 = ?", "10");
        Condition c2 = create.plainSQLCondition("TABLE2.ID2 = 20 or TABLE2.ID2 = ?", 30);

        q.addConditions(c1);
        q.addConditions(c2);
        assertEquals("select * from dual where ((TABLE1.ID1 = '10') and (TABLE2.ID2 = 20 or TABLE2.ID2 = 30))", q.toSQLReference(true));
        assertEquals("select * from dual where ((TABLE1.ID1 = ?) and (TABLE2.ID2 = 20 or TABLE2.ID2 = ?))", q.toSQLReference(false));
        assertEquals(q, create.select().where(c1, c2).getQuery());

        context.checking(new Expectations() {{
            oneOf(statement).setString(1, "10");
            oneOf(statement).setInt(2, 30);
        }});

        int i = q.bind(statement);
        assertEquals(3, i);

        context.assertIsSatisfied();
    }

    @Test
    public final void testProductSelectQuery() throws Exception {
        SelectQuery q = create.selectQuery();

        q.addFrom(TABLE1);
        q.addFrom(TABLE2);
        q.addFrom(TABLE3);
        assertEquals("select * from TABLE1, TABLE2, TABLE3", q.toSQLReference(true));
        assertEquals("select * from TABLE1, TABLE2, TABLE3", q.toSQLReference(false));
        assertEquals(q, create.select().from(TABLE1, TABLE2, TABLE3).getQuery());

        int i = q.bind(statement);
        assertEquals(1, i);
    }

    @Test
    public final void testJoinSelectQuery() throws Exception {
        SelectQuery q = create.selectQuery();

        q.addFrom(TABLE1);
        q.addJoin(TABLE2);
        assertEquals("select * from TABLE1 join TABLE2", q.toSQLReference(true));
        assertEquals("select * from TABLE1 join TABLE2", q.toSQLReference(false));
        assertEquals(q, create.select().from(TABLE1).join(TABLE2).on().getQuery());

        int i = q.bind(statement);
        assertEquals(1, i);
    }

    @Test
    public final void testJoinOnConditionSelectQuery() throws Exception {
        SelectQuery q = create.selectQuery();
        q.addFrom(TABLE1);
        q.addJoin(TABLE2, FIELD_ID1.equal(FIELD_ID2));

        assertEquals("select * from TABLE1 join TABLE2 on TABLE1.ID1 = TABLE2.ID2", q.toSQLReference(true));
        assertEquals("select * from TABLE1 join TABLE2 on TABLE1.ID1 = TABLE2.ID2", q.toSQLReference(false));
        assertEquals(q, create.select().from(TABLE1).join(TABLE2).on(FIELD_ID1.equal(FIELD_ID2)).getQuery());

        q.addJoin(TABLE3, FIELD_ID2, FIELD_ID3);
        assertEquals("select * from TABLE1 join TABLE2 on TABLE1.ID1 = TABLE2.ID2 join TABLE3 on TABLE2.ID2 = TABLE3.ID3", q.toSQLReference(true));
        assertEquals("select * from TABLE1 join TABLE2 on TABLE1.ID1 = TABLE2.ID2 join TABLE3 on TABLE2.ID2 = TABLE3.ID3", q.toSQLReference(false));
        assertEquals(q, create.select().from(TABLE1)
                                      .join(TABLE2).on(FIELD_ID1.equal(FIELD_ID2))
                                      .join(TABLE3).on(FIELD_ID2.equal(FIELD_ID3)).getQuery());

        int i = q.bind(statement);
        assertEquals(1, i);
    }

    @Test
    public final void testJoinComplexSelectQuery() throws Exception {
        SelectQuery q = create.selectQuery();

        q.addFrom(TABLE1);
        q.addJoin(TABLE2,
                FIELD_ID1.equal(FIELD_ID2),
                FIELD_ID1.equal(1),
                FIELD_ID2.in(1, 2, 3));
        assertEquals("select * from TABLE1 join TABLE2 on (TABLE1.ID1 = TABLE2.ID2 and TABLE1.ID1 = 1 and TABLE2.ID2 in (1, 2, 3))", q.toSQLReference(true));
        assertEquals("select * from TABLE1 join TABLE2 on (TABLE1.ID1 = TABLE2.ID2 and TABLE1.ID1 = ? and TABLE2.ID2 in (?, ?, ?))", q.toSQLReference(false));
        assertEquals(q, create.select().from(TABLE1)
                                .join(TABLE2).on(FIELD_ID1.equal(FIELD_ID2)
                                             .and(FIELD_ID1.equal(1))
                                             .and(FIELD_ID2.in(1, 2, 3))).getQuery());

        q.addJoin(TABLE3, FIELD_ID2, FIELD_ID3);
        assertEquals("select * from TABLE1 join TABLE2 on (TABLE1.ID1 = TABLE2.ID2 and TABLE1.ID1 = 1 and TABLE2.ID2 in (1, 2, 3)) join TABLE3 on TABLE2.ID2 = TABLE3.ID3", q.toSQLReference(true));
        assertEquals("select * from TABLE1 join TABLE2 on (TABLE1.ID1 = TABLE2.ID2 and TABLE1.ID1 = ? and TABLE2.ID2 in (?, ?, ?)) join TABLE3 on TABLE2.ID2 = TABLE3.ID3", q.toSQLReference(false));
        assertEquals(q, create.select().from(TABLE1)
                                .join(TABLE2).on(FIELD_ID1.equal(FIELD_ID2)
                                            .and(FIELD_ID1.equal(1))
                                            .and(FIELD_ID2.in(1, 2, 3)))
                                .join(TABLE3).on(FIELD_ID2.equal(FIELD_ID3)).getQuery());

        context.checking(new Expectations() {{
            oneOf(statement).setInt(1, 1);
            oneOf(statement).setInt(2, 1);
            oneOf(statement).setInt(3, 2);
            oneOf(statement).setInt(4, 3);
        }});

        int i = q.bind(statement);
        assertEquals(5, i);

        context.assertIsSatisfied();
    }

    @Test
    public final void testJoinSelf() throws Exception {
        Table<Table1Record> t1 = TABLE1.as("t1");
        Table<Table1Record> t2 = TABLE1.as("t2");

        SelectQuery q = create.selectQuery();
        q.addFrom(t1);
        q.addJoin(t2, t1.getField(FIELD_ID1), t2.getField(FIELD_ID1));

        assertEquals("select * from TABLE1 t1 join TABLE1 t2 on t1.ID1 = t2.ID1", q.toSQLReference(true));
        assertEquals("select * from TABLE1 t1 join TABLE1 t2 on t1.ID1 = t2.ID1", q.toSQLReference(false));
        assertEquals(q, create.select().from(t1)
                                .join(t2).on(t1.getField(FIELD_ID1).equal(
                                             t2.getField(FIELD_ID1))).getQuery());

        int i = q.bind(statement);
        assertEquals(1, i);
    }

    @Test
    public final void testJoinTypeSelectQuery() throws Exception {
        SelectQuery q = create.selectQuery();
        q.addFrom(TABLE1);
        Join j = create.join(TABLE2, LEFT_OUTER_JOIN, FIELD_ID1, FIELD_ID2);

        q.addJoin(j);
        assertEquals("select * from TABLE1 left outer join TABLE2 on TABLE1.ID1 = TABLE2.ID2", q.toSQLReference(true));
        assertEquals("select * from TABLE1 left outer join TABLE2 on TABLE1.ID1 = TABLE2.ID2", q.toSQLReference(false));
        assertEquals(q, create.select().from(TABLE1).leftOuterJoin(TABLE2).on(FIELD_ID1.equal(FIELD_ID2)).getQuery());

        int i = q.bind(statement);
        assertEquals(1, i);
    }

    @Test
    public final void testGroupSelectQuery() throws Exception {
        SelectQuery q = create.selectQuery();
        q.addFrom(TABLE1);

        q.addGroupBy(FIELD_ID1);
        assertEquals("select * from TABLE1 group by TABLE1.ID1", q.toSQLReference(true));
        assertEquals("select * from TABLE1 group by TABLE1.ID1", q.toSQLReference(false));
        assertEquals(q, create.select().from(TABLE1).groupBy(FIELD_ID1).getQuery());

        q.addGroupBy(FIELD_ID2, FIELD_ID3);
        assertEquals("select * from TABLE1 group by TABLE1.ID1, TABLE2.ID2, TABLE3.ID3", q.toSQLReference(true));
        assertEquals("select * from TABLE1 group by TABLE1.ID1, TABLE2.ID2, TABLE3.ID3", q.toSQLReference(false));
        assertEquals(q, create.select().from(TABLE1).groupBy(FIELD_ID1, FIELD_ID2, FIELD_ID3).getQuery());

        q.addHaving(FIELD_ID1.equal(1));
        assertEquals("select * from TABLE1 group by TABLE1.ID1, TABLE2.ID2, TABLE3.ID3 having TABLE1.ID1 = 1", q.toSQLReference(true));
        assertEquals("select * from TABLE1 group by TABLE1.ID1, TABLE2.ID2, TABLE3.ID3 having TABLE1.ID1 = ?", q.toSQLReference(false));
        assertEquals(q, create.select().from(TABLE1)
                                  .groupBy(FIELD_ID1, FIELD_ID2, FIELD_ID3)
                                  .having(FIELD_ID1.equal(1)).getQuery());

        context.checking(new Expectations() {{
            oneOf(statement).setInt(1, 1);
        }});

        int i = q.bind(statement);
        assertEquals(2, i);

        context.assertIsSatisfied();
    }

    @Test
    public final void testOrderSelectQuery() throws Exception {
        SimpleSelectQuery<Table1Record> q = create.selectQuery(TABLE1);

        q.addOrderBy(FIELD_ID1);
        assertEquals("select * from TABLE1 order by TABLE1.ID1", q.toSQLReference(true));
        assertEquals("select * from TABLE1 order by TABLE1.ID1", q.toSQLReference(false));
        assertEquals(q, create.select(TABLE1).orderBy(FIELD_ID1).getQuery());

        q.addOrderBy(FIELD_ID2, SortOrder.DESC);
        assertEquals("select * from TABLE1 order by TABLE1.ID1, TABLE2.ID2 desc", q.toSQLReference(true));
        assertEquals("select * from TABLE1 order by TABLE1.ID1, TABLE2.ID2 desc", q.toSQLReference(false));
        assertEquals(q, create.select(TABLE1)
                                .orderBy(FIELD_ID1)
                                .orderBy(FIELD_ID2, SortOrder.DESC).getQuery());

        int i = q.bind(statement);
        assertEquals(1, i);
    }

    @Test
    public final void testCompleteSelectQuery() throws Exception {
        SelectQuery q = create.selectQuery();
        q.addFrom(TABLE1);
        q.addJoin(TABLE2, FIELD_ID1, FIELD_ID2);
        q.addSelect(FIELD_ID1, FIELD_ID2);
        q.addGroupBy(FIELD_ID1, FIELD_ID2);
        q.addHaving(FIELD_ID1, 1);
        q.addOrderBy(FIELD_ID1, SortOrder.ASC);
        q.addOrderBy(FIELD_ID2, SortOrder.DESC);

        assertEquals("select TABLE1.ID1, TABLE2.ID2 from TABLE1 join TABLE2 on TABLE1.ID1 = TABLE2.ID2 group by TABLE1.ID1, TABLE2.ID2 having TABLE1.ID1 = 1 order by TABLE1.ID1 asc, TABLE2.ID2 desc", q.toSQLReference(true));
        assertEquals("select TABLE1.ID1, TABLE2.ID2 from TABLE1 join TABLE2 on TABLE1.ID1 = TABLE2.ID2 group by TABLE1.ID1, TABLE2.ID2 having TABLE1.ID1 = ? order by TABLE1.ID1 asc, TABLE2.ID2 desc", q.toSQLReference(false));
        assertEquals(q, create.select(FIELD_ID1, FIELD_ID2)
                          .from(TABLE1)
                          .join(TABLE2).on(FIELD_ID1.equal(FIELD_ID2))
                          .groupBy(FIELD_ID1, FIELD_ID2)
                          .having(FIELD_ID1.equal(1))
                          .orderBy(FIELD_ID1, SortOrder.ASC)
                          .orderBy(FIELD_ID2, SortOrder.DESC).getQuery());

        assertTrue(q.getTables().contains(TABLE1));
        assertTrue(q.getTables().contains(TABLE2));
        assertEquals(2, q.getTables().size());

        context.checking(new Expectations() {{
            oneOf(statement).setInt(1, 1);
        }});

        int i = q.bind(statement);
        assertEquals(2, i);

        context.assertIsSatisfied();
    }

    @Test
    public final void testCombinedSelectQuery() throws Exception {
        SelectQuery combine = createCombinedSelectQuery();
        assertEquals("select * from ((select * from TABLE1 where TABLE1.ID1 = 1) union (select * from TABLE1 where TABLE1.ID1 = 2)) order by TABLE1.ID1", combine.toSQLReference(true));
        assertEquals("select * from ((select * from TABLE1 where TABLE1.ID1 = ?) union (select * from TABLE1 where TABLE1.ID1 = ?)) order by TABLE1.ID1", combine.toSQLReference(false));
        assertEquals(combine, createCombinedSelect().getQuery());

        combine.addSelect(FIELD_ID1);
        assertEquals("select TABLE1.ID1 from ((select * from TABLE1 where TABLE1.ID1 = 1) union (select * from TABLE1 where TABLE1.ID1 = 2)) order by TABLE1.ID1", combine.toSQLReference(true));
        assertEquals("select TABLE1.ID1 from ((select * from TABLE1 where TABLE1.ID1 = ?) union (select * from TABLE1 where TABLE1.ID1 = ?)) order by TABLE1.ID1", combine.toSQLReference(false));
        assertEquals(combine, createCombinedSelect().select(FIELD_ID1).getQuery());

        combine = createCombinedSelectQuery();
        combine.addJoin(TABLE2, FIELD_ID1, FIELD_ID2);
        assertEquals("select * from ((select * from TABLE1 where TABLE1.ID1 = 1) union (select * from TABLE1 where TABLE1.ID1 = 2)) join TABLE2 on TABLE1.ID1 = TABLE2.ID2 order by TABLE1.ID1", combine.toSQLReference(true));
        assertEquals("select * from ((select * from TABLE1 where TABLE1.ID1 = ?) union (select * from TABLE1 where TABLE1.ID1 = ?)) join TABLE2 on TABLE1.ID1 = TABLE2.ID2 order by TABLE1.ID1", combine.toSQLReference(false));
        assertEquals(combine, createCombinedSelect().join(TABLE2).on(FIELD_ID1.equal(FIELD_ID2)).getQuery());

        context.checking(new Expectations() {{
            oneOf(statement).setInt(1, 1);
            oneOf(statement).setInt(2, 2);
        }});

        int i = combine.bind(statement);
        assertEquals(3, i);

        context.assertIsSatisfied();
    }

    private SelectQuery createCombinedSelectQuery() {
        SelectQuery q1 = create.selectQuery();
        SelectQuery q2 = create.selectQuery();

        q1.addFrom(TABLE1);
        q2.addFrom(TABLE1);

        q1.addCompareCondition(FIELD_ID1, 1);
        q2.addCompareCondition(FIELD_ID1, 2);

        SelectQuery combine = q1.combine(q2);
        combine.addOrderBy(FIELD_ID1);
        return combine;
    }

    private Select createCombinedSelect() {
        Select q1 = create.select().from(TABLE1).where(FIELD_ID1.equal(1)).getSelect();
        Select q2 = create.select().from(TABLE1).where(FIELD_ID1.equal(2)).getSelect();

        return q1.union(q2).orderBy(FIELD_ID1).getSelect();
    }

    @Test
    public final void testInnerSelect1() throws Exception {
        SimpleSelectQuery<Table1Record> q1 = create.selectQuery(TABLE1);
        SimpleSelectQuery<Table1Record> q2 = create.selectQuery(q1.asTable().as("inner_temp_table"));
        SimpleSelectQuery<Table1Record> q3 = create.selectQuery(q2.asTable().as("outer_temp_table"));

        assertEquals("select * from (select * from TABLE1) inner_temp_table", q2.toSQLReference(true));
        assertEquals("select * from (select * from TABLE1) inner_temp_table", q2.toSQLReference(false));

        assertEquals("select * from (select * from (select * from TABLE1) inner_temp_table) outer_temp_table", q3.toSQLReference(true));
        assertEquals("select * from (select * from (select * from TABLE1) inner_temp_table) outer_temp_table", q3.toSQLReference(false));
    }

    @Test
    public final void testInnerSelect2() throws Exception {
        SimpleSelectQuery<Table1Record> q1 = create.selectQuery(TABLE1);
        SimpleSelectQuery<Table2Record> q2 = create.selectQuery(TABLE2);

        q1.addSelect(FIELD_ID1.as("inner_id1"));
        q2.addSelect(FIELD_ID2.as("outer_id2"));
        q2.addSelect(q1.asField().as("outer_id1"));

        assertEquals("select TABLE2.ID2 outer_id2, (select TABLE1.ID1 inner_id1 from TABLE1) outer_id1 from TABLE2", q2.toSQLReference(true));
        assertEquals("select TABLE2.ID2 outer_id2, (select TABLE1.ID1 inner_id1 from TABLE1) outer_id1 from TABLE2", q2.toSQLReference(false));
    }

    @Test
    public final void testInnerSelect3() throws Exception {
        SimpleSelectQuery<Table1Record> q1 = create.selectQuery(TABLE1);
        SimpleSelectQuery<Table2Record> q2 = create.selectQuery(TABLE2);

        q2.addSelect(FIELD_ID2);
        q1.addConditions(FIELD_ID1.in(q2));

        assertEquals("select * from TABLE1 where TABLE1.ID1 in (select TABLE2.ID2 from TABLE2)", q1.toSQLReference(true));
        assertEquals("select * from TABLE1 where TABLE1.ID1 in (select TABLE2.ID2 from TABLE2)", q1.toSQLReference(false));
    }

    @Test
    public final void testInnerSelect4() throws Exception {
        SimpleSelectQuery<Table1Record> q1 = create.selectQuery(TABLE1);
        SimpleSelectQuery<Table2Record> q2 = create.selectQuery(TABLE2);

        q2.addSelect(FIELD_ID2);
        q1.addConditions(FIELD_ID1.equal(q2));

        assertEquals("select * from TABLE1 where TABLE1.ID1 = (select TABLE2.ID2 from TABLE2)", q1.toSQLReference(true));
        assertEquals("select * from TABLE1 where TABLE1.ID1 = (select TABLE2.ID2 from TABLE2)", q1.toSQLReference(false));
    }

    @Test
    public final void testInnerSelect5() throws Exception {
        SimpleSelectQuery<Table1Record> q1 = create.selectQuery(TABLE1);
        SimpleSelectQuery<Table2Record> q2 = create.selectQuery(TABLE2);

        q2.addSelect(FIELD_ID2);
        q1.addConditions(FIELD_ID1.greaterThanAny(q2));

        assertEquals("select * from TABLE1 where TABLE1.ID1 > any (select TABLE2.ID2 from TABLE2)", q1.toSQLReference(true));
        assertEquals("select * from TABLE1 where TABLE1.ID1 > any (select TABLE2.ID2 from TABLE2)", q1.toSQLReference(false));
    }

    @Test
    public final void testInnerSelect6() throws Exception {
        SimpleSelectQuery<Table1Record> q1 = create.selectQuery(TABLE1);
        SimpleSelectQuery<Table2Record> q2 = create.selectQuery(TABLE2);

        q2.addSelect(FIELD_ID2);
        q1.addConditions(q2.asExistsCondition());

        assertEquals("select * from TABLE1 where exists (select TABLE2.ID2 from TABLE2)", q1.toSQLReference(true));
        assertEquals("select * from TABLE1 where exists (select TABLE2.ID2 from TABLE2)", q1.toSQLReference(false));
    }
}
