



package java.text;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.spi.NumberFormatProvider;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.ResourceBundleBasedAdapter;


public class DecimalFormat extends NumberFormat {


    public DecimalFormat() {
        Locale def = Locale.getDefault(Locale.Category.FORMAT);
        LocaleProviderAdapter adapter = LocaleProviderAdapter.getAdapter(NumberFormatProvider.class, def);
        if (!(adapter instanceof ResourceBundleBasedAdapter)) {
            adapter = LocaleProviderAdapter.getResourceBundleBased();
        }
        String[] all = adapter.getLocaleResources(def).getNumberPatterns();

        this.symbols = DecimalFormatSymbols.getInstance(def);
        applyPattern(all[0], false);
    }



    public DecimalFormat(String pattern) {
        this.symbols = DecimalFormatSymbols.getInstance(Locale.getDefault(Locale.Category.FORMAT));
        applyPattern(pattern, false);
    }



    public DecimalFormat (String pattern, DecimalFormatSymbols symbols) {
        this.symbols = (DecimalFormatSymbols)symbols.clone();
        applyPattern(pattern, false);
    }


    @Override
    public final StringBuffer format(Object number,
                                     StringBuffer toAppendTo,
                                     FieldPosition pos) {
        if (number instanceof Long || number instanceof Integer ||
                   number instanceof Short || number instanceof Byte ||
                   number instanceof AtomicInteger ||
                   number instanceof AtomicLong ||
                   (number instanceof BigInteger &&
                    ((BigInteger)number).bitLength () < 64)) {
            return format(((Number)number).longValue(), toAppendTo, pos);
        } else if (number instanceof BigDecimal) {
            return format((BigDecimal)number, toAppendTo, pos);
        } else if (number instanceof BigInteger) {
            return format((BigInteger)number, toAppendTo, pos);
        } else if (number instanceof Number) {
            return format(((Number)number).doubleValue(), toAppendTo, pos);
        } else {
            throw new IllegalArgumentException("Cannot format given Object as a Number");
        }
    }


    @Override
    public StringBuffer format(double number, StringBuffer result,
                               FieldPosition fieldPosition) {
        boolean tryFastPath = false;
        if (fieldPosition == DontCareFieldPosition.INSTANCE)
            tryFastPath = true;
        else {
            fieldPosition.setBeginIndex(0);
            fieldPosition.setEndIndex(0);
        }

        if (tryFastPath) {
            String tempResult = fastFormat(number);
            if (tempResult != null) {
                result.append(tempResult);
                return result;
            }
        }

        return format(number, result, fieldPosition.getFieldDelegate());
    }


    private StringBuffer format(double number, StringBuffer result,
                                FieldDelegate delegate) {
        if (Double.isNaN(number) ||
           (Double.isInfinite(number) && multiplier == 0)) {
            int iFieldStart = result.length();
            result.append(symbols.getNaN());
            delegate.formatted(INTEGER_FIELD, Field.INTEGER, Field.INTEGER,
                               iFieldStart, result.length(), result);
            return result;
        }


        boolean isNegative = ((number < 0.0) || (number == 0.0 && 1/number < 0.0)) ^ (multiplier < 0);

        if (multiplier != 1) {
            number *= multiplier;
        }

        if (Double.isInfinite(number)) {
            if (isNegative) {
                append(result, negativePrefix, delegate,
                       getNegativePrefixFieldPositions(), Field.SIGN);
            } else {
                append(result, positivePrefix, delegate,
                       getPositivePrefixFieldPositions(), Field.SIGN);
            }

            int iFieldStart = result.length();
            result.append(symbols.getInfinity());
            delegate.formatted(INTEGER_FIELD, Field.INTEGER, Field.INTEGER,
                               iFieldStart, result.length(), result);

            if (isNegative) {
                append(result, negativeSuffix, delegate,
                       getNegativeSuffixFieldPositions(), Field.SIGN);
            } else {
                append(result, positiveSuffix, delegate,
                       getPositiveSuffixFieldPositions(), Field.SIGN);
            }

            return result;
        }

        if (isNegative) {
            number = -number;
        }

        assert(number >= 0 && !Double.isInfinite(number));

        synchronized(digitList) {
            int maxIntDigits = super.getMaximumIntegerDigits();
            int minIntDigits = super.getMinimumIntegerDigits();
            int maxFraDigits = super.getMaximumFractionDigits();
            int minFraDigits = super.getMinimumFractionDigits();

            digitList.set(isNegative, number, useExponentialNotation ?
                          maxIntDigits + maxFraDigits : maxFraDigits,
                          !useExponentialNotation);
            return subformat(result, delegate, isNegative, false,
                       maxIntDigits, minIntDigits, maxFraDigits, minFraDigits);
        }
    }


    @Override
    public StringBuffer format(long number, StringBuffer result,
                               FieldPosition fieldPosition) {
        fieldPosition.setBeginIndex(0);
        fieldPosition.setEndIndex(0);

        return format(number, result, fieldPosition.getFieldDelegate());
    }


    private StringBuffer format(long number, StringBuffer result,
                               FieldDelegate delegate) {
        boolean isNegative = (number < 0);
        if (isNegative) {
            number = -number;
        }

        boolean useBigInteger = false;
        if (number < 0) { if (multiplier != 0) {
                useBigInteger = true;
            }
        } else if (multiplier != 1 && multiplier != 0) {
            long cutoff = Long.MAX_VALUE / multiplier;
            if (cutoff < 0) {
                cutoff = -cutoff;
            }
            useBigInteger = (number > cutoff);
        }

        if (useBigInteger) {
            if (isNegative) {
                number = -number;
            }
            BigInteger bigIntegerValue = BigInteger.valueOf(number);
            return format(bigIntegerValue, result, delegate, true);
        }

        number *= multiplier;
        if (number == 0) {
            isNegative = false;
        } else {
            if (multiplier < 0) {
                number = -number;
                isNegative = !isNegative;
            }
        }

        synchronized(digitList) {
            int maxIntDigits = super.getMaximumIntegerDigits();
            int minIntDigits = super.getMinimumIntegerDigits();
            int maxFraDigits = super.getMaximumFractionDigits();
            int minFraDigits = super.getMinimumFractionDigits();

            digitList.set(isNegative, number,
                     useExponentialNotation ? maxIntDigits + maxFraDigits : 0);

            return subformat(result, delegate, isNegative, true,
                       maxIntDigits, minIntDigits, maxFraDigits, minFraDigits);
        }
    }


    private StringBuffer format(BigDecimal number, StringBuffer result,
                                FieldPosition fieldPosition) {
        fieldPosition.setBeginIndex(0);
        fieldPosition.setEndIndex(0);
        return format(number, result, fieldPosition.getFieldDelegate());
    }


    private StringBuffer format(BigDecimal number, StringBuffer result,
                                FieldDelegate delegate) {
        if (multiplier != 1) {
            number = number.multiply(getBigDecimalMultiplier());
        }
        boolean isNegative = number.signum() == -1;
        if (isNegative) {
            number = number.negate();
        }

        synchronized(digitList) {
            int maxIntDigits = getMaximumIntegerDigits();
            int minIntDigits = getMinimumIntegerDigits();
            int maxFraDigits = getMaximumFractionDigits();
            int minFraDigits = getMinimumFractionDigits();
            int maximumDigits = maxIntDigits + maxFraDigits;

            digitList.set(isNegative, number, useExponentialNotation ?
                ((maximumDigits < 0) ? Integer.MAX_VALUE : maximumDigits) :
                maxFraDigits, !useExponentialNotation);

            return subformat(result, delegate, isNegative, false,
                maxIntDigits, minIntDigits, maxFraDigits, minFraDigits);
        }
    }


    private StringBuffer format(BigInteger number, StringBuffer result,
                               FieldPosition fieldPosition) {
        fieldPosition.setBeginIndex(0);
        fieldPosition.setEndIndex(0);

        return format(number, result, fieldPosition.getFieldDelegate(), false);
    }


    private StringBuffer format(BigInteger number, StringBuffer result,
                               FieldDelegate delegate, boolean formatLong) {
        if (multiplier != 1) {
            number = number.multiply(getBigIntegerMultiplier());
        }
        boolean isNegative = number.signum() == -1;
        if (isNegative) {
            number = number.negate();
        }

        synchronized(digitList) {
            int maxIntDigits, minIntDigits, maxFraDigits, minFraDigits, maximumDigits;
            if (formatLong) {
                maxIntDigits = super.getMaximumIntegerDigits();
                minIntDigits = super.getMinimumIntegerDigits();
                maxFraDigits = super.getMaximumFractionDigits();
                minFraDigits = super.getMinimumFractionDigits();
                maximumDigits = maxIntDigits + maxFraDigits;
            } else {
                maxIntDigits = getMaximumIntegerDigits();
                minIntDigits = getMinimumIntegerDigits();
                maxFraDigits = getMaximumFractionDigits();
                minFraDigits = getMinimumFractionDigits();
                maximumDigits = maxIntDigits + maxFraDigits;
                if (maximumDigits < 0) {
                    maximumDigits = Integer.MAX_VALUE;
                }
            }

            digitList.set(isNegative, number,
                          useExponentialNotation ? maximumDigits : 0);

            return subformat(result, delegate, isNegative, true,
                maxIntDigits, minIntDigits, maxFraDigits, minFraDigits);
        }
    }


