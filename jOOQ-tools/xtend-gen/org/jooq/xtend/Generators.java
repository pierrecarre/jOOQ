package org.jooq.xtend;

import com.google.common.base.Objects;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IntegerRange;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.jooq.xtend.Util;

/**
 * @author Lukas Eder
 */
@SuppressWarnings("all")
public abstract class Generators {
  public File file(final String className) {
    boolean _contains = className.contains("scala");
    if (_contains) {
      String _replace = className.replace(".", "/");
      String _plus = ("./../jOOQ-scala/src/main/scala/" + _replace);
      String _plus_1 = (_plus + ".scala");
      File _file = new File(_plus_1);
      return _file;
    } else {
      String _replace_1 = className.replace(".", "/");
      String _plus_2 = ("./../jOOQ/src/main/java/" + _replace_1);
      String _plus_3 = (_plus_2 + ".java");
      File _file_1 = new File(_plus_3);
      return _file_1;
    }
  }
  
  public String read(final String className) {
    final File file = this.file(className);
    try {
      RandomAccessFile _randomAccessFile = new RandomAccessFile(file, "r");
      final RandomAccessFile f = _randomAccessFile;
      long _length = f.length();
      final byte[] contents = Util.newByteArray(_length);
      f.readFully(contents);
      String _string = new String(contents);
      return _string;
    } catch (final Throwable _t) {
      if (_t instanceof IOException) {
        final IOException e = (IOException)_t;
        e.printStackTrace();
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
    return null;
  }
  
  public void insert(final String className, final CharSequence contents, final String section) {
    StringBuilder _stringBuilder = new StringBuilder();
    final StringBuilder result = _stringBuilder;
    final String original = this.read(className);
    String _plus = ("// [jooq-tools] START [" + section);
    final String start = (_plus + "]");
    String _plus_1 = ("// [jooq-tools] END [" + section);
    final String end = (_plus_1 + "]");
    int _indexOf = original.indexOf(start);
    int _length = start.length();
    int _plus_2 = (_indexOf + _length);
    int _plus_3 = (_plus_2 + 1);
    String _substring = original.substring(0, _plus_3);
    result.append(_substring);
    result.append(contents);
    result.append("\n");
    int _indexOf_1 = original.indexOf(end);
    String _substring_1 = original.substring(_indexOf_1);
    result.append(_substring_1);
    this.write(className, result);
  }
  
  public void write(final String className, final CharSequence contents) {
    final File file = this.file(className);
    File _parentFile = file.getParentFile();
    _parentFile.mkdirs();
    try {
      String _plus = ("Generating " + file);
      System.out.println(_plus);
      FileWriter _fileWriter = new FileWriter(file);
      final FileWriter fw = _fileWriter;
      fw.append(contents);
      fw.flush();
      fw.close();
    } catch (final Throwable _t) {
      if (_t instanceof IOException) {
        final IOException e = (IOException)_t;
        e.printStackTrace();
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
  
  public String first(final int degree) {
    String _switchResult = null;
    boolean _matched = false;
    if (!_matched) {
      if (Objects.equal(degree,1)) {
        _matched=true;
        _switchResult = "first";
      }
    }
    if (!_matched) {
      if (Objects.equal(degree,2)) {
        _matched=true;
        _switchResult = "second";
      }
    }
    if (!_matched) {
      if (Objects.equal(degree,3)) {
        _matched=true;
        _switchResult = "third";
      }
    }
    if (!_matched) {
      if (Objects.equal(degree,4)) {
        _matched=true;
        _switchResult = "fourth";
      }
    }
    if (!_matched) {
      if (Objects.equal(degree,5)) {
        _matched=true;
        _switchResult = "fifth";
      }
    }
    if (!_matched) {
      if (Objects.equal(degree,6)) {
        _matched=true;
        _switchResult = "sixth";
      }
    }
    if (!_matched) {
      if (Objects.equal(degree,7)) {
        _matched=true;
        _switchResult = "seventh";
      }
    }
    if (!_matched) {
      if (Objects.equal(degree,8)) {
        _matched=true;
        _switchResult = "eighth";
      }
    }
    if (!_matched) {
      if (Objects.equal(degree,9)) {
        _matched=true;
        _switchResult = "ninth";
      }
    }
    if (!_matched) {
      if (Objects.equal(degree,10)) {
        _matched=true;
        _switchResult = "tenth";
      }
    }
    if (!_matched) {
      if (Objects.equal(degree,11)) {
        _matched=true;
        _switchResult = "eleventh";
      }
    }
    if (!_matched) {
      if (Objects.equal(degree,12)) {
        _matched=true;
        _switchResult = "twelfth";
      }
    }
    if (!_matched) {
      if (Objects.equal(degree,13)) {
        _matched=true;
        _switchResult = "thirteenth";
      }
    }
    if (!_matched) {
      if (Objects.equal(degree,14)) {
        _matched=true;
        _switchResult = "fourteenth";
      }
    }
    if (!_matched) {
      if (Objects.equal(degree,15)) {
        _matched=true;
        _switchResult = "fifteenth";
      }
    }
    if (!_matched) {
      if (Objects.equal(degree,16)) {
        _matched=true;
        _switchResult = "sixteenth";
      }
    }
    if (!_matched) {
      if (Objects.equal(degree,17)) {
        _matched=true;
        _switchResult = "seventeenth";
      }
    }
    if (!_matched) {
      if (Objects.equal(degree,18)) {
        _matched=true;
        _switchResult = "eighteenth";
      }
    }
    if (!_matched) {
      if (Objects.equal(degree,19)) {
        _matched=true;
        _switchResult = "ninteenth";
      }
    }
    if (!_matched) {
      if (Objects.equal(degree,20)) {
        _matched=true;
        _switchResult = "twentieth";
      }
    }
    if (!_matched) {
      if (Objects.equal(degree,21)) {
        _matched=true;
        _switchResult = "twenty-first";
      }
    }
    if (!_matched) {
      if (Objects.equal(degree,22)) {
        _matched=true;
        _switchResult = "twenty-second";
      }
    }
    if (!_matched) {
      int _modulo = (degree % 10);
      boolean _equals = (_modulo == 1);
      if (_equals) {
        _matched=true;
        String _plus = (Integer.valueOf(degree) + "st");
        _switchResult = _plus;
      }
    }
    if (!_matched) {
      int _modulo_1 = (degree % 10);
      boolean _equals_1 = (_modulo_1 == 2);
      if (_equals_1) {
        _matched=true;
        String _plus_1 = (Integer.valueOf(degree) + "nd");
        _switchResult = _plus_1;
      }
    }
    if (!_matched) {
      int _modulo_2 = (degree % 10);
      boolean _equals_2 = (_modulo_2 == 3);
      if (_equals_2) {
        _matched=true;
        String _plus_2 = (Integer.valueOf(degree) + "rd");
        _switchResult = _plus_2;
      }
    }
    if (!_matched) {
      String _plus_3 = (Integer.valueOf(degree) + "th");
      _switchResult = _plus_3;
    }
    return _switchResult;
  }
  
  public CharSequence classHeader() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("/**");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* Copyright (c) 2009-2012, Lukas Eder, lukas.eder@gmail.com");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* All rights reserved.");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* This software is licensed to you under the Apache License, Version 2.0");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* (the \"License\"); You may obtain a copy of the License at");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*   http://www.apache.org/licenses/LICENSE-2.0");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* Redistribution and use in source and binary forms, with or without");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* modification, are permitted provided that the following conditions are met:");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* . Redistributions of source code must retain the above copyright notice, this");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*   list of conditions and the following disclaimer.");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* . Redistributions in binary form must reproduce the above copyright notice,");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*   this list of conditions and the following disclaimer in the documentation");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*   and/or other materials provided with the distribution.");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* . Neither the name \"jOOQ\" nor the names of its contributors may be");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*   used to endorse or promote products derived from this software without");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*   specific prior written permission.");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\"");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("* POSSIBILITY OF SUCH DAMAGE.");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("*/");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence generatedAnnotation() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("@Generated(\"This class was generated using jOOQ-tools\")");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence generatedMethod() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("@Generated(\"This method was generated using jOOQ-tools\")");
    _builder.newLine();
    return _builder;
  }
  
  /**
   * A comma-separated list of types
   * <p>
   * <code>T1, T2, .., T[N]</code>
   */
  public String TN(final int degree) {
    IntegerRange _upTo = new IntegerRange(1, degree);
    final Function1<Integer,String> _function = new Function1<Integer,String>() {
        public String apply(final Integer e) {
          String _plus = ("T" + e);
          return _plus;
        }
      };
    String _join = IterableExtensions.<Integer>join(_upTo, ", ", _function);
    return _join;
  }
  
  /**
   * A comma-separated list of identifier references
   * <p>
   * <code>t1, t2, .., t[N]</code>
   */
  public String tn(final int degree) {
    IntegerRange _upTo = new IntegerRange(1, degree);
    final Function1<Integer,String> _function = new Function1<Integer,String>() {
        public String apply(final Integer e) {
          String _plus = ("t" + e);
          return _plus;
        }
      };
    String _join = IterableExtensions.<Integer>join(_upTo, ", ", _function);
    return _join;
  }
  
  /**
   * A comma-separated list of identifier declarations
   * <p>
   * <code>T1 t1, T2 t2, .., T[N] t[N]</code>
   */
  public String TN_tn(final int degree) {
    IntegerRange _upTo = new IntegerRange(1, degree);
    final Function1<Integer,String> _function = new Function1<Integer,String>() {
        public String apply(final Integer e) {
          String _plus = ("T" + e);
          String _plus_1 = (_plus + " t");
          String _plus_2 = (_plus_1 + e);
          return _plus_2;
        }
      };
    String _join = IterableExtensions.<Integer>join(_upTo, ", ", _function);
    return _join;
  }
  
  /**
   * A comma-separated list of identifier declarations
   * <p>
   * <code>T1 t1, T2 t2, .., T[N] t[N]</code>
   */
  public String TN_XXXn(final int degree, final String XXX) {
    IntegerRange _upTo = new IntegerRange(1, degree);
    final Function1<Integer,String> _function = new Function1<Integer,String>() {
        public String apply(final Integer e) {
          String _plus = ("T" + e);
          String _plus_1 = (_plus + " ");
          String _plus_2 = (_plus_1 + XXX);
          String _plus_3 = (_plus_2 + e);
          return _plus_3;
        }
      };
    String _join = IterableExtensions.<Integer>join(_upTo, ", ", _function);
    return _join;
  }
  
  /**
   * A comma-separated list of field declarations
   * <p>
   * <code>Field&lt;T1> t1, Field&lt;T2> t2, .., Field&ltT[N]> t[N]</code>
   */
  public String Field_TN_tn(final int degree) {
    IntegerRange _upTo = new IntegerRange(1, degree);
    final Function1<Integer,String> _function = new Function1<Integer,String>() {
        public String apply(final Integer e) {
          String _plus = ("Field<T" + e);
          String _plus_1 = (_plus + "> t");
          String _plus_2 = (_plus_1 + e);
          return _plus_2;
        }
      };
    String _join = IterableExtensions.<Integer>join(_upTo, ", ", _function);
    return _join;
  }
  
  /**
   * A comma-separated list of field declarations
   * <p>
   * <code>Field&lt;T1> t1, Field&lt;T2> t2, .., Field&ltT[N]> t[N]</code>
   */
  public String Field_TN_XXXn(final int degree, final String XXX) {
    IntegerRange _upTo = new IntegerRange(1, degree);
    final Function1<Integer,String> _function = new Function1<Integer,String>() {
        public String apply(final Integer e) {
          String _plus = ("Field<T" + e);
          String _plus_1 = (_plus + "> ");
          String _plus_2 = (_plus_1 + XXX);
          String _plus_3 = (_plus_2 + e);
          return _plus_3;
        }
      };
    String _join = IterableExtensions.<Integer>join(_upTo, ", ", _function);
    return _join;
  }
  
  /**
   * A comma-separated list of field declarations
   * <p>
   * <code>Field&lt;T1> field1, Field&lt;T2> field2, .., Field&ltT[N]> field[N]</code>
   */
  public String Field_TN_fieldn(final int degree) {
    IntegerRange _upTo = new IntegerRange(1, degree);
    final Function1<Integer,String> _function = new Function1<Integer,String>() {
        public String apply(final Integer e) {
          String _plus = ("Field<T" + e);
          String _plus_1 = (_plus + "> field");
          String _plus_2 = (_plus_1 + e);
          return _plus_2;
        }
      };
    String _join = IterableExtensions.<Integer>join(_upTo, ", ", _function);
    return _join;
  }
  
  /**
   * A comma-separated list of field references
   * <p>
   * <code>field1, field2, .., field[N]</code>
   */
  public String fieldn(final int degree) {
    IntegerRange _upTo = new IntegerRange(1, degree);
    final Function1<Integer,String> _function = new Function1<Integer,String>() {
        public String apply(final Integer e) {
          String _plus = ("field" + e);
          return _plus;
        }
      };
    String _join = IterableExtensions.<Integer>join(_upTo, ", ", _function);
    return _join;
  }
  
  /**
   * A comma-separated list of field references
   * <p>
   * Unlike {@link #fieldn(int)}, this will return at most 5 fields
   * <p>
   * <code>field1, field2, .., field[N]</code>
   */
  public String field1_field2_fieldn(final int degree) {
    boolean _lessEqualsThan = (degree <= 5);
    if (_lessEqualsThan) {
      return this.fieldn(degree);
    } else {
      IntegerRange _upTo = new IntegerRange(1, 3);
      final Function1<Integer,String> _function = new Function1<Integer,String>() {
          public String apply(final Integer e) {
            String _plus = ("field" + e);
            return _plus;
          }
        };
      String _join = IterableExtensions.<Integer>join(_upTo, ", ", _function);
      String _plus = (_join + 
        ", .., ");
      int _minus = (degree - 1);
      IntegerRange _upTo_1 = new IntegerRange(_minus, degree);
      final Function1<Integer,String> _function_1 = new Function1<Integer,String>() {
          public String apply(final Integer e) {
            String _plus = ("field" + e);
            return _plus;
          }
        };
      String _join_1 = IterableExtensions.<Integer>join(_upTo_1, ", ", _function_1);
      return (_plus + _join_1);
    }
  }
  
  /**
   * A comma-separated list of value constructor references
   * <p>
   * <code>val(t1), val(t2), .., val(t[N])</code>
   */
  public String val_tn(final int degree) {
    IntegerRange _upTo = new IntegerRange(1, degree);
    final Function1<Integer,String> _function = new Function1<Integer,String>() {
        public String apply(final Integer e) {
          String _plus = ("val(t" + e);
          String _plus_1 = (_plus + ")");
          return _plus_1;
        }
      };
    String _join = IterableExtensions.<Integer>join(_upTo, ", ", _function);
    return _join;
  }
}
