

package javax.xml.bind;


abstract class WhiteSpaceProcessor {

public static String replace(String text) {
        return replace( (CharSequence)text ).toString();
    }


    public static CharSequence replace(CharSequence text) {
        int i=text.length()-1;

        while( i>=0 && !isWhiteSpaceExceptSpace(text.charAt(i)) )
            i--;

        if( i<0 )
            return text;

        StringBuilder buf = new StringBuilder(text);

        buf.setCharAt(i--,' ');
        for( ; i>=0; i-- )
            if( isWhiteSpaceExceptSpace(buf.charAt(i)))
                buf.setCharAt(i,' ');

        return new String(buf);
    }


    public static CharSequence trim(CharSequence text) {
        int len = text.length();
        int start = 0;

        while( start<len && isWhiteSpace(text.charAt(start)) )
            start++;

        int end = len-1;

        while( end>start && isWhiteSpace(text.charAt(end)) )
            end--;

        if(start==0 && end==len-1)
            return text;    else
            return text.subSequence(start,end+1);
    }

    public static String collapse(String text) {
        return collapse( (CharSequence)text ).toString();
    }


    public static CharSequence collapse(CharSequence text) {
        int len = text.length();

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
        return result;
    }


    public static final boolean isWhiteSpace(CharSequence s) {
        for( int i=s.length()-1; i>=0; i-- )
            if(!isWhiteSpace(s.charAt(i)))
                return false;
        return true;
    }


    public static final boolean isWhiteSpace(char ch) {
        if( ch>0x20 )   return false;

        return ch == 0x9 || ch == 0xA || ch == 0xD || ch == 0x20;
    }


    protected static final boolean isWhiteSpaceExceptSpace(char ch) {
        if( ch>=0x20 )   return false;

        return ch == 0x9 || ch == 0xA || ch == 0xD;
    }
}
