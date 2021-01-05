

package com.sun.org.apache.xml.internal.resolver;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class CatalogEntry {

  protected static AtomicInteger nextEntry = new AtomicInteger(0);


  protected static final Map<String, Integer> entryTypes = new ConcurrentHashMap<>();


  protected static Vector entryArgs = new Vector();


  static int addEntryType(String name, int numArgs) {
    final int index = nextEntry.getAndIncrement();
    entryTypes.put(name, index);
    entryArgs.add(index, numArgs);

    return index;
  }


  public static int getEntryType(String name)
    throws CatalogException {
    if (!entryTypes.containsKey(name)) {
      throw new CatalogException(CatalogException.INVALID_ENTRY_TYPE);
    }

    Integer iType = entryTypes.get(name);

    if (iType == null) {
      throw new CatalogException(CatalogException.INVALID_ENTRY_TYPE);
    }

    return iType;
  }


  public static int getEntryArgCount(String name)
    throws CatalogException {
    return getEntryArgCount(getEntryType(name));
  }


  public static int getEntryArgCount(int type)
    throws CatalogException {
    try {
      Integer iArgs = (Integer) entryArgs.get(type);
      return iArgs.intValue();
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new CatalogException(CatalogException.INVALID_ENTRY_TYPE);
    }
  }


  protected int entryType = 0;


  protected Vector args = null;


  public CatalogEntry() {}


  public CatalogEntry(String name, Vector args)
    throws CatalogException {
    Integer iType = entryTypes.get(name);

    if (iType == null) {
      throw new CatalogException(CatalogException.INVALID_ENTRY_TYPE);
    }

    int type = iType;

    try {
      Integer iArgs = (Integer) entryArgs.get(type);
      if (iArgs.intValue() != args.size()) {
        throw new CatalogException(CatalogException.INVALID_ENTRY);
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new CatalogException(CatalogException.INVALID_ENTRY_TYPE);
    }

    entryType = type;
    this.args = args;
  }


  public CatalogEntry(int type, Vector args)
    throws CatalogException {
    try {
      Integer iArgs = (Integer) entryArgs.get(type);
      if (iArgs.intValue() != args.size()) {
        throw new CatalogException(CatalogException.INVALID_ENTRY);
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new CatalogException(CatalogException.INVALID_ENTRY_TYPE);
    }

    entryType = type;
    this.args = args;
  }


  public int getEntryType() {
    return entryType;
  }


  public String getEntryArg(int argNum) {
    try {
      String arg = (String) args.get(argNum);
      return arg;
    } catch (ArrayIndexOutOfBoundsException e) {
      return null;
    }
  }


  public void setEntryArg(int argNum, String newspec)
    throws ArrayIndexOutOfBoundsException {
    args.set(argNum, newspec);
  }
}
