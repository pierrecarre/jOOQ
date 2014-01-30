/**
 * Copyright (c) 2009-2013, Data Geekery GmbH (http://www.datageekery.com)
 * All rights reserved.
 *
 * This work is dual-licensed
 * - under the Apache Software License 2.0 (the "ASL")
 * - under the jOOQ License and Maintenance Agreement (the "jOOQ License")
 * =============================================================================
 * You may choose which license applies to you:
 *
 * - If you're using this work with Open Source databases, you may choose
 *   either ASL or jOOQ License.
 * - If you're using this work with at least one commercial database, you must
 *   choose jOOQ License
 *
 * For more information, please visit http://www.jooq.org/licenses
 *
 * Apache Software License 2.0:
 * -----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * jOOQ License and Maintenance Agreement:
 * -----------------------------------------------------------------------------
 * Data Geekery grants the Customer the non-exclusive, timely limited and
 * non-transferable license to install and use the Software under the terms of
 * the jOOQ License and Maintenance Agreement.
 *
 * This library is distributed with a LIMITED WARRANTY. See the jOOQ License
 * and Maintenance Agreement for more details: http://www.jooq.org/licensing
 */
package org.jooq.impl;

import org.jooq.RecordContext;
import org.jooq.RecordListener;

/**
 * A publicly available default implementation of {@link RecordListener}.
 * <p>
 * Use this to stay compatible with future API changes (i.e. added methods to
 * <code>RecordListener</code>)
 *
 * @author Lukas Eder
 */
public class DefaultRecordListener implements RecordListener {

    
    public void storeStart(RecordContext ctx) {}

    
    public void storeEnd(RecordContext ctx) {}

    
    public void insertStart(RecordContext ctx) {}

    
    public void insertEnd(RecordContext ctx) {}

    
    public void updateStart(RecordContext ctx) {}

    
    public void updateEnd(RecordContext ctx) {}

    
    public void deleteStart(RecordContext ctx) {}

    
    public void deleteEnd(RecordContext ctx) {}

    
    public void loadStart(RecordContext ctx) {}

    
    public void loadEnd(RecordContext ctx) {}

    
    public void refreshStart(RecordContext ctx) {}

    
    public void refreshEnd(RecordContext ctx) {}

}
