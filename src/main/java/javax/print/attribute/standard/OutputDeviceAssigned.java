
package javax.print.attribute.standard;

import java.util.Locale;

import javax.print.attribute.Attribute;
import javax.print.attribute.TextSyntax;
import javax.print.attribute.PrintJobAttribute;


public final class OutputDeviceAssigned extends TextSyntax
    implements PrintJobAttribute {

    private static final long serialVersionUID = 5486733778854271081L;


    public OutputDeviceAssigned(String deviceName, Locale locale) {

        super (deviceName, locale);
    }

    public boolean equals(Object object) {
        return (super.equals (object) &&
                object instanceof OutputDeviceAssigned);
    }


    public final Class<? extends Attribute> getCategory() {
        return OutputDeviceAssigned.class;
    }


    public final String getName() {
        return "output-device-assigned";
    }

}
