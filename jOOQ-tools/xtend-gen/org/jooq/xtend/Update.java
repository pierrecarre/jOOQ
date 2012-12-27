package org.jooq.xtend;

import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.IntegerRange;
import org.jooq.Constants;
import org.jooq.xtend.Generators;

/**
 * @author Lukas Eder
 */
@SuppressWarnings("all")
public class Update extends Generators {
  public static void main(final String[] args) {
    Update _update = new Update();
    final Update update = _update;
    update.generateUpdateQuery();
    update.generateUpdateQueryImpl();
    update.generateUpdateSetFirstStep();
    update.generateUpdateImpl();
  }
  
  public void generateUpdateQuery() {
    StringBuilder _stringBuilder = new StringBuilder();
    final StringBuilder out = _stringBuilder;
    StringConcatenation _builder = new StringConcatenation();
    {
      IntegerRange _upTo = new IntegerRange(1, Constants.MAX_ROW_DEGREE);
      for(final Integer degree : _upTo) {
        _builder.newLine();
        _builder.append("    ");
        _builder.append("/**");
        _builder.newLine();
        _builder.append("     ");
        _builder.append("* Specify a multi-column set clause for the <code>UPDATE</code> statement.");
        _builder.newLine();
        _builder.append("     ");
        _builder.append("*/");
        _builder.newLine();
        _builder.append("    ");
        CharSequence _generatedMethod = this.generatedMethod();
        _builder.append(_generatedMethod, "    ");
        _builder.newLineIfNotEmpty();
        _builder.append("    ");
        _builder.append("@Support({ DB2, H2, HSQLDB, INGRES, ORACLE, POSTGRES })");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("<");
        String _TN = this.TN((degree).intValue());
        _builder.append(_TN, "    ");
        _builder.append("> void addValues(Row");
        _builder.append(degree, "    ");
        _builder.append("<");
        String _TN_1 = this.TN((degree).intValue());
        _builder.append(_TN_1, "    ");
        _builder.append("> row, Row");
        _builder.append(degree, "    ");
        _builder.append("<");
        String _TN_2 = this.TN((degree).intValue());
        _builder.append(_TN_2, "    ");
        _builder.append("> value);");
        _builder.newLineIfNotEmpty();
      }
    }
    {
      IntegerRange _upTo_1 = new IntegerRange(1, Constants.MAX_ROW_DEGREE);
      for(final Integer degree_1 : _upTo_1) {
        _builder.newLine();
        _builder.append("    ");
        _builder.append("/**");
        _builder.newLine();
        _builder.append("     ");
        _builder.append("* Specify a multi-column set clause for the <code>UPDATE</code> statement.");
        _builder.newLine();
        _builder.append("     ");
        _builder.append("*/");
        _builder.newLine();
        _builder.append("    ");
        CharSequence _generatedMethod_1 = this.generatedMethod();
        _builder.append(_generatedMethod_1, "    ");
        _builder.newLineIfNotEmpty();
        _builder.append("    ");
        _builder.append("@Support({ DB2, H2, HSQLDB, INGRES, ORACLE })");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("<");
        String _TN_3 = this.TN((degree_1).intValue());
        _builder.append(_TN_3, "    ");
        _builder.append("> void addValues(Row");
        _builder.append(degree_1, "    ");
        _builder.append("<");
        String _TN_4 = this.TN((degree_1).intValue());
        _builder.append(_TN_4, "    ");
        _builder.append("> row, Select<? extends Record");
        _builder.append(degree_1, "    ");
        _builder.append("<");
        String _TN_5 = this.TN((degree_1).intValue());
        _builder.append(_TN_5, "    ");
        _builder.append(">> select);");
        _builder.newLineIfNotEmpty();
      }
    }
    out.append(_builder.toString());
    this.insert("org.jooq.UpdateQuery", out, "addValues");
  }
  
