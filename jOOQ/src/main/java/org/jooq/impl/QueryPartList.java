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

import static java.util.Arrays.asList;
import static org.jooq.impl.Utils.visitAll;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.jooq.BindContext;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.QueryPart;
import org.jooq.RenderContext;

/**
 * @author Lukas Eder
 */
class QueryPartList<T extends QueryPart> extends AbstractQueryPart implements List<T> {

    private static final long serialVersionUID = -2936922742534009564L;
    private final List<T>     wrappedList;

    QueryPartList() {
        this((Collection<T>) null);
    }

    QueryPartList(Collection<? extends T> wrappedList) {
        super();

        this.wrappedList = new ArrayList<T>();

        if (wrappedList != null) {
            addAll(wrappedList);
        }
    }

    QueryPartList(T... wrappedList) {
        this(asList(wrappedList));
    }

    
    public final void toSQL(RenderContext context) {

        // Some lists render different SQL when empty
        if (isEmpty()) {
//            if (clause != null && clause != DUMMY)
//                context.start(clause);

            toSQLEmptyList(context);

//            if (clause != null && clause != DUMMY)
//                context.end(clause);
        }

        else {
            String separator = "";
            boolean indent = (size() > 1);

            if (indent)
                context.formatIndentStart();

            for (T queryPart : this) {
                context.sql(separator);

                if (indent)
                    context.formatNewLine();

//                if (clause != null && clause != DUMMY)
//                    context.start(clause);

                context.visit(queryPart);

//                if (clause != null && clause != DUMMY)
//                    context.end(clause);

                separator = ", ";
            }

            if (indent)
                context.formatIndentEnd();
        }
    }

    
    public final void bind(BindContext context) {
        visitAll(context, wrappedList);
    }

    
    public final Clause[] clauses(Context<?> ctx) {
        return null;
    }

    /**
     * Subclasses may override this method
     */
    @SuppressWarnings("unused")
    protected void toSQLEmptyList(RenderContext context) {
    }



    // -------------------------------------------------------------------------
    // Implementations from the List API
    // -------------------------------------------------------------------------

    
    public final int size() {
        return wrappedList.size();
    }

    
    public final boolean isEmpty() {
        return wrappedList.isEmpty();
    }

    
    public final boolean contains(Object o) {
        return wrappedList.contains(o);
    }

    
    public final Iterator<T> iterator() {
        return wrappedList.iterator();
    }

    
    public final Object[] toArray() {
        return wrappedList.toArray();
    }

    
    public final <E> E[] toArray(E[] a) {
        return wrappedList.toArray(a);
    }

    
    public final boolean add(T e) {
        if (e != null) {
            return wrappedList.add(e);
        }

        return false;
    }

    
    public final boolean remove(Object o) {
        return wrappedList.remove(o);
    }

    
    public final boolean containsAll(Collection<?> c) {
        return wrappedList.containsAll(c);
    }

    
    public final boolean addAll(Collection<? extends T> c) {
        return wrappedList.addAll(removeNulls(c));
    }

    
    public final boolean addAll(int index, Collection<? extends T> c) {
        return wrappedList.addAll(index, removeNulls(c));
    }

    private final Collection<? extends T> removeNulls(Collection<? extends T> c) {

        // [#2145] Collections that contain nulls are quite rare, so it is wise
        // to add a relatively cheap defender check to avoid unnecessary loops
        if (c.contains(null)) {
            List<T> list = new ArrayList<T>(c);
            Iterator<T> it = list.iterator();

            while (it.hasNext()) {
                if (it.next() == null) {
                    it.remove();
                }
            }

            return list;
        }
        else {
            return c;
        }
    }

    
    public final boolean removeAll(Collection<?> c) {
        return wrappedList.removeAll(c);
    }

    
    public final boolean retainAll(Collection<?> c) {
        return wrappedList.retainAll(c);
    }

    
    public final void clear() {
        wrappedList.clear();
    }

    
    public final T get(int index) {
        return wrappedList.get(index);
    }

    
    public final T set(int index, T element) {
        if (element != null) {
            return wrappedList.set(index, element);
        }

        return null;
    }

    
    public final void add(int index, T element) {
        if (element != null) {
            wrappedList.add(index, element);
        }
    }

    
    public final T remove(int index) {
        return wrappedList.remove(index);
    }

    
    public final int indexOf(Object o) {
        return wrappedList.indexOf(o);
    }

    
    public final int lastIndexOf(Object o) {
        return wrappedList.lastIndexOf(o);
    }

    
    public final ListIterator<T> listIterator() {
        return wrappedList.listIterator();
    }

    
    public final ListIterator<T> listIterator(int index) {
        return wrappedList.listIterator(index);
    }

    
    public final List<T> subList(int fromIndex, int toIndex) {
        return wrappedList.subList(fromIndex, toIndex);
    }
}
