


package com.sun.org.apache.xpath.internal.compiler;

import java.util.Vector;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.res.XPATHErrorResources;


class Lexer
{


  private Compiler m_compiler;


  PrefixResolver m_namespaceContext;


  XPathParser m_processor;


  static final int TARGETEXTRA = 10000;


  private int m_patternMap[] = new int[100];


  private int m_patternMapSize;


  Lexer(Compiler compiler, PrefixResolver resolver,
        XPathParser xpathProcessor)
  {

    m_compiler = compiler;
    m_namespaceContext = resolver;
    m_processor = xpathProcessor;
  }


  void tokenize(String pat) throws javax.xml.transform.TransformerException
  {
    tokenize(pat, null);
  }


  void tokenize(String pat, Vector targetStrings)
          throws javax.xml.transform.TransformerException
  {

    m_compiler.m_currentPattern = pat;
    m_patternMapSize = 0;

    m_compiler.m_opMap = new OpMapVector(OpMap.MAXTOKENQUEUESIZE * 5, OpMap.BLOCKTOKENQUEUESIZE * 5, OpMap.MAPINDEX_LENGTH);

    int nChars = pat.length();
    int startSubstring = -1;
    int posOfNSSep = -1;
    boolean isStartOfPat = true;
    boolean isAttrName = false;
    boolean isNum = false;

    int nesting = 0;

    for (int i = 0; i < nChars; i++)
    {
      char c = pat.charAt(i);

      switch (c)
      {
      case '\"' :
      {
        if (startSubstring != -1)
        {
          isNum = false;
          isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
          isAttrName = false;

          if (-1 != posOfNSSep)
          {
            posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, i);
          }
          else
          {
            addToTokenQueue(pat.substring(startSubstring, i));
          }
        }

        startSubstring = i;

        for (i++; (i < nChars) && ((c = pat.charAt(i)) != '\"'); i++);

        if (c == '\"' && i < nChars)
        {
          addToTokenQueue(pat.substring(startSubstring, i + 1));

          startSubstring = -1;
        }
        else
        {
          m_processor.error(XPATHErrorResources.ER_EXPECTED_DOUBLE_QUOTE,
                            null);  }
      }
      break;
      case '\'' :
        if (startSubstring != -1)
        {
          isNum = false;
          isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
          isAttrName = false;

          if (-1 != posOfNSSep)
          {
            posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, i);
          }
          else
          {
            addToTokenQueue(pat.substring(startSubstring, i));
          }
        }

        startSubstring = i;

        for (i++; (i < nChars) && ((c = pat.charAt(i)) != '\''); i++);

        if (c == '\'' && i < nChars)
        {
          addToTokenQueue(pat.substring(startSubstring, i + 1));

          startSubstring = -1;
        }
        else
        {
          m_processor.error(XPATHErrorResources.ER_EXPECTED_SINGLE_QUOTE,
                            null);  }
        break;
      case 0x0A :
      case 0x0D :
      case ' ' :
      case '\t' :
        if (startSubstring != -1)
        {
          isNum = false;
          isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
          isAttrName = false;

          if (-1 != posOfNSSep)
          {
            posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, i);
          }
          else
          {
            addToTokenQueue(pat.substring(startSubstring, i));
          }

          startSubstring = -1;
        }
        break;
      case '@' :
        isAttrName = true;

      case '-' :
        if ('-' == c)
        {
          if (!(isNum || (startSubstring == -1)))
          {
            break;
          }

          isNum = false;
        }

      case '(' :
      case '[' :
      case ')' :
      case ']' :
      case '|' :
      case '/' :
      case '*' :
      case '+' :
      case '=' :
      case ',' :
      case '\\' :  case '^' :  case '!' :  case '$' :
      case '<' :
      case '>' :
        if (startSubstring != -1)
        {
          isNum = false;
          isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
          isAttrName = false;

          if (-1 != posOfNSSep)
          {
            posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, i);
          }
          else
          {
            addToTokenQueue(pat.substring(startSubstring, i));
          }