    @Override
    public AttributedCharacterIterator formatToCharacterIterator(Object obj) {
        CharacterIteratorFieldDelegate delegate =
                         new CharacterIteratorFieldDelegate();
        StringBuffer sb = new StringBuffer();

        if (obj instanceof Double || obj instanceof Float) {
            format(((Number)obj).doubleValue(), sb, delegate);
        } else if (obj instanceof Long || obj instanceof Integer ||
                   obj instanceof Short || obj instanceof Byte ||
                   obj instanceof AtomicInteger || obj instanceof AtomicLong) {
            format(((Number)obj).longValue(), sb, delegate);
        } else if (obj instanceof BigDecimal) {
            format((BigDecimal)obj, sb, delegate);
        } else if (obj instanceof BigInteger) {
            format((BigInteger)obj, sb, delegate, false);
        } else if (obj == null) {
            throw new NullPointerException(
                "formatToCharacterIterator must be passed non-null object");
        } else {
            throw new IllegalArgumentException(
                "Cannot format given Object as a Number");
        }
        return delegate.getIterator(sb.toString());
    }

    private boolean checkAndSetFastPathStatus() {

        boolean fastPathWasOn = isFastPath;

        if ((roundingMode == RoundingMode.HALF_EVEN) &&
            (isGroupingUsed()) &&
            (groupingSize == 3) &&
            (multiplier == 1) &&
            (!decimalSeparatorAlwaysShown) &&
            (!useExponentialNotation)) {

            isFastPath = ((minimumIntegerDigits == 1) &&
                          (maximumIntegerDigits >= 10));

            if (isFastPath) {
                if (isCurrencyFormat) {
                    if ((minimumFractionDigits != 2) ||
                        (maximumFractionDigits != 2))
                        isFastPath = false;
                } else if ((minimumFractionDigits != 0) ||
                           (maximumFractionDigits != 3))
                    isFastPath = false;
            }
        } else
            isFastPath = false;

        resetFastPathData(fastPathWasOn);
        fastPathCheckNeeded = false;


        return true;
    }

    private void resetFastPathData(boolean fastPathWasOn) {
        if (isFastPath) {
            if (fastPathData == null) {
                fastPathData = new FastPathData();
            }

            fastPathData.zeroDelta = symbols.getZeroDigit() - '0';
            fastPathData.groupingChar = symbols.getGroupingSeparator();

            fastPathData.fractionalMaxIntBound = (isCurrencyFormat)
                    ? 99 : 999;
            fastPathData.fractionalScaleFactor = (isCurrencyFormat)
                    ? 100.0d : 1000.0d;

            fastPathData.positiveAffixesRequired
                    = (positivePrefix.length() != 0)
                        || (positiveSuffix.length() != 0);
            fastPathData.negativeAffixesRequired
                    = (negativePrefix.length() != 0)
                        || (negativeSuffix.length() != 0);

            int maxNbIntegralDigits = 10;
            int maxNbGroups = 3;
            int containerSize
                    = Math.max(positivePrefix.length(), negativePrefix.length())
                    + maxNbIntegralDigits + maxNbGroups + 1
                    + maximumFractionDigits
                    + Math.max(positiveSuffix.length(), negativeSuffix.length());

            fastPathData.fastPathContainer = new char[containerSize];

            fastPathData.charsPositiveSuffix = positiveSuffix.toCharArray();
            fastPathData.charsNegativeSuffix = negativeSuffix.toCharArray();
            fastPathData.charsPositivePrefix = positivePrefix.toCharArray();
            fastPathData.charsNegativePrefix = negativePrefix.toCharArray();

            int longestPrefixLength
                    = Math.max(positivePrefix.length(),
                            negativePrefix.length());
            int decimalPointIndex
                    = maxNbIntegralDigits + maxNbGroups + longestPrefixLength;

            fastPathData.integralLastIndex = decimalPointIndex - 1;
            fastPathData.fractionalFirstIndex = decimalPointIndex + 1;
            fastPathData.fastPathContainer[decimalPointIndex]
                    = isCurrencyFormat
                            ? symbols.getMonetaryDecimalSeparator()
                            : symbols.getDecimalSeparator();

        } else if (fastPathWasOn) {
            fastPathData.fastPathContainer = null;
            fastPathData.charsPositiveSuffix = null;
            fastPathData.charsNegativeSuffix = null;
            fastPathData.charsPositivePrefix = null;
            fastPathData.charsNegativePrefix = null;
        }
    }


    private boolean exactRoundUp(double fractionalPart,
                                 int scaledFractionalPartAsInt) {






        double approxMax;    double approxMedium; double approxMin;

        double fastTwoSumApproximation = 0.0d;
        double fastTwoSumRoundOff = 0.0d;
        double bVirtual = 0.0d;

        if (isCurrencyFormat) {
            approxMax    = fractionalPart * 128.00d;
            approxMedium = - (fractionalPart * 32.00d);
            approxMin    = fractionalPart * 4.00d;
        } else {
            approxMax    = fractionalPart * 1024.00d;
            approxMedium = - (fractionalPart * 16.00d);
            approxMin    = - (fractionalPart * 8.00d);
        }

        assert(-approxMedium >= Math.abs(approxMin));
        fastTwoSumApproximation = approxMedium + approxMin;
        bVirtual = fastTwoSumApproximation - approxMedium;
        fastTwoSumRoundOff = approxMin - bVirtual;
        double approxS1 = fastTwoSumApproximation;
        double roundoffS1 = fastTwoSumRoundOff;

        assert(approxMax >= Math.abs(approxS1));
        fastTwoSumApproximation = approxMax + approxS1;
        bVirtual = fastTwoSumApproximation - approxMax;
        fastTwoSumRoundOff = approxS1 - bVirtual;
        double roundoff1000 = fastTwoSumRoundOff;
        double approx1000 = fastTwoSumApproximation;
        double roundoffTotal = roundoffS1 + roundoff1000;

        assert(approx1000 >= Math.abs(roundoffTotal));
        fastTwoSumApproximation = approx1000 + roundoffTotal;
        bVirtual = fastTwoSumApproximation - approx1000;

        double scaledFractionalRoundoff = roundoffTotal - bVirtual;

        if (scaledFractionalRoundoff > 0.0) {
            return true;
        } else if (scaledFractionalRoundoff < 0.0) {
            return false;
        } else if ((scaledFractionalPartAsInt & 1) != 0) {
            return true;
        }

        return false;

        }


    private void collectIntegralDigits(int number,
                                       char[] digitsBuffer,
                                       int backwardIndex) {
        int index = backwardIndex;
        int q;
        int r;
        while (number > 999) {
            q = number / 1000;
            r = number - (q << 10) + (q << 4) + (q << 3); number = q;

            digitsBuffer[index--] = DigitArrays.DigitOnes1000[r];
            digitsBuffer[index--] = DigitArrays.DigitTens1000[r];
            digitsBuffer[index--] = DigitArrays.DigitHundreds1000[r];
            digitsBuffer[index--] = fastPathData.groupingChar;
        }

        digitsBuffer[index] = DigitArrays.DigitOnes1000[number];
        if (number > 9) {
            digitsBuffer[--index]  = DigitArrays.DigitTens1000[number];
            if (number > 99)
                digitsBuffer[--index]   = DigitArrays.DigitHundreds1000[number];
        }

        fastPathData.firstUsedIndex = index;
    }


    private void collectFractionalDigits(int number,
                                         char[] digitsBuffer,
                                         int startIndex) {
        int index = startIndex;

        char digitOnes = DigitArrays.DigitOnes1000[number];
        char digitTens = DigitArrays.DigitTens1000[number];

        if (isCurrencyFormat) {
            digitsBuffer[index++] = digitTens;
            digitsBuffer[index++] = digitOnes;
        } else if (number != 0) {
            digitsBuffer[index++] = DigitArrays.DigitHundreds1000[number];

            if (digitOnes != '0') {
                digitsBuffer[index++] = digitTens;
                digitsBuffer[index++] = digitOnes;
            } else if (digitTens != '0')
                digitsBuffer[index++] = digitTens;

        } else
            index--;

        fastPathData.lastFreeIndex = index;
    }


    private void addAffixes(char[] container, char[] prefix, char[] suffix) {

        int pl = prefix.length;
        int sl = suffix.length;
        if (pl != 0) prependPrefix(prefix, pl, container);
        if (sl != 0) appendSuffix(suffix, sl, container);

    }


    private void prependPrefix(char[] prefix,
                               int len,
                               char[] container) {

        fastPathData.firstUsedIndex -= len;
        int startIndex = fastPathData.firstUsedIndex;

        if (len == 1)
            container[startIndex] = prefix[0];
        else if (len <= 4) {
            int dstLower = startIndex;
            int dstUpper = dstLower + len - 1;
            int srcUpper = len - 1;
            container[dstLower] = prefix[0];
            container[dstUpper] = prefix[srcUpper];

            if (len > 2)
                container[++dstLower] = prefix[1];
            if (len == 4)
                container[--dstUpper] = prefix[2];
        } else
            System.arraycopy(prefix, 0, container, startIndex, len);
    }


