package org.jooq.xtend;

import org.jooq.xtend.BetweenAndSteps;
import org.jooq.xtend.Conversions;
import org.jooq.xtend.Factory;
import org.jooq.xtend.Records;
import org.jooq.xtend.Rows;
import org.jooq.xtend.Update;

@SuppressWarnings("all")
public class GenerateAll {
  public static void main(final String[] args) {
    BetweenAndSteps.main(args);
    Conversions.main(args);
    Factory.main(args);
    Records.main(args);
    Rows.main(args);
    Update.main(args);
  }
}
