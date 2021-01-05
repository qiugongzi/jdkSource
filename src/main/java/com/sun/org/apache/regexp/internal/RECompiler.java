


package com.sun.org.apache.regexp.internal;

import com.sun.org.apache.regexp.internal.RE;
import java.util.Hashtable;


public class RECompiler
{
    char[] instruction;                                 int lenInstruction;                                 String pattern;                                     int len;                                            int idx;                                            int parens;                                         static final int NODE_NORMAL   = 0;                 static final int NODE_NULLABLE = 1;                 static final int NODE_TOPLEVEL = 2;                 static final int ESC_MASK      = 0xffff0;           static final int ESC_BACKREF   = 0xfffff;           static final int ESC_COMPLEX   = 0xffffe;           static final int ESC_CLASS     = 0xffffd;           int maxBrackets = 10;                               static final int bracketUnbounded = -1;             int brackets = 0;                                   int[] bracketStart = null;                          int[] bracketEnd = null;                            int[] bracketMin = null;                            int[] bracketOpt = null;                            static Hashtable hashPOSIX = new Hashtable();
    static
    {
        hashPOSIX.put("alnum",     new Character(RE.POSIX_CLASS_ALNUM));
        hashPOSIX.put("alpha",     new Character(RE.POSIX_CLASS_ALPHA));
        hashPOSIX.put("blank",     new Character(RE.POSIX_CLASS_BLANK));
        hashPOSIX.put("cntrl",     new Character(RE.POSIX_CLASS_CNTRL));
        hashPOSIX.put("digit",     new Character(RE.POSIX_CLASS_DIGIT));
        hashPOSIX.put("graph",     new Character(RE.POSIX_CLASS_GRAPH));
        hashPOSIX.put("lower",     new Character(RE.POSIX_CLASS_LOWER));
        hashPOSIX.put("print",     new Character(RE.POSIX_CLASS_PRINT));
        hashPOSIX.put("punct",     new Character(RE.POSIX_CLASS_PUNCT));
        hashPOSIX.put("space",     new Character(RE.POSIX_CLASS_SPACE));
        hashPOSIX.put("upper",     new Character(RE.POSIX_CLASS_UPPER));
        hashPOSIX.put("xdigit",    new Character(RE.POSIX_CLASS_XDIGIT));
        hashPOSIX.put("javastart", new Character(RE.POSIX_CLASS_JSTART));
        hashPOSIX.put("javapart",  new Character(RE.POSIX_CLASS_JPART));
    }


    public RECompiler()
    {
        instruction = new char[128];
        lenInstruction = 0;
    }


    void ensure(int n)
    {
        int curlen = instruction.length;

        if (lenInstruction + n >= curlen)
        {
            while (lenInstruction + n >= curlen)
            {
                curlen *= 2;
            }

            char[] newInstruction = new char[curlen];
            System.arraycopy(instruction, 0, newInstruction, 0, lenInstruction);
            instruction = newInstruction;
        }
    }


    void emit(char c)
    {
        ensure(1);

        instruction[lenInstruction++] = c;
    }


    void nodeInsert(char opcode, int opdata, int insertAt)
    {
        ensure(RE.nodeSize);

        System.arraycopy(instruction, insertAt, instruction, insertAt + RE.nodeSize, lenInstruction - insertAt);
        instruction[insertAt + RE.offsetOpcode] = opcode;
        instruction[insertAt + RE.offsetOpdata] = (char)opdata;
        instruction[insertAt + RE.offsetNext] = 0;
        lenInstruction += RE.nodeSize;
    }


    void setNextOfEnd(int node, int pointTo)
    {
        int next = instruction[node + RE.offsetNext];
        while ( next != 0 && node < lenInstruction )
        {
            if ( node == pointTo ) {
              pointTo = lenInstruction;
            }
            node += next;
            next = instruction[node + RE.offsetNext];
        }
        if ( node < lenInstruction ) {
            instruction[node + RE.offsetNext] = (char)(short)(pointTo - node);
        }
    }


