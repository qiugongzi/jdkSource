

package org.xml.sax.ext;

import org.xml.sax.Attributes;



public interface Attributes2 extends Attributes
{

    public boolean isDeclared (int index);


    public boolean isDeclared (String qName);


    public boolean isDeclared (String uri, String localName);


    public boolean isSpecified (int index);


    public boolean isSpecified (String uri, String localName);


    public boolean isSpecified (String qName);
}
