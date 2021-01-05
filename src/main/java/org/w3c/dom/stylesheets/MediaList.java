



package org.w3c.dom.stylesheets;

import org.w3c.dom.DOMException;


public interface MediaList {

    public String getMediaText();

    public void setMediaText(String mediaText)
                             throws DOMException;


    public int getLength();


    public String item(int index);


    public void deleteMedium(String oldMedium)
                             throws DOMException;


    public void appendMedium(String newMedium)
                             throws DOMException;

}