    int node(char opcode, int opdata)
    {
        ensure(RE.nodeSize);

        instruction[lenInstruction + RE.offsetOpcode] = opcode;
        instruction[lenInstruction + RE.offsetOpdata] = (char)opdata;
        instruction[lenInstruction + RE.offsetNext] = 0;
        lenInstruction += RE.nodeSize;

        return lenInstruction - RE.nodeSize;
    }



    void internalError() throws Error
    {
        throw new Error("Internal error!");
    }


    void syntaxError(String s) throws RESyntaxException
    {
        throw new RESyntaxException(s);
    }


    void allocBrackets()
    {
        if (bracketStart == null)
        {
            bracketStart = new int[maxBrackets];
            bracketEnd   = new int[maxBrackets];
            bracketMin   = new int[maxBrackets];
            bracketOpt   = new int[maxBrackets];

            for (int i = 0; i < maxBrackets; i++)
            {
                bracketStart[i] = bracketEnd[i] = bracketMin[i] = bracketOpt[i] = -1;
            }
        }
    }


    synchronized void reallocBrackets() {
        if (bracketStart == null) {
            allocBrackets();
        }

        int new_size = maxBrackets * 2;
        int[] new_bS = new int[new_size];
        int[] new_bE = new int[new_size];
        int[] new_bM = new int[new_size];
        int[] new_bO = new int[new_size];
        for (int i=brackets; i<new_size; i++) {
            new_bS[i] = new_bE[i] = new_bM[i] = new_bO[i] = -1;
        }
        System.arraycopy(bracketStart,0, new_bS,0, brackets);
        System.arraycopy(bracketEnd,0,   new_bE,0, brackets);
        System.arraycopy(bracketMin,0,   new_bM,0, brackets);
        System.arraycopy(bracketOpt,0,   new_bO,0, brackets);
        bracketStart = new_bS;
        bracketEnd   = new_bE;
        bracketMin   = new_bM;
        bracketOpt   = new_bO;
        maxBrackets  = new_size;
    }


    void bracket() throws RESyntaxException
    {
        if (idx >= len || pattern.charAt(idx++) != '{')
        {
            internalError();
        }

        if (idx >= len || !Character.isDigit(pattern.charAt(idx)))
        {
            syntaxError("Expected digit");
        }

        StringBuffer number = new StringBuffer();
        while (idx < len && Character.isDigit(pattern.charAt(idx)))
        {
            number.append(pattern.charAt(idx++));
        }
        try
        {
            bracketMin[brackets] = Integer.parseInt(number.toString());
        }
        catch (NumberFormatException e)
        {
            syntaxError("Expected valid number");
        }

        if (idx >= len)
        {
            syntaxError("Expected comma or right bracket");
        }

        if (pattern.charAt(idx) == '}')
        {
            idx++;
            bracketOpt[brackets] = 0;
            return;
        }

        if (idx >= len || pattern.charAt(idx++) != ',')
        {
            syntaxError("Expected comma");
        }

        if (idx >= len)
        {
            syntaxError("Expected comma or right bracket");
        }

        if (pattern.charAt(idx) == '}')
        {
            idx++;
            bracketOpt[brackets] = bracketUnbounded;
            return;
        }

        if (idx >= len || !Character.isDigit(pattern.charAt(idx)))
        {
            syntaxError("Expected digit");
        }

        number.setLength(0);
        while (idx < len && Character.isDigit(pattern.charAt(idx)))
        {
            number.append(pattern.charAt(idx++));
        }
        try
        {
            bracketOpt[brackets] = Integer.parseInt(number.toString()) - bracketMin[brackets];
        }
        catch (NumberFormatException e)
        {
            syntaxError("Expected valid number");
        }

        if (bracketOpt[brackets] < 0)
        {
            syntaxError("Bad range");
        }

        if (idx >= len || pattern.charAt(idx++) != '}')
        {
            syntaxError("Missing close brace");
        }
    }


