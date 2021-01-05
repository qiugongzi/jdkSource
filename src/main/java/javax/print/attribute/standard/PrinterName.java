
package javax.print.attribute.standard;

import java.util.Locale;

import javax.print.attribute.Attribute;
import javax.print.attribute.TextSyntax;
import javax.print.attribute.PrintServiceAttribute;


public final class PrinterName extends TextSyntax
        implements PrintServiceAttribute {

    private static final long serialVersionUID = 299740639137803127L;


    public PrinterName(String printerName, Locale locale) {
        super (printerName, locale);
    }


    public boolean equals(Object object) {
        return (super.equals(object) && object instanceof PrinterName);
    }


    public final Class<? extends Attribute> getCategory() {
        return PrinterName.class;
    }


    public final String getName() {
        return "printer-name";
    }

}
