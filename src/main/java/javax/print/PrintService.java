

package javax.print;

import java.util.Locale;

import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.event.PrintServiceAttributeListener;



public interface PrintService {


    public String getName();


    public DocPrintJob createPrintJob();


    public void addPrintServiceAttributeListener(
                                       PrintServiceAttributeListener listener);


    public void removePrintServiceAttributeListener(
                                       PrintServiceAttributeListener listener);


    public PrintServiceAttributeSet getAttributes();


    public <T extends PrintServiceAttribute>
        T getAttribute(Class<T> category);


    public DocFlavor[] getSupportedDocFlavors();


    public boolean isDocFlavorSupported(DocFlavor flavor);



    public Class<?>[] getSupportedAttributeCategories();


    public boolean
        isAttributeCategorySupported(Class<? extends Attribute> category);


    public Object
        getDefaultAttributeValue(Class<? extends Attribute> category);


    public Object
        getSupportedAttributeValues(Class<? extends Attribute> category,
                                    DocFlavor flavor,
                                    AttributeSet attributes);


    public boolean isAttributeValueSupported(Attribute attrval,
                                             DocFlavor flavor,
                                             AttributeSet attributes);



    public AttributeSet getUnsupportedAttributes(DocFlavor flavor,
                                           AttributeSet attributes);


    public ServiceUIFactory getServiceUIFactory();


    public boolean equals(Object obj);


    public int hashCode();

}