    private void appendSuffix(char[] suffix,
                              int len,
                              char[] container) {

        int startIndex = fastPathData.lastFreeIndex;

        if (len == 1)
            container[startIndex] = suffix[0];
        else if (len <= 4) {
            int dstLower = startIndex;
            int dstUpper = dstLower + len - 1;
            int srcUpper = len - 1;
            container[dstLower] = suffix[0];
            container[dstUpper] = suffix[srcUpper];

            if (len > 2)
                container[++dstLower] = suffix[1];
            if (len == 4)
                container[--dstUpper] = suffix[2];
        } else
            System.arraycopy(suffix, 0, container, startIndex, len);

        fastPathData.lastFreeIndex += len;
    }


    private void localizeDigits(char[] digitsBuffer) {

        int digitsCounter =
            fastPathData.lastFreeIndex - fastPathData.fractionalFirstIndex;

        if (digitsCounter < 0)
            digitsCounter = groupingSize;

        for (int cursor = fastPathData.lastFreeIndex - 1;
             cursor >= fastPathData.firstUsedIndex;
             cursor--) {
            if (digitsCounter != 0) {
                digitsBuffer[cursor] += fastPathData.zeroDelta;
                digitsCounter--;
            } else {
                digitsCounter = groupingSize;
            }
        }
    }


    private void fastDoubleFormat(double d,
                                  boolean negative) {

        char[] container = fastPathData.fastPathContainer;



        int integralPartAsInt = (int) d;

        double exactFractionalPart = d - (double) integralPartAsInt;

        double scaledFractional =
            exactFractionalPart * fastPathData.fractionalScaleFactor;

        int fractionalPartAsInt = (int) scaledFractional;

        scaledFractional = scaledFractional - (double) fractionalPartAsInt;

        boolean roundItUp = false;
        if (scaledFractional >= 0.5d) {
            if (scaledFractional == 0.5d)
                roundItUp = exactRoundUp(exactFractionalPart, fractionalPartAsInt);
            else
                roundItUp = true;

            if (roundItUp) {
                if (fractionalPartAsInt < fastPathData.fractionalMaxIntBound) {
                    fractionalPartAsInt++;
                } else {
                    fractionalPartAsInt = 0;
                    integralPartAsInt++;
                }
            }
        }

        collectFractionalDigits(fractionalPartAsInt, container,
                                fastPathData.fractionalFirstIndex);
        collectIntegralDigits(integralPartAsInt, container,
                              fastPathData.integralLastIndex);

        if (fastPathData.zeroDelta != 0)
            localizeDigits(container);

        if (negative) {
            if (fastPathData.negativeAffixesRequired)
                addAffixes(container,
                           fastPathData.charsNegativePrefix,
                           fastPathData.charsNegativeSuffix);
        } else if (fastPathData.positiveAffixesRequired)
            addAffixes(container,
                       fastPathData.charsPositivePrefix,
                       fastPathData.charsPositiveSuffix);
    }


    String fastFormat(double d) {
        boolean isDataSet = false;
        if (fastPathCheckNeeded) {
            isDataSet = checkAndSetFastPathStatus();
        }

        if (!isFastPath )
            return null;

        if (!Double.isFinite(d))
            return null;

        boolean negative = false;
        if (d < 0.0d) {
            negative = true;
            d = -d;
        } else if (d == 0.0d) {
            negative = (Math.copySign(1.0d, d) == -1.0d);
            d = +0.0d;
        }

        if (d > MAX_INT_AS_DOUBLE)
            return null;
        else {
            if (!isDataSet) {

                resetFastPathData(isFastPath);
            }
            fastDoubleFormat(d, negative);

        }


        return new String(fastPathData.fastPathContainer,
                          fastPathData.firstUsedIndex,
                          fastPathData.lastFreeIndex - fastPathData.firstUsedIndex);

    }

    private StringBuffer subformat(StringBuffer result, FieldDelegate delegate,
                                   boolean isNegative, boolean isInteger,
                                   int maxIntDigits, int minIntDigits,
                                   int maxFraDigits, int minFraDigits) {
        char zero = symbols.getZeroDigit();
        int zeroDelta = zero - '0'; char grouping = symbols.getGroupingSeparator();
        char decimal = isCurrencyFormat ?
            symbols.getMonetaryDecimalSeparator() :
            symbols.getDecimalSeparator();


        if (digitList.isZero()) {
            digitList.decimalAt = 0; }

        if (isNegative) {
            append(result, negativePrefix, delegate,
                   getNegativePrefixFieldPositions(), Field.SIGN);
        } else {
            append(result, positivePrefix, delegate,
                   getPositivePrefixFieldPositions(), Field.SIGN);
        }

        if (useExponentialNotation) {
            int iFieldStart = result.length();
            int iFieldEnd = -1;
            int fFieldStart = -1;

            int exponent = digitList.decimalAt;
            int repeat = maxIntDigits;
            int minimumIntegerDigits = minIntDigits;
            if (repeat > 1 && repeat > minIntDigits) {
                if (exponent >= 1) {
                    exponent = ((exponent - 1) / repeat) * repeat;
                } else {
                    exponent = ((exponent - repeat) / repeat) * repeat;
                }
                minimumIntegerDigits = 1;
            } else {
                exponent -= minimumIntegerDigits;
            }

            int minimumDigits = minIntDigits + minFraDigits;
            if (minimumDigits < 0) {    minimumDigits = Integer.MAX_VALUE;
            }

            int integerDigits = digitList.isZero() ? minimumIntegerDigits :
                    digitList.decimalAt - exponent;
            if (minimumDigits < integerDigits) {
                minimumDigits = integerDigits;
            }
            int totalDigits = digitList.count;
            if (minimumDigits > totalDigits) {
                totalDigits = minimumDigits;
            }
            boolean addedDecimalSeparator = false;

            for (int i=0; i<totalDigits; ++i) {
                if (i == integerDigits) {
                    iFieldEnd = result.length();

                    result.append(decimal);
                    addedDecimalSeparator = true;

                    fFieldStart = result.length();
                }
                result.append((i < digitList.count) ?
                              (char)(digitList.digits[i] + zeroDelta) :
                              zero);
            }

            if (decimalSeparatorAlwaysShown && totalDigits == integerDigits) {
                iFieldEnd = result.length();

                result.append(decimal);
                addedDecimalSeparator = true;

                fFieldStart = result.length();
            }

            if (iFieldEnd == -1) {
                iFieldEnd = result.length();
            }
            delegate.formatted(INTEGER_FIELD, Field.INTEGER, Field.INTEGER,
                               iFieldStart, iFieldEnd, result);
            if (addedDecimalSeparator) {
                delegate.formatted(Field.DECIMAL_SEPARATOR,
                                   Field.DECIMAL_SEPARATOR,
                                   iFieldEnd, fFieldStart, result);
            }
            if (fFieldStart == -1) {
                fFieldStart = result.length();
            }
            delegate.formatted(FRACTION_FIELD, Field.FRACTION, Field.FRACTION,
                               fFieldStart, result.length(), result);

            int fieldStart = result.length();

            result.append(symbols.getExponentSeparator());

            delegate.formatted(Field.EXPONENT_SYMBOL, Field.EXPONENT_SYMBOL,
                               fieldStart, result.length(), result);

            if (digitList.isZero()) {
                exponent = 0;
            }

            boolean negativeExponent = exponent < 0;
            if (negativeExponent) {
                exponent = -exponent;
                fieldStart = result.length();
                result.append(symbols.getMinusSign());
                delegate.formatted(Field.EXPONENT_SIGN, Field.EXPONENT_SIGN,
                                   fieldStart, result.length(), result);
            }
            digitList.set(negativeExponent, exponent);

            int eFieldStart = result.length();

            for (int i=digitList.decimalAt; i<minExponentDigits; ++i) {
                result.append(zero);
            }
            for (int i=0; i<digitList.decimalAt; ++i) {
                result.append((i < digitList.count) ?
                          (char)(digitList.digits[i] + zeroDelta) : zero);
            }
            delegate.formatted(Field.EXPONENT, Field.EXPONENT, eFieldStart,
                               result.length(), result);
        } else {
            int iFieldStart = result.length();

            int count = minIntDigits;
            int digitIndex = 0; if (digitList.decimalAt > 0 && count < digitList.decimalAt) {
                count = digitList.decimalAt;
            }

            if (count > maxIntDigits) {
                count = maxIntDigits;
                digitIndex = digitList.decimalAt - count;
            }

            int sizeBeforeIntegerPart = result.length();
            for (int i=count-1; i>=0; --i) {
                if (i < digitList.decimalAt && digitIndex < digitList.count) {
                    result.append((char)(digitList.digits[digitIndex++] + zeroDelta));
                } else {
                    result.append(zero);
                }

                if (isGroupingUsed() && i>0 && (groupingSize != 0) &&
                    (i % groupingSize == 0)) {
                    int gStart = result.length();
                    result.append(grouping);
                    delegate.formatted(Field.GROUPING_SEPARATOR,
                                       Field.GROUPING_SEPARATOR, gStart,
                                       result.length(), result);
                }
            }

            boolean fractionPresent = (minFraDigits > 0) ||
                (!isInteger && digitIndex < digitList.count);

            if (!fractionPresent && result.length() == sizeBeforeIntegerPart) {
                result.append(zero);
            }

            delegate.formatted(INTEGER_FIELD, Field.INTEGER, Field.INTEGER,
                               iFieldStart, result.length(), result);

            int sStart = result.length();
            if (decimalSeparatorAlwaysShown || fractionPresent) {
                result.append(decimal);
            }

            if (sStart != result.length()) {
                delegate.formatted(Field.DECIMAL_SEPARATOR,
                                   Field.DECIMAL_SEPARATOR,
                                   sStart, result.length(), result);
            }
            int fFieldStart = result.length();

            for (int i=0; i < maxFraDigits; ++i) {
                if (i >= minFraDigits &&
                    (isInteger || digitIndex >= digitList.count)) {
                    break;
                }

                if (-1-i > (digitList.decimalAt-1)) {
                    result.append(zero);
                    continue;
                }

                if (!isInteger && digitIndex < digitList.count) {
                    result.append((char)(digitList.digits[digitIndex++] + zeroDelta));
                } else {
                    result.append(zero);
                }
            }

            delegate.formatted(FRACTION_FIELD, Field.FRACTION, Field.FRACTION,
                               fFieldStart, result.length(), result);
        }

        if (isNegative) {
            append(result, negativeSuffix, delegate,
                   getNegativeSuffixFieldPositions(), Field.SIGN);
        } else {
            append(result, positiveSuffix, delegate,
                   getPositiveSuffixFieldPositions(), Field.SIGN);
        }

        return result;
    }


