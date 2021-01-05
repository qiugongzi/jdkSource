


package com.sun.org.apache.xalan.internal.xsltc.runtime;

import java.util.ListResourceBundle;


public class ErrorMessages_it extends ListResourceBundle {



    public Object[][] getContents()
    {
        return new Object[][] {


        {BasisLibrary.RUN_TIME_INTERNAL_ERR,
        "Errore interno in fase di esecuzione in ''{0}''"},


        {BasisLibrary.RUN_TIME_COPY_ERR,
        "Errore in fase di esecuzione durante l'esecuzione di <xsl:copy>."},


        {BasisLibrary.DATA_CONVERSION_ERR,
        "Conversione non valida da ''{0}'' a ''{1}''."},


        {BasisLibrary.EXTERNAL_FUNC_ERR,
        "Funzione esterna ''{0}'' non supportata da XSLTC."},


        {BasisLibrary.EQUALITY_EXPR_ERR,
        "Tipo di argomento sconosciuto nell'espressione di uguaglianza."},


        {BasisLibrary.INVALID_ARGUMENT_ERR,
        "Tipo di argomento ''{0}'' non valido nella chiamata a ''{1}''"},


        {BasisLibrary.FORMAT_NUMBER_ERR,
        "Tentativo di formattare il numero ''{0}'' mediante il pattern ''{1}''."},


        {BasisLibrary.ITERATOR_CLONE_ERR,
        "Impossibile duplicare l''iteratore ''{0}''."},


        {BasisLibrary.AXIS_SUPPORT_ERR,
        "Iteratore per l''asse ''{0}'' non supportato."},


        {BasisLibrary.TYPED_AXIS_SUPPORT_ERR,
        "Iteratore per l''asse immesso ''{0}'' non supportato."},


        {BasisLibrary.STRAY_ATTRIBUTE_ERR,
        "Attributo ''{0}'' al di fuori dell''elemento."},


        {BasisLibrary.STRAY_NAMESPACE_ERR,
        "Dichiarazione dello spazio di nomi ''{0}''=''{1}'' al di fuori dell''elemento."},


        {BasisLibrary.NAMESPACE_PREFIX_ERR,
        "Lo spazio di nomi per il prefisso ''{0}'' non \u00E8 stato dichiarato."},


        {BasisLibrary.DOM_ADAPTER_INIT_ERR,
        "DOMAdapter creato utilizzando il tipo errato di DOM di origine."},


        {BasisLibrary.PARSER_DTD_SUPPORT_ERR,
        "Il parser SAX in uso non gestisce gli eventi di dichiarazione DTD."},


        {BasisLibrary.NAMESPACES_SUPPORT_ERR,
        "Il parser SAX in uso non supporta gli spazi di nomi XML."},


        {BasisLibrary.CANT_RESOLVE_RELATIVE_URI_ERR,
        "Impossibile risolvere il riferimento URI ''{0}''."},


        {BasisLibrary.UNSUPPORTED_XSL_ERR,
        "Elemento XSL \"{0}\" non supportato"},


        {BasisLibrary.UNSUPPORTED_EXT_ERR,
        "Estensione XSLTC ''{0}'' non riconosciuta"},



        {BasisLibrary.UNKNOWN_TRANSLET_VERSION_ERR,
        "Il translet specificato ''{0}'' \u00E8 stato creato utilizzando una versione di XSLTC pi\u00F9 recente di quella della fase di esecuzione XSLTC in uso. Ricompilare il foglio di stile o utilizzare una versione pi\u00F9 recente di XSLTC per eseguire questo translet."},


        {BasisLibrary.INVALID_QNAME_ERR,
        "Un attributo il cui valore deve essere un QName contiene il valore ''{0}''"},



        {BasisLibrary.INVALID_NCNAME_ERR,
        "Un attributo il cui valore deve essere un NCName contiene il valore ''{0}''"},

        {BasisLibrary.UNALLOWED_EXTENSION_FUNCTION_ERR,
        "Non \u00E8 consentito utilizzare la funzione di estensione ''{0}'' se la funzione di elaborazione sicura \u00E8 impostata su true."},

        {BasisLibrary.UNALLOWED_EXTENSION_ELEMENT_ERR,
        "Non \u00E8 consentito utilizzare l''elemento di estensione ''{0}'' se la funzione di elaborazione sicura \u00E8 impostata su true."},
    };
    }

}