  public void generateUpdateQueryImpl() {
    StringBuilder _stringBuilder = new StringBuilder();
    final StringBuilder out = _stringBuilder;
    StringConcatenation _builder = new StringConcatenation();
    {
      IntegerRange _upTo = new IntegerRange(1, Constants.MAX_ROW_DEGREE);
      for(final Integer degree : _upTo) {
        _builder.newLine();
        _builder.append("    ");
        CharSequence _generatedMethod = this.generatedMethod();
        _builder.append(_generatedMethod, "    ");
        _builder.newLineIfNotEmpty();
        _builder.append("    ");
        _builder.append("@Override");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("public final <");
        String _TN = this.TN((degree).intValue());
        _builder.append(_TN, "    ");
        _builder.append("> void addValues(Row");
        _builder.append(degree, "    ");
        _builder.append("<");
        String _TN_1 = this.TN((degree).intValue());
        _builder.append(_TN_1, "    ");
        _builder.append("> row, Row");
        _builder.append(degree, "    ");
        _builder.append("<");
        String _TN_2 = this.TN((degree).intValue());
        _builder.append(_TN_2, "    ");
        _builder.append("> value) {");
        _builder.newLineIfNotEmpty();
        _builder.append("        ");
        _builder.append("addValues0(row, value);");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("}");
        _builder.newLine();
      }
    }
    {
      IntegerRange _upTo_1 = new IntegerRange(1, Constants.MAX_ROW_DEGREE);
      for(final Integer degree_1 : _upTo_1) {
        _builder.newLine();
        _builder.append("    ");
        CharSequence _generatedMethod_1 = this.generatedMethod();
        _builder.append(_generatedMethod_1, "    ");
        _builder.newLineIfNotEmpty();
        _builder.append("    ");
        _builder.append("@Override");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("public final <");
        String _TN_3 = this.TN((degree_1).intValue());
        _builder.append(_TN_3, "    ");
        _builder.append("> void addValues(Row");
        _builder.append(degree_1, "    ");
        _builder.append("<");
        String _TN_4 = this.TN((degree_1).intValue());
        _builder.append(_TN_4, "    ");
        _builder.append("> row, Select<? extends Record");
        _builder.append(degree_1, "    ");
        _builder.append("<");
        String _TN_5 = this.TN((degree_1).intValue());
        _builder.append(_TN_5, "    ");
        _builder.append(">> select) {");
        _builder.newLineIfNotEmpty();
        _builder.append("        ");
        _builder.append("addValues0(row, select);");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("}");
        _builder.newLine();
      }
    }
    out.append(_builder.toString());
    this.insert("org.jooq.impl.UpdateQueryImpl", out, "addValues");
  }
  
