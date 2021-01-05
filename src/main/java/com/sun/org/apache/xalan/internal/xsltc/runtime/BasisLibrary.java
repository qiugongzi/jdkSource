



package com.sun.org.apache.xalan.internal.xsltc.runtime;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xalan.internal.xsltc.dom.AbsoluteIterator;
import com.sun.org.apache.xalan.internal.xsltc.dom.ArrayNodeListIterator;
import com.sun.org.apache.xalan.internal.xsltc.dom.DOMAdapter;
import com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM;
import com.sun.org.apache.xalan.internal.xsltc.dom.SingletonIterator;
import com.sun.org.apache.xalan.internal.xsltc.dom.StepIterator;
import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBase;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeProxy;
import com.sun.org.apache.xml.internal.serializer.NamespaceMappings;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public final class BasisLibrary {

    private final static String EMPTYSTRING = "";


    private static final ThreadLocal<StringBuilder> threadLocalStringBuilder =
        new ThreadLocal<StringBuilder> () {
            @Override protected StringBuilder initialValue() {
                return new StringBuilder();
            }
    };


    private static final ThreadLocal<StringBuffer> threadLocalStringBuffer =
        new ThreadLocal<StringBuffer> () {
            @Override protected StringBuffer initialValue() {
                return new StringBuffer();
            }
    };


    public static int countF(DTMAxisIterator iterator) {
        return(iterator.getLast());
    }


    public static int positionF(DTMAxisIterator iterator) {
        return iterator.isReverse()
                     ? iterator.getLast() - iterator.getPosition() + 1
                     : iterator.getPosition();
    }


    public static double sumF(DTMAxisIterator iterator, DOM dom) {
        try {
            double result = 0.0;
            int node;
            while ((node = iterator.next()) != DTMAxisIterator.END) {
                result += Double.parseDouble(dom.getStringValueX(node));
            }
            return result;
        }
        catch (NumberFormatException e) {
            return Double.NaN;
        }
    }


    public static String stringF(int node, DOM dom) {
        return dom.getStringValueX(node);
    }


    public static String stringF(Object obj, DOM dom) {
        if (obj instanceof DTMAxisIterator) {
            return dom.getStringValueX(((DTMAxisIterator)obj).reset().next());
        }
        else if (obj instanceof Node) {
            return dom.getStringValueX(((Node)obj).node);
        }
        else if (obj instanceof DOM) {
            return ((DOM)obj).getStringValue();
        }
        else {
            return obj.toString();
        }
    }


    public static String stringF(Object obj, int node, DOM dom) {
        if (obj instanceof DTMAxisIterator) {
            return dom.getStringValueX(((DTMAxisIterator)obj).reset().next());
        }
        else if (obj instanceof Node) {
            return dom.getStringValueX(((Node)obj).node);
        }
        else if (obj instanceof DOM) {
            return ((DOM)obj).getStringValue();
        }
        else if (obj instanceof Double) {
            Double d = (Double)obj;
            final String result = d.toString();
            final int length = result.length();
            if ((result.charAt(length-2)=='.') &&
                (result.charAt(length-1) == '0'))
                return result.substring(0, length-2);
            else
                return result;
        }
        else {
            return obj != null ? obj.toString() : "";
        }
    }


    public static double numberF(int node, DOM dom) {
        return stringToReal(dom.getStringValueX(node));
    }


    public static double numberF(Object obj, DOM dom) {
        if (obj instanceof Double) {
            return ((Double) obj).doubleValue();
        }
        else if (obj instanceof Integer) {
            return ((Integer) obj).doubleValue();
        }
        else if (obj instanceof Boolean) {
            return  ((Boolean) obj).booleanValue() ? 1.0 : 0.0;
        }
        else if (obj instanceof String) {
            return stringToReal((String) obj);
        }
        else if (obj instanceof DTMAxisIterator) {
            DTMAxisIterator iter = (DTMAxisIterator) obj;
            return stringToReal(dom.getStringValueX(iter.reset().next()));
        }
        else if (obj instanceof Node) {
            return stringToReal(dom.getStringValueX(((Node) obj).node));
        }
        else if (obj instanceof DOM) {
            return stringToReal(((DOM) obj).getStringValue());
        }
        else {
            final String className = obj.getClass().getName();
            runTimeError(INVALID_ARGUMENT_ERR, className, "number()");
            return 0.0;
        }
    }


    public static double roundF(double d) {
            return (d<-0.5 || d>0.0)?Math.floor(d+0.5):((d==0.0)?
                        d:(Double.isNaN(d)?Double.NaN:-0.0));
    }


    public static boolean booleanF(Object obj) {
        if (obj instanceof Double) {
            final double temp = ((Double) obj).doubleValue();
            return temp != 0.0 && !Double.isNaN(temp);
        }
        else if (obj instanceof Integer) {
            return ((Integer) obj).doubleValue() != 0;
        }
        else if (obj instanceof Boolean) {
            return  ((Boolean) obj).booleanValue();
        }
        else if (obj instanceof String) {
            return !((String) obj).equals(EMPTYSTRING);
        }
        else if (obj instanceof DTMAxisIterator) {
            DTMAxisIterator iter = (DTMAxisIterator) obj;
            return iter.reset().next() != DTMAxisIterator.END;
        }
        else if (obj instanceof Node) {
            return true;
        }
        else if (obj instanceof DOM) {
            String temp = ((DOM) obj).getStringValue();
            return !temp.equals(EMPTYSTRING);
        }
        else {
            final String className = obj.getClass().getName();
            runTimeError(INVALID_ARGUMENT_ERR, className, "boolean()");
        }
        return false;
    }


    public static String substringF(String value, double start) {
        if (Double.isNaN(start))
            return(EMPTYSTRING);

        final int strlen = getStringLength(value);
        int istart = (int)Math.round(start) - 1;

        if (istart > strlen)
            return(EMPTYSTRING);
        if (istart < 1)
            istart = 0;
        try {
            istart = value.offsetByCodePoints(0, istart);
            return value.substring(istart);
        } catch (IndexOutOfBoundsException e) {
            runTimeError(RUN_TIME_INTERNAL_ERR, "substring()");
            return null;
        }
    }


    public static String substringF(String value, double start, double length) {
        if (Double.isInfinite(start) ||
            Double.isNaN(start) ||
            Double.isNaN(length) ||
            length < 0)
            return(EMPTYSTRING);

        int istart = (int)Math.round(start) - 1;
        int ilength = (int)Math.round(length);
        final int isum;
        if (Double.isInfinite(length))
            isum = Integer.MAX_VALUE;
        else
            isum = istart + ilength;

        final int strlen = getStringLength(value);
        if (isum < 0 || istart > strlen)
                return(EMPTYSTRING);

        if (istart < 0) {
            ilength += istart;
            istart = 0;
        }

        try {
            istart = value.offsetByCodePoints(0, istart);
            if (isum > strlen) {
                return value.substring(istart);
            } else {
                int offset = value.offsetByCodePoints(istart, ilength);
                return value.substring(istart, offset);
            }
        } catch (IndexOutOfBoundsException e) {
            runTimeError(RUN_TIME_INTERNAL_ERR, "substring()");
            return null;
        }
    }


    public static String substring_afterF(String value, String substring) {
        final int index = value.indexOf(substring);
        if (index >= 0)
            return value.substring(index + substring.length());
        else
            return EMPTYSTRING;
    }


    public static String substring_beforeF(String value, String substring) {
        final int index = value.indexOf(substring);
        if (index >= 0)
            return value.substring(0, index);
        else
            return EMPTYSTRING;
    }


    public static String translateF(String value, String from, String to) {
        final int tol = to.length();
        final int froml = from.length();
        final int valuel = value.length();

        final StringBuilder result = threadLocalStringBuilder.get();
    result.setLength(0);
        for (int j, i = 0; i < valuel; i++) {
            final char ch = value.charAt(i);
            for (j = 0; j < froml; j++) {
                if (ch == from.charAt(j)) {
                    if (j < tol)
                        result.append(to.charAt(j));
                    break;
                }
            }
            if (j == froml)
                result.append(ch);
        }
        return result.toString();
    }


    public static String normalize_spaceF(int node, DOM dom) {
        return normalize_spaceF(dom.getStringValueX(node));
    }


    public static String normalize_spaceF(String value) {
        int i = 0, n = value.length();
        StringBuilder result = threadLocalStringBuilder.get();
    result.setLength(0);

        while (i < n && isWhiteSpace(value.charAt(i)))
            i++;

        while (true) {
            while (i < n && !isWhiteSpace(value.charAt(i))) {
                result.append(value.charAt(i++));
            }
            if (i == n)
                break;
            while (i < n && isWhiteSpace(value.charAt(i))) {
                i++;
            }
            if (i < n)
                result.append(' ');
        }
        return result.toString();
    }


    public static String generate_idF(int node) {
        if (node > 0)
            return "N" + node;
        else
            return EMPTYSTRING;
    }


    public static String getLocalName(String value) {
        int idx = value.lastIndexOf(':');
        if (idx >= 0) value = value.substring(idx + 1);
        idx = value.lastIndexOf('@');
        if (idx >= 0) value = value.substring(idx + 1);
        return(value);
    }


    public static void unresolved_externalF(String name) {
        runTimeError(EXTERNAL_FUNC_ERR, name);
    }


    public static void unallowed_extension_functionF(String name) {
        runTimeError(UNALLOWED_EXTENSION_FUNCTION_ERR, name);
    }


    public static void unallowed_extension_elementF(String name) {
        runTimeError(UNALLOWED_EXTENSION_ELEMENT_ERR, name);
    }


    public static void unsupported_ElementF(String qname, boolean isExtension) {
        if (isExtension)
            runTimeError(UNSUPPORTED_EXT_ERR, qname);
        else
            runTimeError(UNSUPPORTED_XSL_ERR, qname);
    }


    public static String namespace_uriF(DTMAxisIterator iter, DOM dom) {
        return namespace_uriF(iter.next(), dom);
    }


    public static String system_propertyF(String name) {
        if (name.equals("xsl:version"))
            return("1.0");
        if (name.equals("xsl:vendor"))
            return("Apache Software Foundation (Xalan XSLTC)");
        if (name.equals("xsl:vendor-url"))
            return("http:runTimeError(INVALID_ARGUMENT_ERR, name, "system-property()");
        return(EMPTYSTRING);
    }


    public static String namespace_uriF(int node, DOM dom) {
        final String value = dom.getNodeName(node);
        final int colon = value.lastIndexOf(':');
        if (colon >= 0)
            return value.substring(0, colon);
        else
            return EMPTYSTRING;
    }


    public static String objectTypeF(Object obj)
    {
      if (obj instanceof String)
        return "string";
      else if (obj instanceof Boolean)
        return "boolean";
      else if (obj instanceof Number)
        return "number";
      else if (obj instanceof DOM)
        return "RTF";
      else if (obj instanceof DTMAxisIterator)
        return "node-set";
      else
        return "unknown";
    }


    public static DTMAxisIterator nodesetF(Object obj) {
        if (obj instanceof DOM) {
           final DOM dom = (DOM)obj;
           return new SingletonIterator(dom.getDocument(), true);
        }
        else if (obj instanceof DTMAxisIterator) {
           return (DTMAxisIterator) obj;
        }
        else {
            final String className = obj.getClass().getName();
            runTimeError(DATA_CONVERSION_ERR, "node-set", className);
            return null;
        }
    }

    private static boolean isWhiteSpace(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
    }

    private static boolean compareStrings(String lstring, String rstring,
                                          int op, DOM dom) {
        switch (op) {
    case Operators.EQ:
            return lstring.equals(rstring);

    case Operators.NE:
            return !lstring.equals(rstring);

    case Operators.GT:
            return numberF(lstring, dom) > numberF(rstring, dom);

    case Operators.LT:
            return numberF(lstring, dom) < numberF(rstring, dom);

    case Operators.GE:
            return numberF(lstring, dom) >= numberF(rstring, dom);

    case Operators.LE:
            return numberF(lstring, dom) <= numberF(rstring, dom);

        default:
            runTimeError(RUN_TIME_INTERNAL_ERR, "compare()");
            return false;
        }
    }


    public static boolean compare(DTMAxisIterator left, DTMAxisIterator right,
                                  int op, DOM dom) {
        int lnode;
        left.reset();

        while ((lnode = left.next()) != DTMAxisIterator.END) {
            final String lvalue = dom.getStringValueX(lnode);

            int rnode;
            right.reset();
            while ((rnode = right.next()) != DTMAxisIterator.END) {
                if (lnode == rnode) {
                    if (op == Operators.EQ) {
                        return true;
                    } else if (op == Operators.NE) {
                        continue;
                    }
                }
                if (compareStrings(lvalue, dom.getStringValueX(rnode), op,
                                   dom)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean compare(int node, DTMAxisIterator iterator,
                                  int op, DOM dom) {
        int rnode;
        String value;

        switch(op) {
    case Operators.EQ:
            rnode = iterator.next();
            if (rnode != DTMAxisIterator.END) {
                value = dom.getStringValueX(node);
                do {
                    if (node == rnode
                          || value.equals(dom.getStringValueX(rnode))) {
                       return true;
                    }
                } while ((rnode = iterator.next()) != DTMAxisIterator.END);
            }
            break;
    case Operators.NE:
            rnode = iterator.next();
            if (rnode != DTMAxisIterator.END) {
                value = dom.getStringValueX(node);
                do {
                    if (node != rnode
                          && !value.equals(dom.getStringValueX(rnode))) {
                        return true;
                    }
                } while ((rnode = iterator.next()) != DTMAxisIterator.END);
            }
            break;
    case Operators.LT:
            while ((rnode = iterator.next()) != DTMAxisIterator.END) {
                if (rnode > node) return true;
            }
            break;
    case Operators.GT:
            while ((rnode = iterator.next()) != DTMAxisIterator.END) {
                if (rnode < node) return true;
            }
            break;
        }
        return(false);
    }


    public static boolean compare(DTMAxisIterator left, final double rnumber,
                                  final int op, DOM dom) {
        int node;
        switch (op) {
    case Operators.EQ:
            while ((node = left.next()) != DTMAxisIterator.END) {
                if (numberF(dom.getStringValueX(node), dom) == rnumber)
                    return true;
            }
            break;

    case Operators.NE:
            while ((node = left.next()) != DTMAxisIterator.END) {
                if (numberF(dom.getStringValueX(node), dom) != rnumber)
                    return true;
            }
            break;

    case Operators.GT:
            while ((node = left.next()) != DTMAxisIterator.END) {
                if (numberF(dom.getStringValueX(node), dom) > rnumber)
                    return true;
            }
            break;

    case Operators.LT:
            while ((node = left.next()) != DTMAxisIterator.END) {
                if (numberF(dom.getStringValueX(node), dom) < rnumber)
                    return true;
            }
            break;

    case Operators.GE:
            while ((node = left.next()) != DTMAxisIterator.END) {
                if (numberF(dom.getStringValueX(node), dom) >= rnumber)
                    return true;
            }
            break;

    case Operators.LE:
            while ((node = left.next()) != DTMAxisIterator.END) {
                if (numberF(dom.getStringValueX(node), dom) <= rnumber)
                    return true;
            }
            break;

        default:
            runTimeError(RUN_TIME_INTERNAL_ERR, "compare()");
        }

        return false;
    }


    public static boolean compare(DTMAxisIterator left, final String rstring,
                                  int op, DOM dom) {
        int node;
        while ((node = left.next()) != DTMAxisIterator.END) {
            if (compareStrings(dom.getStringValueX(node), rstring, op, dom)) {
                return true;
            }
        }
        return false;
    }


    public static boolean compare(Object left, Object right,
                                  int op, DOM dom)
    {
        boolean result = false;
        boolean hasSimpleArgs = hasSimpleType(left) && hasSimpleType(right);

    if (op != Operators.EQ && op != Operators.NE) {
            if (left instanceof Node || right instanceof Node) {
                if (left instanceof Boolean) {
                    right = new Boolean(booleanF(right));
                    hasSimpleArgs = true;
                }
                if (right instanceof Boolean) {
                    left = new Boolean(booleanF(left));
                    hasSimpleArgs = true;
                }
            }

            if (hasSimpleArgs) {
                switch (op) {
        case Operators.GT:
                    return numberF(left, dom) > numberF(right, dom);

        case Operators.LT:
                    return numberF(left, dom) < numberF(right, dom);

        case Operators.GE:
                    return numberF(left, dom) >= numberF(right, dom);

        case Operators.LE:
                    return numberF(left, dom) <= numberF(right, dom);

        default:
                    runTimeError(RUN_TIME_INTERNAL_ERR, "compare()");
                }
            }
            }

        if (hasSimpleArgs) {
            if (left instanceof Boolean || right instanceof Boolean) {
                result = booleanF(left) == booleanF(right);
            }
            else if (left instanceof Double || right instanceof Double ||
                     left instanceof Integer || right instanceof Integer) {
                result = numberF(left, dom) == numberF(right, dom);
            }
            else { result = stringF(left, dom).equals(stringF(right, dom));
            }

            if (op == Operators.NE) {
                result = !result;
            }
        }
        else {
            if (left instanceof Node) {
                left = new SingletonIterator(((Node)left).node);
            }
            if (right instanceof Node) {
                right = new SingletonIterator(((Node)right).node);
            }

            if (hasSimpleType(left) ||
                left instanceof DOM && right instanceof DTMAxisIterator) {
                final Object temp = right; right = left; left = temp;
                op = Operators.swapOp(op);
            }

            if (left instanceof DOM) {
                if (right instanceof Boolean) {
                    result = ((Boolean)right).booleanValue();
                    return result == (op == Operators.EQ);
                }

                final String sleft = ((DOM)left).getStringValue();

                if (right instanceof Number) {
                    result = ((Number)right).doubleValue() ==
                        stringToReal(sleft);
                }
                else if (right instanceof String) {
                    result = sleft.equals((String)right);
                }
                else if (right instanceof DOM) {
                    result = sleft.equals(((DOM)right).getStringValue());
                }

                if (op == Operators.NE) {
                    result = !result;
                }
                return result;
            }

            DTMAxisIterator iter = ((DTMAxisIterator)left).reset();

            if (right instanceof DTMAxisIterator) {
                result = compare(iter, (DTMAxisIterator)right, op, dom);
            }
            else if (right instanceof String) {
                result = compare(iter, (String)right, op, dom);
            }
            else if (right instanceof Number) {
                final double temp = ((Number)right).doubleValue();
                result = compare(iter, temp, op, dom);
            }
            else if (right instanceof Boolean) {
                boolean temp = ((Boolean)right).booleanValue();
                result = (iter.reset().next() != DTMAxisIterator.END) == temp;
            }
            else if (right instanceof DOM) {
                result = compare(iter, ((DOM)right).getStringValue(),
                                 op, dom);
            }
            else if (right == null) {
                return(false);
            }
            else {
                final String className = right.getClass().getName();
                runTimeError(INVALID_ARGUMENT_ERR, className, "compare()");
            }
        }
        return result;
    }


    public static boolean testLanguage(String testLang, DOM dom, int node) {
        String nodeLang = dom.getLanguage(node);
        if (nodeLang == null)
            return(false);
        else
            nodeLang = nodeLang.toLowerCase();

        testLang = testLang.toLowerCase();
        if (testLang.length() == 2) {
            return(nodeLang.startsWith(testLang));
        }
        else {
            return(nodeLang.equals(testLang));
        }
    }

    private static boolean hasSimpleType(Object obj) {
        return obj instanceof Boolean || obj instanceof Double ||
            obj instanceof Integer || obj instanceof String ||
            obj instanceof Node || obj instanceof DOM;
    }


    public static double stringToReal(String s) {
        try {
            return Double.valueOf(s).doubleValue();
        }
        catch (NumberFormatException e) {
            return Double.NaN;
        }
    }


    public static int stringToInt(String s) {
        try {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException e) {
            return(-1); }
    }

    private static final int DOUBLE_FRACTION_DIGITS = 340;
    private static final double lowerBounds = 0.001;
    private static final double upperBounds = 10000000;
    private static DecimalFormat defaultFormatter, xpathFormatter;
    private static String defaultPattern = "";

    static {
        NumberFormat f = NumberFormat.getInstance(Locale.getDefault());
        defaultFormatter = (f instanceof DecimalFormat) ?
            (DecimalFormat) f : new DecimalFormat();
        defaultFormatter.setMaximumFractionDigits(DOUBLE_FRACTION_DIGITS);
        defaultFormatter.setMinimumFractionDigits(0);
        defaultFormatter.setMinimumIntegerDigits(1);
        defaultFormatter.setGroupingUsed(false);

        xpathFormatter = new DecimalFormat("",
            new DecimalFormatSymbols(Locale.US));
        xpathFormatter.setMaximumFractionDigits(DOUBLE_FRACTION_DIGITS);
        xpathFormatter.setMinimumFractionDigits(0);
        xpathFormatter.setMinimumIntegerDigits(1);
        xpathFormatter.setGroupingUsed(false);
    }


    public static String realToString(double d) {
        final double m = Math.abs(d);
        if ((m >= lowerBounds) && (m < upperBounds)) {
            final String result = Double.toString(d);
            final int length = result.length();
            if ((result.charAt(length-2) == '.') &&
                (result.charAt(length-1) == '0'))
                return result.substring(0, length-2);
            else
                return result;
        }
        else {
            if (Double.isNaN(d) || Double.isInfinite(d))
                return(Double.toString(d));

            d = d + 0.0;

            StringBuffer result = threadLocalStringBuffer.get();
            result.setLength(0);
            xpathFormatter.format(d, result, _fieldPosition);
            return result.toString();
        }
    }


    public static int realToInt(double d) {
        return (int)d;
    }


    private static FieldPosition _fieldPosition = new FieldPosition(0);

    public static String formatNumber(double number, String pattern,
                                      DecimalFormat formatter) {
        if (formatter == null) {
            formatter = defaultFormatter;
        }
        try {
            StringBuffer result = threadLocalStringBuffer.get();
        result.setLength(0);
            if (pattern != defaultPattern) {
                formatter.applyLocalizedPattern(pattern);
            }
        formatter.format(number, result, _fieldPosition);
            return result.toString();
        }
        catch (IllegalArgumentException e) {
            runTimeError(FORMAT_NUMBER_ERR, Double.toString(number), pattern);
            return(EMPTYSTRING);
        }
    }


    public static DTMAxisIterator referenceToNodeSet(Object obj) {
        if (obj instanceof Node) {
            return(new SingletonIterator(((Node)obj).node));
        }
        else if (obj instanceof DTMAxisIterator) {
            return(((DTMAxisIterator)obj).cloneIterator().reset());
        }
        else {
            final String className = obj.getClass().getName();
            runTimeError(DATA_CONVERSION_ERR, className, "node-set");
            return null;
        }
    }


    public static NodeList referenceToNodeList(Object obj, DOM dom) {
        if (obj instanceof Node || obj instanceof DTMAxisIterator) {
            DTMAxisIterator iter = referenceToNodeSet(obj);
            return dom.makeNodeList(iter);
        }
        else if (obj instanceof DOM) {
          dom = (DOM)obj;
          return dom.makeNodeList(DTMDefaultBase.ROOTNODE);
        }
        else {
            final String className = obj.getClass().getName();
            runTimeError(DATA_CONVERSION_ERR, className,
                "org.w3c.dom.NodeList");
            return null;
        }
    }


    public static org.w3c.dom.Node referenceToNode(Object obj, DOM dom) {
        if (obj instanceof Node || obj instanceof DTMAxisIterator) {
            DTMAxisIterator iter = referenceToNodeSet(obj);
            return dom.makeNode(iter);
        }
        else if (obj instanceof DOM) {
          dom = (DOM)obj;
          DTMAxisIterator iter = dom.getChildren(DTMDefaultBase.ROOTNODE);
          return dom.makeNode(iter);
        }
        else {
            final String className = obj.getClass().getName();
            runTimeError(DATA_CONVERSION_ERR, className, "org.w3c.dom.Node");
            return null;
        }
    }


    public static long referenceToLong(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).longValue();    }
        else {
            final String className = obj.getClass().getName();
            runTimeError(DATA_CONVERSION_ERR, className, Long.TYPE);
            return 0;
        }
    }


    public static double referenceToDouble(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();   }
        else {
            final String className = obj.getClass().getName();
            runTimeError(DATA_CONVERSION_ERR, className, Double.TYPE);
            return 0;
        }
    }


    public static boolean referenceToBoolean(Object obj) {
        if (obj instanceof Boolean) {
            return ((Boolean) obj).booleanValue();
        }
        else {
            final String className = obj.getClass().getName();
            runTimeError(DATA_CONVERSION_ERR, className, Boolean.TYPE);
            return false;
        }
    }


    public static String referenceToString(Object obj, DOM dom) {
        if (obj instanceof String) {
            return (String) obj;
        }
        else if (obj instanceof DTMAxisIterator) {
            return dom.getStringValueX(((DTMAxisIterator)obj).reset().next());
        }
        else if (obj instanceof Node) {
            return dom.getStringValueX(((Node)obj).node);
        }
        else if (obj instanceof DOM) {
            return ((DOM) obj).getStringValue();
        }
        else {
            final String className = obj.getClass().getName();
            runTimeError(DATA_CONVERSION_ERR, className, String.class);
            return null;
        }
    }


    public static DTMAxisIterator node2Iterator(org.w3c.dom.Node node,
        Translet translet, DOM dom)
    {
        final org.w3c.dom.Node inNode = node;
        org.w3c.dom.NodeList nodelist = new org.w3c.dom.NodeList() {
            public int getLength() {
                return 1;
            }

            public org.w3c.dom.Node item(int index) {
                if (index == 0)
                    return inNode;
                else
                    return null;
            }
        };

        return nodeList2Iterator(nodelist, translet, dom);
    }


    private static DTMAxisIterator nodeList2IteratorUsingHandleFromNode(
                                        org.w3c.dom.NodeList nodeList,
                                        Translet translet, DOM dom)
    {
        final int n = nodeList.getLength();
        final int[] dtmHandles = new int[n];
        DTMManager dtmManager = null;
        if (dom instanceof MultiDOM)
            dtmManager = ((MultiDOM) dom).getDTMManager();
        for (int i = 0; i < n; ++i) {
            org.w3c.dom.Node node = nodeList.item(i);
            int handle;
            if (dtmManager != null) {
                handle = dtmManager.getDTMHandleFromNode(node);
            }
            else if (node instanceof DTMNodeProxy
                     && ((DTMNodeProxy) node).getDTM() == dom) {
                handle = ((DTMNodeProxy) node).getDTMNodeNumber();
            }
            else {
                runTimeError(RUN_TIME_INTERNAL_ERR, "need MultiDOM");
                return null;
            }
            dtmHandles[i] = handle;
            System.out.println("Node " + i + " has handle 0x" +
                               Integer.toString(handle, 16));
        }
        return new ArrayNodeListIterator(dtmHandles);
    }


    public static DTMAxisIterator nodeList2Iterator(
                                        org.w3c.dom.NodeList nodeList,
                                        Translet translet, DOM dom)
    {
        int n = 0; Document doc = null;
        DTMManager dtmManager = null;
        int[] proxyNodes = new int[nodeList.getLength()];
        if (dom instanceof MultiDOM)
            dtmManager = ((MultiDOM) dom).getDTMManager();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            org.w3c.dom.Node node = nodeList.item(i);
            if (node instanceof DTMNodeProxy) {
                DTMNodeProxy proxy = (DTMNodeProxy)node;
                DTM nodeDTM = proxy.getDTM();
                int handle = proxy.getDTMNodeNumber();
                boolean isOurDOM = (nodeDTM == dom);
                if (!isOurDOM && dtmManager != null) {
                    try {
                        isOurDOM = (nodeDTM == dtmManager.getDTM(handle));
                    }
                    catch (ArrayIndexOutOfBoundsException e) {
                        }
                }
                if (isOurDOM) {
                    proxyNodes[i] = handle;
                    ++n;
                    continue;
                }
            }
            proxyNodes[i] = DTM.NULL;
            int nodeType = node.getNodeType();
            if (doc == null) {
                if (dom instanceof MultiDOM == false) {
                    runTimeError(RUN_TIME_INTERNAL_ERR, "need MultiDOM");
                    return null;
                }
                try {
                    AbstractTranslet at = (AbstractTranslet) translet;
                    doc = at.newDocument("", "__top__");
                }
                catch (javax.xml.parsers.ParserConfigurationException e) {
                    runTimeError(RUN_TIME_INTERNAL_ERR, e.getMessage());
                    return null;
                }
            }
            Element mid;
            switch (nodeType) {
                case org.w3c.dom.Node.ELEMENT_NODE:
                case org.w3c.dom.Node.TEXT_NODE:
                case org.w3c.dom.Node.CDATA_SECTION_NODE:
                case org.w3c.dom.Node.COMMENT_NODE:
                case org.w3c.dom.Node.ENTITY_REFERENCE_NODE:
                case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
                    mid = doc.createElementNS(null, "__dummy__");
                    mid.appendChild(doc.importNode(node, true));
                    doc.getDocumentElement().appendChild(mid);
                    ++n;
                    break;
                case org.w3c.dom.Node.ATTRIBUTE_NODE:
                    mid = doc.createElementNS(null, "__dummy__");
                    mid.setAttributeNodeNS((Attr)doc.importNode(node, true));
                    doc.getDocumentElement().appendChild(mid);
                    ++n;
                    break;
                default:
                    runTimeError(RUN_TIME_INTERNAL_ERR,
                                 "Don't know how to convert node type "
                                 + nodeType);
            }
        }

        DTMAxisIterator iter = null, childIter = null, attrIter = null;
        if (doc != null) {
            final MultiDOM multiDOM = (MultiDOM) dom;
            DOM idom = (DOM)dtmManager.getDTM(new DOMSource(doc), false,
                                              null, true, false);
            DOMAdapter domAdapter = new DOMAdapter(idom,
                translet.getNamesArray(),
                translet.getUrisArray(),
                translet.getTypesArray(),
                translet.getNamespaceArray());
            multiDOM.addDOMAdapter(domAdapter);

            DTMAxisIterator iter1 = idom.getAxisIterator(Axis.CHILD);
            DTMAxisIterator iter2 = idom.getAxisIterator(Axis.CHILD);
            iter = new AbsoluteIterator(
                new StepIterator(iter1, iter2));

            iter.setStartNode(DTMDefaultBase.ROOTNODE);

            childIter = idom.getAxisIterator(Axis.CHILD);
            attrIter = idom.getAxisIterator(Axis.ATTRIBUTE);
        }

        int[] dtmHandles = new int[n];
        n = 0;
        for (int i = 0; i < nodeList.getLength(); ++i) {
            if (proxyNodes[i] != DTM.NULL) {
                dtmHandles[n++] = proxyNodes[i];
                continue;
            }
            org.w3c.dom.Node node = nodeList.item(i);
            DTMAxisIterator iter3 = null;
            int nodeType = node.getNodeType();
            switch (nodeType) {
                case org.w3c.dom.Node.ELEMENT_NODE:
                case org.w3c.dom.Node.TEXT_NODE:
                case org.w3c.dom.Node.CDATA_SECTION_NODE:
                case org.w3c.dom.Node.COMMENT_NODE:
                case org.w3c.dom.Node.ENTITY_REFERENCE_NODE:
                case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
                    iter3 = childIter;
                    break;
                case org.w3c.dom.Node.ATTRIBUTE_NODE:
                    iter3 = attrIter;
                    break;
                default:
                    throw new InternalRuntimeError("Mismatched cases");
            }
            if (iter3 != null) {
                iter3.setStartNode(iter.next());
                dtmHandles[n] = iter3.next();
                if (dtmHandles[n] == DTMAxisIterator.END)
                    throw new InternalRuntimeError("Expected element missing at " + i);
                if (iter3.next() != DTMAxisIterator.END)
                    throw new InternalRuntimeError("Too many elements at " + i);
                ++n;
            }
        }
        if (n != dtmHandles.length)
            throw new InternalRuntimeError("Nodes lost in second pass");

        return new ArrayNodeListIterator(dtmHandles);
    }


    public static DOM referenceToResultTree(Object obj) {
        try {
            return ((DOM) obj);
        }
        catch (IllegalArgumentException e) {
            final String className = obj.getClass().getName();
            runTimeError(DATA_CONVERSION_ERR, "reference", className);
            return null;
        }
    }


    public static DTMAxisIterator getSingleNode(DTMAxisIterator iterator) {
        int node = iterator.next();
        return(new SingletonIterator(node));
    }


    private static char[] _characterArray = new char[32];

    public static void copy(Object obj,
                            SerializationHandler handler,
                            int node,
                            DOM dom) {
        try {
            if (obj instanceof DTMAxisIterator)
      {
                DTMAxisIterator iter = (DTMAxisIterator) obj;
                dom.copy(iter.reset(), handler);
            }
            else if (obj instanceof Node) {
                dom.copy(((Node) obj).node, handler);
            }
            else if (obj instanceof DOM) {
                DOM newDom = (DOM)obj;
                newDom.copy(newDom.getDocument(), handler);
            }
            else {
                String string = obj.toString();         final int length = string.length();
                if (length > _characterArray.length)
                    _characterArray = new char[length];
                string.getChars(0, length, _characterArray, 0);
                handler.characters(_characterArray, 0, length);
            }
        }
        catch (SAXException e) {
            runTimeError(RUN_TIME_COPY_ERR);
        }
    }


    public static void checkAttribQName(String name) {
        final int firstOccur = name.indexOf(":");
        final int lastOccur = name.lastIndexOf(":");
        final String localName = name.substring(lastOccur + 1);

        if (firstOccur > 0) {
            final String newPrefix = name.substring(0, firstOccur);

            if (firstOccur != lastOccur) {
               final String oriPrefix = name.substring(firstOccur+1, lastOccur);
                if (!XML11Char.isXML11ValidNCName(oriPrefix)) {
                    runTimeError(INVALID_QNAME_ERR,oriPrefix+":"+localName);
                }
            }

            if (!XML11Char.isXML11ValidNCName(newPrefix)) {
                runTimeError(INVALID_QNAME_ERR,newPrefix+":"+localName);
            }
        }

        if ((!XML11Char.isXML11ValidNCName(localName))||(localName.equals(Constants.XMLNS_PREFIX))) {
            runTimeError(INVALID_QNAME_ERR,localName);
        }
    }


    public static void checkNCName(String name) {
        if (!XML11Char.isXML11ValidNCName(name)) {
            runTimeError(INVALID_NCNAME_ERR,name);
        }
    }


    public static void checkQName(String name) {
        if (!XML11Char.isXML11ValidQName(name)) {
            runTimeError(INVALID_QNAME_ERR,name);
        }
    }


    public static String startXslElement(String qname, String namespace,
        SerializationHandler handler, DOM dom, int node)
    {
        try {
            String prefix;
            final int index = qname.indexOf(':');

            if (index > 0) {
                prefix = qname.substring(0, index);

                if (namespace == null || namespace.length() == 0) {
                    try {
                        namespace = dom.lookupNamespace(node, prefix);
                    }
                    catch(RuntimeException e) {
                        handler.flushPending();  NamespaceMappings nm = handler.getNamespaceMappings();
                        namespace = nm.lookupNamespace(prefix);
                        if (namespace == null) {
                            runTimeError(NAMESPACE_PREFIX_ERR,prefix);
                        }
                    }
                }

                handler.startElement(namespace, qname.substring(index+1),
                                         qname);
                handler.namespaceAfterStartElement(prefix, namespace);
            }
            else {
                if (namespace != null && namespace.length() > 0) {
                    prefix = generatePrefix();
                    qname = prefix + ':' + qname;
                    handler.startElement(namespace, qname, qname);
                    handler.namespaceAfterStartElement(prefix, namespace);
                }
                else {
                    handler.startElement(null, null, qname);
                }
            }
        }
        catch (SAXException e) {
            throw new RuntimeException(e.getMessage());
        }

        return qname;
    }


    public static String getPrefix(String qname) {
        final int index = qname.indexOf(':');
        return (index > 0) ? qname.substring(0, index) : null;
    }


    public static String generatePrefix() {
        return ("ns" + threadLocalPrefixIndex.get().getAndIncrement());
    }

    public static void resetPrefixIndex() {
        threadLocalPrefixIndex.get().set(0);
    }

    private static final ThreadLocal<AtomicInteger> threadLocalPrefixIndex =
        new ThreadLocal<AtomicInteger>() {
            @Override
            protected AtomicInteger initialValue() {
                return new AtomicInteger();
            }
        };

    public static final String RUN_TIME_INTERNAL_ERR =
                                           "RUN_TIME_INTERNAL_ERR";
    public static final String RUN_TIME_COPY_ERR =
                                           "RUN_TIME_COPY_ERR";
    public static final String DATA_CONVERSION_ERR =
                                           "DATA_CONVERSION_ERR";
    public static final String EXTERNAL_FUNC_ERR =
                                           "EXTERNAL_FUNC_ERR";
    public static final String EQUALITY_EXPR_ERR =
                                           "EQUALITY_EXPR_ERR";
    public static final String INVALID_ARGUMENT_ERR =
                                           "INVALID_ARGUMENT_ERR";
    public static final String FORMAT_NUMBER_ERR =
                                           "FORMAT_NUMBER_ERR";
    public static final String ITERATOR_CLONE_ERR =
                                           "ITERATOR_CLONE_ERR";
    public static final String AXIS_SUPPORT_ERR =
                                           "AXIS_SUPPORT_ERR";
    public static final String TYPED_AXIS_SUPPORT_ERR =
                                           "TYPED_AXIS_SUPPORT_ERR";
    public static final String STRAY_ATTRIBUTE_ERR =
                                           "STRAY_ATTRIBUTE_ERR";
    public static final String STRAY_NAMESPACE_ERR =
                                           "STRAY_NAMESPACE_ERR";
    public static final String NAMESPACE_PREFIX_ERR =
                                           "NAMESPACE_PREFIX_ERR";
    public static final String DOM_ADAPTER_INIT_ERR =
                                           "DOM_ADAPTER_INIT_ERR";
    public static final String PARSER_DTD_SUPPORT_ERR =
                                           "PARSER_DTD_SUPPORT_ERR";
    public static final String NAMESPACES_SUPPORT_ERR =
                                           "NAMESPACES_SUPPORT_ERR";
    public static final String CANT_RESOLVE_RELATIVE_URI_ERR =
                                           "CANT_RESOLVE_RELATIVE_URI_ERR";
    public static final String UNSUPPORTED_XSL_ERR =
                                           "UNSUPPORTED_XSL_ERR";
    public static final String UNSUPPORTED_EXT_ERR =
                                           "UNSUPPORTED_EXT_ERR";
    public static final String UNKNOWN_TRANSLET_VERSION_ERR =
                                           "UNKNOWN_TRANSLET_VERSION_ERR";
    public static final String INVALID_QNAME_ERR = "INVALID_QNAME_ERR";
    public static final String INVALID_NCNAME_ERR = "INVALID_NCNAME_ERR";
    public static final String UNALLOWED_EXTENSION_FUNCTION_ERR = "UNALLOWED_EXTENSION_FUNCTION_ERR";
    public static final String UNALLOWED_EXTENSION_ELEMENT_ERR = "UNALLOWED_EXTENSION_ELEMENT_ERR";

    private static ResourceBundle m_bundle;

    public final static String ERROR_MESSAGES_KEY = "error-messages";

    static {
        String resource = "com.sun.org.apache.xalan.internal.xsltc.runtime.ErrorMessages";
        m_bundle = SecuritySupport.getResourceBundle(resource);
    }


    public static void runTimeError(String code) {
        throw new RuntimeException(m_bundle.getString(code));
    }

    public static void runTimeError(String code, Object[] args) {
        final String message = MessageFormat.format(m_bundle.getString(code),
                                                    args);
        throw new RuntimeException(message);
    }

    public static void runTimeError(String code, Object arg0) {
        runTimeError(code, new Object[]{ arg0 } );
    }

    public static void runTimeError(String code, Object arg0, Object arg1) {
        runTimeError(code, new Object[]{ arg0, arg1 } );
    }

    public static void consoleOutput(String msg) {
        System.out.println(msg);
    }


    public static String replace(String base, char ch, String str) {
        return (base.indexOf(ch) < 0) ? base :
            replace(base, String.valueOf(ch), new String[] { str });
    }

    public static String replace(String base, String delim, String[] str) {
        final int len = base.length();
        final StringBuilder result = threadLocalStringBuilder.get();
        result.setLength(0);

        for (int i = 0; i < len; i++) {
            final char ch = base.charAt(i);
            final int k = delim.indexOf(ch);

            if (k >= 0) {
                result.append(str[k]);
            }
            else {
                result.append(ch);
            }
        }
        return result.toString();
    }



    public static String mapQNameToJavaName (String base ) {
       return replace(base, ".-:/{}?#%*",
                      new String[] { "$dot$", "$dash$" ,"$colon$", "$slash$",
                                     "","$colon$","$ques$","$hash$","$per$",
                                     "$aster$"});

    }


    public static int getStringLength(String str) {
        return str.codePointCount(0,str.length());
    }

    }
