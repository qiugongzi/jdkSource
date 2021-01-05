


package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import java.util.ListResourceBundle;


public class ErrorMessages_es extends ListResourceBundle {



    public Object[][] getContents()
    {
      return new Object[][] {
        {ErrorMsg.MULTIPLE_STYLESHEET_ERR,
        "Se ha definido m\u00E1s de una hoja de estilo en el mismo archivo."},


        {ErrorMsg.TEMPLATE_REDEF_ERR,
        "La plantilla ''{0}'' ya se ha definido en esta hoja de estilo."},



        {ErrorMsg.TEMPLATE_UNDEF_ERR,
        "La plantilla ''{0}'' no se ha definido en esta hoja de estilo."},


        {ErrorMsg.VARIABLE_REDEF_ERR,
        "Se ha definido varias veces la variable ''{0}'' en el mismo \u00E1mbito."},


        {ErrorMsg.VARIABLE_UNDEF_ERR,
        "No se ha definido la variable o el par\u00E1metro ''{0}''."},


        {ErrorMsg.CLASS_NOT_FOUND_ERR,
        "No se ha encontrado la clase ''{0}''."},


        {ErrorMsg.METHOD_NOT_FOUND_ERR,
        "No se ha encontrado el m\u00E9todo externo ''{0}'' (debe ser p\u00FAblico)."},


        {ErrorMsg.ARGUMENT_CONVERSION_ERR,
        "No se puede convertir el tipo de argumento/retorno en la llamada al m\u00E9todo ''{0}''"},


        {ErrorMsg.FILE_NOT_FOUND_ERR,
        "No se ha encontrado el archivo o URI ''{0}''."},


        {ErrorMsg.INVALID_URI_ERR,
        "URI ''{0}'' no v\u00E1lido."},


        {ErrorMsg.FILE_ACCESS_ERR,
        "No se puede abrir el archivo o URI ''{0}''."},


        {ErrorMsg.MISSING_ROOT_ERR,
        "Se esperaba el elemento <xsl:stylesheet> o <xsl:transform>."},


        {ErrorMsg.NAMESPACE_UNDEF_ERR,
        "No se ha declarado el prefijo de espacio de nombres ''{0}''."},


        {ErrorMsg.FUNCTION_RESOLVE_ERR,
        "No se ha podido resolver la llamada a la funci\u00F3n ''{0}''."},


        {ErrorMsg.NEED_LITERAL_ERR,
        "El argumento en ''{0}'' debe ser una cadena literal."},


        {ErrorMsg.XPATH_PARSER_ERR,
        "Error al analizar la expresi\u00F3n XPath ''{0}''."},


        {ErrorMsg.REQUIRED_ATTR_ERR,
        "Falta el atributo ''{0}'' necesario."},


        {ErrorMsg.ILLEGAL_CHAR_ERR,
        "Car\u00E1cter ''{0}'' no permitido en la expresi\u00F3n XPath."},


        {ErrorMsg.ILLEGAL_PI_ERR,
        "Nombre ''{0}'' no permitido para la instrucci\u00F3n de procesamiento."},


        {ErrorMsg.STRAY_ATTRIBUTE_ERR,
        "El atributo ''{0}'' est\u00E1 fuera del elemento."},


        {ErrorMsg.ILLEGAL_ATTRIBUTE_ERR,
        "Atributo ''{0}'' no permitido."},


        {ErrorMsg.CIRCULAR_INCLUDE_ERR,
        "Import/include circular. La hoja de estilo ''{0}'' ya se ha cargado."},


        {ErrorMsg.RESULT_TREE_SORT_ERR,
        "Los fragmentos del \u00E1rbol de resultados no se pueden ordenar (los elementos <xsl:sort> se ignoran). Debe ordenar los nodos al crear el \u00E1rbol de resultados."},


        {ErrorMsg.SYMBOLS_REDEF_ERR,
        "Ya se ha definido el formato decimal ''{0}''."},


        {ErrorMsg.XSL_VERSION_ERR,
        "La versi\u00F3n XSL ''{0}'' no est\u00E1 soportada por XSLTC."},


        {ErrorMsg.CIRCULAR_VARIABLE_ERR,
        "La referencia de variable/par\u00E1metro circular en ''{0}''."},


        {ErrorMsg.ILLEGAL_BINARY_OP_ERR,
        "Operador desconocido para la expresi\u00F3n binaria."},


        {ErrorMsg.ILLEGAL_ARG_ERR,
        "Argumentos no permitidos para la llamada de funci\u00F3n."},


        {ErrorMsg.DOCUMENT_ARG_ERR,
        "El segundo argumento en la funci\u00F3n document() debe ser un juego de nodos."},


        {ErrorMsg.MISSING_WHEN_ERR,
        "Se necesita al menos un elemento <xsl:when> en <xsl:choose>."},


        {ErrorMsg.MULTIPLE_OTHERWISE_ERR,
        "S\u00F3lo se permite un elemento <xsl:otherwise> en <xsl:choose>."},


        {ErrorMsg.STRAY_OTHERWISE_ERR,
        "<xsl:otherwise> s\u00F3lo se puede utilizar en <xsl:choose>."},


        {ErrorMsg.STRAY_WHEN_ERR,
        "<xsl:when> s\u00F3lo se puede utilizar en <xsl:choose>."},


        {ErrorMsg.WHEN_ELEMENT_ERR,
        "S\u00F3lo se permiten los elementos <xsl:when> y <xsl:otherwise> en <xsl:choose>."},


        {ErrorMsg.UNNAMED_ATTRIBSET_ERR,
        "Falta el atributo 'name' en <xsl:attribute-set>"},


        {ErrorMsg.ILLEGAL_CHILD_ERR,
        "Elemento secundario no permitido."},


        {ErrorMsg.ILLEGAL_ELEM_NAME_ERR,
        "No se puede llamar ''{0}'' a un elemento"},


        {ErrorMsg.ILLEGAL_ATTR_NAME_ERR,
        "No se puede llamar ''{0}'' a un atributo"},


        {ErrorMsg.ILLEGAL_TEXT_NODE_ERR,
        "Datos de texto fuera del elemento <xsl:stylesheet> de nivel superior."},


        {ErrorMsg.SAX_PARSER_CONFIG_ERR,
        "El analizador JAXP no se ha configurado correctamente"},


        {ErrorMsg.INTERNAL_ERR,
        "Error interno de XSLTC irrecuperable: ''{0}''"},


        {ErrorMsg.UNSUPPORTED_XSL_ERR,
        "Elemento ''{0}'' de XSL no soportado."},


        {ErrorMsg.UNSUPPORTED_EXT_ERR,
        "Extensi\u00F3n ''{0}'' de XSLTC no reconocida."},


        {ErrorMsg.MISSING_XSLT_URI_ERR,
        "El documento de entrada no es una hoja de estilo (el espacio de nombres XSL no se ha declarado en el elemento ra\u00EDz)."},


        {ErrorMsg.MISSING_XSLT_TARGET_ERR,
        "No se ha encontrado el destino de hoja de estilo ''{0}''."},


        {ErrorMsg.ACCESSING_XSLT_TARGET_ERR,
        "No se ha podido leer el destino de hoja de estilos ''{0}'', porque no se permite el acceso ''{1}'' debido a una restricci\u00F3n definida por la propiedad accessExternalStylesheet."},


        {ErrorMsg.NOT_IMPLEMENTED_ERR,
        "No implantado: ''{0}''."},


        {ErrorMsg.NOT_STYLESHEET_ERR,
        "El documento de entrada no contiene una hoja de estilo XSL."},


        {ErrorMsg.ELEMENT_PARSE_ERR,
        "No se ha podido analizar el elemento ''{0}''"},


        {ErrorMsg.KEY_USE_ATTR_ERR,
        "El atributo use de <key> debe ser node, node-set, string o number."},


        {ErrorMsg.OUTPUT_VERSION_ERR,
        "La versi\u00F3n del documento XML de salida debe ser 1.0"},


        {ErrorMsg.ILLEGAL_RELAT_OP_ERR,
        "Operador desconocido para la expresi\u00F3n relacional"},


        {ErrorMsg.ATTRIBSET_UNDEF_ERR,
        "Se est\u00E1 intentando utilizar el juego de atributos ''{0}'' no existente."},


        {ErrorMsg.ATTR_VAL_TEMPLATE_ERR,
        "No se puede analizar la plantilla del valor de atributo ''{0}''."},


        {ErrorMsg.UNKNOWN_SIG_TYPE_ERR,
        "Tipo de datos desconocido en la firma para la clase ''{0}''."},


        {ErrorMsg.DATA_CONVERSION_ERR,
        "No se puede convertir el tipo de datos ''{0}'' en ''{1}''."},


        {ErrorMsg.NO_TRANSLET_CLASS_ERR,
        "Templates no contiene una definici\u00F3n de clase translet."},


        {ErrorMsg.NO_MAIN_TRANSLET_ERR,
        "Templates no contiene una clase con el nombre ''{0}''."},


        {ErrorMsg.TRANSLET_CLASS_ERR,
        "No se ha podido cargar la clase de translet ''{0}''."},

        {ErrorMsg.TRANSLET_OBJECT_ERR,
        "La clase de translet se ha cargado, pero no se puede crear una instancia de translet."},


        {ErrorMsg.ERROR_LISTENER_NULL_ERR,
        "Intentando definir ErrorListener para ''{0}'' como nulo"},


        {ErrorMsg.JAXP_UNKNOWN_SOURCE_ERR,
        "S\u00F3lo StreamSource, SAXSource y DOMSource est\u00E1n soportados por XSLTC"},


        {ErrorMsg.JAXP_NO_SOURCE_ERR,
        "El objeto Source que se ha transferido a ''{0}'' no tiene contenido."},


        {ErrorMsg.JAXP_COMPILE_ERR,
        "No se ha podido compilar la hoja de estilo"},


        {ErrorMsg.JAXP_INVALID_ATTR_ERR,
        "TransformerFactory no reconoce el atributo ''{0}''."},

        {ErrorMsg.JAXP_INVALID_ATTR_VALUE_ERR,
        "Valor no v\u00E1lido especificado para el atributo ''{0}''."},


        {ErrorMsg.JAXP_SET_RESULT_ERR,
        "setResult() debe llamarse antes de startDocument()."},


        {ErrorMsg.JAXP_NO_TRANSLET_ERR,
        "Transformer no tiene un objeto translet encapsulado."},


        {ErrorMsg.JAXP_NO_HANDLER_ERR,
        "No se ha definido el manejador de salida para el resultado de la transformaci\u00F3n."},


        {ErrorMsg.JAXP_NO_RESULT_ERR,
        "El objeto Result que se ha pasado a ''{0}'' no es v\u00E1lido."},


        {ErrorMsg.JAXP_UNKNOWN_PROP_ERR,
        "Se est\u00E1 intentando acceder a la propiedad ''{0}'' de Transformer no v\u00E1lida."},


        {ErrorMsg.SAX2DOM_ADAPTER_ERR,
        "No se ha podido crear el adaptador SAX2DOM: ''{0}''."},


        {ErrorMsg.XSLTC_SOURCE_ERR,
        "Se ha llamado a XSLTCSource.build() sin haber definido la identificaci\u00F3n del sistema."},

        { ErrorMsg.ER_RESULT_NULL,
            "El resultado no debe ser nulo"},


        {ErrorMsg.JAXP_INVALID_SET_PARAM_VALUE,
        "El valor del par\u00E1metro {0} debe ser un objeto Java v\u00E1lido"},


        {ErrorMsg.COMPILE_STDIN_ERR,
        "La opci\u00F3n -i debe utilizarse con la opci\u00F3n -o."},



        {ErrorMsg.COMPILE_USAGE_STR,
        "SINOPSIS\n   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Compile [-o <salida>]\n      [-d <directorio>] [-j <archivo jar>] [-p <paquete>]\n      [-n] [-x] [-u] [-v] [-h] { <hoja de estilo> | -i }\n\nOPCIONES\n   -o <salida>    asigna el nombre de <salida> al translet\n                  generado. Por defecto, el nombre del translet se\n                  deriva del nombre de <hoja de estilo>. Esta opci\u00F3n\n                  se ignora si se compilan varias hojas de estilo.\n   -d <directorio> especifica un directorio de destino para el translet\n   -j <archivo jar>   empaqueta las clases de translet en un archivo jar del\n                  nombre especificado como <archivo jar>\n   -p <paquete>   especifica un prefijo de nombre de paquete para todas las clases de translet n\n                  generadas.\n   -n             permite poner en l\u00EDnea la plantilla (comportamiento por defecto mejor\n                  sobre la media).\n   -x             activa la salida del mensaje de depuraci\u00F3n\n   -u             interpreta los argumentos <hoja de estilo> como URL\n   -i             obliga al compilador a leer la hoja de estilo de stdin\n   -v             imprime la versi\u00F3n del compilador\n   -h             imprime esta sentencia de uso\n"},


        {ErrorMsg.TRANSFORM_USAGE_STR,
        "SYNOPSIS \n   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Transform [-j <archivo jar>]\n      [-x] [-n <iteraciones>] {-u <url_documento> | <documento>}\n      <clase> [<par\u00E1metro1>=<valor1> ...]\n\n   utiliza el translet <clase> para transformar un documento XML \n   especificado como <documento>. El translet <clase> se encuentra en\n   la CLASSPATH del usuario o en el <archivo jar> especificado opcionalmente.\nOPCIONES\n   -j <archivo jar>    especifica un archivo jar desde el que cargar el translet\n   -x              activa la salida del mensaje de depuraci\u00F3n adicional\n   -n <iteraciones> ejecuta el n\u00FAmero de <iteraciones> de una transformaci\u00F3n y\n                   muestra la informaci\u00F3n de la creaci\u00F3n de perfil\n   -u <url_documento> especifica el documento de entrada XML como una URL\n"},




        {ErrorMsg.STRAY_SORT_ERR,
        "<xsl:sort> s\u00F3lo se puede utilizar en <xsl:for-each> o <xsl:apply-templates>."},


        {ErrorMsg.UNSUPPORTED_ENCODING,
        "La codificaci\u00F3n de salida ''{0}'' no est\u00E1 soportada en esta JVM."},


        {ErrorMsg.SYNTAX_ERR,
        "Error de sintaxis en ''{0}''."},


        {ErrorMsg.CONSTRUCTOR_NOT_FOUND,
        "No se ha encontrado el constructor externo ''{0}''."},


        {ErrorMsg.NO_JAVA_FUNCT_THIS_REF,
        "El primer argumento de la funci\u00F3n Java no est\u00E1tica ''{0}'' no es una referencia de objeto v\u00E1lida."},


        {ErrorMsg.TYPE_CHECK_ERR,
        "Error al comprobar el tipo de la expresi\u00F3n ''{0}''."},


        {ErrorMsg.TYPE_CHECK_UNK_LOC_ERR,
        "Error al comprobar el tipo de una expresi\u00F3n en una ubicaci\u00F3n desconocida."},


        {ErrorMsg.ILLEGAL_CMDLINE_OPTION_ERR,
        "La opci\u00F3n de l\u00EDnea de comandos ''{0}'' no es v\u00E1lida."},


        {ErrorMsg.CMDLINE_OPT_MISSING_ARG_ERR,
        "Falta un argumento necesario en la opci\u00F3n de l\u00EDnea de comandos ''{0}''."},


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
        "Transformaci\u00F3n que utiliza el translet ''{0}'' "},


        {ErrorMsg.TRANSFORM_WITH_JAR_STR,
        "Transformaci\u00F3n que utiliza el translet ''{0}'' del archivo jar ''{1}''"},


        {ErrorMsg.COULD_NOT_CREATE_TRANS_FACT,
        "No se ha podido crear una instancia de la clase TransformerFactory ''{0}''."},


        {ErrorMsg.TRANSLET_NAME_JAVA_CONFLICT,
         "El nombre ''{0}'' no se ha podido utilizar como el nombre de la clase de translet porque contiene caracteres que no est\u00E1n permitidos en el nombre de la clase Java. Se ha utilizado el nombre ''{1}'' en su lugar."},


        {ErrorMsg.COMPILER_ERROR_KEY,
        "Errores del compilador:"},


        {ErrorMsg.COMPILER_WARNING_KEY,
        "Advertencias del compilador:"},


        {ErrorMsg.RUNTIME_ERROR_KEY,
        "Errores del translet:"},


        {ErrorMsg.INVALID_QNAME_ERR,
        "Un atributo cuyo valor debe ser un QName o lista de QNames separados por espacios en blanco ten\u00EDa el valor ''{0}''"},


        {ErrorMsg.INVALID_NCNAME_ERR,
        "Un atributo cuyo valor debe ser un NCName ten\u00EDa el valor ''{0}''"},


        {ErrorMsg.INVALID_METHOD_IN_OUTPUT,
        "El atributo method de un elemento <xsl:output> ten\u00EDa el valor ''{0}''. El valor debe ser ''xml'', ''html'', ''text'' o qname-but-not-ncname"},

        {ErrorMsg.JAXP_GET_FEATURE_NULL_NAME,
        "El nombre de funci\u00F3n no puede ser nulo en TransformerFactory.getFeature (nombre de cadena)."},

        {ErrorMsg.JAXP_SET_FEATURE_NULL_NAME,
        "El nombre de funci\u00F3n no puede ser nulo en TransformerFactory.setFeature (nombre de cadena, valor booleano)."},

        {ErrorMsg.JAXP_UNSUPPORTED_FEATURE,
        "No se puede definir la funci\u00F3n ''{0}''en esta f\u00E1brica del transformador."},

        {ErrorMsg.JAXP_SECUREPROCESSING_FEATURE,
        "FEATURE_SECURE_PROCESSING: no se puede definir la funci\u00F3n en false cuando est\u00E1 presente el gestor de seguridad."},


        {ErrorMsg.OUTLINE_ERR_TRY_CATCH,
         "Error interno de XSLTC: el c\u00F3digo de bytes generado contiene un bloque try-catch-finally y no se puede delimitar."},


        {ErrorMsg.OUTLINE_ERR_UNBALANCED_MARKERS,
         "Error interno de XSLTC: los marcadores OutlineableChunkStart y OutlineableChunkEnd deben estar equilibrados y correctamente anidados."},


        {ErrorMsg.OUTLINE_ERR_DELETED_TARGET,
         "Error interno de XSLTC: todav\u00EDa se hace referencia a una instrucci\u00F3n que formaba parte de un bloque de c\u00F3digo de bytes delimitado en el m\u00E9todo original."
        },



        {ErrorMsg.OUTLINE_ERR_METHOD_TOO_BIG,
         "Error interno de XSLTC: un m\u00E9todo en el translet excede la limitaci\u00F3n de Java Virtual Machine de longitud de un m\u00E9todo de 64 kilobytes. Normalmente, esto lo causan plantillas en una hoja de estilos demasiado grandes. Pruebe a reestructurar la hoja de estilos para utilizar plantillas m\u00E1s peque\u00F1as."
        },

         {ErrorMsg.DESERIALIZE_TRANSLET_ERR, "Cuando la seguridad de Java est\u00E1 activada, el soporte para anular la serializaci\u00F3n de TemplatesImpl est\u00E1 desactivado. Esto se puede sustituir definiendo la propiedad del sistema jdk.xml.enableTemplatesImplDeserialization en true."}

    };

    }
}
