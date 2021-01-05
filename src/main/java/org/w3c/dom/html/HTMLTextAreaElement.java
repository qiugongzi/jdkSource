



package org.w3c.dom.html;


public interface HTMLTextAreaElement extends HTMLElement {

    public String getDefaultValue();
    public void setDefaultValue(String defaultValue);


    public HTMLFormElement getForm();


    public String getAccessKey();
    public void setAccessKey(String accessKey);


    public int getCols();
    public void setCols(int cols);


    public boolean getDisabled();
    public void setDisabled(boolean disabled);


    public String getName();
    public void setName(String name);


    public boolean getReadOnly();
    public void setReadOnly(boolean readOnly);


    public int getRows();
    public void setRows(int rows);


    public int getTabIndex();
    public void setTabIndex(int tabIndex);


    public String getType();


    public String getValue();
    public void setValue(String value);


    public void blur();


    public void focus();


    public void select();

}
