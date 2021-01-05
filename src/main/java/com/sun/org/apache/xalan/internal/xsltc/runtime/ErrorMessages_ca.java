



package com.sun.org.apache.xalan.internal.xsltc.runtime;

import java.util.ListResourceBundle;


public class ErrorMessages_ca extends ListResourceBundle {



    public Object[][] getContents()
    {
        return new Object[][] {


        {BasisLibrary.RUN_TIME_INTERNAL_ERR,
        "S''ha produ\u00eft un error intern de temps d''execuci\u00f3 a ''{0}''"},


        {BasisLibrary.RUN_TIME_COPY_ERR,
        "Es produeix un error de temps d'execuci\u00f3 en executar <xsl:copy>."},


        {BasisLibrary.DATA_CONVERSION_ERR,
        "La conversi\u00f3 de ''{0}'' a ''{1}'' no \u00e9s v\u00e0lida."},


        {BasisLibrary.EXTERNAL_FUNC_ERR,
        "XSLTC no d\u00f3na suport a la funci\u00f3 externa ''{0}''."},


        {BasisLibrary.EQUALITY_EXPR_ERR,
        "L'expressi\u00f3 d'igualtat cont\u00e9 un tipus d'argument desconegut."},


        {BasisLibrary.INVALID_ARGUMENT_ERR,
        "La crida a ''{1}'' cont\u00e9 un tipus d''argument ''{0}'' no v\u00e0lid."},


        {BasisLibrary.FORMAT_NUMBER_ERR,
        "S''ha intentat donar format al n\u00famero ''{0}'' mitjan\u00e7ant el patr\u00f3 ''{1}''."},


        {BasisLibrary.ITERATOR_CLONE_ERR,
        "No es pot clonar l''iterador ''{0}''."},


        {BasisLibrary.AXIS_SUPPORT_ERR,
        "L''iterador de l''eix ''{0}'' no t\u00e9 suport."},


        {BasisLibrary.TYPED_AXIS_SUPPORT_ERR,
        "L''iterador de l''eix escrit ''{0}'' no t\u00e9 suport."},


        {BasisLibrary.STRAY_ATTRIBUTE_ERR,
        "L''atribut ''{0}'' es troba fora de l''element."},


        {BasisLibrary.STRAY_NAMESPACE_ERR,
        "La declaraci\u00f3 d''espai de noms ''{0}''=''{1}'' es troba fora de l''element."},


        {BasisLibrary.NAMESPACE_PREFIX_ERR,
        "L''espai de noms del prefix ''{0}'' no s''ha declarat."},


        {BasisLibrary.DOM_ADAPTER_INIT_ERR,
        "DOMAdapter s'ha creat mitjan\u00e7ant un tipus incorrecte de DOM d'origen."},


        {BasisLibrary.PARSER_DTD_SUPPORT_ERR,
        "L'analitzador SAX que feu servir no gestiona esdeveniments de declaraci\u00f3 de DTD."},


        {BasisLibrary.NAMESPACES_SUPPORT_ERR,
        "L'analitzador SAX que feu servir no d\u00f3na suport a espais de noms XML."},


        {BasisLibrary.CANT_RESOLVE_RELATIVE_URI_ERR,
        "No s''ha pogut resoldre la refer\u00e8ncia d''URI ''{0}''."}
    };

    }

}
