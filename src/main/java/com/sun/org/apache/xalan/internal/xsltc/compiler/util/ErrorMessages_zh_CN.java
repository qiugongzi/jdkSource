


package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import java.util.ListResourceBundle;


public class ErrorMessages_zh_CN extends ListResourceBundle {



    public Object[][] getContents()
    {
      return new Object[][] {
        {ErrorMsg.MULTIPLE_STYLESHEET_ERR,
        "\u540C\u4E00\u6587\u4EF6\u4E2D\u5B9A\u4E49\u4E86\u591A\u4E2A\u6837\u5F0F\u8868\u3002"},


        {ErrorMsg.TEMPLATE_REDEF_ERR,
        "\u5DF2\u5728\u6B64\u6837\u5F0F\u8868\u4E2D\u5B9A\u4E49\u6A21\u677F ''{0}''\u3002"},



        {ErrorMsg.TEMPLATE_UNDEF_ERR,
        "\u672A\u5728\u6B64\u6837\u5F0F\u8868\u4E2D\u5B9A\u4E49\u6A21\u677F ''{0}''\u3002"},


        {ErrorMsg.VARIABLE_REDEF_ERR,
        "\u540C\u4E00\u4F5C\u7528\u57DF\u4E2D\u591A\u6B21\u5B9A\u4E49\u4E86\u53D8\u91CF ''{0}''\u3002"},


        {ErrorMsg.VARIABLE_UNDEF_ERR,
        "\u672A\u5B9A\u4E49\u53D8\u91CF\u6216\u53C2\u6570 ''{0}''\u3002"},


        {ErrorMsg.CLASS_NOT_FOUND_ERR,
        "\u627E\u4E0D\u5230\u7C7B ''{0}''\u3002"},


        {ErrorMsg.METHOD_NOT_FOUND_ERR,
        "\u627E\u4E0D\u5230\u5916\u90E8\u65B9\u6CD5 ''{0}'' (\u5FC5\u987B\u4E3A public)\u3002"},


        {ErrorMsg.ARGUMENT_CONVERSION_ERR,
        "\u65E0\u6CD5\u5728\u8C03\u7528\u65B9\u6CD5 ''{0}'' \u65F6\u8F6C\u6362\u53C2\u6570/\u8FD4\u56DE\u7C7B\u578B"},


        {ErrorMsg.FILE_NOT_FOUND_ERR,
        "\u627E\u4E0D\u5230\u6587\u4EF6\u6216 URI ''{0}''\u3002"},


        {ErrorMsg.INVALID_URI_ERR,
        "URI ''{0}'' \u65E0\u6548\u3002"},


        {ErrorMsg.FILE_ACCESS_ERR,
        "\u65E0\u6CD5\u6253\u5F00\u6587\u4EF6\u6216 URI ''{0}''\u3002"},


        {ErrorMsg.MISSING_ROOT_ERR,
        "\u9700\u8981 <xsl:stylesheet> \u6216 <xsl:transform> \u5143\u7D20\u3002"},


        {ErrorMsg.NAMESPACE_UNDEF_ERR,
        "\u672A\u58F0\u660E\u540D\u79F0\u7A7A\u95F4\u524D\u7F00 ''{0}''\u3002"},


        {ErrorMsg.FUNCTION_RESOLVE_ERR,
        "\u65E0\u6CD5\u89E3\u6790\u5BF9\u51FD\u6570 ''{0}'' \u7684\u8C03\u7528\u3002"},


        {ErrorMsg.NEED_LITERAL_ERR,
        "''{0}'' \u7684\u53C2\u6570\u5FC5\u987B\u662F\u6587\u5B57\u5B57\u7B26\u4E32\u3002"},


        {ErrorMsg.XPATH_PARSER_ERR,
        "\u89E3\u6790 XPath \u8868\u8FBE\u5F0F ''{0}'' \u65F6\u51FA\u9519\u3002"},


        {ErrorMsg.REQUIRED_ATTR_ERR,
        "\u7F3A\u5C11\u6240\u9700\u5C5E\u6027 ''{0}''\u3002"},


        {ErrorMsg.ILLEGAL_CHAR_ERR,
        "XPath \u8868\u8FBE\u5F0F\u4E2D\u7684\u5B57\u7B26 ''{0}'' \u975E\u6CD5\u3002"},


        {ErrorMsg.ILLEGAL_PI_ERR,
        "processing instruction \u7684\u540D\u79F0 ''{0}'' \u975E\u6CD5\u3002"},


        {ErrorMsg.STRAY_ATTRIBUTE_ERR,
        "\u5C5E\u6027 ''{0}'' \u5728\u5143\u7D20\u5916\u90E8\u3002"},


        {ErrorMsg.ILLEGAL_ATTRIBUTE_ERR,
        "\u5C5E\u6027 ''{0}'' \u975E\u6CD5\u3002"},


        {ErrorMsg.CIRCULAR_INCLUDE_ERR,
        "\u5FAA\u73AF import/include\u3002\u5DF2\u52A0\u8F7D\u6837\u5F0F\u8868 ''{0}''\u3002"},


        {ErrorMsg.RESULT_TREE_SORT_ERR,
        "\u65E0\u6CD5\u5BF9\u7ED3\u679C\u6811\u7247\u6BB5\u6392\u5E8F (\u5FFD\u7565 <xsl:sort> \u5143\u7D20)\u3002\u5FC5\u987B\u5728\u521B\u5EFA\u7ED3\u679C\u6811\u65F6\u5BF9\u8282\u70B9\u8FDB\u884C\u6392\u5E8F\u3002"},


        {ErrorMsg.SYMBOLS_REDEF_ERR,
        "\u5DF2\u5B9A\u4E49\u5341\u8FDB\u5236\u683C\u5F0F ''{0}''\u3002"},


        {ErrorMsg.XSL_VERSION_ERR,
        "XSLTC \u4E0D\u652F\u6301 XSL \u7248\u672C ''{0}''\u3002"},


        {ErrorMsg.CIRCULAR_VARIABLE_ERR,
        "''{0}'' \u4E2D\u5B58\u5728\u5FAA\u73AF\u53D8\u91CF/\u53C2\u6570\u5F15\u7528\u3002"},


        {ErrorMsg.ILLEGAL_BINARY_OP_ERR,
        "\u4E8C\u8FDB\u5236\u8868\u8FBE\u5F0F\u7684\u8FD0\u7B97\u7B26\u672A\u77E5\u3002"},


        {ErrorMsg.ILLEGAL_ARG_ERR,
        "\u51FD\u6570\u8C03\u7528\u7684\u53C2\u6570\u975E\u6CD5\u3002"},


        {ErrorMsg.DOCUMENT_ARG_ERR,
        "document() \u51FD\u6570\u7684\u7B2C\u4E8C\u4E2A\u53C2\u6570\u5FC5\u987B\u662F\u8282\u70B9\u96C6\u3002"},


        {ErrorMsg.MISSING_WHEN_ERR,
        "<xsl:choose> \u4E2D\u81F3\u5C11\u9700\u8981\u4E00\u4E2A <xsl:when> \u5143\u7D20\u3002"},


        {ErrorMsg.MULTIPLE_OTHERWISE_ERR,
        "<xsl:choose> \u4E2D\u4EC5\u5141\u8BB8\u4F7F\u7528\u4E00\u4E2A <xsl:otherwise> \u5143\u7D20\u3002"},


        {ErrorMsg.STRAY_OTHERWISE_ERR,
        "<xsl:otherwise> \u53EA\u80FD\u5728 <xsl:choose> \u4E2D\u4F7F\u7528\u3002"},


        {ErrorMsg.STRAY_WHEN_ERR,
        "<xsl:when> \u53EA\u80FD\u5728 <xsl:choose> \u4E2D\u4F7F\u7528\u3002"},


        {ErrorMsg.WHEN_ELEMENT_ERR,
        "<xsl:choose> \u4E2D\u4EC5\u5141\u8BB8\u4F7F\u7528 <xsl:when> \u548C <xsl:otherwise> \u5143\u7D20\u3002"},


        {ErrorMsg.UNNAMED_ATTRIBSET_ERR,
        "<xsl:attribute-set> \u7F3A\u5C11 'name' \u5C5E\u6027\u3002"},


        {ErrorMsg.ILLEGAL_CHILD_ERR,
        "\u5B50\u5143\u7D20\u975E\u6CD5\u3002"},


        {ErrorMsg.ILLEGAL_ELEM_NAME_ERR,
        "\u65E0\u6CD5\u8C03\u7528\u5143\u7D20 ''{0}''"},


        {ErrorMsg.ILLEGAL_ATTR_NAME_ERR,
        "\u65E0\u6CD5\u8C03\u7528\u5C5E\u6027 ''{0}''"},


        {ErrorMsg.ILLEGAL_TEXT_NODE_ERR,
        "\u6587\u672C\u6570\u636E\u4F4D\u4E8E\u9876\u7EA7 <xsl:stylesheet> \u5143\u7D20\u5916\u90E8\u3002"},


        {ErrorMsg.SAX_PARSER_CONFIG_ERR,
        "JAXP \u89E3\u6790\u5668\u672A\u6B63\u786E\u914D\u7F6E"},


        {ErrorMsg.INTERNAL_ERR,
        "\u4E0D\u53EF\u6062\u590D\u7684 XSLTC \u5185\u90E8\u9519\u8BEF: ''{0}''"},


        {ErrorMsg.UNSUPPORTED_XSL_ERR,
        "XSL \u5143\u7D20 ''{0}'' \u4E0D\u53D7\u652F\u6301\u3002"},


        {ErrorMsg.UNSUPPORTED_EXT_ERR,
        "XSLTC \u6269\u5C55 ''{0}'' \u65E0\u6CD5\u8BC6\u522B\u3002"},


        {ErrorMsg.MISSING_XSLT_URI_ERR,
        "\u8F93\u5165\u6587\u6863\u4E0D\u662F\u6837\u5F0F\u8868 (\u672A\u5728\u6839\u5143\u7D20\u4E2D\u58F0\u660E XSL \u540D\u79F0\u7A7A\u95F4)\u3002"},


        {ErrorMsg.MISSING_XSLT_TARGET_ERR,
        "\u627E\u4E0D\u5230\u6837\u5F0F\u8868\u76EE\u6807 ''{0}''\u3002"},


        {ErrorMsg.ACCESSING_XSLT_TARGET_ERR,
        "\u7531\u4E8E accessExternalStylesheet \u5C5E\u6027\u8BBE\u7F6E\u7684\u9650\u5236\u800C\u4E0D\u5141\u8BB8 ''{1}'' \u8BBF\u95EE, \u56E0\u6B64\u65E0\u6CD5\u8BFB\u53D6\u6837\u5F0F\u8868\u76EE\u6807 ''{0}''\u3002"},


        {ErrorMsg.NOT_IMPLEMENTED_ERR,
        "\u672A\u5B9E\u73B0: ''{0}''\u3002"},


        {ErrorMsg.NOT_STYLESHEET_ERR,
        "\u8F93\u5165\u6587\u6863\u4E0D\u5305\u542B XSL \u6837\u5F0F\u8868\u3002"},


        {ErrorMsg.ELEMENT_PARSE_ERR,
        "\u65E0\u6CD5\u89E3\u6790\u5143\u7D20 ''{0}''"},


        {ErrorMsg.KEY_USE_ATTR_ERR,
        "<key> \u7684 use \u5C5E\u6027\u5FC5\u987B\u662F node, node-set, string \u6216 number\u3002"},


        {ErrorMsg.OUTPUT_VERSION_ERR,
        "\u8F93\u51FA XML \u6587\u6863\u7248\u672C\u5E94\u4E3A 1.0"},


        {ErrorMsg.ILLEGAL_RELAT_OP_ERR,
        "\u5173\u7CFB\u8868\u8FBE\u5F0F\u7684\u8FD0\u7B97\u7B26\u672A\u77E5"},


        {ErrorMsg.ATTRIBSET_UNDEF_ERR,
        "\u5C1D\u8BD5\u4F7F\u7528\u4E0D\u5B58\u5728\u7684\u5C5E\u6027\u96C6 ''{0}''\u3002"},


        {ErrorMsg.ATTR_VAL_TEMPLATE_ERR,
        "\u65E0\u6CD5\u89E3\u6790\u5C5E\u6027\u503C\u6A21\u677F ''{0}''\u3002"},


        {ErrorMsg.UNKNOWN_SIG_TYPE_ERR,
        "\u7C7B ''{0}'' \u7684\u7B7E\u540D\u4E2D\u7684\u6570\u636E\u7C7B\u578B\u672A\u77E5\u3002"},


        {ErrorMsg.DATA_CONVERSION_ERR,
        "\u65E0\u6CD5\u5C06\u6570\u636E\u7C7B\u578B ''{0}'' \u8F6C\u6362\u4E3A ''{1}''\u3002"},


        {ErrorMsg.NO_TRANSLET_CLASS_ERR,
        "\u6B64 Templates \u4E0D\u5305\u542B\u6709\u6548\u7684 translet \u7C7B\u5B9A\u4E49\u3002"},


        {ErrorMsg.NO_MAIN_TRANSLET_ERR,
        "\u6B64 Templates \u4E0D\u5305\u542B\u540D\u4E3A ''{0}'' \u7684\u7C7B\u3002"},


        {ErrorMsg.TRANSLET_CLASS_ERR,
        "\u65E0\u6CD5\u52A0\u8F7D translet \u7C7B ''{0}''\u3002"},

        {ErrorMsg.TRANSLET_OBJECT_ERR,
        "\u5DF2\u52A0\u8F7D Translet \u7C7B, \u4F46\u65E0\u6CD5\u521B\u5EFA translet \u5B9E\u4F8B\u3002"},


        {ErrorMsg.ERROR_LISTENER_NULL_ERR,
        "\u5C1D\u8BD5\u5C06 ''{0}'' \u7684 ErrorListener \u8BBE\u7F6E\u4E3A\u7A7A\u503C"},


        {ErrorMsg.JAXP_UNKNOWN_SOURCE_ERR,
        "XSLTC \u4EC5\u652F\u6301 StreamSource, SAXSource \u548C DOMSource"},


        {ErrorMsg.JAXP_NO_SOURCE_ERR,
        "\u4F20\u9012\u5230 ''{0}'' \u7684 Source \u5BF9\u8C61\u4E0D\u5305\u542B\u4EFB\u4F55\u5185\u5BB9\u3002"},


        {ErrorMsg.JAXP_COMPILE_ERR,
        "\u65E0\u6CD5\u7F16\u8BD1\u6837\u5F0F\u8868"},


        {ErrorMsg.JAXP_INVALID_ATTR_ERR,
        "TransformerFactory \u65E0\u6CD5\u8BC6\u522B\u5C5E\u6027 ''{0}''\u3002"},

        {ErrorMsg.JAXP_INVALID_ATTR_VALUE_ERR,
        "\u4E3A ''{0}'' \u5C5E\u6027\u6307\u5B9A\u7684\u503C\u4E0D\u6B63\u786E\u3002"},


        {ErrorMsg.JAXP_SET_RESULT_ERR,
        "\u5FC5\u987B\u5148\u8C03\u7528 setResult(), \u518D\u8C03\u7528 startDocument()\u3002"},


        {ErrorMsg.JAXP_NO_TRANSLET_ERR,
        "Transformer \u6CA1\u6709\u5185\u5D4C\u7684 translet \u5BF9\u8C61\u3002"},


        {ErrorMsg.JAXP_NO_HANDLER_ERR,
        "\u8F6C\u6362\u7ED3\u679C\u6CA1\u6709\u5B9A\u4E49\u7684\u8F93\u51FA\u5904\u7406\u7A0B\u5E8F\u3002"},


        {ErrorMsg.JAXP_NO_RESULT_ERR,
        "\u4F20\u9012\u5230 ''{0}'' \u7684 Result \u5BF9\u8C61\u65E0\u6548\u3002"},


        {ErrorMsg.JAXP_UNKNOWN_PROP_ERR,
        "\u5C1D\u8BD5\u8BBF\u95EE\u65E0\u6548\u7684 Transformer \u5C5E\u6027 ''{0}''\u3002"},


        {ErrorMsg.SAX2DOM_ADAPTER_ERR,
        "\u65E0\u6CD5\u521B\u5EFA SAX2DOM \u9002\u914D\u5668: ''{0}''\u3002"},


        {ErrorMsg.XSLTC_SOURCE_ERR,
        "\u8C03\u7528 XSLTCSource.build() \u65F6\u672A\u8BBE\u7F6E systemId\u3002"},

        { ErrorMsg.ER_RESULT_NULL,
            "Result \u4E0D\u80FD\u4E3A\u7A7A\u503C"},


        {ErrorMsg.JAXP_INVALID_SET_PARAM_VALUE,
        "\u53C2\u6570 {0} \u7684\u503C\u5FC5\u987B\u662F\u6709\u6548 Java \u5BF9\u8C61"},


        {ErrorMsg.COMPILE_STDIN_ERR,
        "-i \u9009\u9879\u5FC5\u987B\u4E0E -o \u9009\u9879\u4E00\u8D77\u4F7F\u7528\u3002"},



        {ErrorMsg.COMPILE_USAGE_STR,
        "\u63D0\u8981\n   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Compile [-o <\u8F93\u51FA>]\n      [-d <\u76EE\u5F55>] [-j <jarfile>] [-p <\u7A0B\u5E8F\u5305>]\n      [-n] [-x] [-u] [-v] [-h] { <\u6837\u5F0F\u8868> | -i }\n\n\u9009\u9879\n   -o <\u8F93\u51FA>    \u4E3A\u751F\u6210\u7684 translet \u5206\u914D\n                  \u540D\u79F0 <\u8F93\u51FA>\u3002\u9ED8\u8BA4\u60C5\u51B5\u4E0B, translet \u540D\u79F0\n                  \u6D3E\u751F\u81EA <\u6837\u5F0F\u8868> \u540D\u79F0\u3002\u5982\u679C\u8981\u7F16\u8BD1\u591A\u4E2A\u6837\u5F0F\u8868, \n                  \u5219\u5FFD\u7565\u6B64\u9009\u9879\u3002\n   -d <\u76EE\u5F55> \u6307\u5B9A translet \u7684\u76EE\u6807\u76EE\u5F55\n   -j <jarfile>   \u5C06 translet \u7C7B\u6253\u5305\u5230\u5177\u6709 <jarfile>\n                  \u6307\u5B9A\u7684\u540D\u79F0\u7684 jar \u6587\u4EF6\u4E2D\n   -p <\u7A0B\u5E8F\u5305>   \u4E3A\u751F\u6210\u7684\u6240\u6709 translet \u7C7B\n                  \u6307\u5B9A\u7A0B\u5E8F\u5305\u540D\u79F0\u524D\u7F00\u3002\n   -n             \u542F\u7528\u6A21\u677F\u5185\u5D4C (\u9ED8\u8BA4\u884C\u4E3A\n                  \u901A\u5E38\u53EF\u63D0\u4F9B\u8F83\u597D\u7684\u6027\u80FD)\u3002\n   -x             \u542F\u7528\u5176\u4ED6\u8C03\u8BD5\u6D88\u606F\u8F93\u51FA\n   -u             \u5C06 <\u6837\u5F0F\u8868> \u53C2\u6570\u89E3\u91CA\u4E3A URL\n   -i             \u5F3A\u5236\u7F16\u8BD1\u5668\u4ECE stdin \u8BFB\u53D6\u6837\u5F0F\u8868\n   -v             \u8F93\u51FA\u7F16\u8BD1\u5668\u7684\u7248\u672C\n   -h             \u8F93\u51FA\u6B64\u7528\u6CD5\u8BED\u53E5\n"},


        {ErrorMsg.TRANSFORM_USAGE_STR,
        "\u63D0\u8981\n   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Transform [-j <jarfile>]\n      [-x] [-n <\u8FED\u4EE3\u6570>] {-u <document_url> | <\u6587\u6863>}\n      <\u7C7B> [<param1>=<value1> ...]\n\n   \u4F7F\u7528 translet <\u7C7B> \u8F6C\u6362\n   <\u6587\u6863> \u6307\u5B9A\u7684 XML \u6587\u6863\u3002translet <\u7C7B> \u4F4D\u4E8E\n   \u7528\u6237\u7684 CLASSPATH \u6216\u9009\u62E9\u6027\u6307\u5B9A\u7684 <jarfile> \u4E2D\u3002\n\u9009\u9879\n   -j <jarfile>    \u6307\u5B9A\u8981\u4ECE\u4E2D\u52A0\u8F7D translet \u7684 jarfile\n   -x              \u542F\u7528\u5176\u4ED6\u8C03\u8BD5\u6D88\u606F\u8F93\u51FA\n   -n <\u8FED\u4EE3\u6570> \u8FD0\u884C <\u8FED\u4EE3\u6570> \u6B21\u8F6C\u6362\u5E76\n                   \u663E\u793A\u914D\u7F6E\u6587\u4EF6\u4FE1\u606F\n   -u <document_url> \u4EE5 URL \u5F62\u5F0F\u6307\u5B9A XML \u8F93\u5165\u6587\u6863\n"},




        {ErrorMsg.STRAY_SORT_ERR,
        "<xsl:sort> \u53EA\u80FD\u5728 <xsl:for-each> \u6216 <xsl:apply-templates> \u4E2D\u4F7F\u7528\u3002"},


        {ErrorMsg.UNSUPPORTED_ENCODING,
        "\u6B64 JVM \u4E2D\u4E0D\u652F\u6301\u8F93\u51FA\u7F16\u7801 ''{0}''\u3002"},


        {ErrorMsg.SYNTAX_ERR,
        "''{0}'' \u4E2D\u7684\u8BED\u6CD5\u9519\u8BEF\u3002"},


        {ErrorMsg.CONSTRUCTOR_NOT_FOUND,
        "\u627E\u4E0D\u5230\u5916\u90E8\u6784\u9020\u5668 ''{0}''\u3002"},


        {ErrorMsg.NO_JAVA_FUNCT_THIS_REF,
        "\u975E static Java \u51FD\u6570 ''{0}'' \u7684\u7B2C\u4E00\u4E2A\u53C2\u6570\u4E0D\u662F\u6709\u6548\u7684\u5BF9\u8C61\u5F15\u7528\u3002"},


        {ErrorMsg.TYPE_CHECK_ERR,
        "\u68C0\u67E5\u8868\u8FBE\u5F0F ''{0}'' \u7684\u7C7B\u578B\u65F6\u51FA\u9519\u3002"},


        {ErrorMsg.TYPE_CHECK_UNK_LOC_ERR,
        "\u68C0\u67E5\u672A\u77E5\u4F4D\u7F6E\u7684\u8868\u8FBE\u5F0F\u7C7B\u578B\u65F6\u51FA\u9519\u3002"},


        {ErrorMsg.ILLEGAL_CMDLINE_OPTION_ERR,
        "\u547D\u4EE4\u884C\u9009\u9879 ''{0}'' \u65E0\u6548\u3002"},


        {ErrorMsg.CMDLINE_OPT_MISSING_ARG_ERR,
        "\u547D\u4EE4\u884C\u9009\u9879 ''{0}'' \u7F3A\u5C11\u6240\u9700\u53C2\u6570\u3002"},


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
        "\u4F7F\u7528 translet ''{0}'' \u8FDB\u884C\u8F6C\u6362 "},


        {ErrorMsg.TRANSFORM_WITH_JAR_STR,
        "\u4F7F\u7528 translet ''{0}'' \u4ECE jar \u6587\u4EF6 ''{1}'' \u8FDB\u884C\u8F6C\u6362"},


        {ErrorMsg.COULD_NOT_CREATE_TRANS_FACT,
        "\u65E0\u6CD5\u521B\u5EFA TransformerFactory \u7C7B ''{0}'' \u7684\u5B9E\u4F8B\u3002"},


        {ErrorMsg.TRANSLET_NAME_JAVA_CONFLICT,
         "\u540D\u79F0 ''{0}'' \u5305\u542B\u4E0D\u5141\u8BB8\u5728 Java \u7C7B\u540D\u4E2D\u4F7F\u7528\u7684\u5B57\u7B26, \u56E0\u6B64\u65E0\u6CD5\u5C06\u6B64\u540D\u79F0\u7528\u4F5C translet \u7C7B\u7684\u540D\u79F0\u3002\u5DF2\u6539\u7528\u540D\u79F0 ''{1}''\u3002"},


        {ErrorMsg.COMPILER_ERROR_KEY,
        "\u7F16\u8BD1\u5668\u9519\u8BEF:"},


        {ErrorMsg.COMPILER_WARNING_KEY,
        "\u7F16\u8BD1\u5668\u8B66\u544A:"},


        {ErrorMsg.RUNTIME_ERROR_KEY,
        "Translet \u9519\u8BEF:"},


        {ErrorMsg.INVALID_QNAME_ERR,
        "\u5176\u503C\u5FC5\u987B\u4E3A QName \u6216\u7531\u7A7A\u683C\u5206\u9694\u7684 QName \u5217\u8868\u7684\u5C5E\u6027\u5177\u6709\u503C ''{0}''"},


        {ErrorMsg.INVALID_NCNAME_ERR,
        "\u5176\u503C\u5FC5\u987B\u4E3A NCName \u7684\u5C5E\u6027\u5177\u6709\u503C ''{0}''"},


        {ErrorMsg.INVALID_METHOD_IN_OUTPUT,
        "<xsl:output> \u5143\u7D20\u7684 method \u5C5E\u6027\u5177\u6709\u503C ''{0}''\u3002\u8BE5\u503C\u5FC5\u987B\u662F ''xml'', ''html'', ''text'' \u6216 qname-but-not-ncname \u4E4B\u4E00"},

        {ErrorMsg.JAXP_GET_FEATURE_NULL_NAME,
        "TransformerFactory.getFeature(String name) \u4E2D\u7684\u529F\u80FD\u540D\u79F0\u4E0D\u80FD\u4E3A\u7A7A\u503C\u3002"},

        {ErrorMsg.JAXP_SET_FEATURE_NULL_NAME,
        "TransformerFactory.setFeature(String name, boolean value) \u4E2D\u7684\u529F\u80FD\u540D\u79F0\u4E0D\u80FD\u4E3A\u7A7A\u503C\u3002"},

        {ErrorMsg.JAXP_UNSUPPORTED_FEATURE,
        "\u65E0\u6CD5\u5BF9\u6B64 TransformerFactory \u8BBE\u7F6E\u529F\u80FD ''{0}''\u3002"},

        {ErrorMsg.JAXP_SECUREPROCESSING_FEATURE,
        "FEATURE_SECURE_PROCESSING: \u5B58\u5728 Security Manager \u65F6, \u65E0\u6CD5\u5C06\u6B64\u529F\u80FD\u8BBE\u7F6E\u4E3A\u201C\u5047\u201D\u3002"},


        {ErrorMsg.OUTLINE_ERR_TRY_CATCH,
         "\u5185\u90E8 XSLTC \u9519\u8BEF: \u751F\u6210\u7684\u5B57\u8282\u4EE3\u7801\u5305\u542B try-catch-finally \u5757, \u65E0\u6CD5\u8FDB\u884C Outlined\u3002"},


        {ErrorMsg.OUTLINE_ERR_UNBALANCED_MARKERS,
         "\u5185\u90E8 XSLTC \u9519\u8BEF: OutlineableChunkStart \u548C OutlineableChunkEnd \u6807\u8BB0\u5FC5\u987B\u914D\u5BF9\u5E76\u4E14\u6B63\u786E\u5D4C\u5957\u3002"},


        {ErrorMsg.OUTLINE_ERR_DELETED_TARGET,
         "\u5185\u90E8 XSLTC \u9519\u8BEF: \u5C5E\u4E8E\u5DF2\u8FDB\u884C Outlined \u7684\u5B57\u8282\u4EE3\u7801\u5757\u7684\u6307\u4EE4\u5728\u539F\u59CB\u65B9\u6CD5\u4E2D\u4ECD\u88AB\u5F15\u7528\u3002"
        },



        {ErrorMsg.OUTLINE_ERR_METHOD_TOO_BIG,
         "\u5185\u90E8 XSLTC \u9519\u8BEF: translet \u4E2D\u7684\u65B9\u6CD5\u8D85\u8FC7\u4E86 Java \u865A\u62DF\u673A\u7684\u65B9\u6CD5\u957F\u5EA6\u9650\u5236 64 KB\u3002\u8FD9\u901A\u5E38\u662F\u7531\u4E8E\u6837\u5F0F\u8868\u4E2D\u7684\u6A21\u677F\u975E\u5E38\u5927\u9020\u6210\u7684\u3002\u8BF7\u5C1D\u8BD5\u4F7F\u7528\u8F83\u5C0F\u7684\u6A21\u677F\u91CD\u65B0\u6784\u5EFA\u6837\u5F0F\u8868\u3002"
        },

         {ErrorMsg.DESERIALIZE_TRANSLET_ERR, "\u542F\u7528\u4E86 Java \u5B89\u5168\u65F6, \u5C06\u7981\u7528\u5BF9\u53CD\u5E8F\u5217\u5316 TemplatesImpl \u7684\u652F\u6301\u3002\u53EF\u4EE5\u901A\u8FC7\u5C06 jdk.xml.enableTemplatesImplDeserialization \u7CFB\u7EDF\u5C5E\u6027\u8BBE\u7F6E\u4E3A\u201C\u771F\u201D\u6765\u8986\u76D6\u6B64\u8BBE\u7F6E\u3002"}

    };

    }
}
