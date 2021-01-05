


package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.parsers.XML11Configuration;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import java.io.IOException;
import java.lang.ref.SoftReference;
import javax.xml.XMLConstants;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import jdk.xml.internal.JdkXmlUtils;
import org.xml.sax.SAXException;


final class StreamValidatorHelper implements ValidatorHelper {

    private static final String PARSER_SETTINGS =
        Constants.XERCES_FEATURE_PREFIX + Constants.PARSER_SETTINGS;

    private static final String ENTITY_RESOLVER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_RESOLVER_PROPERTY;


    private static final String ERROR_HANDLER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_HANDLER_PROPERTY;


    private static final String ERROR_REPORTER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;


    private static final String SCHEMA_VALIDATOR =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SCHEMA_VALIDATOR_PROPERTY;


    private static final String SYMBOL_TABLE =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;


    private static final String VALIDATION_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.VALIDATION_MANAGER_PROPERTY;


    private static final String SECURITY_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SECURITY_MANAGER_PROPERTY;

    private SoftReference fConfiguration = new SoftReference(null);


    private com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator fSchemaValidator;


    private XMLSchemaValidatorComponentManager fComponentManager;

    private ValidatorHandlerImpl handler = null;

    public StreamValidatorHelper(XMLSchemaValidatorComponentManager componentManager) {
        fComponentManager = componentManager;
        fSchemaValidator = (com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator) fComponentManager.getProperty(SCHEMA_VALIDATOR);
    }

    public void validate(Source source, Result result)
        throws SAXException, IOException {
        if (result == null || result instanceof StreamResult) {
            final StreamSource streamSource = (StreamSource) source;
            TransformerHandler identityTransformerHandler ;

            if( result!=null ) {
                try {
                    SAXTransformerFactory tf = JdkXmlUtils.getSAXTransformFactory(
                            fComponentManager.getFeature(JdkXmlUtils.OVERRIDE_PARSER));

                    identityTransformerHandler = tf.newTransformerHandler();
                } catch (TransformerConfigurationException e) {
                    throw new TransformerFactoryConfigurationError(e);
                }

                handler = new ValidatorHandlerImpl(fComponentManager);
                handler.setContentHandler(identityTransformerHandler);
                identityTransformerHandler.setResult(result);
            }

            XMLInputSource input = new XMLInputSource(streamSource.getPublicId(), streamSource.getSystemId(), null);
            input.setByteStream(streamSource.getInputStream());
            input.setCharacterStream(streamSource.getReader());

            XMLParserConfiguration config = (XMLParserConfiguration) fConfiguration.get();
            if (config == null) {
                config = initialize();
            }
            else if (fComponentManager.getFeature(PARSER_SETTINGS)) {
                config.setProperty(ENTITY_RESOLVER, fComponentManager.getProperty(ENTITY_RESOLVER));
                config.setProperty(ERROR_HANDLER, fComponentManager.getProperty(ERROR_HANDLER));
            }

            fComponentManager.reset();
            fSchemaValidator.setDocumentHandler(handler);

            try {
                config.parse(input);
            }
            catch (XMLParseException e) {
                throw Util.toSAXParseException(e);
            }
            catch (XNIException e) {
                throw Util.toSAXException(e);
            }
            return;
        }
        throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(fComponentManager.getLocale(),
                "SourceResultMismatch",
                new Object [] {source.getClass().getName(), result.getClass().getName()}));
    }

    private XMLParserConfiguration initialize() {
        XML11Configuration config = new XML11Configuration();
        if (fComponentManager.getFeature(XMLConstants.FEATURE_SECURE_PROCESSING)) {
            config.setProperty(SECURITY_MANAGER, new XMLSecurityManager());
        }
        config.setProperty(ENTITY_RESOLVER, fComponentManager.getProperty(ENTITY_RESOLVER));
        config.setProperty(ERROR_HANDLER, fComponentManager.getProperty(ERROR_HANDLER));
        XMLErrorReporter errorReporter = (XMLErrorReporter) fComponentManager.getProperty(ERROR_REPORTER);
        config.setProperty(ERROR_REPORTER, errorReporter);
        if (errorReporter.getMessageFormatter(XMLMessageFormatter.XML_DOMAIN) == null) {
            XMLMessageFormatter xmft = new XMLMessageFormatter();
            errorReporter.putMessageFormatter(XMLMessageFormatter.XML_DOMAIN, xmft);
            errorReporter.putMessageFormatter(XMLMessageFormatter.XMLNS_DOMAIN, xmft);
        }
        config.setProperty(SYMBOL_TABLE, fComponentManager.getProperty(SYMBOL_TABLE));
        config.setProperty(VALIDATION_MANAGER, fComponentManager.getProperty(VALIDATION_MANAGER));
        config.setDocumentHandler(fSchemaValidator);
        config.setDTDHandler(null);
        config.setDTDContentModelHandler(null);
        config.setProperty(Constants.XML_SECURITY_PROPERTY_MANAGER,
                fComponentManager.getProperty(Constants.XML_SECURITY_PROPERTY_MANAGER));
        config.setProperty(Constants.SECURITY_MANAGER,
                fComponentManager.getProperty(Constants.SECURITY_MANAGER));
        fConfiguration = new SoftReference(config);
        return config;
    }

}