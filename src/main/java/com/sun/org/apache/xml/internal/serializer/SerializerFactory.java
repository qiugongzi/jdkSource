


package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xml.internal.serializer.utils.MsgKey;
import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import java.util.Properties;
import javax.xml.transform.OutputKeys;
import org.xml.sax.ContentHandler;


public final class SerializerFactory
{


  private SerializerFactory() {

  }


  public static Serializer getSerializer(Properties format)
  {
      Serializer ser;

      try
      {
        String method = format.getProperty(OutputKeys.METHOD);

        if (method == null) {
            String msg = Utils.messages.createMessage(
                MsgKey.ER_FACTORY_PROPERTY_MISSING,
                new Object[] { OutputKeys.METHOD});
            throw new IllegalArgumentException(msg);
        }

        String className =
            format.getProperty(OutputPropertiesFactory.S_KEY_CONTENT_HANDLER);


        if (null == className)
        {
            Properties methodDefaults =
                OutputPropertiesFactory.getDefaultMethodProperties(method);
            className =
            methodDefaults.getProperty(OutputPropertiesFactory.S_KEY_CONTENT_HANDLER);
            if (null == className) {
                String msg = Utils.messages.createMessage(
                    MsgKey.ER_FACTORY_PROPERTY_MISSING,
                    new Object[] { OutputPropertiesFactory.S_KEY_CONTENT_HANDLER});
                throw new IllegalArgumentException(msg);
            }

        }



        Class cls = ObjectFactory.findProviderClass(className, true);

        Object obj = cls.newInstance();

        if (obj instanceof SerializationHandler)
        {
              ser = (Serializer) cls.newInstance();
            ser.setOutputFormat(format);
        }
        else
        {

               if (obj instanceof ContentHandler)
               {


                  className = SerializerConstants.DEFAULT_SAX_SERIALIZER;
                  cls = ObjectFactory.findProviderClass(className, true);
                  SerializationHandler sh =
                      (SerializationHandler) cls.newInstance();
                  sh.setContentHandler( (ContentHandler) obj);
                  sh.setOutputFormat(format);

                  ser = sh;
               }
               else
               {
                  throw new Exception(
                       Utils.messages.createMessage(
                           MsgKey.ER_SERIALIZER_NOT_CONTENTHANDLER,
                               new Object[] { className}));
               }

        }
      }
      catch (Exception e)
      {
        throw new com.sun.org.apache.xml.internal.serializer.utils.WrappedRuntimeException(e);
      }

      return ser;
  }
}
