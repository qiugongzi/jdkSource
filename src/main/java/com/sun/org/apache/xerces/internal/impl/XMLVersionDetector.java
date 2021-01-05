


package com.sun.org.apache.xerces.internal.impl;

import java.io.EOFException;
import java.io.IOException;

import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.xml.internal.stream.Entity.ScannedEntity;


public class XMLVersionDetector {

    private final static char[] XML11_VERSION = new char[]{'1', '.', '1'};


    protected static final String SYMBOL_TABLE =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SYMBOL_TABLE_PROPERTY;


    protected static final String ERROR_REPORTER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ERROR_REPORTER_PROPERTY;


    protected static final String ENTITY_MANAGER =
        Constants.XERCES_PROPERTY_PREFIX + Constants.ENTITY_MANAGER_PROPERTY;

    protected final static String fVersionSymbol = "version".intern();

    protected static final String fXMLSymbol = "[xml]".intern();


    protected SymbolTable fSymbolTable;


    protected XMLErrorReporter fErrorReporter;


    protected XMLEntityManager fEntityManager;

    protected String fEncoding = null;

    private XMLString fVersionNum = new XMLString();

    private final char [] fExpectedVersionString = {'<', '?', 'x', 'm', 'l', ' ', 'v', 'e', 'r', 's',
                    'i', 'o', 'n', '=', ' ', ' ', ' ', ' ', ' '};


    public void reset(XMLComponentManager componentManager)
        throws XMLConfigurationException {

        fSymbolTable = (SymbolTable)componentManager.getProperty(SYMBOL_TABLE);
        fErrorReporter = (XMLErrorReporter)componentManager.getProperty(ERROR_REPORTER);
        fEntityManager = (XMLEntityManager)componentManager.getProperty(ENTITY_MANAGER);
        for(int i=14; i<fExpectedVersionString.length; i++ )
            fExpectedVersionString[i] = ' ';
    } public void startDocumentParsing(XMLEntityHandler scanner, short version){

        if (version == Constants.XML_VERSION_1_0){
            fEntityManager.setScannerVersion(Constants.XML_VERSION_1_0);
        }
        else {
            fEntityManager.setScannerVersion(Constants.XML_VERSION_1_1);
        }
        fErrorReporter.setDocumentLocator(fEntityManager.getEntityScanner());

        fEntityManager.setEntityHandler(scanner);

        scanner.startEntity(fXMLSymbol, fEntityManager.getCurrentResourceIdentifier(), fEncoding, null);
    }



    public short determineDocVersion(XMLInputSource inputSource) throws IOException {
        fEncoding = fEntityManager.setupCurrentEntity(false, fXMLSymbol, inputSource, false, true);

        fEntityManager.setScannerVersion(Constants.XML_VERSION_1_0);
        XMLEntityScanner scanner = fEntityManager.getEntityScanner();
        scanner.detectingVersion = true;
        try {
            if (!scanner.skipString("<?xml")) {
                scanner.detectingVersion = false;
                return Constants.XML_VERSION_1_0;
            }
            if (!scanner.skipDeclSpaces()) {
                fixupCurrentEntity(fEntityManager, fExpectedVersionString, 5);
                scanner.detectingVersion = false;
                return Constants.XML_VERSION_1_0;
            }
            if (!scanner.skipString("version")) {
                fixupCurrentEntity(fEntityManager, fExpectedVersionString, 6);
                scanner.detectingVersion = false;
                return Constants.XML_VERSION_1_0;
            }
            scanner.skipDeclSpaces();
            if (scanner.peekChar() != '=') {
                fixupCurrentEntity(fEntityManager, fExpectedVersionString, 13);
                scanner.detectingVersion = false;
                return Constants.XML_VERSION_1_0;
            }
            scanner.scanChar(null);
            scanner.skipDeclSpaces();
            int quoteChar = scanner.scanChar(null);
            fExpectedVersionString[14] = (char) quoteChar;
            for (int versionPos = 0; versionPos < XML11_VERSION.length; versionPos++) {
                fExpectedVersionString[15 + versionPos] = (char) scanner.scanChar(null);
            }
            fExpectedVersionString[18] = (char) scanner.scanChar(null);
            fixupCurrentEntity(fEntityManager, fExpectedVersionString, 19);
            int matched = 0;
            for (; matched < XML11_VERSION.length; matched++) {
                if (fExpectedVersionString[15 + matched] != XML11_VERSION[matched])
                    break;
            }
            scanner.detectingVersion = false;
            if (matched == XML11_VERSION.length)
                return Constants.XML_VERSION_1_1;
            return Constants.XML_VERSION_1_0;
            }
        catch (EOFException e) {
            fErrorReporter.reportError(
                XMLMessageFormatter.XML_DOMAIN,
                "PrematureEOF",
                null,
                XMLErrorReporter.SEVERITY_FATAL_ERROR);
            scanner.detectingVersion = false;
            return Constants.XML_VERSION_1_0;
        }
    }

    private void fixupCurrentEntity(XMLEntityManager manager,
                char [] scannedChars, int length) {
        ScannedEntity currentEntity = manager.getCurrentEntity();
        if(currentEntity.count-currentEntity.position+length > currentEntity.ch.length) {
            char[] tempCh = currentEntity.ch;
            currentEntity.ch = new char[length+currentEntity.count-currentEntity.position+1];
            System.arraycopy(tempCh, 0, currentEntity.ch, 0, tempCh.length);
        }
        if(currentEntity.position < length) {
            System.arraycopy(currentEntity.ch, currentEntity.position, currentEntity.ch, length, currentEntity.count-currentEntity.position);
            currentEntity.count += length-currentEntity.position;
        } else {
            for(int i=length; i<currentEntity.position; i++)
                currentEntity.ch[i]=' ';
        }
        System.arraycopy(scannedChars, 0, currentEntity.ch, 0, length);
        currentEntity.position = 0;
        currentEntity.baseCharOffset = 0;
        currentEntity.startPosition = 0;
        currentEntity.columnNumber = currentEntity.lineNumber = 1;
    }

}