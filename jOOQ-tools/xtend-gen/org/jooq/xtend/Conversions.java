package org.jooq.xtend;

import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.IntegerRange;
import org.jooq.Constants;
import org.jooq.xtend.Generators;

/**
 * @author Lukas Eder
 */
@SuppressWarnings("all")
public class Conversions extends Generators {
  public static void main(final String[] args) {
    Conversions _conversions = new Conversions();
    final Conversions conversions = _conversions;
    conversions.generateConversions();
  }
  
  public void generateConversions() {
    StringBuilder _stringBuilder = new StringBuilder();
    final StringBuilder out = _stringBuilder;
    StringConcatenation _builder = new StringConcatenation();
    {
      IntegerRange _upTo = new IntegerRange(1, Constants.MAX_ROW_DEGREE);
      for(final Integer degree : _upTo) {
        _builder.newLine();
        _builder.append("  ");
        _builder.append("/**");
        _builder.newLine();
        _builder.append("   ");
        _builder.append("* Enrich any {@link org.jooq.Record");
        _builder.append(degree, "   ");
        _builder.append("} with the {@link Tuple");
        _builder.append(degree, "   ");
        _builder.append("} case class");
        _builder.newLineIfNotEmpty();
        _builder.append("   ");
        _builder.append("*/");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("implicit def asTuple");
        _builder.append(degree, "  ");
        _builder.append("[");
        String _TN = this.TN((degree).intValue());
        _builder.append(_TN, "  ");
        _builder.append("](r : Record");
        _builder.append(degree, "  ");
        _builder.append("[");
        String _TN_1 = this.TN((degree).intValue());
        _builder.append(_TN_1, "  ");
        _builder.append("]): Tuple");
        _builder.append(degree, "  ");
        _builder.append("[");
        String _TN_2 = this.TN((degree).intValue());
        _builder.append(_TN_2, "  ");
        _builder.append("] = r match {");
        _builder.newLineIfNotEmpty();
        _builder.append("    ");
        _builder.append("case null => null");
        _builder.newLine();
        _builder.append("    ");
        _builder.append("case _ => Tuple");
        _builder.append(degree, "    ");
        _builder.append("(");
        {
          IntegerRange _upTo_1 = new IntegerRange(1, (degree).intValue());
          boolean _hasElements = false;
          for(final Integer d : _upTo_1) {
            if (!_hasElements) {
              _hasElements = true;
            } else {
              _builder.appendImmediate(", ", "    ");
            }
            _builder.append("r.value");
            _builder.append(d, "    ");
          }
        }
        _builder.append(")");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("}");
        _builder.newLine();
      }
    }
    out.append(_builder.toString());
    this.insert("org.jooq.scala.Conversions", out, "tuples");
  }
}
