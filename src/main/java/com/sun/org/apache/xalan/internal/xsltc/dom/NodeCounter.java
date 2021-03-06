



package com.sun.org.apache.xalan.internal.xsltc.dom;

import java.util.Vector;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;


public abstract class NodeCounter {
    public static final int END = DTM.NULL;

    protected int _node = END;
    protected int _nodeType = DOM.FIRST_TYPE - 1;
    protected double _value = Integer.MIN_VALUE;

    public final DOM          _document;
    public final DTMAxisIterator _iterator;
    public final Translet     _translet;

    protected String _format;
    protected String _lang;
    protected String _letterValue;
    protected String _groupSep;
    protected int    _groupSize;

    private boolean _separFirst = true;
    private boolean _separLast = false;
    private Vector _separToks = new Vector();
    private Vector _formatToks = new Vector();
    private int _nSepars  = 0;
    private int _nFormats = 0;

    private final static String[] Thousands =
        {"", "m", "mm", "mmm" };
    private final static String[] Hundreds =
    {"", "c", "cc", "ccc", "cd", "d", "dc", "dcc", "dccc", "cm"};
    private final static String[] Tens =
    {"", "x", "xx", "xxx", "xl", "l", "lx", "lxx", "lxxx", "xc"};
    private final static String[] Ones =
    {"", "i", "ii", "iii", "iv", "v", "vi", "vii", "viii", "ix"};

    private StringBuilder _tempBuffer = new StringBuilder();


    protected boolean _hasFrom;

    protected NodeCounter(Translet translet,
              DOM document, DTMAxisIterator iterator) {
    _translet = translet;
    _document = document;
    _iterator = iterator;
    }

    protected NodeCounter(Translet translet,
              DOM document, DTMAxisIterator iterator, boolean hasFrom) {
        _translet = translet;
        _document = document;
        _iterator = iterator;
        _hasFrom = hasFrom;
    }


    abstract public NodeCounter setStartNode(int node);


    public NodeCounter setValue(double value) {
    _value = value;
    return this;
    }


    protected void setFormatting(String format, String lang, String letterValue,
                 String groupSep, String groupSize) {
    _lang = lang;
    _groupSep = groupSep;
    _letterValue = letterValue;
    _groupSize = parseStringToAnInt(groupSize);
    setTokens(format);

 }


    private int parseStringToAnInt(String s) {
        if (s == null)
            return 0;

        int result = 0;
        boolean negative = false;
        int radix = 10, i = 0, max = s.length();
        int limit, multmin, digit;

        if (max > 0) {
            if (s.charAt(0) == '-') {
                negative = true;
                limit = Integer.MIN_VALUE;
                i++;
            } else {
                limit = -Integer.MAX_VALUE;
            }
            multmin = limit / radix;
            if (i < max) {
                digit = Character.digit(s.charAt(i++), radix);
                if (digit < 0)
                    return 0;
                else
                    result = -digit;
            }
            while (i < max) {
                digit = Character.digit(s.charAt(i++), radix);
                if (digit < 0)
                    return 0;
                if (result < multmin)
                    return 0;
                result *= radix;
                if (result < limit + digit)
                    return 0;
                result -= digit;
            }
        } else {
            return 0;
        }
        if (negative) {
            if (i > 1)
                return result;
            else
                return 0;
        } else {
            return -result;
        }
    }

  private final void setTokens(final String format){
     if( (_format!=null) &&(format.equals(_format)) ){return;
     }
     _format = format;
     final int length = _format.length();
     boolean isFirst = true;
     _separFirst = true;
     _separLast = false;
     _nSepars  = 0;
     _nFormats = 0;
     _separToks.clear() ;
     _formatToks.clear();


         for (int j = 0, i = 0; i < length;) {
                 char c = format.charAt(i);
                 for (j = i; Character.isLetterOrDigit(c);) {
                     if (++i == length) break;
             c = format.charAt(i);
                 }
                 if (i > j) {
                     if (isFirst) {
                         _separToks.addElement(".");
                         isFirst = _separFirst = false;
                     }
                     _formatToks.addElement(format.substring(j, i));
                 }

                 if (i == length) break;

                 c = format.charAt(i);
                 for (j = i; !Character.isLetterOrDigit(c);) {
                     if (++i == length) break;
                     c = format.charAt(i);
                     isFirst = false;
                 }
                 if (i > j) {
                     _separToks.addElement(format.substring(j, i));
                 }
             }

         _nSepars = _separToks.size();
         _nFormats = _formatToks.size();
         if (_nSepars > _nFormats) _separLast = true;

         if (_separFirst) _nSepars--;
         if (_separLast) _nSepars--;
         if (_nSepars == 0) {
             _separToks.insertElementAt(".", 1);
             _nSepars++;
         }
         if (_separFirst) _nSepars ++;

 }

