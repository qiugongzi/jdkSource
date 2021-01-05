


package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import java.util.ListResourceBundle;


public class ErrorMessages_it extends ListResourceBundle {



    public Object[][] getContents()
    {
      return new Object[][] {
        {ErrorMsg.MULTIPLE_STYLESHEET_ERR,
        "Sono stati definiti pi\u00F9 fogli di stile nello stesso file."},


        {ErrorMsg.TEMPLATE_REDEF_ERR,
        "Il modello ''{0}'' \u00E8 gi\u00E0 stato definito in questo foglio di stile."},



        {ErrorMsg.TEMPLATE_UNDEF_ERR,
        "Il modello ''{0}'' non \u00E8 stato definito in questo foglio di stile."},


        {ErrorMsg.VARIABLE_REDEF_ERR,
        "La variabile ''{0}'' \u00E8 stata definita pi\u00F9 volte nello stesso ambito."},


        {ErrorMsg.VARIABLE_UNDEF_ERR,
        "Variabile o parametro ''{0}'' non definito."},


        {ErrorMsg.CLASS_NOT_FOUND_ERR,
        "Impossibile trovare la classe \"{0}\"."},


        {ErrorMsg.METHOD_NOT_FOUND_ERR,
        "Impossibile trovare il metodo esterno ''{0}'' (deve essere pubblico)."},


        {ErrorMsg.ARGUMENT_CONVERSION_ERR,
        "Impossibile convertire l''argomento o il tipo restituito in una chiamata per il metodo ''{0}''"},


        {ErrorMsg.FILE_NOT_FOUND_ERR,
        "File o URI ''{0}'' non trovato."},


        {ErrorMsg.INVALID_URI_ERR,
        "URI ''{0}'' non valido."},


        {ErrorMsg.FILE_ACCESS_ERR,
        "Impossibile aprire il file o l''URI ''{0}''."},


        {ErrorMsg.MISSING_ROOT_ERR,
        "\u00C8 previsto un elemento <xsl:stylesheet> o <xsl:transform>."},


        {ErrorMsg.NAMESPACE_UNDEF_ERR,
        "Prefisso spazio di nomi ''{0}'' non dichiarato."},


        {ErrorMsg.FUNCTION_RESOLVE_ERR,
        "Impossibile risolvere la chiamata per la funzione ''{0}''."},


        {ErrorMsg.NEED_LITERAL_ERR,
        "L''argomento per ''{0}'' deve essere una stringa di valori."},


        {ErrorMsg.XPATH_PARSER_ERR,
        "Errore durante l''analisi dell''espressione XPath ''{0}''."},


        {ErrorMsg.REQUIRED_ATTR_ERR,
        "Attributo obbligatorio ''{0}'' mancante."},


        {ErrorMsg.ILLEGAL_CHAR_ERR,
        "Carattere ''{0}'' non valido nell''espressione XPath."},


        {ErrorMsg.ILLEGAL_PI_ERR,
        "Nome ''{0}'' non valido per l''istruzione di elaborazione."},


        {ErrorMsg.STRAY_ATTRIBUTE_ERR,
        "Attributo ''{0}'' al di fuori dell''elemento."},


        {ErrorMsg.ILLEGAL_ATTRIBUTE_ERR,
        "Attributo ''{0}'' non valido."},


        {ErrorMsg.CIRCULAR_INCLUDE_ERR,
        "Importazione/inclusione circolare. Il foglio di stile ''{0}'' \u00E8 gi\u00E0 stato caricato."},


        {ErrorMsg.RESULT_TREE_SORT_ERR,
        "Impossibile ordinare i frammenti della struttura di risultati (gli elementi <xsl:sort> verranno ignorati). \u00C8 necessario ordinare i nodi quando si crea la struttura di risultati."},


        {ErrorMsg.SYMBOLS_REDEF_ERR,
        "Formattazione decimale ''{0}'' gi\u00E0 definita."},


        {ErrorMsg.XSL_VERSION_ERR,
        "La versione XSL ''{0}'' non \u00E8 supportata da XSLTC."},


        {ErrorMsg.CIRCULAR_VARIABLE_ERR,
        "Riferimento di variabile/parametro circolare in ''{0}''."},


        {ErrorMsg.ILLEGAL_BINARY_OP_ERR,
        "Operatore sconosciuto per l'espressione binaria."},


        {ErrorMsg.ILLEGAL_ARG_ERR,
        "Uno o pi\u00F9 argomenti non validi per la chiamata della funzione."},


        {ErrorMsg.DOCUMENT_ARG_ERR,
        "Il secondo argomento per la funzione document() deve essere un set di nodi."},


        {ErrorMsg.MISSING_WHEN_ERR,
        "\u00C8 richiesto almeno un elemento <xsl:when> in <xsl:choose>."},


        {ErrorMsg.MULTIPLE_OTHERWISE_ERR,
        "\u00C8 consentito un solo elemento <xsl:otherwise> in <xsl:choose>."},


        {ErrorMsg.STRAY_OTHERWISE_ERR,
        "<xsl:otherwise> pu\u00F2 essere utilizzato sono in <xsl:choose>."},


        {ErrorMsg.STRAY_WHEN_ERR,
        "<xsl:when> pu\u00F2 essere utilizzato sono in <xsl:choose>."},


        {ErrorMsg.WHEN_ELEMENT_ERR,
        "Sono consentiti solo elementi <xsl:when> e <xsl:otherwise> in <xsl:choose>."},


        {ErrorMsg.UNNAMED_ATTRIBSET_ERR,
        "<xsl:attribute-set> mancante nell'attributo 'name'."},


        {ErrorMsg.ILLEGAL_CHILD_ERR,
        "Elemento figlio non valido."},


        {ErrorMsg.ILLEGAL_ELEM_NAME_ERR,
        "Impossibile richiamare un elemento ''{0}''"},


        {ErrorMsg.ILLEGAL_ATTR_NAME_ERR,
        "Impossibile richiamare un attributo ''{0}''"},


        {ErrorMsg.ILLEGAL_TEXT_NODE_ERR,
        "I dati di testo non rientrano nell'elemento <xsl:stylesheet> di livello superiore."},


        {ErrorMsg.SAX_PARSER_CONFIG_ERR,
        "Parser JAXP non configurato correttamente"},


        {ErrorMsg.INTERNAL_ERR,
        "Errore interno XSLTC irreversibile: ''{0}''"},


        {ErrorMsg.UNSUPPORTED_XSL_ERR,
        "Elemento XSL \"{0}\" non supportato."},


        {ErrorMsg.UNSUPPORTED_EXT_ERR,
        "Estensione XSLTC ''{0}'' non riconosciuta."},


        {ErrorMsg.MISSING_XSLT_URI_ERR,
        "Il documento di input non \u00E8 un foglio di stile (spazio di nomi XSL non dichiarato nell'elemento radice)."},


        {ErrorMsg.MISSING_XSLT_TARGET_ERR,
        "Impossibile trovare la destinazione ''{0}'' del foglio di stile."},


        {ErrorMsg.ACCESSING_XSLT_TARGET_ERR,
        "Impossibile leggere la destinazione del foglio di stile ''{0}''. Accesso ''{1}'' non consentito a causa della limitazione definita dalla propriet\u00E0 accessExternalStylesheet."},


        {ErrorMsg.NOT_IMPLEMENTED_ERR,
        "Non implementato: ''{0}''."},


        {ErrorMsg.NOT_STYLESHEET_ERR,
        "Il documento di input non contiene un foglio di stile XSL."},


        {ErrorMsg.ELEMENT_PARSE_ERR,
        "Impossibile analizzare l''elemento ''{0}''"},


        {ErrorMsg.KEY_USE_ATTR_ERR,
        "L'attributo di uso <key> deve essere un nodo, un set di nodi, una stringa o un numero."},


        {ErrorMsg.OUTPUT_VERSION_ERR,
        "La versione del documento XML di output deve essere 1.0"},


        {ErrorMsg.ILLEGAL_RELAT_OP_ERR,
        "Operatore sconosciuto per l'espressione relazionale"},


        {ErrorMsg.ATTRIBSET_UNDEF_ERR,
        "Tentativo di utilizzare un set di attributi ''{0}'' inesistente."},


        {ErrorMsg.ATTR_VAL_TEMPLATE_ERR,
        "Impossibile analizzare il modello di valore di attributo ''{0}''."},


        {ErrorMsg.UNKNOWN_SIG_TYPE_ERR,
        "Tipo di dati sconosciuto nella firma per la classe ''{0}''."},


        {ErrorMsg.DATA_CONVERSION_ERR,
        "Impossibile convertire il tipo di dati ''{0}'' in ''{1}''."},


        {ErrorMsg.NO_TRANSLET_CLASS_ERR,
        "Il modello non contiene una definizione di classe di translet valida."},


        {ErrorMsg.NO_MAIN_TRANSLET_ERR,
        "Il modello non contiene una classe denominata ''{0}''."},


        {ErrorMsg.TRANSLET_CLASS_ERR,
        "Impossibile caricare la classe di translet ''{0}''."},

        {ErrorMsg.TRANSLET_OBJECT_ERR,
        "La classe di translet \u00E8 stata caricata, ma non \u00E8 possibile creare l'istanza del translet."},


        {ErrorMsg.ERROR_LISTENER_NULL_ERR,
        "Tentativo di impostare ErrorListener per ''{0}'' su null"},


        {ErrorMsg.JAXP_UNKNOWN_SOURCE_ERR,
        "XSLTC supporta solo StreamSource, SAXSource e DOMSource."},


        {ErrorMsg.JAXP_NO_SOURCE_ERR,
        "L''oggetto di origine passato a ''{0}'' non ha contenuti."},


        {ErrorMsg.JAXP_COMPILE_ERR,
        "Impossibile compilare il foglio di stile"},


        {ErrorMsg.JAXP_INVALID_ATTR_ERR,
        "TransformerFactory non riconosce l''attributo ''{0}''."},

        {ErrorMsg.JAXP_INVALID_ATTR_VALUE_ERR,
        "Valore errato specificato per l''attributo ''{0}''."},


        {ErrorMsg.JAXP_SET_RESULT_ERR,
        "setResult() deve essere richiamato prima di startDocument()."},


        {ErrorMsg.JAXP_NO_TRANSLET_ERR,
        "Il trasformatore non contiene alcun oggetto incapsulato."},


        {ErrorMsg.JAXP_NO_HANDLER_ERR,
        "Nessun handler di output definito per il risultato della trasformazione."},


        {ErrorMsg.JAXP_NO_RESULT_ERR,
        "L''oggetto di risultato passato a ''{0}'' non \u00E8 valido."},


        {ErrorMsg.JAXP_UNKNOWN_PROP_ERR,
        "Tentativo di accedere a una propriet\u00E0 ''{0}'' del trasformatore non valida."},


        {ErrorMsg.SAX2DOM_ADAPTER_ERR,
        "Impossibile creare l''adattatore SAX2DOM ''{0}''."},


        {ErrorMsg.XSLTC_SOURCE_ERR,
        "XSLTCSource.build() richiamato senza che sia stato impostato systemId."},

        { ErrorMsg.ER_RESULT_NULL,
            "Il risultato non deve essere nullo"},


        {ErrorMsg.JAXP_INVALID_SET_PARAM_VALUE,
        "Il valore del parametro {0} deve essere un oggetto Java valido"},


        {ErrorMsg.COMPILE_STDIN_ERR,
        "L'opzione -i deve essere utilizzata con l'opzione -o."},



        {ErrorMsg.COMPILE_USAGE_STR,
        "RIEPILOGO\n   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Compile [-o <output>]\n      [-d <directory>] [-j <file jar>] [-p <package>]\n      [-n] [-x] [-u] [-v] [-h] { <foglio di stile> | -i }\n\nOPZIONI\n   -o <output>    assegna l'<output> del nome al translet\n                  generato.  Per impostazione predefinita, il nome translet\n                  \u00E8 derivato dal nome <foglio di stile>.  Questa opzione\n                  viene ignorata se si compilano pi\u00F9 fogli di stile.\n   -d <directory> specifica una directory di destinazione per il translet\n   -j <file jar>   crea un package di classi di translet inserendolo in un file JAR con il\n                  nome specificato come <jarfile>\n   -p <package>   specifica un prefisso di nome package per tutte le\n                  classi di translet generate.\n   -n             abilita l'inserimento in linea dei modelli (in media, l'impostazione predefinita \u00E8\n                  la migliore).\n   -x             attiva l'output di altri messaggi di debug\n   -u             interpreta gli argomenti <stylesheet> come URL\n   -i             obbliga il compilatore a leggere il foglio di stile da stdin\n   -v             visualizza la versione del compilatore\n   -h             visualizza questa istruzione di uso\n"},


        {ErrorMsg.TRANSFORM_USAGE_STR,
        "RIPEILOGO\n   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Transform [-j <file jar>]\n      [-x] [-n <iterazioni>] {-u <url_documento> | <documento>}\n      <classe> [<param1>=<valore1> ...]\n\n   utilizza la <classe> translet per trasformare un documento XML\n   specificato come <documento>. La <classe> di translet si trova nel\n   CLASSPATH dell'utente o nel <file jar> specificato facoltativamente.\\OPZIONI\n   -j <file jar>    specifica un file JAR dal quale caricare il translet\n   -x              attiva l'output di altri messaggi di debug\n   -n <iterazioni> esegue le <iterazioni> di trasformazione e\n                   visualizza le informazioni sui profili\n   -u <url_documento> specifica il documento di input XML come URL\n"},




        {ErrorMsg.STRAY_SORT_ERR,
        "<xsl:sort> pu\u00F2 essere utilizzato sono in <xsl:for-each> o <xsl:apply-templates>."},


        {ErrorMsg.UNSUPPORTED_ENCODING,
        "La codifica di output ''{0}'' non \u00E8 supportata in questa JVM."},


        {ErrorMsg.SYNTAX_ERR,
        "Errore di sintassi in ''{0}''."},


        {ErrorMsg.CONSTRUCTOR_NOT_FOUND,
        "Impossibile trovare il costruttore esterno ''{0}''."},


        {ErrorMsg.NO_JAVA_FUNCT_THIS_REF,
        "Il primo argomento per la funzione Java non statica ''{0}'' non \u00E8 un riferimento di oggetto valido."},


        {ErrorMsg.TYPE_CHECK_ERR,
        "Errore durante il controllo del tipo dell''espressione ''{0}''."},


        {ErrorMsg.TYPE_CHECK_UNK_LOC_ERR,
        "Errore durante il controllo del tipo di un''espressione in una posizione sconosciuta."},


        {ErrorMsg.ILLEGAL_CMDLINE_OPTION_ERR,
        "L''opzione di riga di comando ''{0}'' non \u00E8 valida."},


        {ErrorMsg.CMDLINE_OPT_MISSING_ARG_ERR,
        "Nell''opzione di riga di comando ''{0}'' manca un argomento obbligatorio."},


        {ErrorMsg.WARNING_PLUS_WRAPPED_MSG,
        "WARNING:  ''{0}''\n       :{1}"},


        {ErrorMsg.WARNING_MSG,
        "WARNING:  ''{0}''"},


        {ErrorMsg.FATAL_ERR_PLUS_WRAPPED_MSG,
        "FATAL ERROR:  ''{0}''\n           :{1}"},


        {ErrorMsg.FATAL_ERR_MSG,
        "FATAL ERROR:  ''{0}''"},


        {ErrorMsg.ERROR_PLUS_WRAPPED_MSG,
        "ERROR:  ''{0}''\n     :{1}"},


        {ErrorMsg.ERROR_MSG,
        "ERROR:  ''{0}''"},


        {ErrorMsg.TRANSFORM_WITH_TRANSLET_STR,
        "Trasformazione mediante il translet ''{0}'' "},


        {ErrorMsg.TRANSFORM_WITH_JAR_STR,
        "Trasformazione mediante il translet ''{0}'' del file jar ''{1}''"},


        {ErrorMsg.COULD_NOT_CREATE_TRANS_FACT,
        "Impossibile creare un''istanza della classe TransformerFactory ''{0}''."},


        {ErrorMsg.TRANSLET_NAME_JAVA_CONFLICT,
         "Impossibile utilizzare il nome ''{0}'' per la classe di translet poich\u00E9 contiene caratteri non consentiti nel nome della classe Java. Verr\u00E0 utilizzato il nome ''{1}''."},


        {ErrorMsg.COMPILER_ERROR_KEY,
        "Errori del compilatore:"},


        {ErrorMsg.COMPILER_WARNING_KEY,
        "Avvertenze del compilatore:"},


        {ErrorMsg.RUNTIME_ERROR_KEY,
        "Errori del translet:"},


        {ErrorMsg.INVALID_QNAME_ERR,
        "Un attributo il cui valore deve essere un QName o una lista separata da spazi di QName contiene il valore ''{0}''"},


        {ErrorMsg.INVALID_NCNAME_ERR,
        "Un attributo il cui valore deve essere un NCName contiene il valore ''{0}''"},


        {ErrorMsg.INVALID_METHOD_IN_OUTPUT,
        "L''attributo di metodo per un elemento <xsl:output> ha il valore ''{0}'', ma deve essere uno tra ''xml'', ''html'', ''text'' o qname-but-not-ncname"},

        {ErrorMsg.JAXP_GET_FEATURE_NULL_NAME,
        "Il nome funzione non pu\u00F2 essere nullo in TransformerFactory.getFeature (nome stringa)."},

        {ErrorMsg.JAXP_SET_FEATURE_NULL_NAME,
        "Il nome funzione non pu\u00F2 essere nullo in TransformerFactory.setFeature (nome stringa, valore booleano)."},

        {ErrorMsg.JAXP_UNSUPPORTED_FEATURE,
        "Impossibile impostare la funzione ''{0}'' in questo TransformerFactory."},

        {ErrorMsg.JAXP_SECUREPROCESSING_FEATURE,
        "FEATURE_SECURE_PROCESSING: impossibile impostare la funzione su false se \u00E8 presente Security Manager."},


        {ErrorMsg.OUTLINE_ERR_TRY_CATCH,
         "Errore XSLTC interno: il bytecode generato contiene un blocco try-catch-finally e non pu\u00F2 essere di tipo outlined."},


        {ErrorMsg.OUTLINE_ERR_UNBALANCED_MARKERS,
         "Errore XSLTC interno: gli indicatori OutlineableChunkStart e OutlineableChunkEnd devono essere bilanciati e nidificati correttamente."},


        {ErrorMsg.OUTLINE_ERR_DELETED_TARGET,
         "Errore XSLTC interno: a un'istruzione che faceva parte di un blocco di bytecode di tipo outlined viene ancora fatto riferimento nel metodo originale."
        },



        {ErrorMsg.OUTLINE_ERR_METHOD_TOO_BIG,
         "Errore XSLTC interno: un metodo nel translet supera la limitazione Java Virtual Machine relativa alla lunghezza per un metodo di 64 kilobyte. Ci\u00F2 \u00E8 generalmente causato dalle grandi dimensioni dei modelli in un foglio di stile. Provare a ristrutturare il foglio di stile per utilizzare modelli di dimensioni inferiori."
        },

         {ErrorMsg.DESERIALIZE_TRANSLET_ERR, "Quando la sicurezza Java \u00E8 abilitata, il supporto per la deserializzazione TemplatesImpl \u00E8 disabilitato. \u00C8 possibile ignorare questa condizione impostando su true la propriet\u00E0 di sistema jdk.xml.enableTemplatesImplDeserialization."}

    };

    }
}
