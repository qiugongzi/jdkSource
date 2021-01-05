


package java.lang;



class CharacterDataLatin1 extends CharacterData {



    int getProperties(int ch) {
        char offset = (char)ch;
        int props = A[offset];
        return props;
    }

    int getPropertiesEx(int ch) {
        char offset = (char)ch;
        int props = B[offset];
        return props;
    }

    boolean isOtherLowercase(int ch) {
        int props = getPropertiesEx(ch);
        return (props & 0x0001) != 0;
    }

    boolean isOtherUppercase(int ch) {
        int props = getPropertiesEx(ch);
        return (props & 0x0002) != 0;
    }

    boolean isOtherAlphabetic(int ch) {
        int props = getPropertiesEx(ch);
        return (props & 0x0004) != 0;
    }

    boolean isIdeographic(int ch) {
        int props = getPropertiesEx(ch);
        return (props & 0x0010) != 0;
    }

    int getType(int ch) {
        int props = getProperties(ch);
        return (props & 0x1F);
    }

    boolean isJavaIdentifierStart(int ch) {
        int props = getProperties(ch);
        return ((props & 0x00007000) >= 0x00005000);
    }

    boolean isJavaIdentifierPart(int ch) {
        int props = getProperties(ch);
        return ((props & 0x00003000) != 0);
    }

    boolean isUnicodeIdentifierStart(int ch) {
        int props = getProperties(ch);
        return ((props & 0x00007000) == 0x00007000);
    }

    boolean isUnicodeIdentifierPart(int ch) {
        int props = getProperties(ch);
        return ((props & 0x00001000) != 0);
    }

    boolean isIdentifierIgnorable(int ch) {
        int props = getProperties(ch);
        return ((props & 0x00007000) == 0x00001000);
    }

    int toLowerCase(int ch) {
        int mapChar = ch;
        int val = getProperties(ch);

        if (((val & 0x00020000) != 0) && 
                ((val & 0x07FC0000) != 0x07FC0000)) { 
            int offset = val << 5 >> (5+18);
            mapChar = ch + offset;
        }
        return mapChar;
    }

    int toUpperCase(int ch) {
        int mapChar = ch;
        int val = getProperties(ch);

        if ((val & 0x00010000) != 0) {
            if ((val & 0x07FC0000) != 0x07FC0000) {
                int offset = val  << 5 >> (5+18);
                mapChar =  ch - offset;
            } else if (ch == 0x00B5) {
                mapChar = 0x039C;
            }
        }
        return mapChar;
    }

    int toTitleCase(int ch) {
        return toUpperCase(ch);
    }

    int digit(int ch, int radix) {
        int value = -1;
        if (radix >= Character.MIN_RADIX && radix <= Character.MAX_RADIX) {
            int val = getProperties(ch);
            int kind = val & 0x1F;
            if (kind == Character.DECIMAL_DIGIT_NUMBER) {
                value = ch + ((val & 0x3E0) >> 5) & 0x1F;
            }
            else if ((val & 0xC00) == 0x00000C00) {
                value = (ch + ((val & 0x3E0) >> 5) & 0x1F) + 10;
            }
        }
        return (value < radix) ? value : -1;
    }

    int getNumericValue(int ch) {
        int val = getProperties(ch);
        int retval = -1;

        switch (val & 0xC00) {
            default: case (0x00000000):         retval = -1;
                break;
            case (0x00000400):              retval = ch + ((val & 0x3E0) >> 5) & 0x1F;
                break;
            case (0x00000800)      :       retval = -2;
                 break;
            case (0x00000C00):           retval = (ch + ((val & 0x3E0) >> 5) & 0x1F) + 10;
                break;
        }
        return retval;
    }

    boolean isWhitespace(int ch) {
        int props = getProperties(ch);
        return ((props & 0x00007000) == 0x00004000);
    }

    byte getDirectionality(int ch) {
        int val = getProperties(ch);
        byte directionality = (byte)((val & 0x78000000) >> 27);

        if (directionality == 0xF ) {
            directionality = -1;
        }
        return directionality;
    }

    boolean isMirrored(int ch) {
        int props = getProperties(ch);
        return ((props & 0x80000000) != 0);
    }

