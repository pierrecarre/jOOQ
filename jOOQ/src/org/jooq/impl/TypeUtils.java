/**
 * Copyright (c) 2009-2011, Lukas Eder, lukas.eder@gmail.com
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
package org.jooq.impl;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;

import org.jooq.SQLDialectNotSupportedException;

/**
 * Utility methods for type conversions
 *
 * @author Lukas Eder
 */
final class TypeUtils {

    @SuppressWarnings("unchecked")
    public static Object[] convert(Object[] from, Class<?> toClass) {
        if (from == null) {
            return null;
        }
        else if (!toClass.isArray()) {
            return convert(from, Array.newInstance(toClass, 0).getClass());
        }
        else if (toClass == from.getClass()) {
            return from;
        }
        else {
            final Class<?> toComponentType = toClass.getComponentType();

            if (from.length == 0) {
                return Arrays.copyOf(from, from.length, (Class<? extends Object[]>) toClass);
            }
            else if (from[0].getClass() == toComponentType) {
                return Arrays.copyOf(from, from.length, (Class<? extends Object[]>) toClass);
            }
            else {
                final Object[] result = (Object[]) Array.newInstance(toComponentType, from.length);

                for (int i = 0; i < from.length; i++) {
                    result[i] = convert(from[i], toComponentType);
                }

                return result;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T convert(Object from, Class<?> toClass) {
        if (from == null) {
            return null;
        }
        else {
            final Class<?> fromClass = from.getClass();

            if (fromClass.isArray()) {
                return (T) convert((Object[]) from, toClass);
            }
            else if (toClass == fromClass) {
                return (T) from;
            }

            // All types can be converted into String
            else if (toClass == String.class) {
                return (T) from.toString();
            }

            // Various number types are converted between each other via String
            else if (toClass == Byte.class) {
                return (T) Byte.valueOf(from.toString());
            }
            else if (toClass == Short.class) {
                return (T) Short.valueOf(from.toString());
            }
            else if (toClass == Integer.class) {
                return (T) Integer.valueOf(from.toString());
            }
            else if (toClass == Long.class) {
                return (T) Long.valueOf(from.toString());
            }
            else if (toClass == Float.class) {
                return (T) Float.valueOf(from.toString());
            }
            else if (toClass == Double.class) {
                return (T) Double.valueOf(from.toString());
            }
            else if (toClass == BigDecimal.class) {
                return (T) new BigDecimal(from.toString());
            }
            else if (toClass == BigInteger.class) {
                return (T) new BigInteger(from.toString());
            }

            // Date types can be converted among each other
            else if (toClass == Date.class && java.util.Date.class.isAssignableFrom(fromClass)) {
                return (T) new Date(((java.util.Date) from).getTime());
            }
            else if (toClass == Time.class && java.util.Date.class.isAssignableFrom(fromClass)) {
                return (T) new Time(((java.util.Date) from).getTime());
            }
            else if (toClass == Timestamp.class && java.util.Date.class.isAssignableFrom(fromClass)) {
                return (T) new Timestamp(((java.util.Date) from).getTime());
            }
        }

        throw new SQLDialectNotSupportedException("Cannot convert from " + from + " to " + toClass);
    }

    private TypeUtils() {}
}