    int escape() throws RESyntaxException
    {
        if (pattern.charAt(idx) != '\\')
        {
            internalError();
        }

        if (idx + 1 == len)
        {
            syntaxError("Escape terminates string");
        }

        idx += 2;
        char escapeChar = pattern.charAt(idx - 1);
        switch (escapeChar)
        {
            case RE.E_BOUND:
            case RE.E_NBOUND:
                return ESC_COMPLEX;

            case RE.E_ALNUM:
            case RE.E_NALNUM:
            case RE.E_SPACE:
            case RE.E_NSPACE:
            case RE.E_DIGIT:
            case RE.E_NDIGIT:
                return ESC_CLASS;

            case 'u':
            case 'x':
                {
                    int hexDigits = (escapeChar == 'u' ? 4 : 2);

                    int val = 0;
                    for ( ; idx < len && hexDigits-- > 0; idx++)
                    {
                        char c = pattern.charAt(idx);

                        if (c >= '0' && c <= '9')
                        {
                            val = (val << 4) + c - '0';
                        }
                        else
                        {
                            c = Character.toLowerCase(c);
                            if (c >= 'a' && c <= 'f')
                            {
                                val = (val << 4) + (c - 'a') + 10;
                            }
                            else
                            {
                                syntaxError("Expected " + hexDigits + " hexadecimal digits after \\" + escapeChar);
                            }
                        }
                    }
                    return val;
                }

            case 't':
                return '\t';

            case 'n':
                return '\n';

            case 'r':
                return '\r';

            case 'f':
                return '\f';

            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':

                if ((idx < len && Character.isDigit(pattern.charAt(idx))) || escapeChar == '0')
                {
                    int val = escapeChar - '0';
                    if (idx < len && Character.isDigit(pattern.charAt(idx)))
                    {
                        val = ((val << 3) + (pattern.charAt(idx++) - '0'));
                        if (idx < len && Character.isDigit(pattern.charAt(idx)))
                        {
                            val = ((val << 3) + (pattern.charAt(idx++) - '0'));
                        }
                    }
                    return val;
                }

                return ESC_BACKREF;

            default:

                return escapeChar;
        }
    }


