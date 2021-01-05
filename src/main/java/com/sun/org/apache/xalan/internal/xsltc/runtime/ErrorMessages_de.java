


package com.sun.org.apache.xalan.internal.xsltc.runtime;

import java.util.ListResourceBundle;


public class ErrorMessages_de extends ListResourceBundle {



    public Object[][] getContents()
    {
        return new Object[][] {


        {BasisLibrary.RUN_TIME_INTERNAL_ERR,
        "Interner Laufzeitfehler in \"{0}\""},


        {BasisLibrary.RUN_TIME_COPY_ERR,
        "Laufzeitfehler beim Ausf\u00FChren von <xsl:copy>."},


        {BasisLibrary.DATA_CONVERSION_ERR,
        "Ung\u00FCltige Konvertierung von \"{0}\" in \"{1}\"."},


        {BasisLibrary.EXTERNAL_FUNC_ERR,
        "Externe Funktion \"{0}\" nicht unterst\u00FCtzt von XSLTC."},


        {BasisLibrary.EQUALITY_EXPR_ERR,
        "Unbekannter Argumenttyp in Gleichheitsausdruck."},


        {BasisLibrary.INVALID_ARGUMENT_ERR,
        "Ung\u00FCltiger Argumenttyp \"{0}\" in Aufruf von \"{1}\""},


        {BasisLibrary.FORMAT_NUMBER_ERR,
        "Versuch, Zahl \"{0}\" mit Muster \"{1}\" zu formatieren."},


        {BasisLibrary.ITERATOR_CLONE_ERR,
        "Iterator \"{0}\" kann nicht geclont werden."},


        {BasisLibrary.AXIS_SUPPORT_ERR,
        "Iterator f\u00FCr Achse \"{0}\" nicht unterst\u00FCtzt."},


        {BasisLibrary.TYPED_AXIS_SUPPORT_ERR,
        "Iterator f\u00FCr typisierte Achse \"{0}\" nicht unterst\u00FCtzt."},


        {BasisLibrary.STRAY_ATTRIBUTE_ERR,
        "Attribut \"{0}\" au\u00DFerhalb des Elements."},


        {BasisLibrary.STRAY_NAMESPACE_ERR,
        "Namespace-Deklaration {0}={1} au\u00DFerhalb des Elements."},


        {BasisLibrary.NAMESPACE_PREFIX_ERR,
        "Namespace f\u00FCr Pr\u00E4fix \"{0}\" wurde nicht deklariert."},


        {BasisLibrary.DOM_ADAPTER_INIT_ERR,
        "DOMAdapter mit falschem Typ von Quell-DOM erstellt."},


        {BasisLibrary.PARSER_DTD_SUPPORT_ERR,
        "Der verwendete SAX-Parser verarbeitet keine DTD-Deklarationsereignisse."},


        {BasisLibrary.NAMESPACES_SUPPORT_ERR,
        "Der verwendete SAX-Parser unterst\u00FCtzt keine XML-Namespaces."},


        {BasisLibrary.CANT_RESOLVE_RELATIVE_URI_ERR,
        "URI-Referenz \"{0}\" konnte nicht aufgel\u00F6st werden."},


        {BasisLibrary.UNSUPPORTED_XSL_ERR,
        "Nicht unterst\u00FCtztes XSL-Element \"{0}\""},


        {BasisLibrary.UNSUPPORTED_EXT_ERR,
        "Unbekannte XSLTC-Erweiterung \"{0}\""},



        {BasisLibrary.UNKNOWN_TRANSLET_VERSION_ERR,
        "Das angegebene Translet \"{0}\" wurde mit einer neueren Version von XSLTC als die verwendete Version der XSLTC-Laufzeit erstellt. Sie m\u00FCssen das Stylesheet neu kompilieren oder eine aktuellere Version von XSLTC verwenden, um dieses Translet auszuf\u00FChren."},


        {BasisLibrary.INVALID_QNAME_ERR,
        "Ein Attribut, dessen Wert ein QName sein muss, hatte den Wert \"{0}\""},



        {BasisLibrary.INVALID_NCNAME_ERR,
        "Ein Attribut, dessen Wert ein NCName sein muss, hatte den Wert \"{0}\""},

        {BasisLibrary.UNALLOWED_EXTENSION_FUNCTION_ERR,
        "Verwendung der Erweiterungsfunktion \"{0}\" ist nicht zul\u00E4ssig, wenn das Feature f\u00FCr die sichere Verarbeitung auf \"true\" gesetzt ist."},

        {BasisLibrary.UNALLOWED_EXTENSION_ELEMENT_ERR,
        "Verwendung des Erweiterungselements \"{0}\" ist nicht zul\u00E4ssig, wenn das Feature f\u00FCr die sichere Verarbeitung auf \"true\" gesetzt ist."},
    };
    }

}
