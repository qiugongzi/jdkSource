



package org.w3c.dom.html;

import org.w3c.dom.DOMException;


public interface HTMLTableElement extends HTMLElement {

    public HTMLTableCaptionElement getCaption();
    public void setCaption(HTMLTableCaptionElement caption);


    public HTMLTableSectionElement getTHead();
    public void setTHead(HTMLTableSectionElement tHead);


    public HTMLTableSectionElement getTFoot();
    public void setTFoot(HTMLTableSectionElement tFoot);


    public HTMLCollection getRows();


    public HTMLCollection getTBodies();


    public String getAlign();
    public void setAlign(String align);


    public String getBgColor();
    public void setBgColor(String bgColor);


    public String getBorder();
    public void setBorder(String border);


    public String getCellPadding();
    public void setCellPadding(String cellPadding);


    public String getCellSpacing();
    public void setCellSpacing(String cellSpacing);


    public String getFrame();
    public void setFrame(String frame);


    public String getRules();
    public void setRules(String rules);


    public String getSummary();
    public void setSummary(String summary);


    public String getWidth();
    public void setWidth(String width);


    public HTMLElement createTHead();


    public void deleteTHead();


    public HTMLElement createTFoot();


    public void deleteTFoot();


    public HTMLElement createCaption();


    public void deleteCaption();


    public HTMLElement insertRow(int index)
                                 throws DOMException;


    public void deleteRow(int index)
                          throws DOMException;

}
