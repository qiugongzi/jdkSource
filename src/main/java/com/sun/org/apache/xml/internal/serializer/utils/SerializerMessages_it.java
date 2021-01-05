

package com.sun.org.apache.xml.internal.serializer.utils;

import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


public class SerializerMessages_it extends ListResourceBundle {




    public Object[][] getContents() {
        Object[][] contents = new Object[][] {
            {   MsgKey.BAD_MSGKEY,
                "La chiave di messaggio ''{0}'' non si trova nella classe messaggio ''{1}''" },

            {   MsgKey.BAD_MSGFORMAT,
                "Formato di messaggio ''{0}'' in classe messaggio ''{1}'' non riuscito." },

            {   MsgKey.ER_SERIALIZER_NOT_CONTENTHANDLER,
                "La classe serializzatore ''{0}'' non implementa org.xml.sax.ContentHandler." },

            {   MsgKey.ER_RESOURCE_COULD_NOT_FIND,
                    "Risorsa [ {0} ] non trovata.\n {1}" },

            {   MsgKey.ER_RESOURCE_COULD_NOT_LOAD,
                    "Impossibile caricare la risorsa [ {0} ]: {1} \n {2} \t {3}" },

            {   MsgKey.ER_BUFFER_SIZE_LESSTHAN_ZERO,
                    "Dimensione buffer <=0" },

            {   MsgKey.ER_INVALID_UTF16_SURROGATE,
                    "Rilevato surrogato UTF-16 non valido: {0}?" },

            {   MsgKey.ER_OIERROR,
                "Errore di I/O" },

            {   MsgKey.ER_ILLEGAL_ATTRIBUTE_POSITION,
                "Impossibile aggiungere l''attributo {0} dopo i nodi figlio o prima che sia prodotto un elemento. L''attributo verr\u00E0 ignorato." },


            {   MsgKey.ER_NAMESPACE_PREFIX,
                "Lo spazio di nomi per il prefisso ''{0}'' non \u00E8 stato dichiarato." },


            {   MsgKey.ER_STRAY_ATTRIBUTE,
                "Attributo ''{0}'' al di fuori dell''elemento." },


            {   MsgKey.ER_STRAY_NAMESPACE,
                "Dichiarazione dello spazio di nomi ''{0}''=''{1}'' al di fuori dell''elemento." },

            {   MsgKey.ER_COULD_NOT_LOAD_RESOURCE,
                "Impossibile caricare ''{0}'' (verificare CLASSPATH); verranno utilizzati i valori predefiniti" },

            {   MsgKey.ER_ILLEGAL_CHARACTER,
                "Tentativo di eseguire l''output di un carattere di valore integrale {0} non rappresentato nella codifica di output {1} specificata." },

            {   MsgKey.ER_COULD_NOT_LOAD_METHOD_PROPERTY,
                "Impossibile caricare il file delle propriet\u00E0 ''{0}'' per il metodo di emissione ''{1}'' (verificare CLASSPATH)" },

            {   MsgKey.ER_INVALID_PORT,
                "Numero di porta non valido" },

            {   MsgKey.ER_PORT_WHEN_HOST_NULL,
                "La porta non pu\u00F2 essere impostata se l'host \u00E8 nullo" },

            {   MsgKey.ER_HOST_ADDRESS_NOT_WELLFORMED,
                "Host non \u00E8 un indirizzo corretto" },

            {   MsgKey.ER_SCHEME_NOT_CONFORMANT,
                "Lo schema non \u00E8 conforme." },

            {   MsgKey.ER_SCHEME_FROM_NULL_STRING,
                "Impossibile impostare lo schema da una stringa nulla" },

            {   MsgKey.ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE,
                "Il percorso contiene sequenza di escape non valida" },

            {   MsgKey.ER_PATH_INVALID_CHAR,
                "Il percorso contiene un carattere non valido: {0}" },

            {   MsgKey.ER_FRAG_INVALID_CHAR,
                "Il frammento contiene un carattere non valido" },

            {   MsgKey.ER_FRAG_WHEN_PATH_NULL,
                "Il frammento non pu\u00F2 essere impostato se il percorso \u00E8 nullo" },

            {   MsgKey.ER_FRAG_FOR_GENERIC_URI,
                "Il frammento pu\u00F2 essere impostato solo per un URI generico" },

            {   MsgKey.ER_NO_SCHEME_IN_URI,
                "Nessuno schema trovato nell'URI" },

            {   MsgKey.ER_CANNOT_INIT_URI_EMPTY_PARMS,
                "Impossibile inizializzare l'URI con i parametri vuoti" },

            {   MsgKey.ER_NO_FRAGMENT_STRING_IN_PATH,
                "Il frammento non pu\u00F2 essere specificato sia nel percorso che nel frammento" },

            {   MsgKey.ER_NO_QUERY_STRING_IN_PATH,
                "La stringa di query non pu\u00F2 essere specificata nella stringa di percorso e query." },

            {   MsgKey.ER_NO_PORT_IF_NO_HOST,
                "La porta non pu\u00F2 essere specificata se l'host non \u00E8 specificato" },

            {   MsgKey.ER_NO_USERINFO_IF_NO_HOST,
                "Userinfo non pu\u00F2 essere specificato se l'host non \u00E8 specificato" },

            {   MsgKey.ER_XML_VERSION_NOT_SUPPORTED,
                "Avvertenza: la versione del documento di output deve essere ''{0}''. Questa versione di XML non \u00E8 supportata. La versione del documento di output sar\u00E0 ''1.0''." },

            {   MsgKey.ER_SCHEME_REQUIRED,
                "Lo schema \u00E8 obbligatorio." },


            {   MsgKey.ER_FACTORY_PROPERTY_MISSING,
                "L''oggetto Properties passato a SerializerFactory non dispone di una propriet\u00E0 ''{0}''." },

            {   MsgKey.ER_ENCODING_NOT_SUPPORTED,
                "Avvertenza: la codifica ''{0}'' non \u00E8 supportata da Java Runtime." },


        };

        return contents;
    }
}
