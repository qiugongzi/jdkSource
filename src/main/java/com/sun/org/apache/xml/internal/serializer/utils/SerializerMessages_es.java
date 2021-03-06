

package com.sun.org.apache.xml.internal.serializer.utils;

import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


public class SerializerMessages_es extends ListResourceBundle {




    public Object[][] getContents() {
        Object[][] contents = new Object[][] {
            {   MsgKey.BAD_MSGKEY,
                "La clave de mensaje ''{0}'' no est\u00E1 en la clase de mensaje ''{1}''" },

            {   MsgKey.BAD_MSGFORMAT,
                "Fallo de formato del mensaje ''{0}'' en la clase de mensaje ''{1}''." },

            {   MsgKey.ER_SERIALIZER_NOT_CONTENTHANDLER,
                "La clase de serializador ''{0}'' no implanta org.xml.sax.ContentHandler." },

            {   MsgKey.ER_RESOURCE_COULD_NOT_FIND,
                    "No se ha encontrado el recurso [ {0} ].\n {1}" },

            {   MsgKey.ER_RESOURCE_COULD_NOT_LOAD,
                    "No se ha podido cargar el recurso [ {0} ]: {1} \n {2} \t {3}" },

            {   MsgKey.ER_BUFFER_SIZE_LESSTHAN_ZERO,
                    "Tama\u00F1o de buffer menor o igual que 0" },

            {   MsgKey.ER_INVALID_UTF16_SURROGATE,
                    "\u00BFSe ha detectado un sustituto UTF-16 no v\u00E1lido: {0}?" },

            {   MsgKey.ER_OIERROR,
                "Error de E/S" },

            {   MsgKey.ER_ILLEGAL_ATTRIBUTE_POSITION,
                "No se puede agregar el atributo {0} despu\u00E9s de nodos secundarios o antes de que se produzca un elemento. Se ignorar\u00E1 el atributo." },


            {   MsgKey.ER_NAMESPACE_PREFIX,
                "No se ha declarado el espacio de nombres para el prefijo ''{0}''." },


            {   MsgKey.ER_STRAY_ATTRIBUTE,
                "El atributo ''{0}'' est\u00E1 fuera del elemento." },


            {   MsgKey.ER_STRAY_NAMESPACE,
                "Declaraci\u00F3n del espacio de nombres ''{0}''=''{1}'' fuera del elemento." },

            {   MsgKey.ER_COULD_NOT_LOAD_RESOURCE,
                "No se ha podido cargar ''{0}'' (compruebe la CLASSPATH), ahora s\u00F3lo se est\u00E1n utilizando los valores por defecto" },

            {   MsgKey.ER_ILLEGAL_CHARACTER,
                "Intento de realizar la salida del car\u00E1cter del valor integral {0}, que no est\u00E1 representado en la codificaci\u00F3n de salida de {1}." },

            {   MsgKey.ER_COULD_NOT_LOAD_METHOD_PROPERTY,
                "No se ha podido cargar el archivo de propiedades ''{0}'' para el m\u00E9todo de salida ''{1}'' (compruebe la CLASSPATH)" },

            {   MsgKey.ER_INVALID_PORT,
                "N\u00FAmero de puerto no v\u00E1lido" },

            {   MsgKey.ER_PORT_WHEN_HOST_NULL,
                "No se puede definir el puerto si el host es nulo" },

            {   MsgKey.ER_HOST_ADDRESS_NOT_WELLFORMED,
                "El formato de la direcci\u00F3n de host no es correcto" },

            {   MsgKey.ER_SCHEME_NOT_CONFORMANT,
                "El esquema no es v\u00E1lido." },

            {   MsgKey.ER_SCHEME_FROM_NULL_STRING,
                "No se puede definir un esquema a partir de una cadena nula" },

            {   MsgKey.ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE,
                "La ruta de acceso contiene una secuencia de escape no v\u00E1lida" },

            {   MsgKey.ER_PATH_INVALID_CHAR,
                "La ruta de acceso contiene un car\u00E1cter no v\u00E1lido: {0}" },

            {   MsgKey.ER_FRAG_INVALID_CHAR,
                "El fragmento contiene un car\u00E1cter no v\u00E1lido" },

            {   MsgKey.ER_FRAG_WHEN_PATH_NULL,
                "No se puede definir el fragmento si la ruta de acceso es nula" },

            {   MsgKey.ER_FRAG_FOR_GENERIC_URI,
                "S\u00F3lo se puede definir el fragmento para un URI gen\u00E9rico" },

            {   MsgKey.ER_NO_SCHEME_IN_URI,
                "No se ha encontrado un esquema en el URI" },

            {   MsgKey.ER_CANNOT_INIT_URI_EMPTY_PARMS,
                "No se puede inicializar el URI con par\u00E1metros vac\u00EDos" },

            {   MsgKey.ER_NO_FRAGMENT_STRING_IN_PATH,
                "No se puede especificar el fragmento en la ruta de acceso y en el fragmento" },

            {   MsgKey.ER_NO_QUERY_STRING_IN_PATH,
                "No se puede especificar la cadena de consulta en la ruta de acceso y en la cadena de consulta" },

            {   MsgKey.ER_NO_PORT_IF_NO_HOST,
                "No se puede especificar el puerto si no se ha especificado el host" },

            {   MsgKey.ER_NO_USERINFO_IF_NO_HOST,
                "No se puede especificar la informaci\u00F3n de usuario si no se ha especificado el host" },

            {   MsgKey.ER_XML_VERSION_NOT_SUPPORTED,
                "Advertencia: es necesario que la versi\u00F3n del documento de salida sea ''{0}''. Esta versi\u00F3n de XML no est\u00E1 soportada. La versi\u00F3n del documento de salida ser\u00E1 ''1.0''." },

            {   MsgKey.ER_SCHEME_REQUIRED,
                "Se necesita un esquema." },


            {   MsgKey.ER_FACTORY_PROPERTY_MISSING,
                "El objeto de propiedades transferido a SerializerFactory no tiene una propiedad ''{0}''." },

            {   MsgKey.ER_ENCODING_NOT_SUPPORTED,
                "Advertencia: el tiempo de ejecuci\u00F3n de Java no soporta la codificaci\u00F3n ''{0}''." },


        };

        return contents;
    }
}
