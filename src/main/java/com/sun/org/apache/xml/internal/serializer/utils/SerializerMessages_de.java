

package com.sun.org.apache.xml.internal.serializer.utils;

import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


public class SerializerMessages_de extends ListResourceBundle {




    public Object[][] getContents() {
        Object[][] contents = new Object[][] {
            {   MsgKey.BAD_MSGKEY,
                "Der Nachrichtenschl\u00FCssel \"{0}\" ist nicht in der Nachrichtenklasse \"{1}\" enthalten" },

            {   MsgKey.BAD_MSGFORMAT,
                "Das Format der Nachricht \"{0}\" in der Nachrichtenklasse \"{1}\" war nicht erfolgreich." },

            {   MsgKey.ER_SERIALIZER_NOT_CONTENTHANDLER,
                "Serializer-Klasse \"{0}\" implementiert org.xml.sax.ContentHandler nicht." },

            {   MsgKey.ER_RESOURCE_COULD_NOT_FIND,
                    "Ressource [ {0} ] konnte nicht gefunden werden.\n {1}" },

            {   MsgKey.ER_RESOURCE_COULD_NOT_LOAD,
                    "Ressource [ {0} ] konnte nicht geladen werden: {1} \n {2} \t {3}" },

            {   MsgKey.ER_BUFFER_SIZE_LESSTHAN_ZERO,
                    "Puffergr\u00F6\u00DFe <=0" },

            {   MsgKey.ER_INVALID_UTF16_SURROGATE,
                    "Ung\u00FCltige UTF-16-Ersetzung festgestellt: {0}?" },

            {   MsgKey.ER_OIERROR,
                "I/O-Fehler" },

            {   MsgKey.ER_ILLEGAL_ATTRIBUTE_POSITION,
                "Attribut {0} kann nicht nach untergeordneten Knoten oder vor dem Erstellen eines Elements hinzugef\u00FCgt werden. Attribut wird ignoriert." },


            {   MsgKey.ER_NAMESPACE_PREFIX,
                "Namespace f\u00FCr Pr\u00E4fix \"{0}\" wurde nicht deklariert." },


            {   MsgKey.ER_STRAY_ATTRIBUTE,
                "Attribut \"{0}\" au\u00DFerhalb des Elements." },


            {   MsgKey.ER_STRAY_NAMESPACE,
                "Namespace-Deklaration {0}={1} au\u00DFerhalb des Elements." },

            {   MsgKey.ER_COULD_NOT_LOAD_RESOURCE,
                "\"{0}\" konnte nicht geladen werden (CLASSPATH pr\u00FCfen). Die Standardwerte werden verwendet" },

            {   MsgKey.ER_ILLEGAL_CHARACTER,
                "Versuch, Zeichen mit Integralwert {0} auszugeben, das nicht in der speziellen Ausgabecodierung von {1} dargestellt wird." },

            {   MsgKey.ER_COULD_NOT_LOAD_METHOD_PROPERTY,
                "Property-Datei \"{0}\" konnte f\u00FCr Ausgabemethode \"{1}\" nicht geladen werden (CLASSPATH pr\u00FCfen)" },

            {   MsgKey.ER_INVALID_PORT,
                "Ung\u00FCltige Portnummer" },

            {   MsgKey.ER_PORT_WHEN_HOST_NULL,
                "Port kann nicht festgelegt werden, wenn der Host null ist" },

            {   MsgKey.ER_HOST_ADDRESS_NOT_WELLFORMED,
                "Host ist keine wohlgeformte Adresse" },

            {   MsgKey.ER_SCHEME_NOT_CONFORMANT,
                "Schema ist nicht konform." },

            {   MsgKey.ER_SCHEME_FROM_NULL_STRING,
                "Schema kann nicht von Nullzeichenfolge festgelegt werden" },

            {   MsgKey.ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE,
                "Pfad enth\u00E4lt eine ung\u00FCltige Escapesequenz" },

            {   MsgKey.ER_PATH_INVALID_CHAR,
                "Pfad enth\u00E4lt ung\u00FCltiges Zeichen: {0}" },

            {   MsgKey.ER_FRAG_INVALID_CHAR,
                "Fragment enth\u00E4lt ein ung\u00FCltiges Zeichen" },

            {   MsgKey.ER_FRAG_WHEN_PATH_NULL,
                "Fragment kann nicht festgelegt werden, wenn der Pfad null ist" },

            {   MsgKey.ER_FRAG_FOR_GENERIC_URI,
                "Fragment kann nur f\u00FCr einen generischen URI festgelegt werden" },

            {   MsgKey.ER_NO_SCHEME_IN_URI,
                "Kein Schema gefunden in URI" },

            {   MsgKey.ER_CANNOT_INIT_URI_EMPTY_PARMS,
                "URI kann nicht mit leeren Parametern initialisiert werden" },

            {   MsgKey.ER_NO_FRAGMENT_STRING_IN_PATH,
                "Fragment kann nicht im Pfad und im Fragment angegeben werden" },

            {   MsgKey.ER_NO_QUERY_STRING_IN_PATH,
                "Abfragezeichenfolge kann nicht im Pfad und in der Abfragezeichenfolge angegeben werden" },

            {   MsgKey.ER_NO_PORT_IF_NO_HOST,
                "Port kann nicht angegeben werden, wenn der Host nicht angegeben wurde" },

            {   MsgKey.ER_NO_USERINFO_IF_NO_HOST,
                "Benutzerinformationen k\u00F6nnen nicht angegeben werden, wenn der Host nicht angegeben wurde" },

            {   MsgKey.ER_XML_VERSION_NOT_SUPPORTED,
                "Warnung: Die Version des Ausgabedokuments soll \"{0}\" sein. Diese Version von XML wird nicht unterst\u00FCtzt. Die Version des Ausgabedokuments wird \"1.0\" sein." },

            {   MsgKey.ER_SCHEME_REQUIRED,
                "Schema ist erforderlich." },


            {   MsgKey.ER_FACTORY_PROPERTY_MISSING,
                "Das an die SerializerFactory \u00FCbergebene Properties-Objekt verf\u00FCgt \u00FCber keine Eigenschaft \"{0}\"." },

            {   MsgKey.ER_ENCODING_NOT_SUPPORTED,
                "Warnung: Die Codierung \"{0}\" wird nicht von der Java-Laufzeit unterst\u00FCtzt." },


        };

        return contents;
    }
}
