


package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import java.util.ListResourceBundle;


public class ErrorMessages_sv extends ListResourceBundle {



    public Object[][] getContents()
    {
      return new Object[][] {
        {ErrorMsg.MULTIPLE_STYLESHEET_ERR,
        "Fler \u00E4n en formatmall har definierats i samma fil."},


        {ErrorMsg.TEMPLATE_REDEF_ERR,
        "Mallen ''{0}'' har redan definierats i denna formatmall."},



        {ErrorMsg.TEMPLATE_UNDEF_ERR,
        "Mallen ''{0}'' har inte definierats i denna formatmall."},


        {ErrorMsg.VARIABLE_REDEF_ERR,
        "Variabeln ''{0}'' har definierats flera g\u00E5nger i samma omfattning."},


        {ErrorMsg.VARIABLE_UNDEF_ERR,
        "Variabeln eller parametern ''{0}'' har inte definierats."},


        {ErrorMsg.CLASS_NOT_FOUND_ERR,
        "Hittar inte klassen ''{0}''."},


        {ErrorMsg.METHOD_NOT_FOUND_ERR,
        "Hittar inte den externa metoden ''{0}'' (m\u00E5ste vara allm\u00E4n)."},


        {ErrorMsg.ARGUMENT_CONVERSION_ERR,
        "Kan inte konvertera argument/returtyp vid anrop till metoden ''{0}''"},


        {ErrorMsg.FILE_NOT_FOUND_ERR,
        "Fil eller URI ''{0}'' hittades inte."},


        {ErrorMsg.INVALID_URI_ERR,
        "Ogiltig URI ''{0}''."},


        {ErrorMsg.FILE_ACCESS_ERR,
        "Kan inte \u00F6ppna filen eller URI ''{0}''."},


        {ErrorMsg.MISSING_ROOT_ERR,
        "F\u00F6rv\u00E4ntade <xsl:stylesheet>- eller <xsl:transform>-element."},


        {ErrorMsg.NAMESPACE_UNDEF_ERR,
        "Namnrymdsprefixet ''{0}'' har inte deklarerats."},


        {ErrorMsg.FUNCTION_RESOLVE_ERR,
        "Kan inte matcha anrop till funktionen ''{0}''."},


        {ErrorMsg.NEED_LITERAL_ERR,
        "Argument till ''{0}'' m\u00E5ste vara en litteral str\u00E4ng."},


        {ErrorMsg.XPATH_PARSER_ERR,
        "Fel vid tolkning av XPath-uttrycket ''{0}''."},


        {ErrorMsg.REQUIRED_ATTR_ERR,
        "Det obligatoriska attributet ''{0}'' saknas."},


        {ErrorMsg.ILLEGAL_CHAR_ERR,
        "Otill\u00E5tet tecken ''{0}'' i XPath-uttrycket."},


        {ErrorMsg.ILLEGAL_PI_ERR,
        "''{0}'' \u00E4r ett otill\u00E5tet namn i bearbetningsinstruktion."},


        {ErrorMsg.STRAY_ATTRIBUTE_ERR,
        "Attributet ''{0}'' finns utanf\u00F6r elementet."},


        {ErrorMsg.ILLEGAL_ATTRIBUTE_ERR,
        "''{0}'' \u00E4r ett otill\u00E5tet attribut."},


        {ErrorMsg.CIRCULAR_INCLUDE_ERR,
        "Cirkul\u00E4r import/include. Formatmallen ''{0}'' har redan laddats."},


        {ErrorMsg.RESULT_TREE_SORT_ERR,
        "Resultattr\u00E4dfragment kan inte sorteras (<xsl:sort>-element ignoreras). Du m\u00E5ste sortera noderna n\u00E4r resultattr\u00E4det skapas."},


        {ErrorMsg.SYMBOLS_REDEF_ERR,
        "Decimalformateringen ''{0}'' har redan definierats."},


        {ErrorMsg.XSL_VERSION_ERR,
        "XSL-versionen ''{0}'' underst\u00F6ds inte i XSLTC."},


        {ErrorMsg.CIRCULAR_VARIABLE_ERR,
        "Cirkul\u00E4r variabel-/parameterreferens i ''{0}''."},


        {ErrorMsg.ILLEGAL_BINARY_OP_ERR,
        "Ok\u00E4nd operator f\u00F6r bin\u00E4rt uttryck."},


        {ErrorMsg.ILLEGAL_ARG_ERR,
        "Otill\u00E5tna argument f\u00F6r funktionsanrop."},


        {ErrorMsg.DOCUMENT_ARG_ERR,
        "Andra argumentet f\u00F6r document()-funktion m\u00E5ste vara en nodupps\u00E4ttning."},


        {ErrorMsg.MISSING_WHEN_ERR,
        "Minst ett <xsl:when>-element kr\u00E4vs i <xsl:choose>."},


        {ErrorMsg.MULTIPLE_OTHERWISE_ERR,
        "Endast ett <xsl:otherwise>-element \u00E4r till\u00E5tet i <xsl:choose>."},


        {ErrorMsg.STRAY_OTHERWISE_ERR,
        "<xsl:otherwise> anv\u00E4nds endast inom <xsl:choose>."},


        {ErrorMsg.STRAY_WHEN_ERR,
        "<xsl:when> anv\u00E4nds endast inom <xsl:choose>."},


        {ErrorMsg.WHEN_ELEMENT_ERR,
        "Endast <xsl:when>- och <xsl:otherwise>-element \u00E4r till\u00E5tna i <xsl:choose>."},


        {ErrorMsg.UNNAMED_ATTRIBSET_ERR,
        "<xsl:attribute-set> saknar 'name'-attribut."},


        {ErrorMsg.ILLEGAL_CHILD_ERR,
        "Otill\u00E5tet underordnat element."},


        {ErrorMsg.ILLEGAL_ELEM_NAME_ERR,
        "Du kan inte anropa elementet ''{0}''"},


        {ErrorMsg.ILLEGAL_ATTR_NAME_ERR,
        "Du kan inte anropa attributet ''{0}''"},


        {ErrorMsg.ILLEGAL_TEXT_NODE_ERR,
        "Textdata utanf\u00F6r toppniv\u00E5elementet <xsl:stylesheet>."},


        {ErrorMsg.SAX_PARSER_CONFIG_ERR,
        "JAXP-parser har inte konfigurerats korrekt"},


        {ErrorMsg.INTERNAL_ERR,
        "O\u00E5terkalleligt internt XSLTC-fel: ''{0}''"},


        {ErrorMsg.UNSUPPORTED_XSL_ERR,
        "XSL-elementet ''{0}'' st\u00F6ds inte."},


        {ErrorMsg.UNSUPPORTED_EXT_ERR,
        "XSLTC-till\u00E4gget ''{0}'' \u00E4r ok\u00E4nt."},


        {ErrorMsg.MISSING_XSLT_URI_ERR,
        "Indatadokumentet \u00E4r ingen formatmall (XSL-namnrymden har inte deklarerats i rotelementet)."},


        {ErrorMsg.MISSING_XSLT_TARGET_ERR,
        "Hittade inte formatmallen ''{0}''."},


        {ErrorMsg.ACCESSING_XSLT_TARGET_ERR,
        "Kunde inte l\u00E4sa formatmallen ''{0}'', eftersom ''{1}''-\u00E5tkomst inte till\u00E5ts p\u00E5 grund av begr\u00E4nsning som anges av egenskapen accessExternalStylesheet."},


        {ErrorMsg.NOT_IMPLEMENTED_ERR,
        "Inte implementerad: ''{0}''."},


        {ErrorMsg.NOT_STYLESHEET_ERR,
        "Indatadokumentet inneh\u00E5ller ingen XSL-formatmall."},


        {ErrorMsg.ELEMENT_PARSE_ERR,
        "Kunde inte tolka elementet ''{0}''"},


        {ErrorMsg.KEY_USE_ATTR_ERR,
        "use-attribut f\u00F6r <key> m\u00E5ste vara node, node-set, string eller number."},


        {ErrorMsg.OUTPUT_VERSION_ERR,
        "XML-dokumentets utdataversion m\u00E5ste vara 1.0"},


        {ErrorMsg.ILLEGAL_RELAT_OP_ERR,
        "Ok\u00E4nd operator f\u00F6r relationsuttryck"},


        {ErrorMsg.ATTRIBSET_UNDEF_ERR,
        "F\u00F6rs\u00F6ker anv\u00E4nda en icke-befintlig attributupps\u00E4ttning ''{0}''."},


        {ErrorMsg.ATTR_VAL_TEMPLATE_ERR,
        "Kan inte tolka attributv\u00E4rdemallen ''{0}''."},


        {ErrorMsg.UNKNOWN_SIG_TYPE_ERR,
        "Ok\u00E4nd datatyp i signaturen f\u00F6r klassen ''{0}''."},


        {ErrorMsg.DATA_CONVERSION_ERR,
        "Kan inte konvertera datatyp ''{0}'' till ''{1}''."},


        {ErrorMsg.NO_TRANSLET_CLASS_ERR,
        "Templates inneh\u00E5ller inte n\u00E5gon giltig klassdefinition f\u00F6r translet."},


        {ErrorMsg.NO_MAIN_TRANSLET_ERR,
        "Templates inneh\u00E5ller inte n\u00E5gon klass med namnet {0}."},


        {ErrorMsg.TRANSLET_CLASS_ERR,
        "Kunde inte ladda translet-klassen ''{0}''."},

        {ErrorMsg.TRANSLET_OBJECT_ERR,
        "Translet-klassen har laddats, men kan inte skapa instans av translet."},


        {ErrorMsg.ERROR_LISTENER_NULL_ERR,
        "F\u00F6rs\u00F6ker st\u00E4lla in ErrorListener f\u00F6r ''{0}'' p\u00E5 null"},


        {ErrorMsg.JAXP_UNKNOWN_SOURCE_ERR,
        "Endast StreamSource, SAXSource och DOMSource st\u00F6ds av XSLTC"},


        {ErrorMsg.JAXP_NO_SOURCE_ERR,
        "Source-objektet som \u00F6verf\u00F6rdes till ''{0}'' saknar inneh\u00E5ll."},


        {ErrorMsg.JAXP_COMPILE_ERR,
        "Kunde inte kompilera formatmall"},


        {ErrorMsg.JAXP_INVALID_ATTR_ERR,
        "TransformerFactory k\u00E4nner inte igen attributet ''{0}''."},

        {ErrorMsg.JAXP_INVALID_ATTR_VALUE_ERR,
        "Fel v\u00E4rde har angetts f\u00F6r attributet ''{0}''."},


        {ErrorMsg.JAXP_SET_RESULT_ERR,
        "setResult() m\u00E5ste anropas f\u00F6re startDocument()."},


        {ErrorMsg.JAXP_NO_TRANSLET_ERR,
        "Transformer saknar inkapslat objekt f\u00F6r translet."},


        {ErrorMsg.JAXP_NO_HANDLER_ERR,
        "Det finns ingen definierad utdatahanterare f\u00F6r transformeringsresultat."},


        {ErrorMsg.JAXP_NO_RESULT_ERR,
        "Result-objekt som \u00F6verf\u00F6rdes till ''{0}'' \u00E4r ogiltigt."},


        {ErrorMsg.JAXP_UNKNOWN_PROP_ERR,
        "F\u00F6rs\u00F6ker f\u00E5 \u00E5tkomst till ogiltig Transformer-egenskap, ''{0}''."},


        {ErrorMsg.SAX2DOM_ADAPTER_ERR,
        "Kunde inte skapa SAX2DOM-adapter: ''{0}''."},


        {ErrorMsg.XSLTC_SOURCE_ERR,
        "XSLTCSource.build() anropades utan angivet systemId."},

        { ErrorMsg.ER_RESULT_NULL,
            "Result borde inte vara null"},


        {ErrorMsg.JAXP_INVALID_SET_PARAM_VALUE,
        "Parameterv\u00E4rdet f\u00F6r {0} m\u00E5ste vara giltigt Java-objekt"},


        {ErrorMsg.COMPILE_STDIN_ERR,
        "Alternativet -i m\u00E5ste anv\u00E4ndas med alternativet -o."},



        {ErrorMsg.COMPILE_USAGE_STR,
        "SYNOPSIS\n   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Compile [-o <utdata>]\n      [-d <katalog>] [-j <jarfile>] [-p <paket>]\n      [-n] [-x] [-u] [-v] [-h] { <formatmall> | -i }\n\nALTERNATIV\n   -o <utdata>    tilldelar namnet <utdata> till genererad\n                  translet. Som standard tas namnet p\u00E5 translet\n                  fr\u00E5n namnet p\u00E5 <formatmallen>. Alternativet\n                  ignoreras vid kompilering av flera formatmallar.\n   -d <katalog> anger en destinationskatalog f\u00F6r translet\n   -j <jarfile>   paketerar transletklasserna i en jar-fil med\n                  namnet <jarfile>\n   -p <paket>   anger ett paketnamnprefix f\u00F6r alla genererade\n                  transletklasser.\n   -n             aktiverar mallinfogning (ger ett b\u00E4ttre genomsnittligt\n                  standardbeteende).\n   -x             ger ytterligare fels\u00F6kningsmeddelanden\n   -u             tolkar argument i <formatmall> som URL:er\n   -i             tvingar kompilatorn att l\u00E4sa formatmallen fr\u00E5n stdin\n   -v             skriver ut kompilatorns versionsnummer\n   -h             skriver ut denna syntaxsats\n"},


        {ErrorMsg.TRANSFORM_USAGE_STR,
        "SYNOPSIS \n   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Transform [-j <jarfile>]\n      [-x] [-n <iterationer>] {-u <dokument_url> | <dokument>}\n      <klass> [<param1>=<v\u00E4rde1> ...]\n\n   anv\u00E4nder translet <klass> vid transformering av XML-dokument \n   angivna som <dokument>. Translet-<klass> finns antingen i\n   anv\u00E4ndarens CLASSPATH eller i valfritt angiven <jarfile>.\nALTERNATIV\n   -j <jarfile>    anger en jar-fil varifr\u00E5n translet laddas\n   -x              ger ytterligare fels\u00F6kningsmeddelanden\n   -n <iterationer> k\u00F6r <iterations>-tider vid transformering och\n                   visar profileringsinformation\n   -u <dokument_url> anger XML-indatadokument som URL\n"},




        {ErrorMsg.STRAY_SORT_ERR,
        "<xsl:sort> kan anv\u00E4ndas endast i <xsl:for-each> eller <xsl:apply-templates>."},


        {ErrorMsg.UNSUPPORTED_ENCODING,
        "Utdatakodning ''{0}'' underst\u00F6ds inte i JVM."},


        {ErrorMsg.SYNTAX_ERR,
        "Syntaxfel i ''{0}''."},


        {ErrorMsg.CONSTRUCTOR_NOT_FOUND,
        "Hittar inte den externa konstruktorn ''{0}''."},


        {ErrorMsg.NO_JAVA_FUNCT_THIS_REF,
        "Det f\u00F6rsta argumentet f\u00F6r den icke-statiska Java-funktionen ''{0}'' \u00E4r inte n\u00E5gon giltig objektreferens."},


        {ErrorMsg.TYPE_CHECK_ERR,
        "Fel vid kontroll av typ av uttrycket ''{0}''."},


        {ErrorMsg.TYPE_CHECK_UNK_LOC_ERR,
        "Fel vid kontroll av typ av ett uttryck p\u00E5 ok\u00E4nd plats."},


        {ErrorMsg.ILLEGAL_CMDLINE_OPTION_ERR,
        "Ogiltigt kommandoradsalternativ: ''{0}''."},


        {ErrorMsg.CMDLINE_OPT_MISSING_ARG_ERR,
        "Kommandoradsalternativet ''{0}'' saknar obligatoriskt argument."},


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
        "Transformering via translet ''{0}'' "},


        {ErrorMsg.TRANSFORM_WITH_JAR_STR,
        "Transformering via translet ''{0}'' fr\u00E5n jar-filen ''{1}''"},


        {ErrorMsg.COULD_NOT_CREATE_TRANS_FACT,
        "Kunde inte skapa en instans av TransformerFactory-klassen ''{0}''."},


        {ErrorMsg.TRANSLET_NAME_JAVA_CONFLICT,
         "''{0}'' kunde inte anv\u00E4ndas som namn p\u00E5 transletklassen eftersom det inneh\u00E5ller otill\u00E5tna tecken f\u00F6r Java-klassnamn. Namnet ''{1}'' anv\u00E4ndes ist\u00E4llet."},


        {ErrorMsg.COMPILER_ERROR_KEY,
        "Kompileringsfel:"},


        {ErrorMsg.COMPILER_WARNING_KEY,
        "Kompileringsvarningar:"},


        {ErrorMsg.RUNTIME_ERROR_KEY,
        "Transletfel:"},


        {ErrorMsg.INVALID_QNAME_ERR,
        "Ett attribut vars v\u00E4rde m\u00E5ste vara ett QName eller en blankteckenavgr\u00E4nsad lista med QNames hade v\u00E4rdet ''{0}''"},


        {ErrorMsg.INVALID_NCNAME_ERR,
        "Ett attribut vars v\u00E4rde m\u00E5ste vara ett NCName hade v\u00E4rdet ''{0}''"},


        {ErrorMsg.INVALID_METHOD_IN_OUTPUT,
        "Metodattributet f\u00F6r ett <xsl:output>-element hade v\u00E4rdet ''{0}''. Endast n\u00E5got av f\u00F6ljande v\u00E4rden kan anv\u00E4ndas: ''xml'', ''html'', ''text'' eller qname-but-not-ncname i XML"},

        {ErrorMsg.JAXP_GET_FEATURE_NULL_NAME,
        "Funktionsnamnet kan inte vara null i TransformerFactory.getFeature(namn p\u00E5 str\u00E4ng)."},

        {ErrorMsg.JAXP_SET_FEATURE_NULL_NAME,
        "Funktionsnamnet kan inte vara null i TransformerFactory.setFeature(namn p\u00E5 str\u00E4ng, booleskt v\u00E4rde)."},

        {ErrorMsg.JAXP_UNSUPPORTED_FEATURE,
        "Kan inte st\u00E4lla in funktionen ''{0}'' i denna TransformerFactory."},

        {ErrorMsg.JAXP_SECUREPROCESSING_FEATURE,
        "FEATURE_SECURE_PROCESSING: Funktionen kan inte anges till false om s\u00E4kerhetshanteraren anv\u00E4nds."},


        {ErrorMsg.OUTLINE_ERR_TRY_CATCH,
         "Internt XSLTC-fel: den genererade bytekoden inneh\u00E5ller ett try-catch-finally-block och kan inte g\u00F6ras till en disposition."},


        {ErrorMsg.OUTLINE_ERR_UNBALANCED_MARKERS,
         "Internt XSLTC-fel: mark\u00F6rerna OutlineableChunkStart och OutlineableChunkEnd m\u00E5ste vara balanserade och korrekt kapslade."},


        {ErrorMsg.OUTLINE_ERR_DELETED_TARGET,
         "Internt XSLTC-fel: originalmetoden refererar fortfarande till en instruktion som var en del av ett bytekodsblock som gjordes till en disposition."
        },



        {ErrorMsg.OUTLINE_ERR_METHOD_TOO_BIG,
         "Internt XSLTC-fel: en metod i transleten \u00F6verstiger Java Virtual Machines l\u00E4ngdbegr\u00E4nsning f\u00F6r en metod p\u00E5 64 kilobytes.  Det h\u00E4r orsakas vanligen av mycket stora mallar i en formatmall. F\u00F6rs\u00F6k att omstrukturera formatmallen att anv\u00E4nda mindre mallar."
        },

         {ErrorMsg.DESERIALIZE_TRANSLET_ERR, "N\u00E4r Java-s\u00E4kerheten \u00E4r aktiverad \u00E4r st\u00F6det f\u00F6r avserialisering av TemplatesImpl avaktiverat. Du kan \u00E5sidos\u00E4tta det h\u00E4r genom att st\u00E4lla in systemegenskapen jdk.xml.enableTemplatesImplDeserialization till sant."}

    };

    }
}
