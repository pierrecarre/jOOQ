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

import java.sql.Date;

import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.TableFieldImpl;
import org.jooq.impl.TableImpl;

/**
 * @author Lukas Eder
 */
public final class Data {
	public static final Table TABLE1 = new TableImpl("TABLE1");
	public static final Table TABLE2 = new TableImpl("TABLE2");
	public static final Table TABLE3 = new TableImpl("TABLE3");

	public static final TableField<Integer> FIELD_ID1 = new TableFieldImpl<Integer>("ID1", Integer.class, TABLE1);
	public static final TableField<Integer> FIELD_ID2 = new TableFieldImpl<Integer>("ID2", Integer.class, TABLE2);
	public static final TableField<Integer> FIELD_ID3 = new TableFieldImpl<Integer>("ID3", Integer.class, TABLE3);

	public static final TableField<String> FIELD_NAME1 = new TableFieldImpl<String>("NAME1", String.class, TABLE1);
	public static final TableField<String> FIELD_NAME2 = new TableFieldImpl<String>("NAME2", String.class, TABLE2);
	public static final TableField<String> FIELD_NAME3 = new TableFieldImpl<String>("NAME3", String.class, TABLE3);

	public static final TableField<Date> FIELD_DATE1 = new TableFieldImpl<Date>("DATE1", Date.class, TABLE1);
	public static final TableField<Date> FIELD_DATE2 = new TableFieldImpl<Date>("DATE2", Date.class, TABLE2);
	public static final TableField<Date> FIELD_DATE3 = new TableFieldImpl<Date>("DATE3", Date.class, TABLE3);

	private Data() {}
}