    int toUpperCaseEx(int ch) {
        int mapChar = ch;
        int val = getProperties(ch);

        if ((val & 0x00010000) != 0) {
            if ((val & 0x07FC0000) != 0x07FC0000) {
                int offset = val  << 5 >> (5+18);
                mapChar =  ch - offset;
            }
            else {
                switch(ch) {
                    case 0x00B5 : mapChar = 0x039C; break;
                    default       : mapChar = Character.ERROR; break;
                }
            }
        }
        return mapChar;
    }

    static char[] sharpsMap = new char[] {'S', 'S'};

    char[] toUpperCaseCharArray(int ch) {
        char[] upperMap = {(char)ch};
        if (ch == 0x00DF) {
            upperMap = sharpsMap;
        }
        return upperMap;
    }

    static final CharacterDataLatin1 instance = new CharacterDataLatin1();
    private CharacterDataLatin1() {};

    static final int A[] = new int[256];
  static final String A_DATA =
    "\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800"+
    "\u100F\u4800\u100F\u4800\u100F\u5800\u400F\u5000\u400F\u5800\u400F\u6000\u400F"+
    "\u5000\u400F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800"+
    "\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F"+
    "\u4800\u100F\u4800\u100F\u5000\u400F\u5000\u400F\u5000\u400F\u5800\u400F\u6000"+
    "\u400C\u6800\030\u6800\030\u2800\030\u2800\u601A\u2800\030\u6800\030\u6800"+
    "\030\uE800\025\uE800\026\u6800\030\u2000\031\u3800\030\u2000\024\u3800\030"+
    "\u3800\030\u1800\u3609\u1800\u3609\u1800\u3609\u1800\u3609\u1800\u3609\u1800"+
    "\u3609\u1800\u3609\u1800\u3609\u1800\u3609\u1800\u3609\u3800\030\u6800\030"+
    "\uE800\031\u6800\031\uE800\031\u6800\030\u6800\030\202\u7FE1\202\u7FE1\202"+
    "\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1"+
    "\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202"+
    "\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1\202\u7FE1"+
    "\202\u7FE1\uE800\025\u6800\030\uE800\026\u6800\033\u6800\u5017\u6800\033\201"+
    "\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2"+
    "\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201"+
    "\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2\201\u7FE2"+
    "\201\u7FE2\201\u7FE2\201\u7FE2\uE800\025\u6800\031\uE800\026\u6800\031\u4800"+
    "\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u5000\u100F"+
    "\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800"+
    "\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F"+
    "\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800"+
    "\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F\u4800\u100F"+
    "\u3800\014\u6800\030\u2800\u601A\u2800\u601A\u2800\u601A\u2800\u601A\u6800"+
    "\034\u6800\030\u6800\033\u6800\034\000\u7005\uE800\035\u6800\031\u4800\u1010"+
    "\u6800\034\u6800\033\u2800\034\u2800\031\u1800\u060B\u1800\u060B\u6800\033"+
    "\u07FD\u7002\u6800\030\u6800\030\u6800\033\u1800\u050B\000\u7005\uE800\036"+
    "\u6800\u080B\u6800\u080B\u6800\u080B\u6800\030\202\u7001\202\u7001\202\u7001"+
    "\202\u7001\202\u7001\202\u7001\202\u7001\202\u7001\202\u7001\202\u7001\202"+
    "\u7001\202\u7001\202\u7001\202\u7001\202\u7001\202\u7001\202\u7001\202\u7001"+
    "\202\u7001\202\u7001\202\u7001\202\u7001\202\u7001\u6800\031\202\u7001\202"+
    "\u7001\202\u7001\202\u7001\202\u7001\202\u7001\202\u7001\u07FD\u7002\201\u7002"+
    "\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002\201"+
    "\u7002\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002"+
    "\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002\u6800"+
    "\031\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002\201\u7002"+
    "\u061D\u7002";

  static final char B[] = (
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\001"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\001\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"+
    "\000\000\000\000\000\000\000\000\000").toCharArray();

  static {
                { char[] data = A_DATA.toCharArray();
            assert (data.length == (256 * 2));
            int i = 0, j = 0;
            while (i < (256 * 2)) {
                int entry = data[i++] << 16;
                A[j++] = entry | data[i++];
            }
        }

    }        
}

