

package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.RevalidationHandler;
import com.sun.org.apache.xerces.internal.parsers.DOMParserImpl;
import com.sun.org.apache.xerces.internal.parsers.DTDConfiguration;
import com.sun.org.apache.xerces.internal.parsers.XIncludeAwareParserConfiguration;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xml.internal.serialize.DOMSerializerImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

public class CoreDOMImplementationImpl
        implements DOMImplementation, DOMImplementationLS {
        private static final int SIZE = 2;
    private RevalidationHandler validators[] = new RevalidationHandler[SIZE];

    private RevalidationHandler dtdValidators[] = new RevalidationHandler[SIZE];
    private int freeValidatorIndex = -1;
    private int freeDTDValidatorIndex = -1;
    private int currentSize = SIZE;

    private int docAndDoctypeCounter = 0;

        static CoreDOMImplementationImpl singleton =
                new CoreDOMImplementationImpl();
        public static DOMImplementation getDOMImplementation() {
                return singleton;
        }
        public boolean hasFeature(String feature, String version) {

            boolean anyVersion = version == null || version.length() == 0;

            if ((feature.equalsIgnoreCase("+XPath"))
                && (anyVersion || version.equals("3.0"))) {
                try {
                    Class xpathClass = ObjectFactory.findProviderClass(
                        "com.sun.org.apache.xpath.internal.domapi.XPathEvaluatorImpl", true);

                Class interfaces[] = xpathClass.getInterfaces();
                for (int i = 0; i < interfaces.length; i++) {
                    if (interfaces[i].getName().equals(
                        "org.w3c.dom.xpath.XPathEvaluator")) {
                        return true;
                    }
                }
                } catch (Exception e) {
                    return false;
                }
                return true;
            }
            if (feature.startsWith("+")) {
                feature = feature.substring(1);
            }
            return (
                feature.equalsIgnoreCase("Core")
                    && (anyVersion
                        || version.equals("1.0")
                        || version.equals("2.0")
                        || version.equals("3.0")))
                        || (feature.equalsIgnoreCase("XML")
                    && (anyVersion
                        || version.equals("1.0")
                        || version.equals("2.0")
                        || version.equals("3.0")))
                        || (feature.equalsIgnoreCase("LS")
                    && (anyVersion || version.equals("3.0")));
        } public DocumentType createDocumentType( String qualifiedName,
                                    String publicID, String systemID) {
                checkQName(qualifiedName);
                return new DocumentTypeImpl(null, qualifiedName, publicID, systemID);
        }

    final void checkQName(String qname){
        int index = qname.indexOf(':');
        int lastIndex = qname.lastIndexOf(':');
        int length = qname.length();

        if (index == 0 || index == length - 1 || lastIndex != index) {
            String msg =
                DOMMessageFormatter.formatMessage(
                    DOMMessageFormatter.DOM_DOMAIN,
                    "NAMESPACE_ERR",
                    null);
            throw new DOMException(DOMException.NAMESPACE_ERR, msg);
        }
        int start = 0;
        if (index > 0) {
            if (!XMLChar.isNCNameStart(qname.charAt(start))) {
                String msg =
                    DOMMessageFormatter.formatMessage(
                        DOMMessageFormatter.DOM_DOMAIN,
                        "INVALID_CHARACTER_ERR",
                        null);
                throw new DOMException(DOMException.INVALID_CHARACTER_ERR, msg);
            }
            for (int i = 1; i < index; i++) {
                if (!XMLChar.isNCName(qname.charAt(i))) {
                    String msg =
                        DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "INVALID_CHARACTER_ERR",
                            null);
                    throw new DOMException(
                        DOMException.INVALID_CHARACTER_ERR,
                        msg);
                }
            }
            start = index + 1;
        }

        if (!XMLChar.isNCNameStart(qname.charAt(start))) {
            String msg =
                DOMMessageFormatter.formatMessage(
                    DOMMessageFormatter.DOM_DOMAIN,
                    "INVALID_CHARACTER_ERR",
                    null);
            throw new DOMException(DOMException.INVALID_CHARACTER_ERR, msg);
        }
        for (int i = start + 1; i < length; i++) {
            if (!XMLChar.isNCName(qname.charAt(i))) {
                String msg =
                    DOMMessageFormatter.formatMessage(
                        DOMMessageFormatter.DOM_DOMAIN,
                        "INVALID_CHARACTER_ERR",
                        null);
                throw new DOMException(DOMException.INVALID_CHARACTER_ERR, msg);
            }
        }
    }



        public Document createDocument(
                String namespaceURI,
                String qualifiedName,
                DocumentType doctype)
                throws DOMException {
                if (doctype != null && doctype.getOwnerDocument() != null) {
                        String msg =
                                DOMMessageFormatter.formatMessage(
                                        DOMMessageFormatter.DOM_DOMAIN,
                                        "WRONG_DOCUMENT_ERR",
                                        null);
                        throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, msg);
                }
                CoreDocumentImpl doc = new CoreDocumentImpl(doctype);
                Element e = doc.createElementNS(namespaceURI, qualifiedName);
                doc.appendChild(e);
                return doc;
        }


        public Object getFeature(String feature, String version) {
            if (singleton.hasFeature(feature, version)) {
                if ((feature.equalsIgnoreCase("+XPath"))) {
                    try {
                        Class xpathClass = ObjectFactory.findProviderClass(
                            "com.sun.org.apache.xpath.internal.domapi.XPathEvaluatorImpl", true);
                        Class interfaces[] = xpathClass.getInterfaces();
                        for (int i = 0; i < interfaces.length; i++) {
                            if (interfaces[i].getName().equals(
                                "org.w3c.dom.xpath.XPathEvaluator")) {
                                return xpathClass.newInstance();
                            }
                        }
                    } catch (Exception e) {
                        return null;
                    }
                } else {
                    return singleton;
                }
            }
            return null;
        }

        public LSParser createLSParser(short mode, String schemaType)
                throws DOMException {
                if (mode != DOMImplementationLS.MODE_SYNCHRONOUS || (schemaType !=null &&
                   !"http:!"http:String msg =
                                DOMMessageFormatter.formatMessage(
                                        DOMMessageFormatter.DOM_DOMAIN,
                                        "NOT_SUPPORTED_ERR",
                                        null);
                        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
                }
                if (schemaType != null
                        && schemaType.equals("http:return new DOMParserImpl(new DTDConfiguration(),
                                schemaType);
                }
                else {
                        return new DOMParserImpl(new XIncludeAwareParserConfiguration(),
                                schemaType);
                }
        }


        public LSSerializer createLSSerializer() {
        return new DOMSerializerImpl();
    }

        public LSInput createLSInput() {
                return new DOMInputImpl();
        }

        synchronized RevalidationHandler getValidator(String schemaType) {
                if (schemaType == XMLGrammarDescription.XML_SCHEMA) {
            if(freeValidatorIndex < 0) {
                return (RevalidationHandler) (ObjectFactory
                            .newInstance(
                                "com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator",
                                ObjectFactory.findClassLoader(),
                                true));
            }
            RevalidationHandler val = validators[freeValidatorIndex];
            validators[freeValidatorIndex--] = null;
            return val;
        }
        else if(schemaType == XMLGrammarDescription.XML_DTD) {
            if(freeDTDValidatorIndex < 0) {
                return (RevalidationHandler) (ObjectFactory
                            .newInstance(
                                "com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidator",
                                ObjectFactory.findClassLoader(),
                                true));
            }
            RevalidationHandler val = dtdValidators[freeDTDValidatorIndex];
            dtdValidators[freeDTDValidatorIndex--] = null;
            return val;
        }
        return null;
        }


        synchronized void releaseValidator(String schemaType,
                                         RevalidationHandler validator) {
       if(schemaType == XMLGrammarDescription.XML_SCHEMA) {
           ++freeValidatorIndex;
           if (validators.length == freeValidatorIndex ){
                currentSize+=SIZE;
                RevalidationHandler newarray[] =  new RevalidationHandler[currentSize];
                System.arraycopy(validators, 0, newarray, 0, validators.length);
                validators = newarray;
           }
           validators[freeValidatorIndex]=validator;
       }
       else if(schemaType == XMLGrammarDescription.XML_DTD) {
           ++freeDTDValidatorIndex;
           if (dtdValidators.length == freeDTDValidatorIndex ){
                currentSize+=SIZE;
                RevalidationHandler newarray[] =  new RevalidationHandler[currentSize];
                System.arraycopy(dtdValidators, 0, newarray, 0, dtdValidators.length);
                dtdValidators = newarray;
           }
           dtdValidators[freeDTDValidatorIndex]=validator;
       }
        }


       protected synchronized int assignDocumentNumber() {
            return ++docAndDoctypeCounter;
       }

       protected synchronized int assignDocTypeNumber() {
            return ++docAndDoctypeCounter;
       }


       public LSOutput createLSOutput() {
           return new DOMOutputImpl();
       }

}