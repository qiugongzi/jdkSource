



package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;


final class SymbolTable {

    private final Map<String, Stylesheet> _stylesheets = new HashMap<>();
    private final Map<String, Vector> _primops     = new HashMap<>();

    private Map<String, VariableBase> _variables = null;
    private Map<String, Template> _templates = null;
    private Map<String, AttributeSet> _attributeSets = null;
    private Map<String, String> _aliases = null;
    private Map<String, Integer> _excludedURI = null;
    private Stack<Map<String, Integer>>     _excludedURIStack = null;
    private Map<String, DecimalFormatting> _decimalFormats = null;
    private Map<String, Key> _keys = null;

    public DecimalFormatting getDecimalFormatting(QName name) {
        if (_decimalFormats == null) return null;
        return(_decimalFormats.get(name.getStringRep()));
    }

    public void addDecimalFormatting(QName name, DecimalFormatting symbols) {
        if (_decimalFormats == null) _decimalFormats = new HashMap<>();
        _decimalFormats.put(name.getStringRep(), symbols);
    }

    public Key getKey(QName name) {
        if (_keys == null) return null;
        return _keys.get(name.getStringRep());
    }

    public void addKey(QName name, Key key) {
        if (_keys == null) _keys = new HashMap<>();
        _keys.put(name.getStringRep(), key);
    }

    public Stylesheet addStylesheet(QName name, Stylesheet node) {
        return _stylesheets.put(name.getStringRep(), node);
    }

    public Stylesheet lookupStylesheet(QName name) {
        return _stylesheets.get(name.getStringRep());
    }

    public Template addTemplate(Template template) {
        final QName name = template.getName();
        if (_templates == null) _templates = new HashMap<>();
        return _templates.put(name.getStringRep(), template);
    }

    public Template lookupTemplate(QName name) {
        if (_templates == null) return null;
        return _templates.get(name.getStringRep());
    }

    public Variable addVariable(Variable variable) {
        if (_variables == null) _variables = new HashMap<>();
        final String name = variable.getName().getStringRep();
        return (Variable)_variables.put(name, variable);
    }

    public Param addParam(Param parameter) {
        if (_variables == null) _variables = new HashMap<>();
        final String name = parameter.getName().getStringRep();
        return (Param)_variables.put(name, parameter);
    }

    public Variable lookupVariable(QName qname) {
        if (_variables == null) return null;
        final String name = qname.getStringRep();
        final VariableBase obj = _variables.get(name);
        return obj instanceof Variable ? (Variable)obj : null;
    }

    public Param lookupParam(QName qname) {
        if (_variables == null) return null;
        final String name = qname.getStringRep();
        final VariableBase obj = _variables.get(name);
        return obj instanceof Param ? (Param)obj : null;
    }

    public SyntaxTreeNode lookupName(QName qname) {
        if (_variables == null) return null;
        final String name = qname.getStringRep();
        return (SyntaxTreeNode)_variables.get(name);
    }

    public AttributeSet addAttributeSet(AttributeSet atts) {
        if (_attributeSets == null) _attributeSets = new HashMap<>();
        return _attributeSets.put(atts.getName().getStringRep(), atts);
    }

    public AttributeSet lookupAttributeSet(QName name) {
        if (_attributeSets == null) return null;
        return _attributeSets.get(name.getStringRep());
    }


    public void addPrimop(String name, MethodType mtype) {
        Vector methods = _primops.get(name);
        if (methods == null) {
            _primops.put(name, methods = new Vector());
        }
        methods.addElement(mtype);
    }


    public Vector lookupPrimop(String name) {
        return _primops.get(name);
    }


    private int _nsCounter = 0;

    public String generateNamespacePrefix() {
        return("ns"+(_nsCounter++));
    }


    private SyntaxTreeNode _current = null;

    public void setCurrentNode(SyntaxTreeNode node) {
        _current = node;
    }

    public String lookupNamespace(String prefix) {
        if (_current == null) return(Constants.EMPTYSTRING);
        return(_current.lookupNamespace(prefix));
    }


    public void addPrefixAlias(String prefix, String alias) {
        if (_aliases == null) _aliases = new HashMap<>();
        _aliases.put(prefix,alias);
    }


    public String lookupPrefixAlias(String prefix) {
        if (_aliases == null) return null;
        return _aliases.get(prefix);
    }


    public void excludeURI(String uri) {
        if (uri == null) return;

        if (_excludedURI == null) _excludedURI = new HashMap<>();

        Integer refcnt = _excludedURI.get(uri);
        if (refcnt == null)
            refcnt = 1;
        else
            refcnt = refcnt + 1;
        _excludedURI.put(uri,refcnt);
    }


    public void excludeNamespaces(String prefixes) {
        if (prefixes != null) {
            StringTokenizer tokens = new StringTokenizer(prefixes);
            while (tokens.hasMoreTokens()) {
                final String prefix = tokens.nextToken();
                final String uri;
                if (prefix.equals("#default"))
                    uri = lookupNamespace(Constants.EMPTYSTRING);
                else
                    uri = lookupNamespace(prefix);
                if (uri != null) excludeURI(uri);
            }
        }
    }


    public boolean isExcludedNamespace(String uri) {
        if (uri != null && _excludedURI != null) {
            final Integer refcnt = _excludedURI.get(uri);
            return (refcnt != null && refcnt > 0);
        }
        return false;
    }


    public void unExcludeNamespaces(String prefixes) {
        if (_excludedURI == null) return;
        if (prefixes != null) {
            StringTokenizer tokens = new StringTokenizer(prefixes);
            while (tokens.hasMoreTokens()) {
                final String prefix = tokens.nextToken();
                final String uri;
                if (prefix.equals("#default"))
                    uri = lookupNamespace(Constants.EMPTYSTRING);
                else
                    uri = lookupNamespace(prefix);
                Integer refcnt = _excludedURI.get(uri);
                if (refcnt != null)
                    _excludedURI.put(uri, refcnt - 1);
            }
        }
    }

    public void pushExcludedNamespacesContext() {
        if (_excludedURIStack == null) {
            _excludedURIStack = new Stack();
        }
        _excludedURIStack.push(_excludedURI);
        _excludedURI = null;
    }


    public void popExcludedNamespacesContext() {
        _excludedURI = _excludedURIStack.pop();
        if (_excludedURIStack.isEmpty()) {
            _excludedURIStack = null;
        }
    }

}