    private void append(StringBuffer result, String string,
                        FieldDelegate delegate,
                        FieldPosition[] positions,
                        Format.Field signAttribute) {
        int start = result.length();

        if (string.length() > 0) {
            result.append(string);
            for (int counter = 0, max = positions.length; counter < max;
                 counter++) {
                FieldPosition fp = positions[counter];
                Format.Field attribute = fp.getFieldAttribute();

                if (attribute == Field.SIGN) {
                    attribute = signAttribute;
                }
                delegate.formatted(attribute, attribute,
                                   start + fp.getBeginIndex(),
                                   start + fp.getEndIndex(), result);
            }
        }
    }


    @Override
    public Number parse(String text, ParsePosition pos) {
        if (text.regionMatches(pos.index, symbols.getNaN(), 0, symbols.getNaN().length())) {
            pos.index = pos.index + symbols.getNaN().length();
            return new Double(Double.NaN);
        }

        boolean[] status = new boolean[STATUS_LENGTH];
        if (!subparse(text, pos, positivePrefix, negativePrefix, digitList, false, status)) {
            return null;
        }

        if (status[STATUS_INFINITE]) {
            if (status[STATUS_POSITIVE] == (multiplier >= 0)) {
                return new Double(Double.POSITIVE_INFINITY);
            } else {
                return new Double(Double.NEGATIVE_INFINITY);
            }
        }

        if (multiplier == 0) {
            if (digitList.isZero()) {
                return new Double(Double.NaN);
            } else if (status[STATUS_POSITIVE]) {
                return new Double(Double.POSITIVE_INFINITY);
            } else {
                return new Double(Double.NEGATIVE_INFINITY);
            }
        }

        if (isParseBigDecimal()) {
            BigDecimal bigDecimalResult = digitList.getBigDecimal();

            if (multiplier != 1) {
                try {
                    bigDecimalResult = bigDecimalResult.divide(getBigDecimalMultiplier());
                }
                catch (ArithmeticException e) {  bigDecimalResult = bigDecimalResult.divide(getBigDecimalMultiplier(), roundingMode);
                }
            }

            if (!status[STATUS_POSITIVE]) {
                bigDecimalResult = bigDecimalResult.negate();
            }
            return bigDecimalResult;
        } else {
            boolean gotDouble = true;
            boolean gotLongMinimum = false;
            double  doubleResult = 0.0;
            long    longResult = 0;

            if (digitList.fitsIntoLong(status[STATUS_POSITIVE], isParseIntegerOnly())) {
                gotDouble = false;
                longResult = digitList.getLong();
                if (longResult < 0) {  gotLongMinimum = true;
                }
            } else {
                doubleResult = digitList.getDouble();
            }

            if (multiplier != 1) {
                if (gotDouble) {
                    doubleResult /= multiplier;
                } else {
                    if (longResult % multiplier == 0) {
                        longResult /= multiplier;
                    } else {
                        doubleResult = ((double)longResult) / multiplier;
                        gotDouble = true;
                    }
                }
            }

            if (!status[STATUS_POSITIVE] && !gotLongMinimum) {
                doubleResult = -doubleResult;
                longResult = -longResult;
            }

            if (multiplier != 1 && gotDouble) {
                longResult = (long)doubleResult;
                gotDouble = ((doubleResult != (double)longResult) ||
                            (doubleResult == 0.0 && 1/doubleResult < 0.0)) &&
                            !isParseIntegerOnly();
            }

            return gotDouble ?
                (Number)new Double(doubleResult) : (Number)new Long(longResult);
        }
    }


    private BigInteger getBigIntegerMultiplier() {
        if (bigIntegerMultiplier == null) {
            bigIntegerMultiplier = BigInteger.valueOf(multiplier);
        }
        return bigIntegerMultiplier;
    }
    private transient BigInteger bigIntegerMultiplier;


    private BigDecimal getBigDecimalMultiplier() {
        if (bigDecimalMultiplier == null) {
            bigDecimalMultiplier = new BigDecimal(multiplier);
        }
        return bigDecimalMultiplier;
    }
    private transient BigDecimal bigDecimalMultiplier;

    private static final int STATUS_INFINITE = 0;
    private static final int STATUS_POSITIVE = 1;
    private static final int STATUS_LENGTH   = 2;


    private final boolean subparse(String text, ParsePosition parsePosition,
                   String positivePrefix, String negativePrefix,
                   DigitList digits, boolean isExponent,
                   boolean status[]) {
        int position = parsePosition.index;
        int oldStart = parsePosition.index;
        int backup;
        boolean gotPositive, gotNegative;

        gotPositive = text.regionMatches(position, positivePrefix, 0,
                                         positivePrefix.length());
        gotNegative = text.regionMatches(position, negativePrefix, 0,
                                         negativePrefix.length());

        if (gotPositive && gotNegative) {
            if (positivePrefix.length() > negativePrefix.length()) {
                gotNegative = false;
            } else if (positivePrefix.length() < negativePrefix.length()) {
                gotPositive = false;
            }
        }

        if (gotPositive) {
            position += positivePrefix.length();
        } else if (gotNegative) {
            position += negativePrefix.length();
        } else {
            parsePosition.errorIndex = position;
            return false;
        }

        status[STATUS_INFINITE] = false;
        if (!isExponent && text.regionMatches(position,symbols.getInfinity(),0,
                          symbols.getInfinity().length())) {
            position += symbols.getInfinity().length();
            status[STATUS_INFINITE] = true;
        } else {
            digits.decimalAt = digits.count = 0;
            char zero = symbols.getZeroDigit();
            char decimal = isCurrencyFormat ?
                symbols.getMonetaryDecimalSeparator() :
                symbols.getDecimalSeparator();
            char grouping = symbols.getGroupingSeparator();
            String exponentString = symbols.getExponentSeparator();
            boolean sawDecimal = false;
            boolean sawExponent = false;
            boolean sawDigit = false;
            int exponent = 0; int digitCount = 0;

            backup = -1;
            for (; position < text.length(); ++position) {
                char ch = text.charAt(position);


                int digit = ch - zero;
                if (digit < 0 || digit > 9) {
                    digit = Character.digit(ch, 10);
                }

                if (digit == 0) {
                    backup = -1; sawDigit = true;

                    if (digits.count == 0) {
                        if (!sawDecimal) {
                            continue;
                        }

                        --digits.decimalAt;
                    } else {
                        ++digitCount;
                        digits.append((char)(digit + '0'));
                    }
                } else if (digit > 0 && digit <= 9) { sawDigit = true;
                    ++digitCount;
                    digits.append((char)(digit + '0'));

                    backup = -1;
                } else if (!isExponent && ch == decimal) {
                    if (isParseIntegerOnly() || sawDecimal) {
                        break;
                    }
                    digits.decimalAt = digitCount; sawDecimal = true;
                } else if (!isExponent && ch == grouping && isGroupingUsed()) {
                    if (sawDecimal) {
                        break;
                    }
                    backup = position;
                } else if (!isExponent && text.regionMatches(position, exponentString, 0, exponentString.length())
                             && !sawExponent) {
                    ParsePosition pos = new ParsePosition(position + exponentString.length());
                    boolean[] stat = new boolean[STATUS_LENGTH];
                    DigitList exponentDigits = new DigitList();

                    if (subparse(text, pos, "", Character.toString(symbols.getMinusSign()), exponentDigits, true, stat) &&
                        exponentDigits.fitsIntoLong(stat[STATUS_POSITIVE], true)) {
                        position = pos.index; exponent = (int)exponentDigits.getLong();
                        if (!stat[STATUS_POSITIVE]) {
                            exponent = -exponent;
                        }
                        sawExponent = true;
                    }
                    break; } else {
                    break;
                }
            }

            if (backup != -1) {
                position = backup;
            }

            if (!sawDecimal) {
                digits.decimalAt = digitCount; }

            digits.decimalAt += exponent;

            if (!sawDigit && digitCount == 0) {
                parsePosition.index = oldStart;
                parsePosition.errorIndex = oldStart;
                return false;
            }
        }

        if (!isExponent) {
            if (gotPositive) {
                gotPositive = text.regionMatches(position,positiveSuffix,0,
                                                 positiveSuffix.length());
            }
            if (gotNegative) {
                gotNegative = text.regionMatches(position,negativeSuffix,0,
                                                 negativeSuffix.length());
            }

        if (gotPositive && gotNegative) {
            if (positiveSuffix.length() > negativeSuffix.length()) {
                gotNegative = false;
            } else if (positiveSuffix.length() < negativeSuffix.length()) {
                gotPositive = false;
            }
        }

        if (gotPositive == gotNegative) {
            parsePosition.errorIndex = position;
            return false;
        }

        parsePosition.index = position +
            (gotPositive ? positiveSuffix.length() : negativeSuffix.length()); } else {
            parsePosition.index = position;
        }

        status[STATUS_POSITIVE] = gotPositive;
        if (parsePosition.index == oldStart) {
            parsePosition.errorIndex = position;
            return false;
        }
        return true;
    }


