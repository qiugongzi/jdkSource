



package java.text;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import sun.misc.FloatingDecimal;


final class DigitList implements Cloneable {

    public static final int MAX_COUNT = 19; public int decimalAt = 0;
    public int count = 0;
    public char[] digits = new char[MAX_COUNT];

    private char[] data;
    private RoundingMode roundingMode = RoundingMode.HALF_EVEN;
    private boolean isNegative = false;


    boolean isZero() {
        for (int i=0; i < count; ++i) {
            if (digits[i] != '0') {
                return false;
            }
        }
        return true;
    }


    void setRoundingMode(RoundingMode r) {
        roundingMode = r;
    }


    public void clear () {
        decimalAt = 0;
        count = 0;
    }


    public void append(char digit) {
        if (count == digits.length) {
            char[] data = new char[count + 100];
            System.arraycopy(digits, 0, data, 0, count);
            digits = data;
        }
        digits[count++] = digit;
    }


    public final double getDouble() {
        if (count == 0) {
            return 0.0;
        }

        StringBuffer temp = getStringBuffer();
        temp.append('.');
        temp.append(digits, 0, count);
        temp.append('E');
        temp.append(decimalAt);
        return Double.parseDouble(temp.toString());
    }


    public final long getLong() {
        if (count == 0) {
            return 0;
        }

        if (isLongMIN_VALUE()) {
            return Long.MIN_VALUE;
        }

        StringBuffer temp = getStringBuffer();
        temp.append(digits, 0, count);
        for (int i = count; i < decimalAt; ++i) {
            temp.append('0');
        }
        return Long.parseLong(temp.toString());
    }

    public final BigDecimal getBigDecimal() {
        if (count == 0) {
            if (decimalAt == 0) {
                return BigDecimal.ZERO;
            } else {
                return new BigDecimal("0E" + decimalAt);
            }
        }

       if (decimalAt == count) {
           return new BigDecimal(digits, 0, count);
       } else {
           return new BigDecimal(digits, 0, count).scaleByPowerOfTen(decimalAt - count);
       }
    }


    boolean fitsIntoLong(boolean isPositive, boolean ignoreNegativeZero) {
        while (count > 0 && digits[count - 1] == '0') {
            --count;
        }

        if (count == 0) {
            return isPositive || ignoreNegativeZero;
        }

        if (decimalAt < count || decimalAt > MAX_COUNT) {
            return false;
        }

        if (decimalAt < MAX_COUNT) return true;

        for (int i=0; i<count; ++i) {
            char dig = digits[i], max = LONG_MIN_REP[i];
            if (dig > max) return false;
            if (dig < max) return true;
        }

        if (count < decimalAt) return true;

        return !isPositive;
    }


    final void set(boolean isNegative, double source, int maximumFractionDigits) {
        set(isNegative, source, maximumFractionDigits, true);
    }


    final void set(boolean isNegative, double source, int maximumDigits, boolean fixedPoint) {

        FloatingDecimal.BinaryToASCIIConverter fdConverter  = FloatingDecimal.getBinaryToASCIIConverter(source);
        boolean hasBeenRoundedUp = fdConverter.digitsRoundedUp();
        boolean valueExactAsDecimal = fdConverter.decimalDigitsExact();
        assert !fdConverter.isExceptional();
        String digitsString = fdConverter.toJavaFormatString();

        set(isNegative, digitsString,
            hasBeenRoundedUp, valueExactAsDecimal,
            maximumDigits, fixedPoint);
    }