    int characterClass() throws RESyntaxException
    {
        if (pattern.charAt(idx) != '[')
        {
            internalError();
        }

        if ((idx + 1) >= len || pattern.charAt(++idx) == ']')
        {
            syntaxError("Empty or unterminated class");
        }

        if (idx < len && pattern.charAt(idx) == ':')
        {
            idx++;

            int idxStart = idx;
            while (idx < len && pattern.charAt(idx) >= 'a' && pattern.charAt(idx) <= 'z')
            {
                idx++;
            }

            if ((idx + 1) < len && pattern.charAt(idx) == ':' && pattern.charAt(idx + 1) == ']')
            {
                String charClass = pattern.substring(idxStart, idx);

                Character i = (Character)hashPOSIX.get(charClass);
                if (i != null)
                {
                    idx += 2;

                    return node(RE.OP_POSIXCLASS, i.charValue());
                }
                syntaxError("Invalid POSIX character class '" + charClass + "'");
            }
            syntaxError("Invalid POSIX character class syntax");
        }

        int ret = node(RE.OP_ANYOF, 0);

        char CHAR_INVALID = Character.MAX_VALUE;
        char last = CHAR_INVALID;
        char simpleChar = 0;
        boolean include = true;
        boolean definingRange = false;
        int idxFirst = idx;
        char rangeStart = Character.MIN_VALUE;
        char rangeEnd;
        RERange range = new RERange();
        while (idx < len && pattern.charAt(idx) != ']')
        {

            switchOnCharacter:

            switch (pattern.charAt(idx))
            {
                case '^':
                    include = !include;
                    if (idx == idxFirst)
                    {
                        range.include(Character.MIN_VALUE, Character.MAX_VALUE, true);
                    }
                    idx++;
                    continue;

                case '\\':
                {
                    int c;
                    switch (c = escape ())
                    {
                        case ESC_COMPLEX:
                        case ESC_BACKREF:

                            syntaxError("Bad character class");

                        case ESC_CLASS:

                            if (definingRange)
                            {
                                syntaxError("Bad character class");
                            }

                            switch (pattern.charAt(idx - 1))
                            {
                                case RE.E_NSPACE:
                                case RE.E_NDIGIT:
                                case RE.E_NALNUM:
                                    syntaxError("Bad character class");

                                case RE.E_SPACE:
                                    range.include('\t', include);
                                    range.include('\r', include);
                                    range.include('\f', include);
                                    range.include('\n', include);
                                    range.include('\b', include);
                                    range.include(' ', include);
                                    break;

                                case RE.E_ALNUM:
                                    range.include('a', 'z', include);
                                    range.include('A', 'Z', include);
                                    range.include('_', include);

                                    case RE.E_DIGIT:
                                    range.include('0', '9', include);
                                    break;
                            }

                            last = CHAR_INVALID;
                            break;

                        default:

                            simpleChar = (char) c;
                            break switchOnCharacter;
                    }
                }
                continue;

                case '-':

                    if (definingRange)
                    {
                        syntaxError("Bad class range");
                    }
                    definingRange = true;

                    rangeStart = (last == CHAR_INVALID ? 0 : last);

                    if ((idx + 1) < len && pattern.charAt(++idx) == ']')
                    {
                        simpleChar = Character.MAX_VALUE;
                        break;
                    }
                    continue;

                default:
                    simpleChar = pattern.charAt(idx++);
                    break;
            }

            if (definingRange)
            {
                rangeEnd = simpleChar;

                if (rangeStart >= rangeEnd)
                {
                    syntaxError("Bad character class");
                }
                range.include(rangeStart, rangeEnd, include);

                last = CHAR_INVALID;
                definingRange = false;
            }
            else
            {
                if (idx >= len || pattern.charAt(idx) != '-')
                {
                    range.include(simpleChar, include);
                }
                last = simpleChar;
            }
        }

        if (idx == len)
        {
            syntaxError("Unterminated character class");
        }

        idx++;

        instruction[ret + RE.offsetOpdata] = (char)range.num;
        for (int i = 0; i < range.num; i++)
        {
            emit((char)range.minRange[i]);
            emit((char)range.maxRange[i]);
        }
        return ret;
    }


    int atom() throws RESyntaxException
    {
        int ret = node(RE.OP_ATOM, 0);

        int lenAtom = 0;

        atomLoop:

        while (idx < len)
        {
            if ((idx + 1) < len)
            {
                char c = pattern.charAt(idx + 1);

                if (pattern.charAt(idx) == '\\')
                {
                    int idxEscape = idx;
                    escape();
                    if (idx < len)
                    {
                        c = pattern.charAt(idx);
                    }
                    idx = idxEscape;
                }

                switch (c)
                {
                    case '{':
                    case '?':
                    case '*':
                    case '+':

                        if (lenAtom != 0)
                        {
                            break atomLoop;
                        }
                }
            }

            switch (pattern.charAt(idx))
            {
                case ']':
                case '^':
                case '$':
                case '.':
                case '[':
                case '(':
                case ')':
                case '|':
                    break atomLoop;

                case '{':
                case '?':
                case '*':
                case '+':

                    if (lenAtom == 0)
                    {
                        syntaxError("Missing operand to closure");
                    }
                    break atomLoop;

                case '\\':

                    {
                        int idxBeforeEscape = idx;
                        int c = escape();

                        if ((c & ESC_MASK) == ESC_MASK)
                        {
                            idx = idxBeforeEscape;
                            break atomLoop;
                        }

                        emit((char) c);
                        lenAtom++;
                    }
                    break;

                default:

                    emit(pattern.charAt(idx++));
                    lenAtom++;
                    break;
            }
        }

        if (lenAtom == 0)
        {
            internalError();
        }

        instruction[ret + RE.offsetOpdata] = (char)lenAtom;
        return ret;
    }


