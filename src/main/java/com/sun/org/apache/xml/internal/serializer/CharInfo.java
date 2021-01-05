


package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.serializer.utils.MsgKey;
import com.sun.org.apache.xml.internal.serializer.utils.SystemIDResolver;
import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import com.sun.org.apache.xml.internal.serializer.utils.WrappedRuntimeException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.xml.transform.TransformerException;


final class CharInfo
{

    private HashMap m_charToString = new HashMap();


    public static final String HTML_ENTITIES_RESOURCE =
                "com.sun.org.apache.xml.internal.serializer.HTMLEntities";


    public static final String XML_ENTITIES_RESOURCE =
                "com.sun.org.apache.xml.internal.serializer.XMLEntities";


    public static final char S_HORIZONAL_TAB = 0x09;


    public static final char S_LINEFEED = 0x0A;


    public static final char S_CARRIAGERETURN = 0x0D;


    final boolean onlyQuotAmpLtGt;


    private static final int ASCII_MAX = 128;


    private boolean[] isSpecialAttrASCII = new boolean[ASCII_MAX];


    private boolean[] isSpecialTextASCII = new boolean[ASCII_MAX];

    private boolean[] isCleanTextASCII = new boolean[ASCII_MAX];


    private int array_of_bits[] = createEmptySetOfIntegers(65535);


    private static final int SHIFT_PER_WORD = 5;


    private static final int LOW_ORDER_BITMASK = 0x1f;


    private int firstWordNotUsed;



    private CharInfo(String entitiesResource, String method)
    {
        this(entitiesResource, method, false);
    }

