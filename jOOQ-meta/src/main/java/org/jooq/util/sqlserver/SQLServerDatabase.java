package org.jooq.util.sqlserver;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.jooq.impl.Factory;
import org.jooq.util.SequenceDefinition;
import org.jooq.util.ansi.ANSIDatabase;

/**
 * @author Lukas Eder
 */
public class SQLServerDatabase extends ANSIDatabase {

    @Override
    public Factory create() {
        return new SQLServerFactory(getConnection());
    }

    @Override
    protected List<SequenceDefinition> getSequences0() throws SQLException {
        return Collections.emptyList();
    }
}