  public void generateUpdateSetFirstStep() {
    StringBuilder _stringBuilder = new StringBuilder();
    final StringBuilder out = _stringBuilder;
    StringConcatenation _builder = new StringConcatenation();
    CharSequence _classHeader = this.classHeader();
    _builder.append(_classHeader, "");
    _builder.newLineIfNotEmpty();
    _builder.append("package org.jooq;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import static org.jooq.SQLDialect.DB2;");
    _builder.newLine();
    _builder.append("import static org.jooq.SQLDialect.H2;");
    _builder.newLine();
    _builder.append("import static org.jooq.SQLDialect.HSQLDB;");
    _builder.newLine();
    _builder.append("import static org.jooq.SQLDialect.INGRES;");
    _builder.newLine();
    _builder.append("import static org.jooq.SQLDialect.ORACLE;");
    _builder.newLine();
    _builder.append("import static org.jooq.SQLDialect.POSTGRES;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import javax.annotation.Generated;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("/**");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* This type is used for the {@link Update}\'s DSL API.");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* <p>");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* Example: <code><pre>");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* Executor create = new Executor(connection, dialect);");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* create.update(table)");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*       .set(field1, value1)");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*       .set(field2, value2)");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*       .where(field1.greaterThan(100))");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*       .execute();");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* </pre></code>");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* @author Lukas Eder");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*/");
    _builder.newLine();
    CharSequence _generatedAnnotation = this.generatedAnnotation();
    _builder.append(_generatedAnnotation, "");
    _builder.newLineIfNotEmpty();
    _builder.append("public interface UpdateSetFirstStep<R extends Record> extends UpdateSetStep<R> {");
    _builder.newLine();
    {
      IntegerRange _upTo = new IntegerRange(1, Constants.MAX_ROW_DEGREE);
      for(final Integer degree : _upTo) {
        _builder.newLine();
        _builder.append("    ");
        _builder.append("/**");
        _builder.newLine();
        _builder.append("     ");
        _builder.append("* Specify a multi-column set clause for the <code>UPDATE</code> statement.");
        _builder.newLine();
        _builder.append("     ");
        _builder.append("* <p>");
        _builder.newLine();
        _builder.append("     ");
        _builder.append("* This is simulated using a subquery for the <code>value</code>, where row");
        _builder.newLine();
        _builder.append("     ");
        _builder.append("* value expressions aren\'t supported.");
        _builder.newLine();
        _builder.append("     ");
        _builder.append("*/");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("@Support({ DB2, H2, HSQLDB, INGRES, ORACLE, POSTGRES })");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("<");
        String _TN = this.TN((degree).intValue());
        _builder.append(_TN, "    ");
        _builder.append("> UpdateWhereStep<R> set(Row");
        _builder.append(degree, "    ");
        _builder.append("<");
        String _TN_1 = this.TN((degree).intValue());
        _builder.append(_TN_1, "    ");
        _builder.append("> row, Row");
        _builder.append(degree, "    ");
        _builder.append("<");
        String _TN_2 = this.TN((degree).intValue());
        _builder.append(_TN_2, "    ");
        _builder.append("> value);");
        _builder.newLineIfNotEmpty();
      }
    }
    {
      IntegerRange _upTo_1 = new IntegerRange(1, Constants.MAX_ROW_DEGREE);
      for(final Integer degree_1 : _upTo_1) {
        _builder.newLine();
        _builder.append("    ");
        _builder.append("/**");
        _builder.newLine();
        _builder.append("     ");
        _builder.append("* Specify a multi-column set clause for the <code>UPDATE</code> statement.");
        _builder.newLine();
        _builder.append("     ");
        _builder.append("*/");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("@Support({ DB2, H2, HSQLDB, INGRES, ORACLE })");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("<");
        String _TN_3 = this.TN((degree_1).intValue());
        _builder.append(_TN_3, "    ");
        _builder.append("> UpdateWhereStep<R> set(Row");
        _builder.append(degree_1, "    ");
        _builder.append("<");
        String _TN_4 = this.TN((degree_1).intValue());
        _builder.append(_TN_4, "    ");
        _builder.append("> row, Select<? extends Record");
        _builder.append(degree_1, "    ");
        _builder.append("<");
        String _TN_5 = this.TN((degree_1).intValue());
        _builder.append(_TN_5, "    ");
        _builder.append(">> select);");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    out.append(_builder.toString());
    this.write("org.jooq.UpdateSetFirstStep", out);
  }
  
  public void generateUpdateImpl() {
    StringBuilder _stringBuilder = new StringBuilder();
    final StringBuilder out = _stringBuilder;
    StringConcatenation _builder = new StringConcatenation();
    {
      IntegerRange _upTo = new IntegerRange(1, Constants.MAX_ROW_DEGREE);
      for(final Integer degree : _upTo) {
        _builder.newLine();
        _builder.append("    ");
        CharSequence _generatedMethod = this.generatedMethod();
        _builder.append(_generatedMethod, "    ");
        _builder.newLineIfNotEmpty();
        _builder.append("    ");
        _builder.append("@Override");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("public final <");
        String _TN = this.TN((degree).intValue());
        _builder.append(_TN, "    ");
        _builder.append("> UpdateWhereStep<R> set(Row");
        _builder.append(degree, "    ");
        _builder.append("<");
        String _TN_1 = this.TN((degree).intValue());
        _builder.append(_TN_1, "    ");
        _builder.append("> row, Row");
        _builder.append(degree, "    ");
        _builder.append("<");
        String _TN_2 = this.TN((degree).intValue());
        _builder.append(_TN_2, "    ");
        _builder.append("> value) {");
        _builder.newLineIfNotEmpty();
        _builder.append("        ");
        _builder.append("getDelegate().addValues(row, value);");
        _builder.newLine();
        _builder.append("        ");
        _builder.append("return this;");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("}");
        _builder.newLine();
      }
    }
    {
      IntegerRange _upTo_1 = new IntegerRange(1, Constants.MAX_ROW_DEGREE);
      for(final Integer degree_1 : _upTo_1) {
        _builder.newLine();
        _builder.append("    ");
        CharSequence _generatedMethod_1 = this.generatedMethod();
        _builder.append(_generatedMethod_1, "    ");
        _builder.newLineIfNotEmpty();
        _builder.append("    ");
        _builder.append("@Override");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("public final <");
        String _TN_3 = this.TN((degree_1).intValue());
        _builder.append(_TN_3, "    ");
        _builder.append("> UpdateWhereStep<R> set(Row");
        _builder.append(degree_1, "    ");
        _builder.append("<");
        String _TN_4 = this.TN((degree_1).intValue());
        _builder.append(_TN_4, "    ");
        _builder.append("> row, Select<? extends Record");
        _builder.append(degree_1, "    ");
        _builder.append("<");
        String _TN_5 = this.TN((degree_1).intValue());
        _builder.append(_TN_5, "    ");
        _builder.append(">> select) {");
        _builder.newLineIfNotEmpty();
        _builder.append("        ");
        _builder.append("getDelegate().addValues(row, select);");
        _builder.newLine();
        _builder.append("        ");
        _builder.append("return this;");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("}");
        _builder.newLine();
      }
    }
    out.append(_builder.toString());
    this.insert("org.jooq.impl.UpdateImpl", out, "set");
  }
}