    private void set(boolean isNegative, String s,
                     boolean roundedUp, boolean valueExactAsDecimal,
                     int maximumDigits, boolean fixedPoint) {

        this.isNegative = isNegative;
        int len = s.length();
        char[] source = getDataChars(len);
        s.getChars(0, len, source, 0);

        decimalAt = -1;
        count = 0;
        int exponent = 0;
        int leadingZerosAfterDecimal = 0;
        boolean nonZeroDigitSeen = false;

        for (int i = 0; i < len; ) {
            char c = source[i++];
            if (c == '.') {
                decimalAt = count;
            } else if (c == 'e' || c == 'E') {
                exponent = parseInt(source, i, len);
                break;
            } else {
                if (!nonZeroDigitSeen) {
                    nonZeroDigitSeen = (c != '0');
                    if (!nonZeroDigitSeen && decimalAt != -1)
                        ++leadingZerosAfterDecimal;
                }
                if (nonZeroDigitSeen) {
                    digits[count++] = c;
                }
            }
        }
        if (decimalAt == -1) {
            decimalAt = count;
        }
        if (nonZeroDigitSeen) {
            decimalAt += exponent - leadingZerosAfterDecimal;
        }

        if (fixedPoint) {
            if (-decimalAt > maximumDigits) {
                count = 0;
                return;
            } else if (-decimalAt == maximumDigits) {
                if (shouldRoundUp(0, roundedUp, valueExactAsDecimal)) {
                    count = 1;
                    ++decimalAt;
                    digits[0] = '1';
                } else {
                    count = 0;
                }
                return;
            }
            }

        while (count > 1 && digits[count - 1] == '0') {
            --count;
        }

        round(fixedPoint ? (maximumDigits + decimalAt) : maximumDigits,
              roundedUp, valueExactAsDecimal);

     }


    private final void round(int maximumDigits,
                             boolean alreadyRounded,
                             boolean valueExactAsDecimal) {
        if (maximumDigits >= 0 && maximumDigits < count) {
            if (shouldRoundUp(maximumDigits, alreadyRounded, valueExactAsDecimal)) {
                for (;;) {
                    --maximumDigits;
                    if (maximumDigits < 0) {
                        digits[0] = '1';
                        ++decimalAt;
                        maximumDigits = 0; break;
                    }

                    ++digits[maximumDigits];
                    if (digits[maximumDigits] <= '9') break;
                    }
                ++maximumDigits; }
            count = maximumDigits;

            while (count > 1 && digits[count-1] == '0') {
                --count;
            }
        }
    }



    private boolean shouldRoundUp(int maximumDigits,
                                  boolean alreadyRounded,
                                  boolean valueExactAsDecimal) {
        if (maximumDigits < count) {


            switch(roundingMode) {
            case UP:
                for (int i=maximumDigits; i<count; ++i) {
                    if (digits[i] != '0') {
                        return true;
                    }
                }
                break;
            case DOWN:
                break;
            case CEILING:
                for (int i=maximumDigits; i<count; ++i) {
                    if (digits[i] != '0') {
                        return !isNegative;
                    }
                }
                break;
            case FLOOR:
                for (int i=maximumDigits; i<count; ++i) {
                    if (digits[i] != '0') {
                        return isNegative;
                    }
                }
                break;
            case HALF_UP:
            case HALF_DOWN:
                if (digits[maximumDigits] > '5') {
                    return true;
                } else if (digits[maximumDigits] == '5') {
                    if (maximumDigits != (count - 1)) {
                        return true;
                    } else {
                        if (valueExactAsDecimal) {
                            return roundingMode == RoundingMode.HALF_UP;
                        } else {
                            return !alreadyRounded;
                        }
                    }
                }
                break;
            case HALF_EVEN:
                if (digits[maximumDigits] > '5') {
                    return true;
                } else if (digits[maximumDigits] == '5' ) {
                    if (maximumDigits == (count - 1)) {
                        if (alreadyRounded)
                            return false;

                        if (!valueExactAsDecimal)
                            return true;
                        else {
                            return ((maximumDigits > 0) &&
                                    (digits[maximumDigits-1] % 2 != 0));
                        }
                    } else {
                        for (int i=maximumDigits+1; i<count; ++i) {
                            if (digits[i] != '0')
                                return true;
                        }
                    }
                }
                break;
            case UNNECESSARY:
                for (int i=maximumDigits; i<count; ++i) {
                    if (digits[i] != '0') {
                        throw new ArithmeticException(
                            "Rounding needed with the rounding mode being set to RoundingMode.UNNECESSARY");
                    }
                }
                break;
            default:
                assert false;
            }
        }
        return false;
    }


