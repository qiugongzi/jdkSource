



package java.text;

import java.io.InvalidObjectException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.spi.NumberFormatProvider;
import java.util.Currency;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.spi.LocaleServiceProvider;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleServiceProviderPool;


public abstract class NumberFormat extends Format  {


    public static final int INTEGER_FIELD = 0;


    public static final int FRACTION_FIELD = 1;


    protected NumberFormat() {
    }


    @Override
    public StringBuffer format(Object number,
                               StringBuffer toAppendTo,
                               FieldPosition pos) {
        if (number instanceof Long || number instanceof Integer ||
            number instanceof Short || number instanceof Byte ||
            number instanceof AtomicInteger || number instanceof AtomicLong ||
            (number instanceof BigInteger &&
             ((BigInteger)number).bitLength() < 64)) {
            return format(((Number)number).longValue(), toAppendTo, pos);
        } else if (number instanceof Number) {
            return format(((Number)number).doubleValue(), toAppendTo, pos);
        } else {
            throw new IllegalArgumentException("Cannot format given Object as a Number");
        }
    }


    @Override
    public final Object parseObject(String source, ParsePosition pos) {
        return parse(source, pos);
    }


    public final String format(double number) {
        String result = fastFormat(number);
        if (result != null)
            return result;

        return format(number, new StringBuffer(),
                      DontCareFieldPosition.INSTANCE).toString();
    }


    String fastFormat(double number) { return null; }


    public final String format(long number) {
        return format(number, new StringBuffer(),
                      DontCareFieldPosition.INSTANCE).toString();
    }


    public abstract StringBuffer format(double number,
                                        StringBuffer toAppendTo,
                                        FieldPosition pos);


    public abstract StringBuffer format(long number,
                                        StringBuffer toAppendTo,
                                        FieldPosition pos);


    public abstract Number parse(String source, ParsePosition parsePosition);


    public Number parse(String source) throws ParseException {
        ParsePosition parsePosition = new ParsePosition(0);
        Number result = parse(source, parsePosition);
        if (parsePosition.index == 0) {
            throw new ParseException("Unparseable number: \"" + source + "\"",
                                     parsePosition.errorIndex);
        }
        return result;
    }


    public boolean isParseIntegerOnly() {
        return parseIntegerOnly;
    }


    public void setParseIntegerOnly(boolean value) {
        parseIntegerOnly = value;
    }

    public final static NumberFormat getInstance() {
        return getInstance(Locale.getDefault(Locale.Category.FORMAT), NUMBERSTYLE);
    }


    public static NumberFormat getInstance(Locale inLocale) {
        return getInstance(inLocale, NUMBERSTYLE);
    }


    public final static NumberFormat getNumberInstance() {
        return getInstance(Locale.getDefault(Locale.Category.FORMAT), NUMBERSTYLE);
    }


    public static NumberFormat getNumberInstance(Locale inLocale) {
        return getInstance(inLocale, NUMBERSTYLE);
    }


    public final static NumberFormat getIntegerInstance() {
        return getInstance(Locale.getDefault(Locale.Category.FORMAT), INTEGERSTYLE);
    }


    public static NumberFormat getIntegerInstance(Locale inLocale) {
        return getInstance(inLocale, INTEGERSTYLE);
    }


    public final static NumberFormat getCurrencyInstance() {
        return getInstance(Locale.getDefault(Locale.Category.FORMAT), CURRENCYSTYLE);
    }


    public static NumberFormat getCurrencyInstance(Locale inLocale) {
        return getInstance(inLocale, CURRENCYSTYLE);
    }


    public final static NumberFormat getPercentInstance() {
        return getInstance(Locale.getDefault(Locale.Category.FORMAT), PERCENTSTYLE);
    }


    public static NumberFormat getPercentInstance(Locale inLocale) {
        return getInstance(inLocale, PERCENTSTYLE);
    }


     final static NumberFormat getScientificInstance() {
        return getInstance(Locale.getDefault(Locale.Category.FORMAT), SCIENTIFICSTYLE);
    }


