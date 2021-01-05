

package com.sun.org.apache.xerces.internal.xs.datatypes;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;


public interface XSDateTime {


    public int getYears();


    public int getMonths();


    public int getDays();


    public int getHours();


    public int getMinutes();


    public double getSeconds();


    public boolean hasTimeZone();


    public int getTimeZoneHours();


    public int getTimeZoneMinutes();


    public String getLexicalValue();


    public XSDateTime normalize();


    public boolean isNormalized();


    public XMLGregorianCalendar getXMLGregorianCalendar();


    public Duration getDuration();
}