          startSubstring = -1;
        }
        else if (('/' == c) && isStartOfPat)
        {
          isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
        }
        else if ('*' == c)
        {
          isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
          isAttrName = false;
        }

        if (0 == nesting)
        {
          if ('|' == c)
          {
            if (null != targetStrings)
            {
              recordTokenString(targetStrings);
            }

            isStartOfPat = true;
          }
        }

        if ((')' == c) || (']' == c))
        {
          nesting--;
        }
        else if (('(' == c) || ('[' == c))
        {
          nesting++;
        }

        addToTokenQueue(pat.substring(i, i + 1));
        break;
      case ':' :
        if (i>0)
        {
          if (posOfNSSep == (i - 1))
          {
            if (startSubstring != -1)
            {
              if (startSubstring < (i - 1))
                addToTokenQueue(pat.substring(startSubstring, i - 1));
            }

            isNum = false;
            isAttrName = false;
            startSubstring = -1;
            posOfNSSep = -1;

            addToTokenQueue(pat.substring(i - 1, i + 1));

            break;
          }
          else
          {
            posOfNSSep = i;
          }
        }

      default :
        if (-1 == startSubstring)
        {
          startSubstring = i;
          isNum = Character.isDigit(c);
        }
        else if (isNum)
        {
          isNum = Character.isDigit(c);
        }
      }
    }

    if (startSubstring != -1)
    {
      isNum = false;
      isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);

      if ((-1 != posOfNSSep) ||
         ((m_namespaceContext != null) && (m_namespaceContext.handlesNullPrefixes())))
      {
        posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, nChars);
      }
      else
      {
        addToTokenQueue(pat.substring(startSubstring, nChars));
      }
    }

    if (0 == m_compiler.getTokenQueueSize())
    {
      m_processor.error(XPATHErrorResources.ER_EMPTY_EXPRESSION, null);  }
    else if (null != targetStrings)
    {
      recordTokenString(targetStrings);
    }

    m_processor.m_queueMark = 0;
  }


  private boolean mapPatternElemPos(int nesting, boolean isStart,
                                    boolean isAttrName)
  {

    if (0 == nesting)
    {
      if(m_patternMapSize >= m_patternMap.length)
      {
        int patternMap[] = m_patternMap;
        int len = m_patternMap.length;
        m_patternMap = new int[m_patternMapSize + 100];
        System.arraycopy(patternMap, 0, m_patternMap, 0, len);
      }
      if (!isStart)
      {
        m_patternMap[m_patternMapSize - 1] -= TARGETEXTRA;
      }
      m_patternMap[m_patternMapSize] =
        (m_compiler.getTokenQueueSize() - (isAttrName ? 1 : 0)) + TARGETEXTRA;

      m_patternMapSize++;

      isStart = false;
    }

    return isStart;
  }


  private int getTokenQueuePosFromMap(int i)
  {

    int pos = m_patternMap[i];

    return (pos >= TARGETEXTRA) ? (pos - TARGETEXTRA) : pos;
  }


  private final void resetTokenMark(int mark)
  {

    int qsz = m_compiler.getTokenQueueSize();

    m_processor.m_queueMark = (mark > 0)
                              ? ((mark <= qsz) ? mark - 1 : mark) : 0;

    if (m_processor.m_queueMark < qsz)
    {
      m_processor.m_token =
        (String) m_compiler.getTokenQueue().elementAt(m_processor.m_queueMark++);
      m_processor.m_tokenChar = m_processor.m_token.charAt(0);
    }
    else
    {
      m_processor.m_token = null;
      m_processor.m_tokenChar = 0;
    }
  }


  final int getKeywordToken(String key)
  {

    int tok;

    try
    {
      Integer itok = (Integer) Keywords.getKeyWord(key);

      tok = (null != itok) ? itok.intValue() : 0;
    }
    catch (NullPointerException npe)
    {
      tok = 0;
    }
    catch (ClassCastException cce)
    {
      tok = 0;
    }

    return tok;
  }


  private void recordTokenString(Vector targetStrings)
  {

    int tokPos = getTokenQueuePosFromMap(m_patternMapSize - 1);

    resetTokenMark(tokPos + 1);

    if (m_processor.lookahead('(', 1))
    {
      int tok = getKeywordToken(m_processor.m_token);

      switch (tok)
      {
      case OpCodes.NODETYPE_COMMENT :
        targetStrings.addElement(PsuedoNames.PSEUDONAME_COMMENT);
        break;
      case OpCodes.NODETYPE_TEXT :
        targetStrings.addElement(PsuedoNames.PSEUDONAME_TEXT);
        break;
      case OpCodes.NODETYPE_NODE :
        targetStrings.addElement(PsuedoNames.PSEUDONAME_ANY);
        break;
      case OpCodes.NODETYPE_ROOT :
        targetStrings.addElement(PsuedoNames.PSEUDONAME_ROOT);
        break;
      case OpCodes.NODETYPE_ANYELEMENT :
        targetStrings.addElement(PsuedoNames.PSEUDONAME_ANY);
        break;
      case OpCodes.NODETYPE_PI :
        targetStrings.addElement(PsuedoNames.PSEUDONAME_ANY);
        break;
      default :
        targetStrings.addElement(PsuedoNames.PSEUDONAME_ANY);
      }
    }
    else
    {
      if (m_processor.tokenIs('@'))
      {
        tokPos++;

        resetTokenMark(tokPos + 1);
      }

      if (m_processor.lookahead(':', 1))
      {
        tokPos += 2;
      }

      targetStrings.addElement(m_compiler.getTokenQueue().elementAt(tokPos));
    }
  }


  private final void addToTokenQueue(String s)
  {
    m_compiler.getTokenQueue().addElement(s);
  }


  private int mapNSTokens(String pat, int startSubstring, int posOfNSSep,
                          int posOfScan)
           throws javax.xml.transform.TransformerException
 {

    String prefix = "";

    if ((startSubstring >= 0) && (posOfNSSep >= 0))
    {
       prefix = pat.substring(startSubstring, posOfNSSep);
    }
    String uName;

    if ((null != m_namespaceContext) &&!prefix.equals("*")
            &&!prefix.equals("xmlns"))
    {
      try
      {
        if (prefix.length() > 0)
          uName = ((PrefixResolver) m_namespaceContext).getNamespaceForPrefix(
            prefix);
        else
        {

          if (false)
          {
            addToTokenQueue(":");

            String s = pat.substring(posOfNSSep + 1, posOfScan);

            if (s.length() > 0)
              addToTokenQueue(s);

            return -1;
          }
          else
          {
            uName =
              ((PrefixResolver) m_namespaceContext).getNamespaceForPrefix(
                prefix);
          }
        }
      }
      catch (ClassCastException cce)
      {
        uName = m_namespaceContext.getNamespaceForPrefix(prefix);
      }
    }
    else
    {
      uName = prefix;
    }

    if ((null != uName) && (uName.length() > 0))
    {
      addToTokenQueue(uName);
      addToTokenQueue(":");

      String s = pat.substring(posOfNSSep + 1, posOfScan);

      if (s.length() > 0)
        addToTokenQueue(s);
    }
    else
    {
        m_processor.errorForDOM3(XPATHErrorResources.ER_PREFIX_MUST_RESOLVE,
                                                 new String[] {prefix});  }

    return -1;
  }
}
