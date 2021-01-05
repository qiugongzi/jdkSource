
package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintJobAttribute;


public final class SheetCollate extends EnumSyntax
    implements DocAttribute, PrintRequestAttribute, PrintJobAttribute {

    private static final long serialVersionUID = 7080587914259873003L;


    public static final SheetCollate UNCOLLATED = new SheetCollate(0);


    public static final SheetCollate COLLATED = new SheetCollate(1);


    protected SheetCollate(int value) {
        super (value);
    }

    private static final String[] myStringTable = {
        "uncollated",
        "collated"
    };

    private static final SheetCollate[] myEnumValueTable = {
        UNCOLLATED,
        COLLATED
    };


    protected String[] getStringTable() {
        return myStringTable;
    }


    protected EnumSyntax[] getEnumValueTable() {
        return myEnumValueTable;
    }


    public final Class<? extends Attribute> getCategory() {
        return SheetCollate.class;
    }


    public final String getName() {
        return "sheet-collate";
    }

}
