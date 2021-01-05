



package org.w3c.dom.html;

import org.w3c.dom.DOMException;


public interface HTMLSelectElement extends HTMLElement {

    public String getType();


    public int getSelectedIndex();
    public void setSelectedIndex(int selectedIndex);


    public String getValue();
    public void setValue(String value);


    public int getLength();


    public HTMLFormElement getForm();


    public HTMLCollection getOptions();


    public boolean getDisabled();
    public void setDisabled(boolean disabled);


    public boolean getMultiple();
    public void setMultiple(boolean multiple);


    public String getName();
    public void setName(String name);


    public int getSize();
    public void setSize(int size);


    public int getTabIndex();
    public void setTabIndex(int tabIndex);


    public void add(HTMLElement element,
                    HTMLElement before)
                    throws DOMException;


    public void remove(int index);


    public void blur();


    public void focus();

}