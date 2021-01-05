

package javax.print;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.StringReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.DocAttributeSet;



public final class SimpleDoc implements Doc {

    private DocFlavor flavor;
    private DocAttributeSet attributes;
    private Object printData;
    private Reader reader;
    private InputStream inStream;


    public SimpleDoc(Object printData,
                     DocFlavor flavor, DocAttributeSet attributes) {

       if (flavor == null || printData == null) {
           throw new IllegalArgumentException("null argument(s)");
       }

       Class repClass = null;
       try {
            String className = flavor.getRepresentationClassName();
            sun.reflect.misc.ReflectUtil.checkPackageAccess(className);
            repClass = Class.forName(className, false,
                              Thread.currentThread().getContextClassLoader());
       } catch (Throwable e) {
           throw new IllegalArgumentException("unknown representation class");
       }

       if (!repClass.isInstance(printData)) {
           throw new IllegalArgumentException("data is not of declared type");
       }

       this.flavor = flavor;
       if (attributes != null) {
           this.attributes = AttributeSetUtilities.unmodifiableView(attributes);
       }
       this.printData = printData;
    }


    public DocFlavor getDocFlavor() {
        return flavor;
    }


    public DocAttributeSet getAttributes() {
        return attributes;
    }


    public Object getPrintData() throws IOException {
        return printData;
    }


    public Reader getReaderForText() throws IOException {

        if (printData instanceof Reader) {
            return (Reader)printData;
        }

        synchronized (this) {
            if (reader != null) {
                return reader;
            }

            if (printData instanceof char[]) {
               reader = new CharArrayReader((char[])printData);
            }
            else if (printData instanceof String) {
                reader = new StringReader((String)printData);
            }
        }
        return reader;
    }


    public InputStream getStreamForBytes() throws IOException {

        if (printData instanceof InputStream) {
            return (InputStream)printData;
        }

        synchronized (this) {
            if (inStream != null) {
                return inStream;
            }

            if (printData instanceof byte[]) {
               inStream = new ByteArrayInputStream((byte[])printData);
            }
        }
        return inStream;
    }

}
