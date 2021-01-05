



package java.text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleServiceProviderPool;
import sun.util.locale.provider.ResourceBundleBasedAdapter;



public class DecimalFormatSymbols implements Cloneable, Serializable {


    public DecimalFormatSymbols() {
        initialize( Locale.getDefault(Locale.Category.FORMAT) );
    }


    public DecimalFormatSymbols( Locale locale ) {
        initialize( locale );
    }


    public static Locale[] getAvailableLocales() {
        LocaleServiceProviderPool pool =
            LocaleServiceProviderPool.getPool(DecimalFormatSymbolsProvider.class);
        return pool.getAvailableLocales();
    }


    public static final DecimalFormatSymbols getInstance() {
        return getInstance(Locale.getDefault(Locale.Category.FORMAT));
    }


    public static final DecimalFormatSymbols getInstance(Locale locale) {
        LocaleProviderAdapter adapter;
        adapter = LocaleProviderAdapter.getAdapter(DecimalFormatSymbolsProvider.class, locale);
        DecimalFormatSymbolsProvider provider = adapter.getDecimalFormatSymbolsProvider();
        DecimalFormatSymbols dfsyms = provider.getInstance(locale);
        if (dfsyms == null) {
            provider = LocaleProviderAdapter.forJRE().getDecimalFormatSymbolsProvider();
            dfsyms = provider.getInstance(locale);
        }
        return dfsyms;
    }


    public char getZeroDigit() {
        return zeroDigit;
    }


    public void setZeroDigit(char zeroDigit) {
        this.zeroDigit = zeroDigit;
    }


    public char getGroupingSeparator() {
        return groupingSeparator;
    }


    public void setGroupingSeparator(char groupingSeparator) {
        this.groupingSeparator = groupingSeparator;
    }


    public char getDecimalSeparator() {
        return decimalSeparator;
    }


    public void setDecimalSeparator(char decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }


    public char getPerMill() {
        return perMill;
    }


    public void setPerMill(char perMill) {
        this.perMill = perMill;
    }


    public char getPercent() {
        return percent;
    }


    public void setPercent(char percent) {
        this.percent = percent;
    }


    public char getDigit() {
        return digit;
    }


    public void setDigit(char digit) {
        this.digit = digit;
    }


    public char getPatternSeparator() {
        return patternSeparator;
    }


    public void setPatternSeparator(char patternSeparator) {
        this.patternSeparator = patternSeparator;
    }


    public String getInfinity() {
        return infinity;
    }


    public void setInfinity(String infinity) {
        this.infinity = infinity;
    }


    public String getNaN() {
        return NaN;
    }


    public void setNaN(String NaN) {
        this.NaN = NaN;
    }


    public char getMinusSign() {
        return minusSign;
    }


    public void setMinusSign(char minusSign) {
        this.minusSign = minusSign;
    }


    public String getCurrencySymbol()
    {
        return currencySymbol;
    }


    public void setCurrencySymbol(String currency)
    {
        currencySymbol = currency;
    }


    public String getInternationalCurrencySymbol()
    {
        return intlCurrencySymbol;
    }


    public void setInternationalCurrencySymbol(String currencyCode)
    {
        intlCurrencySymbol = currencyCode;
        currency = null;
        if (currencyCode != null) {
            try {
                currency = Currency.getInstance(currencyCode);
                currencySymbol = currency.getSymbol();
            } catch (IllegalArgumentException e) {
            }
        }
    }


    public Currency getCurrency() {
        return currency;
    }


    public void setCurrency(Currency currency) {
        if (currency == null) {
            throw new NullPointerException();
        }
        this.currency = currency;
        intlCurrencySymbol = currency.getCurrencyCode();
        currencySymbol = currency.getSymbol(locale);
    }



    public char getMonetaryDecimalSeparator()
    {
        return monetarySeparator;
    }


    public void setMonetaryDecimalSeparator(char sep)
    {
        monetarySeparator = sep;
    }

    char getExponentialSymbol()
    {
        return exponential;
    }

    public String getExponentSeparator()
    {
        return exponentialSeparator;
    }


    void setExponentialSymbol(char exp)
    {
        exponential = exp;
    }


    public void setExponentSeparator(String exp)
    {
        if (exp == null) {
            throw new NullPointerException();
        }
        exponentialSeparator = exp;
     }


