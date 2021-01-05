


package com.sun.org.apache.xalan.internal.xsltc.runtime;

import java.util.ListResourceBundle;


public class ErrorMessages_pt_BR extends ListResourceBundle {



    public Object[][] getContents()
    {
        return new Object[][] {


        {BasisLibrary.RUN_TIME_INTERNAL_ERR,
        "Erro interno de runtime em ''{0}''"},


        {BasisLibrary.RUN_TIME_COPY_ERR,
        "Erro de runtime ao executar <xsl:copy>."},


        {BasisLibrary.DATA_CONVERSION_ERR,
        "Convers\u00E3o inv\u00E1lida de ''{0}'' para ''{1}''."},


        {BasisLibrary.EXTERNAL_FUNC_ERR,
        "Fun\u00E7\u00E3o externa ''{0}'' n\u00E3o suportada por XSLTC."},


        {BasisLibrary.EQUALITY_EXPR_ERR,
        "Tipo de argumento desconhecido na express\u00E3o de igualdade."},


        {BasisLibrary.INVALID_ARGUMENT_ERR,
        "Tipo de argumento inv\u00E1lido ''{0}'' na chamada para ''{1}''"},


        {BasisLibrary.FORMAT_NUMBER_ERR,
        "Tentativa de formatar o n\u00FAmero ''{0}'' usando o padr\u00E3o ''{1}''."},


        {BasisLibrary.ITERATOR_CLONE_ERR,
        "N\u00E3o \u00E9 poss\u00EDvel clonar o iterador ''{0}''."},


        {BasisLibrary.AXIS_SUPPORT_ERR,
        "Iterador do eixo ''{0}'' n\u00E3o suportado."},


        {BasisLibrary.TYPED_AXIS_SUPPORT_ERR,
        "Iterador do eixo digitado ''{0}'' n\u00E3o suportado."},


        {BasisLibrary.STRAY_ATTRIBUTE_ERR,
        "Atributo ''{0}'' fora do elemento."},


        {BasisLibrary.STRAY_NAMESPACE_ERR,
        "Declara\u00E7\u00E3o de namespace ''{0}''=''{1}'' fora do elemento."},


        {BasisLibrary.NAMESPACE_PREFIX_ERR,
        "O namespace do prefixo ''{0}'' n\u00E3o foi declarado."},


        {BasisLibrary.DOM_ADAPTER_INIT_ERR,
        "DOMAdapter criado usando o tipo incorreto de DOM de origem."},


        {BasisLibrary.PARSER_DTD_SUPPORT_ERR,
        "O parser SAX que voc\u00EA est\u00E1 usando n\u00E3o trata eventos de declara\u00E7\u00E3o de DTD."},


        {BasisLibrary.NAMESPACES_SUPPORT_ERR,
        "O parser SAX que voc\u00EA est\u00E1 usando n\u00E3o tem suporte para os Namespaces de XML."},


        {BasisLibrary.CANT_RESOLVE_RELATIVE_URI_ERR,
        "N\u00E3o foi poss\u00EDvel resolver a refer\u00EAncia do URI ''{0}''."},


        {BasisLibrary.UNSUPPORTED_XSL_ERR,
        "Elemento XSL ''{0}'' n\u00E3o suportado"},


        {BasisLibrary.UNSUPPORTED_EXT_ERR,
        "Extens\u00E3o ''{0}'' de XSLTC n\u00E3o reconhecida"},



        {BasisLibrary.UNKNOWN_TRANSLET_VERSION_ERR,
        "O translet especificado, ''{0}'', foi criado usando uma vers\u00E3o do XSLTC mais recente que a vers\u00E3o de runtime de XSLTC em uso. Recompile a folha de estilos ou use uma vers\u00E3o mais recente de XSLTC para executar este translet."},


        {BasisLibrary.INVALID_QNAME_ERR,
        "Um atributo cujo valor deve ser um QName tinha o valor ''{0}''"},



        {BasisLibrary.INVALID_NCNAME_ERR,
        "Um atributo cujo valor deve ser um NCName tinha o valor ''{0}''"},

        {BasisLibrary.UNALLOWED_EXTENSION_FUNCTION_ERR,
        "O uso da fun\u00E7\u00E3o da extens\u00E3o ''{0}'' n\u00E3o ser\u00E1 permitido quando o recurso de processamento seguro for definido como verdadeiro."},

        {BasisLibrary.UNALLOWED_EXTENSION_ELEMENT_ERR,
        "O uso do elemento da extens\u00E3o ''{0}'' n\u00E3o ser\u00E1 permitido quando o recurso de processamento seguro for definido como verdadeiro."},
    };
    }

}
