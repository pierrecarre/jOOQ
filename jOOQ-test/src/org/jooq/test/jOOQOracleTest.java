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
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.test.oracle.generatedclasses.tables.TAuthor;
import org.jooq.test.oracle.generatedclasses.tables.TBook;
import org.jooq.test.oracle.generatedclasses.tables.VLibrary;
import org.jooq.test.oracle.generatedclasses.tables.records.TAuthorRecord;
import org.jooq.test.oracle.generatedclasses.tables.records.TBookRecord;
import org.jooq.test.oracle.generatedclasses.tables.records.VLibraryRecord;


/**
 * @author Lukas Eder
 */
public class jOOQOracleTest extends jOOQAbstractTest<TAuthorRecord, TBookRecord, VLibraryRecord> {

	@Override
	protected Connection getConnection() throws Exception {
		Configuration.getInstance().setDialect(SQLDialect.ORACLE);

		Class.forName("oracle.jdbc.OracleDriver");
		return DriverManager.getConnection ("jdbc:oracle:thin:@rhea:1521:RD_DEV", "ODS_TEST", "ODS_TEST");
	}

	@Override
	protected String getCreateScript() {
		return "/org/jooq/test/oracle/create.sql";
	}

    @Override
    protected Table<TAuthorRecord> getTAuthor() {
        return TAuthor.T_AUTHOR;
    }

    @Override
    protected TableField<TAuthorRecord, String> getTAuthor_LAST_NAME() {
        return TAuthor.LAST_NAME;
    }

    @Override
    protected TableField<TAuthorRecord, String> getTAuthor_FIRST_NAME() {
        return TAuthor.FIRST_NAME;
    }

    @Override
    protected TableField<TAuthorRecord, Date> getTAuthor_DATE_OF_BIRTH() {
        return TAuthor.DATE_OF_BIRTH;
    }

    @Override
    protected TableField<TAuthorRecord, Integer> getTAuthor_YEAR_OF_BIRTH() {
        return TAuthor.YEAR_OF_BIRTH;
    }

    @Override
    protected TableField<TAuthorRecord, Integer> getTAuthor_ID() {
        return TAuthor.ID;
    }

    @Override
    protected Table<TBookRecord> getTBook() {
        return TBook.T_BOOK;
    }

    @Override
    protected TableField<TBookRecord, Integer> getTBook_ID() {
        return TBook.ID;
    }

    @Override
    protected TableField<TBookRecord, Integer> getTBook_AUTHOR_ID() {
        return TBook.AUTHOR_ID;
    }

    @Override
    protected TableField<TBookRecord, String> getTBook_TITLE() {
        return TBook.TITLE;
    }

    @Override
    protected TableField<TBookRecord, String> getTBook_CONTENT_TEXT() {
        return null;
    }

    @Override
    protected TableField<TBookRecord, byte[]> getTBook_CONTENT_PDF() {
        return null;
    }

    @Override
    protected Table<VLibraryRecord> getVLibrary() {
        return VLibrary.V_LIBRARY;
    }

    @Override
    protected TableField<VLibraryRecord, String> getVLibrary_TITLE() {
        return VLibrary.TITLE;
    }

    @Override
    protected TableField<VLibraryRecord, String> getVLibrary_AUTHOR() {
        return VLibrary.AUTHOR;
    }
}