     static NumberFormat getScientificInstance(Locale inLocale) {
        return getInstance(inLocale, SCIENTIFICSTYLE);
    }


    public static Locale[] getAvailableLocales() {
        LocaleServiceProviderPool pool =
            LocaleServiceProviderPool.getPool(NumberFormatProvider.class);
        return pool.getAvailableLocales();
    }


    @Override
    public int hashCode() {
        return maximumIntegerDigits * 37 + maxFractionDigits;
        }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NumberFormat other = (NumberFormat) obj;
        return (maximumIntegerDigits == other.maximumIntegerDigits
            && minimumIntegerDigits == other.minimumIntegerDigits
            && maximumFractionDigits == other.maximumFractionDigits
            && minimumFractionDigits == other.minimumFractionDigits
            && groupingUsed == other.groupingUsed
            && parseIntegerOnly == other.parseIntegerOnly);
    }


    @Override
    public Object clone() {
        NumberFormat other = (NumberFormat) super.clone();
        return other;
    }


    public boolean isGroupingUsed() {
        return groupingUsed;
    }


    public void setGroupingUsed(boolean newValue) {
        groupingUsed = newValue;
    }


    public int getMaximumIntegerDigits() {
        return maximumIntegerDigits;
    }


    public void setMaximumIntegerDigits(int newValue) {
        maximumIntegerDigits = Math.max(0,newValue);
        if (minimumIntegerDigits > maximumIntegerDigits) {
            minimumIntegerDigits = maximumIntegerDigits;
        }
    }


    public int getMinimumIntegerDigits() {
        return minimumIntegerDigits;
    }


    public void setMinimumIntegerDigits(int newValue) {
        minimumIntegerDigits = Math.max(0,newValue);
        if (minimumIntegerDigits > maximumIntegerDigits) {
            maximumIntegerDigits = minimumIntegerDigits;
        }
    }


    public int getMaximumFractionDigits() {
        return maximumFractionDigits;
    }


    public void setMaximumFractionDigits(int newValue) {
        maximumFractionDigits = Math.max(0,newValue);
        if (maximumFractionDigits < minimumFractionDigits) {
            minimumFractionDigits = maximumFractionDigits;
        }
    }


    public int getMinimumFractionDigits() {
        return minimumFractionDigits;
    }


    public void setMinimumFractionDigits(int newValue) {
        minimumFractionDigits = Math.max(0,newValue);
        if (maximumFractionDigits < minimumFractionDigits) {
            maximumFractionDigits = minimumFractionDigits;
        }
    }


    public Currency getCurrency() {
        throw new UnsupportedOperationException();
    }


    public void setCurrency(Currency currency) {
        throw new UnsupportedOperationException();
    }


    public RoundingMode getRoundingMode() {
        throw new UnsupportedOperationException();
    }


    public void setRoundingMode(RoundingMode roundingMode) {
        throw new UnsupportedOperationException();
    }

    private static NumberFormat getInstance(Locale desiredLocale,
                                           int choice) {
        LocaleProviderAdapter adapter;
        adapter = LocaleProviderAdapter.getAdapter(NumberFormatProvider.class,
                                                   desiredLocale);
        NumberFormat numberFormat = getInstance(adapter, desiredLocale, choice);
        if (numberFormat == null) {
            numberFormat = getInstance(LocaleProviderAdapter.forJRE(),
                                       desiredLocale, choice);
        }
        return numberFormat;
    }

    private static NumberFormat getInstance(LocaleProviderAdapter adapter,
                                            Locale locale, int choice) {
        NumberFormatProvider provider = adapter.getNumberFormatProvider();
        NumberFormat numberFormat = null;
        switch (choice) {
        case NUMBERSTYLE:
            numberFormat = provider.getNumberInstance(locale);
            break;
        case PERCENTSTYLE:
            numberFormat = provider.getPercentInstance(locale);
            break;
        case CURRENCYSTYLE:
            numberFormat = provider.getCurrencyInstance(locale);
            break;
        case INTEGERSTYLE:
            numberFormat = provider.getIntegerInstance(locale);
            break;
        }
        return numberFormat;
    }


    private void readObject(ObjectInputStream stream)
         throws IOException, ClassNotFoundException
    {
        stream.defaultReadObject();
        if (serialVersionOnStream < 1) {
            maximumIntegerDigits = maxIntegerDigits;
            minimumIntegerDigits = minIntegerDigits;
            maximumFractionDigits = maxFractionDigits;
            minimumFractionDigits = minFractionDigits;
        }
        if (minimumIntegerDigits > maximumIntegerDigits ||
            minimumFractionDigits > maximumFractionDigits ||
            minimumIntegerDigits < 0 || minimumFractionDigits < 0) {
            throw new InvalidObjectException("Digit count range invalid");
        }
        serialVersionOnStream = currentSerialVersion;
    }


    private void writeObject(ObjectOutputStream stream)
         throws IOException
    {
        maxIntegerDigits = (maximumIntegerDigits > Byte.MAX_VALUE) ?
                           Byte.MAX_VALUE : (byte)maximumIntegerDigits;
        minIntegerDigits = (minimumIntegerDigits > Byte.MAX_VALUE) ?
                           Byte.MAX_VALUE : (byte)minimumIntegerDigits;
        maxFractionDigits = (maximumFractionDigits > Byte.MAX_VALUE) ?
                            Byte.MAX_VALUE : (byte)maximumFractionDigits;
        minFractionDigits = (minimumFractionDigits > Byte.MAX_VALUE) ?
                            Byte.MAX_VALUE : (byte)minimumFractionDigits;
        stream.defaultWriteObject();
    }

    private static final int NUMBERSTYLE = 0;
    private static final int CURRENCYSTYLE = 1;
    private static final int PERCENTSTYLE = 2;
    private static final int SCIENTIFICSTYLE = 3;
    private static final int INTEGERSTYLE = 4;


    private boolean groupingUsed = true;


    private byte    maxIntegerDigits = 40;


    private byte    minIntegerDigits = 1;


    private byte    maxFractionDigits = 3;    private byte    minFractionDigits = 0;


    private boolean parseIntegerOnly = false;

    private int    maximumIntegerDigits = 40;


    private int    minimumIntegerDigits = 1;


    private int    maximumFractionDigits = 3;    private int    minimumFractionDigits = 0;

    static final int currentSerialVersion = 1;


    private int serialVersionOnStream = currentSerialVersion;

    static final long serialVersionUID = -2308460125733713944L;


    public static class Field extends Format.Field {

        private static final long serialVersionUID = 7494728892700160890L;

        private static final Map<String, Field> instanceMap = new HashMap<>(11);


        protected Field(String name) {
            super(name);
            if (this.getClass() == NumberFormat.Field.class) {
                instanceMap.put(name, this);
            }
        }


        @Override
        protected Object readResolve() throws InvalidObjectException {
            if (this.getClass() != NumberFormat.Field.class) {
                throw new InvalidObjectException("subclass didn't correctly implement readResolve");
            }

            Object instance = instanceMap.get(getName());
            if (instance != null) {
                return instance;
            } else {
                throw new InvalidObjectException("unknown attribute name");
            }
        }


        public static final Field INTEGER = new Field("integer");


        public static final Field FRACTION = new Field("fraction");


        public static final Field EXPONENT = new Field("exponent");


        public static final Field DECIMAL_SEPARATOR =
                            new Field("decimal separator");


        public static final Field SIGN = new Field("sign");


        public static final Field GROUPING_SEPARATOR =
                            new Field("grouping separator");


        public static final Field EXPONENT_SYMBOL = new
                            Field("exponent symbol");


        public static final Field PERCENT = new Field("percent");


        public static final Field PERMILLE = new Field("per mille");


        public static final Field CURRENCY = new Field("currency");


        public static final Field EXPONENT_SIGN = new Field("exponent sign");
    }
}
