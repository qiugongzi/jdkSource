

package java.lang;

import sun.misc.FloatingDecimal;
import sun.misc.FpUtils;
import sun.misc.DoubleConsts;


public final class Double extends Number implements Comparable<Double> {

    public static final double POSITIVE_INFINITY = 1.0 / 0.0;


    public static final double NEGATIVE_INFINITY = -1.0 / 0.0;


    public static final double NaN = 0.0d / 0.0;


    public static final double MAX_VALUE = 0x1.fffffffffffffP+1023; public static final double MIN_NORMAL = 0x1.0p-1022; public static final double MIN_VALUE = 0x0.0000000000001P-1022; public static final int MAX_EXPONENT = 1023;


    public static final int MIN_EXPONENT = -1022;


    public static final int SIZE = 64;


    public static final int BYTES = SIZE / Byte.SIZE;


    @SuppressWarnings("unchecked")
    public static final Class<Double>   TYPE = (Class<Double>) Class.getPrimitiveClass("double");


    public static String toString(double d) {
        return FloatingDecimal.toJavaFormatString(d);
    }


    public static String toHexString(double d) {

        if (!isFinite(d) )
            return Double.toString(d);
        else {
            StringBuilder answer = new StringBuilder(24);

            if (Math.copySign(1.0, d) == -1.0)    answer.append("-");                  answer.append("0x");

            d = Math.abs(d);

            if(d == 0.0) {
                answer.append("0.0p0");
            } else {
                boolean subnormal = (d < DoubleConsts.MIN_NORMAL);

                long signifBits = (Double.doubleToLongBits(d)
                                   & DoubleConsts.SIGNIF_BIT_MASK) |
                    0x1000000000000000L;

                answer.append(subnormal ? "0." : "1.");

                String signif = Long.toHexString(signifBits).substring(3,16);
                answer.append(signif.equals("0000000000000") ? "0":
                              signif.replaceFirst("0{1,12}$", ""));

                answer.append('p');
                answer.append(subnormal ?
                              DoubleConsts.MIN_EXPONENT:
                              Math.getExponent(d));
            }
            return answer.toString();
        }
    }


    public static Double valueOf(String s) throws NumberFormatException {
        return new Double(parseDouble(s));
    }


    public static Double valueOf(double d) {
        return new Double(d);
    }


    public static double parseDouble(String s) throws NumberFormatException {
        return FloatingDecimal.parseDouble(s);
    }


    public static boolean isNaN(double v) {
        return (v != v);
    }


    public static boolean isInfinite(double v) {
        return (v == POSITIVE_INFINITY) || (v == NEGATIVE_INFINITY);
    }


    public static boolean isFinite(double d) {
        return Math.abs(d) <= DoubleConsts.MAX_VALUE;
    }


    private final double value;


    public Double(double value) {
        this.value = value;
    }


    public Double(String s) throws NumberFormatException {
        value = parseDouble(s);
    }


    public boolean isNaN() {
        return isNaN(value);
    }


    public boolean isInfinite() {
        return isInfinite(value);
    }


    public String toString() {
        return toString(value);
    }


    public byte byteValue() {
        return (byte)value;
    }


    public short shortValue() {
        return (short)value;
    }


    public int intValue() {
        return (int)value;
    }


    public long longValue() {
        return (long)value;
    }


    public float floatValue() {
        return (float)value;
    }


    public double doubleValue() {
        return value;
    }


    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }


    public static int hashCode(double value) {
        long bits = doubleToLongBits(value);
        return (int)(bits ^ (bits >>> 32));
    }


    public boolean equals(Object obj) {
        return (obj instanceof Double)
               && (doubleToLongBits(((Double)obj).value) ==
                      doubleToLongBits(value));
    }


    public static long doubleToLongBits(double value) {
        long result = doubleToRawLongBits(value);
        if ( ((result & DoubleConsts.EXP_BIT_MASK) ==
              DoubleConsts.EXP_BIT_MASK) &&
             (result & DoubleConsts.SIGNIF_BIT_MASK) != 0L)
            result = 0x7ff8000000000000L;
        return result;
    }


    public static native long doubleToRawLongBits(double value);


    public static native double longBitsToDouble(long bits);


    public int compareTo(Double anotherDouble) {
        return Double.compare(value, anotherDouble.value);
    }


    public static int compare(double d1, double d2) {
        if (d1 < d2)
            return -1;           if (d1 > d2)
            return 1;            long thisBits    = Double.doubleToLongBits(d1);
        long anotherBits = Double.doubleToLongBits(d2);

        return (thisBits == anotherBits ?  0 : (thisBits < anotherBits ? -1 : 1));                          }


    public static double sum(double a, double b) {
        return a + b;
    }


    public static double max(double a, double b) {
        return Math.max(a, b);
    }


    public static double min(double a, double b) {
        return Math.min(a, b);
    }


    private static final long serialVersionUID = -9172774392245257468L;
}
