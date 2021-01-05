
package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintJobAttribute;


public abstract class Media extends EnumSyntax
    implements DocAttribute, PrintRequestAttribute, PrintJobAttribute {

    private static final long serialVersionUID = -2823970704630722439L;


    protected Media(int value) {
           super (value);
    }


    public boolean equals(Object object) {
        return(object != null && object instanceof Media &&
               object.getClass() == this.getClass() &&
               ((Media)object).getValue() == this.getValue());
    }


    public final Class<? extends Attribute> getCategory() {
        return Media.class;
    }


    public final String getName() {
        return "media";
    }

}
