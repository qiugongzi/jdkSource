

package java.lang;
import java.util.Random;

import sun.misc.FloatConsts;
import sun.misc.DoubleConsts;



public final class Math {


    private Math() {}


    public static final double E = 2.7182818284590452354;


    public static final double PI = 3.14159265358979323846;


    public static double sin(double a) {
        return StrictMath.sin(a); }


    public static double cos(double a) {
        return StrictMath.cos(a); }


    public static double tan(double a) {
        return StrictMath.tan(a); }


    public static double asin(double a) {
        return StrictMath.asin(a); }


    public static double acos(double a) {
        return StrictMath.acos(a); }


    public static double atan(double a) {
        return StrictMath.atan(a); }


    public static double toRadians(double angdeg) {
        return angdeg / 180.0 * PI;
    }


    public static double toDegrees(double angrad) {
        return angrad * 180.0 / PI;
    }


    public static double exp(double a) {
        return StrictMath.exp(a); }


    public static double log(double a) {
        return StrictMath.log(a); }


    public static double log10(double a) {
        return StrictMath.log10(a); }


    public static double sqrt(double a) {
        return StrictMath.sqrt(a); }



    public static double cbrt(double a) {
        return StrictMath.cbrt(a);
    }


    public static double IEEEremainder(double f1, double f2) {
        return StrictMath.IEEEremainder(f1, f2); }


    public static double ceil(double a) {
        return StrictMath.ceil(a); }


    public static double floor(double a) {
        return StrictMath.floor(a); }


    public static double rint(double a) {
        return StrictMath.rint(a); }


    public static double atan2(double y, double x) {
        return StrictMath.atan2(y, x); }


    public static double pow(double a, double b) {
        return StrictMath.pow(a, b); }


    public static int round(float a) {
        int intBits = Float.floatToRawIntBits(a);
        int biasedExp = (intBits & FloatConsts.EXP_BIT_MASK)
                >> (FloatConsts.SIGNIFICAND_WIDTH - 1);
        int shift = (FloatConsts.SIGNIFICAND_WIDTH - 2
                + FloatConsts.EXP_BIAS) - biasedExp;
        if ((shift & -32) == 0) { int r = ((intBits & FloatConsts.SIGNIF_BIT_MASK)
                    | (FloatConsts.SIGNIF_BIT_MASK + 1));
            if (intBits < 0) {
                r = -r;
            }
            return ((r >> shift) + 1) >> 1;
        } else {
            return (int) a;
        }
    }


    public static long round(double a) {
        long longBits = Double.doubleToRawLongBits(a);
        long biasedExp = (longBits & DoubleConsts.EXP_BIT_MASK)
                >> (DoubleConsts.SIGNIFICAND_WIDTH - 1);
        long shift = (DoubleConsts.SIGNIFICAND_WIDTH - 2
                + DoubleConsts.EXP_BIAS) - biasedExp;
        if ((shift & -64) == 0) { long r = ((longBits & DoubleConsts.SIGNIF_BIT_MASK)
                    | (DoubleConsts.SIGNIF_BIT_MASK + 1));
            if (longBits < 0) {
                r = -r;
            }
            return ((r >> shift) + 1) >> 1;
        } else {
            return (long) a;
        }
    }

    private static final class RandomNumberGeneratorHolder {
        static final Random randomNumberGenerator = new Random();
    }


    public static double random() {
        return RandomNumberGeneratorHolder.randomNumberGenerator.nextDouble();
    }


    public static int addExact(int x, int y) {
        int r = x + y;
        if (((x ^ r) & (y ^ r)) < 0) {
            throw new ArithmeticException("integer overflow");
        }
        return r;
    }


    public static long addExact(long x, long y) {
        long r = x + y;
        if (((x ^ r) & (y ^ r)) < 0) {
            throw new ArithmeticException("long overflow");
        }
        return r;
    }


    public static int subtractExact(int x, int y) {
        int r = x - y;
        if (((x ^ y) & (x ^ r)) < 0) {
            throw new ArithmeticException("integer overflow");
        }
        return r;
    }


    public static long subtractExact(long x, long y) {
        long r = x - y;
        if (((x ^ y) & (x ^ r)) < 0) {
            throw new ArithmeticException("long overflow");
        }
        return r;
    }


    public static int multiplyExact(int x, int y) {
        long r = (long)x * (long)y;
        if ((int)r != r) {
            throw new ArithmeticException("integer overflow");
        }
        return (int)r;
    }


