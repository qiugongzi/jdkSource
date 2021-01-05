



package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import java.util.ListResourceBundle;


public class ErrorMessages_ca extends ListResourceBundle {



    public Object[][] getContents()
    {
      return new Object[][] {
        {ErrorMsg.MULTIPLE_STYLESHEET_ERR,
        "S'ha definit m\u00e9s d'un full d'estils en el mateix fitxer."},


        {ErrorMsg.TEMPLATE_REDEF_ERR,
        "La plantilla ''{0}'' ja est\u00e0 definida en aquest full d''estils."},



        {ErrorMsg.TEMPLATE_UNDEF_ERR,
        "La plantilla ''{0}'' no est\u00e0 definida en aquest full d''estils."},


        {ErrorMsg.VARIABLE_REDEF_ERR,
        "La variable ''{0}'' s''ha definit m\u00e9s d''una vegada en el mateix \u00e0mbit."},


        {ErrorMsg.VARIABLE_UNDEF_ERR,
        "La variable o el par\u00e0metre ''{0}'' no s''ha definit."},


        {ErrorMsg.CLASS_NOT_FOUND_ERR,
        "No s''ha trobat la classe ''{0}''."},


        {ErrorMsg.METHOD_NOT_FOUND_ERR,
        "No s''ha trobat el m\u00e8tode extern ''{0}'' (ha de ser public)."},


        {ErrorMsg.ARGUMENT_CONVERSION_ERR,
        "No s''ha pogut convertir l''argument o tipus de retorn a la crida del m\u00e8tode ''{0}''"},


        {ErrorMsg.FILE_NOT_FOUND_ERR,
        "No s''ha trobat el fitxer o URI ''{0}''."},


        {ErrorMsg.INVALID_URI_ERR,
        "L''URI ''{0}'' no \u00e9s v\u00e0lid."},


        {ErrorMsg.FILE_ACCESS_ERR,
        "No es pot obrir el fitxer o l''URI ''{0}''."},


        {ErrorMsg.MISSING_ROOT_ERR,
        "S''esperava l''element <xsl:stylesheet> o <xsl:transform>."},


        {ErrorMsg.NAMESPACE_UNDEF_ERR,
        "El prefix d''espai de noms ''{0}'' no s''ha declarat."},


        {ErrorMsg.FUNCTION_RESOLVE_ERR,
        "No s''ha pogut resoldre la crida de la funci\u00f3 ''{0}''."},


        {ErrorMsg.NEED_LITERAL_ERR,
        "L''argument de ''{0}'' ha de ser una cadena de literals."},


        {ErrorMsg.XPATH_PARSER_ERR,
        "S''ha produ\u00eft un error en analitzar l''expressi\u00f3 XPath ''{0}''."},


        {ErrorMsg.REQUIRED_ATTR_ERR,
        "No s''ha especificat l''atribut obligatori ''{0}''."},


        {ErrorMsg.ILLEGAL_CHAR_ERR,
        "L''expressi\u00f3 XPath cont\u00e9 el car\u00e0cter no perm\u00e8s ''{0}''."},


        {ErrorMsg.ILLEGAL_PI_ERR,
        "La instrucci\u00f3 de processament t\u00e9 el nom no perm\u00e8s ''{0}''."},


        {ErrorMsg.STRAY_ATTRIBUTE_ERR,
        "L''atribut ''{0}'' es troba fora de l''element."},


        {ErrorMsg.ILLEGAL_ATTRIBUTE_ERR,
        "No es permet l''atribut ''{0}''."},


        {ErrorMsg.CIRCULAR_INCLUDE_ERR,
        "Import/include circular. El full d''estils ''{0}'' ja s''ha carregat."},


        {ErrorMsg.RESULT_TREE_SORT_ERR,
        "Els fragments de l'arbre de resultats no es poden classificar (es passen per alt els elements <xsl:sort>). Heu de classificar els nodes quan creeu l'arbre de resultats. "},


        {ErrorMsg.SYMBOLS_REDEF_ERR,
        "El formatatge decimal ''{0}'' ja est\u00e0 definit."},


        {ErrorMsg.XSL_VERSION_ERR,
        "XSLTC no d\u00f3na suport a la versi\u00f3 XSL ''{0}''."},


        {ErrorMsg.CIRCULAR_VARIABLE_ERR,
        "Hi ha una refer\u00e8ncia de variable/par\u00e0metre circular a ''{0}''."},


        {ErrorMsg.ILLEGAL_BINARY_OP_ERR,
        "L'operador de l'expressi\u00f3 bin\u00e0ria \u00e9s desconegut."},


        {ErrorMsg.ILLEGAL_ARG_ERR,
        "La crida de funci\u00f3 t\u00e9 arguments no permesos."},


        {ErrorMsg.DOCUMENT_ARG_ERR,
        "El segon argument de la funci\u00f3 document() ha de ser un conjunt de nodes."},


        {ErrorMsg.MISSING_WHEN_ERR,
        "Es necessita com a m\u00ednim un element <xsl:when> a <xsl:choose>."},


        {ErrorMsg.MULTIPLE_OTHERWISE_ERR,
        "Nom\u00e9s es permet un element <xsl:otherwise> a <xsl:choose>."},


        {ErrorMsg.STRAY_OTHERWISE_ERR,
        "<xsl:otherwise> nom\u00e9s es pot utilitzar dins de <xsl:choose>."},


        {ErrorMsg.STRAY_WHEN_ERR,
        "<xsl:when> nom\u00e9s es pot utilitzar dins de <xsl:choose>."},


        {ErrorMsg.WHEN_ELEMENT_ERR,
        "A <xsl:choose> nom\u00e9s es permeten els elements <xsl:when> i <xsl:otherwise>."},


        {ErrorMsg.UNNAMED_ATTRIBSET_ERR,
        "L'atribut 'name' falta a <xsl:attribute-set>."},


        {ErrorMsg.ILLEGAL_CHILD_ERR,
        "L'element subordinat no \u00e9s perm\u00e8s."},


        {ErrorMsg.ILLEGAL_ELEM_NAME_ERR,
        "No podeu cridar un element ''{0}''"},


        {ErrorMsg.ILLEGAL_ATTR_NAME_ERR,
        "No podeu cridar un atribut ''{0}''"},


        {ErrorMsg.ILLEGAL_TEXT_NODE_ERR,
        "Hi ha dades fora de l'element de nivell superior <xsl:stylesheet>."},


        {ErrorMsg.SAX_PARSER_CONFIG_ERR,
        "L'analitzador JAXP no s'ha configurat correctament"},


        {ErrorMsg.INTERNAL_ERR,
        "S''ha produ\u00eft un error intern d''XSLTC irrecuperable: ''{0}''"},


        {ErrorMsg.UNSUPPORTED_XSL_ERR,
        "L''element d''XSL ''{0}'' no t\u00e9 suport."},


        {ErrorMsg.UNSUPPORTED_EXT_ERR,
        "No es reconeix l''extensi\u00f3 d''XSLTC ''{0}''."},


        {ErrorMsg.MISSING_XSLT_URI_ERR,
        "El document d'entrada no \u00e9s un full d'estils (l'espai de noms XSL no s'ha declarat en l'element arrel)."},


        {ErrorMsg.MISSING_XSLT_TARGET_ERR,
        "No s''ha trobat la destinaci\u00f3 ''{0}'' del full d''estils."},


        {ErrorMsg.ACCESSING_XSLT_TARGET_ERR,
        "Could not read stylesheet target ''{0}'', because ''{1}'' access is not allowed."},


        {ErrorMsg.NOT_IMPLEMENTED_ERR,
        "No s''ha implementat ''{0}''."},


        {ErrorMsg.NOT_STYLESHEET_ERR,
        "El document d'entrada no cont\u00e9 cap full d'estils XSL."},


        {ErrorMsg.ELEMENT_PARSE_ERR,
        "No s''ha pogut analitzar l''element ''{0}''"},


        {ErrorMsg.KEY_USE_ATTR_ERR,
        "L'atribut use de <key> ha de ser node, node-set, string o number."},


        {ErrorMsg.OUTPUT_VERSION_ERR,
        "La versi\u00f3 del document XML de sortida ha de ser 1.0"},


        {ErrorMsg.ILLEGAL_RELAT_OP_ERR,
        "L'operador de l'expressi\u00f3 relacional \u00e9s desconegut."},


        {ErrorMsg.ATTRIBSET_UNDEF_ERR,
        "S''ha intentat utilitzar el conjunt d''atributs ''{0}'' que no existeix."},


        {ErrorMsg.ATTR_VAL_TEMPLATE_ERR,
        "No es pot analitzar la plantilla de valors d''atributs ''{0}''."},


        {ErrorMsg.UNKNOWN_SIG_TYPE_ERR,
        "El tipus de dades de la signatura de la classe ''{0}'' \u00e9s desconegut."},


        {ErrorMsg.DATA_CONVERSION_ERR,
        "No es pot convertir el tipus de dades ''{0}'' en ''{1}''."},


        {ErrorMsg.NO_TRANSLET_CLASS_ERR,
        "Templates no cont\u00e9 cap definici\u00f3 de classe translet."},


        {ErrorMsg.NO_MAIN_TRANSLET_ERR,
        "Templates no cont\u00e9 cap classe amb el nom ''{0}''."},


        {ErrorMsg.TRANSLET_CLASS_ERR,
        "No s''ha pogut carregar la classe translet ''{0}''."},

        {ErrorMsg.TRANSLET_OBJECT_ERR,
        "La classe translet s''ha carregat, per\u00f2 no es pot crear la inst\u00e0ncia translet."},


        {ErrorMsg.ERROR_LISTENER_NULL_ERR,
        "S''ha intentat establir ErrorListener de ''{0}'' en un valor nul."},


        {ErrorMsg.JAXP_UNKNOWN_SOURCE_ERR,
        "XSLTC nom\u00e9s d\u00f3na suport a StreamSource, SAXSource i DOMSource."},


        {ErrorMsg.JAXP_NO_SOURCE_ERR,
        "L''objecte source donat a ''{0}'' no t\u00e9 contingut."},


        {ErrorMsg.JAXP_COMPILE_ERR,
        "No s'ha pogut compilar el full d'estils."},


        {ErrorMsg.JAXP_INVALID_ATTR_ERR,
        "TransformerFactory no reconeix l''atribut ''{0}''."},


        {ErrorMsg.JAXP_SET_RESULT_ERR,
        "setResult() s'ha de cridar abans de startDocument()."},


        {ErrorMsg.JAXP_NO_TRANSLET_ERR,
        "Transformer no cont\u00e9 cap objecte translet."},


        {ErrorMsg.JAXP_NO_HANDLER_ERR,
        "No s'ha definit cap manejador de sortida per al resultat de transformaci\u00f3."},


        {ErrorMsg.JAXP_NO_RESULT_ERR,
        "L''objecte result donat a ''{0}'' no \u00e9s v\u00e0lid."},


        {ErrorMsg.JAXP_UNKNOWN_PROP_ERR,
        "S''ha intentat accedir a una propietat Transformer ''{0}'' no v\u00e0lida."},


        {ErrorMsg.SAX2DOM_ADAPTER_ERR,
        "No s''ha pogut crear l''adaptador SAX2DOM ''{0}''."},


        {ErrorMsg.XSLTC_SOURCE_ERR,
        "S'ha cridat XSLTCSource.build() sense que s'hagu\u00e9s establert la identificaci\u00f3 del sistema."},


        {ErrorMsg.COMPILE_STDIN_ERR,
        "L'opci\u00f3 -i s'ha d'utilitzar amb l'opci\u00f3 -o."},



        {ErrorMsg.COMPILE_USAGE_STR,
        "RESUM\n   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Compile [-o <sortida>]\n      [-d <directori>] [-j <fitxer_jar>] [-p <paquet>]\n      [-n] [-x] [-s] [-u] [-v] [-h] { <full_estils> |  -i }\n\nOPCIONS\n   -o <sortida>    assigna el nom <sortida> al translet\n generat. Per defecte, el nom de translet\n s'obt\u00e9 del nom de <full_estils>. Aquesta opci\u00f3\n no es t\u00e9 en compte si es compilen diversos fulls d'estils.\n   -d <directori> especifica un directori de destinaci\u00f3 per al translet\n   -j <fitxer_jar>   empaqueta les classes translet en un fitxer jar del nom\n                  especificat com a <fitxer_jar>\n   -p <paquet> especifica un prefix de nom de paquet per a totes les classes\n                  translet generades.\n -n habilita l'inlining (com a mitjana, el funcionament per defecte\n \u00e9s millor).\n   -x            habilita la sortida de missatges de depuraci\u00f3 addicionals\n   -s inhabilita la crida de System.exit\n   -u             interpreta els arguments <full_estils> com URL\n -i obliga el compilador a llegir el full d'estils des de l'entrada est\u00e0ndard\n   -v imprimeix la versi\u00f3 del compilador\n   -h             imprimeix aquesta sent\u00e8ncia d'\u00fas.\n"},


        {ErrorMsg.TRANSFORM_USAGE_STR,
        "RESUM \n   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Transform [-j <fitxer_jar>]\n      [-x] [-s] [-n <iteracions>] {-u <url_document> | <document>}\n      <classe> [<par\u00e0m1>=<valor1> ...]\n\n   utilitza la <classe> translet per transformar un document XML\n   especificat com a <document>. La <classe> translet es troba\n   o b\u00e9 a la CLASSPATH de l'usuari o b\u00e9 al <fitxer_jar> que es pot especificar opcionalment.\nOPCIONS\n   -j <fitxer_jar>    especifica un fitxer jar des del qual es pot carregar el translet\n   -x habilita la sortida de missatges de depuraci\u00f3 addicionals\n   -s              inhabilita la crida de System.exit\n   -n <iteracions> executa la transformaci\u00f3 el nombre de vegades <iteracions> i\n               mostra informaci\u00f3 de perfil\n   -u <url_document> especifica el document d'entrada XML com una URL\n"},




        {ErrorMsg.STRAY_SORT_ERR,
        "<xsl:sort> nom\u00e9s es pot utilitzar amb <xsl:for-each> o <xsl:apply-templates>."},


        {ErrorMsg.UNSUPPORTED_ENCODING,
        "Aquesta JVM no d\u00f3na suport a la codificaci\u00f3 de sortida ''{0}''."},


        {ErrorMsg.SYNTAX_ERR,
        "S''ha produ\u00eft un error de sintaxi a ''{0}''."},


        {ErrorMsg.CONSTRUCTOR_NOT_FOUND,
        "No s''ha trobat el constructor extern ''{0}''."},


        {ErrorMsg.NO_JAVA_FUNCT_THIS_REF,
        "El primer argument de la funci\u00f3 Java no static ''{0}'' no \u00e9s una refer\u00e8ncia d''objecte v\u00e0lida."},


        {ErrorMsg.TYPE_CHECK_ERR,
        "S''ha produ\u00eft un error en comprovar el tipus de l''expressi\u00f3 ''{0}''."},


        {ErrorMsg.TYPE_CHECK_UNK_LOC_ERR,
        "S'ha produ\u00eft un error en comprovar el tipus d'expressi\u00f3 en una ubicaci\u00f3 desconeguda."},


        {ErrorMsg.ILLEGAL_CMDLINE_OPTION_ERR,
        "L''opci\u00f3 de l\u00ednia d''ordres ''{0}'' no \u00e9s v\u00e0lida."},


        {ErrorMsg.CMDLINE_OPT_MISSING_ARG_ERR,
        "A l''opci\u00f3 de l\u00ednia d''ordres ''{0}'' li falta un argument obligatori."},


        {ErrorMsg.WARNING_PLUS_WRAPPED_MSG,
        "AV\u00cdS: ''{0}''\n       :{1}"},


        {ErrorMsg.WARNING_MSG,
        "AV\u00cdS: ''{0}''"},


        {ErrorMsg.FATAL_ERR_PLUS_WRAPPED_MSG,
        "ERROR MOLT GREU: ''{0}''\n           :{1}"},


        {ErrorMsg.FATAL_ERR_MSG,
        "ERROR MOLT GREU: ''{0}''"},


        {ErrorMsg.ERROR_PLUS_WRAPPED_MSG,
        "ERROR:  ''{0}''\n     :{1}"},


        {ErrorMsg.ERROR_MSG,
        "ERROR:  ''{0}''"},


        {ErrorMsg.TRANSFORM_WITH_TRANSLET_STR,
        "Transformaci\u00f3 mitjan\u00e7ant translet ''{0}'' "},


        {ErrorMsg.TRANSFORM_WITH_JAR_STR,
        "Transformaci\u00f3 mitjan\u00e7ant translet ''{0}'' des del fitxer jar ''{1}''"},


        {ErrorMsg.COULD_NOT_CREATE_TRANS_FACT,
        "No s''ha pogut crear una inst\u00e0ncia de la classe TransformerFactory ''{0}''."},


        {ErrorMsg.COMPILER_ERROR_KEY,
        "Errors del compilador:"},


        {ErrorMsg.COMPILER_WARNING_KEY,
        "Avisos del compilador:"},


        {ErrorMsg.RUNTIME_ERROR_KEY,
        "Errors de translet:"},

        {ErrorMsg.JAXP_SECUREPROCESSING_FEATURE,
        "FEATURE_SECURE_PROCESSING: Cannot set the feature to false when security manager is present."}
    };

    }
}
