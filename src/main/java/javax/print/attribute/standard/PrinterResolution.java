
package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.ResolutionSyntax;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintJobAttribute;


public final class PrinterResolution    extends ResolutionSyntax
        implements DocAttribute, PrintRequestAttribute, PrintJobAttribute {

    private static final long serialVersionUID = 13090306561090558L;


    public PrinterResolution(int crossFeedResolution, int feedResolution,
                             int units) {
        super (crossFeedResolution, feedResolution, units);
    }


    public boolean equals(Object object) {
        return (super.equals (object) &&
                object instanceof PrinterResolution);
    }


    public final Class<? extends Attribute> getCategory() {
        return PrinterResolution.class;
                }


    public final String getName() {
        return "printer-resolution";
    }

}
