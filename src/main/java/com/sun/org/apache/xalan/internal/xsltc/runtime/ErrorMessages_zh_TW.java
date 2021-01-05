


package com.sun.org.apache.xalan.internal.xsltc.runtime;

import java.util.ListResourceBundle;


public class ErrorMessages_zh_TW extends ListResourceBundle {



    public Object[][] getContents()
    {
        return new Object[][] {


        {BasisLibrary.RUN_TIME_INTERNAL_ERR,
        "''{0}'' \u4E2D\u7684\u57F7\u884C\u968E\u6BB5\u5167\u90E8\u932F\u8AA4"},


        {BasisLibrary.RUN_TIME_COPY_ERR,
        "\u57F7\u884C <xsl:copy> \u6642\u767C\u751F\u57F7\u884C\u968E\u6BB5\u932F\u8AA4"},


        {BasisLibrary.DATA_CONVERSION_ERR,
        "\u5F9E ''{0}'' \u81F3 ''{1}'' \u7684\u8F49\u63DB\u7121\u6548\u3002"},


        {BasisLibrary.EXTERNAL_FUNC_ERR,
        "XSLTC \u4E0D\u652F\u63F4\u5916\u90E8\u51FD\u6578 ''{0}''\u3002"},


        {BasisLibrary.EQUALITY_EXPR_ERR,
        "\u76F8\u7B49\u6027\u8868\u793A\u5F0F\u4E2D\u7684\u5F15\u6578\u985E\u578B\u4E0D\u660E\u3002"},


        {BasisLibrary.INVALID_ARGUMENT_ERR,
        "\u547C\u53EB ''{1}'' \u4E2D\u7684\u5F15\u6578\u985E\u578B ''{0}'' \u7121\u6548"},


        {BasisLibrary.FORMAT_NUMBER_ERR,
        "\u5617\u8A66\u4F7F\u7528\u6A23\u5F0F ''{1}'' \u683C\u5F0F\u5316\u6578\u5B57 ''{0}''\u3002"},


        {BasisLibrary.ITERATOR_CLONE_ERR,
        "\u7121\u6CD5\u8907\u88FD\u91CD\u8907\u7A0B\u5F0F ''{0}''\u3002"},


        {BasisLibrary.AXIS_SUPPORT_ERR,
        "\u4E0D\u652F\u63F4\u8EF8 ''{0}'' \u7684\u91CD\u8907\u7A0B\u5F0F\u3002"},


        {BasisLibrary.TYPED_AXIS_SUPPORT_ERR,
        "\u4E0D\u652F\u63F4\u985E\u578B\u8EF8 ''{0}'' \u7684\u91CD\u8907\u7A0B\u5F0F\u3002"},


        {BasisLibrary.STRAY_ATTRIBUTE_ERR,
        "\u5C6C\u6027 ''{0}'' \u5728\u5143\u7D20\u4E4B\u5916\u3002"},


        {BasisLibrary.STRAY_NAMESPACE_ERR,
        "\u547D\u540D\u7A7A\u9593\u5BA3\u544A ''{0}''=''{1}'' \u8D85\u51FA\u5143\u7D20\u5916\u3002"},


        {BasisLibrary.NAMESPACE_PREFIX_ERR,
        "\u5B57\u9996 ''{0}'' \u7684\u547D\u540D\u7A7A\u9593\u5C1A\u672A\u5BA3\u544A\u3002"},


        {BasisLibrary.DOM_ADAPTER_INIT_ERR,
        "\u4F7F\u7528\u932F\u8AA4\u7684\u4F86\u6E90 DOM \u985E\u578B\u5EFA\u7ACB DOMAdapter\u3002"},


        {BasisLibrary.PARSER_DTD_SUPPORT_ERR,
        "\u60A8\u6B63\u5728\u4F7F\u7528\u7684 SAX \u5256\u6790\u5668\u4E0D\u6703\u8655\u7406 DTD \u5BA3\u544A\u4E8B\u4EF6\u3002"},


        {BasisLibrary.NAMESPACES_SUPPORT_ERR,
        "\u60A8\u6B63\u5728\u4F7F\u7528\u7684 SAX \u5256\u6790\u5668\u4E0D\u652F\u63F4 XML \u547D\u540D\u7A7A\u9593\u3002"},


        {BasisLibrary.CANT_RESOLVE_RELATIVE_URI_ERR,
        "\u7121\u6CD5\u89E3\u6790 URI \u53C3\u7167 ''{0}''\u3002"},


        {BasisLibrary.UNSUPPORTED_XSL_ERR,
        "\u4E0D\u652F\u63F4\u7684 XSL \u5143\u7D20 ''{0}''"},


        {BasisLibrary.UNSUPPORTED_EXT_ERR,
        "\u7121\u6CD5\u8FA8\u8B58\u7684 XSLTC \u64F4\u5145\u5957\u4EF6 ''{0}''"},



        {BasisLibrary.UNKNOWN_TRANSLET_VERSION_ERR,
        "\u5EFA\u7ACB\u6307\u5B9A translet ''{0}'' \u7684 XSLTC \u7248\u672C\u6BD4\u4F7F\u7528\u4E2D XSLTC \u57F7\u884C\u968E\u6BB5\u7684\u7248\u672C\u8F03\u65B0\u3002\u60A8\u5FC5\u9808\u91CD\u65B0\u7DE8\u8B6F\u6A23\u5F0F\u8868\uFF0C\u6216\u4F7F\u7528\u8F03\u65B0\u7684 XSLTC \u7248\u672C\u4F86\u57F7\u884C\u6B64 translet\u3002"},


        {BasisLibrary.INVALID_QNAME_ERR,
        "\u503C\u5FC5\u9808\u70BA QName \u7684\u5C6C\u6027\uFF0C\u5177\u6709\u503C ''{0}''"},



        {BasisLibrary.INVALID_NCNAME_ERR,
        "\u503C\u5FC5\u9808\u70BA NCName \u7684\u5C6C\u6027\uFF0C\u5177\u6709\u503C ''{0}''"},

        {BasisLibrary.UNALLOWED_EXTENSION_FUNCTION_ERR,
        "\u7576\u5B89\u5168\u8655\u7406\u529F\u80FD\u8A2D\u70BA\u771F\u6642\uFF0C\u4E0D\u5141\u8A31\u4F7F\u7528\u64F4\u5145\u5957\u4EF6\u51FD\u6578 ''{0}''\u3002"},

        {BasisLibrary.UNALLOWED_EXTENSION_ELEMENT_ERR,
        "\u7576\u5B89\u5168\u8655\u7406\u529F\u80FD\u8A2D\u70BA\u771F\u6642\uFF0C\u4E0D\u5141\u8A31\u4F7F\u7528\u64F4\u5145\u5957\u4EF6\u5143\u7D20 ''{0}''\u3002"},
    };
    }

}
