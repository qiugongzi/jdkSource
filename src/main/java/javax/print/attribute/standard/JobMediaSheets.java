
package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintJobAttribute;


public class JobMediaSheets extends IntegerSyntax
        implements PrintRequestAttribute, PrintJobAttribute {


    private static final long serialVersionUID = 408871131531979741L;


    public JobMediaSheets(int value) {
        super (value, 0, Integer.MAX_VALUE);
    }


    public boolean equals(Object object) {
        return super.equals(object) && object instanceof JobMediaSheets;
    }


    public final Class<? extends Attribute> getCategory() {
        return JobMediaSheets.class;
    }


    public final String getName() {
        return "job-media-sheets";
    }

}
