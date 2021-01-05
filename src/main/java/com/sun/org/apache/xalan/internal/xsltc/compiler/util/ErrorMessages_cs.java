



package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import java.util.ListResourceBundle;


public class ErrorMessages_cs extends ListResourceBundle {



    public Object[][] getContents()
    {
      return new Object[][] {
        {ErrorMsg.MULTIPLE_STYLESHEET_ERR,
        "V\u00edce ne\u017e jedna p\u0159edloha stylu je definov\u00e1na ve stejn\u00e9m souboru."},


        {ErrorMsg.TEMPLATE_REDEF_ERR,
        "\u0160ablona ''{0}'' je ji\u017e v t\u00e9to p\u0159edloze stylu definov\u00e1na."},



        {ErrorMsg.TEMPLATE_UNDEF_ERR,
        "\u0160ablona ''{0}'' nen\u00ed v t\u00e9to p\u0159edloze stylu definov\u00e1na."},


        {ErrorMsg.VARIABLE_REDEF_ERR,
        "Prom\u011bnn\u00e1 ''{0}'' je n\u011bkolikan\u00e1sobn\u011b definov\u00e1na ve stejn\u00e9m oboru."},


        {ErrorMsg.VARIABLE_UNDEF_ERR,
        "Prom\u011bnn\u00e1 nebo parametr ''{0}'' nejsou definov\u00e1ny."},


        {ErrorMsg.CLASS_NOT_FOUND_ERR,
        "Nelze naj\u00edt t\u0159\u00eddu ''{0}''."},


        {ErrorMsg.METHOD_NOT_FOUND_ERR,
        "Nelze naj\u00edt extern\u00ed metodu ''{0}'' (mus\u00ed b\u00fdt ve\u0159ejn\u00e1)."},


        {ErrorMsg.ARGUMENT_CONVERSION_ERR,
        "Nelze p\u0159ev\u00e9st argument/n\u00e1vratov\u00fd typ ve vol\u00e1n\u00ed metody ''{0}''"},


        {ErrorMsg.FILE_NOT_FOUND_ERR,
        "Soubor nebo URI ''{0}'' nebyl nalezen."},


        {ErrorMsg.INVALID_URI_ERR,
        "Neplatn\u00e9 URI ''{0}''."},


        {ErrorMsg.FILE_ACCESS_ERR,
        "Nelze otev\u0159\u00edt soubor nebo URI ''{0}''."},


        {ErrorMsg.MISSING_ROOT_ERR,
        "Byl o\u010dek\u00e1v\u00e1n prvek <xsl:stylesheet> nebo <xsl:transform>."},


        {ErrorMsg.NAMESPACE_UNDEF_ERR,
        "P\u0159edpona oboru n\u00e1zv\u016f ''{0}'' nen\u00ed deklarov\u00e1na."},


        {ErrorMsg.FUNCTION_RESOLVE_ERR,
        "Nelze vy\u0159e\u0161it vol\u00e1n\u00ed funkce ''{0}''."},


        {ErrorMsg.NEED_LITERAL_ERR,
        "Argument pro ''{0}'' mus\u00ed b\u00fdt \u0159et\u011bzcem liter\u00e1lu."},


        {ErrorMsg.XPATH_PARSER_ERR,
        "Chyba p\u0159i anal\u00fdze v\u00fdrazu XPath ''{0}''."},


        {ErrorMsg.REQUIRED_ATTR_ERR,
        "Po\u017eadovan\u00fd atribut ''{0}'' chyb\u00ed."},


        {ErrorMsg.ILLEGAL_CHAR_ERR,
        "Neplatn\u00fd znak ''{0}'' ve v\u00fdrazu XPath."},


        {ErrorMsg.ILLEGAL_PI_ERR,
        "Neplatn\u00fd n\u00e1zev ''{0}'' pro zpracov\u00e1n\u00ed instrukce."},


        {ErrorMsg.STRAY_ATTRIBUTE_ERR,
        "Atribut ''{0}'' je vn\u011b prvku."},


        {ErrorMsg.ILLEGAL_ATTRIBUTE_ERR,
        "Neplatn\u00fd atribut ''{0}''."},


        {ErrorMsg.CIRCULAR_INCLUDE_ERR,
        "Cyklick\u00fd import/zahrnut\u00ed. P\u0159edloha stylu ''{0}'' je ji\u017e zavedena."},


        {ErrorMsg.RESULT_TREE_SORT_ERR,
        "Fragmenty stromu v\u00fdsledk\u016f nemohou b\u00fdt \u0159azeny (prvky <xsl:sort> se ignoruj\u00ed). P\u0159i vytv\u00e1\u0159en\u00ed stromu v\u00fdsledk\u016f mus\u00edte se\u0159adit uzly."},


        {ErrorMsg.SYMBOLS_REDEF_ERR,
        "Desetinn\u00e9 form\u00e1tov\u00e1n\u00ed ''{0}'' je ji\u017e definov\u00e1no."},


        {ErrorMsg.XSL_VERSION_ERR,
        "Verze XSL ''{0}'' nen\u00ed produktem XSLTC podporov\u00e1na."},


        {ErrorMsg.CIRCULAR_VARIABLE_ERR,
        "Cyklick\u00fd odkaz na prom\u011bnnou/parametr v ''{0}''."},


        {ErrorMsg.ILLEGAL_BINARY_OP_ERR,
        "Nezn\u00e1m\u00fd oper\u00e1tor pro bin\u00e1rn\u00ed v\u00fdraz."},


        {ErrorMsg.ILLEGAL_ARG_ERR,
        "Neplatn\u00fd argument pro vol\u00e1n\u00ed funkce."},


        {ErrorMsg.DOCUMENT_ARG_ERR,
        "Druh\u00fd argument pro funkci document() mus\u00ed b\u00fdt node-set."},


        {ErrorMsg.MISSING_WHEN_ERR,
        "Alespo\u0148 jeden prvek <xsl:when> se vy\u017eaduje v <xsl:choose>."},


        {ErrorMsg.MULTIPLE_OTHERWISE_ERR,
        "Jen jeden prvek <xsl:otherwise> je povolen v <xsl:choose>."},


        {ErrorMsg.STRAY_OTHERWISE_ERR,
        "Prvek <xsl:otherwise> m\u016f\u017ee b\u00fdt pou\u017eit jen v <xsl:choose>."},


        {ErrorMsg.STRAY_WHEN_ERR,
        "Prvek <xsl:when> m\u016f\u017ee b\u00fdt pou\u017eit jen v <xsl:choose>."},


        {ErrorMsg.WHEN_ELEMENT_ERR,
        "Pouze prvky <xsl:when> a <xsl:otherwise> jsou povoleny v <xsl:choose>."},


        {ErrorMsg.UNNAMED_ATTRIBSET_ERR,
        "V prvku <xsl:attribute-set> chyb\u00ed atribut 'name'."},


        {ErrorMsg.ILLEGAL_CHILD_ERR,
        "Neplatn\u00fd prvek potomka."},


        {ErrorMsg.ILLEGAL_ELEM_NAME_ERR,
        "Nelze volat prvek ''{0}''"},


        {ErrorMsg.ILLEGAL_ATTR_NAME_ERR,
        "Nelze volat atribut ''{0}''"},


        {ErrorMsg.ILLEGAL_TEXT_NODE_ERR,
        "Textov\u00e1 data jsou vn\u011b prvku nejvy\u0161\u0161\u00ed \u00farovn\u011b <xsl:stylesheet>."},


        {ErrorMsg.SAX_PARSER_CONFIG_ERR,
        "Analyz\u00e1tor JAXP je nespr\u00e1vn\u011b konfigurov\u00e1n."},


        {ErrorMsg.INTERNAL_ERR,
        "Neopraviteln\u00e1 chyba XSLTC-internal: ''{0}''"},


        {ErrorMsg.UNSUPPORTED_XSL_ERR,
        "Nepodporovan\u00fd prvek XSL ''{0}''."},


        {ErrorMsg.UNSUPPORTED_EXT_ERR,
        "Nerozpoznan\u00e1 p\u0159\u00edpona XSLTC ''{0}''."},


        {ErrorMsg.MISSING_XSLT_URI_ERR,
        "Vstupn\u00ed dokument nen\u00ed p\u0159edloha stylu (obor n\u00e1zv\u016f XSL nen\u00ed deklarov\u00e1n v ko\u0159enov\u00e9m elementu)."},


        {ErrorMsg.MISSING_XSLT_TARGET_ERR,
        "Nelze naj\u00edt c\u00edlovou p\u0159edlohu se stylem ''{0}''."},


        {ErrorMsg.ACCESSING_XSLT_TARGET_ERR,
        "Could not read stylesheet target ''{0}'', because ''{1}'' access is not allowed."},


        {ErrorMsg.NOT_IMPLEMENTED_ERR,
        "Neimplementov\u00e1no: ''{0}''."},


        {ErrorMsg.NOT_STYLESHEET_ERR,
        "Vstupn\u00ed dokument neobsahuje p\u0159edlohu stylu XSL."},


        {ErrorMsg.ELEMENT_PARSE_ERR,
        "Nelze analyzovat prvek ''{0}''"},


        {ErrorMsg.KEY_USE_ATTR_ERR,
        "Atribut use prom\u011bnn\u00e9 <key> mus\u00ed b\u00fdt typu node, node-set, string nebo number."},


        {ErrorMsg.OUTPUT_VERSION_ERR,
        "V\u00fdstupn\u00ed verze dokumentu XML by m\u011bla b\u00fdt 1.0"},


        {ErrorMsg.ILLEGAL_RELAT_OP_ERR,
        "Nezn\u00e1m\u00fd oper\u00e1tor pro rela\u010dn\u00ed v\u00fdraz"},


        {ErrorMsg.ATTRIBSET_UNDEF_ERR,
        "Pokus pou\u017e\u00edt neexistuj\u00edc\u00ed sadu atribut\u016f ''{0}''."},


        {ErrorMsg.ATTR_VAL_TEMPLATE_ERR,
        "Nelze analyzovat \u0161ablonu hodnoty atributu ''{0}''."},


        {ErrorMsg.UNKNOWN_SIG_TYPE_ERR,
        "Nezn\u00e1m\u00fd datov\u00fd typ prom\u011bnn\u00e9 signature pro t\u0159\u00eddu ''{0}''."},


        {ErrorMsg.DATA_CONVERSION_ERR,
        "Nelze p\u0159ev\u00e9st datov\u00fd typ ''{0}'' na ''{1}''."},


        {ErrorMsg.NO_TRANSLET_CLASS_ERR,
        "Tato \u0161ablona neobsahuje platnou definici t\u0159\u00eddy translet."},


        {ErrorMsg.NO_MAIN_TRANSLET_ERR,
        "Tato \u0161ablona neobsahuje t\u0159\u00eddu se jm\u00e9nem ''{0}''."},


        {ErrorMsg.TRANSLET_CLASS_ERR,
        "Nelze zav\u00e9st t\u0159\u00eddu translet ''{0}''."},

        {ErrorMsg.TRANSLET_OBJECT_ERR,
        "T\u0159\u00edda translet byla zavedena, av\u0161ak nelze vytvo\u0159it instanci translet."},


        {ErrorMsg.ERROR_LISTENER_NULL_ERR,
        "Pokus nastavit objekt ErrorListener pro ''{0}'' na hodnotu null"},


        {ErrorMsg.JAXP_UNKNOWN_SOURCE_ERR,
        "Pouze prom\u011bnn\u00e9 StreamSource, SAXSource a DOMSource jsou podporov\u00e1ny produktem XSLTC"},


        {ErrorMsg.JAXP_NO_SOURCE_ERR,
        "Zdrojov\u00fd objekt p\u0159edan\u00fd ''{0}'' nem\u00e1 \u017e\u00e1dn\u00fd obsah."},


        {ErrorMsg.JAXP_COMPILE_ERR,
        "Nelze kompilovat p\u0159edlohu se stylem"},


        {ErrorMsg.JAXP_INVALID_ATTR_ERR,
        "T\u0159\u00edda TransformerFactory nerozpoznala atribut ''{0}''."},


        {ErrorMsg.JAXP_SET_RESULT_ERR,
        "Metoda setResult() mus\u00ed b\u00fdt vol\u00e1na p\u0159ed metodou startDocument()."},


        {ErrorMsg.JAXP_NO_TRANSLET_ERR,
        "Objekt Transformer nem\u00e1 \u017e\u00e1dn\u00fd zapouzd\u0159en\u00fd objekt translet."},


        {ErrorMsg.JAXP_NO_HANDLER_ERR,
        "Neexistuje \u017e\u00e1dn\u00fd definovan\u00fd v\u00fdstupn\u00ed obslu\u017en\u00fd program pro v\u00fdsledek transformace."},


        {ErrorMsg.JAXP_NO_RESULT_ERR,
        "V\u00fdsledn\u00fd objekt p\u0159edan\u00fd ''{0}'' je neplatn\u00fd."},


        {ErrorMsg.JAXP_UNKNOWN_PROP_ERR,
        "Pokus o p\u0159\u00edstup k neplatn\u00e9 vlastnosti objektu Transformer: ''{0}''."},


        {ErrorMsg.SAX2DOM_ADAPTER_ERR,
        "Nelze vytvo\u0159it adapt\u00e9r SAX2DOM: ''{0}''."},


        {ErrorMsg.XSLTC_SOURCE_ERR,
        "Byla vol\u00e1na metoda XSLTCSource.build(), ani\u017e by byla nastavena hodnota systemId."},


        {ErrorMsg.COMPILE_STDIN_ERR,
        "Volba -i mus\u00ed b\u00fdt pou\u017eita s volbou -o."},



        {ErrorMsg.COMPILE_USAGE_STR,
        "SYNOPSIS\n   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Compile [-o <output>]\n      [-d <directory>] [-j <jarfile>] [-p <package>]\n      [-n] [-x] [-s] [-u] [-v] [-h] { <stylesheet> | -i }\n\nVOLBY\n   -o <output>    p\u0159i\u0159azuje n\u00e1zev <output> generovan\u00e9mu\n                  transletu. Standardn\u011b je n\u00e1zev transletu\n                  p\u0159evzat z n\u00e1zvu <stylesheet>. Tato volba\n                   se ignoruje, pokud se kompiluj\u00ed n\u00e1sobn\u00e9 p\u0159edlohy styl\u016f.\n   -d <directory> ur\u010duje v\u00fdchoz\u00ed adres\u00e1\u0159 pro translet\n   -j <jarfile>   zabal\u00ed t\u0159\u00eddu transletu do souboru jar\n     pojmenovan\u00e9ho jako <jarfile>\n   -p <package>   ur\u010duje p\u0159edponu n\u00e1zvu bal\u00ed\u010dku pro v\u0161echny generovan\u00e9 \n t\u0159\u00eddy transletu.\n   -n             povoluje zarovn\u00e1n\u00ed \u0161ablony (v\u00fdchoz\u00ed chov\u00e1n\u00ed je v pr\u016fm\u011bru lep\u0161\u00ed\n                  .\n   -x             zapne dal\u0161\u00ed v\u00fdstup zpr\u00e1vy lad\u011bn\u00ed\n   -s             zak\u00e1\u017ee vol\u00e1n\u00ed System.exit\n   -u             interpretuje <stylesheet> argumenty jako URL\n   -i             vynut\u00ed kompil\u00e1tor \u010d\u00edst p\u0159edlohu styl\u016f ze stdin\n   -v             tiskne verzi kompil\u00e1toru \n   -h             tiskne v\u00fdpis tohoto pou\u017eit\u00ed \n"},


        {ErrorMsg.TRANSFORM_USAGE_STR,
        "SYNOPSIS \n   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Transform [-j <jarfile>]\n      [-x] [-s] [-n <iterations>] {-u <document_url> | <document>}\n      <class> [<param1>=<value1> ...]\n\n   pou\u017eije translet <class> k transformaci dokumentu XML \n ur\u010den\u00e9ho jako <document>. Translet <class> je bu\u010f v\n   v u\u017eivatelsk\u00e9 cest\u011b CLASSPATH nebo ve voliteln\u011b ur\u010den\u00e9m souboru <jarfile>.\nVOLBY\n     -j <jarfile>    ur\u010duje soubor jarfile, ze kter\u00e9ho se zavede translet\n   -x      p\u0159evede dal\u0161\u00ed v\u00fdstup zpr\u00e1vy lad\u011bn\u00ed\n   -s              vypne vol\u00e1n\u00ed System.exit\n   -n <iterations> spust\u00ed transformaci <iterations> kr\u00e1t a\n                   zobraz\u00ed informaci  o profilu\n   -u <document_url> ur\u010d\u00ed vstupn\u00ed dokument XML jako URL\n"},




        {ErrorMsg.STRAY_SORT_ERR,
        "Prvek <xsl:sort> m\u016f\u017ee b\u00fdt pou\u017eit jen v <xsl:for-each> nebo <xsl:apply-templates>."},


        {ErrorMsg.UNSUPPORTED_ENCODING,
        "V\u00fdstupn\u00ed k\u00f3dov\u00e1n\u00ed ''{0}'' nen\u00ed v tomto prost\u0159ed\u00ed JVM podporov\u00e1no."},


        {ErrorMsg.SYNTAX_ERR,
        "Chyba syntaxe v ''{0}''."},


        {ErrorMsg.CONSTRUCTOR_NOT_FOUND,
        "Nelze naj\u00edt vn\u011bj\u0161\u00ed konstruktor ''{0}''."},


        {ErrorMsg.NO_JAVA_FUNCT_THIS_REF,
        "Prvn\u00ed argument nestatick\u00e9 funkce Java ''{0}'' nen\u00ed platn\u00fdm odkazem na objekt."},


        {ErrorMsg.TYPE_CHECK_ERR,
        "Chyba p\u0159i kontrole typu v\u00fdrazu ''{0}''."},


        {ErrorMsg.TYPE_CHECK_UNK_LOC_ERR,
        "Chyba p\u0159i kontrole typu v\u00fdrazu na nezn\u00e1m\u00e9m m\u00edst\u011b."},


        {ErrorMsg.ILLEGAL_CMDLINE_OPTION_ERR,
        "Volba p\u0159\u00edkazov\u00e9ho \u0159\u00e1dku ''{0}'' nen\u00ed platn\u00e1."},


        {ErrorMsg.CMDLINE_OPT_MISSING_ARG_ERR,
        "Volb\u011b p\u0159\u00edkazov\u00e9ho \u0159\u00e1dku ''{0}'' chyb\u00ed po\u017eadovan\u00fd argument."},


        {ErrorMsg.WARNING_PLUS_WRAPPED_MSG,
        "VAROV\u00c1N\u00cd: ''{0}''\n        :{1}"},


        {ErrorMsg.WARNING_MSG,
        "VAROV\u00c1N\u00cd: ''{0}''"},


        {ErrorMsg.FATAL_ERR_PLUS_WRAPPED_MSG,
        "Z\u00c1VA\u017dN\u00c1 CHYBA: ''{0}''\n             :{1}"},


        {ErrorMsg.FATAL_ERR_MSG,
        "Z\u00c1VA\u017dN\u00c1 CHYBA: ''{0}''"},


        {ErrorMsg.ERROR_PLUS_WRAPPED_MSG,
        "CHYBA: ''{0}''\n     :{1}"},


        {ErrorMsg.ERROR_MSG,
        "CHYBA: ''{0}''"},


        {ErrorMsg.TRANSFORM_WITH_TRANSLET_STR,
        "Transformace pou\u017eit\u00edm transletu ''{0}'' "},


        {ErrorMsg.TRANSFORM_WITH_JAR_STR,
        "Transformace pou\u017eit\u00edm transletu ''{0}'' ze souboru jar ''{1}''"},


        {ErrorMsg.COULD_NOT_CREATE_TRANS_FACT,
        "Nelze vytvo\u0159it instanci t\u0159\u00eddy TransformerFactory ''{0}''."},


        {ErrorMsg.COMPILER_ERROR_KEY,
        "Chyby kompil\u00e1toru:"},


        {ErrorMsg.COMPILER_WARNING_KEY,
        "Varov\u00e1n\u00ed kompil\u00e1toru:"},


        {ErrorMsg.RUNTIME_ERROR_KEY,
        "Chyby transletu:"},

        {ErrorMsg.JAXP_SECUREPROCESSING_FEATURE,
        "FEATURE_SECURE_PROCESSING: Cannot set the feature to false when security manager is present."}
    };

    }
}