    public DecimalFormatSymbols getDecimalFormatSymbols() {
        try {
            return (DecimalFormatSymbols) symbols.clone();
        } catch (Exception foo) {
            return null; }
    }



    public void setDecimalFormatSymbols(DecimalFormatSymbols newSymbols) {
        try {
            symbols = (DecimalFormatSymbols) newSymbols.clone();
            expandAffixes();
            fastPathCheckNeeded = true;
        } catch (Exception foo) {
            }
    }


    public String getPositivePrefix () {
        return positivePrefix;
    }


    public void setPositivePrefix (String newValue) {
        positivePrefix = newValue;
        posPrefixPattern = null;
        positivePrefixFieldPositions = null;
        fastPathCheckNeeded = true;
    }


    private FieldPosition[] getPositivePrefixFieldPositions() {
        if (positivePrefixFieldPositions == null) {
            if (posPrefixPattern != null) {
                positivePrefixFieldPositions = expandAffix(posPrefixPattern);
            } else {
                positivePrefixFieldPositions = EmptyFieldPositionArray;
            }
        }
        return positivePrefixFieldPositions;
    }


    public String getNegativePrefix () {
        return negativePrefix;
    }


    public void setNegativePrefix (String newValue) {
        negativePrefix = newValue;
        negPrefixPattern = null;
        fastPathCheckNeeded = true;
    }


    private FieldPosition[] getNegativePrefixFieldPositions() {
        if (negativePrefixFieldPositions == null) {
            if (negPrefixPattern != null) {
                negativePrefixFieldPositions = expandAffix(negPrefixPattern);
            } else {
                negativePrefixFieldPositions = EmptyFieldPositionArray;
            }
        }
        return negativePrefixFieldPositions;
    }


    public String getPositiveSuffix () {
        return positiveSuffix;
    }


    public void setPositiveSuffix (String newValue) {
        positiveSuffix = newValue;
        posSuffixPattern = null;
        fastPathCheckNeeded = true;
    }


    private FieldPosition[] getPositiveSuffixFieldPositions() {
        if (positiveSuffixFieldPositions == null) {
            if (posSuffixPattern != null) {
                positiveSuffixFieldPositions = expandAffix(posSuffixPattern);
            } else {
                positiveSuffixFieldPositions = EmptyFieldPositionArray;
            }
        }
        return positiveSuffixFieldPositions;
    }


    public String getNegativeSuffix () {
        return negativeSuffix;
    }


    public void setNegativeSuffix (String newValue) {
        negativeSuffix = newValue;
        negSuffixPattern = null;
        fastPathCheckNeeded = true;
    }


    private FieldPosition[] getNegativeSuffixFieldPositions() {
        if (negativeSuffixFieldPositions == null) {
            if (negSuffixPattern != null) {
                negativeSuffixFieldPositions = expandAffix(negSuffixPattern);
            } else {
                negativeSuffixFieldPositions = EmptyFieldPositionArray;
            }
        }
        return negativeSuffixFieldPositions;
    }


    public int getMultiplier () {
        return multiplier;
    }


    public void setMultiplier (int newValue) {
        multiplier = newValue;
        bigDecimalMultiplier = null;
        bigIntegerMultiplier = null;
        fastPathCheckNeeded = true;
    }


    @Override
    public void setGroupingUsed(boolean newValue) {
        super.setGroupingUsed(newValue);
        fastPathCheckNeeded = true;
    }


    public int getGroupingSize () {
        return groupingSize;
    }


    public void setGroupingSize (int newValue) {
        groupingSize = (byte)newValue;
        fastPathCheckNeeded = true;
    }


    public boolean isDecimalSeparatorAlwaysShown() {
        return decimalSeparatorAlwaysShown;
    }


    public void setDecimalSeparatorAlwaysShown(boolean newValue) {
        decimalSeparatorAlwaysShown = newValue;
        fastPathCheckNeeded = true;
    }


    public boolean isParseBigDecimal() {
        return parseBigDecimal;
    }


    public void setParseBigDecimal(boolean newValue) {
        parseBigDecimal = newValue;
    }


