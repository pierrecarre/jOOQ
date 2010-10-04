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

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;

import org.jooq.Configuration;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.test.postgres.generatedclasses.tables.TAuthor;
import org.jooq.test.postgres.generatedclasses.tables.TBook;
import org.jooq.test.postgres.generatedclasses.tables.VLibrary;


/**
 * @author Lukas Eder
 */
public class jOOQPostgresTest extends jOOQAbstractTest {

	@Override
	protected Connection getConnection() throws Exception {
		Configuration.getInstance().setDialect(SQLDialect.POSTGRES);

		Class.forName("org.postgresql.Driver");
		return DriverManager.getConnection ("jdbc:postgresql:postgres", "postgres", "password");
	}

	@Override
	protected String getCreateScript() {
		return "/org/jooq/test/postgres/create.sql";
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Table getTAuthor() {
		return TAuthor.T_AUTHOR;
	}

	@Override
	protected TableField<? extends Record, String> getTAuthor_LAST_NAME() {
		return TAuthor.LAST_NAME;
	}

	@Override
	protected TableField<? extends Record, String> getTAuthor_FIRST_NAME() {
		return TAuthor.FIRST_NAME;
	}

	@Override
	protected TableField<? extends Record, Date> getTAuthor_DATE_OF_BIRTH() {
		return TAuthor.DATE_OF_BIRTH;
	}

	@Override
	protected TableField<? extends Record, Integer> getTAuthor_YEAR_OF_BIRTH() {
		return TAuthor.YEAR_OF_BIRTH;
	}

	@Override
	protected TableField<? extends Record, Integer> getTAuthor_ID() {
		return TAuthor.ID;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Table getTBook() {
		return TBook.T_BOOK;
	}

	@Override
	protected TableField<? extends Record, Integer> getTBook_ID() {
		return TBook.ID;
	}

	@Override
	protected TableField<? extends Record, Integer> getTBook_AUTHOR_ID() {
		return TBook.AUTHOR_ID;
	}

	@Override
	protected TableField<? extends Record, String> getTBook_TITLE() {
		return TBook.TITLE;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Table getVLibrary() {
		return VLibrary.V_LIBRARY;
	}

	@Override
	protected TableField<? extends Record, String> getVLibrary_TITLE() {
		return VLibrary.TITLE;
	}

	@Override
	protected TableField<? extends Record, String> getVLibrary_AUTHOR() {
		return VLibrary.AUTHOR;
	}
}
