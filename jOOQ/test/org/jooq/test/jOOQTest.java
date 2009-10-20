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
import static org.jooq.test.ConditionStub.CONDITION;
import static org.jooq.test.Fields.FIELD_ID;

import org.jooq.BetweenCondition;
import org.jooq.CombinedCondition;
import org.jooq.Comparator;
import org.jooq.CompareCondition;
import org.jooq.InCondition;
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

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testEmptyCombinedCondition() throws Exception {
		CombinedCondition c = QueryFactory.createCombinedCondition();
		assertEquals("1 = 1", c.toSQL());
	}

	@Test
	public final void testSingleCombinedCondition() throws Exception {
		CombinedCondition c = QueryFactory.createCombinedCondition(CONDITION);
		assertEquals(CONDITION.toSQL(true), c.toSQL(true));
		assertEquals(CONDITION.toSQL(false), c.toSQL(false));
	}

	@Test
	public final void testMultipleCombinedCondition() throws Exception {
		CombinedCondition c = QueryFactory.createCombinedCondition(CONDITION, CONDITION);
		assertEquals("(" + CONDITION.toSQL(true) + " and " + CONDITION.toSQL(true) + ")", c.toSQL(true));
		assertEquals("(" + CONDITION.toSQL(false) + " and " + CONDITION.toSQL(false) + ")", c.toSQL(false));
	}
	
	@Test
	public final void testBetweenCondition() throws Exception {
		BetweenCondition<Integer> c = QueryFactory.createBetweenCondition(FIELD_ID, 1, 10);
		assertEquals("ID between 1 and 10", c.toSQL(true));
		assertEquals("ID between ? and ?", c.toSQL(false));
	}
	
	@Test
	public final void testInCondition() throws Exception {
		InCondition<Integer> c = QueryFactory.createInCondition(FIELD_ID, 1, 10);
		assertEquals("ID in (1, 10)", c.toSQL(true));
		assertEquals("ID in (?, ?)", c.toSQL(false));
	}
	
	@Test
	public final void testCompareCondition() throws Exception {
		CompareCondition<Integer> c = QueryFactory.createCompareCondition(FIELD_ID, 10);
		assertEquals("ID = 10", c.toSQL(true));
		assertEquals("ID = ?", c.toSQL(false));
	}
	
	@Test
	public final void testIsNullCondition() throws Exception {
		CompareCondition<Integer> c1 = QueryFactory.createCompareCondition(FIELD_ID, null);
		assertEquals("ID is null", c1.toSQL(true));
		assertEquals("ID is null", c1.toSQL(false));
		
		CompareCondition<Integer> c2 = QueryFactory.createCompareCondition(FIELD_ID, null, Comparator.NOT_EQUALS);
		assertEquals("ID is not null", c2.toSQL(true));
		assertEquals("ID is not null", c2.toSQL(false));
	}
}