    public static long multiplyExact(long x, long y) {
        long r = x * y;
        long ax = Math.abs(x);
        long ay = Math.abs(y);
        if (((ax | ay) >>> 31 != 0)) {
            if (((y != 0) && (r / y != x)) ||
               (x == Long.MIN_VALUE && y == -1)) {
                throw new ArithmeticException("long overflow");
            }
        }
        return r;
    }


    public static int incrementExact(int a) {
        if (a == Integer.MAX_VALUE) {
            throw new ArithmeticException("integer overflow");
        }

        return a + 1;
    }


    public static long incrementExact(long a) {
        if (a == Long.MAX_VALUE) {
            throw new ArithmeticException("long overflow");
        }

        return a + 1L;
    }


    public static int decrementExact(int a) {
        if (a == Integer.MIN_VALUE) {
            throw new ArithmeticException("integer overflow");
        }

        return a - 1;
    }


    public static long decrementExact(long a) {
        if (a == Long.MIN_VALUE) {
            throw new ArithmeticException("long overflow");
        }

        return a - 1L;
    }


    public static int negateExact(int a) {
        if (a == Integer.MIN_VALUE) {
            throw new ArithmeticException("integer overflow");
        }

        return -a;
    }


    public static long negateExact(long a) {
        if (a == Long.MIN_VALUE) {
            throw new ArithmeticException("long overflow");
        }

        return -a;
    }


    public static int toIntExact(long value) {
        if ((int)value != value) {
            throw new ArithmeticException("integer overflow");
        }
        return (int)value;
    }


    public static int floorDiv(int x, int y) {
        int r = x / y;
        if ((x ^ y) < 0 && (r * y != x)) {
            r--;
        }
        return r;
    }


    public static long floorDiv(long x, long y) {
        long r = x / y;
        if ((x ^ y) < 0 && (r * y != x)) {
            r--;
        }
        return r;
    }


    public static int floorMod(int x, int y) {
        int r = x - floorDiv(x, y) * y;
        return r;
    }


    public static long floorMod(long x, long y) {
        return x - floorDiv(x, y) * y;
    }


    public static int abs(int a) {
        return (a < 0) ? -a : a;
    }


    public static long abs(long a) {
        return (a < 0) ? -a : a;
    }


    public static float abs(float a) {
        return (a <= 0.0F) ? 0.0F - a : a;
    }


    public static double abs(double a) {
        return (a <= 0.0D) ? 0.0D - a : a;
    }


    public static int max(int a, int b) {
        return (a >= b) ? a : b;
    }


    public static long max(long a, long b) {
        return (a >= b) ? a : b;
    }

    private static long negativeZeroFloatBits  = Float.floatToRawIntBits(-0.0f);
    private static long negativeZeroDoubleBits = Double.doubleToRawLongBits(-0.0d);


    public static float max(float a, float b) {
        if (a != a)
            return a;   if ((a == 0.0f) &&
            (b == 0.0f) &&
            (Float.floatToRawIntBits(a) == negativeZeroFloatBits)) {
            return b;
        }
        return (a >= b) ? a : b;
    }


    public static double max(double a, double b) {
        if (a != a)
            return a;   if ((a == 0.0d) &&
            (b == 0.0d) &&
            (Double.doubleToRawLongBits(a) == negativeZeroDoubleBits)) {
            return b;
        }
        return (a >= b) ? a : b;
    }


    public static int min(int a, int b) {
        return (a <= b) ? a : b;
    }


    public static long min(long a, long b) {
        return (a <= b) ? a : b;
    }


    public static float min(float a, float b) {
        if (a != a)
            return a;   if ((a == 0.0f) &&
            (b == 0.0f) &&
            (Float.floatToRawIntBits(b) == negativeZeroFloatBits)) {
            return b;
        }
        return (a <= b) ? a : b;
    }


    public static double min(double a, double b) {
        if (a != a)
            return a;   if ((a == 0.0d) &&
            (b == 0.0d) &&
            (Double.doubleToRawLongBits(b) == negativeZeroDoubleBits)) {
            return b;
        }
        return (a <= b) ? a : b;
    }


