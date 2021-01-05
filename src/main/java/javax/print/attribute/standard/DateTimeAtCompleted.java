
package javax.print.attribute.standard;

import java.util.Date;
import javax.print.attribute.Attribute;
import javax.print.attribute.DateTimeSyntax;
import javax.print.attribute.PrintJobAttribute;


public final class DateTimeAtCompleted extends DateTimeSyntax
    implements PrintJobAttribute {

    private static final long serialVersionUID = 6497399708058490000L;


    public DateTimeAtCompleted(Date dateTime) {
        super (dateTime);
    }


    public boolean equals(Object object) {
        return(super.equals (object) &&
               object instanceof DateTimeAtCompleted);
    }

public final Class<? extends Attribute> getCategory() {
        return DateTimeAtCompleted.class;
    }


    public final String getName() {
        return "date-time-at-completed";
    }

}