    int terminal(int[] flags) throws RESyntaxException
    {
        switch (pattern.charAt(idx))
        {
        case RE.OP_EOL:
        case RE.OP_BOL:
        case RE.OP_ANY:
            return node(pattern.charAt(idx++), 0);

        case '[':
            return characterClass();

        case '(':
            return expr(flags);

        case ')':
            syntaxError("Unexpected close paren");

        case '|':
            internalError();

        case ']':
            syntaxError("Mismatched class");

        case 0:
            syntaxError("Unexpected end of input");

        case '?':
        case '+':
        case '{':
        case '*':
            syntaxError("Missing operand to closure");

        case '\\':
            {
                int idxBeforeEscape = idx;

                switch (escape())
                {
                    case ESC_CLASS:
                    case ESC_COMPLEX:
                        flags[0] &= ~NODE_NULLABLE;
                        return node(RE.OP_ESCAPE, pattern.charAt(idx - 1));

                    case ESC_BACKREF:
                        {
                            char backreference = (char)(pattern.charAt(idx - 1) - '0');
                            if (parens <= backreference)
                            {
                                syntaxError("Bad backreference");
                            }
                            flags[0] |= NODE_NULLABLE;
                            return node(RE.OP_BACKREF, backreference);
                        }

                    default:

                        idx = idxBeforeEscape;
                        flags[0] &= ~NODE_NULLABLE;
                        break;
                }
            }
        }

        flags[0] &= ~NODE_NULLABLE;
        return atom();
    }


    int closure(int[] flags) throws RESyntaxException
    {
        int idxBeforeTerminal = idx;

        int[] terminalFlags = { NODE_NORMAL };

        int ret = terminal(terminalFlags);

        flags[0] |= terminalFlags[0];

        if (idx >= len)
        {
            return ret;
        }
        boolean greedy = true;
        char closureType = pattern.charAt(idx);
        switch (closureType)
        {
            case '?':
            case '*':

                flags[0] |= NODE_NULLABLE;

            case '+':

                idx++;

            case '{':

                int opcode = instruction[ret + RE.offsetOpcode];
                if (opcode == RE.OP_BOL || opcode == RE.OP_EOL)
                {
                    syntaxError("Bad closure operand");
                }
                if ((terminalFlags[0] & NODE_NULLABLE) != 0)
                {
                    syntaxError("Closure operand can't be nullable");
                }
                break;
        }

        if (idx < len && pattern.charAt(idx) == '?')
        {
            idx++;
            greedy = false;
        }

        if (greedy)
        {
            switch (closureType)
            {
                case '{':
                {
                    boolean found = false;
                    int i;
                    allocBrackets();
                    for (i = 0; i < brackets; i++)
                    {
                        if (bracketStart[i] == idx)
                        {
                            found = true;
                            break;
                        }
                    }

                    if (!found)
                    {
                        if (brackets >= maxBrackets)
                        {
                            reallocBrackets();
                        }
                        bracketStart[brackets] = idx;
                        bracket();
                        bracketEnd[brackets] = idx;
                        i = brackets++;
                    }

                    if (bracketMin[i]-- > 0)
                    {
                        if (bracketMin[i] > 0 || bracketOpt[i] != 0) {
                            for (int j = 0; j < brackets; j++) {
                                if (j != i && bracketStart[j] < idx
                                    && bracketStart[j] >= idxBeforeTerminal)
                                {
                                    brackets--;
                                    bracketStart[j] = bracketStart[brackets];
                                    bracketEnd[j] = bracketEnd[brackets];
                                    bracketMin[j] = bracketMin[brackets];
                                    bracketOpt[j] = bracketOpt[brackets];
                                }
                            }

                            idx = idxBeforeTerminal;
                        } else {
                            idx = bracketEnd[i];
                        }
                        break;
                    }

                    if (bracketOpt[i] == bracketUnbounded)
                    {
                        closureType = '*';
                        bracketOpt[i] = 0;
                        idx = bracketEnd[i];
                    }
                    else
                        if (bracketOpt[i]-- > 0)
                        {
                            if (bracketOpt[i] > 0)
                            {
                                idx = idxBeforeTerminal;
                            } else {
                                idx = bracketEnd[i];
                            }
                            closureType = '?';
                        }
                        else
                        {
                            lenInstruction = ret;
                            node(RE.OP_NOTHING, 0);

                            idx = bracketEnd[i];
                            break;
                        }
                }

                case '?':
                case '*':

                    if (!greedy)
                    {
                        break;
                    }

                    if (closureType == '?')
                    {
                        nodeInsert(RE.OP_BRANCH, 0, ret);                 setNextOfEnd(ret, node (RE.OP_BRANCH, 0));        int nothing = node (RE.OP_NOTHING, 0);            setNextOfEnd(ret, nothing);                       setNextOfEnd(ret + RE.nodeSize, nothing);         }

                    if (closureType == '*')
                    {
                        nodeInsert(RE.OP_BRANCH, 0, ret);                         setNextOfEnd(ret + RE.nodeSize, node(RE.OP_BRANCH, 0));   setNextOfEnd(ret + RE.nodeSize, node(RE.OP_GOTO, 0));     setNextOfEnd(ret + RE.nodeSize, ret);                     setNextOfEnd(ret, node(RE.OP_BRANCH, 0));                 setNextOfEnd(ret, node(RE.OP_NOTHING, 0));                }
                    break;

                case '+':
                {
                    int branch;
                    branch = node(RE.OP_BRANCH, 0);                   setNextOfEnd(ret, branch);                        setNextOfEnd(node(RE.OP_GOTO, 0), ret);           setNextOfEnd(branch, node(RE.OP_BRANCH, 0));      setNextOfEnd(ret, node(RE.OP_NOTHING, 0));        }
                break;
            }
        }
        else
        {
            setNextOfEnd(ret, node(RE.OP_END, 0));

            switch (closureType)
            {
                case '?':
                    nodeInsert(RE.OP_RELUCTANTMAYBE, 0, ret);
                    break;

                case '*':
                    nodeInsert(RE.OP_RELUCTANTSTAR, 0, ret);
                    break;

                case '+':
                    nodeInsert(RE.OP_RELUCTANTPLUS, 0, ret);
                    break;
            }

            setNextOfEnd(ret, lenInstruction);
        }
        return ret;
    }


