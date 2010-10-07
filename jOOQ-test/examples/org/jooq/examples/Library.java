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
package org.jooq.examples;

import static org.jooq.test.mysql.generatedclasses.tables.TAuthor.T_AUTHOR;
import static org.jooq.test.mysql.generatedclasses.tables.TBook.T_BOOK;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jooq.Comparator;
import org.jooq.Configuration;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectQuery;
import org.jooq.impl.Create;
import org.jooq.test.mysql.generatedclasses.tables.TAuthor;
import org.jooq.test.mysql.generatedclasses.tables.TBook;
import org.jooq.test.mysql.generatedclasses.tables.records.TAuthorRecord;

public class Library {

	public static void main(String[] args) throws Exception {
		System.out.println("First run...");
		firstRun(getConnection());

		System.out.println();
		System.out.println("Second run...");
		secondRun(getConnection());

		System.out.println();
		System.out.println("Third run...");
		thirdRun(getConnection());
	}

	protected static Connection getConnection() throws Exception {
		Configuration.getInstance().setDialect(SQLDialect.MYSQL);

		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection ("jdbc:mysql://localhost/test", "root", "");
	}

	/**
	 * Run this code providing your own database connection.
	 */
	public static void firstRun(Connection c) throws SQLException {
		// Create the query
		SelectQuery<Record> q = Create.selectQuery();
		q.addFrom(T_AUTHOR);
		q.addJoin(T_BOOK, TAuthor.ID, TBook.AUTHOR_ID);
		q.addCompareCondition(TAuthor.YEAR_OF_BIRTH, 1920, Comparator.GREATER);
		q.addOrderBy(TBook.TITLE);

		// Execute the query and fetch the results
		q.execute(c);
		Result<Record> result = q.getResult();

		// Loop over the resulting records
		for (Record record : result) {

			// Type safety assured with generics
			String firstName = record.getValue(TAuthor.FIRST_NAME);
			String lastName = record.getValue(TAuthor.LAST_NAME);
			String title = record.getValue(TBook.TITLE);
			Integer publishedIn = record.getValue(TBook.PUBLISHED_IN);

			System.out.println(title + " (published in " + publishedIn + ") by " + firstName + " " + lastName);
		}
	}

	/**
	 * Run this code providing your own database connection.
	 */
	public static void secondRun(Connection c) throws SQLException {
		SelectQuery<Record> q = Create.select()
			.from(T_AUTHOR)
			.join(T_BOOK).on(TAuthor.ID.equal(TBook.AUTHOR_ID))
			.where(TAuthor.YEAR_OF_BIRTH.greaterThan(1920))
			.orderBy(TBook.TITLE).getQuery();

		// Execute the query and fetch the results
		q.execute(c);
		Result<Record> result = q.getResult();

		// Loop over the resulting records
		for (Record record : result) {

			// Type safety assured with generics
			String firstName = record.getValue(TAuthor.FIRST_NAME);
			String lastName = record.getValue(TAuthor.LAST_NAME);
			String title = record.getValue(TBook.TITLE);
			Integer publishedIn = record.getValue(TBook.PUBLISHED_IN);

			System.out.println(title + " (published in " + publishedIn + ") by " + firstName + " " + lastName);
		}
	}

	/**
	 * Run this code providing your own database connection.
	 */
	public static void thirdRun(Connection c) throws SQLException {
		SelectQuery<TAuthorRecord> q = Create.select(T_AUTHOR)
			.where(TAuthor.YEAR_OF_BIRTH.greaterThan(1920))
			.orderBy(TAuthor.LAST_NAME).getQuery();

		// Execute the query and fetch the results
		q.execute(c);
		Result<TAuthorRecord> result = q.getResult();

		// Loop over the resulting records
		for (TAuthorRecord record : result) {

			// Type safety assured with generics
			String firstName = record.getFirstName();
			String lastName = record.getLastName();

			System.out.println("Author : " + firstName + " " + lastName);
		}
	}
}
