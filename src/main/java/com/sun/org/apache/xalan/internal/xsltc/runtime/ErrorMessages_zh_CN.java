


package com.sun.org.apache.xalan.internal.xsltc.runtime;

import java.util.ListResourceBundle;


public class ErrorMessages_zh_CN extends ListResourceBundle {



    public Object[][] getContents()
    {
        return new Object[][] {


        {BasisLibrary.RUN_TIME_INTERNAL_ERR,
        "''{0}'' \u4E2D\u7684\u8FD0\u884C\u65F6\u5185\u90E8\u9519\u8BEF"},


        {BasisLibrary.RUN_TIME_COPY_ERR,
        "\u6267\u884C <xsl:copy> \u65F6\u51FA\u73B0\u8FD0\u884C\u65F6\u9519\u8BEF\u3002"},


        {BasisLibrary.DATA_CONVERSION_ERR,
        "\u4ECE ''{0}'' \u5230 ''{1}'' \u7684\u8F6C\u6362\u65E0\u6548\u3002"},


        {BasisLibrary.EXTERNAL_FUNC_ERR,
        "XSLTC \u4E0D\u652F\u6301\u5916\u90E8\u51FD\u6570 ''{0}''\u3002"},


        {BasisLibrary.EQUALITY_EXPR_ERR,
        "\u7B49\u5F0F\u8868\u8FBE\u5F0F\u4E2D\u7684\u53C2\u6570\u7C7B\u578B\u672A\u77E5\u3002"},


        {BasisLibrary.INVALID_ARGUMENT_ERR,
        "\u8C03\u7528 ''{1}'' \u65F6\u7684\u53C2\u6570\u7C7B\u578B ''{0}'' \u65E0\u6548"},


        {BasisLibrary.FORMAT_NUMBER_ERR,
        "\u5C1D\u8BD5\u4F7F\u7528\u6A21\u5F0F ''{1}'' \u8BBE\u7F6E\u6570\u5B57 ''{0}'' \u7684\u683C\u5F0F\u3002"},


        {BasisLibrary.ITERATOR_CLONE_ERR,
        "\u65E0\u6CD5\u514B\u9686\u8FED\u4EE3\u5668 ''{0}''\u3002"},


        {BasisLibrary.AXIS_SUPPORT_ERR,
        "\u4E0D\u652F\u6301\u8F74 ''{0}'' \u7684\u8FED\u4EE3\u5668\u3002"},


        {BasisLibrary.TYPED_AXIS_SUPPORT_ERR,
        "\u4E0D\u652F\u6301\u7C7B\u578B\u5316\u8F74 ''{0}'' \u7684\u8FED\u4EE3\u5668\u3002"},


        {BasisLibrary.STRAY_ATTRIBUTE_ERR,
        "\u5C5E\u6027 ''{0}'' \u5728\u5143\u7D20\u5916\u90E8\u3002"},


        {BasisLibrary.STRAY_NAMESPACE_ERR,
        "\u540D\u79F0\u7A7A\u95F4\u58F0\u660E ''{0}''=''{1}'' \u5728\u5143\u7D20\u5916\u90E8\u3002"},


        {BasisLibrary.NAMESPACE_PREFIX_ERR,
        "\u6CA1\u6709\u8BF4\u660E\u540D\u79F0\u7A7A\u95F4\u524D\u7F00 ''{0}''\u3002"},


        {BasisLibrary.DOM_ADAPTER_INIT_ERR,
        "\u4F7F\u7528\u9519\u8BEF\u7C7B\u578B\u7684\u6E90 DOM \u521B\u5EFA\u4E86 DOMAdapter\u3002"},


        {BasisLibrary.PARSER_DTD_SUPPORT_ERR,
        "\u4F7F\u7528\u7684 SAX \u89E3\u6790\u5668\u4E0D\u4F1A\u5904\u7406 DTD \u58F0\u660E\u4E8B\u4EF6\u3002"},


        {BasisLibrary.NAMESPACES_SUPPORT_ERR,
        "\u4F7F\u7528\u7684 SAX \u89E3\u6790\u5668\u4E0D\u652F\u6301 XML \u540D\u79F0\u7A7A\u95F4\u3002"},


        {BasisLibrary.CANT_RESOLVE_RELATIVE_URI_ERR,
        "\u65E0\u6CD5\u89E3\u6790 URI \u5F15\u7528 ''{0}''\u3002"},


        {BasisLibrary.UNSUPPORTED_XSL_ERR,
        "XSL \u5143\u7D20 ''{0}'' \u4E0D\u53D7\u652F\u6301"},


        {BasisLibrary.UNSUPPORTED_EXT_ERR,
        "XSLTC \u6269\u5C55 ''{0}'' \u65E0\u6CD5\u8BC6\u522B"},



        {BasisLibrary.UNKNOWN_TRANSLET_VERSION_ERR,
        "\u521B\u5EFA\u6307\u5B9A translet ''{0}'' \u65F6\u4F7F\u7528\u7684 XSLTC \u7684\u7248\u672C\u9AD8\u4E8E\u6B63\u5728\u4F7F\u7528\u7684 XSLTC \u8FD0\u884C\u65F6\u7684\u7248\u672C\u3002\u5FC5\u987B\u91CD\u65B0\u7F16\u8BD1\u6837\u5F0F\u8868\u6216\u4F7F\u7528\u8F83\u65B0\u7684 XSLTC \u7248\u672C\u8FD0\u884C\u6B64 translet\u3002"},


        {BasisLibrary.INVALID_QNAME_ERR,
        "\u5176\u503C\u5FC5\u987B\u4E3A QName \u7684\u5C5E\u6027\u5177\u6709\u503C ''{0}''"},



        {BasisLibrary.INVALID_NCNAME_ERR,
        "\u5176\u503C\u5FC5\u987B\u4E3A NCName \u7684\u5C5E\u6027\u5177\u6709\u503C ''{0}''"},

        {BasisLibrary.UNALLOWED_EXTENSION_FUNCTION_ERR,
        "\u5F53\u5B89\u5168\u5904\u7406\u529F\u80FD\u8BBE\u7F6E\u4E3A\u201C\u771F\u201D\u65F6, \u4E0D\u5141\u8BB8\u4F7F\u7528\u6269\u5C55\u51FD\u6570 ''{0}''\u3002"},

        {BasisLibrary.UNALLOWED_EXTENSION_ELEMENT_ERR,
        "\u5F53\u5B89\u5168\u5904\u7406\u529F\u80FD\u8BBE\u7F6E\u4E3A\u201C\u771F\u201D\u65F6, \u4E0D\u5141\u8BB8\u4F7F\u7528\u6269\u5C55\u5143\u7D20 ''{0}''\u3002"},
    };
    }

}
