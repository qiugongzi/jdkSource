



package org.w3c.dom.html;


public interface HTMLOptionElement extends HTMLElement {

    public HTMLFormElement getForm();


    public boolean getDefaultSelected();
    public void setDefaultSelected(boolean defaultSelected);


    public String getText();


    public int getIndex();


    public boolean getDisabled();
    public void setDisabled(boolean disabled);


    public String getLabel();
    public void setLabel(String label);


    public boolean getSelected();
    public void setSelected(boolean selected);


    public String getValue();
    public void setValue(String value);

}
