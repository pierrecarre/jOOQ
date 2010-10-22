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

package org.jooq.util.mysql;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.jooq.util.AbstractProcedureDefinition;
import org.jooq.util.ColumnDefinition;
import org.jooq.util.Database;
import org.jooq.util.DefaultColumnDefinition;
import org.jooq.util.InOutDefinition;
import org.jooq.util.ProcedureDefinition;

/**
 * @author Lukas Eder
 */
public class MySQLProcedureDefinition extends AbstractProcedureDefinition implements ProcedureDefinition {

	private List<ColumnDefinition> inParameters;
	private List<ColumnDefinition> outParameters;
	private List<ColumnDefinition> allParameters;

	public MySQLProcedureDefinition(Database database, String name, String comment, String params) {
		super(database, name, comment);

		init (params);
	}

	private void init(String params) {
		inParameters = new ArrayList<ColumnDefinition>();
		outParameters = new ArrayList<ColumnDefinition>();
		allParameters = new ArrayList<ColumnDefinition>();

		String[] split = params.split(",");
		for (int i = 0; i < split.length; i++) {
			String param = split[i];

			param = param.trim();
			Matcher matcher = PARAMETER_PATTERN.matcher(param);
			while (matcher.find()) {
				String inOut = matcher.group(2);
				String paramName = matcher.group(3);
				String paramType = matcher.group(4);

				Class<?> type = MySQLDataType.valueOf(paramType).getType();
				ColumnDefinition column = new DefaultColumnDefinition(getDatabase(), getName(), paramName, i + 1, type, null);
				allParameters.add(column);

				switch (InOutDefinition.getFromString(inOut)) {
				case IN:
					inParameters.add(column);
					break;
				case OUT:
					outParameters.add(column);
					break;
				case INOUT:
					inParameters.add(column);
					outParameters.add(column);
					break;
				}
			}
		}
	}

	@Override
	public List<ColumnDefinition> getInParameters() {
		return inParameters;
	}

	@Override
	public List<ColumnDefinition> getOutParameters() {
		return outParameters;
	}

	@Override
	public List<ColumnDefinition> getAllParameters() {
		return allParameters;
	}
}
