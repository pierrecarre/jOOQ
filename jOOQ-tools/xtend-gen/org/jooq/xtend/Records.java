package org.jooq.xtend;

import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.IntegerRange;
import org.jooq.Constants;
import org.jooq.xtend.Generators;

/**
 * @author Lukas Eder
 */
@SuppressWarnings("all")
public class Records extends Generators {
  public static void main(final String[] args) {
    Records _records = new Records();
    final Records records = _records;
    records.generateRecordClasses();
    records.generateRecordImpl();
  }
  
  public void generateRecordClasses() {
    IntegerRange _upTo = new IntegerRange(1, Constants.MAX_ROW_DEGREE);
    for (final Integer degree : _upTo) {
      {
        StringBuilder _stringBuilder = new StringBuilder();
        final StringBuilder out = _stringBuilder;
        StringConcatenation _builder = new StringConcatenation();
        CharSequence _classHeader = this.classHeader();
        _builder.append(_classHeader, "");
        _builder.newLineIfNotEmpty();
        _builder.append("package org.jooq;");
        _builder.newLine();
        _builder.newLine();
        _builder.append("import javax.annotation.Generated;");
        _builder.newLine();
        _builder.newLine();
        _builder.append("/**");
        _builder.newLine();
        _builder.append(" ");
        _builder.append("* A model type for a records with degree <code>");
        _builder.append(degree, " ");
        _builder.append("</code>");
        _builder.newLineIfNotEmpty();
        _builder.append(" ");
        _builder.append("*");
        _builder.newLine();
        _builder.append(" ");
        _builder.append("* @see Row");
        _builder.append(degree, " ");
        _builder.newLineIfNotEmpty();
        _builder.append(" ");
        _builder.append("* @author Lukas Eder");
        _builder.newLine();
        _builder.append(" ");
        _builder.append("*/");
        _builder.newLine();
        CharSequence _generatedAnnotation = this.generatedAnnotation();
        _builder.append(_generatedAnnotation, "");
        _builder.newLineIfNotEmpty();
        _builder.append("public interface Record");
        _builder.append(degree, "");
        _builder.append("<");
        String _TN = this.TN((degree).intValue());
        _builder.append(_TN, "");
        _builder.append("> extends Record {");
        _builder.newLineIfNotEmpty();
        _builder.newLine();
        _builder.append("    ");
        _builder.append("// ------------------------------------------------------------------------");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("// Row value expressions");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("// ------------------------------------------------------------------------");
        _builder.newLine();
        _builder.newLine();
        _builder.append("    ");
        _builder.append("/**");
        _builder.newLine();
        _builder.append("     ");
        _builder.append("* Get this record\'s fields as a {@link Row");
        _builder.append(degree, "     ");
        _builder.append("}");
        _builder.newLineIfNotEmpty();
        _builder.append("     ");
        _builder.append("*/");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("Row");
        _builder.append(degree, "    ");
        _builder.append("<");
        String _TN_1 = this.TN((degree).intValue());
        _builder.append(_TN_1, "    ");
        _builder.append("> fieldsRow();");
        _builder.newLineIfNotEmpty();
        _builder.newLine();
        _builder.append("    ");
        _builder.append("/**");
        _builder.newLine();
        _builder.append("     ");
        _builder.append("* Get this record\'s values as a {@link Row");
        _builder.append(degree, "     ");
        _builder.append("}");
        _builder.newLineIfNotEmpty();
        _builder.append("     ");
        _builder.append("*/");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("Row");
        _builder.append(degree, "    ");
        _builder.append("<");
        String _TN_2 = this.TN((degree).intValue());
        _builder.append(_TN_2, "    ");
        _builder.append("> valuesRow();");
        _builder.newLineIfNotEmpty();
        _builder.newLine();
        _builder.append("    ");
        _builder.append("// ------------------------------------------------------------------------");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("// Field accessors");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("// ------------------------------------------------------------------------");
        _builder.newLine();
        {
          IntegerRange _upTo_1 = new IntegerRange(1, (degree).intValue());
          for(final Integer d : _upTo_1) {
            _builder.newLine();
            _builder.append("    ");
            _builder.append("/**");
            _builder.newLine();
            _builder.append("    ");
            _builder.append(" ");
            _builder.append("* Get the ");
            String _first = this.first((d).intValue());
            _builder.append(_first, "     ");
            _builder.append(" field");
            _builder.newLineIfNotEmpty();
            _builder.append("    ");
            _builder.append(" ");
            _builder.append("*/");
            _builder.newLine();
            _builder.append("    ");
            _builder.append("Field<T");
            _builder.append(d, "    ");
            _builder.append("> field");
            _builder.append(d, "    ");
            _builder.append("();");
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.newLine();
        _builder.append("    ");
        _builder.append("// ------------------------------------------------------------------------");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("// Value accessors");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("// ------------------------------------------------------------------------");
        _builder.newLine();
        {
          IntegerRange _upTo_2 = new IntegerRange(1, (degree).intValue());
          for(final Integer d_1 : _upTo_2) {
            _builder.newLine();
            _builder.append("    ");
            _builder.append("/**");
            _builder.newLine();
            _builder.append("    ");
            _builder.append(" ");
            _builder.append("* Get the ");
            String _first_1 = this.first((d_1).intValue());
            _builder.append(_first_1, "     ");
            _builder.append(" value");
            _builder.newLineIfNotEmpty();
            _builder.append("    ");
            _builder.append(" ");
            _builder.append("*/");
            _builder.newLine();
            _builder.append("    ");
            _builder.append("T");
            _builder.append(d_1, "    ");
            _builder.append(" value");
            _builder.append(d_1, "    ");
            _builder.append("();");
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.newLine();
        _builder.append("}");
        _builder.newLine();
        out.append(_builder.toString());
        String _plus = ("org.jooq.Record" + degree);
        this.write(_plus, out);
      }
    }
  }
  
  public void generateRecordImpl() {
    StringBuilder _stringBuilder = new StringBuilder();
    final StringBuilder out = _stringBuilder;
    StringConcatenation _builder = new StringConcatenation();
    CharSequence _classHeader = this.classHeader();
    _builder.append(_classHeader, "");
    _builder.newLineIfNotEmpty();
    _builder.append("package org.jooq.impl;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import static org.jooq.impl.Factory.vals;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import java.util.List;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import javax.annotation.Generated;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("import org.jooq.Field;");
    _builder.newLine();
    _builder.append("import org.jooq.FieldProvider;");
    _builder.newLine();
    _builder.append("import org.jooq.Record;");
    _builder.newLine();
    {
      IntegerRange _upTo = new IntegerRange(1, Constants.MAX_ROW_DEGREE);
      for(final Integer degree : _upTo) {
        _builder.append("import org.jooq.Record");
        _builder.append(degree, "");
        _builder.append(";");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.newLine();
    _builder.append("/**");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* A general purpose record, typically used for ad-hoc types.");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* <p>");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* This type implements both the general-purpose, type-unsafe {@link Record}");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* interface, as well as the more specific, type-safe {@link Record1},");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* {@link Record2} through {@link Record");
    _builder.append(Constants.MAX_ROW_DEGREE, " ");
    _builder.append("} interfaces");
    _builder.newLineIfNotEmpty();
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
    _builder.append("@SuppressWarnings({ \"unchecked\", \"rawtypes\" })");
    _builder.newLine();
    _builder.append("class RecordImpl<");
    String _TN = this.TN(Constants.MAX_ROW_DEGREE);
    _builder.append(_TN, "");
    _builder.append("> extends AbstractRecord");
    _builder.newLineIfNotEmpty();
    _builder.append("implements");
    _builder.newLine();
    _builder.newLine();
    _builder.append("    ");
    _builder.append("// This record implementation implements all record types. Type-safety is");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("// being checked through the type-safe API. No need for further checks here");
    _builder.newLine();
    {
      IntegerRange _upTo_1 = new IntegerRange(1, Constants.MAX_ROW_DEGREE);
      boolean _hasElements = false;
      for(final Integer degree_1 : _upTo_1) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          _builder.appendImmediate(",", "    ");
        }
        _builder.append("    ");
        _builder.append("Record");
        _builder.append(degree_1, "    ");
        _builder.append("<");
        String _TN_1 = this.TN((degree_1).intValue());
        _builder.append(_TN_1, "    ");
        _builder.append(">");
        {
          boolean _equals = ((degree_1).intValue() == Constants.MAX_ROW_DEGREE);
          if (_equals) {
            _builder.append(" {");
          }
        }
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.newLine();
    _builder.append("    ");
    _builder.append("/**");
    _builder.newLine();
    _builder.append("     ");
    _builder.append("* Generated UID");
    _builder.newLine();
    _builder.append("     ");
    _builder.append("*/");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("private static final long serialVersionUID = -2201346180421463830L;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("    ");
    _builder.append("/**");
    _builder.newLine();
    _builder.append("     ");
    _builder.append("* Create a new general purpos record");
    _builder.newLine();
    _builder.append("     ");
    _builder.append("*/");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("public RecordImpl(FieldProvider fields) {");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("super(fields);");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("    ");
    _builder.append("// ------------------------------------------------------------------------");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("// XXX: Type-safe Record APIs");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("// ------------------------------------------------------------------------");
    _builder.newLine();
    _builder.newLine();
    _builder.append("    ");
    _builder.append("@Override");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("public RowImpl<");
    String _TN_2 = this.TN(Constants.MAX_ROW_DEGREE);
    _builder.append(_TN_2, "    ");
    _builder.append("> fieldsRow() {");
    _builder.newLineIfNotEmpty();
    _builder.append("        ");
    _builder.append("return new RowImpl(getFields());");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("    ");
    _builder.append("@Override");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("public final RowImpl<");
    String _TN_3 = this.TN(Constants.MAX_ROW_DEGREE);
    _builder.append(_TN_3, "    ");
    _builder.append("> valuesRow() {");
    _builder.newLineIfNotEmpty();
    _builder.append("        ");
    _builder.append("List<Field<?>> fields = getFields();");
    _builder.newLine();
    _builder.append("        ");
    _builder.append("return new RowImpl(vals(intoArray(), fields.toArray(new Field[fields.size()])));");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("}");
    _builder.newLine();
    {
      IntegerRange _upTo_2 = new IntegerRange(1, Constants.MAX_ROW_DEGREE);
      for(final Integer degree_2 : _upTo_2) {
        _builder.newLine();
        _builder.append("    ");
        _builder.append("@Override");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("public final Field<T");
        _builder.append(degree_2, "    ");
        _builder.append("> field");
        _builder.append(degree_2, "    ");
        _builder.append("() {");
        _builder.newLineIfNotEmpty();
        _builder.append("    ");
        _builder.append("    ");
        _builder.append("return (Field<T");
        _builder.append(degree_2, "        ");
        _builder.append(">) getField(");
        int _minus = ((degree_2).intValue() - 1);
        _builder.append(_minus, "        ");
        _builder.append(");");
        _builder.newLineIfNotEmpty();
        _builder.append("    ");
        _builder.append("}");
        _builder.newLine();
      }
    }
    {
      IntegerRange _upTo_3 = new IntegerRange(1, Constants.MAX_ROW_DEGREE);
      for(final Integer degree_3 : _upTo_3) {
        _builder.newLine();
        _builder.append("    ");
        _builder.append("@Override");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("public final T");
        _builder.append(degree_3, "    ");
        _builder.append(" value");
        _builder.append(degree_3, "    ");
        _builder.append("() {");
        _builder.newLineIfNotEmpty();
        _builder.append("    ");
        _builder.append("    ");
        _builder.append("return (T");
        _builder.append(degree_3, "        ");
        _builder.append(") getValue(");
        int _minus_1 = ((degree_3).intValue() - 1);
        _builder.append(_minus_1, "        ");
        _builder.append(");");
        _builder.newLineIfNotEmpty();
        _builder.append("    ");
        _builder.append("}");
        _builder.newLine();
      }
    }
    _builder.append("}");
    _builder.newLine();
    out.append(_builder.toString());
    this.write("org.jooq.impl.RecordImpl", out);
  }
}