    @Override
    public Object clone() {
        DecimalFormat other = (DecimalFormat) super.clone();
        other.symbols = (DecimalFormatSymbols) symbols.clone();
        other.digitList = (DigitList) digitList.clone();

        other.fastPathCheckNeeded = true;
        other.isFastPath = false;
        other.fastPathData = null;

        return other;
    }


    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        if (!super.equals(obj))
            return false; DecimalFormat other = (DecimalFormat) obj;
        return ((posPrefixPattern == other.posPrefixPattern &&
                 positivePrefix.equals(other.positivePrefix))
                || (posPrefixPattern != null &&
                    posPrefixPattern.equals(other.posPrefixPattern)))
            && ((posSuffixPattern == other.posSuffixPattern &&
                 positiveSuffix.equals(other.positiveSuffix))
                || (posSuffixPattern != null &&
                    posSuffixPattern.equals(other.posSuffixPattern)))
            && ((negPrefixPattern == other.negPrefixPattern &&
                 negativePrefix.equals(other.negativePrefix))
                || (negPrefixPattern != null &&
                    negPrefixPattern.equals(other.negPrefixPattern)))
            && ((negSuffixPattern == other.negSuffixPattern &&
                 negativeSuffix.equals(other.negativeSuffix))
                || (negSuffixPattern != null &&
                    negSuffixPattern.equals(other.negSuffixPattern)))
            && multiplier == other.multiplier
            && groupingSize == other.groupingSize
            && decimalSeparatorAlwaysShown == other.decimalSeparatorAlwaysShown
            && parseBigDecimal == other.parseBigDecimal
            && useExponentialNotation == other.useExponentialNotation
            && (!useExponentialNotation ||
                minExponentDigits == other.minExponentDigits)
            && maximumIntegerDigits == other.maximumIntegerDigits
            && minimumIntegerDigits == other.minimumIntegerDigits
            && maximumFractionDigits == other.maximumFractionDigits
            && minimumFractionDigits == other.minimumFractionDigits
            && roundingMode == other.roundingMode
            && symbols.equals(other.symbols);
    }


    @Override
    public int hashCode() {
        return super.hashCode() * 37 + positivePrefix.hashCode();
        }


    public String toPattern() {
        return toPattern( false );
    }


    public String toLocalizedPattern() {
        return toPattern( true );
    }


    private void expandAffixes() {
        StringBuffer buffer = new StringBuffer();
        if (posPrefixPattern != null) {
            positivePrefix = expandAffix(posPrefixPattern, buffer);
            positivePrefixFieldPositions = null;
        }
        if (posSuffixPattern != null) {
            positiveSuffix = expandAffix(posSuffixPattern, buffer);
            positiveSuffixFieldPositions = null;
        }
        if (negPrefixPattern != null) {
            negativePrefix = expandAffix(negPrefixPattern, buffer);
            negativePrefixFieldPositions = null;
        }
        if (negSuffixPattern != null) {
            negativeSuffix = expandAffix(negSuffixPattern, buffer);
            negativeSuffixFieldPositions = null;
        }
    }


    private String expandAffix(String pattern, StringBuffer buffer) {
        buffer.setLength(0);
        for (int i=0; i<pattern.length(); ) {
            char c = pattern.charAt(i++);
            if (c == QUOTE) {
                c = pattern.charAt(i++);
                switch (c) {
                case CURRENCY_SIGN:
                    if (i<pattern.length() &&
                        pattern.charAt(i) == CURRENCY_SIGN) {
                        ++i;
                        buffer.append(symbols.getInternationalCurrencySymbol());
                    } else {
                        buffer.append(symbols.getCurrencySymbol());
                    }
                    continue;
                case PATTERN_PERCENT:
                    c = symbols.getPercent();
                    break;
                case PATTERN_PER_MILLE:
                    c = symbols.getPerMill();
                    break;
                case PATTERN_MINUS:
                    c = symbols.getMinusSign();
                    break;
                }
            }
            buffer.append(c);
        }
        return buffer.toString();
    }


    private FieldPosition[] expandAffix(String pattern) {
        ArrayList<FieldPosition> positions = null;
        int stringIndex = 0;
        for (int i=0; i<pattern.length(); ) {
            char c = pattern.charAt(i++);
            if (c == QUOTE) {
                int field = -1;
                Format.Field fieldID = null;
                c = pattern.charAt(i++);
                switch (c) {
                case CURRENCY_SIGN:
                    String string;
                    if (i<pattern.length() &&
                        pattern.charAt(i) == CURRENCY_SIGN) {
                        ++i;
                        string = symbols.getInternationalCurrencySymbol();
                    } else {
                        string = symbols.getCurrencySymbol();
                    }
                    if (string.length() > 0) {
                        if (positions == null) {
                            positions = new ArrayList<>(2);
                        }
                        FieldPosition fp = new FieldPosition(Field.CURRENCY);
                        fp.setBeginIndex(stringIndex);
                        fp.setEndIndex(stringIndex + string.length());
                        positions.add(fp);
                        stringIndex += string.length();
                    }
                    continue;
                case PATTERN_PERCENT:
                    c = symbols.getPercent();
                    field = -1;
                    fieldID = Field.PERCENT;
                    break;
                case PATTERN_PER_MILLE:
                    c = symbols.getPerMill();
                    field = -1;
                    fieldID = Field.PERMILLE;
                    break;
                case PATTERN_MINUS:
                    c = symbols.getMinusSign();
                    field = -1;
                    fieldID = Field.SIGN;
                    break;
                }
                if (fieldID != null) {
                    if (positions == null) {
                        positions = new ArrayList<>(2);
                    }
                    FieldPosition fp = new FieldPosition(fieldID, field);
                    fp.setBeginIndex(stringIndex);
                    fp.setEndIndex(stringIndex + 1);
                    positions.add(fp);
                }
            }
            stringIndex++;
        }
        if (positions != null) {
            return positions.toArray(EmptyFieldPositionArray);
        }
        return EmptyFieldPositionArray;
    }


    private void appendAffix(StringBuffer buffer, String affixPattern,
                             String expAffix, boolean localized) {
        if (affixPattern == null) {
            appendAffix(buffer, expAffix, localized);
        } else {
            int i;
            for (int pos=0; pos<affixPattern.length(); pos=i) {
                i = affixPattern.indexOf(QUOTE, pos);
                if (i < 0) {
                    appendAffix(buffer, affixPattern.substring(pos), localized);
                    break;
                }
                if (i > pos) {
                    appendAffix(buffer, affixPattern.substring(pos, i), localized);
                }
                char c = affixPattern.charAt(++i);
                ++i;
                if (c == QUOTE) {
                    buffer.append(c);
                    } else if (c == CURRENCY_SIGN &&
                           i<affixPattern.length() &&
                           affixPattern.charAt(i) == CURRENCY_SIGN) {
                    ++i;
                    buffer.append(c);
                    } else if (localized) {
                    switch (c) {
                    case PATTERN_PERCENT:
                        c = symbols.getPercent();
                        break;
                    case PATTERN_PER_MILLE:
                        c = symbols.getPerMill();
                        break;
                    case PATTERN_MINUS:
                        c = symbols.getMinusSign();
                        break;
                    }
                }
                buffer.append(c);
            }
        }
    }


    private void appendAffix(StringBuffer buffer, String affix, boolean localized) {
        boolean needQuote;
        if (localized) {
            needQuote = affix.indexOf(symbols.getZeroDigit()) >= 0
                || affix.indexOf(symbols.getGroupingSeparator()) >= 0
                || affix.indexOf(symbols.getDecimalSeparator()) >= 0
                || affix.indexOf(symbols.getPercent()) >= 0
                || affix.indexOf(symbols.getPerMill()) >= 0
                || affix.indexOf(symbols.getDigit()) >= 0
                || affix.indexOf(symbols.getPatternSeparator()) >= 0
                || affix.indexOf(symbols.getMinusSign()) >= 0
                || affix.indexOf(CURRENCY_SIGN) >= 0;
        } else {
            needQuote = affix.indexOf(PATTERN_ZERO_DIGIT) >= 0
                || affix.indexOf(PATTERN_GROUPING_SEPARATOR) >= 0
                || affix.indexOf(PATTERN_DECIMAL_SEPARATOR) >= 0
                || affix.indexOf(PATTERN_PERCENT) >= 0
                || affix.indexOf(PATTERN_PER_MILLE) >= 0
                || affix.indexOf(PATTERN_DIGIT) >= 0
                || affix.indexOf(PATTERN_SEPARATOR) >= 0
                || affix.indexOf(PATTERN_MINUS) >= 0
                || affix.indexOf(CURRENCY_SIGN) >= 0;
        }
        if (needQuote) buffer.append('\'');
        if (affix.indexOf('\'') < 0) buffer.append(affix);
        else {
            for (int j=0; j<affix.length(); ++j) {
                char c = affix.charAt(j);
                buffer.append(c);
                if (c == '\'') buffer.append(c);
            }
        }
        if (needQuote) buffer.append('\'');
    }


    private String toPattern(boolean localized) {
        StringBuffer result = new StringBuffer();
        for (int j = 1; j >= 0; --j) {
            if (j == 1)
                appendAffix(result, posPrefixPattern, positivePrefix, localized);
            else appendAffix(result, negPrefixPattern, negativePrefix, localized);
            int i;
            int digitCount = useExponentialNotation
                        ? getMaximumIntegerDigits()
                        : Math.max(groupingSize, getMinimumIntegerDigits())+1;
            for (i = digitCount; i > 0; --i) {
                if (i != digitCount && isGroupingUsed() && groupingSize != 0 &&
                    i % groupingSize == 0) {
                    result.append(localized ? symbols.getGroupingSeparator() :
                                  PATTERN_GROUPING_SEPARATOR);
                }
                result.append(i <= getMinimumIntegerDigits()
                    ? (localized ? symbols.getZeroDigit() : PATTERN_ZERO_DIGIT)
                    : (localized ? symbols.getDigit() : PATTERN_DIGIT));
            }
            if (getMaximumFractionDigits() > 0 || decimalSeparatorAlwaysShown)
                result.append(localized ? symbols.getDecimalSeparator() :
                              PATTERN_DECIMAL_SEPARATOR);
            for (i = 0; i < getMaximumFractionDigits(); ++i) {
                if (i < getMinimumFractionDigits()) {
                    result.append(localized ? symbols.getZeroDigit() :
                                  PATTERN_ZERO_DIGIT);
                } else {
                    result.append(localized ? symbols.getDigit() :
                                  PATTERN_DIGIT);
                }
            }
        if (useExponentialNotation)
        {
            result.append(localized ? symbols.getExponentSeparator() :
                  PATTERN_EXPONENT);
        for (i=0; i<minExponentDigits; ++i)
                    result.append(localized ? symbols.getZeroDigit() :
                                  PATTERN_ZERO_DIGIT);
        }
            if (j == 1) {
                appendAffix(result, posSuffixPattern, positiveSuffix, localized);
                if ((negSuffixPattern == posSuffixPattern && negativeSuffix.equals(positiveSuffix))
                    || (negSuffixPattern != null &&
                        negSuffixPattern.equals(posSuffixPattern))) {
                    if ((negPrefixPattern != null && posPrefixPattern != null &&
                         negPrefixPattern.equals("'-" + posPrefixPattern)) ||
                        (negPrefixPattern == posPrefixPattern && negativePrefix.equals(symbols.getMinusSign() + positivePrefix)))
                        break;
                }
                result.append(localized ? symbols.getPatternSeparator() :
                              PATTERN_SEPARATOR);
            } else appendAffix(result, negSuffixPattern, negativeSuffix, localized);
        }
        return result.toString();
    }


    public void applyPattern(String pattern) {
        applyPattern(pattern, false);
    }


    public void applyLocalizedPattern(String pattern) {
        applyPattern(pattern, true);
    }


    private void applyPattern(String pattern, boolean localized) {
        char zeroDigit         = PATTERN_ZERO_DIGIT;
        char groupingSeparator = PATTERN_GROUPING_SEPARATOR;
        char decimalSeparator  = PATTERN_DECIMAL_SEPARATOR;
        char percent           = PATTERN_PERCENT;
        char perMill           = PATTERN_PER_MILLE;
        char digit             = PATTERN_DIGIT;
        char separator         = PATTERN_SEPARATOR;
        String exponent          = PATTERN_EXPONENT;
        char minus             = PATTERN_MINUS;
        if (localized) {
            zeroDigit         = symbols.getZeroDigit();
            groupingSeparator = symbols.getGroupingSeparator();
            decimalSeparator  = symbols.getDecimalSeparator();
            percent           = symbols.getPercent();
            perMill           = symbols.getPerMill();
            digit             = symbols.getDigit();
            separator         = symbols.getPatternSeparator();
            exponent          = symbols.getExponentSeparator();
            minus             = symbols.getMinusSign();
        }
        boolean gotNegative = false;
        decimalSeparatorAlwaysShown = false;
        isCurrencyFormat = false;
        useExponentialNotation = false;

        int phaseOneStart = 0;
        int phaseOneLength = 0;

        int start = 0;
        for (int j = 1; j >= 0 && start < pattern.length(); --j) {
            boolean inQuote = false;
            StringBuffer prefix = new StringBuffer();
            StringBuffer suffix = new StringBuffer();
            int decimalPos = -1;
            int multiplier = 1;
            int digitLeftCount = 0, zeroDigitCount = 0, digitRightCount = 0;
            byte groupingCount = -1;

            int phase = 0;

            StringBuffer affix = prefix;

            for (int pos = start; pos < pattern.length(); ++pos) {
                char ch = pattern.charAt(pos);
                switch (phase) {
                case 0:
                case 2:
                    if (inQuote) {
                        if (ch == QUOTE) {
                            if ((pos+1) < pattern.length() &&
                                pattern.charAt(pos+1) == QUOTE) {
                                ++pos;
                                affix.append("''"); } else {
                                inQuote = false; }
                            continue;
                        }
                    } else {
                        if (ch == digit ||
                            ch == zeroDigit ||
                            ch == groupingSeparator ||
                            ch == decimalSeparator) {
                            phase = 1;
                            if (j == 1) {
                                phaseOneStart = pos;
                            }
                            --pos; continue;
                        } else if (ch == CURRENCY_SIGN) {
                            boolean doubled = (pos + 1) < pattern.length() &&
                                pattern.charAt(pos + 1) == CURRENCY_SIGN;
                            if (doubled) { ++pos;
                            }
                            isCurrencyFormat = true;
                            affix.append(doubled ? "'\u00A4\u00A4" : "'\u00A4");
                            continue;
                        } else if (ch == QUOTE) {
                            if (ch == QUOTE) {
                                if ((pos+1) < pattern.length() &&
                                    pattern.charAt(pos+1) == QUOTE) {
                                    ++pos;
                                    affix.append("''"); } else {
                                    inQuote = true; }
                                continue;
                            }
                        } else if (ch == separator) {
                            if (phase == 0 || j == 0) {
                                throw new IllegalArgumentException("Unquoted special character '" +
                                    ch + "' in pattern \"" + pattern + '"');
                            }
                            start = pos + 1;
                            pos = pattern.length();
                            continue;
                        }

                        else if (ch == percent) {
                            if (multiplier != 1) {
                                throw new IllegalArgumentException("Too many percent/per mille characters in pattern \"" +
                                    pattern + '"');
                            }
                            multiplier = 100;
                            affix.append("'%");
                            continue;
                        } else if (ch == perMill) {
                            if (multiplier != 1) {
                                throw new IllegalArgumentException("Too many percent/per mille characters in pattern \"" +
                                    pattern + '"');
                            }
                            multiplier = 1000;
                            affix.append("'\u2030");
                            continue;
                        } else if (ch == minus) {
                            affix.append("'-");
                            continue;
                        }
                    }
                    affix.append(ch);
                    break;

                case 1:
                    if (j == 1) {
                        ++phaseOneLength;
                    } else {
                        if (--phaseOneLength == 0) {
                            phase = 2;
                            affix = suffix;
                        }
                        continue;
                    }

                    if (ch == digit) {
                        if (zeroDigitCount > 0) {
                            ++digitRightCount;
                        } else {
                            ++digitLeftCount;
                        }
                        if (groupingCount >= 0 && decimalPos < 0) {
                            ++groupingCount;
                        }
                    } else if (ch == zeroDigit) {
                        if (digitRightCount > 0) {
                            throw new IllegalArgumentException("Unexpected '0' in pattern \"" +
                                pattern + '"');
                        }
                        ++zeroDigitCount;
                        if (groupingCount >= 0 && decimalPos < 0) {
                            ++groupingCount;
                        }
                    } else if (ch == groupingSeparator) {
                        groupingCount = 0;
                    } else if (ch == decimalSeparator) {
                        if (decimalPos >= 0) {
                            throw new IllegalArgumentException("Multiple decimal separators in pattern \"" +
                                pattern + '"');
                        }
                        decimalPos = digitLeftCount + zeroDigitCount + digitRightCount;
                    } else if (pattern.regionMatches(pos, exponent, 0, exponent.length())){
                        if (useExponentialNotation) {
                            throw new IllegalArgumentException("Multiple exponential " +
                                "symbols in pattern \"" + pattern + '"');
                        }
                        useExponentialNotation = true;
                        minExponentDigits = 0;

                        pos = pos+exponent.length();
                         while (pos < pattern.length() &&
                               pattern.charAt(pos) == zeroDigit) {
                            ++minExponentDigits;
                            ++phaseOneLength;
                            ++pos;
                        }

                        if ((digitLeftCount + zeroDigitCount) < 1 ||
                            minExponentDigits < 1) {
                            throw new IllegalArgumentException("Malformed exponential " +
                                "pattern \"" + pattern + '"');
                        }

                        phase = 2;
                        affix = suffix;
                        --pos;
                        continue;
                    } else {
                        phase = 2;
                        affix = suffix;
                        --pos;
                        --phaseOneLength;
                        continue;
                    }
                    break;
                }
            }

            if (zeroDigitCount == 0 && digitLeftCount > 0 && decimalPos >= 0) {
                int n = decimalPos;
                if (n == 0) { ++n;
                }
                digitRightCount = digitLeftCount - n;
                digitLeftCount = n - 1;
                zeroDigitCount = 1;
            }

            if ((decimalPos < 0 && digitRightCount > 0) ||
                (decimalPos >= 0 && (decimalPos < digitLeftCount ||
                 decimalPos > (digitLeftCount + zeroDigitCount))) ||
                 groupingCount == 0 || inQuote) {
                throw new IllegalArgumentException("Malformed pattern \"" +
                    pattern + '"');
            }

            if (j == 1) {
                posPrefixPattern = prefix.toString();
                posSuffixPattern = suffix.toString();
                negPrefixPattern = posPrefixPattern;   negSuffixPattern = posSuffixPattern;
                int digitTotalCount = digitLeftCount + zeroDigitCount + digitRightCount;

                int effectiveDecimalPos = decimalPos >= 0 ?
                    decimalPos : digitTotalCount;
                setMinimumIntegerDigits(effectiveDecimalPos - digitLeftCount);
                setMaximumIntegerDigits(useExponentialNotation ?
                    digitLeftCount + getMinimumIntegerDigits() :
                    MAXIMUM_INTEGER_DIGITS);
                setMaximumFractionDigits(decimalPos >= 0 ?
                    (digitTotalCount - decimalPos) : 0);
                setMinimumFractionDigits(decimalPos >= 0 ?
                    (digitLeftCount + zeroDigitCount - decimalPos) : 0);
                setGroupingUsed(groupingCount > 0);
                this.groupingSize = (groupingCount > 0) ? groupingCount : 0;
                this.multiplier = multiplier;
                setDecimalSeparatorAlwaysShown(decimalPos == 0 ||
                    decimalPos == digitTotalCount);
            } else {
                negPrefixPattern = prefix.toString();
                negSuffixPattern = suffix.toString();
                gotNegative = true;
            }
        }

        if (pattern.length() == 0) {
            posPrefixPattern = posSuffixPattern = "";
            setMinimumIntegerDigits(0);
            setMaximumIntegerDigits(MAXIMUM_INTEGER_DIGITS);
            setMinimumFractionDigits(0);
            setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS);
        }

        if (!gotNegative ||
            (negPrefixPattern.equals(posPrefixPattern)
             && negSuffixPattern.equals(posSuffixPattern))) {
            negSuffixPattern = posSuffixPattern;
            negPrefixPattern = "'-" + posPrefixPattern;
        }

        expandAffixes();
    }


    @Override
    public void setMaximumIntegerDigits(int newValue) {
        maximumIntegerDigits = Math.min(Math.max(0, newValue), MAXIMUM_INTEGER_DIGITS);
        super.setMaximumIntegerDigits((maximumIntegerDigits > DOUBLE_INTEGER_DIGITS) ?
            DOUBLE_INTEGER_DIGITS : maximumIntegerDigits);
        if (minimumIntegerDigits > maximumIntegerDigits) {
            minimumIntegerDigits = maximumIntegerDigits;
            super.setMinimumIntegerDigits((minimumIntegerDigits > DOUBLE_INTEGER_DIGITS) ?
                DOUBLE_INTEGER_DIGITS : minimumIntegerDigits);
        }
        fastPathCheckNeeded = true;
    }


    @Override
    public void setMinimumIntegerDigits(int newValue) {
        minimumIntegerDigits = Math.min(Math.max(0, newValue), MAXIMUM_INTEGER_DIGITS);
        super.setMinimumIntegerDigits((minimumIntegerDigits > DOUBLE_INTEGER_DIGITS) ?
            DOUBLE_INTEGER_DIGITS : minimumIntegerDigits);
        if (minimumIntegerDigits > maximumIntegerDigits) {
            maximumIntegerDigits = minimumIntegerDigits;
            super.setMaximumIntegerDigits((maximumIntegerDigits > DOUBLE_INTEGER_DIGITS) ?
                DOUBLE_INTEGER_DIGITS : maximumIntegerDigits);
        }
        fastPathCheckNeeded = true;
    }


    @Override
    public void setMaximumFractionDigits(int newValue) {
        maximumFractionDigits = Math.min(Math.max(0, newValue), MAXIMUM_FRACTION_DIGITS);
        super.setMaximumFractionDigits((maximumFractionDigits > DOUBLE_FRACTION_DIGITS) ?
            DOUBLE_FRACTION_DIGITS : maximumFractionDigits);
        if (minimumFractionDigits > maximumFractionDigits) {
            minimumFractionDigits = maximumFractionDigits;
            super.setMinimumFractionDigits((minimumFractionDigits > DOUBLE_FRACTION_DIGITS) ?
                DOUBLE_FRACTION_DIGITS : minimumFractionDigits);
        }
        fastPathCheckNeeded = true;
    }


    @Override
    public void setMinimumFractionDigits(int newValue) {
        minimumFractionDigits = Math.min(Math.max(0, newValue), MAXIMUM_FRACTION_DIGITS);
        super.setMinimumFractionDigits((minimumFractionDigits > DOUBLE_FRACTION_DIGITS) ?
            DOUBLE_FRACTION_DIGITS : minimumFractionDigits);
        if (minimumFractionDigits > maximumFractionDigits) {
            maximumFractionDigits = minimumFractionDigits;
            super.setMaximumFractionDigits((maximumFractionDigits > DOUBLE_FRACTION_DIGITS) ?
                DOUBLE_FRACTION_DIGITS : maximumFractionDigits);
        }
        fastPathCheckNeeded = true;
    }


    @Override
    public int getMaximumIntegerDigits() {
        return maximumIntegerDigits;
    }


    @Override
    public int getMinimumIntegerDigits() {
        return minimumIntegerDigits;
    }


    @Override
    public int getMaximumFractionDigits() {
        return maximumFractionDigits;
    }


    @Override
    public int getMinimumFractionDigits() {
        return minimumFractionDigits;
    }


    @Override
    public Currency getCurrency() {
        return symbols.getCurrency();
    }


    @Override
    public void setCurrency(Currency currency) {
        if (currency != symbols.getCurrency()) {
            symbols.setCurrency(currency);
            if (isCurrencyFormat) {
                expandAffixes();
            }
        }
        fastPathCheckNeeded = true;
    }


    @Override
    public RoundingMode getRoundingMode() {
        return roundingMode;
    }


    @Override
    public void setRoundingMode(RoundingMode roundingMode) {
        if (roundingMode == null) {
            throw new NullPointerException();
        }

        this.roundingMode = roundingMode;
        digitList.setRoundingMode(roundingMode);
        fastPathCheckNeeded = true;
    }


    private void readObject(ObjectInputStream stream)
         throws IOException, ClassNotFoundException
    {
        stream.defaultReadObject();
        digitList = new DigitList();

        fastPathCheckNeeded = true;
        isFastPath = false;
        fastPathData = null;

        if (serialVersionOnStream < 4) {
            setRoundingMode(RoundingMode.HALF_EVEN);
        } else {
            setRoundingMode(getRoundingMode());
        }

        if (super.getMaximumIntegerDigits() > DOUBLE_INTEGER_DIGITS ||
            super.getMaximumFractionDigits() > DOUBLE_FRACTION_DIGITS) {
            throw new InvalidObjectException("Digit count out of range");
        }
        if (serialVersionOnStream < 3) {
            setMaximumIntegerDigits(super.getMaximumIntegerDigits());
            setMinimumIntegerDigits(super.getMinimumIntegerDigits());
            setMaximumFractionDigits(super.getMaximumFractionDigits());
            setMinimumFractionDigits(super.getMinimumFractionDigits());
        }
        if (serialVersionOnStream < 1) {
            useExponentialNotation = false;
        }
        serialVersionOnStream = currentSerialVersion;
    }

    private transient DigitList digitList = new DigitList();


    private String  positivePrefix = "";


    private String  positiveSuffix = "";


    private String  negativePrefix = "-";


    private String  negativeSuffix = "";


    private String posPrefixPattern;


    private String posSuffixPattern;


    private String negPrefixPattern;


    private String negSuffixPattern;


    private int     multiplier = 1;


    private byte    groupingSize = 3;  private boolean decimalSeparatorAlwaysShown = false;


    private boolean parseBigDecimal = false;



    private transient boolean isCurrencyFormat = false;


    private DecimalFormatSymbols symbols = null; private boolean useExponentialNotation;  private transient FieldPosition[] positivePrefixFieldPositions;


    private transient FieldPosition[] positiveSuffixFieldPositions;


    private transient FieldPosition[] negativePrefixFieldPositions;


    private transient FieldPosition[] negativeSuffixFieldPositions;


    private byte    minExponentDigits;       private int    maximumIntegerDigits = super.getMaximumIntegerDigits();


    private int    minimumIntegerDigits = super.getMinimumIntegerDigits();


    private int    maximumFractionDigits = super.getMaximumFractionDigits();


    private int    minimumFractionDigits = super.getMinimumFractionDigits();


    private RoundingMode roundingMode = RoundingMode.HALF_EVEN;

    private static class FastPathData {
        int lastFreeIndex;


        int firstUsedIndex;

        int  zeroDelta;


        char groupingChar;


        int integralLastIndex;


        int fractionalFirstIndex;


        double fractionalScaleFactor;
        int fractionalMaxIntBound;



        char[] fastPathContainer;


        char[] charsPositivePrefix;
        char[] charsNegativePrefix;
        char[] charsPositiveSuffix;
        char[] charsNegativeSuffix;
        boolean positiveAffixesRequired = true;
        boolean negativeAffixesRequired = true;
    }


    private transient boolean isFastPath = false;


    private transient boolean fastPathCheckNeeded = true;


    private transient FastPathData fastPathData;


    static final int currentSerialVersion = 4;


    private int serialVersionOnStream = currentSerialVersion;

    private static final double MAX_INT_AS_DOUBLE = (double) Integer.MAX_VALUE;


    private static class DigitArrays {
        static final char[] DigitOnes1000 = new char[1000];
        static final char[] DigitTens1000 = new char[1000];
        static final char[] DigitHundreds1000 = new char[1000];

        static {
            int tenIndex = 0;
            int hundredIndex = 0;
            char digitOne = '0';
            char digitTen = '0';
            char digitHundred = '0';
            for (int i = 0;  i < 1000; i++ ) {

                DigitOnes1000[i] = digitOne;
                if (digitOne == '9')
                    digitOne = '0';
                else
                    digitOne++;

                DigitTens1000[i] = digitTen;
                if (i == (tenIndex + 9)) {
                    tenIndex += 10;
                    if (digitTen == '9')
                        digitTen = '0';
                    else
                        digitTen++;
                }

                DigitHundreds1000[i] = digitHundred;
                if (i == (hundredIndex + 99)) {
                    digitHundred++;
                    hundredIndex += 100;
                }
            }
        }
    }
    private static final char       PATTERN_ZERO_DIGIT         = '0';
    private static final char       PATTERN_GROUPING_SEPARATOR = ',';
    private static final char       PATTERN_DECIMAL_SEPARATOR  = '.';
    private static final char       PATTERN_PER_MILLE          = '\u2030';
    private static final char       PATTERN_PERCENT            = '%';
    private static final char       PATTERN_DIGIT              = '#';
    private static final char       PATTERN_SEPARATOR          = ';';
    private static final String     PATTERN_EXPONENT           = "E";
    private static final char       PATTERN_MINUS              = '-';


    private static final char       CURRENCY_SIGN = '\u00A4';

    private static final char       QUOTE = '\'';

    private static FieldPosition[] EmptyFieldPositionArray = new FieldPosition[0];

    static final int DOUBLE_INTEGER_DIGITS  = 309;
    static final int DOUBLE_FRACTION_DIGITS = 340;

    static final int MAXIMUM_INTEGER_DIGITS  = Integer.MAX_VALUE;
    static final int MAXIMUM_FRACTION_DIGITS = Integer.MAX_VALUE;

    static final long serialVersionUID = 864413376551465018L;
}
