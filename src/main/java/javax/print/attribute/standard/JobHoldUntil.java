
package javax.print.attribute.standard;

import java.util.Date;
import javax.print.attribute.Attribute;
import javax.print.attribute.DateTimeSyntax;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintJobAttribute;


public final class JobHoldUntil extends DateTimeSyntax
        implements PrintRequestAttribute, PrintJobAttribute {

    private static final long serialVersionUID = -1664471048860415024L;



    public JobHoldUntil(Date dateTime) {
        super (dateTime);
    }


    public boolean equals(Object object) {
        return (super.equals(object) && object instanceof JobHoldUntil);
    }



    public final Class<? extends Attribute> getCategory() {
        return JobHoldUntil.class;
    }


    public final String getName() {
        return "job-hold-until";
    }

}