    @Override
    public Object clone() {
        try {
            return (DecimalFormatSymbols)super.clone();
            } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (getClass() != obj.getClass()) return false;
        DecimalFormatSymbols other = (DecimalFormatSymbols) obj;
        return (zeroDigit == other.zeroDigit &&
        groupingSeparator == other.groupingSeparator &&
        decimalSeparator == other.decimalSeparator &&
        percent == other.percent &&
        perMill == other.perMill &&
        digit == other.digit &&
        minusSign == other.minusSign &&
        patternSeparator == other.patternSeparator &&
        infinity.equals(other.infinity) &&
        NaN.equals(other.NaN) &&
        currencySymbol.equals(other.currencySymbol) &&
        intlCurrencySymbol.equals(other.intlCurrencySymbol) &&
        currency == other.currency &&
        monetarySeparator == other.monetarySeparator &&
        exponentialSeparator.equals(other.exponentialSeparator) &&
        locale.equals(other.locale));
    }


    @Override
    public int hashCode() {
            int result = zeroDigit;
            result = result * 37 + groupingSeparator;
            result = result * 37 + decimalSeparator;
            return result;
    }


    private void initialize( Locale locale ) {
        this.locale = locale;

        LocaleProviderAdapter adapter = LocaleProviderAdapter.getAdapter(DecimalFormatSymbolsProvider.class, locale);
        if (!(adapter instanceof ResourceBundleBasedAdapter)) {
            adapter = LocaleProviderAdapter.getResourceBundleBased();
        }
        Object[] data = adapter.getLocaleResources(locale).getDecimalFormatSymbolsData();
        String[] numberElements = (String[]) data[0];

        decimalSeparator = numberElements[0].charAt(0);
        groupingSeparator = numberElements[1].charAt(0);
        patternSeparator = numberElements[2].charAt(0);
        percent = numberElements[3].charAt(0);
        zeroDigit = numberElements[4].charAt(0); digit = numberElements[5].charAt(0);
        minusSign = numberElements[6].charAt(0);
        exponential = numberElements[7].charAt(0);
        exponentialSeparator = numberElements[7]; perMill = numberElements[8].charAt(0);
        infinity  = numberElements[9];
        NaN = numberElements[10];

        if (locale.getCountry().length() > 0) {
            try {
                currency = Currency.getInstance(locale);
            } catch (IllegalArgumentException e) {
                }
        }
        if (currency != null) {
            intlCurrencySymbol = currency.getCurrencyCode();
            if (data[1] != null && data[1] == intlCurrencySymbol) {
                currencySymbol = (String) data[2];
            } else {
                currencySymbol = currency.getSymbol(locale);
                data[1] = intlCurrencySymbol;
                data[2] = currencySymbol;
            }
        } else {
            intlCurrencySymbol = "XXX";
            try {
                currency = Currency.getInstance(intlCurrencySymbol);
            } catch (IllegalArgumentException e) {
            }
            currencySymbol = "\u00A4";
        }
        monetarySeparator = decimalSeparator;
    }


    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        if (serialVersionOnStream < 1) {
            monetarySeparator = decimalSeparator;
            exponential       = 'E';
        }
        if (serialVersionOnStream < 2) {
            locale = Locale.ROOT;
        }
        if (serialVersionOnStream < 3) {
            exponentialSeparator = Character.toString(exponential);
        }
        serialVersionOnStream = currentSerialVersion;

        if (intlCurrencySymbol != null) {
            try {
                 currency = Currency.getInstance(intlCurrencySymbol);
            } catch (IllegalArgumentException e) {
            }
        }
    }


    private  char    zeroDigit;


    private  char    groupingSeparator;


    private  char    decimalSeparator;


    private  char    perMill;


    private  char    percent;


    private  char    digit;


    private  char    patternSeparator;


    private  String  infinity;


    private  String  NaN;


    private  char    minusSign;


    private  String  currencySymbol;


    private  String  intlCurrencySymbol;


    private  char    monetarySeparator; private  char    exponential;       private  String    exponentialSeparator;       private Locale locale;

    private transient Currency currency;

    static final long serialVersionUID = 5772796243397350300L;

    private static final int currentSerialVersion = 3;


    private int serialVersionOnStream = currentSerialVersion;
}
