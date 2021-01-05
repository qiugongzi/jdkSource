

package com.sun.jmx.snmp;


import java.io.*;
import java.util.Hashtable;
import java.util.*;





abstract public class Enumerated  implements Serializable {


  public Enumerated() throws IllegalArgumentException {
    Enumeration<Integer> e =getIntTable().keys();
    if (e.hasMoreElements()) {
      value = e.nextElement().intValue() ;
    }
    else {
      throw new IllegalArgumentException() ;
    }
  }


  public Enumerated(int valueIndex) throws IllegalArgumentException {
    if (getIntTable().get(new Integer(valueIndex)) == null) {
      throw new IllegalArgumentException() ;
    }
    value = valueIndex ;
  }


  public Enumerated(Integer valueIndex) throws IllegalArgumentException {
    if (getIntTable().get(valueIndex) == null) {
      throw new IllegalArgumentException() ;
    }
    value = valueIndex.intValue() ;
  }



  public Enumerated(String valueString) throws IllegalArgumentException {
    Integer index = getStringTable().get(valueString) ;
    if (index == null) {
      throw new IllegalArgumentException() ;
    }
    else {
      value = index.intValue() ;
    }
  }




  public int intValue() {
    return value ;
  }




  public Enumeration<Integer> valueIndexes() {
    return getIntTable().keys() ;
  }




  public Enumeration<String> valueStrings() {
    return getStringTable().keys() ;
  }



  @Override
  public boolean equals(Object obj) {

    return ((obj != null) &&
            (getClass() == obj.getClass()) &&
            (value == ((Enumerated)obj).value)) ;
  }



  @Override
  public int hashCode() {
    String hashString = getClass().getName() + String.valueOf(value) ;
    return hashString.hashCode() ;
  }



  @Override
  public String toString() {
    return getIntTable().get(new Integer(value)) ;
  }




  protected abstract Hashtable<Integer,String>  getIntTable() ;





  protected abstract Hashtable<String,Integer> getStringTable() ;



  protected int value ;

}
