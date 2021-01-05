

package javax.xml.bind.annotation.adapters;




public class CollapsedStringAdapter extends XmlAdapter<String,String> {

    public String unmarshal(String text) {
        if(text==null)  return null;        int len = text.length();

        int s=0;
        while(s<len) {
            if(isWhiteSpace(text.charAt(s)))
                break;
            s++;
        }
        if(s==len)
            return text;

        StringBuilder result = new StringBuilder(len  );

        if(s!=0) {
            for( int i=0; i<s; i++ )
                result.append(text.charAt(i));
            result.append(' ');
        }

        boolean inStripMode = true;
        for (int i = s+1; i < len; i++) {
            char ch = text.charAt(i);
            boolean b = isWhiteSpace(ch);
            if (inStripMode && b)
                continue; inStripMode = b;
            if (inStripMode)
                result.append(' ');
            else
                result.append(ch);
        }

        len = result.length();
        if (len > 0 && result.charAt(len - 1) == ' ')
            result.setLength(len - 1);
        return result.toString();
    }


    public String marshal(String s) {
        return s;
    }



    protected static boolean isWhiteSpace(char ch) {
        if( ch>0x20 )   return false;

        return ch == 0x9 || ch == 0xA || ch == 0xD || ch == 0x20;
    }
}
