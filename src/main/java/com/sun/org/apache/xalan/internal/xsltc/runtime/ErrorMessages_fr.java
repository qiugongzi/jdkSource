


package com.sun.org.apache.xalan.internal.xsltc.runtime;

import java.util.ListResourceBundle;


public class ErrorMessages_fr extends ListResourceBundle {



    public Object[][] getContents()
    {
        return new Object[][] {


        {BasisLibrary.RUN_TIME_INTERNAL_ERR,
        "Erreur interne d''ex\u00E9cution dans ''{0}''"},


        {BasisLibrary.RUN_TIME_COPY_ERR,
        "Erreur d'ex\u00E9cution de <xsl:copy>."},


        {BasisLibrary.DATA_CONVERSION_ERR,
        "Conversion de ''{0}'' \u00E0 ''{1}'' non valide."},


        {BasisLibrary.EXTERNAL_FUNC_ERR,
        "Fonction externe ''{0}'' non prise en charge par XSLTC."},


        {BasisLibrary.EQUALITY_EXPR_ERR,
        "Type d'argument inconnu dans l'expression d'\u00E9galit\u00E9."},


        {BasisLibrary.INVALID_ARGUMENT_ERR,
        "Type d''argument ''{0}'' non valide dans l''appel de ''{1}''"},


        {BasisLibrary.FORMAT_NUMBER_ERR,
        "Tentative de formatage du nombre ''{0}'' \u00E0 l''aide du mod\u00E8le ''{1}''."},


        {BasisLibrary.ITERATOR_CLONE_ERR,
        "Impossible de cloner l''it\u00E9rateur ''{0}''."},


        {BasisLibrary.AXIS_SUPPORT_ERR,
        "It\u00E9rateur de l''axe ''{0}'' non pris en charge."},


        {BasisLibrary.TYPED_AXIS_SUPPORT_ERR,
        "It\u00E9rateur de l''axe saisi ''{0}'' non pris en charge."},


        {BasisLibrary.STRAY_ATTRIBUTE_ERR,
        "Attribut ''{0}'' en dehors de l''\u00E9l\u00E9ment."},


        {BasisLibrary.STRAY_NAMESPACE_ERR,
        "La d\u00E9claration d''espace de noms ''{0}''=''{1}'' est \u00E0 l''ext\u00E9rieur de l''\u00E9l\u00E9ment."},


        {BasisLibrary.NAMESPACE_PREFIX_ERR,
        "L''espace de noms du pr\u00E9fixe ''{0}'' n''a pas \u00E9t\u00E9 d\u00E9clar\u00E9."},


        {BasisLibrary.DOM_ADAPTER_INIT_ERR,
        "DOMAdapter cr\u00E9\u00E9 avec le mauvais type de DOM source."},


        {BasisLibrary.PARSER_DTD_SUPPORT_ERR,
        "L'analyseur SAX que vous utilisez ne g\u00E8re pas les \u00E9v\u00E9nements de d\u00E9claration DTD."},


        {BasisLibrary.NAMESPACES_SUPPORT_ERR,
        "L'analyseur SAX que vous utilisez ne prend pas en charge les espaces de noms XML."},


        {BasisLibrary.CANT_RESOLVE_RELATIVE_URI_ERR,
        "Impossible de r\u00E9soudre la r\u00E9f\u00E9rence d''URI ''{0}''."},


        {BasisLibrary.UNSUPPORTED_XSL_ERR,
        "El\u00E9ment XSL ''{0}'' non pris en charge"},


        {BasisLibrary.UNSUPPORTED_EXT_ERR,
        "Extension XSLTC ''{0}'' non reconnue"},



        {BasisLibrary.UNKNOWN_TRANSLET_VERSION_ERR,
        "Le translet sp\u00E9cifi\u00E9, ''{0}'', a \u00E9t\u00E9 cr\u00E9\u00E9 \u00E0 l''aide d''une version de XSLTC plus r\u00E9cente que la version de l''ex\u00E9cution XSLTC utilis\u00E9e. Vous devez recompiler la feuille de style ou utiliser une version plus r\u00E9cente de XSLTC pour ex\u00E9cuter ce translet."},


        {BasisLibrary.INVALID_QNAME_ERR,
        "Un attribut dont la valeur doit \u00EAtre un QName avait la valeur ''{0}''"},



        {BasisLibrary.INVALID_NCNAME_ERR,
        "Un attribut dont la valeur doit \u00EAtre un NCName avait la valeur ''{0}''"},

        {BasisLibrary.UNALLOWED_EXTENSION_FUNCTION_ERR,
        "L''utilisation de la fonction d''extension ''{0}'' n''est pas autoris\u00E9e lorsque la fonctionnalit\u00E9 de traitement s\u00E9curis\u00E9 est d\u00E9finie sur True."},

        {BasisLibrary.UNALLOWED_EXTENSION_ELEMENT_ERR,
        "L''utilisation de l''\u00E9l\u00E9ment d''extension ''{0}'' n''est pas autoris\u00E9e lorsque la fonctionnalit\u00E9 de traitement s\u00E9curis\u00E9 est d\u00E9finie sur True."},
    };
    }

}
