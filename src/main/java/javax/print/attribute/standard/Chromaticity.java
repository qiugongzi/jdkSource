

package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintJobAttribute;


public final class Chromaticity extends EnumSyntax
    implements DocAttribute, PrintRequestAttribute, PrintJobAttribute {

    private static final long serialVersionUID = 4660543931355214012L;


    public static final Chromaticity MONOCHROME = new Chromaticity(0);


    public static final Chromaticity COLOR = new Chromaticity(1);



    protected Chromaticity(int value) {
        super(value);
    }

    private static final String[] myStringTable = {"monochrome",
                                                   "color"};

    private static final Chromaticity[] myEnumValueTable = {MONOCHROME,
                                                            COLOR};


    protected String[] getStringTable() {
        return myStringTable;
    }


    protected EnumSyntax[] getEnumValueTable() {
        return myEnumValueTable;
    }


    public final Class<? extends Attribute> getCategory() {
        return Chromaticity.class;
    }


        public final String getName() {
            return "chromaticity";
        }

        }
