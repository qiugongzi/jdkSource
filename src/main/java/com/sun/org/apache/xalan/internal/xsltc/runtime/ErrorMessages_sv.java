


package com.sun.org.apache.xalan.internal.xsltc.runtime;

import java.util.ListResourceBundle;


public class ErrorMessages_sv extends ListResourceBundle {



    public Object[][] getContents()
    {
        return new Object[][] {


        {BasisLibrary.RUN_TIME_INTERNAL_ERR,
        "Internt exekveringsfel i ''{0}''"},


        {BasisLibrary.RUN_TIME_COPY_ERR,
        "Exekveringsexekveringsfel av <xsl:copy>."},


        {BasisLibrary.DATA_CONVERSION_ERR,
        "Ogiltig konvertering fr\u00E5n ''{0}'' till ''{1}''."},


        {BasisLibrary.EXTERNAL_FUNC_ERR,
        "Den externa funktionen ''{0}'' underst\u00F6ds inte i XSLTC."},


        {BasisLibrary.EQUALITY_EXPR_ERR,
        "Ok\u00E4nd argumenttyp i likhetsuttryck."},


        {BasisLibrary.INVALID_ARGUMENT_ERR,
        "Argumenttyp ''{0}'' i anrop till ''{1}'' \u00E4r inte giltig"},


        {BasisLibrary.FORMAT_NUMBER_ERR,
        "F\u00F6rs\u00F6ker formatera talet ''{0}'' med m\u00F6nstret ''{1}''."},


        {BasisLibrary.ITERATOR_CLONE_ERR,
        "Kan inte klona iteratorn ''{0}''."},


        {BasisLibrary.AXIS_SUPPORT_ERR,
        "Iteratorn f\u00F6r axeln ''{0}'' underst\u00F6ds inte."},


        {BasisLibrary.TYPED_AXIS_SUPPORT_ERR,
        "Iteratorn f\u00F6r den typade axeln ''{0}'' underst\u00F6ds inte."},


        {BasisLibrary.STRAY_ATTRIBUTE_ERR,
        "Attributet ''{0}'' finns utanf\u00F6r elementet."},


        {BasisLibrary.STRAY_NAMESPACE_ERR,
        "Namnrymdsdeklarationen ''{0}''=''{1}'' finns utanf\u00F6r element."},


        {BasisLibrary.NAMESPACE_PREFIX_ERR,
        "Namnrymd f\u00F6r prefix ''{0}'' har inte deklarerats."},


        {BasisLibrary.DOM_ADAPTER_INIT_ERR,
        "DOMAdapter har skapats med fel typ av DOM-k\u00E4lla."},


        {BasisLibrary.PARSER_DTD_SUPPORT_ERR,
        "Den SAX-parser som du anv\u00E4nder hanterar inga DTD-deklarationsh\u00E4ndelser."},


        {BasisLibrary.NAMESPACES_SUPPORT_ERR,
        "Den SAX-parser som du anv\u00E4nder saknar st\u00F6d f\u00F6r XML-namnrymder."},


        {BasisLibrary.CANT_RESOLVE_RELATIVE_URI_ERR,
        "Kunde inte matcha URI-referensen ''{0}''."},


        {BasisLibrary.UNSUPPORTED_XSL_ERR,
        "XSL-elementet ''{0}'' st\u00F6ds inte"},


        {BasisLibrary.UNSUPPORTED_EXT_ERR,
        "XSLTC-till\u00E4gget ''{0}'' \u00E4r ok\u00E4nt"},



        {BasisLibrary.UNKNOWN_TRANSLET_VERSION_ERR,
        "Angiven translet, ''{0}'', har skapats med en XSLTC-version som \u00E4r senare \u00E4n den XSLTC-k\u00F6rning i bruk. F\u00F6r att kunna k\u00F6ra denna translet m\u00E5ste du omkompilera formatmallen eller anv\u00E4nda en senare version av XSLTC."},


        {BasisLibrary.INVALID_QNAME_ERR,
        "Ett attribut vars v\u00E4rde m\u00E5ste vara ett QName hade v\u00E4rdet ''{0}''"},



        {BasisLibrary.INVALID_NCNAME_ERR,
        "Ett attribut vars v\u00E4rde m\u00E5ste vara ett NCName hade v\u00E4rdet ''{0}''"},

        {BasisLibrary.UNALLOWED_EXTENSION_FUNCTION_ERR,
        "Anv\u00E4ndning av till\u00E4ggsfunktionen ''{0}'' \u00E4r inte till\u00E5tet n\u00E4r s\u00E4ker bearbetning till\u00E4mpas."},

        {BasisLibrary.UNALLOWED_EXTENSION_ELEMENT_ERR,
        "Anv\u00E4ndning av till\u00E4ggselementet ''{0}'' \u00E4r inte till\u00E5tet n\u00E4r s\u00E4ker bearbetning till\u00E4mpas."},
    };
    }

}
