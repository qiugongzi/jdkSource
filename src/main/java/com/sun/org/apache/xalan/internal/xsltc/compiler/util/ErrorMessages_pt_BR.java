


package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import java.util.ListResourceBundle;


public class ErrorMessages_pt_BR extends ListResourceBundle {



    public Object[][] getContents()
    {
      return new Object[][] {
        {ErrorMsg.MULTIPLE_STYLESHEET_ERR,
        "Mais de uma folha de estilos definida no mesmo arquivo."},


        {ErrorMsg.TEMPLATE_REDEF_ERR,
        "O modelo ''{0}'' j\u00E1 foi definido nesta folha de estilos."},



        {ErrorMsg.TEMPLATE_UNDEF_ERR,
        "O modelo ''{0}'' n\u00E3o foi definido nesta folha de estilos."},


        {ErrorMsg.VARIABLE_REDEF_ERR,
        "A vari\u00E1vel ''{0}'' est\u00E1 definida v\u00E1rias vezes no mesmo escopo."},


        {ErrorMsg.VARIABLE_UNDEF_ERR,
        "Vari\u00E1vel ou par\u00E2metro ''{0}'' indefinido."},


        {ErrorMsg.CLASS_NOT_FOUND_ERR,
        "N\u00E3o \u00E9 poss\u00EDvel localizar a classe ''{0}''."},


        {ErrorMsg.METHOD_NOT_FOUND_ERR,
        "N\u00E3o \u00E9 poss\u00EDvel localizar o m\u00E9todo externo ''{0}'' (deve ser p\u00FAblico)."},


        {ErrorMsg.ARGUMENT_CONVERSION_ERR,
        "N\u00E3o \u00E9 poss\u00EDvel converter o argumento/tipo de retorno na chamada para o m\u00E9todo ''{0}''"},


        {ErrorMsg.FILE_NOT_FOUND_ERR,
        "Arquivo ou URI ''{0}'' n\u00E3o encontrado."},


        {ErrorMsg.INVALID_URI_ERR,
        "URI inv\u00E1lido ''{0}''."},


        {ErrorMsg.FILE_ACCESS_ERR,
        "N\u00E3o \u00E9 poss\u00EDvel abrir o arquivo ou o URI ''{0}''."},


        {ErrorMsg.MISSING_ROOT_ERR,
        "elemento <xsl:stylesheet> ou <xsl:transform> esperado."},


        {ErrorMsg.NAMESPACE_UNDEF_ERR,
        "O prefixo do namespace ''{0}'' n\u00E3o foi declarado."},


        {ErrorMsg.FUNCTION_RESOLVE_ERR,
        "N\u00E3o \u00E9 poss\u00EDvel resolver a chamada para a fun\u00E7\u00E3o ''{0}''."},


        {ErrorMsg.NEED_LITERAL_ERR,
        "O argumento para \"{0}'' deve ser uma string literal."},


        {ErrorMsg.XPATH_PARSER_ERR,
        "Erro durante o parsing da express\u00E3o XPath ''{0}''."},


        {ErrorMsg.REQUIRED_ATTR_ERR,
        "O atributo obrigat\u00F3rio ''{0}'' n\u00E3o foi encontrado."},


        {ErrorMsg.ILLEGAL_CHAR_ERR,
        "Caractere inv\u00E1lido ''{0}'' na express\u00E3o XPath."},


        {ErrorMsg.ILLEGAL_PI_ERR,
        "Nome inv\u00E1lido ''{0}'' para instru\u00E7\u00E3o de processamento."},


        {ErrorMsg.STRAY_ATTRIBUTE_ERR,
        "Atributo ''{0}'' fora do elemento."},


        {ErrorMsg.ILLEGAL_ATTRIBUTE_ERR,
        "Atributo ''{0}'' inv\u00E1lido."},


        {ErrorMsg.CIRCULAR_INCLUDE_ERR,
        "Import/Include circular. Folha de estilos ''{0}'' j\u00E1 carregada."},


        {ErrorMsg.RESULT_TREE_SORT_ERR,
        "Os fragmentos da \u00E1rvore n\u00E3o podem ser classificados (os elementos <xsl:sort> foram ignorados). Voc\u00EA deve classificar os n\u00F3s ao criar a \u00E1rvore de resultados."},


        {ErrorMsg.SYMBOLS_REDEF_ERR,
        "A formata\u00E7\u00E3o decimal ''{0}'' j\u00E1 foi definida."},


        {ErrorMsg.XSL_VERSION_ERR,
        "A vers\u00E3o XSL \"{0}'' n\u00E3o \u00E9 suportada por XSLTC."},


        {ErrorMsg.CIRCULAR_VARIABLE_ERR,
        "Refer\u00EAncia de vari\u00E1vel/par\u00E2metro circulares ''{0}''."},


        {ErrorMsg.ILLEGAL_BINARY_OP_ERR,
        "Operador desconhecido para a express\u00E3o bin\u00E1ria."},


        {ErrorMsg.ILLEGAL_ARG_ERR,
        "Argumento(s) inv\u00E1lido(s) para a chamada da fun\u00E7\u00E3o."},


        {ErrorMsg.DOCUMENT_ARG_ERR,
        "O segundo argumento para a fun\u00E7\u00E3o document() deve ser um conjunto de n\u00F3s."},


        {ErrorMsg.MISSING_WHEN_ERR,
        "\u00C9 necess\u00E1rio, pelo menos, um elemento <xsl:when> em <xsl:choose>."},


        {ErrorMsg.MULTIPLE_OTHERWISE_ERR,
        "\u00C9 permitido somente um elemento <xsl:otherwise> em <xsl:choose>."},


        {ErrorMsg.STRAY_OTHERWISE_ERR,
        "<xsl:otherwise> s\u00F3 pode ser usado em <xsl:choose>."},


        {ErrorMsg.STRAY_WHEN_ERR,
        "<xsl:when> s\u00F3 pode ser usado em <xsl:choose>."},


        {ErrorMsg.WHEN_ELEMENT_ERR,
        "Somente os elementos <xsl:when> e <xsl:otherwise> s\u00E3o permitidos em <xsl:choose>."},


        {ErrorMsg.UNNAMED_ATTRIBSET_ERR,
        "<xsl:attribute-set> n\u00E3o encontrado no atributo 'name'."},


        {ErrorMsg.ILLEGAL_CHILD_ERR,
        "Elemento filho inv\u00E1lido."},


        {ErrorMsg.ILLEGAL_ELEM_NAME_ERR,
        "Voc\u00EA n\u00E3o pode chamar um elemento ''{0}''"},


        {ErrorMsg.ILLEGAL_ATTR_NAME_ERR,
        "Voc\u00EA n\u00E3o pode chamar um atributo ''{0}''"},


        {ErrorMsg.ILLEGAL_TEXT_NODE_ERR,
        "Dados de texto fora do elemento <xsl:stylesheet> de n\u00EDvel superior."},


        {ErrorMsg.SAX_PARSER_CONFIG_ERR,
        "Parser de JAXP n\u00E3o configurado corretamente"},


        {ErrorMsg.INTERNAL_ERR,
        "Erro interno-XSLTC irrecuper\u00E1vel: ''{0}''"},


        {ErrorMsg.UNSUPPORTED_XSL_ERR,
        "Elemento XSL n\u00E3o suportado ''{0}''."},


        {ErrorMsg.UNSUPPORTED_EXT_ERR,
        "Extens\u00E3o de XSLTC n\u00E3o reconhecida ''{0}''."},


        {ErrorMsg.MISSING_XSLT_URI_ERR,
        "O documento de entrada n\u00E3o \u00E9 uma folha de estilos (o namespace XSL n\u00E3o foi declarado no elemento-raiz)."},


        {ErrorMsg.MISSING_XSLT_TARGET_ERR,
        "N\u00E3o foi poss\u00EDvel localizar o alvo da folha de estilos ''{0}''."},


        {ErrorMsg.ACCESSING_XSLT_TARGET_ERR,
        "N\u00E3o foi poss\u00EDvel ler o alvo ''{0}'' da folha de estilos, porque o acesso a ''{1}'' n\u00E3o \u00E9 permitido em virtude da restri\u00E7\u00E3o definida pela propriedade accessExternalStylesheet."},


        {ErrorMsg.NOT_IMPLEMENTED_ERR,
        "N\u00E3o implementado: ''{0}''."},


        {ErrorMsg.NOT_STYLESHEET_ERR,
        "O documento de entrada n\u00E3o cont\u00E9m uma folha de estilos XSL."},


        {ErrorMsg.ELEMENT_PARSE_ERR,
        "N\u00E3o foi poss\u00EDvel fazer parsing do elemento ''{0}''"},


        {ErrorMsg.KEY_USE_ATTR_ERR,
        "O atributo use de <key> deve ser node, node-set, string ou number."},


        {ErrorMsg.OUTPUT_VERSION_ERR,
        "A vers\u00E3o do documento XML de sa\u00EDda deve ser 1.0"},


        {ErrorMsg.ILLEGAL_RELAT_OP_ERR,
        "Opera\u00E7\u00E3o desconhecida para a express\u00E3o relacional"},


        {ErrorMsg.ATTRIBSET_UNDEF_ERR,
        "Tentativa de usar um conjunto de atributos ''{0}'' n\u00E3o existente."},


        {ErrorMsg.ATTR_VAL_TEMPLATE_ERR,
        "N\u00E3o \u00E9 poss\u00EDvel fazer parsing do modelo do valor do atributo ''{0}''."},


        {ErrorMsg.UNKNOWN_SIG_TYPE_ERR,
        "Tipo de dados desconhecido na assinatura da classe ''{0}''."},


        {ErrorMsg.DATA_CONVERSION_ERR,
        "N\u00E3o \u00E9 poss\u00EDvel converter o tipo de dados ''{0}'' em ''{1}''."},


        {ErrorMsg.NO_TRANSLET_CLASS_ERR,
        "Este Templates n\u00E3o cont\u00E9m uma defini\u00E7\u00E3o de classe translet v\u00E1lida."},


        {ErrorMsg.NO_MAIN_TRANSLET_ERR,
        "Este Templates n\u00E3o cont\u00E9m uma classe com o nome ''{0}''."},


        {ErrorMsg.TRANSLET_CLASS_ERR,
        "N\u00E3o foi poss\u00EDvel carregar a classe translet ''{0}''."},

        {ErrorMsg.TRANSLET_OBJECT_ERR,
        "Classe translet carregada, mas n\u00E3o \u00E9 poss\u00EDvel criar uma inst\u00E2ncia translet."},


        {ErrorMsg.ERROR_LISTENER_NULL_ERR,
        "Tentativa de definir ErrorListener para ''{0}'' como nulo"},


        {ErrorMsg.JAXP_UNKNOWN_SOURCE_ERR,
        "Somente StreamSource, SAXSource e DOMSource s\u00E3o suportados por XSLTC"},


        {ErrorMsg.JAXP_NO_SOURCE_ERR,
        "O objeto source especificado para ''{0}'' n\u00E3o tem conte\u00FAdo."},


        {ErrorMsg.JAXP_COMPILE_ERR,
        "N\u00E3o foi poss\u00EDvel compilar a folha de estilos"},


        {ErrorMsg.JAXP_INVALID_ATTR_ERR,
        "TransformerFactory n\u00E3o reconhece o atributo ''{0}''."},

        {ErrorMsg.JAXP_INVALID_ATTR_VALUE_ERR,
        "Valor incorreto especificado para o atributo ''{0}''."},


        {ErrorMsg.JAXP_SET_RESULT_ERR,
        "setResult() deve ser chamado antes de startDocument()."},


        {ErrorMsg.JAXP_NO_TRANSLET_ERR,
        "O Transformer n\u00E3o tem um objeto translet encapsulado."},


        {ErrorMsg.JAXP_NO_HANDLER_ERR,
        "Nenhum handler de sa\u00EDda definido para o resultado da transforma\u00E7\u00E3o."},


        {ErrorMsg.JAXP_NO_RESULT_ERR,
        "O objeto result especificado para ''{0}'' \u00E9 inv\u00E1lido."},


        {ErrorMsg.JAXP_UNKNOWN_PROP_ERR,
        "Tentativa de acessar a propriedade ''{0}'' do Transformer inv\u00E1lida."},


        {ErrorMsg.SAX2DOM_ADAPTER_ERR,
        "N\u00E3o foi poss\u00EDvel criar o adaptador SAX2DOM: ''{0}''."},


        {ErrorMsg.XSLTC_SOURCE_ERR,
        "XSLTCSource.build() chamado sem o systemId ser definido."},

        { ErrorMsg.ER_RESULT_NULL,
            "O resultado n\u00E3o deve ser nulo"},


        {ErrorMsg.JAXP_INVALID_SET_PARAM_VALUE,
        "O valor do par\u00E2metro {0} deve ser um Objeto Java v\u00E1lido"},


        {ErrorMsg.COMPILE_STDIN_ERR,
        "A op\u00E7\u00E3o -i deve ser usada com a op\u00E7\u00E3o -o."},



        {ErrorMsg.COMPILE_USAGE_STR,
        "SINOPSE\n   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Compile [-o <sa\u00EDda>]\n      [-d <diret\u00F3rio>] [-j <jarfile>] [-p <pacote>]\n      [-n] [-x] [-u] [-v] [-h] { <folha de estilos> | -i }\n\nOP\u00C7\u00D5ES\n   -o <sa\u00EDda>    atribui o nome <sa\u00EDda> ao translet\n                  gerado.  Por padr\u00E3o, o nome translet\n                  origina-se do nome <folha de estilos>.  Esta op\u00E7\u00E3o\n                  \u00E9 ignorada caso sejam compiladas v\u00E1rias folhas de estilos.\n   -d <diret\u00F3rio> especifica um diret\u00F3rio de destino para translet\n   -j <arquivo jar>   empacota as classes translet em um arquivo jar do\n                  nome especificado como <arquivo jar>\n   -p <pacote>   especifica um prefixo de nome do pacote para todas as classes\n                  translet geradas.\n   -n             permite a inclus\u00E3o do modelo na linha (comportamento padr\u00E3o melhor\n                  em m\u00E9dia).\n   -x             ativa a sa\u00EDda de mensagens de depura\u00E7\u00E3o adicionais\n   -u             interpreta os argumentos <folha de estilos> como URLs\n   -i             obriga o compilador a ler a folha de estilos de stdin\n   -v             imprime a vers\u00E3o do compilador\n   -h             imprime esta instru\u00E7\u00E3o de uso\n"},


        {ErrorMsg.TRANSFORM_USAGE_STR,
        "SINOPSE \n   java com.sun.org.apache.xalan.internal.xsltc.cmdline.Transforme [-j <arquivo jar>]\n      [-x] [-n <itera\u00E7\u00F5es>] {-u <url_documento> | <documento>}\n      <classe> [<par\u00E2m1>=<valor1> ...]\n\n   usa a <classe> translet para transformar um documento XML \n   especificado como <documento>. O translet <classe> est\u00E1 no\n   CLASSPATH do usu\u00E1rio ou no <arquivo jar> opcionalmente especificado.\nOP\u00C7\u00D5ES\n   -j <arquivo jar>    especifica um arquivo jar com base no qual ser\u00E1 carregado o translet\n   -x              ativa a sa\u00EDda de mensagens de depura\u00E7\u00E3o adicionais\n   -n <itera\u00E7\u00F5es> executa a transforma\u00E7\u00E3o <itera\u00E7\u00F5es> vezes e\n                   exibe as informa\u00E7\u00F5es de perfis\n   -u <url_documento> especifica o documento XML de entrada na forma de URL\n"},




        {ErrorMsg.STRAY_SORT_ERR,
        "<xsl:sort> s\u00F3 pode ser usado dentro de <xsl:for-each> ou <xsl:apply-templates>."},


        {ErrorMsg.UNSUPPORTED_ENCODING,
        "A codifica\u00E7\u00E3o de sa\u00EDda ''{0}'' n\u00E3o \u00E9 suportada nesta JVM."},


        {ErrorMsg.SYNTAX_ERR,
        "Erro de sintaxe em ''{0}''."},


        {ErrorMsg.CONSTRUCTOR_NOT_FOUND,
        "N\u00E3o \u00E9 poss\u00EDvel localizar o construtor externo ''{0}''."},


        {ErrorMsg.NO_JAVA_FUNCT_THIS_REF,
        "O primeiro argumento para a fun\u00E7\u00E3o Java n\u00E3o static ''{0}'' n\u00E3o \u00E9 uma refer\u00EAncia de objeto v\u00E1lida."},


        {ErrorMsg.TYPE_CHECK_ERR,
        "Erro ao verificar o tipo de express\u00E3o ''{0}''."},


        {ErrorMsg.TYPE_CHECK_UNK_LOC_ERR,
        "Erro ao verificar o tipo de uma express\u00E3o em uma localiza\u00E7\u00E3o desconhecida."},


        {ErrorMsg.ILLEGAL_CMDLINE_OPTION_ERR,
        "A op\u00E7\u00E3o da linha de comandos ''{0}'' n\u00E3o \u00E9 v\u00E1lida."},


        {ErrorMsg.CMDLINE_OPT_MISSING_ARG_ERR,
        "A op\u00E7\u00E3o da linha de comandos ''{0}'' n\u00E3o encontrou um argumento obrigat\u00F3rio."},


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
        "Transformar usando translet ''{0}'' "},


        {ErrorMsg.TRANSFORM_WITH_JAR_STR,
        "Transformar usando translet ''{0}'' do arquivo jar ''{1}''"},


        {ErrorMsg.COULD_NOT_CREATE_TRANS_FACT,
        "N\u00E3o foi poss\u00EDvel criar uma inst\u00E2ncia da classe TransformerFactory ''{0}''."},


        {ErrorMsg.TRANSLET_NAME_JAVA_CONFLICT,
         "N\u00E3o foi poss\u00EDvel usar o nome ''{0}'' como o nome da classe translet, pois ele cont\u00E9m caracteres que n\u00E3o s\u00E3o permitidos no nome da classe Java. O nome ''{1}'' foi usado."},


        {ErrorMsg.COMPILER_ERROR_KEY,
        "Erros do compilador:"},


        {ErrorMsg.COMPILER_WARNING_KEY,
        "Advert\u00EAncias do compilador:"},


        {ErrorMsg.RUNTIME_ERROR_KEY,
        "Erros de translet:"},


        {ErrorMsg.INVALID_QNAME_ERR,
        "Um atributo cujo valor deve ser um QName ou uma lista de QNames separada por espa\u00E7os em branco tinha o valor ''{0}''"},


        {ErrorMsg.INVALID_NCNAME_ERR,
        "Um atributo cujo valor deve ser um NCName tinha o valor ''{0}''"},


        {ErrorMsg.INVALID_METHOD_IN_OUTPUT,
        "O atributo method de um elemento <xsl:output> tinha o valor ''{0}''. O valor deve ser um dos seguintes: ''xml'', ''html'', ''text'', ou qname, mas n\u00E3o ncname"},

        {ErrorMsg.JAXP_GET_FEATURE_NULL_NAME,
        "O nome do recurso n\u00E3o pode ser nulo em TransformerFactory.getFeature(Nome da string)."},

        {ErrorMsg.JAXP_SET_FEATURE_NULL_NAME,
        "O nome do recurso n\u00E3o pode ser nulo em TransformerFactory.setFeature(Nome da string, valor booliano)."},

        {ErrorMsg.JAXP_UNSUPPORTED_FEATURE,
        "N\u00E3o \u00E9 poss\u00EDvel definir o recurso ''{0}'' nesta TransformerFactory."},

        {ErrorMsg.JAXP_SECUREPROCESSING_FEATURE,
        "FEATURE_SECURE_PROCESSING: N\u00E3o \u00E9 poss\u00EDvel definir o recurso como falso quando o gerenciador de seguran\u00E7a est\u00E1 presente."},


        {ErrorMsg.OUTLINE_ERR_TRY_CATCH,
         "Erro interno de XSLTC: o byte code gerado cont\u00E9m um bloco try-catch-finally e n\u00E3o pode ser outlined."},


        {ErrorMsg.OUTLINE_ERR_UNBALANCED_MARKERS,
         "Erro interno de XSLTC: os marcadores OutlineableChunkStart e OutlineableChunkEnd devem ser balanceados e aninhados corretamente."},


        {ErrorMsg.OUTLINE_ERR_DELETED_TARGET,
         "Erro interno de XSLTC: ainda h\u00E1 refer\u00EAncia no m\u00E9todo original a uma instru\u00E7\u00E3o que fazia parte de um bloco de byte code que foi outlined."
        },



        {ErrorMsg.OUTLINE_ERR_METHOD_TOO_BIG,
         "Erro interno de XSLTC: um m\u00E9todo no translet excede a limita\u00E7\u00E3o da M\u00E1quina Virtual Java quanto ao tamanho de um m\u00E9todo de de 64 kilobytes. Em geral, essa situa\u00E7\u00E3o \u00E9 causada por modelos de uma folha de estilos que s\u00E3o muito grandes. Tente reestruturar sua folha de estilos de forma a usar modelos menores."
        },

         {ErrorMsg.DESERIALIZE_TRANSLET_ERR, "Quando a seguran\u00E7a do Java est\u00E1 ativada, o suporte para desserializar TemplatesImpl fica desativado. Essa situa\u00E7\u00E3o pode ser corrigida definindo a propriedade do sistema jdk.xml.enableTemplatesImplDeserialization como true."}

    };

    }
}
