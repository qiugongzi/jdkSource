



package com.sun.org.apache.xalan.internal.xsltc.runtime;

import java.util.ListResourceBundle;


public class ErrorMessages_cs extends ListResourceBundle {



    public Object[][] getContents()
    {
        return new Object[][] {


        {BasisLibrary.RUN_TIME_INTERNAL_ERR,
        "Vnit\u0159n\u00ed b\u011bhov\u00e1 chyba v ''{0}''"},


        {BasisLibrary.RUN_TIME_COPY_ERR,
        "Vnit\u0159n\u00ed b\u011bhov\u00e1 chyba p\u0159i prov\u00e1d\u011bn\u00ed funkce <xsl:copy>."},


        {BasisLibrary.DATA_CONVERSION_ERR,
        "Neplatn\u00e1 konverze z ''{0}'' do ''{1}''."},


        {BasisLibrary.EXTERNAL_FUNC_ERR,
        "Extern\u00ed funkce ''{0}'' nen\u00ed podporov\u00e1na produktem SLTC."},


        {BasisLibrary.EQUALITY_EXPR_ERR,
        "Nezn\u00e1m\u00fd typ argumentu ve v\u00fdrazu rovnosti."},


        {BasisLibrary.INVALID_ARGUMENT_ERR,
        "Neplatn\u00fd typ argumentu ''{0}'' p\u0159i vol\u00e1n\u00ed ''{1}''"},


        {BasisLibrary.FORMAT_NUMBER_ERR,
        "Pokus form\u00e1tovat \u010d\u00edslo ''{0}'' pou\u017eit\u00edm vzorku ''{1}''."},


        {BasisLibrary.ITERATOR_CLONE_ERR,
        "Nelze klonovat iter\u00e1tor ''{0}''."},


        {BasisLibrary.AXIS_SUPPORT_ERR,
        "Iter\u00e1tor pro osu ''{0}'' nen\u00ed podporov\u00e1n."},


        {BasisLibrary.TYPED_AXIS_SUPPORT_ERR,
        "Iter\u00e1tor pro typizovanou osu ''{0}'' nen\u00ed podporov\u00e1n."},


        {BasisLibrary.STRAY_ATTRIBUTE_ERR,
        "Atribut ''{0}'' je vn\u011b prvku."},


        {BasisLibrary.STRAY_NAMESPACE_ERR,
        "Deklarace oboru n\u00e1zv\u016f ''{0}''=''{1}'' je vn\u011b prvku."},


        {BasisLibrary.NAMESPACE_PREFIX_ERR,
        "Obor n\u00e1zv\u016f pro p\u0159edponu ''{0}'' nebyl deklarov\u00e1n."},


        {BasisLibrary.DOM_ADAPTER_INIT_ERR,
        "DOMAdapter byl vytvo\u0159en s pou\u017eit\u00edm chybn\u00e9ho typu zdroje DOM."},


        {BasisLibrary.PARSER_DTD_SUPPORT_ERR,
        "Pou\u017eit\u00fd analyz\u00e1tor SAX nem\u016f\u017ee manipulovat s deklara\u010dn\u00edmi ud\u00e1lostmi DTD."},


        {BasisLibrary.NAMESPACES_SUPPORT_ERR,
        "Pou\u017eit\u00fd analyz\u00e1tor SAX nem\u016f\u017ee podporovat obory n\u00e1zv\u016f pro XML."},


        {BasisLibrary.CANT_RESOLVE_RELATIVE_URI_ERR,
        "Nelze p\u0159elo\u017eit odkazy URI ''{0}''."}
    };

    }

}
