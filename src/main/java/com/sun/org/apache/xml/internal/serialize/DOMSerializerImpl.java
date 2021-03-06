


package com.sun.org.apache.xml.internal.serialize;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;
import java.util.Vector;

import com.sun.org.apache.xerces.internal.dom.AbortException;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.DOMErrorImpl;
import com.sun.org.apache.xerces.internal.dom.DOMLocatorImpl;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.dom.DOMNormalizer;
import com.sun.org.apache.xerces.internal.dom.DOMStringListImpl;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMStringList;
import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.ls.LSSerializerFilter;



public class DOMSerializerImpl implements LSSerializer, DOMConfiguration {

    private XMLSerializer serializer;

    private XML11Serializer xml11Serializer;

    private DOMStringList fRecognizedParameters;


    protected short features = 0;

    protected final static short NAMESPACES          = 0x1<<0;
    protected final static short WELLFORMED          = 0x1<<1;
    protected final static short ENTITIES            = 0x1<<2;
    protected final static short CDATA               = 0x1<<3;
    protected final static short SPLITCDATA          = 0x1<<4;
    protected final static short COMMENTS            = 0x1<<5;
    protected final static short DISCARDDEFAULT      = 0x1<<6;
    protected final static short INFOSET             = 0x1<<7;
    protected final static short XMLDECL             = 0x1<<8;
    protected final static short NSDECL              = 0x1<<9;
    protected final static short DOM_ELEMENT_CONTENT_WHITESPACE = 0x1<<10;
    protected final static short FORMAT_PRETTY_PRINT = 0x1<<11;

    private DOMErrorHandler fErrorHandler = null;
    private final DOMErrorImpl fError = new DOMErrorImpl();
    private final DOMLocatorImpl fLocator = new DOMLocatorImpl();


    public DOMSerializerImpl() {
        features |= NAMESPACES;
        features |= ENTITIES;
        features |= COMMENTS;
        features |= CDATA;
        features |= SPLITCDATA;
        features |= WELLFORMED;
        features |= NSDECL;
        features |= DOM_ELEMENT_CONTENT_WHITESPACE;
        features |= DISCARDDEFAULT;
        features |= XMLDECL;

        serializer = new XMLSerializer();
        initSerializer(serializer);
    }



    public DOMConfiguration getDomConfig(){
        return this;
    }


