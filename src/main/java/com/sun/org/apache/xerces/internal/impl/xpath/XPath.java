


package com.sun.org.apache.xerces.internal.impl.xpath;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class XPath {

    private static final boolean DEBUG_ALL = false;

    private static final boolean DEBUG_XPATH_PARSE = DEBUG_ALL || false;

    private static final boolean DEBUG_ANY = DEBUG_XPATH_PARSE;

    protected String fExpression;


    protected SymbolTable fSymbolTable;


    protected LocationPath[] fLocationPaths;

    public XPath(String xpath, SymbolTable symbolTable,
                 NamespaceContext context)
        throws XPathException {
        fExpression = xpath;
        fSymbolTable = symbolTable;
        parseExpression(context);
    } public LocationPath[] getLocationPaths() {
        LocationPath[] ret=new LocationPath[fLocationPaths.length];
        for (int i=0;i<fLocationPaths.length;i++){
            ret[i]=(LocationPath)fLocationPaths[i].clone();
        }
        return ret;
    } public LocationPath getLocationPath() {
        return (LocationPath)fLocationPaths[0].clone();
    } public String toString() {
        StringBuffer buf=new StringBuffer();
        for (int  i=0;i<fLocationPaths.length;i++){
            if (i>0){
                buf.append("|");
            }
            buf.append(fLocationPaths[i].toString());
        }
        return buf.toString();
    } private static void check( boolean b ) throws XPathException {
        if(!b)      throw new XPathException("c-general-xpath");
    }


    private LocationPath buildLocationPath( Vector stepsVector ) throws XPathException {
        int size = stepsVector.size();
        check(size!=0);
        Step[] steps = new Step[size];
        stepsVector.copyInto(steps);
        stepsVector.removeAllElements();

        return new LocationPath(steps);
    }


    private void parseExpression(final NamespaceContext context)
        throws XPathException {

        final XPath.Tokens xtokens = new XPath.Tokens(fSymbolTable);

        XPath.Scanner scanner = new XPath.Scanner(fSymbolTable) {
            protected void addToken(XPath.Tokens tokens, int token)
                throws XPathException {
                if (
                    token == XPath.Tokens.EXPRTOKEN_ATSIGN ||
                    token == XPath.Tokens.EXPRTOKEN_AXISNAME_ATTRIBUTE ||
                    token == XPath.Tokens.EXPRTOKEN_NAMETEST_QNAME ||
                    token == XPath.Tokens.EXPRTOKEN_OPERATOR_SLASH ||
                    token == XPath.Tokens.EXPRTOKEN_PERIOD ||
                    token == XPath.Tokens.EXPRTOKEN_NAMETEST_ANY ||
                    token == XPath.Tokens.EXPRTOKEN_NAMETEST_NAMESPACE ||
                    token == XPath.Tokens.EXPRTOKEN_OPERATOR_DOUBLE_SLASH ||
                    token == XPath.Tokens.EXPRTOKEN_OPERATOR_UNION ||
                    token == XPath.Tokens.EXPRTOKEN_AXISNAME_CHILD ||
                    token == XPath.Tokens.EXPRTOKEN_DOUBLE_COLON
                    ) {
                    super.addToken(tokens, token);
                    return;
                }
                throw new XPathException("c-general-xpath");
            }
        };

        int length = fExpression.length();

        boolean success = scanner.scanExpr(fSymbolTable,
                                           xtokens, fExpression, 0, length);
        if(!success)
            throw new XPathException("c-general-xpath");

        Vector stepsVector = new Vector();
        Vector locationPathsVector= new Vector();

        boolean expectingStep = true;
        boolean expectingDoubleColon = false;

        while(xtokens.hasMore()) {
            final int token = xtokens.nextToken();

            switch (token) {
                case  XPath.Tokens.EXPRTOKEN_OPERATOR_UNION :{
                    check(!expectingStep);
                    locationPathsVector.addElement(buildLocationPath(stepsVector));
                    expectingStep=true;
                    break;
                }

                case XPath.Tokens.EXPRTOKEN_ATSIGN: {
                    check(expectingStep);
                    Step step = new Step(
                            new Axis(Axis.ATTRIBUTE),
                            parseNodeTest(xtokens.nextToken(),xtokens,context));
                    stepsVector.addElement(step);
                    expectingStep=false;
                    break;
                }
                case XPath.Tokens.EXPRTOKEN_NAMETEST_ANY:
                case XPath.Tokens.EXPRTOKEN_NAMETEST_NAMESPACE:
                case XPath.Tokens.EXPRTOKEN_NAMETEST_QNAME: {
                    check(expectingStep);
                    Step step = new Step(
                            new Axis(Axis.CHILD),
                            parseNodeTest(token,xtokens,context));
                    stepsVector.addElement(step);
                    expectingStep=false;
                    break;
                }

                case XPath.Tokens.EXPRTOKEN_PERIOD: {
                    check(expectingStep);
                    expectingStep=false;

                    if (stepsVector.size()==0) {
                        Axis axis = new Axis(Axis.SELF);
                        NodeTest nodeTest = new NodeTest(NodeTest.NODE);
                        Step step = new Step(axis, nodeTest);
                        stepsVector.addElement(step);

                        if( xtokens.hasMore()
                         && xtokens.peekToken() == XPath.Tokens.EXPRTOKEN_OPERATOR_DOUBLE_SLASH){
                            xtokens.nextToken();

                            axis = new Axis(Axis.DESCENDANT);
                            nodeTest = new NodeTest(NodeTest.NODE);
                            step = new Step(axis, nodeTest);
                            stepsVector.addElement(step);
                            expectingStep=true;
                        }
                    }
                    break;
                }

                case XPath.Tokens.EXPRTOKEN_OPERATOR_DOUBLE_SLASH:{
                    throw new XPathException("c-general-xpath");
                }
                case XPath.Tokens.EXPRTOKEN_OPERATOR_SLASH: {
                    check(!expectingStep);
                    expectingStep=true;
                    break;
                }
                case XPath.Tokens.EXPRTOKEN_AXISNAME_ATTRIBUTE: {
                     check(expectingStep);
                     expectingDoubleColon = true;

                     if (xtokens.nextToken() == XPath.Tokens.EXPRTOKEN_DOUBLE_COLON){
                         Step step = new Step(
                         new Axis(Axis.ATTRIBUTE),
                                 parseNodeTest(xtokens.nextToken(),xtokens,context));
                         stepsVector.addElement(step);
                         expectingStep=false;
                         expectingDoubleColon = false;
                     }
                     break;
                }
                case XPath.Tokens.EXPRTOKEN_AXISNAME_CHILD:{
                    check(expectingStep);
                    expectingDoubleColon = true;
                    break;
                }
                case XPath.Tokens.EXPRTOKEN_DOUBLE_COLON :{
                    check(expectingStep);
                    check(expectingDoubleColon);
                    expectingDoubleColon = false;
                    break;
                }
                default:
                    throw new XPathException("c-general-xpath");
           }
        }

        check(!expectingStep);

        locationPathsVector.addElement(buildLocationPath(stepsVector));

        fLocationPaths=new LocationPath[locationPathsVector.size()];
        locationPathsVector.copyInto(fLocationPaths);


        if (DEBUG_XPATH_PARSE) {
            System.out.println(">>> "+fLocationPaths);
        }

    } private NodeTest parseNodeTest( int typeToken, Tokens xtokens, NamespaceContext context )
        throws XPathException {
        switch(typeToken) {
        case XPath.Tokens.EXPRTOKEN_NAMETEST_ANY:
            return new NodeTest(NodeTest.WILDCARD);

        case XPath.Tokens.EXPRTOKEN_NAMETEST_NAMESPACE:
        case XPath.Tokens.EXPRTOKEN_NAMETEST_QNAME:
            String prefix = xtokens.nextTokenAsString();
            String uri = null;
            if (context != null && prefix != XMLSymbols.EMPTY_STRING) {
                uri = context.getURI(prefix);
            }
            if (prefix != XMLSymbols.EMPTY_STRING && context != null && uri == null) {
                throw new XPathException("c-general-xpath-ns");
            }

            if (typeToken==XPath.Tokens.EXPRTOKEN_NAMETEST_NAMESPACE)
                return new NodeTest(prefix,uri);

            String localpart = xtokens.nextTokenAsString();
            String rawname = prefix != XMLSymbols.EMPTY_STRING
            ? fSymbolTable.addSymbol(prefix+':'+localpart)
            : localpart;

            return new NodeTest(new QName(prefix, localpart, rawname, uri));

        default:
            throw new XPathException("c-general-xpath");

        }
    }


    public static class LocationPath
        implements Cloneable {

        public Step[] steps;

        public LocationPath(Step[] steps) {
            this.steps = steps;
        } protected LocationPath(LocationPath path) {
            steps = new Step[path.steps.length];
            for (int i = 0; i < steps.length; i++) {
                steps[i] = (Step)path.steps[i].clone();
            }
        } public String toString() {
            StringBuffer str = new StringBuffer();
            for (int i = 0; i < steps.length; i++) {
                if (i > 0       && (steps[i-1].axis.type!=Axis.DESCENDANT
                    && steps[i].axis.type!=Axis.DESCENDANT) ){
                    str.append('/');
                }
                str.append(steps[i].toString());
            }
            if (false) {
                str.append('[');
                String s = super.toString();
                str.append(s.substring(s.indexOf('@')));
                str.append(']');
            }
            return str.toString();
        } public Object clone() {
            return new LocationPath(this);
        } } public static class Step
        implements Cloneable {

        public Axis axis;


        public NodeTest nodeTest;

        public Step(Axis axis, NodeTest nodeTest) {
            this.axis = axis;
            this.nodeTest = nodeTest;
        } protected Step(Step step) {
            axis = (Axis)step.axis.clone();
            nodeTest = (NodeTest)step.nodeTest.clone();
        } public String toString() {
            if (axis.type == Axis.SELF) {
                return ".";
            }
            if (axis.type == Axis.ATTRIBUTE) {
                return "@" + nodeTest.toString();
            }
            if (axis.type == Axis.CHILD) {
                return nodeTest.toString();
            }
            if (axis.type == Axis.DESCENDANT) {
                return "}
            return "??? ("+axis.type+')';
        } public Object clone() {
            return new Step(this);
        } } public static class Axis
        implements Cloneable {

        public static final short CHILD = 1;


        public static final short ATTRIBUTE = 2;


        public static final short SELF = 3;



        public static final short DESCENDANT = 4;
        public short type;

        public Axis(short type) {
            this.type = type;
        } protected Axis(Axis axis) {
            type = axis.type;
        } public String toString() {
            switch (type) {
                case CHILD: return "child";
                case ATTRIBUTE: return "attribute";
                case SELF: return "self";
                case DESCENDANT: return "descendant";
            }
            return "???";
        } public Object clone() {
            return new Axis(this);
        } } public static class NodeTest
        implements Cloneable {

        public static final short QNAME = 1;


        public static final short WILDCARD = 2;


        public static final short NODE = 3;


        public static final short NAMESPACE= 4;

        public short type;


        public final QName name = new QName();

        public NodeTest(short type) {
            this.type = type;
        } public NodeTest(QName name) {
            this.type = QNAME;
            this.name.setValues(name);
        } public NodeTest(String prefix, String uri) {
            this.type = NAMESPACE;
            this.name.setValues(prefix, null, null, uri);
        } public NodeTest(NodeTest nodeTest) {
            type = nodeTest.type;
            name.setValues(nodeTest.name);
        } public String toString() {

            switch (type) {
                case QNAME: {
                    if (name.prefix.length() !=0) {
                        if (name.uri != null) {
                            return name.prefix+':'+name.localpart;
                        }
                        return "{"+name.uri+'}'+name.prefix+':'+name.localpart;
                    }
                    return name.localpart;
                }
                case NAMESPACE: {
                    if (name.prefix.length() !=0) {
                        if (name.uri != null) {
                            return name.prefix+":*";
                        }
                        return "{"+name.uri+'}'+name.prefix+":*";
                    }
                    return "???:*";
                }
                case WILDCARD: {
                    return "*";
                }
                case NODE: {
                    return "node()";
                }
            }
            return "???";

        } public Object clone() {
            return new NodeTest(this);
        } } private static final class Tokens {

        static final boolean DUMP_TOKENS = false;


        public static final int
            EXPRTOKEN_OPEN_PAREN                    =   0,
            EXPRTOKEN_CLOSE_PAREN                   =   1,
            EXPRTOKEN_OPEN_BRACKET                  =   2,
            EXPRTOKEN_CLOSE_BRACKET                 =   3,
            EXPRTOKEN_PERIOD                        =   4,
            EXPRTOKEN_DOUBLE_PERIOD                 =   5,
            EXPRTOKEN_ATSIGN                        =   6,
            EXPRTOKEN_COMMA                         =   7,
            EXPRTOKEN_DOUBLE_COLON                  =   8,
            EXPRTOKEN_NAMETEST_ANY                  =   9,
            EXPRTOKEN_NAMETEST_NAMESPACE            =  10,
            EXPRTOKEN_NAMETEST_QNAME                =  11,
            EXPRTOKEN_NODETYPE_COMMENT              =  12,
            EXPRTOKEN_NODETYPE_TEXT                 =  13,
            EXPRTOKEN_NODETYPE_PI                   =  14,
            EXPRTOKEN_NODETYPE_NODE                 =  15,
            EXPRTOKEN_OPERATOR_AND                  =  16,
            EXPRTOKEN_OPERATOR_OR                   =  17,
            EXPRTOKEN_OPERATOR_MOD                  =  18,
            EXPRTOKEN_OPERATOR_DIV                  =  19,
            EXPRTOKEN_OPERATOR_MULT                 =  20,
            EXPRTOKEN_OPERATOR_SLASH                =  21,
            EXPRTOKEN_OPERATOR_DOUBLE_SLASH         =  22,
            EXPRTOKEN_OPERATOR_UNION                =  23,
            EXPRTOKEN_OPERATOR_PLUS                 =  24,
            EXPRTOKEN_OPERATOR_MINUS                =  25,
            EXPRTOKEN_OPERATOR_EQUAL                =  26,
            EXPRTOKEN_OPERATOR_NOT_EQUAL            =  27,
            EXPRTOKEN_OPERATOR_LESS                 =  28,
            EXPRTOKEN_OPERATOR_LESS_EQUAL           =  29,
            EXPRTOKEN_OPERATOR_GREATER              =  30,
            EXPRTOKEN_OPERATOR_GREATER_EQUAL        =  31,

            EXPRTOKEN_FUNCTION_NAME                 =  32,
            EXPRTOKEN_AXISNAME_ANCESTOR             =  33,
            EXPRTOKEN_AXISNAME_ANCESTOR_OR_SELF     =  34,
            EXPRTOKEN_AXISNAME_ATTRIBUTE            =  35,
            EXPRTOKEN_AXISNAME_CHILD                =  36,
            EXPRTOKEN_AXISNAME_DESCENDANT           =  37,
            EXPRTOKEN_AXISNAME_DESCENDANT_OR_SELF   =  38,
            EXPRTOKEN_AXISNAME_FOLLOWING            =  39,
            EXPRTOKEN_AXISNAME_FOLLOWING_SIBLING    =  40,
            EXPRTOKEN_AXISNAME_NAMESPACE            =  41,
            EXPRTOKEN_AXISNAME_PARENT               =  42,
            EXPRTOKEN_AXISNAME_PRECEDING            =  43,
            EXPRTOKEN_AXISNAME_PRECEDING_SIBLING    =  44,
            EXPRTOKEN_AXISNAME_SELF                 =  45,
            EXPRTOKEN_LITERAL                       =  46,
            EXPRTOKEN_NUMBER                        =  47,
            EXPRTOKEN_VARIABLE_REFERENCE            =  48;

        private static final String[] fgTokenNames = {
            "EXPRTOKEN_OPEN_PAREN",
            "EXPRTOKEN_CLOSE_PAREN",
            "EXPRTOKEN_OPEN_BRACKET",
            "EXPRTOKEN_CLOSE_BRACKET",
            "EXPRTOKEN_PERIOD",
            "EXPRTOKEN_DOUBLE_PERIOD",
            "EXPRTOKEN_ATSIGN",
            "EXPRTOKEN_COMMA",
            "EXPRTOKEN_DOUBLE_COLON",
            "EXPRTOKEN_NAMETEST_ANY",
            "EXPRTOKEN_NAMETEST_NAMESPACE",
            "EXPRTOKEN_NAMETEST_QNAME",
            "EXPRTOKEN_NODETYPE_COMMENT",
            "EXPRTOKEN_NODETYPE_TEXT",
            "EXPRTOKEN_NODETYPE_PI",
            "EXPRTOKEN_NODETYPE_NODE",
            "EXPRTOKEN_OPERATOR_AND",
            "EXPRTOKEN_OPERATOR_OR",
            "EXPRTOKEN_OPERATOR_MOD",
            "EXPRTOKEN_OPERATOR_DIV",
            "EXPRTOKEN_OPERATOR_MULT",
            "EXPRTOKEN_OPERATOR_SLASH",
            "EXPRTOKEN_OPERATOR_DOUBLE_SLASH",
            "EXPRTOKEN_OPERATOR_UNION",
            "EXPRTOKEN_OPERATOR_PLUS",
            "EXPRTOKEN_OPERATOR_MINUS",
            "EXPRTOKEN_OPERATOR_EQUAL",
            "EXPRTOKEN_OPERATOR_NOT_EQUAL",
            "EXPRTOKEN_OPERATOR_LESS",
            "EXPRTOKEN_OPERATOR_LESS_EQUAL",
            "EXPRTOKEN_OPERATOR_GREATER",
            "EXPRTOKEN_OPERATOR_GREATER_EQUAL",
            "EXPRTOKEN_FUNCTION_NAME",
            "EXPRTOKEN_AXISNAME_ANCESTOR",
            "EXPRTOKEN_AXISNAME_ANCESTOR_OR_SELF",
            "EXPRTOKEN_AXISNAME_ATTRIBUTE",
            "EXPRTOKEN_AXISNAME_CHILD",
            "EXPRTOKEN_AXISNAME_DESCENDANT",
            "EXPRTOKEN_AXISNAME_DESCENDANT_OR_SELF",
            "EXPRTOKEN_AXISNAME_FOLLOWING",
            "EXPRTOKEN_AXISNAME_FOLLOWING_SIBLING",
            "EXPRTOKEN_AXISNAME_NAMESPACE",
            "EXPRTOKEN_AXISNAME_PARENT",
            "EXPRTOKEN_AXISNAME_PRECEDING",
            "EXPRTOKEN_AXISNAME_PRECEDING_SIBLING",
            "EXPRTOKEN_AXISNAME_SELF",
            "EXPRTOKEN_LITERAL",
            "EXPRTOKEN_NUMBER",
            "EXPRTOKEN_VARIABLE_REFERENCE"
        };


        private static final int INITIAL_TOKEN_COUNT = 1 << 8;
        private int[] fTokens = new int[INITIAL_TOKEN_COUNT];
        private int fTokenCount = 0;    private SymbolTable fSymbolTable;

        private Map<String, Integer> fSymbolMapping = new HashMap<>();

        private Map<Integer, String> fTokenNames = new HashMap<>();


        private int fCurrentTokenIndex;

        public Tokens(SymbolTable symbolTable) {
            fSymbolTable = symbolTable;
            final String[] symbols = {
                "ancestor",     "ancestor-or-self",     "attribute",
                "child",        "descendant",           "descendant-or-self",
                "following",    "following-sibling",    "namespace",
                "parent",       "preceding",            "preceding-sibling",
                "self",
            };
            for (int i = 0; i < symbols.length; i++) {
                fSymbolMapping.put(fSymbolTable.addSymbol(symbols[i]), i);
            }
            fTokenNames.put(EXPRTOKEN_OPEN_PAREN, "EXPRTOKEN_OPEN_PAREN");
            fTokenNames.put(EXPRTOKEN_CLOSE_PAREN, "EXPRTOKEN_CLOSE_PAREN");
            fTokenNames.put(EXPRTOKEN_OPEN_BRACKET, "EXPRTOKEN_OPEN_BRACKET");
            fTokenNames.put(EXPRTOKEN_CLOSE_BRACKET, "EXPRTOKEN_CLOSE_BRACKET");
            fTokenNames.put(EXPRTOKEN_PERIOD, "EXPRTOKEN_PERIOD");
            fTokenNames.put(EXPRTOKEN_DOUBLE_PERIOD, "EXPRTOKEN_DOUBLE_PERIOD");
            fTokenNames.put(EXPRTOKEN_ATSIGN, "EXPRTOKEN_ATSIGN");
            fTokenNames.put(EXPRTOKEN_COMMA, "EXPRTOKEN_COMMA");
            fTokenNames.put(EXPRTOKEN_DOUBLE_COLON, "EXPRTOKEN_DOUBLE_COLON");
            fTokenNames.put(EXPRTOKEN_NAMETEST_ANY, "EXPRTOKEN_NAMETEST_ANY");
            fTokenNames.put(EXPRTOKEN_NAMETEST_NAMESPACE, "EXPRTOKEN_NAMETEST_NAMESPACE");
            fTokenNames.put(EXPRTOKEN_NAMETEST_QNAME, "EXPRTOKEN_NAMETEST_QNAME");
            fTokenNames.put(EXPRTOKEN_NODETYPE_COMMENT, "EXPRTOKEN_NODETYPE_COMMENT");
            fTokenNames.put(EXPRTOKEN_NODETYPE_TEXT, "EXPRTOKEN_NODETYPE_TEXT");
            fTokenNames.put(EXPRTOKEN_NODETYPE_PI, "EXPRTOKEN_NODETYPE_PI");
            fTokenNames.put(EXPRTOKEN_NODETYPE_NODE, "EXPRTOKEN_NODETYPE_NODE");
            fTokenNames.put(EXPRTOKEN_OPERATOR_AND, "EXPRTOKEN_OPERATOR_AND");
            fTokenNames.put(EXPRTOKEN_OPERATOR_OR, "EXPRTOKEN_OPERATOR_OR");
            fTokenNames.put(EXPRTOKEN_OPERATOR_MOD, "EXPRTOKEN_OPERATOR_MOD");
            fTokenNames.put(EXPRTOKEN_OPERATOR_DIV, "EXPRTOKEN_OPERATOR_DIV");
            fTokenNames.put(EXPRTOKEN_OPERATOR_MULT, "EXPRTOKEN_OPERATOR_MULT");
            fTokenNames.put(EXPRTOKEN_OPERATOR_SLASH, "EXPRTOKEN_OPERATOR_SLASH");
            fTokenNames.put(EXPRTOKEN_OPERATOR_DOUBLE_SLASH, "EXPRTOKEN_OPERATOR_DOUBLE_SLASH");
            fTokenNames.put(EXPRTOKEN_OPERATOR_UNION, "EXPRTOKEN_OPERATOR_UNION");
            fTokenNames.put(EXPRTOKEN_OPERATOR_PLUS, "EXPRTOKEN_OPERATOR_PLUS");
            fTokenNames.put(EXPRTOKEN_OPERATOR_MINUS, "EXPRTOKEN_OPERATOR_MINUS");
            fTokenNames.put(EXPRTOKEN_OPERATOR_EQUAL, "EXPRTOKEN_OPERATOR_EQUAL");
            fTokenNames.put(EXPRTOKEN_OPERATOR_NOT_EQUAL, "EXPRTOKEN_OPERATOR_NOT_EQUAL");
            fTokenNames.put(EXPRTOKEN_OPERATOR_LESS, "EXPRTOKEN_OPERATOR_LESS");
            fTokenNames.put(EXPRTOKEN_OPERATOR_LESS_EQUAL, "EXPRTOKEN_OPERATOR_LESS_EQUAL");
            fTokenNames.put(EXPRTOKEN_OPERATOR_GREATER, "EXPRTOKEN_OPERATOR_GREATER");
            fTokenNames.put(EXPRTOKEN_OPERATOR_GREATER_EQUAL, "EXPRTOKEN_OPERATOR_GREATER_EQUAL");
            fTokenNames.put(EXPRTOKEN_FUNCTION_NAME, "EXPRTOKEN_FUNCTION_NAME");
            fTokenNames.put(EXPRTOKEN_AXISNAME_ANCESTOR, "EXPRTOKEN_AXISNAME_ANCESTOR");
            fTokenNames.put(EXPRTOKEN_AXISNAME_ANCESTOR_OR_SELF, "EXPRTOKEN_AXISNAME_ANCESTOR_OR_SELF");
            fTokenNames.put(EXPRTOKEN_AXISNAME_ATTRIBUTE, "EXPRTOKEN_AXISNAME_ATTRIBUTE");
            fTokenNames.put(EXPRTOKEN_AXISNAME_CHILD, "EXPRTOKEN_AXISNAME_CHILD");
            fTokenNames.put(EXPRTOKEN_AXISNAME_DESCENDANT, "EXPRTOKEN_AXISNAME_DESCENDANT");
            fTokenNames.put(EXPRTOKEN_AXISNAME_DESCENDANT_OR_SELF, "EXPRTOKEN_AXISNAME_DESCENDANT_OR_SELF");
            fTokenNames.put(EXPRTOKEN_AXISNAME_FOLLOWING, "EXPRTOKEN_AXISNAME_FOLLOWING");
            fTokenNames.put(EXPRTOKEN_AXISNAME_FOLLOWING_SIBLING, "EXPRTOKEN_AXISNAME_FOLLOWING_SIBLING");
            fTokenNames.put(EXPRTOKEN_AXISNAME_NAMESPACE, "EXPRTOKEN_AXISNAME_NAMESPACE");
            fTokenNames.put(EXPRTOKEN_AXISNAME_PARENT, "EXPRTOKEN_AXISNAME_PARENT");
            fTokenNames.put(EXPRTOKEN_AXISNAME_PRECEDING, "EXPRTOKEN_AXISNAME_PRECEDING");
            fTokenNames.put(EXPRTOKEN_AXISNAME_PRECEDING_SIBLING, "EXPRTOKEN_AXISNAME_PRECEDING_SIBLING");
            fTokenNames.put(EXPRTOKEN_AXISNAME_SELF, "EXPRTOKEN_AXISNAME_SELF");
            fTokenNames.put(EXPRTOKEN_LITERAL, "EXPRTOKEN_LITERAL");
            fTokenNames.put(EXPRTOKEN_NUMBER, "EXPRTOKEN_NUMBER");
            fTokenNames.put(EXPRTOKEN_VARIABLE_REFERENCE, "EXPRTOKEN_VARIABLE_REFERENCE");
        }

        public String getTokenString(int token) {
            return fTokenNames.get(token);
        }

        public void addToken(String tokenStr) {
            Integer tokenInt = null;
            for (Map.Entry<Integer, String> entry : fTokenNames.entrySet()) {
                if (entry.getValue().equals(tokenStr)) {
                    tokenInt = entry.getKey();
                }
            }
            if (tokenInt == null) {
                tokenInt = fTokenNames.size();
                fTokenNames.put(tokenInt, tokenStr);
            }
            addToken(tokenInt);
        }

        public void addToken(int token) {
            try {
                fTokens[fTokenCount] = token;
            } catch (ArrayIndexOutOfBoundsException ex) {
                int[] oldList = fTokens;
                fTokens = new int[fTokenCount << 1];
                System.arraycopy(oldList, 0, fTokens, 0, fTokenCount);
                fTokens[fTokenCount] = token;
            }
            fTokenCount++;
        }
public void rewind() {
            fCurrentTokenIndex=0;
        }

        public boolean hasMore() {
            return fCurrentTokenIndex<fTokenCount;
        }

        public int nextToken() throws XPathException {
            if( fCurrentTokenIndex==fTokenCount )
                throw new XPathException("c-general-xpath");
            return fTokens[fCurrentTokenIndex++];
        }

        public int peekToken() throws XPathException {
            if( fCurrentTokenIndex==fTokenCount )
                throw new XPathException("c-general-xpath");
            return fTokens[fCurrentTokenIndex];
        }

        public String nextTokenAsString() throws XPathException {
            String s = getTokenString(nextToken());
            if(s==null)     throw new XPathException("c-general-xpath");
            return s;
        }

        public void dumpTokens() {
            for (int i = 0; i < fTokenCount; i++) {
                    switch (fTokens[i]) {
                    case EXPRTOKEN_OPEN_PAREN:
                        System.out.print("<OPEN_PAREN/>");
                        break;
                    case EXPRTOKEN_CLOSE_PAREN:
                        System.out.print("<CLOSE_PAREN/>");
                        break;
                    case EXPRTOKEN_OPEN_BRACKET:
                        System.out.print("<OPEN_BRACKET/>");
                        break;
                    case EXPRTOKEN_CLOSE_BRACKET:
                        System.out.print("<CLOSE_BRACKET/>");
                        break;
                    case EXPRTOKEN_PERIOD:
                        System.out.print("<PERIOD/>");
                        break;
                    case EXPRTOKEN_DOUBLE_PERIOD:
                        System.out.print("<DOUBLE_PERIOD/>");
                        break;
                    case EXPRTOKEN_ATSIGN:
                        System.out.print("<ATSIGN/>");
                        break;
                    case EXPRTOKEN_COMMA:
                        System.out.print("<COMMA/>");
                        break;
                    case EXPRTOKEN_DOUBLE_COLON:
                        System.out.print("<DOUBLE_COLON/>");
                        break;
                    case EXPRTOKEN_NAMETEST_ANY:
                        System.out.print("<NAMETEST_ANY/>");
                        break;
                    case EXPRTOKEN_NAMETEST_NAMESPACE:
                        System.out.print("<NAMETEST_NAMESPACE");
                        System.out.print(" prefix=\"" + getTokenString(fTokens[++i]) + "\"");
                        System.out.print("/>");
                        break;
                    case EXPRTOKEN_NAMETEST_QNAME:
                        System.out.print("<NAMETEST_QNAME");
                        if (fTokens[++i] != -1)
                            System.out.print(" prefix=\"" + getTokenString(fTokens[i]) + "\"");
                        System.out.print(" localpart=\"" + getTokenString(fTokens[++i]) + "\"");
                        System.out.print("/>");
                        break;
                    case EXPRTOKEN_NODETYPE_COMMENT:
                        System.out.print("<NODETYPE_COMMENT/>");
                        break;
                    case EXPRTOKEN_NODETYPE_TEXT:
                        System.out.print("<NODETYPE_TEXT/>");
                        break;
                    case EXPRTOKEN_NODETYPE_PI:
                        System.out.print("<NODETYPE_PI/>");
                        break;
                    case EXPRTOKEN_NODETYPE_NODE:
                        System.out.print("<NODETYPE_NODE/>");
                        break;
                    case EXPRTOKEN_OPERATOR_AND:
                        System.out.print("<OPERATOR_AND/>");
                        break;
                    case EXPRTOKEN_OPERATOR_OR:
                        System.out.print("<OPERATOR_OR/>");
                        break;
                    case EXPRTOKEN_OPERATOR_MOD:
                        System.out.print("<OPERATOR_MOD/>");
                        break;
                    case EXPRTOKEN_OPERATOR_DIV:
                        System.out.print("<OPERATOR_DIV/>");
                        break;
                    case EXPRTOKEN_OPERATOR_MULT:
                        System.out.print("<OPERATOR_MULT/>");
                        break;
                    case EXPRTOKEN_OPERATOR_SLASH:
                        System.out.print("<OPERATOR_SLASH/>");
                        if (i + 1 < fTokenCount) {
                            System.out.println();
                            System.out.print("  ");
                        }
                        break;
                    case EXPRTOKEN_OPERATOR_DOUBLE_SLASH:
                        System.out.print("<OPERATOR_DOUBLE_SLASH/>");
                        break;
                    case EXPRTOKEN_OPERATOR_UNION:
                        System.out.print("<OPERATOR_UNION/>");
                        break;
                    case EXPRTOKEN_OPERATOR_PLUS:
                        System.out.print("<OPERATOR_PLUS/>");
                        break;
                    case EXPRTOKEN_OPERATOR_MINUS:
                        System.out.print("<OPERATOR_MINUS/>");
                        break;
                    case EXPRTOKEN_OPERATOR_EQUAL:
                        System.out.print("<OPERATOR_EQUAL/>");
                        break;
                    case EXPRTOKEN_OPERATOR_NOT_EQUAL:
                        System.out.print("<OPERATOR_NOT_EQUAL/>");
                        break;
                    case EXPRTOKEN_OPERATOR_LESS:
                        System.out.print("<OPERATOR_LESS/>");
                        break;
                    case EXPRTOKEN_OPERATOR_LESS_EQUAL:
                        System.out.print("<OPERATOR_LESS_EQUAL/>");
                        break;
                    case EXPRTOKEN_OPERATOR_GREATER:
                        System.out.print("<OPERATOR_GREATER/>");
                        break;
                    case EXPRTOKEN_OPERATOR_GREATER_EQUAL:
                        System.out.print("<OPERATOR_GREATER_EQUAL/>");
                        break;
                    case EXPRTOKEN_FUNCTION_NAME:
                        System.out.print("<FUNCTION_NAME");
                        if (fTokens[++i] != -1)
                            System.out.print(" prefix=\"" + getTokenString(fTokens[i]) + "\"");
                        System.out.print(" localpart=\"" + getTokenString(fTokens[++i]) + "\"");
                        System.out.print("/>");
                        break;
                    case EXPRTOKEN_AXISNAME_ANCESTOR:
                        System.out.print("<AXISNAME_ANCESTOR/>");
                        break;
                    case EXPRTOKEN_AXISNAME_ANCESTOR_OR_SELF:
                        System.out.print("<AXISNAME_ANCESTOR_OR_SELF/>");
                        break;
                    case EXPRTOKEN_AXISNAME_ATTRIBUTE:
                        System.out.print("<AXISNAME_ATTRIBUTE/>");
                        break;
                    case EXPRTOKEN_AXISNAME_CHILD:
                        System.out.print("<AXISNAME_CHILD/>");
                        break;
                    case EXPRTOKEN_AXISNAME_DESCENDANT:
                        System.out.print("<AXISNAME_DESCENDANT/>");
                        break;
                    case EXPRTOKEN_AXISNAME_DESCENDANT_OR_SELF:
                        System.out.print("<AXISNAME_DESCENDANT_OR_SELF/>");
                        break;
                    case EXPRTOKEN_AXISNAME_FOLLOWING:
                        System.out.print("<AXISNAME_FOLLOWING/>");
                        break;
                    case EXPRTOKEN_AXISNAME_FOLLOWING_SIBLING:
                        System.out.print("<AXISNAME_FOLLOWING_SIBLING/>");
                        break;
                    case EXPRTOKEN_AXISNAME_NAMESPACE:
                        System.out.print("<AXISNAME_NAMESPACE/>");
                        break;
                    case EXPRTOKEN_AXISNAME_PARENT:
                        System.out.print("<AXISNAME_PARENT/>");
                        break;
                    case EXPRTOKEN_AXISNAME_PRECEDING:
                        System.out.print("<AXISNAME_PRECEDING/>");
                        break;
                    case EXPRTOKEN_AXISNAME_PRECEDING_SIBLING:
                        System.out.print("<AXISNAME_PRECEDING_SIBLING/>");
                        break;
                    case EXPRTOKEN_AXISNAME_SELF:
                        System.out.print("<AXISNAME_SELF/>");
                        break;
                    case EXPRTOKEN_LITERAL:
                        System.out.print("<LITERAL");
                        System.out.print(" value=\"" + getTokenString(fTokens[++i]) + "\"");
                        System.out.print("/>");
                        break;
                    case EXPRTOKEN_NUMBER:
                        System.out.print("<NUMBER");
                        System.out.print(" whole=\"" + getTokenString(fTokens[++i]) + "\"");
                        System.out.print(" part=\"" + getTokenString(fTokens[++i]) + "\"");
                        System.out.print("/>");
                        break;
                    case EXPRTOKEN_VARIABLE_REFERENCE:
                        System.out.print("<VARIABLE_REFERENCE");
                        if (fTokens[++i] != -1)
                            System.out.print(" prefix=\"" + getTokenString(fTokens[i]) + "\"");
                        System.out.print(" localpart=\"" + getTokenString(fTokens[++i]) + "\"");
                        System.out.print("/>");
                        break;
                    default:
                        System.out.println("<???/>");
                    }
                }
                System.out.println();
            }

    } private static class Scanner {


        private static final byte
            CHARTYPE_INVALID            =  0,   CHARTYPE_OTHER              =  1,   CHARTYPE_WHITESPACE         =  2,   CHARTYPE_EXCLAMATION        =  3,   CHARTYPE_QUOTE              =  4,   CHARTYPE_DOLLAR             =  5,   CHARTYPE_OPEN_PAREN         =  6,   CHARTYPE_CLOSE_PAREN        =  7,   CHARTYPE_STAR               =  8,   CHARTYPE_PLUS               =  9,   CHARTYPE_COMMA              = 10,   CHARTYPE_MINUS              = 11,   CHARTYPE_PERIOD             = 12,   CHARTYPE_SLASH              = 13,   CHARTYPE_DIGIT              = 14,   CHARTYPE_COLON              = 15,   CHARTYPE_LESS               = 16,   CHARTYPE_EQUAL              = 17,   CHARTYPE_GREATER            = 18,   CHARTYPE_ATSIGN             = 19,   CHARTYPE_LETTER             = 20,   CHARTYPE_OPEN_BRACKET       = 21,   CHARTYPE_CLOSE_BRACKET      = 22,   CHARTYPE_UNDERSCORE         = 23,   CHARTYPE_UNION              = 24,   CHARTYPE_NONASCII           = 25;   private static final byte[] fASCIICharMap = {
            0,  0,  0,  0,  0,  0,  0,  0,  0,  2,  2,  0,  0,  2,  0,  0,
            0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
            2,  3,  4,  1,  5,  1,  1,  4,  6,  7,  8,  9, 10, 11, 12, 13,
           14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 15,  1, 16, 17, 18,  1,
           19, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
           20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 21,  1, 22,  1, 23,
            1, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
           20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20,  1, 24,  1,  1,  1
        };



        private SymbolTable fSymbolTable;

        private static final String fAndSymbol = "and".intern();
        private static final String fOrSymbol = "or".intern();
        private static final String fModSymbol = "mod".intern();
        private static final String fDivSymbol = "div".intern();

        private static final String fCommentSymbol = "comment".intern();
        private static final String fTextSymbol = "text".intern();
        private static final String fPISymbol = "processing-instruction".intern();
        private static final String fNodeSymbol = "node".intern();

        private static final String fAncestorSymbol = "ancestor".intern();
        private static final String fAncestorOrSelfSymbol = "ancestor-or-self".intern();
        private static final String fAttributeSymbol = "attribute".intern();
        private static final String fChildSymbol = "child".intern();
        private static final String fDescendantSymbol = "descendant".intern();
        private static final String fDescendantOrSelfSymbol = "descendant-or-self".intern();
        private static final String fFollowingSymbol = "following".intern();
        private static final String fFollowingSiblingSymbol = "following-sibling".intern();
        private static final String fNamespaceSymbol = "namespace".intern();
        private static final String fParentSymbol = "parent".intern();
        private static final String fPrecedingSymbol = "preceding".intern();
        private static final String fPrecedingSiblingSymbol = "preceding-sibling".intern();
        private static final String fSelfSymbol = "self".intern();

        public Scanner(SymbolTable symbolTable) {

            fSymbolTable = symbolTable;

        } public boolean scanExpr(SymbolTable symbolTable,
                                XPath.Tokens tokens, String data,
                                int currentOffset, int endOffset)
            throws XPathException {

            int nameOffset;
            String nameHandle, prefixHandle;
            boolean starIsMultiplyOperator = false;
            int ch;

            while (true) {
                if (currentOffset == endOffset) {
                    break;
                }
                ch = data.charAt(currentOffset);
                while (ch == ' ' || ch == 0x0A || ch == 0x09 || ch == 0x0D) {
                    if (++currentOffset == endOffset) {
                        break;
                    }
                    ch = data.charAt(currentOffset);
                }
                if (currentOffset == endOffset) {
                    break;
                }
                byte chartype = (ch >= 0x80) ? CHARTYPE_NONASCII : fASCIICharMap[ch];
                switch (chartype) {
                case CHARTYPE_OPEN_PAREN:       addToken(tokens, XPath.Tokens.EXPRTOKEN_OPEN_PAREN);
                    starIsMultiplyOperator = false;
                    if (++currentOffset == endOffset) {
                        break;
                    }
                    break;
                case CHARTYPE_CLOSE_PAREN:      addToken(tokens, XPath.Tokens.EXPRTOKEN_CLOSE_PAREN);
                    starIsMultiplyOperator = true;
                    if (++currentOffset == endOffset) {
                        break;
                    }
                    break;
                case CHARTYPE_OPEN_BRACKET:     addToken(tokens, XPath.Tokens.EXPRTOKEN_OPEN_BRACKET);
                    starIsMultiplyOperator = false;
                    if (++currentOffset == endOffset) {
                        break;
                    }
                    break;
                case CHARTYPE_CLOSE_BRACKET:    addToken(tokens, XPath.Tokens.EXPRTOKEN_CLOSE_BRACKET);
                    starIsMultiplyOperator = true;
                    if (++currentOffset == endOffset) {
                        break;
                    }
                    break;
                case CHARTYPE_PERIOD:           if (currentOffset + 1 == endOffset) {
                        addToken(tokens, XPath.Tokens.EXPRTOKEN_PERIOD);
                        starIsMultiplyOperator = true;
                        currentOffset++;
                        break;
                    }
                    ch = data.charAt(currentOffset + 1);
                    if (ch == '.') {            addToken(tokens, XPath.Tokens.EXPRTOKEN_DOUBLE_PERIOD);
                        starIsMultiplyOperator = true;
                        currentOffset += 2;
                    } else if (ch >= '0' && ch <= '9') {
                        addToken(tokens, XPath.Tokens.EXPRTOKEN_NUMBER);
                        starIsMultiplyOperator = true;
                        currentOffset = scanNumber(tokens, data, endOffset, currentOffset);
                    } else if (ch == '/') {
                        addToken(tokens, XPath.Tokens.EXPRTOKEN_PERIOD);
                        starIsMultiplyOperator = true;
                        currentOffset++;
                    } else if (ch == '|') {
                        addToken(tokens, XPath.Tokens.EXPRTOKEN_PERIOD);
                        starIsMultiplyOperator = true;
                        currentOffset++;
                        break;
                    } else if (ch == ' ' || ch == 0x0A || ch == 0x09 || ch == 0x0D) {
                        do {
                            if (++currentOffset == endOffset) {
                                break;
                            }
                            ch = data.charAt(currentOffset);
                        } while (ch == ' ' || ch == 0x0A || ch == 0x09 || ch == 0x0D);
                        if (currentOffset == endOffset || ch == '|' || ch == '/') {
                            addToken(tokens, XPath.Tokens.EXPRTOKEN_PERIOD);
                            starIsMultiplyOperator = true;
                            break;
                        }
                        throw new XPathException ("c-general-xpath");
                    } else {                    throw new XPathException ("c-general-xpath");
                    }
                    if (currentOffset == endOffset) {
                        break;
                    }
                    break;
                case CHARTYPE_ATSIGN:           addToken(tokens, XPath.Tokens.EXPRTOKEN_ATSIGN);
                    starIsMultiplyOperator = false;
                    if (++currentOffset == endOffset) {
                        break;
                    }
                    break;
                case CHARTYPE_COMMA:            addToken(tokens, XPath.Tokens.EXPRTOKEN_COMMA);
                    starIsMultiplyOperator = false;
                    if (++currentOffset == endOffset) {
                        break;
                    }
                    break;
                case CHARTYPE_COLON:            if (++currentOffset == endOffset) {
                return false; }
                    ch = data.charAt(currentOffset);
                    if (ch != ':') {
                return false; }
                    addToken(tokens, XPath.Tokens.EXPRTOKEN_DOUBLE_COLON);
                    starIsMultiplyOperator = false;
                    if (++currentOffset == endOffset) {
                        break;
                    }
                    break;
                case CHARTYPE_SLASH:            if (++currentOffset == endOffset) {
                        addToken(tokens, XPath.Tokens.EXPRTOKEN_OPERATOR_SLASH);
                        starIsMultiplyOperator = false;
                        break;
                    }
                    ch = data.charAt(currentOffset);
                    if (ch == '/') { addToken(tokens, XPath.Tokens.EXPRTOKEN_OPERATOR_DOUBLE_SLASH);
                        starIsMultiplyOperator = false;
                        if (++currentOffset == endOffset) {
                            break;
                        }
                    } else {
                        addToken(tokens, XPath.Tokens.EXPRTOKEN_OPERATOR_SLASH);
                        starIsMultiplyOperator = false;
                    }
                    break;
                case CHARTYPE_UNION:            addToken(tokens, XPath.Tokens.EXPRTOKEN_OPERATOR_UNION);
                    starIsMultiplyOperator = false;
                    if (++currentOffset == endOffset) {
                        break;
                    }
                    break;
                case CHARTYPE_PLUS:             addToken(tokens, XPath.Tokens.EXPRTOKEN_OPERATOR_PLUS);
                    starIsMultiplyOperator = false;
                    if (++currentOffset == endOffset) {
                        break;
                    }
                    break;
                case CHARTYPE_MINUS:            addToken(tokens, XPath.Tokens.EXPRTOKEN_OPERATOR_MINUS);
                    starIsMultiplyOperator = false;
                    if (++currentOffset == endOffset) {
                        break;
                    }
                    break;
                case CHARTYPE_EQUAL:            addToken(tokens, XPath.Tokens.EXPRTOKEN_OPERATOR_EQUAL);
                    starIsMultiplyOperator = false;
                    if (++currentOffset == endOffset) {
                        break;
                    }
                    break;
                case CHARTYPE_EXCLAMATION:      if (++currentOffset == endOffset) {
                return false; }
                    ch = data.charAt(currentOffset);
                    if (ch != '=') {
                return false; }
                    addToken(tokens, XPath.Tokens.EXPRTOKEN_OPERATOR_NOT_EQUAL);
                    starIsMultiplyOperator = false;
                    if (++currentOffset == endOffset) {
                        break;
                    }
                    break;
                case CHARTYPE_LESS: if (++currentOffset == endOffset) {
                        addToken(tokens, XPath.Tokens.EXPRTOKEN_OPERATOR_LESS);
                        starIsMultiplyOperator = false;
                        break;
                    }
                    ch = data.charAt(currentOffset);
                    if (ch == '=') { addToken(tokens, XPath.Tokens.EXPRTOKEN_OPERATOR_LESS_EQUAL);
                        starIsMultiplyOperator = false;
                        if (++currentOffset == endOffset) {
                            break;
                        }
                    } else {
                        addToken(tokens, XPath.Tokens.EXPRTOKEN_OPERATOR_LESS);
                        starIsMultiplyOperator = false;
                    }
                    break;
                case CHARTYPE_GREATER: if (++currentOffset == endOffset) {
                        addToken(tokens, XPath.Tokens.EXPRTOKEN_OPERATOR_GREATER);
                        starIsMultiplyOperator = false;
                        break;
                    }
                    ch = data.charAt(currentOffset);
                    if (ch == '=') { addToken(tokens, XPath.Tokens.EXPRTOKEN_OPERATOR_GREATER_EQUAL);
                        starIsMultiplyOperator = false;
                        if (++currentOffset == endOffset) {
                            break;
                        }
                    } else {
                        addToken(tokens, XPath.Tokens.EXPRTOKEN_OPERATOR_GREATER);
                        starIsMultiplyOperator = false;
                    }
                    break;
                case CHARTYPE_QUOTE:            int qchar = ch;
                    if (++currentOffset == endOffset) {
                return false; }
                    ch = data.charAt(currentOffset);
                    int litOffset = currentOffset;
                    while (ch != qchar) {
                        if (++currentOffset == endOffset) {
                return false; }
                        ch = data.charAt(currentOffset);
                    }
                    int litLength = currentOffset - litOffset;
                    addToken(tokens, XPath.Tokens.EXPRTOKEN_LITERAL);
                    starIsMultiplyOperator = true;
                    tokens.addToken(symbolTable.addSymbol(data.substring(litOffset, litOffset + litLength)));
                    if (++currentOffset == endOffset) {
                        break;
                    }
                    break;
                case CHARTYPE_DIGIT:
                    addToken(tokens, XPath.Tokens.EXPRTOKEN_NUMBER);
                    starIsMultiplyOperator = true;
                    currentOffset = scanNumber(tokens, data, endOffset, currentOffset);
                    break;
                case CHARTYPE_DOLLAR:
                    if (++currentOffset == endOffset) {
                return false; }
                    nameOffset = currentOffset;
                    currentOffset = scanNCName(data, endOffset, currentOffset);
                    if (currentOffset == nameOffset) {
                return false; }
                    if (currentOffset < endOffset) {
                        ch = data.charAt(currentOffset);
                    }
                    else {
                        ch = -1;
                    }
                    nameHandle = symbolTable.addSymbol(data.substring(nameOffset, currentOffset));
                    if (ch != ':') {
                        prefixHandle = XMLSymbols.EMPTY_STRING;
                    } else {
                        prefixHandle = nameHandle;
                        if (++currentOffset == endOffset) {
                return false; }
                        nameOffset = currentOffset;
                        currentOffset = scanNCName(data, endOffset, currentOffset);
                        if (currentOffset == nameOffset) {
                return false; }
                        if (currentOffset < endOffset) {
                            ch = data.charAt(currentOffset);
                        }
                        else {
                            ch = -1;
                        }
                        nameHandle = symbolTable.addSymbol(data.substring(nameOffset, currentOffset));
                    }
                    addToken(tokens, XPath.Tokens.EXPRTOKEN_VARIABLE_REFERENCE);
                    starIsMultiplyOperator = true;
                    tokens.addToken(prefixHandle);
                    tokens.addToken(nameHandle);
                    break;
                case CHARTYPE_STAR:             if (starIsMultiplyOperator) {
                        addToken(tokens, XPath.Tokens.EXPRTOKEN_OPERATOR_MULT);
                        starIsMultiplyOperator = false;
                    } else {
                        addToken(tokens, XPath.Tokens.EXPRTOKEN_NAMETEST_ANY);
                        starIsMultiplyOperator = true;
                    }
                    if (++currentOffset == endOffset) {
                        break;
                    }
                    break;
                case CHARTYPE_NONASCII: case CHARTYPE_LETTER:
                case CHARTYPE_UNDERSCORE:
                    nameOffset = currentOffset;
                    currentOffset = scanNCName(data, endOffset, currentOffset);
                    if (currentOffset == nameOffset) {
                return false; }
                    if (currentOffset < endOffset) {
                        ch = data.charAt(currentOffset);
                    }
                    else {
                        ch = -1;
                    }
                    nameHandle = symbolTable.addSymbol(data.substring(nameOffset, currentOffset));
                    boolean isNameTestNCName = false;
                    boolean isAxisName = false;
                    prefixHandle = XMLSymbols.EMPTY_STRING;
                    if (ch == ':') {
                        if (++currentOffset == endOffset) {
                return false; }
                        ch = data.charAt(currentOffset);
                        if (ch == '*') {
                            if (++currentOffset < endOffset) {
                                ch = data.charAt(currentOffset);
                            }
                            isNameTestNCName = true;
                        } else if (ch == ':') {
                            if (++currentOffset < endOffset) {
                                ch = data.charAt(currentOffset);
                            }
                            isAxisName = true;
                        } else {
                            prefixHandle = nameHandle;
                            nameOffset = currentOffset;
                            currentOffset = scanNCName(data, endOffset, currentOffset);
                            if (currentOffset == nameOffset) {
                return false; }
                            if (currentOffset < endOffset) {
                                ch = data.charAt(currentOffset);
                            }
                            else {
                                ch = -1;
                            }
                            nameHandle = symbolTable.addSymbol(data.substring(nameOffset, currentOffset));
                        }
                    }
                    while (ch == ' ' || ch == 0x0A || ch == 0x09 || ch == 0x0D) {
                        if (++currentOffset == endOffset) {
                            break;
                        }
                        ch = data.charAt(currentOffset);
                    }
                    if (starIsMultiplyOperator) {
                        if (nameHandle == fAndSymbol) {
                            addToken(tokens, XPath.Tokens.EXPRTOKEN_OPERATOR_AND);
                            starIsMultiplyOperator = false;
                        } else if (nameHandle == fOrSymbol) {
                            addToken(tokens, XPath.Tokens.EXPRTOKEN_OPERATOR_OR);
                            starIsMultiplyOperator = false;
                        } else if (nameHandle == fModSymbol) {
                            addToken(tokens, XPath.Tokens.EXPRTOKEN_OPERATOR_MOD);
                            starIsMultiplyOperator = false;
                        } else if (nameHandle == fDivSymbol) {
                            addToken(tokens, XPath.Tokens.EXPRTOKEN_OPERATOR_DIV);
                            starIsMultiplyOperator = false;
                        } else {
                return false; }
                        if (isNameTestNCName) {
                return false; } else if (isAxisName) {
                return false; }
                        break;
                    }
                    if (ch == '(' && !isNameTestNCName && !isAxisName) {
                        if (nameHandle == fCommentSymbol) {
                            addToken(tokens, XPath.Tokens.EXPRTOKEN_NODETYPE_COMMENT);
                        } else if (nameHandle == fTextSymbol) {
                            addToken(tokens, XPath.Tokens.EXPRTOKEN_NODETYPE_TEXT);
                        } else if (nameHandle == fPISymbol) {
                            addToken(tokens, XPath.Tokens.EXPRTOKEN_NODETYPE_PI);
                        } else if (nameHandle == fNodeSymbol) {
                            addToken(tokens, XPath.Tokens.EXPRTOKEN_NODETYPE_NODE);
                        } else {
                            addToken(tokens, XPath.Tokens.EXPRTOKEN_FUNCTION_NAME);
                            tokens.addToken(prefixHandle);
                            tokens.addToken(nameHandle);
                        }
                        addToken(tokens, XPath.Tokens.EXPRTOKEN_OPEN_PAREN);
                        starIsMultiplyOperator = false;
                        if (++currentOffset == endOffset) {
                            break;
                        }
                        break;
                    }
                    if (isAxisName ||
                        (ch == ':' && currentOffset + 1 < endOffset &&
                         data.charAt(currentOffset + 1) == ':')) {
                        if (nameHandle == fAncestorSymbol) {
                            addToken(tokens, XPath.Tokens.EXPRTOKEN_AXISNAME_ANCESTOR);
                        } else if (nameHandle == fAncestorOrSelfSymbol) {
                            addToken(tokens, XPath.Tokens.EXPRTOKEN_AXISNAME_ANCESTOR_OR_SELF);
                        } else if (nameHandle == fAttributeSymbol) {
                            addToken(tokens, XPath.Tokens.EXPRTOKEN_AXISNAME_ATTRIBUTE);
                        } else if (nameHandle == fChildSymbol) {
                            addToken(tokens, XPath.Tokens.EXPRTOKEN_AXISNAME_CHILD);
                        } else if (nameHandle == fDescendantSymbol) {
                            addToken(tokens, XPath.Tokens.EXPRTOKEN_AXISNAME_DESCENDANT);
                        } else if (nameHandle == fDescendantOrSelfSymbol) {
                            addToken(tokens, XPath.Tokens.EXPRTOKEN_AXISNAME_DESCENDANT_OR_SELF);
                        } else if (nameHandle == fFollowingSymbol) {
                            addToken(tokens, XPath.Tokens.EXPRTOKEN_AXISNAME_FOLLOWING);
                        } else if (nameHandle == fFollowingSiblingSymbol) {
                            addToken(tokens, XPath.Tokens.EXPRTOKEN_AXISNAME_FOLLOWING_SIBLING);
                        } else if (nameHandle == fNamespaceSymbol) {
                            addToken(tokens, XPath.Tokens.EXPRTOKEN_AXISNAME_NAMESPACE);
                        } else if (nameHandle == fParentSymbol) {
                            addToken(tokens, XPath.Tokens.EXPRTOKEN_AXISNAME_PARENT);
                        } else if (nameHandle == fPrecedingSymbol) {
                            addToken(tokens, XPath.Tokens.EXPRTOKEN_AXISNAME_PRECEDING);
                        } else if (nameHandle == fPrecedingSiblingSymbol) {
                            addToken(tokens, XPath.Tokens.EXPRTOKEN_AXISNAME_PRECEDING_SIBLING);
                        } else if (nameHandle == fSelfSymbol) {
                            addToken(tokens, XPath.Tokens.EXPRTOKEN_AXISNAME_SELF);
                        } else {
                return false; }
                        if (isNameTestNCName) {
                return false; }
                        addToken(tokens, XPath.Tokens.EXPRTOKEN_DOUBLE_COLON);
                        starIsMultiplyOperator = false;
                        if (!isAxisName) {
                            currentOffset++;
                            if (++currentOffset == endOffset) {
                                break;
                            }
                        }
                        break;
                    }
                    if (isNameTestNCName) {
                        addToken(tokens, XPath.Tokens.EXPRTOKEN_NAMETEST_NAMESPACE);
                        starIsMultiplyOperator = true;
                        tokens.addToken(nameHandle);
                    } else {
                        addToken(tokens, XPath.Tokens.EXPRTOKEN_NAMETEST_QNAME);
                        starIsMultiplyOperator = true;
                        tokens.addToken(prefixHandle);
                        tokens.addToken(nameHandle);
                    }
                    break;
                }
            }
            if (XPath.Tokens.DUMP_TOKENS) {
                tokens.dumpTokens();
            }
            return true;
        }
        int scanNCName(String data, int endOffset, int currentOffset) {
            int ch = data.charAt(currentOffset);
            if (ch >= 0x80) {
                if (!XMLChar.isNameStart(ch))

                {
                    return currentOffset;
                }
            }
            else {
                byte chartype = fASCIICharMap[ch];
                if (chartype != CHARTYPE_LETTER && chartype != CHARTYPE_UNDERSCORE) {
                    return currentOffset;
                }
            }
            while (++currentOffset < endOffset) {
                ch = data.charAt(currentOffset);
                if (ch >= 0x80) {
                    if (!XMLChar.isName(ch))

                    {
                        break;
                    }
                }
                else {
                    byte chartype = fASCIICharMap[ch];
                    if (chartype != CHARTYPE_LETTER && chartype != CHARTYPE_DIGIT &&
                        chartype != CHARTYPE_PERIOD && chartype != CHARTYPE_MINUS &&
                        chartype != CHARTYPE_UNDERSCORE)
                    {
                        break;
                    }
                }
            }
            return currentOffset;
        }
        private int scanNumber(XPath.Tokens tokens, String data, int endOffset, int currentOffset) {
            int ch = data.charAt(currentOffset);
            int whole = 0;
            int part = 0;
            while (ch >= '0' && ch <= '9') {
                whole = (whole * 10) + (ch - '0');
                if (++currentOffset == endOffset) {
                    break;
                }
                ch = data.charAt(currentOffset);
            }
            if (ch == '.') {
                if (++currentOffset < endOffset) {

                    ch = data.charAt(currentOffset);
                    while (ch >= '0' && ch <= '9') {
                        part = (part * 10) + (ch - '0');
                        if (++currentOffset == endOffset) {
                            break;
                        }
                        ch = data.charAt(currentOffset);
                    }
                    if (part != 0) {

                        throw new RuntimeException("find a solution!");
                        }
                }
            }
            tokens.addToken(whole);
            tokens.addToken(part);
            return currentOffset;
        }

        protected void addToken(XPath.Tokens tokens, int token)
            throws XPathException {
            tokens.addToken(token);
        } } public static void main(String[] argv) throws Exception {

        for (int i = 0; i < argv.length; i++) {
            final String expression = argv[i];
            System.out.println("# XPath expression: \""+expression+'"');
            try {
                SymbolTable symbolTable = new SymbolTable();
                XPath xpath = new XPath(expression, symbolTable, null);
                System.out.println("expanded xpath: \""+xpath.toString()+'"');
            }
            catch (XPathException e) {
                System.out.println("error: "+e.getMessage());
            }
        }

    } }