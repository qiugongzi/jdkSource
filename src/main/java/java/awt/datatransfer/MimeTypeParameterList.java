

package java.awt.datatransfer;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;



class MimeTypeParameterList implements Cloneable {


    public MimeTypeParameterList() {
        parameters = new Hashtable<>();
    }

    public MimeTypeParameterList(String rawdata)
        throws MimeTypeParseException
    {
        parameters = new Hashtable<>();

        parse(rawdata);
    }

    public int hashCode() {
        int code = Integer.MAX_VALUE/45; String paramName = null;
        Enumeration<String> enum_ = this.getNames();

        while (enum_.hasMoreElements()) {
            paramName = enum_.nextElement();
            code += paramName.hashCode();
            code += this.get(paramName).hashCode();
        }

        return code;
    } public boolean equals(Object thatObject) {
        if (!(thatObject instanceof MimeTypeParameterList)) {
            return false;
        }
        MimeTypeParameterList that = (MimeTypeParameterList)thatObject;
        if (this.size() != that.size()) {
            return false;
        }
        String name = null;
        String thisValue = null;
        String thatValue = null;
        Set<Map.Entry<String, String>> entries = parameters.entrySet();
        Iterator<Map.Entry<String, String>> iterator = entries.iterator();
        Map.Entry<String, String> entry = null;
        while (iterator.hasNext()) {
            entry = iterator.next();
            name = entry.getKey();
            thisValue = entry.getValue();
            thatValue = that.parameters.get(name);
            if ((thisValue == null) || (thatValue == null)) {
                if (thisValue != thatValue) {
                    return false;
                }
            } else if (!thisValue.equals(thatValue)) {
                return false;
            }
        } return true;
    } protected void parse(String rawdata) throws MimeTypeParseException {
        int length = rawdata.length();
        if(length > 0) {
            int currentIndex = skipWhiteSpace(rawdata, 0);
            int lastIndex = 0;

            if(currentIndex < length) {
                char currentChar = rawdata.charAt(currentIndex);
                while ((currentIndex < length) && (currentChar == ';')) {
                    String name;
                    String value;
                    boolean foundit;

                    ++currentIndex;

                    currentIndex = skipWhiteSpace(rawdata, currentIndex);

                    if(currentIndex < length) {
                        lastIndex = currentIndex;
                        currentChar = rawdata.charAt(currentIndex);
                        while((currentIndex < length) && isTokenChar(currentChar)) {
                            ++currentIndex;
                            currentChar = rawdata.charAt(currentIndex);
                        }
                        name = rawdata.substring(lastIndex, currentIndex).toLowerCase();

                        currentIndex = skipWhiteSpace(rawdata, currentIndex);

                        if((currentIndex < length) && (rawdata.charAt(currentIndex) == '='))  {
                            ++currentIndex;

                            currentIndex = skipWhiteSpace(rawdata, currentIndex);

                            if(currentIndex < length) {
                                currentChar = rawdata.charAt(currentIndex);
                                if(currentChar == '"') {
                                    ++currentIndex;
                                    lastIndex = currentIndex;

                                    if(currentIndex < length) {
                                        foundit = false;
                                        while((currentIndex < length) && !foundit) {
                                            currentChar = rawdata.charAt(currentIndex);
                                            if(currentChar == '\\') {
                                                currentIndex += 2;
                                            } else if(currentChar == '"') {
                                                foundit = true;
                                            } else {
                                                ++currentIndex;
                                            }
                                        }
                                        if(currentChar == '"') {
                                            value = unquote(rawdata.substring(lastIndex, currentIndex));
                                            ++currentIndex;
                                        } else {
                                            throw new MimeTypeParseException("Encountered unterminated quoted parameter value.");
                                        }
                                    } else {
                                        throw new MimeTypeParseException("Encountered unterminated quoted parameter value.");
                                    }
                                } else if(isTokenChar(currentChar)) {
                                    lastIndex = currentIndex;
                                    foundit = false;
                                    while((currentIndex < length) && !foundit) {
                                        currentChar = rawdata.charAt(currentIndex);

                                        if(isTokenChar(currentChar)) {
                                            ++currentIndex;
                                        } else {
                                            foundit = true;
                                        }
                                    }
                                    value = rawdata.substring(lastIndex, currentIndex);
                                } else {
                                    throw new MimeTypeParseException("Unexpected character encountered at index " + currentIndex);
                                }

                                parameters.put(name, value);
                            } else {
                                throw new MimeTypeParseException("Couldn't find a value for parameter named " + name);
                            }
                        } else {
                            throw new MimeTypeParseException("Couldn't find the '=' that separates a parameter name from its value.");
                        }
                    } else {
                        throw new MimeTypeParseException("Couldn't find parameter name");
                    }

                    currentIndex = skipWhiteSpace(rawdata, currentIndex);
                    if(currentIndex < length) {
                        currentChar = rawdata.charAt(currentIndex);
                    }
                }
                if(currentIndex < length) {
                    throw new MimeTypeParseException("More characters encountered in input than expected.");
                }
            }
        }
    }


