
package javax.print.attribute.standard;

import java.util.Locale;

import javax.print.attribute.Attribute;
import javax.print.attribute.TextSyntax;
import javax.print.attribute.DocAttribute;


public final class DocumentName extends TextSyntax implements DocAttribute {

    private static final long serialVersionUID = 7883105848533280430L;


    public DocumentName(String documentName, Locale locale) {
        super (documentName, locale);
    }


    public boolean equals(Object object) {
        return (super.equals (object) && object instanceof DocumentName);
    }


    public final Class<? extends Attribute> getCategory() {
        return DocumentName.class;
    }


    public final String getName() {
        return "document-name";
    }

}
