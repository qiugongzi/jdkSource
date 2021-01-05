


package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.serializer.ElemDesc;
import com.sun.org.apache.xml.internal.serializer.ToHTMLStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;


final class LiteralElement extends Instruction {

    private String _name;
    private LiteralElement _literalElemParent = null;
    private List<SyntaxTreeNode> _attributeElements = null;
    private Map<String, String> _accessedPrefixes = null;

    private boolean _allAttributesUnique = false;


    public QName getName() {
        return _qname;
    }


    public void display(int indent) {
        indent(indent);
        Util.println("LiteralElement name = " + _name);
        displayContents(indent + IndentIncrement);
    }


    private String accessedNamespace(String prefix) {
        if (_literalElemParent != null) {
            String result = _literalElemParent.accessedNamespace(prefix);
            if (result != null) {
                return result;
            }
        }
        return _accessedPrefixes != null ? _accessedPrefixes.get(prefix) : null;
    }


    public void registerNamespace(String prefix, String uri,
                                  SymbolTable stable, boolean declared) {

        if (_literalElemParent != null) {
            final String parentUri = _literalElemParent.accessedNamespace(prefix);
            if (parentUri != null && parentUri.equals(uri)) {
                return;
            }
        }

        if (_accessedPrefixes == null) {
            _accessedPrefixes = new Hashtable<>();
        }
        else {
            if (!declared) {
                final String old = _accessedPrefixes.get(prefix);
                if (old != null) {
                    if (old.equals(uri))
                        return;
                    else
                        prefix = stable.generateNamespacePrefix();
                }
            }
        }

        if (!prefix.equals("xml")) {
            _accessedPrefixes.put(prefix,uri);
        }
    }


    private String translateQName(QName qname, SymbolTable stable) {
        String localname = qname.getLocalPart();
        String prefix = qname.getPrefix();

        if (prefix == null)
            prefix = Constants.EMPTYSTRING;
        else if (prefix.equals(XMLNS_PREFIX))
            return(XMLNS_PREFIX);

        final String alternative = stable.lookupPrefixAlias(prefix);
        if (alternative != null) {
            stable.excludeNamespaces(prefix);
            prefix = alternative;
        }

        String uri = lookupNamespace(prefix);
        if (uri == null) return(localname);

        registerNamespace(prefix, uri, stable, false);

        if (prefix != Constants.EMPTYSTRING)
            return(prefix+":"+localname);
        else
            return(localname);
    }


    public void addAttribute(SyntaxTreeNode attribute) {
        if (_attributeElements == null) {
            _attributeElements = new ArrayList<>(2);
        }
        _attributeElements.add(attribute);
    }