    public static double ulp(double d) {
        int exp = getExponent(d);

        switch(exp) {
        case DoubleConsts.MAX_EXPONENT+1:       return Math.abs(d);

        case DoubleConsts.MIN_EXPONENT-1:       return Double.MIN_VALUE;

        default:
            assert exp <= DoubleConsts.MAX_EXPONENT && exp >= DoubleConsts.MIN_EXPONENT;

            exp = exp - (DoubleConsts.SIGNIFICAND_WIDTH-1);
            if (exp >= DoubleConsts.MIN_EXPONENT) {
                return powerOfTwoD(exp);
            }
            else {
                return Double.longBitsToDouble(1L <<
                (exp - (DoubleConsts.MIN_EXPONENT - (DoubleConsts.SIGNIFICAND_WIDTH-1)) ));
            }
        }
    }


    public static float ulp(float f) {
        int exp = getExponent(f);

        switch(exp) {
        case FloatConsts.MAX_EXPONENT+1:        return Math.abs(f);

        case FloatConsts.MIN_EXPONENT-1:        return FloatConsts.MIN_VALUE;

        default:
            assert exp <= FloatConsts.MAX_EXPONENT && exp >= FloatConsts.MIN_EXPONENT;

            exp = exp - (FloatConsts.SIGNIFICAND_WIDTH-1);
            if (exp >= FloatConsts.MIN_EXPONENT) {
                return powerOfTwoF(exp);
            }
            else {
                return Float.intBitsToFloat(1 <<
                (exp - (FloatConsts.MIN_EXPONENT - (FloatConsts.SIGNIFICAND_WIDTH-1)) ));
            }
        }
    }


    public static double signum(double d) {
        return (d == 0.0 || Double.isNaN(d))?d:copySign(1.0, d);
    }


    public static float signum(float f) {
        return (f == 0.0f || Float.isNaN(f))?f:copySign(1.0f, f);
    }


    public static double sinh(double x) {
        return StrictMath.sinh(x);
    }


    public static double cosh(double x) {
        return StrictMath.cosh(x);
    }


    public static double tanh(double x) {
        return StrictMath.tanh(x);
    }


    public static double hypot(double x, double y) {
        return StrictMath.hypot(x, y);
    }


    public static double expm1(double x) {
        return StrictMath.expm1(x);
    }


    public static double log1p(double x) {
        return StrictMath.log1p(x);
    }


    public static double copySign(double magnitude, double sign) {
        return Double.longBitsToDouble((Double.doubleToRawLongBits(sign) &
                                        (DoubleConsts.SIGN_BIT_MASK)) |
                                       (Double.doubleToRawLongBits(magnitude) &
                                        (DoubleConsts.EXP_BIT_MASK |
                                         DoubleConsts.SIGNIF_BIT_MASK)));
    }


    public static float copySign(float magnitude, float sign) {
        return Float.intBitsToFloat((Float.floatToRawIntBits(sign) &
                                     (FloatConsts.SIGN_BIT_MASK)) |
                                    (Float.floatToRawIntBits(magnitude) &
                                     (FloatConsts.EXP_BIT_MASK |
                                      FloatConsts.SIGNIF_BIT_MASK)));
    }


    public static int getExponent(float f) {

        return ((Float.floatToRawIntBits(f) & FloatConsts.EXP_BIT_MASK) >>
                (FloatConsts.SIGNIFICAND_WIDTH - 1)) - FloatConsts.EXP_BIAS;
    }


    public static int getExponent(double d) {

        return (int)(((Double.doubleToRawLongBits(d) & DoubleConsts.EXP_BIT_MASK) >>
                      (DoubleConsts.SIGNIFICAND_WIDTH - 1)) - DoubleConsts.EXP_BIAS);
    }


    public static double nextAfter(double start, double direction) {


        if (Double.isNaN(start) || Double.isNaN(direction)) {
            return start + direction;
        } else if (start == direction) {
            return direction;
        } else {        long transducer = Double.doubleToRawLongBits(start + 0.0d);


            if (direction > start) { transducer = transducer + (transducer >= 0L ? 1L:-1L);
            } else  { assert direction < start;
                if (transducer > 0L)
                    --transducer;
                else
                    if (transducer < 0L )
                        ++transducer;

                    else
                        transducer = DoubleConsts.SIGN_BIT_MASK | 1L;
            }

            return Double.longBitsToDouble(transducer);
        }
    }


