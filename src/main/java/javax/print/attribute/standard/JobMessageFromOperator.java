
package javax.print.attribute.standard;

import java.util.Locale;

import javax.print.attribute.Attribute;
import javax.print.attribute.TextSyntax;
import javax.print.attribute.PrintJobAttribute;


public final class JobMessageFromOperator extends TextSyntax
        implements PrintJobAttribute {

    private static final long serialVersionUID = -4620751846003142047L;


    public JobMessageFromOperator(String message, Locale locale) {
        super (message, locale);
    }


    public boolean equals(Object object) {
        return (super.equals (object) &&
                object instanceof JobMessageFromOperator);
    }


    public final Class<? extends Attribute> getCategory() {
        return JobMessageFromOperator.class;
    }


    public final String getName() {
        return "job-message-from-operator";
    }

}
