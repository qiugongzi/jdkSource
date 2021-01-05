



package org.w3c.dom.css;

import org.w3c.dom.DOMException;


public interface CSSStyleDeclaration {

    public String getCssText();

    public void setCssText(String cssText)
                       throws DOMException;


    public String getPropertyValue(String propertyName);


    public CSSValue getPropertyCSSValue(String propertyName);


    public String removeProperty(String propertyName)
                                 throws DOMException;


    public String getPropertyPriority(String propertyName);


    public void setProperty(String propertyName,
                            String value,
                            String priority)
                            throws DOMException;


    public int getLength();


    public String item(int index);


    public CSSRule getParentRule();

}