    final void set(boolean isNegative, long source) {
        set(isNegative, source, 0);
    }


    final void set(boolean isNegative, long source, int maximumDigits) {
        this.isNegative = isNegative;

        if (source <= 0) {
            if (source == Long.MIN_VALUE) {
                decimalAt = count = MAX_COUNT;
                System.arraycopy(LONG_MIN_REP, 0, digits, 0, count);
            } else {
                decimalAt = count = 0; }
        } else {
            int left = MAX_COUNT;
            int right;
            while (source > 0) {
                digits[--left] = (char)('0' + (source % 10));
                source /= 10;
            }
            decimalAt = MAX_COUNT - left;
            for (right = MAX_COUNT - 1; digits[right] == '0'; --right)
                ;
            count = right - left + 1;
            System.arraycopy(digits, left, digits, 0, count);
        }
        if (maximumDigits > 0) round(maximumDigits, false, true);
    }


    final void set(boolean isNegative, BigDecimal source, int maximumDigits, boolean fixedPoint) {
        String s = source.toString();
        extendDigits(s.length());

        set(isNegative, s,
            false, true,
            maximumDigits, fixedPoint);
    }


    final void set(boolean isNegative, BigInteger source, int maximumDigits) {
        this.isNegative = isNegative;
        String s = source.toString();
        int len = s.length();
        extendDigits(len);
        s.getChars(0, len, digits, 0);

        decimalAt = len;
        int right;
        for (right = len - 1; right >= 0 && digits[right] == '0'; --right)
            ;
        count = right + 1;

        if (maximumDigits > 0) {
            round(maximumDigits, false, true);
        }
    }


    public boolean equals(Object obj) {
        if (this == obj)                      return true;
        if (!(obj instanceof DigitList))         return false;
        DigitList other = (DigitList) obj;
        if (count != other.count ||
        decimalAt != other.decimalAt)
            return false;
        for (int i = 0; i < count; i++)
            if (digits[i] != other.digits[i])
                return false;
        return true;
    }


    public int hashCode() {
        int hashcode = decimalAt;

        for (int i = 0; i < count; i++) {
            hashcode = hashcode * 37 + digits[i];
        }

        return hashcode;
    }


    public Object clone() {
        try {
            DigitList other = (DigitList) super.clone();
            char[] newDigits = new char[digits.length];
            System.arraycopy(digits, 0, newDigits, 0, digits.length);
            other.digits = newDigits;
            other.tempBuffer = null;
            return other;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }


    private boolean isLongMIN_VALUE() {
        if (decimalAt != count || count != MAX_COUNT) {
            return false;
        }

        for (int i = 0; i < count; ++i) {
            if (digits[i] != LONG_MIN_REP[i]) return false;
        }

        return true;
    }

    private static final int parseInt(char[] str, int offset, int strLen) {
        char c;
        boolean positive = true;
        if ((c = str[offset]) == '-') {
            positive = false;
            offset++;
        } else if (c == '+') {
            offset++;
        }

        int value = 0;
        while (offset < strLen) {
            c = str[offset++];
            if (c >= '0' && c <= '9') {
                value = value * 10 + (c - '0');
            } else {
                break;
            }
        }
        return positive ? value : -value;
    }

    private static final char[] LONG_MIN_REP = "9223372036854775808".toCharArray();

    public String toString() {
        if (isZero()) {
            return "0";
        }
        StringBuffer buf = getStringBuffer();
        buf.append("0.");
        buf.append(digits, 0, count);
        buf.append("x10^");
        buf.append(decimalAt);
        return buf.toString();
    }

    private StringBuffer tempBuffer;

    private StringBuffer getStringBuffer() {
        if (tempBuffer == null) {
            tempBuffer = new StringBuffer(MAX_COUNT);
        } else {
            tempBuffer.setLength(0);
        }
        return tempBuffer;
    }

    private void extendDigits(int len) {
        if (len > digits.length) {
            digits = new char[len];
        }
    }

    private final char[] getDataChars(int length) {
        if (data == null || data.length < length) {
            data = new char[length];
        }
        return data;
    }
}