    public static float nextAfter(float start, double direction) {


        if (Float.isNaN(start) || Double.isNaN(direction)) {
            return start + (float)direction;
        } else if (start == direction) {
            return (float)direction;
        } else {        int transducer = Float.floatToRawIntBits(start + 0.0f);


            if (direction > start) {transducer = transducer + (transducer >= 0 ? 1:-1);
            } else  { assert direction < start;
                if (transducer > 0)
                    --transducer;
                else
                    if (transducer < 0 )
                        ++transducer;

                    else
                        transducer = FloatConsts.SIGN_BIT_MASK | 1;
            }

            return Float.intBitsToFloat(transducer);
        }
    }


    public static double nextUp(double d) {
        if( Double.isNaN(d) || d == Double.POSITIVE_INFINITY)
            return d;
        else {
            d += 0.0d;
            return Double.longBitsToDouble(Double.doubleToRawLongBits(d) +
                                           ((d >= 0.0d)?+1L:-1L));
        }
    }


    public static float nextUp(float f) {
        if( Float.isNaN(f) || f == FloatConsts.POSITIVE_INFINITY)
            return f;
        else {
            f += 0.0f;
            return Float.intBitsToFloat(Float.floatToRawIntBits(f) +
                                        ((f >= 0.0f)?+1:-1));
        }
    }


    public static double nextDown(double d) {
        if (Double.isNaN(d) || d == Double.NEGATIVE_INFINITY)
            return d;
        else {
            if (d == 0.0)
                return -Double.MIN_VALUE;
            else
                return Double.longBitsToDouble(Double.doubleToRawLongBits(d) +
                                               ((d > 0.0d)?-1L:+1L));
        }
    }


    public static float nextDown(float f) {
        if (Float.isNaN(f) || f == Float.NEGATIVE_INFINITY)
            return f;
        else {
            if (f == 0.0f)
                return -Float.MIN_VALUE;
            else
                return Float.intBitsToFloat(Float.floatToRawIntBits(f) +
                                            ((f > 0.0f)?-1:+1));
        }
    }


    public static double scalb(double d, int scaleFactor) {


        final int MAX_SCALE = DoubleConsts.MAX_EXPONENT + -DoubleConsts.MIN_EXPONENT +
                              DoubleConsts.SIGNIFICAND_WIDTH + 1;
        int exp_adjust = 0;
        int scale_increment = 0;
        double exp_delta = Double.NaN;

        if(scaleFactor < 0) {
            scaleFactor = Math.max(scaleFactor, -MAX_SCALE);
            scale_increment = -512;
            exp_delta = twoToTheDoubleScaleDown;
        }
        else {
            scaleFactor = Math.min(scaleFactor, MAX_SCALE);
            scale_increment = 512;
            exp_delta = twoToTheDoubleScaleUp;
        }

        int t = (scaleFactor >> 9-1) >>> 32 - 9;
        exp_adjust = ((scaleFactor + t) & (512 -1)) - t;

        d *= powerOfTwoD(exp_adjust);
        scaleFactor -= exp_adjust;

        while(scaleFactor != 0) {
            d *= exp_delta;
            scaleFactor -= scale_increment;
        }
        return d;
    }


    public static float scalb(float f, int scaleFactor) {
        final int MAX_SCALE = FloatConsts.MAX_EXPONENT + -FloatConsts.MIN_EXPONENT +
                              FloatConsts.SIGNIFICAND_WIDTH + 1;

        scaleFactor = Math.max(Math.min(scaleFactor, MAX_SCALE), -MAX_SCALE);


        return (float)((double)f*powerOfTwoD(scaleFactor));
    }

    static double twoToTheDoubleScaleUp = powerOfTwoD(512);
    static double twoToTheDoubleScaleDown = powerOfTwoD(-512);


    static double powerOfTwoD(int n) {
        assert(n >= DoubleConsts.MIN_EXPONENT && n <= DoubleConsts.MAX_EXPONENT);
        return Double.longBitsToDouble((((long)n + (long)DoubleConsts.EXP_BIAS) <<
                                        (DoubleConsts.SIGNIFICAND_WIDTH-1))
                                       & DoubleConsts.EXP_BIT_MASK);
    }


    static float powerOfTwoF(int n) {
        assert(n >= FloatConsts.MIN_EXPONENT && n <= FloatConsts.MAX_EXPONENT);
        return Float.intBitsToFloat(((n + FloatConsts.EXP_BIAS) <<
                                     (FloatConsts.SIGNIFICAND_WIDTH-1))
                                    & FloatConsts.EXP_BIT_MASK);
    }
}
