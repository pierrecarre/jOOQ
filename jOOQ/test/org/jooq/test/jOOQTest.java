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
import static org.jooq.JoinType.LEFT_OUTER_JOIN;
import static org.jooq.impl.FalseCondition.FALSE_CONDITION;
import static org.jooq.impl.TrueCondition.TRUE_CONDITION;
import static org.jooq.test.Data.FIELD_ID1;
import static org.jooq.test.Data.FIELD_ID2;
import static org.jooq.test.Data.FIELD_ID3;
import static org.jooq.test.Data.FIELD_NAME1;
import static org.jooq.test.Data.TABLE1;
import static org.jooq.test.Data.TABLE2;
import static org.jooq.test.Data.TABLE3;

import java.sql.PreparedStatement;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jooq.BetweenCondition;
import org.jooq.CombinedCondition;
import org.jooq.Comparator;
import org.jooq.CompareCondition;
import org.jooq.DeleteQuery;
import org.jooq.Field;
import org.jooq.Function;
import org.jooq.InCondition;
import org.jooq.InsertQuery;
import org.jooq.Join;
import org.jooq.JoinCondition;
import org.jooq.SelectQuery;
import org.jooq.SortOrder;
import org.jooq.UpdateQuery;
import org.jooq.impl.Functions;
import org.jooq.impl.QueryFactory;
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
	
	@Before
	public void setUp() throws Exception {
		context = new Mockery();
		statement = context.mock(PreparedStatement.class);
	}

	@After
	public void tearDown() throws Exception {
		statement = null;
		context = null;
	}

	@Test
	public final void testEmptyCombinedCondition() throws Exception {
		CombinedCondition c = QueryFactory.createCombinedCondition();
		assertEquals("1 = 1", c.toSQL());

		int i = c.bind(statement);
		assertEquals(1, i);
	}

	@Test
	public final void testSingleCombinedCondition() throws Exception {
		CombinedCondition c = QueryFactory.createCombinedCondition(TRUE_CONDITION);
		assertEquals(TRUE_CONDITION.toSQL(true), c.toSQL(true));
		assertEquals(TRUE_CONDITION.toSQL(false), c.toSQL(false));

		int i = c.bind(statement);
		assertEquals(1, i);
	}

	@Test
	public final void testMultipleCombinedCondition() throws Exception {
		CompareCondition<Integer> c1 = QueryFactory.createCompareCondition(FIELD_ID1, 10);
		CompareCondition<Integer> c2 = QueryFactory.createCompareCondition(FIELD_ID2, 20);

		CombinedCondition c = QueryFactory.createCombinedCondition(c1, c2);
		assertEquals("(ID1 = 10 and ID2 = 20)", c.toSQL(true));
		assertEquals("(ID1 = ? and ID2 = ?)", c.toSQL(false));

		context.checking(new Expectations() {{
			oneOf(statement).setInt(1, 10);
			oneOf(statement).setInt(2, 20);
		}});
		
		int i = c.bind(statement);
		assertEquals(3, i);
		
		context.assertIsSatisfied();
	}
	
	@Test
	public final void testBetweenCondition() throws Exception {
		BetweenCondition<Integer> c = QueryFactory.createBetweenCondition(FIELD_ID1, 1, 10);
		assertEquals("ID1 between 1 and 10", c.toSQL(true));
		assertEquals("ID1 between ? and ?", c.toSQL(false));

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
		InCondition<Integer> c = QueryFactory.createInCondition(FIELD_ID1, 1, 10);
		assertEquals("ID1 in (1, 10)", c.toSQL(true));
		assertEquals("ID1 in (?, ?)", c.toSQL(false));
		
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
		CompareCondition<Integer> c = QueryFactory.createCompareCondition(FIELD_ID1, 10);
		assertEquals("ID1 = 10", c.toSQL(true));
		assertEquals("ID1 = ?", c.toSQL(false));
		
		context.checking(new Expectations() {{
			oneOf(statement).setInt(1, 10);
		}});
		
		int i = c.bind(statement);
		assertEquals(2, i);
		
		context.assertIsSatisfied();
	}
	
	@Test
	public final void testIsNullCondition() throws Exception {
		CompareCondition<Integer> c1 = QueryFactory.createCompareCondition(FIELD_ID1, null);
		assertEquals("ID1 is null", c1.toSQL(true));
		assertEquals("ID1 is null", c1.toSQL(false));
		
		CompareCondition<Integer> c2 = QueryFactory.createCompareCondition(FIELD_ID1, null, Comparator.NOT_EQUALS);
		assertEquals("ID1 is not null", c2.toSQL(true));
		assertEquals("ID1 is not null", c2.toSQL(false));

		int i = c1.bind(statement);
		assertEquals(1, i);

		int j = c2.bind(statement);
		assertEquals(1, j);
	}
	
	@Test
	public final void testNullFunction() throws Exception {
		Field<?> f = Functions.NULL();
		assertEquals("null", f.toSQL(true));
		assertEquals("null", f.toSQL(false));

		int i = f.bind(statement);
		assertEquals(1, i);
	}
	
	@Test
	public final void testConstantFunction() throws Exception {
		Field<Integer> f1 = Functions.constant(Integer.valueOf(1));
		assertEquals(Integer.class, f1.getType());
		assertEquals("'1'", f1.toSQL(true));
		assertEquals("'1'", f1.toSQL(false));
		
		Field<String> f2 = Functions.constant("test");
		assertEquals(String.class, f2.getType());
		assertEquals("'test'", f2.toSQL(true));
		assertEquals("'test'", f2.toSQL(false));

		int i = f1.bind(statement);
		assertEquals(1, i);

		int j = f2.bind(statement);
		assertEquals(1, j);
	}
	
	@Test
	public final void testArithmeticFunctions() throws Exception {
		Function<Integer> sum = Functions.sum(FIELD_ID1);
		assertEquals(Integer.class, sum.getType());
		assertEquals("sum(ID1)", sum.toSQL(true));
		assertEquals("sum(ID1)", sum.toSQL(false));
		assertEquals(1, sum.bind(statement));

		Function<Double> avg = Functions.avg(FIELD_ID1);
		assertEquals(Double.class, avg.getType());
		assertEquals("avg(ID1)", avg.toSQL(true));
		assertEquals("avg(ID1)", avg.toSQL(false));
		assertEquals(1, avg.bind(statement));

		Function<Integer> min = Functions.min(FIELD_ID1);
		assertEquals(Integer.class, min.getType());
		assertEquals("min(ID1)", min.toSQL(true));
		assertEquals("min(ID1)", min.toSQL(false));
		assertEquals(1, min.bind(statement));

		Function<Integer> max = Functions.max(FIELD_ID1);
		assertEquals(Integer.class, max.getType());
		assertEquals("max(ID1)", max.toSQL(true));
		assertEquals("max(ID1)", max.toSQL(false));
		assertEquals(1, max.bind(statement));

		Function<Integer> count1 = Functions.count();
		assertEquals(Integer.class, count1.getType());
		assertEquals("count(*)", count1.toSQL(true));
		assertEquals("count(*)", count1.toSQL(false));
		assertEquals(1, count1.bind(statement));

		Function<Integer> count2 = Functions.count(FIELD_ID1);
		assertEquals(Integer.class, count2.getType());
		assertEquals("count(ID1)", count2.toSQL(true));
		assertEquals("count(ID1)", count2.toSQL(false));
		assertEquals(1, count2.bind(statement));

		Function<Integer> count3 = Functions.countDistinct(FIELD_ID1);
		assertEquals(Integer.class, count3.getType());
		assertEquals("count(distinct ID1)", count3.toSQL(true));
		assertEquals("count(distinct ID1)", count3.toSQL(false));
		assertEquals(1, count3.bind(statement));
	}
	
	@Test(expected = IllegalStateException.class)  
	public final void testEmptyInsertQuery() throws Exception {
		InsertQuery q = QueryFactory.createInsertQuery(TABLE1);
		q.toSQL();
	}
	
	@Test
	public final void testInsertQuery1() throws Exception {
		InsertQuery q = QueryFactory.createInsertQuery(TABLE1);

		q.addValue(FIELD_ID1, 10);
		assertEquals("insert into TABLE1 (ID1) values (10)", q.toSQL(true));
		assertEquals("insert into TABLE1 (ID1) values (?)", q.toSQL(false));
		
		context.checking(new Expectations() {{
			oneOf(statement).setInt(1, 10);
		}});
		
		int i = q.bind(statement);
		assertEquals(2, i);
		
		context.assertIsSatisfied();
	}
	
	@Test
	public final void testInsertQuery2() throws Exception {
		InsertQuery q = QueryFactory.createInsertQuery(TABLE1);

		q.addValue(FIELD_ID1, 10);
		q.addValue(FIELD_NAME1, "ABC");
		assertEquals("insert into TABLE1 (ID1, NAME1) values (10, 'ABC')", q.toSQL(true));
		assertEquals("insert into TABLE1 (ID1, NAME1) values (?, ?)", q.toSQL(false));
		
		context.checking(new Expectations() {{
			oneOf(statement).setInt(1, 10);
			oneOf(statement).setString(2, "ABC");
		}});
		
		int i = q.bind(statement);
		assertEquals(3, i);
		
		context.assertIsSatisfied();
	}
	
	@Test(expected = IllegalStateException.class)  
	public final void testEmptyUpdateQuery() throws Exception {
		UpdateQuery q = QueryFactory.createUpdateQuery(TABLE1);
		q.toSQL();
	}

	@Test
	public final void testUpdateQuery1() throws Exception {
		UpdateQuery q = QueryFactory.createUpdateQuery(TABLE1);
		
		q.addValue(FIELD_ID1, 10);
		assertEquals("update TABLE1 set ID1 = 10", q.toSQL(true));
		assertEquals("update TABLE1 set ID1 = ?", q.toSQL(false));

		context.checking(new Expectations() {{
			oneOf(statement).setInt(1, 10);
		}});
		
		int i = q.bind(statement);
		assertEquals(2, i);
		
		context.assertIsSatisfied();
	}
	
	@Test
	public final void testUpdateQuery2() throws Exception {
		UpdateQuery q = QueryFactory.createUpdateQuery(TABLE1);
		
		q.addValue(FIELD_ID1, 10);
		q.addValue(FIELD_NAME1, "ABC");
		assertEquals("update TABLE1 set ID1 = 10, NAME1 = 'ABC'", q.toSQL(true));
		assertEquals("update TABLE1 set ID1 = ?, NAME1 = ?", q.toSQL(false));
		
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
		UpdateQuery q = QueryFactory.createUpdateQuery(TABLE1);
		CompareCondition<Integer> c = QueryFactory.createCompareCondition(FIELD_ID1, 10);
		
		q.addValue(FIELD_ID1, 10);
		q.addValue(FIELD_NAME1, "ABC");
		q.addConditions(c);
		assertEquals("update TABLE1 set ID1 = 10, NAME1 = 'ABC' where ID1 = 10", q.toSQL(true));
		assertEquals("update TABLE1 set ID1 = ?, NAME1 = ? where ID1 = ?", q.toSQL(false));
		
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
		UpdateQuery q = QueryFactory.createUpdateQuery(TABLE1);
		CompareCondition<Integer> c1 = QueryFactory.createCompareCondition(FIELD_ID1, 10);
		CompareCondition<Integer> c2 = QueryFactory.createCompareCondition(FIELD_ID1, 20);
		
		q.addValue(FIELD_ID1, 10);
		q.addValue(FIELD_NAME1, "ABC");
		q.addConditions(c1);
		q.addConditions(c2);
		assertEquals("update TABLE1 set ID1 = 10, NAME1 = 'ABC' where (ID1 = 10 and ID1 = 20)", q.toSQL(true));
		assertEquals("update TABLE1 set ID1 = ?, NAME1 = ? where (ID1 = ? and ID1 = ?)", q.toSQL(false));
		
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
		UpdateQuery q = QueryFactory.createUpdateQuery(TABLE1);
		CompareCondition<Integer> c1 = QueryFactory.createCompareCondition(FIELD_ID1, 10);
		CompareCondition<Integer> c2 = QueryFactory.createCompareCondition(FIELD_ID1, 20);
		
		q.addValue(FIELD_ID1, 10);
		q.addValue(FIELD_NAME1, "ABC");
		q.addConditions(c1);
		q.addConditions(c2);
		q.addConditions(c2, c1);
		assertEquals("update TABLE1 set ID1 = 10, NAME1 = 'ABC' where ((ID1 = 10 and ID1 = 20) and (ID1 = 20 and ID1 = 10))", q.toSQL(true));
		assertEquals("update TABLE1 set ID1 = ?, NAME1 = ? where ((ID1 = ? and ID1 = ?) and (ID1 = ? and ID1 = ?))", q.toSQL(false));
		
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
		DeleteQuery q = QueryFactory.createDeleteQuery(TABLE1);
		
		assertEquals("delete from TABLE1", q.toSQL(true));
		assertEquals("delete from TABLE1", q.toSQL(false));
	}
	
	@Test
	public final void testDeleteQuery2() throws Exception {
		DeleteQuery q = QueryFactory.createDeleteQuery(TABLE1);
		
		q.addConditions(FALSE_CONDITION);
		assertEquals("delete from TABLE1 where 1 = 0", q.toSQL(true));
		assertEquals("delete from TABLE1 where 1 = 0", q.toSQL(false));
	}
	
	@Test
	public final void testDeleteQuery3() throws Exception {
		DeleteQuery q = QueryFactory.createDeleteQuery(TABLE1);
		CompareCondition<Integer> c1 = QueryFactory.createCompareCondition(FIELD_ID1, 10);
		CompareCondition<Integer> c2 = QueryFactory.createCompareCondition(FIELD_ID1, 20);
		
		q.addConditions(c1);
		q.addConditions(c2);
		assertEquals("delete from TABLE1 where (ID1 = 10 and ID1 = 20)", q.toSQL(true));
		assertEquals("delete from TABLE1 where (ID1 = ? and ID1 = ?)", q.toSQL(false));
		
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
		DeleteQuery q = QueryFactory.createDeleteQuery(TABLE1);
		CompareCondition<Integer> c1 = QueryFactory.createCompareCondition(FIELD_ID1, 10);
		CompareCondition<Integer> c2 = QueryFactory.createCompareCondition(FIELD_ID1, 20);
		
		q.addConditions(c1);
		q.addConditions(c2);
		q.addConditions(c2, c1);
		assertEquals("delete from TABLE1 where ((ID1 = 10 and ID1 = 20) and (ID1 = 20 and ID1 = 10))", q.toSQL(true));
		assertEquals("delete from TABLE1 where ((ID1 = ? and ID1 = ?) and (ID1 = ? and ID1 = ?))", q.toSQL(false));
		
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
		SelectQuery q = QueryFactory.createSelectQuery();
		
		assertEquals("select * from dual", q.toSQL(true));
		assertEquals("select * from dual", q.toSQL(false));
	}
	
	@Test
	public final void testConditionalSelectQuery2() throws Exception {
		SelectQuery q = QueryFactory.createSelectQuery();
		
		q.addConditions(FALSE_CONDITION);
		assertEquals("select * from dual where 1 = 0", q.toSQL(true));
		assertEquals("select * from dual where 1 = 0", q.toSQL(false));
	}
	
	@Test
	public final void testConditionalSelectQuery3() throws Exception {
		SelectQuery q = QueryFactory.createSelectQuery();
		
		q.addConditions(FALSE_CONDITION);
		q.addConditions(TRUE_CONDITION);
		assertEquals("select * from dual where (1 = 0 and 1 = 1)", q.toSQL(true));
		assertEquals("select * from dual where (1 = 0 and 1 = 1)", q.toSQL(false));
	}
	
	@Test
	public final void testConditionalSelectQuery4() throws Exception {
		SelectQuery q = QueryFactory.createSelectQuery();
		CompareCondition<Integer> c1 = QueryFactory.createCompareCondition(FIELD_ID1, 10);
		CompareCondition<Integer> c2 = QueryFactory.createCompareCondition(FIELD_ID1, 20);
		
		q.addConditions(c1);
		q.addConditions(c2);
		q.addConditions(c2, c1);
		assertEquals("select * from dual where ((ID1 = 10 and ID1 = 20) and (ID1 = 20 and ID1 = 10))", q.toSQL(true));
		assertEquals("select * from dual where ((ID1 = ? and ID1 = ?) and (ID1 = ? and ID1 = ?))", q.toSQL(false));
		
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
	public final void testProductSelectQuery() throws Exception {
		SelectQuery q = QueryFactory.createSelectQuery();
		
		q.addFrom(TABLE1);
		q.addFrom(TABLE2, TABLE3);
		assertEquals("select * from TABLE1, TABLE2, TABLE3", q.toSQL(true));
		assertEquals("select * from TABLE1, TABLE2, TABLE3", q.toSQL(false));

		int i = q.bind(statement);
		assertEquals(1, i);
	}
	
	@Test
	public final void testJoinSelectQuery() throws Exception {
		SelectQuery q = QueryFactory.createSelectQuery(TABLE1);
		
		q.addJoin(TABLE2);
		assertEquals("select * from TABLE1 join TABLE2", q.toSQL(true));
		assertEquals("select * from TABLE1 join TABLE2", q.toSQL(false));

		int i = q.bind(statement);
		assertEquals(1, i);
	}
	
	@Test
	public final void testJoinOnConditionSelectQuery() throws Exception {
		SelectQuery q = QueryFactory.createSelectQuery(TABLE1);
		JoinCondition<Integer> c = QueryFactory.createJoinCondition(FIELD_ID1, FIELD_ID2);
		
		q.addJoin(TABLE2, c);
		assertEquals("select * from TABLE1 join TABLE2 on ID1 = ID2", q.toSQL(true));
		assertEquals("select * from TABLE1 join TABLE2 on ID1 = ID2", q.toSQL(false));
		
		q.addJoin(TABLE3, FIELD_ID2, FIELD_ID3);
		assertEquals("select * from TABLE1 join TABLE2 on ID1 = ID2 join TABLE3 on ID2 = ID3", q.toSQL(true));
		assertEquals("select * from TABLE1 join TABLE2 on ID1 = ID2 join TABLE3 on ID2 = ID3", q.toSQL(false));

		int i = q.bind(statement);
		assertEquals(1, i);
	}
	
	@Test
	public final void testJoinTypeSelectQuery() throws Exception {
		SelectQuery q = QueryFactory.createSelectQuery(TABLE1);
		Join j = QueryFactory.createJoin(TABLE2, FIELD_ID1, FIELD_ID2, LEFT_OUTER_JOIN);
		
		q.addJoin(j);
		assertEquals("select * from TABLE1 left outer join TABLE2 on ID1 = ID2", q.toSQL(true));
		assertEquals("select * from TABLE1 left outer join TABLE2 on ID1 = ID2", q.toSQL(false));

		int i = q.bind(statement);
		assertEquals(1, i);
	}
	
	@Test
	public final void testGroupSelectQuery() throws Exception {
		SelectQuery q = QueryFactory.createSelectQuery(TABLE1);
		
		q.addGroupBy(FIELD_ID1);
		assertEquals("select * from TABLE1 group by ID1", q.toSQL(true));
		assertEquals("select * from TABLE1 group by ID1", q.toSQL(false));

		q.addGroupBy(FIELD_ID2, FIELD_ID3);
		assertEquals("select * from TABLE1 group by ID1, ID2, ID3", q.toSQL(true));
		assertEquals("select * from TABLE1 group by ID1, ID2, ID3", q.toSQL(false));

		int i = q.bind(statement);
		assertEquals(1, i);
	}
	
	@Test
	public final void testOrderSelectQuery() throws Exception {
		SelectQuery q = QueryFactory.createSelectQuery(TABLE1);
		
		q.addOrderBy(FIELD_ID1);
		assertEquals("select * from TABLE1 order by ID1", q.toSQL(true));
		assertEquals("select * from TABLE1 order by ID1", q.toSQL(false));

		q.addOrderBy(FIELD_ID2, SortOrder.DESC);
		assertEquals("select * from TABLE1 order by ID1, ID2 desc", q.toSQL(true));
		assertEquals("select * from TABLE1 order by ID1, ID2 desc", q.toSQL(false));

		int i = q.bind(statement);
		assertEquals(1, i);
	}
	
	@Test
	public final void testCompleteSelectQuery() throws Exception {
		SelectQuery q = QueryFactory.createSelectQuery(TABLE1);
		q.addJoin(TABLE2, FIELD_ID1, FIELD_ID2);
		q.addSelect(FIELD_ID1, FIELD_ID2);
		q.addGroupBy(FIELD_ID1, FIELD_ID2);
		q.addOrderBy(FIELD_ID1, SortOrder.ASC);
		q.addOrderBy(FIELD_ID2, SortOrder.DESC);
		
		assertEquals("select ID1, ID2 from TABLE1 join TABLE2 on ID1 = ID2 group by ID1, ID2 order by ID1 asc, ID2 desc", q.toSQL(true));
		assertEquals("select ID1, ID2 from TABLE1 join TABLE2 on ID1 = ID2 group by ID1, ID2 order by ID1 asc, ID2 desc", q.toSQL(false));

		int i = q.bind(statement);
		assertEquals(1, i);
	}
}
