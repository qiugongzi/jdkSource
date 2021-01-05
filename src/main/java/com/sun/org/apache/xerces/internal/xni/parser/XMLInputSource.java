


package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;

import java.io.InputStream;
import java.io.Reader;


public class XMLInputSource {

    protected String fPublicId;


    protected String fSystemId;


    protected String fBaseSystemId;


    protected InputStream fByteStream;


    protected Reader fCharStream;


    protected String fEncoding;

    public XMLInputSource(String publicId, String systemId,
                          String baseSystemId) {
        fPublicId = publicId;
        fSystemId = systemId;
        fBaseSystemId = baseSystemId;
    } public XMLInputSource(XMLResourceIdentifier resourceIdentifier) {

        fPublicId = resourceIdentifier.getPublicId();
        fSystemId = resourceIdentifier.getLiteralSystemId();
        fBaseSystemId = resourceIdentifier.getBaseSystemId();
    } public XMLInputSource(String publicId, String systemId,
                          String baseSystemId, InputStream byteStream,
                          String encoding) {
        fPublicId = publicId;
        fSystemId = systemId;
        fBaseSystemId = baseSystemId;
        fByteStream = byteStream;
        fEncoding = encoding;
    } public XMLInputSource(String publicId, String systemId,
                          String baseSystemId, Reader charStream,
                          String encoding) {
        fPublicId = publicId;
        fSystemId = systemId;
        fBaseSystemId = baseSystemId;
        fCharStream = charStream;
        fEncoding = encoding;
    } public void setPublicId(String publicId) {
        fPublicId = publicId;
    } public String getPublicId() {
        return fPublicId;
    } public void setSystemId(String systemId) {
        fSystemId = systemId;
    } public String getSystemId() {
        return fSystemId;
    } public void setBaseSystemId(String baseSystemId) {
        fBaseSystemId = baseSystemId;
    } public String getBaseSystemId() {
        return fBaseSystemId;
    } public void setByteStream(InputStream byteStream) {
        fByteStream = byteStream;
    } public InputStream getByteStream() {
        return fByteStream;
    } public void setCharacterStream(Reader charStream) {
        fCharStream = charStream;
    } public Reader getCharacterStream() {
        return fCharStream;
    } public void setEncoding(String encoding) {
        fEncoding = encoding;
    } public String getEncoding() {
        return fEncoding;
    } }