    int branch(int[] flags) throws RESyntaxException
    {
        int node;
        int ret = node(RE.OP_BRANCH, 0);
        int chain = -1;
        int[] closureFlags = new int[1];
        boolean nullable = true;
        while (idx < len && pattern.charAt(idx) != '|' && pattern.charAt(idx) != ')')
        {
            closureFlags[0] = NODE_NORMAL;
            node = closure(closureFlags);
            if (closureFlags[0] == NODE_NORMAL)
            {
                nullable = false;
            }

            if (chain != -1)
            {
                setNextOfEnd(chain, node);
            }

            chain = node;
        }

        if (chain == -1)
        {
            node(RE.OP_NOTHING, 0);
        }

        if (nullable)
        {
            flags[0] |= NODE_NULLABLE;
        }
        return ret;
    }


    int expr(int[] flags) throws RESyntaxException
    {
        int paren = -1;
        int ret = -1;
        int closeParens = parens;
        if ((flags[0] & NODE_TOPLEVEL) == 0 && pattern.charAt(idx) == '(')
        {
            if ( idx + 2 < len && pattern.charAt( idx + 1 ) == '?' && pattern.charAt( idx + 2 ) == ':' )
            {
                paren = 2;
                idx += 3;
                ret = node( RE.OP_OPEN_CLUSTER, 0 );
            }
            else
            {
                paren = 1;
                idx++;
                ret = node(RE.OP_OPEN, parens++);
            }
        }
        flags[0] &= ~NODE_TOPLEVEL;

        int branch = branch(flags);
        if (ret == -1)
        {
            ret = branch;
        }
        else
        {
            setNextOfEnd(ret, branch);
        }

        while (idx < len && pattern.charAt(idx) == '|')
        {
            idx++;
            branch = branch(flags);
            setNextOfEnd(ret, branch);
        }

        int end;
        if ( paren > 0 )
        {
            if (idx < len && pattern.charAt(idx) == ')')
            {
                idx++;
            }
            else
            {
                syntaxError("Missing close paren");
            }
            if ( paren == 1 )
            {
                end = node(RE.OP_CLOSE, closeParens);
            }
            else
            {
                end = node( RE.OP_CLOSE_CLUSTER, 0 );
            }
        }
        else
        {
            end = node(RE.OP_END, 0);
        }

        setNextOfEnd(ret, end);

        int currentNode = ret;
        int nextNodeOffset = instruction[ currentNode + RE.offsetNext ];
        while ( nextNodeOffset != 0 && currentNode < lenInstruction )
        {
            if ( instruction[ currentNode + RE.offsetOpcode ] == RE.OP_BRANCH )
            {
                setNextOfEnd( currentNode + RE.nodeSize, end );
            }
            nextNodeOffset = instruction[ currentNode + RE.offsetNext ];
            currentNode += nextNodeOffset;
        }

        return ret;
    }


