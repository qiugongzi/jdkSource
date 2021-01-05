


package com.sun.org.apache.xerces.internal.xni;


public interface XMLAttributes {

    public int addAttribute(QName attrName, String attrType, String attrValue);


    public void removeAllAttributes();


    public void removeAttributeAt(int attrIndex);


    public int getLength();


    public int getIndex(String qName);


    public int getIndex(String uri, String localPart);


    public void setName(int attrIndex, QName attrName);


    public void getName(int attrIndex, QName attrName);


    public String getPrefix(int index);


    public String getURI(int index);


    public String getLocalName(int index);


    public String getQName(int index);

    public QName getQualifiedName(int index);



    public void setType(int attrIndex, String attrType);


    public String getType(int index);


    public String getType(String qName);


    public String getType(String uri, String localName);


    public void setValue(int attrIndex, String attrValue);

    public void setValue(int attrIndex, String attrValue, XMLString value);


    public String getValue(int index);


    public String getValue(String qName);


    public String getValue(String uri, String localName);


    public void setNonNormalizedValue(int attrIndex, String attrValue);


    public String getNonNormalizedValue(int attrIndex);


    public void setSpecified(int attrIndex, boolean specified);


    public boolean isSpecified(int attrIndex);



    public Augmentations getAugmentations (int attributeIndex);


    public Augmentations getAugmentations (String uri, String localPart);



    public Augmentations getAugmentations(String qName);


    public void setAugmentations(int attrIndex, Augmentations augs);




}