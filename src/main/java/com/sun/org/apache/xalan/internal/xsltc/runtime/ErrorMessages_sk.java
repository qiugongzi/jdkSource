



package com.sun.org.apache.xalan.internal.xsltc.runtime;

import java.util.ListResourceBundle;


public class ErrorMessages_sk extends ListResourceBundle {



    public Object[][] getContents()
    {
        return new Object[][] {


        {BasisLibrary.RUN_TIME_INTERNAL_ERR,
        "Intern\u00e1 chyba \u010dasu spustenia v ''{0}''"},


        {BasisLibrary.RUN_TIME_COPY_ERR,
        "Chyba \u010dasu spustenia pri sp\u00fa\u0161\u0165an\u00ed <xsl:copy>."},


        {BasisLibrary.DATA_CONVERSION_ERR,
        "Neplatn\u00e1 konverzia z ''{0}'' na ''{1}''."},


        {BasisLibrary.EXTERNAL_FUNC_ERR,
        "XSLTC nepodporuje extern\u00fa funkciu ''{0}''."},


        {BasisLibrary.EQUALITY_EXPR_ERR,
        "Nezn\u00e1my typ argumentu je v\u00fdrazom rovnosti."},


        {BasisLibrary.INVALID_ARGUMENT_ERR,
        "Neplatn\u00fd typ argumentu ''{0}'' vo volan\u00ed do ''{1}''"},


        {BasisLibrary.FORMAT_NUMBER_ERR,
        "Pokus o form\u00e1tovanie \u010d\u00edsla ''{0}'' pomocou vzoru ''{1}''."},


        {BasisLibrary.ITERATOR_CLONE_ERR,
        "Nie je mo\u017en\u00e9 klonova\u0165 iter\u00e1tor ''{0}''."},


        {BasisLibrary.AXIS_SUPPORT_ERR,
        "Iter\u00e1tor pre os ''{0}'' nie je podporovan\u00fd."},


        {BasisLibrary.TYPED_AXIS_SUPPORT_ERR,
        "Iter\u00e1tor pre nap\u00edsan\u00fa os ''{0}'' nie je podporovan\u00fd."},


        {BasisLibrary.STRAY_ATTRIBUTE_ERR,
        "Atrib\u00fat ''{0}'' je mimo elementu."},


        {BasisLibrary.STRAY_NAMESPACE_ERR,
        "Deklar\u00e1cia n\u00e1zvov\u00e9ho priestoru ''{0}''=''{1}'' je mimo elementu."},


        {BasisLibrary.NAMESPACE_PREFIX_ERR,
        "N\u00e1zvov\u00fd priestor pre predponu ''{0}'' nebol deklarovan\u00fd."},


        {BasisLibrary.DOM_ADAPTER_INIT_ERR,
        "DOMAdapter bol vytvoren\u00fd pomocou nespr\u00e1vneho typu zdrojov\u00e9ho DOM."},


        {BasisLibrary.PARSER_DTD_SUPPORT_ERR,
        "Analyz\u00e1tor SAX, ktor\u00fd pou\u017e\u00edvate, nesprac\u00fava udalosti deklar\u00e1cie DTD."},


        {BasisLibrary.NAMESPACES_SUPPORT_ERR,
        "Analyz\u00e1tor SAX, ktor\u00fd pou\u017e\u00edvate, nem\u00e1 podporu pre n\u00e1zvov\u00e9 priestory XML."},


        {BasisLibrary.CANT_RESOLVE_RELATIVE_URI_ERR,
        "Nebolo mo\u017en\u00e9 rozl\u00ed\u0161i\u0165 referenciu URI ''{0}''."}
    };

    }

}
