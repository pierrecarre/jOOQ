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

import org.jooq.SQLDialect;

/**
 * The term dictionary enumerates standard expressions and their
 * dialect-specific variants if applicable
 *
 * @author Lukas Eder
 */
enum Term {

    ATAN2 {
        
        public String translate(SQLDialect dialect) {
            /* [pro] xx
            xxxxxx xxxxxxxxxxxxxxxxxx x
                xxxx xxxx
                xxxx xxxxxxxxxx
                    xxxxxx xxxxxxx
            x

            xx [/pro] */
            return "atan2";
        }
    },
    BIT_LENGTH {
        
        public String translate(SQLDialect dialect) {
            switch (dialect.family()) {
                /* [pro] xx
                xxxx xxxx
                    xxxxxx xx x xxxxxxxxxxxx

                xxxx xxxxxxxxxx
                    xxxxxx xx x xxxxx

                xxxx xxxxxxx
                    xxxxxx xx x xxxxxxxxx

                xxxx xxxx
                xxxx xxxxxxx
                xxxx xxxxxxx
                xx [/pro] */
                case DERBY:
                case SQLITE:
                    return "8 * length";
            }

            return "bit_length";
        }
    },
    CHAR_LENGTH {
        
        public String translate(SQLDialect dialect) {
            switch (dialect.family()) {
                /* [pro] xx
                xxxx xxxxxxxxxx
                    xxxxxx xxxxxx

                xxxx xxxx
                xxxx xxxxxxx
                xxxx xxxxxxx
                xxxx xxxxxxx
                xx [/pro] */
                case DERBY:
                case SQLITE:
                    return "length";
            }

            return "char_length";
        }
    },
    LIST_AGG {
        
        public String translate(SQLDialect dialect) {
            switch (dialect.family()) {
                /* [pro] xx
                xx xxx xxxxx xx xx xxxx xxxxxx xxxxxxx xxx xxxxxxxxxxxx xx
                xx xxxxxxx xxx xxxx xxxxxxxx xxxxxxxx xxxxxx xxxxxx xx xx
                xxxx xxxx
                    xxxxxx xxxxxxxxx

                xxxx xxxxxxx
                    xxxxxx xxxxxxxxxx

                xxxx xxxxxxx
                    xxxxxx xxxxxxx

                xx [/pro] */
                case CUBRID:
                case H2:
                case HSQLDB:
                case MARIADB:
                case MYSQL:
                    return "group_concat";

                case POSTGRES:
                    return "string_agg";
            }

            return "listagg";
        }
    },
    OCTET_LENGTH {
        
        public String translate(SQLDialect dialect) {
            switch (dialect.family()) {
                /* [pro] xx
                xxxx xxxxxxxxxx
                    xxxxxx xxxxxx

                xxxx xxxxxxx
                    xxxxxx xxxxxxxxxx

                xxxx xxxx
                xxxx xxxxxxx
                xxxx xxxxxxx
                xx [/pro] */
                case DERBY:
                case SQLITE:
                    return "length";
            }

            return "octet_length";
        }
    },
    ROW_NUMBER {
        
        public String translate(SQLDialect dialect) {
            switch (dialect.family()) {
                case HSQLDB:
                    return "rownum";
            }

            return "row_number";
        }
    },
    STDDEV_POP {
        
        public String translate(SQLDialect dialect) {
            /* [pro] xx
            xxxxxx xxxxxxxxxxxxxxxxxx x
                xxxx xxxx
                    xxxxxx xxxxxxxxx

                xxxx xxxxxxxxxx
                    xxxxxx xxxxxxxxx
            x

            xx [/pro] */
            return "stddev_pop";
        }
    },
    STDDEV_SAMP {
        
        public String translate(SQLDialect dialect) {
            /* [pro] xx
            xxxxxx xxxxxxxxxxxxxxxxxx x
                xxxx xxxx
                    xxxxxx xxxxxxxxx

                xxxx xxxxxxxxxx
                    xxxxxx xxxxxxxx
            x

            xx [/pro] */
            return "stddev_samp";
        }
    },
    VAR_POP {
        
        public String translate(SQLDialect dialect) {
            /* [pro] xx
            xxxxxx xxxxxxxxxxxxxxxxxx x
                xxxx xxxx
                    xxxxxx xxxxxxxxxxx

                xxxx xxxxxxxxxx
                    xxxxxx xxxxxxx
            x

            xx [/pro] */
            return "var_pop";
        }
    },
    VAR_SAMP {
        
        public String translate(SQLDialect dialect) {
            /* [pro] xx
            xxxxxx xxxxxxxxxxxxxxxxxx x
                xxxx xxxx
                    xxxxxx xxxxxxxxxxx

                xxxx xxxxxxxxxx
                    xxxxxx xxxxxx
            x

            xx [/pro] */
            return "var_samp";
        }
    },

    ;

    /**
     * Translate the term to its dialect-specific variant
     */
    abstract String translate(SQLDialect dialect);
}