    public REProgram compile(String pattern) throws RESyntaxException
    {
        this.pattern = pattern;                         len = pattern.length();                         idx = 0;                                        lenInstruction = 0;                             parens = 1;                                     brackets = 0;                                   int[] flags = { NODE_TOPLEVEL };

        expr(flags);

        if (idx != len)
        {
            if (pattern.charAt(idx) == ')')
            {
                syntaxError("Unmatched close paren");
            }
            syntaxError("Unexpected input remains");
        }

        char[] ins = new char[lenInstruction];
        System.arraycopy(instruction, 0, ins, 0, lenInstruction);
        return new REProgram(parens, ins);
    }


    class RERange
    {
        int size = 16;                      int[] minRange = new int[size];     int[] maxRange = new int[size];     int num = 0;                        void delete(int index)
        {
            if (num == 0 || index >= num)
            {
                return;
            }

            while (++index < num)
            {
                if (index - 1 >= 0)
                {
                    minRange[index-1] = minRange[index];
                    maxRange[index-1] = maxRange[index];
                }
            }

            num--;
        }


        void merge(int min, int max)
        {
            for (int i = 0; i < num; i++)
            {
                if (min >= minRange[i] && max <= maxRange[i])
                {
                    return;
                }

                else if (min <= minRange[i] && max >= maxRange[i])
                {
                    delete(i);
                    merge(min, max);
                    return;
                }

                else if (min >= minRange[i] && min <= maxRange[i])
                {
                    delete(i);
                    min = minRange[i];
                    merge(min, max);
                    return;
                }

                else if (max >= minRange[i] && max <= maxRange[i])
                {
                    delete(i);
                    max = maxRange[i];
                    merge(min, max);
                    return;
                }
            }

            if (num >= size)
            {
                size *= 2;
                int[] newMin = new int[size];
                int[] newMax = new int[size];
                System.arraycopy(minRange, 0, newMin, 0, num);
                System.arraycopy(maxRange, 0, newMax, 0, num);
                minRange = newMin;
                maxRange = newMax;
            }
            minRange[num] = min;
            maxRange[num] = max;
            num++;
        }


        void remove(int min, int max)
        {
            for (int i = 0; i < num; i++)
            {
                if (minRange[i] >= min && maxRange[i] <= max)
                {
                    delete(i);
                    i--;
                    return;
                }

                else if (min >= minRange[i] && max <= maxRange[i])
                {
                    int minr = minRange[i];
                    int maxr = maxRange[i];
                    delete(i);
                    if (minr < min)
                    {
                        merge(minr, min - 1);
                    }
                    if (max < maxr)
                    {
                        merge(max + 1, maxr);
                    }
                    return;
                }

                else if (minRange[i] >= min && minRange[i] <= max)
                {
                    minRange[i] = max + 1;
                    return;
                }

                else if (maxRange[i] >= min && maxRange[i] <= max)
                {
                    maxRange[i] = min - 1;
                    return;
                }
            }
        }


        void include(int min, int max, boolean include)
        {
            if (include)
            {
                merge(min, max);
            }
            else
            {
                remove(min, max);
            }
        }


        void include(char minmax, boolean include)
        {
            include(minmax, minmax, include);
        }
    }
}
