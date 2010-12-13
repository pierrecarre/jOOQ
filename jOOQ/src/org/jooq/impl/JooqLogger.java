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
package org.jooq.impl;

import org.apache.log4j.Logger;

/**
 * The jOOQ logger abstraction
 *
 * @author Lukas Eder
 */
public final class JooqLogger {

    private static boolean initialisationError = false;
    private Logger logger;

    public static JooqLogger getLogger(Class<?> clazz) {
        JooqLogger result = new JooqLogger();

        try {
            result.logger = Logger.getLogger(clazz);
        }

        // Log4j is not found on the classpath, so ignore most of logging
        catch (Throwable t) {
            if (!initialisationError) {
                initialisationError = true;
                result.error("JooqLogger could not initialise log4j logger. ERROR level logs will be output on stderr");
            }
        }

        return result;
    }

    public boolean isTraceEnabled() {
        if (logger != null) {
            return logger.isTraceEnabled();
        }
        else {
            return false;
        }
    }

    public void trace(Object message) {
        if (logger != null) {
            logger.trace(message);
        }
    }

    public void trace(Object message, Throwable t) {
        if (logger != null) {
            logger.trace(message, t);
        }
    }

    public boolean isDebugEnabled() {
        if (logger != null) {
            return logger.isDebugEnabled();
        }
        else {
            return false;
        }
    }

    public void debug(Object message) {
        if (logger != null) {
            logger.debug(message);
        }
    }

    public void debug(Object message, Throwable t) {
        if (logger != null) {
            logger.debug(message, t);
        }
    }

    public boolean isInfoEnabled() {
        if (logger != null) {
            return logger.isInfoEnabled();
        }
        else {
            return false;
        }
    }

    public void info(Object message) {
        if (logger != null) {
            logger.info(message);
        }
    }

    public void info(Object message, Throwable t) {
        if (logger != null) {
            logger.info(message, t);
        }
    }

    public void warn(Object message) {
        if (logger != null) {
            logger.warn(message);
        }
    }

    public void warn(Object message, Throwable t) {
        if (logger != null) {
            logger.warn(message, t);
        }
    }

    public void error(Object message) {
        if (logger != null) {
            logger.error(message);
        }
        else {
            System.err.println(message);
        }
    }

    public void error(Object message, Throwable t) {
        if (logger != null) {
            logger.error(message, t);
        }
        else {
            System.err.println(message);
            t.printStackTrace(System.err);
        }
    }
}
