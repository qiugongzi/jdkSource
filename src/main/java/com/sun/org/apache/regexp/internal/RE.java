


package com.sun.org.apache.regexp.internal;

import java.io.Serializable;
import java.util.Vector;


public class RE implements Serializable
{

    public static final int MATCH_NORMAL          = 0x0000;


    public static final int MATCH_CASEINDEPENDENT = 0x0001;


    public static final int MATCH_MULTILINE       = 0x0002;


    public static final int MATCH_SINGLELINE      = 0x0004;



                 static final char OP_END              = 'E';  static final char OP_BOL              = '^';  static final char OP_EOL              = '$';  static final char OP_ANY              = '.';  static final char OP_ANYOF            = '[';  static final char OP_BRANCH           = '|';  static final char OP_ATOM             = 'A';  static final char OP_STAR             = '*';  static final char OP_PLUS             = '+';  static final char OP_MAYBE            = '?';  static final char OP_ESCAPE           = '\\'; static final char OP_OPEN             = '(';  static final char OP_OPEN_CLUSTER     = '<';  static final char OP_CLOSE            = ')';  static final char OP_CLOSE_CLUSTER    = '>';  static final char OP_BACKREF          = '#';  static final char OP_GOTO             = 'G';  static final char OP_NOTHING          = 'N';  static final char OP_RELUCTANTSTAR    = '8';  static final char OP_RELUCTANTPLUS    = '=';  static final char OP_RELUCTANTMAYBE   = '/';  static final char OP_POSIXCLASS       = 'P';  static final char E_ALNUM             = 'w';  static final char E_NALNUM            = 'W';  static final char E_BOUND             = 'b';  static final char E_NBOUND            = 'B';  static final char E_SPACE             = 's';  static final char E_NSPACE            = 'S';  static final char E_DIGIT             = 'd';  static final char E_NDIGIT            = 'D';  static final char POSIX_CLASS_ALNUM   = 'w';  static final char POSIX_CLASS_ALPHA   = 'a';  static final char POSIX_CLASS_BLANK   = 'b';  static final char POSIX_CLASS_CNTRL   = 'c';  static final char POSIX_CLASS_DIGIT   = 'd';  static final char POSIX_CLASS_GRAPH   = 'g';  static final char POSIX_CLASS_LOWER   = 'l';  static final char POSIX_CLASS_PRINT   = 'p';  static final char POSIX_CLASS_PUNCT   = '!';  static final char POSIX_CLASS_SPACE   = 's';  static final char POSIX_CLASS_UPPER   = 'u';  static final char POSIX_CLASS_XDIGIT  = 'x';  static final char POSIX_CLASS_JSTART  = 'j';  static final char POSIX_CLASS_JPART   = 'k';  static final int maxNode  = 65536;            static final int MAX_PAREN = 16;              static final int offsetOpcode = 0;            static final int offsetOpdata = 1;            static final int offsetNext   = 2;            static final int nodeSize     = 3;            REProgram program;                            transient CharacterIterator search;           int matchFlags;                               int maxParen = MAX_PAREN;

    transient int parenCount;                     transient int start0;                         transient int end0;                           transient int start1;                         transient int end1;                           transient int start2;                         transient int end2;                           transient int[] startn;                       transient int[] endn;                         transient int[] startBackref;                 transient int[] endBackref;                   public RE(String pattern) throws RESyntaxException
    {
        this(pattern, MATCH_NORMAL);
    }


    public RE(String pattern, int matchFlags) throws RESyntaxException
    {
        this(new RECompiler().compile(pattern));
        setMatchFlags(matchFlags);
    }


    public RE(REProgram program, int matchFlags)
    {
        setProgram(program);
        setMatchFlags(matchFlags);
    }


    public RE(REProgram program)
    {
        this(program, MATCH_NORMAL);
    }


    public RE()
    {
        this((REProgram)null, MATCH_NORMAL);
    }


