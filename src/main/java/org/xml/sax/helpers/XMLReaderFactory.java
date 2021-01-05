

package org.xml.sax.helpers;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;



final public class XMLReaderFactory
{

    private XMLReaderFactory ()
    {
    }

    private static final String property = "org.xml.sax.driver";
    private static SecuritySupport ss = new SecuritySupport();

    private static String _clsFromJar = null;
    private static boolean _jarread = false;

    public static XMLReader createXMLReader ()
        throws SAXException
    {
        String          className = null;
        ClassLoader     cl = ss.getContextClassLoader();

        try {
            className = ss.getSystemProperty(property);
        }
        catch (RuntimeException e) {  }

        if (className == null) {
            if (!_jarread) {
                _jarread = true;
                String      service = "META-INF/services/" + property;
                InputStream in;
                BufferedReader      reader;

                try {
                    if (cl != null) {
                        in = ss.getResourceAsStream(cl, service);

                        if (in == null) {
                            cl = null;
                            in = ss.getResourceAsStream(cl, service);
                        }
                    } else {
                        in = ss.getResourceAsStream(cl, service);
                    }

                    if (in != null) {
                        reader = new BufferedReader (new InputStreamReader (in, "UTF8"));
                        _clsFromJar = reader.readLine ();
                        in.close ();
                    }
                } catch (Exception e) {
                }
            }
            className = _clsFromJar;
        }

        if (className == null) {
public static XMLReader createXMLReader (String className)
        throws SAXException
    {
        return loadClass (ss.getContextClassLoader(), className);
    }

    private static XMLReader loadClass (ClassLoader loader, String className)
    throws SAXException
    {
        try {
            return (XMLReader) NewInstance.newInstance (loader, className);
        } catch (ClassNotFoundException e1) {
            throw new SAXException("SAX2 driver class " + className +
                                   " not found", e1);
        } catch (IllegalAccessException e2) {
            throw new SAXException("SAX2 driver class " + className +
                                   " found but cannot be loaded", e2);
        } catch (InstantiationException e3) {
            throw new SAXException("SAX2 driver class " + className +
           " loaded but cannot be instantiated (no empty public constructor?)",
                                   e3);
        } catch (ClassCastException e4) {
            throw new SAXException("SAX2 driver class " + className +
                                   " does not implement XMLReader", e4);
        }
    }
}