    public int size() {
        return parameters.size();
    }


    public boolean isEmpty() {
        return parameters.isEmpty();
    }


    public String get(String name) {
        return parameters.get(name.trim().toLowerCase());
    }


    public void set(String name, String value) {
        parameters.put(name.trim().toLowerCase(), value);
    }


    public void remove(String name) {
        parameters.remove(name.trim().toLowerCase());
    }


    public Enumeration<String> getNames() {
        return parameters.keys();
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder(parameters.size() * 16);

        Enumeration<String> keys = parameters.keys();
        while(keys.hasMoreElements())
        {
            buffer.append("; ");

            String key = keys.nextElement();
            buffer.append(key);
            buffer.append('=');
               buffer.append(quote(parameters.get(key)));
        }

        return buffer.toString();
    }



     public Object clone() {
         MimeTypeParameterList newObj = null;
         try {
             newObj = (MimeTypeParameterList)super.clone();
         } catch (CloneNotSupportedException cannotHappen) {
         }
         newObj.parameters = (Hashtable)parameters.clone();
         return newObj;
     }

    private Hashtable<String, String> parameters;

    private static boolean isTokenChar(char c) {
        return ((c > 040) && (c < 0177)) && (TSPECIALS.indexOf(c) < 0);
    }


    private static int skipWhiteSpace(String rawdata, int i) {
        int length = rawdata.length();
        if (i < length) {
            char c =  rawdata.charAt(i);
            while ((i < length) && Character.isWhitespace(c)) {
                ++i;
                c = rawdata.charAt(i);
            }
        }

        return i;
    }


    private static String quote(String value) {
        boolean needsQuotes = false;

        int length = value.length();
        for(int i = 0; (i < length) && !needsQuotes; ++i) {
            needsQuotes = !isTokenChar(value.charAt(i));
        }

        if(needsQuotes) {
            StringBuilder buffer = new StringBuilder((int)(length * 1.5));

            buffer.append('"');

            for(int i = 0; i < length; ++i) {
                char c = value.charAt(i);
                if((c == '\\') || (c == '"')) {
                    buffer.append('\\');
                }
                buffer.append(c);
            }

            buffer.append('"');

            return buffer.toString();
        }
        else
        {
            return value;
        }
    }


    private static String unquote(String value) {
        int valueLength = value.length();
        StringBuilder buffer = new StringBuilder(valueLength);

        boolean escaped = false;
        for(int i = 0; i < valueLength; ++i) {
            char currentChar = value.charAt(i);
            if(!escaped && (currentChar != '\\')) {
                buffer.append(currentChar);
            } else if(escaped) {
                buffer.append(currentChar);
                escaped = false;
            } else {
                escaped = true;
            }
        }

        return buffer.toString();
    }


    private static final String TSPECIALS = "()<>@,;:\\\"/[]?=";

}
