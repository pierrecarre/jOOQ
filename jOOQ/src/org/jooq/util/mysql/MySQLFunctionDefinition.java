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

import org.jooq.util.ColumnDefinition;
import org.jooq.util.Database;
import org.jooq.util.FunctionDefinition;

/**
 * @author Lukas Eder
 */
public class MySQLFunctionDefinition extends AbstractProcedureDefinition implements FunctionDefinition {

	private List<ColumnDefinition> inParameters;
	private ColumnDefinition returnValue;
	
	public MySQLFunctionDefinition(Database database, String name, String comment, String params, String returnValue) {
		super(database, name, comment);
		
		init (params, returnValue);
	}

	private void init(String params, String returnValue) {
		inParameters = new ArrayList<ColumnDefinition>();
		
		String[] split = params.split(",");
		for (int i = 0; i < split.length; i++) {
			String param = split[i];
			
			param = param.trim();
			Matcher matcher = PARAMETER_PATTERN.matcher(param);
			while (matcher.find()) {
				inParameters.add(createColumn(matcher, 3, i + 1));
			}
		}
		
		Matcher matcher = TYPE_PATTERN.matcher(returnValue);
		if (matcher.find()) {
			this.returnValue = createColumn(matcher, 0, -1);
		}
	}
	
	private ColumnDefinition createColumn(Matcher matcher, int group, int columnIndex) {
		String paramName = matcher.group(group);
		String paramType = matcher.group(group + 1);
		
		Class<?> type = MySQLDataType.valueOf(paramType.toUpperCase()).getType();
		return new MySQLColumnDefinition(this, paramName, columnIndex, type, null);
	}

	@Override
	public List<ColumnDefinition> getInParameters() {
		return inParameters;
	}

	@Override
	public ColumnDefinition getReturnValue() {
		return returnValue;
	}
}
