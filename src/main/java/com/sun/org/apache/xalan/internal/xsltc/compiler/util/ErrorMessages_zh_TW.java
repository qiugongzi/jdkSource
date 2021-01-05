


package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import java.util.ListResourceBundle;


public class ErrorMessages_zh_TW extends ListResourceBundle {



    public Object[][] getContents()
    {
      return new Object[][] {
        {ErrorMsg.MULTIPLE_STYLESHEET_ERR,
        "\u76F8\u540C\u6A94\u6848\u4E2D\u5B9A\u7FA9\u4E86\u8D85\u904E\u4E00\u500B\u6A23\u5F0F\u8868\u3002"},


        {ErrorMsg.TEMPLATE_REDEF_ERR,
        "\u6A23\u677F ''{0}'' \u5DF2\u7D93\u5B9A\u7FA9\u5728\u6B64\u6A23\u5F0F\u8868\u4E2D\u3002"},



        {ErrorMsg.TEMPLATE_UNDEF_ERR,
        "\u6A23\u677F ''{0}'' \u672A\u5B9A\u7FA9\u5728\u6B64\u6A23\u5F0F\u8868\u4E2D\u3002"},


        {ErrorMsg.VARIABLE_REDEF_ERR,
        "\u8B8A\u6578 ''{0}'' \u5728\u76F8\u540C\u7BC4\u570D\u4E2D\u5B9A\u7FA9\u591A\u6B21\u3002"},


        {ErrorMsg.VARIABLE_UNDEF_ERR,
        "\u8B8A\u6578\u6216\u53C3\u6578 ''{0}'' \u672A\u5B9A\u7FA9\u3002"},


        {ErrorMsg.CLASS_NOT_FOUND_ERR,
        "\u627E\u4E0D\u5230\u985E\u5225 ''{0}''\u3002"},


        {ErrorMsg.METHOD_NOT_FOUND_ERR,
        "\u627E\u4E0D\u5230\u5916\u90E8\u65B9\u6CD5 ''{0}'' (\u5FC5\u9808\u70BA\u516C\u7528)\u3002"},


        {ErrorMsg.ARGUMENT_CONVERSION_ERR,
        "\u7121\u6CD5\u8F49\u63DB\u547C\u53EB\u65B9\u6CD5 ''{0}'' \u4E2D\u7684\u5F15\u6578/\u50B3\u56DE\u985E\u578B"},


        {ErrorMsg.FILE_NOT_FOUND_ERR,
        "\u627E\u4E0D\u5230\u6A94\u6848\u6216 URI ''{0}''\u3002"},


        {ErrorMsg.INVALID_URI_ERR,
        "\u7121\u6548\u7684 URI ''{0}''\u3002"},


        {ErrorMsg.FILE_ACCESS_ERR,
        "\u7121\u6CD5\u958B\u555F\u6A94\u6848\u6216 URI ''{0}''\u3002"},


        {ErrorMsg.MISSING_ROOT_ERR,
        "\u9810\u671F <xsl:stylesheet> \u6216 <xsl:transform> \u5143\u7D20\u3002"},


        {ErrorMsg.NAMESPACE_UNDEF_ERR,
        "\u672A\u5BA3\u544A\u547D\u540D\u7A7A\u9593\u524D\u7F6E\u78BC ''{0}''\u3002"},


        {ErrorMsg.FUNCTION_RESOLVE_ERR,
        "\u7121\u6CD5\u89E3\u6790\u51FD\u6578 ''{0}'' \u7684\u547C\u53EB\u3002"},


        {ErrorMsg.NEED_LITERAL_ERR,
        "''{0}'' \u7684\u5F15\u6578\u5FC5\u9808\u662F\u6587\u5B57\u5B57\u4E32\u3002"},


        {ErrorMsg.XPATH_PARSER_ERR,
        "\u5256\u6790 XPath \u8868\u793A\u5F0F ''{0}'' \u6642\u767C\u751F\u932F\u8AA4\u3002"},


        {ErrorMsg.REQUIRED_ATTR_ERR,
        "\u907A\u6F0F\u5FC5\u8981\u7684\u5C6C\u6027 ''{0}''\u3002"},


        {ErrorMsg.ILLEGAL_CHAR_ERR,
        "XPath \u8868\u793A\u5F0F\u4E2D\u7121\u6548\u7684\u5B57\u5143 ''{0}''\u3002"},


        {ErrorMsg.ILLEGAL_PI_ERR,
        "\u8655\u7406\u6307\u793A\u7684\u7121\u6548\u540D\u7A31 ''{0}''\u3002"},


        {ErrorMsg.STRAY_ATTRIBUTE_ERR,
        "\u5C6C\u6027 ''{0}'' \u5728\u5143\u7D20\u4E4B\u5916\u3002"},


        {ErrorMsg.ILLEGAL_ATTRIBUTE_ERR,
        "\u7121\u6548\u7684\u5C6C\u6027 ''{0}''\u3002"},


        {ErrorMsg.CIRCULAR_INCLUDE_ERR,
        "\u5FAA\u74B0\u532F\u5165/\u5305\u542B\u3002\u5DF2\u7D93\u8F09\u5165\u6A23\u5F0F\u8868 ''{0}''\u3002"},


        {ErrorMsg.RESULT_TREE_SORT_ERR,
        "\u7121\u6CD5\u6392\u5E8F Result-tree \u7247\u6BB5 (\u5FFD\u7565 <xsl:sort> \u5143\u7D20)\u3002\u5EFA\u7ACB\u7D50\u679C\u6A39\u72C0\u7D50\u69CB\u6642\uFF0C\u5FC5\u9808\u6392\u5E8F\u7BC0\u9EDE\u3002"},


        {ErrorMsg.SYMBOLS_REDEF_ERR,
        "\u5DF2\u7D93\u5B9A\u7FA9\u5341\u9032\u4F4D\u683C\u5F0F ''{0}''\u3002"},


        {ErrorMsg.XSL_VERSION_ERR,
        "XSLTC \u4E0D\u652F\u63F4 XSL \u7248\u672C ''{0}''\u3002"},


        {ErrorMsg.CIRCULAR_VARIABLE_ERR,
        "\u5728 ''{0}'' \u4E2D\u6709\u5FAA\u74B0\u8B8A\u6578/\u53C3\u6578\u53C3\u7167\u3002"},


        {ErrorMsg.ILLEGAL_BINARY_OP_ERR,
        "\u4E8C\u9032\u4F4D\u8868\u793A\u5F0F\u4E0D\u660E\u7684\u904B\u7B97\u5B50\u3002"},


        {ErrorMsg.ILLEGAL_ARG_ERR,
        "\u51FD\u6578\u547C\u53EB\u7121\u6548\u7684\u5F15\u6578\u3002"},


        {ErrorMsg.DOCUMENT_ARG_ERR,
        "document() \u51FD\u6578\u7684\u7B2C\u4E8C\u500B\u5F15\u6578\u5FC5\u9808\u662F node-set\u3002"},


        {ErrorMsg.MISSING_WHEN_ERR,
        "\u5728 <xsl:choose> \u4E2D\u81F3\u5C11\u9700\u8981\u4E00\u500B <xsl:when> \u5143\u7D20\u3002"},


        {ErrorMsg.MULTIPLE_OTHERWISE_ERR,
        "\u5728 <xsl:choose> \u4E2D\u53EA\u5141\u8A31\u4E00\u500B <xsl:otherwise> \u5143\u7D20\u3002"},


        {ErrorMsg.STRAY_OTHERWISE_ERR,
        "<xsl:otherwise> \u53EA\u80FD\u5728 <xsl:choose> \u5167\u4F7F\u7528\u3002"},


        {ErrorMsg.STRAY_WHEN_ERR,
        "<xsl:when> \u53EA\u80FD\u5728 <xsl:choose> \u5167\u4F7F\u7528\u3002"},


        {ErrorMsg.WHEN_ELEMENT_ERR,
        "\u5728 <xsl:choose> \u4E2D\u53EA\u5141\u8A31 <xsl:when> \u8207 <xsl:otherwise> \u5143\u7D20\u3002"},


        {ErrorMsg.UNNAMED_ATTRIBSET_ERR,
        "<xsl:attribute-set> \u907A\u6F0F 'name' \u5C6C\u6027\u3002"},


        {ErrorMsg.ILLEGAL_CHILD_ERR,
        "\u7121\u6548\u7684\u5B50\u9805\u5143\u7D20\u3002"},


        {ErrorMsg.ILLEGAL_ELEM_NAME_ERR,
        "\u60A8\u7121\u6CD5\u547C\u53EB\u5143\u7D20 ''{0}''"},


        {ErrorMsg.ILLEGAL_ATTR_NAME_ERR,
        "\u60A8\u7121\u6CD5\u547C\u53EB\u5C6C\u6027 ''{0}''"},


        {ErrorMsg.ILLEGAL_TEXT_NODE_ERR,
        "\u6700\u4E0A\u5C64 <xsl:stylesheet> \u5143\u7D20\u4E4B\u5916\u7684\u6587\u5B57\u8CC7\u6599\u3002"},


        {ErrorMsg.SAX_PARSER_CONFIG_ERR,
        "\u672A\u6B63\u78BA\u8A2D\u5B9A JAXP \u5256\u6790\u5668"},


        {ErrorMsg.INTERNAL_ERR,
        "\u7121\u6CD5\u5FA9\u539F\u7684 XSLTC-internal \u932F\u8AA4: ''{0}''"},


        {ErrorMsg.UNSUPPORTED_XSL_ERR,
        "\u4E0D\u652F\u63F4\u7684 XSL \u5143\u7D20 ''{0}''\u3002"},


        {ErrorMsg.UNSUPPORTED_EXT_ERR,
        "\u7121\u6CD5\u8FA8\u8B58\u7684 XSLTC \u64F4\u5145\u5957\u4EF6 ''{0}''\u3002"},


        {ErrorMsg.MISSING_XSLT_URI_ERR,
        "\u8F38\u5165\u6587\u4EF6\u4E0D\u662F\u6A23\u5F0F\u8868 (\u6839\u5143\u7D20\u4E2D\u672A\u5BA3\u544A XSL \u547D\u540D\u7A7A\u9593)\u3002"},


        {ErrorMsg.MISSING_XSLT_TARGET_ERR,
        "\u627E\u4E0D\u5230\u6A23\u5F0F\u8868\u76EE\u6A19 ''{0}''\u3002"},


        {ErrorMsg.ACCESSING_XSLT_TARGET_ERR,
        "\u7121\u6CD5\u8B80\u53D6\u6A23\u5F0F\u8868\u76EE\u6A19 ''{0}''\uFF0C\u56E0\u70BA accessExternalStylesheet \u5C6C\u6027\u8A2D\u5B9A\u7684\u9650\u5236\uFF0C\u6240\u4EE5\u4E0D\u5141\u8A31 ''{1}'' \u5B58\u53D6\u3002"},


        {ErrorMsg.NOT_IMPLEMENTED_ERR,
        "\u672A\u5BE6\u884C: ''{0}''\u3002"},


        {ErrorMsg.NOT_STYLESHEET_ERR,
        "\u8F38\u5165\u6587\u4EF6\u672A\u5305\u542B XSL \u6A23\u5F0F\u8868\u3002"},


        {ErrorMsg.ELEMENT_PARSE_ERR,
        "\u7121\u6CD5\u5256\u6790\u5143\u7D20 ''{0}''"},


        {ErrorMsg.KEY_USE_ATTR_ERR,
        "<key> \u7684\u4F7F\u7528\u5C6C\u6027\u5FC5\u9808\u662F\u7BC0\u9EDE\u3001node-set\u3001\u5B57\u4E32\u6216\u6578\u5B57\u3002"},


        {ErrorMsg.OUTPUT_VERSION_ERR,
        "\u8F38\u51FA XML \u6587\u4EF6\u7248\u672C\u61C9\u70BA 1.0"},


        {ErrorMsg.ILLEGAL_RELAT_OP_ERR,
        "\u95DC\u806F\u8868\u793A\u5F0F\u7684\u904B\u7B97\u5B50\u4E0D\u660E"},


        {ErrorMsg.ATTRIBSET_UNDEF_ERR,
        "\u5617\u8A66\u4F7F\u7528\u4E0D\u5B58\u5728\u7684\u5C6C\u6027\u96C6 ''{0}''\u3002"},


        {ErrorMsg.ATTR_VAL_TEMPLATE_ERR,
        "\u7121\u6CD5\u5256\u6790\u5C6C\u6027\u503C\u6A23\u677F ''{0}''\u3002"},


        {ErrorMsg.UNKNOWN_SIG_TYPE_ERR,
        "\u985E\u5225 ''{0}'' \u7C3D\u7AE0\u6709\u4E0D\u660E\u7684 data-type\u3002"},


        {ErrorMsg.DATA_CONVERSION_ERR,
        "\u7121\u6CD5\u8F49\u63DB data-type ''{0}'' \u70BA ''{1}''\u3002"},


        {ErrorMsg.NO_TRANSLET_CLASS_ERR,
        "\u6B64\u6A23\u677F\u672A\u5305\u542B\u6709\u6548\u7684 translet \u985E\u5225\u5B9A\u7FA9\u3002"},


        {ErrorMsg.NO_MAIN_TRANSLET_ERR,
        "\u6B64\u6A23\u677F\u672A\u5305\u542B\u540D\u7A31\u70BA ''{0}'' \u7684\u985E\u5225\u3002"},


        {ErrorMsg.TRANSLET_CLASS_ERR,
        "\u7121\u6CD5\u8F09\u5165 translet \u985E\u5225 ''{0}''\u3002"},

        {ErrorMsg.TRANSLET_OBJECT_ERR,
        "\u5DF2\u8F09\u5165 translet \u985E\u5225\uFF0C\u4F46\u7121\u6CD5\u5EFA\u7ACB translet \u57F7\u884C\u8655\u7406\u3002"},


        {ErrorMsg.ERROR_LISTENER_NULL_ERR,
        "\u5617\u8A66\u5C07 ''{0}'' \u7684 ErrorListener \u8A2D\u5B9A\u70BA\u7A7A\u503C"},


        {ErrorMsg.JAXP_UNKNOWN_SOURCE_ERR,
        "XSLTC \u50C5\u652F\u63F4 StreamSource\u3001SAXSource \u8207 DOMSource"},


        {ErrorMsg.JAXP_NO_SOURCE_ERR,
        "\u50B3\u9001\u81F3 ''{0}'' \u7684\u4F86\u6E90\u7269\u4EF6\u6C92\u6709\u5167\u5BB9\u3002"},


        {ErrorMsg.JAXP_COMPILE_ERR,
        "\u7121\u6CD5\u7DE8\u8B6F\u6A23\u5F0F\u8868"},


        {ErrorMsg.JAXP_INVALID_ATTR_ERR,
        "TransformerFactory \u7121\u6CD5\u8FA8\u8B58\u5C6C\u6027 ''{0}''\u3002"},

        {ErrorMsg.JAXP_INVALID_ATTR_VALUE_ERR,
        "\u70BA ''{0}'' \u5C6C\u6027\u6307\u5B9A\u7684\u503C\u4E0D\u6B63\u78BA\u3002"},


        {ErrorMsg.JAXP_SET_RESULT_ERR,
        "\u547C\u53EB startDocument() \u4E4B\u524D\uFF0C\u5FC5\u9808\u5148\u547C\u53EB setResult()\u3002"},


        {ErrorMsg.JAXP_NO_TRANSLET_ERR,
        "\u8F49\u63DB\u5668\u6C92\u6709\u5C01\u88DD\u7684 translet \u7269\u4EF6\u3002"},


        {ErrorMsg.JAXP_NO_HANDLER_ERR,
        "\u8F49\u63DB\u7D50\u679C\u6C92\u6709\u5B9A\u7FA9\u7684\u8F38\u51FA\u8655\u7406\u7A0B\u5F0F\u3002"},


        {ErrorMsg.JAXP_NO_RESULT_ERR,
        "\u50B3\u9001\u81F3 ''{0}'' \u7684\u7D50\u679C\u7269\u4EF6\u7121\u6548\u3002"},


        {ErrorMsg.JAXP_UNKNOWN_PROP_ERR,
        "\u5617\u8A66\u5B58\u53D6\u7121\u6548\u7684\u8F49\u63DB\u5668\u5C6C\u6027 ''{0}''\u3002"},


        {ErrorMsg.SAX2DOM_ADAPTER_ERR,
        "\u7121\u6CD5\u5EFA\u7ACB SAX2DOM \u8F49\u63A5\u5668: ''{0}''\u3002"},


        {ErrorMsg.XSLTC_SOURCE_ERR,
        "\u672A\u8A2D\u5B9A systemId \u800C\u547C\u53EB XSLTCSource.build()\u3002"},

        { ErrorMsg.ER_RESULT_NULL,
            "\u7D50\u679C\u4E0D\u61C9\u70BA\u7A7A\u503C"},


        {ErrorMsg.JAXP_INVALID_SET_PARAM_VALUE,
        "\u53C3\u6578 {0} \u7684\u503C\u5FC5\u9808\u662F\u6709\u6548\u7684 Java \u7269\u4EF6"},


        {ErrorMsg.COMPILE_STDIN_ERR,
        "-i \u9078\u9805\u5FC5\u9808\u8207 -o \u9078\u9805\u4E00\u8D77\u4F7F\u7528\u3002"},



        {ErrorMsg.COMPILE_USAGE_STR,
        "\u6982\u8981\n   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Compile [-o <output>]\n      [-d <directory>] [-j <jarfile>] [-p <package>]\n      [-n] [-x] [-u] [-v] [-h] { <stylesheet> | -i }\n\n\u9078\u9805\n   -o <output>    \u6307\u6D3E\u540D\u7A31 <output> \u81F3\u7522\u751F\u7684\n                  translet\u3002\u6839\u64DA\u9810\u8A2D\uFF0Ctranslet \u540D\u7A31\n                  \u884D\u751F\u81EA <stylesheet> \u540D\u7A31\u3002\u82E5\u7DE8\u8B6F\n                  \u591A\u500B\u6A23\u5F0F\u8868\uFF0C\u5C07\u5FFD\u7565\u6B64\u9078\u9805\u3002\n   -d <directory> \u6307\u5B9A translet \u7684\u76EE\u7684\u5730\u76EE\u9304\n   -j <jarfile>   \u5C01\u88DD translet \u985E\u5225\u6210\u70BA jar \u6A94\u6848\uFF0C\n                  \u540D\u7A31\u6307\u5B9A\u70BA <jarfile>\n   -p <package>   \u6307\u5B9A\u6240\u6709\u7522\u751F\u7684 translet \u985E\u5225\u7684\u5957\u88DD\u7A0B\u5F0F\n                  \u540D\u7A31\u524D\u7F6E\u78BC\u3002\n   -n             \u555F\u7528\u6A23\u677F\u5167\u5D4C (\u9810\u8A2D\u884C\u70BA\u4E00\u822C\u800C\u8A00\n                  \u8F03\u4F73)\u3002\n   -x             \u958B\u555F\u984D\u5916\u7684\u9664\u932F\u8A0A\u606F\u8F38\u51FA\n   -u             \u89E3\u8B6F <stylesheet> \u5F15\u6578\u70BA URL\n   -i             \u5F37\u5236\u7DE8\u8B6F\u5668\u5F9E stdin \u8B80\u53D6\u6A23\u5F0F\u8868\n   -v             \u5217\u5370\u7DE8\u8B6F\u5668\u7248\u672C\n   -h             \u5217\u5370\u6B64\u7528\u6CD5\u6558\u8FF0\n"},


        {ErrorMsg.TRANSFORM_USAGE_STR,
        "\u6982\u8981 \n   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Transform [-j <jarfile>]\n      [-x] [-n <iterations>] {-u <document_url> | <document>}\n      <class> [<param1>=<value1> ...]\n\n   \u4F7F\u7528 translet <class> \u8F49\u63DB\u6307\u5B9A\u70BA <document> \n   \u7684 XML \u6587\u4EF6\u3002translet <class> \u4F4D\u65BC\n   \u4F7F\u7528\u8005\u7684\u985E\u5225\u8DEF\u5F91\uFF0C\u6216\u662F\u5728\u9078\u64C7\u6027\u6307\u5B9A\u7684 <jarfile> \u4E2D\u3002\n\u9078\u9805\n   -j <jarfile>    \u6307\u5B9A\u8F09\u5165 translet \u7684\u4F86\u6E90 jarfile\n   -x              \u958B\u555F\u984D\u5916\u7684\u9664\u932F\u8A0A\u606F\u8F38\u51FA\n   -n <iterations> \u57F7\u884C\u8F49\u63DB <iterations> \u6B21\u6578\u8207\n                   \u986F\u793A\u5206\u6790\u8CC7\u8A0A\n   -u <document_url> \u6307\u5B9A XML \u8F38\u5165\u6587\u4EF6\u70BA URL\n"},




        {ErrorMsg.STRAY_SORT_ERR,
        "<xsl:sort> \u53EA\u80FD\u5728 <xsl:for-each> \u6216 <xsl:apply-templates> \u4E2D\u4F7F\u7528\u3002"},


        {ErrorMsg.UNSUPPORTED_ENCODING,
        "\u6B64 JVM \u4E0D\u652F\u63F4\u8F38\u51FA\u7DE8\u78BC ''{0}''\u3002"},


        {ErrorMsg.SYNTAX_ERR,
        "''{0}'' \u4E2D\u7684\u8A9E\u6CD5\u932F\u8AA4\u3002"},


        {ErrorMsg.CONSTRUCTOR_NOT_FOUND,
        "\u627E\u4E0D\u5230\u5916\u90E8\u5EFA\u69CB\u5B50 ''{0}''\u3002"},


        {ErrorMsg.NO_JAVA_FUNCT_THIS_REF,
        "\u975E\u975C\u614B Java \u51FD\u6578 ''{0}'' \u7684\u7B2C\u4E00\u500B\u5F15\u6578\u4E0D\u662F\u6709\u6548\u7684\u7269\u4EF6\u53C3\u7167\u3002"},


        {ErrorMsg.TYPE_CHECK_ERR,
        "\u6AA2\u67E5\u8868\u793A\u5F0F ''{0}'' \u7684\u985E\u578B\u6642\u767C\u751F\u932F\u8AA4\u3002"},


        {ErrorMsg.TYPE_CHECK_UNK_LOC_ERR,
        "\u6AA2\u67E5\u4F4D\u65BC\u4E0D\u660E\u4F4D\u7F6E\u8868\u793A\u5F0F\u7684\u985E\u578B\u6642\u767C\u751F\u932F\u8AA4\u3002"},


        {ErrorMsg.ILLEGAL_CMDLINE_OPTION_ERR,
        "\u547D\u4EE4\u884C\u9078\u9805 ''{0}'' \u7121\u6548\u3002"},


        {ErrorMsg.CMDLINE_OPT_MISSING_ARG_ERR,
        "\u547D\u4EE4\u884C\u9078\u9805 ''{0}'' \u907A\u6F0F\u5FC5\u8981\u7684\u5F15\u6578\u3002"},


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
        "\u4F7F\u7528 translet ''{0}'' \u8F49\u63DB"},


        {ErrorMsg.TRANSFORM_WITH_JAR_STR,
        "\u4F7F\u7528\u4F86\u81EA jar \u6A94\u6848 ''{1}'' \u7684 translet ''{0}'' \u8F49\u63DB"},


        {ErrorMsg.COULD_NOT_CREATE_TRANS_FACT,
        "\u7121\u6CD5\u5EFA\u7ACB TransformerFactory \u985E\u5225 ''{0}'' \u7684\u57F7\u884C\u8655\u7406\u3002"},


        {ErrorMsg.TRANSLET_NAME_JAVA_CONFLICT,
         "\u540D\u7A31 ''{0}'' \u7121\u6CD5\u4F5C\u70BA translet \u985E\u5225\u7684\u540D\u7A31\uFF0C\u56E0\u70BA\u5B83\u5305\u542B Java \u985E\u5225\u540D\u7A31\u4E0D\u5141\u8A31\u7684\u5B57\u5143\u3002\u8ACB\u6539\u7528\u540D\u7A31 ''{1}''\u3002"},


        {ErrorMsg.COMPILER_ERROR_KEY,
        "\u7DE8\u8B6F\u5668\u932F\u8AA4:"},


        {ErrorMsg.COMPILER_WARNING_KEY,
        "\u7DE8\u8B6F\u5668\u8B66\u544A:"},


        {ErrorMsg.RUNTIME_ERROR_KEY,
        "Translet \u932F\u8AA4:"},


        {ErrorMsg.INVALID_QNAME_ERR,
        "\u503C\u5FC5\u9808\u70BA QName \u6216\u4F7F\u7528\u7A7A\u683C\u52A0\u4EE5\u5340\u9694\u7684 QNames \u6E05\u55AE\u7684\u5C6C\u6027\uFF0C\u5177\u6709\u503C ''{0}''"},


        {ErrorMsg.INVALID_NCNAME_ERR,
        "\u503C\u5FC5\u9808\u70BA NCName \u7684\u5C6C\u6027\uFF0C\u5177\u6709\u503C ''{0}''"},


        {ErrorMsg.INVALID_METHOD_IN_OUTPUT,
        "<xsl:output> \u5143\u7D20\u7684\u65B9\u6CD5\u5C6C\u6027\u5177\u6709\u503C ''{0}''\u3002\u6B64\u503C\u5FC5\u9808\u662F ''xml''\u3001''html''\u3001''text'' \u6216 qname-but-not-ncname \u5176\u4E2D\u4E4B\u4E00"},

        {ErrorMsg.JAXP_GET_FEATURE_NULL_NAME,
        "TransformerFactory.getFeature(\u5B57\u4E32\u540D\u7A31) \u4E2D\u7684\u529F\u80FD\u540D\u7A31\u4E0D\u53EF\u70BA\u7A7A\u503C\u3002"},

        {ErrorMsg.JAXP_SET_FEATURE_NULL_NAME,
        "TransformerFactory.setFeature(\u5B57\u4E32\u540D\u7A31, \u5E03\u6797\u503C) \u4E2D\u7684\u529F\u80FD\u540D\u7A31\u4E0D\u53EF\u70BA\u7A7A\u503C\u3002"},

        {ErrorMsg.JAXP_UNSUPPORTED_FEATURE,
        "\u7121\u6CD5\u5728\u6B64 TransformerFactory \u4E0A\u8A2D\u5B9A\u529F\u80FD ''{0}''\u3002"},

        {ErrorMsg.JAXP_SECUREPROCESSING_FEATURE,
        "FEATURE_SECURE_PROCESSING: \u5B89\u5168\u7BA1\u7406\u7A0B\u5F0F\u5B58\u5728\u6642\uFF0C\u7121\u6CD5\u5C07\u529F\u80FD\u8A2D\u70BA\u507D\u3002"},


        {ErrorMsg.OUTLINE_ERR_TRY_CATCH,
         "\u5167\u90E8 XSLTC \u932F\u8AA4:  \u7522\u751F\u7684\u4F4D\u5143\u7D44\u78BC\u5305\u542B try-catch-finally \u5340\u584A\uFF0C\u7121\u6CD5\u52A0\u4EE5 outlined."},


        {ErrorMsg.OUTLINE_ERR_UNBALANCED_MARKERS,
         "\u5167\u90E8 XSLTC \u932F\u8AA4:  OutlineableChunkStart \u548C OutlineableChunkEnd \u6A19\u8A18\u5FC5\u9808\u6210\u5C0D\u51FA\u73FE\uFF0C\u4E26\u4F7F\u7528\u6B63\u78BA\u7684\u5DE2\u72C0\u7D50\u69CB\u3002"},


        {ErrorMsg.OUTLINE_ERR_DELETED_TARGET,
         "\u5167\u90E8 XSLTC \u932F\u8AA4:  \u539F\u59CB\u65B9\u6CD5\u4E2D\u4ECD\u7136\u53C3\u7167\u5C6C\u65BC outlined \u4F4D\u5143\u7D44\u78BC\u5340\u584A\u4E00\u90E8\u5206\u7684\u6307\u793A\u3002"
        },



        {ErrorMsg.OUTLINE_ERR_METHOD_TOO_BIG,
         "\u5167\u90E8 XSLTC \u932F\u8AA4:  translet \u4E2D\u7684\u65B9\u6CD5\u8D85\u904E Java \u865B\u64EC\u6A5F\u5668\u5C0D\u65BC\u65B9\u6CD5\u9577\u5EA6 64 KB \u7684\u9650\u5236\u3002\u9019\u901A\u5E38\u662F\u56E0\u70BA\u6A23\u5F0F\u8868\u4E2D\u6709\u975E\u5E38\u5927\u7684\u6A23\u677F\u3002\u8ACB\u5617\u8A66\u91CD\u65B0\u7D44\u7E54\u60A8\u7684\u6A23\u5F0F\u8868\u4EE5\u4F7F\u7528\u8F03\u5C0F\u7684\u6A23\u677F\u3002"
        },

         {ErrorMsg.DESERIALIZE_TRANSLET_ERR, "\u555F\u7528 Java \u5B89\u5168\u6642\uFF0C\u6703\u505C\u7528\u9084\u539F\u5E8F\u5217\u5316 TemplatesImpl \u7684\u652F\u63F4\u3002\u5C07 jdk.xml.enableTemplatesImplDeserialization \u7CFB\u7D71\u5C6C\u6027\u8A2D\u70BA\u771F\u5373\u53EF\u8986\u5BEB\u6B64\u8A2D\u5B9A\u3002"}

    };

    }
}