    public NodeCounter setDefaultFormatting() {
    setFormatting("1", "en", "alphabetic", null, null);
    return this;
    }


    abstract public String getCounter();


    public String getCounter(String format, String lang, String letterValue,
                String groupSep, String groupSize) {
    setFormatting(format, lang, letterValue, groupSep, groupSize);
    return getCounter();
    }


    public boolean matchesCount(int node) {
    return _nodeType == _document.getExpandedTypeID(node);
    }


    public boolean matchesFrom(int node) {
    return false;
    }


    protected String formatNumbers(int value) {
    return formatNumbers(new int[] { value });
    }


    protected String formatNumbers(int[] values) {
    final int nValues = values.length;

    boolean isEmpty = true;
    for (int i = 0; i < nValues; i++)
        if (values[i] != Integer.MIN_VALUE)
        isEmpty = false;
    if (isEmpty) return("");

    boolean isFirst = true;
    int t = 0, n = 0, s = 1;
  _tempBuffer.setLength(0);
    final StringBuilder buffer = _tempBuffer;

    if (_separFirst) buffer.append((String)_separToks.elementAt(0));

    while (n < nValues) {
        final int value = values[n];
        if (value != Integer.MIN_VALUE) {
        if (!isFirst) buffer.append((String) _separToks.elementAt(s++));
        formatValue(value, (String)_formatToks.elementAt(t++), buffer);
        if (t == _nFormats) t--;
        if (s >= _nSepars) s--;
        isFirst = false;
        }
        n++;
    }

    if (_separLast) buffer.append((String)_separToks.lastElement());
    return buffer.toString();
    }


    private void formatValue(int value, String format, StringBuilder buffer) {
        char c = format.charAt(0);

        if (Character.isDigit(c)) {
            char zero = (char)(c - Character.getNumericValue(c));

            StringBuilder temp = buffer;
            if (_groupSize > 0) {
                temp = new StringBuilder();
            }
            String s = "";
            int n = value;
            while (n > 0) {
                s = (char) ((int) zero + (n % 10)) + s;
                n = n / 10;
            }

            for (int i = 0; i < format.length() - s.length(); i++) {
                temp.append(zero);
            }
            temp.append(s);

            if (_groupSize > 0) {
                for (int i = 0; i < temp.length(); i++) {
                    if (i != 0 && ((temp.length() - i) % _groupSize) == 0) {
                        buffer.append(_groupSep);
                    }
                    buffer.append(temp.charAt(i));
                }
            }
        }
    else if (c == 'i' && !_letterValue.equals("alphabetic")) {
            buffer.append(romanValue(value));
        }
    else if (c == 'I' && !_letterValue.equals("alphabetic")) {
            buffer.append(romanValue(value).toUpperCase());
        }
    else {
        int min = (int) c;
        int max = (int) c;

        if (c >= 0x3b1 && c <= 0x3c9) {
        max = 0x3c9;    }
        else {
        while (Character.isLetterOrDigit((char) (max + 1))) {
            max++;
        }
        }
            buffer.append(alphaValue(value, min, max));
        }
    }

    private String alphaValue(int value, int min, int max) {
        if (value <= 0) {
        return "" + value;
    }

        int range = max - min + 1;
        char last = (char)(((value-1) % range) + min);
        if (value > range) {
            return alphaValue((value-1) / range, min, max) + last;
        }
    else {
            return "" + last;
        }
    }

    private String romanValue(int n) {
        if (n <= 0 || n > 4000) {
        return "" + n;
    }
        return
        Thousands[n / 1000] +
        Hundreds[(n / 100) % 10] +
        Tens[(n/10) % 10] +
        Ones[n % 10];
    }

}
