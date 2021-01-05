


package com.sun.org.apache.xalan.internal.xsltc.runtime;

import java.util.ListResourceBundle;


public class ErrorMessages_es extends ListResourceBundle {



    public Object[][] getContents()
    {
        return new Object[][] {


        {BasisLibrary.RUN_TIME_INTERNAL_ERR,
        "Error interno de tiempo de ejecuci\u00F3n en ''{0}''"},


        {BasisLibrary.RUN_TIME_COPY_ERR,
        "Error de tiempo de ejecuci\u00F3n al ejecutar <xsl:copy>."},


        {BasisLibrary.DATA_CONVERSION_ERR,
        "Conversi\u00F3n no v\u00E1lida de ''{0}'' a ''{1}''."},


        {BasisLibrary.EXTERNAL_FUNC_ERR,
        "Funci\u00F3n externa ''{0}'' no soportada por XSLTC."},


        {BasisLibrary.EQUALITY_EXPR_ERR,
        "Tipo de argumento desconocido en la expresi\u00F3n de igualdad."},


        {BasisLibrary.INVALID_ARGUMENT_ERR,
        "Tipo de argumento ''{0}'' no v\u00E1lido en la llamada a ''{1}''"},


        {BasisLibrary.FORMAT_NUMBER_ERR,
        "Intentando formatear n\u00FAmero ''{0}'' mediante el patr\u00F3n ''{1}''."},


        {BasisLibrary.ITERATOR_CLONE_ERR,
        "No se puede clonar el iterador ''{0}''."},


        {BasisLibrary.AXIS_SUPPORT_ERR,
        "El iterador para el eje ''{0}'' no est\u00E1 soportado."},


        {BasisLibrary.TYPED_AXIS_SUPPORT_ERR,
        "El iterador para el eje introducido ''{0}'' no est\u00E1 soportado."},


        {BasisLibrary.STRAY_ATTRIBUTE_ERR,
        "El atributo ''{0}'' est\u00E1 fuera del elemento."},


        {BasisLibrary.STRAY_NAMESPACE_ERR,
        "Declaraci\u00F3n del espacio de nombres ''{0}''=''{1}'' fuera del elemento."},


        {BasisLibrary.NAMESPACE_PREFIX_ERR,
        "No se ha declarado el espacio de nombres para el prefijo ''{0}''."},


        {BasisLibrary.DOM_ADAPTER_INIT_ERR,
        "Se ha creado DOMAdapter mediante un tipo incorrecto de DOM de origen."},


        {BasisLibrary.PARSER_DTD_SUPPORT_ERR,
        "El analizador SAX que est\u00E1 utilizando no maneja los eventos de declaraci\u00F3n DTD."},


        {BasisLibrary.NAMESPACES_SUPPORT_ERR,
        "El analizador SAX que est\u00E1 utilizando no soporta los espacios de nombres XML."},


        {BasisLibrary.CANT_RESOLVE_RELATIVE_URI_ERR,
        "No se ha podido resolver la referencia al URI ''{0}''."},


        {BasisLibrary.UNSUPPORTED_XSL_ERR,
        "Elemento ''{0}'' de XSL no soportado"},


        {BasisLibrary.UNSUPPORTED_EXT_ERR,
        "Extensi\u00F3n ''{0}'' de XSLTC no reconocida"},



        {BasisLibrary.UNKNOWN_TRANSLET_VERSION_ERR,
        "El translet especificado, ''{0}'' se ha creado con una versi\u00F3n de XSLTC m\u00E1s reciente que la versi\u00F3n del tiempo de ejecuci\u00F3n de XSLTC que se est\u00E1 utilizando. Debe volver a compilar la hoja de estilo o utilizar una versi\u00F3n m\u00E1s reciente de XSLTC para ejecutar este translet."},


        {BasisLibrary.INVALID_QNAME_ERR,
        "Un atributo cuyo valor debe ser un QName ten\u00EDa el valor ''{0}''"},



        {BasisLibrary.INVALID_NCNAME_ERR,
        "Un atributo cuyo valor debe ser un NCName ten\u00EDa el valor ''{0}''"},

        {BasisLibrary.UNALLOWED_EXTENSION_FUNCTION_ERR,
        "El uso de la funci\u00F3n de extensi\u00F3n ''{0}'' no est\u00E1 permitido cuando la funci\u00F3n de procesamiento seguro se ha definido en true."},

        {BasisLibrary.UNALLOWED_EXTENSION_ELEMENT_ERR,
        "El uso del elemento de extensi\u00F3n ''{0}'' no est\u00E1 permitido cuando la funci\u00F3n de procesamiento seguro se ha definido en true."},
    };
    }

}
