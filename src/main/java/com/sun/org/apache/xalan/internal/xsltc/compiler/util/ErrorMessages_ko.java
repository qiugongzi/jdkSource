


package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import java.util.ListResourceBundle;


public class ErrorMessages_ko extends ListResourceBundle {



    public Object[][] getContents()
    {
      return new Object[][] {
        {ErrorMsg.MULTIPLE_STYLESHEET_ERR,
        "\uB3D9\uC77C\uD55C \uD30C\uC77C\uC5D0 \uC2A4\uD0C0\uC77C\uC2DC\uD2B8\uAC00 \uB450 \uAC1C \uC774\uC0C1 \uC815\uC758\uB418\uC5C8\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.TEMPLATE_REDEF_ERR,
        "\uC774 \uC2A4\uD0C0\uC77C\uC2DC\uD2B8\uC5D0\uB294 ''{0}'' \uD15C\uD50C\uB9AC\uD2B8\uAC00 \uC774\uBBF8 \uC815\uC758\uB418\uC5C8\uC2B5\uB2C8\uB2E4."},



        {ErrorMsg.TEMPLATE_UNDEF_ERR,
        "\uC774 \uC2A4\uD0C0\uC77C\uC2DC\uD2B8\uC5D0\uB294 ''{0}'' \uD15C\uD50C\uB9AC\uD2B8\uAC00 \uC815\uC758\uB418\uC9C0 \uC54A\uC558\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.VARIABLE_REDEF_ERR,
        "\uB3D9\uC77C\uD55C \uBC94\uC704\uC5D0\uC11C ''{0}'' \uBCC0\uC218\uAC00 \uC5EC\uB7EC \uAC1C \uC815\uC758\uB418\uC5C8\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.VARIABLE_UNDEF_ERR,
        "\uBCC0\uC218 \uB610\uB294 \uB9E4\uAC1C\uBCC0\uC218 ''{0}''\uC774(\uAC00) \uC815\uC758\uB418\uC9C0 \uC54A\uC558\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.CLASS_NOT_FOUND_ERR,
        "''{0}'' \uD074\uB798\uC2A4\uB97C \uCC3E\uC744 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.METHOD_NOT_FOUND_ERR,
        "\uC678\uBD80 \uBA54\uC18C\uB4DC ''{0}''\uC744(\uB97C) \uCC3E\uC744 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4. \uC774 \uBA54\uC18C\uB4DC\uB294 public\uC774\uC5B4\uC57C \uD569\uB2C8\uB2E4."},


        {ErrorMsg.ARGUMENT_CONVERSION_ERR,
        "''{0}'' \uBA54\uC18C\uB4DC\uC5D0 \uB300\uD55C \uD638\uCD9C\uC5D0\uC11C \uC778\uC218/\uBC18\uD658 \uC720\uD615\uC744 \uBCC0\uD658\uD560 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.FILE_NOT_FOUND_ERR,
        "\uD30C\uC77C \uB610\uB294 URI ''{0}''\uC744(\uB97C) \uCC3E\uC744 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.INVALID_URI_ERR,
        "URI ''{0}''\uC774(\uAC00) \uBD80\uC801\uD569\uD569\uB2C8\uB2E4."},


        {ErrorMsg.FILE_ACCESS_ERR,
        "\uD30C\uC77C \uB610\uB294 URI ''{0}''\uC744(\uB97C) \uC5F4 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.MISSING_ROOT_ERR,
        "<xsl:stylesheet> \uB610\uB294 <xsl:transform> \uC694\uC18C\uAC00 \uD544\uC694\uD569\uB2C8\uB2E4."},


        {ErrorMsg.NAMESPACE_UNDEF_ERR,
        "\uB124\uC784\uC2A4\uD398\uC774\uC2A4 \uC811\uB450\uC5B4 ''{0}''\uC774(\uAC00) \uC120\uC5B8\uB418\uC9C0 \uC54A\uC558\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.FUNCTION_RESOLVE_ERR,
        "''{0}'' \uD568\uC218\uC5D0 \uB300\uD55C \uD638\uCD9C\uC744 \uBD84\uC11D\uD560 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.NEED_LITERAL_ERR,
        "''{0}''\uC5D0 \uB300\uD55C \uC778\uC218\uB294 \uB9AC\uD130\uB7F4 \uBB38\uC790\uC5F4\uC774\uC5B4\uC57C \uD569\uB2C8\uB2E4."},


        {ErrorMsg.XPATH_PARSER_ERR,
        "XPath \uD45C\uD604\uC2DD ''{0}''\uC758 \uAD6C\uBB38\uC744 \uBD84\uC11D\uD558\uB294 \uC911 \uC624\uB958\uAC00 \uBC1C\uC0DD\uD588\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.REQUIRED_ATTR_ERR,
        "\uD544\uC218 \uC18D\uC131 ''{0}''\uC774(\uAC00) \uB204\uB77D\uB418\uC5C8\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.ILLEGAL_CHAR_ERR,
        "XPath \uD45C\uD604\uC2DD\uC5D0 \uC798\uBABB\uB41C \uBB38\uC790 ''{0}''\uC774(\uAC00) \uC788\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.ILLEGAL_PI_ERR,
        "''{0}''\uC740(\uB294) \uBA85\uB839 \uCC98\uB9AC\uC5D0 \uC798\uBABB\uB41C \uC774\uB984\uC785\uB2C8\uB2E4."},


        {ErrorMsg.STRAY_ATTRIBUTE_ERR,
        "''{0}'' \uC18D\uC131\uC774 \uC694\uC18C\uC5D0 \uD3EC\uD568\uB418\uC5B4 \uC788\uC9C0 \uC54A\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.ILLEGAL_ATTRIBUTE_ERR,
        "''{0}''\uC740(\uB294) \uC798\uBABB\uB41C \uC18D\uC131\uC785\uB2C8\uB2E4."},


        {ErrorMsg.CIRCULAR_INCLUDE_ERR,
        "\uC21C\uD658 import/include\uC785\uB2C8\uB2E4. ''{0}'' \uC2A4\uD0C0\uC77C\uC2DC\uD2B8\uAC00 \uC774\uBBF8 \uB85C\uB4DC\uB418\uC5C8\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.RESULT_TREE_SORT_ERR,
        "Result-tree \uBD80\uBD84\uC744 \uC815\uB82C\uD560 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4(<xsl:sort> \uC694\uC18C\uAC00 \uBB34\uC2DC\uB428). \uACB0\uACFC \uD2B8\uB9AC\uB97C \uC0DD\uC131\uD560 \uB54C\uB294 \uB178\uB4DC\uB97C \uC815\uB82C\uD574\uC57C \uD569\uB2C8\uB2E4."},


        {ErrorMsg.SYMBOLS_REDEF_ERR,
        "\uC2ED\uC9C4\uC218 \uD615\uC2DD ''{0}''\uC774(\uAC00) \uC774\uBBF8 \uC815\uC758\uB418\uC5C8\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.XSL_VERSION_ERR,
        "XSLTC\uB294 XSL \uBC84\uC804 ''{0}''\uC744(\uB97C) \uC9C0\uC6D0\uD558\uC9C0 \uC54A\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.CIRCULAR_VARIABLE_ERR,
        "''{0}''\uC5D0 \uC21C\uD658 \uBCC0\uC218/\uB9E4\uAC1C\uBCC0\uC218 \uCC38\uC870\uAC00 \uC788\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.ILLEGAL_BINARY_OP_ERR,
        "\uBC14\uC774\uB108\uB9AC \uD45C\uD604\uC2DD\uC5D0 \uB300\uD574 \uC54C \uC218 \uC5C6\uB294 \uC5F0\uC0B0\uC790\uC785\uB2C8\uB2E4."},


        {ErrorMsg.ILLEGAL_ARG_ERR,
        "\uD568\uC218 \uD638\uCD9C\uC5D0 \uB300\uD55C \uC778\uC218\uAC00 \uC798\uBABB\uB418\uC5C8\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.DOCUMENT_ARG_ERR,
        "document() \uD568\uC218\uC5D0 \uB300\uD55C \uB450\uBC88\uC9F8 \uC778\uC218\uB294 node-set\uC5EC\uC57C \uD569\uB2C8\uB2E4."},


        {ErrorMsg.MISSING_WHEN_ERR,
        "<xsl:choose>\uC5D0\uB294 <xsl:when> \uC694\uC18C\uAC00 \uD558\uB098 \uC774\uC0C1 \uD544\uC694\uD569\uB2C8\uB2E4."},


        {ErrorMsg.MULTIPLE_OTHERWISE_ERR,
        "<xsl:choose>\uC5D0\uC11C\uB294 <xsl:otherwise> \uC694\uC18C\uAC00 \uD558\uB098\uB9CC \uD5C8\uC6A9\uB429\uB2C8\uB2E4."},


        {ErrorMsg.STRAY_OTHERWISE_ERR,
        "<xsl:otherwise>\uB294 <xsl:choose>\uC5D0\uC11C\uB9CC \uC0AC\uC6A9\uD560 \uC218 \uC788\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.STRAY_WHEN_ERR,
        "<xsl:when>\uC740 <xsl:choose>\uC5D0\uC11C\uB9CC \uC0AC\uC6A9\uD560 \uC218 \uC788\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.WHEN_ELEMENT_ERR,
        "<xsl:choose>\uC5D0\uC11C\uB294 <xsl:when> \uBC0F <xsl:otherwise> \uC694\uC18C\uB9CC \uD5C8\uC6A9\uB429\uB2C8\uB2E4."},


        {ErrorMsg.UNNAMED_ATTRIBSET_ERR,
        "<xsl:attribute-set>\uC5D0 'name' \uC18D\uC131\uC774 \uB204\uB77D\uB418\uC5C8\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.ILLEGAL_CHILD_ERR,
        "\uD558\uC704 \uC694\uC18C\uAC00 \uC798\uBABB\uB418\uC5C8\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.ILLEGAL_ELEM_NAME_ERR,
        "''{0}'' \uC694\uC18C\uB97C \uD638\uCD9C\uD560 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.ILLEGAL_ATTR_NAME_ERR,
        "''{0}'' \uC18D\uC131\uC744 \uD638\uCD9C\uD560 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.ILLEGAL_TEXT_NODE_ERR,
        "\uD14D\uC2A4\uD2B8 \uB370\uC774\uD130\uAC00 \uCD5C\uC0C1\uC704 \uB808\uBCA8 <xsl:stylesheet> \uC694\uC18C\uC5D0 \uD3EC\uD568\uB418\uC5B4 \uC788\uC9C0 \uC54A\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.SAX_PARSER_CONFIG_ERR,
        "JAXP \uAD6C\uBB38 \uBD84\uC11D\uAE30\uAC00 \uC81C\uB300\uB85C \uAD6C\uC131\uB418\uC9C0 \uC54A\uC558\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.INTERNAL_ERR,
        "\uBCF5\uAD6C\uD560 \uC218 \uC5C6\uB294 XSLTC \uB0B4\uBD80 \uC624\uB958: ''{0}''"},


        {ErrorMsg.UNSUPPORTED_XSL_ERR,
        "''{0}''\uC740(\uB294) \uC9C0\uC6D0\uB418\uC9C0 \uC54A\uB294 XSL \uC694\uC18C\uC785\uB2C8\uB2E4."},


        {ErrorMsg.UNSUPPORTED_EXT_ERR,
        "''{0}''\uC740(\uB294) \uC54C \uC218 \uC5C6\uB294 XSLTC \uD655\uC7A5\uC785\uB2C8\uB2E4."},


        {ErrorMsg.MISSING_XSLT_URI_ERR,
        "\uC785\uB825 \uBB38\uC11C\uB294 \uC2A4\uD0C0\uC77C\uC2DC\uD2B8\uAC00 \uC544\uB2D9\uB2C8\uB2E4. XSL \uB124\uC784\uC2A4\uD398\uC774\uC2A4\uAC00 \uB8E8\uD2B8 \uC694\uC18C\uC5D0 \uC120\uC5B8\uB418\uC9C0 \uC54A\uC558\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.MISSING_XSLT_TARGET_ERR,
        "\uC2A4\uD0C0\uC77C\uC2DC\uD2B8 \uB300\uC0C1 ''{0}''\uC744(\uB97C) \uCC3E\uC744 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.ACCESSING_XSLT_TARGET_ERR,
        "accessExternalStylesheet \uC18D\uC131\uC73C\uB85C \uC124\uC815\uB41C \uC81C\uD55C\uC73C\uB85C \uC778\uD574 ''{1}'' \uC561\uC138\uC2A4\uAC00 \uD5C8\uC6A9\uB418\uC9C0 \uC54A\uC73C\uBBC0\uB85C \uC2A4\uD0C0\uC77C\uC2DC\uD2B8 \uB300\uC0C1 ''{0}''\uC744(\uB97C) \uC77D\uC744 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.NOT_IMPLEMENTED_ERR,
        "\uAD6C\uD604\uB418\uC9C0 \uC54A\uC74C: ''{0}''."},


        {ErrorMsg.NOT_STYLESHEET_ERR,
        "\uC785\uB825 \uBB38\uC11C\uC5D0 XSL \uC2A4\uD0C0\uC77C\uC2DC\uD2B8\uAC00 \uD3EC\uD568\uB418\uC5B4 \uC788\uC9C0 \uC54A\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.ELEMENT_PARSE_ERR,
        "''{0}'' \uC694\uC18C\uC758 \uAD6C\uBB38\uC744 \uBD84\uC11D\uD560 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.KEY_USE_ATTR_ERR,
        "<key>\uC758 use \uC18D\uC131\uC740 node, node-set, string \uB610\uB294 number\uC5EC\uC57C \uD569\uB2C8\uB2E4."},


        {ErrorMsg.OUTPUT_VERSION_ERR,
        "\uCD9C\uB825 XML \uBB38\uC11C \uBC84\uC804\uC740 1.0\uC774\uC5B4\uC57C \uD569\uB2C8\uB2E4."},


        {ErrorMsg.ILLEGAL_RELAT_OP_ERR,
        "\uAD00\uACC4 \uD45C\uD604\uC2DD\uC5D0 \uB300\uD574 \uC54C \uC218 \uC5C6\uB294 \uC5F0\uC0B0\uC790\uC785\uB2C8\uB2E4."},


        {ErrorMsg.ATTRIBSET_UNDEF_ERR,
        "\uC874\uC7AC\uD558\uC9C0 \uC54A\uB294 \uC18D\uC131 \uC9D1\uD569 ''{0}''\uC744(\uB97C) \uC0AC\uC6A9\uD558\uB824\uACE0 \uC2DC\uB3C4\uD558\uB294 \uC911\uC785\uB2C8\uB2E4."},


        {ErrorMsg.ATTR_VAL_TEMPLATE_ERR,
        "\uC18D\uC131\uAC12 \uD15C\uD50C\uB9AC\uD2B8 ''{0}''\uC758 \uAD6C\uBB38\uC744 \uBD84\uC11D\uD560 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.UNKNOWN_SIG_TYPE_ERR,
        "''{0}'' \uD074\uB798\uC2A4\uC5D0 \uB300\uD55C \uC11C\uBA85\uC5D0 \uC54C \uC218 \uC5C6\uB294 \uB370\uC774\uD130 \uC720\uD615\uC774 \uC788\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.DATA_CONVERSION_ERR,
        "\uB370\uC774\uD130 \uC720\uD615 ''{0}''\uC744(\uB97C) ''{1}''(\uC73C)\uB85C \uBCC0\uD658\uD560 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.NO_TRANSLET_CLASS_ERR,
        "\uC774 Templates\uC5D0\uB294 \uC801\uD569\uD55C translet \uD074\uB798\uC2A4 \uC815\uC758\uAC00 \uD3EC\uD568\uB418\uC5B4 \uC788\uC9C0 \uC54A\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.NO_MAIN_TRANSLET_ERR,
        "\uC774 Templates\uC5D0\uB294 \uC774\uB984\uC774 ''{0}''\uC778 \uD074\uB798\uC2A4\uAC00 \uD3EC\uD568\uB418\uC5B4 \uC788\uC9C0 \uC54A\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.TRANSLET_CLASS_ERR,
        "Translet \uD074\uB798\uC2A4 ''{0}''\uC744(\uB97C) \uB85C\uB4DC\uD560 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},

        {ErrorMsg.TRANSLET_OBJECT_ERR,
        "Translet \uD074\uB798\uC2A4\uAC00 \uB85C\uB4DC\uB418\uC5C8\uC9C0\uB9CC translet \uC778\uC2A4\uD134\uC2A4\uB97C \uC0DD\uC131\uD560 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.ERROR_LISTENER_NULL_ERR,
        "''{0}''\uC5D0 \uB300\uD55C ErrorListener\uB97C null\uB85C \uC124\uC815\uD558\uB824\uACE0 \uC2DC\uB3C4\uD558\uB294 \uC911"},


        {ErrorMsg.JAXP_UNKNOWN_SOURCE_ERR,
        "XSLTC\uB294 StreamSource, SAXSource \uBC0F DOMSource\uB9CC \uC9C0\uC6D0\uD569\uB2C8\uB2E4."},


        {ErrorMsg.JAXP_NO_SOURCE_ERR,
        "''{0}''(\uC73C)\uB85C \uC804\uB2EC\uB41C Source \uAC1D\uCCB4\uC5D0 \uCF58\uD150\uCE20\uAC00 \uC5C6\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.JAXP_COMPILE_ERR,
        "\uC2A4\uD0C0\uC77C\uC2DC\uD2B8\uB97C \uCEF4\uD30C\uC77C\uD560 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.JAXP_INVALID_ATTR_ERR,
        "TransformerFactory\uC5D0\uC11C ''{0}'' \uC18D\uC131\uC744 \uC778\uC2DD\uD558\uC9C0 \uBABB\uD588\uC2B5\uB2C8\uB2E4."},

        {ErrorMsg.JAXP_INVALID_ATTR_VALUE_ERR,
        "''{0}'' \uC18D\uC131\uC5D0 \uB300\uD574 \uC62C\uBC14\uB974\uC9C0 \uC54A\uC740 \uAC12\uC774 \uC9C0\uC815\uB418\uC5C8\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.JAXP_SET_RESULT_ERR,
        "setResult()\uB294 startDocument() \uC55E\uC5D0 \uD638\uCD9C\uB418\uC5B4\uC57C \uD569\uB2C8\uB2E4."},


        {ErrorMsg.JAXP_NO_TRANSLET_ERR,
        "Transformer\uC5D0 \uCEA1\uC290\uD654\uB41C translet \uAC1D\uCCB4\uAC00 \uC5C6\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.JAXP_NO_HANDLER_ERR,
        "\uBCC0\uD658 \uACB0\uACFC\uC5D0 \uB300\uD574 \uC815\uC758\uB41C \uCD9C\uB825 \uCC98\uB9AC\uAE30\uAC00 \uC5C6\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.JAXP_NO_RESULT_ERR,
        "''{0}''(\uC73C)\uB85C \uC804\uB2EC\uB41C Result \uAC1D\uCCB4\uAC00 \uBD80\uC801\uD569\uD569\uB2C8\uB2E4."},


        {ErrorMsg.JAXP_UNKNOWN_PROP_ERR,
        "\uBD80\uC801\uD569\uD55C Transformer \uC18D\uC131 ''{0}''\uC5D0 \uC561\uC138\uC2A4\uD558\uB824\uACE0 \uC2DC\uB3C4\uD558\uB294 \uC911\uC785\uB2C8\uB2E4."},


        {ErrorMsg.SAX2DOM_ADAPTER_ERR,
        "SAX2DOM \uC5B4\uB311\uD130\uB97C \uC0DD\uC131\uD560 \uC218 \uC5C6\uC74C: ''{0}''."},


        {ErrorMsg.XSLTC_SOURCE_ERR,
        "systemId\uB97C \uC124\uC815\uD558\uC9C0 \uC54A\uC740 \uC0C1\uD0DC\uB85C XSLTCSource.build()\uAC00 \uD638\uCD9C\uB418\uC5C8\uC2B5\uB2C8\uB2E4."},

        { ErrorMsg.ER_RESULT_NULL,
            "\uACB0\uACFC\uB294 \uB110\uC774 \uC544\uB2C8\uC5B4\uC57C \uD569\uB2C8\uB2E4."},


        {ErrorMsg.JAXP_INVALID_SET_PARAM_VALUE,
        "{0} \uB9E4\uAC1C\uBCC0\uC218\uC758 \uAC12\uC740 \uC801\uD569\uD55C Java \uAC1D\uCCB4\uC5EC\uC57C \uD569\uB2C8\uB2E4."},


        {ErrorMsg.COMPILE_STDIN_ERR,
        "-i \uC635\uC158\uC740 -o \uC635\uC158\uACFC \uD568\uAED8 \uC0AC\uC6A9\uD574\uC57C \uD569\uB2C8\uB2E4."},



        {ErrorMsg.COMPILE_USAGE_STR,
        "\uC0AC\uC6A9\uBC95\n   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Compile [-o <output>]\n      [-d <directory>] [-j <jarfile>] [-p <package>]\n      [-n] [-x] [-u] [-v] [-h] { <stylesheet> | -i }\n\n\uC635\uC158\n   -o <output>    \uC0DD\uC131\uB41C translet\uC5D0 <output> \uC774\uB984\uC744\n                  \uC9C0\uC815\uD569\uB2C8\uB2E4. \uAE30\uBCF8\uC801\uC73C\uB85C translet \uC774\uB984\uC740\n                  <stylesheet> \uC774\uB984\uC5D0\uC11C \uD30C\uC0DD\uB429\uB2C8\uB2E4. \uC5EC\uB7EC \uC2A4\uD0C0\uC77C\uC2DC\uD2B8\uB97C\n                  \uCEF4\uD30C\uC77C\uD558\uB294 \uACBD\uC6B0 \uC774 \uC635\uC158\uC740 \uBB34\uC2DC\uB429\uB2C8\uB2E4.\n   -d <directory> translet\uC5D0 \uB300\uD55C \uB300\uC0C1 \uB514\uB809\uD1A0\uB9AC\uB97C \uC9C0\uC815\uD569\uB2C8\uB2E4.\n   -j <jarfile>   translet \uD074\uB798\uC2A4\uB97C <jarfile>\uC774\uB77C\uB294 \uC774\uB984\uC774 \uC9C0\uC815\uB41C jar \uD30C\uC77C\uC5D0\n                  \uD328\uD0A4\uC9C0\uD654\uD569\uB2C8\uB2E4.\n   -p <package>   \uC0DD\uC131\uB41C \uBAA8\uB4E0 translet \uD074\uB798\uC2A4\uC5D0 \uB300\uD574 \uD328\uD0A4\uC9C0 \uC774\uB984 \uC811\uB450\uC5B4\uB97C\n                  \uC9C0\uC815\uD569\uB2C8\uB2E4.\n   -n             \uD15C\uD50C\uB9AC\uD2B8 \uC778\uB77C\uC778\uC744 \uC0AC\uC6A9\uC73C\uB85C \uC124\uC815\uD569\uB2C8\uB2E4. \uC77C\uBC18\uC801\uC73C\uB85C \uAE30\uBCF8 \uB3D9\uC791\uC744\n                  \uC0AC\uC6A9\uD558\uB294 \uAC83\uC774 \uC88B\uC2B5\uB2C8\uB2E4.\n   -x             \uCD94\uAC00 \uB514\uBC84\uAE45 \uBA54\uC2DC\uC9C0 \uCD9C\uB825\uC744 \uC124\uC815\uD569\uB2C8\uB2E4.\n   -u             <stylesheet> \uC778\uC218\uB97C URL\uB85C \uD574\uC11D\uD569\uB2C8\uB2E4.\n   -i             \uCEF4\uD30C\uC77C\uB7EC\uAC00 stdin\uC5D0\uC11C \uC2A4\uD0C0\uC77C\uC2DC\uD2B8\uB97C \uAC15\uC81C\uB85C \uC77D\uB3C4\uB85D \uD569\uB2C8\uB2E4.\n   -v             \uCEF4\uD30C\uC77C\uB7EC\uC758 \uBC84\uC804\uC744 \uC778\uC1C4\uD569\uB2C8\uB2E4.\n   -h             \uC774 \uC0AC\uC6A9\uBC95 \uC9C0\uCE68\uC744 \uC778\uC1C4\uD569\uB2C8\uB2E4.\n"},


        {ErrorMsg.TRANSFORM_USAGE_STR,
        "\uC0AC\uC6A9\uBC95 \n   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Transform [-j <jarfile>]\n      [-x] [-n <iterations>] {-u <document_url> | <document>}\n      <class> [<param1>=<value1> ...]\n\n   translet <class>\uB97C \uC0AC\uC6A9\uD558\uC5EC <document>\uB85C \uC9C0\uC815\uB41C XML \uBB38\uC11C\uB97C \n   \uBCC0\uD658\uD569\uB2C8\uB2E4. translet <class>\uB294 \n   \uC0AC\uC6A9\uC790\uC758 CLASSPATH \uB610\uB294 \uC120\uD0DD\uC801\uC73C\uB85C \uC9C0\uC815\uB41C <jarfile>\uC5D0 \uC788\uC2B5\uB2C8\uB2E4.\n\uC635\uC158\n   -j <jarfile>    translet\uC744 \uB85C\uB4DC\uD574 \uC62C jarfile\uC744 \uC9C0\uC815\uD569\uB2C8\uB2E4.\n   -x              \uCD94\uAC00 \uB514\uBC84\uAE45 \uBA54\uC2DC\uC9C0 \uCD9C\uB825\uC744 \uC124\uC815\uD569\uB2C8\uB2E4.\n   -n <iterations> \uBCC0\uD658\uC744 <iterations>\uD68C \uC2E4\uD589\uD558\uACE0\n                   \uD504\uB85C\uD30C\uC77C \uC791\uC131 \uC815\uBCF4\uB97C \uD45C\uC2DC\uD569\uB2C8\uB2E4.\n   -u <document_url> XML \uC785\uB825 \uBB38\uC11C\uB97C URL\uB85C \uC9C0\uC815\uD569\uB2C8\uB2E4.\n"},




        {ErrorMsg.STRAY_SORT_ERR,
        "<xsl:sort>\uB294 <xsl:for-each> \uB610\uB294 <xsl:apply-templates>\uC5D0\uC11C\uB9CC \uC0AC\uC6A9\uD560 \uC218 \uC788\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.UNSUPPORTED_ENCODING,
        "\uC774 JVM\uC5D0\uC11C\uB294 \uCD9C\uB825 \uC778\uCF54\uB529 ''{0}''\uC774(\uAC00) \uC9C0\uC6D0\uB418\uC9C0 \uC54A\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.SYNTAX_ERR,
        "''{0}''\uC5D0 \uAD6C\uBB38 \uC624\uB958\uAC00 \uC788\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.CONSTRUCTOR_NOT_FOUND,
        "\uC678\uBD80 constructor ''{0}''\uC744(\uB97C) \uCC3E\uC744 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.NO_JAVA_FUNCT_THIS_REF,
        "\uBE44static Java \uD568\uC218 ''{0}''\uC5D0 \uB300\uD55C \uCCAB\uBC88\uC9F8 \uC778\uC218\uB294 \uC801\uD569\uD55C \uAC1D\uCCB4 \uCC38\uC870\uAC00 \uC544\uB2D9\uB2C8\uB2E4."},


        {ErrorMsg.TYPE_CHECK_ERR,
        "''{0}'' \uD45C\uD604\uC2DD\uC758 \uC720\uD615\uC744 \uD655\uC778\uD558\uB294 \uC911 \uC624\uB958\uAC00 \uBC1C\uC0DD\uD588\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.TYPE_CHECK_UNK_LOC_ERR,
        "\uC54C \uC218 \uC5C6\uB294 \uC704\uCE58\uC5D0\uC11C \uD45C\uD604\uC2DD\uC758 \uC720\uD615\uC744 \uD655\uC778\uD558\uB294 \uC911 \uC624\uB958\uAC00 \uBC1C\uC0DD\uD588\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.ILLEGAL_CMDLINE_OPTION_ERR,
        "\uBA85\uB839\uD589 \uC635\uC158 ''{0}''\uC774(\uAC00) \uBD80\uC801\uD569\uD569\uB2C8\uB2E4."},


        {ErrorMsg.CMDLINE_OPT_MISSING_ARG_ERR,
        "\uBA85\uB839\uD589 \uC635\uC158 ''{0}''\uC5D0 \uD544\uC218 \uC778\uC218\uAC00 \uB204\uB77D\uB418\uC5C8\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.WARNING_PLUS_WRAPPED_MSG,
        "WARNING:  ''{0}''\n       :{1}"},


        {ErrorMsg.WARNING_MSG,
        "WARNING:  ''{0}''"},


        {ErrorMsg.FATAL_ERR_PLUS_WRAPPED_MSG,
        "FATAL ERROR:  ''{0}''\n           :{1}"},


        {ErrorMsg.FATAL_ERR_MSG,
        "FATAL ERROR:  ''{0}''"},


        {ErrorMsg.ERROR_PLUS_WRAPPED_MSG,
        "ERROR:  ''{0}''\n     :{1}"},


        {ErrorMsg.ERROR_MSG,
        "ERROR:  ''{0}''"},


        {ErrorMsg.TRANSFORM_WITH_TRANSLET_STR,
        "translet ''{0}''\uC744(\uB97C) \uC0AC\uC6A9\uD558\uC5EC \uBCC0\uD658\uD558\uC2ED\uC2DC\uC624. "},


        {ErrorMsg.TRANSFORM_WITH_JAR_STR,
        "jar \uD30C\uC77C ''{1}''\uC758 translet ''{0}''\uC744(\uB97C) \uC0AC\uC6A9\uD558\uC5EC \uBCC0\uD658\uD558\uC2ED\uC2DC\uC624."},


        {ErrorMsg.COULD_NOT_CREATE_TRANS_FACT,
        "TransformerFactory \uD074\uB798\uC2A4 ''{0}''\uC758 \uC778\uC2A4\uD134\uC2A4\uB97C \uC0DD\uC131\uD560 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.TRANSLET_NAME_JAVA_CONFLICT,
         "''{0}'' \uC774\uB984\uC5D0\uB294 Java \uD074\uB798\uC2A4 \uC774\uB984\uC5D0 \uD5C8\uC6A9\uB418\uC9C0 \uC54A\uB294 \uBB38\uC790\uAC00 \uD3EC\uD568\uB418\uC5B4 \uC788\uC5B4 \uC774 \uC774\uB984\uC744 translet \uD074\uB798\uC2A4\uC758 \uC774\uB984\uC73C\uB85C \uC0AC\uC6A9\uD560 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4. \uB300\uC2E0 ''{1}'' \uC774\uB984\uC774 \uC0AC\uC6A9\uB418\uC5C8\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.COMPILER_ERROR_KEY,
        "\uCEF4\uD30C\uC77C\uB7EC \uC624\uB958:"},


        {ErrorMsg.COMPILER_WARNING_KEY,
        "\uCEF4\uD30C\uC77C\uB7EC \uACBD\uACE0:"},


        {ErrorMsg.RUNTIME_ERROR_KEY,
        "Translet \uC624\uB958:"},


        {ErrorMsg.INVALID_QNAME_ERR,
        "\uAC12\uC774 QName \uB610\uB294 \uACF5\uBC31\uC73C\uB85C \uAD6C\uBD84\uB41C QName \uBAA9\uB85D\uC774\uC5B4\uC57C \uD558\uB294 \uC18D\uC131\uC758 \uAC12\uC774 ''{0}''\uC785\uB2C8\uB2E4."},


        {ErrorMsg.INVALID_NCNAME_ERR,
        "\uAC12\uC774 NCName\uC774\uC5B4\uC57C \uD558\uB294 \uC18D\uC131\uC758 \uAC12\uC774 ''{0}''\uC785\uB2C8\uB2E4."},


        {ErrorMsg.INVALID_METHOD_IN_OUTPUT,
        "<xsl:output> \uC694\uC18C\uC5D0 \uB300\uD55C method \uC18D\uC131\uC758 \uAC12\uC774 ''{0}''\uC785\uB2C8\uB2E4. \uAC12\uC740 ''xml'', ''html'', ''text'' \uB610\uB294 qname-but-not-ncname \uC911 \uD558\uB098\uC5EC\uC57C \uD569\uB2C8\uB2E4."},

        {ErrorMsg.JAXP_GET_FEATURE_NULL_NAME,
        "\uAE30\uB2A5 \uC774\uB984\uC740 TransformerFactory.getFeature(\uBB38\uC790\uC5F4 \uC774\uB984)\uC5D0\uC11C \uB110\uC77C \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},

        {ErrorMsg.JAXP_SET_FEATURE_NULL_NAME,
        "\uAE30\uB2A5 \uC774\uB984\uC740 TransformerFactory.setFeature(\uBB38\uC790\uC5F4 \uC774\uB984, \uBD80\uC6B8 \uAC12)\uC5D0\uC11C \uB110\uC77C \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},

        {ErrorMsg.JAXP_UNSUPPORTED_FEATURE,
        "\uC774 TransformerFactory\uC5D0\uC11C ''{0}'' \uAE30\uB2A5\uC744 \uC124\uC815\uD560 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},

        {ErrorMsg.JAXP_SECUREPROCESSING_FEATURE,
        "FEATURE_SECURE_PROCESSING: \uBCF4\uC548 \uAD00\uB9AC\uC790\uAC00 \uC788\uC744 \uACBD\uC6B0 \uAE30\uB2A5\uC744 false\uB85C \uC124\uC815\uD560 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.OUTLINE_ERR_TRY_CATCH,
         "\uB0B4\uBD80 XSLTC \uC624\uB958: \uC0DD\uC131\uB41C \uBC14\uC774\uD2B8 \uCF54\uB4DC\uAC00 try-catch-finally \uBE14\uB85D\uC744 \uD3EC\uD568\uD558\uBBC0\uB85C outlined \uCC98\uB9AC\uD560 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."},


        {ErrorMsg.OUTLINE_ERR_UNBALANCED_MARKERS,
         "\uB0B4\uBD80 XSLTC \uC624\uB958: OutlineableChunkStart \uBC0F OutlineableChunkEnd \uD45C\uC2DC\uC790\uC758 \uC9DD\uC774 \uB9DE\uC544\uC57C \uD558\uACE0 \uC62C\uBC14\uB974\uAC8C \uC911\uCCA9\uB418\uC5B4\uC57C \uD569\uB2C8\uB2E4."},


        {ErrorMsg.OUTLINE_ERR_DELETED_TARGET,
         "\uB0B4\uBD80 XSLTC \uC624\uB958: outlined \uCC98\uB9AC\uB41C \uBC14\uC774\uD2B8 \uCF54\uB4DC \uBE14\uB85D\uC5D0 \uC18D\uD55C \uBA85\uB839\uC774 \uC5EC\uC804\uD788 \uC6D0\uB798 \uBA54\uC18C\uB4DC\uC5D0\uC11C \uCC38\uC870\uB429\uB2C8\uB2E4."
        },



        {ErrorMsg.OUTLINE_ERR_METHOD_TOO_BIG,
         "\uB0B4\uBD80 XSLTC \uC624\uB958: translet\uC758 \uBA54\uC18C\uB4DC\uAC00 Java Virtual Machine\uC758 \uBA54\uC18C\uB4DC \uAE38\uC774 \uC81C\uD55C\uC778 64KB\uB97C \uCD08\uACFC\uD569\uB2C8\uB2E4. \uB300\uAC1C \uC2A4\uD0C0\uC77C\uC2DC\uD2B8\uC758 \uD15C\uD50C\uB9AC\uD2B8\uAC00 \uB9E4\uC6B0 \uD06C\uAE30 \uB54C\uBB38\uC5D0 \uBC1C\uC0DD\uD569\uB2C8\uB2E4. \uB354 \uC791\uC740 \uD15C\uD50C\uB9AC\uD2B8\uB97C \uC0AC\uC6A9\uD558\uB3C4\uB85D \uC2A4\uD0C0\uC77C\uC2DC\uD2B8\uB97C \uC7AC\uAD6C\uC131\uD574 \uBCF4\uC2ED\uC2DC\uC624."
        },

         {ErrorMsg.DESERIALIZE_TRANSLET_ERR, "Java \uBCF4\uC548\uC774 \uC0AC\uC6A9\uC73C\uB85C \uC124\uC815\uB41C \uACBD\uC6B0 TemplatesImpl \uC9C1\uB82C\uD654 \uD574\uC81C\uC5D0 \uB300\uD55C \uC9C0\uC6D0\uC774 \uC0AC\uC6A9 \uC548\uD568\uC73C\uB85C \uC124\uC815\uB429\uB2C8\uB2E4. jdk.xml.enableTemplatesImplDeserialization \uC2DC\uC2A4\uD15C \uC18D\uC131\uC744 true\uB85C \uC124\uC815\uD558\uBA74 \uC774\uB97C \uBB34\uD6A8\uD654\uD560 \uC218 \uC788\uC2B5\uB2C8\uB2E4."}

    };

    }
}