    public void setFirstAttribute(SyntaxTreeNode attribute) {
        if (_attributeElements == null) {
            _attributeElements = new ArrayList<>(2);
        }
        _attributeElements.add(0, attribute);
    }


    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (_attributeElements != null) {
            for (SyntaxTreeNode node : _attributeElements) {
                node.typeCheck(stable);
            }
        }
        typeCheckContents(stable);
        return Type.Void;
    }


    public Set<Map.Entry<String, String>> getNamespaceScope(SyntaxTreeNode node) {
        Map<String, String> all = new HashMap<>();

        while (node != null) {
            Map<String, String> mapping = node.getPrefixMapping();
            if (mapping != null) {
                for( String prefix : mapping.keySet()) {
                    if (!all.containsKey(prefix)) {
                        all.put(prefix, mapping.get(prefix));
                    }
                }
            }
            node = node.getParent();
        }
        return all.entrySet();
    }


    public void parseContents(Parser parser) {
        final SymbolTable stable = parser.getSymbolTable();
        stable.setCurrentNode(this);

        SyntaxTreeNode parent = getParent();
        if (parent != null && parent instanceof LiteralElement) {
            _literalElemParent = (LiteralElement) parent;
        }

        _name = translateQName(_qname, stable);

        final int count = _attributes.getLength();
        for (int i = 0; i < count; i++) {
            final QName qname = parser.getQName(_attributes.getQName(i));
            final String uri = qname.getNamespace();
            final String val = _attributes.getValue(i);

            if (qname.equals(parser.getUseAttributeSets())) {
                if (!Util.isValidQNames(val)) {
                    ErrorMsg err = new ErrorMsg(ErrorMsg.INVALID_QNAME_ERR, val, this);
                    parser.reportError(Constants.ERROR, err);
               }
                setFirstAttribute(new UseAttributeSets(val, parser));
            }
            else if (qname.equals(parser.getExtensionElementPrefixes())) {
                stable.excludeNamespaces(val);
            }
            else if (qname.equals(parser.getExcludeResultPrefixes())) {
                stable.excludeNamespaces(val);
            }
            else {
                final String prefix = qname.getPrefix();
                if (prefix != null && prefix.equals(XMLNS_PREFIX) ||
                    prefix == null && qname.getLocalPart().equals(XMLNS_PREFIX) ||
                    uri != null && uri.equals(XSLT_URI))
                {
                    continue;
                }

                final String name = translateQName(qname, stable);
                LiteralAttribute attr = new LiteralAttribute(name, val, parser, this);
                addAttribute(attr);
                attr.setParent(this);
                attr.parseContents(parser);
            }
        }

        Set<Map.Entry<String, String>> include = getNamespaceScope(this);
        for (Map.Entry<String, String> entry : include) {
            final String prefix = entry.getKey();
            if (!prefix.equals("xml")) {
                final String uri = lookupNamespace(prefix);
                if (uri != null && !stable.isExcludedNamespace(uri)) {
                    registerNamespace(prefix, uri, stable, true);
                }
            }
        }

        parseChildren(parser);

        for (int i = 0; i < count; i++) {
            final QName qname = parser.getQName(_attributes.getQName(i));
            final String val = _attributes.getValue(i);

            if (qname.equals(parser.getExtensionElementPrefixes())) {
                stable.unExcludeNamespaces(val);
            }
            else if (qname.equals(parser.getExcludeResultPrefixes())) {
                stable.unExcludeNamespaces(val);
            }
        }
    }

    protected boolean contextDependent() {
        return dependentContents();
    }


    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {

        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();

        _allAttributesUnique = checkAttributesUnique();

        il.append(methodGen.loadHandler());

        il.append(new PUSH(cpg, _name));
        il.append(DUP2);                il.append(methodGen.startElement());

        int j = 0;
        while (j < elementCount())  {
            final SyntaxTreeNode item = elementAt(j);
            if (item instanceof Variable) {
                item.translate(classGen, methodGen);
            }
            j++;
        }

        if (_accessedPrefixes != null) {
            for (Map.Entry<String, String> entry : _accessedPrefixes.entrySet()) {
                final String prefix = entry.getKey();
                final String uri = entry.getValue();
                il.append(methodGen.loadHandler());
                il.append(new PUSH(cpg, prefix));
                il.append(new PUSH(cpg, uri));
                il.append(methodGen.namespace());
            }
        }

        if (_attributeElements != null) {
            for (SyntaxTreeNode node : _attributeElements) {
                if (!(node instanceof XslAttribute)) {
                    node.translate(classGen, methodGen);
                }
            }
        }

        translateContents(classGen, methodGen);

        il.append(methodGen.endElement());
    }


    private boolean isHTMLOutput() {
        return getStylesheet().getOutputMethod() == Stylesheet.HTML_OUTPUT;
    }


    public ElemDesc getElemDesc() {
        if (isHTMLOutput()) {
            return ToHTMLStream.getElemDesc(_name);
        }
        else
            return null;
    }


    public boolean allAttributesUnique() {
        return _allAttributesUnique;
    }


    private boolean checkAttributesUnique() {
         boolean hasHiddenXslAttribute = canProduceAttributeNodes(this, true);
         if (hasHiddenXslAttribute)
             return false;

         if (_attributeElements != null) {
             int numAttrs = _attributeElements.size();
             Map<String, SyntaxTreeNode> attrsTable = null;
             for (int i = 0; i < numAttrs; i++) {
                 SyntaxTreeNode node = _attributeElements.get(i);

                 if (node instanceof UseAttributeSets) {
                     return false;
                 }
                 else if (node instanceof XslAttribute) {
                     if (attrsTable == null) {
                        attrsTable = new HashMap<>();
                         for (int k = 0; k < i; k++) {
                             SyntaxTreeNode n = _attributeElements.get(k);
                             if (n instanceof LiteralAttribute) {
                                 LiteralAttribute literalAttr = (LiteralAttribute)n;
                                 attrsTable.put(literalAttr.getName(), literalAttr);
                             }
                         }
                     }

                     XslAttribute xslAttr = (XslAttribute)node;
                     AttributeValue attrName = xslAttr.getName();
                     if (attrName instanceof AttributeValueTemplate) {
                         return false;
                     }
                     else if (attrName instanceof SimpleAttributeValue) {
                         SimpleAttributeValue simpleAttr = (SimpleAttributeValue)attrName;
                         String name = simpleAttr.toString();
                         if (name != null && attrsTable.get(name) != null)
                             return false;
                         else if (name != null) {
                             attrsTable.put(name, xslAttr);
                         }
                     }
                 }
             }
         }
         return true;
    }


    private boolean canProduceAttributeNodes(SyntaxTreeNode node, boolean ignoreXslAttribute) {
        List<SyntaxTreeNode> contents = node.getContents();
        for (SyntaxTreeNode child : contents) {
            if (child instanceof Text) {
                Text text = (Text)child;
                if (text.isIgnore())
                    continue;
                else
                    return false;
            }
            else if (child instanceof LiteralElement
                || child instanceof ValueOf
                || child instanceof XslElement
                || child instanceof Comment
                || child instanceof Number
                || child instanceof ProcessingInstruction)
                return false;
            else if (child instanceof XslAttribute) {
                if (ignoreXslAttribute)
                    continue;
                else
                    return true;
            }
            else if (child instanceof CallTemplate
                || child instanceof ApplyTemplates
                || child instanceof Copy
                || child instanceof CopyOf)
                return true;
            else if ((child instanceof If
                       || child instanceof ForEach)
                     && canProduceAttributeNodes(child, false)) {
                return true;
            }
            else if (child instanceof Choose) {
                List<SyntaxTreeNode> chooseContents = child.getContents();
                for (SyntaxTreeNode chooseChild : chooseContents) {
                    if (chooseChild instanceof When || chooseChild instanceof Otherwise) {
                        if (canProduceAttributeNodes(chooseChild, false))
                            return true;
                    }
                }
            }
        }
        return false;
    }

}