    public void setParameter(String name, Object value) throws DOMException {
        if (value instanceof Boolean) {
            boolean state = ((Boolean) value).booleanValue();
            if (name.equalsIgnoreCase(Constants.DOM_INFOSET)){
                if (state){
                    features &= ~ENTITIES;
                    features &= ~CDATA;
                    features |= NAMESPACES;
                    features |= NSDECL;
                    features |= WELLFORMED;
                    features |= COMMENTS;
                }
                } else if (name.equalsIgnoreCase(Constants.DOM_XMLDECL)) {
                features =
                    (short) (state ? features | XMLDECL : features & ~XMLDECL);
            } else if (name.equalsIgnoreCase(Constants.DOM_NAMESPACES)) {
                features =
                    (short) (state
                        ? features | NAMESPACES
                        : features & ~NAMESPACES);
                serializer.fNamespaces = state;
            } else if (name.equalsIgnoreCase(Constants.DOM_SPLIT_CDATA)) {
                features =
                    (short) (state
                        ? features | SPLITCDATA
                        : features & ~SPLITCDATA);
            } else if (name.equalsIgnoreCase(Constants.DOM_DISCARD_DEFAULT_CONTENT)) {
                features =
                    (short) (state
                        ? features | DISCARDDEFAULT
                        : features & ~DISCARDDEFAULT);
            } else if (name.equalsIgnoreCase(Constants.DOM_WELLFORMED)) {
                features =
                    (short) (state
                        ? features | WELLFORMED
                        : features & ~WELLFORMED);
            } else if (name.equalsIgnoreCase(Constants.DOM_ENTITIES)){
                features =
                    (short) (state
                        ? features | ENTITIES
                        : features & ~ENTITIES);
            }
            else if (name.equalsIgnoreCase(Constants.DOM_CDATA_SECTIONS)){
                features =
                    (short) (state
                        ? features | CDATA
                        : features & ~CDATA);
                        }
            else if (name.equalsIgnoreCase(Constants.DOM_COMMENTS)){
                features =
                     (short) (state
                         ? features | COMMENTS
                         : features & ~COMMENTS);
            }
            else if (name.equalsIgnoreCase(Constants.DOM_FORMAT_PRETTY_PRINT)){
                features =
                     (short) (state
                         ? features | FORMAT_PRETTY_PRINT
                         : features & ~FORMAT_PRETTY_PRINT);
            }
                else if (name.equalsIgnoreCase(Constants.DOM_CANONICAL_FORM)
                    || name.equalsIgnoreCase(Constants.DOM_VALIDATE_IF_SCHEMA)
                    || name.equalsIgnoreCase(Constants.DOM_VALIDATE)
                    || name.equalsIgnoreCase(Constants.DOM_CHECK_CHAR_NORMALIZATION)
                    || name.equalsIgnoreCase(Constants.DOM_DATATYPE_NORMALIZATION)) {
                if (state) {
                    String msg =
                        DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "FEATURE_NOT_SUPPORTED",
                            new Object[] { name });
                    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
                }
            }else if (
                        name.equalsIgnoreCase(Constants.DOM_NAMESPACE_DECLARATIONS)) {
                                features =
                                        (short) (state
                                                ? features | NSDECL
                                                : features & ~NSDECL);
                                serializer.fNamespacePrefixes = state;
            } else if (name.equalsIgnoreCase(Constants.DOM_ELEMENT_CONTENT_WHITESPACE)
                    || name.equalsIgnoreCase(Constants.DOM_IGNORE_UNKNOWN_CHARACTER_DENORMALIZATIONS)) {
                if (!state) {
                    String msg =
                        DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "FEATURE_NOT_SUPPORTED",
                            new Object[] { name });
                    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
                }
            } else {
                String msg =
                    DOMMessageFormatter.formatMessage(
                        DOMMessageFormatter.DOM_DOMAIN,
                        "FEATURE_NOT_FOUND",
                        new Object[] { name });
                throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
            }
        } else if (name.equalsIgnoreCase(Constants.DOM_ERROR_HANDLER)) {
            if (value == null || value instanceof DOMErrorHandler) {
                fErrorHandler = (DOMErrorHandler)value;
            } else {
                String msg =
                    DOMMessageFormatter.formatMessage(
                        DOMMessageFormatter.DOM_DOMAIN,
                        "TYPE_MISMATCH_ERR",
                        new Object[] { name });
                throw new DOMException(DOMException.TYPE_MISMATCH_ERR, msg);
            }
        } else if (
            name.equalsIgnoreCase(Constants.DOM_RESOURCE_RESOLVER)
                || name.equalsIgnoreCase(Constants.DOM_SCHEMA_LOCATION)
                || name.equalsIgnoreCase(Constants.DOM_SCHEMA_TYPE)
                || name.equalsIgnoreCase(Constants.DOM_NORMALIZE_CHARACTERS)
                && value != null) {
            String msg =
                DOMMessageFormatter.formatMessage(
                    DOMMessageFormatter.DOM_DOMAIN,
                    "FEATURE_NOT_SUPPORTED",
                    new Object[] { name });
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
        } else {
            String msg =
                DOMMessageFormatter.formatMessage(
                    DOMMessageFormatter.DOM_DOMAIN,
                    "FEATURE_NOT_FOUND",
                    new Object[] { name });
            throw new DOMException(DOMException.NOT_FOUND_ERR, msg);
        }
    }


    public boolean canSetParameter(String name, Object state) {

        if (state == null) {
            return true;
        }

        if (state instanceof Boolean) {
            boolean value = ((Boolean) state).booleanValue();

            if (name.equalsIgnoreCase(Constants.DOM_NAMESPACES)
                || name.equalsIgnoreCase(Constants.DOM_SPLIT_CDATA)
                || name.equalsIgnoreCase(Constants.DOM_DISCARD_DEFAULT_CONTENT)
                || name.equalsIgnoreCase(Constants.DOM_XMLDECL)
                || name.equalsIgnoreCase(Constants.DOM_WELLFORMED)
                || name.equalsIgnoreCase(Constants.DOM_INFOSET)
                || name.equalsIgnoreCase(Constants.DOM_ENTITIES)
                || name.equalsIgnoreCase(Constants.DOM_CDATA_SECTIONS)
                || name.equalsIgnoreCase(Constants.DOM_COMMENTS)
                || name.equalsIgnoreCase(Constants.DOM_NAMESPACE_DECLARATIONS)
                || name.equalsIgnoreCase(Constants.DOM_FORMAT_PRETTY_PRINT)) {
                return true;
            } else if (name.equalsIgnoreCase(Constants.DOM_CANONICAL_FORM)
                || name.equalsIgnoreCase(Constants.DOM_VALIDATE_IF_SCHEMA)
                || name.equalsIgnoreCase(Constants.DOM_VALIDATE)
                || name.equalsIgnoreCase(Constants.DOM_CHECK_CHAR_NORMALIZATION)
                || name.equalsIgnoreCase(Constants.DOM_DATATYPE_NORMALIZATION)) {
                return !value;
            } else if (name.equalsIgnoreCase(Constants.DOM_ELEMENT_CONTENT_WHITESPACE)
                || name.equalsIgnoreCase(Constants.DOM_IGNORE_UNKNOWN_CHARACTER_DENORMALIZATIONS)) {
                return value;
            }
        } else if (name.equalsIgnoreCase(Constants.DOM_ERROR_HANDLER) &&
            state == null || state instanceof DOMErrorHandler) {
            return true;
        }

        return false;
    }


    public DOMStringList getParameterNames() {

        if (fRecognizedParameters == null){
                        Vector parameters = new Vector();

                        parameters.add(Constants.DOM_NAMESPACES);
                        parameters.add(Constants.DOM_SPLIT_CDATA);
                        parameters.add(Constants.DOM_DISCARD_DEFAULT_CONTENT);
                        parameters.add(Constants.DOM_XMLDECL);
                        parameters.add(Constants.DOM_CANONICAL_FORM);
                        parameters.add(Constants.DOM_VALIDATE_IF_SCHEMA);
                        parameters.add(Constants.DOM_VALIDATE);
                        parameters.add(Constants.DOM_CHECK_CHAR_NORMALIZATION);
                        parameters.add(Constants.DOM_DATATYPE_NORMALIZATION);
                        parameters.add(Constants.DOM_FORMAT_PRETTY_PRINT);
                        parameters.add(Constants.DOM_WELLFORMED);
                        parameters.add(Constants.DOM_INFOSET);
                        parameters.add(Constants.DOM_NAMESPACE_DECLARATIONS);
                        parameters.add(Constants.DOM_ELEMENT_CONTENT_WHITESPACE);
                        parameters.add(Constants.DOM_ENTITIES);
                        parameters.add(Constants.DOM_CDATA_SECTIONS);
                        parameters.add(Constants.DOM_COMMENTS);
                        parameters.add(Constants.DOM_IGNORE_UNKNOWN_CHARACTER_DENORMALIZATIONS);
                        parameters.add(Constants.DOM_ERROR_HANDLER);
                        fRecognizedParameters = new DOMStringListImpl(parameters);

        }

        return fRecognizedParameters;
    }


    public Object getParameter(String name) throws DOMException {

        if(name.equalsIgnoreCase(Constants.DOM_NORMALIZE_CHARACTERS)){
                      return null;
        } else if (name.equalsIgnoreCase(Constants.DOM_COMMENTS)) {
            return ((features & COMMENTS) != 0) ? Boolean.TRUE : Boolean.FALSE;
        } else if (name.equalsIgnoreCase(Constants.DOM_NAMESPACES)) {
            return (features & NAMESPACES) != 0 ? Boolean.TRUE : Boolean.FALSE;
        } else if (name.equalsIgnoreCase(Constants.DOM_XMLDECL)) {
            return (features & XMLDECL) != 0 ? Boolean.TRUE : Boolean.FALSE;
        } else if (name.equalsIgnoreCase(Constants.DOM_CDATA_SECTIONS)) {
            return (features & CDATA) != 0 ? Boolean.TRUE : Boolean.FALSE;
        } else if (name.equalsIgnoreCase(Constants.DOM_ENTITIES)) {
            return (features & ENTITIES) != 0 ? Boolean.TRUE : Boolean.FALSE;
        } else if (name.equalsIgnoreCase(Constants.DOM_SPLIT_CDATA)) {
            return (features & SPLITCDATA) != 0 ? Boolean.TRUE : Boolean.FALSE;
        } else if (name.equalsIgnoreCase(Constants.DOM_WELLFORMED)) {
            return (features & WELLFORMED) != 0 ? Boolean.TRUE : Boolean.FALSE;
        } else if (name.equalsIgnoreCase(Constants.DOM_NAMESPACE_DECLARATIONS)) {
            return (features & NSDECL) != 0 ? Boolean.TRUE : Boolean.FALSE;
        } else if (name.equalsIgnoreCase(Constants.DOM_FORMAT_PRETTY_PRINT)) {
            return (features & FORMAT_PRETTY_PRINT) != 0 ? Boolean.TRUE : Boolean.FALSE;
        } else if (name.equalsIgnoreCase(Constants.DOM_ELEMENT_CONTENT_WHITESPACE) ||
                   name.equalsIgnoreCase(Constants.DOM_IGNORE_UNKNOWN_CHARACTER_DENORMALIZATIONS)) {
            return Boolean.TRUE;
        }else if (name.equalsIgnoreCase(Constants.DOM_DISCARD_DEFAULT_CONTENT)){
            return ((features & DISCARDDEFAULT)!=0)?Boolean.TRUE:Boolean.FALSE;
        }else if (name.equalsIgnoreCase(Constants.DOM_INFOSET)){
            if ((features & ENTITIES) == 0 &&
                 (features & CDATA) == 0 &&
                 (features & NAMESPACES) != 0 &&
                 (features & NSDECL) != 0 &&
                 (features & WELLFORMED) != 0 &&
                 (features & COMMENTS) != 0) {
                     return Boolean.TRUE;
                 }
                 return Boolean.FALSE;
        } else if (name.equalsIgnoreCase(Constants.DOM_CANONICAL_FORM)
                || name.equalsIgnoreCase(Constants.DOM_VALIDATE_IF_SCHEMA)
                || name.equalsIgnoreCase(Constants.DOM_CHECK_CHAR_NORMALIZATION)
                || name.equalsIgnoreCase(Constants.DOM_VALIDATE)
                || name.equalsIgnoreCase(Constants.DOM_VALIDATE_IF_SCHEMA)
                || name.equalsIgnoreCase(Constants.DOM_DATATYPE_NORMALIZATION)) {
            return Boolean.FALSE;
        } else if (name.equalsIgnoreCase(Constants.DOM_ERROR_HANDLER)) {
            return fErrorHandler;
        } else if (
            name.equalsIgnoreCase(Constants.DOM_RESOURCE_RESOLVER)
                || name.equalsIgnoreCase(Constants.DOM_SCHEMA_LOCATION)
                || name.equalsIgnoreCase(Constants.DOM_SCHEMA_TYPE)) {
            String msg =
                DOMMessageFormatter.formatMessage(
                    DOMMessageFormatter.DOM_DOMAIN,
                    "FEATURE_NOT_SUPPORTED",
                    new Object[] { name });
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
        } else {
            String msg =
                DOMMessageFormatter.formatMessage(
                    DOMMessageFormatter.DOM_DOMAIN,
                    "FEATURE_NOT_FOUND",
                    new Object[] { name });
            throw new DOMException(DOMException.NOT_FOUND_ERR, msg);
        }
    }



    public String writeToString(Node wnode) throws DOMException, LSException {
        Document doc = (wnode.getNodeType() == Node.DOCUMENT_NODE)?(Document)wnode:wnode.getOwnerDocument();
        Method getVersion = null;
        XMLSerializer ser = null;
        String ver = null;
        try {
            getVersion = doc.getClass().getMethod("getXmlVersion", new Class[]{});
            if(getVersion != null ) {
                ver = (String)getVersion.invoke(doc, (Object[]) null);
            }
        } catch (Exception e) {
            }
        if(ver != null && ver.equals("1.1")) {
            if(xml11Serializer == null) {
                xml11Serializer = new XML11Serializer();
                initSerializer(xml11Serializer);
            }
            copySettings(serializer, xml11Serializer);
            ser = xml11Serializer;
        } else {
            ser = serializer;
        }

        StringWriter destination = new StringWriter();
        try {
            prepareForSerialization(ser, wnode);
            ser._format.setEncoding("UTF-16");
            ser.setOutputCharStream(destination);
            if (wnode.getNodeType() == Node.DOCUMENT_NODE) {
                ser.serialize((Document)wnode);
            }
            else if (wnode.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE) {
                ser.serialize((DocumentFragment)wnode);
            }
            else if (wnode.getNodeType() == Node.ELEMENT_NODE) {
                ser.serialize((Element)wnode);
            }
            else if (wnode.getNodeType() == Node.TEXT_NODE ||
                    wnode.getNodeType() == Node.COMMENT_NODE ||
                    wnode.getNodeType() == Node.ENTITY_REFERENCE_NODE ||
                    wnode.getNodeType() == Node.CDATA_SECTION_NODE ||
                    wnode.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE ) {
                ser.serialize(wnode);
            }
            else {
                String msg = DOMMessageFormatter.formatMessage(
                    DOMMessageFormatter.SERIALIZER_DOMAIN,
                    "unable-to-serialize-node", null);
                if (ser.fDOMErrorHandler != null) {
                    DOMErrorImpl error = new DOMErrorImpl();
                    error.fType = "unable-to-serialize-node";
                    error.fMessage = msg;
                    error.fSeverity = DOMError.SEVERITY_FATAL_ERROR;
                    ser.fDOMErrorHandler.handleError(error);
                }
                throw new LSException(LSException.SERIALIZE_ERR, msg);
            }
        } catch (LSException lse) {
            throw lse;
        } catch (AbortException e) {
            return null;
        } catch (RuntimeException e) {
            throw (LSException) new LSException(LSException.SERIALIZE_ERR, e.toString()).initCause(e);
        } catch (IOException ioe) {
            String msg = DOMMessageFormatter.formatMessage(
                DOMMessageFormatter.DOM_DOMAIN,
                "STRING_TOO_LONG",
                new Object[] { ioe.getMessage()});
            throw (DOMException) new DOMException(DOMException.DOMSTRING_SIZE_ERR, msg).initCause(ioe);
        }

        return destination.toString();
    }


    public void setNewLine(String newLine) {
        serializer._format.setLineSeparator(newLine);
    }



    public String getNewLine() {
        return serializer._format.getLineSeparator();
    }



    public LSSerializerFilter getFilter(){
        return serializer.fDOMFilter;
    }

    public void setFilter(LSSerializerFilter filter){
        serializer.fDOMFilter = filter;
    }

    private void initSerializer(XMLSerializer ser) {
        ser.fNSBinder = new NamespaceSupport();
        ser.fLocalNSBinder = new NamespaceSupport();
        ser.fSymbolTable = new SymbolTable();
    }

    private void copySettings(XMLSerializer src, XMLSerializer dest) {
        dest.fDOMErrorHandler = fErrorHandler;
        dest._format.setEncoding(src._format.getEncoding());
        dest._format.setLineSeparator(src._format.getLineSeparator());
        dest.fDOMFilter = src.fDOMFilter;
    }public boolean write(Node node, LSOutput destination) throws LSException{

        if (node == null)
            return false;

        Method getVersion = null;
        XMLSerializer ser = null;
        String ver = null;
        Document fDocument =(node.getNodeType() == Node.DOCUMENT_NODE)
                ? (Document) node
                : node.getOwnerDocument();
        try {
            getVersion = fDocument.getClass().getMethod("getXmlVersion", new Class[] {});
            if (getVersion != null) {
                ver = (String) getVersion.invoke(fDocument, (Object[]) null);
            }
        } catch (Exception e) {
            }
        if (ver != null && ver.equals("1.1")) {
            if (xml11Serializer == null) {
                xml11Serializer = new XML11Serializer();
                initSerializer(xml11Serializer);
            }
            copySettings(serializer, xml11Serializer);
            ser = xml11Serializer;
        } else {
            ser = serializer;
        }

        String encoding = null;
        if ((encoding = destination.getEncoding()) == null) {
            try {
                Method getEncoding =
                    fDocument.getClass().getMethod("getInputEncoding", new Class[] {});
                if (getEncoding != null) {
                    encoding = (String) getEncoding.invoke(fDocument, (Object[]) null);
                }
            } catch (Exception e) {
                }
            if (encoding == null) {
                try {
                    Method getEncoding =
                        fDocument.getClass().getMethod("getXmlEncoding", new Class[] {});
                    if (getEncoding != null) {
                        encoding = (String) getEncoding.invoke(fDocument, (Object[]) null);
                    }
                } catch (Exception e) {
                    }
                if (encoding == null) {
                    encoding = "UTF-8";
                }
            }
        }
        try {
            prepareForSerialization(ser, node);
            ser._format.setEncoding(encoding);
            OutputStream outputStream = destination.getByteStream();
            Writer writer = destination.getCharacterStream();
            String uri =  destination.getSystemId();
            if (writer == null) {
                if (outputStream == null) {
                    if (uri == null) {
                        String msg = DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.SERIALIZER_DOMAIN,
                            "no-output-specified", null);
                        if (ser.fDOMErrorHandler != null) {
                            DOMErrorImpl error = new DOMErrorImpl();
                            error.fType = "no-output-specified";
                            error.fMessage = msg;
                            error.fSeverity = DOMError.SEVERITY_FATAL_ERROR;
                            ser.fDOMErrorHandler.handleError(error);
                        }
                        throw new LSException(LSException.SERIALIZE_ERR, msg);
                    }
                    else {
                        String expanded = XMLEntityManager.expandSystemId(uri, null, true);
                        URL url = new URL(expanded != null ? expanded : uri);
                        OutputStream out = null;
                        String protocol = url.getProtocol();
                        String host = url.getHost();
                        if (protocol.equals("file")
                            && (host == null || host.length() == 0 || host.equals("localhost"))) {
                            out = new FileOutputStream(getPathWithoutEscapes(url.getFile()));
                        }
                        else {
                            URLConnection urlCon = url.openConnection();
                            urlCon.setDoInput(false);
                            urlCon.setDoOutput(true);
                            urlCon.setUseCaches(false); if (urlCon instanceof HttpURLConnection) {
                                HttpURLConnection httpCon = (HttpURLConnection) urlCon;
                                httpCon.setRequestMethod("PUT");
                            }
                            out = urlCon.getOutputStream();
                        }
                        ser.setOutputByteStream(out);
                    }
                }
                else {
                    ser.setOutputByteStream(outputStream);
                }
            }
            else {
                ser.setOutputCharStream(writer);
            }

            if (node.getNodeType() == Node.DOCUMENT_NODE)
                ser.serialize((Document) node);
            else if (node.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE)
                ser.serialize((DocumentFragment) node);
            else if (node.getNodeType() == Node.ELEMENT_NODE)
                ser.serialize((Element) node);
            else if (node.getNodeType() == Node.TEXT_NODE ||
                    node.getNodeType() == Node.COMMENT_NODE ||
                    node.getNodeType() == Node.ENTITY_REFERENCE_NODE ||
                    node.getNodeType() == Node.CDATA_SECTION_NODE ||
                    node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE ) {
                ser.serialize(node);
            }
            else
                return false;
        } catch( UnsupportedEncodingException ue) {
            if (ser.fDOMErrorHandler != null) {
                DOMErrorImpl error = new DOMErrorImpl();
                error.fException = ue;
                                error.fType = "unsupported-encoding";
                error.fMessage = ue.getMessage();
                                error.fSeverity = DOMError.SEVERITY_FATAL_ERROR;
                ser.fDOMErrorHandler.handleError(error);
                        }
            throw new LSException(LSException.SERIALIZE_ERR,
                DOMMessageFormatter.formatMessage(
                    DOMMessageFormatter.SERIALIZER_DOMAIN,
                    "unsupported-encoding", null));
                        } catch (LSException lse) {
            throw lse;
        } catch (AbortException e) {
            return false;
        } catch (RuntimeException e) {
            throw (LSException) DOMUtil.createLSException(LSException.SERIALIZE_ERR, e).fillInStackTrace();
        } catch (Exception e) {
            if (ser.fDOMErrorHandler != null) {
                DOMErrorImpl error = new DOMErrorImpl();
                error.fException = e;
                error.fMessage = e.getMessage();
                error.fSeverity = DOMError.SEVERITY_ERROR;
                ser.fDOMErrorHandler.handleError(error);

            }
            throw (LSException) DOMUtil.createLSException(LSException.SERIALIZE_ERR, e).fillInStackTrace();
        }
        return true;

    } public boolean writeToURI(Node node, String URI) throws LSException{
        if (node == null){
            return false;
        }

        Method getXmlVersion = null;
        XMLSerializer ser = null;
        String ver = null;
        String encoding = null;

        Document fDocument =(node.getNodeType() == Node.DOCUMENT_NODE)
                ? (Document) node
                : node.getOwnerDocument();
        try {
            getXmlVersion =
                fDocument.getClass().getMethod("getXmlVersion", new Class[] {});
            if (getXmlVersion != null) {
                ver = (String) getXmlVersion.invoke(fDocument, (Object[]) null);
            }
        } catch (Exception e) {
            }
        if (ver != null && ver.equals("1.1")) {
            if (xml11Serializer == null) {
                xml11Serializer = new XML11Serializer();
                initSerializer(xml11Serializer);
            }
            copySettings(serializer, xml11Serializer);
            ser = xml11Serializer;
        } else {
            ser = serializer;
        }

        try {
            Method getEncoding =
                fDocument.getClass().getMethod("getInputEncoding", new Class[] {});
            if (getEncoding != null) {
                encoding = (String) getEncoding.invoke(fDocument, (Object[]) null);
            }
        } catch (Exception e) {
            }
        if (encoding == null) {
            try {
                Method getEncoding =
                    fDocument.getClass().getMethod("getXmlEncoding", new Class[] {});
                if (getEncoding != null) {
                    encoding = (String) getEncoding.invoke(fDocument, (Object[]) null);
                }
            } catch (Exception e) {
                }
            if (encoding == null) {
                encoding = "UTF-8";
            }
        }

        try {
            prepareForSerialization(ser, node);
            ser._format.setEncoding(encoding);

            String expanded = XMLEntityManager.expandSystemId(URI, null, true);
            URL url = new URL(expanded != null ? expanded : URI);
            OutputStream out = null;
            String protocol = url.getProtocol();
            String host = url.getHost();
            if (protocol.equals("file")
                && (host == null || host.length() == 0 || host.equals("localhost"))) {
                out = new FileOutputStream(getPathWithoutEscapes(url.getFile()));
            }
            else {
                URLConnection urlCon = url.openConnection();
                urlCon.setDoInput(false);
                urlCon.setDoOutput(true);
                urlCon.setUseCaches(false); if (urlCon instanceof HttpURLConnection) {
                    HttpURLConnection httpCon = (HttpURLConnection) urlCon;
                    httpCon.setRequestMethod("PUT");
                }
                out = urlCon.getOutputStream();
            }
            ser.setOutputByteStream(out);

            if (node.getNodeType() == Node.DOCUMENT_NODE)
                ser.serialize((Document) node);
            else if (node.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE)
                ser.serialize((DocumentFragment) node);
            else if (node.getNodeType() == Node.ELEMENT_NODE)
                ser.serialize((Element) node);
            else if (node.getNodeType() == Node.TEXT_NODE ||
                    node.getNodeType() == Node.COMMENT_NODE ||
                    node.getNodeType() == Node.ENTITY_REFERENCE_NODE ||
                    node.getNodeType() == Node.CDATA_SECTION_NODE ||
                    node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE ) {
                ser.serialize(node);
            }
            else
                return false;
        } catch (LSException lse) {
            throw lse;
        } catch (AbortException e) {
            return false;
        } catch (RuntimeException e) {
            throw (LSException) DOMUtil.createLSException(LSException.SERIALIZE_ERR, e).fillInStackTrace();
        } catch (Exception e) {
            if (ser.fDOMErrorHandler != null) {
                DOMErrorImpl error = new DOMErrorImpl();
                error.fException = e;
                error.fMessage = e.getMessage();
                error.fSeverity = DOMError.SEVERITY_ERROR;
                ser.fDOMErrorHandler.handleError(error);
            }
            throw (LSException) DOMUtil.createLSException(LSException.SERIALIZE_ERR, e).fillInStackTrace();
        }
        return true;
    } private void prepareForSerialization(XMLSerializer ser, Node node) {
        ser.reset();
        ser.features = features;
        ser.fDOMErrorHandler = fErrorHandler;
        ser.fNamespaces = (features & NAMESPACES) != 0;
        ser.fNamespacePrefixes = (features & NSDECL) != 0;
        ser._format.setOmitComments((features & COMMENTS)==0);
        ser._format.setOmitXMLDeclaration((features & XMLDECL) == 0);
        ser._format.setIndenting((features & FORMAT_PRETTY_PRINT) != 0);

        if ((features & WELLFORMED) != 0) {
            Node next, root;
            root = node;
            Method versionChanged;
            boolean verifyNames = true;
            Document document =(node.getNodeType() == Node.DOCUMENT_NODE)
                    ? (Document) node
                    : node.getOwnerDocument();
            try {
                versionChanged = document.getClass().getMethod("isXMLVersionChanged()", new Class[] {});
                if (versionChanged != null) {
                    verifyNames = ((Boolean)versionChanged.invoke(document, (Object[]) null)).booleanValue();
                }
            } catch (Exception e) {
                }
            if (node.getFirstChild() != null) {
                while (node != null) {
                    verify(node, verifyNames, false);
                    next = node.getFirstChild();
                    while (next == null) {
                      next = node.getNextSibling();
                      if (next == null) {
                          node = node.getParentNode();
                          if (root == node){
                              next = null;
                              break;
                          }
                          next = node.getNextSibling();
                      }
                    }
                    node = next;
                }
            }
            else {
                verify(node, verifyNames, false);
            }
        }
    }


    private void verify (Node node, boolean verifyNames, boolean xml11Version){

        int type = node.getNodeType();
        fLocator.fRelatedNode = node;
        boolean wellformed;
        switch (type) {
            case Node.DOCUMENT_NODE:{
                break;
            }
            case Node.DOCUMENT_TYPE_NODE:{
                break;
            }
            case Node.ELEMENT_NODE:{
                if (verifyNames){
                    if((features & NAMESPACES) != 0){
                        wellformed = CoreDocumentImpl.isValidQName(node.getPrefix() , node.getLocalName(), xml11Version) ;
                    }
                    else{
                        wellformed = CoreDocumentImpl.isXMLName(node.getNodeName() , xml11Version);
                    }
                    if (!wellformed){
                            if (!wellformed){
                                if (fErrorHandler != null) {
                                    String msg = DOMMessageFormatter.formatMessage(
                                        DOMMessageFormatter.DOM_DOMAIN,
                                        "wf-invalid-character-in-node-name",
                                        new Object[]{"Element", node.getNodeName()});
                                        DOMNormalizer.reportDOMError(fErrorHandler, fError, fLocator, msg, DOMError.SEVERITY_FATAL_ERROR,
                                        "wf-invalid-character-in-node-name");
                                }

                            }
                    }
                }

                NamedNodeMap attributes = (node.hasAttributes()) ? node.getAttributes() : null;
                if (attributes != null) {
                    for (int i = 0; i < attributes.getLength(); ++i) {
                        Attr attr = (Attr) attributes.item(i);
                        fLocator.fRelatedNode = attr;
                        DOMNormalizer.isAttrValueWF( fErrorHandler, fError, fLocator,
                                      attributes, attr, attr.getValue(), xml11Version);
                        if (verifyNames) {
                            wellformed = CoreDocumentImpl.isXMLName( attr.getNodeName(), xml11Version);
                            if (!wellformed) {
                                    String msg =
                                        DOMMessageFormatter.formatMessage(
                                            DOMMessageFormatter.DOM_DOMAIN,
                                            "wf-invalid-character-in-node-name",
                                            new Object[] { "Attr", node.getNodeName()});
                                    DOMNormalizer.reportDOMError( fErrorHandler, fError, fLocator, msg, DOMError.SEVERITY_FATAL_ERROR,
                                        "wf-invalid-character-in-node-name");
                            }
                        }
                    }

                }

                break;
            }

        case Node.COMMENT_NODE: {
            if ((features & COMMENTS) != 0)
                DOMNormalizer.isCommentWF(fErrorHandler, fError, fLocator, ((Comment)node).getData(), xml11Version);
            break;
        }
        case Node.ENTITY_REFERENCE_NODE: {
            if (verifyNames && (features & ENTITIES) != 0){
                CoreDocumentImpl.isXMLName(node.getNodeName() , xml11Version);
            }
            break;

        }
        case Node.CDATA_SECTION_NODE: {
            DOMNormalizer.isXMLCharWF(fErrorHandler, fError, fLocator, node.getNodeValue(), xml11Version);
            break;
        }
        case Node.TEXT_NODE:{
            DOMNormalizer.isXMLCharWF(fErrorHandler, fError, fLocator, node.getNodeValue(), xml11Version);
            break;
        }
        case Node.PROCESSING_INSTRUCTION_NODE:{
            ProcessingInstruction pinode = (ProcessingInstruction)node ;
            String target = pinode.getTarget();
            if (verifyNames) {
                if (xml11Version) {
                    wellformed = XML11Char.isXML11ValidName(target);
                } else {
                    wellformed = XMLChar.isValidName(target);
                }

                if (!wellformed) {
                    String msg =
                        DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "wf-invalid-character-in-node-name",
                            new Object[] { "Element", node.getNodeName()});
                    DOMNormalizer.reportDOMError(
                        fErrorHandler,
                        fError,
                        fLocator,
                        msg,
                        DOMError.SEVERITY_FATAL_ERROR,
                        "wf-invalid-character-in-node-name");
                }
            }
            DOMNormalizer.isXMLCharWF(fErrorHandler, fError, fLocator, pinode.getData(), xml11Version);
            break;
        }
        }

    }

    private String getPathWithoutEscapes(String origPath) {
        if (origPath != null && origPath.length() != 0 && origPath.indexOf('%') != -1) {
            StringTokenizer tokenizer = new StringTokenizer(origPath, "%");
            StringBuffer result = new StringBuffer(origPath.length());
            int size = tokenizer.countTokens();
            result.append(tokenizer.nextToken());
            for(int i = 1; i < size; ++i) {
                String token = tokenizer.nextToken();
                result.append((char)Integer.valueOf(token.substring(0, 2), 16).intValue());
                result.append(token.substring(2));
            }
            return result.toString();
        }
        return origPath;
    }

}