    public static String simplePatternToFullRegularExpression(String pattern)
    {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < pattern.length(); i++)
        {
            char c = pattern.charAt(i);
            switch (c)
            {
                case '*':
                    buf.append(".*");
                    break;

                case '.':
                case '[':
                case ']':
                case '\\':
                case '+':
                case '?':
                case '{':
                case '}':
                case '$':
                case '^':
                case '|':
                case '(':
                case ')':
                    buf.append('\\');
                default:
                    buf.append(c);
                    break;
            }
        }
        return buf.toString();
    }


    public void setMatchFlags(int matchFlags)
    {
        this.matchFlags = matchFlags;
    }


    public int getMatchFlags()
    {
        return matchFlags;
    }


    public void setProgram(REProgram program)
    {
        this.program = program;
        if (program != null && program.maxParens != -1) {
            this.maxParen = program.maxParens;
        } else {
            this.maxParen = MAX_PAREN;
        }
    }


    public REProgram getProgram()
    {
        return program;
    }


    public int getParenCount()
    {
        return parenCount;
    }


    public String getParen(int which)
    {
        int start;
        if (which < parenCount && (start = getParenStart(which)) >= 0)
        {
            return search.substring(start, getParenEnd(which));
        }
        return null;
    }


    public final int getParenStart(int which)
    {
        if (which < parenCount)
        {
            switch (which)
            {
                case 0:
                    return start0;

                case 1:
                    return start1;

                case 2:
                    return start2;

                default:
                    if (startn == null)
                    {
                        allocParens();
                    }
                    return startn[which];
            }
        }
        return -1;
    }


    public final int getParenEnd(int which)
    {
        if (which < parenCount)
        {
            switch (which)
            {
                case 0:
                    return end0;

                case 1:
                    return end1;

                case 2:
                    return end2;

                default:
                    if (endn == null)
                    {
                        allocParens();
                    }
                    return endn[which];
            }
        }
        return -1;
    }


    public final int getParenLength(int which)
    {
        if (which < parenCount)
        {
            return getParenEnd(which) - getParenStart(which);
        }
        return -1;
    }


    protected final void setParenStart(int which, int i)
    {
        if (which < parenCount)
        {
            switch (which)
            {
                case 0:
                    start0 = i;
                    break;

                case 1:
                    start1 = i;
                    break;

                case 2:
                    start2 = i;
                    break;

                default:
                    if (startn == null)
                    {
                        allocParens();
                    }
                    startn[which] = i;
                    break;
            }
        }
    }


    protected final void setParenEnd(int which, int i)
    {
        if (which < parenCount)
        {
            switch (which)
            {
                case 0:
                    end0 = i;
                    break;

                case 1:
                    end1 = i;
                    break;

                case 2:
                    end2 = i;
                    break;

                default:
                    if (endn == null)
                    {
                        allocParens();
                    }
                    endn[which] = i;
                    break;
            }
        }
    }


    protected void internalError(String s) throws Error
    {
        throw new Error("RE internal error: " + s);
    }


    private final void allocParens()
    {
        startn = new int[maxParen];
        endn = new int[maxParen];

        for (int i = 0; i < maxParen; i++)
        {
            startn[i] = -1;
            endn[i] = -1;
        }
    }


    protected int matchNodes(int firstNode, int lastNode, int idxStart)
    {
        int idx = idxStart;

        int next, opcode, opdata;
        int idxNew;
        char[] instruction = program.instruction;
        for (int node = firstNode; node < lastNode; )
        {
            opcode = instruction[node + offsetOpcode];
            next   = node + (short)instruction[node + offsetNext];
            opdata = instruction[node + offsetOpdata];

            switch (opcode)
            {
                case OP_RELUCTANTMAYBE:
                    {
                        int once = 0;
                        do
                        {
                            if ((idxNew = matchNodes(next, maxNode, idx)) != -1)
                            {
                                return idxNew;
                            }
                        }
                        while ((once++ == 0) && (idx = matchNodes(node + nodeSize, next, idx)) != -1);
                        return -1;
                    }

                case OP_RELUCTANTPLUS:
                    while ((idx = matchNodes(node + nodeSize, next, idx)) != -1)
                    {
                        if ((idxNew = matchNodes(next, maxNode, idx)) != -1)
                        {
                            return idxNew;
                        }
                    }
                    return -1;

                case OP_RELUCTANTSTAR:
                    do
                    {
                        if ((idxNew = matchNodes(next, maxNode, idx)) != -1)
                        {
                            return idxNew;
                        }
                    }
                    while ((idx = matchNodes(node + nodeSize, next, idx)) != -1);
                    return -1;

                case OP_OPEN:

                    if ((program.flags & REProgram.OPT_HASBACKREFS) != 0)
                    {
                        startBackref[opdata] = idx;
                    }
                    if ((idxNew = matchNodes(next, maxNode, idx)) != -1)
                    {
                        if ((opdata + 1) > parenCount)
                        {
                            parenCount = opdata + 1;
                        }

                        if (getParenStart(opdata) == -1)
                        {
                            setParenStart(opdata, idx);
                        }
                    }
                    return idxNew;

                case OP_CLOSE:

                    if ((program.flags & REProgram.OPT_HASBACKREFS) != 0)
                    {
                        endBackref[opdata] = idx;
                    }
                    if ((idxNew = matchNodes(next, maxNode, idx)) != -1)
                    {
                        if ((opdata + 1) > parenCount)
                        {
                            parenCount = opdata + 1;
                        }

                        if (getParenEnd(opdata) == -1)
                        {
                            setParenEnd(opdata, idx);
                        }
                    }
                    return idxNew;

                case OP_OPEN_CLUSTER:
                case OP_CLOSE_CLUSTER:
                    return matchNodes( next, maxNode, idx );

                case OP_BACKREF:
                    {
                        int s = startBackref[opdata];
                        int e = endBackref[opdata];

                        if (s == -1 || e == -1)
                        {
                            return -1;
                        }

                        if (s == e)
                        {
                            break;
                        }

                        int l = e - s;

                        if (search.isEnd(idx + l - 1))
                        {
                            return -1;
                        }

                        final boolean caseFold =
                            ((matchFlags & MATCH_CASEINDEPENDENT) != 0);
                        for (int i = 0; i < l; i++)
                        {
                            if (compareChars(search.charAt(idx++), search.charAt(s + i), caseFold) != 0)
                            {
                                return -1;
                            }
                        }
                    }
                    break;

                case OP_BOL:

                    if (idx != 0)
                    {
                        if ((matchFlags & MATCH_MULTILINE) == MATCH_MULTILINE)
                        {
                            if (idx <= 0 || !isNewline(idx - 1)) {
                                return -1;
                            } else {
                                break;
                            }
                        }
                        return -1;
                    }
                    break;

                case OP_EOL:

                    if (!search.isEnd(0) && !search.isEnd(idx))
                    {
                        if ((matchFlags & MATCH_MULTILINE) == MATCH_MULTILINE)
                        {
                            if (!isNewline(idx)) {
                                return -1;
                            } else {
                                break;
                            }
                        }
                        return -1;
                    }
                    break;

                case OP_ESCAPE:

                    switch (opdata)
                    {
                        case E_NBOUND:
                        case E_BOUND:
                            {
                                char cLast = ((idx == 0) ? '\n' : search.charAt(idx - 1));
                                char cNext = ((search.isEnd(idx)) ? '\n' : search.charAt(idx));
                                if ((Character.isLetterOrDigit(cLast) == Character.isLetterOrDigit(cNext)) == (opdata == E_BOUND))
                                {
                                    return -1;
                                }
                            }
                            break;

                        case E_ALNUM:
                        case E_NALNUM:
                        case E_DIGIT:
                        case E_NDIGIT:
                        case E_SPACE:
                        case E_NSPACE:

                            if (search.isEnd(idx))
                            {
                                return -1;
                            }

                            char c = search.charAt(idx);

                            switch (opdata)
                            {
                                case E_ALNUM:
                                case E_NALNUM:
                                    if (!((Character.isLetterOrDigit(c) || c == '_') == (opdata == E_ALNUM)))
                                    {
                                        return -1;
                                    }
                                    break;

                                case E_DIGIT:
                                case E_NDIGIT:
                                    if (!(Character.isDigit(c) == (opdata == E_DIGIT)))
                                    {
                                        return -1;
                                    }
                                    break;

                                case E_SPACE:
                                case E_NSPACE:
                                    if (!(Character.isWhitespace(c) == (opdata == E_SPACE)))
                                    {
                                        return -1;
                                    }
                                    break;
                            }
                            idx++;
                            break;

                        default:
                            internalError("Unrecognized escape '" + opdata + "'");
                    }
                    break;

                case OP_ANY:

                    if ((matchFlags & MATCH_SINGLELINE) == MATCH_SINGLELINE) {
                        if (search.isEnd(idx))
                        {
                            return -1;
                        }
                    }
                    else
                    {
                        if (search.isEnd(idx) || isNewline(idx))
                        {
                            return -1;
                        }
                    }
                    idx++;
                    break;

                case OP_ATOM:
                    {
                        if (search.isEnd(idx))
                        {
                            return -1;
                        }

                        int lenAtom = opdata;
                        int startAtom = node + nodeSize;

                        if (search.isEnd(lenAtom + idx - 1))
                        {
                            return -1;
                        }

                        final boolean caseFold =
                            ((matchFlags & MATCH_CASEINDEPENDENT) != 0);

                        for (int i = 0; i < lenAtom; i++)
                        {
                            if (compareChars(search.charAt(idx++), instruction[startAtom + i], caseFold) != 0)
                            {
                                return -1;
                            }
                        }
                    }
                    break;

                case OP_POSIXCLASS:
                    {
                        if (search.isEnd(idx))
                        {
                            return -1;
                        }

                        switch (opdata)
                        {
                            case POSIX_CLASS_ALNUM:
                                if (!Character.isLetterOrDigit(search.charAt(idx)))
                                {
                                    return -1;
                                }
                                break;

                            case POSIX_CLASS_ALPHA:
                                if (!Character.isLetter(search.charAt(idx)))
                                {
                                    return -1;
                                }
                                break;

                            case POSIX_CLASS_DIGIT:
                                if (!Character.isDigit(search.charAt(idx)))
                                {
                                    return -1;
                                }
                                break;

                            case POSIX_CLASS_BLANK: if (!Character.isSpaceChar(search.charAt(idx)))
                                {
                                    return -1;
                                }
                                break;

                            case POSIX_CLASS_SPACE:
                                if (!Character.isWhitespace(search.charAt(idx)))
                                {
                                    return -1;
                                }
                                break;

                            case POSIX_CLASS_CNTRL:
                                if (Character.getType(search.charAt(idx)) != Character.CONTROL)
                                {
                                    return -1;
                                }
                                break;

                            case POSIX_CLASS_GRAPH: switch (Character.getType(search.charAt(idx)))
                                {
                                    case Character.MATH_SYMBOL:
                                    case Character.CURRENCY_SYMBOL:
                                    case Character.MODIFIER_SYMBOL:
                                    case Character.OTHER_SYMBOL:
                                        break;

                                    default:
                                        return -1;
                                }
                                break;

                            case POSIX_CLASS_LOWER:
                                if (Character.getType(search.charAt(idx)) != Character.LOWERCASE_LETTER)
                                {
                                    return -1;
                                }
                                break;

                            case POSIX_CLASS_UPPER:
                                if (Character.getType(search.charAt(idx)) != Character.UPPERCASE_LETTER)
                                {
                                    return -1;
                                }
                                break;

                            case POSIX_CLASS_PRINT:
                                if (Character.getType(search.charAt(idx)) == Character.CONTROL)
                                {
                                    return -1;
                                }
                                break;

                            case POSIX_CLASS_PUNCT:
                            {
                                int type = Character.getType(search.charAt(idx));
                                switch(type)
                                {
                                    case Character.DASH_PUNCTUATION:
                                    case Character.START_PUNCTUATION:
                                    case Character.END_PUNCTUATION:
                                    case Character.CONNECTOR_PUNCTUATION:
                                    case Character.OTHER_PUNCTUATION:
                                        break;

                                    default:
                                        return -1;
                                }
                            }
                            break;

                            case POSIX_CLASS_XDIGIT: {
                                boolean isXDigit = ((search.charAt(idx) >= '0' && search.charAt(idx) <= '9') ||
                                                    (search.charAt(idx) >= 'a' && search.charAt(idx) <= 'f') ||
                                                    (search.charAt(idx) >= 'A' && search.charAt(idx) <= 'F'));
                                if (!isXDigit)
                                {
                                    return -1;
                                }
                            }
                            break;

                            case POSIX_CLASS_JSTART:
                                if (!Character.isJavaIdentifierStart(search.charAt(idx)))
                                {
                                    return -1;
                                }
                                break;

                            case POSIX_CLASS_JPART:
                                if (!Character.isJavaIdentifierPart(search.charAt(idx)))
                                {
                                    return -1;
                                }
                                break;

                            default:
                                internalError("Bad posix class");
                                break;
                        }

                        idx++;
                    }
                    break;

                case OP_ANYOF:
                    {
                        if (search.isEnd(idx))
                        {
                            return -1;
                        }

                        char c = search.charAt(idx);
                        boolean caseFold = (matchFlags & MATCH_CASEINDEPENDENT) != 0;
                        int idxRange = node + nodeSize;
                        int idxEnd = idxRange + (opdata * 2);
                        boolean match = false;
                        for (int i = idxRange; !match && i < idxEnd; )
                        {
                            char s = instruction[i++];
                            char e = instruction[i++];

                            match = ((compareChars(c, s, caseFold) >= 0)
                                     && (compareChars(c, e, caseFold) <= 0));
                        }

                        if (!match)
                        {
                            return -1;
                        }
                        idx++;
                    }
                    break;

                case OP_BRANCH:
                {
                    if (instruction[next + offsetOpcode] != OP_BRANCH)
                    {
                        node += nodeSize;
                        continue;
                    }

                    short nextBranch;
                    do
                    {
                        if ((idxNew = matchNodes(node + nodeSize, maxNode, idx)) != -1)
                        {
                            return idxNew;
                        }

                        nextBranch = (short)instruction[node + offsetNext];
                        node += nextBranch;
                    }
                    while (nextBranch != 0 && (instruction[node + offsetOpcode] == OP_BRANCH));

                    return -1;
                }

                case OP_NOTHING:
                case OP_GOTO:

                    break;

                case OP_END:

                    setParenEnd(0, idx);
                    return idx;

                default:

                    internalError("Invalid opcode '" + opcode + "'");
            }

            node = next;
        }

        internalError("Corrupt program");
        return -1;
    }


    protected boolean matchAt(int i)
    {
        start0 = -1;
        end0   = -1;
        start1 = -1;
        end1   = -1;
        start2 = -1;
        end2   = -1;
        startn = null;
        endn   = null;
        parenCount = 1;
        setParenStart(0, i);

        if ((program.flags & REProgram.OPT_HASBACKREFS) != 0)
        {
            startBackref = new int[maxParen];
            endBackref = new int[maxParen];
        }

        int idx;
        if ((idx = matchNodes(0, maxNode, i)) != -1)
        {
            setParenEnd(0, idx);
            return true;
        }

        parenCount = 0;
        return false;
    }


    public boolean match(String search, int i)
    {
        return match(new StringCharacterIterator(search), i);
    }


    public boolean match(CharacterIterator search, int i)
    {
        if (program == null)
        {
            internalError("No RE program to run!");
        }

        this.search = search;

        if (program.prefix == null)
        {
            for ( ;! search.isEnd(i - 1); i++)
            {
                if (matchAt(i))
                {
                    return true;
                }
            }
            return false;
        }
        else
        {
            boolean caseIndependent = (matchFlags & MATCH_CASEINDEPENDENT) != 0;
            char[] prefix = program.prefix;
            for ( ; !search.isEnd(i + prefix.length - 1); i++)
            {
                int j = i;
                int k = 0;

                boolean match;
                do {
                    match = (compareChars(search.charAt(j++), prefix[k++], caseIndependent) == 0);
                } while (match && k < prefix.length);

                if (k == prefix.length)
                {
                    if (matchAt(i))
                    {
                        return true;
                    }
                }
            }
            return false;
        }
    }


    public boolean match(String search)
    {
        return match(search, 0);
    }


    public String[] split(String s)
    {
        Vector v = new Vector();

        int pos = 0;
        int len = s.length();

        while (pos < len && match(s, pos))
        {
            int start = getParenStart(0);

            int newpos = getParenEnd(0);

            if (newpos == pos)
            {
                v.addElement(s.substring(pos, start + 1));
                newpos++;
            }
            else
            {
                v.addElement(s.substring(pos, start));
            }

            pos = newpos;
        }

        String remainder = s.substring(pos);
        if (remainder.length() != 0)
        {
            v.addElement(remainder);
        }

        String[] ret = new String[v.size()];
        v.copyInto(ret);
        return ret;
    }


    public static final int REPLACE_ALL            = 0x0000;


    public static final int REPLACE_FIRSTONLY      = 0x0001;


    public static final int REPLACE_BACKREFERENCES = 0x0002;


    public String subst(String substituteIn, String substitution)
    {
        return subst(substituteIn, substitution, REPLACE_ALL);
    }


    public String subst(String substituteIn, String substitution, int flags)
    {
        StringBuffer ret = new StringBuffer();

        int pos = 0;
        int len = substituteIn.length();

        while (pos < len && match(substituteIn, pos))
        {
            ret.append(substituteIn.substring(pos, getParenStart(0)));

            if ((flags & REPLACE_BACKREFERENCES) != 0)
            {
                int lCurrentPosition = 0;
                int lLastPosition = -2;
                int lLength = substitution.length();
                boolean bAddedPrefix = false;

                while ((lCurrentPosition = substitution.indexOf("$", lCurrentPosition)) >= 0)
                {
                    if ((lCurrentPosition == 0 || substitution.charAt(lCurrentPosition - 1) != '\\')
                        && lCurrentPosition+1 < lLength)
                    {
                        char c = substitution.charAt(lCurrentPosition + 1);
                        if (c >= '0' && c <= '9')
                        {
                            if (bAddedPrefix == false)
                            {
                                ret.append(substitution.substring(0, lCurrentPosition));
                                bAddedPrefix = true;
                            }
                            else
                            {
                                ret.append(substitution.substring(lLastPosition + 2, lCurrentPosition));
                            }

                            ret.append(getParen(c - '0'));
                            lLastPosition = lCurrentPosition;
                        }
                    }

                    lCurrentPosition++;
                }

                ret.append(substitution.substring(lLastPosition + 2, lLength));
            }
            else
            {
                ret.append(substitution);
            }

            int newpos = getParenEnd(0);

            if (newpos == pos)
            {
                newpos++;
            }

            pos = newpos;

            if ((flags & REPLACE_FIRSTONLY) != 0)
            {
                break;
            }
        }

        if (pos < len)
        {
            ret.append(substituteIn.substring(pos));
        }

        return ret.toString();
    }


    public String[] grep(Object[] search)
    {
        Vector v = new Vector();

        for (int i = 0; i < search.length; i++)
        {
            String s = search[i].toString();

            if (match(s))
            {
                v.addElement(s);
            }
        }

        String[] ret = new String[v.size()];
        v.copyInto(ret);
        return ret;
    }


    private boolean isNewline(int i)
    {
        char nextChar = search.charAt(i);

        if (nextChar == '\n' || nextChar == '\r' || nextChar == '\u0085'
            || nextChar == '\u2028' || nextChar == '\u2029')
        {
            return true;
        }

        return false;
    }


    private int compareChars(char c1, char c2, boolean caseIndependent)
    {
        if (caseIndependent)
        {
            c1 = Character.toLowerCase(c1);
            c2 = Character.toLowerCase(c2);
        }
        return ((int)c1 - (int)c2);
    }
}
