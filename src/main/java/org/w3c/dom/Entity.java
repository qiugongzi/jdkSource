



package org.w3c.dom;


public interface Entity extends Node {

    public String getPublicId();


    public String getSystemId();


    public String getNotationName();


    public String getInputEncoding();


    public String getXmlEncoding();


    public String getXmlVersion();

}
