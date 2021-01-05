
package javax.print.attribute.standard;

import java.util.Locale;

import javax.print.attribute.Attribute;
import javax.print.attribute.TextSyntax;
import javax.print.attribute.PrintServiceAttribute;


public final class PrinterMessageFromOperator   extends TextSyntax
    implements PrintServiceAttribute {

    static final long serialVersionUID = -4486871203218629318L;


    public PrinterMessageFromOperator(String message, Locale locale) {
        super (message, locale);
    }


    public boolean equals(Object object) {
        return (super.equals(object) &&
                object instanceof PrinterMessageFromOperator);
    }


    public final Class<? extends Attribute> getCategory() {
        return PrinterMessageFromOperator.class;
    }


    public final String getName() {
        return "printer-message-from-operator";
    }

}
