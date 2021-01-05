


package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import java.util.ListResourceBundle;


public class ErrorMessages_fr extends ListResourceBundle {



    public Object[][] getContents()
    {
      return new Object[][] {
        {ErrorMsg.MULTIPLE_STYLESHEET_ERR,
        "Plusieurs feuilles de style d\u00E9finies dans le m\u00EAme fichier."},


        {ErrorMsg.TEMPLATE_REDEF_ERR,
        "Mod\u00E8le ''{0}'' d\u00E9j\u00E0 d\u00E9fini dans cette feuille de style."},



        {ErrorMsg.TEMPLATE_UNDEF_ERR,
        "Mod\u00E8le ''{0}'' non d\u00E9fini dans cette feuille de style."},


        {ErrorMsg.VARIABLE_REDEF_ERR,
        "Plusieurs variables ''{0}'' d\u00E9finies dans la m\u00EAme port\u00E9e."},


        {ErrorMsg.VARIABLE_UNDEF_ERR,
        "La variable ou le param\u00E8tre ''{0}'' n''est pas d\u00E9fini."},


        {ErrorMsg.CLASS_NOT_FOUND_ERR,
        "Impossible de trouver la classe ''{0}''."},


        {ErrorMsg.METHOD_NOT_FOUND_ERR,
        "M\u00E9thode externe ''{0}'' introuvable (elle doit \u00EAtre \"public\")."},


        {ErrorMsg.ARGUMENT_CONVERSION_ERR,
        "Impossible de convertir le type de retour/d''argument dans l''appel de la m\u00E9thode ''{0}''"},


        {ErrorMsg.FILE_NOT_FOUND_ERR,
        "Fichier ou URI ''{0}'' introuvable."},


        {ErrorMsg.INVALID_URI_ERR,
        "URI ''{0}'' non valide."},


        {ErrorMsg.FILE_ACCESS_ERR,
        "Impossible d''ouvrir le fichier ou l''URI ''{0}''."},


        {ErrorMsg.MISSING_ROOT_ERR,
        "El\u00E9ment <xsl:stylesheet> ou <xsl:transform> attendu."},


        {ErrorMsg.NAMESPACE_UNDEF_ERR,
        "Le pr\u00E9fixe de l''espace de noms ''{0}'' n''a pas \u00E9t\u00E9 d\u00E9clar\u00E9."},


        {ErrorMsg.FUNCTION_RESOLVE_ERR,
        "Impossible de r\u00E9soudre l''appel de la fonction ''{0}''."},


        {ErrorMsg.NEED_LITERAL_ERR,
        "L''argument pour ''{0}'' doit \u00EAtre une cha\u00EEne litt\u00E9rale."},


        {ErrorMsg.XPATH_PARSER_ERR,
        "Erreur lors de l''analyse de l''expression XPath ''{0}''."},


        {ErrorMsg.REQUIRED_ATTR_ERR,
        "Attribut ''{0}'' obligatoire manquant."},


        {ErrorMsg.ILLEGAL_CHAR_ERR,
        "Caract\u00E8re ''{0}'' non admis dans l''expression XPath."},


        {ErrorMsg.ILLEGAL_PI_ERR,
        "Nom ''{0}'' non admis pour l''instruction de traitement."},


        {ErrorMsg.STRAY_ATTRIBUTE_ERR,
        "Attribut ''{0}'' \u00E0 l''ext\u00E9rieur de l''\u00E9l\u00E9ment."},


        {ErrorMsg.ILLEGAL_ATTRIBUTE_ERR,
        "Attribut ''{0}'' non admis."},


        {ErrorMsg.CIRCULAR_INCLUDE_ERR,
        "Op\u00E9ration import/include circulaire. La feuille de style ''{0}'' est d\u00E9j\u00E0 charg\u00E9e."},


        {ErrorMsg.RESULT_TREE_SORT_ERR,
        "Les fragments de l'arborescence de r\u00E9sultats ne peuvent pas \u00EAtre tri\u00E9s (les \u00E9l\u00E9ments <xsl:sort> ne sont pas pris en compte). Vous devez trier les noeuds lorsque vous cr\u00E9ez l'arborescence de r\u00E9sultats."},


        {ErrorMsg.SYMBOLS_REDEF_ERR,
        "Le formatage d\u00E9cimal ''{0}'' est d\u00E9j\u00E0 d\u00E9fini."},


        {ErrorMsg.XSL_VERSION_ERR,
        "La version XSL ''{0}'' n''est pas prise en charge par XSLTC."},


        {ErrorMsg.CIRCULAR_VARIABLE_ERR,
        "R\u00E9f\u00E9rence de param\u00E8tre/variable circulaire dans ''{0}''."},


        {ErrorMsg.ILLEGAL_BINARY_OP_ERR,
        "Op\u00E9rateur inconnu pour l'expression binaire."},


        {ErrorMsg.ILLEGAL_ARG_ERR,
        "Arguments non admis pour l'appel de la fonction."},


        {ErrorMsg.DOCUMENT_ARG_ERR,
        "Le deuxi\u00E8me argument de la fonction document() doit \u00EAtre un jeu de noeuds."},


        {ErrorMsg.MISSING_WHEN_ERR,
        "Au moins un \u00E9l\u00E9ment <xsl:when> est obligatoire dans <xsl:choose>."},


        {ErrorMsg.MULTIPLE_OTHERWISE_ERR,
        "Un seul \u00E9l\u00E9ment <xsl:otherwise> est autoris\u00E9 dans <xsl:choose>."},


        {ErrorMsg.STRAY_OTHERWISE_ERR,
        "<xsl:otherwise> ne peut \u00EAtre utilis\u00E9 que dans <xsl:choose>."},


        {ErrorMsg.STRAY_WHEN_ERR,
        "<xsl:when> ne peut \u00EAtre utilis\u00E9 que dans <xsl:choose>."},


        {ErrorMsg.WHEN_ELEMENT_ERR,
        "Seuls les \u00E9l\u00E9ments <xsl:when> et <xsl:otherwise> sont autoris\u00E9s dans <xsl:choose>."},


        {ErrorMsg.UNNAMED_ATTRIBSET_ERR,
        "Attribut \"name\" manquant dans <xsl:attribute-set>."},


        {ErrorMsg.ILLEGAL_CHILD_ERR,
        "El\u00E9ment enfant non admis."},


        {ErrorMsg.ILLEGAL_ELEM_NAME_ERR,
        "Vous ne pouvez pas appeler un \u00E9l\u00E9ment ''{0}''"},


        {ErrorMsg.ILLEGAL_ATTR_NAME_ERR,
        "Vous ne pouvez pas appeler un attribut ''{0}''"},


        {ErrorMsg.ILLEGAL_TEXT_NODE_ERR,
        "Donn\u00E9es texte en dehors de l'\u00E9l\u00E9ment <xsl:stylesheet> de niveau sup\u00E9rieur."},


        {ErrorMsg.SAX_PARSER_CONFIG_ERR,
        "L'analyseur JAXP n'est pas configur\u00E9 correctement"},


        {ErrorMsg.INTERNAL_ERR,
        "Erreur interne XSLTC irr\u00E9cup\u00E9rable : ''{0}''"},


        {ErrorMsg.UNSUPPORTED_XSL_ERR,
        "El\u00E9ment ''{0}'' XSL non pris en charge."},


        {ErrorMsg.UNSUPPORTED_EXT_ERR,
        "Extension ''{0}'' XSLTC non reconnue."},


        {ErrorMsg.MISSING_XSLT_URI_ERR,
        "Le document d'entr\u00E9e n'est pas une feuille de style (l'espace de noms XSL n'est pas d\u00E9clar\u00E9 dans l'\u00E9l\u00E9ment racine)."},


        {ErrorMsg.MISSING_XSLT_TARGET_ERR,
        "Cible de feuille de style ''{0}'' introuvable."},


        {ErrorMsg.ACCESSING_XSLT_TARGET_ERR,
        "Impossible de lire la cible de feuille de style ''{0}'' car l''acc\u00E8s \u00E0 ''{1}'' n''est pas autoris\u00E9 en raison d''une restriction d\u00E9finie par la propri\u00E9t\u00E9 accessExternalStylesheet."},


        {ErrorMsg.NOT_IMPLEMENTED_ERR,
        "Non impl\u00E9ment\u00E9 : ''{0}''."},


        {ErrorMsg.NOT_STYLESHEET_ERR,
        "Le document d'entr\u00E9e ne contient pas de feuille de style XSL."},


        {ErrorMsg.ELEMENT_PARSE_ERR,
        "Impossible d''analyser l''\u00E9l\u00E9ment ''{0}''"},


        {ErrorMsg.KEY_USE_ATTR_ERR,
        "L'attribut \"use\" de <key> doit \u00EAtre node, node-set, string ou number."},


        {ErrorMsg.OUTPUT_VERSION_ERR,
        "La version du document XML de sortie doit \u00EAtre 1.0"},


        {ErrorMsg.ILLEGAL_RELAT_OP_ERR,
        "Op\u00E9rateur inconnu pour l'expression relationnelle"},


        {ErrorMsg.ATTRIBSET_UNDEF_ERR,
        "Tentative d''utilisation de l''ensemble d''attributs non existant ''{0}''."},


        {ErrorMsg.ATTR_VAL_TEMPLATE_ERR,
        "Impossible d''analyser le mod\u00E8le de valeur d''attribut ''{0}''."},


        {ErrorMsg.UNKNOWN_SIG_TYPE_ERR,
        "Type de donn\u00E9es inconnu dans la signature pour la classe ''{0}''."},


        {ErrorMsg.DATA_CONVERSION_ERR,
        "Impossible de convertir le type de donn\u00E9es ''{0}'' en ''{1}''."},


        {ErrorMsg.NO_TRANSLET_CLASS_ERR,
        "Cette classe Templates ne contient pas de d\u00E9finition de classe de translet valide."},


        {ErrorMsg.NO_MAIN_TRANSLET_ERR,
        "Cette classe Termplates ne contient pas de classe portant le nom ''{0}''."},


        {ErrorMsg.TRANSLET_CLASS_ERR,
        "Impossible de charger la classe de translet ''{0}''."},

        {ErrorMsg.TRANSLET_OBJECT_ERR,
        "Classe de translet charg\u00E9e, mais impossible de cr\u00E9er une instance de translet."},


        {ErrorMsg.ERROR_LISTENER_NULL_ERR,
        "Tentative de d\u00E9finition d''ErrorListener sur NULL pour ''{0}''"},


        {ErrorMsg.JAXP_UNKNOWN_SOURCE_ERR,
        "Seuls StreamSource, SAXSource et DOMSource sont pris en charge par XSLTC"},


        {ErrorMsg.JAXP_NO_SOURCE_ERR,
        "L''objet Source transmis \u00E0 ''{0}'' n''a pas de contenu."},


        {ErrorMsg.JAXP_COMPILE_ERR,
        "Impossible de compiler la feuille de style"},


        {ErrorMsg.JAXP_INVALID_ATTR_ERR,
        "TransformerFactory ne reconna\u00EEt pas l''attribut ''{0}''."},

        {ErrorMsg.JAXP_INVALID_ATTR_VALUE_ERR,
        "La valeur indiqu\u00E9e pour l''attribut ''{0}'' est incorrecte."},


        {ErrorMsg.JAXP_SET_RESULT_ERR,
        "setResult() doit \u00EAtre appel\u00E9 avant startDocument()."},


        {ErrorMsg.JAXP_NO_TRANSLET_ERR,
        "La classe Transformer ne contient pas d'objet translet encapsul\u00E9."},


        {ErrorMsg.JAXP_NO_HANDLER_ERR,
        "Aucun gestionnaire de sortie d\u00E9fini pour le r\u00E9sultat de la transformation."},


        {ErrorMsg.JAXP_NO_RESULT_ERR,
        "L''objet de r\u00E9sultat transmis \u00E0 ''{0}'' n''est pas valide."},


        {ErrorMsg.JAXP_UNKNOWN_PROP_ERR,
        "Tentative d''acc\u00E8s \u00E0 la propri\u00E9t\u00E9 Transformer non valide ''{0}''."},


        {ErrorMsg.SAX2DOM_ADAPTER_ERR,
        "Impossible de cr\u00E9er l''adaptateur SAX2DOM : ''{0}''."},


        {ErrorMsg.XSLTC_SOURCE_ERR,
        "XSLTCSource.build() appel\u00E9 sans que l'ID syst\u00E8me soit d\u00E9fini."},

        { ErrorMsg.ER_RESULT_NULL,
            "Le r\u00E9sultat ne doit pas \u00EAtre NULL"},


        {ErrorMsg.JAXP_INVALID_SET_PARAM_VALUE,
        "La valeur du param\u00E8tre {0} doit \u00EAtre un objet Java valide"},


        {ErrorMsg.COMPILE_STDIN_ERR,
        "L'option -i doit \u00EAtre utilis\u00E9e avec l'option -o."},



        {ErrorMsg.COMPILE_USAGE_STR,
        "SYNTAXE\n   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Compile [-o <output>]\n      [-d <directory>] [-j <jarfile>] [-p <package>]\n      [-n] [-x] [-u] [-v] [-h] { <stylesheet> | -i }\n\nOPTIONS\n   -o <output>    attribue le nom <output> au\n                  translet g\u00E9n\u00E9r\u00E9. Par d\u00E9faut, le nom du translet est\n                  d\u00E9riv\u00E9 du nom <stylesheet>. Cette option\n                  n'est pas prise en compte lors de la compilation de plusieurs feuilles de style.\n   -d <directory> indique un r\u00E9pertoire de destination pour le translet\n   -j <jarfile>   package les classes de translet dans un fichier JAR portant le\n                  nom sp\u00E9cifi\u00E9 comme <jarfile>\n   -p <package>   indique un pr\u00E9fixe de nom de package pour toutes les\n                  classes de translet g\u00E9n\u00E9r\u00E9es.\n   -n             active le mode INLINE du mod\u00E8le (comportement par d\u00E9faut am\u00E9lior\u00E9\n                  en moyenne).\n   -x             active la sortie de messages de d\u00E9bogage suppl\u00E9mentaires\n   -u             interpr\u00E8te les arguments <stylesheet> comme des URL\n   -i             force le compilateur \u00E0 lire la feuille de style \u00E0 partir de STDIN\n   -v             affiche la version du compilateur\n   -h             affiche cette instruction de syntaxe\n"},


        {ErrorMsg.TRANSFORM_USAGE_STR,
        "SYNTAXE \n   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Transform [-j <jarfile>]\n      [-x] [-n <iterations>] {-u <document_url> | <document>}\n      <class> [<param1>=<value1> ...]\n\n   utilise le translet <class> pour transformer un document XML\n   sp\u00E9cifi\u00E9 comme <document>. Le translet <class> est soit dans\n   la variable d'environnement CLASSPATH de l'utilisateur, soit dans un fichier <jarfile> indiqu\u00E9 en option.\nOPTIONS\n   -j <jarfile>    indique un fichier JAR \u00E0 partir duquel charger le translet\n   -x              active la sortie de messages de d\u00E9bogage suppl\u00E9mentaires\n   -n <iterations> ex\u00E9cute la transformation <iterations> fois et\n                   affiche les informations de profilage\n   -u <document_url> sp\u00E9cifie le document d'entr\u00E9e XML comme URL\n"},




        {ErrorMsg.STRAY_SORT_ERR,
        "<xsl:sort> peut uniquement \u00EAtre utilis\u00E9 dans <xsl:for-each> ou <xsl:apply-templates>."},


        {ErrorMsg.UNSUPPORTED_ENCODING,
        "L''encodage de sortie ''{0}'' n''est pas pris en charge sur cette Java Virtual Machine (JVM)."},


        {ErrorMsg.SYNTAX_ERR,
        "Erreur de syntaxe dans ''{0}''."},


        {ErrorMsg.CONSTRUCTOR_NOT_FOUND,
        "Constructeur ''{0}'' externe introuvable."},


        {ErrorMsg.NO_JAVA_FUNCT_THIS_REF,
        "Le premier argument pour la fonction Java ''{0}'' non static n''est pas une r\u00E9f\u00E9rence d''objet valide."},


        {ErrorMsg.TYPE_CHECK_ERR,
        "Erreur lors de la v\u00E9rification du type de l''expression ''{0}''."},


        {ErrorMsg.TYPE_CHECK_UNK_LOC_ERR,
        "Erreur lors de la v\u00E9rification du type d'expression \u00E0 un emplacement inconnu."},


        {ErrorMsg.ILLEGAL_CMDLINE_OPTION_ERR,
        "L''option de ligne de commande ''{0}'' n''est pas valide."},


        {ErrorMsg.CMDLINE_OPT_MISSING_ARG_ERR,
        "Argument obligatoire manquant dans l''option de ligne de commande ''{0}''."},


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
        "Transformation \u00E0 l''aide du translet ''{0}'' "},


        {ErrorMsg.TRANSFORM_WITH_JAR_STR,
        "Transformation \u00E0 l''aide du translet ''{0}'' dans le fichier JAR ''{1}''"},


        {ErrorMsg.COULD_NOT_CREATE_TRANS_FACT,
        "Impossible de cr\u00E9er une instance de la classe TransformerFactory ''{0}''."},


        {ErrorMsg.TRANSLET_NAME_JAVA_CONFLICT,
         "Impossible d''utiliser le nom ''{0}'' comme nom de classe de translet car il contient des caract\u00E8res non autoris\u00E9s dans le nom de la classe Java. Le nom ''{1}'' a \u00E9t\u00E9 utilis\u00E9 \u00E0 la place."},


        {ErrorMsg.COMPILER_ERROR_KEY,
        "Erreurs de compilateur :"},


        {ErrorMsg.COMPILER_WARNING_KEY,
        "Avertissements de compilateur :"},


        {ErrorMsg.RUNTIME_ERROR_KEY,
        "Erreurs de translet :"},


        {ErrorMsg.INVALID_QNAME_ERR,
        "Un attribut dont la valeur doit \u00EAtre un QName ou une liste de QNames s\u00E9par\u00E9s par des espaces avait la valeur ''{0}''"},


        {ErrorMsg.INVALID_NCNAME_ERR,
        "Un attribut dont la valeur doit \u00EAtre un NCName avait la valeur ''{0}''"},


        {ErrorMsg.INVALID_METHOD_IN_OUTPUT,
        "L''attribut \"method\" d''un \u00E9l\u00E9ment <xsl:output> avait la valeur ''{0}''. La valeur doit \u00EAtre l''une des suivantes : ''xml'', ''html'', ''text'' ou qname-but-not-ncname"},

        {ErrorMsg.JAXP_GET_FEATURE_NULL_NAME,
        "Le nom de la fonctionnalit\u00E9 ne peut pas \u00EAtre NULL dans TransformerFactory.getFeature (cha\u00EEne pour le nom)."},

        {ErrorMsg.JAXP_SET_FEATURE_NULL_NAME,
        "Le nom de la fonctionnalit\u00E9 ne peut pas \u00EAtre NULL dans TransformerFactory.setFeature (cha\u00EEne pour le nom, valeur bool\u00E9enne)."},

        {ErrorMsg.JAXP_UNSUPPORTED_FEATURE,
        "Impossible de d\u00E9finir la fonctionnalit\u00E9 ''{0}'' sur cette propri\u00E9t\u00E9 TransformerFactory."},

        {ErrorMsg.JAXP_SECUREPROCESSING_FEATURE,
        "FEATURE_SECURE_PROCESSING : impossible de d\u00E9finir la fonctionnalit\u00E9 sur False en pr\u00E9sence du gestionnaire de s\u00E9curit\u00E9."},


        {ErrorMsg.OUTLINE_ERR_TRY_CATCH,
         "Erreur XSLTC interne : le code ex\u00E9cutable g\u00E9n\u00E9r\u00E9 contient un bloc try-catch-finally et ne peut pas \u00EAtre d\u00E9limit\u00E9."},


        {ErrorMsg.OUTLINE_ERR_UNBALANCED_MARKERS,
         "Erreur XSLTC interne : les marqueurs OutlineableChunkStart et OutlineableChunkEnd doivent \u00EAtre \u00E9quilibr\u00E9s et correctement imbriqu\u00E9s."},


        {ErrorMsg.OUTLINE_ERR_DELETED_TARGET,
         "Erreur XSLTC interne : une instruction ayant fait partie d'un bloc de code ex\u00E9cutable d\u00E9limit\u00E9 est toujours r\u00E9f\u00E9renc\u00E9e dans la m\u00E9thode d'origine."
        },



        {ErrorMsg.OUTLINE_ERR_METHOD_TOO_BIG,
         "Erreur XSLTC interne : une m\u00E9thode dans le translet d\u00E9passe la limite de la JVM concernant la longueur d'une m\u00E9thode de 64 kilo-octets. En g\u00E9n\u00E9ral, ceci est d\u00FB \u00E0 de tr\u00E8s grands mod\u00E8les dans une feuille de style. Essayez de restructurer la feuille de style pour utiliser des mod\u00E8les plus petits."
        },

         {ErrorMsg.DESERIALIZE_TRANSLET_ERR, "Lorsque la s\u00E9curit\u00E9 Java est activ\u00E9e, la prise en charge de la d\u00E9s\u00E9rialisation de TemplatesImpl est d\u00E9sactiv\u00E9e. La d\u00E9finition de la propri\u00E9t\u00E9 syst\u00E8me jdk.xml.enableTemplatesImplDeserialization sur True permet de remplacer ce param\u00E8tre."}

    };

    }
}
