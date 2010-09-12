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
import static org.jooq.test.mysql.generatedclasses.tables.TAuthor.LAST_NAME;
import static org.jooq.test.mysql.generatedclasses.tables.TAuthor.T_AUTHOR;
import static org.jooq.test.mysql.generatedclasses.tables.TBook.T_BOOK;
import static org.jooq.test.mysql.generatedclasses.tables.VLibrary.AUTHOR;
import static org.jooq.test.mysql.generatedclasses.tables.VLibrary.V_LIBRARY;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;

import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.test.mysql.generatedclasses.functions.FAuthorExists;
import org.jooq.test.mysql.generatedclasses.procedures.PAuthorExists;
import org.jooq.test.mysql.generatedclasses.tables.TAuthor;
import org.jooq.test.mysql.generatedclasses.tables.TBook;
import org.jooq.test.mysql.generatedclasses.tables.VLibrary;
import org.junit.Test;


/**
 * @author Lukas Eder
 */
public class jOOQMySQLTest extends jOOQAbstractTest {

	@Override
	protected Connection getConnection() throws Exception {
		Configuration.getInstance().setDialect(SQLDialect.MYSQL);

		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection ("jdbc:mysql://localhost/test", "root", "");
	}

	@Override
	protected String getCreateScript() {
		return "/org/jooq/test/mysql/create.sql";
	}

	@Override
	protected Table getTAuthor() {
		return T_AUTHOR;
	}

	@Override
	protected TableField<String> getTAuthor_LAST_NAME() {
		return LAST_NAME;
	}

	@Override
	protected TableField<String> getTAuthor_FIRST_NAME() {
		return TAuthor.FIRST_NAME;
	}

	@Override
	protected TableField<Date> getTAuthor_DATE_OF_BIRTH() {
		return TAuthor.DATE_OF_BIRTH;
	}

	@Override
	protected TableField<Integer> getTAuthor_ID() {
		return TAuthor.ID;
	}

	@Override
	protected Table getTBook() {
		return T_BOOK;
	}

	@Override
	protected TableField<Integer> getTBook_ID() {
		return TBook.ID;
	}

	@Override
	protected TableField<Integer> getTBook_AUTHOR_ID() {
		return TBook.AUTHOR_ID;
	}

	@Override
	protected TableField<String> getTBook_TITLE() {
		return TBook.TITLE;
	}

	@Override
	protected Table getVLibrary() {
		return V_LIBRARY;
	}

	@Override
	protected TableField<String> getVLibrary_TITLE() {
		return VLibrary.TITLE;
	}

	@Override
	protected TableField<String> getVLibrary_AUTHOR() {
		return AUTHOR;
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
