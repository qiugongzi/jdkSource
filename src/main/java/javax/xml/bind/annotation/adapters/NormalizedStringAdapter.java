
    protected static boolean isWhiteSpaceExceptSpace(char ch) {


        if( ch>=0x20 )   return false;


        return ch == 0x9 || ch == 0xA || ch == 0xD;
    }
}
