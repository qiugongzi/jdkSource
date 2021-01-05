


package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import java.util.ListResourceBundle;


public class ErrorMessages_de extends ListResourceBundle {



    public Object[][] getContents()
    {
      return new Object[][] {
        {ErrorMsg.MULTIPLE_STYLESHEET_ERR,
        "Mehrere Stylesheets in derselben Datei definiert."},


        {ErrorMsg.TEMPLATE_REDEF_ERR,
        "Vorlage \"{0}\" bereits in diesem Stylesheet definiert."},



        {ErrorMsg.TEMPLATE_UNDEF_ERR,
        "Vorlage \"{0}\" nicht in diesem Stylesheet definiert."},


        {ErrorMsg.VARIABLE_REDEF_ERR,
        "Variable \"{0}\" ist mehrmals in demselben G\u00FCltigkeitsbereich definiert."},


        {ErrorMsg.VARIABLE_UNDEF_ERR,
        "Variable oder Parameter \"{0}\" ist nicht definiert."},


        {ErrorMsg.CLASS_NOT_FOUND_ERR,
        "Klasse \"{0}\" kann nicht gefunden werden."},


        {ErrorMsg.METHOD_NOT_FOUND_ERR,
        "Externe Methode \"{0}\" kann nicht gefunden werden (muss \"public\" sein)."},


        {ErrorMsg.ARGUMENT_CONVERSION_ERR,
        "Konvertierung von Argument-/R\u00FCckgabetyp in Aufruf von Methode \"{0}\" nicht m\u00F6glich"},


        {ErrorMsg.FILE_NOT_FOUND_ERR,
        "Datei oder URI \"{0}\" nicht gefunden."},


        {ErrorMsg.INVALID_URI_ERR,
        "Ung\u00FCltiger URI \"{0}\"."},


        {ErrorMsg.FILE_ACCESS_ERR,
        "Datei oder URI \"{0}\" kann nicht ge\u00F6ffnet werden."},


        {ErrorMsg.MISSING_ROOT_ERR,
        "<xsl:stylesheet>- oder <xsl:transform>-Element erwartet."},


        {ErrorMsg.NAMESPACE_UNDEF_ERR,
        "Namespace-Pr\u00E4fix \"{0}\" ist nicht deklariert."},


        {ErrorMsg.FUNCTION_RESOLVE_ERR,
        "Aufruf kann nicht in Funktion \"{0}\" aufgel\u00F6st werden."},


        {ErrorMsg.NEED_LITERAL_ERR,
        "Argument f\u00FCr \"{0}\" muss eine literale Zeichenfolge sein."},


        {ErrorMsg.XPATH_PARSER_ERR,
        "Fehler beim Parsen von XPath-Ausdruck \"{0}\"."},


        {ErrorMsg.REQUIRED_ATTR_ERR,
        "Erforderliches Attribut \"{0}\" fehlt."},


        {ErrorMsg.ILLEGAL_CHAR_ERR,
        "Ung\u00FCltiges Zeichen \"{0}\" in XPath-Ausdruck."},


        {ErrorMsg.ILLEGAL_PI_ERR,
        "Ung\u00FCltiger Name \"{0}\" f\u00FCr Verarbeitungsanweisung."},


        {ErrorMsg.STRAY_ATTRIBUTE_ERR,
        "Attribut \"{0}\" au\u00DFerhalb des Elements."},


        {ErrorMsg.ILLEGAL_ATTRIBUTE_ERR,
        "Unzul\u00E4ssiges Attribut \"{0}\"."},


        {ErrorMsg.CIRCULAR_INCLUDE_ERR,
        "Zyklisches import/include. Stylesheet \"{0}\" bereits geladen."},


        {ErrorMsg.RESULT_TREE_SORT_ERR,
        "Ergebnisbaumfragmente k\u00F6nnen nicht sortiert werden (<xsl:sort>-Elemente werden ignoriert). Sie m\u00FCssen die Knoten sortieren, wenn Sie den Ergebnisbaum erstellen."},


        {ErrorMsg.SYMBOLS_REDEF_ERR,
        "Dezimalformatierung \"{0}\" ist bereits definiert."},


        {ErrorMsg.XSL_VERSION_ERR,
        "XSL-Version \"{0}\" wird nicht von XSLTC unterst\u00FCtzt."},


        {ErrorMsg.CIRCULAR_VARIABLE_ERR,
        "Zyklische Variablen-/Parameterreferenz in \"{0}\"."},


        {ErrorMsg.ILLEGAL_BINARY_OP_ERR,
        "Unbekannter Operator f\u00FCr Bin\u00E4rausdruck."},


        {ErrorMsg.ILLEGAL_ARG_ERR,
        "Unzul\u00E4ssige Argumente f\u00FCr Funktionsaufruf."},


        {ErrorMsg.DOCUMENT_ARG_ERR,
        "Zweites Argument f\u00FCr document()-Funktion muss ein NodeSet sein."},


        {ErrorMsg.MISSING_WHEN_ERR,
        "Mindestens ein <xsl:when>-Element in <xsl:choose> erforderlich."},


        {ErrorMsg.MULTIPLE_OTHERWISE_ERR,
        "Nur ein <xsl:otherwise>-Element in <xsl:choose> zul\u00E4ssig."},


        {ErrorMsg.STRAY_OTHERWISE_ERR,
        "<xsl:otherwise> kann nur in <xsl:choose> verwendet werden."},


        {ErrorMsg.STRAY_WHEN_ERR,
        "<xsl:when> kann nur in <xsl:choose> verwendet werden."},


        {ErrorMsg.WHEN_ELEMENT_ERR,
        "Nur <xsl:when>- und <xsl:otherwise>-Elemente in <xsl:choose> zul\u00E4ssig."},


        {ErrorMsg.UNNAMED_ATTRIBSET_ERR,
        "Bei <xsl:attribute-set> fehlt das \"name\"-Attribut."},


        {ErrorMsg.ILLEGAL_CHILD_ERR,
        "Ung\u00FCltiges untergeordnetes Element."},


        {ErrorMsg.ILLEGAL_ELEM_NAME_ERR,
        "Elemente d\u00FCrfen nicht den Namen \"{0}\" haben"},


        {ErrorMsg.ILLEGAL_ATTR_NAME_ERR,
        "Attribute d\u00FCrfen nicht den Namen \"{0}\" haben"},


        {ErrorMsg.ILLEGAL_TEXT_NODE_ERR,
        "Textdaten au\u00DFerhalb des <xsl:stylesheet>-Elements der obersten Ebene."},


        {ErrorMsg.SAX_PARSER_CONFIG_ERR,
        "JAXP-Parser nicht korrekt konfiguriert"},


        {ErrorMsg.INTERNAL_ERR,
        "Nicht behebbarer interner XSLTC-Fehler: \"{0}\""},


        {ErrorMsg.UNSUPPORTED_XSL_ERR,
        "Nicht unterst\u00FCtztes XSL-Element \"{0}\"."},


        {ErrorMsg.UNSUPPORTED_EXT_ERR,
        "Unbekannte XSLTC-Erweiterung \"{0}\"."},


        {ErrorMsg.MISSING_XSLT_URI_ERR,
        "Das Eingabedokument ist kein Stylesheet (der XSL-Namespace ist nicht im Root-Element deklariert)."},


        {ErrorMsg.MISSING_XSLT_TARGET_ERR,
        "Stylesheet-Ziel \"{0}\" konnte nicht gefunden werden."},


        {ErrorMsg.ACCESSING_XSLT_TARGET_ERR,
        "Stylesheet-Ziel \"{0}\" konnte nicht gelesen werden, weil der \"{1}\"-Zugriff wegen einer von der Eigenschaft accessExternalStylesheet festgelegten Einschr\u00E4nkung nicht zul\u00E4ssig ist."},


        {ErrorMsg.NOT_IMPLEMENTED_ERR,
        "Nicht implementiert: \"{0}\"."},


        {ErrorMsg.NOT_STYLESHEET_ERR,
        "Das Eingabedokument enth\u00E4lt kein XSL-Stylesheet."},


        {ErrorMsg.ELEMENT_PARSE_ERR,
        "Element \"{0}\" konnte nicht geparst werden"},


        {ErrorMsg.KEY_USE_ATTR_ERR,
        "Das \"use\"-Attribut von <key> muss \"node\", \"node-set\", \"string\" oder \"number\" sein."},


        {ErrorMsg.OUTPUT_VERSION_ERR,
        "Ausgabe-XML-Dokumentversion muss 1.0 sein"},


        {ErrorMsg.ILLEGAL_RELAT_OP_ERR,
        "Unbekannter Operator f\u00FCr Vergleichsausdruck"},


        {ErrorMsg.ATTRIBSET_UNDEF_ERR,
        "Versuch, nicht vorhandene Attributgruppe \"{0}\" zu verwenden."},


        {ErrorMsg.ATTR_VAL_TEMPLATE_ERR,
        "Attributwertvorlage \"{0}\" kann nicht geparst werden."},


        {ErrorMsg.UNKNOWN_SIG_TYPE_ERR,
        "Unbekannter Datentyp in Signatur f\u00FCr Klasse \"{0}\"."},


        {ErrorMsg.DATA_CONVERSION_ERR,
        "Datentyp \"{0}\" kann nicht in \"{1}\" konvertiert werden."},


        {ErrorMsg.NO_TRANSLET_CLASS_ERR,
        "Dieses \"Templates\" enth\u00E4lt keine g\u00FCltige Translet-Klassendefinition."},


        {ErrorMsg.NO_MAIN_TRANSLET_ERR,
        "Dieses \"Templates\" enth\u00E4lt keine Klasse mit dem Namen \"{0}\"."},


        {ErrorMsg.TRANSLET_CLASS_ERR,
        "Translet-Klasse \"{0}\" konnte nicht geladen werden."},

        {ErrorMsg.TRANSLET_OBJECT_ERR,
        "Translet-Klasse geladen, Translet-Instanz kann aber nicht erstellt werden."},


        {ErrorMsg.ERROR_LISTENER_NULL_ERR,
        "Versuch, ErrorListener f\u00FCr \"{0}\" auf null zu setzen"},


        {ErrorMsg.JAXP_UNKNOWN_SOURCE_ERR,
        "Nur StreamSource, SAXSource und DOMSource werden von XSLTC unterst\u00FCtzt"},


        {ErrorMsg.JAXP_NO_SOURCE_ERR,
        "An \"{0}\" \u00FCbergebenes Source-Objekt hat keinen Inhalt."},


        {ErrorMsg.JAXP_COMPILE_ERR,
        "Stylesheet konnte nicht kompiliert werden"},


        {ErrorMsg.JAXP_INVALID_ATTR_ERR,
        "TransformerFactory erkennt Attribut \"{0}\" nicht."},

        {ErrorMsg.JAXP_INVALID_ATTR_VALUE_ERR,
        "Falscher Wert f\u00FCr Attribut \"{0}\" angegeben."},


        {ErrorMsg.JAXP_SET_RESULT_ERR,
        "setResult() muss vor startDocument() aufgerufen werden."},


        {ErrorMsg.JAXP_NO_TRANSLET_ERR,
        "Der Transformer hat kein gekapseltes Translet-Objekt."},


        {ErrorMsg.JAXP_NO_HANDLER_ERR,
        "Kein definierter Ausgabe-Handler f\u00FCr Transformationsergebnis."},


        {ErrorMsg.JAXP_NO_RESULT_ERR,
        "An \"{0}\" \u00FCbergebenes Result-Objekt ist ung\u00FCltig."},


        {ErrorMsg.JAXP_UNKNOWN_PROP_ERR,
        "Versuch, auf ung\u00FCltige Transformer-Eigenschaft \"{0}\" zuzugreifen."},


        {ErrorMsg.SAX2DOM_ADAPTER_ERR,
        "SAX2DOM-Adapter \"{0}\" konnte nicht erstellt werden."},


        {ErrorMsg.XSLTC_SOURCE_ERR,
        "XSLTCSource.build() ohne festgelegte systemID aufgerufen."},

        { ErrorMsg.ER_RESULT_NULL,
            "Ergebnis darf nicht null sein"},


        {ErrorMsg.JAXP_INVALID_SET_PARAM_VALUE,
        "Wert von Parameter {0} muss ein g\u00FCltiges Java-Objekt sein"},


        {ErrorMsg.COMPILE_STDIN_ERR,
        "Die Option \"-i\" muss mit der Option \"-o\" verwendet werden."},



        {ErrorMsg.COMPILE_USAGE_STR,
        "SYNOPSIS\n   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Compile [-o <Ausgabe>]\n      [-d <Verzeichnis>] [-j <JAR-Datei>] [-p <Package>]\n      [-n] [-x] [-u] [-v] [-h] { <Stylesheet> | -i }\n\nOPTIONS\n   -o <Ausgabe>    weist den Namen <Ausgabe> dem generierten\n                  Translet zu. Standardm\u00E4\u00DFig wird der Translet-Name\n                  vom <Stylesheet>-Namen abgeleitet. Diese Option\n                  wird ignoriert, wenn mehrere Stylesheets kompiliert werden.\n   -d <Verzeichnis> gibt ein Zielverzeichnis f\u00FCr das Translet an\n   -j <JAR-Datei>   verpackt Translet-Klassen in einer JAR-Datei mit dem\n                  als <jarfile> angegebenen Namen\n   -p <package>   gibt ein Packagenamenspr\u00E4fix f\u00FCr alle generierten\n                  Translet-Klassen an.\n   -n             aktiviert das Vorlagen-Inlining (Standardverhalten durchschnittlich\n                  besser).\n   -x             schaltet die zus\u00E4tzliche Debugging-Meldungsausgabe ein\n   -u             interpretiert <Stylesheet>-Argumente als URLs\n   -i             erzwingt, dass der Compiler das Stylesheet aus stdin liest\n   -v             druckt die Version des Compilers\n   -h             druckt diese Verwendungsanweisung\n"},


        {ErrorMsg.TRANSFORM_USAGE_STR,
        "SYNOPSIS \n   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Transform [-j <JAR-Datei>]\n      [-x] [-n <Iterationen>] {-u <document_url> | <Dokument>}\n      <Klasse> [<param1>=<value1> ...]\n\n   verwendet die Translet-<Klasse> zur Transformation eines XML-Dokuments, \n   das als <Dokument> angegeben wird. Die Translet-<Klasse> befindet sich entweder im\n   CLASSPATH des Benutzers oder in der optional angegebenen <JAR-Datei>.\nOPTIONS\n   -j <JAR-Datei>    gibt eine JAR-Datei an, aus der das Translet geladen werden soll\n   -x              schaltet die zus\u00E4tzliche Debugging-Meldungsausgabe ein\n   -n <Iterationen> f\u00FChrt die Transformation so oft aus, wie in <Iterationen> angegeben und\n                   zeigt Profilinformationen an\n   -u <document_url> gibt das XML-Eingabedokument als URL an\n"},




        {ErrorMsg.STRAY_SORT_ERR,
        "<xsl:sort> kann nur in <xsl:for-each> oder <xsl:apply-templates> verwendet werden."},


        {ErrorMsg.UNSUPPORTED_ENCODING,
        "Ausgabecodierung \"{0}\" wird auf dieser JVM nicht unterst\u00FCtzt."},


        {ErrorMsg.SYNTAX_ERR,
        "Syntaxfehler in \"{0}\"."},


        {ErrorMsg.CONSTRUCTOR_NOT_FOUND,
        "Externer Constructor \"{0}\" kann nicht gefunden werden."},


        {ErrorMsg.NO_JAVA_FUNCT_THIS_REF,
        "Das erste Argument f\u00FCr die nicht-\"static\"-Java-Funktion \"{0}\" ist keine g\u00FCltige Objektreferenz."},


        {ErrorMsg.TYPE_CHECK_ERR,
        "Fehler beim Pr\u00FCfen des Typs des Ausdrucks \"{0}\"."},


        {ErrorMsg.TYPE_CHECK_UNK_LOC_ERR,
        "Fehler beim Pr\u00FCfen des Typs eines Ausdrucks an einer unbekannten Stelle."},


        {ErrorMsg.ILLEGAL_CMDLINE_OPTION_ERR,
        "Die Befehlszeilenoption \"{0}\" ist nicht g\u00FCltig."},


        {ErrorMsg.CMDLINE_OPT_MISSING_ARG_ERR,
        "Bei der Befehlszeilenoption \"{0}\" fehlt ein erforderliches Argument."},


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
        "Transformation mit Translet \"{0}\" "},


        {ErrorMsg.TRANSFORM_WITH_JAR_STR,
        "Transformation mit Translet \"{0}\" aus JAR-Datei \"{1}\""},


        {ErrorMsg.COULD_NOT_CREATE_TRANS_FACT,
        "Es konnte keine Instanz der TransformerFactory-Klasse \"{0}\" erstellt werden."},


        {ErrorMsg.TRANSLET_NAME_JAVA_CONFLICT,
         "Der Name \"{0}\" konnte nicht als Name der Translet-Klasse verwendet werden, da er Zeichen enth\u00E4lt, die nicht im Namen einer Java-Klasse zul\u00E4ssig sind. Der Name \"{1}\" wurde stattdessen verwendet."},


        {ErrorMsg.COMPILER_ERROR_KEY,
        "Compiler-Fehler:"},


        {ErrorMsg.COMPILER_WARNING_KEY,
        "Compiler-Warnungen:"},


        {ErrorMsg.RUNTIME_ERROR_KEY,
        "Translet-Fehler:"},


        {ErrorMsg.INVALID_QNAME_ERR,
        "Ein Attribut, dessen Wert ein QName oder eine durch Leerstellen getrennte Liste mit QNames sein muss, hatte den Wert \"{0}\""},


        {ErrorMsg.INVALID_NCNAME_ERR,
        "Ein Attribut, dessen Wert ein NCName sein muss, hatte den Wert \"{0}\""},


        {ErrorMsg.INVALID_METHOD_IN_OUTPUT,
        "Das \"method\"-Attribut eines <xsl:output>-Elements hatte den Wert \"{0}\". Der Wert muss \"xml\", \"html\", \"text\" oder qname-but-not-ncname sein"},

        {ErrorMsg.JAXP_GET_FEATURE_NULL_NAME,
        "Der Featurename darf nicht null in TransformerFactory.getFeature(Zeichenfolgenname) sein."},

        {ErrorMsg.JAXP_SET_FEATURE_NULL_NAME,
        "Der Featurename darf nicht null in TransformerFactory.setFeature(Zeichenfolgenname, boolescher Wert) sein."},

        {ErrorMsg.JAXP_UNSUPPORTED_FEATURE,
        "Das Feature \"{0}\" kann nicht f\u00FCr diese TransformerFactory festgelegt werden."},

        {ErrorMsg.JAXP_SECUREPROCESSING_FEATURE,
        "FEATURE_SECURE_PROCESSING: Feature kann nicht auf \"false\" gesetzt werden, wenn Security Manager vorhanden ist."},


        {ErrorMsg.OUTLINE_ERR_TRY_CATCH,
         "Interner XSLTC-Fehler: Der generierte Bytecode enth\u00E4lt einen Try-Catch-Finally-Block. Outline nicht m\u00F6glich."},


        {ErrorMsg.OUTLINE_ERR_UNBALANCED_MARKERS,
         "Interner XSLTC-Fehler: Die Marker OutlineableChunkStart und OutlineableChunkEnd m\u00FCssen ausgeglichen und ordnungsgem\u00E4\u00DF platziert sein."},


        {ErrorMsg.OUTLINE_ERR_DELETED_TARGET,
         "Interner XSLTC-Fehler: Eine Anweisung, die Teil eines Bytecodeblocks war, f\u00FCr den ein Outline erstellt wurde, wird nach wie vor in der Originalmethode referenziert."
        },



        {ErrorMsg.OUTLINE_ERR_METHOD_TOO_BIG,
         "Interner XSLTC-Fehler: Eine Methode im Translet \u00FCberschreitet die Java Virtual Machine-L\u00E4ngeneinschr\u00E4nkung einer Methode von 64 KB. Ursache hierf\u00FCr sind in der Regel sehr gro\u00DFe Vorlagen in einem Stylesheet. Versuchen Sie, das Stylesheet mit kleineren Vorlagen umzustrukturieren."
        },

         {ErrorMsg.DESERIALIZE_TRANSLET_ERR, "Wenn die Java-Sicherheit aktiviert ist, ist die Unterst\u00FCtzung f\u00FCr das Deserialisieren von TemplatesImpl deaktiviert. Dies kann durch Setzen der Systemeigenschaft jdk.xml.enableTemplatesImplDeserialization auf \"True\" au\u00DFer Kraft gesetzt werden."}

    };

    }
}
