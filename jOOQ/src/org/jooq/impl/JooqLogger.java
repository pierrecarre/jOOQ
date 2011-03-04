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

import java.util.logging.Level;



/**
 * The jOOQ logger abstraction
 *
 * @author Lukas Eder
 */
public final class JooqLogger {

    private org.apache.log4j.Logger log4j;
    private java.util.logging.Logger util;

    public static JooqLogger getLogger(Class<?> clazz) {
        JooqLogger result = new JooqLogger();

        try {
            result.log4j = org.apache.log4j.Logger.getLogger(clazz);
        }

        // Log4j is not found on the classpath, so ignore most of logging
        catch (Throwable t) {
            result.util = java.util.logging.Logger.getLogger(clazz.getName());
        }

        return result;
    }

    public boolean isTraceEnabled() {
        if (log4j != null) {
            return log4j.isTraceEnabled();
        }
        else {
            return util.isLoggable(Level.FINER);
        }
    }

    public void trace(Object message) {
        if (log4j != null) {
            log4j.trace(message);
        }
        else {
            util.finer("" + message);
        }
    }

    public void trace(Object message, Throwable t) {
        if (log4j != null) {
            log4j.trace(message, t);
        }
        else {
            util.log(Level.FINER, "" + message, t);
        }
    }


    public boolean isDebugEnabled() {
        if (log4j != null) {
            return log4j.isDebugEnabled();
        }
        else {
            return util.isLoggable(Level.FINE);
        }
    }

    public void debug(Object message) {
        if (log4j != null) {
            log4j.debug(message);
        }
        else {
            util.fine("" + message);
        }
    }

    public void debug(Object message, Throwable t) {
        if (log4j != null) {
            log4j.debug(message, t);
        }
        else {
            util.log(Level.FINE, "" + message, t);
        }
    }


    public boolean isInfoEnabled() {
        if (log4j != null) {
            return log4j.isInfoEnabled();
        }
        else {
            return util.isLoggable(Level.INFO);
        }
    }

    public void info(Object message) {
        if (log4j != null) {
            log4j.info(message);
        }
        else {
            util.info("" + message);
        }
    }

    public void info(Object message, Throwable t) {
        if (log4j != null) {
            log4j.info(message, t);
        }
        else {
            util.log(Level.INFO, "" + message, t);
        }
    }


    public void warn(Object message) {
        if (log4j != null) {
            log4j.warn(message);
        }
        else {
            util.warning("" + message);
        }
    }

    public void warn(Object message, Throwable t) {
        if (log4j != null) {
            log4j.warn(message, t);
        }
        else {
            util.log(Level.WARNING, "" + message, t);
        }
    }


    public void error(Object message) {
        if (log4j != null) {
            log4j.error(message);
        }
        else {
            util.severe("" + message);
        }
    }

    public void error(Object message, Throwable t) {
        if (log4j != null) {
            log4j.error(message, t);
        }
        else {
            util.log(Level.SEVERE, "" + message, t);
        }
    }
}