    private CharInfo(String entitiesResource, String method, boolean internal)
    {
        ResourceBundle entities = null;
        boolean noExtraEntities = true;

        try {
            if (internal) {
                entities = PropertyResourceBundle.getBundle(entitiesResource);
            } else {
                ClassLoader cl = SecuritySupport.getContextClassLoader();
                if (cl != null) {
                    entities = PropertyResourceBundle.getBundle(entitiesResource,
                            Locale.getDefault(), cl);
                }
            }
        } catch (Exception e) {}

        if (entities != null) {
            Enumeration keys = entities.getKeys();
            while (keys.hasMoreElements()){
                String name = (String) keys.nextElement();
                String value = entities.getString(name);
                int code = Integer.parseInt(value);
                defineEntity(name, (char) code);
                if (extraEntity(code))
                    noExtraEntities = false;
            }
            set(S_LINEFEED);
            set(S_CARRIAGERETURN);
        } else {
            InputStream is = null;
            String err = null;

            try {
                if (internal) {
                    is = CharInfo.class.getResourceAsStream(entitiesResource);
                } else {
                    ClassLoader cl = SecuritySupport.getContextClassLoader();
                    if (cl != null) {
                        try {
                            is = cl.getResourceAsStream(entitiesResource);
                        } catch (Exception e) {
                            err = e.getMessage();
                        }
                    }

                    if (is == null) {
                        try {
                            URL url = new URL(entitiesResource);
                            is = url.openStream();
                        } catch (Exception e) {
                            err = e.getMessage();
                        }
                    }
                }

                if (is == null) {
                    throw new RuntimeException(
                        Utils.messages.createMessage(
                            MsgKey.ER_RESOURCE_COULD_NOT_FIND,
                            new Object[] {entitiesResource, err}));
                }

                BufferedReader reader;
                try {
                    reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    reader = new BufferedReader(new InputStreamReader(is));
                }

                String line = reader.readLine();

                while (line != null) {
                    if (line.length() == 0 || line.charAt(0) == '#') {
                        line = reader.readLine();

                        continue;
                    }

                    int index = line.indexOf(' ');

                    if (index > 1) {
                        String name = line.substring(0, index);

                        ++index;

                        if (index < line.length()) {
                            String value = line.substring(index);
                            index = value.indexOf(' ');

                            if (index > 0) {
                                value = value.substring(0, index);
                            }

                            int code = Integer.parseInt(value);

                            defineEntity(name, (char) code);
                            if (extraEntity(code))
                                noExtraEntities = false;
                        }
                    }

                    line = reader.readLine();
                }

                is.close();
                set(S_LINEFEED);
                set(S_CARRIAGERETURN);
            } catch (Exception e) {
                throw new RuntimeException(
                    Utils.messages.createMessage(
                        MsgKey.ER_RESOURCE_COULD_NOT_LOAD,
                        new Object[] { entitiesResource,
                                       e.toString(),
                                       entitiesResource,
                                       e.toString()}));
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception except) {}
                }
            }
        }


        for (int ch = 0; ch <ASCII_MAX; ch++)
        if((((0x20 <= ch || (0x0A == ch || 0x0D == ch || 0x09 == ch)))
             && (!get(ch))) || ('"' == ch))
        {
            isCleanTextASCII[ch] = true;
            isSpecialTextASCII[ch] = false;
        }
        else {
            isCleanTextASCII[ch] = false;
            isSpecialTextASCII[ch] = true;
        }



        onlyQuotAmpLtGt = noExtraEntities;

        for (int i=0; i<ASCII_MAX; i++)
            isSpecialAttrASCII[i] = get(i);


        if (Method.XML.equals(method))
        {
            isSpecialAttrASCII[S_HORIZONAL_TAB] = true;
        }
    }


    private void defineEntity(String name, char value)
    {
        StringBuilder sb = new StringBuilder("&");
        sb.append(name);
        sb.append(';');
        String entityString = sb.toString();

        defineChar2StringMapping(entityString, value);
    }


    String getOutputStringForChar(char value)
    {
        CharKey charKey = new CharKey();
        charKey.setChar(value);
        return (String) m_charToString.get(charKey);
    }


    final boolean isSpecialAttrChar(int value)
    {
        if (value < ASCII_MAX)
            return isSpecialAttrASCII[value];

        return get(value);
    }


    final boolean isSpecialTextChar(int value)
    {
        if (value < ASCII_MAX)
            return isSpecialTextASCII[value];

        return get(value);
    }


    final boolean isTextASCIIClean(int value)
    {
        return isCleanTextASCII[value];
    }



    static CharInfo getCharInfoInternal(String entitiesFileName, String method)
    {
        CharInfo charInfo = (CharInfo) m_getCharInfoCache.get(entitiesFileName);
        if (charInfo != null) {
            return charInfo;
        }

        charInfo = new CharInfo(entitiesFileName, method, true);
        m_getCharInfoCache.put(entitiesFileName, charInfo);
        return charInfo;
    }


    static CharInfo getCharInfo(String entitiesFileName, String method)
    {
        try {
            return new CharInfo(entitiesFileName, method, false);
        } catch (Exception e) {}

        String absoluteEntitiesFileName;

        if (entitiesFileName.indexOf(':') < 0) {
            absoluteEntitiesFileName =
                SystemIDResolver.getAbsoluteURIFromRelative(entitiesFileName);
        } else {
            try {
                absoluteEntitiesFileName =
                    SystemIDResolver.getAbsoluteURI(entitiesFileName, null);
            } catch (TransformerException te) {
                throw new WrappedRuntimeException(te);
            }
        }

        return new CharInfo(absoluteEntitiesFileName, method, false);
    }


    private static HashMap m_getCharInfoCache = new HashMap();


    private static int arrayIndex(int i) {
        return (i >> SHIFT_PER_WORD);
    }


    private static int bit(int i) {
        int ret = (1 << (i & LOW_ORDER_BITMASK));
        return ret;
    }


    private int[] createEmptySetOfIntegers(int max) {
        firstWordNotUsed = 0; int[] arr = new int[arrayIndex(max - 1) + 1];
            return arr;

    }


    private final void set(int i) {
        setASCIIdirty(i);

        int j = (i >> SHIFT_PER_WORD); int k = j + 1;

        if(firstWordNotUsed < k) firstWordNotUsed = k;

        array_of_bits[j] |= (1 << (i & LOW_ORDER_BITMASK));
    }



    private final boolean get(int i) {

        boolean in_the_set = false;
        int j = (i >> SHIFT_PER_WORD); if(j < firstWordNotUsed)
            in_the_set = (array_of_bits[j] &
                          (1 << (i & LOW_ORDER_BITMASK))
            ) != 0;  return in_the_set;
    }

    private boolean extraEntity(int entityValue)
    {
        boolean extra = false;
        if (entityValue < 128)
        {
            switch (entityValue)
            {
                case 34 : case 38 : case 60 : case 62 : break;
                default : extra = true;
            }
        }
        return extra;
    }


    private void setASCIIdirty(int j)
    {
        if (0 <= j && j < ASCII_MAX)
        {
            isCleanTextASCII[j] = false;
            isSpecialTextASCII[j] = true;
        }
    }


    private void setASCIIclean(int j)
    {
        if (0 <= j && j < ASCII_MAX)
        {
            isCleanTextASCII[j] = true;
            isSpecialTextASCII[j] = false;
        }
    }

    private void defineChar2StringMapping(String outputString, char inputChar)
    {
        CharKey character = new CharKey(inputChar);
        m_charToString.put(character, outputString);
        set(inputChar);
    }


    private static class CharKey extends Object
    {


      private char m_char;


      public CharKey(char key)
      {
        m_char = key;
      }


      public CharKey()
      {
      }


      public final void setChar(char c)
      {
        m_char = c;
      }




      public final int hashCode()
      {
        return (int)m_char;
      }


      public final boolean equals(Object obj)
      {
        return ((CharKey)obj).m_char == m_char;
      }
    }


}
